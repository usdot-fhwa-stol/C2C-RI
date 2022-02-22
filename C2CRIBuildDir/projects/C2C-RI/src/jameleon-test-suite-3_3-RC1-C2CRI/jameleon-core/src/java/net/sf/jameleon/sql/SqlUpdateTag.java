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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Runs a delete or update SQL statement against the database.
 * 
 * Sometimes putting an application into a required state means connecting 
 * to a database and executing a SQL statment. This can be done with this tag. 
 * The SQL can be an insert, delete or update command. The body of this element 
 * should contain the SQL statement used. See the function tag docs for information 
 * on other attributes supported and/or required by this tag.
 * 
 * An example might be:
 * <pre><source>
 * &lt;junit-session xmlns:j="jelly:core"&gt;
 *   &lt;sql-update 
 *       functionId="Insert another row into test table"
 *       jdbcDriver="org.hsqldb.jdbcDriver"
 *       jdbcUrl="jdbc:hsqldb:tst/_tmp/jameleon_test2"
 *       jdbcUsername="sa"
 *       jdbcPassword=""&gt;
 *       create cached table test2(
 *         test_str varchar,
 *         test_str2 varchar
 *      );
 *   &lt;/sql-update&gt;
 *   &lt;sql-update 
 *       functionId="Delete row from test table"
 *       jdbcDriver="org.hsqldb.jdbcDriver"
 *       jdbcUrl="jdbc:hsqldb:tst/_tmp/jameleon_test2"
 *       jdbcUsername="sa"
 *       jdbcPassword="">
 *       delete from test2 where test_str='some text';
 *       -- some SQL comment
 *       -- Another SQL comment
 *   &lt;/sql-update&gt;
 * &lt;/junit-session&gt;
 * </source></pre>
 * @jameleon.function name="sql-update" type="action"
 * @jameleon.step Connect to the database for the the correct environment
 * @jameleon.step execute the provided sql statement.
 */
public class SqlUpdateTag extends AbstractSqlTag {
    
    public SqlUpdateTag(){
        super();
        sqlVarName = "sqlUpdateSql";
    }

    protected void executeSql(Connection conn, String sql) throws SQLException{
        PreparedStatement stmt = null;
        try{
            stmt = conn.prepareStatement(sql);
            Iterator it = params.iterator();
            int count = 1;
            while (it.hasNext()) {
                ArrayList values = ((ParamTag)it.next()).getParamValues();
                Iterator valuesIt = values.iterator();
                while (valuesIt.hasNext()) {
                    stmt.setString(count++, (String)valuesIt.next());
                }
            }
            stmt.execute();
            conn.commit();
        }catch(SQLException sqle){
            conn.rollback();
            throw sqle;
        }finally{
            if (stmt != null) {
                stmt.close();
            }
        }
    }

    /**
     * @jameleon.attribute required="false"
     */
    public void setSqlUpdateSql(String sqlUpdateSql){
        setVariable(sqlVarName, sqlUpdateSql);
    }

}
