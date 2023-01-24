/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import java.util.ArrayList;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.infolayer.InformationLayerController;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * Send the reply message
 *
 * @author TransCore ITS
 * @jameleon.function name="REQUEST-RESPONSE-OC-REPLY" type="action"
 * @jameleon.step Send the message response
 */
public class RequestResponseOCReplyTag extends C2CRIFunctionTag {

    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String Dialog;
    /**
     * The message response to send
     *
     * @jameleon.attribute required="true"
     */
    protected Object RESPONSEMESSAGE;
    /**
     * A flag which indicates whether the request optional content was
     * successfully verified
     *
     * @jameleon.attribute required="true"
     */
    protected String CONTENTVERIFIED = "NotSet";
    /**
     * The time in milliseconds within which an owner center has to send a
     * response
     *
     * @jameleon.attribute required="true"
     */
    protected String RESPONSETIMEREQUIRED;

    /**
     * The application layer standard.
     */
    private InformationLayerController theApplicationLayerStandard;

    /**
     * Test block.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void testBlock() {
        if (!getVariableAsBoolean("REQUEST-RESPONSE-OC-RECEIVE-ERROR")) {
            try {

                theApplicationLayerStandard = sessionTag.getTheInformationLayerStandard().getInformationLayerController();

                MessageManager theManager = MessageManager.getInstance();
                Message replyMessage = null;

                // First try to set up the replyMessage using Entity Emulation if it is enabled.
                if (RIEmulation.getInstance().isEmulationEnabled()) {
                    try {
                        Message requestMessage = theManager.getMessage("REQUEST");
                        System.out.println("RequestResponseOCReplyTag::  Calling the getRRResponse Method");
                        replyMessage = RIEmulation.getInstance().getRRResponse(Dialog, requestMessage);
                        replyMessage.setParentDialog(Dialog);
                    } catch (Exception ex) {
                        log.debug(ex.getMessage());
                        ex.printStackTrace();
                    }
                }
                if (replyMessage == null) {
                    ArrayList<String> messageSpecArray = new ArrayList<String>();
                    replyMessage = theManager.createMessage(Dialog);
                    if (RESPONSEMESSAGE instanceof String) {
                        String request = (String) RESPONSEMESSAGE;

                        if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                            request = request.replace("#RIMessageSpec#", "");
                            String[] requestArray = request.split("; ");
                            for (int ii = 0; ii < requestArray.length; ii++) {
                                messageSpecArray.add(requestArray[ii]);
                            }
                            MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                            replyMessage.setMessageSpecification(messageSpec);
                        } else {
                            if ((RESPONSEMESSAGE == null) || (((String) RESPONSEMESSAGE).isEmpty())) {
                                TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                                replyMessage = errorProcessor.getErrorResponseMsg("RESPONSE", "missing information prevents processing message", "OCS Testing Data Error.  An empty or null ResponseMessage can not be transmitted.");
                            } else {
                                replyMessage.setMessageBody(((String) RESPONSEMESSAGE).getBytes());
                                replyMessage.setSkipApplicationLayerEncoding(true);
                            }
                        }
                    } else if (RESPONSEMESSAGE instanceof ArrayList) {
                        messageSpecArray = (ArrayList<String>) RESPONSEMESSAGE;
                        MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                        replyMessage.setMessageSpecification(messageSpec);
                    }

                    replyMessage.setMessageName("RESPONSE");
                    theManager.addMessage(replyMessage, true);
                }
                boolean isErrorMessage = false;
                if (!CONTENTVERIFIED.equalsIgnoreCase("true")) {
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    replyMessage = errorProcessor.getErrorResponseMsg("RESPONSE", "missing information prevents processing message", "The message received was missing some project mandatory data content.");
                    isErrorMessage = true;
                }
                InformationLayerOperationResults opResults = theApplicationLayerStandard.performRequestResponseOCResponse(Dialog, replyMessage, isErrorMessage);
//                theApplicationLayerStandard.request_response_oc_reply(Dialog, replyMessage);
//                log.info(replyMessage.toXML());
                long responseTime = opResults.getResponseMessage().getTransportTimeInMillis() - opResults.getRequestMessage().getTransportTimeInMillis();
                long requiredTime = Long.parseLong(RESPONSETIMEREQUIRED);
                String pathValue = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_PATH_DELAY_PARAMETER(),
                        TMDDSettingsImpl.getTMDD_PATH_DELAY_DEFAULT_VALUE());
                long pathTime = Long.parseLong(pathValue);
                if ((responseTime - pathTime) > requiredTime) {
                    throw new JameleonScriptException("The response time " + responseTime + "ms minus the path time value " + pathValue
                            + "ms is greater than the required " + requiredTime + " ms.", this);
                }

                // Verify that this dialog does not violate any defined TMDD operations.
                opResults.checkTMDDOperationResults();          
                
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new JameleonScriptException("Error sending message reply: '" + ex.getMessage(), this);
            }
        }
    }
}
