/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TestConfigCreateUI.java
 *
 * Created on Mar 8, 2010, 5:53:29 PM
 */
package org.fhwa.c2cri.gui.wizard.testconfig.create.page;

//import org.fhwa.c2cri.utilities.ProgressReporter;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import org.fhwa.c2cri.testmodel.TestSuites;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import org.fhwa.c2cri.domain.testmodel.TestConfigurationController;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPage;
import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.utilities.FilenameValidator;
//import org.fhwa.c2cri.utilities.Parameter;
//import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestConfigCreateUI.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestConfigCreatePage extends C2CRIWizardPage {

    private TestConfigurationController controller;
    /** The test config name. */
    private String testConfigName;
    
    /** The test config path. */
    private String testConfigPath;
    
    /** The test description. */
    private String testDescription;
    
    /** The info layer test suite selected. */
    private String infoLayerTestSuiteSelected;
    
    /** The app layer test suite selected. */
    private String appLayerTestSuiteSelected;

    /**
     * Gets the test config name.
     *
     * @return the testConfigName
     */
    public String getTestConfigName() {
        return testConfigName;
    }

    /**
     * Gets the test config path.
     *
     * @return the testConfigPath
     */
    public String getTestConfigPath() {
        return testConfigPath;
    }

    /**
     * Gets the test description.
     *
     * @return the Test Description
     */
    public String getTestDescription() {
        return testDescription;
    }

    /**
     * Gets the info layer test suite selected.
     *
     * @return the Information Layer Test Suite Selected
     */
    public String getInfoLayerTestSuiteSelected() {
        return infoLayerTestSuiteSelected;
    }

    /**
     * Gets the app layer test suite selected.
     *
     * @return the Application Layer Test Suite Selected
     */
    public String getAppLayerTestSuiteSelected() {
        return appLayerTestSuiteSelected;
    }


    /**
     * Creates new form TestConfigCreateUI.
     *
     * @param parent  the parent frame for this object
     * @param modal indicates whether the dialog should be modal
     */
    public TestConfigCreatePage(String title, String description, String default_Path, TestConfigurationController controller){
        super(title,description);
        initComponents();
        
        this.controller = controller;
        infoLayerTestSuiteComboBox.removeAllItems();
        ArrayList availableInfoSuites = TestSuites.getInstance().getAvailableInfoLayerSuites();
        for (int ii = 0; ii < availableInfoSuites.size(); ii++) {
            infoLayerTestSuiteComboBox.addItem(availableInfoSuites.get(ii));
        }

        setInfoAuthenticationLabel();
        appLayerTestSuiteComboBox.removeAllItems();
        ArrayList availableAppSuites = TestSuites.getInstance().getAvailableAppLayerSuites();
        for (int ii = 0; ii < availableAppSuites.size(); ii++) {
            appLayerTestSuiteComboBox.addItem(availableAppSuites.get(ii));
        }
        setAppAuthenticationLabel();

        this.testConfigurationTextField.setText("");
        this.testConfigurationTextField.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    String newName = testConfigurationTextField.getText();
                    validateFileName(newName, false);
                }
            });
        
        
        this.testConfigDescriptionTextArea.setText("");

        File dir = new File(default_Path);
        System.out.println(" Path = " + default_Path + " is valid? " + dir.exists());

        this.pathSelectedTextField.setText(dir.getPath());
        this.testConfigPath = this.pathSelectedTextField.getText();

        this.appLayerTextField.setText(TestSuites.getInstance().getAppLayerStandard((String) this.appLayerTestSuiteComboBox.getSelectedItem()));
        this.infoLayerTextField.setText(TestSuites.getInstance().getInfoLayerStandard((String) this.infoLayerTestSuiteComboBox.getSelectedItem()));
        this.infoLayerTestSuiteDescriptionTextArea.setText(TestSuites.getInstance().getDescription((String) this.infoLayerTestSuiteComboBox.getSelectedItem()));
        this.appLayerTestSuiteDescriptionTextArea.setText(TestSuites.getInstance().getDescription((String) this.appLayerTestSuiteComboBox.getSelectedItem()));
        
        this.setNextEnabled(false);
        this.setFinishEnabled(false);
        this.validate();
    }
    
            /**
             * Do not allow the next button to be enabled at the start. The page
             * will update the button when it is determined to be valid.
             */
            public void rendering(List<WizardPage> path, WizardSettings settings) {
                super.rendering(path, settings);
                if (validateFileName(this.pathSelectedTextField.getText(), true)){
                    setNextEnabled(true);                                        
                    this.setFinishEnabled(false);
                } else {
                    setNextEnabled(false);                    
                    this.setFinishEnabled(false);
                }
            }    
    
    private boolean validateFileName(String file, boolean silentMode){
        if (file.isBlank()){
            this.setNextEnabled(false);
            this.setFinishEnabled(false);            
            return false;
        }
        
        String fileName = this.pathSelectedTextField.getText()+"\\"+file+".ricfg";
        FilenameValidator thisValidator = new FilenameValidator();
        if (!thisValidator.validate(file)) {
            //Pop up the message dialog.
            String message = "Invalid file name entered "+fileName+"\n"
//                    + "Valid file names include 1 to 35 characters with any lower or upper case character, digit or special symbol ' ', _ and - only."
                    +thisValidator.getErrorsEncountered()
                    + "\nPlease try again.";

           if (!silentMode) javax.swing.JOptionPane.showMessageDialog(null, //no owner frame
                    message, //text to display
                    "Invalid File Name", //title
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            this.setNextEnabled(false);
            this.setFinishEnabled(false);            
            return false;
        } else {
            try {
                File f = new File(fileName);
                if (f.exists()) {                    
                    int result = javax.swing.JOptionPane.showConfirmDialog(null, "The configuration file configFile already exists.  Would you like to overwrite the file?", "Warning", javax.swing.JOptionPane.YES_NO_OPTION);
                    if (result == javax.swing.JOptionPane.YES_OPTION) {
                      this.setNextEnabled(true);  
                      this.setFinishEnabled(false);                      
                      return true;
                    }else if (result == javax.swing.JOptionPane.NO_OPTION) {
                      this.setNextEnabled(false);  
                      this.setFinishEnabled(false);                      
                      return false;
                    }
                        
//                    javax.swing.JOptionPane.showMessageDialog(null, "The Configuration File name already exists: " + f.getName(), "File Name Error", javax.swing.JOptionPane.ERROR_MESSAGE);
//                    this.setNextEnabled(false);                    
                    return true;
                } else {
                    f = new File(fileName+"_");                    
                    System.out.println(" Trying ..." + f.getPath() + "    " + f.getName());
                    try {                        
                        boolean b = f.createNewFile();
                        // if b is true , name may be correct
                        if (b) {
                            // because the file is created for checking validity
                            f.delete();
                            
                        }

                    } catch (IOException ioe) {
                        if (!silentMode)javax.swing.JOptionPane.showMessageDialog(null, "File Naming Error: " + ioe.getMessage(), "File Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        this.setNextEnabled(false);  
                        this.setFinishEnabled(false);                        
                        return false;                    
                    }


                }
            } catch (Exception e) {
                if (!silentMode)javax.swing.JOptionPane.showMessageDialog(null, "Invalid file name entered ("+fileName+") : " + e.getMessage(), "File Name Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                this.setNextEnabled(false);  
                this.setFinishEnabled(false);                
                return false; 
            }

        }
        this.setNextEnabled(true);
        this.setFinishEnabled(false);        
        return true;
    }     
    
    /**
     * Sets the info authentication label.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void setInfoAuthenticationLabel(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Object contrastResult = toolkit.getDesktopProperty( "win.highContrast.on" );        
        Boolean highContrast = contrastResult == null?false:(Boolean)toolkit.getDesktopProperty( "win.highContrast.on" );
        
        if (((String)infoLayerTestSuiteComboBox.getSelectedItem()).startsWith("*")){
            if (highContrast){
                this.infoStandardAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                this.infoStandardAuthenticationLabel.setForeground(Color.red);
            }
            this.infoStandardAuthenticationLabel.setText("User Defined Test Suite");
        }else {
            if (highContrast){
                this.infoStandardAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                this.infoStandardAuthenticationLabel.setForeground(Color.black);
            }
            this.infoStandardAuthenticationLabel.setText("Predefined Test Suite");
        }

    }

    /**
     * Sets the app authentication label.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void setAppAuthenticationLabel(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Object contrastResult = toolkit.getDesktopProperty( "win.highContrast.on" );        
        Boolean highContrast = contrastResult == null?false:(Boolean)toolkit.getDesktopProperty( "win.highContrast.on" );
        
        if (((String)appLayerTestSuiteComboBox.getSelectedItem()).startsWith("*")){
            if (highContrast){
                this.appStandardAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                this.appStandardAuthenticationLabel.setForeground(Color.red);
            }
            this.appStandardAuthenticationLabel.setText("User Defined Test Suite");
        }else {
            if (highContrast){
                this.appStandardAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                this.appStandardAuthenticationLabel.setForeground(Color.black);
            }
            this.appStandardAuthenticationLabel.setText("Predefined Test Suite");
        }


    }

    protected void clearTestNameField(){
        this.testConfigurationTextField.setText("");
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        testConfigurationFilePanel = new javax.swing.JPanel();
        testConfigurationTextField = new javax.swing.JTextField();
        testConfigDescriptionFieldjLabel = new javax.swing.JLabel();
        testConfigFieldLabel = new javax.swing.JLabel();
        testConfigTextScrollPane = new javax.swing.JScrollPane();
        testConfigDescriptionTextArea = new javax.swing.JTextArea();
        pathFieldLabel = new javax.swing.JLabel();
        browseButton = new javax.swing.JButton();
        pathSelectedTextField = new javax.swing.JTextField();
        testSuiteSelectionPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        infoLayerStandardFieldLabel = new javax.swing.JLabel();
        testSuiteFieldjLabel = new javax.swing.JLabel();
        testSuiteDescriptionFieldLabel = new javax.swing.JLabel();
        infoLayerTestSuiteComboBox = new javax.swing.JComboBox();
        testSuiteDescriptionScrollPane = new javax.swing.JScrollPane();
        infoLayerTestSuiteDescriptionTextArea = new javax.swing.JTextArea();
        infoLayerTextField = new javax.swing.JTextField();
        infoStandardAuthenticationLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        appLayerFieldjLabel = new javax.swing.JLabel();
        appLayerTextField = new javax.swing.JTextField();
        testSuiteDescriptionFieldLabel1 = new javax.swing.JLabel();
        testSuiteFieldjLabel1 = new javax.swing.JLabel();
        appLayerTestSuiteComboBox = new javax.swing.JComboBox();
        testSuiteDescriptionScrollPane1 = new javax.swing.JScrollPane();
        appLayerTestSuiteDescriptionTextArea = new javax.swing.JTextArea();
        appStandardAuthenticationLabel = new javax.swing.JLabel();

        testConfigurationFilePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Test Configuration File"));

        testConfigurationTextField.setText("TestConfiguration1");
        testConfigurationTextField.setToolTipText("Enter a valid file name without the .ricfg extension");
        testConfigurationTextField.setName("TestConfigurationFileName"); // NOI18N

        testConfigDescriptionFieldjLabel.setText("Test Configuration Description:");

        testConfigFieldLabel.setText("Test Configuration Name:");

        testConfigDescriptionTextArea.setColumns(20);
        testConfigDescriptionTextArea.setLineWrap(true);
        testConfigDescriptionTextArea.setRows(4);
        testConfigDescriptionTextArea.setText("Test a representative set of TMDD v3 requirements using NTCIP 2306 for encoding and transport.");
        testConfigDescriptionTextArea.setToolTipText("Enter a description for this test configuration");
        testConfigDescriptionTextArea.setWrapStyleWord(true);
        testConfigDescriptionTextArea.setName("TestConfigurationDescription"); // NOI18N
        testConfigTextScrollPane.setViewportView(testConfigDescriptionTextArea);

        pathFieldLabel.setText("Path:");

        browseButton.setText("Path ...");
        browseButton.setToolTipText("Set the path for the new file");
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        pathSelectedTextField.setText("jTextField1");
        pathSelectedTextField.setToolTipText("The path for the new configuration file");
        pathSelectedTextField.setMaximumSize(new java.awt.Dimension(6, 20));
        pathSelectedTextField.setName("TestConfigurationFilePath"); // NOI18N
        pathSelectedTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pathSelectedTextFieldActionPerformed(evt);
            }
        });
        pathSelectedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                pathSelectedTextFieldFocusLost(evt);
            }
        });

        javax.swing.GroupLayout testConfigurationFilePanelLayout = new javax.swing.GroupLayout(testConfigurationFilePanel);
        testConfigurationFilePanel.setLayout(testConfigurationFilePanelLayout);
        testConfigurationFilePanelLayout.setHorizontalGroup(
            testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testConfigurationFilePanelLayout.createSequentialGroup()
                .addGroup(testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testConfigDescriptionFieldjLabel)
                    .addGroup(testConfigurationFilePanelLayout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pathFieldLabel)
                            .addComponent(testConfigFieldLabel))))
                .addGap(18, 18, 18)
                .addGroup(testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(pathSelectedTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(testConfigurationTextField)
                    .addComponent(testConfigTextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(browseButton, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                .addContainerGap())
        );
        testConfigurationFilePanelLayout.setVerticalGroup(
            testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testConfigurationFilePanelLayout.createSequentialGroup()
                .addGroup(testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(testConfigurationFilePanelLayout.createSequentialGroup()
                        .addComponent(browseButton)
                        .addGap(5, 5, 5))
                    .addGroup(testConfigurationFilePanelLayout.createSequentialGroup()
                        .addGroup(testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(testConfigurationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(testConfigFieldLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(pathSelectedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pathFieldLabel))
                        .addGap(11, 11, 11)))
                .addGroup(testConfigurationFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(testConfigurationFilePanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(testConfigDescriptionFieldjLabel))
                    .addComponent(testConfigTextScrollPane))
                .addContainerGap())
        );

        testSuiteSelectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Test Suite Selection"));
        testSuiteSelectionPanel.setMaximumSize(new java.awt.Dimension(636, 305));
        testSuiteSelectionPanel.setPreferredSize(new java.awt.Dimension(636, 305));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Information Layer Standard"));
        jPanel1.setMaximumSize(new java.awt.Dimension(600, 134));
        jPanel1.setPreferredSize(new java.awt.Dimension(600, 134));

        infoLayerStandardFieldLabel.setText("Name:");

        testSuiteFieldjLabel.setText("Test Suite:");

        testSuiteDescriptionFieldLabel.setText("Description:");

        infoLayerTestSuiteComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TMDD v3 and NTCIP2306", "TMDD v3 and NTCIP 2304", "TMDD v3 and NTCIP 2306 *", " " }));
        infoLayerTestSuiteComboBox.setToolTipText("Select a Test Suite for this configuration");
        infoLayerTestSuiteComboBox.setMaximumSize(new java.awt.Dimension(138, 20));
        infoLayerTestSuiteComboBox.setMinimumSize(new java.awt.Dimension(138, 20));
        infoLayerTestSuiteComboBox.setName("SelectedInformationTestSuite"); // NOI18N
        infoLayerTestSuiteComboBox.setPreferredSize(new java.awt.Dimension(138, 20));
        infoLayerTestSuiteComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                infoLayerTestSuiteComboBoxItemStateChanged(evt);
            }
        });

        infoLayerTestSuiteDescriptionTextArea.setColumns(20);
        infoLayerTestSuiteDescriptionTextArea.setEditable(false);
        infoLayerTestSuiteDescriptionTextArea.setLineWrap(true);
        infoLayerTestSuiteDescriptionTextArea.setRows(3);
        infoLayerTestSuiteDescriptionTextArea.setText("Initial Test Suite provided by the C2C RI.");
        infoLayerTestSuiteDescriptionTextArea.setToolTipText("The description provided by the Test Suite");
        infoLayerTestSuiteDescriptionTextArea.setWrapStyleWord(true);
        infoLayerTestSuiteDescriptionTextArea.setMaximumSize(new java.awt.Dimension(164, 58));
        infoLayerTestSuiteDescriptionTextArea.setOpaque(false);
        testSuiteDescriptionScrollPane.setViewportView(infoLayerTestSuiteDescriptionTextArea);

        infoLayerTextField.setEditable(false);
        infoLayerTextField.setText("TMDD v3");
        infoLayerTextField.setToolTipText("The information layer standard addressed by the test suite.");
        infoLayerTextField.setMaximumSize(new java.awt.Dimension(138, 20));

        infoStandardAuthenticationLabel.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        infoStandardAuthenticationLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        infoStandardAuthenticationLabel.setFocusable(false);
        infoStandardAuthenticationLabel.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testSuiteFieldjLabel)
                    .addComponent(infoLayerStandardFieldLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(infoLayerTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addComponent(infoStandardAuthenticationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                    .addComponent(infoLayerTestSuiteComboBox, 0, 202, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(testSuiteDescriptionFieldLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(195, 195, 195))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(testSuiteDescriptionScrollPane)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(testSuiteFieldjLabel)
                            .addComponent(infoLayerTestSuiteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(infoStandardAuthenticationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(infoLayerStandardFieldLabel)
                            .addComponent(infoLayerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(testSuiteDescriptionFieldLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(testSuiteDescriptionScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Application Layer Standard"));
        jPanel2.setMaximumSize(new java.awt.Dimension(600, 134));

        appLayerFieldjLabel.setText("Name:");

        appLayerTextField.setEditable(false);
        appLayerTextField.setText("NTCIP 2306 v1.6");
        appLayerTextField.setToolTipText("The application layer standard addressed by the test suite");
        appLayerTextField.setMaximumSize(new java.awt.Dimension(138, 20));

        testSuiteDescriptionFieldLabel1.setText("Description:");

        testSuiteFieldjLabel1.setText("Test Suite:");

        appLayerTestSuiteComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TMDD v3 and NTCIP2306", "TMDD v3 and NTCIP 2304", "TMDD v3 and NTCIP 2306 *", " " }));
        appLayerTestSuiteComboBox.setToolTipText("Select a Test Suite for this configuration");
        appLayerTestSuiteComboBox.setMaximumSize(new java.awt.Dimension(138, 20));
        appLayerTestSuiteComboBox.setMinimumSize(new java.awt.Dimension(138, 20));
        appLayerTestSuiteComboBox.setName("SelectedApplicationTestSuite"); // NOI18N
        appLayerTestSuiteComboBox.setPreferredSize(new java.awt.Dimension(138, 20));
        appLayerTestSuiteComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                appLayerTestSuiteComboBoxItemStateChanged(evt);
            }
        });

        appLayerTestSuiteDescriptionTextArea.setColumns(20);
        appLayerTestSuiteDescriptionTextArea.setEditable(false);
        appLayerTestSuiteDescriptionTextArea.setLineWrap(true);
        appLayerTestSuiteDescriptionTextArea.setRows(3);
        appLayerTestSuiteDescriptionTextArea.setText("Initial Test Suite provided by the C2C RI.");
        appLayerTestSuiteDescriptionTextArea.setToolTipText("The description provided by the Test Suite");
        appLayerTestSuiteDescriptionTextArea.setWrapStyleWord(true);
        appLayerTestSuiteDescriptionTextArea.setMaximumSize(new java.awt.Dimension(164, 58));
        appLayerTestSuiteDescriptionTextArea.setOpaque(false);
        testSuiteDescriptionScrollPane1.setViewportView(appLayerTestSuiteDescriptionTextArea);

        appStandardAuthenticationLabel.setFont(new java.awt.Font("Tahoma", 2, 10)); // NOI18N
        appStandardAuthenticationLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        appStandardAuthenticationLabel.setFocusable(false);
        appStandardAuthenticationLabel.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testSuiteFieldjLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 61, Short.MAX_VALUE)
                    .addComponent(appLayerFieldjLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(appStandardAuthenticationLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(appLayerTextField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(appLayerTestSuiteComboBox, 0, 199, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(testSuiteDescriptionFieldLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(195, 195, 195))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(testSuiteDescriptionScrollPane1)
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(testSuiteDescriptionFieldLabel1)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(appStandardAuthenticationLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(21, 21, 21)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(appLayerTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(appLayerFieldjLabel)))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(testSuiteDescriptionScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(appLayerTestSuiteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(testSuiteFieldjLabel1))))
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout testSuiteSelectionPanelLayout = new javax.swing.GroupLayout(testSuiteSelectionPanel);
        testSuiteSelectionPanel.setLayout(testSuiteSelectionPanelLayout);
        testSuiteSelectionPanelLayout.setHorizontalGroup(
            testSuiteSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testSuiteSelectionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(testSuiteSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 604, Short.MAX_VALUE))
                .addContainerGap())
        );
        testSuiteSelectionPanelLayout.setVerticalGroup(
            testSuiteSelectionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testSuiteSelectionPanelLayout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testSuiteSelectionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(testConfigurationFilePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(testConfigurationFilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(testSuiteSelectionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 666, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 521, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
    }// </editor-fold>//GEN-END:initComponents

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
        File theLocation = new File(this.testConfigPath);
        fc.setCurrentDirectory(theLocation);
        int returnVal = fc.showOpenDialog((javax.swing.JFrame) javax.swing.SwingUtilities.getWindowAncestor(this).getOwner());
        if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File file = fc.getSelectedFile();
            testConfigPath = file.getAbsolutePath();
            pathSelectedTextField.setText(file.getAbsolutePath());
        }
    }//GEN-LAST:event_browseButtonActionPerformed

    /**
     * Info layer test suite combo box item state changed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void infoLayerTestSuiteComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_infoLayerTestSuiteComboBoxItemStateChanged
      
        // Update the fields associated with newly selected test suite
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            this.infoLayerTextField.setText(TestSuites.getInstance().getInfoLayerStandard((String) this.infoLayerTestSuiteComboBox.getSelectedItem()));
            this.infoLayerTestSuiteDescriptionTextArea.setText(TestSuites.getInstance().getDescription((String) this.infoLayerTestSuiteComboBox.getSelectedItem()));
            setInfoAuthenticationLabel();
        }

    }//GEN-LAST:event_infoLayerTestSuiteComboBoxItemStateChanged

    /**
     * Path selected text field action performed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void pathSelectedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pathSelectedTextFieldActionPerformed
        checkPathUpdate();
    }//GEN-LAST:event_pathSelectedTextFieldActionPerformed

    /**
     * Path selected text field focus lost.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void pathSelectedTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_pathSelectedTextFieldFocusLost
        checkPathUpdate();
    }//GEN-LAST:event_pathSelectedTextFieldFocusLost

    /**
     * App layer test suite combo box item state changed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param evt the evt
     */
    private void appLayerTestSuiteComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_appLayerTestSuiteComboBoxItemStateChanged
        if (evt.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            this.appLayerTextField.setText(TestSuites.getInstance().getAppLayerStandard((String) this.appLayerTestSuiteComboBox.getSelectedItem()));
            this.appLayerTestSuiteDescriptionTextArea.setText(TestSuites.getInstance().getDescription((String) this.appLayerTestSuiteComboBox.getSelectedItem()));
            setAppAuthenticationLabel();
        }
        
    }//GEN-LAST:event_appLayerTestSuiteComboBoxItemStateChanged

    /**
     * Check path update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void checkPathUpdate() {
        File dir = new File(this.pathSelectedTextField.getText());

        boolean isDir = dir.isDirectory();
        if (isDir) {
            this.testConfigPath = this.pathSelectedTextField.getText();
        } else {
            //Pop up the message dialog.
            String message = "Invalid file path entered. \n"
                    + "\nPlease try again.";

            javax.swing.JOptionPane.showMessageDialog(null, //no owner frame
                    message, //text to display
                    "Invalid Path", //title
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            this.pathSelectedTextField.setText(this.testConfigPath);
        }

    }

    /**
     * ProgressListener listens to "progress" property
     * changes in the SwingWorkers that search and load
     * images.
     *
     * @see FileCreateProgressEvent
     */
    class FileCreateProgressListener implements PropertyChangeListener {
        // prevent creation without providing a progress bar

        /**
         * Instantiates a new file create progress listener.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        private FileCreateProgressListener() {
        }

        /* (non-Javadoc)
         * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
         */
        public void propertyChange(PropertyChangeEvent evt) {
            String strPropertyName = evt.getPropertyName();
            if ("progress".equals(strPropertyName)) {
            } else if ("state".equals(strPropertyName)) {
                if ((SwingWorker.StateValue) evt.getNewValue() == SwingWorker.StateValue.STARTED) {
                } else if ((SwingWorker.StateValue) evt.getNewValue() == SwingWorker.StateValue.DONE) {
                    try {
                        setVisible(false);
                    } catch (Exception e) {
                        //Pop up the message dialog.
                        String message = "Error Encountered Creating the Config File.";

                        javax.swing.JOptionPane.showMessageDialog(null, //no owner frame
                                message, //text to display
                                "File Create Error", //title
                                javax.swing.JOptionPane.ERROR_MESSAGE);

                    }

                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel appLayerFieldjLabel;
    private javax.swing.JComboBox appLayerTestSuiteComboBox;
    private javax.swing.JTextArea appLayerTestSuiteDescriptionTextArea;
    private javax.swing.JTextField appLayerTextField;
    private javax.swing.JLabel appStandardAuthenticationLabel;
    private javax.swing.JButton browseButton;
    private javax.swing.JLabel infoLayerStandardFieldLabel;
    private javax.swing.JComboBox infoLayerTestSuiteComboBox;
    private javax.swing.JTextArea infoLayerTestSuiteDescriptionTextArea;
    private javax.swing.JTextField infoLayerTextField;
    private javax.swing.JLabel infoStandardAuthenticationLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel pathFieldLabel;
    private javax.swing.JTextField pathSelectedTextField;
    private javax.swing.JLabel testConfigDescriptionFieldjLabel;
    private javax.swing.JTextArea testConfigDescriptionTextArea;
    private javax.swing.JLabel testConfigFieldLabel;
    private javax.swing.JScrollPane testConfigTextScrollPane;
    private javax.swing.JPanel testConfigurationFilePanel;
    private javax.swing.JTextField testConfigurationTextField;
    private javax.swing.JLabel testSuiteDescriptionFieldLabel;
    private javax.swing.JLabel testSuiteDescriptionFieldLabel1;
    private javax.swing.JScrollPane testSuiteDescriptionScrollPane;
    private javax.swing.JScrollPane testSuiteDescriptionScrollPane1;
    private javax.swing.JLabel testSuiteFieldjLabel;
    private javax.swing.JLabel testSuiteFieldjLabel1;
    private javax.swing.JPanel testSuiteSelectionPanel;
    // End of variables declaration//GEN-END:variables
}
