package net.sf.jameleon;

import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.AttributeBroker;

import org.apache.commons.jelly.MissingAttributeException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class JameleonTagSupportTest extends TestCase {

    protected DummyJameleonTagSupport ft;
    protected FunctionalPoint fp;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(JameleonTagSupportTest.class);
    }

    public JameleonTagSupportTest(String name) {
        super(name);
    }

    public void setUp(){
        ft = new DummyJameleonTagSupport();
        fp = ft.fp;
    }

    public void tearDown(){
        fp = null;
        ft = null;
    }

    public void testGetAttributeBroker(){
        assertTrue("Attribute broker instance", ft.broker == ft.getAttributeBroker());
    }

    public void testLoadFunctionalPoint(){
        assertNotNull("Functional Point should not be null", fp);
        assertEquals("Tag Name", "mock-jameleon-tag-support", (String)fp.getTagNames().get(0));
        assertEquals("# of attributes", 2, fp.getAttributes().size());
    }
    
    public void testDescribeAttributes() {
        AttributeBroker broker = new AttributeBroker(ft);
        broker.setUp();
        //There should be three of these, but only two should be passed through.
        assertEquals("Number of attributes", 2, broker.getAttributes().size());
    }

    public void testDescribeAttributesNullFunctionalPoint() {
        ft.fp = null;
        AttributeBroker broker = new AttributeBroker(ft);
        broker.setUp();
        //There should be three of these, but only two should be passed through.
        assertEquals("Number of attributes", 0, broker.getAttributes().size());
    }

    public void testSetAttribute() {
        ft.broker.transferAttributes(ft.getContext());
        assertEquals("fortyTwo instance variable", 42, ft.fortyTwo);
        ft.setAttribute("fortyTwo","43");
        ft.setAttribute("myString","yourString");

        assertEquals("fortyTwo instance variable", 43, ft.fortyTwo);
        String value = (String)ft.fp.getAttribute("fortyTwo").getValue();
        assertEquals("Value in Attribute object", "43", value);
        ft.broker.transferAttributes(ft.getContext());
        value = (String)ft.fp.getAttribute("myString").getValue();
        assertEquals("Value in Attribute object", "yourString", value);

        boolean exceptionThrown = false;
        try{
            ft.broker.validate(ft.getContext());
        }catch (MissingAttributeException mae){
            exceptionThrown = true;
        }
        assertFalse("An exception should not have been thrown after the variable was set.",exceptionThrown);
    }

    public void testAttributesValueGetsSetWhenContextVariableSet(){
        ft.getContext().setVariable("dmmy_string", "hisString");
        ft.broker.transferAttributes(ft.getContext());
        assertEquals("Context variable value should be set in attribute","hisString", ((Attribute)ft.fp.getAttributes().get("myString")).getValue());
    }

    public void testSetAttributeRequiredVariable(){
        DummyJameleonTagSupport2 dft = new DummyJameleonTagSupport2();
        dft.broker.transferAttributes(dft.getContext());
        boolean exceptionThrown = false;
        try{
            dft.broker.validate(dft.getContext());
        }catch (MissingAttributeException mae){
            exceptionThrown = true;
        }
        assertTrue("An exception should have been thrown because the variable was not set.",exceptionThrown);
    }

    public void testSetAttributeCallSetMethodOnRequiredVariable(){
        DummyJameleonTagSupport3 dft = new DummyJameleonTagSupport3();
        fp = dft.fp;
        assertEquals("# of attributes",1,fp.getAttributes().size());
        dft.setAttribute("requiredNum","23");
        try{
            dft.broker.transferAttributes(dft.getContext());
            dft.broker.validate(dft.getContext());
        }catch (MissingAttributeException mae){
            fail("An exception should not have been thrown because the variable was set. "+mae);
        }
    }

    public void testSetAttributeNonExistentAttribute(){
        try{
            ft.setAttribute("__non_existent_var___", "foo");
        }catch (NullPointerException npe){
            fail("an unexpected NullPointerException was thrown for trying to set a variable that doesn't have an attribute instance.");
        }
        assertEquals("Size of a list of unsupport attributes",1, ft.getUnsupportedAttributes().size());
        assertEquals("Attribute name","__non_existent_var___",(String)ft.getUnsupportedAttributes().get(0));
    }

    public void testSetAttributeNullFunctionalPoint(){
        ft.fp = null;
        try{
            ft.setAttribute("__non_existent_var___", "foo");
        }catch (NullPointerException npe){
            fail("an unexpected NullPointerException was thrown for trying to set a variable that doesn't have an attribute instance.");
        }
        assertEquals("Size of a list of unsupport attributes",1, ft.getUnsupportedAttributes().size());
        assertEquals("Attribute name","__non_existent_var___",(String)ft.getUnsupportedAttributes().get(0));
    }

    public void testUnsupportedAttributeCaught(){
        ft.setAttribute("someUnsupportedVariable","blah");
        assertEquals("Size of unsupported attributes",1,ft.getUnsupportedAttributes().size());
        Exception e = null;
        try{
            ft.testForUnsupportedAttributesCaught();
        }catch(JameleonScriptException iae){
            e = iae;
        }
        assertNotNull("An JameleonScriptException should have been thrown", e);
        assertTrue("'someUnsupportedVariable' should be a variable in the list",e.getMessage().indexOf("someUnsupportedVariable") > -1);
    }

    public void testSetAttributeFromScriptViaContext(){
        DummyJameleonTagSupport4 dft = new DummyJameleonTagSupport4();
        String dString = "dmmy_string";
        dft.setAttribute(dString,"value");
        assertNull("dmmy_string", dft.fp.getAttribute("myString").getValue());
        dft.broker.transferAttributes(dft.getContext());
        fp = dft.fp;
        Attribute attr = (Attribute)fp.getAttributes().get("myString");
        assertTrue("instance variable", attr.isInstanceVariable());
        assertEquals("instance variable value","value", (String)attr.getValue());
        try{
            ft.broker.validate(ft.getContext());
        }catch(MissingAttributeException mae){
            fail("this variable was set in the context and should have been mapped");
        }
    }

    public void testSetAttributeFromScriptViaInstanceName(){
        DummyJameleonTagSupport4 dft = new DummyJameleonTagSupport4();
        String dString = "myString";
        dft.setAttribute(dString,"value");
        assertEquals("myString", "value", dft.fp.getAttribute("myString").getValue());
        dft.broker.transferAttributes(dft.getContext());
        fp = dft.fp;
        Attribute attr = (Attribute)fp.getAttributes().get("myString");
        assertTrue("instance variable", attr.isInstanceVariable());
        assertEquals("instance variable value","value", (String)attr.getValue());
        try{
            ft.broker.validate(ft.getContext());
        }catch(MissingAttributeException mae){
            fail("this variable was set directly and should have been mapped");
        }
    }

    public void testCleanVariablesInContext() {
        DummyJameleonTagSupport4 dft = new DummyJameleonTagSupport4();
        String dString = "dmmy_string";
        dft.setAttribute(dString,"value");
        assertEquals("Value set in context", "value", (String)dft.getContext().getVariable(dString));
        dft.cleanVariablesInContext();
        assertEquals("Value removed from context", null, dft.getContext().getVariable(dString));
    }

    public void testResetFunctionalPoint(){
        DummyJameleonTagSupport4 dft = new DummyJameleonTagSupport4();
        Attribute attr = dft.fp.getAttribute("myString");
        String dString = "dmmy_string";
        dft.setAttribute(dString,"value");
        dft.broker.transferAttributes(dft.getContext());
        assertEquals("myString value", "value", attr.getValue().toString());
        assertEquals("Value set in context", "value", (String)dft.getContext().getVariable(dString));
        assertEquals("Value set in instance", "value", dft.myString);
        dft.resetFunctionalPoint();
        assertNull("Value set in context", dft.getContext().getVariable(dString));
        assertNull("Value set in instance ", dft.myString);
    }

}


