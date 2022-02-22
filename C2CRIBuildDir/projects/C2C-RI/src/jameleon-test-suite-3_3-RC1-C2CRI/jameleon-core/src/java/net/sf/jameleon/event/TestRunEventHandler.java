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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.event;

import java.util.*;

public class TestRunEventHandler{

    private static TestRunEventHandler eventHandler;

    private final List testRunListeners = Collections.synchronizedList(new LinkedList());

    private TestRunEventHandler(){}

    public static TestRunEventHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new TestRunEventHandler();
        }
        return eventHandler;
    }

    public void clearInstance(){
        eventHandler = null;
    }

    public void addTestRunListener(TestRunListener trl){
        if (trl != null && !testRunListeners.contains(trl)){
            testRunListeners.add(trl);
        }
    }

    public List getTestRunListeners(){
        return testRunListeners;
    }

    public void removeTestRunListener(TestRunListener trl){
       testRunListeners.remove(trl);
    }

    public void beginTestRun(Calendar startTime){
        TestRunEvent tre = new TestRunEvent(startTime);
        synchronized(testRunListeners){
            Iterator it = testRunListeners.iterator();
            TestRunListener trl;
            while (it.hasNext()) {
                trl = (TestRunListener)it.next();
                trl.beginTestRun(tre);
            }
        }
    }

    public void endTestRun(Calendar endTime){
        TestRunEvent tre = new TestRunEvent(endTime);
        synchronized(testRunListeners){
            Iterator it = testRunListeners.iterator();
            TestRunListener trl;
            while (it.hasNext()) {
                trl = (TestRunListener)it.next();
                trl.endTestRun(tre);
            }
        }
    }

}
