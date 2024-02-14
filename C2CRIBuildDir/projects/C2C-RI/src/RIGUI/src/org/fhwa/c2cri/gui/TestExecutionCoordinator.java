/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.ToolTipManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.event.TestCaseEvent;
import net.sf.jameleon.event.TestCaseListener;
import net.sf.jameleon.function.FunctionTag;
import net.sf.jameleon.result.TestCaseResult;
import org.fhwa.c2cri.center.CenterMonitor;
import org.fhwa.c2cri.logger.RILogging;
import org.fhwa.c2cri.logger.TestLogList;
import org.fhwa.c2cri.testmodel.RITestEngine;
import org.fhwa.c2cri.testmodel.TestActionListener;
import org.fhwa.c2cri.testmodel.TestCase;
import org.fhwa.c2cri.testmodel.TestCaseResults;
import org.fhwa.c2cri.testmodel.TestCaseResultsListener;
import org.fhwa.c2cri.testmodel.TestCases;
import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.testmodel.TestStatusListener;
import org.fhwa.c2cri.utilities.DateUtils;
import org.fhwa.c2cri.utilities.Parameter;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestExecutionCoordinator.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestExecutionCoordinator implements java.awt.event.ActionListener, javax.swing.event.ChangeListener, TestActionListener, TestStatusListener, TestCaseListener, TreeSelectionListener, TestCaseResultsListener {

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
     * Reference to the C2CMainUI object.
     */
    private C2CMainUI mainUI;
    /**
     * Reference to the parent Object that implements the TestExecutionListener
     * interface.
     */
    private TestExecutionListener parentObject;

    /**
     * Reference to the TestExecutionPanel.
     */
    private static TestExecutionPanel executionPanel;
    /**
     * Not used. To be removed.
     */
    private String testExecutionUIState;

    /**
     * Reference to the RITestEnging Object.
     */
    private RITestEngine testEngine;

    /**
     * The test complete.
     */
    private boolean testComplete;

    /**
     * The tc model.
     */
    private TestCaseResultsTableModel tcModel;

    /**
     * The tc reason model.
     */
    private TestStepResultsTableModel tcReasonModel;

    /**
     * The initial run.
     */
    private boolean initialRun = true;

    /**
     * The test cases.
     */
    protected Map testCases = new HashMap();

    /**
     * The Constant TC_NUM.
     */
    private static final int TC_NUM = 0;

    /**
     * The Constant TEST_CASE.
     */
    private static final int TEST_CASE = 1;

    /**
     * The Constant STATUS.
     */
    private static final int STATUS = 2;

    /**
     * The Constant RUN.
     */
    private static final int RUN = 3;

    /**
     * The Constant FAIL.
     */
    private static final int FAIL = 4;

    /**
     * The Constant EXEC_TIME.
     */
    private static final int EXEC_TIME = 5;

    /**
     * The Constant TIME_STAMP.
     */
    private static final int TIME_STAMP = 0;

    /**
     * The Constant TEST_PROCEDURE.
     */
    private static final int TEST_PROCEDURE = 1;

    /**
     * The Constant STEP_NUMBER.
     */
    private static final int STEP_NUMBER = 2;

    /**
     * The Constant PASS_FAIL.
     */
    private static final int PASS_FAIL = 3;

    /**
     * The results.
     */
    private List results = new ArrayList();

    /**
     * The test case results.
     */
    private TestCaseResults testCaseResults;

    /**
     * The initialization results.
     */
    private boolean initializationResults = false;

    /*
    * The index of the information layer test cases tab.
    */
    private static int INFOLAYERTABINDEX = 0;
    
    /*
    * The index of the application layer test cases tab.
    */
    private static int APPLAYERTABINDEX = 1;

    /*
    * listens for changes in the test case table and updates the visible row during a test.
    */
    private ComponentAdapter testCaseListener; 
    
    /*
    * listens for changes in the test steps table and updates the visible row during a test.
    */
    private ComponentAdapter testStepsListener;
    
    /**
     * Captures the current index of the test being executed.
     */
    private int testIndex;
    
    /**
     * Main constructor method for the the TestExecutionCoordinator Class. This
     * class manages the TestExecutionPanel and the responses to the user's
     * interaction with it. It also updates the panel based on events reported
     * from the RITestEngine object.
     *
     * It initializes the applicable class parameters to the values provided in
     * the parameter list.
     *
     * @param parentFrame - reference to the C2CMainUI frame
     * @param parentObject - reference to the monitor/creating object for this
     * class that implements the TestExecutionListener interface.
     * @param executionPanel - reference to the TestExecutionPanel
     */
    @SuppressWarnings("static-access")
    public TestExecutionCoordinator(C2CMainUI parentFrame, TestExecutionListener parentObject, TestExecutionPanel executionPanel) {
        this.mainUI = parentFrame;
        this.parentObject = parentObject;
        this.testExecutionUIState = RI_INIT_STATE_NAME;
        TestExecutionCoordinator.executionPanel = executionPanel;
    }

    /**
     * The Test case comparator.
     */
    public static Comparator<TestCase> TestCaseComparator = new Comparator<TestCase>() {
        public int compare(TestCase testCase1, TestCase testCase2) {
            String testCaseName1 = testCase1.getName().toUpperCase();
            String testCaseName2 = testCase2.getName().toUpperCase();

            return testCaseName1.compareTo(testCaseName2);
        }
    };

    /**
     * This method first prompts the user to complete the information requested
     * by the TestSelectionUI dialog.
     *
     * Set the testComplete flag to false
     *
     * If the user selects OK, then an instance of the RITestEngine is created
     * using the TestConfiguration and other parameters selected by the user.
     * This TestExecutionCoordinator instance then registers itself as an action
     * and status listener for the RITestEngine. Next it registers this object
     * as a listener for button events on the TestExecutionPanel. Finally, it
     * clears the test status and test action text areas.
     *
     * If the user cancels, then return false indicating that the UI was not
     * initialized.
     *
     * @return - true if the intialization was successful
     */
    public boolean initializeUI() {

        TestSelectionUI testSelection = new TestSelectionUI(this.mainUI, true);
        if (testSelection.showDialog()) {

            final TestExecutionCoordinator thisCoordinator = this;
            final String ts_configurationPath = testSelection.getTestConfigurationPath();
            final String ts_configuration = testSelection.getTestConfiguration();
            final String ts_description = testSelection.getTestDescription();
            final String ts_testName = testSelection.getTestName();
            final boolean ts_emulationEnabled = testSelection.isEmulationEnabled();
            final boolean ts_reinitializeEmulation = testSelection.isEmulationReinitialzeEnabled();
            BasicGUIActionWrapper initTestSelectionAction = new BasicGUIActionWrapper(mainUI, "Initializing the RI Test Engine") {

                ConfigFileTableModel model;

                @Override
                protected Boolean actionMethod() throws Exception {
                    testComplete = false;
                    testIndex = 1;
                    initialRun = true;
                    String appStandard = "";
                    String infoStandard = "";
                    TestConfiguration tempTest;
                    TestCases appTestCases, infoTestCases;
                    List<TestCase> appTestCaseList = new ArrayList<TestCase>();
                    List<TestCase> infoTestCaseList = new ArrayList<TestCase>();
                    String testMode = "EC";
                    executionPanel.jRunningLabel.setVisible(false);
                    executionPanel.runButton.setEnabled(false);
                    executionPanel.resumeButton.setEnabled(false);
                    executionPanel.pauseButton.setEnabled(false);
                    executionPanel.terminateButton.setEnabled(false);                    

                    String fileName = ts_configurationPath + File.separator + ts_configuration;

                    executionPanel.infoStandardTree.removeAll();
                    executionPanel.appStandardTree.removeAll();
                    executionPanel.testCaseTable.removeAll();
                    executionPanel.testResultsTable.removeAll();

                    try {
                        ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName));
                        try {
                            tempTest = (TestConfiguration) input.readObject();
                            tempTest.print();
                            input.close();
                            input = null;
                            if (!tempTest.isValidConfiguration()) {
                                javax.swing.JOptionPane.showMessageDialog(mainUI, "Config File is Invalid.  Can not Execute.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                                return false;
                            }
                            appStandard = tempTest.getSelectedAppLayerTestSuite();
                            infoStandard = tempTest.getSelectedInfoLayerTestSuite();

//                    appTestCases = tempTest.getAppLayerParams().getTestCases();
//                    infoTestCases = tempTest.getInfoLayerParams().getTestCases();
                            if (tempTest.getTestMode().isExternalCenterOperation()) {
                                testMode = "EC";
                            } else {
                                testMode = "OC";
                            }
                            appTestCaseList = tempTest.getAppLayerParams().getApplicableTestCases(testMode);
                            infoTestCaseList = tempTest.getInfoLayerParams().getApplicableTestCases(testMode);

                            String filePath = RIParameters.getInstance().getParameterValue(Parameter.log_file_path);
                            String fullLogFile = filePath + File.separator + ts_testName;
                            System.out.println(" Log File stored in " + fullLogFile);
                            testCaseResults = new TestCaseResults();
                            testEngine = new RITestEngine(tempTest, testCaseResults, fullLogFile, ts_description, fileName, ts_emulationEnabled, ts_reinitializeEmulation);
                        } catch (Exception e1) {
                            javax.swing.JOptionPane.showMessageDialog(mainUI, "Error Initializing Test: \n" + e1.getMessage(), "Initialization Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                            e1.printStackTrace();
                            return false;
                        }

                    } catch (Exception e) {
                        javax.swing.JOptionPane.showMessageDialog(mainUI, "Error Opening File:  " + fileName + "\n" + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                        return false;
                    }

                    testEngine.registerTestStatusListener(thisCoordinator);
                    testEngine.registerTestCaseForwardListener(thisCoordinator);

                    executionPanel.testNameTextField.setText(ts_testName);
                    executionPanel.terminateButton.addActionListener(thisCoordinator);
                    executionPanel.runButton.addActionListener(thisCoordinator);
                    executionPanel.pauseButton.addActionListener(thisCoordinator);
                    executionPanel.resumeButton.addActionListener(thisCoordinator);
                    executionPanel.runButton.setEnabled(false);
                    executionPanel.pauseButton.setEnabled(false);
                    executionPanel.resumeButton.setEnabled(false);
                    executionPanel.terminateButton.setEnabled(true);
                    executionPanel.statusTextTable.setModel(StatusLogCache.getInstance());
                    executionPanel.statusTextTable.setColumnSelectionAllowed(false);
                    executionPanel.statusTextTable.setRowSelectionAllowed(true);
                    executionPanel.statusTextTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    executionPanel.statusTextTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
                        public void valueChanged(ListSelectionEvent e){
                            if (e.getValueIsAdjusting()) return;
                            ActionLogCache.getInstance().refreshTableForSelection();
                        }
                    });
                    
 RowFilter<ActionLogCache,Integer> testIndexFilter = new RowFilter<ActionLogCache,Integer>() {
   public boolean include(Entry<? extends ActionLogCache, ? extends Integer> entry) {
     int selectedRow = executionPanel.statusTextTable.getSelectedRow();
     if (selectedRow <0) return true;
     
     int selectedTestIndex = StatusLogCache.getInstance().getTestIndexAt(executionPanel.statusTextTable.getSelectedRow());
     ActionLogCache testActionModel = entry.getModel();
     int rowTestIndex = testActionModel.getTestIndexAt(entry.getIdentifier());
     
     if (rowTestIndex == selectedTestIndex) {
       // Returning true indicates this row should be shown.
       return true;
     }
     // The action log test index does not match the selected rows test Index.
     return false;
   }
 };                    
                    TableRowSorter<ActionLogCache> sorter = new TableRowSorter<ActionLogCache>(ActionLogCache.getInstance());
                    sorter.setRowFilter(testIndexFilter);
                    executionPanel.actionTextTable.setRowSorter(sorter);                    
                    executionPanel.actionTextTable.setModel(ActionLogCache.getInstance());
                    executionPanel.jRunningLabel.setText("Initializing...");
                    executionPanel.jRunningLabel.setVisible(false);
                    executionPanel.jTestExecutionProgressBar.setVisible(false);

                    tcModel = new TestCaseResultsTableModel(testCaseResults);
                    executionPanel.testCaseTable.setModel(tcModel);

                    tcReasonModel = new TestStepResultsTableModel(testCaseResults);
                    tcReasonModel.setNeedListSelectionTable(executionPanel.testCaseTable);
                    tcReasonModel.setTestDescriptionText(executionPanel.jTestCaseDescription);
                    executionPanel.testResultsTable.setModel(tcReasonModel);

                    executionPanel.testCaseTable.setColumnSelectionAllowed(false);
                    executionPanel.testCaseTable.setRowSelectionAllowed(true);
                    executionPanel.testResultsTable.setColumnSelectionAllowed(false);
                    executionPanel.testResultsTable.setRowSelectionAllowed(true);

                    tcModel.fireTableStructureChanged();
                    tcReasonModel.fireTableStructureChanged();

                    tcModel.fireTableDataChanged();
                    tcReasonModel.fireTableDataChanged();

                    testCaseResults.registerListner(thisCoordinator);
                    executionPanel.testCaseTable.getSelectionModel().addListSelectionListener(tcReasonModel);

                    executionPanel.testCaseTable.getColumn("#").setPreferredWidth(10);
                    executionPanel.testCaseTable.getColumn("TestCaseID").setPreferredWidth(200);
                    executionPanel.testCaseTable.getColumn("Status").setPreferredWidth(60);
                    executionPanel.testCaseTable.getColumn("Execution Time").setPreferredWidth(80);
                    executionPanel.testCaseTable.getColumn("Fail Counts").setPreferredWidth(30);
                    executionPanel.testCaseTable.getColumn("Run Counts").setPreferredWidth(30);
                    executionPanel.testCaseTable.getColumn("Run ID").setPreferredWidth(30);

                    TableColumnModel testCaseModel = executionPanel.testCaseTable.getColumnModel();
                    TextAreaRenderer textAreaRenderer = new TextAreaRenderer();
                    testCaseModel.getColumn(TestCaseResultsTableModel.TestCaseID_Col).setCellRenderer(textAreaRenderer);
                    testCaseModel.getColumn(TestCaseResultsTableModel.TestCaseStatus_Col).setCellRenderer(textAreaRenderer);
                    testCaseModel.getColumn(TestCaseResultsTableModel.TestCaseExecutionTime_Col).setCellRenderer(textAreaRenderer);
                    testCaseModel.getColumn(TestCaseResultsTableModel.Number_Col).setCellRenderer(textAreaRenderer);
                    testCaseModel.getColumn(TestCaseResultsTableModel.TestCaseFailTotal_Col).setCellRenderer(textAreaRenderer);
                    testCaseModel.getColumn(TestCaseResultsTableModel.TestCaseRunTotal_Col).setCellRenderer(textAreaRenderer);
                    testCaseModel.getColumn(TestCaseResultsTableModel.TestCaseRun_Col).setCellRenderer(textAreaRenderer);

                    TableColumnModel testResultsModel = executionPanel.testResultsTable.getColumnModel();
                    testResultsModel.getColumn(TestStepResultsTableModel.Description_Col).setCellRenderer(textAreaRenderer);
                    testResultsModel.getColumn(TestStepResultsTableModel.Result_Col).setCellRenderer(textAreaRenderer);
                    testResultsModel.getColumn(TestStepResultsTableModel.TimeStamp_Col).setCellRenderer(textAreaRenderer);

                    TableColumnModel testLogResultsModel = executionPanel.actionTextTable.getColumnModel();
                    testLogResultsModel.getColumn(ActionLogCache.Description).setCellRenderer(textAreaRenderer);
                    testLogResultsModel.getColumn(ActionLogCache.Results).setCellRenderer(textAreaRenderer);
                    testLogResultsModel.getColumn(ActionLogCache.TimeStamp).setCellRenderer(textAreaRenderer);                    
                    
                    TCTreeNode infoRootNode = new TCTreeNode(infoStandard, true);
                    DefaultTreeModel informationTm = new DefaultTreeModel(infoRootNode);
                    
                    // Add an Entity Emulation Tester Test Case to the list of information layer test cases if we are an
                    // OC and entity emulation is selected.
                    if (tempTest.getTestMode().isOwnerCenterOperation() && ts_emulationEnabled){
                            /** The test case script url. */
                        URL emulationTestCaseScriptURL = TestExecutionCoordinator.class.getResource("/org/fhwa/c2cri/centermodel/EntityEmulationTester.xml");
                        URL emulationTestCaseDataURL = TestExecutionCoordinator.class.getResource("/org/fhwa/c2cri/centermodel/EntityEmulationTester.data");

                        TestCase emulationTesterTestCase = new TestCase("EntityEmulationTester",emulationTestCaseScriptURL.toString(),"This Data Path will be overwritten with a custom path.","This test case is used for Entity Emulation Testing.","SUT/RI;RI","");
                        emulationTesterTestCase.setCustomDataLocation(emulationTestCaseDataURL.toString());
                        System.out.println("Entity Emulation Script Location = "+emulationTesterTestCase.getScriptUrlLocation().toString());
                        TCTreeNode theTreeNode = new TCTreeNode(emulationTesterTestCase, false);
                        infoRootNode.add(theTreeNode);
                    }
//            for (TestCase infoTestCase : infoTestCases.testCases) {
                    java.util.Collections.sort(infoTestCaseList, TestCaseComparator);
                    for (TestCase infoTestCase : infoTestCaseList) {
                        if (infoTestCase.getType().equals(testMode)) {
                            TCTreeNode theTreeNode = new TCTreeNode(infoTestCase, false);

                            infoRootNode.add(theTreeNode);
                        }
                    }
                    executionPanel.infoStandardTree.setModel(informationTm);
                    
                    executionPanel.infoStandardTree.addKeyListener(new KeyListener() {
                        @Override
                        public void keyTyped(KeyEvent e) {
                            System.out.println("User typed Character " + e.getKeyChar());
                            if (e.getKeyChar() == '`') {

                                // Get the list of Test Case Names
                                String testCasesString = JOptionPane.showInputDialog("Enter the list of test cases desired:");
                                                   
                               // Split the string entered by space, \t - TAB, \x0B -vertical tab, \f - form feed, \r Carriage Return
                                ArrayList<String> tcNames = new ArrayList(Arrays.asList(testCasesString.split("[ \t\\x0B\f\r]+")));                                

                                ArrayList<Integer> matchedRows = new ArrayList<Integer>();
                                
                                for (String tcName : tcNames) {
                                    int rowCount = executionPanel.infoStandardTree.getRowCount();
                                    for (int ii = 0; ii < rowCount; ii++) {
                                        Object obj = executionPanel.infoStandardTree.getPathForRow(ii).getLastPathComponent();
                                        if (obj instanceof TCTreeNode) {
                                            TCTreeNode tn = (TCTreeNode) obj;
                                            Object tnobj = tn.getUserObject();
                                            if (tnobj instanceof TestCase) {
                                                TestCase tc = (TestCase) tn.getUserObject();
                                                try {
                                                    if (tc.getName().equalsIgnoreCase(tcName)) {
                                                        matchedRows.add(ii);
                                                        continue;
                                                    }
                                                } catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                                }
                                int[] rowArray = new int[matchedRows.size()];
                                for (int ii = 0; ii < matchedRows.size(); ii++) {
                                    rowArray[ii] = matchedRows.get(ii).intValue();
                                }
                                executionPanel.infoStandardTree.setSelectionRows(rowArray);
                            }
                        }

                        @Override
                        public void keyPressed(KeyEvent e) {
                            //                           throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                        @Override
                        public void keyReleased(KeyEvent e) {
                            //                           throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                        }

                    });
                    
                    
            KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            focusManager.addPropertyChangeListener(
                    new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    String properties = e.getPropertyName();
                    if (("focusOwner".equals(properties)) && (e.getNewValue() != null)) {
                        try{
                        JComponent component = (JComponent) e.getNewValue();
                        if (executionPanel.reportPanel.isAncestorOf(component)
                                || executionPanel.testResultsPanel.isAncestorOf(component)
                                || executionPanel.appStandardTree.isAncestorOf(component)
                                || executionPanel.infoStandardTree.isAncestorOf(component)) {

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
                                    if (tableArea.getSelectedRowCount()==0){
                                        tableArea.setRowSelectionInterval(0, 0);                                        
                                    }
                            } else if (component instanceof JTree){
                                JTree exTree = (JTree) component;
                                if (exTree.getVisibleRowCount() >= 1) {
                                    exTree.setSelectionRow(0);
                                }
                            }
                        }
                        } catch (Exception ex){
                           // This is likely a JFrame instead of Component and we don't really care 
                        }
                    }
                }

            });
 
                    executionPanel.testCaseTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
                    executionPanel.testCaseTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
                    executionPanel.testCaseTable.setFocusCycleRoot(false);

                    executionPanel.testResultsTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
                    executionPanel.testResultsTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
                    executionPanel.testResultsTable.setFocusCycleRoot(false);                 
                    
                    //Enable tool tips.
                    ToolTipManager.sharedInstance().registerComponent(executionPanel.infoStandardTree);
                    executionPanel.infoStandardTree.setCellRenderer(new TestCaseRenderer());

                    TCTreeNode appRootNode = new TCTreeNode(appStandard, true);
                    DefaultTreeModel applicationTm = new DefaultTreeModel(appRootNode);
//            for (TestCase appTestCase : appTestCases.testCases) {
                    java.util.Collections.sort(appTestCaseList, TestCaseComparator);
                    for (TestCase appTestCase : appTestCaseList) {
                        if (appTestCase.getType().equals(testMode)) {
                            TCTreeNode theTreeNode = new TCTreeNode(appTestCase, false);
                            appRootNode.add(theTreeNode);
                        }
                    }
                    executionPanel.appStandardTree.setModel(applicationTm);
                    //Enable tool tips.
                    ToolTipManager.sharedInstance().registerComponent(executionPanel.appStandardTree);

                    executionPanel.appStandardTree.setCellRenderer(new TestCaseRenderer());
                    initializationResults = true;

                    executionPanel.infoStandardTree.addTreeSelectionListener(thisCoordinator);
                    executionPanel.appStandardTree.addTreeSelectionListener(thisCoordinator);

                    executionPanel.jTabbedPaneTestCases.addChangeListener(thisCoordinator);


                    testCaseListener = new ComponentAdapter() {
                        public void componentResized(ComponentEvent e) {
                            executionPanel.testCaseTable.scrollRectToVisible(executionPanel.testCaseTable.getCellRect(executionPanel.testCaseTable.getRowCount() - 1, 0, true));
                        }       
                    };
                    
                    testStepsListener = new ComponentAdapter() {
                        public void componentResized(ComponentEvent e) {
                            executionPanel.testResultsTable.scrollRectToVisible(executionPanel.testResultsTable.getCellRect(executionPanel.testResultsTable.getRowCount() - 1, 0, true));
                        }
                    };
                    executionPanel.repaint();
                    RILogging.addGUIListener(ActionLogCache.getInstance());                    
                    return true;
                }

                @Override
                protected void wrapUp(Boolean result) {
                    if (!result) {
                        javax.swing.JOptionPane.showMessageDialog(null, "An error was encountered trying to complete the Initialize TestSelection UI action.");
                        parentObject.testExecutionCompleted();
                        CenterMonitor.getInstance().terminateTest();
                    }
                }
            };
            initTestSelectionAction.execute();
            initializationResults = true;

        } else {
            initializationResults = false;
        }
        testSelection.dispose();
        return initializationResults;
    }

    /**
     * Gets the selected test cases count.
     *
     * @return the selected test cases count
     */
    private int getSelectedTestCasesCount() {
        int totalCounted = 0;
        int[] infoSelectedRows = executionPanel.infoStandardTree.getSelectionRows();
        int[] appSelectedRows = executionPanel.appStandardTree.getSelectionRows();

        if ((infoSelectedRows != null) && (executionPanel.jTabbedPaneTestCases.getSelectedIndex()==INFOLAYERTABINDEX)) {
            for (int thisRow : infoSelectedRows) {
                // Include all rows that are not the root node
                if (thisRow != 0) {
                    totalCounted = totalCounted + 1;
                }
            }
        }
        if ((appSelectedRows != null) && (executionPanel.jTabbedPaneTestCases.getSelectedIndex()==APPLAYERTABINDEX)) {
            for (int thisRow : appSelectedRows) {
                // Include all Rows that are not the root node
                if (thisRow != 0) {
                    totalCounted = totalCounted + 1;
                }
            }
        }

        return totalCounted;

    }

    /* (non-Javadoc)
     * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
     */
    public void valueChanged(TreeSelectionEvent e) {
        if (getSelectedTestCasesCount() > 0) {
            executionPanel.runButton.setEnabled(true);
            System.out.println("I think there are Tree Nodes Selected.");
        } else {
            executionPanel.runButton.setEnabled(false);
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (getSelectedTestCasesCount() > 0){
            executionPanel.runButton.setEnabled(true);
        } else {
            executionPanel.runButton.setEnabled(false);            
        }
    }
        
    /**
     * Gets the script name.
     *
     * @param tct the tct
     * @return the script name
     */
    private String getScriptName(TestCaseTag tct) {
        String scriptName = tct.getTestCase().getFile();
        boolean notFound = true;
        String tmpScriptName = null;
        for (int i = 1; notFound; i++) {
            tmpScriptName = scriptName + i;
            if (testCases.containsKey(tmpScriptName)) {
                Integer rowNum = (Integer) testCases.get(tmpScriptName);
                String status = (String) tcModel.getValueAt(rowNum.intValue(), STATUS);
                if (status.equals("Running")) {
                    notFound = false;
                }
            } else {
                notFound = false;
            }
        }
        return tmpScriptName;
    }

    /**
     * Report tc results.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param tct the tct
     */
    protected void reportTcResults(TestCaseTag tct) {
//        String tcFileName = getScriptName(tct);
        String tcFileName = tct.getName();
        int rowNum = ((Integer) testCases.get(tcFileName)).intValue();

        executionPanel.testCaseTable.setRowSelectionInterval(rowNum, rowNum);
        Rectangle rowLocation = executionPanel.testCaseTable.getCellRect(rowNum, 0, false);
        executionPanel.testCaseTable.scrollRectToVisible(rowLocation);

        TestCaseResult tcr = tct.getResults();
        String status;

        if (tcr.passed()) {
            status = "Passed";
            //           int numRun = tcr.getCountableResults().size();
            if (tcModel.getValueAt(rowNum, RUN) == null) {
                tcModel.setValueAt(new Integer(1), rowNum, RUN);
            } else {
                int numRun = (Integer) tcModel.getValueAt(rowNum, RUN);
                numRun++;
                tcModel.setValueAt(new Integer(numRun), rowNum, RUN);
            }
        } else {
            status = "Failed";

            if (tcModel.getValueAt(rowNum, RUN) == null) {
                tcModel.setValueAt(new Integer(1), rowNum, RUN);
            } else {
                int numRun = (Integer) tcModel.getValueAt(rowNum, RUN);
                numRun++;
                tcModel.setValueAt(new Integer(numRun), rowNum, RUN);
            }

            //            int numFailed = tcr.getFailedCountableResults().size();
            if (tcModel.getValueAt(rowNum, FAIL) == null) {
                tcModel.setValueAt(new Integer(1), rowNum, FAIL);
            } else {
                int numFailed = (Integer) tcModel.getValueAt(rowNum, FAIL);
                numFailed++;
                tcModel.setValueAt(new Integer(numFailed), rowNum, FAIL);

            }
        }
        tcModel.setValueAt(status, rowNum, STATUS);

        String execTime = executionTimeToString(tcr.getExecutionTime());
        tcModel.setValueAt(execTime, rowNum, EXEC_TIME);
//        int numRun = tcr.getCountableResults().size();
//        int numFailed = tcr.getFailedCountableResults().size();
//        tcModel.setValueAt(new Integer(numRun), rowNum, RUN);
//        tcModel.setValueAt(new Integer(numFailed), rowNum, FAIL);
        tcModel.fireTableDataChanged();
        results.add(tcr);
    }

    /**
     * Execution time to string.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param time the time
     * @return the string
     */
    public static String executionTimeToString(long time) {
        long hours, mins, secs, tempTime;
        hours = time / 3600000;
        tempTime = time % 3600000;
        mins = tempTime / 60000;
        tempTime = tempTime % 60000;
        secs = tempTime / 1000;
        tempTime = tempTime % 1000;
        String msS = null;
        if (tempTime < 10) {
            msS = "00" + tempTime;
        } else if (tempTime < 100) {
            msS = "0" + time;
        } else {
            msS = tempTime + "";
        }
        String formattedTime = concatNum(hours, "h ");
        formattedTime += concatNum(mins, "m ");
        if (time == 0 || secs > 0 || tempTime > 0) {
            formattedTime += secs + "." + msS + "s";
        }

        return formattedTime.trim();
//        return hours+"h "+mins+"m "+secs+"."+msS+"s";
    }

    /**
     * Concat num.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param num the num
     * @param postfix the postfix
     * @return the string
     */
    private static String concatNum(long num, String postfix) {
        String concat = "";
        if (num > 0) {
            concat = num + postfix;
        }
        return concat;
    }

    ///////////////////////////////////////////////////////////////////////////////
    //          TestStatusListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * This method is called by the RITestEngine when there is a test status
     * update to report.
     *
     * Add the received status information to the statusTextArea of the
     * TestExecutionPanel.
     *
     * @param theUpdate - the status information
     */
    public void updateStatus(String theUpdate) {
        StatusLogCache.getInstance().addTestStatus(DateUtils.now(), theUpdate, testIndex);
        ActionLogCache.getInstance().setCurrentTestIndex(testIndex);
    }

    /**
     * This method is called by the RITestEngine when the test has completed its
     * execution.
     *
     * Set the terminateButton label to Close. Disable the user's ability to
     * press the pause and resume buttons.
     *
     */
    @Override
    public void testComplete() {
        testComplete = true;
        ActionLogCache.getInstance().setCurrentTestIndex(testIndex);
        testIndex++;        
        executionPanel.terminateButton.setText("Close");  // Changed from Done to Close per Release 2+ Walkthrough
        executionPanel.terminateButton.setMnemonic(KeyEvent.VK_C);
        executionPanel.jRunningLabel.setText("Complete...");
        executionPanel.jRunningLabel.setVisible(false);
        executionPanel.jTestExecutionProgressBar.setIndeterminate(false);
        if (executionPanel.jTestExecutionProgressBar.isVisible()) {
            executionPanel.jTestExecutionProgressBar.setVisible(false);
        }
        executionPanel.pauseButton.setEnabled(false);
        executionPanel.resumeButton.setEnabled(false);
        executionPanel.infoStandardTree.setEnabled(true);
        executionPanel.appStandardTree.setEnabled(true);

    }
    ///////////////////////////////////////////////////////////////////////////////
    //          TestActionListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////

    /**
     * This method is called by the RITestEngine when there is an update in test
     * action information.
     *
     * When called, append the provide update information to the actionTextArea
     * of the executionPanel.
     *
     * @param timestamp - the timestamp the update information received
     * @param theUpdate - the test action update information received
     * @param result - the result of the action update
     */
    public void updateActions(String timestamp,String theUpdate, String result) {
        ActionLogCache.getInstance().addAction(DateUtils.now(),theUpdate,"");
    }

    /**
     * Post condition error event.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void postConditionErrorEvent() {
        RILogging.logEvent("A Post-Condition Error Event Paused the Test");
        testEngine.pauseTest();
        executionPanel.runButton.setEnabled(false);
        executionPanel.resumeButton.setEnabled(true);
        executionPanel.pauseButton.setEnabled(false);
        Object[] options = {"OK"};
        int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                "A Post Condition Action Failed to complete.\n  The test has been paused.",
                "PostCondition Error Warning",
                javax.swing.JOptionPane.OK_OPTION,
                javax.swing.JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);

    }

    //REMOVE THIS ???
    /**
     * Test step update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param ft the ft
     */
    public void testStepUpdate(FunctionTag ft) {
//        addTestStepRow(ft);
    }

    ///////////////////////////////////////////////////////////////////////////////
    //          TestCaseListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * Begin test case.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param event the event
     */
    public void beginTestCase(TestCaseEvent event) {
                    // Set the table to automatically show the last updated row updated.
                    executionPanel.testCaseTable.addComponentListener(testCaseListener);

                    // Set the table to automatically show the last updated row updated.
                    executionPanel.testResultsTable.addComponentListener(testStepsListener);
        
//        TestCaseTag tct = (TestCaseTag) event.getSource();
//        addTestCaseRow(tct);
//        if (executionPanel.testCaseTable.getSelectedRow() == -1) {
        //           clearReasonTable();
//        }
    }

    /**
     * End test case.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param event the event
     */
    public void endTestCase(TestCaseEvent event) {
        // Disable the table from automatically showing the last updated row updated.
        executionPanel.testCaseTable.removeComponentListener(testCaseListener);

        // Disable the table from automatically showing the last updated row updated.
        executionPanel.testResultsTable.removeComponentListener(testStepsListener);

        TestCaseTag tct = (TestCaseTag) event.getSource();
//        reportTcResults(tct);
    }

    ///////////////////////////////////////////////////////////////////////////////
    //          TestCaseResultsListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * Test case update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    @Override
    public void testCaseUpdate() {
//        int selectedRow = executionPanel.testCaseTable.getSelectedRow();
        
        tcModel.fireTableDataChanged();
        int rowCount = executionPanel.testCaseTable.getRowCount();
        tcModel.fireTableRowsInserted(rowCount, rowCount);
        
//        if (selectedRow != -1){
//            executionPanel.testCaseTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
//        } else {            
//            selectedRow = executionPanel.testCaseTable.getRowCount()-1;
//            if (selectedRow > -1){
//                int viewRow = executionPanel.testCaseTable.convertRowIndexToView(selectedRow);
//                JScrollPane jsp = (JScrollPane) executionPanel.testCaseTable.getParent();
//                
//                executionPanel.testCaseTable.scrollRectToVisible(executionPanel.testCaseTable.getCellRect(viewRow, 0, true));
//                executionPanel.testCaseTable.getSelectionModel().setSelectionInterval(selectedRow, selectedRow);
//                executionPanel.testCaseTable.getSelectionModel().clearSelection();
//            }
//        }
        
//        int rowCount = executionPanel.testCaseTable.getRowCount();
//        executionPanel.testCaseTable.addRowSelectionInterval(rowCount, rowCount);
    }

    /**
     * Test step update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    @Override
    public void testStepUpdate() {
        tcReasonModel.fireTableDataChanged();
        int rowCount = executionPanel.testResultsTable.getRowCount();
// Appears to stop the bell from ringing.  Don't know why or what else might be affected right now.        tcReasonModel.fireTableRowsInserted(rowCount, rowCount);
        //       executionPanel.testResultsTable.addRowSelectionInterval(rowCount, rowCount);
    }

    /**
     * Handles actions from the TestExecutionPanel
     *
     * If the Close button was pressed: remove the execution panel from the
     * users view call the testEngine terminateTest method remove this object as
     * an actionlistener for each of the panel's buttons notify the parentObject
     * listener that the testExecution is completed
     *
     * If the run button was pressed: notify the test engine that the test was
     * started notify the parent TestExecutionListener that the test was started
     * disable the run and resume buttons enable the pause button
     *
     * If the pause button was pressed: notify the test engine that the test was
     * paused disable the run and pause buttons enable the resume button
     *
     * If the resume button was pressed: notify the test engine that the test
     * should be resumed disable the resume button (the run button is already
     * disabled) enable the pause button.
     *
     * @param e the action event received
     */
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        try {

            if (source == executionPanel.terminateButton) {
                RILogging.logEvent(TestExecutionCoordinator.class.getName(), RILogging.RI_USER_EVENT, "User selected test Termination");
                testEngine.pauseTest();

                Object[] options = {"Yes",
                    "No"};
                int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                        "Are you sure you want to terminate the test?",
                        "Test Termination Warning",
                        javax.swing.JOptionPane.YES_NO_OPTION,
                        javax.swing.JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                if (n == 0) {
//                    testEngine.resumeTest();
                    testEngine.terminateTest();
                    RILogging.logEvent(TestExecutionCoordinator.class.getName(), RILogging.RI_USER_EVENT, "User confirmed test Termination");
                    RILogging.removeGUIListener(ActionLogCache.getInstance());
                    
                    executionPanel.terminateButton.setText("Close");
                    executionPanel.terminateButton.setMnemonic(KeyEvent.VK_T);

                    executionPanel.terminateButton.removeActionListener(this);
                    executionPanel.runButton.removeActionListener(this);
                    executionPanel.pauseButton.removeActionListener(this);
                    executionPanel.resumeButton.removeActionListener(this);

                    testCaseResults.unRegisterListner(this);
                    executionPanel.setVisible(false);
                    executionPanel.infoStandardTree.removeAll();
                    executionPanel.appStandardTree.removeAll();
                    testCaseResults.clear();
                    executionPanel.testCaseTable.removeAll();
                    executionPanel.testResultsTable.removeAll();
                    parentObject.testExecutionCompleted();
                    CenterMonitor.getInstance().terminateTest();
                    KeyboardFocusManager focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                    focusManager.addPropertyChangeListener(null);
                    executionPanel.infoStandardTree.setModel(null);
                    executionPanel.appStandardTree.setModel(null);
                    ActionLogCache.getInstance().stop();
                    StatusLogCache.getInstance().stop();                    
                } else {
                    RILogging.logEvent(TestExecutionCoordinator.class.getName(), RILogging.RI_USER_EVENT, "User Cancelled Test Termination");
                    testEngine.resumeTest();
                }
            } else if (source == executionPanel.runButton) {
                TestLogList.getInstance().pauseTestLogListing();
                executionPanel.infoStandardTree.setEnabled(false);
                executionPanel.appStandardTree.setEnabled(false);
                final TreePath[] selectedInfoTcs = executionPanel.infoStandardTree.getSelectionPaths();
                final TreePath[] selectedAppTcs = executionPanel.appStandardTree.getSelectionPaths();
                executionPanel.infoStandardTree.clearSelection();
                executionPanel.appStandardTree.clearSelection();

                executionPanel.terminateButton.setText("Terminate");
                executionPanel.terminateButton.setMnemonic(KeyEvent.VK_T);

                int infoTestCases = 0;
                int appTestCases = 0;

                if (selectedInfoTcs != null) {
                    infoTestCases = selectedInfoTcs.length;
                }
                if (selectedAppTcs != null) {
                    appTestCases = selectedAppTcs.length;
                }

                if ((infoTestCases + appTestCases) > 0) {
//                ArrayList<URL> selectedAppTestCases = new ArrayList<URL>();
//                ArrayList<URL> selectedInfoTestCases = new ArrayList<URL>();
                    ArrayList<TestCase> selectedAppTestCases = new ArrayList<TestCase>();
                    ArrayList<TestCase> selectedInfoTestCases = new ArrayList<TestCase>();
//                URL[] selectedTestCases = new URL[infoTestCases + appTestCases];

                    if (infoTestCases > 0) {
                        for (int i = 0; i < selectedInfoTcs.length; i++) {
                            Object obj = selectedInfoTcs[i].getLastPathComponent();
                            if (obj instanceof TCTreeNode) {
                                TCTreeNode tn = (TCTreeNode) obj;
                                Object tnobj = tn.getUserObject();
                                if (tnobj instanceof TestCase) {
                                    TestCase tc = (TestCase) tn.getUserObject();
                                    try {
                                        selectedInfoTestCases.add(tc);
//                                    selectedInfoTestCases.add(tc.getScriptUrlLocation());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    if (appTestCases > 0) {
                        for (int i = 0; i < selectedAppTcs.length; i++) {
                            Object obj = selectedAppTcs[i].getLastPathComponent();
                            if (obj instanceof TCTreeNode) {
                                TCTreeNode tn = (TCTreeNode) obj;
                                Object tnobj = tn.getUserObject();
                                if (tnobj instanceof TestCase) {
                                    TestCase tc = (TestCase) tn.getUserObject();
                                    try {
                                        selectedAppTestCases.add(tc);
//                                    selectedAppTestCases.add(tc.getScriptUrlLocation());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    /**
                     * For internal developer testing File tstFile = new
                     * File("c://inout//jameleon-test-suite/scripts/Session-C2CRI-tags.xml");
                     * try { selectedTestCases.add(tstFile.toURI().toURL());
                     *
                     * } catch (Exception ex){ System.out.println("Extra Test
                     * Case File Error"); ex.printStackTrace(); }
                     */
                    if ((selectedAppTestCases.size() + selectedInfoTestCases.size()) > 0) {
                        String appTestCaseList = "";
                        for (TestCase thisTestCase : selectedAppTestCases) {
                            appTestCaseList = appTestCaseList.concat((appTestCaseList.isEmpty() ? "" : ", ") + thisTestCase.getName());
                        }
                        String infoTestCaseList = "";
                        for (TestCase thisTestCase : selectedInfoTestCases) {
                            infoTestCaseList = infoTestCaseList.concat((infoTestCaseList.isEmpty() ? "" : ", ") + thisTestCase.getName());
                        }
                        RILogging.logEvent(TestExecutionCoordinator.class.getName(), RILogging.RI_USER_EVENT, "User Started the Test with selected Test Case Scripts:"
                                + (appTestCaseList.isEmpty() ? "" : "Application Layer: " + appTestCaseList) + " " + (infoTestCaseList.isEmpty() ? "" : "Information Layer: " + infoTestCaseList));
                        executionPanel.pauseButton.setEnabled(true);
                        executionPanel.resumeButton.setEnabled(false);
                        executionPanel.runButton.setEnabled(false);
                        executionPanel.terminateButton.setText("Terminate");
                        executionPanel.jRunningLabel.setText("Running...");
                        executionPanel.jRunningLabel.setVisible(true);
                        executionPanel.jTestExecutionProgressBar.setIndeterminate(true);
                        executionPanel.jTestExecutionProgressBar.setVisible(true);
                        testComplete = false;
                        parentObject.testExecutionStarted();
                        if ((selectedAppTestCases.size() > 0) && (executionPanel.jTabbedPaneTestCases.getSelectedIndex()==APPLAYERTABINDEX)){
                            testEngine.startTest(selectedAppTestCases);
                        }

                        if ((selectedInfoTestCases.size() > 0) && (executionPanel.jTabbedPaneTestCases.getSelectedIndex()==INFOLAYERTABINDEX)){
                            if (initialRun) {
                                initialRun = false;
                                Object[] options = {"OK"};
                                int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                                        "Succesful performance of Information Layer test cases is usually dependent on the underlying Application Layer Standard. \nIt is recommended that the related "
                                        + "Application layer test cases be performed before executing Information Layer test cases.",
                                        "Information Layer TestCase Warning",
                                        javax.swing.JOptionPane.OK_OPTION,
                                        javax.swing.JOptionPane.WARNING_MESSAGE,
                                        null,
                                        options,
                                        options[0]);
                            }
                            testEngine.startTest(selectedInfoTestCases);

                        }

                    } else {
                        Object[] options = {"OK"};
                        int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                                "One or more Test Cases must be selected to run the test.",
                                "TestCase Selection Error",
                                javax.swing.JOptionPane.OK_OPTION,
                                javax.swing.JOptionPane.ERROR_MESSAGE,
                                null,
                                options,
                                options[0]);

                    }
                } else {
                    Object[] options = {"OK"};
                    int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                            "One or more Test Cases must be selected to run the test.",
                            "TestCase Selection Error",
                            javax.swing.JOptionPane.OK_OPTION,
                            javax.swing.JOptionPane.ERROR_MESSAGE,
                            null,
                            options,
                            options[0]);

                }

            } else if (source == executionPanel.pauseButton) {
                if (!testComplete) {
                    RILogging.logEvent(TestExecutionCoordinator.class.getName(), RILogging.RI_USER_EVENT, "User Paused the Test");
                    testEngine.pauseTest();
                    executionPanel.runButton.setEnabled(false);
                    executionPanel.resumeButton.setEnabled(true);
                    executionPanel.pauseButton.setEnabled(false);
                    executionPanel.jRunningLabel.setText("Paused...");
                }
            } else if (source == executionPanel.resumeButton) {
                if (!testComplete) {
                    RILogging.logEvent(TestExecutionCoordinator.class.getName(), RILogging.RI_USER_EVENT, "User Resumed the Test");
                    testEngine.resumeTest();
                    executionPanel.pauseButton.setEnabled(true);
                    executionPanel.resumeButton.setEnabled(false);
                    executionPanel.jRunningLabel.setText("Running...");
                }                     
            } else {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Object[] options = {"OK"};
            int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                    ex.getMessage(),
                    "Test Execution Error",
                    javax.swing.JOptionPane.OK_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]);

        } finally {
            TestLogList.getInstance().resumeTestLogListing();
        }
    }

    /**
     * The Class TestCaseRenderer.
     *
     * @author TransCore ITS, LLC Last Updated: 1/8/2014
     */
    class TestCaseRenderer extends DefaultTreeCellRenderer {

        /**
         * Instantiates a new test case renderer.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         */
        public TestCaseRenderer() {
			// original implementation was empty
        }

        /* (non-Javadoc)
         * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
         */
        public Component getTreeCellRendererComponent(
                JTree tree,
                Object value,
                boolean sel,
                boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {

            super.getTreeCellRendererComponent(
                    tree, value, sel,
                    expanded, leaf, row,
                    hasFocus);
            if (leaf) {
                TCTreeNode theNode = (TCTreeNode) value;
                Object obj = theNode.getUserObject();
                if (obj instanceof TestCase) {
                    TestCase theTestCase = (TestCase) theNode.getUserObject();
                    setToolTipText(splitString(theTestCase.getDescription()));
//                    System.out.println("The ToolTip Text was set to "+ theTestCase.getDescription());
                } else {
                    setToolTipText(null);
                }
            } else {
                setToolTipText(null); //no tool tip
            }

            return this;
        }

///////////////////////////////////////////////////////////////////
//This method takes a String of text and simulates word wrapping
//by applying HTML code <BR> after 60 characters per line.  It
//will check to make sure that we are not in the middle of a word
//before breaking the line.
///////////////////////////////////////////////////////////////////
        private String splitString(String string) {

            StringBuffer buf = new StringBuffer();
            String tempString = string;

            if (string != null) {
                buf.append("<html>");
                while (tempString.length() > 60) {
                    String block = tempString.substring(0, 60);
                    int index = block.lastIndexOf(' ');
                    if (index < 0) {
                        index = tempString.indexOf(' ');
                    }
                    if (index >= 0) {
                        buf.append(tempString.substring(0, index) + "<br>");
                    }
                    tempString = tempString.substring(index + 1);
                }
                buf.append(tempString);
                buf.append("</html>");
            } else {
                buf.append(" ");
            }
            return buf.toString();

        }

    }
}
