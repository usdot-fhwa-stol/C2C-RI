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

import net.sf.jameleon.exception.JameleonScriptException;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

/**
 * An abstract class used to define some test case attributes
 */
public abstract class AbstractTestCaseDocAttribute extends LocationAwareTagSupport {
    
    protected TestCaseTag tct;

    public AbstractTestCaseDocAttribute(){
        super();
    }

    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        setup();
        String text = (String)getBodyText();
        setTestCaseValue(text);
    }

    protected abstract void setTestCaseValue(String text);
    
    public void setup(){
        Object obj =  getParent();
        if (obj != null && obj instanceof TestCaseTag){
            tct = (TestCaseTag)obj;
        }else{
            throw new JameleonScriptException(getElementName() + " can only exist under the TestCase tag ( testcase )!", this);
        }
    }
}
