/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

/**
 * The listener interface for receiving testCaseResults events.
 * The class that is interested in processing a testCaseResults
 * event implements this interface. When
 * the testCaseResults event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TestCaseResultsEvent
 */
public interface TestCaseResultsListener {
    
    /**
     * Test case update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testCaseUpdate();
    
    /**
     * Test step update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testStepUpdate();
}
