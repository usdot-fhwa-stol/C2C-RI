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

import com.mockobjects.dynamic.AnyConstraintMatcher;
import com.mockobjects.dynamic.Mock;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Calendar;

public class TestRunEventHandlerTest extends TestCase {

    private TestRunEventHandler eventHandler;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestRunEventHandlerTest.class );
    }

    public TestRunEventHandlerTest( String name ) {
        super( name );
    }

    public void setUp() {
        eventHandler = TestRunEventHandler.getInstance();
    }

    public void tearDown(){
        eventHandler.clearInstance();
    }

    public void testGetInstance(){
        TestRunEventHandler eventHandler2 = TestRunEventHandler.getInstance();
        assertTrue("There should only be a single instance", eventHandler == eventHandler2);
    }

    public void testAddTestRunListener(){
        assertEquals("# of test run listeners before add.", 0, eventHandler.getTestRunListeners().size());
        Mock mockListener = new Mock(TestRunListener.class);
        eventHandler.addTestRunListener((TestRunListener)mockListener.proxy());
        assertEquals("# of test run listeners after add.", 1, eventHandler.getTestRunListeners().size());
    }

    public void testRemoveTestRunListener(){
        Mock mockListener = new Mock(TestRunListener.class);
        TestRunListener tsl = (TestRunListener)mockListener.proxy();
        eventHandler.addTestRunListener(tsl);
        assertEquals("# of test run listeners after add.", 1, eventHandler.getTestRunListeners().size());
        eventHandler.removeTestRunListener(tsl);
        assertEquals("# of test run listeners after remove.", 0, eventHandler.getTestRunListeners().size());
    }

    public void testBeginTestRun(){
        Mock mockListener = new Mock(TestRunListener.class);
        mockListener.expect("beginTestRun", new AnyConstraintMatcher());
        TestRunListener trl = (TestRunListener)mockListener.proxy();
        eventHandler.addTestRunListener(trl);
        eventHandler.beginTestRun(Calendar.getInstance());
        mockListener.verify();
    }


    public void testEndTestRun(){
        Mock mockListener = new Mock(TestRunListener.class);
        mockListener.expect("endTestRun",  new AnyConstraintMatcher());
        TestRunListener trl = (TestRunListener)mockListener.proxy();
        eventHandler.addTestRunListener(trl);
        eventHandler.endTestRun(Calendar.getInstance());
        mockListener.verify();
    }

}
