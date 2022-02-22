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

import java.io.StringWriter;
import java.util.Calendar;

public class SimpleTestRunReporterTest extends TestCase {

    private SimpleTestRunReporter reporter;
    private StringWriter writer;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(SimpleTestRunReporterTest.class);
    }

    public SimpleTestRunReporterTest(String name) {
        super(name);
    }

    public void setUp() throws Exception{
        reporter = new SimpleTestRunReporter();
        writer = new StringWriter();
        reporter.setWriter(writer);
    }

    public void testReportTestRunStart() throws Exception{
        Calendar startTime = Calendar.getInstance();
        reporter.reportTestRunStart(startTime);
        String header = writer.toString();
        assertTrue("The writer was not populated", header.length() == 0);
    }

    public void testReportTestRunComplete() throws Exception{
        TestCaseCounter counter = new TestCaseCounter();
        counter.incrementPassed(12);
        counter.incrementFailed(12);
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = (Calendar)startTime.clone();
        endTime.set(Calendar.SECOND, startTime.get(Calendar.SECOND) + 3);
        reporter.reportTestRunComplete(startTime, endTime, counter);
        String results = writer.toString();
        assertTrue("The writer was populated with something", results.length() > 0);
        assertTrue("execution time " + results, results.indexOf("Time: 3.000s") >= 0);
        assertTrue("total executed", results.indexOf("Tests run: 24") > 0);
        assertTrue("total failed", results.indexOf("Failed: 12") > 0);
    }


    public void testReportScriptResult(){
        TestCaseTag tct = initTestCaseTag();
        TestCaseCounter counter = new TestCaseCounter();
        reporter.reportScriptResult(tct, counter);

        StringWriter writer = (StringWriter)reporter.getWriter();
        String row = writer.toString();
        assertTrue("The writer was populated with something", row.length() > 0);
        assertTrue("script result", row.indexOf("testcase.xml : PASSED") >= 0);

        counter.incrementTestCaseNum(1);
        reporter.setWriter(new StringWriter());
        FunctionalPoint fp2 = new FunctionalPoint();
        fp2.addTagName("failed-tag");
        CountableFunctionResult cfr = new CountableFunctionResult(fp2, tct.getResults());
        cfr.setFailed();
        cfr.setError(new RuntimeException("an error message"));
        cfr.setLineNumber(23);
        fp2.setFunctionId("function identifier");
        reporter.reportScriptResult(tct, counter);
        writer = (StringWriter)reporter.getWriter();
        row = writer.toString();
        assertTrue("The writer was populated with something", row.length() > 0);
        assertTrue("Test case name",row.indexOf("testcase.xml : ") >=0 );
        assertTrue("Test case didn't failed",row.indexOf("FAILED") >=0 );
        assertTrue("The output should contain a failed line # " + row, row.indexOf("Line #: 23") >= 0);
        assertTrue("The output should contain a failed functionId " + row, row.indexOf("FunctionId: function identifier") >= 0);
        assertTrue("The output should contain a failed row info column " + row, row.indexOf("Row #: 0") > -1);
        assertTrue("The output should contain an error message " + row, row.indexOf("Error Message: an error message") > -1);

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
        tct.setFileName("some/dir/testcase.xml");
        return tct;
    }



}
