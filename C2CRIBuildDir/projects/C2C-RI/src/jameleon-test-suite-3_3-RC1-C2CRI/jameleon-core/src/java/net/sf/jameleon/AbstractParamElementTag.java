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
package net.sf.jameleon;

import net.sf.jameleon.util.JameleonUtility;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * Abstract class that represents children of a ParamTag.
 */
public abstract class AbstractParamElementTag extends TagSupport {
    
    protected String text = null;
    protected ParamTag parentTag = null;
    protected boolean decodeXMLToText;
    protected String fromVariable = null;

    /**
     * An implementation of the <code>doTag</code> method provided by the <code>TagSupport</code> class.
     * Maps the value in the <code>fromVariable</code> over to the original variable name, <code>toVariable</code>.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        validate();
        if (fromVariable != null) {
            text = (String) context.getVariable(fromVariable);
        }else{
            text = (String)getBodyText();
        }
        if (decodeXMLToText) {
            text = JameleonUtility.decodeXMLToText(text);
        }
        parentTag = (ParamTag)findAncestorWithClass(ParamTag.class);
    }

    /**
     * Used to validate everything is set up correctly.
     * @throws MissingAttributeException - When a required attribute isn't set.
     * @throws JellyTagException - When this tag is used out of context.
     */
    protected void validate()throws MissingAttributeException, JellyTagException{
        validateParentTag();
        validateParam();
    }

    protected void validateParentTag() throws JellyTagException{
        Object obj = findAncestorWithClass(ParamTag.class);
        if ( ! (obj instanceof ParamTag) ) {
            throw new JellyTagException("This tag can only be imbedded in a param tag.");
        }
    }

    protected void validateParam() throws JellyTagException{
        if (fromVariable != null && getBodyText() != null && getBodyText().length() > 0) {
            throw new JellyTagException("This tag can not support both a fromValue and a body");
        }
    }

    public void setDecodeXMLToText(boolean decodeXMLToText){
        this.decodeXMLToText = decodeXMLToText;
    }    

    public void setFromVariable(String fromVariable){
        this.fromVariable = fromVariable;
    }

}
