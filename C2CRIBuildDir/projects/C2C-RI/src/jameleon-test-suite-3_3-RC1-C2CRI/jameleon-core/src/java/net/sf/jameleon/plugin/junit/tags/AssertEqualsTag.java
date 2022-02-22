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

/**
 * Performs an assertEquals on two variables. This tag, along with all JUnit tags
 * can be used inside any other plug-in's session tag
 * 
 * To compare two variables with a default JUnit error message on a failure:
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *     &lt;ju-session application="someApp"&gt;
 *       &lt;ju-assert-equals
 *           functionId="Check that var1 equals var2."
 *           expected="${var1}"
 *           actual="${var2}"/&gt;
 *     &lt;/ju-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 *
 * To compare two variables that gives "First Name" at the beginning of the failure message:
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *     &lt;jiffie-session application="someApp"&gt;
 *       &lt;ju-assert-equals
 *           functionId="Check that var1 equals var2."
 *           expected="${var1}"
 *           actual="${var2}"
 *           msg="First Name"/&gt;
 *     &lt;/jiffie-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 *
 * @jameleon.function name="ju-assert-equals" type="action"
 * @jameleon.step Compare the expected value against the actual value.
 */
public class AssertEqualsTag extends AbstractAssertTag{

    /**
     * The expected value
     * @jameleon.attribute
     */
    protected Object expected;
    /**
     * The actual value
     * @jameleon.attribute
     */
    protected Object actual;
    /**
     * Forces the values to be compared as Strings by calling the toString() method.
     * @jameleon.attribute default="false"
     */
    protected boolean valuesAreStrings;

    public void testBlock(){
        if (valuesAreStrings || 
            (expected != null && actual != null && 
             !expected.getClass().isInstance(actual))) {
            if (msg != null) {
                assertEquals(msg, expected.toString(), actual.toString());
            }else{
                assertEquals(expected.toString(), actual.toString());
            }
        } else{
            if (msg != null) {
                assertEquals(msg, expected, actual);
            }else{
                assertEquals(expected, actual);
            }
        }
    }

}
