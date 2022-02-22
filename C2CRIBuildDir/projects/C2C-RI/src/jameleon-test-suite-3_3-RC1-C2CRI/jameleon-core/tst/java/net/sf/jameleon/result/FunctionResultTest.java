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

import net.sf.jameleon.DummyJameleonTagSupport;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.XMLHelper;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FunctionResultTest extends TestCase{
    
    private FunctionResult res1;
    private FunctionResult res2;
    private FunctionResult res3;
    private FunctionResult res4;
    private DummyJameleonTagSupport ft;
    private static final long EXECUTION_TIME = System.currentTimeMillis();
    private static final String TEST_STEP_ID = "Bar";
    private static final String TAG_NAME = "some tag";
    private static final String ERROR_MSG = "This was Foobared";
    private static final RuntimeException ex = new RuntimeException(ERROR_MSG);
    private static final FunctionalPoint tag = new FunctionalPoint();

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( FunctionResultTest.class );
    }

    public FunctionResultTest( String name ) {
        super( name );
    }

    public void setUp(){
        res1 = new FunctionResult();
        res2 = new FunctionResult();
        
        tag.addTagName(TAG_NAME);
        tag.setFunctionId(TEST_STEP_ID);
        res2.setTag(tag);
        res2.setExecutionTime(EXECUTION_TIME);
        res3 = new FunctionResult();
        res3.setTag(tag);
        res3.setExecutionTime(EXECUTION_TIME);
        ft = new DummyJameleonTagSupport();
        FunctionalPoint fp = ft.getFunctionalPoint();
        res4 = new FunctionResult(fp);
        res4.getTag().setFunctionId(TEST_STEP_ID);
        res4.setExecutionTime(EXECUTION_TIME);
    }

    public void testConstructorWithParentResult(){
        TestResultWithChildren resultWithChildren = new MockTestResultWithChildren(tag);
        FunctionResult res5 = new FunctionResult(tag, resultWithChildren);
        assertTrue("parent results", resultWithChildren == res5.parentResults);
    }

    public void testIsParent(){
    	assertFalse("Should not be a parent", res1.isParent());
    }

    public void testGettersSetters(){
        assertFalse(res1.isPrecondition());
        res1.setPrecondition(true);
        assertTrue(res1.isPrecondition());

        assertFalse(res1.isPostcondition());
        res1.setPostcondition(true);
        assertTrue(res1.isPostcondition());
    }

    public void testGetErrorMsg(){
        assertNull(res1.getErrorMsg());
        res1.setPrecondition(true);
        res1.setError(new RuntimeException("some error"));
        assertEquals("error msg with precondition", "PRECONDITION FAILURE: some error", res1.getErrorMsg());
        res1.setPrecondition(false);
        res1.setPostcondition(true);
        assertEquals("error msg with precondition", "POSTCONDITION FAILURE: some error", res1.getErrorMsg());
    }

    public void testToString(){
        assertTrue(TAG_NAME +" not found in toString()",res2.toString().indexOf(TAG_NAME) >= 0);
        assertTrue("execution time not found in toString()",res2.toString().indexOf(JameleonUtility.executionTimeToString(EXECUTION_TIME)) > 0);
        assertTrue("outcome not found in toString()",res2.toString().indexOf("PASSED") > 0);
        assertTrue(res2.toString().indexOf(ERROR_MSG) == -1);
    }

    public void testPassed(){
        assertTrue(res1.passed());
        res1.setError(ex);
        assertFalse(res1.passed());
    }

    public void testGetErrorMsgPrefix(){
        assertEquals("", res2.getErrorMsgPrefix());
        res2.setPrecondition(true);
        assertEquals("PRECONDITION FAILURE: ", res2.getErrorMsgPrefix());
    }

    public void testToXML(){
        XMLHelper xh = new XMLHelper(res4.toXML());
        res4.setErrorFile(new File("results file"));
        assertEquals(ft.getFunctionalPoint(), res4.getTag());
        ft.setAttribute("fortyTwo","12");

        xh = new XMLHelper(res4.toXML());

        assertEquals("functionId",TEST_STEP_ID,xh.getValueFromXPath("/function-point/functionId"));
        assertEquals("outcome","PASSED",xh.getValueFromXPath("/function-point/outcome"));
        assertEquals("execution time", JameleonUtility.executionTimeToString(EXECUTION_TIME), xh.getValueFromXPath("/function-point/execution-time"));
        assertEquals("results file name","results file",xh.getValueFromXPath("/function-point/error-file-name"));
        assertEquals("attribute fortyTwo","12",xh.getValueFromXPath("/function-point/functional-point-info/attributes/attribute/attribute-name[text()='fortyTwo']/following-sibling::attribute-value"));
    }

}
