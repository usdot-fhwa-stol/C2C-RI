/*
    Jameleon - An automation testing tool..
    Copyright (C) 2005 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.event;

public class TestTestCaseListener implements TestCaseListener {

    public boolean beginTestCaseCalled, endTestCaseCalled;
    public TestCaseEvent beginTestCaseEvent, endTestCaseEvent;
    public int numBeginTestCaseCalled, numEndTestCaseCalled;

    public void beginTestCase(TestCaseEvent event) {
        beginTestCaseCalled = true;
        beginTestCaseEvent = event;
        numBeginTestCaseCalled++;
    }

    public void endTestCase(TestCaseEvent event) {
        endTestCaseCalled = true;
        endTestCaseEvent = event;
        numEndTestCaseCalled++;
    }

    public void reset(){
        beginTestCaseCalled = false;
        beginTestCaseEvent = null;
        endTestCaseCalled = false;
        endTestCaseEvent = null;
        numBeginTestCaseCalled = 0;
        numEndTestCaseCalled = 0;
    }
}

