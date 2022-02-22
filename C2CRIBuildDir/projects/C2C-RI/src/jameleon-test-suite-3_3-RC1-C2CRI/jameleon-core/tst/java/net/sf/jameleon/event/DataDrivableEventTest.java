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

public class DataDrivableEventTest extends TestCase{

    private DataDrivableEvent de;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( DataDrivableEventTest.class );
    }

    public DataDrivableEventTest( String name ) {
        super( name );
    }

    public void setUp() {
        de = new DataDrivableEvent(new String("Hello"));
    }

    public void tearDown(){
        de = null;
    }

    public void testConstructor(){
        assertNotNull("DataDrivableEvent was null", de);
        String source = (String)de.getSource();
        assertEquals("DataDrivableEvent source", "Hello", source);
    }

    public void testSetDataRow(){
        de.setRowData(new HashMap());
        assertNotNull("DataDrivableEvent.rowData", de.getRowData());
        assertEquals("DataDrivableEvent.rowData.size", 0, de.getRowData().size());
    }

}
