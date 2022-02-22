/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.TestStepTag;
import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.infolayer.InformationLayerController;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * Send the message request
 *
 * @author TransCore ITS
 * @jameleon.function name="REQUEST-RESPONSE-OC-RECEIVE" type="action"
 * @jameleon.step Receive the message request
 */
public class RequestResponseOCReceiveTag extends C2CRIFunctionTag {

    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String Dialog;
    /**
     * A flag which indicates whether authentication checking is expected/enabled
     *
     * @jameleon.attribute required="true"
     */
    protected String AUTHENTICATIONCHECK;
    /**
     * A flag which indicates whether a error response is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String ERRORRESPONSEEXPECTED;
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String ERRORTYPEEXPECTED;
    /**
     * The username that was provided for the test case
     *
     * @jameleon.attribute required="true"
     */
    protected String USERNAME;
    /**
     * The password that was provided for the test case
     *
     * @jameleon.attribute required="true"
     */
    protected String PASSWORD;
    /**
     * The type organization ID that was provided for the test case
     *
     * @jameleon.attribute required="true"
     */
    protected String OPERATORID;
    /**
     * A flag indicating whether the external center should be allowed access to the requested information.
     *
     * @jameleon.attribute required="true"
     */
    protected String ALLOWACCESS;
    /**
     * The flag for whether there is valid data available
     *
     * @jameleon.attribute required="false"
     */
    protected String DATAVALID = "True";
    /**
     * The time in milliseconds within which an owner center has to send a
     * response
     *
     * @jameleon.attribute required="true"
     */
    protected String RESPONSETIMEREQUIRED;
    
    /** The information layer controller. */
    private InformationLayerController theInformationLayerController;

    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {
        try {

            theInformationLayerController = sessionTag.getTheInformationLayerStandard().getInformationLayerController();

//            JOptionPane.showMessageDialog(null, "Click the OK button when ready to proceed with REQUEST-RESPONSE-OC-RECEIVE.", "REQUEST-RESPONSE-OC-RECEIVE", JOptionPane.INFORMATION_MESSAGE);
            InformationLayerOperationResults opResults = theInformationLayerController.performRequestResponseOCReceive(Dialog);
//            Message requestMessage = theApplicationLayerStandard.request_response_oc_receive(Dialog);
            Message requestMessage = opResults.getRequestMessage();
            requestMessage.setMessageName("REQUEST");
            MessageManager theManager = MessageManager.getInstance();
            theManager.addMessage(requestMessage, true);

            DialogResults results = opResults.getDialogResults(Message.MESSAGETYPE.Request);
//            log.info(requestMessage.toXML());

            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            // Check the request message for errors.
            if (errorProcessor.requestContainsError(results, AUTHENTICATIONCHECK, ALLOWACCESS,
                    ERRORRESPONSEEXPECTED, ERRORTYPEEXPECTED, DATAVALID, USERNAME, PASSWORD, OPERATORID, this.getTestCaseTag().getTestCase().getTestCaseId(), requestMessage)) {

                // Generate an errorResponseMessage for erroneous message that was received.
                Message errorResponseMessage = errorProcessor.getErrorResponseMsg(Dialog);
                
                // Send the error Response Message to the EC.
                InformationLayerOperationResults errorOpResults = theInformationLayerController.performRequestResponseOCResponse(Dialog, errorResponseMessage, true);

//                theApplicationLayerStandard.request_response_oc_reply(Dialog, errorResponseMessage);
                theManager.addMessage(errorResponseMessage, true);
                errorResponseMessage.setMessageName("RESPONSE");
//                log.info(errorResponseMessage.toXML());
 

                if (ERRORRESPONSEEXPECTED.equalsIgnoreCase("true")) {  // We were expecting an erroneous request

                    if (ERRORTYPEEXPECTED.equals(errorProcessor.getErrorType()) || (ERRORTYPEEXPECTED.equals(errorProcessor.getErrorTypeText()))) {
                        setVariable("REQUEST-RESPONSE-OC-RECEIVE-ERROR", false);
                        // OK
                    } else {  // The type of erroneous request we received was not expected.
                        setVariable("REQUEST-RESPONSE-OC-RECEIVE-ERROR", true);
                        processError("An error response was sent with error code " + errorProcessor.getErrorType() + "-"
                                + errorProcessor.getErrorTypeText() + " while ErrorTypeExpected was set to " + ERRORTYPEEXPECTED + ".");

                    }

                } else {  // We received an erroneous request that we were not expecting.
                    // Set the variable indicating that we received an erroneous request message when we were not expecting one.
                    setVariable("REQUEST-RESPONSE-OC-RECEIVE-ERROR", true);
                    processError("An error response was sent when ErrorResponseExpected was set to " + ERRORRESPONSEEXPECTED + "." + "  The error encountered was: " + errorProcessor.getErrorText());
                }
                
                // Verify the response time requirement
                long responseTime = errorOpResults.getResponseMessage().getTransportTimeInMillis() - errorOpResults.getRequestMessage().getTransportTimeInMillis();
                long requiredTime = Long.parseLong(RESPONSETIMEREQUIRED);
                String pathValue = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_PATH_DELAY_PARAMETER(),
                        TMDDSettingsImpl.getTMDD_PATH_DELAY_DEFAULT_VALUE());
                long pathTime = Long.parseLong(pathValue);
                if ((responseTime - pathTime) > requiredTime) {
                    processError("The response time " + responseTime + "ms minus the path time value " + pathValue
                            + "ms is greater than the required " + requiredTime + " ms.");
                }

            } else {
                // Set the flag indicating that we have NOT already processed this request by sending an error response message.
                setVariable("REQUEST-RESPONSE-OC-RECEIVE-ERROR", false);
            };



        } catch (Exception ex) {
            ex.printStackTrace();
            setVariable("REQUEST-RESPONSE-OC-RECEIVE-ERROR", true);
            throw new JameleonScriptException(ex.getMessage(), this);
        }



    }
    
}
