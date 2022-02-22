
/*
 * LoggingPanel.java - Contains the LoggingPanel Class definition.
 *
 * Created on Feb 12, 2010, 7:44:37 PM
 */
package org.fhwa.c2cri.gui;

/**
 * This class provides the panel view used by the user to designate logging options.
 * Control for this view is provided by a seperate coordinator (control) class.
 *
 * @author TransCore ITS, LLC
 * @deprecated
 * Last Updated:  1/8/2014
 */
public class LoggingPanel extends javax.swing.JPanel {

    /** 
     * Creates new panel LoggingPanel.
     * 
     * First the initComponents method is called to create the interface.
     * Then the radio buttons for the different log options are added to the loggingButtonGroup object.
     */
    public LoggingPanel() {
        initComponents();
        loggingButtonGroup.add(logFailureMessagesRadioButton);
        loggingButtonGroup.add(logMessagesRadioButton);
        loggingButtonGroup.add(logMsgandActionsRadioButton);

    }

    /** This method is called from within the constructor to
     * initialize the panel.
     * This method also sets the default values for the fields.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loggingButtonGroup = new javax.swing.ButtonGroup();
        loggingOptionsPanel = new javax.swing.JPanel();
        logMessagesRadioButton = new javax.swing.JRadioButton();
        logMsgandActionsRadioButton = new javax.swing.JRadioButton();
        logFailureMessagesRadioButton = new javax.swing.JRadioButton();

        loggingOptionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Logging Options"));
        loggingOptionsPanel.setPreferredSize(new java.awt.Dimension(690, 570));

        logMessagesRadioButton.setText("Log all messages");

        logMsgandActionsRadioButton.setSelected(true);
        logMsgandActionsRadioButton.setText("Log all messages and actions");

        logFailureMessagesRadioButton.setText("Log only failure messages");

        javax.swing.GroupLayout loggingOptionsPanelLayout = new javax.swing.GroupLayout(loggingOptionsPanel);
        loggingOptionsPanel.setLayout(loggingOptionsPanelLayout);
        loggingOptionsPanelLayout.setHorizontalGroup(
            loggingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loggingOptionsPanelLayout.createSequentialGroup()
                .addGap(86, 86, 86)
                .addGroup(loggingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logFailureMessagesRadioButton)
                    .addComponent(logMsgandActionsRadioButton)
                    .addComponent(logMessagesRadioButton))
                .addContainerGap(425, Short.MAX_VALUE))
        );
        loggingOptionsPanelLayout.setVerticalGroup(
            loggingOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(loggingOptionsPanelLayout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(logMsgandActionsRadioButton)
                .addGap(18, 18, 18)
                .addComponent(logMessagesRadioButton)
                .addGap(18, 18, 18)
                .addComponent(logFailureMessagesRadioButton)
                .addContainerGap(401, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(loggingOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(loggingOptionsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    /** The log failure messages radio button. */
    protected javax.swing.JRadioButton logFailureMessagesRadioButton;
    
    /** The log messages radio button. */
    protected javax.swing.JRadioButton logMessagesRadioButton;
    
    /** The log msgand actions radio button. */
    protected javax.swing.JRadioButton logMsgandActionsRadioButton;
    
    /** The logging button group. */
    private javax.swing.ButtonGroup loggingButtonGroup;
    
    /** The logging options panel. */
    private javax.swing.JPanel loggingOptionsPanel;
    // End of variables declaration//GEN-END:variables
}
