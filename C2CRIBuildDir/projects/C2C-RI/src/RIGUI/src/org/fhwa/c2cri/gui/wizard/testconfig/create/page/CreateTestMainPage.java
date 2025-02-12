/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard.testconfig.create.page;

import com.github.cjwizard.WizardSettings;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import org.fhwa.c2cri.domain.testmodel.TestConfigurationController;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPage;

/**
 *
 */
public class CreateTestMainPage extends C2CRIWizardPage {

    private final TestConfigurationController controller;
    private String fileName;
    private boolean fileLoaded = false;

    public CreateTestMainPage(String title, String description, TestConfigurationController controller) {
        super(title, description);
        initComponents();
        this.controller = controller;
        jFileNamePanel.setVisible(false);
        this.jFileNameTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                fileName = jFileNameTextField.getText();
                File tmpFile = new File(fileName);
                if (tmpFile.exists()) {
                    setNextEnabled(true);
                } else {
                    setNextEnabled(false);
                }
            }
        });
    }

    @Override
    public void updateSettings(WizardSettings settings) {
        super.updateSettings(settings); //To change body of generated methods, choose Tools | Templates.

        // When the user clicks the Next button this method gets called twice by the C2CRIWizardContainer.  If the user selected to 
        // cancel loading a configuration file, skip the loadTestConfigFile call on the second go around.
        if ((Boolean) settings.get("WizardEditExistingConfigRadioButton")) {
            if (fileLoaded) {
                settings.put("TestConfigFileLoaded", fileName);
            }
        }
    }

    @Override
    public boolean onNext(WizardSettings settings) {
        super.onNext(settings); //To change body of generated methods, choose Tools | Templates.
        if (jEditExistingRadioButton.isSelected()) {
            try {
                controller.openConfig(fileName);
                fileLoaded = true;

            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(null, "Error Opening File:  " + fileName + "\n" + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

//    /**
//     * Creates new form CreateTestMainPanel
//     */
//    public CreateTestMainPanel() {
//        initComponents();
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        createTestConfigButtonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jCreateNewConfigRadioButton1 = new javax.swing.JRadioButton();
        jEditExistingRadioButton = new javax.swing.JRadioButton();
        jFileNamePanel = new javax.swing.JPanel();
        jBrowseConfigFiles = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jFileNameTextField = new javax.swing.JTextField();

        jLabel1.setText("Would you like to create a new Test Configuration or edit an existing one?");

        createTestConfigButtonGroup1.add(jCreateNewConfigRadioButton1);
        jCreateNewConfigRadioButton1.setSelected(true);
        jCreateNewConfigRadioButton1.setText("Create a new Test Configuration file.");
        jCreateNewConfigRadioButton1.setToolTipText("Create a new Test Configuration file.");
        jCreateNewConfigRadioButton1.setName("WizardCreateNewConfigRadioButton"); // NOI18N
        jCreateNewConfigRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCreateNewConfigRadioButton1ActionPerformed(evt);
            }
        });

        createTestConfigButtonGroup1.add(jEditExistingRadioButton);
        jEditExistingRadioButton.setText("Edit an existing Test Configuration file.");
        jEditExistingRadioButton.setToolTipText("Edit an existing Test Configuration file.");
        jEditExistingRadioButton.setName("WizardEditExistingConfigRadioButton"); // NOI18N
        jEditExistingRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jEditExistingRadioButtonActionPerformed(evt);
            }
        });

        jBrowseConfigFiles.setText("Browse...");
        jBrowseConfigFiles.setToolTipText("Search for an existing configuration file");
        jBrowseConfigFiles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jBrowseConfigFilesActionPerformed(evt);
            }
        });

        jLabel2.setText("File Name:");

        jFileNameTextField.setToolTipText("Enter the Test Configuration File name");

        javax.swing.GroupLayout jFileNamePanelLayout = new javax.swing.GroupLayout(jFileNamePanel);
        jFileNamePanel.setLayout(jFileNamePanelLayout);
        jFileNamePanelLayout.setHorizontalGroup(
            jFileNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jFileNamePanelLayout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFileNameTextField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jBrowseConfigFiles)
                .addContainerGap())
        );
        jFileNamePanelLayout.setVerticalGroup(
            jFileNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileNamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jFileNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jFileNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jFileNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jBrowseConfigFiles))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 338, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jEditExistingRadioButton)
                            .addComponent(jCreateNewConfigRadioButton1))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jFileNamePanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jCreateNewConfigRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jEditExistingRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jFileNamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jEditExistingRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jEditExistingRadioButtonActionPerformed
        if (jEditExistingRadioButton.isSelected()) {
            jFileNamePanel.setVisible(true);
            jFileNameTextField.setText("");
            setNextEnabled(false);
        } 
    }//GEN-LAST:event_jEditExistingRadioButtonActionPerformed

    private void jBrowseConfigFilesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jBrowseConfigFilesActionPerformed
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        String default_Path = "c:\\c2cri";

        File current_dir = new File(default_Path);
        System.out.println(" Path = " + default_Path + " is valid? " + current_dir.exists());
        fc.setCurrentDirectory(current_dir);
        FileFilter ricfgFilter = new FileFilter() {

            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".ricfg");
            }

            public String getDescription() {
                return "Filter for RI Config Files";
            }
        };
        fc.setFileFilter(ricfgFilter);

        boolean normalExit = false;
        int returnVal = 0;
        while (!normalExit) {

            returnVal = fc.showOpenDialog(null);

            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                if (file == null || file.getName().equals("")) {
                    javax.swing.JOptionPane.showMessageDialog(null, "Invalid File Name", "File Name Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                } 
                else {
                        fileLoaded = true;
                        normalExit = true;
                        fileName = fc.getSelectedFile().getAbsolutePath();
                }

            } else if (returnVal == javax.swing.JFileChooser.CANCEL_OPTION) {
                normalExit = true;
            }
        }
        fc.setVisible(false);
        if (fileLoaded) {
            jFileNameTextField.setText(fileName);
            setNextEnabled(true);
        }
    }//GEN-LAST:event_jBrowseConfigFilesActionPerformed

    private void jCreateNewConfigRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCreateNewConfigRadioButton1ActionPerformed
        if (jCreateNewConfigRadioButton1.isSelected()) {
            jFileNamePanel.setVisible(false);
            setNextEnabled(true);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jCreateNewConfigRadioButton1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup createTestConfigButtonGroup1;
    private javax.swing.JButton jBrowseConfigFiles;
    private javax.swing.JRadioButton jCreateNewConfigRadioButton1;
    private javax.swing.JRadioButton jEditExistingRadioButton;
    private javax.swing.JPanel jFileNamePanel;
    private javax.swing.JTextField jFileNameTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
