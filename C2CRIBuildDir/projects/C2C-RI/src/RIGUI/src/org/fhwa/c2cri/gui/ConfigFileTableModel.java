/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Callable;
import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.testmodel.TestConfigurationDescription;
import org.fhwa.c2cri.testmodel.TestConfigurationList;

/**
 * The methods in this class allow the JTable component to get and display data
 * about the files in a specified directly. It represents a table with 6
 * columns: file name, size, modification date, plus three columns for flags:
 * directory, readable, writable
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ConfigFileTableModel extends javax.swing.table.AbstractTableModel {

    /** The directory that will be referenced by the model. */
    protected File dir;
    
    /** The list of files that were found at the directory. */
    protected String[] filenames;
//    protected String[] fileDescriptions;
//    protected Date[] fileDates;
    /** The log filter. */
private transient FilenameFilter logFilter;
    
    /** The config filter. */
    private transient FilenameFilter configFilter;
    
    /** The cfg descriptions. */
    private transient ArrayList<TestConfigurationDescription> cfgDescriptions = new ArrayList<>();
    
    /** The Constant NTHREDS. */
    private static final int NTHREDS = 10;
    
    /** The column names that will be displayed on the table. */
    protected String[] columnNames = new String[]{
        "Name", "Description", "Last Modified"
    };
    
    /** The class types associated with the columns on the table. */
    protected Class[] columnClasses = new Class[]{
        String.class, String.class, Date.class
    };
    
    /** The input. */
    private transient ObjectInputStream input;

    // This table model works for any one given directory
    /**
     * The constructor for the ConfigTableModel.
     * <li> Create a config file Filter for checking for Config Files
     * <li> Create a log file Filter for checking for log Files
     * <li> set the filenames array to the list of config files
     *
     * @param dir the dir
     */
    public ConfigFileTableModel(File dir) {
        this.dir = dir;
//        configFilter = new FilenameFilter() {
//            public boolean accept(File dir, String name) {
////                String fileName = dir.getPath() + File.separator + name;
////                System.out.println("ConfigTableModel checking file "+ name);
//                if (name.endsWith(".ricfg") || (name.endsWith(".RICFG"))) {
////                    try {
////                        TestConfiguration tempTest;
////                        ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName));
////                        tempTest = (org.fhwa.c2cri.testmodel.TestConfiguration) input.readObject();
////                        input.close();
////                        input = null;
////                        if (!tempTest.isValidConfiguration()) {
////                            return false;
////                        } else {
//                            return true;
////                        }
////                    } catch (Exception ex) {
////                    }
//                }
//
//                //               return name.endsWith(TestDefinitionCoordinator.CONFIGURATION_FILE_EXTENSION);
//                return false;
//            }
//        };
//
//        logFilter = new FilenameFilter() {
//            public boolean accept(File dir, String name) {
//                return name.contains(".rilog");
//            }
//        };
//
//        System.out.println("Getting the list of config files.");
//        this.filenames = dir.list(configFilter);  // Store a list of files in the directory
//
//        ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
//        ArrayList<Future<ConfigDescription>> cfgList = new ArrayList<Future<ConfigDescription>>();
//        System.out.println("Starting the config File Workers");
//        for (int ii = 0; ii<this.filenames.length;ii++){
//            Callable<ConfigDescription> worker = new ConfigCallable(ii,dir.getAbsolutePath(),filenames[ii]); 
//            Future<ConfigDescription> submit = executor.submit(worker);
//            cfgList.add(submit);
//        }
//        // Now retrieve the result
//        for (Future<ConfigDescription> future : cfgList){
//            try{
//                ConfigDescription result = future.get();
//                if (result.isValid()){
//                    cfgDescriptions.add(result);
//                }
//            } catch (InterruptedException e){
//               e.printStackTrace();
//            } catch (ExecutionException e){
//                e.printStackTrace();
//            }
//        }
//        executor.shutdown();
//        System.out.println("Finished the config File Workers");
//        
////        fileDescriptions = new String[this.filenames.length];
////        fileDates = new Date[this.filenames.length];
////        for (int ii = 0; ii < this.filenames.length; ii++) {
////            File f = new File(dir, filenames[ii]);
////
////            String description = "";
////            try {
////                input = new ObjectInputStream(new FileInputStream(f));
////                try {
////                    TestConfiguration testConfig = null;
////                    testConfig = (TestConfiguration) input.readObject();
////                    if (testConfig.isValidConfiguration()) {
////                        description = testConfig.getTestDescription();
////                    } else {
////                        description = "Invalid Config File";
////                    }
////                    input.close();
////                    input = null;
////                    testConfig = null;
////                } catch (Exception e1) {
////                    description = "Invalid Config File";
////                }
////
////            } catch (Exception e) {
////                description = "Can't read file";
////            }
////            fileDescriptions[ii] = description;
////            fileDates[ii] = new Date(f.lastModified());
////
////        }
        cfgDescriptions.clear();
         ArrayList<TestConfigurationDescription> cfgList = TestConfigurationList.getInstance().getList(dir);
         for (TestConfigurationDescription thisCfg : cfgList){
             if (thisCfg.isValid()){
                cfgDescriptions.add(thisCfg);
             }             
         }
        System.out.println(" Number of files found = " + cfgDescriptions.size());
    }

    // These are easy methods.
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 3;
    }  // A constant for this model

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
//      System.out.println(" Number of files found = "+ this.filenames.length);
        return this.cfgDescriptions.size();
    }  // # of files in dir

    // Information about each column.
    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /* (non-Javadoc)
     * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
     */
    @Override
    public Class getColumnClass(int col) {
        return columnClasses[col];
    }

    // The method that must actually return the value of each cell.
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int col) {
//        File f = new File(dir, filenames[row]);
        switch (col) {
            case 0:
                return cfgDescriptions.get(row).getFilename();
            case 1:
                return cfgDescriptions.get(row).getFileDescription();
//                String description = null;
//                try {
//                    input = new ObjectInputStream(new FileInputStream(f));
//                    try {
//                        TestConfiguration testConfig = null;
//                        testConfig = (TestConfiguration) input.readObject();
//                        if (testConfig.isValidConfiguration()) {
//                            description = testConfig.getTestDescription();
//                        } else {
//                            description = "Invalid Config File";
//                        }
//                        input.close();
//                        input = null;
//                        testConfig = null;
//                    } catch (Exception e1) {
//                        description = "Invalid Config File";
//                    }
//
//                } catch (Exception e) {
//                    description = "Can't read file";
//                }
//                return description;
            case 2:
                return cfgDescriptions.get(row).getFileDate();
//                return new Date(f.lastModified());
            default:
                return null;
        }
    }

    /**
     * The Class ConfigDescription.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class ConfigDescription {

        /** The id. */
        private int id;
        
        /** The valid. */
        private boolean valid;
        
        /** The filenames. */
        private String filenames;
        
        /** The file description. */
        private String fileDescription;
        
        /** The file dates. */
        private Date fileDates;

        /**
         * Gets the id.
         *
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * Sets the id.
         *
         * @param id the new id
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * Gets the filenames.
         *
         * @return the filenames
         */
        public String getFilenames() {
            return filenames;
        }

        /**
         * Sets the filenames.
         *
         * @param filenames the new filenames
         */
        public void setFilenames(String filenames) {
            this.filenames = filenames;
        }

        /**
         * Gets the file description.
         *
         * @return the file description
         */
        public String getFileDescription() {
            return fileDescription;
        }

        /**
         * Sets the file description.
         *
         * @param fileDescriptions the new file description
         */
        public void setFileDescription(String fileDescriptions) {
            this.fileDescription = fileDescriptions;
        }

        /**
         * Gets the file dates.
         *
         * @return the file dates
         */
        public Date getFileDates() {
            return fileDates;
        }

        /**
         * Sets the file dates.
         *
         * @param fileDates the new file dates
         */
        public void setFileDates(Date fileDates) {
            this.fileDates = fileDates;
        }

        /**
         * Checks if is valid.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @return true, if is valid
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Sets the valid.
         *
         * @param valid the new valid
         */
        public void setValid(boolean valid) {
            this.valid = valid;
        }
    }

    
    
    /**
     * The Class ConfigCallable.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class ConfigCallable implements Callable<ConfigDescription> {

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
        public ConfigDescription call() throws Exception {
            File f = new File(dir, file);

            ConfigDescription cfgDescription = new ConfigDescription();
            cfgDescription.setFilenames(file);
            cfgDescription.setId(index);
            cfgDescription.setFileDates(new Date(f.lastModified()));
            String description = "";
            try (ObjectInputStream inputFile = new ObjectInputStream(new FileInputStream(f)))
			{
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
                    testConfig = null;
                } catch (Exception e1) {
                    description = "Invalid Config File";
                }

            } catch (Exception e) {
                description = "Can't read file";
            }
            cfgDescription.setFileDescription(description);
            System.out.println("Index "+index + " : checking File "+file+" : valid = "+cfgDescription.isValid());
            return cfgDescription;
        }
    }
}
