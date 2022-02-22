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

import net.sf.jameleon.TestCaseTag;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TestCaseEventHandler{

    private static TestCaseEventHandler eventHandler;
    private final List testCaseListeners = Collections.synchronizedList(new LinkedList());

    private TestCaseEventHandler(){}

    public static TestCaseEventHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new TestCaseEventHandler();
        }
        return eventHandler;
    }

    public void clearInstance(){
        eventHandler = null;
    }

    public void addTestCaseListener(TestCaseListener tcl){
        if (tcl != null && !testCaseListeners.contains(tcl)){
            testCaseListeners.add(tcl);
        }
    }

    public List getTestCaseListeners(){
        return testCaseListeners;
    }

    public void removeTestCaseListener(TestCaseListener tcl){
        testCaseListeners.remove(tcl);
    }

    public void beginTestCase(TestCaseTag tct){
        TestCaseEvent tce = new TestCaseEvent(tct);
        synchronized(testCaseListeners){
            Iterator it = testCaseListeners.iterator();
            TestCaseListener tcl;
            while (it.hasNext()) {
                tcl = (TestCaseListener)it.next();
                tcl.beginTestCase(tce);
            }
        }
    }

    public void endTestCase(TestCaseTag tct){
        TestCaseEvent tce = new TestCaseEvent(tct);
        synchronized(testCaseListeners){
            Iterator it = testCaseListeners.iterator();
            TestCaseListener tcl;
            while (it.hasNext()) {
                tcl = (TestCaseListener)it.next();
                tcl.endTestCase(tce);
            }
        }
    }

}
