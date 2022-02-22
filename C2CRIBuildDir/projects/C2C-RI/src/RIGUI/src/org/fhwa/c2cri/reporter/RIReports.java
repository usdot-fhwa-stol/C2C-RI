/**
 * 
 */
package org.fhwa.c2cri.reporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.engine.util.JRXmlUtils;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.logger.LogFileProcessor;
import org.fhwa.c2cri.logger.TestCaseConfiguration;
import org.fhwa.c2cri.logger.TestCaseInformation;
import org.fhwa.c2cri.logger.TestCaseProcessor;
import org.fhwa.c2cri.logger.TestProcedureConfiguration;
import org.fhwa.c2cri.logger.TestProcedureProcessor;
import org.fhwa.c2cri.testmodel.NRTM;
import org.fhwa.c2cri.testmodel.Need;
import org.fhwa.c2cri.testmodel.OtherRequirement;
import org.fhwa.c2cri.testmodel.Requirement;
import org.fhwa.c2cri.testmodel.TestCase;
import org.fhwa.c2cri.testmodel.TestCases;
import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.testmodel.TestSuites;
import org.fhwa.c2cri.utilities.Checksum;
import org.fhwa.c2cri.utilities.ProgressMonitor;
import org.fhwa.c2cri.utilities.RIParameters;
import org.w3c.dom.Document;

