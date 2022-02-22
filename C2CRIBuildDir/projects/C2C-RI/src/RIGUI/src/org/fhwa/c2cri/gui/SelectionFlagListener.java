/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.gui;

/**
 * The listener interface for receiving selectionFlag events.
 * The class that is interested in processing a selectionFlag
 * event implements this interface. When
 * the selectionFlag event occurs, that object's appropriate
 * method is invoked.
 *
 * @see SelectionFlagEvent
 */
public interface SelectionFlagListener {
    
    /**
     * Flag value cleared update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param tableRow the table row
     */
    public void flagValueClearedUpdate(int tableRow);
    
    /**
     * Flag value set update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param tableRow the table row
     */
    public void flagValueSetUpdate(int tableRow);
}
