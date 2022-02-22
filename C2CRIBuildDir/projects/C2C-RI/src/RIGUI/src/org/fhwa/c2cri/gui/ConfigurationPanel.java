
/*
 * ConfigurationPanel.java - Contains the Configuration Panel class which
 * provides a view and access to a subset of the test configuration information.
 *
 * Created on Jan 24, 2010, 10:30:40 PM
 */
package org.fhwa.c2cri.gui;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Defines the components present on the Config Tab.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ConfigurationPanel extends javax.swing.JPanel {

    /**
     * Creates new form ConfigurationPanel.
     */
    public ConfigurationPanel() {
        initComponents();
        riModeButtonGroup.add(this.externalModeRadioButton);
        riModeButtonGroup.add(this.ownerModeRadioButton);
    }

    public String getAppLayerAuthenticationLabel() {
        return appLayerAuthenticationLabel.getText();
    }

    public void setAppLayerAuthenticationLabel(String appLayerAuthenticationLabel) {
        this.appLayerAuthenticationLabel.setText(appLayerAuthenticationLabel);
    }

    public String getAppLayerTestSuiteDescriptionTextArea() {
        return appLayerTestSuiteDescriptionTextArea.getText();
    }

    public void setAppLayerTestSuiteDescriptionTextArea(String appLayerTestSuiteDescriptionTextArea) {
        this.appLayerTestSuiteDescriptionTextArea.setText(appLayerTestSuiteDescriptionTextArea);
    }

    public String getAppLayerTestSuiteTextField() {
        return appLayerTestSuiteTextField.toString();
    }

    public void setAppLayerTestSuiteTextField(String appLayerTestSuiteTextField) {
        this.appLayerTestSuiteTextField.setText(appLayerTestSuiteTextField);
    }

    public String getAppLayerTextField() {
        return appLayerTextField.getText();
    }

    public void setAppLayerTextField(String appLayerTextField) {
        this.appLayerTextField.setText(appLayerTextField);
    }

    public String getChecksumTextArea() {
        return checksumTextArea.getText();
    }

    public void setChecksumTextArea(String checksumTextArea) {
        this.checksumTextArea.setText(checksumTextArea);
    }

    public String getConfigDescriptionTextArea() {
        return configDescriptionTextArea.getText();
    }

    public void setConfigDescriptionTextArea(String configDescriptionTextArea) {
        this.configDescriptionTextArea.setText(configDescriptionTextArea);
    }

    public String getConfigNameTextField() {
        return configNameTextField.getText();
    }

    public void setConfigNameTextField(String configNameTextField) {
        this.configNameTextField.setText(configNameTextField);
    }

    public boolean getExternalModeRadioButton() {
        return externalModeRadioButton.isSelected();
    }

    public void setExternalModeRadioButton(boolean externalModeRadioButton) {
        this.externalModeRadioButton.setSelected(externalModeRadioButton);
    }

    public String getInfoLayerAuthenticationLabel() {
        return infoLayerAuthenticationLabel.getText();
    }

    public void setInfoLayerAuthenticationLabel(String infoLayerAuthenticationLabel) {
        this.infoLayerAuthenticationLabel.setText(infoLayerAuthenticationLabel);
    }

    public String getInfoLayerTestSuiteDescriptionTextArea() {
        return infoLayerTestSuiteDescriptionTextArea.getText();
    }

    public void setInfoLayerTestSuiteDescriptionTextArea(String infoLayerTestSuiteDescriptionTextArea) {
        this.infoLayerTestSuiteDescriptionTextArea.setText(infoLayerTestSuiteDescriptionTextArea);
    }

    public String getInfoLayerTestSuiteTextField() {
        return infoLayerTestSuiteTextField.getText();
    }

    public void setInfoLayerTestSuiteTextField(String infoLayerTestSuiteTextField) {
        this.infoLayerTestSuiteTextField.setText(infoLayerTestSuiteTextField);
    }

    public String getInfoLayerTextField() {
        return infoLayerTextField.getText();
    }

    public void setInfoLayerTextField(String infoLayerTextField) {
        this.infoLayerTextField.setText(infoLayerTextField);
    }

    public boolean getOwnerModeRadioButton() {
        return ownerModeRadioButton.isSelected();
    }

    public void setOwnerModeRadioButton(boolean ownerModeRadioButton) {
        this.ownerModeRadioButton.setSelected(ownerModeRadioButton);
    }   
    
    public void setAppLayerTestSuitePredefined(boolean predefined){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Object contrastResult = toolkit.getDesktopProperty( "win.highContrast.on" );        
        Boolean highContrast = contrastResult == null?false:(Boolean)toolkit.getDesktopProperty( "win.highContrast.on" );
        if (predefined) {
            if (highContrast){
                appLayerAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                appLayerAuthenticationLabel.setForeground(Color.black);                
            }
            appLayerAuthenticationLabel.setText("Predefined Test Suite");            
        } else {
            if (highContrast){
                appLayerAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                appLayerAuthenticationLabel.setForeground(Color.red);                
            }
            appLayerAuthenticationLabel.setText("User Defined Test Suite");
        }
               
    }
    
    public void setInfoLayerTestSuitePredefined(boolean predefined){
         Toolkit toolkit = Toolkit.getDefaultToolkit();
        Object contrastResult = toolkit.getDesktopProperty( "win.highContrast.on" );        
        Boolean highContrast = contrastResult == null?false:(Boolean)toolkit.getDesktopProperty( "win.highContrast.on" );
        if (predefined) {
            if (highContrast){
                infoLayerAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                infoLayerAuthenticationLabel.setForeground(Color.black);                
            }
            infoLayerAuthenticationLabel.setText("Predefined Test Suite");            
        } else {
            if (highContrast){
                infoLayerAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                infoLayerAuthenticationLabel.setForeground(Color.red);                
            }
            infoLayerAuthenticationLabel.setText("User Defined Test Suite");
        }
       
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

        riModeButtonGroup = new javax.swing.ButtonGroup();
        configPanel = new javax.swing.JPanel();
        configurationNamePanel = new javax.swing.JPanel();
        testConfigNameLabel = new javax.swing.JLabel();
        configNameTextField = new javax.swing.JTextField();
        testConfigDescriptionLabel = new javax.swing.JLabel();
        configDescriptionScrollPanel = new javax.swing.JScrollPane();
        configDescriptionTextArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        checksumTextArea = new javax.swing.JTextArea();
        testSuitePanel = new javax.swing.JPanel();
        appLayerStandardPanel = new javax.swing.JPanel();
        testSuiteNameLabel1 = new javax.swing.JLabel();
        appLayerTestSuiteTextField = new javax.swing.JTextField();
        appLayerStandardLabel = new javax.swing.JLabel();
        appLayerTextField = new javax.swing.JTextField();
        testSuiteDescriptionLabel1 = new javax.swing.JLabel();
        testSuiteDescriptionScrollPane1 = new javax.swing.JScrollPane();
        appLayerTestSuiteDescriptionTextArea = new javax.swing.JTextArea();
        appLayerAuthenticationLabel = new javax.swing.JLabel();
        infoLayerStandardPanel = new javax.swing.JPanel();
        testSuiteNameLabel = new javax.swing.JLabel();
        infoLayerTestSuiteTextField = new javax.swing.JTextField();
        infoLayerStandardLabel = new javax.swing.JLabel();
        infoLayerTextField = new javax.swing.JTextField();
        testSuiteDescriptionScrollPane = new javax.swing.JScrollPane();
        infoLayerTestSuiteDescriptionTextArea = new javax.swing.JTextArea();
        testSuiteDescriptionLabel = new javax.swing.JLabel();
        infoLayerAuthenticationLabel = new javax.swing.JLabel();
        riModePanel = new javax.swing.JPanel();
        externalModeRadioButton = new javax.swing.JRadioButton();
        ownerModeRadioButton = new javax.swing.JRadioButton();

        setPreferredSize(new java.awt.Dimension(682, 476));
        setLayout(new java.awt.CardLayout());

        configPanel.setMinimumSize(new java.awt.Dimension(662, 431));
        configPanel.setPreferredSize(new java.awt.Dimension(662, 481));

        configurationNamePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Test Configuration"));

        testConfigNameLabel.setText("Name:");

        configNameTextField.setEditable(false);
        configNameTextField.setText("C2CTestConfig1");
        configNameTextField.setToolTipText("Current Test Configuration");

        testConfigDescriptionLabel.setText("<html><P ALIGN=\"right\">Test Configuration Description:</P></html>");

        configDescriptionTextArea.setColumns(20);
        configDescriptionTextArea.setLineWrap(true);
        configDescriptionTextArea.setRows(2);
        configDescriptionTextArea.setText("This is the first test of the TMDDv3\nStandard.");
        configDescriptionTextArea.setToolTipText("Enter description for the Configuration");
        configDescriptionTextArea.setWrapStyleWord(true);
        configDescriptionTextArea.setMaximumSize(getPreferredSize());
        configDescriptionTextArea.setNextFocusableComponent(checksumTextArea);
        configDescriptionScrollPanel.setViewportView(configDescriptionTextArea);

        jLabel1.setText("Version CheckSum:");

        checksumTextArea.setEditable(false);
        checksumTextArea.setBackground(new java.awt.Color(236, 233, 216));
        checksumTextArea.setColumns(20);
        checksumTextArea.setLineWrap(true);
        checksumTextArea.setRows(1);
        checksumTextArea.setToolTipText("Checksum of the configuration file.");
        checksumTextArea.setWrapStyleWord(true);
        checksumTextArea.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        checksumTextArea.setNextFocusableComponent(infoLayerTestSuiteTextField);

        javax.swing.GroupLayout configurationNamePanelLayout = new javax.swing.GroupLayout(configurationNamePanel);
        configurationNamePanel.setLayout(configurationNamePanelLayout);
        configurationNamePanelLayout.setHorizontalGroup(
            configurationNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, configurationNamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(configurationNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(testConfigNameLabel)
                    .addComponent(testConfigDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(configurationNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(configDescriptionScrollPanel)
                    .addComponent(configNameTextField)
                    .addComponent(checksumTextArea))
                .addContainerGap())
        );
        configurationNamePanelLayout.setVerticalGroup(
            configurationNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(configurationNamePanelLayout.createSequentialGroup()
                .addGroup(configurationNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testConfigNameLabel)
                    .addComponent(configNameTextField))
                .addGap(3, 3, 3)
                .addGroup(configurationNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(configDescriptionScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                    .addGroup(configurationNamePanelLayout.createSequentialGroup()
                        .addComponent(testConfigDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(configurationNamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(checksumTextArea, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        configNameTextField.getAccessibleContext().setAccessibleName("Configuration Name");
        checksumTextArea.getAccessibleContext().setAccessibleName("Checksum of the configuration file");
        checksumTextArea.getAccessibleContext().setAccessibleDescription("");

        testSuitePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Test Suite"));

        appLayerStandardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Application Layer Standard"));
        appLayerStandardPanel.setPreferredSize(new java.awt.Dimension(478, 162));

        testSuiteNameLabel1.setText(" Test Suite Name");

        appLayerTestSuiteTextField.setEditable(false);
        appLayerTestSuiteTextField.setText("jTextField1");
        appLayerTestSuiteTextField.setToolTipText("The selected Application Layer Test Suite.");

        appLayerStandardLabel.setText(" Standard Name");

        appLayerTextField.setEditable(false);
        appLayerTextField.setText("jTextField1");

        testSuiteDescriptionLabel1.setText("Description");

        appLayerTestSuiteDescriptionTextArea.setEditable(false);
        appLayerTestSuiteDescriptionTextArea.setColumns(20);
        appLayerTestSuiteDescriptionTextArea.setLineWrap(true);
        appLayerTestSuiteDescriptionTextArea.setRows(2);
        appLayerTestSuiteDescriptionTextArea.setToolTipText("Description of the application layer test suite.");
        appLayerTestSuiteDescriptionTextArea.setWrapStyleWord(true);
        appLayerTestSuiteDescriptionTextArea.setName(""); // NOI18N
        appLayerTestSuiteDescriptionTextArea.setOpaque(false);
        testSuiteDescriptionScrollPane1.setViewportView(appLayerTestSuiteDescriptionTextArea);
        appLayerTestSuiteDescriptionTextArea.getAccessibleContext().setAccessibleName("Application Layer Test Suite Description");
        appLayerTestSuiteDescriptionTextArea.getAccessibleContext().setAccessibleDescription("");

        appLayerAuthenticationLabel.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        appLayerAuthenticationLabel.setText("jLabel1");

        javax.swing.GroupLayout appLayerStandardPanelLayout = new javax.swing.GroupLayout(appLayerStandardPanel);
        appLayerStandardPanel.setLayout(appLayerStandardPanelLayout);
        appLayerStandardPanelLayout.setHorizontalGroup(
            appLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appLayerStandardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(appLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(appLayerStandardPanelLayout.createSequentialGroup()
                        .addComponent(testSuiteDescriptionLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(242, 242, 242))
                    .addGroup(appLayerStandardPanelLayout.createSequentialGroup()
                        .addComponent(appLayerAuthenticationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(247, 247, 247))
                    .addGroup(appLayerStandardPanelLayout.createSequentialGroup()
                        .addGroup(appLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(testSuiteNameLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, appLayerStandardPanelLayout.createSequentialGroup()
                                .addComponent(appLayerTestSuiteTextField)
                                .addGap(5, 5, 5)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(appLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appLayerTextField)
                            .addComponent(appLayerStandardLabel))
                        .addGap(16, 16, 16))
                    .addComponent(testSuiteDescriptionScrollPane1)))
        );
        appLayerStandardPanelLayout.setVerticalGroup(
            appLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appLayerStandardPanelLayout.createSequentialGroup()
                .addGroup(appLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testSuiteNameLabel1)
                    .addComponent(appLayerStandardLabel))
                .addGap(3, 3, 3)
                .addGroup(appLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(appLayerTestSuiteTextField)
                    .addComponent(appLayerTextField))
                .addGap(3, 3, 3)
                .addComponent(appLayerAuthenticationLabel)
                .addGap(3, 3, 3)
                .addComponent(testSuiteDescriptionLabel1)
                .addGap(3, 3, 3)
                .addComponent(testSuiteDescriptionScrollPane1))
        );

        appLayerTestSuiteTextField.getAccessibleContext().setAccessibleName("The name of the application layer test suite.");
        appLayerTextField.getAccessibleContext().setAccessibleName("Application Layer Standard Name");
        appLayerAuthenticationLabel.getAccessibleContext().setAccessibleName("Predefined test suite indicator label.");

        infoLayerStandardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Information Layer Standard"));
        infoLayerStandardPanel.setName("Test"); // NOI18N
        infoLayerStandardPanel.setPreferredSize(new java.awt.Dimension(478, 162));

        testSuiteNameLabel.setText(" Test Suite Name");

        infoLayerTestSuiteTextField.setEditable(false);
        infoLayerTestSuiteTextField.setText("jTextField1");
        infoLayerTestSuiteTextField.setToolTipText("");

        infoLayerStandardLabel.setText("Standard Name");

        infoLayerTextField.setEditable(false);
        infoLayerTextField.setText("jTextField1");

        infoLayerTestSuiteDescriptionTextArea.setEditable(false);
        infoLayerTestSuiteDescriptionTextArea.setColumns(20);
        infoLayerTestSuiteDescriptionTextArea.setLineWrap(true);
        infoLayerTestSuiteDescriptionTextArea.setRows(2);
        infoLayerTestSuiteDescriptionTextArea.setWrapStyleWord(true);
        infoLayerTestSuiteDescriptionTextArea.setOpaque(false);
        testSuiteDescriptionScrollPane.setViewportView(infoLayerTestSuiteDescriptionTextArea);
        infoLayerTestSuiteDescriptionTextArea.getAccessibleContext().setAccessibleName("Description of the test suite");

        testSuiteDescriptionLabel.setText("Description");

        infoLayerAuthenticationLabel.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        infoLayerAuthenticationLabel.setText("jLabel1");

        javax.swing.GroupLayout infoLayerStandardPanelLayout = new javax.swing.GroupLayout(infoLayerStandardPanel);
        infoLayerStandardPanel.setLayout(infoLayerStandardPanelLayout);
        infoLayerStandardPanelLayout.setHorizontalGroup(
            infoLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoLayerStandardPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(infoLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testSuiteDescriptionScrollPane)
                    .addGroup(infoLayerStandardPanelLayout.createSequentialGroup()
                        .addGroup(infoLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(infoLayerStandardPanelLayout.createSequentialGroup()
                                .addComponent(testSuiteDescriptionLabel)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoLayerStandardPanelLayout.createSequentialGroup()
                                .addGroup(infoLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(infoLayerAuthenticationLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(testSuiteNameLabel, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(infoLayerTestSuiteTextField, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(18, 18, 18)
                                .addGroup(infoLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(infoLayerStandardLabel)
                                    .addComponent(infoLayerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())))
        );
        infoLayerStandardPanelLayout.setVerticalGroup(
            infoLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(infoLayerStandardPanelLayout.createSequentialGroup()
                .addGroup(infoLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(infoLayerStandardLabel)
                    .addComponent(testSuiteNameLabel))
                .addGap(3, 3, 3)
                .addGroup(infoLayerStandardPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(infoLayerTextField)
                    .addComponent(infoLayerTestSuiteTextField))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(infoLayerAuthenticationLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testSuiteDescriptionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testSuiteDescriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(6, 6, 6))
        );

        infoLayerTestSuiteTextField.getAccessibleContext().setAccessibleName("The name of the Test Suite");
        infoLayerTextField.getAccessibleContext().setAccessibleName("The name of the Infomation Layer Standard");
        infoLayerAuthenticationLabel.getAccessibleContext().setAccessibleName("Predefined Test Suite Indicator");

        javax.swing.GroupLayout testSuitePanelLayout = new javax.swing.GroupLayout(testSuitePanel);
        testSuitePanel.setLayout(testSuitePanelLayout);
        testSuitePanelLayout.setHorizontalGroup(
            testSuitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testSuitePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(testSuitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoLayerStandardPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appLayerStandardPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        testSuitePanelLayout.setVerticalGroup(
            testSuitePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testSuitePanelLayout.createSequentialGroup()
                .addComponent(infoLayerStandardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addGap(3, 3, 3)
                .addComponent(appLayerStandardPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        riModePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("RI Mode"));

        externalModeRadioButton.setSelected(true);
        externalModeRadioButton.setText("External Center");
        externalModeRadioButton.setToolTipText("Execute testing as External Center");

        ownerModeRadioButton.setText("Owner Center");
        ownerModeRadioButton.setToolTipText("Execute testing as Owner Center");

        javax.swing.GroupLayout riModePanelLayout = new javax.swing.GroupLayout(riModePanel);
        riModePanel.setLayout(riModePanelLayout);
        riModePanelLayout.setHorizontalGroup(
            riModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(riModePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(riModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(riModePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(externalModeRadioButton))
                    .addGroup(riModePanelLayout.createSequentialGroup()
                        .addComponent(ownerModeRadioButton)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        riModePanelLayout.setVerticalGroup(
            riModePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(riModePanelLayout.createSequentialGroup()
                .addComponent(externalModeRadioButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ownerModeRadioButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout configPanelLayout = new javax.swing.GroupLayout(configPanel);
        configPanel.setLayout(configPanelLayout);
        configPanelLayout.setHorizontalGroup(
            configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, configPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(configurationNamePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(testSuitePanel, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(riModePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        configPanelLayout.setVerticalGroup(
            configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, configPanelLayout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(configurationNamePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(configPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(configPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(riModePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(264, 264, 264))
                    .addGroup(configPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(testSuitePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(3, 3, 3))))
        );

        add(configPanel, "card3");
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JLabel appLayerAuthenticationLabel;
    private javax.swing.JLabel appLayerStandardLabel;
    private javax.swing.JPanel appLayerStandardPanel;
    protected javax.swing.JTextArea appLayerTestSuiteDescriptionTextArea;
    protected javax.swing.JTextField appLayerTestSuiteTextField;
    protected javax.swing.JTextField appLayerTextField;
    public javax.swing.JTextArea checksumTextArea;
    private javax.swing.JScrollPane configDescriptionScrollPanel;
    protected javax.swing.JTextArea configDescriptionTextArea;
    protected javax.swing.JTextField configNameTextField;
    private javax.swing.JPanel configPanel;
    private javax.swing.JPanel configurationNamePanel;
    protected javax.swing.JRadioButton externalModeRadioButton;
    protected javax.swing.JLabel infoLayerAuthenticationLabel;
    private javax.swing.JLabel infoLayerStandardLabel;
    private javax.swing.JPanel infoLayerStandardPanel;
    protected javax.swing.JTextArea infoLayerTestSuiteDescriptionTextArea;
    protected javax.swing.JTextField infoLayerTestSuiteTextField;
    protected javax.swing.JTextField infoLayerTextField;
    private javax.swing.JLabel jLabel1;
    protected javax.swing.JRadioButton ownerModeRadioButton;
    private javax.swing.ButtonGroup riModeButtonGroup;
    private javax.swing.JPanel riModePanel;
    private javax.swing.JLabel testConfigDescriptionLabel;
    private javax.swing.JLabel testConfigNameLabel;
    private javax.swing.JLabel testSuiteDescriptionLabel;
    private javax.swing.JLabel testSuiteDescriptionLabel1;
    private javax.swing.JScrollPane testSuiteDescriptionScrollPane;
    private javax.swing.JScrollPane testSuiteDescriptionScrollPane1;
    private javax.swing.JLabel testSuiteNameLabel;
    private javax.swing.JLabel testSuiteNameLabel1;
    private javax.swing.JPanel testSuitePanel;
    // End of variables declaration//GEN-END:variables
}
