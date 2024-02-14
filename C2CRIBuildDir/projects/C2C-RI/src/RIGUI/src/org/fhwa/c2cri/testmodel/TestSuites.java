/**
 *
 */
package org.fhwa.c2cri.testmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.fhwa.c2cri.utilities.RISignedJarVerifier;

/**
 * This class establishes and maintains the Test Suites that are available to
 * the RI.
 *
 * It also implements JasperReports API interface to enable the reporting of
 * Test Suite information (scripts).
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestSuites implements Runnable {

    /**
     * The test suites list.
     */
    private ArrayList<TestSuite> testSuitesList;

    /**
     * The list of available Test Suites.
     */
    private ArrayList<String> availableSuites;

    /**
     * The available info layer suites.
     */
    private ArrayList<String> availableInfoLayerSuites;

    /**
     * The available app layer suites.
     */
    private ArrayList<String> availableAppLayerSuites;

    /**
     * The test suites.
     */
    private static TestSuites testSuites;

    /**
     * The test suites service state.
     */
    private TESTSUITESSERVICE_STATE testSuitesServiceState = TESTSUITESSERVICE_STATE.INITIAL;

    /**
     * The Enum TESTSUITESSERVICE_STATE.
     */
    public static enum TESTSUITESSERVICE_STATE {
        /**
         * The initial.
         */
        INITIAL, /**
         * The initializing.
         */
        INITIALIZING, /**
         * The ready.
         */
        READY
    };

    /**
     * Gets the single instance of TestSuites.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return single instance of TestSuites
     */
    public synchronized static TestSuites getInstance() {
        if (testSuites == null) {
            testSuites = new TestSuites();
        }
        return testSuites;
    }

    /**
     * Constructor operation for Test Suites. It first searches for the Test
     * Suites distributed with the RI. It performs a validity check on the Test
     * Suites and adds them to it's list of Test Suites if valid.
     *
     * It then searches for user provided Test Suites (using an RI Parameter for
     * the location). Any valid user provided Test Suites found are stored in
     * the list with a special designation for user provided.
     *
     */
    private TestSuites() {
        initTestSuites();
    }

    /**
     * Inits the test suites.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private void initTestSuites() {
        if (testSuitesServiceState.equals(TESTSUITESSERVICE_STATE.INITIAL)) {
            testSuitesServiceState = TESTSUITESSERVICE_STATE.INITIALIZING;
            testSuitesList = new ArrayList<TestSuite>();
            availableSuites = new ArrayList<String>();
            availableAppLayerSuites = new ArrayList<String>();
            availableInfoLayerSuites = new ArrayList<String>();

            File testSuiteFolder = new File(".\\TestSuites");
            System.out.println("Looking at " + testSuiteFolder.getPath());
            FilenameFilter jarFilter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
//		    	return true;
                }
            };

            File[] listOfTestSuites = testSuiteFolder.listFiles(jarFilter);
            List<URL> jarList = new ArrayList();

            if (listOfTestSuites == null) {
                // Either dir does not exist or is not a directory
                System.out.println("No Pre-defined Test Suite files found.");
            } else {
                try {
                    java.security.KeyStore riKeyStore = java.security.KeyStore.getInstance("JKS");
                    String keyStoreFileName = testSuiteFolder + "\\c2cri.jks";
                    FileInputStream fIn = new FileInputStream(keyStoreFileName);
                    riKeyStore.load(fIn, null);

                    for (int i = 0; i < listOfTestSuites.length; i++) {
                        // Get filename of file or directory
                        System.out.println(listOfTestSuites[i].getName());

                        // The RI works only with Authorized Signed Test Suite Jar Files
                        try {
                            JarFile currentJar = new JarFile(listOfTestSuites[i]);
                            RISignedJarVerifier.verify(currentJar, riKeyStore);
                            jarList.add(listOfTestSuites[i].toURI().toURL());

                        } catch (Exception ex) {
                            System.out.println("Unable to Validate the " + listOfTestSuites[i] + " File --> " + ex.getMessage());
                            //                   ex.printStackTrace();
                        }

                    }

                } catch (Exception e) {
                    System.out.println("Unable to Access the RI KeyStore File --> " + e.getMessage());

                }

                for (int i = 0; i < jarList.size(); i++) {
                    System.out.println("Jar File => " + jarList.get(i).getFile());
                }

                URL[] jars = (URL[]) jarList.toArray(new URL[0]);
                URLClassLoader loader = new URLClassLoader(jars);
                Thread.currentThread().setContextClassLoader(loader);

                try {
                    Enumeration suiteSpecs = loader.findResources("SuiteSpec.properties");
                    System.out.println("SuiteSpec Instances found in loaded Jar Files? = " + suiteSpecs.hasMoreElements());
                    while (suiteSpecs.hasMoreElements()) {
                        URL thisOne = (URL) suiteSpecs.nextElement();
                        System.out.println("thisOne : " + thisOne.getFile() + " @ " + thisOne.getPath());

                        Properties props = new Properties();

                        try {
							try (InputStream oIn = thisOne.openStream())
							{
								props.load(oIn);
							}
                            System.out.println("thisOne content: " + props.toString());
                            TestSuite testSuiteNew = new TestSuite();
                            testSuiteNew.testSuiteName = props.getProperty("SuiteName");
                            testSuiteNew.description = props.getProperty("SuiteDescription");
                            testSuiteNew.infoLayerStandard = props.getProperty("InfoLayerStandard");
                            testSuiteNew.appLayerStandard = props.getProperty("AppLayerStandard");
                            testSuiteNew.preDefinedTestSuite = true;
                            testSuiteNew.testSuitePath = "jar:" + thisOne.getPath().replace("/SuiteSpec.properties", "");
                            testSuiteNew.baselineTestSuiteName = props.getProperty("BaselineTestSuiteName");
                            testSuiteNew.baselineTestSuiteVersion = props.getProperty("BaselineTestSuiteVersion");
                            testSuiteNew.testSuiteType = props.getProperty("TestSuiteType");
                            testSuitesList.add(testSuiteNew);
                            availableSuites.add(testSuiteNew.testSuiteName);
                            if (testSuiteNew.testSuiteType.equals("Application")) {
                                availableAppLayerSuites.add(testSuiteNew.testSuiteName);
                            } else {
                                availableInfoLayerSuites.add(testSuiteNew.testSuiteName);
                            }
                        } catch (IOException e) {
                            System.err.println(thisOne.getFile() + " not found. Using default settings.");
                        } catch (NullPointerException npe) {
                            System.err.println(npe.getMessage());
                            //this is just an annoying message
                        }

                    }
                } catch (Exception e) {
                    System.err.println("Error Loading Properties from Test Suites");
                }

            }

            File customTestSuiteFolder = new File(".\\CustomTestSuites");
            System.out.println("Looking at " + customTestSuiteFolder.getPath());
            FilenameFilter dirFilter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return dir.isDirectory() && (!name.endsWith(".jar"));
                }
            };

            File[] listOfCustomTestSuites = customTestSuiteFolder.listFiles(dirFilter);
            if (listOfCustomTestSuites == null) {
                // Either dir does not exist or is not a directory
                System.out.println("No Custom Test Suite files found.");
            } else {
                for (int i = 0; i < listOfCustomTestSuites.length; i++) {
                    // Get filename of file or directory
                    System.out.println(listOfCustomTestSuites[i].getName() + " @ path =>" + listOfCustomTestSuites[i].getPath());

                    File currentSpec = new File(listOfCustomTestSuites[i].getPath() + File.separator + "SuiteSpec.properties");

                    Properties props = new Properties();

                    try {
                        URL thisOne = currentSpec.toURI().toURL();
                        System.out.println("Custom thisOne : " + thisOne.getFile() + " @ " + thisOne.getPath());
						
						try (InputStream oIn = thisOne.openStream())
						{
							props.load(oIn);
						}
                        System.out.println("Custom thisOne content: " + props.toString());
                        TestSuite testSuiteNew = new TestSuite();
                        testSuiteNew.testSuiteName = "*" + props.getProperty("SuiteName");
                        testSuiteNew.description = props.getProperty("SuiteDescription");
                        testSuiteNew.infoLayerStandard = props.getProperty("InfoLayerStandard");
                        testSuiteNew.appLayerStandard = props.getProperty("AppLayerStandard");
                        testSuiteNew.preDefinedTestSuite = false;
                        testSuiteNew.baselineTestSuiteName = props.getProperty("BaselineTestSuiteName");
                        testSuiteNew.baselineTestSuiteVersion = props.getProperty("BaselineTestSuiteVersion");
                        testSuiteNew.testSuiteType = props.getProperty("TestSuiteType");
                        File parentPath = new File(listOfCustomTestSuites[i].getPath());
                        testSuiteNew.testSuitePath = parentPath.toURI().toURL().toString();

                        int ii = 2;
                        while (availableSuites.contains(testSuiteNew.testSuiteName + " Version" + testSuiteNew.testSuiteVersion)) {
                            testSuiteNew.testSuiteName = testSuiteNew.testSuiteName + " Version" + testSuiteNew.testSuiteVersion + " [Duplicate]" + ii;
                            ii++;
                        }
                        testSuitesList.add(testSuiteNew);
                        availableSuites.add(testSuiteNew.testSuiteName);
                        if (testSuiteNew.testSuiteType.equals("Application")) {
                            availableAppLayerSuites.add(testSuiteNew.testSuiteName);
                        } else {
                            availableInfoLayerSuites.add(testSuiteNew.testSuiteName);
                        }

                    } catch (IOException e) {
                        System.err.println("SuiteSpec.properties not found. Using default settings.");
                    } catch (NullPointerException npe) {
                        System.err.println(npe.getMessage());
                        npe.printStackTrace();
                        //this is just an annoying message
                    }

                }
            }
            testSuitesServiceState = TESTSUITESSERVICE_STATE.READY;
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        testSuitesServiceState = TESTSUITESSERVICE_STATE.INITIALIZING;
        initTestSuites();
        testSuitesServiceState = TESTSUITESSERVICE_STATE.READY;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public TESTSUITESSERVICE_STATE getStatus() {
        return testSuitesServiceState;
    }

    /**
     * Performs a validity check on a Test Suite. For Test Suites at the
     * distribution location an additional check will be performed to ensure the
     * configuration is properly signed/encoded.
     *
     * @param testSuite the test suite
     * @return true, if successful
     */
    private boolean verifyTestSuite(String testSuite) {
        return true;
    }

    /**
     * returns the list of test suites that are available to the RI application.
     *
     * @return the available suites
     */
    public ArrayList<String> getAvailableSuites() {
        return this.availableSuites;
    }

    /**
     * returns the list of Application Layer test suites that are available to
     * the RI application.
     *
     * @return the available app layer suites
     */
    public ArrayList<String> getAvailableAppLayerSuites() {
        return this.availableAppLayerSuites;
    }

    /**
     * returns the list of Information Layer test suites that are available to
     * the RI application.
     *
     * @return the available info layer suites
     */
    public ArrayList<String> getAvailableInfoLayerSuites() {
        return this.availableInfoLayerSuites;
    }

    /**
     * returns the application layer standard associated with the test suite.
     *
     * @param theSuite the the suite
     * @return the app layer standard
     */
    public String getAppLayerStandard(String theSuite) {
        String results = "";
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                results = tempSuite.appLayerStandard;
            }
        }

        return results;
    }

    /**
     * returns the Test Suite Description associated with the test suite.
     *
     * @param theSuite the the suite
     * @return the description
     */
    public String getDescription(String theSuite) {
        String results = "";
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                results = tempSuite.description;
            }
        }

        return results;
    }

    /**
     * returns the information layer standard associated with the test suite.
     *
     * @param theSuite the the suite
     * @return the info layer standard
     */
    public String getInfoLayerStandard(String theSuite) {
        String results = "";
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                results = tempSuite.infoLayerStandard;
            }
        }

        return results;
    }

    /**
     * returns the information layer standard associated with the test suite.
     *
     * @param theSuite the the suite
     * @return the baseline test suite
     */
    public String getBaselineTestSuite(String theSuite) {
        String results = "";
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                results = tempSuite.baselineTestSuiteName;
            }
        }

        return results;
    }

    /**
     * returns the directory path associated with the test suite.
     *
     * @param theSuite the the suite
     * @return the test suite path
     */
    public String getTestSuitePath(String theSuite) {
        String results = "";
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                results = tempSuite.testSuitePath;
            }
        }

        return results;
    }

    /**
     * returns a flag indicating whether the test suite is predefined.
     *
     * @param theSuite the the suite
     * @return true, if is predefined
     */
    public boolean isPredefined(String theSuite) {
        boolean predefinedFlag = false;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                predefinedFlag = tempSuite.preDefinedTestSuite;
            }
        }

        return predefinedFlag;
    }

    /**
     * returns the URL associated with the test suites Needs CSV File.
     *
     * @param theSuite the the suite
     * @return the test suite needs url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteNeedsURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/Needs.csv");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/Needs.csv");
                }
            }
        }

        return results;
    }

    /**
     * returns the URL associated with the test suites Requirements CSV File.
     *
     * @param theSuite the the suite
     * @return the test suite requirements url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteRequirementsURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/Requirements.csv");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/Requirements.csv");
                }
            }
        }

        return results;
    }

    /**
     * returns the URL associated with the test suites TestCases CSV File.
     *
     * @param theSuite the the suite
     * @return the test suite test cases url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteTestCasesURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/TestCases.csv");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/TestCases.csv");
                }
            }
        }

        return results;
    }

    /**
     * returns the URL associated with the test suites TestCases CSV File.
     *
     * @param theSuite the the suite
     * @return the test suite test case path url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteTestCasePathURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer");
                }
            }
        }

        return results;
    }

    /**
     * returns the URL associated with the test suites TestCaseMatrix CSV File.
     *
     * @param theSuite the the suite
     * @return the test suite test case matrix path url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteTestCaseMatrixPathURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/TestCaseMatrix.csv");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/TestCaseMatrix.csv");
                }
            }
        }

        return results;
    }

    /**
     * returns the URL associated with the test suites NRTM CSV File.
     *
     * @param theSuite the the suite
     * @return the test suite nrtmurl
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteNRTMURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {

                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/NRTM.csv");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/NRTM.csv");
                }
            }
        }

        return results;
    }

    /**
     * returns the String representation of the URL associated with the test
     * suites Scripts Path.
     *
     * @param theSuite the the suite
     * @return the test suite test script paths string
     * @throws Exception the exception
     */
    public String getTestSuiteTestScriptPathsString(String theSuite) throws Exception {
        String results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {

                if (tempSuite.testSuiteType.equals("Application")) {
                    results = tempSuite.testSuitePath + "/AppLayer/Scripts/";
                } else {
                    results = tempSuite.testSuitePath + "/InfoLayer/Scripts/";
                }
            }
        }
        if (results == null) {
            throw new Exception("The required test suite " + theSuite + " is not available.");
        }
        return results;
    }

    /**
     * returns the String representation of the URL associated with the test
     * suites Data Path.
     *
     * @param theSuite the the suite
     * @return the test suite test data pathas string
     */
    public String getTestSuiteTestDataPathasString(String theSuite) {
        String results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {

                if (tempSuite.testSuiteType.equals("Application")) {
                    results = tempSuite.testSuitePath + "/AppLayer/Data/";
                } else {
                    results = tempSuite.testSuitePath + "/InfoLayer/Data/";
                }
            }
        }

        return results;
    }

    /**
     * returns the String representation of the URL associated with the test
     * suites Emulation Data Path.
     *
     * @param theSuite the the suite
     * @return the test suite test emulation data path as string
     */
    public URL[] getTestSuiteEmulationDataURLs(String theSuite) {
        URL[] results = null;
        String dataPath = null;
        ArrayList<String> testSuitePathList = new ArrayList<String>();

        try {
            TestSuite tempSuite = getTestSuiteByName(theSuite);
            if (tempSuite.testSuiteType.equals("Application")) {
                dataPath = "AppLayer/EmulationData/";
            } else {
                dataPath = "InfoLayer/EmulationData/";
            }
            // If the test suite is an extension to a test suite.  Add the parent to the test suite list first.
            // This will allow the extension standard to replace base emulation data if necessary.
            if (!tempSuite.isPreDefinedTestSuite()) {
                try {
                    testSuitePathList.add(getTestSuiteByName(tempSuite.getBaselineTestSuiteName()).getTestSuitePath());
                } catch (Exception ex) {
                    // Intentionally do nothing here.
                }
            }
            testSuitePathList.add(tempSuite.getTestSuitePath());

            final String path = dataPath;
            try {
                ArrayList<URL> urlList = new ArrayList();
                for (String testSuitePath : testSuitePathList) {
                    final URL url = new URL(testSuitePath.replaceFirst("jar:", "").replace("!", ""));  // Temporarily replace the jar: from the beginning and the "!" from the end.
                    final File jarFile = new File(url.toURI());
                    try {
                        // Assume the file is a jar file
                        try (final JarFile jar = new JarFile(jarFile))
						{
							final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
							while (entries.hasMoreElements()) {
								final JarEntry entry = entries.nextElement();
								if (entry.getName().startsWith(path) && !entry.isDirectory()) { //filter according to the path and files only            
									urlList.add(new URL(testSuitePath + "/" + entry.getName()));   // Add the required / back to the path.
									System.out.println("Entity Emulation File " + entry.getName());
								}
							}
						}
                    } catch (Exception ex) {
                        // Run with IDE
                        if (url != null) {
                            try {
                                final File apps = new File(new URL(testSuitePath + dataPath).toURI());
                                if (apps != null) {
                                    for (File app : apps.listFiles()) {
                                        if (!app.isDirectory()) { //filter according to files only            
                                            urlList.add(new URL(testSuitePath + dataPath + "/" + app.getName()));
                                            System.out.println(app);
                                        }
                                    }
                                }
                            } catch (URISyntaxException ex2) {
                                // never happens
                            }
                        }

                    }

                }
                results = new URL[urlList.size()];
                urlList.toArray(results);
                
            } catch (Exception ex) {
                System.out.println("TestSuites:getTestSuiteEmulationDataURLS: " + ex.getMessage());
                ex.printStackTrace();
            }
        } catch (Exception ex) {

        }

        return results;
    }

    /**
     * This method goes through the list of test suites and returns the first
     * test suite with a matching name.
     *
     * @param testSuiteName
     * @return
     * @throws Exception
     */
    private TestSuite getTestSuiteByName(String testSuiteName) throws Exception {
        TestSuite tempSuite = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            tempSuite = (testSuitesList.get(ii));
            if (testSuiteName.equals(tempSuite.testSuiteName)) {
                return tempSuite;
            }
        }
        throw new Exception("Could not find test suite " + testSuiteName);
    }

    /**
     * returns the URL associated with the test suites OtherRequirements CSV
     * File.
     *
     * @param theSuite the the suite
     * @return the test suite other requirements url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteOtherRequirementsURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {

                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/OtherRequirements.csv");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/OtherRequirements.csv");
                }
            }
        }

        return results;
    }

    /**
     * returns the URL associated with the test suites Predicates CSV File.
     *
     * @param theSuite the the suite
     * @return the test suite predicates url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuitePredicatesURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {

                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/Predicates.csv");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/Predicates.csv");
                }
            }
        }

        return results;
    }

    /**
     * Gets the test suite main script url.
     *
     * @param theSuite the the suite
     * @return the test suite main script url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteMainScriptURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {

                results = new URL(tempSuite.testSuitePath + "/Scripts/Main.xml");
            }
        }

        return results;
    }

    
    /**
     * returns the URL associated with the test suites TestCaseDescriptions XML File.
     *
     * @param theSuite the the suite
     * @return the test suite test case descriptions url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteTestCaseDescriptionsURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/TestCaseDescriptions.xml");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/TestCaseDescriptions.xml");
                }
            }
        }

        return results;
    }
    
   /**
     * returns the URL associated with the test suites TestProcedureDescriptions XML File.
     *
     * @param theSuite the the suite
     * @return the test suite test procedure descriptions url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteTestProcedureDescriptionsURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/TestProcedureDescriptions.xml");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/TestProcedureDescriptions.xml");
                }
            }
        }

        return results;
    }

   /**
     * returns the URL associated with the test suites TestProcedureSteps XML File.
     *
     * @param theSuite the the suite
     * @return the test suite test procedure steps url
     * @throws MalformedURLException the malformed url exception
     */
    public URL getTestSuiteTestProcedureStepsURL(String theSuite) throws MalformedURLException {
        URL results = null;
        for (int ii = 0; ii < this.testSuitesList.size(); ii++) {
            TestSuite tempSuite = (testSuitesList.get(ii));
            if (theSuite.equals(tempSuite.testSuiteName)) {
                if (tempSuite.testSuiteType.equals("Application")) {
                    results = new URL(tempSuite.testSuitePath + "/AppLayer/TestProcedureSteps.xml");
                } else {
                    results = new URL(tempSuite.testSuitePath + "/InfoLayer/TestProcedureSteps.xml");
                }
            }
        }

        return results;
    }
    
    /**
     * The TestSuite Class represents each Test Suite recognized by the RI as
     * being loaded on the application platform.
     *
     * @author TransCore ITS, LLC Last Updated: 1/8/2014
     */
    private class TestSuite {

        /**
         * The test suite name.
         */
        protected String testSuiteName = "";

        /**
         * The test suite version.
         */
        protected String testSuiteVersion = "";

        /**
         * The app layer standard.
         */
        protected String appLayerStandard = "";

        /**
         * The info layer standard.
         */
        protected String infoLayerStandard = "";

        /**
         * The pre defined test suite.
         */
        protected boolean preDefinedTestSuite = false;

        /**
         * The test suite path.
         */
        protected String testSuitePath = "";

        /**
         * The description.
         */
        protected String description = "";

        /**
         * The baseline test suite name.
         */
        protected String baselineTestSuiteName = "";

        /**
         * The baseline test suite version.
         */
        protected String baselineTestSuiteVersion = "";

        /**
         * The test suite type.
         */
        protected String testSuiteType = "";

        /**
         * Gets the baseline test suite name.
         *
         * @return the baseline test suite name
         */
        public String getBaselineTestSuiteName() {
            return baselineTestSuiteName;
        }

        /**
         * Gets the test suite version.
         *
         * @return the test suite version
         */
        public String getTestSuiteVersion() {
            return testSuiteVersion;
        }

        /**
         * Gets the baseline test suite version.
         *
         * @return the baseline test suite version
         */
        public String getBaselineTestSuiteVersion() {
            return baselineTestSuiteVersion;
        }

        /**
         * Checks if is pre defined test suite.
         *
         * Pre-Conditions: N/A Post-Conditions: N/A
         *
         * @return true, if is pre defined test suite
         */
        public boolean isPreDefinedTestSuite() {
            return preDefinedTestSuite;
        }

        /**
         * Gets the test suite type.
         *
         * @return the test suite type
         */
        public String getTestSuiteType() {
            return testSuiteType;
        }

        /**
         * Gets the app layer standard.
         *
         * @return the app layer standard
         */
        public String getAppLayerStandard() {
            return appLayerStandard;
        }

        /**
         * Gets the description.
         *
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Gets the info layer standard.
         *
         * @return the info layer standard
         */
        public String getInfoLayerStandard() {
            return infoLayerStandard;
        }

        /**
         * Gets the test suite path.
         *
         * @return the test suite path
         */
        public String getTestSuitePath() {
            return testSuitePath;
        }
    }
}
