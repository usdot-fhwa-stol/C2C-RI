/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard.testconfig.edit.page;

import com.github.cjwizard.WizardSettings;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.fhwa.c2cri.domain.testmodel.TestConfigurationController;
import org.fhwa.c2cri.gui.SUTPanel;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPage;

/**
 *
 * @author TransCore ITS, LLC
 */
public class SelectSUTSettingsPage extends C2CRIWizardPage {

    SUTPanel sutPanel;
    TestConfigurationController controller;

    /**
     * Creates new form SelectNeedsPage
     */
    public SelectSUTSettingsPage(String title, String description, TestConfigurationController controller) {
        super(title, description);
        this.controller = controller;
        sutPanel = new SUTPanel();
        add(sutPanel);
        initSUTPanel();

    }

    /**
     * Inits the sut panel.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private void initSUTPanel() {
        sutPanel.setHostNameTextField(controller.getSutParams().getHostName());
        sutPanel.getHostNameTextField().addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    String newName =sutPanel.getHostNameTextField().getText();
                    controller.getSutParams().setHostName(newName);
                }
            });       
        
        sutPanel.setIpAddressTextField(controller.getSutParams().getIpAddress());
        sutPanel.getIpAddressTextField().addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    String newAddress =sutPanel.getIpAddressTextField().getText();
                    controller.getSutParams().setIpAddress(newAddress);
                }
            });      
        
        sutPanel.setPortTextField(controller.getSutParams().getIpPort());
        sutPanel.getPortTextField().addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    String newPort =sutPanel.getPortTextField().getText();
                    controller.getSutParams().setIpPort(newPort);
                }
            });       
        
        sutPanel.setPasswordText(controller.getSutParams().getPassword());
        sutPanel.setUserNameText(controller.getSutParams().getUserName());
        sutPanel.setWebServiceURLTextField(controller.getSutParams().getWebServiceURL());
        sutPanel.getWebServiceURLTextField().addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    String newURL =sutPanel.getWebServiceURLTextField().getText();
                    controller.getSutParams().setWebServiceURL(newURL);
                }
            });               
        
        sutPanel.setJuserNameCheckBox(controller.getSutParams().isUserNameRequired());
        sutPanel.setJpasswordCheckBox(controller.getSutParams().isPasswordRequired());
        updateUserNameFields();
        updatePasswordFields();
        
        sutPanel.getUserNameCheckBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateUserNameFields();
            }

        });
        sutPanel.getPasswordCheckBox().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePasswordFields();
            }
        });
    }

    private void updateUserNameFields() {
        if (sutPanel.getUserNameCheckBox().isSelected()) {
            sutPanel.getUserNameTextField().setEnabled(true);
        } else {
            sutPanel.getUserNameTextField().setEnabled(true);
        };
    }

    private void updatePasswordFields() {
        if (sutPanel.getPasswordCheckBox().isSelected()) {
            sutPanel.getPasswordTextField().setEnabled(true);
        } else {
            sutPanel.getPasswordTextField().setEnabled(true);
        };
    }

    @Override
    public void updateSettings(WizardSettings settings) {
        super.updateSettings(settings);

        controller.getSutParams().setIpAddress(sutPanel.getIpAddressText());
        controller.getSutParams().setIpPort(sutPanel.getPortText());
        controller.getSutParams().setHostName(sutPanel.getHostNameText());
        controller.getSutParams().setWebServiceURL(sutPanel.getWebServiceURLText());
        controller.getSutParams().setUserNameRequired(sutPanel.isJuserNameCheckBoxSelected());
        controller.getSutParams().setUserName(sutPanel.getHostNameText());
        controller.getSutParams().setPasswordRequired(sutPanel.isJpasswordCheckBoxSelected());
        controller.getSutParams().setPassword(sutPanel.getPasswordText());

    }

    @Override
    public boolean isCheckRequiredBeforeCancel() {
        return true;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
