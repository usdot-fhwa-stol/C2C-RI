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

import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.jameleon.exception.JameleonScriptException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class XMLHelper {

    protected SAXReader parser;
    protected Document document;

    /**
     * Contstructor
     * @param xml - The XML to parse
     */
    public XMLHelper(String xml){
        setUpSaxReader();
        try{
            document = parser.read(new StringReader(xml));
        }catch(DocumentException de){
            throw new JameleonScriptException(cleanNestedMessage(de), de);
        }
    }

    /**
     * Contstructor
     * @param xmlFile - The XML to parse
     */
    public XMLHelper(URL xmlFile){
        setUpSaxReader();
        try{
            document = parser.read(xmlFile);
        }catch(DocumentException de){
            throw new JameleonScriptException(cleanNestedMessage(de), de);
        }
    }

    /**
     * Set up the SAXReader such that it understands the jameleon namespace
     */
    protected void setUpSaxReader(){
        parser = new SAXReader();
        Map uris = new HashMap();
        uris.put("jm", "jelly:jameleon");
        DocumentFactory.getInstance().setXPathNamespaceURIs(uris);
    }

    /**
     * Gets the text value of the given tag
     * @param xpath - the xpath representing a tag
     * @return The text representing the tag
     */
    public String getValueFromXPath(String xpath){
        String value = null;
        Node node = document.selectSingleNode(xpath);
        if (node != null) {
            value = node.getText();
        }
        if ("null".equals(value)) {
            value = null;
        }
        return value;
    }

    /**
     * Gets the boolean value of the given tag
     * @param xpath - the xpath representing a tag
     * @return a boolean value of the given tag
     */
    public boolean getBooleanValueFromXPath(String xpath){
        String value = document.selectSingleNode( xpath ).getText();
        boolean bValue = false;
        if ("true".equals(value) || "yes".equals(value)) {
            bValue = true;
        }
        return bValue;
    }

    /**
     * Gets the Document for the XML represented by the provided text
     * @return The Document for the XML represented by the provided text
     */
    public Document getDocument(){
        return document;
    }

    /**
     * Gets a list of Values from the xml
     * @param xpath - the xpath representing the desired tags
     * @return a List of nodes matching the xpath
     */
    public List getValuesFromXPath(String xpath){
        List nodes = getListFromXPath(xpath);
        List values = new LinkedList();
        if (nodes != null) {
            Iterator it = nodes.iterator();
            Node node = null;
            while (it.hasNext()) {
                node = (Node)it.next();
                values.add(node.getText());
            }
        }
        return values;
    }

    /**
     * Gets a list of Nodes from the xml
     * @param xpath - the xpath representing the desired tags
     * @return a List of nodes matching the xpath
     */
    public List getListFromXPath(String xpath){
        return document.selectNodes(xpath);
    }

    protected String cleanNestedMessage(Throwable t){
        String msg = t.getMessage();
        if (msg != null) {
            msg = msg.replaceAll(" Nested exception:.*", "");
            msg = msg.replace(": ", ":\n");
        }
        return msg;
    }

}