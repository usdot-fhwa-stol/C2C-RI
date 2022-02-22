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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.XMLHelper;
import org.apache.commons.jelly.LocationAware;

import java.io.File;
import java.util.Calendar;

public class JameleonTestResultTest extends TestCase {

    private JameleonTestResult results;
    private FunctionalPoint tag;
    private static final String ERROR_MSG = "error msg";
    private static final String ERROR_FILE = "some/error_file";
    private static final long EXECUTION_TIME = 12l;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( JameleonTestResultTest.class );
    }

    public JameleonTestResultTest( String name ) {
        super( name );
    }

    public void setUp(){
        tag = new FunctionalPoint();
        results = new JTestResult(tag);
        results.setExecutionTime(EXECUTION_TIME);
    }

    public void tearDown(){
        results = null;
        tag = null;
    }

    public void testDateTimeExecuted(){
        //First, let's test the constructor did its job
        assertNotNull("dateTime was null", results.getDateTimeExecuted());
        Calendar now = Calendar.getInstance();
        assertEquals("dateTime day", now.get(Calendar.DAY_OF_MONTH), results.getDateTimeExecuted().get(Calendar.DAY_OF_MONTH));
        results.setDateTimeExecuted(now);
        assertTrue("checking for exact match of dateTime", now == results.getDateTimeExecuted());
    }



    public void testGetHtmlFormattedStackTrace(){
        results.setError(new RuntimeException("some <error>"));
        String stack = JameleonUtility.decodeTextToXML(JameleonUtility.getStack(results.getError()));
        assertEquals("stack trace", stack, results.getHtmlFormattedStackTrace());
    }

    public void testGetFailedRowNum(){
        DataDrivableRowResult ddrr = new DataDrivableRowResult(tag);
        ddrr.setRowNum(2);
        assertEquals("failed row", 0, results.getFailedRowNum());
        results.setParentResults(ddrr);
        assertEquals("failed row", 0, results.getFailedRowNum());
        results.setFailed();
        assertEquals("failed row", 2, results.getFailedRowNum());

    }

    public void testGetHtmlFormattedMessage() {
        results.setError(new RuntimeException("some <error>"));
        assertEquals("error message", "some &lt;error&gt;", results.getHtmlFormattedErrorMsg());
    }

    public void testGetExecutionTimeToDisplay(){
        assertEquals("execution time", JameleonUtility.executionTimeToString(EXECUTION_TIME), results.getExecutionTimeToDisplay());
    }
    
    public void testIsA(){
    	assertTrue("JTestResults is an instance of a JameleonTestResult", results.isA("net.sf.jameleon.result.JameleonTestResult"));
    	assertTrue("JTestResults is an instance of a XMLable", results.isA("net.sf.jameleon.XMLable"));
    	assertFalse("JTestResults is not an instance of a String", results.isA("java.lang.String"));
    	assertFalse("JTestResults is not an instance of an unknown class", results.isA("UNkownCLASSName"));
    }

    public void testFindAncestorByClass(){
        CountableTestResult countableResult = new CountableTestResult(tag);
        CountableTestResult countableResult2 = new CountableTestResult(tag, countableResult);
        results.setParentResults(countableResult);
        assertTrue(countableResult == results.findAncestorByClass(CountableResult.class));
        assertTrue(countableResult == countableResult2.findAncestorByClass(CountableResult.class));
        assertFalse(countableResult2 == results.findAncestorByClass(CountableResult.class));
    }

    public void testRecordFailureToCountableResult(){
        CountableTestResult countableResult = new CountableTestResult(tag);
        CountableTestResult countableResult2 = new CountableTestResult(tag, countableResult);
        results.setParentResults(countableResult);
        results.recordFailureToCountableResult();
        assertTrue("countableResult should have been marked as failure", countableResult.isCountableResultFailed());
        assertFalse("countableResult should not have been marked as failure", countableResult2.isCountableResultFailed());
    }

    public void testGetIdentifier(){
        tag.addTagName("some tag name");
        assertEquals("some tag name", results.getIdentifier());
        tag.setFunctionId("some id");
        assertEquals("some id", results.getIdentifier());
    }

    public void testGetSetError(){
        assertNull(results.getError());
        RuntimeException re = new RuntimeException("some error");

        CountableTestResult countableResult = new CountableTestResult(tag);
        results.setParentResults(countableResult);
        results.setError(re);
        assertTrue(re == results.getError());
        assertTrue(countableResult.isCountableResultFailed());

    }

    public void testGetErrorMsg(){
        assertNull(results.getErrorMsg());
        RuntimeException re = new RuntimeException("some error");
        results.setError(re);
        assertEquals("some error", results.getErrorMsg());
    }

    public void testContstructorWithFunctionalPoint(){
        FunctionalPoint tag = new FunctionalPoint();
        tag.addTagName("some-tag");
        results = new JTestResult(tag);
        assertEquals("tag", tag, results.getTag());
    }

    public void testContstructorWithFunctionalPointAndParentResults(){
        FunctionalPoint tag = new FunctionalPoint();
        tag.addTagName("some-tag");
        HasChildResults parentResults = new MockTestResultWithChildren(tag);
        JameleonTestResult tr = new JTestResult(tag, parentResults);
        assertEquals("tag", tag, tr.getTag());
        assertEquals("parent results", parentResults, tr.parentResults);
    }

    public void testSetGetParentResults(){
        FunctionalPoint tag = new FunctionalPoint();
        tag.addTagName("some-tag");
        TestResultWithChildren tr2 = new MockTestResultWithChildren(tag);
        results.setParentResults(tr2);
        assertTrue(tr2 == results.getParentResults());
    }

    public void testSetFailed(){
        assertTrue("should pass", results.passed());
        assertFalse("should not fail", results.failed());
        results.setFailed();
        assertFalse("should not pass", results.passed());
        assertTrue("should fail", results.failed());

        TestResultWithChildren trwc = new MockTestResultWithChildren(tag);
        JameleonTestResult tr = new JTestResult(tag, trwc);

        tr.setFailed();
        assertTrue("should fail", tr.failed());
        assertTrue("should fail", trwc.failed());
        assertEquals("# of results", 1, trwc.getFailedResults().size());
        assertEquals("failed result", tr, trwc.getFailedResults().get(0));
    }

    public void testSetGetExecutionTime(){
        assertEquals("execTime", EXECUTION_TIME, results.getExecutionTime());
    }

    public void testSetGetTag(){
        FunctionalPoint tag = new FunctionalPoint();
        tag.addTagName("some-tag");
        results.setTag(tag);
        assertEquals("tag", tag, results.getTag());
    }

    public void testHashCode(){
        assertTrue("Hash code should be greater than 0", results.hashCode() > 0);
    }

    public void testEscapeXML(){
        String html = "<some text>";
        assertEquals("&lt;some text&gt;", results.escapeXML(html));
        html = "http://some.com/?v=p&z=x";
        assertEquals("http://some.com/?v=p&amp;z=x", results.escapeXML(html));
    }

    public void testPassed(){
        assertTrue("should pass", results.passed());
        results.setError(new RuntimeException());
        assertFalse("should fail", results.passed());
    }

    public void testFailed(){
        assertFalse("should pass", results.failed());
        results.setError(new RuntimeException());
        assertTrue("should fail", results.failed());

        FunctionalPoint tag = new FunctionalPoint();
        tag.addTagName("some-tag");
        TestResultWithChildren trwc = new MockTestResultWithChildren(tag);
        JameleonTestResult tr = new JTestResult(tag, trwc);

        tr.setError(new RuntimeException());
        assertTrue("should fail", tr.failed());
        assertTrue("should fail", trwc.failed());
    }

    public void testGetOutcome(){
        //No tests run
        assertEquals("PASSED", results.getOutcome());
        results.setError(new RuntimeException());
        assertEquals("FAILED", results.getOutcome());
    }

    public void testToXML(){
        results.setError(new RuntimeException(ERROR_MSG));
        File f = new File(ERROR_FILE);
        results.setErrorFile(f);
        tag.addTagName("tag-name");
        tag.addTagName("another-tag-name");
        String xml = results.toXML();
        XMLHelper xh = new XMLHelper(xml);
        assertEquals("execution time", ""+EXECUTION_TIME, xh.getValueFromXPath("/test/execution-time-millis"));
        assertEquals("error message '"+xh.getValueFromXPath("/test/error-message")+"'", ERROR_MSG, xh.getValueFromXPath("/test/error-message"));
        assertEquals("outcome", "FAILED", xh.getValueFromXPath("/test/outcome"));
        assertEquals("error file", f.getPath(), xh.getValueFromXPath("/test/error-file-name"));

        assertEquals("functional point", "tag-name", xh.getValueFromXPath("/test/functional-point-info/tag-name[1]"));
        assertEquals("functional point", "another-tag-name", xh.getValueFromXPath("/test/functional-point-info/tag-name[2]"));
    }

    public void testCopyLocationAwareProperties(){
        LocationAware la = new MockLocationAware();
        results.copyLocationAwareProperties(la);
        testLocationAwareProperties(la, results);
    }

    protected void testLocationAwareProperties(LocationAware la, JameleonTestResult jtr){
        assertEquals("lineNumber", la.getLineNumber(), jtr.getLineNumber());
        assertEquals("columnNumber", la.getColumnNumber(), jtr.getColumnNumber());
        assertEquals("fileName", la.getFileName(), jtr.getFileName());
        assertEquals("elementName", la.getElementName(), jtr.getElementName());
    }

    protected class MockLocationAware implements LocationAware{
        public int getLineNumber(){return 10;}
        public void setLineNumber(int lineNumber){}
        public int getColumnNumber(){return 20;}
        public void setColumnNumber(int columnNumber){}
        public String getFileName(){return "fileName";}
        public void setFileName(String fileName){}
        public String getElementName(){return "elementName";}
        public void setElementName(String elementName){}
    }

    private class JTestResult extends JameleonTestResult{

        public JTestResult(FunctionalPoint tag){
            super(tag);
        }

        public JTestResult(FunctionalPoint tag, HasChildResults parentResults){
            super(tag, parentResults);
        }

        public boolean isParent() {
            return false;
        }

        public boolean isDataDriven() {
            return false;  
        }

        public boolean hasChildren() {
            return false;
        }

        public String toXML(){
            StringBuffer str = new StringBuffer();
            str.append("<test>\n");
            str.append(super.toXML());
            str.append("</test>\n");
            return str.toString();
        }

    }

    private class CountableTestResult extends TestResultWithChildren implements CountableResult{

        protected boolean countableFailure;

        public CountableTestResult(FunctionalPoint tag){
            super(tag);
        }

        public CountableTestResult(FunctionalPoint tag, HasChildResults parentResults){
            super(tag, parentResults);
        }

        public boolean isDataDriven() {
            return false;
        }

        public void countFailure(){
            countableFailure = true;
        }

        public boolean isCountableResultFailed(){
            return countableFailure;
        }

        public String toXML(){
            StringBuffer str = new StringBuffer();
            str.append("<test>\n");
            str.append(super.toXML());
            str.append("</test>\n");
            return str.toString();
        }
    }



}