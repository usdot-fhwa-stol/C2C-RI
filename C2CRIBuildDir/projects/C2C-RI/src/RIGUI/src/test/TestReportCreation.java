/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;
import org.fhwa.c2cri.reporter.RIReports;

/**
 * The Class TestReportCreation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestReportCreation {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        HashMap inputParameters = new HashMap();

        inputParameters.put("SUBREPORT_DIR", "C:\\projects\\Release2\\projects\\C2C-RI\\src\\RIGUI\\.\\reports\\");
        inputParameters.put("dataSource", "C:\\c2cri\\TryMe.2011-10-10_15-48-25.xml");
        inputParameters.put("reportSource", RIReports.TestCaseDetails_Logreport_Source);
        inputParameters.put("reportDest", "C:\\c2cri\\TryMe.pdf");

        RIReports newReport = new RIReports();
        newReport.createLogReport(inputParameters);

    }
}
