/*
    Jameleon - An automation testing tool..
    Copyright (C) 2007 Christian W. Hargraves (engrean@hotmail.com)

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
package net.sf.jameleon.reporting;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TestCaseCounterTest extends TestCase {

    private TestCaseCounter counter;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(TestCaseCounterTest.class);
    }

    public TestCaseCounterTest(String name) {
        super(name);
    }

    public void setUp(){
        counter = new TestCaseCounter();
    }

    public void testIncrementTestCaseNum(){
        assertEquals("initial test case #", 1, counter.getTestCaseNum());
        counter.incrementTestCaseNum(5);
        assertEquals("test case #", 6, counter.getTestCaseNum());
    }

    public void testIncrementPassed(){
        assertEquals("initial # passed", 0, counter.getNumPassed());
        counter.incrementPassed(5);
        assertEquals("# passed", 5, counter.getNumPassed());
    }

    public void testIncrementFailed(){
        assertEquals("initial # failed", 0, counter.getNumFailed());
        counter.incrementFailed(5);
        assertEquals("# failed", 5, counter.getNumFailed());
    }

    
}
