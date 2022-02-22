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
 * Tests that a value for variable <b>simpleFunctionVar2</b> isn't set
 * @jameleon.function name="junit-simple-function-2-vars" type="validation" 
 * @jameleon.step Test that the first value is set to true.
 * @jameleon.step Test that the second value is not set.
 */
public class SimpleFunctionWithTwoVars extends SimpleFunction {

    public void setSimpleFunctionVar2(String var2){
        setVariable("simpleFunctionVar2", var2);
    }

    public void testBlock(){
        assertNull("simpleFunctionVar2 should be null.", getVariable("simpleFunctionVar2"));
    }
}

