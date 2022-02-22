/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;

/**
 * The Class WSDLTest_7_2_2_3.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 1/8/2014
 */
public class WSDLTest_7_2_2_3 implements WSDLTest
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
     * Instantiates a new wSDL test_7_2_2_3.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL  the wsdl url
     */
    public WSDLTest_7_2_2_3(RIWSDL wsdlSpec, String wsdlURL)
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
        assertion.setTestAssertionID("7.2.2.3");

        WSDLTest_7_1_1_1 wsdlTest7111 = new WSDLTest_7_1_1_1(wsdlSpec, wsdlURL, false);
        TestAssertion results7111 = wsdlTest7111.perform();
        
        WSDLTest_7_1_1_2 wsdlTest7112 = new WSDLTest_7_1_1_2(wsdlSpec, wsdlURL, false);
        TestAssertion results7112 = wsdlTest7112.perform();
        
        WSDLTest_7_1_1_3 wsdlTest7113 = new WSDLTest_7_1_1_3(wsdlSpec, wsdlURL, false);
        TestAssertion results7113 = wsdlTest7113.perform();
        
        WSDLTest_7_1_1_4 wsdlTest7114 = new WSDLTest_7_1_1_4(wsdlSpec, wsdlURL, false);
        TestAssertion results7114 = wsdlTest7114.perform();

        if (results7111.getTestResult().equals("Passed") 
            && results7112.getTestResult().equals("Passed") 
            && results7113.getTestResult().equals("Passed") 
            && results7114.getTestResult().equals("Passed"))
        {
            assertion.setTestResult("Passed");
            assertion.setTestResultDescription("");
        }
        else
        {
            String failureResult = "";

            assertion.setTestResult("Failed");
            
            /* We have to check 7112 first because it lets the process continue.
             * If any of the other cases failed too, they would stop the test process by overriding the flag to false.
             */
            if (results7112.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7112.getTestAssertionID() + ": " + results7112.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7112.isContinueAfterFailure());
            }
            if (results7111.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7111.getTestAssertionID() + ": " + results7111.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7111.isContinueAfterFailure());
            }
            if (results7113.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7113.getTestAssertionID() + ": " + results7113.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7113.isContinueAfterFailure());
            }
            if (results7114.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7114.getTestAssertionID() + ": " + results7114.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7114.isContinueAfterFailure());
            }
            assertion.setTestResultDescription(failureResult);
        }
        assertion.setTestAssertionPrescription("The operation name, input message, and output message follow the same rules as those defined for a SOAP Request-Response portType defined in Section 7.1.3. ");

        return assertion;
    }
}
