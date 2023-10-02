package org.fhwa.c2cri.testmodel;

//import net.sf.jameleon.ExecuteTestCase;
import net.sf.jameleon.event.FunctionEventHandler;
import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.event.DataDrivableEventHandler;
import net.sf.jameleon.event.TestRunEventHandler;
import net.sf.jameleon.event.DataDrivableListener;
import net.sf.jameleon.event.FunctionListener;
import net.sf.jameleon.event.TestCaseListener;
import net.sf.jameleon.event.TestRunListener;
import net.sf.jameleon.event.DataDrivableEvent;
import net.sf.jameleon.event.FunctionEvent;
import net.sf.jameleon.event.TestCaseEvent;
import net.sf.jameleon.event.TestRunEvent;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.function.FunctionTag;
import net.sf.jameleon.data.DataDrivable;
import org.fhwa.c2cri.plugin.c2cri.event.VerificationEvent;
import org.fhwa.c2cri.plugin.c2cri.event.VerificationEventHandler;
import org.fhwa.c2cri.plugin.c2cri.event.VerificationListener;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import net.sf.jameleon.TestCaseTagLibrary;
import net.sf.jameleon.function.TestStepTag;
import net.sf.jameleon.result.FunctionResult;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.StateStorer;
import org.apache.commons.jelly.JellyException;
import org.apache.log4j.helpers.Transform;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.RINRTMSelection;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.gui.BasicGUIActionWrapper;
import org.fhwa.c2cri.utilities.DateUtils;
import org.fhwa.c2cri.logger.RILogging;
import org.fhwa.c2cri.plugin.c2cri.tags.RISubscriptTag;
import org.fhwa.c2cri.testmodel.verification.UserVerificationStatus;
import org.fhwa.c2cri.utilities.Checksum;
import org.fhwa.c2cri.utilities.ProgressMonitor;
import org.fhwa.c2cri.utilities.RIVerification;
import org.fhwa.c2cri.utilities.RIVerificationResults;

