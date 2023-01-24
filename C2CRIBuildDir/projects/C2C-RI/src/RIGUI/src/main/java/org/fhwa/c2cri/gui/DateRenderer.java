/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.gui;

import java.text.DateFormat;

/**
 * The Class DateRenderer formats the date into a predetermined format.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
class DateRenderer extends TextAreaRenderer {
    
    /** The formatter. */
    DateFormat formatter;
    
    /**
     * Instantiates a new date renderer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public DateRenderer() { super(); }

    /* (non-Javadoc)
     * @see javax.swing.table.DefaultTableCellRenderer#setValue(java.lang.Object)
     */
    public void setValue(Object value) {
        if (formatter==null) {
            formatter = DateFormat.getDateInstance();
        }
        setText((value == null) ? "" : formatter.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(value));
    }
}
