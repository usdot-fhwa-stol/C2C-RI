/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

import net.sf.jameleon.function.FunctionTag;

/**
 * The listener interface for receiving testAction events.
 * The class that is interested in processing a testAction
 * event implements this interface. When
 * the testAction event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TestActionEvent
 */
public interface TestActionListener {
    
    /**
     * Update actions.Pre-Conditions: N/A
     * Post-Conditions: N/A
     * 
     * @param timeStamp the time the action was performed
     * @param actions the actions
     * @param result the results of the action (if applicable)
     */
    public void updateActions(String timeStamp, String actions, String result);

    /**
     * Post condition error event.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void postConditionErrorEvent();

    /**
     * Test step update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param ft the ft
     */
    public void testStepUpdate(FunctionTag ft);
}
