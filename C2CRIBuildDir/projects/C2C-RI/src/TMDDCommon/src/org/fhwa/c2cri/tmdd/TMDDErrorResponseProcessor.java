/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

import java.util.ArrayList;
import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;

/**
 * The Class TMDDErrorResponseProcessor generates error responses as defined within TMDD and inspects error response messages received from other centers.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TMDDErrorResponseProcessor {

    /** The error text. */
    private String errorText = "";
    
    /** The error type. */
    private String errorType = "0";
    
    /** The error type text. */
    private String errorTypeText = "";
    
    /** The restrictions. */
    private static String RESTRICTIONS = "tmdd:errorReportMsg.restrictions.organization-information-forwarding-restrictions = 1";
    
    /** The organizationinformation. */
    private static String ORGANIZATIONINFORMATION = "tmdd:errorReportMsg.organization-information.organization-id = C2C RI";
    
    /** The organizationrequesting. */
    private static String ORGANIZATIONREQUESTING = "tmdd:errorReportMsg.organization-requesting.organization-id = SUT";
    
    /** The errorreportcode. */
    private static String ERRORREPORTCODE = "tmdd:errorReportMsg.error-code = #ERRORCODE#";
    
    /** The errorreporttext. */
    private static String ERRORREPORTTEXT = "tmdd:errorReportMsg.error-text = #ERRORTEXT#";

    /**
     * Request contains error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialogResults the dialog results
     * @param authenticationCheck the authentication check
     * @param allowAccess the allow access
     * @param errorResponseExpected the error response expected
     * @param errorTypeExpected the error type expected
     * @param dataAvailable the data available
     * @param userName the user name
     * @param password the password
     * @param organizationId the organization id
     * @param testCaseID the test case id
     * @param requestMessage the request message
     * @return true, if successful
     */
    public boolean requestContainsError(DialogResults dialogResults, String authenticationCheck, String allowAccess,
           String errorResponseExpected, String errorTypeExpected, String dataAvailable, String userName, String password, String organizationId, String testCaseID, Message requestMessage) {

        return requestContainsError(dialogResults, authenticationCheck, allowAccess,
           errorResponseExpected, errorTypeExpected, dataAvailable, userName, password, organizationId, testCaseID, requestMessage, false);

    }
    
    
    /**
     * Request contains error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialogResults the dialog results
     * @param authenticationCheck the authentication check
     * @param allowAccess the allow access
     * @param errorResponseExpected the error response expected
     * @param errorTypeExpected the error type expected
     * @param dataAvailable the data available
     * @param userName the user name
     * @param password the password
     * @param operatorId the operator id
     * @param testCaseID the test case id
     * @param requestMessage the request message
     * @param periodicUpdateError the periodic update error
     * @return true, if successful
     */
    public boolean requestContainsError(DialogResults dialogResults, String authenticationCheck, String allowAccess,
           String errorResponseExpected, String errorTypeExpected, String dataAvailable, String userName, String password, String operatorId, String testCaseID, Message requestMessage,
           boolean periodicUpdateError) {

        System.out.println("RequestContainsError?  FieldResults = "+dialogResults.isMessageFieldsVerified());
        System.out.println("RequestContainsError?  ValuesResults = "+dialogResults.isMessageValuesVerified());
        System.out.println("RequestContainsError?  ParsingResults = "+dialogResults.isParsingErrorsEncountered());

        if (errorResponseExpected.equalsIgnoreCase("true")&&(errorTypeExpected.equals("1")||errorTypeExpected.equals("unknown processing error"))) {
            errorText = "The receiving center encountered an unknown processing error. (Based on Test Case Input Data)" + dialogResults.getOtherErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "1";
            errorTypeText = "unknown processing error";
            return true;
        }

        // If message not well formed and can't be parsed.
        if (dialogResults.isParsingErrorsEncountered()) {
            errorText = "The message received contained parsing errors. " + dialogResults.getOtherErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "4";
            errorTypeText = "message is not well formed or cannot be parsed";
            return true;
        }

        // If the fields contained within the RESPONSE satisfy the information layer standard design then continue
        if (!dialogResults.isMessageFieldsVerified()) {
            errorText = "The fields contained within the REQUEST do not match the TMDD Design. " + dialogResults.getMessageFieldsVerifiedErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "3";
            errorTypeText = "missing information prevents processing message";
            return true;
        }


        // If the value of each field contained within the RESPONSE satisfy the information layer standard design then continue
        if (!dialogResults.isMessageValuesVerified()) {
            errorText = "The value of each field contained within the REQUEST do not match the TMDD Design." + dialogResults.getMessageValuesVerifiedErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "5";
            errorTypeText = "out of range values";
            return true;
        }


        // If the SUT responded with one message per the app layer standard rules then continue
        if (!dialogResults.isOneMessageReceived()) {
            // Send an appropriate error response and throw Exception
            errorText = "The SUT responded with more than one message. " + dialogResults.getMessageReceivedErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "2";
            errorTypeText = "center does not support this type message";
            return true;
        }

        // If the SUT sent the wrong message type then note error and return
        if (dialogResults.isWrongMessageTypeReceived()) {
            // Send an appropriate error response and throw Exception
            errorText = "The SUT sent a message type not supported by this service. " + dialogResults.getWrongMessageTypeErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "2";
            errorTypeText = "center does not support this type message";
            return true;
        }


        if (!allowAccess.equalsIgnoreCase("true")) {
            errorText = "The Allow Access flag for test case " + testCaseID + " was set to false.";
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "6";
            errorTypeText = "permission not granted for request";
            return true;
        }

        if (authenticationCheck.equalsIgnoreCase("true")) {
            try {
                TMDDAuthenticationProcessor.processMessageAuthentication(userName, password, operatorId, requestMessage);
            } catch (Exception ex) {
                errorText = ex.getMessage();
                if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
                errorType = "7";
                errorTypeText = "authentication not recognized";
                return true;
            }
        }

        if (!dataAvailable.equalsIgnoreCase("true")) {
            errorText = "The DataAvailable Access flag for test case " + testCaseID + " was set to false.";
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "8";
            errorTypeText = "no valid data available";
            return true;
        }

        // If an unknown processing error was encountered
        if (dialogResults.isOtherErrorsEncountered()) {
            errorText = "The receiving center encountered an unknown processing error. " + dialogResults.getOtherErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "1";
            errorTypeText = "unknown processing error";
            return true;
        }

        // If the SUT responded with one message per the app layer standard rules then continue
        if (periodicUpdateError){
            // Send an appropriate error response and throw Exception
            errorText = "The OC does not support periodic updates with the requested frequency.";
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "2";
            errorTypeText = "center does not support this type message";
            return true;            
            
        }
        return false;
    }

    /**
     * Request contains error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialogResults the dialog results
     * @param authenticationCheck the authentication check
     * @param allowAccess the allow access
     * @param errorResponseExpected the error response expected
     * @param errorTypeExpected the error type expected
     * @param dataAvailable the data available
     * @param userName the user name
     * @param password the password
     * @param operatorId the operator id
     * @param testCaseID the test case id
     * @param requestMessage the request message
     * @param periodicUpdateError the periodic update error
     * @return true, if successful
     */
    public boolean requestContainsError(DialogResults dialogResults) {

        System.out.println("RequestContainsError?  FieldResults = "+dialogResults.isMessageFieldsVerified());
        System.out.println("RequestContainsError?  ValuesResults = "+dialogResults.isMessageValuesVerified());
        System.out.println("RequestContainsError?  ParsingResults = "+dialogResults.isParsingErrorsEncountered());

        // If message not well formed and can't be parsed.
        if (dialogResults.isParsingErrorsEncountered()) {
            errorText = "The message received contained parsing errors. " + dialogResults.getOtherErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "4";
            errorTypeText = "message is not well formed or cannot be parsed";
            return true;
        }

        // If the fields contained within the RESPONSE satisfy the information layer standard design then continue
        if (!dialogResults.isMessageFieldsVerified()) {
            errorText = "The fields contained within the REQUEST do not match the TMDD Design. " + dialogResults.getMessageFieldsVerifiedErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "3";
            errorTypeText = "missing information prevents processing message";
            return true;
        }


        // If the value of each field contained within the RESPONSE satisfy the information layer standard design then continue
        if (!dialogResults.isMessageValuesVerified()) {
            errorText = "The value of each field contained within the REQUEST do not match the TMDD Design." + dialogResults.getMessageValuesVerifiedErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "5";
            errorTypeText = "out of range values";
            return true;
        }


        // If the SUT responded with one message per the app layer standard rules then continue
        if (!dialogResults.isOneMessageReceived()) {
            // Send an appropriate error response and throw Exception
            errorText = "The SUT responded with more than one message. " + dialogResults.getMessageReceivedErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "2";
            errorTypeText = "center does not support this type message";
            return true;
        }

        // If the SUT sent the wrong message type then note error and return
        if (dialogResults.isWrongMessageTypeReceived()) {
            // Send an appropriate error response and throw Exception
            errorText = "The SUT sent a message type not supported by this service. " + dialogResults.getWrongMessageTypeErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "2";
            errorTypeText = "center does not support this type message";
            return true;
        }

        // If an unknown processing error was encountered
        if (dialogResults.isOtherErrorsEncountered()) {
            errorText = "The receiving center encountered an unknown processing error. " + dialogResults.getOtherErrors();
            if (errorText.length()> 255)errorText = errorText.substring(0, 249)+"...";
            errorType = "1";
            errorTypeText = "unknown processing error";
            return true;
        }

        return false;
    }
        
    
    /**
     * Gets the error response msg.
     *
     * @param messageName the message name
     * @return the error response msg
     */
    public Message getErrorResponseMsg(String messageName) {
        MessageManager theManager = MessageManager.getInstance();
        Message newMessage = theManager.createMessage(messageName);

        ArrayList<String> errorMessage = new ArrayList<String>();
        String errorCodeLine = ERRORREPORTCODE.replace("#ERRORCODE#", errorTypeText);
        String errorTextLine = ERRORREPORTTEXT.replace("#ERRORTEXT#", errorText);

        errorMessage.add(RESTRICTIONS);
        errorMessage.add(ORGANIZATIONINFORMATION);
        errorMessage.add(ORGANIZATIONREQUESTING);
        errorMessage.add(errorCodeLine);
        errorMessage.add(errorTextLine);

        MessageSpecification theMessageSpec = new MessageSpecification(errorMessage);

        newMessage.setMessageSpecification(theMessageSpec);

        return newMessage;
    }

    /**
     * Gets the error response msg.
     *
     * @param messageName the message name
     * @param errorCode the error code
     * @param errorText the error text
     * @return the error response msg
     */
    public Message getErrorResponseMsg(String messageName, String errorCode, String errorText) {
        MessageManager theManager = MessageManager.getInstance();
        Message newMessage = theManager.createMessage(messageName);

        ArrayList<String> errorMessage = new ArrayList<String>();
        String errorCodeLine = ERRORREPORTCODE.replace("#ERRORCODE#", errorCode);
        String errorTextLine = ERRORREPORTTEXT.replace("#ERRORTEXT#", errorText);

        errorMessage.add(RESTRICTIONS);
        errorMessage.add(ORGANIZATIONINFORMATION);
        errorMessage.add(ORGANIZATIONREQUESTING);
        errorMessage.add(errorCodeLine);
        errorMessage.add(errorTextLine);

        MessageSpecification theMessageSpec = new MessageSpecification(errorMessage);

        newMessage.setMessageSpecification(theMessageSpec);

        return newMessage;
    }

    
    /**
     * Gets the error response Message Specification.
     *
     * @param messageName the message name
     * @param errorCode the error code
     * @param errorText the error text
     * @return the error response msg
     */
    public MessageSpecification getErrorResponseMsgSpec(String messageName, String errorCode, String errorText) {

        ArrayList<String> errorMessage = new ArrayList<String>();
        String errorCodeLine = ERRORREPORTCODE.replace("#ERRORCODE#", errorCode);
        if (errorText==null){
            errorText = "Null Data Error Encountered";
        }
        String errorTextLine = ERRORREPORTTEXT.replace("#ERRORTEXT#", errorText);

        errorMessage.add(RESTRICTIONS);
        errorMessage.add(ORGANIZATIONINFORMATION);
        errorMessage.add(ORGANIZATIONREQUESTING);
        errorMessage.add(errorCodeLine);
        errorMessage.add(errorTextLine);

        MessageSpecification theMessageSpec = new MessageSpecification(errorMessage);

 
        return theMessageSpec;
    }
    
    
    /**
     * Gets the error text.
     *
     * @return the error text
     */
    public String getErrorText() {
        return errorText;
    }

    /**
     * Gets the error type.
     *
     * @return the error type
     */
    public String getErrorType() {
        return errorType;
    }

    /**
     * Gets the error type text.
     *
     * @return the error type text
     */
    public String getErrorTypeText() {
        return errorTypeText;
    }


    /**
     * Process error response message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param errorResponseExpected the error response expected
     * @param errorTypeExpected the error type expected
     * @param responseMessage the response message
     * @throws Exception the exception
     */
    public static void processErrorResponseMessage(String errorResponseExpected, String errorTypeExpected, Message responseMessage) throws Exception {
        if (errorResponseExpected.equalsIgnoreCase("true")) {
            if (responseMessage.getMessageSpecification().containsMessageOfType("tmdd:errorReportMsg")) {
                String errorCode = responseMessage.getMessageSpecification().getElementValue("tmdd:errorReportMsg.error-code");
                if (errorTypeExpected.equals("1") || (errorTypeExpected.equals("unknown processing error"))) {
                    if (!errorCode.equals("1") && (!errorCode.equals("unknown processing error"))) {
                        throw new Exception("Error type returned: " + errorCode + " does not match error type expected: " + errorTypeExpected);
                    }
                } else if (errorTypeExpected.equals("2") || (errorTypeExpected.equals("center does not support this type message"))) {
                    if (!errorCode.equals("2") && (!errorCode.equals("center does not support this type message"))) {
                        throw new Exception("Error type returned: " + errorCode + " does not match error type expected: " + errorTypeExpected);
                    }

                } else if (errorTypeExpected.equals("3") || (errorTypeExpected.equals("missing information prevents processing message"))) {
                    if (!errorCode.equals("3") && (!errorCode.equals("missing information prevents processing message"))) {
                        throw new Exception("Error type returned: " + errorCode + " does not match error type expected: " + errorTypeExpected);
                    }

                } else if (errorTypeExpected.equals("4") || (errorTypeExpected.equals("message is not well formed or cannot be parsed"))) {
                    if (!errorCode.equals("4") && (!errorCode.equals("message is not well formed or cannot be parsed"))) {
                        throw new Exception("Error type returned: " + errorCode + " does not match error type expected: " + errorTypeExpected);
                    }

                } else if (errorTypeExpected.equals("5") || (errorTypeExpected.equals("out of range values"))) {
                    if (!errorCode.equals("5") && (!errorCode.equals("out of range values"))) {
                        throw new Exception("Error type returned: " + errorCode + " does not match error type expected: " + errorTypeExpected);
                    }

                } else if (errorTypeExpected.equals("6") || (errorTypeExpected.equals("permission not granted for request"))) {
                    if (!errorCode.equals("6") && (!errorCode.equals("permission not granted for request"))) {
                        throw new Exception("Error type returned: " + errorCode + " does not match error type expected: " + errorTypeExpected);
                    }

                } else if (errorTypeExpected.equals("7") || (errorTypeExpected.equals("authentication not recognized"))) {
                    if (!errorCode.equals("7") && (!errorCode.equals("authentication not recognized"))) {
                        throw new Exception("Error type returned: " + errorCode + " does not match error type expected: " + errorTypeExpected);
                    }

                } else if (errorTypeExpected.equals("8") || (errorTypeExpected.equals("no valid data available"))) {
                    if (!errorCode.equals("8") && (!errorCode.equals("no valid data available"))) {
                        throw new Exception("Error type returned: " + errorCode + " does not match error type expected: " + errorTypeExpected);
                    }

                } else {
                    throw new Exception("ErrorTypeExpected Parameter is not valid for TMDD. Error Code received = " + errorCode);
                }

            } else {
                throw new Exception("An errorReportMsg response was expected but was NOT received.");
            }
        } else if (responseMessage.getMessageSpecification().containsMessageOfType("tmdd:errorReportMsg")) {
                throw new Exception("An errorReportMsg response was NOT expected, but was received.");
            
        } else if (responseMessage.getEncodedMessageType().equals("errorMessage")){
                throw new Exception("An error was encountered processing the request.\n"+new String(responseMessage.getMessageBody(),"UTF-8"));                
        }

    }
}
