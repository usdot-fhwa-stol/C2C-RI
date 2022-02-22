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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.result.*;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class HtmlTestCaseResultGeneratorTest extends TestCase {

    private FunctionalPoint tag;
    private HtmlTestCaseResultGenerator generator;
    private TestCaseTag tct;
    protected static SAXReader parser = new SAXReader();


    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(HtmlTestCaseResultGeneratorTest.class);
    }

    public HtmlTestCaseResultGeneratorTest(String name) {
        super(name);
    }

    public void setUp(){
        tag = createFp("foo", new String[]{"do something", "do something else"}, new String[] {"attr1", "attr2"},
                        new String[]{"attr1 value", "attr2 value"}, "a long description of the tag", "action");
        tct = new TestCaseTag();
        tct.getTestCase().setAuthor("Christian");
        tct.getTestCase().setApplication("jemeleon");
        tct.getTestCase().setFunctionalPointTested("html results");
        tct.getTestCase().setName("html results test");
        tct.getTestCase().setOrganization("sourceforge");
        tct.getTestCase().setSummary("a test that does something");
        tct.getTestCase().setTestCaseId("123");
        tct.getTestCase().setTestCaseRequirement("req-e234");
        tct.getTestCase().setTestEnvironment("unit");
        LinkedList levels = new LinkedList();
        levels.add("Some level");
        levels.add("Another level");
        tct.getTestCase().setTestLevels(levels);
        tct.getTestCase().addBug("some bug");
        tct.getTestCase().addBug("another bug");
        tct.getTestCase().setFile(new File("tst/xml/framework/assertEqualsPass.xml").getPath());
        generator = new HtmlTestCaseResultGenerator(tct);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                             PAGE GENERATION TESTS                                            ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void testGenerateTestCaseResultSummaryPassed() throws Exception {
        TestCaseResult result = new TestCaseResult();
        result.setExecutionTime(3032);
        
        tct.setResults(result);
        tct.setGenTestCaseDocsEncoding("some-charset");
        String html = generator.generateTestCaseResultSummary();

        assertNotNull("Some HTML should have been generated", html);

        assertTrue("Should have found a character set", html.indexOf("charset=some-charset") != -1);

        assertNotNull("passing header",
                getNodeByXPath(getDocument(html),
                "//table[@class='result_passed' and @title='Result Header']"));

        assertXPathText("header", html,
                "//table[@class='result_passed' and @title='Result Header']//td[2]", "Passed");

        assertXPathText("Executio Time", html,
                "//td/div[text()='Execution Time']/parent::td/following-sibling::td/div", "3.03032s");

        assertXPathText("Number Run", html,
                "//td/div[text()='Number Run']/parent::td/following-sibling::td/div", "1");


        assertXPathText("Number Failed", html,
                "//td/div[text()='Number Failed']/parent::td/following-sibling::td/div", "0");


        assertXPathText("Percent Passed", html,
                "//td/div[text()='Percent Passed']/parent::td/following-sibling::td/div", "100%");

        assertNull("No Errors Should Exist",
                getNodeByXPath(getDocument(html),
                "//div[@title='Error Message']"));


    }

    public void testGenerateTestCaseResultSummaryFailed() throws Exception {
        TestCaseResult result = new TestCaseResult();
        result.setExecutionTime(3032);
        SessionResult sResult = new SessionResult(tag, result);
        sResult.setElementName(tag.getDefaultTagName());
        sResult.getTag().addApplication("Some Application");
        sResult.setExecutionTime(1000);
        sResult.setError(new RuntimeException("A funky error happened"));
        SessionResult sResult2 = new SessionResult(tag, result);
        sResult2.setElementName(tag.getDefaultTagName());
        sResult2.getTag().addApplication("Another Application");
        sResult2.setExecutionTime(2032);
        sResult2.setError(new RuntimeException("Another funky error happened"));
        tct.setResults(result);

        String html = generator.generateTestCaseResultSummary();

        assertNotNull("Some HTML should have been generated", html);
        assertTrue("Should have found a character set", html.indexOf("charset=UTF-8") != -1);

        assertNotNull("failing header",
                getNodeByXPath(getDocument(html),
                "//table[@class='result_failed' and @title='Result Header']"));

        assertXPathText("header", html,
                "//table[@class='result_failed' and @title='Result Header']//td[2]", "Failed");

        assertXPathText("Executio Time", html,
                "//td/div[text()='Execution Time']/parent::td/following-sibling::td/div", "3.03032s");

        assertXPathText("Number Run", html,
                "//td/div[text()='Number Run']/parent::td/following-sibling::td/div", "1");


        assertXPathText("Number Failed", html,
                "//td/div[text()='Number Failed']/parent::td/following-sibling::td/div", "1");


        assertXPathText("Percent Passed", html,
                "//td/div[text()='Percent Passed']/parent::td/following-sibling::td/div", "0%");

        assertNotNull("Errors Should Exist",
                getNodeByXPath(getDocument(html),
                "//div[@title='Error Message']"));

        assertXPathText("The first error message", html,
                "//div[@title='Summary of Failures']/following-sibling::div[@title='Error Message']", "A funky error happened");

        assertXPathText("The 2nd error message", html,
                "//div[@title='Summary of Failures']/following-sibling::div[position() = 2 and @title='Error Message']", "Another funky error happened");


    }

    public void testGenerateTestCasePage() throws Exception {
        net.sf.jameleon.bean.TestCase tc = tct.getTestCase();
        tct.setGenTestCaseDocsEncoding("some-charset");
        String html = generator.generateTestCasePage();

        assertXPathText("page title", html,
                "//title", tc.getName() + " Results");
        assertTrue("Should have found a character set", html.indexOf("charset=some-charset") != -1);

        String src = getNodeTextByXPath(getDocument(html), "//div[@class='source']/pre");
        //Dom4j resolves escaped XML as actual XML. Need to convert it back, but first make sure we get it the way
        //expected.
        assertTrue("There should be a > in the source", src.indexOf("x") > -1);
        src = JameleonUtility.decodeTextToXML(src.trim());
        assertEquals("source code", tc.getScriptContents().trim(), src);
        assertXPathNode("Couldn't find actual reslts", html, "//div[@class='begin_result']");
    }


    public void testGenerateTestCaseSummary() throws Exception {
        TestCaseResult result = new TestCaseResult();
        tct.setResults(result);
        net.sf.jameleon.bean.TestCase tc = tct.getTestCase();
        tct.setGenTestCaseDocsEncoding("some-charset");
        String html = generator.generateTestCaseSummary();

        assertNotNull("Some HTML should have been generated", html);
        assertTrue("Should have found a character set", html.indexOf("charset=some-charset") != -1);

        assertXPathText("date/time executed", html,
                "//td/div[text()='Time Executed']/ancestor::td/following-sibling::td/div", result.getDateTimeExecuted().getTime().toString());

        assertXPathText("Test Case Name", html,
                "//td/div[text()='Test Case']/ancestor::td/following-sibling::td/div", tc.getName());

        assertXPathText("Test Case Summary", html,
                "//td/div[text()='Summary']/ancestor::td/following-sibling::td/div", tc.getSummary());

        assertXPathText("Test Case ID", html,
                "//td/div[text()='Test Case ID']/ancestor::td/following-sibling::td/div", tc.getTestCaseId());

        assertXPathText("Requirement ID", html,
                "//td/div[text()='Requirement ID']/ancestor::td/following-sibling::td/div", tc.getTestCaseRequirement());

        assertXPathText("Author", html,
                "//td/div[text()='Author']/ancestor::td/following-sibling::td/div", tc.getAuthor());

        assertXPathText("Application Tested", html,
                "//td/div[text()='Application Tested']/ancestor::td/following-sibling::td/div", tc.getApplication());

        assertXPathText("Feature Tested", html,
                "//td/div[text()='Feature Tested']/ancestor::td/following-sibling::td/div", tc.getFunctionalPointTested());

        assertXPathText("Test Environment", html,
                "//td/div[text()='Test Environment']/ancestor::td/following-sibling::td/div", tc.getTestEnvironment());

        assertXPathText("Organization", html,
                "//td/div[text()='Organization']/ancestor::td/following-sibling::td/div", tc.getOrganization());

        assertXPathText("Bug #1", html,
                "//div[@id='bugs']/a[1]", tc.getBugs().toArray()[0].toString());

        assertXPathText("Bug #2", html,
                "//div[@id='bugs']/a[2]", tc.getBugs().toArray()[1].toString());
        int numOfBugs = tc.getBugs().size();
        assertNull("Only two bugs should exist",
                getNodeByXPath(getDocument(html),
                "//div[@id='bugs']/a["+(numOfBugs+1)+"]"));


        assertXPathText("Test Level #1", html,
                "//td/div[text()='Test Level(s)']/ancestor::td/following-sibling::td/div",
                " "+tc.getTestLevels().get(0).toString() + "  " +tc.getTestLevels().get(1) + " ");

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                             TEST CASE RESULT TESTS                                       ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void testGenerateAppropriateResultTestCaseNoCSV(){
        TestCaseResult result = new TestCaseResult(tag);
        result.setElementName(tag.getDefaultTagName());
        SessionResult sResult = new SessionResult(tag, result);
        sResult.setElementName(tag.getDefaultTagName());
        sResult.getTag().addApplication("Some Application");

        HtmlTestCaseResultGeneratorGenerateResultsForTestCaseStub generatorStub =
                new HtmlTestCaseResultGeneratorGenerateResultsForTestCaseStub(new TestCaseTag());

        generatorStub.generateAppropriateResult(result);
        assertTrue("testcase results were not generated", generatorStub.generateResultsForChildren);
    }


    public void testGenerateAppropriateResultTestCaseUseCSV(){
        TestCaseResult result = new TestCaseResult(tag);
        CountableDataDrivableResultContainer RowContainer = new CountableDataDrivableResultContainer(tag, result);

        result.setElementName(tag.getDefaultTagName());
        HtmlTestCaseResultGeneratorGenerateResultsForTestCaseStub generatorStub =
                new HtmlTestCaseResultGeneratorGenerateResultsForTestCaseStub(new TestCaseTag());

        generatorStub.generateAppropriateResult(result);
        assertTrue("testcase results were not generated", generatorStub.generateResultsForChildren);
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                             DATA-DRIVABLE RESULT TESTS                                       ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void testGenerateDDRowResultPass() throws Exception {
        FunctionalPoint ddTag = createFp("foo", new String[]{"do something", "do something else"}, new String[] {"attr1", "attr2"},
                        new String[]{"attr1 value", "attr2 value"}, "a long description of the tag", "action");

        DataDrivableRowResult result = new DataDrivableRowResult(ddTag);
        result.setElementName("row-sessionResult");

        Map rowData = new HashMap();
        rowData.put("zero", null);
        rowData.put("one", "1");
        rowData.put("two", "2");
        rowData.put("three", "3");
        rowData.put("four", null);
        result.setRowData(rowData);

        SessionResult sessionResult = new SessionResult(tag);
        sessionResult.setElementName(tag.getDefaultTagName());
        sessionResult.getTag().addApplication("Some Application");
        sessionResult.setExecutionTime(1000);

        FunctionalPoint tag2 = createFp("st-child", new String[]{"check something", "check something else"},
                new String[] {"attr3", "attr4"}, new String[]{"attr3 value", "attr4 value"}, "description", "navigation");
        tag2.setFunctionId("a tag that does something");
        FunctionResult functionResult = new FunctionResult(tag2);
        functionResult.setElementName(tag2.getDefaultTagName());
        sessionResult.addChildResult(functionResult);

        FunctionalPoint tag3 = createFp("st-child2", new String[]{"check something2", "check something else2"},
                new String[] {"attr5", "attr6"}, new String[]{"attr5 value", "attr6 value"}, "description", "navigation");
        tag3.setFunctionId("a tag that does something else");
        FunctionResult functionResult2 = new FunctionResult(tag3);
        functionResult2.setElementName(tag3.getDefaultTagName());
        sessionResult.addChildResult(functionResult2);

        result.addChildResult(sessionResult);

        String html = generator.generateDDRowResult(result);

        assertNotNull("An passing doc image was not found",
                getNodeByXPath(getDocument(html),
                "//div[@title='Data Driven Result']//img[ends-with(@src, 'results_good.gif')]"));

        assertXPathText("Data Entry 'zero'", html,
                "//table[@title='data entries']//td[text()='zero']/following-sibling::td",
                "");

        assertXPathText("Data Entry 'one'", html,
                "//table[@title='data entries']//td[text()='one']/following-sibling::td",
                rowData.get("one").toString());

        assertXPathText("Data Entry 'two'", html,
                "//table[@title='data entries']//td[text()='two']/following-sibling::td",
                rowData.get("two").toString());

        assertXPathText("Data Entry 'three'", html,
                "//table[@title='data entries']//td[text()='three']/following-sibling::td",
                rowData.get("three").toString());

        assertXPathText("Data Entry 'four'", html,
                "//table[@title='data entries']//td[text()='four']/following-sibling::td",
                "");

        assertResultAttributes(html, result.getTag(), sessionResult, "Session Result", "info_blue_bkg.gif");
        assertNull("An error message should not exist", getNodeByXPath(getDocument(html), "//div[@title='Error Information']"));

        assertFunctionHtml(tag2, functionResult);
        assertFunctionHtml(tag3, functionResult2);

    }

    public void testGenerateDDRowResultError() throws Exception {
        FunctionalPoint ddTag = createFp("foo", new String[]{"do something", "do something else"}, new String[] {"attr1", "attr2"},
                        new String[]{"attr1 value", "attr2 value"}, "a long description of the tag", "action");

        DataDrivableRowResult result = new DataDrivableRowResult(ddTag);
        result.setElementName("row-sessionResult");

        Map rowData = new HashMap();
        rowData.put("one", "1");
        rowData.put("two", "2");
        rowData.put("three", "3");
        result.setRowData(rowData);

        result.setError(new RuntimeException("data error"));


        String html = generator.generateDDRowResult(result);

        assertNotNull("An error doc image was not found",
                getNodeByXPath(getDocument(html),
                "//div[@title='Data Driven Result']//img[ends-with(@src, 'results_error.gif')]"));

        assertXPathText("Data Entry 'one'", html,
                "//table[@title='data entries']//td[text()='one']/following-sibling::td",
                rowData.get("one").toString());

        assertXPathText("Data Entry 'two'", html,
                "//table[@title='data entries']//td[text()='two']/following-sibling::td",
                rowData.get("two").toString());

        assertXPathText("Data Entry 'three'", html,
                "//table[@title='data entries']//td[text()='three']/following-sibling::td",
                rowData.get("three").toString());

        assertNotNull("An error message should exist", getNodeByXPath(getDocument(html), "//div[@title='Data Driven Result']//div[@title='Error Information']"));
        assertStackTrace(html, result.getHtmlFormattedStackTrace());

    }

    public void testGenerateAppropriateResultDDResultContainer(){
        DataDrivableResultContainer result = new DataDrivableResultContainer(tag);
        result.setElementName(tag.getDefaultTagName());
        DataDrivableRowResult cResult1 = new DataDrivableRowResult(tag);
        cResult1.setElementName("row-result");
        result.addChildResult(cResult1);

        HtmlTestCaseResultGeneratorGenerateAppropStub generatorStub =
                new HtmlTestCaseResultGeneratorGenerateAppropStub(new TestCaseTag());

        generatorStub.generateAppropriateResult(result);
        assertEquals("# of child results called", 1, generatorStub.numOfTimeDDRowResultCalled);

        generatorStub.numOfTimeDDRowResultCalled = 0;
        DataDrivableRowResult cResult2 = new DataDrivableRowResult(tag);
        cResult2.setElementName("row-result");
        result.addChildResult(cResult2);

        generatorStub.generateAppropriateResult(result);
        assertEquals("# of child results called", 2, generatorStub.numOfTimeDDRowResultCalled);

    }

    public void testGenerateAppropriateResultDDRowResult(){
        DataDrivableRowResult result = new DataDrivableRowResult(tag);
        result.setElementName(tag.getDefaultTagName());
        HtmlTestCaseResultGeneratorGenerateAppropStub generatorStub =
                new HtmlTestCaseResultGeneratorGenerateAppropStub(new TestCaseTag());

        generatorStub.generateAppropriateResult(result);
        assertTrue("function results were not generated", generatorStub.genDDRowResultCalled);
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                             SESSION RESULT TESTS                                             ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void testGenerateSessionResultWithAppAndError() throws Exception{
        SessionResult result = new SessionResult(tag);
        result.setElementName(tag.getDefaultTagName());
        result.getTag().addApplication("Some Application");
        result.setExecutionTime(1000);
        result.setError(new RuntimeException("A funky error happened"));

        String html = generator.generateSessionResult(result);

        assertXPathText("Session Title", html,
                "//div[@title='Session Result']//td[@title='Session Identifier']",
                result.getTag().getApplications().get(0).toString());

        assertResultAttributes(html, result.getTag(), result, "Session Result", "info_blue_bkg.gif");
        assertStackTrace(html, result.getHtmlFormattedStackTrace());
    }

    public void testGenerateSessionResultWithApp() throws Exception{
        SessionResult result = new SessionResult(tag);
        result.setElementName(tag.getDefaultTagName());
        result.getTag().addApplication("Some Application");
        result.setExecutionTime(1000);

        FunctionalPoint tag2 = createFp("st-child", new String[]{"check something", "check something else"},
                new String[] {"attr3", "attr4"}, new String[]{"attr3 value", "attr4 value"}, "description", "navigation");
        tag2.setFunctionId("a tag that does something");
        FunctionResult functionResult = new FunctionResult(tag2);
        functionResult.setElementName(tag2.getDefaultTagName());
        result.addChildResult(functionResult);

        FunctionalPoint tag3 = createFp("st-child2", new String[]{"check something2", "check something else2"},
                new String[] {"attr5", "attr6"}, new String[]{"attr5 value", "attr6 value"}, "description", "navigation");
        tag3.setFunctionId("a tag that does something else");
        FunctionResult functionResult2 = new FunctionResult(tag3);
        functionResult2.setElementName(tag3.getDefaultTagName());
        result.addChildResult(functionResult2);
        String html = generator.generateSessionResult(result);

        assertXPathText("Session Title", html,
                "//div[@title='Session Result']//td[@title='Session Identifier']",
                result.getTag().getApplications().get(0).toString());

        assertResultAttributes(html, result.getTag(), result, "Session Result", "info_blue_bkg.gif");
        assertNull("An error message should not exist", getNodeByXPath(getDocument(html), "//div[@title='Error Information']"));

        assertFunctionHtml(tag2, functionResult);
        assertFunctionHtml(tag3, functionResult2);
    }

    public void testGenerateSessionResultNoApp() throws Exception{
        SessionResult result = new SessionResult(tag);
        result.setElementName(tag.getDefaultTagName());
        result.setExecutionTime(1000);

        FunctionalPoint tag2 = createFp("st-child", new String[]{"check something", "check something else"},
                new String[] {"attr3", "attr4"}, new String[]{"attr3 value", "attr4 value"}, "description", "navigation");
        tag2.setFunctionId("a tag that does something");
        FunctionResult functionResult = new FunctionResult(tag2);
        functionResult.setElementName(tag2.getDefaultTagName());
        result.addChildResult(functionResult);

        FunctionalPoint tag3 = createFp("st-child2", new String[]{"check something2", "check something else2"},
                new String[] {"attr5", "attr6"}, new String[]{"attr5 value", "attr6 value"}, "description", "navigation");
        tag3.setFunctionId("a tag that does something else");
        FunctionResult functionResult2 = new FunctionResult(tag3);
        functionResult2.setElementName(tag3.getDefaultTagName());
        result.addChildResult(functionResult2);
        String html = generator.generateSessionResult(result);

        assertXPathText("Session Title", html,
                "//div[@title='Session Result']//td[@title='Session Identifier']",
                result.getTag().getDefaultTagName());
        assertResultAttributes(html, result.getTag(), result, "Session Result", "info_blue_bkg.gif");
        assertNull("An error message should not exist", getNodeByXPath(getDocument(html), "//div[@title='Error Information']"));
    }

    public void testGenerateAppropriateResultSessionResult(){
        SessionResult sessionResult = new SessionResult(tag);
        sessionResult.setElementName(tag.getDefaultTagName());
        HtmlTestCaseResultGeneratorGenerateAppropStub generatorStub =
                new HtmlTestCaseResultGeneratorGenerateAppropStub(new TestCaseTag());

        generatorStub.generateAppropriateResult(sessionResult);
        assertTrue("session results were not generated", generatorStub.genSessionResultCalled);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                             FUNCTION RESULT TESTS                                            ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void testGenerateFunctionResultPass() throws Exception {
        tag.setFunctionId("a tag that does something");
        FunctionResult result = new FunctionResult(tag);
        result.setElementName(tag.getDefaultTagName());
        String html = generator.generateFunctionResult(result);

        assertFunctionHtml(tag, result);
        assertNull("no error msg should be available", getNodeByXPath(getDocument(html), "//div[@title='Error Information']"));
        assertNull("no stack trace should be available", getNodeByXPath(getDocument(html), "//div[@title='Error Information']//span[@title='Stack Trace']"));
    }

    public void testGenerateFunctionResultFail() throws Exception {
        tag.setFunctionId("a tag that does something");
        FunctionResult result = new FunctionResult(tag);
        result.setExecutionTime(7000);
        result.setElementName(tag.getDefaultTagName());
        result.setError(new RuntimeException("this tag failed"));
        String html = generator.generateFunctionResult(result);

        assertXPathText("error msg", html,
                "//div[@title='Error Information']//span[@title='Error Message']",
                result.getHtmlFormattedErrorMsg());

        assertStackTrace(html, result.getHtmlFormattedStackTrace());
        assertNull("no snapshot should be available", getNodeByXPath(getDocument(html), "//div[@title='Error Information']//img[@alt='snapshot']"));
    }

    public void testGenerateFunctionResultFailWithSnapshot() throws Exception {
        tag.setFunctionId("a tag that does something");
        FunctionResult result = new FunctionResult(tag);
        result.setExecutionTime(7000);
        result.setElementName(tag.getDefaultTagName());
        result.setError(new RuntimeException("this tag failed"));
        result.setErrorFile(new File("."));
        String html = generator.generateFunctionResult(result);

        assertXPathText("error msg", html,
                "//div[@title='Error Information']//span[@title='Error Message']",
                result.getHtmlFormattedErrorMsg());

        assertStackTrace(html, result.getHtmlFormattedStackTrace());
        assertNotNull("A snapshot should be available", getNodeByXPath(getDocument(html), "//div[@title='Error Information']//a[@href='../../../.']/img[@alt='snapshot']"));
    }

    public void testGenerateAppropriateResultFunctionResult(){
        FunctionResult result = new FunctionResult(tag);
        result.setElementName(tag.getDefaultTagName());
        HtmlTestCaseResultGeneratorGenerateAppropStub generatorStub =
                new HtmlTestCaseResultGeneratorGenerateAppropStub(new TestCaseTag());

        generatorStub.generateAppropriateResult(result);
        assertTrue("function results were not generated", generatorStub.genFunctionResultCalled);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                             LEGACY RESULT TESTS                                              ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void testGenerateResultsForChildren(){
        SessionResult sessionResult = new SessionResult(tag);
        sessionResult.setElementName(tag.getDefaultTagName());
        FunctionResult fr;
        for(int i = 1; i < 5; i++){
            fr = new FunctionResult(
                    createFp("fp"+i,new String[]{}, new String[]{"baseUrl"},
                            new String[]{"localhost"}, "a session tag", "session")
                  );
            sessionResult.addChildResult(fr);
        }
        HtmlTestCaseResultGeneratorGenerateResultsForChildrenStub generatorStub =
                new HtmlTestCaseResultGeneratorGenerateResultsForChildrenStub(new TestCaseTag());
        generatorStub.generateResultsForChildren(sessionResult);
        assertEquals("# of children processed", 4, generatorStub.numberOfChildren);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                                             HELPER METHODS                                                   ///
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void assertStackTrace(String html, String expectedStack) throws Exception{
        String stackTrace = getNodeTextByXPath(getDocument(html), "//div[@title='Error Information']//span[@title='Stack Trace']/pre");
        assertEquals("stack trace", stackTrace.trim(), windowsToUnix(expectedStack).trim());
    }

    // allow *nix syntax in unit tests
    private String windowsToUnix(final String str) {
        return str.replaceAll("\\r\\n","\n").replaceAll("\\\\","/");
    }

    private void assertFunctionHtml(FunctionalPoint tag, FunctionResult result) throws Exception{
        String html = windowsToUnix(generator.generateFunctionResult(result));
        boolean passed = result.passed();
        String style = "txtFailed";
        String functionIcon = "doc-errored.gif";
        if (passed){
            style = "txtPassed";
            functionIcon = "doc-passed.gif";
        }
        assertXPathText("function Id", html,
                "//div[@title='Action Information']//span[@title='Action Description' and @class='"+style+"']",
                tag.getFunctionId());

        assertXPathNode("The appropriate function icon was not found", html,
                "//div[@title='Action Information']//img[@alt='function' and @src='../../images/"+functionIcon+"']");

        int i = 1;
        for (Iterator it = tag.getSteps().iterator(); it.hasNext(); i++){
            String step = it.next().toString();
            assertXPathText("step " + i, html,
                    "//div[@title='Action Information']//div[@title='Action Steps']//tr["+i+"]/td",
                    i + "- " + step);
        }

        assertResultAttributes(html, tag, result, "Action Information", "info_white_blue_bkg.gif");
    }

    private void assertResultAttributes(String html, FunctionalPoint tag, JameleonTestResult result, String title, String imageSrc) throws Exception {
        assertXPathNode("attribute icon", html,
                "//div[@title='"+title+"']//div[@title='Attributes']/a/img[ends-with(@src, '"+imageSrc+"') ]");

        assertXPathText("attribute - tag name", html,
                "//div[@title='"+title+"']//div[@title='Attributes']//tr/td[text()='tag']/following-sibling::td",
                tag.getDefaultTagName());

        assertXPathText("attribute - time", html,
                "//div[@title='"+title+"']//div[@title='Attributes']//tr/td[text()='exec time']/following-sibling::td",
                result.getExecutionTimeToDisplay());

        assertXPathText("attribute - time", html,
                "//div[@title='"+title+"']//div[@title='Attributes']//tr/td[text()='date time']/following-sibling::td",
                result.getDateTimeExecuted().getTime().toString());

        for (Iterator it = tag.getAttributes().keySet().iterator(); it.hasNext(); ){
            Attribute attr = tag.getAttribute(it.next().toString());
            assertXPathText("attribute - " + attr.getName(), html,
                    "//div[@title='"+title+"']//div[@title='Attributes']//td[text()='"+attr.getName()+"']/following-sibling::td",
                    attr.getValue().toString());
        }
    }

    private FunctionalPoint createFp(String tagName, String[] steps, String[] attrNames, String[] attrValues, String desc, String type){
        FunctionalPoint fp = new FunctionalPoint();
        fp.addTagName(tagName);
        for(int i = 0; i < steps.length; i++){
            fp.addStep(steps[i]);
        }
        for(int i = 0; i < attrNames.length; i++){
            Attribute attr = new Attribute();
            attr.setName(attrNames[i]);
            attr.setValue(attrValues[i]);
            fp.addAttribute(attr);
        }
        fp.setDescription(desc);
        fp.setType(type);
        return fp;
    }

    private Document getDocument(String results) throws Exception{
        results = windowsToUnix(results);
        //Make sure the document is well formed, by giving it a root tag
        String res = "<root>" + results + "</root>";
        StringReader sr = new StringReader(res);
    	Document doc = null;
        try{
            doc = parser.read(sr);
        }finally{
        	sr.close();
        }
        return doc;

    }

    private void assertXPathText(String errMsg, String html, String xpath, String expectedText) throws Exception {
        String text = getNodeTextByXPath(getDocument(html), xpath);
        assertNotNull(errMsg + ": "+xpath + " did not return a valid result ", text);
        assertEquals(errMsg, expectedText.trim(), text.trim());
    }

    private void assertXPathNode(String errMsg, String html, String xpath) throws Exception{
        Node node = getNodeByXPath(getDocument(html), xpath);
        assertNotNull(errMsg, node);
    }

    private Node getNodeByXPath(Document doc, String xpath){
        return doc.selectSingleNode( xpath );
    }

    private String getNodeTextByXPath(Document doc, String xpath){
        Node node = getNodeByXPath(doc, xpath);
        String text = null;
        if (node != null){
            text = node.getText();
        }
        return text;
    }

    private class HtmlTestCaseResultGeneratorGenerateResultsForChildrenStub extends HtmlTestCaseResultGenerator{

        private short numberOfChildren;

        public HtmlTestCaseResultGeneratorGenerateResultsForChildrenStub(TestCaseTag tct){
            super(tct);
        }

        public String generateAppropriateResult(JameleonTestResult result){
            numberOfChildren ++;
            return "";
        }

    }

    private class HtmlTestCaseResultGeneratorGenerateResultsForTestCaseStub extends HtmlTestCaseResultGenerator{

        private boolean generateResultsForChildren;

        public HtmlTestCaseResultGeneratorGenerateResultsForTestCaseStub(TestCaseTag tct){
            super(tct);
        }

        public String generateResultsForChildren(TestResultWithChildren result){
            generateResultsForChildren = true;
            return "";
        }

    }

    private class HtmlTestCaseResultGeneratorGenerateAppropStub extends HtmlTestCaseResultGenerator{

        private boolean genSessionResultCalled;
        private boolean genFunctionResultCalled;
        private boolean genDDRowResultCalled;
        private int numOfTimeDDRowResultCalled;

        public HtmlTestCaseResultGeneratorGenerateAppropStub(TestCaseTag tct){
            super(tct);
        }

        public String generateSessionResult(SessionResult result){
            genSessionResultCalled = true;
            return "";
        }

        public String generateFunctionResult(FunctionResult result){
            genFunctionResultCalled = true;
            return "";
        }

        public String generateDDRowResult(DataDrivableRowResult result){
            genDDRowResultCalled = true;
            numOfTimeDDRowResultCalled++;
            return "";
        }



    }

}
