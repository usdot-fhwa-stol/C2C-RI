/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JFileChooser;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;

/**
 *
 * @author pearsonb
 */
public class LoadEntityDataForm extends javax.swing.JDialog {

    private boolean dataUpdated = false;
    private byte[] dataSource;
    private LoadEntityDataForm thisForm;

    /**
     * Creates new form LoadEntityDataForm
     */
    public LoadEntityDataForm(javax.swing.JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        thisForm = this;
        /** The LoadEntityCancelButton.addActionListener is used to determine the what happens when the cancel button is clicked on the LoadEntityDataForm UI.*/
        LoadEntityCancelButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                try
                {
                    /** The LoadEntityDataForm UI is closed.*/
                    thisForm.setVisible(false);
                }
                catch(Exception Ex)
                {
                    System.out.println("The error occured clicking cancel in the \"Load Entity Data\" UI and the error is: \n" + Ex.getMessage());
                }
            }

        });
        
        /** The LoadButton.addActionListener is used to determine the what happens when the load button is clicked on the LoadEntityDataForm UI.*/
        LoadButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                try
                {
                    /** The selectedFileNow variable is used to retrieve the file path that was chosen and stored in the LoadEntityDataForm UI's FileNameTextField.*/
                    File selectedFileNow = new File(FileNameTextField.getText());

                    /** A condition statement that checks if the file that was stored in the FileNameTextField of the LoadEntityDataForm UI is valid and return if is not valid.*/
                    if(selectedFileNow.isFile() == false)
                    {
                        System.out.println("This is not a file!");
                        javax.swing.JOptionPane.showMessageDialog(null, 
                                "The file does not exist!", 
                                "File Load Error", 
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                        
                        return;
                    }
                    
                    Object[] options = {"Yes", "No"};
                    int n = javax.swing.JOptionPane.showOptionDialog(null,
                            "Are you sure you want to overwrite the existing data for the " + EntityNameTextField.getText() + " entity?",
                            "File Load Data Warning",
                            javax.swing.JOptionPane.YES_NO_OPTION,
                            javax.swing.JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);
                    
                    if(n == 0)
                    {                        
                        dataSource = EmulationDataFileProcessor.getByteArray(selectedFileNow.getPath());
                        dataUpdated = true;
                        
                    }
                    
                }
                catch(Exception Ex)
                {
                    System.out.println("The error occured clicking the \"Load\" button in the \"Load Entity Data\" UI and the error is: \n" + Ex.getMessage());
                }
            }

        });
        
        /** The PathButton.addActionListener is used to determine the what happens when the Path button is clicked on the LoadEntityDataForm UI.*/
        PathButton.addActionListener(new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                try
                {
                    /** The fileChooser variable is used choose and select a file to load in the LoadEntityDataForm UI.*/
                    FileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                    int result = FileChooser.showOpenDialog(FileChooser);
                   
                    /** A condition statement to validate if the correct file has been selected.*/
                    if(result == JFileChooser.APPROVE_OPTION)
                    {
                        /** The selectedFile variable is used to retrieve the selected file from the fileChooser dialog.*/
                        File selectedFile = FileChooser.getSelectedFile();
                        
                        /** The text is set for the FileNameTextField in the LoadEntityDataForm UI.*/
                        FileNameTextField.setText(selectedFile.getAbsolutePath());
                    }
                  
                   
                }
                catch(Exception Ex)
                {
                    System.out.println("The error occured clicking the \"Path\" button in the \"Load Entity Data\" UI and the error is: \n" + Ex.getMessage());
                }
            }
        });
        
    }
    
    public boolean isDataUpdated() {
        return dataUpdated;
    }

    public void setDataUpdated(boolean dataUpdated) {
        this.dataUpdated = dataUpdated;
    }

    public byte[] getDataSource() {
        return dataSource;
    }

    public void setDataSource(byte[] dataSource) {
        this.dataSource = dataSource;
    }

    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        FileChooser = new javax.swing.JFileChooser();
        EntityNameLabel = new javax.swing.JLabel();
        EntityNameTextField = new javax.swing.JTextField();
        FileNameLabel = new javax.swing.JLabel();
        FileNameTextField = new javax.swing.JTextField();
        PathButton = new javax.swing.JButton();
        LoadButton = new javax.swing.JButton();
        LoadEntityCancelButton = new javax.swing.JButton();

        FileChooser.setName("FileChooser"); // NOI18N
        FileChooser.setOpaque(true);
        FileChooser.getAccessibleContext().setAccessibleName("FileChooser");

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Load Entity Data");
        setMinimumSize(new java.awt.Dimension(400, 300));
        setName("LoadEntityDataForm"); // NOI18N

        EntityNameLabel.setText("Entity Name");

        EntityNameTextField.setEditable(false);
        EntityNameTextField.setText("Entity Name");
        EntityNameTextField.setToolTipText("Load Entity Data Entity Name");

        FileNameLabel.setText("File Name");

        FileNameTextField.setText("Select A File...");
        FileNameTextField.setToolTipText("Choose A File For Loading");
        FileNameTextField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));

        PathButton.setText("Path...");
        PathButton.setToolTipText("Load Entity Data File Path Button");
        PathButton.setActionCommand("");

        LoadButton.setText("Load");
        LoadButton.setToolTipText("Load Entity Data Load Button");

        LoadEntityCancelButton.setText("Cancel");
        LoadEntityCancelButton.setToolTipText("Load Entity Data Cancel Button");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(LoadButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(EntityNameLabel)
                            .addComponent(FileNameLabel))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(FileNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE)
                            .addComponent(EntityNameTextField))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(PathButton)
                    .addComponent(LoadEntityCancelButton))
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(EntityNameLabel)
                    .addComponent(EntityNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FileNameLabel)
                    .addComponent(FileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(PathButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 194, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LoadButton)
                    .addComponent(LoadEntityCancelButton))
                .addContainerGap())
        );

        FileNameTextField.getAccessibleContext().setAccessibleName("FileNameTextbox");
        LoadEntityCancelButton.getAccessibleContext().setAccessibleName("LoadEntityCancelButton");
        LoadEntityCancelButton.getAccessibleContext().setAccessibleDescription("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoadEntityDataForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoadEntityDataForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoadEntityDataForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoadEntityDataForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoadEntityDataForm(null, true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel EntityNameLabel;
    public javax.swing.JTextField EntityNameTextField;
    public javax.swing.JFileChooser FileChooser;
    private javax.swing.JLabel FileNameLabel;
    public javax.swing.JTextField FileNameTextField;
    public javax.swing.JButton LoadButton;
    public javax.swing.JButton LoadEntityCancelButton;
    public javax.swing.JButton PathButton;
    // End of variables declaration//GEN-END:variables
}
