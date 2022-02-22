
/*
 * SUTPanel.java - contains the class for creating the SUT Panel.
 *
 * Created on Feb 12, 2010, 7:23:54 PM
 */
package org.fhwa.c2cri.gui;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

/**
 * Test Configuration Panel Class for obtaining SUT information.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class SUTPanel extends javax.swing.JPanel {

    /** Creates new panel SUTPanel.
     *
     * It calls initComponents to create all panel elements.
     */
    public SUTPanel() {
        initComponents();

        /* This code was placed here to test whether accessibility screen reader software
        * does a better job of reading the screen content if we included a couple of recommendations
         * regarding conformance to the Section 508 requirements for java applications.
         *
         * So far we have seen no difference.  Otherwise the following code is unnecessary.
         *
         */
        this.ipAddressLabel.setLabelFor(this.ipAddressTextField);
        this.portLabel.setLabelFor(this.portTextField);
        javax.accessibility.AccessibleRelationSet arSet1 =
                this.ipAddressLabel.getAccessibleContext().getAccessibleRelationSet();
        javax.accessibility.AccessibleRelation ar1 =
                new javax.accessibility.AccessibleRelation(javax.accessibility.AccessibleRelation.LABEL_FOR, this.ipAddressTextField);
        arSet1.add(ar1);

        javax.accessibility.AccessibleRelationSet arSet2 =
                this.ipAddressLabel.getAccessibleContext().getAccessibleRelationSet();
        javax.accessibility.AccessibleRelation ar2 =
                new javax.accessibility.AccessibleRelation(javax.accessibility.AccessibleRelation.LABELED_BY, this.ipAddressLabel);
        arSet2.add(ar2);
        // End of Section 508 test code

    }

    public String getHostNameText() {
        return hostNameTextField.getText();
    }

    public void setHostNameTextField(String hostNameTextField) {
        this.hostNameTextField.setText(hostNameTextField);
    }

    public String getIpAddressText() {
        return ipAddressTextField.getText();
    }

    public void setIpAddressTextField(String ipAddressTextField) {
        this.ipAddressTextField.setText(ipAddressTextField);
    }

    public boolean isJpasswordCheckBoxSelected() {
        return jpasswordCheckBox.isSelected();
    }

    public void setJpasswordCheckBox(boolean jpasswordCheckBox) {
        this.jpasswordCheckBox.setSelected(jpasswordCheckBox);
    }

    public boolean isJuserNameCheckBoxSelected() {
        return juserNameCheckBox.isSelected();
    }

    public void setJuserNameCheckBox(boolean juserNameCheckBox) {
        this.juserNameCheckBox.setSelected(juserNameCheckBox);
    }

    public String getPasswordText() {
        return passwordTextField.getText();
    }

    public void setPasswordText(String passwordTextField) {
        this.passwordTextField.setText(passwordTextField);
    }

    public String getPortText() {
        return portTextField.getText();
    }

    public void setPortTextField(String portTextField) {
        this.portTextField.setText(portTextField);
    }

    public String getUserNameText() {
        return userNameTextField.getText();
    }

    public void setUserNameText(String userNameTextField) {
        this.userNameTextField.setText(userNameTextField);
    }

    public String getWebServiceURLText() {
        return webServiceURLTextField.getText();
    }

    public void setWebServiceURLTextField(String webServiceURLTextField) {
        this.webServiceURLTextField.setText(webServiceURLTextField);
    }

    public JTextField getHostNameTextField() {
        return hostNameTextField;
    }

    public JTextField getIpAddressTextField() {
        return ipAddressTextField;
    }

    public JTextField getPortTextField() {
        return portTextField;
    }

    public JTextField getWebServiceURLTextField() {
        return webServiceURLTextField;
    }
   
    
    
    public JCheckBox getUserNameCheckBox(){
        return this.juserNameCheckBox;
    }

    public JCheckBox getPasswordCheckBox(){
        return this.juserNameCheckBox;
    }
    
    public JTextField getUserNameTextField(){
        return this.userNameTextField;
    }

    public JTextField getPasswordTextField(){
        return this.passwordTextField;
    }
    
    /** This method is called from within the constructor to
     * initialize the panel.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ipAddressLabel = new javax.swing.JLabel();
        ipAddressTextField = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        portTextField = new javax.swing.JTextField();
        hostNameLabel = new javax.swing.JLabel();
        hostNameTextField = new javax.swing.JTextField();
        webServiceURLLabel = new javax.swing.JLabel();
        webServiceURLTextField = new javax.swing.JTextField();
        userNameLabel = new javax.swing.JLabel();
        userNameTextField = new javax.swing.JTextField();
        passwordLabel = new javax.swing.JLabel();
        passwordTextField = new javax.swing.JTextField();
        juserNameCheckBox = new javax.swing.JCheckBox();
        jpasswordCheckBox = new javax.swing.JCheckBox();

        setBorder(javax.swing.BorderFactory.createTitledBorder("SUT Settings"));

        ipAddressLabel.setText("IP Address");

        ipAddressTextField.setText("127.0.0.1");
        ipAddressTextField.setToolTipText("The SUT IP Address");

        portLabel.setText("Port");

        portTextField.setText("8004");
        portTextField.setToolTipText("The SUT Port");

        hostNameLabel.setText("Host Name");

        hostNameTextField.setText("LocalHost");
        hostNameTextField.setToolTipText("The SUT Host Name");

        webServiceURLLabel.setText("Web Service URL");

        webServiceURLTextField.setText("http://127.0.0.1/webaddress");
        webServiceURLTextField.setToolTipText("The address for locating a Web Service");

        userNameLabel.setText("User Name");

        userNameTextField.setText("twilson");
        userNameTextField.setToolTipText("User Name for SUT Access");

        passwordLabel.setText("Password");

        passwordTextField.setText("password1");
        passwordTextField.setToolTipText("Password for SUT Access");

        juserNameCheckBox.setText("User Name Required");
        juserNameCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                juserNameCheckBoxActionPerformed(evt);
            }
        });

        jpasswordCheckBox.setText("Password Required");
        jpasswordCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jpasswordCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jpasswordCheckBox)
                        .addContainerGap())
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(portLabel)
                                .addComponent(hostNameLabel)
                                .addComponent(webServiceURLLabel)
                                .addComponent(ipAddressLabel)
                                .addComponent(userNameLabel)
                                .addComponent(passwordLabel))
                            .addGap(21, 21, 21)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(passwordTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                .addComponent(userNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                .addComponent(ipAddressTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                .addComponent(webServiceURLTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                .addComponent(hostNameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                .addComponent(portTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                            .addGap(36, 36, 36))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(juserNameCheckBox)
                            .addContainerGap(305, Short.MAX_VALUE)))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipAddressLabel)
                    .addComponent(ipAddressTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(hostNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hostNameLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(webServiceURLTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(webServiceURLLabel))
                .addGap(18, 18, 18)
                .addComponent(juserNameCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(userNameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(userNameLabel))
                .addGap(11, 11, 11)
                .addComponent(jpasswordCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("SUT Settings Panel");
        getAccessibleContext().setAccessibleDescription("Contains the set of SUT test parameters.");
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Juser name check box action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void juserNameCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_juserNameCheckBoxActionPerformed
        if(this.juserNameCheckBox.isSelected()){
            this.userNameTextField.setEnabled(true);
        } else {
           this.userNameTextField.setEnabled(false);
           this.userNameTextField.setText("");
        }
    }//GEN-LAST:event_juserNameCheckBoxActionPerformed

    /**
     * Jpassword check box action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void jpasswordCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jpasswordCheckBoxActionPerformed
        if(this.jpasswordCheckBox.isSelected()){
            this.passwordTextField.setEnabled(true);
        } else {
           this.passwordTextField.setEnabled(false);
           this.passwordTextField.setText("");
        }
    }//GEN-LAST:event_jpasswordCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel hostNameLabel;
    protected javax.swing.JTextField hostNameTextField;
    private javax.swing.JLabel ipAddressLabel;
    protected javax.swing.JTextField ipAddressTextField;
    public javax.swing.JCheckBox jpasswordCheckBox;
    public javax.swing.JCheckBox juserNameCheckBox;
    private javax.swing.JLabel passwordLabel;
    protected javax.swing.JTextField passwordTextField;
    private javax.swing.JLabel portLabel;
    protected javax.swing.JTextField portTextField;
    private javax.swing.JLabel userNameLabel;
    protected javax.swing.JTextField userNameTextField;
    private javax.swing.JLabel webServiceURLLabel;
    protected javax.swing.JTextField webServiceURLTextField;
    // End of variables declaration//GEN-END:variables
}
