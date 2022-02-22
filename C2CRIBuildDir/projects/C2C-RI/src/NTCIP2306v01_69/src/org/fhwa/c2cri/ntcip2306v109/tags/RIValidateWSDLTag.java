/**
 * 
 */
package org.fhwa.c2cri.ntcip2306v109.tags;

import java.util.List;
import net.sf.jameleon.exception.JameleonScriptException;

/**
 * Validate the Session's WSDL.
 *
 * @author TransCore ITS,LLC
 * Last Updated: 9/12/2013
 *
 * @jameleon.function name="ri-validate-WSDL" type="action"
 * @jameleon.step Validate the specified WSDL
 */
public class RIValidateWSDLTag extends RIWSDLFunctionTag {

    /**
     * The returned test result status
     *        
     * @jameleon.attribute require = "true"
     */
    protected String testResult;
    /**
     * The returned test result expected
     *
     * @jameleon.attribute require = "true"
     */
    protected String testResultExpected;
    /**
     * The returned error description (if any)
     *        
     * @jameleon.attribute 
     */
    protected String errorDescription;

    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {
        // The actual validation of the WSDL is performed as part of the WSDL Session Tag.  This
        // tag simply provides the results of that validation.
        String results;
        String errorResults="";

        if (sessionTag.isWSDLValidatedAgainstSchema()) {
            results = "Passed";
            errorResults = "";
        } else {
            results = "Failed";
            List<String> errorList = sessionTag.getWsdlError();
            for (String thisError : errorList) {
                errorResults = errorResults.concat(thisError + "\n");
            }
        }

        if (!testResult.isEmpty()) {
            setVariable(testResult, results);
        }

        if (!errorDescription.isEmpty()) {
            setVariable(errorDescription, errorResults);
        }

        if (testResultExpected != null) {
            if (!results.equalsIgnoreCase(testResultExpected)) {
                throw new JameleonScriptException("Verification Error -- Expected:" + testResultExpected + "  Received:" + results + "\n Description: " + errorResults, this);
            }
        }

    }
}
