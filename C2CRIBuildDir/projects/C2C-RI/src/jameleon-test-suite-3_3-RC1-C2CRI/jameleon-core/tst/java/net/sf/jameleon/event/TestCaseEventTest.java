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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestCaseEventTest extends TestCase{

    private TestCaseEvent tce;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestCaseEventTest.class );
    }

    public TestCaseEventTest( String name ) {
        super( name );
    }

    public void setUp() {
        tce = new TestCaseEvent(new String("Hello"));
    }

    public void tearDown(){
        tce = null;
    }

    public void testConstructor(){
        assertNotNull("TestCaseEvent was null", tce);
        String source = (String)tce.getSource();
        assertEquals("TestCaseEvent source", "Hello", source);
    }

}
