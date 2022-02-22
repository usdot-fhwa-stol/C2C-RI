
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.exception;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JameleonTagExceptionTest extends TestCase {
    private JameleonTagException jte;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( JameleonTagExceptionTest.class );
    }

    public JameleonTagExceptionTest( String name ) {
        super( name );
    }

    public void setUp() {
    }

    public void tearDown(){
    }

    public void testDefaultConstructor(){
        jte = new JameleonTagException();
        assertNotNull("JameleonTagException after initialization", jte);
    }

    public void testConstructorWithTagName(){
        jte = new JameleonTagException("tagname");
        assertNotNull("JameleonTagException after initialization", jte);
        assertTrue("JameleonTagException error message", jte.getMessage().indexOf("null:-1:-1: <tagname> null") >= 0);
    }

    public void testConstructorWithTagNameAndMessage(){
        jte = new JameleonTagException("tagname", "some message");
        assertNotNull("JameleonTagException after initialization", jte);
        assertTrue("JameleonTagException error message", jte.getMessage().indexOf("null:-1:-1: <tagname> some message") >= 0);
    }

}
