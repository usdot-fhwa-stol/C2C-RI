/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.gui;

/**
 * The listener interface for receiving reports events.
 * The class that is interested in processing a reports
 * event implements this interface. When
 * the reports event occurs, that object's appropriate
 * method is invoked.
 *
 * @see ReportsEvent
 */
public interface ReportsListener {

    /**
     *  Method called when the user has completed working with RI Reports.
     */
    public void reportsCompleted();
}
