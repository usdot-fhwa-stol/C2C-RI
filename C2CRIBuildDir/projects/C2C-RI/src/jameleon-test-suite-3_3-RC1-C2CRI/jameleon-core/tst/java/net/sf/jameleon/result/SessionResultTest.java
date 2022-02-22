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

import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.util.XMLHelper;

public class SessionResultTest extends TestCase {

    private SessionResult sr;
    private static final FunctionalPoint tag = new FunctionalPoint();

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( SessionResultTest.class );
    }

    public SessionResultTest( String name ) {
        super( name );
    }

    public void setUp(){
        sr = new SessionResult(tag);
    }

    public void testConstructor1(){
        assertNotNull("tag", sr.getTag());
        assertEquals("tag", tag, sr.getTag());
    }

    public void testConstructor2(){
        SessionResult sr2 = new SessionResult(tag, sr);
        assertTrue(sr == sr2.parentResults);
    }

    public void testAddChildResult(){
        assertEquals("# of function results", 0, sr.getChildrenResults().size());
        FunctionResult fr = new FunctionResult();
        sr.addChildResult(fr);
        assertEquals("# of function results", 1, sr.getChildrenResults().size());
        assertTrue("function result", fr == sr.getChildrenResults().get(0));
    }

    public void testToXML(){
        String xml = sr.toXML();
        XMLHelper xh = new XMLHelper(xml);
        
        assertEquals("application", "0", xh.getValueFromXPath("/session-result/execution-time-millis"));

        Attribute attr = new Attribute();
        attr.setName("application");
        attr.setValue("app");
        MockFunctionalPoint mfp = new MockFunctionalPoint();
        mfp.addAttribute(attr);
        sr.setTag(mfp);

        xml = sr.toXML();
        xh = new XMLHelper(xml);

        assertEquals("application", "app", xh.getValueFromXPath("/session-result/application"));
        assertEquals("functional point toXML got called", 1, mfp.toXMLNumOfCalls);
    }

    private class MockFunctionalPoint extends FunctionalPoint{
        public int toXMLNumOfCalls;
        public String toXML() {
            toXMLNumOfCalls ++;
            return "";
        }
    }

}