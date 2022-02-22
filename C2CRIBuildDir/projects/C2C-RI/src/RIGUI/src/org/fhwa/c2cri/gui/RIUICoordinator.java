/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.awt.event.ActionEvent;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardDialog;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPanel;
import org.fhwa.c2cri.gui.wizard.C2CRWizardFactory;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class RIUICoordinator is the primary User Interface class for the RI. It
 * handles events that occur on the main menu and toolbar. It delegates control
 * to other coordinators depending on the state of the UI and user actions.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class RIUICoordinator implements java.awt.event.ActionListener, TestDefinitionListener, TestExecutionListener, ReportsListener {

    /**
     * The initial default state of the RI UI Coordinator.
     */
    protected static String RI_INIT_STATE_NAME = "Initial";

    /**
     * The state of the RI UI Coordinator while defining a test configuration.
     */
    protected static String RI_DEFINE_STATE_NAME = "Defining";

    /**
     * The state of the RI UI Coordinator while executing a test.
     */
    protected static String RI_EXECUTE_STATE_NAME = "Executing";

    /**
     * The state of the RI UI Coordinator while creating reports.
     */
    protected static String RI_REPORT_STATE_NAME = "Reporting";

    /**
     * The state of the RI UI Coordinator while working with system options.
     */
    protected static String RI_OPTIONS_STATE_NAME = "Options";

    /**
     * The state of the RI UI Coordinator while working with system maintenance.
     */
    protected static String RI_MAINTENANCE_STATE_NAME = "Maintenance";

    /**
     * The main ui.
     */
    private C2CMainUI mainUI;

    /**
     * The core panel.
     */
    private static ConfigurationBasePanel corePanel;

    /**
     * The definition ui coordinator.
     */
    private TestDefinitionCoordinator definitionUICoordinator;

    /**
     * The ri ui state.
     */
    private String riUIState;

    /**
     * The execution ui coordinator.
     */
    private TestExecutionCoordinator executionUICoordinator;

    /**
     * The execution panel.
     */
    private static TestExecutionPanel executionPanel;

    /**
     * The reports ui coordinator.
     */
    private ReportsCoordinator reportsUICoordinator;

    /**
     * The reports panel.
     */
    private static TestReportsPanel reportsPanel;
    /**
     * The help ui.
     */
    private HelpUI helpUI;

    /**
     * The c2cri main panel cl.
     */
    private static CardLayout c2criMainPanelCL;

    /**
     * The options ui.
     */
    private OptionsUI optionsUI;

    /**
     * The maintenance ui.
     */
    private MaintenanceUI maintenanceUI;

    /**
     * The Wizard UI
     */
    private static C2CRIWizardPanel wizardPanel;
    
    /**
     * Flag indicating whether wizard mode is enabled.
     */
    private boolean wizardMode = true;

    /**
     * Constructor for the RIUI Coordinator.
     *
     * @param parentFrame - reference to the C2CMainUI
     */
    public RIUICoordinator(C2CMainUI parentFrame) {
        this.mainUI = parentFrame;
        c2criMainPanelCL = (CardLayout) mainUI.c2criMainPanel.getLayout();
        riUIState = RI_INIT_STATE_NAME;

        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();

        c.anchor = java.awt.GridBagConstraints.NORTHWEST;
        corePanel = new ConfigurationBasePanel();
        mainUI.c2criMainPanel.add(corePanel, "COREPANEL");
        corePanel.setVisible(false);

        definitionUICoordinator = new TestDefinitionCoordinator(mainUI, this, corePanel);

        executionPanel = new TestExecutionPanel();
        mainUI.c2criMainPanel.add(executionPanel, "EXECUTIONPANEL");
        executionPanel.setVisible(false);
        executionUICoordinator = new TestExecutionCoordinator(mainUI, this, executionPanel);

        reportsPanel = new TestReportsPanel();
        mainUI.c2criMainPanel.add(reportsPanel, "REPORTSPANEL");
        reportsPanel.setVisible(false);
        reportsUICoordinator = new ReportsCoordinator(mainUI, this, reportsPanel);

        wizardPanel = new C2CRIWizardPanel();
        String wizardMode = RIParameters.getInstance().getParameterValue(RIParameters.RI_USER_PARAMETER_GROUP, RIParameters.RI_WIZARDMODE_PARAMETER, "true");
        if (Boolean.parseBoolean(wizardMode)){
            mainUI.c2criMainPanel.add(wizardPanel, "WIZARDPANEL");
            mainUI.editorScrollPane.getViewport().setVisible(false);
            c2criMainPanelCL.show(mainUI.c2criMainPanel, "WIZARDPANEL");
            wizardPanel.setPanelVisible(true);
            wizardPanel.setVisible(true);        
            wizardPanel.validate();
            this.wizardMode = true;
        } else {
            this.wizardMode = false;
        }

        mainUI.newMenuItem.addActionListener(this);
        mainUI.openMenuItem.addActionListener(this);
        mainUI.executeMenuItem.addActionListener(this);
        mainUI.reportsMenuItem.addActionListener(this);
        mainUI.optionsMenuItem.addActionListener(this);
        mainUI.maintenanceMenuItem.addActionListener(this);
        mainUI.aboutMenuItem.addActionListener(this);
        mainUI.openExistingToolbarButton.addActionListener(this);
        mainUI.createNewConfigToolbarButton.addActionListener(this);
        mainUI.executeTestToolbarButton.addActionListener(this);
        mainUI.riOptionsToolbarButton.addActionListener(this);
        mainUI.reportsToolbarButton.addActionListener(this);
        mainUI.helpAboutToolbarButton.addActionListener(this);
        wizardPanel.getWizardCreateTestButton().addActionListener(this);
        wizardPanel.getWizardExecuteTestButton().addActionListener(this);
        wizardPanel.getWizardCreateReportButton().addActionListener(this);
        wizardPanel.getjDisableCheckBox().addActionListener(this);

        /**
         * try {
         * javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
         * } catch (Exception e) { System.out.println("Error setting native LAF:
         * " + e); }
         */
        mainUI.c2criMainPanel.setVisible(true);
        System.out.println("RIUICoordinator-  The current look and feel is " + javax.swing.UIManager.getLookAndFeel());

//        KeyboardFocusManager focusManager = 
//                KeyboardFocusManager.getCurrentKeyboardFocusManager();
//        focusManager.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
//          public void propertyChange(java.beans.PropertyChangeEvent e) {
//              String prop = e.getPropertyName();
//              if ("focusOwner".equals(prop)){
//                  java.awt.Component comp = (java.awt.Component)e.getNewValue();
//                  if (comp != null){
//                  String name = comp.getAccessibleContext().getAccessibleName();
//                  System.out.println("FocusChangeListener:: Component "+name + " has focus.");
//                  } else {
//                      System.out.println("FocusChangeListener:: No component appears to have focus.");       
//                  }
//              } else {
//                  System.out.println("FocusChangeListener:: Property "+prop + " has changed.");                  
//              }
//          }
//        }
//        );
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if ((source == mainUI.newMenuItem) || (source == mainUI.createNewConfigToolbarButton)) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                // Show the panel if initialization is successful
                if (definitionUICoordinator.initializeUI(true)) {
                    mainUI.editorScrollPane.setVisible(false);
                    corePanel.setVisible(true);
                    c2criMainPanelCL.show(mainUI.c2criMainPanel, "COREPANEL");
                    riUIState = RI_DEFINE_STATE_NAME;

                    mainUI.fileMenu.setEnabled(true);
                    mainUI.newMenuItem.setEnabled(false);
                    mainUI.openMenuItem.setEnabled(false);
                    mainUI.executeMenuItem.setEnabled(false);
                    mainUI.reportsMenuItem.setEnabled(false);
                    mainUI.openExistingToolbarButton.setEnabled(false);
                    mainUI.createNewConfigToolbarButton.setEnabled(false);
                    mainUI.riOptionsToolbarButton.setEnabled(false);
                    mainUI.executeTestToolbarButton.setEnabled(false);
                    mainUI.reportsToolbarButton.setEnabled(false);
                    mainUI.helpAboutToolbarButton.setEnabled(false);
                    mainUI.toolsMenu.setEnabled(false);
                    mainUI.helpMenu.setEnabled(false);
                    if (wizardMode)wizardPanel.setPanelVisible(false);

                }
            }

        } else if ((source == mainUI.openMenuItem) || (source == mainUI.openExistingToolbarButton)) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                // Show the panel if initialization is successful
                if (definitionUICoordinator.initializeUI(false)) {
                    mainUI.editorScrollPane.setVisible(false);
                    corePanel.setVisible(true);
                    c2criMainPanelCL.show(mainUI.c2criMainPanel, "COREPANEL");
                    riUIState = RI_DEFINE_STATE_NAME;

                    mainUI.fileMenu.setEnabled(true);
                    mainUI.newMenuItem.setEnabled(false);
                    mainUI.openMenuItem.setEnabled(false);
                    mainUI.executeMenuItem.setEnabled(false);
                    mainUI.reportsMenuItem.setEnabled(false);
                    mainUI.openExistingToolbarButton.setEnabled(false);
                    mainUI.createNewConfigToolbarButton.setEnabled(false);
                    mainUI.riOptionsToolbarButton.setEnabled(false);
                    mainUI.executeTestToolbarButton.setEnabled(false);
                    mainUI.reportsToolbarButton.setEnabled(false);
                    mainUI.helpAboutToolbarButton.setEnabled(false);
                    mainUI.toolsMenu.setEnabled(false);
                    mainUI.helpMenu.setEnabled(false);
                    if (wizardMode)wizardPanel.setPanelVisible(false);

                }
            }

        } else if ((source == mainUI.executeMenuItem) || (source == mainUI.executeTestToolbarButton) || (source == wizardPanel.getWizardExecuteTestButton())) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                if (executionUICoordinator.initializeUI()) {
                    mainUI.editorScrollPane.setVisible(false);
                    corePanel.setVisible(true);
                    //             executionPanel.setVisible(true);
                    c2criMainPanelCL.show(mainUI.c2criMainPanel, "EXECUTIONPANEL");
                    mainUI.fileMenu.setEnabled(true);
                    mainUI.newMenuItem.setEnabled(false);
                    mainUI.openMenuItem.setEnabled(false);
                    mainUI.executeMenuItem.setEnabled(false);
                    mainUI.reportsMenuItem.setEnabled(false);
                    mainUI.openExistingToolbarButton.setEnabled(false);
                    mainUI.createNewConfigToolbarButton.setEnabled(false);
                    mainUI.riOptionsToolbarButton.setEnabled(false);
                    mainUI.executeTestToolbarButton.setEnabled(false);
                    mainUI.reportsToolbarButton.setEnabled(false);
                    mainUI.helpAboutToolbarButton.setEnabled(false);
                    mainUI.toolsMenu.setEnabled(false);
                    mainUI.helpMenu.setEnabled(false);
                    if (wizardMode)wizardPanel.setPanelVisible(false);

                    riUIState = RI_EXECUTE_STATE_NAME;
                }
            }

        } else if ((source == mainUI.reportsMenuItem) || (source == mainUI.reportsToolbarButton) || (source == wizardPanel.getWizardCreateReportButton())) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                if (reportsUICoordinator.initializeUI()) {
                    mainUI.editorScrollPane.setVisible(false);
                    corePanel.setVisible(true);
                    //                reportsPanel.setVisible(true);
                    if (wizardMode)wizardPanel.setPanelVisible(false);
                    c2criMainPanelCL.show(mainUI.c2criMainPanel, "REPORTSPANEL");
                    riUIState = RI_REPORT_STATE_NAME;
                    mainUI.fileMenu.setEnabled(true);
                    mainUI.newMenuItem.setEnabled(false);
                    mainUI.openMenuItem.setEnabled(false);
                    mainUI.executeMenuItem.setEnabled(false);
                    mainUI.reportsMenuItem.setEnabled(false);
                    mainUI.openExistingToolbarButton.setEnabled(false);
                    mainUI.createNewConfigToolbarButton.setEnabled(false);
                    mainUI.riOptionsToolbarButton.setEnabled(false);
                    mainUI.executeTestToolbarButton.setEnabled(false);
                    mainUI.reportsToolbarButton.setEnabled(false);
                    mainUI.helpAboutToolbarButton.setEnabled(false);
                    mainUI.toolsMenu.setEnabled(false);
                    mainUI.helpMenu.setEnabled(false);
                    reportsPanel.transferFocus();

                }
            }

        } else if ((source == mainUI.optionsMenuItem) || (source == mainUI.riOptionsToolbarButton)) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                org.fhwa.c2cri.utilities.RIParameters.getInstance().configure();
                org.fhwa.c2cri.gui.propertyeditor.PropEditor1 pE = new org.fhwa.c2cri.gui.propertyeditor.PropEditor1();
                pE.openFile();
