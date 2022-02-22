/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Interface WSDLTest.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface WSDLTest {

    /**
     * Perform.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    public TestAssertion perform();

}
