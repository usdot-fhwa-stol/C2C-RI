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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon;

import net.sf.jameleon.util.JameleonUtility;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * used only as a child of &lt;map-variable/&gt;, it allows the ability to map one or more
 * text values to a variable. The variableType of &lt;map-variable/&gt; can be used to when defining
 * multiple values.
 * 
 * For example:
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *
 *   &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."&gt;
 *           &lt;map-variable toVariable="resultsText" variableType="list"&gt;
 *               &lt;variable-value&gt;value 1&lt;/variable-value&gt;
 *               &lt;variable-value&gt;value 2&lt;/variable-value&gt;
 *               &lt;variable-value&gt;value 3&lt;/variable-value&gt;
 *               &lt;variable-value&gt;value 4&lt;/variable-value&gt;
 *           &lt;/map-variable/&gt;
 *       &lt;/some-tag-that-uses-context-variables&gt;
 *   &lt;/some-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 * 
 * @jameleon.function name="variable-value"
 */
public class VariableValueTag extends VariableTag {
    private boolean decodeXMLToText;

    public VariableValueTag(){
        super();
    }
    /**
     * An implementation of the <code>doTag</code> method provided by the <code>TagSupport</code> class.
     * Maps the value in the <code>fromVariable</code> over to the original variable name, <code>toVariable</code>.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        init();
        VariableMappingTag tag = (VariableMappingTag)getParent();
        toVariable = tag.getToVariable();
        variableType = tag.getVariableType();
        value = (String)getBodyText();

        if (decodeXMLToText) {
            value = JameleonUtility.decodeXMLToText(value);
        }
        super.doTag(out);
        tag.setChildExecuted(true);
    }

    /**
     * Used to validate everything is set up correctly.
     * @throws MissingAttributeException - When a required attribute isn't set.
     * @throws JellyTagException - When this tag is used out of context.
     */
    protected void validate()throws MissingAttributeException, JellyTagException{
        Tag tag = getParent();
        if ( ! (tag instanceof VariableMappingTag) ) {
            throw new JellyTagException("This tag can only be imbedded in a the map-variable tag.");
        }
        if (value == null ){
            throw new MissingAttributeException("value is required as an attribute or the body of the element!");
        }
        super.validate();
    }

    /**
     * @return The variable name to be mapped to the <code>fromVariable</code> or the originally supported variable name that has a set method for it
     * in the function point.
     */
    public String getValue(){
        return this.value;
    }

    /**
     * Sets the variable value.
     * @param value - the variable value.
     */
    public void setValue(String value){
        this.value = value;
    }

    public void setDecodeXMLToText(boolean decodeXMLToText){
        this.decodeXMLToText = decodeXMLToText;
    }    
}
