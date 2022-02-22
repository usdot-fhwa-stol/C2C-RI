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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon;

import net.sf.jameleon.event.TestSuiteEventHandler;
import net.sf.jameleon.event.TestSuiteListener;
import net.sf.jameleon.exception.JameleonScriptException;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

/**
 * This is currently a tag meant to group several test case scripts 
 * together into a test suite.
 * An example use might be:
 * <pre>
 *  &lt;test-suite name="some test suite name"&gt;
 *    &lt;precondition&gt;
 *      &lt;test-script script="scripts/setup.xml"/&gt;
 *    &lt;/precondition&gt;
 *    &lt;test-script script="scripts/foo/0002.xml"/&gt;
 *    &lt;test-script script="scripts/foo/0003.xml"/&gt;
 *  &lt;/test-suite&gt;
 * </pre>
 * NOTE: There is currently no support for a postcondition tag yet.
 * @jameleon.function name="test-suite"
 */
public class TestSuiteTag extends JameleonTagSupport {


    /**
     * cache this for later removal
     */
    private TestSuiteListener listener;
    /**
     * The executor to use to execute all test scripts
     */
    protected ExecuteTestCase executor;
    /**
     * A fully qualified class name that implements TestSuiteListener.
     * @jameleon.attribute
     */
    protected String testSuiteListener;
    /**
     * The name of the test suite.
     * @jameleon.attribute
     */
    protected String name;

    public void init() throws MissingAttributeException{
        testForUnsupportedAttributesCaught();
        broker.transferAttributes(context);
        broker.validate(context);
    }

    public void registerTestSuiteListener(String testSuiteListener){
        if (testSuiteListener != null) {
            TestSuiteEventHandler tsHandler = TestSuiteEventHandler.getInstance();
            Class c;
            try{
                c = Thread.currentThread().getContextClassLoader().loadClass(testSuiteListener);
                listener = (TestSuiteListener)c.newInstance();
                tsHandler.addTestSuiteListener(listener);
            }catch(Exception e){
                throw new JameleonScriptException("Could not register '"+testSuiteListener+"' due to the following reason", e, this);
            }
        }
    }

    public ExecuteTestCase getExecuteTestCase(){
        return executor;
    }

    /**
     * This method executes the tags inside the test-suite tag.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        init();
        registerTestSuiteListener(testSuiteListener);
        TestSuiteEventHandler eventHandler = TestSuiteEventHandler.getInstance();
        eventHandler.beginTestSuite(this);
        executor = new ExecuteTestCase();
//        executor.registerEventListeners();
        try{
            invokeBody(out);
        }finally{
            //executor.deregisterEventListeners();
            eventHandler.endTestSuite(this);
            eventHandler.removeTestSuiteListener(listener);
        }
    }

}
