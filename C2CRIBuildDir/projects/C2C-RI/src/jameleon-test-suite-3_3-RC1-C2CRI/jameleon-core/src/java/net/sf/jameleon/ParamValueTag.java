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

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Gives the parameter a value.
 * For example:
 * <pre>
 *      &lt;param&gt;
 *          &lt;param-name&gt;someName&lt;/param-name&gt;
 *          &lt;param-value&gt;foo&lt;/param-value&gt;
 *      &lt;/param&gt;
 * </pre>
 * @jameleon.function name="param-value"
 */
public class ParamValueTag extends AbstractParamElementTag {
    
    /**
     * An implementation of the <code>doTag</code> method provided by the <code>TagSupport</code> class.
     * Maps the value in the <code>fromVariable</code> over to the original variable name, <code>toVariable</code>.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        super.doTag(out);
        parentTag.addParamValue(text);
	parentTag.setFromVariable(fromVariable);
    }

}
