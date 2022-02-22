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
package net.sf.jameleon.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to test the getKeyMapping method
 * 
 * @jameleon.function name="sql-test"
 */

public class SqlTestTag extends SqlTag {

    public void setupDataDriver(){
        super.setupDataDriver();
        setAttribute("query", getQuery());
    }

    /**
     * @jameleon.attribute default="VAR1"
     */
    protected String var1ContextName;

    /**
     * @jameleon.attribute default="VAR2"
     */
    protected String var2ContextName;

    /**
     * Gets the query to be used for this tag
     * @return the query used by this tag
     */
    public String getQuery(){
        return "SELECT VAR1, VAR2 FROM DATA";
    }

    public Map getKeyMapping(){
        Map keyMap = new HashMap();
        keyMap.put("VAR1", var1ContextName);
        keyMap.put("VAR2", var2ContextName);
        return keyMap;
    }

}
