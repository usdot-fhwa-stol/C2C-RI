/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.util.HashMap;
import javax.swing.SwingWorker;
import org.fhwa.c2cri.reporter.RIReports;
import org.fhwa.c2cri.utilities.ProgressMonitor;

/**
 * The Class LogReportCreateAction creates log file reports based on user input.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class LogReportCreateAction extends SwingWorker<String, String> {

    /** The input parameters. */
    private HashMap inputParameters;

    /**
     * Instantiates a new log report create action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private LogReportCreateAction() {
    }

    /**
     * Instantiates a new log report create action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inputParameters the input parameters
     */
    public LogReportCreateAction(HashMap inputParameters) {
        this.inputParameters = inputParameters;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected String doInBackground() throws Exception {

        ProgressMonitor monitor = ProgressMonitor.getInstance(
                null,
                "C2C RI",
                "Creating the requested Report ...",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();

        this.publish("Creating the reqested Report ...");
        try{
        RIReports newReport = new RIReports();
        if (newReport.createLogReport(inputParameters)){
            performPostAction();
            this.publish("Completed creating the reqested Report ...");            
        }
        newReport = null;
        } catch (Exception ex){
            javax.swing.JOptionPane.showMessageDialog(null,
            "The Log File Report creation action encountered error: "+ex.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.err.println("The Log File Report creation action encountered error: "+ex.getMessage());
            
        } catch (Error ex){
            javax.swing.JOptionPane.showMessageDialog(null,
            "The Log File Report creation action encountered error: "+ex.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.err.println("The Log File Report creation action encountered error: "+ex.getMessage());
            ex.printStackTrace();
            
        }
        monitor.dispose();
        return ("Report Complete");
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
