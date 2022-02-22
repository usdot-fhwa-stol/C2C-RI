/*

    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.result.*;

public class SessionTagTest extends TestCase {

    private MockSessionTag mst;
    private TestCaseTag tct;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( SessionTagTest.class );
    }

    public SessionTagTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception{
        mst = new MockSessionTag();
        mst.setContext(new JellyContext());
        tct = (TestCaseTag)mst.getAncestorTag(TestCaseTag.class);
        tct.setName("some name");
        tct.setGenTestCaseDocs(false);
        tct.setUpDataDrivable();
        mst.beginSession = true;
    }

    public void tearDown(){
        mst = null;
    }

    public void testRecordFunctionResult(){
        FunctionResult fr = new FunctionResult();
        assertEquals(0, mst.getSessionResult().getChildrenResults().size());
        mst.recordFunctionResult(fr);
        assertEquals(1, mst.getSessionResult().getChildrenResults().size());
        assertTrue(mst.getSessionResult() == fr.getParentResults());
    }

    public void testInit() throws Exception {
        mst.organization = "some org";
        mst.init();
        assertTrue(tct.getResults() == (TestCaseResult)mst.getSessionResult().getParentResults());
        assertTrue(mst.addt == tct);
        assertEquals("some org", mst.getContext().getVariable("organization"));
        assertEquals("some org", tct.getOrganization());

        mst.organization = null;
        tct.setOrganization("another org");
        mst.init();
        assertEquals("another org", mst.organization);
    }

    public void testSessionDelay() throws Exception {
        long startTime = System.currentTimeMillis();
        mst.delaySession();
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        assertTrue("execution time exceeded 10 millis", totalTime <= 20);
        mst.setSessionDelay(500);
        startTime = System.currentTimeMillis();
        mst.delaySession();
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        assertTrue("execution time didn't exceed 500 millis "+totalTime, totalTime >= 480);


        mst.setAttribute("sessionDelay","500");
        startTime = System.currentTimeMillis();
        try{
            mst.doTag(XMLOutput.createDummyXMLOutput());
        }catch(NullPointerException npe){
            //Just means there are no children
        }
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        assertTrue("execution time didn't exceed 500 millis "+totalTime, totalTime >= 480);
    }

    public void testStartApplication() throws Exception{
        try{
            mst.doTag(XMLOutput.createDummyXMLOutput());
        }catch(NullPointerException npe){
            //Just means there are no children
        }
        assertTrue("startApplication() was not called", mst.applicationStarted);
        mst.applicationStarted = false;
        tct.setFailedOnCurrentRow(true);
        try{
            mst.doTag(XMLOutput.createDummyXMLOutput());
        }catch(NullPointerException npe){
            //Just means there are no children
        }
        assertFalse("startApplication() was called", mst.applicationStarted);
    }

    public void testStartApplicationPostcondition() throws Exception{
        tct.setFailedOnCurrentRow(true);
        PostconditionTag pt = new PostconditionTag();
        mst.setParent(pt);
        pt.setParent(tct);
        try{
            mst.doTag(XMLOutput.createDummyXMLOutput());
        }catch(NullPointerException npe){
            //Just means there are no children
        }
        assertTrue("startApplication() was not called", mst.applicationStarted);
    }

}
