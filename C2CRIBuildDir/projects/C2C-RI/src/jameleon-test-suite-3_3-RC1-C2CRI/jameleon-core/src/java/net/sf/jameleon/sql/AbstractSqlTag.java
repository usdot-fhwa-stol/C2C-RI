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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.FunctionTag;
import net.sf.jameleon.util.JameleonUtility;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;

/**
 * An abstract class used to execute SQL.
 */
public abstract class AbstractSqlTag extends FunctionTag {

    protected String sqlVarName;
    /**
     * The jdbc driver
     * @jameleon.attribute required="true" contextName="jdbc.driver"
     */
    protected String jdbcDriver;
    /**
     * The jdbc url
     * @jameleon.attribute required="true" contextName="jdbc.url"
     */
    protected String jdbcUrl;
    /**
     * The jdbc username
     * @jameleon.attribute required="true" contextName="jdbc.username"
     */
    protected String jdbcUsername;
    /**
     * The jdbc password
     * @jameleon.attribute required="false" contextName="jdbc.password"
     */
    protected String jdbcPassword;

    public AbstractSqlTag(){
        super();
    }

    public void testBlock() {
        init();
        String sql = getSql();
        try (Connection conn = getConnection())
		{
            executeSql(conn, sql);
            assertTrue(true);
        }catch (SQLException sqle){
            JameleonScriptException jse = new JameleonScriptException("problem with: "+sql, sqle, this);
            getFunctionResults().setError(jse);
        }
        destroy();
    }

    protected void validate() throws MissingAttributeException {
        String sql = getSql();
        if (sql == null || sql.trim().length() == 0) {
            throw new MissingAttributeException(sqlVarName);
        }
    }

    protected abstract void executeSql(Connection conn, String sql) throws SQLException;

    /**
     * Gets a connection to the database desired
     * or a CSV file.
     * This properties file should define an entry for each of the following:
     *      <ol>
     *          <li>jdbc.driver -- The driver using to connect to the datasource.
     *          <li>jdbc.username -- The username used to connect to the datasource
     *          <li>jdbc.password -- the password used to connect the username to the datasource
     *          <li>jdbc.url -- The url where the database exists.
     *      </ol>
     */
    protected Connection getConnection() throws SQLException{
        Connection conn;

        try {
            Class.forName( jdbcDriver );
            conn = DriverManager.getConnection( jdbcUrl, jdbcUsername, jdbcPassword );
            conn.setAutoCommit( false );
        } catch ( SQLException sqle ) {
            String error = "Couldn't connect to database!!\n";
            throw new SQLException( error + sqle.toString() +" " );
        } catch ( ClassNotFoundException cnfe){
            String error = "Couldn't load class "+jdbcDriver+"!!\n";
            throw new SQLException( error + cnfe.toString());
        }
         return conn;
    }
    

    public String getSql(){
        String sql = null;
        if (isContextVariableNull(sqlVarName)) {
            try{
                sql = (String)getBodyText();
            }catch(JellyTagException jte){
                 throw new JameleonScriptException(jte.getMessage(), jte, this);
            }
        }else{
            sql = getVariableAsString(sqlVarName);
        }
        sql = JameleonUtility.decodeXMLToText(sql);
        return sql;
    }

    protected void init(){}
    protected void destroy(){}

}
