/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.messaging;

import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;

/**
 * The Interface ResponseQueueController which handles the queuing of Response Messages that are part of a defined operation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface ResponseQueueController {
    
    /**
     * Adds the to ext response queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param em the em
     */
    public void addToExtResponseQueue(OperationIdentifier operation, NTCIP2306Message em);
    
    /**
     * Gets the message from ext response queue.
     *
     * @param operation the operation
     * @return the message from ext response queue
     */
    public NTCIP2306Message getMessageFromExtResponseQueue(OperationIdentifier operation);
    
    /**
     * Gets the message from ext response queue.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the message from ext response queue
     */
    public NTCIP2306Message getMessageFromExtResponseQueue(OperationIdentifier operation, int maxWaitInMillis);
    
    /**
     * Gets the message from ext response queue with interrupt.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the message from ext response queue with interrupt
     * @throws InterruptedException the interrupted exception
     */
    public NTCIP2306Message getMessageFromExtResponseQueueWithInterrupt(OperationIdentifier operation, int maxWaitInMillis) throws InterruptedException;
    
    /**
     * Checks if the message is available in response queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @return true, if is available in response queue
     */
    public boolean isAvaliableInResponseQueue(OperationIdentifier operation);
}
