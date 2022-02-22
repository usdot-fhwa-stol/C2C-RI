/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import java.util.ArrayList;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
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
 * @jameleon.function name="PUBLICATION-EC-REPLY" type="action"
 * @jameleon.step Send the message response
 */
public class PublicationECReplyTag extends C2CRIFunctionTag {

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
    protected Object PUBLICATIONRESPONSEMESSAGE;
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

    /** The information layer controller. */
    private InformationLayerController theInformationLayerController;

    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {
        if (!getVariableAsBoolean("PUBLICATION-EC-RECEIVE-ERROR")) {
            try {

                theInformationLayerController = sessionTag.getTheInformationLayerStandard().getInformationLayerController();

                ArrayList<String> messageSpecArray = new ArrayList<String>();
                MessageManager theManager = MessageManager.getInstance();
                System.out.println("PublicationECReplyTag::testBlock MessageManger = " + theManager + "\n");

                Message replyMessage = theManager.createMessage(Dialog);
                if (PUBLICATIONRESPONSEMESSAGE instanceof String) {
                    String request = (String) PUBLICATIONRESPONSEMESSAGE;

                    if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                        request = request.replace("#RIMessageSpec#", "");
                        String[] requestArray = request.split("; ");
                        for (int ii = 0; ii < requestArray.length; ii++) {
                            messageSpecArray.add(requestArray[ii]);
                        }
                        MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                        replyMessage.setMessageSpecification(messageSpec);
                    } else {
                        if ((PUBLICATIONRESPONSEMESSAGE == null) || (((String) PUBLICATIONRESPONSEMESSAGE).isEmpty())) {
                            messageSpecArray.add("c2c:c2cMessageReceipt.informationalText=Success (PublicationResponseMessage was Empty or Null)");
                            MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                            replyMessage.setMessageSpecification(messageSpec);
                        } else {
                            replyMessage.setMessageBody(((String) PUBLICATIONRESPONSEMESSAGE).getBytes());
                            replyMessage.setSkipApplicationLayerEncoding(true);
                        }
                    }
                } else if (PUBLICATIONRESPONSEMESSAGE instanceof ArrayList) {
                    messageSpecArray = (ArrayList<String>) PUBLICATIONRESPONSEMESSAGE;
                    MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                    replyMessage.setMessageSpecification(messageSpec);
                }

                replyMessage.setMessageName("RESPONSE");
                theManager.addMessage(replyMessage, true);

                if (!CONTENTVERIFIED.equalsIgnoreCase("true")) {
                    TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
                    replyMessage = errorProcessor.getErrorResponseMsg("RESPONSE", "missing information prevents processing message", "The message received was missing some project mandatory data content.");
                    setVariable("CONTINUEPUBLICATION", false);
                }
                InformationLayerOperationResults opResults = theInformationLayerController.performPublicationECResponse(Dialog, replyMessage, false);
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
        }
        System.out.println("Value of CONTINUEPUBLICATION = " + this.getVariable("CONTINUEPUBLICATION"));
    }
}
