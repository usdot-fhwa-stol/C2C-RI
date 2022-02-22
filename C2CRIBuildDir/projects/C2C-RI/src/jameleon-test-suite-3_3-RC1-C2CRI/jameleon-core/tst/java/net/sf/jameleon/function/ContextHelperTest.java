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
package net.sf.jameleon.function;

import net.sf.jameleon.util.Configurator;

import org.apache.commons.jelly.JellyContext;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.LinkedList;
import java.util.List;

public class ContextHelperTest extends TestCase {

    JellyContext context;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(ContextHelperTest.class);
    }

    public ContextHelperTest(String name) {
        super(name);
    }

    public void setUp() {
        context = new JellyContext();
    }

    public void tearDown(){
        context.clear();
        context = null;
    }

    public void testGetVariableAsInt() {
        assertEquals(-1, ContextHelper.getVariableAsInt(context, "myName"));
        ContextHelper.setVariable(context, "myName", "1");
        assertEquals(1, ContextHelper.getVariableAsInt(context, "myName"));
        ContextHelper.setVariable(context, "myName", "0");
        assertEquals(0, ContextHelper.getVariableAsInt(context, "myName"));
        ContextHelper.setVariable(context, "myName", "abc");
        assertEquals(-1, ContextHelper.getVariableAsInt(context, "myName"));
    }

    public void testGetVariableAsStringWithConfig(){
        Configurator config = Configurator.getInstance();
        final String varName = "varName";
        String value = "one";
        assertEquals(value, ContextHelper.getValueAsStringWithConfig(context, varName, varName, value));
        value = "two";
        config.setValue(varName, value);
        assertEquals(value, ContextHelper.getValueAsStringWithConfig(context, varName, varName, "one"));
        value = "three";
        context.setVariable(varName, value);
        assertEquals(value, ContextHelper.getValueAsStringWithConfig(context, varName, varName, "one"));
    }

    public void testGetVariableAsBooleanWithConfig(){
        Configurator config = Configurator.getInstance();
        final String varName = "SomevarName";
        boolean value = true;
        assertEquals(value, ContextHelper.getValueAsBooleanWithConfig(context, varName, varName, value));
        value = false;
        config.setValue(varName, value+"");
        assertEquals(value, ContextHelper.getValueAsBooleanWithConfig(context, varName, varName, true));
        value = true;
        context.setVariable(varName, value+"");
        assertEquals(value, ContextHelper.getValueAsBooleanWithConfig(context, varName, varName, false));
    }

    public void testGetVariableAsIntWithConfig(){
        Configurator config = Configurator.getInstance();
        final String varName = "AnotherVarName";
        int value = 1;
        assertEquals(value, ContextHelper.getValueAsIntWithConfig(context, varName, varName, value));
        value = 2;
        config.setValue(varName, value+"");
        assertEquals(value, ContextHelper.getValueAsIntWithConfig(context, varName, varName, 1));
        value = 3;
        context.setVariable(varName, value+"");
        assertEquals(value, ContextHelper.getValueAsIntWithConfig(context, varName, varName, 1));
        context.removeVariable(varName);
        config.setValue(varName, "abd");
        assertEquals(-1, ContextHelper.getValueAsIntWithConfig(context, varName, varName, 1));
    }

    public void testJellyContextMethods() {
        assertNull(ContextHelper.getVariable(context, "myName"));
        ContextHelper.setVariable(context, "myName", "myValue");
        assertEquals("myValue", ContextHelper.getVariable(context, "myName"));
        ContextHelper.removeVariable(context, "myName");
        assertNull(ContextHelper.getVariable(context, "myName"));
    }

    public void testGetVariableAsString() {
        assertEquals(null, ContextHelper.getVariableAsString(context, "myName"));
        ContextHelper.setVariable(context, "myName", "myValue");
        assertEquals("myValue", ContextHelper.getVariableAsString(context, "myName"));
    }

    public void testGetVariableWithNullKey(){
        boolean exceptionThrown = false;
        try{
            ContextHelper.getVariable(context, null);
        }catch(NullPointerException npe){
            exceptionThrown = true;
        }
        assertFalse("An exception was thrown when getting a null key",exceptionThrown);
    }

    public void testGetVariableWithEmptyKey(){
        boolean exceptionThrown = false;
        try{
            ContextHelper.getVariable(context, "");
        }catch(NullPointerException npe){
            exceptionThrown = true;
        }
        assertFalse("An exception was thrown when getting an empty key",exceptionThrown);
    }

    public void testGetVariableAsBoolean() {
        ContextHelper.setVariable(context, "myName", Boolean.TRUE);
        assertEquals(true, ContextHelper.getVariableAsBoolean(context, "myName"));
        ContextHelper.setVariable(context, "myName", Boolean.FALSE);
        assertEquals(false, ContextHelper.getVariableAsBoolean(context, "myName"));
        ContextHelper.setVariable(context, "myName", "true");
        assertEquals(true, ContextHelper.getVariableAsBoolean(context, "myName"));
        ContextHelper.setVariable(context, "myName", "yes");
        assertEquals(false, ContextHelper.getVariableAsBoolean(context, "myName"));
        ContextHelper.setVariable(context, "myName", "");
        assertEquals(false, ContextHelper.getVariableAsBoolean(context, "myName"));
    }

    public void testGetVariableAsList() {
        ContextHelper.setVariable(context, "myName", "myValue");
        assertEquals(1, ContextHelper.getVariableAsList(context, "myName").size());
        assertEquals("myValue", ContextHelper.getVariableAsList(context, "myName").get(0));
        LinkedList list = new LinkedList();
        list.add("Hello");
        list.add("World");
        ContextHelper.setVariable(context, "myName", list);
        assertEquals(2, ContextHelper.getVariableAsList(context, "myName").size());
        assertEquals("Hello", ContextHelper.getVariableAsList(context, "myName").get(0));
        assertEquals("World", ContextHelper.getVariableAsList(context, "myName").get(1));
    }

    public void testIsVariableNull() {
        assertTrue(ContextHelper.isVariableNull(context, "myName"));
        ContextHelper.setVariable(context, "myName", "");
        assertTrue(ContextHelper.isVariableNull(context, "myName"));
        ContextHelper.setVariable(context, "myName", "true");
        assertFalse(ContextHelper.isVariableNull(context, "myName"));
    }

    public void testMakeList() {
        LinkedList list = new LinkedList();
        list.add("Hello");
        list.add("World");
        assertEquals(2, ContextHelper.makeList(list).size());
        assertEquals("Hello", ContextHelper.makeList(list).get(0));
        assertEquals("World", ContextHelper.makeList(list).get(1));
        assertEquals(1, ContextHelper.makeList("Hello World").size());
        assertEquals("Hello World", ContextHelper.makeList("Hello World").get(0));
    }

    public void testRemoveVariables(){
        context.setVariable("1", "one");
        context.setVariable("2", "two");
        context.setVariable("3", "three");
        List vars = new LinkedList();
        vars.add("1");
        vars.add("3");
        ContextHelper.removeVariables(context, vars);
        assertEquals("this variable should be left alone", "two", context.getVariable("2"));
        assertNull("this variable should be null", context.getVariable("1"));
        assertNull("this variable should be null", context.getVariable("3"));
    }

}



