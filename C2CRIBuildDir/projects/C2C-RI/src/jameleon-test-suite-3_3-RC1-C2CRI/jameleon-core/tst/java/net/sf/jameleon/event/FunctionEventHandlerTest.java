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

import net.sf.jameleon.function.MockFunctionTag;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FunctionEventHandlerTest extends TestCase{

    private FunctionEventHandler eventHandler;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( FunctionEventHandlerTest.class );
    }

    public FunctionEventHandlerTest( String name ) {
        super( name );
    }

    public void setUp() {
        eventHandler = FunctionEventHandler.getInstance();
    }

    public void tearDown(){
        eventHandler.clearInstance();
    }

    public void testGetInstance(){
        FunctionEventHandler eventHandler2 = FunctionEventHandler.getInstance();
        assertTrue("There should only be a single instance", eventHandler == eventHandler2);
    }

    public void testAddFunctionListener(){
        assertEquals("# of function listeners before add.", 0, eventHandler.getFunctionListeners().size());
        Mock mockListener = new Mock(FunctionListener.class);
        eventHandler.addFunctionListener((FunctionListener)mockListener.proxy());
        assertEquals("# of function listeners after add.", 1, eventHandler.getFunctionListeners().size());
    }

    public void testRemoveFunctionListener(){
        Mock mockListener = new Mock(FunctionListener.class);
        FunctionListener fl = (FunctionListener)mockListener.proxy();
        eventHandler.addFunctionListener(fl);
        assertEquals("# of function listeners after add.", 1, eventHandler.getFunctionListeners().size());
        eventHandler.removeFunctionListener(fl);
        assertEquals("# of function listeners after remove.", 0, eventHandler.getFunctionListeners().size());
    }

    public void testBeginFunction(){
        MockFunctionTag ft = new MockFunctionTag();

        TestFunctionListener listener = new TestFunctionListener();
        eventHandler.addFunctionListener(listener);
        eventHandler.beginFunction(ft,0);
        assertTrue("beginFunction wasn't called", listener.beginFunctionCalled);
        assertTrue("beginFunction's event should contain the function tag for it", ft == listener.beginFunctionEvent.getSource());
    }


    public void testEndFunction(){
        MockFunctionTag ft = new MockFunctionTag();

        TestFunctionListener listener = new TestFunctionListener();
        eventHandler.addFunctionListener(listener);
        eventHandler.endFunction(ft, 0);
        assertTrue("endFunction wasn't called", listener.endFunctionCalled);
        assertTrue("endFunction's event should contain the function tag for it", ft == listener.endFunctionEvent.getSource());
    }

}
