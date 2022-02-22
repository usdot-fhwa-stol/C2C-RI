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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.result;

import net.sf.jameleon.bean.FunctionalPoint;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * An implementation of @see TestResult that keeps track of all of the function point results as well as the the sessio results
 */
public class TestCaseResult 
    extends TestResultWithChildren
    implements CountableResult {

    private static final long serialVersionUID = 1L;

    /**
     * The name of the test case
     */
    protected String testName;
    /**
     * The name of the test case docs file
     */
    protected String testCaseDocsFile;
    protected boolean countableResultFailed;
    protected Calendar dateTimeExecuted;

    public TestCaseResult(){
        super();
        dateTimeExecuted = Calendar.getInstance();
    }

    public TestCaseResult(FunctionalPoint tag){
        super(tag);
        dateTimeExecuted = Calendar.getInstance();
    }

    public TestCaseResult(String testName, FunctionalPoint tag){
        this(tag);
        this.testName = testName;
    }

    public void setTestName(String name){
        testName = name;
    }

    public String getTestName(){
        return testName;
    }

    public String getTestCaseDocsFile(){
        return testCaseDocsFile;
    }

    public void setTestCaseDocsFile(String testCaseDocsFile){
        this.testCaseDocsFile = testCaseDocsFile;
    }


    public Calendar getDateTimeExecuted() {
        return dateTimeExecuted;
    }

    public void setDateTimeExecuted(Calendar dateTimeExecuted) {
        this.dateTimeExecuted = dateTimeExecuted;
    }

    /**
     * Gets a list of failed child results
     * @return a list of failed child results
     */
    public List getFailedResults(){
        if (getError() != null && !failedResults.contains(this)) {
            failedResults.add(this);
        }
        return failedResults;
    }


    public void destroy(){
        super.destroy();
        testCaseDocsFile = null;
    }

    /**
     * @return an XML representation of the results
     */
    public String toXML(){
        StringBuffer str = new StringBuffer("\n");
        str.append("\t<test-case>\n");
        str.append("\t\t<test-case-name>").append(testName).append("</test-case-name>\n");
        str.append(super.toXML());
        if (testCaseDocsFile != null && testCaseDocsFile.length() > 0) {
            str.append("\t\t<doc-file>").append(testCaseDocsFile.replace('\\','/')).append("</doc-file>\n");
        }
        str.append(super.toXML());
        str.append("\t</test-case>\n");
        return str.toString();
    }

    public List getCountableResults(){
        List countabeResults = new ArrayList();
        countabeResults.addAll(getCountableChildResults());
        if (countabeResults.size() == 0) {
            countabeResults.add(this);
        }
        return countabeResults;
    }

    /**
     * Gets all the failed ancestor children results that can be counted as
     * test case results
     * 
     * @return List
     */
    public List getFailedCountableResults(){
        List failedCountableResults = super.getFailedCountableResults();
        if (failedCountableResults.size() == 0 &&
            isCountableResultFailed()) {
            failedCountableResults.add(this);
        }
        return failedCountableResults;
    }

    /**
     *  Gets the percentage total tests passed.
     *  @return the summary for the total tests run, passed and failed.
     */
    public String getPercentagePassed() {
        int testsRun = getCountableResults().size();
        int testsFailed = getFailedCountableResults().size();
        return getPercentagePassed(testsRun, testsFailed);
    }

    /**
     *  Gets the percentage total tests passed.
     * @param testsRun The number of tests run
     * @param testsFailed The number of tests failed
     *  @return the percentage total tests passed.
     */
    public static String getPercentagePassed(int testsRun, int testsFailed) {
        String percentagePassed = "N/A";
        try {
            NumberFormat nf = NumberFormat.getPercentInstance();
            int testsPassed = testsRun - testsFailed;
            if (testsRun > 0) {
                percentagePassed = nf.format((double) testsPassed/testsRun);
            } else {
                percentagePassed = nf.format(0);
            }
        } catch (ArithmeticException ae) {
            System.err.println("Problem calculating percentage passed");
            ae.printStackTrace();
        }
        return percentagePassed;
    }

    public void recordFailureToCountableResult(){
        countFailure();
    }

    public boolean isDataDriven() {
        return (getTag().getAttribute("useCSV").getValue().equals("true"));
    }

    /////////////////////////////////////////////////////////////////////////////////
    //                CountableResult methods                                      //
    /////////////////////////////////////////////////////////////////////////////////

    public void countFailure(){
        countableResultFailed = true;
    }

    public boolean isCountableResultFailed(){
        return countableResultFailed;
    }
}
