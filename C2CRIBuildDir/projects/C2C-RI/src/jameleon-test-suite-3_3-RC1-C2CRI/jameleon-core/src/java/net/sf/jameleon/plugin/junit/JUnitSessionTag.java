/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.plugin.junit;

import net.sf.jameleon.SessionTag;

/**
 * A Session for the JUnit plug-in.
 * 
 * An example of its use might:
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

 * @jameleon.function name="ju-session"
 * @jameleon.function name="junit-session"
 */
public class JUnitSessionTag extends SessionTag {
    
    public JUnitSessionTag(){
        super();
    }

}
