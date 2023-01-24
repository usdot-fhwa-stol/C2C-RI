/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.utilities;


/**
 * The Interface ProgressReporter supplies methods to report progress to the progress monitor.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/3/2012
 */
public interface ProgressReporter {
    
    /**
     * Start.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void start();
    
    /**
     * Update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param updateText the update text
     */
    public void update(String updateText);
    
    /**
     * Done.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void done();
}
