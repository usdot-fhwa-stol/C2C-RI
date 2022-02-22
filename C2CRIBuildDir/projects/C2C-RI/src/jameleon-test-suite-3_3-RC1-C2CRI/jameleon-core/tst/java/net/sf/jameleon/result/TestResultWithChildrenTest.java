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

public class TestResultWithChildrenTest extends TestCase {

    private TestResultWithChildren trwc;
    private static final FunctionalPoint tag = new FunctionalPoint();

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestResultWithChildrenTest.class );
    }
    
    public TestResultWithChildrenTest( String name ) {
        super( name );
    }

    public void setUp(){
        tag.addTagName("some tag");
        trwc = new MockTestResultWithChildren(tag);
    }

    public void testConstructor1(){
        assertNotNull("tag",trwc.getTag());
        assertEquals("tag", tag, trwc.getTag());
    }

    public void testConstructor2(){
        TestResultWithChildren tr = new MockTestResultWithChildren(tag);
        assertTrue(tr.tag == tag);
    }

    public void testConstructor3(){
        TestResultWithChildren tr = new MockTestResultWithChildren(tag, trwc);
        assertTrue(trwc == tr.parentResults);
        assertTrue(tr.tag == tag);
    }

    public void testIsParent(){
    	assertTrue("Should be a parent", trwc.isParent());
    }
    
    public void testGetFailedCountableResults(){
        TestResultWithChildren res = new MockCountableResult(createTag("one"), trwc);
        TestResultWithChildren res2 = new MockCountableResult(createTag("two"), res);
        SessionResult res3 = new SessionResult(createTag("session"), res2);
        FunctionResult res4 = new FunctionResult(createTag("function"), res3);
        res4.setError(new RuntimeException());
        assertEquals(1, trwc.getFailedCountableResults().size());
        assertEquals(1, res.getFailedCountableResults().size());
        assertEquals(1, res2.getFailedCountableResults().size());
    }

    public void testGetCountableResults(){
        TestResultWithChildren res = new MockCountableResult(tag, trwc);
        assertEquals(1, res.getCountableResults().size());
        TestResultWithChildren res2 = new MockCountableResult(tag, res);
        TestResultWithChildren res3 = new MockCountableResult(tag, res2);
        TestResultWithChildren res4 = new MockCountableResult(tag, res3);
        assertEquals(4, trwc.getCountableResults().size());
        assertEquals(4, res.getCountableResults().size());
        MockTestResultWithChildren res5 = new MockTestResultWithChildren(tag, res4);
        assertEquals(4, res.getCountableResults().size());
        new MockCountableResult(tag, res5);
        assertEquals(5, res.getCountableResults().size());
        new CountableFunctionResult(tag, trwc);
        assertEquals(6, trwc.getCountableResults().size());
    }

    public void testGetAllChildrenResults(){
        TestResultWithChildren res = new MockTestResultWithChildren(tag, trwc);
        TestResultWithChildren res2 = new MockTestResultWithChildren(tag, res);
        TestResultWithChildren res3 = new MockTestResultWithChildren(tag, res2);
        TestResultWithChildren res4 = new MockTestResultWithChildren(tag, res3);

        assertEquals(0, res4.getAllChildrenResults().size());
        assertEquals(1, res3.getAllChildrenResults().size());
        assertEquals(2, res2.getAllChildrenResults().size());
        assertEquals(3, res.getAllChildrenResults().size());
        assertEquals(4, trwc.getAllChildrenResults().size());
        res4.addChildResult(new MockTestResultWithChildren(tag));
        assertEquals(5, trwc.getAllChildrenResults().size());
    }

    public void testGetAllFailedLeafChildrenResults(){
        TestResultWithChildren res = new MockTestResultWithChildren(tag, trwc);
        TestResultWithChildren res2 = new MockTestResultWithChildren(tag, res);
        FunctionResult res3 = new FunctionResult();
        FunctionResult res4 = new FunctionResult();
        res.setFailed();
        res2.setFailed();
        res4.setFailed();
        res2.addChildResult(res3);
        res2.addChildResult(res4);

        assertEquals(1, res2.getAllFailedLeafChildrenResults().size());
        assertEquals(1, res.getAllFailedLeafChildrenResults().size());
        assertEquals(1, trwc.getAllFailedLeafChildrenResults().size());

        assertSame(res4, res2.getAllFailedLeafChildrenResults().get(0));
        assertSame(res4, res.getAllFailedLeafChildrenResults().get(0));
        assertSame(res4, trwc.getAllFailedLeafChildrenResults().get(0));

        res3.setFailed();
        assertEquals(2, res2.getAllFailedLeafChildrenResults().size());
        assertEquals(2, res.getAllFailedLeafChildrenResults().size());
        assertEquals(2, trwc.getAllFailedLeafChildrenResults().size());

    }

    public void testAddFailedResult(){
        TestResultWithChildren res = new MockTestResultWithChildren(tag, trwc);
        TestResultWithChildren res2 = new MockTestResultWithChildren(tag, res);
        TestResultWithChildren res3 = new MockTestResultWithChildren(tag);
        res2.addFailedResult(res3);
        assertTrue("should have failed", res2.failed());
        assertEquals("# of results", 1, res2.getFailedResults().size());
        assertTrue("should have failed", res.failed());
        assertEquals("# of results for parent", 1, res.getFailedResults().size());
        assertTrue("should have failed", trwc.failed());
        assertEquals("# of results for grandparent", 1, trwc.getFailedResults().size());
        assertEquals("failed result", res3, res2.getFailedResults().get(0));
        assertEquals("failed result for parent", res3, res.getFailedResults().get(0));
        assertEquals("failed result for parent", res3, trwc.getFailedResults().get(0));
    }

    public void testAddChildResult(){
        assertEquals("# of function results", 0, trwc.getChildrenResults().size());
        FunctionResult fr = new FunctionResult();
        trwc.addChildResult(fr);
        assertEquals("# of function results", 1, trwc.getChildrenResults().size());
        assertTrue("function result", fr == trwc.getChildrenResults().get(0));
    }

    public void testToXML(){
        FunctionalPoint tag2 = new FunctionalPoint();
        tag2.addTagName("another tag");
        TestResultWithChildren mtr = new MockTestResultWithChildren(tag2);
        TestResultWithChildren mtr2 = new MockTestResultWithChildren(tag);
        trwc.addChildResult(mtr);
        trwc.addChildResult(mtr2);

        String xml = trwc.toXML();
        XMLHelper xmlHelper = new XMLHelper(xml);
        assertEquals("another tag", xmlHelper.getValueFromXPath("/mock-tr/children-results/mock-tr[1]/functional-point-info/tag-name"));
        assertEquals("some tag", xmlHelper.getValueFromXPath("/mock-tr/children-results/mock-tr[2]/functional-point-info/tag-name"));
    }

    protected FunctionalPoint createTag(String name){
        FunctionalPoint fp = new FunctionalPoint();
        fp.addTagName(name);
        return fp;
    }

}