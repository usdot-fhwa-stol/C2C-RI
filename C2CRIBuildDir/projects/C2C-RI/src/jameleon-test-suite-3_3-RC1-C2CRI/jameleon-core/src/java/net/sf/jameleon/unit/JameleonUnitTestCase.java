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
package net.sf.jameleon.unit;

import junit.framework.TestCase;
import net.sf.jameleon.event.TestCaseListener;
import net.sf.jameleon.event.TestCaseEvent;
import net.sf.jameleon.ExecuteTestCase;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.result.TestCaseResult;

import java.io.File;

/**
 * A class to help with unit testing scripts and tags.
 */
public abstract class JameleonUnitTestCase extends TestCase implements TestCaseListener {

    private TestCaseResult testCaseResult;
    private TestCaseTag testCaseTag;

    public JameleonUnitTestCase(String name){
        super(name);
    }

    /**
     * Implementation of the TestCaseListener interface. Currently does nothing.
     * @param event The TestCaseEvent that spawned this event. The source of the event is the TestCaseTag
     */
    public void beginTestCase(TestCaseEvent event){}

    /**
     * Implementation of the TestCaseListener interface. Gets the handle on the test case tag and the results.
     * @param event The TestCaseEvent that spawned this event. The source of the event is the TestCaseTag
     */
    public void endTestCase(TestCaseEvent event){
         testCaseTag = (TestCaseTag)event.getSource();
         testCaseResult = testCaseTag.getResults();
    }

    public TestCaseResult runScript(String script){
        return runScript(new File(script));
    }

    public TestCaseResult runScript(File script){
         ExecuteTestCase exec = new ExecuteTestCase();
         exec.registerEventListener(this);
         exec.executeJellyScript(script);
         return testCaseResult;
    }

    /**
     * Gets the test case result of the script that was run from the <code>runScript</code> method
     * @return the test case result of the script that was run from the <code>runScript</code> method
     */
    public TestCaseResult getTestCaseResult() {
        return testCaseResult;
    }

    /**
     * Gets the test case tag of the script that was run from the <code>runScript</code> method
     * @return the test case tag of the script that was run from the <code>runScript</code> method 
     */
    public TestCaseTag getTestCaseTag() {
        return testCaseTag;
    }
}
