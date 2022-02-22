/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.applayer;

import org.fhwa.c2cri.infolayer.MessageProvider;
import org.fhwa.c2cri.messagemanager.Message;


/**
 * A MessageContentGenerator is capable of creating messages based
 * on current information.  This class will be very useful when entity
 * emulation is implemented.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface MessageContentGenerator {
    
    /**
     * Gets the application standards supported.
     *
     * @return the application standards supported
     */
    public String[] getApplicationStandardsSupported();
    
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
    public boolean isMessageSupported(String operation, String nameSpace, String messageName, MessageProvider messageProvider);
    
    /**
     * Register message update listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param msgListener the msg listener
     * @param operation the operation that is being listened for.
     * 
     */
    public void registerMessageUpdateListener(MessageUpdateListener msgListener, String operation);
    
    /**
     * Unregister message update listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param msgListener the msg listener
     */
    public void unregisterMessageUpdateListener(MessageUpdateListener msgListener);
    
    /**
     * Gets the message.
     *
     * @param operation the operation
     * @param nameSpace the name space
     * @param messageName the message name
     * @return the message
     * @throws Exception the exception
     */
    public Message getMessage(String operation, String nameSpace, String messageName) throws Exception;
    
    /**
     * Gets the error message.
     *
     * @param operation the operation
     * @param nameSpace the name space
     * @param errorDescription the error description
     * @return the error message
     * @throws Exception the exception
     */
    public Message getErrorMessage(String operation, String nameSpace, String errorDescription) throws Exception;
    
    /**
     * Gets the response message.
     *
     * @param operation the operation
     * @param nameSpace the name space
     * @param messageName the message name
     * @param requestMessage the request message
     * @param operationResults the current status of application layer tests
     * @return the response message
     */
    public Message getResponseMessage(String operation, String nameSpace, String messageName, Message requestMessage, ApplicationLayerOperationResults operationResults);
    
    /**
     * Provide a notification to listeners that a data update that has occurred for the given operation.
     * @param operation 
     */
    public void operationRelatedDataUpdate(String operation);    
}
