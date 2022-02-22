/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import java.util.ArrayList;
import net.sf.jameleon.exception.JameleonScriptException;
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
 * @author TransCore ITS, LLC Last Updated: 10/5/2013
 * @jameleon.function name="SUBSCRIPTION-OC-REPLY" type="action"
 * @jameleon.step Send the message response
 */
public class SubscriptionOCReplyTag extends C2CRIFunctionTag {

    /**
     * The name of the dialog associated with this subscription
     *
     * @jameleon.attribute required="true"
     */
    protected String Dialog;
    /**
     * The message response to send
     *
     * @jameleon.attribute required="true"
     */
    protected Object SUBSCRIPTIONRESPONSEMESSAGE;
    /**
     * A flag which indicates whether the request optional content was
     * successfully verified
     *
     * @jameleon.attribute required="true" default="NotSet"
     */
    protected String CONTENTVERIFIED;
    /**
     * The time in milliseconds within which an owner center has to send a
     * response
     *
     * @jameleon.attribute required="false" default="60000"
     */
    protected String RESPONSETIMEREQUIRED;

    /**
     * The information layer controller.
     */
    private InformationLayerController theInformationLayerController;

    /**
     * Test block.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void testBlock() {
        if (RESPONSETIMEREQUIRED == null) {
            RESPONSETIMEREQUIRED = "60000";
        }
        if (!getVariableAsBoolean("SUBSCRIPTION-OC-RECEIVE-ERROR")) {
            try {
                setVariable("CONTINUEPUBLICATION", true);

                theInformationLayerController = sessionTag.getTheInformationLayerStandard().getInformationLayerController();

                ArrayList<String> messageSpecArray = new ArrayList<String>();
                MessageManager theManager = MessageManager.getInstance();

                Message replyMessage = null;
                boolean emulationReturnedErrorMessage = false;

                // First try to set up the replyMessage using Entity Emulation if it is enabled.
                if (RIEmulation.getInstance().isEmulationEnabled()) {
                    try {
                        replyMessage = RIEmulation.getInstance().getSubResponse(Dialog, theManager.getMessage("REQUEST"));
                        replyMessage.setParentDialog(Dialog);
                        if (replyMessage.getMessageName().contains("errorReportMsg")) {
                            emulationReturnedErrorMessage = true;
                        } else {
                            // Change the message name to SUBSCRIPTION so that it can be referenced by the Publication.
                            theManager.getMessage("REQUEST").setMessageName("SUBSCRIPTION");
                        }

                    } catch (Exception ex) {
                        log.debug(ex.getMessage());
                    }
                }
                if (replyMessage == null) {

                    replyMessage = theManager.createMessage(Dialog);
                    if (SUBSCRIPTIONRESPONSEMESSAGE instanceof String) {
                        String request = (String) SUBSCRIPTIONRESPONSEMESSAGE;

                        if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                            request = request.replace("#RIMessageSpec#", "");
                            String[] requestArray = request.split("; ");
                            for (int ii = 0; ii < requestArray.length; ii++) {
                                messageSpecArray.add(requestArray[ii]);
                            }
                            MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                            replyMessage.setMessageSpecification(messageSpec);
                        } else if ((SUBSCRIPTIONRESPONSEMESSAGE == null) || (((String) SUBSCRIPTIONRESPONSEMESSAGE).isEmpty())) {
                            messageSpecArray.add("c2c:c2cMessageReceipt.informationalText=\"Success (SubscriptionResponseMessage was Empty or Null)\"");
                            MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                            replyMessage.setMessageSpecification(messageSpec);
                        } else {
                            replyMessage.setMessageBody(((String) SUBSCRIPTIONRESPONSEMESSAGE).getBytes());
                            replyMessage.setSkipApplicationLayerEncoding(true);
                        }
                    } else if (SUBSCRIPTIONRESPONSEMESSAGE instanceof ArrayList) {
                        messageSpecArray = (ArrayList<String>) SUBSCRIPTIONRESPONSEMESSAGE;
                        MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                        replyMessage.setMessageSpecification(messageSpec);
                    }
                }
                replyMessage.setMessageName("RESPONSE");
                theManager.addMessage(replyMessage, true);

                if (!CONTENTVERIFIED.equalsIgnoreCase("true")) {
                    log.debug("SubscriptionOCReplyTag: OptionalContentVerified = " + CONTENTVERIFIED);
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    replyMessage = errorProcessor.getErrorResponseMsg("RESPONSE", "missing information prevents processing message", "The message received was missing some project mandatory data content.");
                    setVariable("CONTINUEPUBLICATION", false);
                } else if (emulationReturnedErrorMessage){  // The subscription request was not valid.
                    setVariable("CONTINUEPUBLICATION", false);                    
                }
                
                InformationLayerOperationResults opResults = theInformationLayerController.performSubscriptionOCResponse(Dialog, replyMessage, false);
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
                setVariable("CONTINUEPUBLICATION", false);
                ex.printStackTrace();
                throw new JameleonScriptException("Error sending message reply: '" + ex.getMessage(), this);
            }
        } else {
            setVariable("CONTINUEPUBLICATION", false);

        }
    }
}
