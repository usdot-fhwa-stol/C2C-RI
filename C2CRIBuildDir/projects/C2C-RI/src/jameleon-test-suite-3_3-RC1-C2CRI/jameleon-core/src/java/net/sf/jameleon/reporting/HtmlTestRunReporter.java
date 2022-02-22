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

import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.result.TestCaseResult;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.JameleonDefaultValues;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * Reports on all script results in a given test run as a summary in HTML. The current implementation reports
 * a single result as an HTML table row.
 */
public class HtmlTestRunReporter extends AbstractTestRunReporter {

    public static final String DEFAULT_HEADER_TEMPLATE = "templates/results/summary/TestRunResultsHeader.html";
    public static final String DEFAULT_FOOTER_TEMPLATE = "templates/results/summary/TestRunResultsFooter.html";
    public static final String DEFAULT_RESULT_ROW_TEMPLATE = "templates/results/summary/TestRunRowResult.html";

    private String testRunResultsHeaderTemplate = DEFAULT_HEADER_TEMPLATE;
    private String testResultRowTemplate = DEFAULT_RESULT_ROW_TEMPLATE;
    private String testRunResultsFooterTemplate = DEFAULT_FOOTER_TEMPLATE;

    public void reportScriptResult(TestCaseTag tct, TestCaseCounter counter) {
        Map params = new HashMap();
        params.put("result", tct.getResults());
        String path = tct.getResultsDir().getParent();
        params.put("resultsFile", JameleonUtility.getEndingPath(path, tct.getResults().getTestCaseDocsFile()));
        params.put("results_res_dir", "../..");
        params.put("testCaseNum", counter.getTestCaseNum()+"");
        ReporterUtils.outputToTemplate(getWriter(), testResultRowTemplate, params);
    }

    public void reportTestRunStart(Calendar startTime) {
        Configurator config = Configurator.getInstance();
        Map params = new HashMap();
        params.put("organization", config.getValue("organization"));
        params.put("environment", config.getValue("testEnvironment"));
        params.put("startTime", startTime.getTime());
        params.put("encoding", config.getValue("genTestCaseDocsEncoding", JameleonDefaultValues.FILE_CHARSET));

        ReporterUtils.outputToTemplate(getWriter(), testRunResultsHeaderTemplate, params);
    }

    public void reportTestRunComplete(Calendar startTime, Calendar endTime, TestCaseCounter counter) {
        int numPassed = 0, numFailed = 0;
        if (counter != null){
            numPassed = counter.getNumPassed();
            numFailed = counter.getNumFailed();
        }
        Map params = new HashMap();
        params.put("totalPassed", new Integer(numPassed));
        params.put("totalFailed", new Integer(numFailed));
        params.put("totalTime", ReporterUtils.getExecutionTime(startTime, endTime));
        String percentagePassed = TestCaseResult.getPercentagePassed(numPassed, numFailed);
        params.put("percentPassed", percentagePassed);
        ReporterUtils.outputToTemplate(getWriter(), testRunResultsFooterTemplate, params);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////                      GETTERS & SETTERS                                                       //////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Gets the test run results footer template
     * @return The test run results footer template
     */
    public String getTestRunResultsFooterTemplate() {
        return testRunResultsFooterTemplate;
    }

    /**
     * Sets the test run results footer template
     * @param testRunResultsFooterTemplate The test run results footer template
     */
    public void setTestRunResultsFooterTemplate(String testRunResultsFooterTemplate) {
        this.testRunResultsFooterTemplate = testRunResultsFooterTemplate;
    }

    /**
     * Gets the template for the test run results header file
     * @return The test run results template
     */
    public String getTestRunResultsHeaderTemplate() {
        return testRunResultsHeaderTemplate;
    }

    /**
     * Sets the template for the test run results header file
     * @param testRunResultsHeaderTemplate The test run results template
     */
    public void setTestRunResultsHeaderTemplate(String testRunResultsHeaderTemplate) {
        this.testRunResultsHeaderTemplate = testRunResultsHeaderTemplate;
    }

    /**
     * Gets the template for the invidual test script result
     * @return The template for the invidual test script result
     */
    public String getTestResultRowTemplate() {
        return testResultRowTemplate;
    }

    /**
     * Sets the template for the invidual test script result
     * @param testResultRowTemplate The template for the invidual test script result
     */
    public void setTestResultRowTemplate(String testResultRowTemplate) {
        this.testResultRowTemplate = testResultRowTemplate;
    }
}
