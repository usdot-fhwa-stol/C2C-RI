/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import org.fhwa.c2cri.utilities.ProgressReporter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;

/**
 * ProgressListener listens to "progress" property
 * changes in the SwingWorkers that perform actions.
 *
 * @see ProgressEvent
 */
public class ProgressListener implements PropertyChangeListener {

    /**
     * Instantiates a new progress listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private ProgressListener() {
    }
    
    /** The progress reporter. */
    private ProgressReporter progressReporter;

    /**
     * Instantiates a new progress listener.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param progressReporter the progress reporter
     */
    ProgressListener(ProgressReporter progressReporter) {
        this.progressReporter = progressReporter;
    }

    /* (non-Javadoc)
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        String strPropertyName = evt.getPropertyName();
        if ("progress".equals(strPropertyName)) {
            String progressValue = (String) evt.getNewValue();
            progressReporter.update(progressValue);
            System.out.println("Progress Listener received a progress update" + progressValue);
        } else if ("state".equals(strPropertyName)) {
            if ((SwingWorker.StateValue) evt.getNewValue() == SwingWorker.StateValue.STARTED) {
                progressReporter.start();
            } else if ((SwingWorker.StateValue) evt.getNewValue() == SwingWorker.StateValue.DONE) {
                progressReporter.done();

            }
        } 
    }
}