//                 org.fhwa.c2cri.gui.propertyeditor.PropertyEditorDialog ed = pE.getDialog();
// 		 ed.setVisible(true);

                optionsUI = new OptionsUI(mainUI, true);
                optionsUI.showDialog();
                optionsUI.dispose();

                /**
                 * Removed to make options selection available through a dialog.
                 * if (optionsUICoordinator.initializeUI()){
                 * mainUI.editorScrollPane.setVisible(false);
                 * optionsPanel.setVisible(true);
                 * c2criMainPanelCL.show(mainUI.c2criMainPanel, "OPTIONSPANEL");
                 * riUIState = RI_OPTIONS_STATE_NAME; }
                 */
            }

        } else if (source == mainUI.maintenanceMenuItem) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                maintenanceUI = new MaintenanceUI(mainUI, true);
                maintenanceUI.showDialog();
                maintenanceUI.dispose();

                /**
                 * if (maintenanceUICoordinator.initializeUI()){
                 * mainUI.editorScrollPane.setVisible(false);
                 * maintenancePanel.setVisible(true);
                 * c2criMainPanelCL.show(mainUI.c2criMainPanel,
                 * "MAINTENANCEPANEL"); riUIState = RI_MAINTENANCE_STATE_NAME; }
                 */
            }

        } else if ((source == mainUI.aboutMenuItem) || (source == mainUI.helpAboutToolbarButton)) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                helpUI = new HelpUI(mainUI, true);
                helpUI.riVersionLabel.setText(RIParameters.RI_VERSION_NUMBER);
                helpUI.showDialog();
                helpUI.dispose();

            }
        } else if ((source == wizardPanel.getWizardCreateTestButton())) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                C2CRIWizardDialog test = new C2CRIWizardDialog((javax.swing.JFrame)this.mainUI, true, new C2CRWizardFactory(C2CRWizardFactory.FactoryType.TEST_CONFIGURATION));
                test.setSize(new Dimension(950, 700));
                test.setModalityType(Dialog.ModalityType.APPLICATION_MODAL); // prevent user from doing something else
                test.setLocationRelativeTo(null);
                test.setVisible(true);
                test.dispose();
            }
        } else if ((source == wizardPanel.getjDisableCheckBox())) {
            if (riUIState.equals(RI_INIT_STATE_NAME)) {
                String wizardFlag = "";
                if (wizardPanel.getjDisableCheckBox().isSelected()){
                    wizardFlag = "false";
                    Object[] options = {"Now",
                        "Restart"};
                    int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                        "Would you like to disable the Wizard Panel now or when the C2C RI Application restarts?",
                        "Wizard Mode Warning",
                        javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                    if (n == 0) {
                        mainUI.c2criMainPanel.remove(wizardPanel);
                        mainUI.editorScrollPane.getViewport().setVisible(true);
                        wizardPanel.setPanelVisible(false);
                        wizardPanel.setVisible(false);                    
                    }                    
     
                    
                } else wizardFlag = "true";
                
                org.fhwa.c2cri.utilities.RIParameters.getInstance().configure();
                RIParameters.getInstance().setParameterValue(RIParameters.RI_USER_PARAMETER_GROUP, RIParameters.RI_WIZARDMODE_PARAMETER, wizardFlag);
                RIParameters.getInstance().saveFile();
                }
        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * Method called to indicate that the user has completed test definition.
     * Return the state to initialization.
     */
    public void testDefinitionCompleted() {
        corePanel.setVisible(false);
        mainUI.fileMenu.setEnabled(true);
        mainUI.newMenuItem.setEnabled(true);
        mainUI.openMenuItem.setEnabled(true);
        mainUI.executeMenuItem.setEnabled(true);
        mainUI.reportsMenuItem.setEnabled(true);
        mainUI.openExistingToolbarButton.setEnabled(true);
        mainUI.createNewConfigToolbarButton.setEnabled(true);
        mainUI.riOptionsToolbarButton.setEnabled(true);
        mainUI.executeTestToolbarButton.setEnabled(true);
        mainUI.reportsToolbarButton.setEnabled(true);
        mainUI.helpAboutToolbarButton.setEnabled(true);
        mainUI.exitAppToolbarButton.setEnabled(true);
        mainUI.toolsMenu.setEnabled(true);
        mainUI.helpMenu.setEnabled(true);
        riUIState = RI_INIT_STATE_NAME;
        if (wizardMode)wizardPanel.setPanelVisible(true);
    }

    /**
     * Method called to indicate that the user has completed test execution.
     * Return the state to initialization.
     */
    public void testExecutionCompleted() {
        mainUI.fileMenu.setEnabled(true);
        mainUI.newMenuItem.setEnabled(true);
        mainUI.openMenuItem.setEnabled(true);
        mainUI.executeMenuItem.setEnabled(true);
        mainUI.reportsMenuItem.setEnabled(true);
        mainUI.openExistingToolbarButton.setEnabled(true);
        mainUI.createNewConfigToolbarButton.setEnabled(true);
        mainUI.riOptionsToolbarButton.setEnabled(true);
        mainUI.executeTestToolbarButton.setEnabled(true);
        mainUI.reportsToolbarButton.setEnabled(true);
        mainUI.helpAboutToolbarButton.setEnabled(true);
        mainUI.exitAppToolbarButton.setEnabled(true);
        mainUI.toolsMenu.setEnabled(true);
        mainUI.helpMenu.setEnabled(true);
        mainUI.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        riUIState = RI_INIT_STATE_NAME;
        executionPanel.setVisible(false);
        if (wizardMode)wizardPanel.setPanelVisible(true);
    }

    /**
     * Method called to indicate that the user has running a test. Disable the
     * main UI menu items and disable the defaultCloseOperation for the main
     * window. *
     */
    public void testExecutionStarted() {
        mainUI.fileMenu.setEnabled(false);
        mainUI.toolsMenu.setEnabled(false);
        mainUI.helpMenu.setEnabled(false);
        mainUI.openExistingToolbarButton.setEnabled(false);
        mainUI.createNewConfigToolbarButton.setEnabled(false);
        mainUI.riOptionsToolbarButton.setEnabled(false);
        mainUI.reportsToolbarButton.setEnabled(false);
        mainUI.executeTestToolbarButton.setEnabled(false);
        mainUI.helpAboutToolbarButton.setEnabled(false);
        mainUI.exitAppToolbarButton.setEnabled(false);

        mainUI.setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Method called to indicate that the user has completed report creation.
     * Return the state to initialization. *
     */
    public void reportsCompleted() {
        riUIState = RI_INIT_STATE_NAME;
        mainUI.fileMenu.setEnabled(true);
        mainUI.newMenuItem.setEnabled(true);
        mainUI.openMenuItem.setEnabled(true);
        mainUI.executeMenuItem.setEnabled(true);
        mainUI.reportsMenuItem.setEnabled(true);
        mainUI.openExistingToolbarButton.setEnabled(true);
        mainUI.createNewConfigToolbarButton.setEnabled(true);
        mainUI.riOptionsToolbarButton.setEnabled(true);
        mainUI.executeTestToolbarButton.setEnabled(true);
        mainUI.reportsToolbarButton.setEnabled(true);
        mainUI.helpAboutToolbarButton.setEnabled(true);
        mainUI.exitAppToolbarButton.setEnabled(true);
        mainUI.toolsMenu.setEnabled(true);
        mainUI.helpMenu.setEnabled(true);
        if (wizardMode)wizardPanel.setPanelVisible(true);
        reportsPanel.setVisible(false);
    }
    /**
     * Method called to indicate that the user has completed working with system
     * maintenance. Return the state to initialization.
     *
     */
    /**
     * public void maintenanceCompleted(){ riUIState = RI_INIT_STATE_NAME;
     * maintenancePanel.setVisible(false);
     * mainUI.editorScrollPane.setVisible(true); }
     */
}
