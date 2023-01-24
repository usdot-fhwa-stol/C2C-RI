/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.messaging;

import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;

/**
 * The Interface for a RequestQueueController which handles the queuing of Request Messages that are part of a defined operation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface RequestQueueController {
    
    /**
     * Adds the to ext request queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param em the em
     */
    public void addToExtRequestQueue(OperationIdentifier operation, NTCIP2306Message em);
    
    /**
     * Gets the message from ext request queue.
     *
     * @param operation the operation
     * @return the message from ext request queue
     */
    public NTCIP2306Message getMessageFromExtRequestQueue(OperationIdentifier operation);
    
    /**
     * Gets the message from ext request queue.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the message from ext request queue
     */
    public NTCIP2306Message getMessageFromExtRequestQueue(OperationIdentifier operation, int maxWaitInMillis);
    
    /**
     * Gets the message from ext request queue with interrupt.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the message from ext request queue with interrupt
     * @throws InterruptedException the interrupted exception
     */
    public NTCIP2306Message getMessageFromExtRequestQueueWithInterrupt(OperationIdentifier operation, int maxWaitInMillis) throws InterruptedException;

    /**
     * Gets the message from ext request queue with interrupt.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @param waitMaxTime wait the maximum wait time before returning.  This should allow time for a scripted operation to register before the request is processed.
     * @return the message from ext request queue with interrupt
     * @throws InterruptedException the interrupted exception
     */
    public NTCIP2306Message getMessageFromExtRequestQueueWithInterruptAndWait(OperationIdentifier operation, int maxWaitInMillis, boolean waitMaxTime) throws InterruptedException;
    
    /**
     * Checks if is avaliable in request queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @return true, if is avaliable in request queue
     */
    public boolean isAvaliableInRequestQueue(OperationIdentifier operation);
}
