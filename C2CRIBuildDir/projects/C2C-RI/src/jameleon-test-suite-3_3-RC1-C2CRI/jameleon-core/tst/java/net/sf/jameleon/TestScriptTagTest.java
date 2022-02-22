/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)
    
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

import java.io.File;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestScriptTagTest extends TestCase {

    private TestSuiteTag tsTag;
    private TestScriptTag tScriptTag;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestScriptTagTest.class);
    }

    public TestScriptTagTest(String name) {
        super(name);
    }

    public void setUp(){
        tsTag = new TestSuiteTag();
        tsTag.executor = new ExecuteTestCase();
        tScriptTag = new TestScriptTag();
        tScriptTag.setParent(tsTag);
    }

    public void testInitValid() throws Exception{
        tScriptTag.setAttribute("script", "value.xml");
        File f = new File("value.xml");
        tScriptTag.init();
        assertEquals("script field", f, tScriptTag.script);
    }

    public void testInitInvalid(){
        tScriptTag.setAttribute("invalid field", "value");
        boolean exceptionThrown = false;
        try{
            tScriptTag.init();
        }catch(Exception e){
            exceptionThrown = true;
        }
        assertTrue("an exception should have been thrown", exceptionThrown);
    }

    public void testSetUpEnvironmentValid(){
        tScriptTag.setUpEnvironment();
        assertTrue(tScriptTag.tst == tsTag);
    }

    public void testSetUpEnvironmentInvalid(){
        tScriptTag.setParent(null);
        boolean error = false;
        try{
            tScriptTag.setUpEnvironment();
        }catch(ClassCastException cce){
            error = true;
        }
        assertTrue("An exception was not thrown", error);
    }

}
