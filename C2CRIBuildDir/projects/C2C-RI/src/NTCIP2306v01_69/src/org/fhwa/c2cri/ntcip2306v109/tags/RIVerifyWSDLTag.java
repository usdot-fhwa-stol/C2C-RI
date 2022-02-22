/**
 *
 */
package org.fhwa.c2cri.ntcip2306v109.tags;

import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.TestStepTag;
import org.fhwa.c2cri.ntcip2306v109.wsdl.verification.WSDLTest;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;

/**
 * Verify the Session's WSDL using the Test Assertions associated with the
 * designated predefined or custom test suite.
 *
 * @author TransCore ITS,LLC Last Updated: 9/12/2013
 *
 * @jameleon.function name="ri-verify-WSDL" type="action"
 * @jameleon.step Verify the specified WSDL Verification element
 */
public class RIVerifyWSDLTag extends RIWSDLFunctionTag {

    /**
     * The level to perform the test assertions (predefined or custom)
     *
     * @jameleon.attribute
     */
    protected String level;
    /**
     * The test result to receive
     *
     * @jameleon.attribute required="true"
     */
    protected String testName;
    /**
     * The returned test result status
     *
     * @jameleon.attribute
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

    @Override
    public void testBlock() {

        String theResults = "";
        String errorResults = "";

        try {
            WSDLTest theTest = sessionTag.getRiWSDLTesterFactory().getTest(testName);
            TestAssertion thisAssertion = theTest.perform();
            theResults = thisAssertion.getTestResult();
            errorResults = thisAssertion.getTestResultDescription();
            if (!theResults.equalsIgnoreCase(testResultExpected)) {

                if (thisAssertion.isContinueAfterFailure()) {
                    try {
                        TestStepTag thisOne = (TestStepTag) this.getParent();
                        thisOne.getFunctionResults().getParentResults().getFailedResults().clear();
                        this.getFunctionResults().setError(new JameleonScriptException(errorResults));
                        thisOne.getFunctionResults().setFailed();
                    } catch (Exception ex) {
                    }

                } else {
                    throw new JameleonScriptException("Verification Error -- Expected:" + testResultExpected + "  Received:" + theResults + "\n Description: " + errorResults, this);
                }
            }

        } catch (JameleonScriptException ex) {
            throw ex;
        }catch (Exception ex) {
            throw new JameleonScriptException("Verification Error -- Expected:" + testResultExpected + "  Received: Not Tested\n  Description: The requested test was not performed." + ex.getMessage(), this);
        }

        if ((testResult != null) && (!testResult.isEmpty())) {
            setVariable(testResult, theResults);
        }

        if ((errorDescription != null) && (!errorDescription.isEmpty())) {
            setVariable(errorDescription, errorResults);
        }

    }
}
