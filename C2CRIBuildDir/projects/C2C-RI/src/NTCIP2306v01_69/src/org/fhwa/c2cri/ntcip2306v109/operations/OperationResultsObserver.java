/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.operations;


/**
 * An asynchronous update interface for receiving notifications
 * about OperationResults information as the OperationResults is constructed.
 */
public interface OperationResultsObserver {
    
    /**
     * This method is called when information about an OperationResults
     * which was previously requested using an asynchronous
     * interface becomes available.
     *
     * @param results the results
     */
    public void notify(OperationResults results);
}
