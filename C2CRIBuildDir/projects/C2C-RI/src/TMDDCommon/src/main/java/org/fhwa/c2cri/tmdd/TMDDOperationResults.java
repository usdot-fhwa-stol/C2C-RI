/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.fhwa.c2cri.applayer.ApplicationLayerOperationResults;
import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ApplicationLayerOperationResults;
import org.fhwa.c2cri.testmodel.verification.MessageResults;
import org.fhwa.c2cri.tmdd.dbase.TMDDConnectionPool;
import org.fhwa.c2cri.tmdd.interfaces.ntcip2306.NTCIP2306XMLValidator;

/**
 * The Class TMDDOperationResults represents the result of a TMDD dialog operation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TMDDOperationResults implements InformationLayerOperationResults {

    /** The app layer results. */
    private ApplicationLayerOperationResults appLayerResults;
    
    /** The dialog. */
    private String dialog;
    
    /** The operation errors list. */
    private ArrayList<String> operationErrorsList = new ArrayList();
    
    /** The request message errors list. */
    private ArrayList<String> requestMessageErrorsList = new ArrayList();
    
    /** The response message errors list. */
    private ArrayList<String> responseMessageErrorsList = new ArrayList();
    
    /** The dialog name. */
    private String dialogName = "";
    
    /** The request message name. */
    private String requestMessageName = "";
    
    /** The response message name. */
    private String responseMessageName = "";
    
    /** The error message name. */
    private String errorMessageName = "";
    
    /** The dialog type. */
    private String dialogType = "";
    
    /** Flag indicating that the dialog is defined within the TMDD Standard. */
    private boolean dialogExistsInTMDD = false;

    /** Flag indicating that message request name is valid for the TMDD Standard defined dialog. */
    private boolean requestNameValid = false;

    /** Flag indicating that message request message is valid per the TMDD Standard defined schema. */
    private boolean requestMessageValid = false;

    /** Flag indicating that message response name is valid for the TMDD Standard defined dialog. */
    private boolean responseNameValid = false;

    /** Flag indicating that message response message is valid per the TMDD Standard defined schema. */
    private boolean responseMessageValid = false;
     
    /** The name of the request message received */
    private String receivedRequestMessageName = "";

    /** The name of the response message received */
    private String receivedResponseMessageName = "";
    
    /**
     * Instantiates a new tMDD dv303 operation results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param dialog the dialog
     * @param appLayerResults the app layer results
     */
    public TMDDOperationResults(String dialog, ApplicationLayerOperationResults appLayerResults) {
        this.appLayerResults = appLayerResults;
        this.dialog = dialog;
        updateDialogParameters();
    }

    private TMDDOperationResults(){
    }
    /**
     * Update dialog parameters.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void updateDialogParameters() {
        Connection conn = null;
        Statement stmt = null;

        try {
            // Create a SQLite connection
            conn = TMDDConnectionPool.getConnection();
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM TMDDDialogs where dialog = '" + this.dialog + "'");

            String dialogName = "";
            requestMessageName = "";
            responseMessageName = "";
            errorMessageName = "";
            dialogType = "";
            while (rs.next()) {
                dialogName = rs.getString("dialog");
                requestMessageName = rs.getString("input");
                responseMessageName = rs.getString("output");
                errorMessageName = rs.getString("error");
                dialogType = rs.getString("dialogType");
            }
            rs.close();
            stmt.close();
//            conn.close();
            TMDDConnectionPool.releaseConnection(conn);

            /** Indicate that we were able to find the dialog in TMDD */
            if (!dialogName.isEmpty()){
                this.dialogExistsInTMDD = true;
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Unable to update the parameters for the dialog because reading the TMDD Database failed: \n"
                    + ex.getMessage(),
                    "TMDD",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Process ntcip2306 dialog results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param ntcip2306Results the ntcip2306 results
     * @param messageType the message type
     * @param dialogResults the dialog results
     * @return the dialog results
     */
    private DialogResults processNTCIP2306DialogResults(NTCIP2306ApplicationLayerOperationResults ntcip2306Results,
            Message.MESSAGETYPE messageType,
            DialogResults dialogResults) {

        Message messageRequested = null;

        if (messageType.equals(Message.MESSAGETYPE.Request)) {
            messageRequested = ntcip2306Results.getRequestMessage();
        } else if (messageType.equals(Message.MESSAGETYPE.Response)) {
            messageRequested = ntcip2306Results.getResponseMessage();
        }

        NTCIP2306XMLValidator xmlValidator = NTCIP2306XMLValidator.getInstance();
        boolean schemaValidated = false;
        if (messageRequested.getMessageBodyParts().size() == 1) {
            schemaValidated = xmlValidator.isXMLValidatedToSchema(messageRequested.getMessageBodyParts().get(0));
        } else if (messageRequested.getMessageBodyParts().size() >= 2) {
            schemaValidated = xmlValidator.isXMLValidatedToSchema(messageRequested.getMessageBodyParts().get(1));
        }
        
        boolean schemaMessageValuesVerified = (xmlValidator.getSchemaValidationValueErrorList().isEmpty());
        boolean schemaMessageContentVerified = (xmlValidator.getSchemaValidationContentErrorList().isEmpty());
        
        if (messageType.equals(Message.MESSAGETYPE.Request)){
            if(!messageRequested.isSkipApplicationLayerEncoding()||!messageRequested.getEncodedMessageType().equals("Envelope"))this.requestMessageValid = schemaMessageValuesVerified&&schemaMessageContentVerified;
            System.out.println("TMDDOperationResults::processNTCIPDialogResults Request Message skipEncodingFlag="+messageRequested.isSkipApplicationLayerEncoding()+" encodedMessageType="+messageRequested.getEncodedMessageType() +" messageEncoding="+ messageRequested.getMessageEncoding());
            System.out.println("TMDDOperationResults::processNTCIPDialogResults Request Message Valid? "+this.requestMessageValid+" messageValuesVerified="+schemaMessageValuesVerified+" messageContentVerified="+schemaMessageContentVerified);
            if(!this.requestMessageValid)System.out.println("TMDDOperationResults::processNTCIPDialogResults validationErrors = " + xmlValidator.getSchemaValidationErrors());
        } else if (messageType.equals(Message.MESSAGETYPE.Response)){
            if(!messageRequested.isSkipApplicationLayerEncoding())this.responseMessageValid = schemaMessageValuesVerified&&schemaMessageContentVerified;                                
            System.out.println("TMDDOperationResults::processNTCIPDialogResults Response Message Valid? "+this.responseMessageValid+" messageValuesVerified="+schemaMessageValuesVerified+" messageContentVerified="+schemaMessageContentVerified);
            if(!this.responseMessageValid)System.out.println("TMDDOperationResults::processNTCIPDialogResults validationErrors = " + xmlValidator.getSchemaValidationErrors());
        }
        
        System.out.println("isFieldValueOK? "+xmlValidator.isFieldValidationOK());
        dialogResults.setMessageFieldsVerified(xmlValidator.isFieldValidationOK()&&schemaMessageContentVerified);
        ArrayList<String> combinedFieldErrorList = xmlValidator.getFieldValidationErrors();
        combinedFieldErrorList.addAll(xmlValidator.getSchemaValidationContentErrorList());
        dialogResults.setMessageFieldsVerifiedErrors(combinedFieldErrorList);
        System.out.println("isValueValidationOK? "+xmlValidator.isValueValidationOK());
        dialogResults.setMessageValuesVerified(xmlValidator.isValueValidationOK()&&schemaMessageValuesVerified);
        ArrayList<String> combinedValueErrorList = xmlValidator.getValueValidationErrors();
        combinedFieldErrorList.addAll(xmlValidator.getSchemaValidationValueErrorList());
        dialogResults.setMessageValuesVerifiedErrors(combinedValueErrorList);
        dialogResults.setNumberOfMessagePartsReceived(messageRequested.getMessageBodyParts().size());

        ArrayList<String> combinedOtherErrorList = new ArrayList<String>();
        combinedOtherErrorList.addAll(xmlValidator.getParserValidationErrors());
        combinedOtherErrorList.addAll(xmlValidator.getSchemaValidationOtherErrorList());        
         for (String thisOtherError : xmlValidator.getSchemaValidationOtherErrorList()){
            if(thisOtherError.contains(":Envelope")||thisOtherError.toUpperCase().contains("SOAP")){
                combinedOtherErrorList.add("See NTCIP 2306 Section 4.2 for SOAP Encoding requirements.");
            }
        }
       dialogResults.setOtherErrors(combinedOtherErrorList);
        dialogResults.setOtherErrorsEncountered(!combinedOtherErrorList.isEmpty());
        System.out.println("This is the newer TMDDOperationResults Code!  Combined Other Errors Size = "+combinedOtherErrorList.size()+"\n SchemaFieldsVerified = "+schemaMessageValuesVerified + " schemaContentVerified = "+schemaMessageContentVerified);
        dialogResults.setParsingErrors(xmlValidator.getParserValidationErrors());
        dialogResults.setParsingErrorsEncountered(!xmlValidator.isparserValidationOK());
        dialogResults.setOneMessageReceived(true);

        isValidTMDDOperation(messageType);
        boolean oneMessageReceived = false;
        if (messageType.equals(Message.MESSAGETYPE.Request)) {
            if (dialog.endsWith("Request")) {
                oneMessageReceived = messageRequested.getMessageBodyParts().size() == 1 ? true : false;
            } else {
                oneMessageReceived = messageRequested.getMessageBodyParts().size() == 2 ? true : false;
            }
            dialogResults.setWrongMessageTypeErrors(requestMessageErrorsList);
            dialogResults.setWrongMessageTypeReceived(requestMessageErrorsList.size() > 0 ? true : false);
            dialogResults.setMessageReceivedErrors(requestMessageErrorsList);

        } else {  // Response or Error Message
            oneMessageReceived = messageRequested.getMessageBodyParts().size() == 1 ? true : false;
            dialogResults.setWrongMessageTypeErrors(responseMessageErrorsList);
            dialogResults.setWrongMessageTypeReceived(responseMessageErrorsList.size() > 0 ? true : false);
            dialogResults.setMessageReceivedErrors(responseMessageErrorsList);
        }
        dialogResults.setOneMessageReceived(oneMessageReceived);

        System.out.println("This is the newer TMDDOperationResults Code!  Returning messageFieldsVerified = "+dialogResults.isMessageFieldsVerified() + " messageValuesVerified = "+dialogResults.isMessageValuesVerified());        
        System.out.println(Thread.currentThread().getStackTrace().toString());
        
        return dialogResults;
    }

    /**
     * Gets the message results.
     *
     * @param messageType the message type
     * @return the message results
     */
    @Override
    public MessageResults getMessageResults(Message.MESSAGETYPE messageType) {
        TMDDMessageTester tmddTester = TMDDMessageTester.getInstance();
        MessageResults results = null;
        if (messageType.equals(Message.MESSAGETYPE.Request)) {
            int index = appLayerResults.getRequestMessage().getMessageBodyParts().size() - 1;
            try {
                results = tmddTester.testMessage(appLayerResults.getRequestMessage().getMessageBodyParts().get(index));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            int index = appLayerResults.getResponseMessage().getMessageBodyParts().size() - 1;
            try {
                results = tmddTester.testMessage(appLayerResults.getResponseMessage().getMessageBodyParts().get(index));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return results;
    }

    /**
     * Gets the total operation delay.
     *
     * @return the total operation delay
     */
    @Override
    public long getTotalOperationDelay() {
        if ((appLayerResults.getRequestMessage() != null) && (appLayerResults.getResponseMessage() != null)) {
            return appLayerResults.getResponseMessage().getTransportTimeInMillis() - appLayerResults.getRequestMessage().getTransportTimeInMillis();
        } else {
            return -1;
        }
    }

    /**
     * Checks if is valid tmdd operation.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid tmdd operation
     */
    @Override
    public boolean isValidTMDDOperation() {
        System.out.println("TMDDOperationResults::isValidTMDDOperation Running the old one  TMDD!!!");
        boolean results = true;
        this.operationErrorsList.clear();
        this.requestMessageErrorsList.clear();
        this.responseMessageErrorsList.clear();

        if (!dialogName.equals(this.dialog)) {
            results = false;
            operationErrorsList.add("No TMDD dialog exists with name " + this.dialog + ".");
        }

        NTCIP2306XMLValidator xmlValidator = NTCIP2306XMLValidator.getInstance();
        String TMDDNS = "http://www.tmdd.org/303/messages";
        String c2cNS = "http://www.ntcip.org/c2c-message-administration";
//            String requestMessageNS = "";
        String requestMessageNamePresent = "";
        String requestMessageNSPresent = "";

        if (appLayerResults.getRequestMessage().getMessageBodyParts().size() == 0) {
            operationErrorsList.add("No Request Message was received for dialog " + this.dialog + ".");
            return results;
        }

        try {
            // Verify the Request Message
            if (dialogType.equals("RR")) {
                xmlValidator.isXMLValidatedToSchema(appLayerResults.getRequestMessage().getMessageBodyParts().get(0));
                requestMessageNamePresent = xmlValidator.getMessageTypeName();
                requestMessageNSPresent = xmlValidator.getMessageTypeNameSpace();
            } else {
                if (appLayerResults.getRequestMessage().getMessageBodyParts().size() > 1) {
                    xmlValidator.isXMLValidatedToSchema(appLayerResults.getRequestMessage().getMessageBodyParts().get(1));
                    requestMessageNamePresent = xmlValidator.getMessageTypeName();
                    requestMessageNSPresent = xmlValidator.getMessageTypeNameSpace();
                }
            }

            this.requestNameValid = true;
            this.receivedRequestMessageName = requestMessageNSPresent+":"+requestMessageNamePresent;
            if (!requestMessageName.equals(requestMessageNamePresent)) {
                results = false;
                operationErrorsList.add(requestMessageNamePresent + " is not a valid request message for TMDD dialog " + this.dialog + ".");
                requestMessageErrorsList.add(requestMessageNamePresent + " is not a valid request message for TMDD dialog " + this.dialog + ".");
                this.requestNameValid = false;
            }

            if (!TMDDNS.equals(requestMessageNSPresent)) {
                results = false;
                operationErrorsList.add(requestMessageNSPresent + " is not the correct request message namespace for TMDD dialog " + this.dialog + ".");
                requestMessageErrorsList.add(requestMessageNSPresent + " is not the correct request message namespace for TMDD dialog " + this.dialog + ".");
                if(!c2cNS.equals(requestMessageNSPresent))this.requestNameValid = false;
            }


            // Verify the Response Message
            String responseMessageNamePresent;
            String responseMessageNSPresent;

            if (appLayerResults.getResponseMessage().getMessageBodyParts().size() == 0) {
                operationErrorsList.add("No Response Message was received for dialog " + this.dialog + ".");
                return results;
            }

            xmlValidator.isXMLValidatedToSchema(appLayerResults.getResponseMessage().getMessageBodyParts().get(0));
            responseMessageNamePresent = xmlValidator.getMessageTypeName();
            responseMessageNSPresent = xmlValidator.getMessageTypeNameSpace();

            this.responseNameValid = true;
            this.receivedResponseMessageName = responseMessageNSPresent+":"+responseMessageNamePresent;
            if ((!responseMessageName.equals(responseMessageNamePresent)) && (!errorMessageName.equals(responseMessageNamePresent))) {
                results = false;
                operationErrorsList.add(responseMessageNamePresent + " is not a valid response message for TMDD dialog " + this.dialog + ".");
                responseMessageErrorsList.add(responseMessageNamePresent + " is not a valid response message for TMDD dialog " + this.dialog + ".");
                this.responseNameValid = false;
            }

            if (!TMDDNS.equals(responseMessageNSPresent)) {
                results = false;
                System.err.println("TMDDOperationResults::isValidTMDDOperation  NameSpaceError: Field \n"+  xmlValidator.getFieldValidationErrors()+
                        " \nValue "+xmlValidator.getValueValidationErrors()+" \nParser "+xmlValidator.getParserValidationErrors());
                operationErrorsList.add(responseMessageNSPresent + " is not the correct response message namespace for TMDD dialog " + this.dialog + ".");
                responseMessageErrorsList.add(responseMessageNSPresent + " is not the correct response message namespace for TMDD dialog " + this.dialog + ".");
                if(!c2cNS.equals(responseMessageNSPresent))this.responseNameValid = false;
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
            results = false;
        }

        return results;
    }

/**
 * 
 * @param messageType
 * @return 
 */
    public boolean isValidTMDDOperation(Message.MESSAGETYPE messageType) {
        System.out.println("TMDDOperationResults::isValidTMDDOperation Running the new one  TMDD!!!");
        boolean results = true;
        this.operationErrorsList.clear();
        this.requestMessageErrorsList.clear();
        this.responseMessageErrorsList.clear();

        if (!dialogName.equals(this.dialog)) {
            results = false;
            operationErrorsList.add("No TMDD dialog exists with name " + this.dialog + ".");
        }

        NTCIP2306XMLValidator xmlValidator = NTCIP2306XMLValidator.getInstance();
        String TMDDNS = "http://www.tmdd.org/303/messages";
        String c2cNS = "http://www.ntcip.org/c2c-message-administration";

//            String requestMessageNS = "";
        String requestMessageNamePresent = "";
        String requestMessageNSPresent = "";

        if (appLayerResults.getRequestMessage().getMessageBodyParts().size() == 0) {
            operationErrorsList.add("No Request Message was received for dialog " + this.dialog + ".");
            return results;
        }

        try {
            if (messageType.equals(Message.MESSAGETYPE.Request)) {

                // Verify the Request Message
                if (dialogType.equals("RR")) {
                    if(appLayerResults.getRequestMessage().isSkipApplicationLayerEncoding()){
                       try{
                           xmlValidator.isXMLValidatedToSchema(xmlValidator.getSOAPMessagePart(appLayerResults.getRequestMessage().getMessageBody(), 1));
                           requestMessageNamePresent = xmlValidator.getSOAPBodyMessageName(1);
                           requestMessageNSPresent = xmlValidator.getSOAPBodyMessageNameSpace(1);                        
                       } catch (Exception ex){
                           ex.printStackTrace();
                           requestMessageNamePresent = "";
                           requestMessageNSPresent = "";                                                  
                       }
                    } else {
                       xmlValidator.isXMLValidatedToSchema(appLayerResults.getRequestMessage().getMessageBodyParts().get(0));
                       requestMessageNamePresent = xmlValidator.getMessageTypeName();
                       requestMessageNSPresent = xmlValidator.getMessageTypeNameSpace();                        
                    }
                } else if (appLayerResults.getRequestMessage().getMessageBodyParts().size() > 1) {
                    if(appLayerResults.getRequestMessage().isSkipApplicationLayerEncoding()){
                       try{
                           xmlValidator.isXMLValidatedToSchema(xmlValidator.getSOAPMessagePart(appLayerResults.getRequestMessage().getMessageBody(), 2));
                           requestMessageNamePresent = xmlValidator.getSOAPBodyMessageName(2);
                           requestMessageNSPresent = xmlValidator.getSOAPBodyMessageNameSpace(2);                        
                       } catch (Exception ex){
                           ex.printStackTrace();
                           requestMessageNamePresent = "";
                           requestMessageNSPresent = "";                                                  
                       }
                    } else {
                        xmlValidator.isXMLValidatedToSchema(appLayerResults.getRequestMessage().getMessageBodyParts().get(1));
                        requestMessageNamePresent = xmlValidator.getMessageTypeName();
                        requestMessageNSPresent = xmlValidator.getMessageTypeNameSpace();                        
                    }
                }

                this.requestNameValid = true;
                this.receivedRequestMessageName = requestMessageNSPresent + ":" + requestMessageNamePresent;
                System.out.println("TMDDOperationResults::isValidTMDDOperationNew received name = "+this.receivedRequestMessageName + " and expected name = "+requestMessageName);
                if (!requestMessageName.equals(requestMessageNamePresent)) {
                    results = false;
                    operationErrorsList.add(requestMessageNamePresent + " is not a valid request message for TMDD dialog " + this.dialog + ".");
                    requestMessageErrorsList.add(requestMessageNamePresent + " is not a valid request message for TMDD dialog " + this.dialog + ".");
                    this.requestNameValid = false;
                }

                if (!TMDDNS.equals(requestMessageNSPresent)) {
                    results = false;
                    operationErrorsList.add(requestMessageNSPresent + " is not the correct request message namespace for TMDD dialog " + this.dialog + ".");
                    requestMessageErrorsList.add(requestMessageNSPresent + " is not the correct request message namespace for TMDD dialog " + this.dialog + ".");
                    if (!c2cNS.equals(requestMessageNSPresent)) {
                        this.requestNameValid = false;
                    }
                }

            } else if (messageType.equals(Message.MESSAGETYPE.Response)) {
                // Verify the Response Message
                String responseMessageNamePresent;
                String responseMessageNSPresent;

                if (appLayerResults.getResponseMessage().getMessageBodyParts().size() == 0) {
                    operationErrorsList.add("No Response Message was received for dialog " + this.dialog + ".");
                    return results;
                }

                if(appLayerResults.getResponseMessage().isSkipApplicationLayerEncoding()){
                       try{
                           xmlValidator.isXMLValidatedToSchema(xmlValidator.getSOAPMessagePart(appLayerResults.getResponseMessage().getMessageBody(), 1));
                           responseMessageNamePresent = xmlValidator.getSOAPBodyMessageName(1);
                           responseMessageNSPresent = xmlValidator.getSOAPBodyMessageNameSpace(1);                        
                       } catch (Exception ex){
                           ex.printStackTrace();
                           responseMessageNamePresent = "";
                           responseMessageNSPresent = "";                                                  
                       }
                } else {
                    xmlValidator.isXMLValidatedToSchema(appLayerResults.getResponseMessage().getMessageBodyParts().get(0));
                    responseMessageNamePresent = xmlValidator.getMessageTypeName();
                    responseMessageNSPresent = xmlValidator.getMessageTypeNameSpace();                    
                }

                this.responseNameValid = true;
                this.receivedResponseMessageName = responseMessageNSPresent + ":" + responseMessageNamePresent;
                if ((!responseMessageName.equals(responseMessageNamePresent)) && (!errorMessageName.equals(responseMessageNamePresent))) {
                    results = false;
                    operationErrorsList.add(responseMessageNamePresent + " is not a valid response message for TMDD dialog " + this.dialog + ".");
                    responseMessageErrorsList.add(responseMessageNamePresent + " is not a valid response message for TMDD dialog " + this.dialog + ".");
                    this.responseNameValid = false;
                }

                if (!TMDDNS.equals(responseMessageNSPresent)) {
                    results = false;
                    System.err.println("TMDDOperationResults::isValidTMDDOperation  NameSpaceError: Field \n" + xmlValidator.getFieldValidationErrors()
                            + " \nValue " + xmlValidator.getValueValidationErrors() + " \nParser " + xmlValidator.getParserValidationErrors());
                    operationErrorsList.add(responseMessageNSPresent + " is not the correct response message namespace for TMDD dialog " + this.dialog + ".");
                    responseMessageErrorsList.add(responseMessageNSPresent + " is not the correct response message namespace for TMDD dialog " + this.dialog + ".");
                    if (!c2cNS.equals(responseMessageNSPresent)) {
                        this.responseNameValid = false;
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            results = false;
        }

        return results;
    }

    /**
     * Gets the tMDD operation errors.
     *
     * @return the tMDD operation errors
     */
    @Override
    public ArrayList<String> getTMDDOperationErrors() {
        return this.operationErrorsList;
    }

    /**
     * Checks if is valid tmdd operation or extension.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid tmdd extension
     */
    @Override
    public boolean isValidTMDDExtension() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void checkTMDDOperationResults() throws Exception {
        System.out.println("TMDDOperationResults::checkTMDDOperationResults Dialog Exists? "+this.dialogExistsInTMDD);
        if (this.dialogExistsInTMDD){
            System.out.println("TMDDOperationResults::checkTMDDOperationResults requestNameValid="+this.requestNameValid + " receivedRequestMessageName="+this.receivedRequestMessageName);
            if (!this.requestNameValid){
                if (this.receivedRequestMessageName.isEmpty())getDialogResults(Message.MESSAGETYPE.Request);
                if (!this.requestNameValid)
               throw new Exception("The request message "+this.receivedRequestMessageName+" was not valid for the TMDD defined dialog.");
            }
            System.out.println("TMDDOperationResults::checkTMDDOperationResults requestMessageValid="+this.requestMessageValid);
            if (!receivedRequestMessageName.isEmpty() && !this.requestMessageValid ){
                throw new Exception ("The request message "+this.receivedRequestMessageName+" was not valid per the TMDD schema.");
            }

            System.out.println("TMDDOperationResults::checkTMDDOperationResults responseNameValid="+this.responseNameValid + " receivedResponseMessageName="+this.receivedResponseMessageName);
            if (!this.responseNameValid){
                if (this.receivedResponseMessageName.isEmpty())getDialogResults(Message.MESSAGETYPE.Response);
                if (!this.responseNameValid)
                throw new Exception("The response message "+this.receivedResponseMessageName+" was not valid for the TMDD defined dialog.");
            }
            System.out.println("TMDDOperationResults::checkTMDDOperationResults responseMessageValid="+this.responseMessageValid);
            if (!receivedResponseMessageName.isEmpty() && !this.responseMessageValid ){
                throw new Exception ("The response message "+this.receivedResponseMessageName+" was not valid per the TMDD schema.");
            }

        }  // if the dialog is not defined in TMDD then application layer tests will verify this.        
    }

    /**
     * Gets the tMDD extension errors.
     *
     * @return the tMDD extension errors
     */
    @Override
    public ArrayList<String> getTMDDExtensionErrors() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Gets the dialog results.
     *
     * @param messageType the message type
     * @return the dialog results
     */
    @Override
    public DialogResults getDialogResults(Message.MESSAGETYPE messageType) {
        DialogResults results = new DialogResults();

        if (appLayerResults instanceof NTCIP2306ApplicationLayerOperationResults) {
            results = processNTCIP2306DialogResults((NTCIP2306ApplicationLayerOperationResults) appLayerResults,
                    messageType, results);
        System.out.println("getDialogResults:  This is how the tag should print out the errors: "+results.getOtherErrors());
        for (String thisOtherError : results.getOtherErrors()){
            System.out.println("getDialogResults Other Error -> "+thisOtherError);
        }
             
        }
        return results;
    }

    /**
     * Checks if is subscription active.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is subscription active
     */
    @Override
    public boolean isSubscriptionActive() {
        return this.appLayerResults.isSubscriptionActive();
    }

    /**
     * Checks if is publication complete.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is publication complete
     */
    @Override
    public boolean isPublicationComplete() {
        return this.appLayerResults.isPublicationComplete();
    }

    /**
     * Gets the publication count.
     *
     * @return the publication count
     */
    @Override
    public long getPublicationCount() {
        return this.appLayerResults.getPublicationCount();
    }

    /**
     * Gets the millis since last periodic publication.
     *
     * @return the millis since last periodic publication
     */
    @Override
    public long getMillisSinceLastPeriodicPublication() {
        return this.appLayerResults.getMillisSinceLastPeriodicPublication();
    }
    
    /**
     * Gets the subscription periodic frequency.
     *
     * @return the subscription periodic frequency
     */
    @Override
    public long getSubscriptionPeriodicFrequency(){
        return this.appLayerResults.getSubscriptionPeriodicFrequency();
    }

    /**
     * Gets the request message.
     *
     * @return the request message
     */
    @Override
    public Message getRequestMessage() {
        return this.appLayerResults.getRequestMessage();
    }

    /**
     * Gets the response message.
     *
     * @return the response message
     */
    @Override
    public Message getResponseMessage() {
        return this.appLayerResults.getResponseMessage();
    }
}
