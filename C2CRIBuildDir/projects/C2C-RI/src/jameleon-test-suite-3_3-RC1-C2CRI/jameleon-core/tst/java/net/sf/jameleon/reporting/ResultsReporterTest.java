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
import net.sf.jameleon.event.TestCaseEvent;
import net.sf.jameleon.event.TestRunEvent;
import net.sf.jameleon.result.CountableFunctionResult;
import net.sf.jameleon.result.TestCaseResult;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonUtility;

import java.io.File;
import java.io.Writer;
import java.util.Calendar;

public class ResultsReporterTest extends TestCase {

    private ResultsReporter reporter;
    private static final File BASE_DIR = new File("jameleon_test_results");
    private TestRunReporterStub htmlTestRunReporter;
    private TestRunSummaryReporterStub htmlTestRunSummrayReporter;
    private TestRunReporterStub stdOutTestRunReporter;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(ResultsReporterTest.class);
    }

    public ResultsReporterTest(String name) {
        super(name);
    }

    public void setUp() throws Exception{
        super.setUp();
        reporter = ResultsReporter.getInstance();
        htmlTestRunReporter = new TestRunReporterStub();
        htmlTestRunSummrayReporter = new TestRunSummaryReporterStub();
        stdOutTestRunReporter = new TestRunReporterStub();
        reporter.setHtmlTestRunReporter(htmlTestRunReporter);
        reporter.setHtmlTestRunSummaryReporter(htmlTestRunSummrayReporter);
        reporter.setSimpleTestRunReporter(stdOutTestRunReporter);
    }

    public void tearDown() throws Exception {
        super.tearDown();
        ResultsReporter.clearInstance();
    }

    public void testGetSetTestCaseCounter(){
        reporter.setTestCaseCounter(null);
        assertNull("Expected a null TestCaseCounter", reporter.getTestCaseCounter());
        TestCaseCounter counter = new TestCaseCounter();
        reporter.setTestCaseCounter(counter);
        assertTrue("TestCaseCounter should be the same as the one set", counter == reporter.getTestCaseCounter());

    }

    public void testGetResultsDirectoryForTestCase(){
        TestCaseTag tct = new TestCaseTag();
        TestCaseResult tcr = new TestCaseResult();
        tcr.setTestName("some-name");
        tct.setResults(tcr);
        Calendar c = getCalendar(2007, Calendar.MAY, 18, 10, 23, 32, 725);
        reporter.setStartTime(c);
        File dir = reporter.getResultsDirectoryForTestCase(tct);
        assertEquals("test case res dir", JameleonUtility.fixFileSeparators(
                                            "./jameleon_test_results/20070518102332725/some-name"),
                                          dir.getPath());
    }

    public void testResetStats(){
        reporter.getTestCaseCounter().incrementFailed(5);
        reporter.getTestCaseCounter().incrementPassed(23);
        reporter.getTestCaseCounter().incrementTestCaseNum(55);
        reporter.resetStats();
        assertEquals("# failed", 0, reporter.getTestCaseCounter().getNumFailed());
        assertEquals("# passed", 0, reporter.getTestCaseCounter().getNumPassed());
        assertEquals("test case #", 1, reporter.getTestCaseCounter().getTestCaseNum());
    }

    public void testBeginTestRun(){
        Configurator config = Configurator.getInstance();
        config.setValue("organization", "sf");
        config.setValue("testEnvironment", "foo");
        Calendar startTime = Calendar.getInstance();
        reporter.beginTestRun(new TestRunEvent(startTime));
        assertEquals("Start time", startTime, reporter.getStartTime());
        assertTrue("reportTestRunStart wasn't called", htmlTestRunReporter.reportTestRunStartCalled);
        assertTrue("reportTestRunStart wasn't called", htmlTestRunSummrayReporter.reportTestRunStartCalled);
    }

    public void testEndTestRun(){
        reporter.getTestCaseCounter().incrementPassed(12);
        reporter.getTestCaseCounter().incrementFailed(12);
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        reporter.setStartTime(startTime);
        reporter.endTestRun(new TestRunEvent(endTime));
        assertEquals("# passed", 0, reporter.getTestCaseCounter().getNumPassed());
        assertEquals("# failed", 0, reporter.getTestCaseCounter().getNumFailed());
        assertEquals("test case #", 1, reporter.getTestCaseCounter().getTestCaseNum());
        assertTrue("reportTestRunComplete wasn't called in html results", htmlTestRunReporter.reportTestRunCompleteCalled);
        assertTrue("reportTestRunComplete wasn't called in test run summary results", htmlTestRunSummrayReporter.reportTestRunCompleteCalled);
        assertTrue("reportTestRunComplete wasn't called in stdout results", stdOutTestRunReporter.reportTestRunCompleteCalled);

    }

    public void testBeginTestCase(){
        Calendar c = getCalendar(2007, Calendar.MAY, 18, 10, 23, 32, 725);
        reporter.setStartTime(c);
        TestCaseTag tct = initTestCaseTag();
        assertNull("timestamped results dir", tct.getTimestampedResultsDir());
        reporter.beginTestCase(new TestCaseEvent(tct));
        assertNotNull("timestamped results dir", tct.getTimestampedResultsDir());
        assertEquals("timestamped results dir", JameleonUtility.fixFileSeparators(
                "./jameleon_test_results/20070518102332725/some name"),
                tct.getTimestampedResultsDir().getPath());
    }

    public void testEndTestCase(){
        TestCaseTag tct = initTestCaseTag();
        TestCaseEvent tce = new TestCaseEvent(tct);
        FunctionalPoint fp2 = new FunctionalPoint();
        fp2.addTagName("failed-tag");
        CountableFunctionResult cfr = new CountableFunctionResult(fp2, tct.getResults());
        cfr.setFailed();
        cfr.setError(new RuntimeException("an error message"));
        cfr.setLineNumber(23);
        reporter.setStartTime(Calendar.getInstance());
        reporter.beginTestCase(tce);
        reporter.endTestCase(tce);
        assertEquals("# of tests passed", 3, reporter.getTestCaseCounter().getNumPassed());
        assertEquals("# of tests failed", 1, reporter.getTestCaseCounter().getNumFailed());
        assertEquals("test case #", 2, reporter.getTestCaseCounter().getTestCaseNum());
        assertTrue("reportScriptResult wasn't called in html results", htmlTestRunReporter.reportScriptResultCalled);
        assertTrue("reportScriptResult wasn't called in stdout results", stdOutTestRunReporter.reportScriptResultCalled);

    }

    public void testGetResultsDirectory(){
        Calendar c = getCalendar(2007, Calendar.MAY, 18, 10, 23, 32, 725);
        File f = ResultsReporter.getResultsDir(BASE_DIR, c);
        assertEquals("resultsDir", JameleonUtility.fixFileSeparators(
                                    "jameleon_test_results/20070518102332725"), f.getPath());
    }

    private Calendar getCalendar(int year, int month, int day, int hour, int min, int sec, int milli){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, day);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, sec);
        c.set(Calendar.MILLISECOND, milli);
        return c;
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
        tct.setFileName("some/path/sometest.xml");
        return tct;
    }

    private class TestRunReporterStub implements TestRunReporter {

        boolean reportScriptResultCalled;
        boolean reportTestRunStartCalled;
        boolean reportTestRunCompleteCalled;
        boolean cleanUpCalled;

        public void reportScriptResult(TestCaseTag tct, TestCaseCounter counter) {
            reportScriptResultCalled = true;
        }

        public void reportTestRunStart(Calendar startTime) {
            reportTestRunStartCalled = true;
        }

        public void reportTestRunComplete(Calendar startTime, Calendar endTime, TestCaseCounter counter) {
            reportTestRunCompleteCalled = true;
        }

        public void cleanUp() {
            cleanUpCalled = true;
        }

        public Writer getWriter() {
            return null;
        }

        public void setWriter(Writer writer) {
        }
    }

    private class TestRunSummaryReporterStub implements TestRunSummaryReporter {

        boolean reportTestRunStartCalled;
        boolean reportTestRunCompleteCalled;
        boolean cleanUpCalled;

        public void reportTestRunStart(Calendar startTime) {
            reportTestRunStartCalled = true;
        }

        public void reportTestRunComplete(Calendar startTime, Calendar endTime, TestCaseCounter counter) {
            reportTestRunCompleteCalled = true;
        }

        public void cleanUp() {
            cleanUpCalled = true;
        }

        public Writer getWriter() {
            return null;
        }

        public void setWriter(Writer writer) {
        }

        public boolean isPrintHeader() {
            return false;
        }

        public void setPrintHeader(boolean printHeader) {
        }
    }
}
