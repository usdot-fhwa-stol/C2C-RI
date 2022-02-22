/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.gui;

/**
 * The listener interface for receiving testExecution events.
 * The class that is interested in processing a testExecution
 * event implements this interface. When
 * the testExecution event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TestExecutionEvent
 */
public interface TestExecutionListener {

    /**
     * Test execution started.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testExecutionStarted();

    /**
     * Test execution completed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testExecutionCompleted();
}
