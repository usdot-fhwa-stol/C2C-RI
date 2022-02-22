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
package net.sf.jameleon.bean;

import net.sf.jameleon.function.MockFunctionTag;

/**
 * Tests Javadoc2Bean.
 * Some more text
 * @jameleon.function name="javadoc2bean-dummy1" type="action"
 * @jameleon.function name="javadoc2bean-dummy1-2" type="action"
 * @author Christian Hargraves
 * @jameleon.step Do something
 * @jameleon.step 안녕하세요 [Korean Characters]
 * @author Christian Hargraves
 */
public class Dummy1 extends MockFunctionTag{
    /**
     * Some variable that does something
     * @jameleon.attribute required="true" default="cool" contextName="j2bDummy1Var1"
     */
    private String testVar1;

    //Gets rid of PMD error since this variable isn't accessed directly in the unit tests
    //but it is accessed indirectly by getting the variable from the attribute broker
    public String getTestVar1(){
    	return testVar1;
    }
    
}