/**
 * Primary RI class for initiating and maintaining user initiated tests. It
 * receives messages from the operator via the TestExecutionPanel and forwards
 * the requests (start, pause, resume, terminate) to Jameleon.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class RITestEngine implements TestCaseListener, FunctionListener, DataDrivableListener, TestRunListener, VerificationListener {

    /**
     * The stop execution.
     */
    protected static Boolean stopExecution = true;

    /**
     * The pause execution.
     */
    private volatile boolean pauseExecution = false;

    /**
     * The executing.
     */
    private static Boolean executing = false;
	
	private static final Object LOCK = new Object();

    /**
     * The debug.
     */
    final boolean debug = true;

    /**
     * The status listener.
     */
    private static TestStatusListener statusListener;


    /**
     * The test case listener.
     */
    private static TestCaseListener testCaseListener;

    /**
     * The function listener.
     */
    private static FunctionListener functionListener;

    /**
     * The data listener.
     */
    private static DataDrivableListener dataListener;

    /**
     * The verification listener.
     */
    private static VerificationListener verificationListener;

    /**
     * The test run listener.
     */
    private static TestRunListener testRunListener;

    /**
     * The test case forward listener.
     */
    private static TestCaseListener testCaseForwardListener;

    /**
     * The test step map.
     */
    private static HashMap<FunctionTag, Integer> testStepMap = new HashMap();

    /**
     * The t.
     */
    private static Thread t;

    /**
     * The etc thread1.
     */
    private static Thread etcThread1;

    /**
     * The test config.
     */
    private TestConfiguration testConfig;

    /**
     * The logger.
     */
    private RILogging logger;

    /**
     * The tc results.
     */
    private TestCaseResults tcResults;

    /**
     * The test step failure.
     */
    private boolean testStepFailure = false;

    /**
     * The setting that determines whether device emulation should be used
     * during testing. *
     */
    private boolean emulationEnabled = false;

    /**
     * The setting that determines whether device emulation should be
     * reinitialized before each test case. *
     */
    private boolean reinitializeEmulation = false;

    /**
     * The information layer standard selected for emulation.
     */
    private String emulationInformationLayerStandard;
    
    /**
     * The Application layer standard that will be used in emulation.
     */
    private String emulationApplicationLayerStandard;

    private String primaryIteration = "";
    
    // reference to the last thread that provided a tag notification.
    private static Thread lastTagThread;
    
    /**
     * Notify Jameleon to terminate the currently running test. Log this action.
     */
    public void terminateTest() {
//        BasicGUIActionWrapper initTerminateTestAction = new BasicGUIActionWrapper(null, "Completing the RI Test Termination Process ...") {
//
//            @Override
//            protected Boolean actionMethod() throws Exception {
//                setStopExecution(true);
//                RILogging.logEvent(RITestEngine.class.getName(), RILogging.RI_USER_EVENT, "User Confirmed Test Termination");
//                testStepMap.clear();
//                TestResults theResult = new TestResults(testConfig, tcResults);
////        System.out.println(theResult.to_LogFormat());
//                logger.logEvent(RITestEngine.class.getName(), RILogging.RI_VERIFICATION_EVENT, theResult.to_LogFormat());
////        javax.swing.JOptionPane.showMessageDialog(null, "TestEngine finsihed logging the test results.", "RITESTENGINE", javax.swing.JOptionPane.INFORMATION_MESSAGE);
//
//                logger.stopLogging();
//                RIEmulation.getInstance().setEmulationEnabled(false);
//                RIEmulation.getInstance().setNrtmSelections(new HashMap<String, String>());
//                tcResults.clear();
//                return true;
//            }
//
//            @Override
//            protected void wrapUp(Boolean result) {
//                if (!result) {
//                    javax.swing.JOptionPane.showMessageDialog(null, "An error was encountered trying to complete the Test Termination action.");
//                }
//            }
//        };
//        initTerminateTestAction.execute();

        ProgressMonitor monitor = ProgressMonitor.getInstance(
                null,
                "C2C RI",
                "Storing and signing the log file.",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE);
        setStopExecution(true);
        if ((lastTagThread != null)&&lastTagThread.isAlive()){
            System.out.println("Sending a Terminate Interrupt to thread "+lastTagThread.getName()+" with Stack Trace: \n");
            for (StackTraceElement stElement : lastTagThread.getStackTrace()){
                System.out.println(stElement.toString());
            }
            lastTagThread.interrupt();
        }
        RILogging.logEvent(RITestEngine.class.getName(), RILogging.RI_USER_EVENT, "User Confirmed Test Termination");
        testStepMap.clear();
        monitor.show();
        TestResults theResult = new TestResults(testConfig, tcResults);
//        System.out.println(theResult.to_LogFormat());
        logger.logEvent(RITestEngine.class.getName(), RILogging.RI_VERIFICATION_EVENT, theResult.to_LogFormat());
//        javax.swing.JOptionPane.showMessageDialog(null, "TestEngine finsihed logging the test results.", "RITESTENGINE", javax.swing.JOptionPane.INFORMATION_MESSAGE);

        tcResults.clear();
        theResult = null;

        logger.stopLogging();
        RIEmulation.getInstance().setEmulationEnabled(false);            
        RIEmulation.getInstance().setNrtmSelections(new ArrayList<RINRTMSelection>());            
        monitor.dispose();

//        javax.swing.JOptionPane.showMessageDialog(null, "TestEngine stopped the logger.", "RITESTENGINE", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * method to restart the previously paused test. Log this action.
     */
    public void resumeTest() {
        pauseExecution = false;
    }

    /**
     * pause the previously started test being executed by Jameleon. Log this
     * action.
     */
    public void pauseTest() {
        pauseExecution = true;
    }

    /**
     * Check for stop.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param tct the tct
     */
    private void checkForStop(TestCaseTag tct) {
        if (getStopExecution()) {
            tct.setExecuteTestCase(false);
        }
    }

    /**
     * Gets the stop execution.
     *
     * @return the stop execution
     */
    public Boolean getStopExecution() {
        boolean results;
        synchronized (LOCK) {
            results = RITestEngine.stopExecution;
        }
        return results;
    }

    /**
     * Sets the stop execution.
     *
     * @param stopExecution the new stop execution
     */
    public void setStopExecution(Boolean stopExecution) {
        synchronized (LOCK) {
            RITestEngine.stopExecution = stopExecution;
        }
    }

    /**
     * Gets the executing.
     *
     * @return the executing
     */
    public Boolean getExecuting() {
        boolean results;
        synchronized (LOCK) {
            results = RITestEngine.executing;
        }
        return results;
    }

    /**
     * Sets the executing.
     *
     * @param executing the new executing
     */
    public void setExecuting(Boolean executing) {
        synchronized (LOCK) {
            RITestEngine.executing = executing;
        }
    }

    /**
     * Constructor method for initializing the test engine for a test.
     *
     * It performs all test setup scripts available in the Test Suite.
     *
     * It sends test information to RILogging to initialize the test log.
     *
     * @param testConfig the test config
     * @param tcResults the tc results
     * @param testName the test name
     * @param testDescription the test description
     * @param testConfigName the test config name
     */
    public RITestEngine(TestConfiguration testConfig, TestCaseResults tcResults, String testName, String testDescription, String testConfigName, boolean emulationEnabled, boolean reinitializeEmulation) throws EntityEmulationException {
        testRunListener = this;
        testCaseListener = this;
        functionListener = this;
        dataListener = this;
        verificationListener = this;
        lastTagThread = null;

        this.tcResults = tcResults;
        this.testConfig = testConfig;
        logger = new RILogging();
        UserVerificationStatus.getInstance().skipVerification(false);

        Checksum cs = new Checksum();
        String checksum = "";
        try {
            checksum = cs.getChecksum(testConfigName);
        } catch (Exception ex) {
            checksum = "Error - " + ex.getMessage();
        }

        if (!testConfig.getTestMode().isExternalCenterOperation()) {
            this.emulationEnabled = emulationEnabled;
            this.reinitializeEmulation = reinitializeEmulation;
        } else {
            this.emulationEnabled = false;
            this.reinitializeEmulation = false;
        }

        if (emulationEnabled) {
            RIEmulation.getInstance().setNrtmSelections(getNRTMSelections());            
            RIEmulation.getInstance().initialize(TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedInfoLayerTestSuite()), testConfig.getEmulationParameters());
            RIEmulation.getInstance().setEmulationEnabled(true);
            this.emulationApplicationLayerStandard = TestSuites.getInstance().getAppLayerStandard(testConfig.getSelectedInfoLayerTestSuite());
            this.emulationInformationLayerStandard = TestSuites.getInstance().getInfoLayerStandard(testConfig.getSelectedInfoLayerTestSuite());
        } else {
            RIEmulation.getInstance().setEmulationEnabled(false);            
        }

        logger.configureLogging(testName, testConfigName, testDescription, checksum, emulationEnabled, reinitializeEmulation);

        // Remove any invalid XML characters that may exist.
        // ref : http://www.w3.org/TR/REC-xml/#charsets
        // jdk 7
        Pattern xmlInvalidChars =
         Pattern.compile("[^\\u0009\\u000A\\u000D\\u0020-\\uD7FF\\uE000-\\uFFFD\\x{10000}-\\x{10FFFF}]");
        logger.logEvent(RITestEngine.class.getName(), RILogging.RI_INIT_EVENT, xmlInvalidChars.matcher(testConfig.to_LogFormat()).replaceAll(""));

    }

    /**
     * method for registering a listener to receive notification of test status.
     *
     * @param theListener the the listener
     */
    @SuppressWarnings("static-access")
    public void registerTestStatusListener(TestStatusListener theListener) {
        RITestEngine.statusListener = theListener;
    }


    /**
     * method for registering a listener to receive notification of test
     * actions.
     *
     * @param theListener the the listener
     */
    @SuppressWarnings("static-access")
    public void registerTestCaseForwardListener(TestCaseListener theListener) {
        RITestEngine.testCaseForwardListener = theListener;
    }


    /**
     * Gets the test suite plugins.
     *
     * @return the test suite plugins
     */
    public String getTestSuitePlugins() {
        String results = "";
        ArrayList<String> testSuites = TestSuites.getInstance().getAvailableSuites();
        for (String testSuite : testSuites) {
            if (TestSuites.getInstance().isPredefined(testSuite)) {
                try {
                    URL jarURL = new URL(TestSuites.getInstance().getTestSuitePath(testSuite).replace("/.", "").replace("!", "").replace("jar:", ""));
                    try (ZipInputStream zip = new ZipInputStream(jarURL.openStream()))
					{
						while (true) {
							ZipEntry e = zip.getNextEntry();
							if (e == null) {
								break;
							}

							if (e.getName().endsWith(".properties") && !e.getName().equalsIgnoreCase("SuiteSpec.properties") && e.getName().indexOf("/") == -1) {
								System.out.println("Entry is :" + e.getName());
								results = results.concat(" " + e.getName().replace(".properties", ""));
							}
						}
                    }
                } catch (Exception ex) {
                    System.err.println("TestSuitePathFailure: " + TestSuites.getInstance().getTestSuitePath(testSuite).replace("/.", "").replace("!", "").replace("jar:", ""));
                    ex.printStackTrace();
                }
            }
        }

        return results;
    }

    /**
     * Notify Jameleon to start running the specified test suite, and provide
     * its global context with user defined app and info layer parameters. Log
     * this test action.
     *
     * For each main script available in the Test Suite, provide each to
     * Jameleon to be run.
     *
     * @param testCaseList the test case list
     */
    public void startTest(final ArrayList<TestCase> testCaseList) throws EntityEmulationException {

        setStopExecution(false);

        ClassLoader theContext = Thread.currentThread().getContextClassLoader();
        testStepMap.clear();

        final Thread tTest = new Thread() {

            @Override
            public void run() {
                String pluginString = Configurator.getInstance().getValue("plugins");
                pluginString = pluginString.concat(getTestSuitePlugins());
                System.out.println("Plugin Add On = " + pluginString);
//                pluginString = pluginString.concat(" NTCIP2306v01_69");
//                pluginString = pluginString.concat(" TMDDv303c");
                Configurator.getInstance().setValue("plugins", pluginString);
                TestCaseTagLibrary.resetTags();
                RIExecuteTestCase executor = new RIExecuteTestCase(testConfig, emulationInformationLayerStandard, emulationApplicationLayerStandard);
                executor.registerEventListeners();
                //              String filePath = "";
                URL scriptFile = null;
                try {
                    scriptFile = TestSuites.getInstance().getTestSuiteMainScriptURL(testConfig.getSelectedInfoLayerTestSuite());
                } catch (Exception e) {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Script file designation failed: \n"
                            + e.getMessage(),
                            "C2C RI",
                            javax.swing.JOptionPane.ERROR_MESSAGE);

                }
//                File file1 = new File(filePath);
                /**
                 *
                 * if
                 * (testConfig.getSelectedInfoLayerTestSuite().startsWith("*")){
                 * System.out.println(" This is a Custom Test Suite
                 * =>"+testConfig.getSelectedInfoLayerTestSuite()); String
                 * tempPath =
                 * TestSuites.getInstance().getTestSuitePath(testConfig.getSelectedInfoLayerTestSuite());
                 * System.out.println("Before reducing the path =>"+ tempPath);
                 * if (tempPath.contains(".\\")){ int searchIndex =
                 * tempPath.indexOf(".\\"); filePath = tempPath.substring(0,
                 * searchIndex); filePath = filePath +
                 * tempPath.substring(searchIndex+2); filePath = filePath +
                 * File.separator+"Scripts"; System.out.println("After reducing
                 * the path =>"+ filePath); } }else { System.out.println(" This
                 * is a Pre-defined Test Suite
                 * =>"+testConfig.getSelectedInfoLayerTestSuite()); filePath =
                 * System.getProperty("user.dir"); }
                 *
                 * File file1 = new File(filePath+File.separator+"Main.xml");
                 */
                //        System.out.println("Does the file " + file1.getPath() + "  exist?" + file1.exists());
                setExecuting(true);
                for (TestCase thisTestCase : testCaseList) {
//                    if (getExecuting()){
                    etcThread1 = executeTestCase(thisTestCase, debug, executor);
                    while (getExecuting()) {
                        if (getStopExecution()) {
                            //                                      resultsPane.stopExecution();
                            if (!this.isInterrupted()) { // If the thread hasn't already been interrupted then interrupt
                                setExecuting(false);
                                this.interrupt();
                            }
                            break;
                        } else {
                            this.interrupted(); // clear an interruption if set otherwise do nothing
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ie) {
							Thread.currentThread().interrupt();
                            javax.swing.JOptionPane.showMessageDialog(null,
                                    "Test Case " + thisTestCase.getName() + " thread failed: \n"
                                    + ie.getMessage(),
                                    "C2C RI",
                                    javax.swing.JOptionPane.ERROR_MESSAGE);

                            //Apparently people don't like a stack trace printing out to the screen.
                            //ie.printStackTrace();
                        }
                    }

                    if (!etcThread1.isAlive()) {
                        etcThread1 = null;
                    };
//                    }
                }
                /**
                 * }
                 * }
                 * }
                 */
                executor.deregisterEventListeners();
                executor = null;
                RITestEngine.statusListener.testComplete();
            }
        };
        if (emulationEnabled && reinitializeEmulation) {
            BasicGUIActionWrapper initEmulationAction = new BasicGUIActionWrapper(null, "Initializing the RI Emulation Settings") {

                @Override
                protected Boolean actionMethod() throws Exception {
                    RIEmulation.getInstance().initialize(TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedInfoLayerTestSuite()), testConfig.getEmulationParameters());
                    return true;
                }

                @Override
                protected void wrapUp(Boolean result) {
                    if (!result) {
                        javax.swing.JOptionPane.showMessageDialog(null, "An error was encountered trying to complete the Entity Emulation Initialization action.");
                    }
                    tTest.start();
                }
            };
            initEmulationAction.execute();
        } else {
            tTest.start();
        }

    }

    /**
     * Execute test case.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param fn the fn
     * @param debug the debug
     * @param executor the executor
     * @return the thread
     */
    protected Thread executeTestCase(final TestCase fn, final boolean debug, final RIExecuteTestCase executor) {
        setExecuting(true);
        t = new Thread() {

            @Override
            public void run() {
//                        tcTree.setTestCaseSource(fn.getFile(), false);
                /**
                 * *********************************************************
                 * Added for RI POC
                 *
                 */
                /**
                 * Change the output of logging messages to go to the new log
                 * file.
                 */
                /**
                 * boolean fileChanged = false; String fileName = "";
                 *
                 * try{ Logger log = Logger.getRootLogger();
                 * System.out.println("The rootLogger returned -->" +
                 * log.getName()); Enumeration appender_enum =
                 * log.getAllAppenders(); System.out.println("The first appender
                 * returned = " + appender_enum.toString()); FileAppender
                 * stdOutAppender = (FileAppender)log.getAppender("STDOUT");
                 * DateFormat dateFormat = new
                 * SimpleDateFormat("yyyy-MM-dd_HH-mm-ss"); Date date = new
                 * Date(); System.out.println("The current output file is set to
                 * --> " + stdOutAppender.getFile());
                 * System.out.println("Setting the file to -->" +
                 * fn.getFile().toString()); fileName = fn.getFile().toString()
                 * + "-" + dateFormat.format(date);
                 * stdOutAppender.setFile(fn.getFile().toString() + "-" +
                 * dateFormat.format(date)); stdOutAppender.activateOptions();
                 * System.out.println("STDOUT Appender was found and
                 * successfully activated"); fileChanged = true; } catch
                 * (Exception ex) { System.out.println("No STDOUT Appender was
                 * found"); }
                 */
                /**
                 * *********************************************************
                 * End Added for RI POC
                 *
                 */
                TestRunEventHandler runEventHandler = TestRunEventHandler.getInstance();
                TestCaseEventHandler tcEventHandler = TestCaseEventHandler.getInstance();
                FunctionEventHandler fEventHandler = FunctionEventHandler.getInstance();
                DataDrivableEventHandler ddEventHandler = DataDrivableEventHandler.getInstance();
                VerificationEventHandler vEventHandler = VerificationEventHandler.getInstance();

                try {
                    if (debug) {
//                                resultsPane.setDebug(true);
                    }
                    fEventHandler.addFunctionListener(functionListener);
                    tcEventHandler.addTestCaseListener(testCaseListener);
                    ddEventHandler.addDataDrivableListener(dataListener);
                    runEventHandler.addTestRunListener(testRunListener);
                    vEventHandler.addVerificationListener(verificationListener);
                    runEventHandler.beginTestRun(Calendar.getInstance());
//                            fEventHandler.addFunctionListener(resultsPane);
//                            tcEventHandler.addTestCaseListener(resultsPane);
//                            ddEventHandler.addDataDrivableListener(resultsPane);

                    try {
//                                File filename = new File(fn);
                        if (!getStopExecution()) {
                            executor.runScript(fn);
                        }

                        /**
                         * *********************************************************
                         * Added for RI POC
                         *
                         */
                        /**
                         * if (fileChanged) { Logger templog =
                         * Logger.getRootLogger(); FileAppender
                         * tempstdOutAppender =
                         * (FileAppender)templog.getAppender("STDOUT");
                         *
                         * tempstdOutAppender.close(); System.out.println("Now
                         * Altering file "+ fileName + " to remove log4j:
                         * references");
                         *
                         * readReplace(fileName, "log4j:", "");
                         * createLogXML(fileName); System.out.println("Now
                         * Finished creating the LogXML file "+ fileName +
                         * ".xml"); fileChanged = false;
                         *
                         * }
                         */
                        /**
                         * *********************************************************
                         * End Added for RI POC
                         *
                         */
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                        javax.swing.JOptionPane.showMessageDialog(null,
                            getTextInScrollPane("IO Error running test script: \n"
                            + ioe.getMessage()),
                            "C2C RI",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                    } catch (JellyException je) {
                        je.printStackTrace();
//                        if (!getStopExecution()){
//                        javax.swing.JOptionPane.showMessageDialog(null,
//                            getTextInScrollPane("Exception running test script: \n"
//                            + je.getMessage()),
//                            "C2C RI",
//                            javax.swing.JOptionPane.ERROR_MESSAGE);
//                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
//                        javax.swing.JOptionPane.showMessageDialog(null,
//                            "Exception encountered running script: \n"
//                            + ex.getMessage(),
//                            "C2C RI",
//                            javax.swing.JOptionPane.ERROR_MESSAGE);
                        if (!getStopExecution()){
                        javax.swing.JOptionPane.showMessageDialog(null,
                            getTextInScrollPane("Exception encountered running test script: \n"
                            + ex.getMessage()),
                            "C2C RI",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                        }
                        //ignore this for now.
                    }
                } finally {
                    runEventHandler.endTestRun(Calendar.getInstance());
                    fEventHandler.removeFunctionListener(functionListener);
                    tcEventHandler.removeTestCaseListener(testCaseListener);
                    ddEventHandler.removeDataDrivableListener(dataListener);
                    vEventHandler.removeVerificationListener(verificationListener);
                    runEventHandler.removeTestRunListener(testRunListener);
                    fEventHandler.clearInstance();
                    runEventHandler.clearInstance();
                    tcEventHandler.clearInstance();
                    ddEventHandler.clearInstance();
                    vEventHandler.clearInstance();
                    runEventHandler.clearInstance();
                    setExecuting(false);

                }
            }
        };
        t.setDaemon(false);
        t.start();
        return t;
    }

    /**
     * Gets the text in scroll pane.
     *
     * @param text the text
     * @return the text in scroll pane
     */
    private javax.swing.JScrollPane getTextInScrollPane(String text) {
        // create a JTextArea
        javax.swing.JTextArea textArea = new javax.swing.JTextArea(12, 80);
        textArea.setText(text);
        textArea.setEditable(false);

        // wrap a scrollpane around it
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);

        return scrollPane;
    }


    /**
     * Notify status listeners.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param notification the notification
     */
    @SuppressWarnings("static-access")
    public void notifyStatusListeners(String notification) {
        RITestEngine.statusListener.updateStatus(notification);
    }

    /**
     * Gets the tc results.
     *
     * @return the tc results
     */
    public TestCaseResults getTcResults() {
        return tcResults;
    }

    ///////////////////////////////////////////////////////////////////////////////
    //          TestRunListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * Begin test run.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param event the event
     */
    public void beginTestRun(TestRunEvent event) {
        Calendar startTime = (Calendar) event.getSource();
        notifyStatusListeners("Beginning Test @ " + startTime.getTime());
        lastTagThread = Thread.currentThread();
    }

    /**
     * End test run.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param event the event
     */
    @SuppressWarnings("static-access")
    public void endTestRun(TestRunEvent event) {
        lastTagThread = Thread.currentThread();
        Calendar endTime = (Calendar) event.getSource();
        notifyStatusListeners("Ending Test @ " + endTime.getTime());
        if (!testStepMap.isEmpty()) {
            if (testStepMap.size() == 1) {
                for (FunctionTag ft : testStepMap.keySet()) {
                    tcResults.updateExistingTestStep(
                            tcResults.getLastTestCaseResult(), endTime.getTimeInMillis(),
                            ft.getTestCaseTag().getResults().getErrorMsg(),
                            "Failed", testStepMap.get(ft));
                    ft.getFunctionResults().setFailed();
                }
            }
        }
        testStepMap.clear();
        //     RITestEngine.statusListener.testComplete();
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
        lastTagThread = Thread.currentThread();
        primaryIteration = "";
        if (event.getSource() instanceof RISubscriptTag) {
            return;
        } else if (getStopExecution()) {
            TestCaseTag tct = (TestCaseTag) event.getSource();
            tct.setFailedOnCurrentRow(true);
            tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis() - tct.getResults().getExecutionTime(),
                    "Dont know the layer yet",
                    tcResults.getCurrentRunID(tct.getTestCase().getTestCaseId()),
                    "FAILED", "The User terminated the test case.");

            etcThread1.interrupt();
            return;
        }

        TestCaseTag tct = (TestCaseTag) event.getSource();
        checkForStop(tct);

        // The ID is set from the script text, not the context variable.  We fix that here!!!
        if (tct.getTestCase().getTestCaseId().equals("${C2CRITestCaseID}") || (tct.getTestCase().getTestCaseId().equals(""))) {
            tct.getTestCase().setTestCaseId((String) tct.getContext().getVariable("C2CRITestCaseID"));
        }
        tct.getResults().setTestName((String) tct.getContext().getVariable("C2CRITestCaseID"));

        testCaseForwardListener.beginTestCase(event);

        if (tct.getNeedsVerified() != null) {
            for (String need : tct.getNeedsVerified()) {
                System.out.println("Verified: " + need);
            }
        }
        if (tct.getRequirementsVerified() != null) {
            for (String requirement : tct.getRequirementsVerified()) {
                System.out.println("Verified: " + requirement);
            }
        }

        String extraInfo = "<line>" + tct.getLineNumber() + "</line>\n"
                + "<column>" + tct.getColumnNumber() + "</column>\n"
                + "<file>" + tct.getFileName() + "</file>\n";
        String testCaseLog = tct.getResults().toXML().replace("<test-case>", "<tag src=\"test-case\" type=\"Begin\">\n" + extraInfo);
        testCaseLog = testCaseLog.replace("</test-case>", "</tag>");
        RILogging.logEvent(RITestEngine.class.getName(), RILogging.RI_SCRIPT_EVENT, RILogging.removeElement(RILogging.removeElement(testCaseLog, "functional-point-info"), "children-results"));

        String testCaseDescription = "";
        System.out.println("RITestEngine:: Searching for Test Case " + tct.getTestCase().getTestCaseId());
        if (testConfig.getInfoLayerParams().getTestCases().lh_testCasesMap.containsKey(tct.getTestCase().getTestCaseId())){
            testCaseDescription = ((TestCase)testConfig.getInfoLayerParams().getTestCases().lh_testCasesMap.get(tct.getTestCase().getTestCaseId())).getDescription();
            System.out.println("RITestEngine:: Found Test Case in Info Layer ");
        } else if (testConfig.getAppLayerParams().getTestCases().lh_testCasesMap.containsKey(tct.getTestCase().getTestCaseId())){
            testCaseDescription = ((TestCase)testConfig.getAppLayerParams().getTestCases().lh_testCasesMap.get(tct.getTestCase().getTestCaseId())).getDescription();            
            System.out.println("RITestEngine:: Found Test Case in App Layer ");
        } else if (tct.getTestCase().getTestCaseId().equals("EntityEmulationTester")){
            testCaseDescription = "This test case is used for Entity Emulation Testing.";            
            System.out.println("RITestEngine:: Found The EntityEmulationTester Test Case ");            
        }
        tcResults.addResult(tct.getTestCase().getTestCaseId(), tct.getResults().getDateTimeExecuted().getTimeInMillis(), "None Yet", "Running",testCaseDescription);

        if (this.pauseExecution) {
            tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis() - tct.getResults().getExecutionTime(),
                    "Dont know the layer yet",
                    tcResults.getCurrentRunID(tct.getTestCase().getTestCaseId()),
                    "Paused", "");
            while (this.pauseExecution && (!getStopExecution())) {
                synchronized (this) {
                    try {
                        this.wait(250); // wait 250 ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
						Thread.currentThread().interrupt();
                    }
                }
            }
            tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis() - tct.getResults().getExecutionTime(),
                    "Dont know the layer yet",
                    tcResults.getCurrentRunID(tct.getTestCase().getTestCaseId()),
                    "Running", "");
        }
