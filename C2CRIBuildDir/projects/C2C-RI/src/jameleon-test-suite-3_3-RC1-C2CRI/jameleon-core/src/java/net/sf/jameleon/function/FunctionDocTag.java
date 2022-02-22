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
package net.sf.jameleon.function;

import java.io.File;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/**
 * Documents a single step of a test case.
 * This tag can be used to document manual test cases. 
 * Also, sometimes it is too difficult to fit everything 
 * in the functionId attribute of a tag because there is more 
 * than one thing happening.
 * 
 * @jameleon.function name="function-doc"
 */
public class FunctionDocTag extends FunctionTag {


    public void testBlock(XMLOutput out) throws MissingAttributeException{
        //Do nothing since this functional point is used for documentation only.
    }

    public void doTag(XMLOutput out) throws MissingAttributeException{
        setupManualFunction();
        super.doTag(out);
    }

    public void store(File f, int event){
        //Nothing to do here
    }

    protected void setupManualFunction(){
        String tmpFunctionId = null;
        try{
            tmpFunctionId = (String)getBodyText();
        }catch (JellyTagException jte){
            //Do nothing. I guess this means there is no text in the body.
        }
        if (tmpFunctionId != null && tmpFunctionId.length() > 0) {
            setAttribute("functionId",tmpFunctionId);
        }
    }

}
