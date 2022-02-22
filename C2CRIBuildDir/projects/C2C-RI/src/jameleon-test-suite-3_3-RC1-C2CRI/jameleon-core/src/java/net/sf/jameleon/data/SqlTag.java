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

import org.apache.log4j.Logger;
/**
 * Used to iterate over all tags it is surrounding one time per row in a SQL ResultSet.
 * This tag should work with any RDBMS that provides a JDBC driver.
 * One thing to remember is each JDBC driver seems to act a bit differently when returning
 * the column names returned. Some JDBC drivers will returned the column names in all caps
 * some will return them as undercase, while others may return them as defined when the table
 * was created.
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *   &lt;sql query="SELECT ID, col1 as NAME, col2 as GENDER FROM TEST"
 *        jdbcUsername="sa"
 *        jdbcPassword=""
 *        jdbcUrl="jdbc:hsqldb:hsqldb_sample"
 *        jdbcDriver="org.hsqldb.jdbcDriver"
 *        countRow="true">
 *
 *     &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."
 *           id="${ID}"
 *           name="${NAME}"
 *           gender="${GENDER}"/&gt;
 *     &lt;/some-session&gt;
 *   &lt;/sql&gt;
 * &lt;/testcase&gt;
 * </source></pre>

 * @jameleon.function name="sql"
 */

public class SqlTag extends AbstractDataDrivableTag {
    
    protected SqlDataDriver sqld;
    protected String query;
    protected String id = "-";
    protected String jdbcDriver;
    protected String jdbcUrl;
    protected String jdbcUsername;
    protected String jdbcPassword;

    /**
     * Gets the logger used for this tag
     * @return the logger used for this tag.
     */
    protected Logger getLogger() {
        return Logger.getLogger(SqlTag.class.getName());
    }

    /**
     * Gets the DataDriver used for this tag.
     * @return the DataDriver used for this tag.
     */
    protected DataDriver getDataDriver() {
        if (sqld == null) {
            sqld = new SqlDataDriver();
        }
        return sqld;
    }

    /**
     * Gets an error message to be displayed when a error occurs due to the DataDriver.
     * @return an error message to be displayed when a error occurs due to the DataDriver.
     */
    protected String getDataExceptionMessage() {
        return "Trouble querying database with '"+getQuery()+"'";
    }

    /**
     * Sets up the DataDriver by calling any implementation-dependent
     * methods.
     */
    protected void setupDataDriver() {
        sqld.setJDBCDriver(jdbcDriver);
        sqld.setJDBCPassword(jdbcPassword);
        sqld.setJDBCUrl(jdbcUrl);
        sqld.setJDBCUsername(jdbcUsername);
        sqld.setQuery(getQuery());
    }

    /**
     * Gets the query to be used for this tag
     * @return the query used by this tag
     */
    public String getQuery(){
        return query;
    }

    /**
     * Gets the trace message when the execution is beginning and ending.
     * The message displayed will already start with BEGIN: or END:
     * @return the trace message when the execution is just beginning and ending.
     */
    protected String getTagTraceMsg() {
        return "executing sql " + getQuery();
    }

    /**
     * Describe the tag when error messages occur.
     * The most appropriate message might be the tag name itself.
     * @return A brief description of the tag or the tag name itself.
     */
    public String getTagDescription() {
        return "sql tag";
    }

    /**
     * Calculates the location of the state to be stored for any tags under this tag.
     * The result should be a relative path that simply has the row number in it along
     * with some unique indentifier for this tag like the handle name or something.
     * @return the location of the state to be stored for any tags under this tag minus the baseDir calculation stuff.
     */
    protected String getNewStateStoreLocation(int rowNum) {
        return "sql-"+id+"-"+rowNum;
    }

    /**
     * Set the JDBC Driver needed for the Database Connection
     * @jameleon.attribute required="true"
     */
    public void setJdbcDriver (String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    /**
     * Set the JDBC URL for the Database Connection
     * @jameleon.attribute required="true"
     */
    public void setJdbcUrl (String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * Set the JDBC UserName for the Database Connection
     * @jameleon.attribute required="true"
     */
    public void setJdbcUsername (String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }
   
    /**
     * Set the JDBC Password for the Database Connection
     * @jameleon.attribute required="false"
     */
    public void setJdbcPassword (String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    /**
     * Set the SQL Query for the Database Connection
     * @jameleon.attribute required="true"
     */
    public void setQuery (String query) {
        this.query = query;
    }

    /**
     * Set the SQL ID number
     * This is primarily used for debugging. 
     * It also provides a means to distinguish between nested <code>SqlTag</code> tags. 
     * @jameleon.attribute required="false"
     */
    public void setId (String id) {
        this.id = id;
    }

}
