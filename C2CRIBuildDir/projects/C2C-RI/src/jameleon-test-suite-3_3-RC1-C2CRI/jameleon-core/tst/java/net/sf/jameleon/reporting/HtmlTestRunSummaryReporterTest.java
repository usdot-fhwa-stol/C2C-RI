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

import java.io.StringWriter;
import java.util.Calendar;

public class HtmlTestRunSummaryReporterTest extends TestCase {

    HtmlTestRunSummaryReporter reporter;
    StringWriter writer;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(HtmlTestRunSummaryReporterTest.class);
    }

    public HtmlTestRunSummaryReporterTest(String name) {
        super(name);
    }

    public void setUp(){
        reporter = new HtmlTestRunSummaryReporter();
        writer = new StringWriter();
        reporter.setWriter(writer);
    }

    public void testGettersSetters(){
        assertEquals("Default results header", HtmlTestRunSummaryReporter.DEFAULT_HEADER_TEMPLATE, reporter.getTestRunSummaryResultsHeaderTemplate());
        reporter.setTestRunSummaryResultsHeaderTemplate("foo");
        assertEquals("results header", "foo", reporter.getTestRunSummaryResultsHeaderTemplate());

        assertEquals("Default results begin row", HtmlTestRunSummaryReporter.DEFAULT_RESULT_BEGIN_ROW_TEMPLATE, reporter.getTestRunBeginResultRowTemplate());
        reporter.setTestRunBeginResultRowTemplate("foo");
        assertEquals("results footer", "foo", reporter.getTestRunBeginResultRowTemplate());

        assertEquals("Default result row", HtmlTestRunSummaryReporter.DEFAULT_RESULT_ROW_TEMPLATE, reporter.getTestRunResultRowTemplate());
        reporter.setTestRunResultRowTemplate("foo");
        assertEquals("results result row", "foo", reporter.getTestRunResultRowTemplate());

        assertFalse("printHeader should be false", reporter.isPrintHeader());
        reporter.setPrintHeader(true);
        assertTrue("printHeader should be true", reporter.isPrintHeader());
    }

//    public void testReportTestRunStart() throws Exception{
//        Calendar startTime = Calendar.getInstance();
//        reporter.setPrintHeader(false);
//        reporter.reportTestRunStart(startTime);
//        assertFalse("printHeader should be false", reporter.isPrintHeader());
//        String result = writer.toString();
//        assertTrue("The writer was populated with something", result.length() > 0);
//        String path = ReporterUtils.formatTime(startTime)+"/TestResults.html";
//        assertTrue("The writer should contain a link to the test run results file " + result, result.indexOf(path) > 0);
//    }

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
        String path = ReporterUtils.formatTime(startTime)+"/TestResults.html";
        assertTrue("The writer should contain a link to the test run results file " + results, results.indexOf(path) > 0);
        assertTrue("execution time " + results, results.indexOf("<td nowrap=\"nowrap\" style=\"padding-right:15px\">Execution Time: 3.000s</td>") >= 0);
        assertTrue("total executed " + results, results.indexOf("<td nowrap=\"nowrap\" style=\"padding-right:15px\">Total Run: 24</td>") > 0);
        assertTrue("total failed "  + results, results.indexOf("<td nowrap=\"nowrap\" style=\"padding-right:15px\">Total Failed: 12</td>") > 0);
        assertTrue("percent passed " + results, results.indexOf("<td nowrap=\"nowrap\" style=\"padding-right:15px\">Percent Passed: 50%</td>") > 0);
        assertTrue("The writer should contain an test id", results.indexOf("id=\""+ReporterUtils.formatTime(startTime)+"\"") > 0);
    }


}
