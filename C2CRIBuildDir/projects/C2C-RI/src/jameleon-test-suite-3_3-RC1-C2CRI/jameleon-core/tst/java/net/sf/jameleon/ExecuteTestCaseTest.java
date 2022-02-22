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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.event.TestRunEventHandler;
import net.sf.jameleon.event.TestTestCaseListener;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonDefaultValues;

import java.io.File;
import java.util.List;

public class ExecuteTestCaseTest extends junit.framework.TestCase {

    private ExecuteTestCase exec;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( ExecuteTestCaseTest.class );
    }

    public ExecuteTestCaseTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception{
        exec = new ExecuteTestCase();
    }

    public void tearDown(){
        exec = null;
        TestCaseEventHandler.getInstance().clearInstance();
        Configurator.clearInstance();
        new File(JameleonDefaultValues.TEST_RUN_SUMMARY_FILE_NAME).delete();
    }

    public void testRegisterEventListeners(){
   /** For C2C RI the default listeners were removed.
    *  As a result the assertion numbers dropped by 1.
    */
        exec.registerEventListeners();

        List listeners = TestCaseEventHandler.getInstance().getTestCaseListeners();
//        assertEquals("# of tc listeners before", 1, listeners.size());
        assertEquals("# of tc listeners before", 0, listeners.size());

        List testRunListeners = TestRunEventHandler.getInstance().getTestRunListeners();
//        assertEquals("# of tr listeners before", 1, testRunListeners.size());
        assertEquals("# of tr listeners before", 0, testRunListeners.size());


        Configurator.getInstance().setValue(ExecuteTestCase.TEST_CASE_LISTENERS, TestTestCaseListener.class.getName() +
                        " "+ TestTestCaseListener.class.getName());
        exec.deregisterEventListeners();
        exec.registerEventListeners();
        listeners = TestCaseEventHandler.getInstance().getTestCaseListeners();
//        assertEquals("# of listeners after", 3, listeners.size());
        assertEquals("# of listeners after", 2, listeners.size());

        testRunListeners = TestRunEventHandler.getInstance().getTestRunListeners();
//        assertEquals("# of tr listeners after", 1, testRunListeners.size());
        assertEquals("# of tr listeners after", 0, testRunListeners.size());
        
//        assertTrue("2nd listener is not an instance of DummyTestCaseListener1", listeners.get(1) instanceof TestTestCaseListener);
//        assertTrue("3rd listener is not an instance of DummyTestCaseListener2", listeners.get(2) instanceof TestTestCaseListener);
        assertTrue("2nd listener is not an instance of DummyTestCaseListener1", listeners.get(0) instanceof TestTestCaseListener);
        assertTrue("3rd listener is not an instance of DummyTestCaseListener2", listeners.get(1) instanceof TestTestCaseListener);
//        assertFalse( listeners.get(1) == listeners.get(2));
        assertFalse( listeners.get(0) == listeners.get(1));
    }

    public void testRegisterEventListener(){
        exec.registerEventListener(null);

        List listeners = TestCaseEventHandler.getInstance().getTestCaseListeners();
        assertEquals("# of listeners before", 0, listeners.size());

        exec.registerEventListener(new TestTestCaseListener());
        listeners = TestCaseEventHandler.getInstance().getTestCaseListeners();
        assertEquals("# of listeners after", 1, listeners.size());
        assertTrue("1st listener is not an instance of DummyTestCaseListener1", listeners.get(0) instanceof TestTestCaseListener);
        exec.registerEventListener(new TestTestCaseListener());
        assertEquals("# of listeners after", 2, listeners.size());
        assertTrue("2nd listener is not an instance of DummyTestCaseListener2", listeners.get(1) instanceof TestTestCaseListener);
        assertFalse( listeners.get(0) == listeners.get(1));
    }

    public void testDeregisterEventListeners(){
        /** For C2C RI the default listeners were removed.
         *  As a result the assertion numbers dropped by 1.
         */
        Configurator.getInstance().setValue(ExecuteTestCase.TEST_CASE_LISTENERS, TestTestCaseListener.class.getName() +
                        " "+ TestTestCaseListener.class.getName());
        exec.registerEventListeners();
//        assertEquals("# of listeners", 3, TestCaseEventHandler.getInstance().getTestCaseListeners().size());
        assertEquals("# of listeners", 2, TestCaseEventHandler.getInstance().getTestCaseListeners().size());
        TestCaseEventHandler.getInstance().addTestCaseListener(new TestTestCaseListener());
//        assertEquals("# of listeners", 4, TestCaseEventHandler.getInstance().getTestCaseListeners().size());
        assertEquals("# of listeners", 3, TestCaseEventHandler.getInstance().getTestCaseListeners().size());
        exec.deregisterEventListeners();
//        assertEquals("# of listeners", 1, TestCaseEventHandler.getInstance().getTestCaseListeners().size());
        assertEquals("# of listeners", 1, TestCaseEventHandler.getInstance().getTestCaseListeners().size());
    }

}
