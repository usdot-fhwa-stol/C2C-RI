/**
 * 
 */
package test;

import java.util.ArrayList;
import java.util.List;
import net.sf.jasperreports.engine.JasperCompileManager;

/**
 * This class creates jasper files from the provided C2C RI report jrxml files.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class CompileRIReports {

    
        public static void main(String[] args) throws Exception {

            String reportDirectory = "C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\reports";
            List<String> reportList = new ArrayList<>();
            reportList.add("ConformanceReport");
            reportList.add("1201ConformanceReport");
            reportList.add("c2cRIReport");
            reportList.add("MessageDetailReport");
            reportList.add("MessageSummaryReport");
            reportList.add("TestCaseDetailReport");
            reportList.add("TestCaseMain");
            reportList.add("TestCaseSummaryReport");
            reportList.add("TestConfigurationReport");
            reportList.add("TestProcedureMain");
            reportList.add("TestScriptActionLogReport");
            reportList.add("TestScriptsReport");
            
            
            for (String report : reportList){
                String reportSource = reportDirectory + "\\" + report;
                String reportFormat = reportSource + ".jrxml";
                JasperCompileManager.compileReportToFile(reportFormat, reportSource+".jasper");                    
            }

        }
        
}
