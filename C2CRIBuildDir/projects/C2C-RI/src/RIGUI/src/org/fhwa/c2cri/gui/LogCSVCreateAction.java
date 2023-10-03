/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.util.HashMap;
import javax.swing.SwingWorker;
import org.fhwa.c2cri.reporter.RIReports;

/**
 * The Class LogCSVCreateAction creates a CSV file from the log file outside of the event thread.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class LogCSVCreateAction extends SwingWorker<String, String> {

    /** The input parameters. */
    private HashMap inputParameters;

    /**
     * Instantiates a new log csv create action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private LogCSVCreateAction() {
    }

    /**
     * Instantiates a new log csv create action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inputParameters the input parameters
     */
    public LogCSVCreateAction(HashMap inputParameters) {
        this.inputParameters = inputParameters;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected String doInBackground() throws Exception {

        this.publish("Creating the reqested CSV File ...");
        RIReports newReport = new RIReports();
        if (newReport.createLogExport(inputParameters)){
            performPostAction();
            this.publish("Completed creating the reqested CSV File ...");
        }
        newReport = null;
        return ("CSV File Complete");
    }

    /**
     * Perform post action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void performPostAction(){     
		// original implementation was empty
    }
}
