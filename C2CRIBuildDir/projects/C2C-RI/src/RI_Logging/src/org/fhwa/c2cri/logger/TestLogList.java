/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.logger;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import static java.nio.file.StandardWatchEventKinds.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.fhwa.c2cri.utilities.Parameter;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * Manages the current list of test log files in the current path.
 *
 * @author TransCore ITS
 * Last updated:  10/10/2013
 */
public class TestLogList {

    /** The log descriptions. */
    private static final ArrayList<TestLogDescription> logDescriptions = new ArrayList<>();
    
    /** The Constant NTHREDS. */
    private static final int NTHREDS = 1;
    
    /** The this instance. */
    private static TestLogList thisInstance = new TestLogList();
    
    /** The directory. */
    private static File directory;
    
    /** The executor. */
    final ExecutorService executor = Executors.newCachedThreadPool();
    
    /** The log updater. */
    private static LogFileUpdater logUpdater;

    /**
     * Cast.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param <T> the generic type
     * @param event the event
     * @return the watch event
     */
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }
    
    /** The config filter. */
    FilenameFilter configFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            if ((name.length() > 3) && (name.toUpperCase().endsWith(".XML"))) {
                return true;
            }
            return false;
        }
    };

    /**
     * Instantiates a new test log list.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestLogList() {
        String filePath = RIParameters.getInstance().getParameterValue(Parameter.log_file_path);
        directory = new File(filePath);
        logUpdater = new LogFileUpdater();
        executor.submit(logUpdater);
        executor.shutdown();

    }

    /**
     * Gets the single instance of TestLogList.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of TestLogList
     */
    public static TestLogList getInstance() {
        if (thisInstance == null) {
            thisInstance = new TestLogList();
        }
        return thisInstance;
    }

    /**
     * Gets the list.
     *
     * @param dir the dir
     * @return the list
     */
    public ArrayList<TestLogDescription> getList(File dir) {
        if (!dir.getAbsolutePath().equalsIgnoreCase(thisInstance.directory.getAbsolutePath())) {
            // The directory has changed.  Reset the list.
            thisInstance.logUpdater.unregister();
            thisInstance.directory = dir;
            thisInstance.logUpdater.initialize();
        }
        while (logUpdater.isInitializing()) {
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thisInstance.logUpdater.register(thisInstance.directory);
        return getLogDescriptions();
    }

    /**
     * Gets the log descriptions.
     *
     * @return the log descriptions
     */
    private ArrayList<TestLogDescription> getLogDescriptions() {

        return thisInstance.logDescriptions;
    }
    
    /**
     * Pause test log listing.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void pauseTestLogListing(){
        logUpdater.setPauseFlag(true);
    }
    
    /**
     * Resume test log listing.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void resumeTestLogListing(){
        logUpdater.setPauseFlag(false);        
    }

    /**
     * The Class LogFileUpdater operates as a Thread which gathers and maintains a list of log files.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class LogFileUpdater implements Runnable {

        /** The watcher. */
        private final WatchService watcher;
        
        /** The keys. */
        private final Map<WatchKey, Path> keys;
        
        /** The trace. */
        private boolean trace = false;
        
        /** The initializing. */
        private Boolean initializing;
        
        /** The pause flag. */
        private Boolean pauseFlag;
		
		private boolean running;

        /**
         * Instantiates a new log file updater.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        LogFileUpdater() {
            initializing = true;
            pauseFlag = false;
            this.keys = new HashMap<>();
            WatchService tmpWatcher = null;
            try {
                tmpWatcher = FileSystems.getDefault().newWatchService();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            watcher = tmpWatcher;
            try {
                register(Paths.get(directory.toURI()));
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }

        /**
         * Register.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param dir the dir
         */
        protected void register(File dir) {
            try {
                register(Paths.get(dir.toURI()));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        /**
         * * Register the given directory with the WatchService.
         *
         * @param dir the dir
         * @throws IOException Signals that an I/O exception has occurred.
         */
        private void register(Path dir) throws IOException {
            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            if (trace) {
                Path prev = keys.get(key);
                if (prev == null) {
                    System.out.format("register: %s\n", dir);
                } else {
                    if (!dir.equals(prev)) {
                        System.out.format("update: %s -> %s\n", prev, dir);
                    }
                }
            }
            keys.put(key, dir);
        }

        /**
         * * UnRegister the given directory with the WatchService.
         */
        protected void unregister() {
            keys.clear();
        }

        /**
         * * Process all events for keys queued to the watcher.
         */
        void processEvents() {

            // wait for key to be signalled             
            WatchKey key;
            if (watcher != null) {
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }
                Path dir = keys.get(key);
                if (dir == null) {
                    System.err.println("WatchKey not recognized!!  " + key.toString());

                } else {


                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind kind = event.kind();
                        // TBD - provide example of how OVERFLOW event is handled                 
                        if (kind == OVERFLOW) {
                            continue;
                        }

                        // Context for directory entry event is the file name of entry                 
                        WatchEvent<Path> ev = cast(event);
                        Path name = ev.context();
                        Path child = dir.resolve(name);
                        // print out event                 
                        if ((child.getFileName().toString().length() > 3) && (child.getFileName().toString().toUpperCase().endsWith(".XML"))) {
                            System.out.format("%s: %s\n", event.kind().name(), child);
                            while (isPauseFlag()) {
                                try {
                                    Thread.currentThread().sleep(200);

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            update(child.getFileName().toString());
                        }
                        // if directory is created, and watching recursively, then                 
                        // register it and its sub-directories                 

                    }
                    // reset key and remove from set if directory no longer accessible             
                    boolean valid = key.reset();
                    if (!valid) {
                        keys.remove(key);
                        // all directories are inaccessible                 
                        if (keys.isEmpty()) {

                        }

                    }
                }
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            initialize();
            while (running) {

                try {
                    processEvents();
                    Thread.currentThread().sleep(200);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

		public void stopRunning()
		{
			running = false;
		}
        /**
         * Initialize.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        public void initialize() {
            synchronized (TestLogList.logDescriptions) {
                setInitializing(true);
                System.out.println("Getting the list of log files.");
                String[] filenames = TestLogList.directory.list(thisInstance.configFilter);  // Store a list of files in the directory
                TestLogList.logDescriptions.clear();
                ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
                ArrayList<Future<TestLogDescription>> logList = new ArrayList<>();
                System.out.println("Starting the log File Workers");
                for (int ii = 0; ii < filenames.length; ii++) {
                    Callable<TestLogDescription> worker = new LogCallable(ii, TestLogList.directory.getAbsolutePath(), filenames[ii]);
                    Future<TestLogDescription> submit = executor.submit(worker);
                    logList.add(submit);
                }
                // Now retrieve the result
                for (Future<TestLogDescription> future : logList) {
                    try {
                        TestLogDescription result = future.get();
                        TestLogList.logDescriptions.add(result);
                        if (result.isProcessingError()){
                           javax.swing.JOptionPane.showMessageDialog(null,"The Application encountered a "+result.getFileDescription()+" error while trying to process file "+result.getFilename()+".\n This file will not be available for reports."+(result.getFileDescription().equalsIgnoreCase("Java heap space")?"\nTry using the 64-bit version of the C2C RI, or contact C2C RI support.":""),"Log File Warning",javax.swing.JOptionPane.WARNING_MESSAGE);                                                                
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                executor.shutdown();
                System.out.println("Finished the log File Workers");
                setInitializing(false);
            }
        }

        /**
         * Update.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param fileName the file name
         */
        public void update(String fileName) {
            synchronized (TestLogList.logDescriptions) {
                setInitializing(true);
                System.out.println("Getting the list of log files.");
                ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
                ArrayList<Future<TestLogDescription>> logList = new ArrayList<>();
                System.out.println("Starting the log File Workers");
                int index = 0;
                File chkFile = new File(directory, fileName);
                if (chkFile.exists()) {

                    boolean fileFound = false;
                    for (int ii = 0; ii < TestLogList.logDescriptions.size(); ii++) {
                        if (TestLogList.logDescriptions.get(ii).getFilename().equals(fileName)) {
                            fileFound = true;
                            index = ii;
                            break;
                        }
                    }
                    if (!fileFound) {
                        index = TestLogList.logDescriptions.size();
                    }
                    Callable<TestLogDescription> worker = new LogCallable(index, TestLogList.directory.getAbsolutePath(), fileName);
                    Future<TestLogDescription> submit = executor.submit(worker);
                    logList.add(submit);
                    // Now retrieve the result
                    for (Future<TestLogDescription> future : logList) {
                        try {
                            TestLogDescription result = future.get();
                            if (fileFound) {
                                TestLogDescription tstLog = TestLogList.logDescriptions.get(index);
                                tstLog.setFileDate(result.getFileDate());
                                tstLog.setFileDescription(result.getFileDescription());
                                tstLog.setValid(result.isValid());
                                System.out.println("Updated " + tstLog.getFilename());
                            } else {
                                TestLogList.logDescriptions.add(result);
                                System.out.println("Added " + result.getFilename());                                
                            }
                            if (result.isProcessingError()){
                                javax.swing.JOptionPane.showMessageDialog(null,"The Application encountered a "+result.getFileDescription()+" error while trying to process file "+result.getFilename()+".\n This file will not be available for reports."+(result.getFileDescription().equalsIgnoreCase("Java heap space")?"\nTry using the 64-bit version of the C2C RI, or contact C2C RI support.":""),"Log File Warning",javax.swing.JOptionPane.WARNING_MESSAGE);                                                                
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    executor.shutdown();
                } else {  // Remove the file from the list
                    boolean fileFound = false;
                    for (int ii = 0; ii < TestLogList.logDescriptions.size(); ii++) {
                        if (TestLogList.logDescriptions.get(ii).getFilename().equals(fileName)) {
                            fileFound = true;
                            index = ii;
                            break;
                        }
                    }
                    if (fileFound) {
                        TestLogList.logDescriptions.remove(index);
                        System.out.println("Removed " + fileName);
                    }
                }
                System.out.println("Finished the log File Workers");
                setInitializing(false);
            }
        }

        /**
         * Checks if is initializing.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @return true, if is initializing
         */
        public synchronized boolean isInitializing() {
            return this.initializing;
        }

        /**
         * Sets the initializing.
         *
         * @param state the new initializing
         */
        public synchronized void setInitializing(boolean state) {
                this.initializing = state;
        }

        /**
         * Checks if is pause flag.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @return true, if is pause flag
         */
        private synchronized boolean isPauseFlag() {
            return this.pauseFlag;
        }

        /**
         * Sets the pause flag.
         *
         * @param pauseFlag the new pause flag
         */
        public synchronized void setPauseFlag(boolean pauseFlag) {
                this.pauseFlag = pauseFlag;
        }
        
        
        
        
    }

    /**
     * The Class LogCallable performs validation of log files and provides a description including whether the file has a valid signature.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class LogCallable implements Callable<TestLogDescription> {

        /** The index. */
        private final int index;
        
        /** The dir. */
        private final String dir;
        
        /** The file. */
        private final String file;

        /**
         * Instantiates a new log callable.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param index the index
         * @param dir the dir
         * @param file the file
         */
        public LogCallable(int index, String dir, String file) {
            this.index = index;
            this.dir = dir;
            this.file = file;

        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public TestLogDescription call() throws Exception {

            File f = new File(dir, file);

            TestLogDescription logDescription = new TestLogDescription();
            logDescription.setFilename(file);
            logDescription.setId(index);
            logDescription.setFileDate(new Date(f.lastModified()));
            String description = "";
            try {
                XMLSignatureValidate xmlSignatureValidator = new XMLSignatureValidate();
                System.out.println("TestLogList about to check " + file);
                if (xmlSignatureValidator.isValidXMLSignedFile(dir + File.separator + file)) {
                    description = "";
                    logDescription.setValid(true);
                    System.out.println("TestLogList determined that "+ file + " is valid. ");
                } else {
                    description = "Invalid Log File";
                    logDescription.setValid(false);
                    System.out.println("TestLogList determined that "+ file + " is NOT valid. ");
                }

            } catch (Exception e1) {
                description = "Invalid Log File";
            } catch (Error e2){
                logDescription.setProcessingError(true);
                description = e2.getMessage();
                System.out.println("TestLogList Error received = "+e2.getMessage());
            }
            logDescription.setFileDescription(description);
            System.out.println(logDescription.toString());
            return logDescription;
        }
    }
}
