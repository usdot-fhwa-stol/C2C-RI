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
 * Does a simple boolean test -- used for unit testing.
 * @jameleon.function name="junit-simple-function" type="validation" 
 * @jameleon.step Test that the value sent is true.
 */
public class SimpleFunction extends JUnitFunctionTag {

    /**
     * @jameleon.attribute required="false" contextName="simpleFunctionTest"
     */
    protected boolean test;
    /**
     * @jameleon.attribute required="false" contextName="simpleFunctionThrowException"
     */
    protected boolean throwException;

    public void testBlock(){
        assertTrue("simpleFunction attribute 'test' should be set to true", test);
        if (throwException) {
            throw new RuntimeException("Expected Exception throw");
        }
    }

}