//        javax.swing.JOptionPane.showMessageDialog(null, "User has Paused the Test", "Test Paused", javax.swing.JOptionPane.WARNING_MESSAGE);
//        if(stopExecution)tct.setExecuteTestCase(false);
    }

    /**
     * End test case.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param event the event
     */
    public void endTestCase(TestCaseEvent event) {
        lastTagThread = Thread.currentThread();
        if (event.getSource() instanceof RISubscriptTag) {
            return;
//        } else if (getStopExecution()){
//            etcThread1.interrupt();
//            return;
        }

        TestCaseTag tct = (TestCaseTag) event.getSource();
        checkForStop(tct);
        notifyStatusListeners(DateUtils.now() + " Test Case " + tct.getName() + "---" + tct.getResults().getOutcome());
        testCaseForwardListener.endTestCase(event);

//        String functionLog = ft.getFunctionResults().toXML().replace("<function-point>", "<function-point tag=\""+ft.getElementName()+"\">\n"+extraInfo);
        String extraInfo = "<line>" + tct.getLineNumber() + "</line>\n"
                + "<column>" + tct.getColumnNumber() + "</column>\n"
                + "<file>" + tct.getFileName() + "</file>\n";
        String testCaseLog = tct.getResults().toXML().replace("<test-case>", "<tag src=\"test-case\" type=\"End\">\n" + extraInfo);
        testCaseLog = testCaseLog.replace("</test-case>", "</tag>");

        RILogging.logEvent(RITestEngine.class.getName(), RILogging.RI_SCRIPT_EVENT, RILogging.removeElement(RILogging.removeElement(testCaseLog, "functional-point-info"), "children-results"));

        if (this.pauseExecution) {
            tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis() - tct.getResults().getExecutionTime(),
                    "Dont know the layer yet",
                    tcResults.getCurrentRunID(tct.getTestCase().getTestCaseId()),
                    "Paused", "");
            while (this.pauseExecution && (!getStopExecution())) {
                synchronized (this) {
                    try {
                        this.wait(250); // wait 250 ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
						Thread.currentThread().interrupt();
                    }
                }
            }
            tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis(),
                    tct.getResults().getDateTimeExecuted().getTimeInMillis() - tct.getResults().getExecutionTime(),
                    "Dont know the layer yet",
                    tcResults.getCurrentRunID(tct.getTestCase().getTestCaseId()),
                    "Running", "");

            //           javax.swing.JOptionPane.showMessageDialog(null, "User has Paused the Test", "Test Paused", javax.swing.JOptionPane.WARNING_MESSAGE);
        }

        String tcErrorResults = "";
        List tcFailedResults = tct.getResults().getFailedResults();
        for (Object failure : tcFailedResults) {
            if (failure instanceof FunctionResult) {
                FunctionResult theFailure = (FunctionResult) failure;
                if (theFailure.getErrorMsg() != null) {
                    tcErrorResults = tcErrorResults.concat(theFailure.getErrorMsg() + "\n");
                }
            }
        }

