package net.sf.jameleon.reporting;

import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.bean.TestCase;
import net.sf.jameleon.result.*;

import java.io.StringWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Generates the HTML results for a given test case
 */
public class HtmlTestCaseResultGenerator {

    private TestCaseTag tct;
    private final static String RES_DIR = ".."+File.separator+"..";

    public HtmlTestCaseResultGenerator(TestCaseTag tct){
        this.tct = tct;    
    }

    /**
     * Generates the main page along with all other iframes and content.
     * @return The main file that was written to.
     */
    public File generateTestCaseResultPage(){
        File resultsDir = tct.getTimestampedResultsDir();
        File mainFile = new File(resultsDir, "index.html");
        File tcSummaryFile = new File(resultsDir, "summary.html");
        File tcResultSummaryFile = new File(resultsDir, "resultsSummary.html");
        try {
            JameleonUtility.recordResultsToFile(mainFile, generateTestCasePage());
            JameleonUtility.recordResultsToFile(tcSummaryFile, generateTestCaseSummary());
            JameleonUtility.recordResultsToFile(tcResultSummaryFile, generateTestCaseResultSummary());
        } catch (IOException e) {
            System.err.println("Error writing results to file: ");
            e.printStackTrace();
        }
        return mainFile;
    }

    public String generateTestCaseResult(){
        StringWriter writer = new StringWriter();
        String childrenResults = generateAppropriateResult(tct.getResults());
        Map params = new HashMap();
        params.put("results_res_dir", RES_DIR);
        params.put("childrenResults", childrenResults);
        ReporterUtils.outputToTemplate(writer, tct.getTestCaseResultTemplate(), params);
        return writer.toString();
    }

    public String generateTestCasePage() {
        final TestCase testCase = tct.getTestCase();
        StringWriter writer = new StringWriter();
        String results = generateTestCaseResult();
        Map params = new HashMap();
        params.put("testCase", testCase);
        params.put("results_res_dir", RES_DIR);
        params.put("encoding", tct.getGenTestCaseDocsEncoding());
        params.put("results", results);
        ReporterUtils.outputToTemplate(writer, tct.getTestCaseMainPageTemplate(), params);
        return writer.toString();
    }

    public String generateTestCaseSummary() {
        TestCase testCase = tct.getTestCase();
        TestCaseResult result = tct.getResults();
        StringWriter writer = new StringWriter();
        Map params = new HashMap();
        params.put("result", result);
        params.put("tc", testCase);
        params.put("encoding", tct.getGenTestCaseDocsEncoding());
        params.put("results_res_dir", RES_DIR);
        if (tct.getBugTrackerUrl() != null) {
            params.put("bugTrackerUrl", tct.getBugTrackerUrl());
        }
        ReporterUtils.outputToTemplate(writer, tct.getTestCaseSummaryTemplate(), params);
        return writer.toString();
    }

    public String generateTestCaseResultSummary() {
        TestCaseResult result = tct.getResults();
        StringWriter writer = new StringWriter();
        Map params = new HashMap();
        params.put("result", result);
        params.put("encoding", tct.getGenTestCaseDocsEncoding());
        params.put("results_res_dir", RES_DIR);
        ReporterUtils.outputToTemplate(writer, tct.getTestCaseResultSummaryTemplate(), params);

        return writer.toString();
    }

    protected String generateSessionResult(SessionResult result) {
        StringWriter writer = new StringWriter();
        String childrenResults = generateResultsForChildren(result);

        Map params = new HashMap();
        params.put("result", result);
        params.put("childrenResults", childrenResults);
        params.put("results_res_dir", RES_DIR);
        ReporterUtils.outputToTemplate(writer, tct.getTestCaseResultSessionTemplate(), params);
        return writer.toString();
    }

    protected String generateFunctionResult(FunctionResult result) {
        StringWriter writer = new StringWriter();
        Map params = new HashMap();
        params.put("result", result);
        params.put("results_res_dir", RES_DIR);
        ReporterUtils.outputToTemplate(writer, tct.getTestCaseResultFunctionTemplate(), params);
        return writer.toString();
    }

    protected String generateDDRowResult(DataDrivableRowResult result){
        StringWriter writer = new StringWriter();
        String childrenResults = generateResultsForChildren(result);

        Map params = new HashMap();
        params.put("result", result);
        params.put("childrenResults", childrenResults);
        params.put("results_res_dir", RES_DIR);
        ReporterUtils.outputToTemplate(writer, tct.getTestCaseResultDataRowTemplate(), params);
        return writer.toString();
    }

    protected String generateResultsForChildren(TestResultWithChildren result) {
        StringBuffer childrenResults = new StringBuffer();
        for(Iterator i = result.getChildrenResults().iterator(); i.hasNext();){
            childrenResults.append(generateAppropriateResult((JameleonTestResult)i.next()));
        }
        return childrenResults.toString();
    }

    protected String generateAppropriateResult(JameleonTestResult result) {
        String resString = null;
        if (result instanceof SessionResult){
            resString = generateSessionResult((SessionResult)result);
        }else if (result instanceof FunctionResult){
            resString = generateFunctionResult((FunctionResult)result);
        }else if (result instanceof DataDrivableResultContainer){
            resString = generateResultsForChildren((DataDrivableResultContainer)result);
        }else if (result instanceof DataDrivableRowResult){
            resString = generateDDRowResult((DataDrivableRowResult)result);
        }else if (result instanceof TestCaseResult){
            TestCaseResult tcr = (TestCaseResult)result;
            if (tcr.getChildrenResults() != null && tcr.getChildrenResults().size() > 0 &&
                tcr.getChildrenResults().get(0) instanceof DataDrivableResultContainer ){
                resString = generateResultsForChildren((DataDrivableResultContainer)tcr.getChildrenResults().get(0));
            }else if (tcr.getChildrenResults() != null && tcr.getChildrenResults().size() > 0 &&
                    !(tcr.getChildrenResults().get(0) instanceof DataDrivableResultContainer) ){
                resString = generateResultsForChildren((TestResultWithChildren) result);
            }
        }
        return resString;
    }

}
