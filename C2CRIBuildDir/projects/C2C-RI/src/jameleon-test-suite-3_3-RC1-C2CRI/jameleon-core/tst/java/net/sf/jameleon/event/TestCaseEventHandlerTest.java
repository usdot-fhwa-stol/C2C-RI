/*
    Jameleon - An automation testing tool..
    Copyright (C) 2005 Christian W. Hargraves (engrean@hotmail.com)
    
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

import net.sf.jameleon.MockTestCaseTag;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestCaseEventHandlerTest extends TestCase{

    private TestCaseEventHandler eventHandler;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestCaseEventHandlerTest.class );
    }

    public TestCaseEventHandlerTest( String name ) {
        super( name );
    }

    public void setUp() {
        eventHandler = TestCaseEventHandler.getInstance();
    }

    public void tearDown(){
        eventHandler.clearInstance();
    }

    public void testGetInstance(){
        TestCaseEventHandler eventHandler2 = TestCaseEventHandler.getInstance();
        assertTrue("There should only be a single instance", eventHandler == eventHandler2);
    }

    public void testAddTestCaseListener(){
        assertEquals("# of test case listeners before add.", 0, eventHandler.getTestCaseListeners().size());
        Mock mockListener = new Mock(TestCaseListener.class);
        eventHandler.addTestCaseListener((TestCaseListener)mockListener.proxy());
        assertEquals("# of test case listeners after add.", 1, eventHandler.getTestCaseListeners().size());
    }

    public void testRemoveTestCaseListener(){
        Mock mockListener = new Mock(TestCaseListener.class);
        TestCaseListener tcl = (TestCaseListener)mockListener.proxy();
        eventHandler.addTestCaseListener(tcl);
        assertEquals("# of test case listeners after add.", 1, eventHandler.getTestCaseListeners().size());
        eventHandler.removeTestCaseListener(tcl);
        assertEquals("# of test case listeners after remove.", 0, eventHandler.getTestCaseListeners().size());
    }

    public void testBeginTestCase(){
        MockTestCaseTag tct = new MockTestCaseTag();

        TestTestCaseListener listener = new TestTestCaseListener();
        eventHandler.addTestCaseListener(listener);
        eventHandler.beginTestCase(tct);
        assertTrue("beginTestCase wasn't called", listener.beginTestCaseCalled);
        assertTrue("beginTestCase's event should contain the test case tag for it", tct == listener.beginTestCaseEvent.getSource());
    }


    public void testEndTestCase(){
        MockTestCaseTag tct = new MockTestCaseTag();

        TestTestCaseListener listener = new TestTestCaseListener();
        eventHandler.addTestCaseListener(listener);
        eventHandler.endTestCase(tct);
        assertTrue("endTestCase wasn't called", listener.endTestCaseCalled);
        assertTrue("endTestCase's event should contain the test case tag for it", tct == listener.endTestCaseEvent.getSource());
    }

}
