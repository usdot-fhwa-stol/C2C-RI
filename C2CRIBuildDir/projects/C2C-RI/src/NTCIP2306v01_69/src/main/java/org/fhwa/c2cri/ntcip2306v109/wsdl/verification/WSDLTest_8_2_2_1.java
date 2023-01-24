/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;

/**
 * The Class WSDLTest_8_2_2_1.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 1/8/2014
 */
public class WSDLTest_8_2_2_1 implements WSDLTest
{

    /**
     * The wsdl spec.
     */
    RIWSDL wsdlSpec;

    /**
     * The wsdl url.
     */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_8_2_2_1.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL  the wsdl url
     */
    public WSDLTest_8_2_2_1(RIWSDL wsdlSpec, String wsdlURL)
    {
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
    }

    /**
     * Perform.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    @Override
    public TestAssertion perform()
    {
        TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("8.2.2.1");

        WSDLTest_8_1_2_1_Post wsdl8121Test = new WSDLTest_8_1_2_1_Post(wsdlSpec, wsdlURL);
        TestAssertion results8121 = wsdl8121Test.perform();

        WSDLTest_8_1_2_2_Post wsdl8122Test = new WSDLTest_8_1_2_2_Post(wsdlSpec, wsdlURL);
        TestAssertion results8122 = wsdl8122Test.perform();

        WSDLTest_8_1_2_3_Post wsdl8123Test = new WSDLTest_8_1_2_3_Post(wsdlSpec, wsdlURL);
        TestAssertion results8123 = wsdl8123Test.perform();

        WSDLTest_8_1_2_4_Post wsdl8124Test = new WSDLTest_8_1_2_4_Post(wsdlSpec, wsdlURL);
        TestAssertion results8124 = wsdl8124Test.perform();

        WSDLTest_8_1_2_5_Post wsdl8125Test = new WSDLTest_8_1_2_5_Post(wsdlSpec, wsdlURL);
        TestAssertion results8125 = wsdl8125Test.perform();

        WSDLTest_8_1_2_6_Post wsdl8126Test = new WSDLTest_8_1_2_6_Post(wsdlSpec, wsdlURL);
        TestAssertion results8126 = wsdl8126Test.perform();

// Conflicts with Requirement 8.2.2.2
//        WSDLTest_8_1_2_7_Post wsdl8127Test = new WSDLTest_8_1_2_7_Post(wsdlSpec, wsdlURL);
//        TestAssertion results8127 = wsdl8127Test.perform();
        WSDLTest_8_1_2_8_Post wsdl8128Test = new WSDLTest_8_1_2_8_Post(wsdlSpec, wsdlURL);
        TestAssertion results8128 = wsdl8128Test.perform();

        WSDLTest_8_1_2_9_Post wsdl8129Test = new WSDLTest_8_1_2_9_Post(wsdlSpec, wsdlURL);
        TestAssertion results8129 = wsdl8129Test.perform();

        WSDLTest_8_1_2_10_Post wsdl81210Test = new WSDLTest_8_1_2_10_Post(wsdlSpec, wsdlURL);
        TestAssertion results81210 = wsdl81210Test.perform();

        if (results8121.getTestResult().equals("Passed")
            && results8122.getTestResult().equals("Passed")
            && results8123.getTestResult().equals("Passed")
            && results8124.getTestResult().equals("Passed")
            && results8125.getTestResult().equals("Passed")
            && results8126.getTestResult().equals("Passed")
            //                && results8127.getTestResult().equals("Passed")
            && results8128.getTestResult().equals("Passed")
            && results8129.getTestResult().equals("Passed")
            && results81210.getTestResult().equals("Passed"))
        {
            assertion.setTestResult("Passed");
            assertion.setTestResultDescription("");

        }
        else
        {
            String failureResult = "";
            
            assertion.setTestResult("Failed");
            if (results8121.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat("8.1.2.1: " + results8121.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results8121.isContinueAfterFailure());
            }
            if (results8122.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results8122.getTestAssertionID() + ": " + results8121.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results8122.isContinueAfterFailure());
            }
            if (results8123.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results8123.getTestAssertionID() + ": " + results8123.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results8123.isContinueAfterFailure());
            }
            if (results8124.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results8124.getTestAssertionID() + ": " + results8124.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results8124.isContinueAfterFailure());
            }
            if (results8125.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat("8.1.2.5: " + results8125.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results8125.isContinueAfterFailure());
            }
            if (results8126.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat("8.1.2.6: " + results8126.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results8126.isContinueAfterFailure());
            }
//            if (results8127.getTestResult().equals("Failed")) {
//                failureResult = failureResult.concat("8.1.2.7: " + results8127.getTestResultDescription() + "\n");
//            }
            if (results8128.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat("8.1.2.8: " + results8128.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results8128.isContinueAfterFailure());
            }
            if (results8129.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat("8.1.2.9: " + results8129.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results8129.isContinueAfterFailure());
            }
            if (results81210.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat("8.1.2.10: " + results81210.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results81210.isContinueAfterFailure());
            }
            assertion.setTestResultDescription(failureResult);
        }
        assertion.setTestAssertionPrescription("The same requirement of the Request Only Binding shall apply. ");

        return assertion;
    }
}
