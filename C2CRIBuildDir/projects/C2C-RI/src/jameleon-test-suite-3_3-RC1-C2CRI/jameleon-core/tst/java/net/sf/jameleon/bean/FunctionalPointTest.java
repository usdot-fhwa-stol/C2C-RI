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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.bean;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.exception.JameleonException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionalPointTest extends TestCase{

    private FunctionalPoint fp = null;
    private FunctionalPoint fp2 = null;
    private static final String TAG_NAME = "fp-test"; 
    private static final String CLASS_NAME = "net.sf.jameleon.SomeTag"; 
    private static final String AUTHOR = "Christian"; 
    private static final String DESC = "Does nothing"; 
    private static final String VALID_TYPE = "action"; 
    private static final String INVALID_TYPE = "function"; 
    private static final String APPLICATION1 = "app1"; 
    private static final String APPLICATION2 = "app2"; 
    private static final String SHORT_DESC = "short desc"; 
    private static final String STEP1 = "do something"; 
    private static final String STEP2 = "do something else";
    private static final String ATTR_NAME1 = "Attr name 1"; 
    private static final String ATTR_NAME2 = "Attr name 2"; 
    
    public FunctionalPointTest( String name ) {
        super( name );
    }

    //JUnit Methods
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( FunctionalPointTest.class );
    }

    public void setUp(){
        fp2 = new FunctionalPoint();
        fp2.setAuthor(AUTHOR);
        fp2.addTagName(TAG_NAME);
        fp2.setDescription(DESC);
        fp2.setType(VALID_TYPE);
    }

    public void tearDown(){
        fp2 = null;
    }

    public void testClone() throws Exception{
    	List applications = new ArrayList();
    	applications.add("app1");
    	applications.add("app2");
    	
    	fp2.getApplications().addAll(applications);
    	fp2.setClassName("some.class.name");
    	fp2.setFunctionId("some function id");
    	fp2.setShortDescription("short description");
    	ArrayList steps = new ArrayList();
    	steps.add("step1");
    	steps.add("step2");
    	steps.add("step3");
    	fp2.setSteps(steps);
    	Map m = new HashMap();
    	Attribute attr1 = new Attribute();
    	attr1.setName("name1");
    	Attribute attr2 = new Attribute();
    	attr2.setName("name2");
    	m.put("att1", attr1);
    	m.put("att2", attr2);
    	fp2.setAttributes(m);
    	FunctionalPoint fpC = (FunctionalPoint) fp2.clone();
    	assertEquals("functional points", fp2.toXML(), fpC.toXML());
    }
    
    public void testSetGetClassName(){
        assertEquals("ClassName", "", fp2.getClassName());
        fp2.setClassName(CLASS_NAME);
        assertEquals("ClassName", CLASS_NAME, fp2.getClassName());
    }

    //End JUnit Methods
    public void testFunctionalPointConstructorDefault(){
        fp = new FunctionalPoint();
        assertNotNull("Author should be an empty non-null String!", fp.getAuthor());
        assertNotNull("Tag Name should be an empty non-null String!", fp.getDefaultTagName());
        assertNotNull("Description should be an empty non-null String!", fp.getDescription());
        assertNotNull("Short Description should be an empty non-null String!", fp.getShortDescription());
        assertEquals("Steps should be LinkList with no elements", 0, fp.getSteps().size());
        assertEquals("Attributes should be LinkList with no elements", 0, fp.getAttributes().size());
        assertNotNull("Type should be an empty non-null String!", fp.getType());
        assertNotNull("ClassName should be an empty non-null String!", fp.getClassName());
    }

    public void testSetShortDescription(){
        fp = new FunctionalPoint();
        fp.setShortDescription(SHORT_DESC);
        assertEquals("Short Description:", SHORT_DESC, fp.getShortDescription());
    }

    public void testFunctionalPointSetAuthor(){
        fp = new FunctionalPoint();
        fp.setAuthor(AUTHOR);
        assertEquals("Author:", AUTHOR, fp.getAuthor());
    }

    public void testFunctionalPointAddTagNames(){
        fp = new FunctionalPoint();
        assertEquals("Tag name should be an empty non-null string", "", fp.getDefaultTagName());
        fp.addTagName(TAG_NAME);
        assertEquals("Tag Name:", TAG_NAME, fp.getDefaultTagName());
        fp.addTagName("another-" + TAG_NAME);
        assertEquals("Number of tag names", 2, fp.getTagNames().size());
        assertEquals("Tag Name:", "another-" + TAG_NAME, (String)fp.getTagNames().get(1));
    }

    public void testFunctionalPointSetDescription(){
        fp = new FunctionalPoint();
        fp.setDescription(DESC);
        assertEquals("Description:", DESC, fp.getDescription());
    }

    public void testFunctionalPointSetTypeValid(){
        fp = new FunctionalPoint();
        fp.setType(VALID_TYPE);
        assertEquals("Type:", VALID_TYPE, fp.getType());
    }
    public void testFunctionalPointSetTypeInvalid(){
        fp = new FunctionalPoint();
        fp.setType(INVALID_TYPE);
        boolean exception = false;
        try{
            fp.getType();
        }catch(JameleonException e){
            exception = true;
        }
        assertTrue("An exception should have been thrown", exception);
    }

    public void testFunctionalPointAddStep(){
        fp = new FunctionalPoint();
        fp.addStep(STEP1);
        assertEquals("Steps Size:", 1, fp.getSteps().size());
        assertEquals("Steps:", STEP1, fp.getSteps().get(0));
    }

    public void testFunctionalPointSetSteps(){
        fp = new FunctionalPoint();
        ArrayList steps = new ArrayList();
        steps.add(STEP1);
        steps.add(STEP2);
        fp.setSteps(steps);
        assertEquals("Steps Size:", 2, fp.getSteps().size());
        assertEquals("Steps:", STEP1, fp.getSteps().get(0));
        assertEquals("Steps:", STEP2, fp.getSteps().get(1));
    }
    public void testFunctionalPointAddAttribute(){
        fp = new FunctionalPoint();
        Attribute attr = new Attribute();
        try{
            fp.addAttribute(attr);
            fail("An exception should have been thrown about adding an attribute with no name nor contextName");
        }catch(JameleonException iae){
            assertTrue(true);
        }
        attr.setName(ATTR_NAME1);
        fp.addAttribute(attr);
        assertEquals("Attributes Size:", 1, fp.getAttributes().size());
        assertEquals("Attribute Name:", ATTR_NAME1, ((Attribute)fp.getAttributes().get(ATTR_NAME1)).getName());
        attr.setContextName("contextName");
        fp.addAttribute(attr);
        assertEquals("Attributes Size:", 1, fp.getAttributes().size());
        assertEquals("Attribute Name:", ATTR_NAME1, ((Attribute)fp.getAttributes().get(ATTR_NAME1)).getName());
    }

    public void testEqualsTrue(){
        fp = new FunctionalPoint();
        fp.addTagName("foo");
        fp.addTagName("bar");
        FunctionalPoint fp2 = new FunctionalPoint();
        fp2.addTagName("bar");
        fp2.addTagName("foo");
        assertTrue("equals():", fp.equals(fp2));
    }

    public void testEqualsFalse(){
        fp = new FunctionalPoint();
        fp.addTagName("foo");
        fp.addTagName("bar");
        FunctionalPoint fp2 = new FunctionalPoint();
        fp2.addTagName("spoo");
        fp2.addTagName("bar");
        assertFalse("equals():", fp.equals(fp2));
    }

    public void testFunctionalPointSetAttributes(){
        fp = new FunctionalPoint();
        Map attrs = new HashMap();
        Attribute attr1 = new Attribute();
        attr1.setName(ATTR_NAME1);
        Attribute attr2 = new Attribute();
        attr2.setName(ATTR_NAME2);
        attrs.put(ATTR_NAME1, attr1);
        attrs.put(ATTR_NAME2, attr2);
        fp.setAttributes(attrs);
        assertEquals("Attributes Size:", 2, fp.getAttributes().size());
        assertEquals("Attribute Name:", ATTR_NAME1, ((Attribute)fp.getAttributes().get(ATTR_NAME1)).getName());
        assertEquals("Attribute Name:", ATTR_NAME2, ((Attribute)fp.getAttributes().get(ATTR_NAME2)).getName());
    }

    public void testToXML() {
        fp2.addTagName("another-" + TAG_NAME);
        fp2.addApplication(APPLICATION1);
        fp2.addApplication(APPLICATION2);
        fp2.addStep(STEP1);
        fp2.addStep(STEP2);
        Attribute attr1 = new Attribute();
        attr1.setName(ATTR_NAME1);
        Attribute attr2 = new Attribute();
        attr2.setName(ATTR_NAME2);
        fp2.addAttribute(attr1);
        fp2.addAttribute(attr2);
        String xml = fp2.toXML();
        assertContains("Couldn't find tag name", "<tag-name>" + TAG_NAME + "</tag-name>", xml);
        assertContains("Couldn't find tag name", "<tag-name>" + "another-" + TAG_NAME + "</tag-name>", xml);
        assertContains("Couldn't find application", "<application>" + "app1" + "</application>", xml);
        assertContains("Couldn't find application", "<application>" + "app2" + "</application>", xml);
        assertContains("Couldn't find type", "<type>" + VALID_TYPE + "</type>", xml);
        assertContains("Couldn't find author", "<author>" + AUTHOR + "</author>", xml);
        assertContains("Couldn't find description", "<description>" + DESC + "</description>", xml);
        assertContains("Couldn't find step", "<step>" + STEP1 + "</step>", xml);
        assertContains("Couldn't find step", "<step>" + STEP2 + "</step>", xml);
        assertContains("Couldn't find attribute", "<attribute-name>" + ATTR_NAME1 + "</attribute-name>", xml);
        assertContains("Couldn't find attribute", "<attribute-name>" + ATTR_NAME2 + "</attribute-name>", xml);
    }

    public void testGetAttribute(){
        Attribute attr = new Attribute();
        attr.setName("attr1");
        attr.setContextName("attrContext");
        fp2.addAttribute(attr);
        attr = fp2.getAttribute("attr1");
        assertEquals("Attribute name:","attr1", attr.getName());
        assertEquals("Attribute context name:","attrContext", attr.getContextName());
    }

    public void testAddApplication(){
        fp2.addApplication("app1");
        assertEquals("# of applications", 1, fp2.getApplications().size());
        assertEquals("1st application", "app1", fp2.getApplications().get(0));
        fp2.addApplication("app2");
        assertEquals("# of applications", 2, fp2.getApplications().size());
        assertEquals("1st application", "app2", fp2.getApplications().get(1));
        //Validate that the same application is not added twice.
        fp2.addApplication("app2");
        assertEquals("# of applications", 2, fp2.getApplications().size());
        assertEquals("1st application", "app2", fp2.getApplications().get(1));
    }

    //Helper methods
    protected void assertContains(String msg, String expectedPattern, String actual) {
        assertNotNull(msg + ": The string was null", actual);
        assertTrue(msg, actual.indexOf(expectedPattern) >= 0);
    }
    
}