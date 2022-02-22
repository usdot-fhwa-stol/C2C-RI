/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.operations;


/**
 * The Class OperationResultsMonitor provides notification to an observer when an Operation Result occurs.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class OperationResultsMonitor {

    /** The this monitor. */
    private static OperationResultsMonitor thisMonitor;
    
    /** The this observer. */
    private static OperationResultsObserver thisObserver = null;
    
    
    /**
     * Instantiates a new operation results monitor.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private OperationResultsMonitor(){    
    }
    
    /**
     * Gets the single instance of OperationResultsMonitor.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of OperationResultsMonitor
     */
    public static OperationResultsMonitor getInstance(){
        if (thisMonitor == null){
            thisMonitor = new OperationResultsMonitor();
        }
        return thisMonitor;
    }
    
    /**
     * Adds the operation result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param results the results
     */
    public void addOperationResult(OperationResults results){
        if (thisObserver != null){
            thisObserver.notify(results);
        }
    }
    
    /**
     * Register operation results observer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param observer the observer
     */
    public void registerOperationResultsObserver(OperationResultsObserver observer){
        thisObserver = observer;
    }
    
    /**
     * Unregister operation results observer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param observer the observer
     */
    public void unregisterOperationResultsObserver(OperationResultsObserver observer){
        thisObserver = null;
    }
    
}
