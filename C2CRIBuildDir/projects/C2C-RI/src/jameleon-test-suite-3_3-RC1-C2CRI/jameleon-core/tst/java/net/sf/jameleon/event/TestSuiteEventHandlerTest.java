/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.event;

import com.mockobjects.dynamic.Mock;

import net.sf.jameleon.TestSuiteTag;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestSuiteEventHandlerTest extends TestCase{

    private TestSuiteEventHandler eventHandler;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestSuiteEventHandlerTest.class );
    }

    public TestSuiteEventHandlerTest( String name ) {
        super( name );
    }

    public void setUp() {
        eventHandler = TestSuiteEventHandler.getInstance();
    }

    public void tearDown(){
        eventHandler.clearInstance();
    }

    public void testGetInstance(){
        TestSuiteEventHandler eventHandler2 = TestSuiteEventHandler.getInstance();
        assertTrue("There should only be a single instance", eventHandler == eventHandler2);
    }

    public void testAddTestSuiteListener(){
        assertEquals("# of test suite listeners before add.", 0, eventHandler.getTestSuiteListeners().size());
        Mock mockListener = new Mock(TestSuiteListener.class);
        eventHandler.addTestSuiteListener((TestSuiteListener)mockListener.proxy());
        assertEquals("# of test suite listeners after add.", 1, eventHandler.getTestSuiteListeners().size());
    }

    public void testRemoveTestCaseListener(){
        Mock mockListener = new Mock(TestSuiteListener.class);
        TestSuiteListener tsl = (TestSuiteListener)mockListener.proxy();
        eventHandler.addTestSuiteListener(tsl);
        assertEquals("# of test case listeners after add.", 1, eventHandler.getTestSuiteListeners().size());
        eventHandler.removeTestSuiteListener(tsl);
        assertEquals("# of test suite listeners after remove.", 0, eventHandler.getTestSuiteListeners().size());
    }

    public void testBeginTestSuite(){
        TestSuiteTag tst = new TestSuiteTag();

        TestTestSuiteListener listener = new TestTestSuiteListener();
        eventHandler.addTestSuiteListener(listener);
        eventHandler.beginTestSuite(tst);
        assertEquals("beginTestSuite wasn't called", 1, listener.numBeginCalled);
        assertTrue("beginTestSuite's event should contain the test suite tag for it", tst == listener.beginEvent.getSource());
    }


    public void testEndTestSuite(){
        TestSuiteTag tst = new TestSuiteTag();

        TestTestSuiteListener listener = new TestTestSuiteListener();
        eventHandler.addTestSuiteListener(listener);
        eventHandler.endTestSuite(tst);
        assertEquals("endTestSuite wasn't called", 1, listener.numEndCalled);
        assertTrue("endTestSuite's event should contain the test case tag for it", tst == listener.endEvent.getSource());
    }

}
