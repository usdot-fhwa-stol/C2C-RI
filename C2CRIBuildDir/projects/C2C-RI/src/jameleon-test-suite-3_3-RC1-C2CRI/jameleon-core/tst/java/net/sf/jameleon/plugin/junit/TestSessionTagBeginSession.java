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

import net.sf.jameleon.MockSessionTag;

/**
 * Does a simple boolean test -- used for unit testing.
 * @jameleon.function name="junit-test-begin-session" type="validation" 
 */
public class TestSessionTagBeginSession extends JUnitFunctionTag {

    /**
     * @jameleon.attribute
     */
    private boolean shouldStart;
    /**
     * @jameleon.attribute
     */
    private boolean fail;

    public void testBlock(){
        MockSessionTag mst = (MockSessionTag)findAncestorWithClass(MockSessionTag.class);
        String msg;
        if (shouldStart) {
            msg = "should have started.";
        }else{
            msg = "should not have started.";
        }
        assertEquals(msg, shouldStart, mst.applicationStarted);
        assertFalse("you told me to fail", fail);
    }

}

