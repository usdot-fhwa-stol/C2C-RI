/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2007 Christian W. Hargraves (engrean@hotmail.com)
    
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.plugin.junit;


import net.sf.jameleon.ExecuteTestCase;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.event.TestCaseEvent;
import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.event.TestCaseListener;
import net.sf.jameleon.reporting.ResultsReporter;
import net.sf.jameleon.reporting.TestCaseCounter;
import net.sf.jameleon.result.*;
import net.sf.jameleon.util.JameleonUtility;
import org.apache.log4j.LogManager;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.varia.DenyAllFilter;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Used to acceptance test Jameleon tags. Executes a Jameleon script and checks various outputs.
 * @jameleon.function name="execute-jameleon-script" type="action" 
 * @jameleon.step Execute the given script
 * @jameleon.step Validate that the script ran correctly
 */
public class ExecuteJameleonScriptTag extends JUnitFunctionTag {

    protected static final boolean DEBUG = false;
    /**
     * The script to execute
     * @jameleon.attribute required="true"
     */
    protected File script;
    /**
     * Pass only if the script passed
     * @jameleon.attribute
     */
    protected boolean checkOutcomePassed;
    /**
     * Pass only if the script failed
     * @jameleon.attribute
     */
    protected boolean checkOutcomeFailed;
    /**
     * @jameleon.attribute
     */
    protected boolean noTestCaseResults;
    /**
     * The number of functional points that should be run from the script being executed
     * @jameleon.attribute default="-1"
     */
    protected int numOfFunctionsRun;
    /**
     * The number of failures that should fail from the script being executed
     * @jameleon.attribute default="-1"
     */
    protected int numOfFailures;
    /**
     * A functionId where no failure should occur
     * @jameleon.attribute
     */
    protected String noFailOnFunctionId;
    /**
     * The number of test cases that should run from the script being executed (for when useCSV is set to true in the testcase tag)
     * @jameleon.attribute default="-1"
     */
    protected int numOfTestCasesRun;
    /**
     * The number of test cases that should fail from the script being executed (for when useCSV is set to true in the testcase tag)
     * @jameleon.attribute default="-1"
     */
    protected int numOfTestCasesFailed;
    /**
     * The name of the generated test case doc file.
     * @jameleon.attribute
     */
    protected String testCaseDocsFile;
    /**
     * The execution time should be greater than this number if set
     * @jameleon.attribute default="-1"
     */
    protected int executionTimeGreaterThan;
    /**
     * The execution time should be greater than this number if set
     * @jameleon.attribute default="-1"
     */
    protected int executionTimeLessThan;
    /**
     * The line # of the script that is stated as failing in the HTML . This requires the testCaseName attribute be set
     * @jameleon.attribute default="-1"
     */
    protected int lineNumFailed;
    /**
     * The given error message for failure in the HTML results
     * @jameleon.attribute
     */
    protected String lineFailedReason;
    /**
     * The functionId of the row that failed
     * @jameleon.attribute
     */
    protected String lineFailedFunctionId;
    /**
     * A snippet of the error message or reason for that is expected in stdout.
     * @jameleon.attribute
     */
    protected String errorMsgContains;
    /**
     * A snippet of the error message or reason for that is expected in stdout.
     * @jameleon.attribute
     */
    protected String testCaseName;

    protected StringWriter htmlResults;
    protected Writer timestampedResults;
    protected ExecuteTestCase jmln;
    protected DenyAllFilter denyAll;
    protected static SAXReader parser = new SAXReader();
    protected TestCaseResult result;
    protected TestCaseCounter originalCounter;
    protected Writer stdOutWriter;

    protected TestCaseListener listener = new TestCaseListener(){
                public void beginTestCase(TestCaseEvent event) {
                    result = null;
                }
                public void endTestCase(TestCaseEvent event) {
                    TestCaseTag tct = (TestCaseTag)event.getSource();
                    result = tct.getResults();
                }
                };

