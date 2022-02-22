/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
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
 * The Class TestConfigurationList maintains a running list of Configuration Files.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestConfigurationList {

    /** The cfg descriptions. */
    private static ArrayList<TestConfigurationDescription> cfgDescriptions = new ArrayList<>();
    
    /** The Constant NTHREDS. */
    private static final int NTHREDS = 4;
    
    /** The this instance. */
    private static TestConfigurationList thisInstance = new TestConfigurationList();
    
    /** The directory. */
    private static File directory;
    
    /** The executor. */
    final ExecutorService executor = Executors.newCachedThreadPool();
    
    /** The config updater. */
    private static ConfigFileUpdater configUpdater;

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
            if ((name.length()>5) && (name.toUpperCase().endsWith(".RICFG"))) {
                return true;
            }
            return false;
        }
    };

    /**
     * Instantiates a new test configuration list.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestConfigurationList() {
        String filePath = RIParameters.getInstance().getParameterValue(Parameter.config_file_path);
        directory = new File(filePath);
        configUpdater = new ConfigFileUpdater();
        executor.submit(configUpdater);
        executor.shutdown();

    }

    /**
     * Gets the single instance of TestConfigurationList.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of TestConfigurationList
     */
    public static TestConfigurationList getInstance() {
        if (thisInstance == null) {
            thisInstance = new TestConfigurationList();
        }
        return thisInstance;
    }

    /**
     * Gets the list.
     *
     * @param dir the dir
     * @return the list
     */
    public ArrayList<TestConfigurationDescription> getList(File dir) {
        if (!dir.getAbsolutePath().equalsIgnoreCase(thisInstance.directory.getAbsolutePath())) {
            // The directory has changed.  Reset the list.
            thisInstance.configUpdater.unregister();
            thisInstance.directory = dir;
            thisInstance.configUpdater.initialize();
        }
        while (configUpdater.isInitializing()) {
            try {
                Thread.currentThread().sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thisInstance.configUpdater.register(thisInstance.directory);
        return getCfgDescriptions();
    }

    /**
     * Gets the cfg descriptions.
     *
     * @return the cfg descriptions
     */
    private ArrayList<TestConfigurationDescription> getCfgDescriptions() {

        return thisInstance.cfgDescriptions;
    }

    /**
     * The Class ConfigFileUpdater.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class ConfigFileUpdater implements Runnable {

        /** The watcher. */
        private final WatchService watcher;
        
        /** The keys. */
        private final Map<WatchKey, Path> keys;
        
        /** The trace. */
        private boolean trace = false;
        
        /** The initializing. */
        private boolean initializing;

        /**
         * Instantiates a new config file updater.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        ConfigFileUpdater() {
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
//        for (;;) {
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
                    System.err.println("WatchKey not recognized!!");
//                continue;
                }
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
                    if ((child.getFileName().toString().length()>5) &&(child.getFileName().toString().toUpperCase().endsWith(".RICFG"))) {                    
                        System.out.format("%s: %s\n", event.kind().name(), child);
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
//                    break;
                    }
//            }
                }
            }
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            initialize();
            while (true) {

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

        /**
         * Initialize.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        public void initialize() {
            synchronized (TestConfigurationList.cfgDescriptions) {
                initializing = true;
                System.out.println("Getting the list of config files.");
                String[] filenames = TestConfigurationList.directory.list(thisInstance.configFilter);  // Store a list of files in the directory
                TestConfigurationList.cfgDescriptions.clear();
                ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
                ArrayList<Future<TestConfigurationDescription>> cfgList = new ArrayList<>();
                System.out.println("Starting the config File Workers");
                for (int ii = 0; ii < filenames.length; ii++) {
                    Callable<TestConfigurationDescription> worker = new ConfigCallable(ii, TestConfigurationList.directory.getAbsolutePath(), filenames[ii]);
                    Future<TestConfigurationDescription> submit = executor.submit(worker);
                    cfgList.add(submit);
                }
                // Now retrieve the result
                for (Future<TestConfigurationDescription> future : cfgList) {
                    try {
                        TestConfigurationDescription result = future.get();
//                    if (result.isValid()) {
                        TestConfigurationList.cfgDescriptions.add(result);
//                    }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                executor.shutdown();
                System.out.println("Finished the config File Workers");
                initializing = false;
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
            synchronized (TestConfigurationList.cfgDescriptions) {
                initializing = true;
                System.out.println("Getting the list of config files.");
                ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
                ArrayList<Future<TestConfigurationDescription>> cfgList = new ArrayList<>();
                System.out.println("Starting the config File Workers");
                int index = 0;
                File chkFile = new File(directory, fileName);
                if (chkFile.exists()) {

                    boolean fileFound = false;
                    for (int ii = 0; ii < TestConfigurationList.cfgDescriptions.size(); ii++) {
                        if (TestConfigurationList.cfgDescriptions.get(ii).getFilename().equals(fileName)) {
                            fileFound = true;
                            index = ii;
                            break;
                        }
                    }
                    if (!fileFound) {
                        index = TestConfigurationList.cfgDescriptions.size();
                    }
                    Callable<TestConfigurationDescription> worker = new ConfigCallable(index, TestConfigurationList.directory.getAbsolutePath(), fileName);
                    Future<TestConfigurationDescription> submit = executor.submit(worker);
                    cfgList.add(submit);
                    // Now retrieve the result
                    for (Future<TestConfigurationDescription> future : cfgList) {
                        try {
                            TestConfigurationDescription result = future.get();
//                    if (result.isValid()) {
                            if (fileFound) {
                                TestConfigurationDescription tstConfig = TestConfigurationList.cfgDescriptions.get(index);
                                tstConfig.setFileDate(result.getFileDate());
                                tstConfig.setFileDescription(result.getFileDescription());
                                tstConfig.setValid(result.isValid());
                            } else {
                                TestConfigurationList.cfgDescriptions.add(result);
                            }
//                    }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    executor.shutdown();
                } else {  // Remove the file from the list
                    boolean fileFound = false;
                    for (int ii = 0; ii < TestConfigurationList.cfgDescriptions.size(); ii++) {
                        if (TestConfigurationList.cfgDescriptions.get(ii).getFilename().equals(fileName)) {
                            fileFound = true;
                            index = ii;
                            break;
                        }
                    }
                    if (fileFound) {
                        TestConfigurationList.cfgDescriptions.remove(index);
                    }
                }
                System.out.println("Finished the config File Workers");
                initializing = false;
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
        public boolean isInitializing() {
            return initializing;
        }
    }

    /**
     * The Class ConfigCallable.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class ConfigCallable implements Callable<TestConfigurationDescription> {

        /** The index. */
        private final int index;
        
        /** The dir. */
        private final String dir;
        
        /** The file. */
        private final String file;

        /**
         * Instantiates a new config callable.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param index the index
         * @param dir the dir
         * @param file the file
         */
        public ConfigCallable(int index, String dir, String file) {
            this.index = index;
            this.dir = dir;
            this.file = file;

        }

        /* (non-Javadoc)
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public TestConfigurationDescription call() throws Exception {
            ObjectInputStream inputFile=null;

            File f = new File(dir, file);

            TestConfigurationDescription cfgDescription = new TestConfigurationDescription();
            cfgDescription.setFilename(file);
            cfgDescription.setId(index);
            cfgDescription.setFileDate(new Date(f.lastModified()));
            String description = "";
            try {
                inputFile = new ObjectInputStream(new FileInputStream(f));
                try {
                    TestConfiguration testConfig = null;
                    testConfig = (TestConfiguration) inputFile.readObject();
                    if (testConfig.isValidConfiguration()) {
                        description = testConfig.getTestDescription();
                        cfgDescription.setValid(true);
                    } else {
                        description = "Invalid Config File";
                        cfgDescription.setValid(false);
                    }
                    inputFile.close();
                    inputFile = null;
                    testConfig = null;
                } catch (Exception e1) {
                    description = "Invalid Config File";
                    if (inputFile!= null){
                        inputFile.close();
                        inputFile = null;
                    }
                }

            } catch (Exception e) {
                description = "Can't read file";
                    if (inputFile!=null){
                        inputFile.close();
                        inputFile = null;
                    }
            }
            cfgDescription.setFileDescription(description);
            System.out.println(cfgDescription.toString());
            return cfgDescription;
        }
    }
}
