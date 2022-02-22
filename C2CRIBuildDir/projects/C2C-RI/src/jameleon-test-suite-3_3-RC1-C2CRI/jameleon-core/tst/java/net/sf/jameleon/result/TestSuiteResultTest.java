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

public class TestSuiteResultTest extends TestCase {

    private TestSuiteResult suiteResult;
    private static final FunctionalPoint tag = new FunctionalPoint();

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestSuiteResultTest.class );
    }

    public TestSuiteResultTest( String name ) {
        super( name );
    }

    public void setUp(){
        suiteResult = new TestSuiteResult();
        tag.addTagName("some tag");
    }

    public void tearDown(){
    }

    public void testConstructor(){
        assertNotNull("TestSuiteResult after call to constructor", suiteResult);
        assertEquals("# of test case results", 0, suiteResult.testCaseResults.size());
    }

    public void testAddTestCaseResult(){
        TestCaseResult tcRes = new TestCaseResult("name", tag);
        tcRes.setTestName("george");
        suiteResult.addTestCaseResult(tcRes);
        assertEquals("# of test case results", 1, suiteResult.testCaseResults.size());
        assertEquals("test case result", tcRes, suiteResult.testCaseResults.get(0));
    }

    public void testGetTestCaseResults(){
        TestCaseResult tcRes = new TestCaseResult("name", tag);
        tcRes.setTestName("george");
        suiteResult.addTestCaseResult(tcRes);
        assertEquals("testCaseResults", suiteResult.testCaseResults, suiteResult.getTestCaseResults());
    }
}
