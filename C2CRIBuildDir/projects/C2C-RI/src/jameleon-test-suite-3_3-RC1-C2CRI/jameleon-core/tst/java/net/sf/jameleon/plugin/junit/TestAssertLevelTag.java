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

import net.sf.jameleon.util.AssertLevel;
/**
 * Starts the application
 * @jameleon.function name="test-assert-levels" type="validation" 
 * @jameleon.step Test that asserts only occur at specified levels
 */
public class TestAssertLevelTag extends JUnitFunctionTag {

    /**
     * @jameleon.attribute required="false"
     */
    protected String var1;
    /**
     * @jameleon.attribute required="false"
     */
    protected String var2;
    /**
     * @jameleon.attribute required="false"
     */
    protected boolean doSmokeTest;
    /**
     * @jameleon.attribute required="false"
     */
    protected boolean doFunctionTest;
    /**
     * @jameleon.attribute required="false"
     */
    protected boolean doAcceptanceTest;
    /**
     * @jameleon.attribute required="false"
     */
    protected boolean doRegressionTest;

    public void testBlock(){
        if (doSmokeTest) {
            assertEquals("smoke test level", AssertLevel.SMOKE, AssertLevel.getInstance().getGreaterThanLevel());
        }
        if (doFunctionTest) {
            assertEquals("function test level", AssertLevel.FUNCTION, AssertLevel.getInstance().getGreaterThanLevel());
        }
        if (doAcceptanceTest) {
            assertEquals("acceptance test level", AssertLevel.ACCEPTANCE, AssertLevel.getInstance().getGreaterThanLevel());
        }
        if (doRegressionTest) {
            assertEquals("regression test level", AssertLevel.REGRESSION, AssertLevel.getInstance().getGreaterThanLevel());
        }
    }

}

