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
package net.sf.jameleon.plugin.junit;

/**
 * Used for testing data-driven attributes.
 * @jameleon.function name="data-driven-attributes" type="validation" 
 * @jameleon.step Request attributes for this tag.
 * @jameleon.step Verify the Jameleon framework has set the attributes as requested.
 * 
 */
public class BrokeredAttributesTag extends JUnitFunctionTag {
    /**
     * @jameleon.attribute required="true" contextName="brokeredAttributesStringAttr"
     **/
    protected String myString;
    /**
     * @jameleon.attribute required="true" contextName="brokeredAttributesBooleanAttr"
     **/
    protected boolean myBoolean;
    /**
     * @jameleon.attribute required="false" contextName="brokeredAttributesOptionalAttr"
     **/
    protected String optionalAttribute;
    /**
     * @jameleon.attribute required="false" contextName="brokeredAttributesDefaultAttr" default="this is the default value"
     **/
    protected String defaultAttribute1;
    /**
     * @jameleon.attribute required="false" contextName="brokeredAttributesDefaultAttr2" default="this is the default value"
     **/
    protected String defaultAttribute2;
    /**
     * @jameleon.attribute contextName="brokeredAttributesDefaultAttr3" default="default value3"
     **/
    protected String defaultAttribute3;
    /**
     * @jameleon.attribute
     **/
    protected String value3;
    /**
     * @jameleon.attribute default="default value4"
     **/
    protected String defaultAttribute4;
    /**
     * @jameleon.attribute
     **/
    protected String value4;

    public void testBlock() {
        assertEquals("test succeeded!", myString);
        assertEquals(true, myBoolean);
        assertEquals("this is the default value", defaultAttribute2);
        assertNull("optionalAttribute should be null!", optionalAttribute);
        assertEquals("no longer default", defaultAttribute1);
        assertEquals(value3, defaultAttribute3);
        assertEquals(value4, defaultAttribute4);
    }
}

