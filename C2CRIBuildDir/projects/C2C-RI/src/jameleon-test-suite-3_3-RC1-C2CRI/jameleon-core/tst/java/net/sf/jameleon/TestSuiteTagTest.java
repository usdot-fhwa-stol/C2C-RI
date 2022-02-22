/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)
    
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.event.TestSuiteEventHandler;
import net.sf.jameleon.exception.JameleonScriptException;

public class TestSuiteTagTest extends TestCase {

    private TestSuiteTag tsTag;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestSuiteTagTest.class);
    }

    public TestSuiteTagTest(String name) {
        super(name);
    }

    public void setUp(){
        tsTag = new TestSuiteTag();
    }

    public void testInitValid() throws Exception{
        tsTag.setAttribute("name", "value");
        tsTag.setAttribute("testSuiteListener", "net.sf.jameleon.event.TestTestSuiteListener");
        tsTag.init();
        assertEquals("name field", "value", tsTag.name);
        assertEquals("testSuiteListener field", "net.sf.jameleon.event.TestTestSuiteListener", tsTag.testSuiteListener);
    }

    public void testInitInvalid(){
        tsTag.setAttribute("invalid field", "value");
        boolean exceptionThrown = false;
        try{
            tsTag.init();
        }catch(Exception e){
            exceptionThrown = true;
        }
        assertTrue("an exception should have been thrown", exceptionThrown);
    }

    public void testRegisterTestSuiteListenerValid(){
        TestSuiteEventHandler eventHandler = TestSuiteEventHandler.getInstance();
        assertEquals("# of test suite listeners", 0, eventHandler.getTestSuiteListeners().size());
        tsTag.registerTestSuiteListener("net.sf.jameleon.event.TestTestSuiteListener");
        assertEquals("# of test suite listeners", 1, eventHandler.getTestSuiteListeners().size());
    }

    public void testRegisterTestSuiteListenerInvalid(){
        TestSuiteEventHandler.getInstance().clearInstance();
        TestSuiteEventHandler eventHandler = TestSuiteEventHandler.getInstance();
        assertEquals("# of test suite listeners", 0, eventHandler.getTestSuiteListeners().size());
        boolean exceptionThrown = false;
        try{
            tsTag.registerTestSuiteListener("net.sf.jameleon.event.BadListenerName");
        }catch(JameleonScriptException jse){
            exceptionThrown = true;
        }
        assertTrue("an exception should have been thrown", exceptionThrown);
        assertEquals("# of test suite listeners", 0, eventHandler.getTestSuiteListeners().size());
    }

}
