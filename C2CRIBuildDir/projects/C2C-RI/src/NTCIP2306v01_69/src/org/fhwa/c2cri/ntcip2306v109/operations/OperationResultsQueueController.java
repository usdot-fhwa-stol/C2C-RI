/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.operations;


/**
 * The Interface OperationResultsQueueController defines the methods that must be provided by a controller that manages an Operation Results Queue.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface OperationResultsQueueController {
    
    /**
     * Adds the to operation results queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param opResults the op results
     */
    public void addToOperationResultsQueue(OperationIdentifier operation, OperationResults opResults);
    
    /**
     * Gets the results from operation results queue.
     *
     * @param operation the operation
     * @return the results from operation results queue
     */
    public OperationResults getResultsFromOperationResultsQueue(OperationIdentifier operation);
    
    /**
     * Gets the results from operation results queue.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the results from operation results queue
     */
    public OperationResults getResultsFromOperationResultsQueue(OperationIdentifier operation, int maxWaitInMillis);
    
    /**
     * Gets the results from operation results queue with interrupt.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the results from operation results queue with interrupt
     * @throws InterruptedException the interrupted exception
     */
    public OperationResults getResultsFromOperationResultsQueueWithInterrupt(OperationIdentifier operation, int maxWaitInMillis)throws InterruptedException;
    
    /**
     * Checks if is available in operation results queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @return true, if is available in operation results queue
     */
    public boolean isAvaliableInOperationResultsQueue(OperationIdentifier operation);
}
