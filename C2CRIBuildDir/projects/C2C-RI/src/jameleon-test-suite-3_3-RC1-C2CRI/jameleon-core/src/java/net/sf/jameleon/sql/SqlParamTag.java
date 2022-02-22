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
package net.sf.jameleon.sql;

import net.sf.jameleon.ParamTag;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;

/**
 * Used to set binding parameter to the parent sql-update tag.
 * 
 * An example might be:
 * <pre><source>
 * &lt;sql-update 
 *   functionId="Insert a row into test table via a prepared statement with multiple param values"
 *     sqlUpdateSql="insert into test(test_str, test_str2) values(?,?)"/&gt;
 *       &lt;sql-param&gt;
 *         &lt;sql-param-value&gt;text2&gt;/sql-param-value&gt;
 *         &lt;sql-param-value&gt;text3&gt;/sql-param-value&gt;
 *       &lt;/sql-param&gt;
 * &lt;/sql-update&gt;
 * </source></pre>
 * 
 * @jameleon.function name="sql-param"
 */
public class SqlParamTag extends ParamTag {
               
    /**
     * Used to validate everything is set up correctly.
     * @throws MissingAttributeException - When a required attribute isn't set.
     * @throws JellyTagException - When this tag is used out of context.
     */
    protected void validate()throws MissingAttributeException, JellyTagException{
        if (values == null || values.size() < 1){
            throw new MissingAttributeException("sql-param-value is a required tag!");
        }
    }

}
