/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import org.fhwa.c2cri.gui.components.TestCasesTableButtonRenderer;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.InputVerifier;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultTreeModel;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.gui.components.TestCaseEditJButton;
import org.fhwa.c2cri.gui.components.TestCaseViewJButton;
import org.fhwa.c2cri.gui.components.TestCasesTableButtonEditor;
import org.fhwa.c2cri.testmodel.DefaultLayerParameters;
import org.fhwa.c2cri.testmodel.NRTM;
import org.fhwa.c2cri.testmodel.Need;
import org.fhwa.c2cri.testmodel.ProjectRequirementsInterface;
import org.fhwa.c2cri.testmodel.Requirement;
import org.fhwa.c2cri.testmodel.TestSuites;
import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.testmodel.UserNeedsInterface;
import org.fhwa.c2cri.utilities.Checksum;
import org.fhwa.c2cri.utilities.FilenameValidator;
import org.fhwa.c2cri.utilities.Parameter;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestDefinitionCoordinator.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestDefinitionCoordinator implements java.awt.event.ActionListener {

    /**
     * The ri init state name.
     */
    protected static String RI_INIT_STATE_NAME = "Initial";

    /**
     * The ri define state name.
     */
    protected static String RI_DEFINE_STATE_NAME = "Running";

    /**
     * The ri execute state name.
     */
    protected static String RI_EXECUTE_STATE_NAME = "Paused";

    /**
     * The ri report state name.
     */
    protected static String RI_REPORT_STATE_NAME = "Resuming";

    /**
     * The ri options state name.
     */
    protected static String RI_OPTIONS_STATE_NAME = "Terminating";

    /**
     * The configuration file extension.
     */
    protected static String CONFIGURATION_FILE_EXTENSION = ".ricfg";

    /**
     * The main ui.
     */
    private C2CMainUI mainUI;

    /**
     * The parent object.
     */
    private TestDefinitionListener parentObject;

    /**
     * The core panel.
     */
    private static ConfigurationBasePanel corePanel;

    /**
     * The test definition ui state.
     */
    private String testDefinitionUIState;

    /**
     * The test config.
     */
    private TestConfiguration testConfig;

    /**
     * The config file open.
     */
    private boolean configFileOpen = false;
//    private ObjectOutputStream output;
//    private ObjectInputStream input;
    /**
     * The sorter.
     */
    private TableRowSorter<NeedsTableModel> sorter;

    /**
     * The sorter2.
     */
    private TableRowSorter<RequirementsTableModel> sorter2;

    /**
     * The sorter3.
     */
    private TableRowSorter<OtherRequirementsTableModel> sorter3;

    /**
     * The standard nrtm.
     */
    private NRTM standardNRTM;

    /**
     * The standard app nrtm.
     */
    private NRTM standardAppNRTM;

    /**
     * The validation errors found.
     */
    private boolean validationErrorsFound = false;

    /**
     * Instantiates a new test definition coordinator.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param parentFrame the parent frame
     * @param parentObject the parent object
     * @param definitionPanel the definition panel
     */
    @SuppressWarnings("static-access")
    public TestDefinitionCoordinator(C2CMainUI parentFrame, TestDefinitionListener parentObject, ConfigurationBasePanel definitionPanel) {
        this.mainUI = parentFrame;
        this.parentObject = parentObject;
        this.testDefinitionUIState = RI_INIT_STATE_NAME;
        TestDefinitionCoordinator.corePanel = definitionPanel;

        corePanel.cancelButton.addActionListener(this);
        corePanel.okButton.addActionListener(this);
        corePanel.saveButton.addActionListener(this);
        corePanel.saveAsButton.addActionListener(this);
        corePanel.validateConfigurationButton.addActionListener(this);
        corePanel.infoPanel.needsClearOptionsButton.addActionListener(this);
        corePanel.infoPanel.requirementsClearAllButton.addActionListener(this);
        corePanel.appPanel.needsClearjButton.addActionListener(this);
        corePanel.appPanel.requirementsClearButton.addActionListener(this);
        corePanel.emulationParametersPanel.UpdateButton.addActionListener(this);
        corePanel.emulationParametersPanel.ViewButton.addActionListener(this);
    }

    /**
     * Initialize ui.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param createNewConfig the create new config
     * @return true, if successful
     */
    public boolean initializeUI(boolean createNewConfig) {
        boolean results = false;

//        executionPanel = new TestExecutionPanel();
//        mainUI.c2criMainPanel.add(executionPanel);
        if (createNewConfig) {
            TestConfigCreateUI createConfigUI = new TestConfigCreateUI(mainUI, true);
            if (createConfigUI.showDialog()) {
                //TO DO  -- Create a Test Configuration object using the information provided in the Dialog
                testConfig = createConfigUI.getTestConfiguration();
                corePanel.configPanel.configNameTextField.setText(createConfigUI.getTestConfigPath() + "\\" + createConfigUI.getTestConfigName() + CONFIGURATION_FILE_EXTENSION);
                initConfigPanel();
                initSUTPanel();
                initParametersPanel(corePanel.infoPanel.standardNeedsTable,
                        corePanel.infoPanel.standardRequirementsTable,
                        corePanel.infoPanel.parametersTable,
                        testConfig.getInfoLayerParams());
                initParametersPanel(corePanel.appPanel.standardNeedsTable,
                        corePanel.appPanel.standardRequirementsTable,
                        corePanel.appPanel.parametersTable,
                        testConfig.getAppLayerParams());
//                initInfoLayerParametersPanel();
//                initAppLayerParametersPanel();
                initInfoLayerTestCasesPanel();
                initAppLayerTestCasesPanel();
                initEmulationParametersPanel(testConfig.getEmulationParameters());
                Checksum cs = new Checksum();
                try {
                    corePanel.configPanel.checksumTextArea.setText(cs.getChecksum(corePanel.configPanel.configNameTextField.getText()));
                } catch (Exception ex) {
                    corePanel.configPanel.checksumTextArea.setText("Error - " + ex.getMessage());
                }
                this.mainUI.setTitle("C2C RI" + "  Configuration File: " + createConfigUI.getTestConfigName());
                results = true;

            }
            //Added to clear up the screen refresh issues that occured after creating and saving
            // a new test configuration.
            createConfigUI.dispose();
            createConfigUI = null;

        } else {
            javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
            String default_Path = RIParameters.getInstance().getParameterValue(Parameter.config_file_path);
            if (default_Path.equals("")) {
                default_Path = Parameter.default_config_file_path;
            }
            File current_dir = new File(default_Path);
            System.out.println(" Path = " + default_Path + " is valid? " + current_dir.exists());
            fc.setCurrentDirectory(current_dir);
            FileFilter ricfgFilter = new FileFilter() {

                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().endsWith(CONFIGURATION_FILE_EXTENSION);
                }

                public String getDescription() {
                    return "Filter for RI Config Files";
                }
            };
            fc.setFileFilter(ricfgFilter);

            boolean normalExit = false;
            int returnVal = 0;
            while (!normalExit) {

                returnVal = fc.showOpenDialog(mainUI);

                if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    if (file == null || file.getName().equals("")) {
                        javax.swing.JOptionPane.showMessageDialog(corePanel, "Invalid File Name", "File Name Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                    } else {
                        try {

                            ObjectInputStream input = new ObjectInputStream(new FileInputStream(fc.getSelectedFile()));
                            try {
                                testConfig = null;
                                testConfig = (TestConfiguration) input.readObject();
                                testConfig.print();
                                input.close();
                                input = null;
                                configFileOpen = false;
                                corePanel.configPanel.configNameTextField.setText(file.getPath());
                                initConfigPanel();
                                initSUTPanel();
                                initParametersPanel(corePanel.infoPanel.standardNeedsTable,
                                        corePanel.infoPanel.standardRequirementsTable,
                                        corePanel.infoPanel.parametersTable,
                                        testConfig.getInfoLayerParams());
                                initParametersPanel(corePanel.appPanel.standardNeedsTable,
                                        corePanel.appPanel.standardRequirementsTable,
                                        corePanel.appPanel.parametersTable,
                                        testConfig.getAppLayerParams());
//                                initInfoLayerParametersPanel();
//                                initAppLayerParametersPanel();
                                initInfoLayerTestCasesPanel();
                                initAppLayerTestCasesPanel();
                                initEmulationParametersPanel(testConfig.getEmulationParameters());
                                Checksum cs = new Checksum();
                                try {
                                    corePanel.configPanel.checksumTextArea.setText(cs.getChecksum(fc.getSelectedFile().getAbsolutePath()));
                                } catch (Exception ex) {
                                    corePanel.configPanel.checksumTextArea.setText("Error - " + ex.getMessage());
                                }

                                this.mainUI.setTitle("C2C RI" + "  Configuration File: " + file.getPath());
                                results = true;
                                normalExit = true;

                            } catch (Exception e1) {
                                javax.swing.JOptionPane.showMessageDialog(corePanel, "Error Reading Config File", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                                e1.printStackTrace();
                            }

                        } catch (Exception e) {
                            javax.swing.JOptionPane.showMessageDialog(corePanel, "Error Opening File:  " + fc.getSelectedFile().getName() + "\n" + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                            e.printStackTrace();
                        }

                    }

                } else if (returnVal == javax.swing.JFileChooser.CANCEL_OPTION) {
                    normalExit = true;
                }

            }

        }

        if (results) {
            KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            focusManager.addPropertyChangeListener(
                    new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    String properties = e.getPropertyName();
                    if (("focusOwner".equals(properties)) && (e.getNewValue() != null)) {
                        try{
                        JComponent component = (JComponent) e.getNewValue();
                        if (corePanel.configPanel.isAncestorOf(component)
                                || corePanel.appPanel.isAncestorOf(component)
                                || corePanel.appTestCasePanel.isAncestorOf(component)
                                || corePanel.infoPanel.isAncestorOf(component)
                                || corePanel.infoTestCasePanel.isAncestorOf(component)
                                || corePanel.emulationParametersPanel.isAncestorOf(component)
                                || corePanel.sutPanel.isAncestorOf(component)) {

                            if (component instanceof JTextField) {
                                JTextField textField = (JTextField) component;
                                if (!textField.isEditable()) {
                                    textField.selectAll();
                                }
                            } else if (component instanceof JTextArea) {
                                JTextArea textArea = (JTextArea) component;
                                if (!textArea.isEditable()) {
                                    textArea.selectAll();
                                }
                            } else if (component instanceof JTable) {
                                JTable tableArea = (JTable) component;
                                if (tableArea.getRowCount() > 0) {
                                    if (tableArea.getSelectedRowCount()==0){
                                        tableArea.setRowSelectionInterval(0, 0);                                        
                                    }
                                }
                            }
                        }
                        } catch (Exception ex){
                           // This is likely a JFrame instead of Component and we don't really care 
                        }
                    }
                }

            });
            
        }
        // Show the panel if initialization is successful
        return results;
    }

    /**
     * Sets the Test Config Parameters on the Config Panel.
     */
    private void initConfigPanel() {
        corePanel.configPanel.configDescriptionTextArea.setText(testConfig.getTestDescription());
        corePanel.configPanel.infoLayerTestSuiteTextField.setText(testConfig.getSelectedInfoLayerTestSuite());
        corePanel.configPanel.appLayerTestSuiteTextField.setText(testConfig.getSelectedAppLayerTestSuite());
        corePanel.configPanel.appLayerTextField.setText(TestSuites.getInstance().getAppLayerStandard(testConfig.getSelectedAppLayerTestSuite()));
        corePanel.configPanel.infoLayerTextField.setText(TestSuites.getInstance().getInfoLayerStandard(testConfig.getSelectedInfoLayerTestSuite()));
        corePanel.configPanel.infoLayerTestSuiteDescriptionTextArea.setText(TestSuites.getInstance().getDescription(testConfig.getSelectedInfoLayerTestSuite()));
        corePanel.configPanel.infoLayerTestSuiteDescriptionTextArea.moveCaretPosition(0);
        corePanel.configPanel.appLayerTestSuiteDescriptionTextArea.setText(TestSuites.getInstance().getDescription(testConfig.getSelectedAppLayerTestSuite()));
        corePanel.configPanel.appLayerTestSuiteDescriptionTextArea.moveCaretPosition(0);
        corePanel.configPanel.checksumTextArea.setFocusable(true);
        corePanel.configPanel.checksumTextArea.setEditable(false);
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Object contrastResult = toolkit.getDesktopProperty( "win.highContrast.on" );        
        Boolean highContrast = contrastResult == null?false:(Boolean)toolkit.getDesktopProperty( "win.highContrast.on" );
        if (TestSuites.getInstance().isPredefined(testConfig.getSelectedInfoLayerTestSuite())) {
            if (highContrast){
                corePanel.configPanel.infoLayerAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                corePanel.configPanel.infoLayerAuthenticationLabel.setForeground(Color.black);                
            }
            corePanel.configPanel.infoLayerAuthenticationLabel.setText("Predefined Test Suite");
        } else {
            if (highContrast){
                corePanel.configPanel.infoLayerAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                corePanel.configPanel.infoLayerAuthenticationLabel.setForeground(Color.red);                
            }
            corePanel.configPanel.infoLayerAuthenticationLabel.setText("User Defined Test Suite");
        }

        if (TestSuites.getInstance().isPredefined(testConfig.getSelectedAppLayerTestSuite())) {
            if (highContrast){
                corePanel.configPanel.appLayerAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                corePanel.configPanel.appLayerAuthenticationLabel.setForeground(Color.black);                
            }
            corePanel.configPanel.appLayerAuthenticationLabel.setText("Predefined Test Suite");
        } else {
            if (highContrast){
                corePanel.configPanel.appLayerAuthenticationLabel.setForeground(SystemColor.textText);
            } else {
                corePanel.configPanel.appLayerAuthenticationLabel.setForeground(Color.red);                
            
            }
            corePanel.configPanel.appLayerAuthenticationLabel.setText("User Defined Test Suite");
        }

        corePanel.configPanel.ownerModeRadioButton.setSelected(testConfig.getTestMode().isOwnerCenterOperation());
        corePanel.configPanel.externalModeRadioButton.setSelected(testConfig.getTestMode().isExternalCenterOperation());

        corePanel.configurationTree.setFocusable(false);
        ConfigurationTreeNode rootNode = new ConfigurationTreeNode("Configuration", true);
        DefaultTreeModel configurationTm = new DefaultTreeModel(rootNode);
        try {
            String configPanel = "Configuration Panel";
            String sutPanel = "SUT Panel";
            String informationLayerPanel = "Information Layer Panel";
            String applicationLayerPanel = "Application Layer Panel";
            String informationLayerTestCasesPanel = "Information Layer Test Cases";
            String applicationLayerTestCasesPanel = "Application Layer Test Cases";
            String emulationParametersPanel = "Entity Emulation Parameters";

            ConfigurationTreeNode cfTreeNode1 = new ConfigurationTreeNode(configPanel, false);
            ConfigurationTreeNode cfTreeNode2 = new ConfigurationTreeNode(sutPanel, false);
            ConfigurationTreeNode cfTreeNode3 = new ConfigurationTreeNode(informationLayerPanel, false);
            ConfigurationTreeNode cfTreeNode4 = new ConfigurationTreeNode(applicationLayerPanel, false);
            ConfigurationTreeNode cfTreeNode5 = new ConfigurationTreeNode(informationLayerTestCasesPanel, false);
            ConfigurationTreeNode cfTreeNode6 = new ConfigurationTreeNode(applicationLayerTestCasesPanel, false);
            ConfigurationTreeNode cfTreeNode7 = new ConfigurationTreeNode(emulationParametersPanel, false);
            rootNode.add(cfTreeNode1);
            rootNode.add(cfTreeNode2);
            rootNode.add(cfTreeNode3);
            rootNode.add(cfTreeNode4);
            rootNode.add(cfTreeNode5);
            rootNode.add(cfTreeNode6);
            rootNode.add(cfTreeNode7);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        corePanel.configurationTree.setModel(configurationTm);
        corePanel.configurationTree.addTreeSelectionListener(corePanel);
       
    }

    /**
     * Inits the parameters panel.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param standardNeedsTable the standard needs table
     * @param standardRequirementsTable the standard requirements table
     * @param parametersTable the parameters table
     * @param layerParams the layer params
     */
    private void initParametersPanel(final JTable standardNeedsTable, final JTable standardRequirementsTable, final JTable parametersTable, final DefaultLayerParameters layerParams) {
        System.out.println(" Loading the Parameter Panel  ....... ");
        //Ensure the tables do not currently have sorters activated
        standardRequirementsTable.setRowSorter(null);
        standardNeedsTable.setRowSorter(null);
        parametersTable.setRowSorter(null);

        NeedsTableModel needsTableModel = new NeedsTableModel(layerParams.getNrtm());
        RequirementsTableModel requirementsTableModel = new RequirementsTableModel(layerParams.getNrtm());
        OtherRequirementsTableModel otherRequirementsTableModel = new OtherRequirementsTableModel(layerParams.getNrtm());

        standardNeedsTable.setModel(needsTableModel);
        standardRequirementsTable.setModel(requirementsTableModel);
        parametersTable.setModel(otherRequirementsTableModel);

        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.
        standardNeedsTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        standardNeedsTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        standardNeedsTable.setFocusCycleRoot(false);
        
        standardRequirementsTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        standardRequirementsTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        standardRequirementsTable.setFocusCycleRoot(false);

        parametersTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        parametersTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        parametersTable.setFocusCycleRoot(false);
        
        needsTableModel.fireTableStructureChanged();
        requirementsTableModel.fireTableStructureChanged();
        otherRequirementsTableModel.fireTableStructureChanged();

        // For Debugging Only -- Safe to remove
        if (standardNeedsTable.getRowCount() != needsTableModel.getRowCount()) {
            System.out.println("NEEDS TABLE and MODEL Rowcount Mismatch");
        }
        ;
        // For Debugging Only -- Safe to remove

        needsTableModel.fireTableDataChanged();
        requirementsTableModel.fireTableDataChanged();
        otherRequirementsTableModel.fireTableDataChanged();

        sorter = new TableRowSorter<NeedsTableModel>(needsTableModel);
        standardNeedsTable.setRowSorter(sorter);
        standardNeedsTable.getColumn(UserNeedsInterface.flagValue_Header).setCellRenderer(new SelectionFlagRenderer(UserNeedsInterface.type_Header));

        SelectionFlagListener unselectedNeedListener = new SelectionFlagListener() {

            @Override
            public void flagValueSetUpdate(int tableRow) {
                standardRequirementsTable.setEnabled(true);
                parametersTable.setEnabled(true);
            }

            @Override
            public void flagValueClearedUpdate(int tableModelRow) {

                String deselectedNeed = layerParams.getNrtm().getUserNeeds().needs.get(tableModelRow).getTitle();

                List<String> selectedRequirementsList = new ArrayList<String>();
                // Gather a list of optional selected requirements associated with this need
                for (Requirement thisProjectRequirement : layerParams.getNrtm().getUserNeeds().getNeed(deselectedNeed).getProjectRequirements().requirements) {
                    if ((thisProjectRequirement.getFlagValue()) && (!thisProjectRequirement.getType().equals("M") && !thisProjectRequirement.getType().equals("Mandatory"))) {
//                        selectedRequirementsList.add(thisProjectRequirement.getTitle());
                        thisProjectRequirement.setFlagValue(false);
                        ((RequirementsTableModel) standardRequirementsTable.getModel()).fireTableDataChanged();
                    }

                }
                /**
                 * // Gather a list of the currently selected needs List<String>
                 * selectedNeedsList = new ArrayList<String>(); for (Need
                 * theNeed : layerParams.getNrtm().getUserNeeds().needs) { // We
                 * don't want to recapture the need we're working with if
                 * (!theNeed.getTitle().equals(deselectedNeed)) { if
                 * (theNeed.getFlagValue()) {
                 * selectedNeedsList.add(theNeed.getTitle()); } } } //Process
                 * each requirement in the selected Requirement list. See if the
                 * requirement is part of a // different selected need. If not,
                 * then it's ok to clear the requirement. for (String
                 * theRequirement : selectedRequirementsList) { boolean
                 * okToClearRequirement = true; for (String theNeed :
                 * selectedNeedsList) { for (Requirement checkRequirement :
                 * layerParams.getNrtm().getUserNeeds().getNeed(theNeed).getProjectRequirements().requirements)
                 * { if (theRequirement.equals(checkRequirement.getTitle())) {
                 * okToClearRequirement = false; } } } if (okToClearRequirement)
                 * { try { for (String theNeed : selectedNeedsList) {
                 * ((Requirement)
                 * layerParams.getNrtm().getUserNeeds().getNeed(theNeed).getProjectRequirements().requirementsMap.get(theRequirement)).setFlagValue(false);
                 * ((RequirementsTableModel)
                 * standardRequirementsTable.getModel()).fireTableDataChanged();
                 *
                 * }
                 * } catch (Exception ex) { ex.printStackTrace(); } } }
                 */

                int currentRow = standardRequirementsTable.getSelectionModel().getLeadSelectionIndex();
                standardRequirementsTable.getSelectionModel().removeSelectionInterval(currentRow, currentRow);
                standardRequirementsTable.setEnabled(false);
                parametersTable.setEnabled(false);
            }
        };
        SelectionFlagEditor infoLayerFlagEditor = new SelectionFlagEditor(UserNeedsInterface.type_Header);
        infoLayerFlagEditor.registerSelectionFlagListener(unselectedNeedListener);
        standardNeedsTable.getColumn(UserNeedsInterface.flagValue_Header).setCellEditor(infoLayerFlagEditor);

        //When selection changes, provide user with row numbers for
        //both view and model.
        standardNeedsTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                int viewRow = standardNeedsTable.getSelectedRow();
                if (viewRow < 0) {
                    //Selection got filtered away.
                } else {
//                            Boolean needSelected = layerParams.getNrtm().getUserNeeds().needs.get(viewRow).getFlagValue();
                    Boolean needSelected = (Boolean) standardNeedsTable.getValueAt(standardNeedsTable.getSelectedRow(), standardNeedsTable.getColumn(UserNeedsInterface.flagValue_Header).getModelIndex());
                    if (needSelected) {
                        standardRequirementsTable.setEnabled(true);
                    } else {
                        standardRequirementsTable.setEnabled(false);
                    }
                }
            }
        });

        //When selection changes, provide user with row numbers for
        //both view and model.
        standardRequirementsTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                int viewRow = standardRequirementsTable.getSelectedRow();

                if (parametersTable.isEditing()) {
                    parametersTable.getCellEditor().cancelCellEditing();
                }

                if (viewRow < 0) {
                    //Selection got filtered away.
                } else {
                    Boolean requirementSelected = (Boolean) standardRequirementsTable.getValueAt(standardRequirementsTable.getSelectedRow(), standardRequirementsTable.getColumn(ProjectRequirementsInterface.flagValue_Header).getModelIndex());
                    if (requirementSelected) {
                        parametersTable.setEnabled(true);
                    } else {
                        parametersTable.setEnabled(false);
                    }
                }
            }
        });

        standardNeedsTable.getSelectionModel().addListSelectionListener(requirementsTableModel);
        standardNeedsTable.getSelectionModel().addListSelectionListener(otherRequirementsTableModel);
        requirementsTableModel.setNeedListSelectionTable(standardNeedsTable);
        otherRequirementsTableModel.setNeedListSelectionTable(standardNeedsTable);
        otherRequirementsTableModel.setRequirementListSelectionTable(standardRequirementsTable);
        standardRequirementsTable.getSelectionModel().addListSelectionListener(otherRequirementsTableModel);

        sorter2 = new TableRowSorter<RequirementsTableModel>(requirementsTableModel);
        SelectionFlagListener projectRequirementSelectedListener = new SelectionFlagListener() {

            @Override
            public void flagValueSetUpdate(int tableRow) {
                parametersTable.setEnabled(true);
            }

            @Override
            public void flagValueClearedUpdate(int tableModelRow) {
                if (parametersTable.isEditing()) {
                    parametersTable.getCellEditor().cancelCellEditing();
                }

                parametersTable.setEnabled(false);
            }
        };

        SelectionFlagEditor projectRequirementsFlagEditor = new SelectionFlagEditor(ProjectRequirementsInterface.type_Header);
        projectRequirementsFlagEditor.registerSelectionFlagListener(projectRequirementSelectedListener);
        standardRequirementsTable.getColumn(ProjectRequirementsInterface.flagValue_Header).setCellRenderer(new SelectionFlagRenderer(ProjectRequirementsInterface.type_Header));
        standardRequirementsTable.getColumn(ProjectRequirementsInterface.flagValue_Header).setCellEditor(projectRequirementsFlagEditor);

        TableColumnModel needModel = standardNeedsTable.getColumnModel();
        TextAreaRenderer textAreaRendererNeeds = new TextAreaRenderer();
        needModel.getColumn(RequirementsTableModel.Text_Col).setCellRenderer(textAreaRendererNeeds);

        TableColumnModel cmodel = standardRequirementsTable.getColumnModel();
        TextAreaRenderer textAreaRenderer = new TextAreaRenderer();
        cmodel.getColumn(RequirementsTableModel.Text_Col).setCellRenderer(textAreaRenderer);

        TableColumnModel parameterModel = parametersTable.getColumnModel();
        TextAreaRenderer textAreaParameters = new TextAreaRenderer();
        parameterModel.getColumn(OtherRequirementsTableModel.Text_Col).setCellRenderer(textAreaParameters);

    }

    /**
     * Inits the sut panel.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private void initSUTPanel() {
        corePanel.sutPanel.hostNameTextField.setText(testConfig.getSutParams().getHostName());
        corePanel.sutPanel.ipAddressTextField.setText(testConfig.getSutParams().getIpAddress());
        corePanel.sutPanel.portTextField.setText(testConfig.getSutParams().getIpPort());
        corePanel.sutPanel.passwordTextField.setText(testConfig.getSutParams().getPassword());
        corePanel.sutPanel.userNameTextField.setText(testConfig.getSutParams().getUserName());
        corePanel.sutPanel.webServiceURLTextField.setText(testConfig.getSutParams().getWebServiceURL());
        corePanel.sutPanel.juserNameCheckBox.setSelected(testConfig.getSutParams().isUserNameRequired());
        corePanel.sutPanel.jpasswordCheckBox.setSelected(testConfig.getSutParams().isPasswordRequired());
        if (!testConfig.getSutParams().isUserNameRequired()) {
            corePanel.sutPanel.userNameTextField.setEnabled(false);
        }
        if (!testConfig.getSutParams().isPasswordRequired()) {
            corePanel.sutPanel.passwordTextField.setEnabled(false);
        }
    }

    /**
     * Sets the Test Cases Parameters on the Information Layer Standard
     * Parameters Config Panel.
     */
    private void initInfoLayerTestCasesPanel() {
        System.out.println(" Loading the Info Layer Test Cases Panel  ....... ");
        //Ensure the tables do not currently have sorters activated

        TestCasesTableModel testCasesTableModel = new TestCasesTableModel(testConfig.getInfoLayerParams().getTestCases());

        corePanel.infoTestCasePanel.testCasesTable.setModel(testCasesTableModel);
        
        // Add a renderer to allow the Edit and View buttons to be available in the Test Cases Table.
        TableCellRenderer tableRenderer;
        tableRenderer = corePanel.infoTestCasePanel.testCasesTable.getDefaultRenderer(TestCaseEditJButton.class);
        corePanel.infoTestCasePanel.testCasesTable.setDefaultRenderer(TestCaseEditJButton.class, new TestCasesTableButtonRenderer(tableRenderer));
        corePanel.infoTestCasePanel.testCasesTable.setDefaultEditor(TestCaseEditJButton.class, new TestCasesTableButtonEditor(new JCheckBox()));
        corePanel.infoTestCasePanel.testCasesTable.setDefaultRenderer(TestCaseViewJButton.class, new TestCasesTableButtonRenderer(tableRenderer));
        corePanel.infoTestCasePanel.testCasesTable.setDefaultEditor(TestCaseViewJButton.class, new TestCasesTableButtonEditor(new JCheckBox()));
        // Allow the created tables to be edited using the keyboard only.
        corePanel.infoTestCasePanel.testCasesTable.setSurrendersFocusOnKeystroke(true);

        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.
        corePanel.infoTestCasePanel.testCasesTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        corePanel.infoTestCasePanel.testCasesTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        corePanel.infoTestCasePanel.testCasesTable.setFocusCycleRoot(false);

        testCasesTableModel.fireTableStructureChanged();
    }

    /**
     * Sets the Test Cases Parameters on the Application Layer Standard
     * Parameters Config Panel.
     */
    private void initAppLayerTestCasesPanel() {
        System.out.println(" Loading the Info Layer Test Cases Panel  ....... ");
        //Ensure the tables do not currently have sorters activated

        TestCasesTableModel testCasesTableModel = new TestCasesTableModel(testConfig.getAppLayerParams().getTestCases());

        corePanel.appTestCasePanel.testCasesTable.setModel(testCasesTableModel);
        // Add a renderer to allow the Edit and View buttons to be available in the Test Cases Table.
        TableCellRenderer tableRenderer;
        tableRenderer = corePanel.appTestCasePanel.testCasesTable.getDefaultRenderer(TestCaseEditJButton.class);
        corePanel.appTestCasePanel.testCasesTable.setDefaultRenderer(TestCaseEditJButton.class, new TestCasesTableButtonRenderer(tableRenderer));
        corePanel.appTestCasePanel.testCasesTable.setDefaultEditor(TestCaseEditJButton.class, new TestCasesTableButtonEditor(new JCheckBox()));
        corePanel.appTestCasePanel.testCasesTable.setDefaultRenderer(TestCaseViewJButton.class, new TestCasesTableButtonRenderer(tableRenderer));
        corePanel.appTestCasePanel.testCasesTable.setDefaultEditor(TestCaseViewJButton.class, new TestCasesTableButtonEditor(new JCheckBox()));

        // Allow the created tables to be edited using the keyboard only.
        corePanel.appTestCasePanel.testCasesTable.setSurrendersFocusOnKeystroke(true);

        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.
        corePanel.appTestCasePanel.testCasesTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        corePanel.appTestCasePanel.testCasesTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        corePanel.appTestCasePanel.testCasesTable.setFocusCycleRoot(false);
        
        
        testCasesTableModel.fireTableStructureChanged();
    }

    /**
     * Sets the Emulation Parameters on the Emulation Parameters Config Panel.
     */
    private void initEmulationParametersPanel(RIEmulationParameters emulationParameters) {
        System.out.println(" Loading the Emulation Parameters Panel  ....... ");
        EmulationParametersTableModel emulationParametersTableModel = new EmulationParametersTableModel(emulationParameters);
        corePanel.emulationParametersPanel.emulationParametersTable.setModel(emulationParametersTableModel);
        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.
        corePanel.emulationParametersPanel.emulationParametersTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        corePanel.emulationParametersPanel.emulationParametersTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        corePanel.emulationParametersPanel.emulationParametersTable.setFocusCycleRoot(false);

        corePanel.emulationParametersPanel.CommandQueueLengthTextField.setText(Integer.toString(emulationParameters.getCommandQueueLength()));
//        corePanel.emulationParametersPanel.emulationParametersTable.removeColumn(corePanel.emulationParametersPanel.emulationParametersTable.getColumn(EmulationParametersTableModel.Data));

        /** The addDocumentListener is used to determine what happens when the CommandQueueLengthTextField is updated on the EntityEmulationParametersPanel.*/
        corePanel.emulationParametersPanel.CommandQueueLengthTextField.setInputVerifier( new InputVerifier(){
            public boolean verify(JComponent input) {
                JTextField text = (JTextField) input;
                String value = text.getText().trim();
                try {
                    Integer newValue = Integer.parseInt(value);
//            int lastGood = testConfig.getEmulationParameters().getCommandQueueLength();
                    testConfig.getEmulationParameters().setCommandQueueLength(newValue);
                    if (testConfig.getEmulationParameters().getCommandQueueLength() != newValue) {
                        javax.swing.JOptionPane.showMessageDialog(corePanel,
                                "The command queue length entered is invalid and has been set to the nearest valid value.",
                                "Command Queue Length Validation",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        text.setText(String.valueOf(testConfig.getEmulationParameters().getCommandQueueLength()));
                    }

                } catch (NumberFormatException e) {
                    text.setText(String.valueOf(testConfig.getEmulationParameters().getCommandQueueLength()));
                    // assumed it should return false
                    return false;
                }
                return true;
            }

        });


        emulationParametersTableModel.fireTableStructureChanged();

    }

    /**
     * Stores the Test Config Parameters from the Config and SUT Panels.
     */
    private void saveConfigurationData() {
        saveConfigPanelData();
        saveSUTPanelData();

    }

    /**
     * Stores the Test Config Parameters from the Config Panel.
     */
    private void saveConfigPanelData() {

        // Note the Test Suite information is already stored in the Test Configuration as part of it's initialization
        testConfig.setTestDescription(corePanel.configPanel.configDescriptionTextArea.getText());
        org.fhwa.c2cri.testmodel.TestMode tempMode = new org.fhwa.c2cri.testmodel.TestMode();
        tempMode.setExternalCenterOperation(corePanel.configPanel.externalModeRadioButton.isSelected());
        tempMode.setOwnerCenterOperation(corePanel.configPanel.ownerModeRadioButton.isSelected());
        testConfig.setTestMode(tempMode);
        org.fhwa.c2cri.testmodel.TestOptions tempOptions = new org.fhwa.c2cri.testmodel.TestOptions();
//        tempOptions.setNegativeTesting(corePanel.configPanel.negativeTestingCheckBox.isSelected());
//        tempOptions.setRangeTesting(corePanel.configPanel.rangeTestingCheckBox.isSelected());
//        tempOptions.setOtherTestingOptions(corePanel.configPanel.otherCheckBox.isSelected());
//        testConfig.setTestingOptions(tempOptions);
    }

    /**
     * Stores the Test Config Parameters from the SUT Panel.
     */
    private void saveSUTPanelData() {
        org.fhwa.c2cri.testmodel.SUT tempSUT = new org.fhwa.c2cri.testmodel.SUT();
        tempSUT.setHostName(corePanel.sutPanel.hostNameTextField.getText());
        tempSUT.setIpAddress(corePanel.sutPanel.ipAddressTextField.getText());
        tempSUT.setIpPort(corePanel.sutPanel.portTextField.getText());
        tempSUT.setWebServiceURL(corePanel.sutPanel.webServiceURLTextField.getText());
        tempSUT.setUserName(corePanel.sutPanel.userNameTextField.getText());
        tempSUT.setPassword(corePanel.sutPanel.passwordTextField.getText());
        tempSUT.setUserNameRequired(corePanel.sutPanel.juserNameCheckBox.isSelected());
        tempSUT.setPasswordRequired(corePanel.sutPanel.jpasswordCheckBox.isSelected());
        testConfig.setSutParams(tempSUT);
    }

    /**
     * Tests all areas of Test Configuration user input.
     *
     * @return true, if is valid test config
     */
    private boolean isValidTestConfig() {
        validationErrorsFound = false;
        return isValidConfigInput()
                & isValidSUTInput()
                & isValidAppLayerParamsInput()
                & isValidInfoLayerParamsInput();
    }

    /**
     * Tests user input on the Configuration Panel.
     *
     * @return true, if is valid config input
     */
    private boolean isValidConfigInput() {
        if (corePanel.configPanel.externalModeRadioButton.isSelected() || corePanel.configPanel.ownerModeRadioButton.isSelected()) {
            return true;
        } else {
            javax.swing.JOptionPane.showMessageDialog(corePanel, "Config Tab -- At least one of Owner Center or External Center Mode must be selected", "Input Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Tests user input on the SUT Panel.
     *
     * @return true, if is valid sut input
     */
    private boolean isValidSUTInput() {
        return SwingValidator.isPresent(corePanel.sutPanel.hostNameTextField, "SUT Tab -- Host Name")
                && SwingValidator.isPresent(corePanel.sutPanel.ipAddressTextField, "SUT Tab -- IP Address")
                && SwingValidator.isPresent(corePanel.sutPanel.portTextField, "SUT Tab -- Port")
                && SwingValidator.isPresent(corePanel.sutPanel.webServiceURLTextField, "SUT Tab -- Web Service URL")
                //                && SwingValidator.isPresent(corePanel.sutPanel.userNameTextField, "SUT Tab -- User Name")
                //                && SwingValidator.isPresent(corePanel.sutPanel.passwordTextField, "SUT Tab -- Password")
                && SwingValidator.isHostName(corePanel.sutPanel.hostNameTextField, "SUT Tab -- Host Name")
                && SwingValidator.isIPAddress(corePanel.sutPanel.ipAddressTextField, "SUT Tab -- IP Address")
                && SwingValidator.isPort(corePanel.sutPanel.portTextField, "SUT Tab -- Port")
                && SwingValidator.isURL(corePanel.sutPanel.webServiceURLTextField, "SUT Tab -- Web Service URL")
                && ((corePanel.sutPanel.juserNameCheckBox.isSelected() && SwingValidator.isPresent(corePanel.sutPanel.userNameTextField, "SUT Tab -- User Name")) || !corePanel.sutPanel.juserNameCheckBox.isSelected());
        // No Need to check the password field as it can be anything.
        //&& SwingValidator.isAlphanumeric(corePanel.sutPanel.passwordTextField, "SUT Tab -- Password");
    }

    /**
     * Tests user input on the appLayerParams Panel.
     *
     * @return true, if is valid app layer params input
     */
    private boolean isValidAppLayerParamsInput() {
        ArrayList<String> errorLog = testConfig.getAppLayerParams().verifyLayerParameters("Application Layer");
        if (errorLog.size() > 0) {
            ErrorLogDialog errorDialog = new ErrorLogDialog(null, true);
            errorDialog.initialize(errorLog);
            errorDialog.setTitle("Application Layer ErrorLog");
            validationErrorsFound = true;
            return errorDialog.showDialog();  // True means Continue, False means Return
        }
        return true;
    }

    /**
     * Tests user input on the infoLayerParams Panel.
     *
     * @return true, if is valid info layer params input
     */
    private boolean isValidInfoLayerParamsInput() {
        ArrayList<String> errorLog = testConfig.getInfoLayerParams().verifyLayerParameters("Information Layer");
        if (errorLog.size() > 0) {
            ErrorLogDialog errorDialog = new ErrorLogDialog(null, true);
            errorDialog.initialize(errorLog);
            errorDialog.setTitle("Information Layer ErrorLog");
            validationErrorsFound = true;
            return errorDialog.showDialog();  // True means Continue, False means Return
        }
        return true;
    }

    /**
     * Handles actions from the TestExecutionPanel.
     *
     * @param e the action event received
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == corePanel.cancelButton) {
            //TO DO define code to take place on Cancel
            //Custom button text
            Object[] options = {"Yes",
                "No"};
            int n = javax.swing.JOptionPane.showOptionDialog(corePanel,
                    "Are you sure you want to cancel editing the configuration file: " + corePanel.configPanel.configNameTextField.getText(),
                    "File Cancel Warning",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[1]);

            if (n == 0) {

                this.mainUI.setTitle("C2C RI");
                corePanel.setVisible(false);
                parentObject.testDefinitionCompleted();
                KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                focusManager.addPropertyChangeListener(null);
                
            }

            //          mainUI.remove(executionPanel);
            //          executionPanel = null;
        } else if (source == corePanel.okButton) {

//            if (isValidConfigInput() && isValidSUTInput()) {
            if (isValidTestConfig()) {
                // Per customer request, the RI will allow a configuration File to be saveable
                // if the errors are within the Information Layer and Application Layer NRTM selections.
                // The user is notified about these errors.  If the user clicks "Continue" the isValidTestConfig()
                // method will actually return true - meaning it's ok to save the configuration.  The validationErrorsFound flag will
                // indicate that there were actually some information or application layer errors found.
                if (!validationErrorsFound) {                
                    testConfig.setValidConfiguration(true);
                } else {
                    testConfig.setValidConfiguration(false);                    
                }

                saveConfigurationData();

                String fileName = corePanel.configPanel.configNameTextField.getText();

                //Custom button text
                Object[] options = {"Yes",
                    "No"};
                int n = javax.swing.JOptionPane.showOptionDialog(corePanel,
                        "Are you sure you want to overwite the file: " + fileName,
                        "File Overwrite Warning",
                        javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                if (n == 0) {
                    try {
                        File file = new File(fileName);
                        file.delete();
                        String userName = RIParameters.getInstance().getParameterValue(RIParameters.RI_USER_PARAMETER_GROUP, RIParameters.RI_USER_PARAMETER, RIParameters.DEFAULT_RI_USER_PARAMETER_VALUE);
                        if (userName.isEmpty()) {
                            testConfig.setConfigurationAuthor(System.getProperty("user.name"));
                        } else {
                            testConfig.setConfigurationAuthor(userName + ":" + System.getProperty("user.name"));
                        }

                        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName));
                        output.writeObject(testConfig);
                        output.flush();
                        output.close();
                        output = null;
                        configFileOpen = false;
                        testConfig.print();
                        this.mainUI.setTitle("C2C RI");
                        corePanel.setVisible(false);
                        parentObject.testDefinitionCompleted();

                        System.out.println("File " + fileName + " was Succesfully Saved ");
                KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                focusManager.addPropertyChangeListener(null);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(corePanel, "The Configuration File will not be saved due to the reported Errors.", "Configuration Not Saved", javax.swing.JOptionPane.ERROR_MESSAGE);

            }
        } else if (source == corePanel.saveButton) {
            if (isValidTestConfig()) {
                // Per customer request, the RI will allow a configuration File to be saveable,
                // if the errors are within the Information Layer and Application Layer NRTM selections.
                // The user is notified about these errors.  If the user clicks "Continue" the isValidTestConfig()
                // method will actually return true - meaning it's ok to save the configuration.  The validationErrorsFound flag will
                // indicate that there were actually some information or application layer errors found.
                if (!validationErrorsFound) {                
                    testConfig.setValidConfiguration(true);
                } else {
                    testConfig.setValidConfiguration(false);                    
                }
                saveConfigurationData();
                String fileName = corePanel.configPanel.configNameTextField.getText();

                //Custom button text
                Object[] options = {"Yes",
                    "No"};
                int n = javax.swing.JOptionPane.showOptionDialog(corePanel,
                        "Are you sure you want to overwite the file: " + fileName,
                        "File Overwrite Warning",
                        javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                if (n == 0) {
                    try {
                        File file = new File(fileName);
                        file.delete();

                        String userName = RIParameters.getInstance().getParameterValue(RIParameters.RI_USER_PARAMETER_GROUP, RIParameters.RI_USER_PARAMETER, RIParameters.DEFAULT_RI_USER_PARAMETER_VALUE);
                        if (userName.isEmpty()) {
                            testConfig.setConfigurationAuthor(System.getProperty("user.name"));
                        } else {
                            testConfig.setConfigurationAuthor(userName + ":" + System.getProperty("user.name"));
                        }

                        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName));
                        output.writeObject(testConfig);
                        output.flush();
                        output.close();
                        output = null;
                        configFileOpen = true;
                        testConfig.print();
                        System.out.println("File " + fileName + " was Succesfully Saved ");
                        Checksum cs = new Checksum();
                        try {
                            corePanel.configPanel.checksumTextArea.setText(cs.getChecksum(fileName));
                        } catch (Exception ex) {
                            corePanel.configPanel.checksumTextArea.setText("Error - " + ex.getMessage());
                        }

                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }

                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(corePanel, "The Configuration File will not be saved due to the reported Errors.", "Configuration Not Saved", javax.swing.JOptionPane.ERROR_MESSAGE);

            }
        } else if (source == corePanel.saveAsButton) {
//            if (isValidConfigInput() && isValidSUTInput()) {
            if (isValidTestConfig()) {
                // Per customer request, the RI will allow a configuration File to be saveable,
                // if the errors are within the Information Layer and Application Layer NRTM selections.
                // The user is notified about these errors.  If the user clicks "Continue" the isValidTestConfig()
                // method will actually return true - meaning it's ok to save the configuration.  The validationErrorsFound flag will
                // indicate that there were actually some information or application layer errors found.
                if (!validationErrorsFound) {                
                    testConfig.setValidConfiguration(true);
                } else {
                    testConfig.setValidConfiguration(false);                    
                }
                saveConfigurationData();

                javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
                String default_Path = RIParameters.getInstance().getParameterValue(Parameter.config_file_path);
                if (default_Path.equals("")) {
                    default_Path = Parameter.default_config_file_path;
                }
                File current_dir = new File(default_Path);
                System.out.println(" Path = " + default_Path + " is valid? " + current_dir.exists());
                fc.setCurrentDirectory(current_dir);

                fc.setFileFilter(new FileFilter() {

                    /**
                     * Return true for all directories and files ending with
                     * ".ricfg"
                     */
                    @Override
                    public boolean accept(File f) {
                        if (f.isFile() && (f.getName().endsWith(CONFIGURATION_FILE_EXTENSION))) {
                            return true;
                        }
                        if (f.isDirectory()) {
                            return true;
                        }
                        return false;
                    }

                    /**
                     * Return the name of this filter.
                     */
                    @Override
                    public String getDescription() {
                        return "C2C RI Config Files";
                    }
                });

                boolean normalExit = false;
                int returnVal = 0;
                while (!normalExit) {

                    returnVal = fc.showSaveDialog(mainUI);

                    if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        String checkFile;
                        String fullName;
                        if (file.getName().endsWith(CONFIGURATION_FILE_EXTENSION)) {
                            checkFile = file.getName().substring(0, file.getName().lastIndexOf(CONFIGURATION_FILE_EXTENSION));
                            System.out.println("CheckFile = " + checkFile);
                            fullName = file.getAbsolutePath();
                        } else {
                            checkFile = file.getName();
                            fullName = file.getAbsolutePath() + CONFIGURATION_FILE_EXTENSION;
                        }
                        FilenameValidator thisValidator = new FilenameValidator();
                        if (file == null) {
                            javax.swing.JOptionPane.showMessageDialog(corePanel, "Invalid File Name", "No File was Specified", javax.swing.JOptionPane.ERROR_MESSAGE);

                        } else if (!thisValidator.validate(checkFile)) {
                            javax.swing.JOptionPane.showMessageDialog(corePanel, "Invalid File Name\n" + thisValidator.getErrorsEncountered(), "File Name Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        } else {

                            Boolean fileExists = new File(fullName).exists();
                            if (fileExists) {
                                //Custom button text
                                Object[] options = {"Yes",
                                    "No"};
                                int n = javax.swing.JOptionPane.showOptionDialog(corePanel,
                                        "Are you sure you want to overwite the file: " + fullName,
                                        "File Overwrite Warning",
                                        javax.swing.JOptionPane.YES_NO_OPTION,
                                        javax.swing.JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        options,
                                        options[1]);

                                if (n == 0) {

                                    try {
                                        Boolean deleted = new File(fullName).delete();
                                        // Add the config file extension to the file name
                                        String userName = RIParameters.getInstance().getParameterValue(RIParameters.RI_USER_PARAMETER_GROUP, RIParameters.RI_USER_PARAMETER, RIParameters.DEFAULT_RI_USER_PARAMETER_VALUE);
                                        if (userName.isEmpty()) {
                                            testConfig.setConfigurationAuthor(System.getProperty("user.name"));
                                        } else {
                                            testConfig.setConfigurationAuthor(userName + ":" + System.getProperty("user.name"));
                                        }
                                        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fullName));
                                        output.writeObject(testConfig);
                                        output.flush();
                                        output.close();
                                        output = null;
                                        configFileOpen = true;
                                        testConfig.print();
                                        System.out.println("File " + file.getName() + " was Succesfully Saved ");
                                        corePanel.configPanel.configNameTextField.setText(fullName);
                                        normalExit = true;

                                        Checksum cs = new Checksum();
                                        try {
                                            corePanel.configPanel.checksumTextArea.setText(cs.getChecksum(fullName));
                                        } catch (Exception ex) {
                                            corePanel.configPanel.checksumTextArea.setText("Error - " + ex.getMessage());
                                        }

                                        this.mainUI.setTitle("C2C RI" + "  Configuration File: " + fullName);

                                    } catch (Exception e4) {
                                        e4.printStackTrace();
                                    }
                                }

                            } else {
                                try {
                                    // Add the config file extension to the file name
                                    String userName = RIParameters.getInstance().getParameterValue(RIParameters.RI_USER_PARAMETER_GROUP, RIParameters.RI_USER_PARAMETER, RIParameters.DEFAULT_RI_USER_PARAMETER_VALUE);
                                    if (userName.isEmpty()) {
                                        testConfig.setConfigurationAuthor(System.getProperty("user.name"));
                                    } else {
                                        testConfig.setConfigurationAuthor(userName + ":" + System.getProperty("user.name"));
                                    }
                                    ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fullName));
                                    output.writeObject(testConfig);
                                    output.flush();
                                    output.close();
                                    output = null;
                                    configFileOpen = true;
                                    testConfig.print();
                                    System.out.println("File " + file.getName() + " was Succesfully Saved ");
                                    corePanel.configPanel.configNameTextField.setText(fullName);
                                    normalExit = true;

                                    Checksum cs = new Checksum();
                                    try {
                                        corePanel.configPanel.checksumTextArea.setText(cs.getChecksum(fullName));
                                    } catch (Exception ex) {
                                        corePanel.configPanel.checksumTextArea.setText("Error - " + ex.getMessage());
                                    }

                                    this.mainUI.setTitle("C2C RI" + "  Configuration File: " + fullName);

                                } catch (Exception e4) {
                                    e4.printStackTrace();
                                }

                            }

                        }

                    } else {
                        // The user canceled the Save As operation
                        normalExit = true;
                    }

                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(corePanel, "The Configuration File will not be save due to the reported Errors.", "Configuration Not Saved", javax.swing.JOptionPane.ERROR_MESSAGE);

            }
        } else if (source == corePanel.validateConfigurationButton) {
            if (isValidTestConfig()) {
                // Per customer request, the RI will allow a configuration File to be saveable,
                // if the errors are within the Information Layer and Application Layer NRTM selections.
                // The user is notified about these errors.  If the user clicks "Continue" the isValidTestConfig()
                // method will actually return true - meaning it's ok to save the configuration.  The validationErrorsFound flag will
                // indicate that there were actually some information or application layer errors found.
                if (!validationErrorsFound) {
                    javax.swing.JOptionPane.showMessageDialog(corePanel, "The Configuration File is Valid", "Configuration Validation Results", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } else if (source == corePanel.infoPanel.needsClearOptionsButton) {
            for (Need theNeed : testConfig.getInfoLayerParams().getNrtm().getUserNeeds().needs) {
                if ((!theNeed.getType().equals("M")) && (!theNeed.getType().equals("Mandatory"))) {
                    theNeed.setFlagValue(false);
                    ((NeedsTableModel) corePanel.infoPanel.standardNeedsTable.getModel()).fireTableDataChanged();
                    for (Requirement theRequirement : testConfig.getInfoLayerParams().getNrtm().getUserNeeds().getNeed(theNeed.getTitle()).getProjectRequirements().requirements) {
                        if ((!theRequirement.getType().equals("M")) && (!theRequirement.getType().equals("Mandatory"))) {
                            theRequirement.setFlagValue(false);
                            ((RequirementsTableModel) corePanel.infoPanel.standardRequirementsTable.getModel()).fireTableDataChanged();
                        }
                    }

                }
            }
        } else if (source == corePanel.infoPanel.requirementsClearAllButton) {
            if (corePanel.infoPanel.standardNeedsTable.getSelectedRow() > -1) {
                String needID = testConfig.getInfoLayerParams().getNrtm().getUserNeeds().needs.get(corePanel.infoPanel.standardNeedsTable.getSelectedRow()).getTitle();
                for (Requirement theRequirement : testConfig.getInfoLayerParams().getNrtm().getUserNeeds().getNeed(needID).getProjectRequirements().requirements) {
                    if ((!theRequirement.getType().equals("M")) && (!theRequirement.getType().equals("Mandatory"))) {
                        theRequirement.setFlagValue(false);
                    }
                }

            }
            ((RequirementsTableModel) corePanel.infoPanel.standardRequirementsTable.getModel()).fireTableDataChanged();
        } else if (source == corePanel.appPanel.needsClearjButton) {
            for (Need theNeed : testConfig.getAppLayerParams().getNrtm().getUserNeeds().needs) {
                if ((!theNeed.getType().equals("M")) && (!theNeed.getType().equals("Mandatory"))) {
                    theNeed.setFlagValue(false);
                    ((NeedsTableModel) corePanel.appPanel.standardNeedsTable.getModel()).fireTableDataChanged();
                    for (Requirement theRequirement : testConfig.getAppLayerParams().getNrtm().getUserNeeds().getNeed(theNeed.getTitle()).getProjectRequirements().requirements) {
                        if ((!theRequirement.getType().equals("M")) && (!theRequirement.getType().equals("Mandatory"))) {
                            theRequirement.setFlagValue(false);
                            ((RequirementsTableModel) corePanel.appPanel.standardRequirementsTable.getModel()).fireTableDataChanged();
                        }
                    }
                }
            }
        } else if (source == corePanel.appPanel.requirementsClearButton) {
            if (corePanel.appPanel.standardNeedsTable.getSelectedRow() > -1) {
                String needID = testConfig.getAppLayerParams().getNrtm().getUserNeeds().needs.get(corePanel.appPanel.standardNeedsTable.getSelectedRow()).getTitle();
                for (Requirement theRequirement : testConfig.getAppLayerParams().getNrtm().getUserNeeds().getNeed(needID).getProjectRequirements().requirements) {
                    if ((!theRequirement.getType().equals("M")) && (!theRequirement.getType().equals("Mandatory"))) {
                        theRequirement.setFlagValue(false);
                    }
                }
                ((RequirementsTableModel) corePanel.appPanel.standardRequirementsTable.getModel()).fireTableDataChanged();
            } 
        } else if (source == corePanel.emulationParametersPanel.UpdateButton) {
                try {
                    LoadEntityDataForm load = new LoadEntityDataForm(this.mainUI,true);
                    load.setTitle("Load Entity Data");
                    int convertedRow = corePanel.emulationParametersPanel.emulationParametersTable.convertRowIndexToModel(corePanel.emulationParametersPanel.emulationParametersTable.getSelectedRow());
                    load.EntityNameTextField.setText(corePanel.emulationParametersPanel.emulationParametersTable.getModel().getValueAt(convertedRow,EmulationParametersTableModel.Title_Col).toString()); 
                    load.setVisible(true);
//                    load.setDefaultCloseOperation(2);
                    if (load.isDataUpdated()){
                       corePanel.emulationParametersPanel.emulationParametersTable.getModel().setValueAt(load.getDataSource(), convertedRow,EmulationParametersTableModel.Data); 
                       corePanel.emulationParametersPanel.emulationParametersTable.getModel().setValueAt(RIEmulationEntityValueSet.ENTITYDATASTATE.Updated.name(), convertedRow,EmulationParametersTableModel.Source);                        
                       ((EmulationParametersTableModel) corePanel.emulationParametersPanel.emulationParametersTable.getModel()).fireTableDataChanged();
                    }
                    load.dispose();


                } catch (Exception Ex) {
                    Ex.printStackTrace();
                    System.out.println("The error occured clicking the \"Update\" button in the \"Entity Emulation Parameter Selection\" UI and the error is: \n" + Ex.getMessage());
                }
            
        } else if (source == corePanel.emulationParametersPanel.ViewButton) {
                try {
                    EntityDataViewer dataView = new EntityDataViewer();
                    dataView.setTitle("Entity Data Viewer");
                    dataView.setVisible(true);
                    dataView.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

                    int selectedTableRow = corePanel.emulationParametersPanel.emulationParametersTable.getSelectedRow();
                    int modelRow = corePanel.emulationParametersPanel.emulationParametersTable.convertRowIndexToModel(selectedTableRow);
                    byte[] emulationData = (byte[]) corePanel.emulationParametersPanel.emulationParametersTable.getModel().getValueAt(modelRow, EmulationParametersTableModel.Data);
                    dataView.EntityDataViewerTextArea.setText(EmulationDataFileProcessor.getContent(emulationData).toString());

                } catch (Exception Ex) {
                    System.out.println("The error occured clicking the \"View\" button in the \"Entity Emulation Parameter Selection\" UI and the error is: \n" + Ex.getMessage());
                }            

        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
    
    
}
