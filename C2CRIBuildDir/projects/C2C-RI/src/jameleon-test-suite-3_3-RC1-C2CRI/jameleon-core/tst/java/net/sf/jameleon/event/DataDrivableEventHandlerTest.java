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

import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.data.DataDrivable;

import com.mockobjects.dynamic.AnyConstraintMatcher;
import com.mockobjects.dynamic.Mock;

public class DataDrivableEventHandlerTest extends TestCase{

    private DataDrivableEventHandler eventHandler;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( DataDrivableEventHandlerTest.class );
    }

    public DataDrivableEventHandlerTest( String name ) {
        super( name );
    }

    public void setUp() {
        eventHandler = DataDrivableEventHandler.getInstance();
    }

    public void tearDown(){
        eventHandler.clearInstance();
    }

    public void testGetInstance(){
        DataDrivableEventHandler eventHandler2 = DataDrivableEventHandler.getInstance();
        assertTrue("There should only be a single instance", eventHandler == eventHandler2);
    }

    public void testAddFunctionListener(){
        assertEquals("# of function listeners before add.", 0, eventHandler.getDataDrivableListeners().size());
        Mock mockListener = new Mock(DataDrivableListener.class);
        eventHandler.addDataDrivableListener((DataDrivableListener)mockListener.proxy());
        assertEquals("# of function listeners after add.", 1, eventHandler.getDataDrivableListeners().size());
    }

    public void testRemoveFunctionListener(){
        Mock mockListener = new Mock(DataDrivableListener.class);
        DataDrivableListener fl = (DataDrivableListener)mockListener.proxy();
        eventHandler.addDataDrivableListener(fl);
        assertEquals("# of function listeners after add.", 1, eventHandler.getDataDrivableListeners().size());
        eventHandler.removeDataDrivableListener(fl);
        assertEquals("# of function listeners after remove.", 0, eventHandler.getDataDrivableListeners().size());
    }

    public void testOpenEvent(){
        Mock mockListener = new Mock(DataDrivableListener.class);
        mockListener.expect("openEvent", new AnyConstraintMatcher());
        Mock mockDataDrivable = new Mock(DataDrivable.class);
        mockDataDrivable.expect("toString");
        
        eventHandler.addDataDrivableListener((DataDrivableListener)mockListener.proxy());
        eventHandler.openEvent((DataDrivable)mockDataDrivable.proxy());
    }

    public void testCloseEvent(){
        Mock mockListener = new Mock(DataDrivableListener.class);
        mockListener.expect("closeEvent", new AnyConstraintMatcher());
        Mock mockDataDrivable = new Mock(DataDrivable.class);
        mockDataDrivable.expect("toString");

        eventHandler.addDataDrivableListener((DataDrivableListener)mockListener.proxy());
        eventHandler.closeEvent((DataDrivable)mockDataDrivable.proxy());
    }

    public void testExecuteRowEvent(){
        Mock mockListener = new Mock(DataDrivableListener.class);
        mockListener.expect("executeRowEvent", new AnyConstraintMatcher());
        Mock mockDataDrivable = new Mock(DataDrivable.class);
        mockDataDrivable.expect("toString");

        eventHandler.addDataDrivableListener((DataDrivableListener)mockListener.proxy());
        eventHandler.executeRowEvent(new HashMap(),(DataDrivable)mockDataDrivable.proxy(),1);
    }

}
