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

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Am implementation of @{link DataDriver} for SQL.
 */
public class SqlDataDriver implements DataDriver {
    protected String url;
    protected String jdbcDriver;
    protected String username;
    protected String password;
    protected String query;

    protected Connection conn;
    protected List keys;
    protected Statement stmt;
    protected ResultSet rs;

    /**
     * Default construtor. After calling this constructor,
     * The Following will Need to be set.
     * {@link #setJDBCDriver(java.lang.String)}
     * {@link #setJDBCUrl(java.lang.String)}
     */
    public SqlDataDriver(){
        keys = new ArrayList();
    }

    /**
     * Sets up all required fields.
     * @param url - the jdbc url used to connect to the database
     * @param jdbcDriver - the jdbc driver used to connect to the database
     * @param username - the username used to connect to the database
     * @param password - the password used to connect to the database
     */
    public SqlDataDriver(String url, String jdbcDriver, String username, String password){
        this();
        this.url = url;
        this.jdbcDriver = jdbcDriver;
        this.username = username;
        this.password = password;
    }

    /**
     * Closes the handle to the data source
     */
    public void close() {
        try {
            if (rs != null ) rs.close();
            rs = null;
            if (stmt != null ) stmt.close();
            stmt = null;
        } catch (SQLException sqle) {
            //So what? no resultSet or Statement was ever created.
        }
        finally {
            try {
                if ( conn != null ) conn.close();
                conn = null;
            } catch (SQLException sqle) {
                //So what? The connection must already be closed
            }
        }
        keys.clear();
    }

    /**
     * Opens the handle to the data source
     * @throws IOException when the data source can not be found.
     */
    public void open() throws IOException {
        validate();
        registerJDBCDriver();
        try{
            conn = DriverManager.getConnection(url, username, password);
        }catch (SQLException sqle){
            throw new IOException("Could not create connection to the database located at '"+url+"'!");
        }
    }

    protected void validate() {
        if (url == null || url.trim().length() == 0) {
            throw new NullPointerException("'jdbcUrl' is a required field!");
        }
        if (jdbcDriver == null || jdbcDriver.trim().length() == 0) {
            throw new NullPointerException("'jdbcDriver' is a required field!");
        }
        if (username == null || username.trim().length() == 0) {
            throw new NullPointerException("'jdbcUsername' is a required field!");
        }
    }

    protected void registerJDBCDriver() throws IOException {
        try{
            Class.forName(jdbcDriver).newInstance();
        }catch(Exception cnfe){
            throw new IOException("could not register database driver. Class '" + jdbcDriver +"' not found");
        }
    }

    protected void readRecord() throws SQLException{
        if (rs == null) {
            try {
                executeQuery ();
            } catch ( SQLException sqle ) {
                throw new SQLException ("Unable to Execute Query - " + sqle );
            }

            // set the Keys array if it has not already been set. 
            if (keys == null || keys.size() == 0) {
                setKeys();
            }
        }
    }


    /**
     * Executes a query against the database once a valid connection has been established.
     * @throws SQLException if there is no query defined. 
     */
    public void executeQuery () throws SQLException {
        if ( query == null ) {
            throw new SQLException ("No Query is defined to be executed.");
        }
        try {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            rs = stmt.executeQuery(query);
        } catch ( SQLException sqle ) {
            throw new SQLException ("Failed to execute SQL statement. Possibly due to invalid SQL. -- " + sqle);
        }
    }
    
    /**
     * Gets MetaData from the result set and builds an ArrayList of the Column headers
     */
    protected void setKeys () {
        String colName;
        try {
            // Build the Keys ArrayList
            for (int x = 1; x <= rs.getMetaData().getColumnCount(); x++) {
                colName = rs.getMetaData().getColumnName(x);
                keys.add ( colName );
            }
        } catch ( SQLException sqle ) {
            System.out.println("Unable to get ResultSet MetaData - " + sqle );
        } 

    }


    /**
     * Gets the next row from the data source
     * @return a key-value HashMap representing the data row or null if
     * no data row is available  
     */                                                                    
    public Map getNextRow() { 
        Map vars = null;

        try {
            readRecord();
            if ( rs.next() ) { 
                vars = new HashMap();
                String key, value;
                for (int x = 1; x <= keys.size(); x++ ) {
                    key = (String)keys.get(x-1);
                    value = null;
                    try{
                        int columnType = rs.getMetaData().getColumnType(x);
                        
                        if (columnType == Types.TIMESTAMP || 
                            columnType == Types.DATE) {
                            Timestamp ts = rs.getTimestamp(x);
                            if (ts != null) {
                                value = ts.toString();
                            }
                        }else{
                            value = rs.getString(x);
                        }
                    }catch (SQLException sqle){
                        System.err.println ( "Unable to get Next Row " + sqle );
                        sqle.printStackTrace();
                    }
                    vars.put( key, value );
                }
            }
        } catch ( SQLException sqle ) {
            System.err.println ( "Unable to get Next Row " + sqle );
        }
        return vars;
    }

    /**
     * Tells whether the data source has another row
     * @return true if the data source still has more rows
     */
    public boolean hasMoreRows() {
        boolean moreRows = false;
        try {
            readRecord();
            //isAfterLast passes the condition when there are no rows returned.
            moreRows = !(rs.isAfterLast() || rs.isLast());
        } catch ( SQLException sqle ) {
            System.err.println("Unable to process hasMoreRows()");
            sqle.printStackTrace();
        }
        
        return moreRows;
    }

    /**
     * Sets the JDBC Driver.
     */
    public void setJDBCDriver (String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    /**
     * Sets the JDBC url.
     */
    public void setJDBCUrl (String url){
        this.url = url;
    }

    /**
     * Sets the JDBC username.
     */
    public void setJDBCUsername (String username) {
        this.username = username;
    }

    /**
     * Sets the JDBC password.
     */
    public void setJDBCPassword (String password) {
        this.password = password;
    }

    /**
     * Sets the SQL Query.
     */
    public void setQuery (String query) {
        this.query = query;
    }


}
