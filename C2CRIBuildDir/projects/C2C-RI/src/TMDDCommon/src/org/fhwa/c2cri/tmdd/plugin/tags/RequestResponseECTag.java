/**
 * 
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import java.util.ArrayList;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.TestStepTag;
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
 * @jameleon.function name="REQUEST-RESPONSE-EC" type="action"
 * @jameleon.step Send the message request
 */
public class RequestResponseECTag extends C2CRIFunctionTag {

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
    protected Object REQUESTMESSAGE;
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
     * The time in milliseconds within which an owner center has to send a response
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

            ArrayList<String> messageSpecArray = new ArrayList<String>();
            MessageManager theManager = MessageManager.getInstance();
            Message newMessage = theManager.createMessage(Dialog);
            if (REQUESTMESSAGE instanceof String) {
                String request = (String) REQUESTMESSAGE;

                if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                    request = request.replace("#RIMessageSpec#", "");
                    String[] requestArray = request.split("; ");
                    for (int ii = 0; ii < requestArray.length; ii++) {
                        messageSpecArray.add(requestArray[ii]);
                    }
                    MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                    newMessage.setMessageSpecification(messageSpec);

                } else {
                    newMessage.setMessageBody(((String) REQUESTMESSAGE).getBytes());
                    newMessage.setSkipApplicationLayerEncoding(true);
                }
            } else if (REQUESTMESSAGE instanceof ArrayList) {
                messageSpecArray = (ArrayList<String>) REQUESTMESSAGE;
                MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                newMessage.setMessageSpecification(messageSpec);
            }
            newMessage.setMessageName("REQUEST");
            theManager.addMessage(newMessage, true);

            InformationLayerOperationResults infoResults = theInformationLayerController.performRequestResponseEC(Dialog, newMessage);
            newMessage=infoResults.getRequestMessage();
            newMessage.setMessageName("REQUEST");
            theManager.addMessage(newMessage, true);            
            
            Message responseMessage = infoResults.getResponseMessage();
//            Message responseMessage = theApplicationLayerStandard.request_response_ec(Dialog, newMessage);
            responseMessage.setMessageName("RESPONSE");
            theManager.addMessage(responseMessage, true);
            
            DialogResults results = infoResults.getDialogResults(Message.MESSAGETYPE.Response);
//           log.info(newMessage.toXML());
//           log.info(responseMessage.toXML());

            // If the error response conditions are met then continue
            try {
                TMDDErrorResponseProcessor.processErrorResponseMessage(ERRORRESPONSEEXPECTED, ERRORTYPEEXPECTED, responseMessage);
            } catch (Exception ex) {


                throw new JameleonScriptException(ex.getMessage(), this);
            }
        System.out.println("RequestContainsError?  FieldResults = "+results.isMessageFieldsVerified());
        System.out.println("RequestContainsError?  ValuesResults = "+results.isMessageValuesVerified());
        System.out.println("RequestContainsError?  ParsingResults = "+results.isParsingErrorsEncountered());
        System.out.println("RequestContainsError?  OtherErrors = "+results.isOtherErrorsEncountered());
        System.out.println("RequestContainsError?  OtherErrors = " + results.isWrongMessageTypeReceived());
        

            // If the fields contained within the RESPONSE satisfy the information layer standard design then continue
            if (!results.isMessageFieldsVerified()) {
                processError("The fields contained within the RESPONSE do not match the TMDD Design. " + results.getMessageFieldsVerifiedErrors());
            }

            // If the value of each field contained within the RESPONSE satisfy the information layer standard design then continue
            if (!results.isMessageValuesVerified()) {
                processError("The value of each field contained within the RESPONSE does not match the TMDD Design." + results.getMessageValuesVerifiedErrors());
            }

            if (results.isOtherErrorsEncountered()){
                processError("Errors were encountered in the received message." + results.getOtherErrors());
            }

            if (results.isParsingErrorsEncountered()){
                throw new JameleonScriptException("Parsing Errors were encountered in the received message." + results.getParsingErrors(), this);
            }

            // If the SUT responded with one message per the app layer standard rules then continue
            if (!results.isOneMessageReceived()) {
                processError("The SUT responded with more than one message. " + results.getMessageReceivedErrors());
            }            
          long responseTime = infoResults.getResponseMessage().getTransportTimeInMillis() - infoResults.getRequestMessage().getTransportTimeInMillis();
          long requiredTime = Long.parseLong(RESPONSETIMEREQUIRED);
          String pathValue = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_PATH_DELAY_PARAMETER(), 
                                                        TMDDSettingsImpl.getTMDD_PATH_DELAY_DEFAULT_VALUE());
          long pathTime = Long.parseLong(pathValue);
          if ((responseTime - pathTime)>requiredTime){
            processError("The response time "+responseTime+"ms minus the path time value "+pathValue+
                    "ms is greater than the required "+requiredTime+" ms.");            
          }

        // Verify that this dialog does not violate any defined TMDD operations.
        System.out.println("RequestResponseECTag:: ERRORRESPONSEEXPECTED = "+ERRORRESPONSEEXPECTED);
        if(!ERRORRESPONSEEXPECTED.equalsIgnoreCase("TRUE"))infoResults.checkTMDDOperationResults();          
          
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JameleonScriptException("Error with request-response: '" + ex.getMessage(), this);
        }

 
    }

}
