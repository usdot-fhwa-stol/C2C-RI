/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.bean;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.jelly.expression.CompositeExpression;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.jexl.JexlExpressionFactory;

import java.util.ArrayList;
import java.util.List;

public class AttributeTest extends TestCase{

    private Attribute attr = null;
    private static final String A_NAME = "accountType";
    private static final String C_NAME = "contextName";
    private static final String D_VALUE = "defaultValue";
    private static final List VALUE = new ArrayList();
    private static final String DESCRIPTION = "The type of account to include in the query";
    private static final String TYPE = "alpha-numeric";
    private static final boolean REQUIRED = true;

    public AttributeTest( String name ) {
        super( name );
    }

    //JUnit Methods
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( AttributeTest.class );
    }

    public void setUp() {
        attr = new Attribute();
    }
    //End JUnit Methods

    public void testGetDisplayedValue() throws Exception{
    	attr.setValue(A_NAME);
    	assertEquals("value with string", A_NAME, attr.getDisplayedValue());
    	VALUE.add(A_NAME);
    	VALUE.add(C_NAME);
    	VALUE.add(D_VALUE);
    	attr.setValue(VALUE);
    	assertEquals("["+A_NAME+", "+C_NAME+", "+ D_VALUE+"]", attr.getDisplayedValue());
    	final Expression exp = CompositeExpression.parse("${some} text", new JexlExpressionFactory());
    	attr.setValue(exp);
    	assertEquals(exp.getExpressionText(), attr.getDisplayedValue());
    }
    
    public void testClone() throws Exception{
    	attr.setContextName(C_NAME);
    	attr.setDefaultValue(D_VALUE);
    	attr.setDescription(DESCRIPTION);
    	attr.setName(A_NAME);
    	attr.setRequired(REQUIRED);
    	attr.setType(TYPE);
    	VALUE.add("some value");
    	attr.setValue(VALUE);
    	Attribute attr2 = (Attribute) attr.clone();
    	String attrXml = attr.toXML();
    	String attr2Xml = attr2.toXML();
    	assertFalse("should be pointing to different references", attr == attr2);
    	assertEquals("attributes", attrXml, attr2Xml);
    }
    
    public void testAttributeConstructorDefault(){
        assertNotNull("Name should be an empty non-null String",attr.getName());
        assertNotNull("Description should be an empty non-null String",attr.getDescription());
        assertNotNull("Type should be an empty non-null String",attr.getType());
        assertFalse("Required should default to false!",attr.isRequired());
        assertNotNull("InstanceVariableName should be an empty non-null String",attr.getContextName());
        assertNull("Default value should be null", attr.getDefaultValue());
        assertFalse("Should default to non-instance variable", attr.isInstanceVariable());
    }

    public void testAttributeSetName(){
        attr.setName(A_NAME);
        assertEquals("Name:", A_NAME, attr.getName());
    }

    public void testAttributeSetDescription(){
        attr.setDescription(DESCRIPTION);
        assertEquals("Description:", DESCRIPTION, attr.getDescription());
    }

    public void testAttributeSetType(){
        attr.setType(TYPE);
        assertEquals("Type:", TYPE, attr.getType());
    }

    public void testAttributeSetRequired(){
        attr.setRequired(REQUIRED);
        assertTrue("Required:",attr.isRequired());
    }

    public void testAttributeSetContextName(){
        attr.setContextName(C_NAME);
        assertEquals("Context name", C_NAME, attr.getContextName());
    }

    public void testAttributeToStringWithName(){
        attr.setName(A_NAME);
        attr.setRequired(REQUIRED);
        attr.setDescription(DESCRIPTION);
        assertEquals("toString() ", A_NAME, attr.toString());
    }

    public void testAttributeToStringWithContextName(){
        attr.setContextName(C_NAME);
        attr.setRequired(REQUIRED);
        attr.setDescription(DESCRIPTION);
        assertEquals("toString() ", C_NAME, attr.toString());
    }

    public void testAttributeToStringWithNameAndContextName(){
        attr.setName(A_NAME);
        attr.setContextName(C_NAME);
        attr.setRequired(REQUIRED);
        attr.setDescription(DESCRIPTION);
        assertEquals("toString() ", A_NAME+" or "+C_NAME, attr.toString());
    }

    public void testDefaultValue() {
        attr.setDefaultValue("lala");
        assertEquals("default value", "lala", attr.getDefaultValue());
    }

    public void testIsContextVariable(){
        attr.setContextName("somename");
        assertTrue("Should be a context variable", attr.isContextVariable());
        attr.setInstanceVariable(true);
        assertTrue("Should be a context variable", attr.isContextVariable());
        attr = new Attribute();
        assertFalse("Should not be a context variable", attr.isContextVariable());
    }

    public void testSetValue(){
        attr.setValue("some value");
        assertEquals("some value", attr.getValue());
        assertTrue("variable should have been set", attr.isValueSet());
        attr.valueSet = false;
        attr.setValue(null);
        assertFalse("variable should not have been set", attr.isValueSet());
    }

    public void testToXml() {
        attr.setName(A_NAME);
        attr.setRequired(REQUIRED);
        attr.setDescription(DESCRIPTION);
        attr.setContextName(C_NAME);
        attr.setValue(new String("george"));
        attr.setDefaultValue("smith");
        String xml = attr.toXML();
        assertContains("Couldn't find name", "<attribute-name>" + A_NAME + "</attribute-name>", xml);
        assertContains("Couldn't find name", "<attribute-instancevariable>" + false + "</attribute-instancevariable>", xml);
        assertContains("Couldn't find description", "<attribute-description>" + DESCRIPTION + "</attribute-description>", xml);
        assertContains("Couldn't find type", "<attribute-type></attribute-type>", xml);
        assertContains("Couldn't find required", "<attribute-required>" + REQUIRED + "</attribute-required>", xml);
        assertContains("Couldn't find field name", "<attribute-contextname>" + C_NAME + "</attribute-contextname>", xml);
        assertContains("Couldn't find value", "<attribute-value>george</attribute-value>", xml);
        assertContains("Couldn't find default value", "<attribute-defaultvalue>smith</attribute-defaultvalue>", xml);
    }

    protected void assertContains(String msg, String expectedPattern, String actual) {
        assertNotNull(msg + ": The string was null", actual);
        assertTrue(msg, actual.indexOf(expectedPattern) >= 0);
    }

    protected void assertNotContains(String msg, String expectedPattern, String actual) {
        assertTrue(msg, actual == null || actual.indexOf(expectedPattern) < 0);
    }

}