//        tcResults.addResult(tct.getTestCase().getTestCaseId(), tct.getResults().getDateTimeExecuted().getTimeInMillis(), tcErrorResults, tct.getResults().passed() ? "PASSED" : "FAILED");
        tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                tct.getResults().getDateTimeExecuted().getTimeInMillis(),
                tct.getResults().getDateTimeExecuted().getTimeInMillis() - tct.getResults().getExecutionTime(),
                "Dont know the layer yet",
                tcResults.getCurrentRunID(tct.getTestCase().getTestCaseId()),
                tct.getResults().passed() ? "PASSED" : "FAILED", tcErrorResults);
        boolean logVerificationResults = false;
        RIVerificationResults theResults = new RIVerificationResults();
        theResults.setTestCase(tct.getTestCase().getName());

        if (tct.getNeedsVerified() != null) {
            for (String need : tct.getNeedsVerified()) {
                logVerificationResults = true;
                RIVerification thisVerification = new RIVerification();
                thisVerification.setId(need);
                thisVerification.setType(RIVerification.VERIFICATION_NEED_TYPE);
                if (tct.getResults().passed()) {
                    thisVerification.setResult(RIVerification.VERIFICATION_PASS_RESULT);
                } else {
                    thisVerification.setResult(RIVerification.VERIFICATION_FAIL_RESULT);
                    String errorResults = "";
                    List failedResults = tct.getResults().getFailedResults();
                    for (Object failure : failedResults) {
                        if (failure instanceof FunctionResult) {
                            FunctionResult theFailure = (FunctionResult) failure;
                            if (theFailure.getErrorMsg() != null) {
                                errorResults = errorResults.concat(theFailure.getErrorMsg() + "\n");
                            }
                        }
                    }
                    thisVerification.setErrorDescription(errorResults);
                }
                theResults.addVerification(thisVerification);
            }
        }
        if (tct.getRequirementsVerified() != null) {
            for (String requirement : tct.getRequirementsVerified()) {
                logVerificationResults = true;
                RIVerification thisVerification = new RIVerification();
                thisVerification.setId(requirement);
                thisVerification.setType(RIVerification.VERIFICATION_REQUIREMENT_TYPE);
                if (tct.getResults().passed()) {
                    thisVerification.setResult(RIVerification.VERIFICATION_PASS_RESULT);
                } else {
                    thisVerification.setResult(RIVerification.VERIFICATION_FAIL_RESULT);
                    String errorResults = "";
                    List failedResults = tct.getResults().getFailedResults();
                    for (Object failure : failedResults) {
                        if (failure instanceof FunctionResult) {
                            FunctionResult theFailure = (FunctionResult) failure;
                            if (theFailure.getErrorMsg() != null) {
                                errorResults = errorResults.concat(theFailure.getErrorMsg() + "\n");
                            }
                        }
                    }
                    thisVerification.setErrorDescription(errorResults);
                }
                theResults.addVerification(thisVerification);
            }
        }
        StateStorer.getInstance().reset();
        if (logVerificationResults) {
            RILogging.logEvent(RITestEngine.class.getName(), RILogging.RI_VERIFICATION_EVENT, theResults.to_LogFormat());
        }

    }

    ///////////////////////////////////////////////////////////////////////////////
    //          FunctionListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * Begin function.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param event the event
     * @param rowNum the row num
     */
    public void beginFunction(FunctionEvent event, int rowNum) {
        lastTagThread = Thread.currentThread();
        FunctionTag ft = (FunctionTag) event.getSource();
        if (event.getSource() instanceof TestStepTag) {
            if (getStopExecution()) {
                t.interrupt();
                etcThread1.interrupt();
                ((TestStepTag) event.getSource()).fail("The User Terminated the Test.");
                return;
            }
        }

        String iteration = "";
        if (ft != null) {
            checkForStop(ft.getTestCaseTag());
            // If this is the start of a test step indicate that the test step contains no failures
            if (((ft.getFunctionResults().isTestStep()) && (ft.getElementName().equals("testStep")))) {

                try{           
                    if (ft.getTestCaseTag().getDataDrivableRowResult().getRowNum() > 1){
                        iteration = "("+Integer.toString(ft.getTestCaseTag().getDdResult().getRowNum())+") ";
                    }  else if (ft.getCurrentRow() > 1){                        
                        iteration = "("+Integer.toString(ft.getCurrentRow())+") ";                        
                    }
                    iteration = primaryIteration +iteration;
                    System.out.println("RITestEngine::beginFunction TestIterationRow = "+ft.getTestCaseTag().getDataDrivableRowResult().getRowNum() + " or "+ft.getCurrentRow()+" for test case Tag Type with name "+ft.getTestCaseTag().getName());
                } catch (Exception ex){
                    // If we can't convert the value to string just leave the original iteration value.
                }
                
                int identifier = tcResults.addNewTestStep(tcResults.getLastTestCaseResult(),
                        iteration + ft.getFunctionId(),
                        ft.getFunctionResults().getDateTimeExecuted().getTimeInMillis() - ft.getFunctionResults().getExecutionTime(),
                        ft.getFunctionResults().getDateTimeExecuted().getTimeInMillis(),
                        "",
                        "Running");
                testStepMap.put(ft, identifier);
                testStepFailure = false;
            } else if ((ft.getElementName().equals("ri-execute-script"))) {
                    System.out.println("RITestEngine::beginFunction TestIterationRow = "+ft.getCurrentRow() + " for test case Tag Type with name "+ft.getTestCaseTag().getName()+" (not TestStep) and element name "+ft.getElementName()+".");                
                    if (ft.getCurrentRow() > 1){
                        primaryIteration = "("+Integer.toString(ft.getCurrentRow())+") ";
                    } 
                    System.out.println("RITestEngine::beginFunction TestIterationRow = "+ft.getCurrentRow() + " for test case Tag Type with name "+ft.getTestCaseTag().getName()+" (not TestStep).");
            }


            String extraInfo = "<line>" + ft.getLineNumber() + "</line>\n"
                    + "<column>" + ft.getColumnNumber() + "</column>\n"
                    + "<file>" + ft.getFileName() + "</file>\n";

            String functionLog = ft.getFunctionResults().toXML().replace("<function-point>", "<tag src=\"function-point\" tag=\"" + ft.getElementName() + "\" type=\"Begin\">\n" + extraInfo);
            functionLog = functionLog.replace("</function-point>", "</tag>");
            functionLog = functionLog.replace("<functionId>", "<functionId><![CDATA["+iteration);
            functionLog = functionLog.replace("</functionId>", "]]></functionId>");
            functionLog = functionLog.replace("<function-id>", "<function-id><![CDATA[");
            functionLog = functionLog.replace("</function-id>", "]]></function-id>");
            functionLog = functionLog.replace("<attribute-value>", "<attribute-value><![CDATA[");
            functionLog = functionLog.replace("</attribute-value>", "]]></attribute-value>");
            RILogging.logEvent(RITestEngine.class.getName(), RILogging.RI_SCRIPT_EVENT, RILogging.removeElement(functionLog, "functional-point-info"));

            if (this.pauseExecution) {
                tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                        ft.getTestCaseTag().getResults().getDateTimeExecuted().getTimeInMillis(),
                        ft.getTestCaseTag().getResults().getDateTimeExecuted().getTimeInMillis() - ft.getTestCaseTag().getResults().getExecutionTime(),
                        "Dont know the layer yet",
                        tcResults.getCurrentRunID(ft.getTestCaseTag().getTestCase().getTestCaseId()),
                        "Paused", "");
                while (this.pauseExecution && (!getStopExecution())) {
                    synchronized (this) {
                        try {
                            this.wait(250); // wait 250 ms
                        } catch (InterruptedException e) {
                            e.printStackTrace();
							Thread.currentThread().interrupt();
                        }
                    }
                }
                tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                        ft.getTestCaseTag().getResults().getDateTimeExecuted().getTimeInMillis(),
                        ft.getTestCaseTag().getResults().getDateTimeExecuted().getTimeInMillis() - ft.getTestCaseTag().getResults().getExecutionTime(),
                        "Dont know the layer yet",
                        tcResults.getCurrentRunID(ft.getTestCaseTag().getTestCase().getTestCaseId()),
                        "Running", "");

//                javax.swing.JOptionPane.showMessageDialog(null, "User has Paused the Test", "Test Paused", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
            //          if (stopExecution)ft.getTestCaseTag().setExecuteTestCase(false);
        }
    }

    /**
     * End function.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param event the event
     * @param rowNum the row num
     */
    public void endFunction(FunctionEvent event, int rowNum) {
        lastTagThread = Thread.currentThread();
        FunctionTag ft = (FunctionTag) event.getSource();
        checkForStop(ft.getTestCaseTag());

        String extraInfo = "<line>" + ft.getLineNumber() + "</line>\n"
                + "<column>" + ft.getColumnNumber() + "</column>\n"
                + "<file>" + ft.getFileName() + "</file>\n";
        if (!ft.getFunctionResults().passed()) {

            // Append the rendered message. Also make sure to escape any
            // existing CDATA sections.
            StringBuffer buf = new StringBuffer();

            Transform.appendEscapingCDATA(buf, ft.getFunctionResults().getErrorMsg());

            if (((ft.getFunctionResults().isTestStep()) && (ft.getElementName().equals("testStep")))) {
                List failedResults = ft.getFunctionResults().getParentResults().getFailedResults();
                String errorResults = "";
                for (Object failure : failedResults) {
                    if (failure instanceof FunctionResult) {
                        FunctionResult theFailure = (FunctionResult) failure;
                        if (theFailure.getErrorMsg() != null) {
                            // We only want to show the last error which occured under this step.
                            errorResults = theFailure.getErrorMsg();
                        }
                    }
                }
                Transform.appendEscapingCDATA(buf, errorResults);
            }

            extraInfo = extraInfo.concat("<error><![CDATA[" + buf.toString() + "]]></error>\n");
        }

        String functionLog = ft.getFunctionResults().toXML().replace("<function-point>", "<tag src=\"function-point\" tag=\"" + ft.getElementName() + "\" type=\"End\">\n" + extraInfo);
        functionLog = functionLog.replace("</function-point>", "</tag>");
        functionLog = functionLog.replace("<functionId>", "<functionId><![CDATA[");
        functionLog = functionLog.replace("</functionId>", "]]></functionId>");
        functionLog = functionLog.replace("<function-id>", "<function-id><![CDATA[");
        functionLog = functionLog.replace("</function-id>", "]]></function-id>");
        functionLog = functionLog.replace("<attribute-value>", "<attribute-value><![CDATA[");
        functionLog = functionLog.replace("</attribute-value>", "]]></attribute-value>");
        try {
            RILogging.logEvent(RITestEngine.class.getName(), RILogging.RI_SCRIPT_EVENT, RILogging.removeElement(functionLog, "functional-point-info"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (this.pauseExecution) {
            tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                    ft.getTestCaseTag().getResults().getDateTimeExecuted().getTimeInMillis(),
                    ft.getTestCaseTag().getResults().getDateTimeExecuted().getTimeInMillis() - ft.getTestCaseTag().getResults().getExecutionTime(),
                    "Dont know the layer yet",
                    tcResults.getCurrentRunID(ft.getTestCaseTag().getTestCase().getTestCaseId()),
                    "Paused", "");
            while (this.pauseExecution && (!getStopExecution())) {
                synchronized (this) {
                    try {
                        this.wait(250); // wait 250 ms
                    } catch (InterruptedException e) {
                        e.printStackTrace();
						Thread.currentThread().interrupt();
                    }
                }
            }
            tcResults.updateTestCaseResult(tcResults.getLastTestCaseResult(),
                    ft.getTestCaseTag().getResults().getDateTimeExecuted().getTimeInMillis(),
                    ft.getTestCaseTag().getResults().getDateTimeExecuted().getTimeInMillis() - ft.getTestCaseTag().getResults().getExecutionTime(),
                    "Dont know the layer yet",
                    tcResults.getCurrentRunID(ft.getTestCaseTag().getTestCase().getTestCaseId()),
                    "Running", "");

//            javax.swing.JOptionPane.showMessageDialog(null, "User has Paused the Test", "Test Paused", javax.swing.JOptionPane.WARNING_MESSAGE);
        }
        if ((ft.getFunctionResults().isPostcondition()) && (!ft.getFunctionResults().passed())) {
            testStepFailure = true;
        }

        // Some sort of error occurred in a function.
        if (!(ft.getFunctionResults().passed())) {
            testStepFailure = true;
        }

        if (((ft.getFunctionResults().isTestStep()) && (ft.getElementName().equals("testStep")))) {
            String results = "";
            if (event.getSource() instanceof TestStepTag) {
                TestStepTag tst = (TestStepTag) event.getSource();
                if (tst.isPassFailResult()) {
                    results = ft.getFunctionResults().passed() ? "PASSED" : "FAILED";
                    //Make sure and mark the test step as failed if any function inside failed.
                    if (testStepFailure) {
                        ft.getFunctionResults().setFailed();
                    }
                } else {
                    results = "NA";
                }
            }


            List failedResults = ft.getFunctionResults().getParentResults().getFailedResults();
            String errorResults = "";
            for (Object failure : failedResults) {
                if (failure instanceof FunctionResult) {
                    FunctionResult theFailure = (FunctionResult) failure;
                    if (theFailure.getErrorMsg() != null) {
                    // We only want to show the last error which occured under this step.
                        errorResults = theFailure.getErrorMsg();
//                        errorResults = errorResults.concat(theFailure.getErrorMsg() + "\n");
                    }
                }
            }

            if (testStepMap.containsKey(ft)) {
                tcResults.updateExistingTestStep(
                        tcResults.getLastTestCaseResult(), ft.getFunctionResults().getDateTimeExecuted().getTimeInMillis(),
                        errorResults,
                        results, testStepMap.get(ft));
                testStepMap.remove(ft);
            }
//            tcResults.addNewTestStep(tcResults.getLastTestCaseResult(),
//                    iteration + ft.getFunctionId(),
//                    ft.getFunctionResults().getDateTimeExecuted().getTimeInMillis() - ft.getFunctionResults().getExecutionTime(),
//                    ft.getFunctionResults().getDateTimeExecuted().getTimeInMillis(),
//                    errorResults,
//                    results);
            testStepFailure = false;
        }

        if (event.getSource() instanceof TestStepTag) {
            TestStepTag tst = (TestStepTag) event.getSource();

            if (getStopExecution()) {
                t.interrupt();
                etcThread1.interrupt();
                tst.fail("The User Terminated the Test during the test step.");
            }

            boolean logVerificationResults = false;
            RIVerificationResults theResults = new RIVerificationResults();
            theResults.setTestCase(ft.getTestCaseTag().getTestCase().getName());
            theResults.setTestStep(tst.getFunctionId());

            if (tst.getNeedsVerified() != null) {
                for (String need : tst.getNeedsVerified()) {
                    logVerificationResults = true;
                    RIVerification thisVerification = new RIVerification();
                    thisVerification.setId(need);
                    thisVerification.setType(RIVerification.VERIFICATION_NEED_TYPE);
                    if (tst.getFunctionResults().passed()) {
                        thisVerification.setResult(RIVerification.VERIFICATION_PASS_RESULT);
                    } else {
                        thisVerification.setResult(RIVerification.VERIFICATION_FAIL_RESULT);
                        String errorResults = "";
                        List failedResults = tst.getFunctionResults().getParentResults().getFailedResults();
                        for (Object failure : failedResults) {
                            if (failure instanceof FunctionResult) {
                                FunctionResult theFailure = (FunctionResult) failure;
                                if (theFailure.getErrorMsg() != null) {
                                     // We only want to show the last error which occured under this step.
                                      errorResults = theFailure.getErrorMsg();
//                                    errorResults = errorResults.concat(theFailure.getErrorMsg() + "\n");
                                }
                            }
                        }
                        thisVerification.setErrorDescription(errorResults);
                    }
                    theResults.addVerification(thisVerification);
                }
            }
            if (tst.getRequirementsVerified() != null) {
                for (String requirement : tst.getRequirementsVerified()) {
                    logVerificationResults = true;
                    RIVerification thisVerification = new RIVerification();
                    thisVerification.setId(requirement);
                    thisVerification.setType(RIVerification.VERIFICATION_REQUIREMENT_TYPE);
                    if (tst.getFunctionResults().passed()) {
                        thisVerification.setResult(RIVerification.VERIFICATION_PASS_RESULT);
                    } else {
                        thisVerification.setResult(RIVerification.VERIFICATION_FAIL_RESULT);
                        String errorResults = "";
                        List failedResults = tst.getFunctionResults().getParentResults().getFailedResults();
                        for (Object failure : failedResults) {
                            if (failure instanceof FunctionResult) {
                                FunctionResult theFailure = (FunctionResult) failure;
                                if (theFailure.getErrorMsg() != null) {
                                    // We only want to show the last error which occured under this step.
                                    errorResults = theFailure.getErrorMsg();
//                                    errorResults = errorResults.concat(theFailure.getErrorMsg() + "\n");
                                }
                            }
                        }
                        thisVerification.setErrorDescription(errorResults);
                    }
                    theResults.addVerification(thisVerification);
                }
            }

            if (logVerificationResults) {
                RILogging.logEvent(RITestEngine.class.getName(), RILogging.RI_VERIFICATION_EVENT, theResults.to_LogFormat());
            }
        }

        //   if (stopExecution)ft.getTestCaseTag().setExecuteTestCase(false);
    }

    ///////////////////////////////////////////////////////////////////////////////
    //          DataDrivableListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * Gets called before the open method of a DataDrivable.
     *
     * @param event - a DataDrivableEvent Object
     */
    public void openEvent(DataDrivableEvent event) {
        //Not currently supported
    }

    /**
     * Gets called before the close method of a DataDrivable.
     *
     * @param event - a DataDrivableEvent Object
     */
    public void closeEvent(DataDrivableEvent event) {
        //Not currently supported
    }

    /**
     * Gets called before the executeDrivableRow.
     *
     * @param event - a DataDrivableEvent Object
     * @param rowNum - the current row number being executed from the data
     * source.
     */
    public void executeRowEvent(DataDrivableEvent event, int rowNum) {
        DataDrivable dd = (DataDrivable) event.getSource();

    }

    ///////////////////////////////////////////////////////////////////////////////
    //          VerificationListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * Verification update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param event the event
     */
    @Override
    public void verificationUpdate(VerificationEvent event) {
    }
    

    /**
     * Provides a map of selected Needs/Requirement IDs.
     * @return 
     */
    public ArrayList<RINRTMSelection> getNRTMSelections() {
         ArrayList<RINRTMSelection> nrtmSelections = new ArrayList();
         
         for (Need thisNeed : this.testConfig.getInfoLayerParams().getNrtm().getUserNeeds().needs) {
            if (thisNeed.getFlagValue()) {  // This need is selected
                for (Requirement req : this.testConfig.getInfoLayerParams().getNrtm().getNeedRelatedRequirements(thisNeed.getTitle())){
                    if (req.getFlagValue()){
                        nrtmSelections.add(new RINRTMSelection(thisNeed.getOfficialID(), req.getOfficialID()));                            
                    }
                 }
            }
         }
         System.out.println("RITestEngine::NRTM Selections Map Size = "+nrtmSelections.size());
         return nrtmSelections;
    }
    
}
