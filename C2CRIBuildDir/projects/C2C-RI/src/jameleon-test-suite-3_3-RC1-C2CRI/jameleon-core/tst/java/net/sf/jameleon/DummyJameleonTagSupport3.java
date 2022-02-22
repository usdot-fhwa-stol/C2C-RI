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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Some dummy tag with docs
 * @jameleon.function name="mock-jameleon-tag-support3" type="action"
 * @jameleon.step step 1
 * @jameleon.step step 2
 **/
public class DummyJameleonTagSupport3 extends JameleonTagSupport {
    
    protected int requiredNum;

    /**
     * A Required #
     * @jameleon.attribute required="true"
     */
    public void setRequiredNum(int requiredNum){
        this.requiredNum = requiredNum;
    }

    public DummyJameleonTagSupport3(){
        super();
        context = new JellyContext();
    }

    public void doTag(XMLOutput out) throws MissingAttributeException{
    }
}
