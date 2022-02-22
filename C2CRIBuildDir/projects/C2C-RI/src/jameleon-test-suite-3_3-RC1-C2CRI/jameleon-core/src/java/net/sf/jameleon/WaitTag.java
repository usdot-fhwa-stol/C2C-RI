/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon;

import net.sf.jameleon.function.FunctionTag;

/**
 * Wait for the given number of milliseconds.
 * This tag may be used anywhere inside the testcase tag.
 * @jameleon.function name="wait"
 */
public class WaitTag extends FunctionTag {

    /**
     * The number of milliseconds to wait for
     * @jameleon.attribute required="true"
     */
    protected long delayTime;

    //Remove functionId as a required attribute
    public void setup(){
        getFunctionalPoint().getAttribute("functionId").setRequired(false);    
    }

    public void testBlock(){
        delay(delayTime); 
    }

}
