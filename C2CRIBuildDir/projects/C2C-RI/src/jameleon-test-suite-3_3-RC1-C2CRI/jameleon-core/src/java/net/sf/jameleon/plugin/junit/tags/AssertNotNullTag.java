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
 * Performs an assertNotNull on a given variable. This tag, along with all JUnit tags
 * can be used inside any other plug-in's session tag
 * 
 * An example use might be:
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon" xmlns:j="jelly:core"&gt;
 *     &lt;j:set-var var="someVar" value="some value"/&gt;
 *     &lt;ju-session application="someApp"&gt;
 *       &lt;ju-assert-not-null
 *           functionId="Check that var1 equals var2."
 *           value="${someVar}"/&gt;
 *     &lt;/ju-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 *
 * @jameleon.function name="ju-assert-not-null" type="action"
 * @jameleon.step Validate the provided variable is not null
 */
public class AssertNotNullTag extends AbstractAssertTag{

    /**
     * The value to be checked for null
     * @jameleon.attribute
     */
    protected Object value;

    public void testBlock(){
        if (msg != null) {
            assertNotNull(msg, value);
        }else{
            assertNotNull(value);
        }
    }

}
