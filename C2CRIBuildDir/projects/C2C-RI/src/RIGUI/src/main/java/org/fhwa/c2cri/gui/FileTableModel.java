/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import org.fhwa.c2cri.logger.TestLogDescription;
import org.fhwa.c2cri.logger.TestLogList;

/**
 * Provides a list of configuration files in a directory.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class FileTableModel extends javax.swing.table.AbstractTableModel {

    /** the directory which will be searched for config files. */
    protected File dir;
    
    /** The list of config files found in the directory. */
    protected String[] filenames;
    
    /** The log filter. */
    private FilenameFilter logFilter;
    
    /** The config filter. */
    private FilenameFilter configFilter;
    
    /** The date column name. */
    public static String DATE_COLUMN_NAME = "Last Modified";
    
    /** An array of column names for the table. */
    protected String[] columnNames = new String[]{
        "Name", "Last Modified"
    };
    
    /** An array of the classes associated with each column. */
    protected Class[] columnClasses = new Class[]{
        String.class, Date.class
    };

    // This table model works for any one given directory
    /**
     * This table model works for any one given directory.
     *
     * @param dir the dir
     */
    public FileTableModel(File dir) {
        this.dir = dir;
        configFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
//            System.out.println(" File " + name + " results " + name.endsWith(".ricfg"));
                return name.endsWith(".ricfg");
            }
        };

        logFilter = new FilenameFilter() {

            public boolean accept(File dir, String name) {
                boolean validFile = false;
                if (name.endsWith(".xml")) {
                    validFile = true;
                    //                  try {
                    //                      validFile = xmlSignatureValidator.isValidXMLSignedFile(dir.getPath()+dir.separator+name);
                    //                  } catch (Exception ex) {
////                        System.err.println("File "+name+" rejected due to "+ex.getMessage());
                    //                  }
                }
                return validFile;
            }
        };

        this.filenames = dir.list(configFilter);  // Store a list of files in the directory
    }

    /**
     * Changes the filter used by the table model to the logFilter.
     */
    public void setLogFilter() {
//        String[] tempFileNames = this.dir.list(logFilter);  // Store a list of files in the directory
        ArrayList<String> validFileNames = new ArrayList<String>();


 //       monitor.setProgress(0, 1, tempFileNames.length);



        System.out.println("About to check Files");
//        for (int ii = 0; ii <= tempFileNames.length; ii++) {
// //           monitor.setProgress(0, ii, tempFileNames.length);
// //           monitor.setStatus("Validating file "+ ii + " of " + tempFileNames.length);
//            try {
//                if (xmlSignatureValidator.isValidXMLSignedFile(dir.getPath() + dir.separator + tempFileNames[ii])) {
//                    validFileNames.add(tempFileNames[ii]);
// //                   Thread.currentThread().sleep(100);
//                }
//
//            } catch (Exception ex) {
////                        System.err.println("File "+name+" rejected due to "+ex.getMessage());
//            }
//
//        }
        System.out.println("All files have been checked Files");
        ArrayList<TestLogDescription> logList = TestLogList.getInstance().getList(dir);
        validFileNames.clear();
        for (TestLogDescription thisLog : logList){
            if (thisLog.isValid()){
                validFileNames.add(thisLog.getFilename());
            }
        }
        
        this.filenames = validFileNames.toArray(new String[validFileNames.size()]);
//        this.filenames = this.dir.list(logFilter);  // Store a list of files in the directory


    }

    /**
     * Changes the filter used by the table model to the logFilter.
     *
     * @param logFiles the new log filter
     */
    public void setLogFilter(ArrayList<String> logFiles) {

        this.filenames = this.dir.list(logFilter);  // Store a list of files in the directory
    }

    /**
     * Changes the filter used by the table model to the configFilter.
     */
    public void setConfigFilter() {
        this.filenames = this.dir.list(configFilter);  // Store a list of files in the directory
    }

    // These are easy methods.
    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 2;
    }  // A constant for this model

    /* (non-Javadoc)
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        if (filenames != null){
        return filenames.length;
        } else 
            return 0;
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
        File f = new File(dir, filenames[row]);
        switch (col) {
            case 0:
                return filenames[row];
            case 1:
                return new Date(f.lastModified());
            default:
                return null;
        }
    }
}
