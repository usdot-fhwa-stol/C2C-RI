/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.ntcip2306v109.wsdl.verification.WSDLTest;
import org.fhwa.c2cri.ntcip2306v109.wsdl.verification.WSDLTestFactory;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;

/**
 * The Class TestWSDLTestClasses.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestWSDLTestClasses {

    /**
     * The wsdl file.
     */
    private static String wsdlFile = "";

    /**
     * The results map.
     */
    private static HashMap<String, ArrayList<TestAssertion>> resultsMap = new HashMap();

    private static ArrayList<String> testResults = new ArrayList<String>();

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String args[]) {
        String wsdlFile = "http://localhost/ocservice/soap-1.1/ocservice.wsdl";

        if ((args != null) && (args.length != 0)) {
            wsdlFile = args[0];
        }
        for (int ii = 0; ii < 1; ii++) {
            RIWSDL theRIWSDL = new RIWSDL(wsdlFile);
            theRIWSDL.getAllOperationSpecifications();
            System.out.println("Iteration " + ii);

            TestAssertion thisResult;

            tryHere(theRIWSDL, wsdlFile, true);
            System.out.println("\n\n\n\n\n");

            System.out.println("START HERE \n\n");
            for (String theResult : testResults) {
                System.out.println(theResult);
            }
        }
    }

    /**
     * Finder.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param dirName the dir name
     * @return the file[]
     */
    private static File[] finder(String dirName) {
        File dir = new File(dirName);

        return dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String filename) {
                return filename.endsWith(".wsdl");
            }
        });

    }

    /**
     * Do all.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param path the path
     */
    private static void doAll(String path) {
        File[] wsdlFiles = finder(path);
        resultsMap.clear();

        for (File thisFile : wsdlFiles) {
            try {
                String thewsdlFile = thisFile.toURI().toURL().toString();
                wsdlFile = thisFile.getName();
                RIWSDL theRIWSDL = new RIWSDL(thewsdlFile);
                tryHere(theRIWSDL, thewsdlFile, true);
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("\n\n\n\n\n\nStarts Here...");
        for (String thewsdlFile : resultsMap.keySet()) {
            int writecount = 0;
            for (TestAssertion thisResult : resultsMap.get(thewsdlFile)) {
                System.out.println("\"" + thewsdlFile.replace("2306TstData", "").substring(0, 3) + "\",\"" + thewsdlFile + "\",\"" + thisResult.getTestAssertionID() + "\",\"" + (thisResult.getTestResultDescription() == null ? "NULL\"" : thisResult.getTestResultDescription().replace("\n", "<LF>") + "\""));
                writecount++;
                if (writecount >= 3) {
                    break;
                }
            }
        }

    }

    /**
     * Try here.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param theRIWSDL the the riwsdl
     * @param wsdlFile the wsdl file
     * @param failureOnly the failure only
     */
    public static void tryHere(RIWSDL theRIWSDL, String wsdlFile, boolean failureOnly) {
        WSDLTestFactory theFactory = new WSDLTestFactory(theRIWSDL, wsdlFile);
        try {
            TestAssertion thisResult;
            WSDLTest wsdlTest;

            wsdlTest = theFactory.getTest("6.1.1.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.1.1.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.1.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.2.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.3.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.3.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.3.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.1.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.1.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.1.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.1.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.2.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.2.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.2.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.2.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.3.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.3.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.3.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.4.3.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("6.5.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.1.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.1.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.1.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.1.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.5");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.6");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.7");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.8");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.9");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.2.10");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.3.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.3.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.3.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.1.3.5");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.1.2.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.1.2.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.2.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.2.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.2.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.2.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.2.5");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.3.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.4.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.4.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("7.2.4.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.1.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.1.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.1.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.1.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.1.5");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.5");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.6");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.7");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.8");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.9");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.1.2.10");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.2.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.2.2.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.2.2.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.3.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("8.3.1.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.2.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.3.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.3.2");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.3.3");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.3.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.3.5");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.3.6");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.3.7");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.4.1");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("9.1.4.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

            wsdlTest = theFactory.getTest("1.2.3.4");
            thisResult = wsdlTest.perform();
            if (!failureOnly || (failureOnly && !thisResult.getTestResult().equals("Passed"))) {
                printResult(thisResult);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Prints the result.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param thisResult the this result
     */
    public static void printResult(TestAssertion thisResult) {
        System.out.println("Assertion: " + thisResult.getTestAssertionID());
        System.out.println("Prescription: " + thisResult.getTestAssertionPrescription());
        System.out.println("TestResult: " + thisResult.getTestResult());
        System.out.println("Description: " + thisResult.getTestResultDescription());
        System.out.println("");

        testResults.add("Assertion: " + thisResult.getTestAssertionID());
        testResults.add("Prescription: " + thisResult.getTestAssertionPrescription());
        testResults.add("TestResult: " + thisResult.getTestResult());
        testResults.add("Description: " + thisResult.getTestResultDescription());
        testResults.add("");

        if (resultsMap.containsKey(wsdlFile)) {
            resultsMap.get(wsdlFile).add(thisResult);
        } else {
            ArrayList<TestAssertion> thisList = new ArrayList();
            thisList.add(thisResult);
            resultsMap.put(wsdlFile, thisList);
        }
    }
}
