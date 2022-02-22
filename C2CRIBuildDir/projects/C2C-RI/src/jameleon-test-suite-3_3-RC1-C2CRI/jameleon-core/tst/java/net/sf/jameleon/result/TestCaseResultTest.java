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
import net.sf.jameleon.util.XMLHelper;

import java.util.List;

public class TestCaseResultTest extends TestCase {

    private TestCaseResult results;
    private SessionResult sessionResults;
    private static final String TEST_CASE_NAME = "Foo";
    private static final FunctionalPoint tag = new FunctionalPoint();

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestCaseResultTest.class );
    }

    public TestCaseResultTest( String name ) {
        super( name );
    }

    public void setUp(){
        tag.addTagName("testcase");
        results = new TestCaseResult(TEST_CASE_NAME, tag);
        sessionResults = new SessionResult(tag);
        tag.addTagName("testcase");
        tag.addTagName("test-case");
    }

    public void testGetPercentagePassed(){
        for (int i = 0; i < 4; i++) {
            new CountableDataDrivableRowResult(tag, results);
        }
        assertEquals("percent passed", "100%", results.getPercentagePassed());
        CountableDataDrivableRowResult cfr = new CountableDataDrivableRowResult(tag, results);
        cfr.setError(new RuntimeException("failure 1"));
        assertEquals("percent passed", "80%", results.getPercentagePassed());
    }

    public void testGetPercentagePassed2(){
        assertEquals("percent passed", "100%", TestCaseResult.getPercentagePassed(1, 0));
        assertEquals("percent passed", "67%", TestCaseResult.getPercentagePassed(3, 1));
        assertEquals("percent passed", "75%", TestCaseResult.getPercentagePassed(4, 1));
        assertEquals("percent passed", "0%", TestCaseResult.getPercentagePassed(0, 1));
        assertEquals("percent passed", "0%", TestCaseResult.getPercentagePassed(0, 0));
    }

    public void testGetFailedResults(){
        TestResultWithChildren res = new MockTestResultWithChildren(tag, results);
        FunctionalPoint tag2 = new FunctionalPoint();
        tag2.addTagName("tag2");
        TestResultWithChildren res2 = new MockTestResultWithChildren(tag2, res);
        FunctionalPoint tag3 = new FunctionalPoint();
        tag3.addTagName("tag3");
        TestResultWithChildren res3 = new MockTestResultWithChildren(tag3);
        res2.addFailedResult(res3);
        assertEquals("# of results", 1, results.getFailedResults().size());
        results.setError(new RuntimeException("Some error message"));
        assertTrue("should have failed", results.failed());
        assertEquals("# of results", 2, results.getFailedResults().size());
        assertEquals("failed result", results, results.getFailedResults().get(1));
    }

    public void testGetSetTestName(){
        assertEquals("test case name", TEST_CASE_NAME, results.getTestName());
        results.setTestName("Some name");
        assertEquals("test case name", "Some name", results.getTestName());
    }

    public void testConstructor(){
        assertEquals(0,results.getChildrenResults().size());
        assertEquals("test case name", TEST_CASE_NAME, results.testName);
        assertTrue(results.passed());
        assertEquals("functionId of test case result", "testcase", results.getTag().getDefaultTagName());
    }

    public void testToString(){
        results.addChildResult(sessionResults);
        results.addChildResult(sessionResults);
        String str = results.toString();
        assertTrue(results.passed());
        assertTrue("No errors expected", str.indexOf("Error") == -1 );
        assertTrue("No errors expected", str.indexOf("ERROR") == -1 );
    }

    public void testToXML(){
        results.setTestName("test name");
        results.setTestCaseDocsFile("some file");
        String xml = results.toXML();
        XMLHelper xh = new XMLHelper(xml);
        assertEquals("test-case-name", "test name", xh.getValueFromXPath("/test-case/test-case-name"));
        assertEquals("outcome", "PASSED", xh.getValueFromXPath("/test-case/outcome"));
    }

    public void testDestroy(){
        results.setTestCaseDocsFile("some file");
        results.destroy();
        List sessionResultsList = results.getChildrenResults();
        assertEquals("# of session results", 0,sessionResultsList.size());
        assertNull("test case docs file", results.getTestCaseDocsFile());
    }

}