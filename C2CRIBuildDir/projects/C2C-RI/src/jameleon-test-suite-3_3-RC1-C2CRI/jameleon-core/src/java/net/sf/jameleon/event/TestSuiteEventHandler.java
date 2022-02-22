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

import net.sf.jameleon.TestSuiteTag;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TestSuiteEventHandler{

    private static TestSuiteEventHandler eventHandler;
    private final List testSuiteListeners = Collections.synchronizedList(new LinkedList());;

    private TestSuiteEventHandler(){}

    public static TestSuiteEventHandler getInstance(){
        if (eventHandler == null) {
            eventHandler = new TestSuiteEventHandler();
        }
        return eventHandler;
    }

    public void clearInstance(){
        eventHandler = null;
    }

    public void addTestSuiteListener(TestSuiteListener tsl){
        if (tsl != null && !testSuiteListeners.contains(tsl)){
            testSuiteListeners.add(tsl);
        }
    }

    public List getTestSuiteListeners(){
        return testSuiteListeners;
    }

    public void removeTestSuiteListener(TestSuiteListener tsl){
        testSuiteListeners.remove(tsl);
    }

    public void beginTestSuite(TestSuiteTag tst){
        TestSuiteEvent tse = new TestSuiteEvent(tst);
        synchronized(testSuiteListeners){
            Iterator it = testSuiteListeners.iterator();
            TestSuiteListener tsl;
            while (it.hasNext()) {
                tsl = (TestSuiteListener)it.next();
                tsl.beginTestSuite(tse);
            }
        }
    }

    public void endTestSuite(TestSuiteTag tst){
        TestSuiteEvent tse = new TestSuiteEvent(tst);
        synchronized(testSuiteListeners){
            Iterator it = testSuiteListeners.iterator();
            TestSuiteListener tsl;
            while (it.hasNext()) {
                tsl = (TestSuiteListener)it.next();
                tsl.endTestSuite(tse);
            }
        }
    }

}