/**
 * This class provides the RI's interface with JasperReports and initiates/manages report generation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class RIReports {

    /** The All_ logreport_ source. */
    public static String All_Logreport_Source = "./reports/TestScriptActionLogReport.jrxml";
    
    /** The Test case details_ logreport_ source. */
    public static String TestCaseDetails_Logreport_Source = "./reports/TestCaseDetailReport.jrxml";
    
    /** The Test case summary_ logreport_ source. */
    public static String TestCaseSummary_Logreport_Source = "./reports/TestCaseSummaryReport.jrxml";
    
    /** The Conformance_ logreport_ source. */
    public static String Conformance_Logreport_Source = "./reports/ConformanceReport.jrxml";
    
    /** The Section 1201 Conformance_logreport_ source. */
    public static String Section1201_Conformance_Logreport_Source = "./reports/1201ConformanceReport.jrxml";

    /** The Msg summary_ logreport_ source. */
    public static String MsgSummary_Logreport_Source = "./reports/MessageSummaryReport.jrxml";
    
    /** The Msg detail_ logreport_ source. */
    public static String MsgDetail_Logreport_Source = "./reports/MessageDetailReport.jrxml";
    // C2C RI Test configuration report
    /** The All_ configreport_ source. */
    public static String All_Configreport_Source = "./reports/c2cRIReport.jasper";
    
    /** The All_ test scripts_ source. */
    public static String All_TestScripts_Source = "./reports/TestScriptsReport.jasper";
    
    /** The Default_ reports_ path. */
    public static String Default_Reports_Path = "./reports/";
    
    /** The C2 c_ r i_ versio n_ parameter. */
    public static String C2C_RI_VERSION_PARAMETER = "C2CRIVERSION";
    
    /** The config checksum parameter. */
    public static String CONFIG_CHECKSUM_PARAMETER = "CONFIGURATIONCHECKSUM";

        /** The All_ configreport_ source. */
    public static String TestCases_report_Source = "./reports/TestCaseMain.jasper";

        /** The All_ configreport_ source. */
    public static String TestProcedure_report_Source = "./reports/TestProcedureMain.jasper";

    /** The test target parameter. */
    public static String TESTTARGET_PARAMETER = "TESTTARGET";

    /**
     * method called when the user wants to create a Test Log Report.  The method then provides the specified test log information to Jasper Reports..
     * 
     * This method utilizes the ProgresUI to inform the user that the report is running.  After the report is complete the ProgressUI is closed.
     * 
     * Log reports will be exported as PDF files.
     *
     * @param reportParameters the report parameters
     * @return true, if successful
     */
    public boolean createLogReport(HashMap reportParameters) {

        /** method called when the user wants to create a Configuration Report.  The method then provides the specified test configuration to Jasper Reports as a data source.  The TestConfiguration class implements the JasperReports data source API.
         *
         * This method utilizes the ProgresUI to inform the user that the report is running.  After the report is complete the ProgressUI is closed.
         * @param reportParameters
         */
        String reportSource = "";
        String reportDest = "";
        String dataSource = "";
        boolean results = false;
        
        Map<String, Object> params = new HashMap<String, Object>();

//        params.put("reportTitle", "Hello Report World");
//        params.put(
//                "author", "Craig Conover");
        params.put(
                "startDate", (new java.util.Date()).toString());

        if (reportParameters.containsKey("reportDest")) {
            reportDest = (String) reportParameters.get("reportDest");
        }
        if (reportParameters.containsKey("reportSource")) {
            reportSource = (String) reportParameters.get("reportSource");
        }
        if (reportParameters.containsKey("dataSource")) {
            dataSource = (String) reportParameters.get("dataSource");
        }
        if (reportParameters.containsKey("SUBREPORT_DIR")) {
            params.put("SUBREPORT_DIR", reportParameters.get("SUBREPORT_DIR"));
        }


        LogFileProcessor logFileProcessor = new LogFileProcessor(dataSource, "./c2cRI.db3");
        try{
            logFileProcessor.writeLogFileDataToTempDB();
        } catch (Exception ex){
            ex.printStackTrace();
        }


        ProgressMonitor monitor = ProgressMonitor.getInstance(
                null,
                "C2C RI",
                "Creating the requested Report ...",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE);
        
        monitor.show();
        if ((!reportSource.equals("")) && (!reportDest.equals("") && (!dataSource.equals("")))) {
            try {
                
                File tempdb = new File("./tempOutDb.db3");

                // Create a SQLite connection
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection("jdbc:sqlite:" + tempdb.getAbsolutePath());


                params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
                params.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
                params.put(JRXPathQueryExecuterFactory.XML_LOCALE, Locale.ENGLISH);
                params.put(JRParameter.REPORT_LOCALE, Locale.US);
                params.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);

                final long compileStartTime = System.currentTimeMillis();            
                JasperReport jasperReport =
                            JasperCompileManager.compileReport(reportSource);
                final long compileEndTime = System.currentTimeMillis();
                System.out.println("Total Jasper Fill time 1: " + (compileEndTime - compileStartTime) );

                //Added to avoid out of memory error by using Jasper Report Virtualizer
                JRSwapFileVirtualizer virtualizer =  new JRSwapFileVirtualizer(2, new JRSwapFile(RIReports.Default_Reports_Path, 2048, 1024), true);
                params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
                params.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_COMPRESSED,true);
                params.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_FORCE_LINEBREAK_POLICY,true);
                final long startTime = System.currentTimeMillis();            
                    JasperPrint jasperPrint =
                            JasperFillManager.fillReport(
                            jasperReport, params,conn);
                final long endTime = System.currentTimeMillis();
                System.out.println("Total Jasper Fill time 2: " + (endTime - startTime) );

                final long printStartTime = System.currentTimeMillis();            
                JasperExportManager.exportReportToPdfFile(
                            jasperPrint, reportDest);
                final long printEndTime = System.currentTimeMillis();
                System.out.println("Total Jasper timePrint 3: " + (printEndTime - printStartTime) );

                virtualizer.cleanup();
                monitor.dispose();
                javax.swing.JOptionPane completeDialog = new javax.swing.JOptionPane();
                completeDialog.showMessageDialog(null, "Report Creation Completed. \n File saved to " + reportDest, "Report Status", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                completeDialog.getRootFrame().repaint();
                results = true;
            } catch (JRException ex) {

                javax.swing.JOptionPane.showMessageDialog(null, "Error Creating Report " + reportDest + " \n " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();

            } catch (RuntimeException ex){
                javax.swing.JOptionPane.showMessageDialog(null, "Error Creating Report " + reportDest + " \n " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();                
            } catch (Exception ex){
                javax.swing.JOptionPane.showMessageDialog(null, "Error Creating Report " + reportDest + " \n " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Error myErr){
                javax.swing.JOptionPane.showMessageDialog(null, "Error Creating Report " + reportDest + " \n " + myErr.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                myErr.printStackTrace();
                
            } finally{
                monitor.dispose();
            }
            

        }
        return results;
    }

    /**
     * method called when the user wants to create a Test Log CSV Export.  The method then provides the specified test log information to Jasper Reports..
     * 
     * This method utilizes the ProgresUI to inform the user that the report is running.  After the report is complete the ProgressUI is closed.
     * 
     * Log reports will be exported as CSV files.
     *
     * @param reportParameters the report parameters
     * @return true, if successful
     */
    public boolean createLogExport(HashMap reportParameters) {
        boolean results = false;
        String reportSource = "";
        String reportDest = "";
        String dataSource = "";

        Map<String, Object> params = new HashMap<String, Object>();


        if (reportParameters.containsKey("reportDest")) {
            reportDest = (String) reportParameters.get("reportDest");
        }
        if (reportParameters.containsKey("reportSource")) {
            reportSource = (String) reportParameters.get("reportSource");
        }
        if (reportParameters.containsKey("dataSource")) {
            dataSource = (String) reportParameters.get("dataSource");
        }
        if (reportParameters.containsKey("SUBREPORT_DIR")) {
            params.put("SUBREPORT_DIR", reportParameters.get("SUBREPORT_DIR"));
        }

        LogFileProcessor logFileProcessor = new LogFileProcessor(dataSource, "./c2cRI.db3");
        try{
            logFileProcessor.writeLogFileDataToTempDB();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        ProgressMonitor monitor = ProgressMonitor.getInstance(
                null,
                "C2C RI",
                "Creating the requested Report ...",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE);
        
        monitor.show();
        if ((!reportSource.equals("")) && (!reportDest.equals("") && (!dataSource.equals("")))) {
            try {
                File tempdb = new File("./tempOutDb.db3");

                // Create a SQLite connection
                Class.forName("org.sqlite.JDBC");
                conn = DriverManager.getConnection("jdbc:sqlite:" + tempdb.getAbsolutePath());

                Document document = JRXmlUtils.parse(JRLoader.getLocationInputStream(dataSource));
                params.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, document);
                params.put(JRXPathQueryExecuterFactory.XML_DATE_PATTERN, "yyyy-MM-dd");
                params.put(JRXPathQueryExecuterFactory.XML_NUMBER_PATTERN, "#,##0.##");
                params.put(JRXPathQueryExecuterFactory.XML_LOCALE, Locale.ENGLISH);
                params.put(JRParameter.REPORT_LOCALE, Locale.US);

                JasperReport jasperReport =
                        JasperCompileManager.compileReport(reportSource);

                //Added to avoid out of memory error by using Jasper Report Virtualizer
                JRSwapFileVirtualizer virtualizer =  new JRSwapFileVirtualizer(2, new JRSwapFile(RIReports.Default_Reports_Path, 2048, 1024), true);
                params.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
                params.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);

                JasperPrint jasperPrint =
                        JasperFillManager.fillReport(
                        jasperReport, params, conn);

                JRCsvExporter exporter = new JRCsvExporter();
                exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
                exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, reportDest);
                exporter.exportReport();
                virtualizer.cleanup();
                monitor.dispose();
                javax.swing.JOptionPane.showMessageDialog(null, "Report Creation Completed. \n File saved to " + reportDest, "Report Status", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                results = true;
            } catch (JRException ex) {

                javax.swing.JOptionPane.showMessageDialog(null, "Error Creating Report " + reportDest + " \n " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();

            } catch (Exception ex){
                javax.swing.JOptionPane.showMessageDialog(null, "Error Creating Report " + reportDest + " \n " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } finally {
                monitor.dispose();
            }
            
        }
        return results;
    }
    
    /** The conn. */
    Connection conn = null;
    
    /** The pstmt. */
    PreparedStatement pstmt = null;
    
    /** The selected test config obj. */
    TestConfiguration selectedTestConfigObj = null;

    /**
     * Generate a C2C RI test configuration Jasper report.
     *
     * @param reportParameters the report parameters
     * @param testConfig Selected TestConfiguration object
     * @param configFileName Test Configuration file name
     * @return true, if successful
     */
    public boolean createConfigurationReport(HashMap reportParameters, TestConfiguration testConfig, String configFileName) {
        boolean results = false;
        ProgressMonitor monitor = ProgressMonitor.getInstance(
                null,
                "C2C RI",
                "Creating the requested Report ...",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();


        try {
            File sourcedb = new File("./c2cRI.db3");
            File tempdb = new File("./temp.db3");

            // Make a temporary copy of C2C RI SQLite database
            FileChannel sourceCh = new FileInputStream(sourcedb).getChannel();
            FileChannel destCh = new FileOutputStream(tempdb).getChannel();
            sourceCh.transferTo(0, sourceCh.size(), destCh);
            sourceCh.close();
            destCh.close();

            // Create a SQLite connection
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + tempdb.getAbsolutePath());
            selectedTestConfigObj = testConfig;

            // Store data into the temporary database
            writeTestConfigDataToTempDB(configFileName);
            monitor.show();
            // Show the report
            File configReport = new File(All_Configreport_Source);

            String reportDest="";
            if (reportParameters.containsKey("reportDest")) {
                 reportDest = (String) reportParameters.get("reportDest");
            }


            reportParameters.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);
                Checksum cs = new Checksum();
                String checksum = "";
                try{
                   checksum = cs.getChecksum(configFileName);
                } catch (Exception ex){
                   checksum = "Error - " +ex.getMessage();
                }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(configReport);

            //Added to avoid out of memory error by using Jasper Report Virtualizer
            HashMap parameterMap = new HashMap();
            JRSwapFileVirtualizer virtualizer =  new JRSwapFileVirtualizer(2, new JRSwapFile(RIReports.Default_Reports_Path, 2048, 1024), true);
            parameterMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            parameterMap.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);
            parameterMap.put(CONFIG_CHECKSUM_PARAMETER,checksum);
            parameterMap.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_COMPRESSED,true);
            parameterMap.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_FORCE_LINEBREAK_POLICY,true);
            if (reportParameters.containsKey("SUBREPORT_DIR")) {
                parameterMap.put("SUBREPORT_DIR", reportParameters.get("SUBREPORT_DIR"));
            }
           
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, conn);

            JasperExportManager.exportReportToPdfFile(
                        jasperPrint, reportDest);
            virtualizer.cleanup();
            javax.swing.JOptionPane.showMessageDialog(null, "Report Creation Completed. \n File saved to " + reportDest, "Report Status", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            results = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Configuration Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            monitor.dispose();
        }
        return results;
    }

    /**
     * Generate a C2C RI test configuration Jasper report.
     *
     * @param reportParameters the report parameters
     * @param testConfig Selected TestConfiguration object
     * @param configFileName Test Configuration file name
     * @return true, if successful
     */
    public boolean createTestScriptReport(HashMap reportParameters, TestConfiguration testConfig, String configFileName) {
        ProgressMonitor monitor = ProgressMonitor.getInstance(
                null,
                "C2C RI",
                "Creating the requested Report ...",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();
        boolean results = false;
        try {
            File sourcedb = new File("./c2cRI.db3");
            File tempdb = new File("./temp.db3");

            // Make a temporary copy of C2C RI SQLite database
            FileChannel sourceCh = new FileInputStream(sourcedb).getChannel();
            FileChannel destCh = new FileOutputStream(tempdb).getChannel();
            sourceCh.transferTo(0, sourceCh.size(), destCh);
            sourceCh.close();
            destCh.close();

            // Create a SQLite connection
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + tempdb.getAbsolutePath());
            selectedTestConfigObj = testConfig;

            // Store data into the temporary database
            writeTestConfigDataToTempDB(configFileName);
            monitor.show();
            final long scriptStartTime = System.currentTimeMillis();
            writeTestScriptsToTempDB();
            final long scriptEndTime = System.currentTimeMillis();
            System.out.println("Total Scripts timePrint: " + (scriptEndTime - scriptStartTime) );

            // Show the report
            File scriptsReport = new File(All_TestScripts_Source);
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(scriptsReport);

            //Added to avoid out of memory error by using Jasper Report Virtualizer
            HashMap parameterMap = new HashMap();
            JRSwapFileVirtualizer virtualizer =  new JRSwapFileVirtualizer(2, new JRSwapFile(RIReports.Default_Reports_Path, 2048, 1024), true);
            parameterMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            parameterMap.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);
            parameterMap.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_COMPRESSED,true);
            parameterMap.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_FORCE_LINEBREAK_POLICY,true);
            if (reportParameters.containsKey("SUBREPORT_DIR")) {
                parameterMap.put("SUBREPORT_DIR", reportParameters.get("SUBREPORT_DIR"));
            }


            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, conn);
            String reportDest = "";
            if (reportParameters.containsKey("reportDest")) {
                reportDest = (String) reportParameters.get("reportDest");
            }
            JasperExportManager.exportReportToPdfFile(
                        jasperPrint, reportDest);
            virtualizer.cleanup();
            javax.swing.JOptionPane.showMessageDialog(null, "Report Creation Completed. \n File saved to " + reportDest, "Report Status", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            results = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Scripts Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            monitor.dispose();
        }
        return results;
    }

    
    /**
     * Generate a C2C RI test Case Jasper report.
     *
     * @param reportParameters the report parameters
     * @param testConfig Selected TestConfiguration object
     * @param configFileName Test Configuration file name
     * @return true, if successful
     */
    public boolean createConfigurationTestCaseReport(HashMap reportParameters, TestConfiguration testConfig, String configFileName) {
        boolean results = false;
        
        ProgressMonitor monitor = ProgressMonitor.getInstance(
                null,
                "C2C RI",
                "Creating the requested Report ...",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();
        
        try{
            TestCaseConfiguration tccfg = new TestCaseConfiguration();

            // Set up Application Layer data first.
            boolean predefinedTestSuite = TestSuites.getInstance().isPredefined(testConfig.getSelectedAppLayerTestSuite());
            if (predefinedTestSuite){
                String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedAppLayerTestSuite());
                tccfg.setBaseApplicationTestCaseURL(TestSuites.getInstance().getTestSuiteTestCaseDescriptionsURL(baselineTestSuite));            

            } else {
                String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedAppLayerTestSuite());
                tccfg.setBaseApplicationTestCaseURL(TestSuites.getInstance().getTestSuiteTestCaseDescriptionsURL(baselineTestSuite));            

                String extensionTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedAppLayerTestSuite());
                tccfg.setExtensionApplicationTestCaseURL(TestSuites.getInstance().getTestSuiteTestCaseDescriptionsURL(extensionTestSuite));                       
            }

            ArrayList<TestCaseInformation> tciList = new ArrayList();       
            for (TestCase tc : testConfig.getAppLayerParams().getApplicableTestCases((testConfig.getTestMode().isExternalCenterOperation()?"EC":"OC"))){
                TestCaseInformation tci = new TestCaseInformation();
                tci.setTestCaseName(tc.getName());
                tci.setRelatedTestProcedure(tc.getScriptUrlLocation().getFile().replace(".xml",""));
                tci.setBaseTestCaseDataFile(tc.getDataUrlLocation());
                if (tc.isOverriden()){              
                    tci.setExtensionTestCaseDataFile(tc.getCustomDataLocation());
                }
                tciList.add(tci);
            }
            tccfg.setApplicableAppplicationTestCases(tciList);

            // Set up Information Layer data first.
            predefinedTestSuite = TestSuites.getInstance().isPredefined(testConfig.getSelectedInfoLayerTestSuite());
            if (predefinedTestSuite){
                String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedInfoLayerTestSuite());
                tccfg.setBaseInformationTestCaseURL(TestSuites.getInstance().getTestSuiteTestCaseDescriptionsURL(baselineTestSuite));            

            } else {
                String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedInfoLayerTestSuite());
                tccfg.setBaseInformationTestCaseURL(TestSuites.getInstance().getTestSuiteTestCaseDescriptionsURL(baselineTestSuite));            

                String extensionTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedInfoLayerTestSuite());
                tccfg.setExtensionInformationTestCaseURL(TestSuites.getInstance().getTestSuiteTestCaseDescriptionsURL(extensionTestSuite));                       
            }

            ArrayList<TestCaseInformation> tciInfoList = new ArrayList();
            for (TestCase tc : testConfig.getInfoLayerParams().getApplicableTestCases((testConfig.getTestMode().isExternalCenterOperation()?"EC":"OC"))){
                TestCaseInformation tci = new TestCaseInformation();
                tci.setTestCaseName(tc.getName());
                tci.setRelatedTestProcedure(tc.getScriptUrlLocation().getFile().replace(".xml",""));
                tci.setBaseTestCaseDataFile(tc.getDataUrlLocation());
                if (tc.isOverriden()){           
                    tci.setExtensionTestCaseDataFile(tc.getCustomDataLocation());
                }
                tciInfoList.add(tci);
            }
            tccfg.setApplicableInformationTestCases(tciInfoList);

            TestCaseProcessor testCaseProcessor = new TestCaseProcessor(tccfg,"./c2cRI.db3");
          
            testCaseProcessor.writeTestCasesToTempDB();

            File tempdb = new File("./temp.db3");

            // Create a SQLite connection
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + tempdb.getAbsolutePath());
            selectedTestConfigObj = testConfig;

            monitor.show();

            writeTestConfigDataToTempDB(configFileName);
 
            // Show the report
            File testCaseReport = new File(TestCases_report_Source);

            String reportDest="";
            if (reportParameters.containsKey("reportDest")) {
                 reportDest = (String) reportParameters.get("reportDest");
            }


            reportParameters.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);
                Checksum cs = new Checksum();
                String checksum = "";
                try{
                   checksum = cs.getChecksum(configFileName);
                } catch (Exception ex){
                   checksum = "Error - " +ex.getMessage();
                }

            String testTarget = RIParameters.getInstance().getParameterValue(RIParameters.TEST_TARGET);

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(testCaseReport);

            //Added to avoid out of memory error by using Jasper Report Virtualizer
            HashMap parameterMap = new HashMap();
            JRSwapFileVirtualizer virtualizer =  new JRSwapFileVirtualizer(2, new JRSwapFile(RIReports.Default_Reports_Path, 2048, 1024), true);
            parameterMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            parameterMap.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);
            parameterMap.put(CONFIG_CHECKSUM_PARAMETER,checksum);
            parameterMap.put(TESTTARGET_PARAMETER, testTarget);
            parameterMap.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_COMPRESSED,true);
            parameterMap.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_FORCE_LINEBREAK_POLICY,true);
            if (reportParameters.containsKey("SUBREPORT_DIR")) {
                parameterMap.put("SUBREPORT_DIR", reportParameters.get("SUBREPORT_DIR"));
            }
           
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, conn);

            JasperExportManager.exportReportToPdfFile(
                        jasperPrint, reportDest);
            virtualizer.cleanup();
            javax.swing.JOptionPane.showMessageDialog(null, "Report Creation Completed. \n File saved to " + reportDest, "Report Status", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            results = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            monitor.dispose();
        }
        return results;
    }
    

    
    /**
     * Generate a C2C RI test Case Jasper report.
     *
     * @param reportParameters the report parameters
     * @param testConfig Selected TestConfiguration object
     * @param configFileName Test Configuration file name
     * @return true, if successful
     */
    public boolean createConfigurationTestProcedureReport(HashMap reportParameters, TestConfiguration testConfig, String configFileName) {
        boolean results = false;
        
        ProgressMonitor monitor = ProgressMonitor.getInstance(
                null,
                "C2C RI",
                "Creating the requested Report ...",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();
        
        try{
           
            TestProcedureConfiguration tpcfg = new TestProcedureConfiguration();

            // Set up Application Layer data first.
            boolean predefinedTestSuite = TestSuites.getInstance().isPredefined(testConfig.getSelectedAppLayerTestSuite());
            if (predefinedTestSuite){
                String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedAppLayerTestSuite());
                tpcfg.setBaseApplicationTestProcedureURL(TestSuites.getInstance().getTestSuiteTestProcedureDescriptionsURL(baselineTestSuite));            
                tpcfg.setBaseApplicationTestProcedureStepsURL(TestSuites.getInstance().getTestSuiteTestProcedureStepsURL(baselineTestSuite));            

            } else {
                String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedAppLayerTestSuite());
                tpcfg.setBaseApplicationTestProcedureURL(TestSuites.getInstance().getTestSuiteTestProcedureDescriptionsURL(baselineTestSuite));            
                tpcfg.setBaseApplicationTestProcedureStepsURL(TestSuites.getInstance().getTestSuiteTestProcedureStepsURL(baselineTestSuite));            

                String extensionTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedAppLayerTestSuite());
                tpcfg.setExtensionApplicationTestProcedureURL(TestSuites.getInstance().getTestSuiteTestProcedureDescriptionsURL(extensionTestSuite));                       
                tpcfg.setExtensionApplicationTestProcedureStepsURL(TestSuites.getInstance().getTestSuiteTestProcedureStepsURL(extensionTestSuite));            
            }

            ArrayList<TestCaseInformation> tciList = new ArrayList();
            for (TestCase tc : testConfig.getAppLayerParams().getApplicableTestCases((testConfig.getTestMode().isExternalCenterOperation()?"EC":"OC"))){
                TestCaseInformation tci = new TestCaseInformation();
                tci.setTestCaseName(tc.getName());
                int position = tc.getScriptUrlLocation().getFile().indexOf("TPS-");
                tci.setRelatedTestProcedure(tc.getScriptUrlLocation().getFile().substring(position).replace(".xml",""));
                System.out.println("TestProcedure added to list=> "+tci.getRelatedTestProcedure());
                tci.setBaseTestCaseDataFile(tc.getDataUrlLocation());
                if (tc.isOverriden()){              
                    tci.setExtensionTestCaseDataFile(tc.getCustomDataLocation());
                }
                tciList.add(tci);
            }
            tpcfg.setApplicableAppplicationTestProcedures(tciList);

            // Set up Information Layer data.
            predefinedTestSuite = TestSuites.getInstance().isPredefined(testConfig.getSelectedInfoLayerTestSuite());
            if (predefinedTestSuite){
                String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedInfoLayerTestSuite());
                tpcfg.setBaseInformationTestProcedureURL(TestSuites.getInstance().getTestSuiteTestProcedureDescriptionsURL(baselineTestSuite));            
                tpcfg.setBaseInformationTestProcedureStepsURL(TestSuites.getInstance().getTestSuiteTestProcedureStepsURL(baselineTestSuite));            

            } else {
                String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedInfoLayerTestSuite());
                tpcfg.setBaseInformationTestProcedureURL(TestSuites.getInstance().getTestSuiteTestProcedureDescriptionsURL(baselineTestSuite));            
                tpcfg.setBaseInformationTestProcedureStepsURL(TestSuites.getInstance().getTestSuiteTestProcedureStepsURL(baselineTestSuite));            

                String extensionTestSuite = TestSuites.getInstance().getBaselineTestSuite(testConfig.getSelectedInfoLayerTestSuite());
                tpcfg.setExtensionInformationTestProcedureURL(TestSuites.getInstance().getTestSuiteTestProcedureDescriptionsURL(extensionTestSuite));                       
                tpcfg.setExtensionInformationTestProcedureStepsURL(TestSuites.getInstance().getTestSuiteTestProcedureStepsURL(extensionTestSuite));            
            }

            ArrayList<TestCaseInformation> tciInfoList = new ArrayList();
            for (TestCase tc : testConfig.getInfoLayerParams().getApplicableTestCases((testConfig.getTestMode().isExternalCenterOperation()?"EC":"OC"))){
                TestCaseInformation tci = new TestCaseInformation();
                tci.setTestCaseName(tc.getName());
                int position = tc.getScriptUrlLocation().getFile().indexOf("TPS-");
                tci.setRelatedTestProcedure(tc.getScriptUrlLocation().getFile().substring(position).replace(".xml",""));
                tci.setBaseTestCaseDataFile(tc.getDataUrlLocation());
                if (tc.isOverriden()){
                    tci.setExtensionTestCaseDataFile(tc.getCustomDataLocation());
                }
               System.out.println("TestProcedure added to list=> "+tci.getRelatedTestProcedure());
               tciInfoList.add(tci);
            }
            tpcfg.setApplicableInformationTestProcedures(tciInfoList);

            TestProcedureProcessor testProcedureProcessor = new TestProcedureProcessor(tpcfg,"./c2cRI.db3");
          
            testProcedureProcessor.writeTestProceduresToTempDB();

            File tempdb = new File("./temp.db3");

            // Create a SQLite connection
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + tempdb.getAbsolutePath());
            selectedTestConfigObj = testConfig;

            monitor.show();

            writeTestConfigDataToTempDB(configFileName);

            // Show the report
            File testProcedureReport = new File(TestProcedure_report_Source);

            String reportDest="";
            if (reportParameters.containsKey("reportDest")) {
                 reportDest = (String) reportParameters.get("reportDest");
            }

            String testTarget = RIParameters.getInstance().getParameterValue(RIParameters.TEST_TARGET);

            reportParameters.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);
                Checksum cs = new Checksum();
                String checksum = "";
                try{
                   checksum = cs.getChecksum(configFileName);
                } catch (Exception ex){
                   checksum = "Error - " +ex.getMessage();
                }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(testProcedureReport);

            //Added to avoid out of memory error by using Jasper Report Virtualizer
            HashMap parameterMap = new HashMap();
            JRSwapFileVirtualizer virtualizer =  new JRSwapFileVirtualizer(2, new JRSwapFile(RIReports.Default_Reports_Path, 2048, 1024), true);
            parameterMap.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
            parameterMap.put(C2C_RI_VERSION_PARAMETER,RIParameters.RI_VERSION_NUMBER);
            parameterMap.put(CONFIG_CHECKSUM_PARAMETER,checksum);
            parameterMap.put(TESTTARGET_PARAMETER,testTarget);
            parameterMap.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_COMPRESSED,true);
            parameterMap.put(net.sf.jasperreports.engine.export.JRPdfExporterParameter.PROPERTY_FORCE_LINEBREAK_POLICY,true);
            if (reportParameters.containsKey("SUBREPORT_DIR")) {
                parameterMap.put("SUBREPORT_DIR", reportParameters.get("SUBREPORT_DIR"));
            }
           
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameterMap, conn);

            JasperExportManager.exportReportToPdfFile(
                        jasperPrint, reportDest);
            virtualizer.cleanup();
            javax.swing.JOptionPane.showMessageDialog(null, "Report Creation Completed. \n File saved to " + reportDest, "Report Status", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            results = true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Generating C2C RI Test Case Report failed: \n"
                    + ex.getMessage(),
                    "C2C RI",
                    JOptionPane.ERROR_MESSAGE);
        } finally {
            monitor.dispose();
        }
        return results;
    }
    
    
    /**
     * Store TestConfiguration data for reporting purpose.
     *
     * @param configFileName the config file name
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    void writeTestConfigDataToTempDB(String configFileName) throws SQLException, Exception {
        final long configStartTime = System.currentTimeMillis();
        writeConfigDataToTempDB(configFileName);
        writeSUTdataToTempDB();
        final long configEndTime = System.currentTimeMillis();
        System.out.println("Total Jasper timePrint: " + (configEndTime - configStartTime) );
        final long infoStartTime = System.currentTimeMillis();
        writeInfoLayerNRTMDataToTempDB();
        final long infoEndTime = System.currentTimeMillis();
        System.out.println("Total InfoLayer timePrint: " + (infoEndTime - infoStartTime) );
        final long appStartTime = System.currentTimeMillis();
        writeAppLayerNRTMDataToTempDB();
        final long appEndTime = System.currentTimeMillis();
        System.out.println("Total AppLayer timePrint: " + (appEndTime - appStartTime) );
        final long commandQueueLengthStartTime = System.currentTimeMillis();
                writeCommandQueueLength();
        final long commandQueueLengthEndTime = System.currentTimeMillis();
        System.out.println("Total CommandQueueLength timePrint: " + (commandQueueLengthEndTime - commandQueueLengthStartTime) );
        final long entityEmulationDataStartTime = System.currentTimeMillis();
                writeEntityEmulationData();
        final long entityEmulationDataEndTime = System.currentTimeMillis();
        System.out.println("Total EntityEmulationData timePrint: " + (entityEmulationDataEndTime - entityEmulationDataStartTime) );
    }

    /**
     * Write out configuration data to temporary SQLite database.
     *
     * @param configFileName the config file name
     * @throws SQLException the sQL exception
     */
    void writeConfigDataToTempDB(String configFileName) throws SQLException {
        try {
            String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);

            pstmt = conn.prepareStatement("INSERT INTO C2CRI_CONFIG ("
                    + "cfgFileName, cfgFileCreator, cfgFileCreationDate, cfgFileDesc, testSuiteName, testSuiteDesc, "
                    + "infoLayerStd, appLayerStd, conformConpliance, extCenter, ownercenter) VALUES (?,?,?,?,?,?,?,?,?,?,?)");

            int col = 1;
            File cfgFile = new File(configFileName);

            pstmt.setString(col++, configFileName);                                         //cfgFileName
            pstmt.setString(col++, selectedTestConfigObj.getConfigurationAuthor());         // cfgFileCreator
            pstmt.setString(col++, sdf.format(new Date(cfgFile.lastModified())));                              // cfgFileCreationDate
            pstmt.setString(col++, selectedTestConfigObj.getTestDescription());             // cfgFileDesc
            pstmt.setString(col++, selectedTestConfigObj.getSelectedInfoLayerTestSuite());  // testSuiteName
            pstmt.setString(col++, TestSuites.getInstance().getDescription(selectedTestConfigObj.getSelectedInfoLayerTestSuite()));      // testSuiteDesc
            pstmt.setString(col++, TestSuites.getInstance().getInfoLayerStandard(selectedTestConfigObj.getSelectedInfoLayerTestSuite())); // infoLayerStd
            pstmt.setString(col++, TestSuites.getInstance().getAppLayerStandard(selectedTestConfigObj.getSelectedAppLayerTestSuite())); // appLayerStd
            pstmt.setString(col++, selectedTestConfigObj.getSelectedInfoLayerTestSuite().startsWith("*")?"Compliance":"Conformance");        // conformConpliance (???)
            pstmt.setString(col++, selectedTestConfigObj.getTestMode().isExternalCenterOperation() ? "Yes" : "No");   // extCenter
            pstmt.setString(col++, selectedTestConfigObj.getTestMode().isOwnerCenterOperation() ? "Yes" : "No");      // ownercenter
            pstmt.addBatch();
            pstmt.executeBatch();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException ex) {
            }
        }
    }

    /**
     * Write out SUT data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     */
    private void writeSUTdataToTempDB() throws SQLException {
        try {
            // Store C2CRI_SUT table data
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_SUT ("
                    + "ipAddress, ipPort, hostName, webServiceURL, userName, password, userNameRequired, passwordRequired) VALUES (?,?,?,?,?,?,?,?)");

            int col = 1;
            pstmt.setString(col++, selectedTestConfigObj.getSutParams().getIpAddress()); //ipAddress
            pstmt.setString(col++, selectedTestConfigObj.getSutParams().getIpPort()); //ipPort
            pstmt.setString(col++, selectedTestConfigObj.getSutParams().getHostName()); //hostName
            pstmt.setString(col++, selectedTestConfigObj.getSutParams().getWebServiceURL()); //webServiceURL
            pstmt.setString(col++, selectedTestConfigObj.getSutParams().getUserName()); //userName
            pstmt.setString(col++, selectedTestConfigObj.getSutParams().getPassword()); //password
            pstmt.setString(col++, selectedTestConfigObj.getSutParams().isUserNameRequired() ? "true" : "false"); //userNameRequired
            pstmt.setString(col++, selectedTestConfigObj.getSutParams().isPasswordRequired() ? "true" : "false"); //passwordRequired
            pstmt.addBatch();
            pstmt.executeBatch();
        } finally {
            try {
                pstmt.close();
            } catch (SQLException ex) {
            }
        }
    }

    /**
     * Write out InfoLayer data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     */
    private void writeInfoLayerNRTMDataToTempDB() throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_InfoLayerNRTM ("
                + "NeedID,NeedText,NeedFlagValue,NeedFlagName,NeedType,IsNeedExtension,"
                + "RequirementID, RequirementText,RequirementType,RequirementFlagName,RequirementFlagValue,IsRequirementExtension,"
                + "OtherRequirement,OtherRequirementValue,OtherRequirementValueName) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        conn.setAutoCommit(false);
        for (Need thisNeed : selectedTestConfigObj.getInfoLayerParams().getNrtm().getUserNeeds().needs) {
            try {
                if (thisNeed.getFlagValue()) {
                    for (Requirement thisRequirement : selectedTestConfigObj.getInfoLayerParams().getNrtm().getNeedRelatedRequirements(thisNeed.getTitle())) {
                        if (thisRequirement.getFlagValue()) {

                            String otherRequirement = "";
                            String otherRequirementValue = "";
                            String otherRequirementValueName = "";
                            if (thisRequirement.getOtherRequirements().otherRequirements.size() > 0) {
                                otherRequirement = thisRequirement.getOtherRequirements().otherRequirements.get(0).getOtherRequirement();
                                otherRequirementValue = thisRequirement.getOtherRequirements().otherRequirements.get(0).getValue();
                                otherRequirementValueName = thisRequirement.getOtherRequirements().otherRequirements.get(0).getValueName();
                            }
                            // Store C2CRI_InfoLayerNeed table data
                            int col = 1;

                            pstmt.setString(col++, thisNeed.getOfficialID());
                            pstmt.setString(col++, thisNeed.getText());
                            pstmt.setString(col++, thisNeed.getFlagValue().toString());
                            pstmt.setString(col++, thisNeed.getFlagName());
                            pstmt.setString(col++, thisNeed.getType());
                            pstmt.setString(col++, thisNeed.isExtension().toString());
                            pstmt.setString(col++, thisRequirement.getOfficialID());
                            pstmt.setString(col++, thisRequirement.getText());
                            pstmt.setString(col++, thisRequirement.getType());
                            pstmt.setString(col++, thisRequirement.getFlagName());
                            pstmt.setString(col++, thisRequirement.getFlagValue().toString());
                            pstmt.setString(col++, thisRequirement.isExtension().toString());
                            pstmt.setString(col++, otherRequirement);
                            pstmt.setString(col++, otherRequirementValue);
                            pstmt.setString(col++, otherRequirementValueName);
                            pstmt.addBatch();
                        }

                    }
                }
            } finally {
            }
        }
        System.out.println("About to write out Info Layer NRTM to Database...");
        conn.setAutoCommit(true);
        pstmt.executeBatch();
        pstmt.close();
        System.out.println("About to write out Info Layer NRTM to Database...Done");


        List<TestCase> testCases = selectedTestConfigObj.getInfoLayerParams().getApplicableTestCases(selectedTestConfigObj.getTestMode().isExternalCenterOperation() ? "EC" : "OC");
                pstmt = conn.prepareStatement("INSERT INTO C2CRI_InfoLayerTestCases ("
                        + "TestCaseName,TestCaseDescription,TestCaseType,TestCaseDataURL,TestCaseCustomDataURL,TestProcedureScriptURL, IsOverriden) VALUES (?,?,?,?,?,?,?)");
        for (TestCase thisTestCase : testCases) {
            try {

                int col = 1;


                pstmt.setString(col++, thisTestCase.getName());
                pstmt.setString(col++, thisTestCase.getDescription());
                pstmt.setString(col++, thisTestCase.getType());
                pstmt.setString(col++, thisTestCase.getDataUrlLocation().toString());
                pstmt.setString(col++, thisTestCase.getCustomDataLocation());
                pstmt.setString(col++, thisTestCase.getScriptUrlLocation().toString());
                pstmt.setString(col++, thisTestCase.isOverriden() ? "true" : "false");

                pstmt.addBatch();
            } catch (Exception ex) {
                ex.printStackTrace();

            } finally {
            }

        }
        System.out.println("About to write out Info Layer TestCases to Database...");
                pstmt.executeBatch();
                pstmt.close();
        System.out.println("About to write out Info Layer TestCases to Database...Done");

    }

    /**
     * Write out AppLayer data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     */
    private void writeAppLayerNRTMDataToTempDB() throws SQLException {
                            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_AppLayerNRTM ("
                                    + "NeedID,NeedText,NeedFlagValue,NeedFlagName,NeedType,IsNeedExtension,"
                                    + "RequirementID, RequirementText,RequirementType,RequirementFlagName,RequirementFlagValue,IsRequirementExtension,"
                                    + "OtherRequirement,OtherRequirementValue,OtherRequirementValueName) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        for (Need thisNeed : selectedTestConfigObj.getAppLayerParams().getNrtm().getUserNeeds().needs) {
            try {
                if (thisNeed.getFlagValue()) {
                    for (Requirement thisRequirement : selectedTestConfigObj.getAppLayerParams().getNrtm().getNeedRelatedRequirements(thisNeed.getTitle())) {
                        if (thisRequirement.getFlagValue()) {

                            String otherRequirement = "";
                            String otherRequirementValue = "";
                            String otherRequirementValueName = "";
                            if (thisRequirement.getOtherRequirements().otherRequirements.size() > 0) {
                                otherRequirement = thisRequirement.getOtherRequirements().otherRequirements.get(0).getOtherRequirement();
                                otherRequirementValue = thisRequirement.getOtherRequirements().otherRequirements.get(0).getValue();
                                otherRequirementValueName = thisRequirement.getOtherRequirements().otherRequirements.get(0).getValueName();
                            }
                            // Store C2CRI_InfoLayerNeed table data

                            int col = 1;

                            pstmt.setString(col++, thisNeed.getOfficialID());
                            pstmt.setString(col++, thisNeed.getText());
                            pstmt.setString(col++, thisNeed.getFlagValue().toString());
                            pstmt.setString(col++, thisNeed.getFlagName());
                            pstmt.setString(col++, thisNeed.getType());
                            pstmt.setString(col++, thisNeed.isExtension().toString());
                            pstmt.setString(col++, thisRequirement.getOfficialID());
                            pstmt.setString(col++, thisRequirement.getText());
                            pstmt.setString(col++, thisRequirement.getType());
                            pstmt.setString(col++, thisRequirement.getFlagName());
                            pstmt.setString(col++, thisRequirement.getFlagValue().toString());
                            pstmt.setString(col++, thisRequirement.isExtension().toString());
                            pstmt.setString(col++, otherRequirement);
                            pstmt.setString(col++, otherRequirementValue);
                            pstmt.setString(col++, otherRequirementValueName);
                            pstmt.addBatch();
                        }

                    }
                }
            } finally {
//                try {
//                    pstmt.close();
//                } catch (SQLException ex) {
//                }
            }
        }
        pstmt.executeBatch();
        pstmt.close();

        List<TestCase> testCases = selectedTestConfigObj.getAppLayerParams().getApplicableTestCases(selectedTestConfigObj.getTestMode().isExternalCenterOperation() ? "EC" : "OC");
                pstmt = conn.prepareStatement("INSERT INTO C2CRI_AppLayerTestCases ("
                        + "TestCaseName,TestCaseDescription,TestCaseType,TestCaseDataURL,TestCaseCustomDataURL,TestProcedureScriptURL, IsOverriden) VALUES (?,?,?,?,?,?,?)");
        for (TestCase thisTestCase : testCases) {
            try {

                int col = 1;


                pstmt.setString(col++, thisTestCase.getName());
                pstmt.setString(col++, thisTestCase.getDescription());
                pstmt.setString(col++, thisTestCase.getType());
                pstmt.setString(col++, thisTestCase.getDataUrlLocation().toString());
                pstmt.setString(col++, thisTestCase.getCustomDataLocation());
                pstmt.setString(col++, thisTestCase.getScriptUrlLocation().toString());
                pstmt.setString(col++, thisTestCase.isOverriden() ? "true" : "false");

                pstmt.addBatch();
            } catch (Exception ex) {
                ex.printStackTrace();

            } finally {
//                try {
//                    pstmt.close();
//                } catch (SQLException ex) {
//                }
            }

        }
                pstmt.executeBatch();
                pstmt.close();

    }

    /**
     * Write out InfoLayer data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     */
    private void writeInfoLayerDataToTempDB() throws SQLException {
        // Gather a list of the currently selected needs and its requirements
        List<Need> selectedNeedsList = new ArrayList<Need>();
        List<Requirement> selectedReq = new ArrayList<Requirement>();
        List<OtherRequirement> selectedOtherReq = new ArrayList<OtherRequirement>();

        List<String> selectedReqStr = new ArrayList<String>();

        NRTM standardNRTM = null;

        for (Need theNeed : selectedTestConfigObj.getInfoLayerParams().getUserNeeds().needs) {
            if (theNeed.getFlagValue()) {
                selectedNeedsList.add(theNeed);
                // Gather a list of optional selected requirements associated with this need
                standardNRTM = selectedTestConfigObj.getInfoLayerParams().getNrtm();
                selectedReqStr = standardNRTM.getRequirementsList(theNeed.getOfficialID());
                for (String theRequirement : selectedReqStr) {
                    Requirement thisProjectRequirement = (Requirement) selectedTestConfigObj.getInfoLayerParams().getProjectRequirements().lh_requirementsMap.get(theRequirement);
                    selectedReq.add(thisProjectRequirement);

                }
            }
        }

        for (OtherRequirement thisOtherRequirement : selectedTestConfigObj.getInfoLayerParams().getOtherRequirements().otherRequirements) {
            selectedOtherReq.add(thisOtherRequirement);
        }

        for (OtherRequirement theOther : selectedOtherReq) {
            try {
                // Store C2CRI_InfoLayerNeed table data
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_InfoLayerOther ("
                        + "requirement, param, val) VALUES (?,?,?)");

                int col = 1;
                pstmt.setString(col++, theOther.getReqID());            // OfficialID
                pstmt.setString(col++, theOther.getOtherRequirement()); // selected
                pstmt.setString(col++, theOther.getValue());            // selected
                pstmt.addBatch();
                pstmt.executeBatch();
            } finally {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                }
            }
        }

        for (Need theNeed : selectedNeedsList) {
            try {
                // Store C2CRI_InfoLayerNeed table data
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_InfoLayerNeed ("
                        + "need, selected) VALUES (?,?)");

                int col = 1;
                pstmt.setString(col++, theNeed.getTitle());    // OfficialID
                pstmt.setString(col++, theNeed.getFlagValue() ? "Yes" : "No");               // selected
                pstmt.addBatch();
                pstmt.executeBatch();
            } finally {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                }
            }
        }

        for (Requirement theReq : selectedReq) {
            try {
                // Store C2CRI_InfoLayerNeed table data
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_InfoLayerReq ("
                        + "requirement, selected) VALUES (?,?)");

                int col = 1;
                pstmt.setString(col++, theReq.getTitle());      // OfficialID
                pstmt.setString(col++, theReq.getFlagValue() ? "Yes" : "No"); // selected

                pstmt.addBatch();
                pstmt.executeBatch();
            } finally {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    /**
     * Write out AppLayer data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     */
    private void writeAppLayerDataToTempDB() throws SQLException {
        // Gather a list of the currently selected needs and its requirements
        List<Need> selectedNeedsList = new ArrayList<Need>();
        List<Requirement> selectedReq = new ArrayList<Requirement>();
        List<OtherRequirement> selectedOtherReq = new ArrayList<OtherRequirement>();

        List<String> selectedReqStr = new ArrayList<String>();

        NRTM standardNRTM = null;

        for (Need theNeed : selectedTestConfigObj.getAppLayerParams().getUserNeeds().needs) {
            if (theNeed.getFlagValue()) {
                selectedNeedsList.add(theNeed);
                // Gather a list of optional selected requirements associated with this need
                standardNRTM = selectedTestConfigObj.getAppLayerParams().getNrtm();
                selectedReqStr = standardNRTM.getRequirementsList(theNeed.getOfficialID());
                for (String theRequirement : selectedReqStr) {
                    Requirement thisProjectRequirement = (Requirement) selectedTestConfigObj.getAppLayerParams().getProjectRequirements().lh_requirementsMap.get(theRequirement);
                    selectedReq.add(thisProjectRequirement);
                }
            }
        }

        for (OtherRequirement thisOtherRequirement : selectedTestConfigObj.getAppLayerParams().getOtherRequirements().otherRequirements) {
            selectedOtherReq.add(thisOtherRequirement);
        }

        for (OtherRequirement theOther : selectedOtherReq) {
            try {
                // Store C2CRI_InfoLayerNeed table data
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_AppLayerOther ("
                        + "requirement, param, val) VALUES (?,?,?)");

                int col = 1;
                pstmt.setString(col++, theOther.getReqID());            // OfficialID
                pstmt.setString(col++, theOther.getOtherRequirement()); // selected
                pstmt.setString(col++, theOther.getValue());            // selected
                pstmt.addBatch();
                pstmt.executeBatch();
            } finally {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                }
            }
        }

        for (Need theNeed : selectedNeedsList) {
            try {
                // Store C2CRI_InfoLayerNeed table data
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_AppLayerNeed ("
                        + "need, selected) VALUES (?,?)");

                int col = 1;
                pstmt.setString(col++, theNeed.getTitle());                     // OfficialID
                pstmt.setString(col++, theNeed.getFlagValue() ? "Yes" : "No");      // selected
                pstmt.addBatch();
                pstmt.executeBatch();
            } finally {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                }
            }
        }

        for (Requirement theReq : selectedReq) {
            try {
                // Store C2CRI_InfoLayerNeed table data
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_AppLayerReq ("
                        + "requirement, selected) VALUES (?,?)");

                int col = 1;
                pstmt.setString(col++, theReq.getTitle());                  // OfficialID
                pstmt.setString(col++, theReq.getFlagValue() ? "Yes" : "No");   // selected

                pstmt.addBatch();
                pstmt.executeBatch();
            } finally {
                try {
                    pstmt.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    /**
     * Write out test script data to temporary SQLite database.
     *
     * @throws SQLException the sQL exception
     * @throws Exception the exception
     */
    private void writeTestScriptsToTempDB() throws SQLException, Exception {
        TestCases appTestCases, infoTestCases;
        appTestCases = selectedTestConfigObj.getAppLayerParams().getTestCases();
        infoTestCases = selectedTestConfigObj.getInfoLayerParams().getTestCases();

        StringBuilder contents = null;
        URL scriptURL = null;
        PreparedStatement pstmt = null;
        ArrayList<String> infoTestScripts = new ArrayList<String>();
        ArrayList<String> appTestScripts = new ArrayList<String>();
                    pstmt = conn.prepareStatement("INSERT INTO C2CRI_InfoTestCases ("
                            + "scriptName, content) VALUES (?,?)");
        for (TestCase infoTestCase : infoTestCases.testCases) {
            if (!infoTestScripts.contains(infoTestCase.getScriptUrlLocation().getPath())) {
                infoTestScripts.add(infoTestCase.getScriptUrlLocation().getPath());
                try {
                    // Write out to temp db

                    contents = new StringBuilder();
                    scriptURL = infoTestCase.getScriptUrlLocation();
                    URLConnection yc = scriptURL.openConnection();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                            yc.getInputStream()));
                    String inputLine;
                    String pad = "      ";
                    Integer linecount = 0;
                    while ((inputLine = in.readLine()) != null) {
                        linecount++;
                        contents.append(linecount+pad.substring(0, pad.length() - linecount.toString().length()));
                        contents.append(inputLine);
                        contents.append(System.getProperty("line.separator"));
                    }

                    in.close();

                    int col = 1;
                    pstmt.setString(col++, scriptURL.getPath());    // scriptName path
                    pstmt.setString(col++, contents.toString());    // content
                    pstmt.addBatch();
                } catch (MalformedURLException ex) {
                    int col = 1;
                    pstmt.setString(col++, scriptURL.getPath());    // scriptName path
                    pstmt.setString(col++, "Either no legal protocol could be found in a specification string or the string could not be parsed.");    // content
                    pstmt.addBatch();

                } catch (IOException ex) {
                     int col = 1;
                    pstmt.setString(col++, scriptURL.getPath());    // scriptName path
                    pstmt.setString(col++, "IO Exception: "+ex.getMessage());    // content
                    pstmt.addBatch();   
                }finally {
//                    try {
//                        pstmt.close();
//                    } catch (SQLException ex) {
//                    }
                }
            }
       }
       pstmt.executeBatch();
       pstmt.close();

        pstmt = conn.prepareStatement("INSERT INTO C2CRI_AppTestCases ("
                + "scriptName, content) VALUES (?,?)");
        for (TestCase appTestCase : appTestCases.testCases) {
            if (!appTestScripts.contains(appTestCase.getScriptUrlLocation().getPath())) {
                appTestScripts.add(appTestCase.getScriptUrlLocation().getPath());
                try {
                    // Write out to temp db

                    contents = new StringBuilder();
                    scriptURL = appTestCase.getScriptUrlLocation();

                    URLConnection yc = scriptURL.openConnection();
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(
                            yc.getInputStream()));
                    String inputLine;

                    String pad = "      ";
                    Integer linecount = 0;
                    while ((inputLine = in.readLine()) != null) {
                        linecount++;
                        contents.append(linecount+pad.substring(0, pad.length() - linecount.toString().length()));
                        contents.append(inputLine);
                        contents.append(System.getProperty("line.separator"));
                    }

                    in.close();
                    int col = 1;
                    pstmt.setString(col++, scriptURL.getPath());    // scriptName path
                    pstmt.setString(col++, contents.toString());    // content
                    pstmt.addBatch();
                } catch (MalformedURLException ex) {
                    int col = 1;
                    pstmt.setString(col++, scriptURL.getPath());    // scriptName path
                    pstmt.setString(col++, "Either no legal protocol could be found in a specification string or the string could not be parsed.");
                    pstmt.addBatch();
                } finally {
//                    try {
//                        pstmt.close();
//                    } catch (SQLException ex) {
//                    }
                }
            }
        }
                            pstmt.executeBatch();
                        pstmt.close();

    }
    
    private void writeCommandQueueLength()
    {
        try
        {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_EmulationCommandQueueLength ("
                    + "CommandQueueLength) VALUES (?)");//+ selectedTestConfigObj.getEmulationParameters().getCommandQueueLength() +
            
            pstmt.setInt(1, selectedTestConfigObj.getEmulationParameters().getCommandQueueLength());
       
            System.out.println("The CommandQueueLength is: " + selectedTestConfigObj.getEmulationParameters().getCommandQueueLength());
            
            pstmt.addBatch();
            pstmt.executeBatch();
            pstmt.close();
        }
        catch(Exception Ex)
        {
            System.out.println("The error is: " + Ex.getMessage());
        }
    }
    
    private void writeEntityEmulationData()
    {
        try
        {             

            String temp = null;
            
            for(RIEmulationEntityValueSet emuEntityParam : selectedTestConfigObj.getEmulationParameters().getEntityDataMap())//int i = 1; i < selectedTestConfigObj.getEmulationParameters().getEntityDataMap().size() + 1; i++
            {
                PreparedStatement pstmt = conn.prepareStatement("INSERT INTO C2CRI_EmulationEntityData ("
                    + "EntityName, EntitySource, EntityData) VALUES (?, ?, ?)");
                
                int count = 1;
                
                pstmt.setString(count++, emuEntityParam.getValueSetName());
                pstmt.setString(count++, emuEntityParam.getDataSetSource().name());
                pstmt.setString(count++, EmulationDataFileProcessor.getContent(emuEntityParam.getEntityDataSet()).toString());
                pstmt.addBatch();
                pstmt.executeBatch();
              
                //pstmt.addBatch();
            }
           
            
            
            pstmt.close();
        }
        catch(Exception Ex)
        {
            System.out.println("The error is: " + Ex.getMessage());
        }
    }
    
}
