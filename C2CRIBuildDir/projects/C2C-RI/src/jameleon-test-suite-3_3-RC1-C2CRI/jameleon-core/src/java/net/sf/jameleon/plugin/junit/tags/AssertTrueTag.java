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
package net.sf.jameleon.plugin.junit.tags;

import org.apache.commons.jelly.expression.Expression;

/**
 * Performs an assertTrue on a given expression. This tag, along with all JUnit tags
 * can be used inside any other plug-in's session tag
 * 
 * The following is an example:
 * <pre><source>
 *     &lt;testcase xmlns="jelly:jameleon" xmlns:j="jelly:core"&gt;
 *         &lt;j:set var="greeting" value="Hello World!"/&gt;
 *         &lt;ju-session&gt;
 *             &lt;ju-assert-true
 *                 functionId="check that 'greeting' has at least one character in it"
 *                 test="${greeting.length() >= 1"/&gt;
 *         &lt;/ju-session&gt;
 *     &lt;/testcase&gt;
 * </source></pre>
 * @jameleon.function name="ju-assert-true" type="action"
 * @jameleon.step Verify that the expected expression validates to true.
 */
public class AssertTrueTag extends AbstractAssertTag{

    /**
     * The test to be executed
     * @jameleon.attribute required="true"
     */
    protected Expression test;

    public void testBlock(){
        if (msg != null) {
            assertTrue(msg, test.evaluateAsBoolean(context));
        }else{
            assertTrue(test.evaluateAsBoolean(context));
        }
    }

}
