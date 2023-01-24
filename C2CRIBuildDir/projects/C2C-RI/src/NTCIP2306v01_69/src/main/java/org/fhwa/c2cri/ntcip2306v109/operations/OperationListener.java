/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.operations;

import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;


/**
 * The listener interface for receiving operation events.
 * The class that is interested in processing a operation
 * event implements this interface. When
 * the operation event occurs, that object's appropriate
 * method is invoked.
 *
 * @see OperationEvent
 */
public interface OperationListener {
    
    /**
     * External request update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operationId the operation id
     * @param em the em
     */
    public void externalRequestUpdate(OperationIdentifier operationId, NTCIP2306Message em);
    
    /**
     * External response update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operationId the operation id
     * @param em the em
     */
    public void externalResponseUpdate(OperationIdentifier operationId, NTCIP2306Message em);
    
    /**
     * Operation results update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operationId the operation id
     * @param opResults the op results
     */
    public void OperationResultsUpdate(OperationIdentifier operationId, OperationResults opResults);

}
