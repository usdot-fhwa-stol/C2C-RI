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
package org.fhwa.c2cri.plugin.c2cri.tags;


import net.sf.jameleon.plugin.junit.*;
import net.sf.jameleon.ExecuteTestCase;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.event.TestCaseEvent;
import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.event.TestCaseListener;
import net.sf.jameleon.reporting.ResultsReporter;
import net.sf.jameleon.reporting.TestCaseCounter;
import net.sf.jameleon.result.*;
import net.sf.jameleon.util.JameleonUtility;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Used to acceptance test Jameleon tags. Executes a Jameleon script and checks various outputs.
 * 
 * @author TransCore ITS, LLC
 * Last Updated:  10/13/2012
 * 
 * @jameleon.function name="ri-execute-script" type="action"
 * @jameleon.step Execute the given script
 * @jameleon.step Validate that the script ran correctly
 */
public class RIExecuteScriptTag extends JUnitFunctionTag {

    protected static final boolean DEBUG = false;
    /**
     * The script to execute
     * @jameleon.attribute required="true"
     */
    protected String script;
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
     * A functionId where no failure should occur
     * @jameleon.attribute
     */
    protected String failOnFunctionId;
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
     * A snippet of the error message or reason for that is expected in stdout.
     * @jameleon.attribute
     */
    protected String errorMsgContains;
//    /**
//     * A snippet of the error message or reason for that is expected in stdout.
//     * @jameleon.attribute
//     */
//    protected String testCaseName;

    protected StringWriter htmlResults;
    protected Writer timestampedResults;
    protected ExecuteTestCase jmln;
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
        // Transfer over the context variables from this test script to the test subscript
        jmln.setContextVariablesforRI(context.getParent());

        String errMsg = executeScript();
        if (errorMsgContains != null) {
            assertTrue("An expected error message was not provided! <"+errMsg+">", errMsg.indexOf(errorMsgContains) >= 0);
        }
        if (checkOutcomePassed) {
            assertTrue("Errors NOT expected, but found! \n"+errMsg, errMsg.length() == 0);
            assertOutcomePassed();
        }
        if (checkOutcomeFailed) {
            assertTrue("Errors expected, but none found!", errMsg.length() > 0);
            assertOutcomeFailed();
        }

        if (executionTimeGreaterThan != -1) {
            assertExecutionTimeGreaterThan(executionTimeGreaterThan);
        }
        if (executionTimeLessThan != -1) {
            assertExecutionTimeLessThan(executionTimeLessThan);
        }

        if (failOnFunctionId != null) {
            assertFunctionIdFailed(failOnFunctionId);
        }

    }

    protected void setupEnvironment(){
        super.setupEnvironment();
        postcondition = true;
    }

    public void setup(){
        postcondition = true;
        jmln = new ExecuteTestCase(DEBUG);

        timestampedResults = ResultsReporter.getInstance().getHtmlTestRunReporter().getWriter();
        htmlResults = new StringWriter();
        ResultsReporter reporter = ResultsReporter.getInstance();
        reporter.getHtmlTestRunReporter().setWriter(htmlResults);
        originalCounter = reporter.getTestCaseCounter();
        stdOutWriter = reporter.getSimpleTestRunReporter().getWriter();
        reporter.getSimpleTestRunReporter().setWriter(new StringWriter());
        reporter.setTestCaseCounter(new TestCaseCounter());

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

        URL scriptURL=null;
        try{

            scriptURL = new URL(script);
        } catch (Exception ex){
            ex.printStackTrace();
            try{
                System.out.println("RIExecuteScriptTag: Parent Context URL was "+this.getContext().getCurrentURL().toString() + "  Parent URI was "+this.getContext().getCurrentURL().toURI().toString());

                scriptURL = new URL(this.getContext().getCurrentURL().toString().replace("./", "")+script);
                System.out.println("RIExecuteScriptTag: Developed URL was "+scriptURL.toString());
            } catch (Exception ex2){
                javax.swing.JOptionPane.showMessageDialog(null, "RIExecuteScriptTag Error: "+ex2.getMessage(), "RIExecuteScriptTag Error", javax.swing.JOptionPane.ERROR_MESSAGE);
                ex2.printStackTrace();
            }
        }
        return jmln.executeJellyScript(scriptURL);

    }

    protected void assertFunctionIdFailed(String failOnFunctionId){
        boolean found = false;
        JameleonTestResult jtr;
        for (Iterator it = result.getFailedResults().iterator(); it.hasNext() && !found;) {
            jtr = (JameleonTestResult)it.next();
            found = failOnFunctionId.equals(jtr.getIdentifier());
        }
        assertTrue(failOnFunctionId +" was not found in the list of failed results", found);
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
             fail("Error happened executing: "+script);
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
