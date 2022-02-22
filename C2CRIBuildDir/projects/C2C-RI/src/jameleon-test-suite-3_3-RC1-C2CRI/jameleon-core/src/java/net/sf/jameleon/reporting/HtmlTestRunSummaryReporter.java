/*
    Jameleon - An automation testing tool..
    Copyright (C) 2007 Christian W. Hargraves (engrean@hotmail.com)

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.reporting;

import net.sf.jameleon.result.TestCaseResult;
import net.sf.jameleon.util.JarResourceExtractor;
import net.sf.jameleon.util.JameleonDefaultValues;
import net.sf.jameleon.util.Configurator;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Reports on all script results in a given test run as a summary in HTML. The current implementation reports
 * a single result as an HTML table row.
 */
public class HtmlTestRunSummaryReporter implements TestRunSummaryReporter {

    public static final String DEFAULT_HEADER_TEMPLATE = "templates/results/summary/TestRunSummaryResultsHeader.html";
    public static final String DEFAULT_RESULT_ROW_TEMPLATE = "templates/results/summary/TestRunSummaryRowResult.html";
    public static final String DEFAULT_RESULT_BEGIN_ROW_TEMPLATE = "templates/results/summary/TestRunBeginSummaryRowResult.html";

    private String testRunSummaryResultsHeaderTemplate = DEFAULT_HEADER_TEMPLATE;
    private String testRunResultRowTemplate = DEFAULT_RESULT_ROW_TEMPLATE;
    private String testRunBeginResultRowTemplate = DEFAULT_RESULT_BEGIN_ROW_TEMPLATE;

    private boolean printHeader;
    private Writer writer;

    public void extractResources(){
        JarResourceExtractor extractor = null;
        try{
            extractor = new JarResourceExtractor();
            File resultsDir = ReporterUtils.getResultsDir();
            extractor.extractFilesInDirectory("icons", resultsDir);
            extractor.extractFilesInDirectory("images", resultsDir);
            extractor.extractFilesInDirectory("css", resultsDir);
            extractor.extractFilesInDirectory("js", resultsDir);
        }catch(IOException ioe){
            System.err.println("Can not copy over required images, css and js files for reporting");
            ioe.printStackTrace();
        }finally{
            if (extractor != null && extractor.getJarFile() != null){
                try{
                    extractor.getJarFile().close();
                }catch(IOException ioe){
                    System.err.println("Could not close jameleon-core.jar");
                    ioe.printStackTrace();
                    //so we couldn't close the jar file?
                }
            }
        }

    }

    public void printHeader(){
        Map params = new HashMap();
        params.put("encoding",  Configurator.getInstance().getValue("genTestCaseDocsEncoding", JameleonDefaultValues.FILE_CHARSET));

        ReporterUtils.outputToTemplate(getWriter(), getTestRunSummaryResultsHeaderTemplate(), params);
    }

    public void reportTestRunStart(Calendar startTime) {
        if (isPrintHeader()){
            extractResources();
            printHeader();
            setPrintHeader(false);
        }
        Map params = new HashMap();
        params.put("testRunId", ReporterUtils.formatTime(startTime));
        ReporterUtils.outputToTemplate(getWriter(), getTestRunBeginResultRowTemplate(), params);
    }

    public void reportTestRunComplete(Calendar startTime, Calendar endTime, TestCaseCounter counter) {
        Map params = new HashMap();
        String outcome = "fail";
        if (counter.getNumFailed() == 0){
            outcome = "pass";
        }
        params.put("testRunId", ReporterUtils.formatTime(startTime));
        params.put("outcome", outcome);        
        params.put("totalRun", new Integer(counter.getNumRun()));
        params.put("totalFailed", new Integer(counter.getNumFailed()));
        params.put("totalTime", ReporterUtils.getExecutionTime(startTime, endTime));
        params.put("percentPassed", TestCaseResult.getPercentagePassed(counter.getNumRun(), counter.getNumFailed()));
        ReporterUtils.outputToTemplate(getWriter(), getTestRunResultRowTemplate(), params);
    }

    public void cleanUp() {
        try{
            if (writer != null){
                writer.flush();
                writer.close();
                writer = null;
            }
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////                      GETTERS & SETTERS                                                       //////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////


    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    /**
     * Tells whether the header should be printed or not
     * @return true if the header should be printed
     */
    public boolean isPrintHeader(){
        return printHeader;
    }

    /**
     * Sets the print header option
     * @param printHeader true if the header should be printed
     */
    public void setPrintHeader(boolean printHeader) {
        this.printHeader = printHeader;
    }

    /**
     * Gets the template to be used when the test run is started
     * @return The template to be used when the test run is started
     */
    public String getTestRunBeginResultRowTemplate() {
        return testRunBeginResultRowTemplate;
    }

    /**
     * Sets the template to be used when the test run is started
     * @param testRunBeginResultRowTemplate The template to be used when the test run is started
     */
    public void setTestRunBeginResultRowTemplate(String testRunBeginResultRowTemplate) {
        this.testRunBeginResultRowTemplate = testRunBeginResultRowTemplate;
    }

    /**
     * Gets the template for the test run results header file
     * @return The test run results template
     */
    public String getTestRunSummaryResultsHeaderTemplate() {
        return testRunSummaryResultsHeaderTemplate;
    }

    /**
     * Sets the template for the test run results header file
     * @param testRunSummaryResultsHeaderTemplate The test run results template
     */
    public void setTestRunSummaryResultsHeaderTemplate(String testRunSummaryResultsHeaderTemplate) {
        this.testRunSummaryResultsHeaderTemplate = testRunSummaryResultsHeaderTemplate;
    }

    /**
     * Gets the template for the invidual test script result
     * @return The template for the invidual test script result
     */
    public String getTestRunResultRowTemplate() {
        return testRunResultRowTemplate;
    }

    /**
     * Sets the template for the invidual test script result
     * @param testRunResultRowTemplate The template for the invidual test script result
     */
    public void setTestRunResultRowTemplate(String testRunResultRowTemplate) {
        this.testRunResultRowTemplate = testRunResultRowTemplate;
    }
}
