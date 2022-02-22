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
package net.sf.jameleon.util;

import java.net.URL;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.dom4j.Document;
import org.dom4j.Node;

public class XMLHelperTest extends TestCase {
    private XMLHelper xmlHelper;

    private XMLHelper xmlHelperFile;
    private static final String xml = "<x><a id=\"a1\">one</a><a id=\"a2\">two</a>"+
                                      "<b id=\"b1\">true</b><b id=\"b2\">false</b>"+
                                      "<b id=\"b3\">yes</b><b id=\"b4\">no</b>"+
                                      "<b id=\"b5\">text value</b><c id=\"c1\">null</c></x>";

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( XMLHelperTest.class );
    }

    public XMLHelperTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception{
        xmlHelper = new XMLHelper(xml);
        xmlHelperFile = new XMLHelper(new URL("file:tst/res/testXml.xml"));
    }

    public void tearDown(){
        xmlHelper = null;
    }

    public void testCleanNestedMessage(){
        RuntimeException re = new RuntimeException("some error message. Nested exception: .lk");
        assertEquals("some error message.", xmlHelper.cleanNestedMessage(re));
        re = new RuntimeException("another error message w/a nested exception");
        assertEquals("another error message w/a nested exception", xmlHelper.cleanNestedMessage(re));
        re = new RuntimeException("another: error message: w/a nested exception");
        assertEquals("another:\nerror message:\nw/a nested exception", xmlHelper.cleanNestedMessage(re));
    }

    public void testSetUpSaxReader(){
        xmlHelper.parser = null;
        xmlHelper.setUpSaxReader();
        xmlHelperFile.setUpSaxReader();
        assertNotNull("SAXReader", xmlHelper.parser);
        assertNotNull("SAXReader", xmlHelperFile.parser);
    }

    public void testGetValueFromXpathWithNamespaces() throws Exception{
        xmlHelperFile = new XMLHelper(new URL("file:tst/res/testXmlWithNamespaces.xml"));
        assertEquals("<test-case-author>", "Christian", xmlHelperFile.getValueFromXPath("/jm:testcase/jm:test-case-author"));
    }

    public void testGetValueFromXPath(){
        String a1 = xmlHelper.getValueFromXPath("//a[@id='a1']");
        assertEquals("a1 value", "one", a1);
        assertEquals("a1 value", "one", xmlHelperFile.getValueFromXPath("//a[@id='a1']"));
        String c1 = xmlHelper.getValueFromXPath("//c[@id='c1']");
        assertNull("c1 value", c1);
        String none = xmlHelper.getValueFromXPath("//non-existent");
        assertNull("non-existent", none);
    }

    public void testGetBooleanValueFromXPath(){
        assertTrue("true text", xmlHelper.getBooleanValueFromXPath("//b[@id='b1']"));
        assertTrue("yes text", xmlHelper.getBooleanValueFromXPath("//b[@id='b3']"));
        assertFalse("false text", xmlHelper.getBooleanValueFromXPath("//b[@id='b2']"));
        assertFalse("no text", xmlHelper.getBooleanValueFromXPath("//b[@id='b4']"));
        assertFalse("any text value", xmlHelper.getBooleanValueFromXPath("//b[@id='b5']"));
    }

    public void testGetDocument(){
        Document d = xmlHelper.getDocument();
        assertNotNull("document", d);
        String a1 = d.selectSingleNode("//a[@id='a1']").getText();
        assertEquals("a1 value", "one", a1);
    }

    public void testGetListFromXPath(){
        List nodes = xmlHelper.getListFromXPath("//b");
        assertEquals("# of b nodes", 5, nodes.size());
        Node b = (Node)nodes.get(0);
        assertEquals("b1 text", "true", b.getText());
        b = (Node)nodes.get(4);
        assertEquals("b5 text", "text value", b.getText());
        nodes = xmlHelper.getListFromXPath("//some-non-existent-element");
        assertEquals("# of non-existent-element nodes", 0, nodes.size());
    }

    public void testGetValuesFromXPath(){
        List values = xmlHelper.getValuesFromXPath("//b");
        assertEquals("# of b nodes", 5, values.size());
        String b = (String)values.get(0);
        assertEquals("b1 text", "true", b);
        b = (String)values.get(4);
        assertEquals("b5 text", "text value", b);
        values = xmlHelper.getListFromXPath("//some-non-existent-element");
        assertEquals("# of non-existent-element nodes", 0, values.size());
    }
}
