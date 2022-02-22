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

import net.sf.jameleon.util.XMLHelper;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SessionTest extends TestCase {

    private Session session = null;
    private static final String APPLICATION = "app";
    private static final String ORGANIZATION = "org";
    private static final String TAG_NAME = "fp-test"; 
    private static final String AUTHOR = "Christian"; 
    private static final String DESC = "Does nothing"; 
    private static final String VALID_TYPE = "action"; 
    private static final String APPLICATION1 = "app1"; 
    private static final String APPLICATION2 = "app2"; 
    private static final String STEP1 = "do something"; 
    private static final String STEP2 = "do something else";
    private static final String ATTR_NAME1 = "Attr name 1"; 
    private static final String ATTR_NAME2 = "Attr name 2"; 

    public SessionTest( String name ) {
        super( name );
    }

    //JUnit Methods
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( SessionTest.class );
    }

    public void setUp() {
        session = new Session();
    }

    public void tearDown(){
        session = null;
    }

    //End JUnit Methods
    public void testConstructorTwoArgs(){
        session = new Session(APPLICATION, ORGANIZATION);
        assertEquals("application", APPLICATION, session.getApplication());
        assertEquals("organization", ORGANIZATION, session.getOrganization());
    }

    public void testAddFunctionalPoint(){
        FunctionalPoint fp1 = createFunctionalPoint();
        fp1.setFunctionId("function 1");
        FunctionalPoint fp2 = createFunctionalPoint();
        fp2.setFunctionId("function 2");
        assertEquals("# of functional points before adding any", 0, session.getFunctionalPoints().size());
        session.addFunctionalPoint(fp1);
        assertEquals("# of functional points after adding 1", 1, session.getFunctionalPoints().size());
        fp1 = (FunctionalPoint)session.getFunctionalPoints().get(0);
        assertEquals("functionId of first fp", "function 1", fp1.getFunctionId());
        session.addFunctionalPoint(fp2);
        assertEquals("# of functional points after adding 2", 2, session.getFunctionalPoints().size());
        fp2 = (FunctionalPoint)session.getFunctionalPoints().get(1);
        assertEquals("functionId of second fp", "function 2", fp2.getFunctionId());
    }

    public void testToXml() {
        session.setApplication(APPLICATION);
        session.setOrganization(ORGANIZATION);
        session.addFunctionalPoint(createFunctionalPoint());
        String xml = session.toXML();
        XMLHelper xmlHelper = new XMLHelper(xml);
        assertEquals("application", APPLICATION, xmlHelper.getValueFromXPath("/session/application"));
        assertEquals("organization", ORGANIZATION, xmlHelper.getValueFromXPath("/session/organization"));
        assertEquals("functional point tagName", TAG_NAME, xmlHelper.getValueFromXPath("/session/functional-point-info/tag-name"));
        assertEquals("functional point author", AUTHOR, xmlHelper.getValueFromXPath("/session/functional-point-info/author"));
    }

    private FunctionalPoint createFunctionalPoint(){
        FunctionalPoint fp = new FunctionalPoint();
        fp.setAuthor(AUTHOR);
        fp.addTagName(TAG_NAME);
        fp.setDescription(DESC);
        fp.setType(VALID_TYPE);
        fp.addTagName("another-" + TAG_NAME);
        fp.addApplication(APPLICATION1);
        fp.addApplication(APPLICATION2);
        fp.addStep(STEP1);
        fp.addStep(STEP2);
        Attribute attr1 = new Attribute();
        attr1.setName(ATTR_NAME1);
        Attribute attr2 = new Attribute();
        attr2.setName(ATTR_NAME2);
        fp.addAttribute(attr1);
        fp.addAttribute(attr2);
        return fp;
    }
}
