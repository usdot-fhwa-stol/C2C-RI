/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.gui;

/**
 * The listener interface for receiving testDefinition events.
 * The class that is interested in processing a testDefinition
 * event implements this interface. When
 * the testDefinition event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TestDefinitionEvent
 */
public interface TestDefinitionListener {

    /**
     * Test definition completed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testDefinitionCompleted();
}