    public void testBlock(){
        String errMsg = executeScript();
        if (errorMsgContains != null) {
            assertTrue("An expected error message was not provided! <"+errMsg+">", errMsg.indexOf(errorMsgContains) >= 0);
        }
        if (checkOutcomePassed) {
            assertTrue("Errors NOT expected, but found!", errMsg.length() == 0);
            assertOutcomePassed();
        }
        if (checkOutcomeFailed) {
            assertTrue("Errors expected, but none found!", errMsg.length() > 0);
            assertOutcomeFailed();
        }
        if (noTestCaseResults) {
            assertNull("No information about this test case should be given.", result.getTestCaseDocsFile());
        }
        if (numOfFailures != -1) {
            assertNumOfFailures(numOfFailures);
        }
        if (numOfFunctionsRun != -1) {
            assertNumOfFunctionsRun(numOfFunctionsRun);
        }
        if (numOfTestCasesRun != -1) {
            assertNumOfTestCasesRun(numOfTestCasesRun);
        }
        if (numOfTestCasesFailed != -1) {
            assertNumOfTestCasesFailed(numOfTestCasesFailed);
        }
        if (testCaseDocsFile != null) {
            assertTestCaseDocFileEndsWith(testCaseDocsFile);
        }
        if (testCaseName != null) {
            assertHtmlTestCaseName(testCaseName);
        }
        if (lineNumFailed != -1) {
            try{
                JameleonTestResult res = null;

                for (Iterator it = result.getFailedResults().iterator(); it.hasNext(); ){
                    JameleonTestResult tmpRes = (JameleonTestResult)it.next();
                    if (tmpRes.getLineNumber() == lineNumFailed){
                        res = tmpRes;
                    }
                }

                assertNotNull("No matching line # found", res);
				if (res != null)
				{
					assertEquals("Failed Line #:", lineNumFailed, res.getLineNumber());
					if (lineFailedReason != null) {
						assertEquals("Error Message: ", lineFailedReason, res.getErrorMsg());
					}
					if (lineFailedFunctionId != null) {
						assertEquals("Function ID: ", lineFailedFunctionId, res.getIdentifier());
					}
				}
            }catch(Exception e){
                System.err.println("####################################");
                e.printStackTrace();
                System.err.println("####################################");
            }
        }
        if (executionTimeGreaterThan != -1) {
            assertExecutionTimeGreaterThan(executionTimeGreaterThan);
        }
        if (executionTimeLessThan != -1) {
            assertExecutionTimeLessThan(executionTimeLessThan);
        }
        if (noFailOnFunctionId != null) {
            assertFunctionIdNotFailed(noFailOnFunctionId);
        }
    }

    protected void setupEnvironment(){
        super.setupEnvironment();
        postcondition = true;
    }

    public void setup(){
        postcondition = true;
        denyAll = new DenyAllFilter();
        jmln = new ExecuteTestCase(DEBUG);
        timestampedResults = ResultsReporter.getInstance().getHtmlTestRunReporter().getWriter();
        htmlResults = new StringWriter();
        ResultsReporter reporter = ResultsReporter.getInstance();
        reporter.getHtmlTestRunReporter().setWriter(htmlResults);
        originalCounter = reporter.getTestCaseCounter();
        stdOutWriter = reporter.getSimpleTestRunReporter().getWriter();
        reporter.getSimpleTestRunReporter().setWriter(new StringWriter());
        reporter.setTestCaseCounter(new TestCaseCounter());
        LoggerRepository repos = LogManager.getLoggerRepository();

        TestCaseEventHandler.getInstance().addTestCaseListener(listener);
    }

    protected void setUpFunctionResults(){
        fResults = new CountableFunctionResult(fp);
        fResults.copyLocationAwareProperties(this);
        Object obj = findAncestorWithClass(FunctionResultRecordable.class);
        if (obj != null) {
            ((FunctionResultRecordable)obj).recordFunctionResult(fResults);
        }
    }

    public void tearDown(){
        ResultsReporter.getInstance().getHtmlTestRunReporter().setWriter(timestampedResults);
        jmln = null;
        TestCaseEventHandler.getInstance().removeTestCaseListener(listener);
        reverseTestCaseCount();
        ResultsReporter.getInstance().getSimpleTestRunReporter().setWriter(stdOutWriter);
    }

    /**
     * Rolls back the test case execution count. We do this so that the test case
     * inside the script being run does not increment in the total # runs in the case
     * it is data-driven. Basically, one script equals one test case run.
     */
    private void reverseTestCaseCount(){
        ResultsReporter.getInstance().setTestCaseCounter(originalCounter);
    }

    protected String executeScript(){
        return jmln.executeJellyScript(script);
    }

    protected void assertFunctionIdNotFailed(String noFailOnFunctionId){
        boolean found = false;
        JameleonTestResult jtr;
        for (Iterator it = result.getFailedResults().iterator(); it.hasNext() && !found;) {
            jtr = (JameleonTestResult)it.next();
            found = noFailOnFunctionId.equals(jtr.getIdentifier());
        }
        assertFalse(noFailOnFunctionId +" was found in the list of failed results", found);
    }

