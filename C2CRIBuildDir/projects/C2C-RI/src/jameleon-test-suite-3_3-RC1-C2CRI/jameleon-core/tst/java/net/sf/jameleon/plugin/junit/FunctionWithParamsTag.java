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

import net.sf.jameleon.ParamTag;
import net.sf.jameleon.plugin.junit.JUnitFunctionTag;

/**
 * Gets the nested params and validates that number of params matches the number of expected params
 * @jameleon.function name="ju-function-with-params" type="validation" 
 * @jameleon.step Test that n number of parameters were found.
 */
public class FunctionWithParamsTag extends JUnitFunctionTag {

    /**
     * @jameleon.attribute default="-1"
     */
    protected int numOfParamsExpected;
    /**
     * @jameleon.attribute default="-1"
     */
    protected int numOfParamValuesExpected;

    public void testBlock(){
        if (numOfParamsExpected > -1) {
            assertEquals("# of params expected", numOfParamsExpected, getParamLength());
        }
        if (numOfParamValuesExpected > -1 ) {
            ParamTag param = (ParamTag)getParam(0);
            assertEquals("# of param values expected", numOfParamValuesExpected, param.getParamValues().size());
        }
    }

}
