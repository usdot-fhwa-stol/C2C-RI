
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

import org.apache.commons.jelly.LocationAware;

public class JameleonScriptExceptionTest extends TestCase {

    private JameleonScriptException jse;

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( JameleonScriptExceptionTest.class );
    }

    public JameleonScriptExceptionTest( String name ) {
        super( name );
    }

    public void testDefaultConstructor(){
        jse = new JameleonScriptException();
        assertNotNull("JameleonScriptException after initialization", jse);
    }

    public void testConstructorWithErrorMessage(){
        jse = new JameleonScriptException("error message");
        assertNotNull("JameleonScriptException after initialization", jse);
        assertEquals("JameleonScriptException error message", "error message", jse.getMessage());
    }

    public void testConstructorWithCause(){
        RuntimeException re = new RuntimeException("re message");
        jse = new JameleonScriptException(re);
        assertNotNull("JameleonScriptException after initialization", jse);
        assertEquals("cause", re, jse.getCause());
    }

    public void testConstructorWithCauseAndLocationAware(){
        RuntimeException re = new RuntimeException("re message");
        LocationAware la = new MockLocationAware();
        jse = new JameleonScriptException(re, la);
        assertNotNull("JameleonScriptException after initialization", jse);
        assertEquals("cause", re, jse.getCause());
        testLocationAwareProperties(la, jse);
    }

    public void testConstructorWithErrorMessageAndLocationAware(){
        LocationAware la = new MockLocationAware();
        jse = new JameleonScriptException("error message", la);
        assertNotNull("JameleonScriptException after initialization", jse);
        testLocationAwareProperties(la, jse);
    }

    public void testConstructorWithErrorMessageAndCause(){
        RuntimeException re = new RuntimeException("re message");
        jse = new JameleonScriptException("some error",re);
        assertNotNull("JameleonScriptException after initialization", jse);
        assertEquals("JameleonScriptException message", "some error", jse.getMessage());
        assertEquals("cause", re, jse.getCause());
    }

    public void testConstructorWithErrorMessageCauseAndLocationAware(){
        LocationAware la = new MockLocationAware();
        RuntimeException re = new RuntimeException("re message");
        jse = new JameleonScriptException("another message",re, la);
        testLocationAwareProperties(la, jse);
    }   

    public void testCopyLocationAwareProperties(){
        LocationAware la = new MockLocationAware();
        jse = new JameleonScriptException();
        jse.copyLocationAwareProperties(la);
        testLocationAwareProperties(la, jse);
    }

    protected void testLocationAwareProperties(LocationAware la, JameleonScriptException jse){
        assertEquals("lineNumber", la.getLineNumber(), jse.getLineNumber());
        assertEquals("columnNumber", la.getColumnNumber(), jse.getColumnNumber());
        assertEquals("fileName", la.getFileName(), jse.getFileName());
        assertEquals("elementName", la.getElementName(), jse.getElementName());
    }

    protected class MockLocationAware implements LocationAware{
        public int getLineNumber(){return 10;}
        public void setLineNumber(int lineNumber){}
        public int getColumnNumber(){return 20;}
        public void setColumnNumber(int columnNumber){}
        public String getFileName(){return "fileName";}
        public void setFileName(String fileName){}
        public String getElementName(){return "elementName";}
        public void setElementName(String elementName){}
    }

}
