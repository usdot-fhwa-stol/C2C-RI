package net.sf.jameleon.function;

import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.util.JameleonUtility;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.MissingAttributeException;

import java.io.File;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class AttributeBrokerTest extends TestCase {
    protected JellyContext context;
    protected AttributeBroker broker;
    protected AttributeConsumer consumer;
    protected static String[] types = {
        "primLongAttr","primShortAttr","primCharAttr",
        "primBooleanAttr","primByteAttr","primDoubleAttr",
        "primFloatAttr"};

    class AttributeConsumer implements Attributable {
        protected String stringAttr;
        protected Boolean booleanAttr;
        protected List listAttr;
        protected File fileAttr;
        protected Double doubleAttr;
        protected final String cantSetMeImFinal = new String("Hi");
        public int numDescribeAttributesCalls;
        protected boolean primBooleanAttr;
        protected int primIntAttr;
        protected long primLongAttr;
        protected double primDoubleAttr;
        protected float primFloatAttr;
        protected short primShortAttr;
        protected byte primByteAttr;
        protected char primCharAttr;

        public void describeAttributes(AttributeBroker broker) {
            numDescribeAttributesCalls++;
        }

    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(AttributeBrokerTest.class);
    }

    public AttributeBrokerTest(String name) {
        super(name);
    }

    public void setUp() {
        context = new JellyContext();
        consumer = new AttributeConsumer();
        broker = new AttributeBroker(consumer);
    }

    public void testGetAttributeValueFromInstance(){
        //Test Object values
        consumer.stringAttr = "Another Value";
        Attribute attr = createAttribute("stringAttr", "" , false, "Some Value");
        Object actual = broker.getAttributeValueFromInstance(attr);
        assertNotNull("stringAttr's value should not be null", actual);
        assertEquals("stringAttr", "Another Value", (String)actual);
        //Test primitive non-zero value
        consumer.primIntAttr = 2;
        attr = createAttribute("primIntAttr", "" , false, null);
        actual = broker.getAttributeValueFromInstance(attr);
        assertNotNull("primIntAttr's value should not be null", actual);
        assertEquals("primIntAttr", 2, ((Integer)actual).intValue());
        //Test primitive (double) non-zero value
        consumer.primFloatAttr = 2.0f;
        attr = createAttribute("primFloatAttr", "" , false, null);
        actual = broker.getAttributeValueFromInstance(attr);
        assertNotNull("primFloatAttr's value should not be null", actual);
        assertEquals("primFloatAttr", Float.valueOf(2.0f), ((Float)actual));
        //Test primitive very large value
        consumer.primIntAttr = Integer.MAX_VALUE;
        attr = createAttribute("primIntAttr", "" , false, null);
        actual = broker.getAttributeValueFromInstance(attr);
        assertNotNull("primIntAttr's value should not be null", actual);
        assertEquals("primIntAttr", Integer.MAX_VALUE, ((Integer)actual).intValue());
        //Test primitive int zero value
        consumer.primIntAttr = 0;
        attr = createAttribute("primIntAttr", "" , false, null);
        actual = broker.getAttributeValueFromInstance(attr);
        assertNull("primIntAttr's value should not be null", actual);
        //Test primitive byte zero value
        consumer.primByteAttr = 0;
        attr = createAttribute("primByteAttr", "" , false, null);
        actual = broker.getAttributeValueFromInstance(attr);
        assertNull("primByteAttr's value should not be null", actual);
        //Test primitive long zero value
        consumer.primLongAttr = 0;
        attr = createAttribute("primLongAttr", "" , false, null);
        actual = broker.getAttributeValueFromInstance(attr);
        assertNull("primLongAttr's value should not be null", actual);
        //Test primitive char zero value
        consumer.primCharAttr = 0;
        attr = createAttribute("primCharAttr", "" , false, null);
        actual = broker.getAttributeValueFromInstance(attr);
        assertNull("primCharAttr's value should not be null", actual);
        //Test primitive boolean zero value
        consumer.primBooleanAttr = false;
        attr = createAttribute("primBooleanAttr", "" , false, null);
        actual = broker.getAttributeValueFromInstance(attr);
        assertNull("primBooleanAttr's value should not be null", actual);
    }
    
    public void testSetUpCallsDescribeAttributes() {
        consumer.numDescribeAttributesCalls = 0;
        broker.setUp();
        assertEquals(1, consumer.numDescribeAttributesCalls);
    }

    public void testRegisterAttribute() {
        broker.registerAttribute(createAttribute("stringAttr", "testStringAttribute"));
        assertEquals("Number of attributes", 1, broker.attributes.size());
    }

    public void testTransferStringAttribute() {
        broker.registerAttribute(createAttribute("stringAttr", "testStringAttribute"));
        broker.transferAttributes(context);
        assertNull(consumer.stringAttr);
        context.setVariable("testStringAttribute", "testString");
        broker.transferAttributes(context);
        assertEquals("testString", consumer.stringAttr);
    }

    public void testTransferBooleanAttribute() {
        broker.registerAttribute(createAttribute("booleanAttr", "functionTagTestBooleanAttribute"));
        context.setVariable("functionTagTestBooleanAttribute", "true");
        broker.transferAttributes(context);
        assertEquals(Boolean.TRUE, consumer.booleanAttr);
    }

    public void testTransferListAttribute() {
        broker.registerAttribute(createAttribute("listAttr", "functionTagTestListAttribute"));
        LinkedList list = new LinkedList();
        list.add("Hello");
        list.add("World");
        context.setVariable("functionTagTestListAttribute", list);
        broker.transferAttributes(context);
        assertEquals(2, consumer.listAttr.size());
        assertEquals("Hello", consumer.listAttr.get(0));
        assertEquals("World", consumer.listAttr.get(1));
    }

    public void testTransferPrimitiveIntAttributeNull() {
        broker.registerAttribute(createAttribute("primIntAttr", "functionTagTestIntAttribute"));
        context.setVariable("functionTagTestNoAttribute", "1");
        broker.transferAttributes(context);
        assertEquals(0, consumer.primIntAttr);
    }

    public void testTransferPrimitiveIntAttribute() {
        broker.registerAttribute(createAttribute("primIntAttr", "functionTagTestIntAttribute"));
        context.setVariable("functionTagTestIntAttribute", "1");
        broker.transferAttributes(context);
        assertEquals(1, consumer.primIntAttr);
    }

    public void testTransferPrimitiveBooleanAttributeNull() {
        broker.registerAttribute(createAttribute("primBooleanAttr", "functionTagTestBoolAttribute"));
        context.setVariable("functionTagTestNoAttribute", "true");
        broker.transferAttributes(context);
        assertEquals(false, consumer.primBooleanAttr);
    }

    public void testTransferPrimitiveBooleanAttribute() {
        broker.registerAttribute(createAttribute("primBooleanAttr", "functionTagTestBoolAttribute"));
        context.setVariable("functionTagTestBoolAttribute", "true");
        broker.transferAttributes(context);
        assertEquals(true, consumer.primBooleanAttr);
    }

    public void testTransferPrimitiveByteAttributeNull() {
        broker.registerAttribute(createAttribute("primByteAttr", "functionTagTestByteAttribute"));
        context.setVariable("functionTagTestNoAttribute", "1");
        broker.transferAttributes(context);
        assertEquals(0, consumer.primByteAttr);
    }

    public void testTransferPrimitiveByteAttribute() {
        broker.registerAttribute(createAttribute("primByteAttr", "functionTagTestByteAttribute"));
        context.setVariable("functionTagTestByteAttribute", "1");
        broker.transferAttributes(context);
        assertEquals(1, consumer.primByteAttr);
    }

    public void testTransferPrimitiveCharAttributeNull() {
        broker.registerAttribute(createAttribute("primCharAttr", "functionTagTestCharAttribute"));
        context.setVariable("functionTagTestNoAttribute", "a");
        broker.transferAttributes(context);
        assertEquals(0, consumer.primCharAttr);
    }

    public void testTransferPrimitiveCharAttribute() {
        broker.registerAttribute(createAttribute("primCharAttr", "functionTagTestCharAttribute"));
        context.setVariable("functionTagTestCharAttribute", "a");
        broker.transferAttributes(context);
        assertEquals('a', consumer.primCharAttr);
    }

    public void testTransferFile() {
        broker.registerAttribute(createAttribute("fileAttr", "fileAttribute"));
        context.setVariable("fileAttribute", "file.txt");
        broker.transferAttributes(context);
        assertEquals("file.txt", consumer.fileAttr.getPath());
        broker.attributes.clear();
        broker.registerAttribute(createAttribute("fileAttr", "fileAttribute"));
        context.setVariable("fileAttribute", new File("file.txt"));
        broker.transferAttributes(context);
        assertEquals("file.txt", consumer.fileAttr.getPath());
    }

    public void testTransferDouble() {
        broker.registerAttribute(createAttribute("doubleAttr", "doubleAttribute"));
        context.setVariable("doubleAttribute", "20.33");
        broker.transferAttributes(context);
        assertEquals("20.33", ""+consumer.doubleAttr);
        broker.attributes.clear();
        broker.registerAttribute(createAttribute("doubleAttr", "fileAttribute"));
        context.setVariable("fileAttribute", new Double("21.33"));
        broker.transferAttributes(context);
        assertEquals("21.33", ""+consumer.doubleAttr);
    }

    public void testTransferNull() {
        consumer.stringAttr = "Hello World";
        broker.registerAttribute(createAttribute("stringAttr", "functionTagTestStringAttribute"));
        broker.transferAttributes(context);
        assertEquals("Hello World",consumer.stringAttr);
        consumer.primIntAttr = 10;
        broker.registerAttribute(createAttribute("primIntAttr", "functionTagTestStringAttribute"));
        broker.transferAttributes(context);
        assertEquals(10,consumer.primIntAttr);
    }

    public void testValidateOptionalAttribute() {
        broker.registerAttribute(createAttribute("stringAttr", "functionTagTestStringAttribute", AttributeBroker.OPTIONAL));
        MissingAttributeException exception = null;
        try {
            broker.transferAttributes(context);
            broker.validate(context);
        } catch (MissingAttributeException e) {
            exception = e;
        }
        assertNull("The attribute is not required, so the exception should not have been thrown.", exception);
    }

    public void testValidateRequiredContextAttribute() {
        MissingAttributeException exception = null;
        broker.registerAttribute(createAttribute("stringAttr", "functionTagTestStringAttribute", AttributeBroker.REQUIRED));
        try {
            broker.transferAttributes(context);
            broker.validate(context);
        } catch (MissingAttributeException e) {
            exception = e;
        }
        assertNotNull("Required attribute is missing, so an exception should have been thrown.", exception);
        assertTrue("Exception should say something about attribute functionTagTestStringAttribute", 
                   exception.getMessage().indexOf("functionTagTestStringAttribute") > -1);
    }

    public void testValidateRequiredInstanceAttribute() {
        MissingAttributeException exception = null;
        broker.registerAttribute(createAttribute("stringAttr", "", AttributeBroker.REQUIRED));
        try {
            broker.transferAttributes(context);
            broker.validate(context);
        } catch (MissingAttributeException e) {
            exception = e;
        }
        assertNotNull("Required attribute is missing, so an exception should have been thrown.", exception);
        assertTrue("Exception should say something about attribute stringAttr", 
                   exception.getMessage().indexOf("stringAttr") > -1);
    }

    public void testNoSuchAttribute() {
        RuntimeException exception = null;
        broker.registerAttribute(createAttribute("noSuchAttr", "functionTagTestNonexistentAttribute"));
        try {
            broker.transferAttributes(context);
        } catch (RuntimeException e) {
            exception = e;
        }
        assertNotNull("Exception should have been thrown", exception);
        assertTrue("Exception should say something about attribute does not exist", 
                   exception.getMessage().indexOf("Instance variable noSuchAttr does not exist") > -1);
    }

    public void testSetToInstanceVariableOnlyWithDefault() {
        Attribute attr = createAttribute("stringAttr", "" , true, "Some Value");
        attr.setInstanceVariable(true);
        broker.registerAttribute(attr);
        broker.transferAttributes(context);
        assertEquals("Consumer stringAttr", "Some Value", consumer.stringAttr);
        try {
            broker.validate(context);
        } catch (MissingAttributeException mae) {
            fail("No exception should have been thrown because a default value is specified");
        }
        broker.setConsumerAttribute(attr, "another value");
        assertEquals("Consumer stringAttr before transferAttributes", "another value", consumer.stringAttr);
        broker.transferAttributes(context);

//        assertEquals("Consumer stringAttr", "another value", consumer.stringAttr);
    }

    public void testNotSettableAttribute() {
        String javaVersion = System.getProperty("java.version");
        if (javaVersion != null && !javaVersion.startsWith("1.5.") && !javaVersion.startsWith("1.6.")) {
            RuntimeException exception = null;
            broker.registerAttribute(createAttribute("cantSetMeImFinal", "functionTagTestNonexistentAttribute"));
            context.setVariable("functionTagTestNonexistentAttribute","some value");
            try {
                broker.transferAttributes(context);
            } catch (RuntimeException e) {
                exception = e;
            }
            assertNotNull("Exception should have been thrown", exception);
            assertTrue("Exception should say something about attribute is not settable", 
                       exception.getMessage().indexOf("Instance variable cantSetMeImFinal is not settable") > -1);
        }else{
            System.out.println("Skipping testNotSettableAttribute due to bug in JDK 1.5 and 1.6");
        }
    }

    public void testGetValueFromContext() {
        assertEquals("default", (String)broker.getValueFromContext(context, "key", "default"));
        context.setVariable("key", "value");
        assertEquals("value", (String)broker.getValueFromContext(context, "key", "default"));
        assertEquals("null contextName & emptyDefault", "", broker.getValueFromContext(context,null,""));
        assertNull("null contextName", broker.getValueFromContext(context,null,null));
    }

    public void testDefaultObject() {
        Attribute attr = createAttribute("stringAttr", "functionTagTestStringAttribute", true, "default string value");
        broker.registerAttribute(attr);
        assertFalse("Value should not be set", attr.isValueSet());
        broker.transferAttributes(context);
        attr.setValue(null);
        assertEquals("default string value",consumer.stringAttr);
        context.setVariable("functionTagTestStringAttribute", "this is not the default value");
        broker.transferAttributes(context);
        assertEquals("this is not the default value",consumer.stringAttr);
    }

    public void testDefaultPrimitiveNotRequired() {
        Attribute attr = createAttribute("primIntAttr", "functionTagTestPrimIntAttribute", false, "0");
        broker.registerAttribute(attr);
        broker.transferAttributes(context);
        assertEquals(0,consumer.primIntAttr);
        attr.setValue(null);
        context.setVariable("functionTagTestPrimIntAttribute", "2");
        broker.transferAttributes(context);
        assertEquals(2,consumer.primIntAttr);
    }

    public void testDefaultPrimitiveRequired() {
        Attribute attr = createAttribute("primIntAttr", "functionTagTestPrimIntAttribute", true, "0");
        attr.setContextName(null);
        broker.registerAttribute(attr);
        broker.transferAttributes(context);
        try {
            broker.validate(context);
        } catch (MissingAttributeException mae) {
            fail("No exception should have been thrown because a default value is specified");
        }
        assertEquals(0,consumer.primIntAttr);
        attr.setValue(null);
        attr.setContextName("functionTagTestPrimIntAttribute");
        context.setVariable("functionTagTestPrimIntAttribute", "2");
        broker.transferAttributes(context);
        assertEquals(2,consumer.primIntAttr);
    }

    public void testDefaultPrimitiveRequiredNoDefault() {
        Attribute attr = createAttribute("primIntAttr", "functionTagTestPrimIntAttribute", true);
        context.setVariable("functionTagTestPrimIntAttribute", "0");
        broker.registerAttribute(attr);
        broker.transferAttributes(context);
        try {
            broker.validate(context);
        } catch (MissingAttributeException mae) {
            fail("No exception should have been thrown because a value was set");
        }
        assertEquals(0,consumer.primIntAttr);
    }

    public void testDefaultPrimitiveRequiredSetDirectly() {
        Attribute attr = createAttribute("primIntAttr", "", true);
        broker.registerAttribute(attr);
        consumer.primIntAttr = 2;
        broker.transferAttributes(context);
        try {
            broker.validate(context);
        } catch (MissingAttributeException mae) {
            fail("No exception should have been thrown because a value was set");
        }
    }

    public void testTransferAttributesContextOnly(){
        context.setVariable("stringAttr","Some value");
        Attribute attr = createAttribute("stringAttr", "", true);
        attr.setInstanceVariable(false);
        broker.registerAttribute(attr);
        broker.transferAttributes(context);
        try {
            broker.validate(context);
        } catch (MissingAttributeException mae) {
            fail("The property was set, but is reported as not being set.");
        }
    }

    public void testGetAttributeValue(){
        Attribute attr = createAttribute("stringAttr", "",false);
        assertNull("stringAttr should be null", broker.getAttributeValue(attr, context));
        attr.setDefaultValue("Some value");
        assertNotNull("stringAttr should not be null", broker.getAttributeValue(attr,context));
        assertEquals("stringAttr", "Some value", (String)broker.getAttributeValue(attr,context));

        doAttrTest("primIntAttr");
        attr.setDefaultValue("4");
        assertNotNull("primIntAttr should not be null", broker.getAttributeValue(attr,context));
        for (int i = 0; i < types.length; i++) {
            doAttrTest(types[i]);
        }
    }

    public void testGetConsumerField()throws Exception{
        Attribute attr = createAttribute("primIntAttr", "7");
        Field f = broker.getConsumerField(attr);
        consumer.primIntAttr = 10;
        assertNotNull("primIntAttr", f);
        assertEquals(f.getType().toString(), "int");
        assertEquals(10, f.getInt(consumer));

        attr = createAttribute("stringAttr", "value");
        f = broker.getConsumerField(attr);
        consumer.stringAttr = "value";
        assertNotNull("stringAttr", f);
        assertEquals(f.getType().toString(), "class java.lang.String");
        assertEquals("value", f.get(consumer));

        attr = createAttribute("nonExistentVariable", "value");
        f = broker.getConsumerField(attr);
        assertNull("stringAttr", f);
    }

    public void testSetConsumerAttributeAsPrimitive(){
        Attribute attr = createAttribute("primBooleanAttr", "true");
        Field f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "true");
        assertTrue("primBooleanAttr as 'true'", consumer.primBooleanAttr);
        
        attr = createAttribute("primBooleanAttr", "false");
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "false");
        assertFalse("primBooleanAttr as 'false'", consumer.primBooleanAttr);
        
        attr = createAttribute("primByteAttr", "2");
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "2");
        assertEquals("primByteAttr as '2'", 2, consumer.primByteAttr);

        attr = createAttribute("primCharAttr", "a");
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "a");
        assertEquals("primCharAttr as 'a'", 'a', consumer.primCharAttr);
        broker.setConsumerAttributeAsPrimitive(f, null);
        assertEquals("primCharAttr as 'null'", 0, consumer.primCharAttr);
        
        attr = createAttribute("primDoubleAttr", "7.7");
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "7.7");
        assertEquals("primDoubleAttr as '7.7'", 7.7, consumer.primDoubleAttr, 0.0);
        
        attr = createAttribute("primFloatAttr", "7.7");
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "7.7");
        assertEquals("primFloatAttr as '7.7'", 7.7f, consumer.primFloatAttr, 0.0f);
        
        attr = createAttribute("primIntAttr", "7");
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "7");
        assertEquals("primIntAttr as '7'", 7, consumer.primIntAttr);

        attr = createAttribute("primLongAttr", "7");
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "7");
        assertEquals("primLongAttr as '7'", 7, consumer.primLongAttr);

        attr = createAttribute("primShortAttr", "7");
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsPrimitive(f, "7");
        assertEquals("primShortAttr as '7'", 7, consumer.primShortAttr);
    }

    public void testSetConsumerAttributeAsObject(){
        String value = "value";
        Attribute attr = createAttribute("stringAttr", value);
        Field f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertEquals(value, consumer.stringAttr);
        
        value = null;
        attr = createAttribute("listAttr", null);
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertNull(consumer.listAttr);

        value = "value";
        attr = createAttribute("listAttr", value);
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertEquals(value, (String)consumer.listAttr.get(0));

        value = JameleonUtility.fixFileSeparators("some/path");
        attr = createAttribute("fileAttr", value);
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertEquals(value, (String)consumer.fileAttr.getPath());

        value = null;
        attr = createAttribute("fileAttr", value);
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertNull(consumer.fileAttr);

        value = "true";
        attr = createAttribute("booleanAttr", value);
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertEquals(value, consumer.booleanAttr.toString());

        value = null;
        attr = createAttribute("booleanAttr", value);
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertNull(consumer.booleanAttr);

        value = "7.7";
        attr = createAttribute("doubleAttr", value);
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertEquals(value, consumer.doubleAttr.toString());

        value = null;
        attr = createAttribute("doubleAttr", value);
        f = broker.getConsumerField(attr);
        broker.setConsumerAttributeAsObject(f, value);
        assertNull(consumer.doubleAttr);

    }


    protected void doAttrTest(String name){
        Attribute attr = createAttribute(name, "",false);
        assertNull(name+" should be null ", broker.getAttributeValue(attr, context));
    }

    //Helper methods
    protected Attribute createAttribute(String name, String contextName) {
        Attribute attr = new Attribute();
        attr.setName(name);  //change to ContextName?
        attr.setContextName(contextName);
        attr.setInstanceVariable(true);
        return attr;
    }

    protected Attribute createAttribute(String name, String contextName, boolean required) {
        Attribute attr = createAttribute(name, contextName);
        attr.setRequired(required);
        return attr;
    }

    protected Attribute createAttribute(String name, String contextName, boolean required, String defaultValue) {
        Attribute attr = createAttribute(name, contextName, required);
        attr.setDefaultValue(defaultValue);
        return attr;
    }


}


