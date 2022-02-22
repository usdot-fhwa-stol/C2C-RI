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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.HashSet;

public class AssertLevelTest extends TestCase {

    private AssertLevel al;
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( AssertLevelTest.class );
    }

    public AssertLevelTest( String name ) {
        super( name );
    }

    public void setUp(){
        al = AssertLevel.getInstance();
        al.setGreaterThanLevel(AssertLevel.NO_LEVEL);
        al.setLessThanLevel(AssertLevel.NO_LEVEL);
        al.setLevel(AssertLevel.NO_LEVEL);
        al.levels = new HashSet();
    }

    public void testAssertLevelGetInstance(){
        assertEquals(AssertLevel.NO_LEVEL,al.getGreaterThanLevel());
        assertEquals(AssertLevel.NO_LEVEL,al.getLessThanLevel());
        assertEquals(AssertLevel.NO_LEVEL,al.getLevel());
        assertEquals(0,al.getLevels().size());
    }

    public void testAssertLevelSetGreaterThanLevel(){
        al.setGreaterThanLevel(AssertLevel.LEVEL1);
        assertEquals(AssertLevel.LEVEL1,al.getGreaterThanLevel());
        AssertLevel al2 = AssertLevel.getInstance();
        assertEquals(AssertLevel.LEVEL1,al2.getGreaterThanLevel());
    }

    public void testAssertLevelSetLessThanLevel(){
        al.setLessThanLevel(AssertLevel.LEVEL2);
        assertEquals(AssertLevel.LEVEL2,al.getLessThanLevel());
        AssertLevel al2 = AssertLevel.getInstance();
        assertEquals(AssertLevel.LEVEL2,al2.getLessThanLevel());
    }

    public void testAssertLevelSetLevel(){
        al.setLevel(AssertLevel.FUNCTION);
        assertEquals(AssertLevel.FUNCTION,al.getLevel());
        AssertLevel al2 = AssertLevel.getInstance();
        assertEquals(AssertLevel.FUNCTION,al2.getLevel());
    }

    public void testAssertLevelSetLevels(){
        al.addLevel(AssertLevel.LEVEL4);
        assertEquals(1,al.getLevels().size());
        assertEquals(AssertLevel.LEVEL4,((Integer)al.getLevels().toArray()[0]).intValue());
        AssertLevel al2 = AssertLevel.getInstance();
        assertEquals(AssertLevel.LEVEL4,((Integer)al2.getLevels().toArray()[0]).intValue());
    }

    public void testAssertLevelAssertPerformableWithGreaterThanLevel(){
        al.setGreaterThanLevel(AssertLevel.LEVEL4);
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.REGRESSION));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL4));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL7));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL8));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.ACCEPTANCE));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.FUNCTION));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL2));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL1));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.SMOKE));
    }

    public void testAssertLevelAssertPerformableWithLessThanLevel(){
        al.setLessThanLevel(AssertLevel.LEVEL5);
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.SMOKE));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL1));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL2));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.FUNCTION));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL4));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL5));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.REGRESSION));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL7));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL8));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.ACCEPTANCE));
    }

    public void testAssertLevelAssertPerformableWithNoLevel(){
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.NO_LEVEL));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.SMOKE));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.FUNCTION));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL2));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL1));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL4));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.REGRESSION));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL7));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL8));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.ACCEPTANCE));
    }

    public void testAssertLevelAssertPerformableWithLevel(){
        al.setLevel(AssertLevel.LEVEL5);
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL5));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.SMOKE));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.FUNCTION));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL2));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL1));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL4));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.REGRESSION));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL7));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL8));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.ACCEPTANCE));
    }

    public void testAssertLevelAssertPerformableWithLevels1(){
        al.addLevel(AssertLevel.LEVEL5);
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL5));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.SMOKE));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.FUNCTION));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL2));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL1));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL4));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.REGRESSION));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL7));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL8));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.ACCEPTANCE));
    }

    public void testAssertLevelAssertPerformableWithLevels3(){
        al.addLevel(AssertLevel.LEVEL5);
        al.addLevel(AssertLevel.LEVEL1);
        al.addLevel(AssertLevel.LEVEL7);
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL5));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL1));
        assertTrue("Assert should be executed",al.assertPerformable(AssertLevel.LEVEL7));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.SMOKE));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL2));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.FUNCTION));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.REGRESSION));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.LEVEL8));
        assertFalse("Assert should not be executed",al.assertPerformable(AssertLevel.ACCEPTANCE));
    }

}



