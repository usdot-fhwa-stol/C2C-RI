/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.util.HashMap;
import javax.swing.SwingWorker;
import org.fhwa.c2cri.reporter.RIReports;
import org.fhwa.c2cri.testmodel.TestConfiguration;

/**
 * The Class ReportCreateAction.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ReportCreateAction extends SwingWorker<String, String> {

    /** The input parameters. */
    private HashMap inputParameters;
    
    /** The config. */
    TestConfiguration config;
    
    /** The config file name. */
    String configFileName;
    
    /** The report format. */
    String reportFormat;

    /**
     * Instantiates a new report create action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private ReportCreateAction() {
    }

    /**
     * Instantiates a new report create action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inputParams the input params
     * @param testConfig the test config
     * @param configFileName the config file name
     * @param reportFormat the report format
     */
    ReportCreateAction(HashMap inputParams, TestConfiguration testConfig, String configFileName, String reportFormat) {
        this.inputParameters = inputParams;
        this.config = testConfig;
        this.configFileName = configFileName;
        this.reportFormat = reportFormat;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected String doInBackground() throws Exception {

        this.publish("Creating the reqested Report ...");
        try{
            RIReports newReport = new RIReports();
            if (reportFormat.equals(RIReports.All_Configreport_Source)){
               if(newReport.createConfigurationReport(inputParameters, config, configFileName))
                   performPostAction();
            } else if (reportFormat.equals(RIReports.TestCases_report_Source)){
                if (newReport.createConfigurationTestCaseReport(inputParameters, config, configFileName))
                    performPostAction();
            } else if (reportFormat.equals(RIReports.TestProcedure_report_Source)){
                if (newReport.createConfigurationTestProcedureReport(inputParameters, config, configFileName))
                    performPostAction();
            } else {
               if(newReport.createTestScriptReport(inputParameters, config, configFileName))
                   performPostAction();
            }
            newReport = null;
        } catch (Exception ex){
            javax.swing.JOptionPane.showMessageDialog(null,
            "The Configuration File Report creation action encountered error: "+ex.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.err.println("The Configuration File Report creation action encountered error: "+ex.getMessage());
            
        } catch (Error ex){
            javax.swing.JOptionPane.showMessageDialog(null,
            "The Configuration File Report creation action encountered error: "+ex.getMessage(),
            "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.err.println("The Confifguration File Report creation action encountered error: "+ex.getMessage());
            
        }
        
        this.publish("Completed creating the reqested Report ...");
        return ("Report Complete");
    }

    /**
     * Perform post action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void performPostAction(){        
    }
}
