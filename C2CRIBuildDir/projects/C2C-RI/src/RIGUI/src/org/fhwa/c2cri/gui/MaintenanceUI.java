/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MaintenanceUI.java
 *
 * Created on Jul 12, 2010, 11:59:15 AM
 */

package org.fhwa.c2cri.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DateFormat;
import java.util.Date;
import javax.swing.table.DefaultTableCellRenderer;
import org.fhwa.c2cri.utilities.Parameter;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class MaintenanceUI provides the file maintenace function to the user.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MaintenanceUI extends javax.swing.JDialog implements javax.swing.event.ListSelectionListener{

    /** The file path. */
    private String filePath;


    /**
     * Creates new form MaintenanceUI.
     *
     * @param parent the parent
     * @param modal the modal
     */
    public MaintenanceUI(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        this.setTitle("C2C RI Maintenance");

        this.fileTypeButtonGroup.add(this.configurationFilesRadioButton);
        this.fileTypeButtonGroup.add(this.logFilesRadioButton);

//        File dir = new File(System.getProperty("user.home"));
        File dir = new File(RIParameters.getInstance().getParameterValue(Parameter.config_file_path));
        FileTableModel model = new FileTableModel(dir);
        if (this.logFilesRadioButton.isSelected()){
            this.configPathTextField.setText(RIParameters.getInstance().getParameterValue(Parameter.log_file_path));
            model.setLogFilter();
        } else {
            this.configPathTextField.setText(RIParameters.getInstance().getParameterValue(Parameter.log_file_path));
            model.setConfigFilter();
        }

        this.fileDeletionTable.setModel(model);
        this.fileDeletionTable.getColumn("Last Modified").setCellRenderer(new DateRenderer());

        javax.swing.ListSelectionModel listSelectionModel = this.fileDeletionTable.getSelectionModel();
        listSelectionModel.addListSelectionListener(this);
        listSelectionModel.setSelectionMode(listSelectionModel.SINGLE_SELECTION);
        this.fileDeletionTable.setSelectionModel(listSelectionModel);

        filePath = dir.getAbsolutePath();
        configPathTextField.setText(filePath);

        fileNameTextField.setText("");

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileTypeButtonGroup = new javax.swing.ButtonGroup();
        cancelButton = new javax.swing.JButton();
        fileTypeButtonPanel = new javax.swing.JPanel();
        logFilesRadioButton = new javax.swing.JRadioButton();
        configurationFilesRadioButton = new javax.swing.JRadioButton();
        fileNameTextField = new javax.swing.JTextField();
        browseButton = new javax.swing.JButton();
        configPathTextField = new javax.swing.JTextField();
        deleteButton = new javax.swing.JButton();
        fileDeletionScrollPane = new javax.swing.JScrollPane();
        fileDeletionTable = new javax.swing.JTable();
        fileNameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Close this window");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        fileTypeButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Show Files of Type"));

        logFilesRadioButton.setSelected(true);
        logFilesRadioButton.setText("Log Files");
        logFilesRadioButton.setToolTipText("Show log files in path");
        logFilesRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logFilesRadioButtonActionPerformed(evt);
            }
        });

        configurationFilesRadioButton.setText("Configuration Files");
        configurationFilesRadioButton.setToolTipText("Show configuration files in path");
        configurationFilesRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                configurationFilesRadioButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout fileTypeButtonPanelLayout = new javax.swing.GroupLayout(fileTypeButtonPanel);
        fileTypeButtonPanel.setLayout(fileTypeButtonPanelLayout);
        fileTypeButtonPanelLayout.setHorizontalGroup(
            fileTypeButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileTypeButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fileTypeButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(configurationFilesRadioButton)
                    .addComponent(logFilesRadioButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        fileTypeButtonPanelLayout.setVerticalGroup(
            fileTypeButtonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileTypeButtonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(logFilesRadioButton)
                .addGap(3, 3, 3)
                .addComponent(configurationFilesRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        fileNameTextField.setEditable(false);
        fileNameTextField.setText("Jan-12-2010-C2C-v1223-2");
        fileNameTextField.setToolTipText("The name of the file to be deleted.");
        fileNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileNameTextFieldActionPerformed(evt);
            }
        });

        browseButton.setText("Path...");
        browseButton.setToolTipText("Select the path for file search");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        configPathTextField.setEditable(false);
        configPathTextField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        configPathTextField.setText("jTextField1");
        configPathTextField.setToolTipText("The path for the log or configuration files.");

        deleteButton.setText("Delete");
        deleteButton.setToolTipText("Delete the selected file");
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        fileDeletionTable.setAutoCreateRowSorter(true);
        fileDeletionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        fileDeletionTable.setToolTipText("The list of available files.");
        fileDeletionTable.getTableHeader().setReorderingAllowed(false);
        fileDeletionScrollPane.setViewportView(fileDeletionTable);
        fileDeletionTable.getAccessibleContext().setAccessibleName("File List Table");

        fileNameLabel.setText("File Name");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fileNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fileNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(browseButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(configPathTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)))
                        .addGap(36, 36, 36)
                        .addComponent(fileTypeButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(197, Short.MAX_VALUE)
                        .addComponent(deleteButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 223, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(fileDeletionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 659, Short.MAX_VALUE)
                        .addGap(20, 20, 20)))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(fileNameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(fileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(browseButton)
                            .addComponent(configPathTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(fileTypeButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(16, 16, 16)
                .addComponent(fileDeletionScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteButton)
                    .addComponent(cancelButton))
                .addGap(42, 42, 42))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Log files radio button action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void logFilesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logFilesRadioButtonActionPerformed
//        File dir = new File(filePath);
//        File dir = new File(RIParameters.getInstance().getParameterValue(Parameter.log_file_path));
        this.fileNameTextField.setText("");
        File dir = new File(this.configPathTextField.getText());
        FileTableModel model = new FileTableModel(dir);
        model.setLogFilter();
        this.fileDeletionTable.setModel(model);
        this.fileDeletionTable.getColumn("Last Modified").setCellRenderer(new DateRenderer());

}//GEN-LAST:event_logFilesRadioButtonActionPerformed

    /**
     * Configuration files radio button action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void configurationFilesRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_configurationFilesRadioButtonActionPerformed
//        File dir = new File(filePath);
//        File dir = new File(RIParameters.getInstance().getParameterValue(Parameter.config_file_path));
        this.fileNameTextField.setText("");
        File dir = new File(this.configPathTextField.getText());
        FileTableModel model = new FileTableModel(dir);
        model.setConfigFilter();
        this.fileDeletionTable.setModel(model);
        this.fileDeletionTable.getColumn("Last Modified").setCellRenderer(new DateRenderer());

        
}//GEN-LAST:event_configurationFilesRadioButtonActionPerformed

    /**
     * File name text field action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void fileNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileNameTextFieldActionPerformed
        
}//GEN-LAST:event_fileNameTextFieldActionPerformed

    /**
     * Browse button action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        fc.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog((javax.swing.JFrame) javax.swing.SwingUtilities.getRoot(this.getParent()));
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION){
            java.io.File file = fc.getSelectedFile();
            filePath = file.getAbsolutePath();

            configPathTextField.setText(file.getAbsolutePath());

            FileTableModel model = new FileTableModel(file);
            if(this.logFilesRadioButton.isSelected()){
                model.setLogFilter();
            }else model.setConfigFilter();
            this.fileDeletionTable.setModel(model);
            this.fileDeletionTable.getColumn("Last Modified").setCellRenderer(new DateRenderer());

        }
}//GEN-LAST:event_browseButtonActionPerformed

    /**
     * Delete button action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (!(this.fileNameTextField.getText()==null) && !(this.fileNameTextField.getText().equals(""))){

            //Custom button text
            Object[] options = {"Yes",
            "No"};
            int n = javax.swing.JOptionPane.showOptionDialog(this,
                    "Are you sure you want to delete the file: "+ this.fileNameTextField.getText(),
                    "File Deletion Warning",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (n == 0) {
                File theFile = new File(this.filePath+"\\"+this.fileNameTextField.getText());
                try {
                    if (theFile.exists()){
                        theFile.delete();

                        // Refresh the file list
                        File dir = new File(filePath);
                        FileTableModel model = new FileTableModel(dir);
                        if(this.logFilesRadioButton.isSelected()){
                            model.setLogFilter();
                        }else model.setConfigFilter();
                        // Clear the file name field
                        this.fileNameTextField.setText("");
                        this.fileDeletionTable.setModel(model);
                        this.fileDeletionTable.getColumn("Last Modified").setCellRenderer(new DateRenderer());

                    }
                } catch (Exception e) // catch any other IOExceptions
                {
                    javax.swing.JOptionPane.showMessageDialog(this, "Error deleting the file: "+filePath + "\\"+ this.fileNameTextField.getText() + "  " + e.getMessage(), "File Deletion Error", javax.swing.JOptionPane.ERROR_MESSAGE);

                }

            }
        } else
            javax.swing.JOptionPane.showMessageDialog(this, "No file was selected for deletion. ", "File Deletion Error", javax.swing.JOptionPane.ERROR_MESSAGE);

       
}//GEN-LAST:event_deleteButtonActionPerformed

    /**
     * Cancel button action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MaintenanceUI dialog = new MaintenanceUI(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }


    /**
     * Show dialog.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if successful
     */
    public boolean showDialog(){
                setVisible(true);
    return true;
}
        
        /* (non-Javadoc)
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(javax.swing.event.ListSelectionEvent e){
            javax.swing.ListSelectionModel lsm = (javax.swing.ListSelectionModel)e.getSource();

            if (!lsm.isSelectionEmpty()){
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i<=maxIndex; i++){
                    if(lsm.isSelectedIndex(i)){
                        fileNameTextField.setText((String)this.fileDeletionTable.getValueAt(i, 0));
                    }
                }
            }
        }

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

  /** An array of column names for the table. */
  protected String[] columnNames = new String[] {
    "Name", "Last Modified"
  };

  /** An array of the classes associated with each column. */
  protected Class[] columnClasses = new Class[] {
    String.class,  Date.class
  };

  // This table model works for any one given directory
  /**
   * This table model works for any one given directory.
   *
   * @param dir the dir
   */
  public FileTableModel(File dir) {
    this.dir = dir;
    configFilter = new FilenameFilter(){
        public boolean accept (File dir, String name){
//            System.out.println(" File " + name + " results " + name.endsWith(".ricfg"));
            return name.endsWith(".ricfg");
        }
    };

    logFilter = new FilenameFilter(){
        public boolean accept (File dir, String name){
            return name.contains(".xml");
        }
    };

    this.filenames = dir.list(configFilter);  // Store a list of files in the directory
  }

  /**
   * Changes the filter used by the table model to the logFilter.
   */
  public void setLogFilter(){
    this.filenames = this.dir.list(logFilter);  // Store a list of files in the directory
  }

  /**
   * Changes the filter used by the table model to the configFilter.
   */
  public void setConfigFilter(){
    this.filenames = this.dir.list(configFilter);  // Store a list of files in the directory
  }

  // These are easy methods.
  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount() { return 2; }  // A constant for this model
  
  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount() { return filenames.length; }  // # of files in dir

  // Information about each column.
        /* (non-Javadoc)
   * @see javax.swing.table.AbstractTableModel#getColumnName(int)
   */
  @Override
  public String getColumnName(int col) { return columnNames[col]; }
        
        /* (non-Javadoc)
         * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
         */
        @Override
  public Class getColumnClass(int col) { return columnClasses[col]; }

  // The method that must actually return the value of each cell.
  /* (non-Javadoc)
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt(int row, int col) {
    File f = new File(dir, filenames[row]);
    switch(col) {
    case 0: return filenames[row];
    case 1: return new Date(f.lastModified());
    default: return null;
    }
  }


}


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton browseButton;
    protected javax.swing.JButton cancelButton;
    private javax.swing.JTextField configPathTextField;
    private javax.swing.JRadioButton configurationFilesRadioButton;
    protected javax.swing.JButton deleteButton;
    private javax.swing.JScrollPane fileDeletionScrollPane;
    private javax.swing.JTable fileDeletionTable;
    private javax.swing.JLabel fileNameLabel;
    private javax.swing.JTextField fileNameTextField;
    private javax.swing.ButtonGroup fileTypeButtonGroup;
    private javax.swing.JPanel fileTypeButtonPanel;
    private javax.swing.JRadioButton logFilesRadioButton;
    // End of variables declaration//GEN-END:variables

}
