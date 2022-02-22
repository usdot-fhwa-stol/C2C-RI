/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

/**
 * The listener interface for receiving testStatus events.
 * The class that is interested in processing a testStatus
 * event implements this interface. When
 * the testStatus event occurs, that object's appropriate
 * method is invoked.
 *
 * @see TestStatusEvent
 */
public interface TestStatusListener {
    
    /**
     * Update status.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param status the status
     */
    public void updateStatus(String status);
    
    /**
     * Test complete.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testComplete();
}
