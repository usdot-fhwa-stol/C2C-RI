/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import java.util.ArrayList;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.infolayer.InformationLayerController;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * Send the message request
 *
 * @author TransCore ITS
 * Last Updated: 10/1/2012
 * @jameleon.function name="SUBSCRIPTION-EC" type="action"
 * @jameleon.step Send the message request
 */
public class SubscriptionECTag extends C2CRIFunctionTag {

    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String Dialog;
    /**
     * The message request to send
     *
     * @jameleon.attribute required="true" name = "SUBSCRIPTIONMESSAGE"
     */
    protected Object SUBSCRIPTIONMESSAGE;
    /**
     * A flag which indicates whether a error response is expected
     *
     * @jameleon.attribute required="true"
     * name="SUBSCRIPTIONERRORRESPONSEEXPECTED"
     */
    protected String SUBSCRIPTIONERRORRESPONSEEXPECTED;
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true" name="SUBSCRIPTIONERRORTYPEEXPECTED"
     */
    protected String SUBSCRIPTIONERRORTYPEEXPECTED;
    /**
     * The time in milliseconds within which an owner center has to send a
     * response
     *
     * @jameleon.attribute required="false" default="60000"
     */
    protected String RESPONSETIMEREQUIRED;
    
    /** The information layer controller. */
    private InformationLayerController informationLayerController;

    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {
        if (RESPONSETIMEREQUIRED == null) {
            RESPONSETIMEREQUIRED = "60000";
        }
        try {
            setVariable("CONTINUEPUBLICATION", false);

            informationLayerController = sessionTag.getTheInformationLayerStandard().getInformationLayerController();

            ArrayList<String> messageSpecArray = new ArrayList<String>();
            MessageManager theManager = MessageManager.getInstance();
            Message newMessage = theManager.createMessage(Dialog);
            if (SUBSCRIPTIONMESSAGE instanceof String) {
                String request = (String) SUBSCRIPTIONMESSAGE;

                if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                    request = request.replace("#RIMessageSpec#", "");
                    String[] requestArray = request.split("; ");
                    for (int ii = 0; ii < requestArray.length; ii++) {
                        messageSpecArray.add(requestArray[ii]);
                    }
                    MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                    newMessage.setMessageSpecification(messageSpec);

                } else {
                    if ((SUBSCRIPTIONMESSAGE == null) || (((String) SUBSCRIPTIONMESSAGE).isEmpty())) {
                        setVariable("CONTINUEPUBLICATION", false);
                        throw new JameleonScriptException("An empty or null SubscriptionMessage can not be transmitted. ", this);
                    } else {
                        newMessage.setMessageBody(((String) SUBSCRIPTIONMESSAGE).getBytes());
                        newMessage.setSkipApplicationLayerEncoding(true);
                    }

                }
            } else if (SUBSCRIPTIONMESSAGE instanceof ArrayList) {
                messageSpecArray = (ArrayList<String>) SUBSCRIPTIONMESSAGE;
                MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                newMessage.setMessageSpecification(messageSpec);
            }
            newMessage.setMessageName("REQUEST");
            theManager.addMessage(newMessage, true);

            InformationLayerOperationResults infoResults = informationLayerController.performSubscriptionEC(Dialog, newMessage);
            newMessage = infoResults.getRequestMessage();
            newMessage.setMessageName("REQUEST");
            theManager.addMessage(newMessage, true);
//            log.info(newMessage.toXML());
            Message responseMessage = infoResults.getResponseMessage();
            responseMessage.setMessageName("RESPONSE");
            theManager.addMessage(responseMessage, true);
            DialogResults results = infoResults.getDialogResults(Message.MESSAGETYPE.Response);
//            log.info(responseMessage.toXML());

            // If the fields contained within the RESPONSE satisfy the information layer standard design then continue
            if (!results.isMessageFieldsVerified()) {
                processError("The fields contained within the RESPONSE does not match the TMDD Design. " + results.getMessageFieldsVerifiedErrors());
            }

            // If the value of each field contained within the RESPONSE satisfy the information layer standard design then continue
            if (!results.isMessageValuesVerified()) {
                processError("The value of each field contained within the RESPONSE does not match the TMDD Design." + results.getMessageValuesVerifiedErrors());
            }

            // If the SUT responded with one message per the app layer standard rules then continue
            if (!results.isOneMessageReceived()) {
                processError("The SUT responded with more than one message. " + results.getMessageReceivedErrors());
            }


            // If the error response conditions are met then continue
            try {
                TMDDErrorResponseProcessor.processErrorResponseMessage(SUBSCRIPTIONERRORRESPONSEEXPECTED, SUBSCRIPTIONERRORTYPEEXPECTED, responseMessage);
            } catch (Exception ex) {
                throw new JameleonScriptException(ex.getMessage(), this);
            }

            long responseTime = infoResults.getResponseMessage().getTransportTimeInMillis() - infoResults.getRequestMessage().getTransportTimeInMillis();
            long requiredTime = Long.parseLong(RESPONSETIMEREQUIRED);
            String pathValue = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_PATH_DELAY_PARAMETER(),
                    TMDDSettingsImpl.getTMDD_PATH_DELAY_DEFAULT_VALUE());
            long pathTime = Long.parseLong(pathValue);
            if ((responseTime - pathTime) > requiredTime) {
                processError("The response time " + responseTime + "ms minus the path time value " + pathValue
                        + "ms is greater than the required " + requiredTime + " ms.");
            }

            // Verify that this dialog does not violate any defined TMDD operations.
            if(!SUBSCRIPTIONERRORRESPONSEEXPECTED.equalsIgnoreCase("true"))infoResults.checkTMDDOperationResults();          
            
            setVariable("CONTINUEPUBLICATION", true);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JameleonScriptException("Error with subscription-ec: '" + ex.getMessage(), this);
        }



    }
}
