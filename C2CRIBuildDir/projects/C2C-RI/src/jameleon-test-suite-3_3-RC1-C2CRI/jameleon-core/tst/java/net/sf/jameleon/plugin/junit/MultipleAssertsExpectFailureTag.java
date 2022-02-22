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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.plugin.junit;


/**
 * throws an exception
 * @jameleon.function name="multi-asserts-expect-failure" type="action" 
 */
public class MultipleAssertsExpectFailureTag extends JUnitFunctionTag {

    /**
     * @jameleon.attribute default="true"
     */
    protected boolean assert1;
    /**
     * @jameleon.attribute default="true"
     */
    protected boolean assert2;
    /**
     * @jameleon.attribute default="true"
     */
    protected boolean assert3;
    /**
     * @jameleon.attribute default="assert1Msg"
     */
    protected String assert1Msg;
    /**
     * @jameleon.attribute default="assert2Msg"
     */
    protected String assert2Msg;
    /**
     * @jameleon.attribute default="assert1Msg"
     */
    protected String assert3Msg;

    public void testBlock(){
        assertTrue(assert1Msg, assert1);
        assertTrue(assert2Msg, assert2);
        assertTrue(assert3Msg, assert3);
    }

}

