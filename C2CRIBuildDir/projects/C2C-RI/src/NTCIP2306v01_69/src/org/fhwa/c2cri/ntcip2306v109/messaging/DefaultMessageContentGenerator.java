/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.messaging;

import org.fhwa.c2cri.applayer.ApplicationLayerOperationResults;
import org.fhwa.c2cri.applayer.MessageContentGenerator;
import org.fhwa.c2cri.applayer.MessageUpdateListener;
import org.fhwa.c2cri.infolayer.MessageProvider;
import org.fhwa.c2cri.messagemanager.Message;



/**
 * The Class DefaultMessageContentGenerator.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class DefaultMessageContentGenerator implements MessageContentGenerator{

    /** The this generator. */
    private static DefaultMessageContentGenerator thisGenerator;
    
    /** The external generator. */
    private static MessageContentGenerator externalGenerator;
    
    /**
     * Instantiates a new default message content generator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private DefaultMessageContentGenerator(){        
    }
    
    
    
    /**
     * Gets the single instance of DefaultMessageContentGenerator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of DefaultMessageContentGenerator
     */
    public static MessageContentGenerator getInstance(){
        if (thisGenerator == null){
            thisGenerator = new DefaultMessageContentGenerator();
        }
        return thisGenerator;
    }
    
    /**
     * Sets the message content generator.
     *
     * @param msgGenerator the new message content generator
     * @throws Exception the exception
     */
    public static void setMessageContentGenerator(MessageContentGenerator msgGenerator) throws Exception{
        for (String standardSupported: msgGenerator.getApplicationStandardsSupported()){
            if (standardSupported.equalsIgnoreCase("NTCIP 2306v1")||
                standardSupported.equalsIgnoreCase("NTCIP 2306 v1.69")){
                    externalGenerator = msgGenerator;
            } else {
                throw new Exception("MessageContentGenerator supplied does not support NTCIP 2306 v1.69.");
            }
        }
    }
    
    /**
     * Gets the application standards supported.
     *
     * @return the application standards supported
     */
    @Override
    public String[] getApplicationStandardsSupported() {
        String[] result = {"NTCIP 2306v1","NTCIP 2306 v1.69"};
        return result;
    }

    /**
     * Checks if is message supported.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param nameSpace the name space
     * @param messageName the message name
     * @return true, if is message supported
     */
    @Override
    public boolean isMessageSupported(String operation, String nameSpace, String messageName, MessageProvider messageProvider) {
        if (externalGenerator != null){
            if (externalGenerator.isMessageSupported(operation, nameSpace, messageName, messageProvider)){
                return true;
            } else {
                return false;
            }
        }
        return true;
    }


    /**
     * Gets the message.
     *
     * @param operation the operation
     * @param nameSpace the name space
     * @param messageName the message name
     * @return the message
     * @throws Exception the exception
     */
    @Override
    public Message getMessage(String operation, String nameSpace, String messageName) throws Exception {
        if (externalGenerator != null){
            return externalGenerator.getMessage(operation, nameSpace, messageName);
        } 
        return  C2CRIMessageAdapter.toBasicC2CRIMessage(operation, messageName, "Unknown",("<Message xmlns=\""+nameSpace+"\">The DefaultOperationListener received a Request for "
                    + operation + " \nMessage:\n" + messageName + "</Message>").getBytes());
   
    }

    /**
     * Gets the error message.
     *
     * @param operation the operation
     * @param nameSpace the name space
     * @param errorDescription the error description
     * @return the error message
     * @throws Exception the exception
     */
    @Override
    public Message getErrorMessage(String operation, String nameSpace, String errorDescription) throws Exception {
        if (externalGenerator != null){
            return externalGenerator.getErrorMessage(operation, nameSpace, errorDescription);
        } 

        String parsedErrorText;
        if (errorDescription.length()<=250){
            parsedErrorText = errorDescription;
        } else {
            parsedErrorText = errorDescription.substring(0, 246).concat("...");
        }
        String errorTemplate="<errorReportMsg xmlns=\""+nameSpace+"\"><organization-information><organization-id>string</organization-id></organization-information><organization-requesting><organization-id>string</organization-id></organization-requesting><error-code>7</error-code><error-text>"+parsedErrorText.replace("<","&lt;").replace(">", "&gt;")+"</error-text></errorReportMsg>";

        
        return C2CRIMessageAdapter.toBasicC2CRIMessage(operation, "errorReportMsg", "RESPONSE",errorTemplate.getBytes());
    }

    /**
     * Gets the response message.
     *
     * @param operation the operation
     * @param nameSpace the name space
     * @param messageName the message name
     * @param requestMessage the request message
     * @return the response message
     */
    @Override
    public Message getResponseMessage(String operation, String nameSpace, String messageName, Message requestMessage, ApplicationLayerOperationResults opResults) {
        if (externalGenerator != null){
            return externalGenerator.getResponseMessage(operation, nameSpace, messageName, requestMessage, opResults);
        } 
        return  C2CRIMessageAdapter.toBasicC2CRIMessage(operation, messageName, "Unknown",("<Message xmlns=\""+nameSpace+"\">The DefaultOperationListener received a Request for "
                    + operation + " \nMessage:\n" + messageName + "</Message>").getBytes());
    }

    /**
     * Register message update listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param msgListener the msg listener
     */
    @Override
    public void registerMessageUpdateListener(MessageUpdateListener msgListener, String operationName) {
        if (externalGenerator != null){
            externalGenerator.registerMessageUpdateListener(msgListener, operationName);
        } 
		else
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
    }

    /**
     * Unregister message update listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param msgListener the msg listener
     */
    @Override
    public void unregisterMessageUpdateListener(MessageUpdateListener msgListener) {
        if (externalGenerator != null){
            externalGenerator.unregisterMessageUpdateListener(msgListener);
        } 
		else
		{
			throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
		}
    }

    @Override
    public void operationRelatedDataUpdate(String operationName) {
        // Only implemented in external message generators.
    }
    
    
    
}
