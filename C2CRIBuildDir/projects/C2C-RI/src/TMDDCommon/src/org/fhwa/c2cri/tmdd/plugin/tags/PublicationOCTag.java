/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import java.util.ArrayList;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.DialogResults;
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
 * Send the message request
 *
 * @author TransCore ITS
 * @jameleon.function name="PUBLICATION-OC" type="action"
 * @jameleon.step Send the message request
 */
public class PublicationOCTag extends C2CRIFunctionTag {

    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String Dialog;
    /**
     * The message request to send
     *
     * @jameleon.attribute required="true"
     */
    protected Object PUBLICATIONMESSAGE;
    /**
     * A flag which indicates whether a error response is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String PUBLICATIONERRORRESPONSEEXPECTED;
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String PUBLICATIONERRORTYPEEXPECTED;
    /**
     * The number of publications that have been sent including this one
     *
     * @jameleon.attribute required="true"
     */
    protected Integer PUBLICATIONCOUNT;

    /**
     * The time in milliseconds within which an owner center has to send a
     * response
     *
     * @jameleon.attribute required="true"
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
        try {

            theInformationLayerController = sessionTag.getTheInformationLayerStandard().getInformationLayerController();

            ArrayList<String> messageSpecArray = new ArrayList<String>();
            MessageManager theManager = MessageManager.getInstance();
            Message newMessage = null;

            // First try to set up the replyMessage using Entity Emulation if it is enabled.
            if (RIEmulation.getInstance().isEmulationEnabled()) {
                try {
                    newMessage = RIEmulation.getInstance().getPubMessage(Dialog, theManager.getMessage("SUBSCRIPTION"));
                    newMessage.setParentDialog(Dialog);
                    if (newMessage.getMessageName().contains("errorReportMsg")) {
                        newMessage = null;
                    }

                } catch (Exception ex) {
                    log.debug(ex.getMessage());
                }
            }

            if (newMessage == null) {
                newMessage = theManager.createMessage(Dialog);
                if (PUBLICATIONMESSAGE instanceof String) {
                    String request = (String) PUBLICATIONMESSAGE;

                    if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                        request = request.replace("#RIMessageSpec#", "");
                        String[] requestArray = request.split("; ");
                        for (int ii = 0; ii < requestArray.length; ii++) {
                            messageSpecArray.add(requestArray[ii]);
                        }
                        MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                        newMessage.setMessageSpecification(messageSpec);

                    } else if ((PUBLICATIONMESSAGE == null) || (((String) PUBLICATIONMESSAGE).isEmpty())) {
                        setVariable("CONTINUEPUBLICATION", false);
                        throw new JameleonScriptException("An empty or null PublicationMessage can not be transmitted. ", this);
                    } else {
                        newMessage.setMessageBody(((String) PUBLICATIONMESSAGE).getBytes());
                        newMessage.setSkipApplicationLayerEncoding(true);
                    }
                } else if (PUBLICATIONMESSAGE instanceof ArrayList) {
                    messageSpecArray = (ArrayList<String>) PUBLICATIONMESSAGE;
                    MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                    newMessage.setMessageSpecification(messageSpec);
                }
            }
            newMessage.setMessageName("REQUEST");
            theManager.addMessage(newMessage, true);

            /**
             * Put pre-publication Logic Here
             *
             */
            setVariable("CONTINUEPUBLICATION", false);

            InformationLayerOperationResults infoResults = theInformationLayerController.performPublicationOC(Dialog, newMessage);
            newMessage = infoResults.getRequestMessage();
            newMessage.setMessageName("REQUEST");
            theManager.addMessage(newMessage, true);

            Message responseMessage = infoResults.getResponseMessage();
            responseMessage.setMessageName("RESPONSE");
            theManager.addMessage(responseMessage, true);

            DialogResults results = infoResults.getDialogResults(Message.MESSAGETYPE.Response);
//            log.info(newMessage.toXML());
//            log.info(responseMessage.toXML());

            // If the fields contained within the RESPONSE satisfy the information layer standard design then continue
            if (!results.isMessageFieldsVerified()) {
                setVariable("CONTINUEPUBLICATION", false);
                processError("The fields contained within the RESPONSE does not match the TMDD Design. " + results.getMessageFieldsVerifiedErrors());
            }

            // If the value of each field contained within the RESPONSE satisfy the information layer standard design then continue
            if (!results.isMessageValuesVerified()) {
                setVariable("CONTINUEPUBLICATION", false);
                processError("The value of each field contained within the RESPONSE does not match the TMDD Design." + results.getMessageValuesVerifiedErrors());
            }

            // If the SUT responded with one message per the app layer standard rules then continue
            if (!results.isOneMessageReceived()) {
                setVariable("CONTINUEPUBLICATION", false);
                processError("The SUT responded with more than one message. " + results.getMessageReceivedErrors());
            }

            // If the error response conditions are met then continue
            try {
                TMDDErrorResponseProcessor.processErrorResponseMessage(PUBLICATIONERRORRESPONSEEXPECTED, PUBLICATIONERRORTYPEEXPECTED, responseMessage);
            } catch (Exception ex) {
                setVariable("CONTINUEPUBLICATION", false);
                processError(ex.getMessage());
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

            // Self Check
            if ((infoResults.getPublicationCount() > 0) && (infoResults.getSubscriptionPeriodicFrequency() > 0)) {
                long updateTime = infoResults.getSubscriptionPeriodicFrequency();  // in Seconds
                String bufferValue = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_PERIODIC_UPDATE_BUFFER_PARAMETER(),
                        TMDDSettingsImpl.getTMDD_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE());
                long bufferTime = Long.parseLong(bufferValue);  // in Seconds
                if ((Math.abs(infoResults.getMillisSinceLastPeriodicPublication() / 1000 - updateTime) <= bufferTime)) {
                    processError("The periodic update time " + infoResults.getMillisSinceLastPeriodicPublication() / 1000 + "s exceeds the required " + updateTime + "s (including a " + bufferTime + " s buffer).");
                }
            }

            // Verify that this dialog does not violate any defined TMDD operations.
            if(!PUBLICATIONERRORRESPONSEEXPECTED.equalsIgnoreCase("true"))infoResults.checkTMDDOperationResults();          

            if (PUBLICATIONCOUNT > infoResults.getPublicationCount()) {
                setVariable("CONTINUEPUBLICATION", true);
            }
            System.out.println("PublicationOCTag:: PUBLICATIONCOUNT = " + PUBLICATIONCOUNT + " PubCount = " + infoResults.getPublicationCount() + " CONTINUEPUBLICATION = " + getVariable("CONTINUEPUBLICATION"));

        } catch (Exception ex) {
            ex.printStackTrace();
            setVariable("CONTINUEPUBLICATION", false);
            throw new JameleonScriptException("Error with publication-oc: '" + ex.getMessage(), this);
        }

    }
}
