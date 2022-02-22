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
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.result.CountableFunctionResult;
import net.sf.jameleon.result.TestCaseResult;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonDefaultValues;

import java.io.StringWriter;
import java.io.File;
import java.util.Calendar;

public class HtmlTestRunReporterTest extends TestCase {

    HtmlTestRunReporter reporter;
    StringWriter writer;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(HtmlTestRunReporterTest.class);
    }

    public HtmlTestRunReporterTest(String name) {
        super(name);
    }

    public void setUp(){
        reporter = new HtmlTestRunReporter();
        writer = new StringWriter();
        reporter.setWriter(writer);
        Configurator.getInstance().setValue("genTestCaseDocsEncoding", "foo-encoding");
    }

    public void tearDown(){
        Configurator.getInstance().setValue("genTestCaseDocsEncoding", null);        
    }

    public void testGettersSetters(){
        assertEquals("Default results header", HtmlTestRunReporter.DEFAULT_HEADER_TEMPLATE, reporter.getTestRunResultsHeaderTemplate());
        reporter.setTestRunResultsHeaderTemplate("foo");
        assertEquals("results header", "foo", reporter.getTestRunResultsHeaderTemplate());

        assertEquals("Default results footer", HtmlTestRunReporter.DEFAULT_FOOTER_TEMPLATE, reporter.getTestRunResultsFooterTemplate());
        reporter.setTestRunResultsFooterTemplate("foo");
        assertEquals("results footer", "foo", reporter.getTestRunResultsFooterTemplate());

        assertEquals("Default result row", HtmlTestRunReporter.DEFAULT_RESULT_ROW_TEMPLATE, reporter.getTestResultRowTemplate());
        reporter.setTestResultRowTemplate("foo");
        assertEquals("results result row", "foo", reporter.getTestResultRowTemplate());
    }
    

    public void testReportTestRunStart() throws Exception{
        Configurator config = Configurator.getInstance();
        Calendar startTime = Calendar.getInstance();
        reporter.reportTestRunStart(startTime);
        String header = writer.toString();
        assertTrue("The writer was populated with something", header.length() > 0);
        assertTrue("Should have found a character set", header.indexOf("charset=foo-encoding") != -1);
    }

    public void testReportTestRunComplete() throws Exception{
        TestCaseCounter counter = new TestCaseCounter();
        counter.incrementPassed(12);
        counter.incrementFailed(12);
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = (Calendar) startTime.clone();
        endTime.set(Calendar.SECOND, startTime.get(Calendar.SECOND) + 3);
        reporter.reportTestRunComplete(startTime, endTime, counter);
        String results = writer.toString();
        assertTrue("The writer was populated with something", results.length() > 0);
        assertTrue("execution time " + results, results.indexOf("<td id=\"totalTime\">3.000s") >= 0);
        assertTrue("total executed", results.indexOf("<td id=\"totalRun\">24") > 0);
        assertTrue("total passed", results.indexOf("<td id=\"totalPassed\">12") > 0);
        assertTrue("total failed", results.indexOf("<td id=\"totalFailed\">12") > 0);
        assertTrue("percent passed", results.indexOf("<td id=\"percentPassed\">0%") > 0);
    }


    public void testReportScriptResult(){
        TestCaseTag tct = initTestCaseTag();
        TestCaseCounter counter = new TestCaseCounter();
        reporter.reportScriptResult(tct, counter);

        StringWriter writer = (StringWriter)reporter.getWriter();
        String row = writer.toString();
        assertTrue("The writer was populated with something", row.length() > 0);
        assertTrue("The file should contain a passed td " + row, row.indexOf("<img src=\"../images/check.png\" align=\"absmiddle\" style=\"padding-right:5px\" border=\"0\"/>some name") >= 0);
        assertFalse("The file should not contain a failed row info column", row.indexOf("title=\"failed info\"") > -1);

        counter.incrementTestCaseNum(1);
        reporter.setWriter(new StringWriter());
        FunctionalPoint fp2 = new FunctionalPoint();
        fp2.addTagName("failed-tag");
        CountableFunctionResult cfr = new CountableFunctionResult(fp2, tct.getResults());
        cfr.setFailed();
        cfr.setError(new RuntimeException("an error message"));
        cfr.setLineNumber(23);
        reporter.reportScriptResult(tct, counter);
        writer = (StringWriter)reporter.getWriter();
        row = writer.toString();
        assertTrue("The writer was populated with something", row.length() > 0);
        assertTrue("The file should contain a failed td " + row, row.indexOf("<img src=\"../images/stop.png\" align=\"absmiddle\" style=\"padding-right:5px\" border=\"0\"/>some name") >= 0);
        assertTrue("The file should contain a failed tag " + row, row.indexOf("<td title=\"FunctionId of Point of Failure\" style=\"width: 150px;\">failed-tag</td>") >= 0);

    }

    private TestCaseTag initTestCaseTag(){
        FunctionalPoint fp = new FunctionalPoint();
        fp.addTagName("tc");
        TestCaseResult tcr = new TestCaseResult(fp);
        tcr.setTestCaseDocsFile("some/dir/some_file.html");
        tcr.setTestName("some name");
        for (int i = 0; i < 3; i++){
            new CountableFunctionResult(fp, tcr);
        }
        TestCaseTag tct = new TestCaseTag();
        tct.setResults(tcr);
        tct.setResultsDir(new File(JameleonDefaultValues.RESULTS_DIR));
        return tct;
    }

}
