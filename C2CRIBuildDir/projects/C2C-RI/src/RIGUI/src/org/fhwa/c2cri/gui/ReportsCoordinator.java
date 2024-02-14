
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

//~--- non-JDK imports --------------------------------------------------------
import org.fhwa.c2cri.utilities.ProgressReporter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.fhwa.c2cri.reporter.RIReports;
import org.fhwa.c2cri.utilities.FilenameValidator;

//~--- JDK imports ------------------------------------------------------------

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.utilities.ProgressMonitor;

/**
 * The Class ReportsCoordinator coordinates control of the Report GUIs.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ReportsCoordinator implements java.awt.event.ActionListener {

    /** The reports panel. */
    private static TestReportsPanel reportsPanel;

    /** The reporter report. */
private ProgressReporter reporterReport;
    
    /** The listener report. */
    private ProgressListener listenerReport;
    
    /** The main ui. */
    private C2CMainUI mainUI;
    
    /** The parent object. */
    private ReportsListener parentObject;

    /**
     * Constructor for the ReportsCoordinator Class.
     *
     * @param parentFrame - the parent frame for the application
     * @param parentObject - the calling object which implements ReportsListener
     * @param reportsPanel - the reportsPanel that will be managed.
     */
    @SuppressWarnings("static-access")
    public ReportsCoordinator(C2CMainUI parentFrame, ReportsListener parentObject, TestReportsPanel reportsPanel) {
        this.mainUI = parentFrame;
        this.parentObject = parentObject;
        ReportsCoordinator.reportsPanel = reportsPanel;
        reportsPanel.logReport.cancelButton.addActionListener(this);
        reportsPanel.logReport.exportButton.addActionListener(this);
        reportsPanel.logReport.viewButton.addActionListener(this);
        reportsPanel.logReport.createButton.addActionListener(this);
        reportsPanel.configurationReport.cancelButton.addActionListener(this);
        reportsPanel.configurationReport.viewButton.addActionListener(this);
        reportsPanel.configurationReport.createButton.addActionListener(this);
        
        final JButton logViewButton = reportsPanel.logReport.viewButton;
        final JButton configViewButton = reportsPanel.configurationReport.viewButton;
        
        final DocumentListener configNameListener = new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (configViewButton != null)
				{
					configViewButton.setEnabled(false);
				}
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (configViewButton != null)
				{
					configViewButton.setEnabled(false);
				}
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (configViewButton != null)
				{
					configViewButton.setEnabled(false);
				}
            }
            
        };
        reportsPanel.configurationReport.reportNameTextField.getDocument().addDocumentListener(configNameListener);
        reportsPanel.configurationReport.reportPathTextField.getDocument().addDocumentListener(configNameListener);
        reportsPanel.configurationReport.configFilePathTextField.getDocument().addDocumentListener(configNameListener);
        reportsPanel.configurationReport.configNameTextField.getDocument().addDocumentListener(configNameListener);
        
        
        final DocumentListener logNameListener = new DocumentListener(){
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (logViewButton != null)
				{
					logViewButton.setEnabled(false);
				}
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if (logViewButton != null)
				{
					logViewButton.setEnabled(false);
				}
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if (logViewButton != null)
				{
					logViewButton.setEnabled(false);
				}
            }
            
        };
        reportsPanel.logReport.reportNameTextField.getDocument().addDocumentListener(logNameListener);
        reportsPanel.logReport.reportPathTextField.getDocument().addDocumentListener(logNameListener);        
        reportsPanel.logReport.logFilePathTextField.getDocument().addDocumentListener(logNameListener);
        reportsPanel.logReport.logNameTextField.getDocument().addDocumentListener(logNameListener);
        
        ActionListener configRadioButtonListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (configViewButton != null)
				{
					configViewButton.setEnabled(false);
				}
            }            
        };
        reportsPanel.configurationReport.allRadioButton.addActionListener(configRadioButtonListener);
        reportsPanel.configurationReport.scriptsRadioButton.addActionListener(configRadioButtonListener);
        reportsPanel.configurationReport.casesRadioButton.addActionListener(configRadioButtonListener);
        reportsPanel.configurationReport.proceduresRadioButton.addActionListener(configRadioButtonListener);

        ActionListener logRadioButtonListener = new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                if (logViewButton != null)
				{
					logViewButton.setEnabled(false);
				}
            }            
        };
        
        // register the logradiobutton listener with radio button on the TestLogReportPane.
        reportsPanel.logReport.conformanceRadioButton.addActionListener(logRadioButtonListener);
        reportsPanel.logReport.msgDetailRadioButton.addActionListener(logRadioButtonListener);
        reportsPanel.logReport.msgSummaryRadioButton.addActionListener(logRadioButtonListener);
        reportsPanel.logReport.scriptLogRadioButton.addActionListener(logRadioButtonListener);
        reportsPanel.logReport.testCaseDetailRadioButton.addActionListener(logRadioButtonListener);
        reportsPanel.logReport.testCaseSummaryRadioButton.addActionListener(logRadioButtonListener);
        reportsPanel.logReport.section1201RadioButton.addActionListener(logRadioButtonListener);
        
        reporterReport = ProgressMonitor.getInstance(
                mainUI,
                "C2C RI",
                "Processing Report",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE);
    }

    /**
     * Initialize the Reports UI. Set the default buttons and make this class
     * the listener for the panel buttons.
     *
     * @return flag indicating whether the initialization was successful.
     */
    public boolean initializeUI() {
        boolean results = false;
        reportsPanel.logReport.viewButton.setEnabled(false);
        reportsPanel.configurationReport.viewButton.setEnabled(false);




        BasicGUIActionWrapper initTestSelectionAction = new BasicGUIActionWrapper(this.mainUI, "Initializing the RI Reports GUI") {
            ConfigFileTableModel model;

            @Override
            protected Boolean actionMethod() throws Exception {


                reportsPanel.logReport.updateFileTable();
                reportsPanel.configurationReport.updateFileTable();
                System.out.println("Reports Initialization Done now.");
                return true;
            }

            @Override
            protected void wrapUp(Boolean result) {
                if (!result) {
                    javax.swing.JOptionPane.showMessageDialog(null, "An error was encountered trying to complete the Initialize TestSelection UI action.");
                }
            }
        };
        initTestSelectionAction.execute();

        results = true;

        return results;
    }

    /**
     * Handles actions from the ReportsPanel.
     *
     * @param e the action event received
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == reportsPanel.logReport.cancelButton) {
            reportsPanel.setVisible(false);
            parentObject.reportsCompleted();

            // mainUI.remove(executionPanel);
            // executionPanel = null;
        } else if (source == reportsPanel.configurationReport.cancelButton) {
            reportsPanel.setVisible(false);
            parentObject.reportsCompleted();
        } else if (source == reportsPanel.configurationReport.viewButton) {
            String reportPath = reportsPanel.configurationReport.reportPathTextField.getText();
            String reportFile = reportsPanel.configurationReport.reportNameTextField.getText();

            
            // Verify that the test name is valid
            FilenameValidator theValidator = new FilenameValidator();

            if (theValidator.validate(reportsPanel.configurationReport.reportNameTextField.getText())) {
                String reportFileName = reportPath + File.separator + reportFile + ".pdf";
                File userReport = new File(reportFileName);
                if (userReport.exists()) {
                    PDFViewUI configViewUI = new PDFViewUI(mainUI, true);

                    configViewUI.showDisplay(reportPath + File.separator + reportFile + ".pdf");
                } else {
                    javax.swing.JOptionPane.showMessageDialog(
                            mainUI,
                            "The file " + reportPath + File.separator + reportFile + " does not exist.",
                            "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(
                        mainUI,
                        "The file " + reportPath + File.separator + reportFile + ".pdf is not valid." + theValidator.getErrorsEncountered(),
                        "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
            
        } else if (source == reportsPanel.logReport.viewButton) {
            String reportPath = reportsPanel.logReport.reportPathTextField.getText();
            String reportFile = reportsPanel.logReport.reportNameTextField.getText();
            // Verify that the test name is valid
            FilenameValidator theValidator = new FilenameValidator();

            if (theValidator.validate(reportsPanel.logReport.reportNameTextField.getText())) {
                String reportFileName = reportPath + File.separator + reportFile + ".pdf";
                File userReport = new File(reportFileName);
                if (userReport.exists()) {
                    PDFViewUI viewUI = new PDFViewUI(mainUI, true);

                    viewUI.showDisplay(reportPath + File.separator + reportFile + ".pdf");
                } else {
                    javax.swing.JOptionPane.showMessageDialog(
                            mainUI,
                            "The file " + reportPath + File.separator + reportFile + " does not exist.",
                            "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                }
            } else {
                javax.swing.JOptionPane.showMessageDialog(
                        mainUI,
                        "The file " + reportPath + File.separator + reportFile + ".pdf is not valid." + theValidator.getErrorsEncountered(),
                        "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);

            }
        } else if (source == reportsPanel.logReport.createButton) {
            System.out.println("Create Log Report Button Press @ "+new java.util.Date().toString());
            reportsPanel.logReport.createButton.setEnabled(false);
            String logPath = reportsPanel.logReport.logFilePathTextField.getText();
            String logFile = reportsPanel.logReport.logNameTextField.getText();
            String reportPath = reportsPanel.logReport.reportPathTextField.getText();
            String reportFile = reportsPanel.logReport.reportNameTextField.getText();

            // Verify that the test name is valid
            FilenameValidator theValidator = new FilenameValidator();

            if (theValidator.validate(reportsPanel.logReport.reportNameTextField.getText())) {
                String reportFileName = reportPath + File.separator + reportFile + ".pdf";
                File userReport = new File(reportFileName);
                boolean continueFlag = true;
                if (userReport.exists()) {
                    Object[] options = {"Yes",
                        "No"};
                    int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                            "The file " + reportFileName + " already exists.  Would you like to overwrite the file?",
                            "File Cancel Warning",
                            javax.swing.JOptionPane.YES_NO_OPTION,
                            javax.swing.JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);

                    if (n == 1) {
                        continueFlag = false;
                    }
                }

                if ((reportFile != null) && (!reportFile.equals("")) && continueFlag) {
                    if ((logFile != null) && (!logFile.equals(""))) {
                        String reportFormat = "";

                        if (reportsPanel.logReport.scriptLogRadioButton.isSelected()) {
                            reportFormat = RIReports.All_Logreport_Source;
                        } else if (reportsPanel.logReport.msgDetailRadioButton.isSelected()) {
                            reportFormat = RIReports.MsgDetail_Logreport_Source;
                        } else if (reportsPanel.logReport.msgSummaryRadioButton.isSelected()) {
                            reportFormat = RIReports.MsgSummary_Logreport_Source;
                        } else if (reportsPanel.logReport.testCaseSummaryRadioButton.isSelected()) {
                            reportFormat = RIReports.TestCaseSummary_Logreport_Source;
                        } else if (reportsPanel.logReport.testCaseDetailRadioButton.isSelected()) {
                            reportFormat = RIReports.TestCaseDetails_Logreport_Source;
                        } else if (reportsPanel.logReport.conformanceRadioButton.isSelected()) {
                            reportFormat = RIReports.Conformance_Logreport_Source;
                        } else if (reportsPanel.logReport.section1201RadioButton.isSelected()) {
                            reportFormat = RIReports.Section1201_Conformance_Logreport_Source;
                        } else {
                            reportFormat = RIReports.All_Logreport_Source;
                            javax.swing.JOptionPane.showMessageDialog(
                                    mainUI,
                                    "This selected report has not yet been implemented.  The All Detail report will be created.",
                                    "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                        }

                        File tryFile = new File(reportFormat);

                        if (tryFile.exists()) {
                            HashMap inputParams = new HashMap();

                            inputParams.put("reportSource", reportFormat);
                            inputParams.put("reportDest", reportFileName);
                            inputParams.put("dataSource", logPath + File.separator + logFile);

                            File subReportDir = new File(RIReports.Default_Reports_Path);

                            inputParams.put("SUBREPORT_DIR", subReportDir.getAbsolutePath() + File.separator);

                            listenerReport = new ProgressListener(reporterReport);

                            // Create an action that will enable the log view button when the report has been created. If
                            // the log report path/name and configuration report path/name are identical then disable the configuration panel view button.
                            LogReportCreateAction reporterAction = new LogReportCreateAction(inputParams){
                                    public void performPostAction(){
                                        ReportsCoordinator.reportsPanel.logReport.viewButton.setEnabled(true);                                        
                                        if (ReportsCoordinator.reportsPanel.configurationReport.reportNameTextField.getText().trim().equalsIgnoreCase(
                                            ReportsCoordinator.reportsPanel.logReport.reportNameTextField.getText().trim())&&
                                            ReportsCoordinator.reportsPanel.configurationReport.reportPathTextField.getText().trim().equalsIgnoreCase(
                                            ReportsCoordinator.reportsPanel.logReport.reportPathTextField.getText().trim())){
                                            ReportsCoordinator.reportsPanel.configurationReport.viewButton.setEnabled(false);
                                        }
                                    }                               
                            };
                            // Add the progress listner to the report action to provide feedback on report generation.
                            reporterAction.addPropertyChangeListener(listenerReport);
                            // Create the report
                            reporterAction.execute();
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(mainUI,
                                    "The Log File Report Source doesn't exist.", "Error",
                                    javax.swing.JOptionPane.ERROR_MESSAGE);
                            System.err.println("The Log File Report Source " + tryFile.getAbsolutePath()
                                    + " doesn't Exist");
                        }
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(mainUI, "The Log File must be specified.", "Error",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (continueFlag)
					{
						javax.swing.JOptionPane.showMessageDialog(mainUI,
							"The Log File Report Destination must be specified.", "Error",
							javax.swing.JOptionPane.ERROR_MESSAGE);
					}
                }
            } else {
                //Pop up the message dialog.
                String message = "Invalid file name entered \n"
                        + theValidator.getErrorsEncountered()
                        + "\nPlease try again.";

                JOptionPane.showMessageDialog(null, //no owner frame
                        message, //text to display
                        "Invalid File Name", //title
                        JOptionPane.WARNING_MESSAGE);

            }
            reportsPanel.logReport.createButton.setEnabled(true);
        } else if (source == reportsPanel.configurationReport.createButton) {
            reportsPanel.configurationReport.createButton.setEnabled(false);
            String configPath = reportsPanel.configurationReport.configFilePathTextField.getText();
            String configFile = reportsPanel.configurationReport.configNameTextField.getText();
            String reportPath = reportsPanel.configurationReport.reportPathTextField.getText();
            String reportFile = reportsPanel.configurationReport.reportNameTextField.getText();

            // Verify that the report name is valid
            FilenameValidator theValidator = new FilenameValidator();

            if (theValidator.validate(reportsPanel.configurationReport.reportNameTextField.getText())) {
                String reportFileName = reportPath + File.separator + reportFile + ".pdf";
                File userReport = new File(reportFileName);
                boolean continueFlag = true;
                if (userReport.exists()) {
                    Object[] options = {"Yes",
                        "No"};
                    int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                            "The file " + reportFileName + " already exists.  Would you like to overwrite the file?",
                            "File Cancel Warning",
                            javax.swing.JOptionPane.YES_NO_OPTION,
                            javax.swing.JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);

                    if (n == 1) {
                        continueFlag = false;
                    }
                }

                if ((reportFile != null) && (!reportFile.equals(""))&&continueFlag) {
                    File checkFile = new File(configPath + File.separator + configFile);

                    if ((!checkFile.isDirectory()) && checkFile.exists()) {
                        String reportFormat = "";

                        File tryFile;
                        if (reportsPanel.configurationReport.allRadioButton.isSelected()) {
                            reportFormat = RIReports.All_Configreport_Source;
                            tryFile = new File(RIReports.All_Configreport_Source);
                        } else if (reportsPanel.configurationReport.casesRadioButton.isSelected()){
                            reportFormat = RIReports.TestCases_report_Source;
                            tryFile = new File(RIReports.TestCases_report_Source);                            
                        } else if (reportsPanel.configurationReport.proceduresRadioButton.isSelected()){
                            reportFormat = RIReports.TestProcedure_report_Source;
                            tryFile = new File(RIReports.TestProcedure_report_Source);                            
                        } else {
                            reportFormat = RIReports.All_TestScripts_Source;
                            tryFile = new File(RIReports.All_TestScripts_Source);
                        }

                        /*javax.swing.JOptionPane.showMessageDialog(mainUI,
                         "This selected report has not yet been implemented.  A Blank report will be created.",
                         "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                         */


                        if (tryFile.exists()) {
                            HashMap inputParams = new HashMap();

                            inputParams.put("reportSource", reportFormat);
                            inputParams.put("reportDest", reportFileName);
                            inputParams.put("dataSource", configPath + File.separator + configFile);

                            File subReportDir = new File(RIReports.Default_Reports_Path);
                            inputParams.put("SUBREPORT_DIR", subReportDir.getAbsolutePath() + File.separator);
                            listenerReport = new ProgressListener(reporterReport);


                            TestConfiguration testConfig = null;
                            try (ObjectInputStream input= new ObjectInputStream(new FileInputStream(new File(configPath, configFile))))
							{
                                testConfig = (TestConfiguration) input.readObject();
                                testConfig.print();

                                String configFileName = reportsPanel.configurationReport.configFilePathTextField.getText();
                                configFileName = configFileName + '\\' + reportsPanel.configurationReport.configNameTextField.getText();
                                ReportCreateAction reporterAction = new ReportCreateAction(inputParams, testConfig, configFileName, reportFormat){
                                    public void performPostAction(){
                                        ReportsCoordinator.reportsPanel.configurationReport.viewButton.setEnabled(true);
                                        if (ReportsCoordinator.reportsPanel.configurationReport.reportNameTextField.getText().trim().equalsIgnoreCase(
                                            ReportsCoordinator.reportsPanel.logReport.reportNameTextField.getText().trim())&&
                                            ReportsCoordinator.reportsPanel.configurationReport.reportPathTextField.getText().trim().equalsIgnoreCase(
                                            ReportsCoordinator.reportsPanel.logReport.reportPathTextField.getText().trim())){
                                            ReportsCoordinator.reportsPanel.logReport.viewButton.setEnabled(false);
                                        }
                                     }
                                };
                                reporterAction.addPropertyChangeListener(listenerReport);
                                reporterAction.execute();

                            } catch (IOException ex) {
                                Logger.getLogger(ReportsCoordinator.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(ReportsCoordinator.class.getName()).log(Level.SEVERE, null, ex);
                            }


                        } else {
                            javax.swing.JOptionPane.showMessageDialog(mainUI,
                                    "The Config File Report Source " + tryFile.getAbsolutePath() + " doesn't exist.",
                                    "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                            System.err.println("The Log File Report Source Doesn't Exist");
                        }
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(mainUI,
                                "The Cconfig File Report Source doesn't exist.", "Error",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                        System.err.println("The Config File Report Source " + checkFile.getAbsolutePath()
                                + " doesn't Exist");
                    }
                } else {
                    if (continueFlag)
                        javax.swing.JOptionPane.showMessageDialog(mainUI,
                            "The Configuration File Report Destination must be specified.", "Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } else {
                //Pop up the message dialog.
                String message = "Invalid file name entered \n"
                        + theValidator.getErrorsEncountered()
                        + "\nPlease try again.";

                JOptionPane.showMessageDialog(null, //no owner frame
                        message, //text to display
                        "Invalid File Name", //title
                        JOptionPane.WARNING_MESSAGE);

            }
            reportsPanel.configurationReport.createButton.setEnabled(true);
        } else if (source == reportsPanel.logReport.exportButton) {

            reportsPanel.logReport.exportButton.setEnabled(false);
            String logPath = reportsPanel.logReport.logFilePathTextField.getText();
            String logFile = reportsPanel.logReport.logNameTextField.getText();
            String reportPath = reportsPanel.logReport.reportPathTextField.getText();
            String reportFile = reportsPanel.logReport.reportNameTextField.getText();

            // Verify that the test name is valid
            FilenameValidator theValidator = new FilenameValidator();

            if (theValidator.validate(reportsPanel.logReport.reportNameTextField.getText())) {
                String reportFileName = reportPath + File.separator + reportFile + ".csv";
                File userReport = new File(reportFileName);
                boolean continueFlag = true;
                if (userReport.exists()) {
                    Object[] options = {"Yes",
                        "No"};
                    int n = javax.swing.JOptionPane.showOptionDialog(mainUI,
                            "The file " + reportFileName + " already exists.  Would you like to overwrite the file?",
                            "File Cancel Warning",
                            javax.swing.JOptionPane.YES_NO_OPTION,
                            javax.swing.JOptionPane.QUESTION_MESSAGE,
                            null,
                            options,
                            options[1]);

                    if (n == 1) {
                        continueFlag = false;
                    }
                }

                if ((reportFile != null) && (!reportFile.equals(""))&&continueFlag) {
                    if ((logFile != null) && (!logFile.equals(""))) {
                        String reportFormat = "";

                        if (reportsPanel.logReport.scriptLogRadioButton.isSelected()) {
                            reportFormat = RIReports.All_Logreport_Source;
                        } else if (reportsPanel.logReport.msgDetailRadioButton.isSelected()) {
                            reportFormat = RIReports.MsgDetail_Logreport_Source;
                        } else if (reportsPanel.logReport.msgSummaryRadioButton.isSelected()) {
                            reportFormat = RIReports.MsgSummary_Logreport_Source;
                        } else if (reportsPanel.logReport.testCaseSummaryRadioButton.isSelected()) {
                            reportFormat = RIReports.TestCaseSummary_Logreport_Source;
                        } else if (reportsPanel.logReport.testCaseDetailRadioButton.isSelected()) {
                            reportFormat = RIReports.TestCaseDetails_Logreport_Source;
                        } else if (reportsPanel.logReport.conformanceRadioButton.isSelected()) {
                            reportFormat = RIReports.Conformance_Logreport_Source;
                        } else if (reportsPanel.logReport.section1201RadioButton.isSelected()) {
                            reportFormat = RIReports.Section1201_Conformance_Logreport_Source;
                        } else {
                            reportFormat = RIReports.All_Logreport_Source;
                            javax.swing.JOptionPane.showMessageDialog(
                                    mainUI,
                                    "This selected report has not yet been implemented.  The All Detail report will be created.",
                                    "Warning", javax.swing.JOptionPane.WARNING_MESSAGE);
                        }

                        File tryFile = new File(reportFormat);

                        if (tryFile.exists()) {
                            HashMap inputParams = new HashMap();

                            inputParams.put("reportSource", reportFormat);
                            inputParams.put("reportDest", reportPath + File.separator + reportFile + ".csv");
                            inputParams.put("dataSource", logPath + File.separator + logFile);

                            File subReportDir = new File(RIReports.Default_Reports_Path);

                            inputParams.put("SUBREPORT_DIR", subReportDir.getAbsolutePath() + File.separator);

//                          newReport.createLogReport(inputParams);
                            listenerReport = new ProgressListener(reporterReport);

                            LogCSVCreateAction reporterAction = new LogCSVCreateAction(inputParams);

                            reporterAction.addPropertyChangeListener(listenerReport);
                            reporterAction.execute();
                        } else {
                            javax.swing.JOptionPane.showMessageDialog(mainUI,
                                    "The Log CSV File Source doesn't exist.", "Error",
                                    javax.swing.JOptionPane.ERROR_MESSAGE);
                            System.err.println("The Log CSV File Source " + tryFile.getAbsolutePath()
                                    + " doesn't Exist");
                        }
                    } else {
                        javax.swing.JOptionPane.showMessageDialog(mainUI, "The Log CSV File must be specified.", "Error",
                                javax.swing.JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    if (continueFlag)
                        javax.swing.JOptionPane.showMessageDialog(mainUI,
                            "The Log CSV File Destination must be specified.", "Error",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            } else {
                //Pop up the message dialog.
                String message = "Invalid file name entered \n"
                        + theValidator.getErrorsEncountered()
                        + "\nPlease try again.";

                JOptionPane.showMessageDialog(null, //no owner frame
                        message, //text to display
                        "Invalid File Name", //title
                        JOptionPane.WARNING_MESSAGE);

            }
            reportsPanel.logReport.exportButton.setEnabled(true);

        } else {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
