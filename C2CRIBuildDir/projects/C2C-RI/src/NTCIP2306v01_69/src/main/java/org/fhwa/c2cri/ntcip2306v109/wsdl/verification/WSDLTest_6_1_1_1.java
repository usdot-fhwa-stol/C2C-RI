/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class WSDLTest_6_1_1_1.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_6_1_1_1 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_6_1_1_1.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_6_1_1_1(RIWSDL wsdlSpec, String wsdlURL){
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
    }

    /**
     * Perform.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    @Override
    public TestAssertion perform() {
        TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("6.1.1.1");
        assertion.setTestResult(wsdlSpec.isWsdlValidatedAgainstSchema()&&wsdlSpec.getWsdlErrors().isEmpty()?"Passed":"Failed");
        String wsdlErrors = "";
        for (String thisError : wsdlSpec.getWsdlErrors()){
            wsdlErrors = wsdlErrors.concat(thisError+"\n");
        }
        assertion.setTestAssertionPrescription("The version of WSDL shall conform with W3C WSDL 1.1." );
        assertion.setTestResultDescription(wsdlErrors);
        return assertion;
    }

}