    protected void assertOutcomeEquals(String expected){
        assertEquals("Outcome of testcase: ", expected, getOutcome());
    }

    protected void assertOutcomePassed(){
        assertOutcomeEquals("PASSED");
    }

    protected void assertOutcomeFailed(){
        assertOutcomeEquals("FAILED");
    }

    protected void assertNumOfFunctionsRun(int expected){
        assertEquals("Num of functional points run: ", expected, getFunctionsRun().size());
    }

    protected void assertNumOfFailures(int expected){
        assertEquals("Num of tags failed: ", expected, result.getFailedResults().size());
    }

    protected void assertNumOfTestCasesRun(int expected){
        assertEquals("Num of test cases run: ", expected, getTestCasesRun());
    }

    protected void assertNumOfTestCasesFailed(int expected){
        assertEquals("Num of test cases failed: ", expected, getTestCasesFailed());
    }

    protected void assertTestCaseDocFileEndsWith(String expected){
        String pathFixedDocFile = JameleonUtility.fixFileSeparators(expected);
        assertTrue("Test Case doc file should end with <"+expected+"> was <"+getTestCaseDocFile()+">", getTestCaseDocFile().endsWith(pathFixedDocFile));
    }

    protected void assertExecutionTimeLessThan(long lessThanTime){
        long execTime = getExecutionTime();
        assertTrue("Execution Time <"+execTime+"> should be less than <"+lessThanTime+">", execTime < lessThanTime);
    }

    protected void assertExecutionTimeGreaterThan(long greaterThanTime){
        long execTime = getExecutionTime();
        assertTrue("Execution Time <"+execTime+"> should be greater than <"+greaterThanTime+">", execTime > greaterThanTime);
    }


    protected String getOutcome(){
        return result.getOutcome();
    }

    protected List getFunctionsRun(){
        List functionsRun = new ArrayList();
        JameleonTestResult jtr;
        for (Iterator it = result.getAllChildrenResults().iterator(); it.hasNext();) {
            jtr = (JameleonTestResult)it.next();
            if (jtr instanceof FunctionResult) {
                functionsRun.add(jtr);
            }
        }
        return functionsRun;
    }

    protected int getTestCasesRun(){
        return result.getCountableResults().size();
    }

    protected int getTestCasesFailed(){
        return result.getFailedCountableResults().size();
    }

    protected long getExecutionTime(){
        return result.getExecutionTime();
    }

    protected String getTestCaseDocFile(){
        return result.getTestCaseDocsFile();
    }

    protected void assertHtmlTestCaseName(String expected){
        assertEquals("The displayed test case name: ", expected, getHtmlDisplayedTestCaseName());
    }

    protected String getHtmlDisplayedOutcome(){
        String row = stripHtmlRow();
        row = row.substring(row.indexOf(">")+1, row.indexOf("</td>"));
        return row;
    }

    protected String getHtmlDisplayedTestCaseName(){
        Document pHtml = getHtmlDocument();
        Node node = pHtml.selectSingleNode( "//span[@title='Test Case Name']/a" );
        return node.getText().trim();
    }

    protected String stripHtmlRow(){
        String row = getRowFromHtml();
        String subString = null;
        try{
            subString = row.substring(row.indexOf("</td>")+6);
        }catch(Exception e){
            System.err.println(row);
            e.printStackTrace();
        }
        return subString;
    }

    protected String getRowFromHtml(){
        return htmlResults.toString();
    }

    protected String getValueFromElement(String elementName, String message){
        String startElement = "<"+elementName+">";
        String endElement = "</"+elementName+">";
        int indexS = message.indexOf(startElement);
        int indexE = message.indexOf(endElement);
        String value;
        if (indexS >= 0 && indexE >= 0) {
            value = message.substring((indexS+startElement.length()),indexE);
        }else{
            value = "N/A";
        }
        return value;
    }

    protected Document getHtmlDocument(){
    	return getDocument(htmlResults);
    }

    protected Document getDocument(StringWriter results){
        //Make sure the document is well formed, by giving it a root tag
        String res = "<root>" + results.toString()+"</root>";
        StringReader sr = new StringReader(res);
    	Document doc = null;
        try{
            doc = parser.read(sr);
        }catch(DocumentException de){
             de.printStackTrace();
             fail("Error happened executing: "+script.getPath());
        }finally{
        	sr.close();
        }
        return doc;
    	
    }

    protected class FailedRow{
        public int lineNum = 0;
        public String functionId = null;
        public String errMsg = null;
    }

}
