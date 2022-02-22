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
import net.sf.jameleon.event.TestCaseEvent;
import net.sf.jameleon.event.TestCaseListener;
import net.sf.jameleon.event.TestRunEvent;
import net.sf.jameleon.event.TestRunListener;
import net.sf.jameleon.result.TestCaseResult;

import java.io.File;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Writes the HTML results out to a file. This class writes both the main results
 * as well as the individual testcase docs.
 */
public class ResultsReporter implements TestCaseListener, TestRunListener {

    private static ResultsReporter reporter;

    private Calendar startTime;
    private TestCaseCounter counter;
    private TestRunReporter htmlTestRunReporter;
    private TestRunReporter simpleTestRunReporter;
    private TestRunSummaryReporter htmlTestRunSummaryReporter;

    // Make this class a singleton
    private ResultsReporter(){
        counter = new TestCaseCounter();
        htmlTestRunReporter = new HtmlTestRunReporter();
        htmlTestRunSummaryReporter = new HtmlTestRunSummaryReporter();
        simpleTestRunReporter = new SimpleTestRunReporter();
        PrintWriter printWriter = new PrintWriter(System.out);
        simpleTestRunReporter.setWriter(printWriter);
    }

    public static ResultsReporter getInstance(){
        if (reporter == null){
            reporter = new ResultsReporter();
        }
        return reporter;
    }

    public static void clearInstance(){
        if (reporter != null){
            reporter.getHtmlTestRunReporter().cleanUp();
            reporter = null;
        }
    }
    
    /**
     * Gets the timestamp formatted results directory
     * @param baseDir - The director to use as the base or just '.'
     * @param c - The date and time
     * @return a file representing the directory to store the results to
     */
    public static File getResultsDir(File baseDir, Calendar c) {
        String path = baseDir.getPath()+File.separator + ReporterUtils.formatTime(c);
        return new File(path);
    }

    protected File getResultsDirectoryForTestCase(TestCaseTag tct){
        return new File(getResultsDir(tct.getResultsDir(false), startTime), tct.getResults().getTestName());
    }

    protected void genTestCaseResultsDoc(TestCaseTag tct){
        if (tct.isGenTestCaseDocs()) {
            HtmlTestCaseResultGenerator tcResultGen = new HtmlTestCaseResultGenerator(tct);
            File mainFile = tcResultGen.generateTestCaseResultPage();
            tct.getResults().setTestCaseDocsFile(mainFile.getPath());
        }
    }

    /**
     * Resets the test run stats (TestCaseCounter)
     */
    public void resetStats() {
        counter = new TestCaseCounter();
    }

    private void checkCounter(){
        if (counter == null){
            resetStats();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////                                    TestCaseListner methods                            ////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public void beginTestCase(TestCaseEvent event) {
        TestCaseTag tct = (TestCaseTag)event.getSource();
        tct.setTimestampedResultsDir(getResultsDirectoryForTestCase(tct));
    }

    public void endTestCase(TestCaseEvent event) {
        try{
            TestCaseTag tct = (TestCaseTag)event.getSource();
            genTestCaseResultsDoc(tct);
            TestCaseResult result = tct.getResults();
            final int totalRun = result.getCountableResults().size();
            final int failed = result.getFailedCountableResults().size();
            checkCounter();
            counter.incrementPassed(totalRun - failed);
            counter.incrementFailed(failed);
            htmlTestRunReporter.reportScriptResult(tct, counter);
            simpleTestRunReporter.reportScriptResult(tct, counter);
        }finally{
            checkCounter();
            counter.incrementTestCaseNum(1);
        }
    }

    public void beginTestRun(TestRunEvent event) {
        setStartTime((Calendar)event.getSource());
        htmlTestRunReporter.reportTestRunStart(getStartTime());
        htmlTestRunSummaryReporter.reportTestRunStart(getStartTime());
    }

    public void endTestRun(TestRunEvent event) {
        Calendar endTime = (Calendar)event.getSource();
        htmlTestRunReporter.reportTestRunComplete(getStartTime(), endTime, counter);
        htmlTestRunSummaryReporter.reportTestRunComplete(getStartTime(), endTime, counter);
        simpleTestRunReporter.reportTestRunComplete(getStartTime(), endTime, counter);
        resetStats();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    ////                                    SETTERS & GETTERS                                  ////
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public Calendar getStartTime(){
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public TestCaseCounter getTestCaseCounter() {
        return counter;
    }

    public void setTestCaseCounter(TestCaseCounter counter) {
        this.counter = counter;
    }

    public TestRunReporter getHtmlTestRunReporter() {
        return htmlTestRunReporter;
    }

    public void setHtmlTestRunReporter(TestRunReporter htmlTestRunReporter) {
        this.htmlTestRunReporter = htmlTestRunReporter;
    }

    public TestRunSummaryReporter getHtmlTestRunSummaryReporter() {
        return htmlTestRunSummaryReporter;
    }

    public void setHtmlTestRunSummaryReporter(TestRunSummaryReporter htmlTestRunSummaryReporter) {
        this.htmlTestRunSummaryReporter = htmlTestRunSummaryReporter;
    }

    public TestRunReporter getSimpleTestRunReporter() {
        return simpleTestRunReporter;
    }

    public void setSimpleTestRunReporter(TestRunReporter simpleTestRunReporter) {
        this.simpleTestRunReporter = simpleTestRunReporter;
    }


}
