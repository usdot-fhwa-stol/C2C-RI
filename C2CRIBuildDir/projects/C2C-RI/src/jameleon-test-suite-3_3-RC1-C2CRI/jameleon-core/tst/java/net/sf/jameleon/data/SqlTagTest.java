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
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.MockTestCaseTag;

import org.apache.commons.jelly.JellyContext;
import org.apache.log4j.Logger;

public class SqlTagTest extends TestCase {

    private SqlTag sqlTag;
    protected final static String DRIVER = "org.h2.Driver";
    protected final static String URL = "jdbc:h2:tst/_tmp/jameleon_test";
    protected final static String USERNAME = "sa";
    protected final static String PASSWORD = "";
    protected final static String TABLE_NAME = "TEST";
    protected final static String CREATE_TABLE_SQL = "Create Table "+TABLE_NAME+" (ID int, FName varchar(15), LName varchar(25), Phone varchar(15))";
    protected final static String INSERT_SQL = "Insert INTO " + TABLE_NAME + " values (1, 'Foo', 'Bar', '801-555-9999'); Insert INTO " + TABLE_NAME + " values (2, 'Bar', 'Foo', '801-555-1212')";

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(SqlTagTest.class);
    }

    public SqlTagTest(String name) {
        super(name);
    }

    public void setUp(){
        sqlTag = new SqlTag();
        sqlTag.setParent(new MockTestCaseTag());
    }

    public void testMapKeys(){
        Map vars = new HashMap();
        vars.put("orig1", "orig value");
        vars.put("orig2", "orig value2");
        vars.put("orig3", "orig value3");
        MockSqlTag sqlTag = new MockSqlTag();
        sqlTag.keyMap = null;
        //test that when mappings keys are null that nothing happens
        sqlTag.mapKeys(vars);
        assertEquals("# of keys", 3, vars.size());
        assertTrue("orig1 should exist", vars.containsKey("orig1"));
        assertTrue("orig2 should exist", vars.containsKey("orig2"));
        assertTrue("orig3 should exist", vars.containsKey("orig3"));

        Map keyMap = new HashMap();
        sqlTag.keyMap = keyMap;
        //test that when mappings keys are empty that nothing happens
        sqlTag.mapKeys(vars);
        assertEquals("# of keys", 3, vars.size());
        assertTrue("orig1 should exist", vars.containsKey("orig1"));
        assertTrue("orig2 should exist", vars.containsKey("orig2"));
        assertTrue("orig3 should exist", vars.containsKey("orig3"));

        keyMap.put("orig1", "new1");
        keyMap.put("orig2", "new2");
        sqlTag.mapKeys(vars);
        assertFalse("orig1 should no longer exist", vars.containsKey("orig1"));
        assertFalse("orig2 should no longer exist", vars.containsKey("orig2"));
        assertTrue("orig3 should still longer exist", vars.containsKey("orig3"));
        assertEquals("orig1's value should not be at new1", "orig value", vars.get("new1"));
        assertEquals("orig2's value should not be at new2", "orig value2", vars.get("new2"));
        assertEquals("orig3's value should be at orig3", "orig value3", vars.get("orig3"));
    }

    public void testMapKeysCallsGetKeyMapping(){
        Map vars = new HashMap();
        MockSqlTag sqlTag = new MockSqlTag();
        sqlTag.mapKeys(vars);
        assertTrue("getKeyMapping was not called", sqlTag.getKeyMappingCalled);
    }

    public void testGetLogger(){
        Logger log = sqlTag.getLogger();
        assertEquals("Name of the logger", "net.sf.jameleon.data.SqlTag", log.getName());
    }

    public void testGetDataDriver(){
        DataDriver dd = sqlTag.getDataDriver();
        assertTrue("Should be of type SqlDataDriver", dd instanceof SqlDataDriver);
        DataDriver dd2 = sqlTag.getDataDriver();
        assertTrue("Should be of type SqlDataDriver", dd == dd2);
    }

    public void testGetDataExceptionMessage(){
        String sql = "Some sql";
        sqlTag.setQuery(sql);
        assertEquals("data exception message", "Trouble querying database with '"+sql+"'", sqlTag.getDataExceptionMessage());
    }

    public void testSetupDataDriver(){
        sqlTag.setJdbcDriver("some driver");
        sqlTag.setJdbcUrl("some url");
        sqlTag.setJdbcPassword("some password");
        sqlTag.setJdbcUsername("some username");
        sqlTag.setQuery("some sql");
        sqlTag.setupDataDriver();
        SqlDataDriver sqld = (SqlDataDriver)sqlTag.sqld;
        assertEquals("SqlDataDriver username", "some username", sqld.username);
        assertEquals("SqlDataDriver password", "some password", sqld.password);
        assertEquals("SqlDataDriver jdbc driver", "some driver", sqld.jdbcDriver);
        assertEquals("SqlDataDriver url", "some url", sqld.url);
        assertEquals("SqlDataDriver sql", "some sql", sqld.query);
    }

    public void testGetTagTraceMsg(){
        String sql = "Some sql";
        sqlTag.setQuery(sql);
        assertEquals("data trace message", "executing sql " + sql, sqlTag.getTagTraceMsg());
    }

    public void testGetTagDescription(){
        assertEquals("data tag description", "sql tag", sqlTag.getTagDescription());
    }

    public void testGetStateStoreLocation(){
        sqlTag.setId("foo");
        assertEquals("The state store location", "sql-foo-12", sqlTag.getNewStateStoreLocation(12));
    }

    public void testSqlConnectionError(){
        testSetupDataDriver();
        IOException ioe = executeData();
        assertEquals("should have failed because the jdbc driver is not valid", "could not register database driver. Class 'some driver' not found", ioe.getMessage());
        sqlTag.setJdbcDriver("org.h2.Driver");
        ioe = executeData();
        assertEquals("should have failed because the jdbc url is not valid", "Could not create connection to the database located at 'some url'!", ioe.getMessage());
    }

    public void testExecuteDrivableRow() throws Exception{
        MockSqlTag mSqlTag = new MockSqlTag();
        mSqlTag.setContext(new JellyContext());
        MockTestCaseTag mtct = new MockTestCaseTag();
        mtct.setContext(new JellyContext());
        mSqlTag.setParent(mtct);
        mSqlTag.tct = mtct;
        SqlDataDriver sqld = (SqlDataDriver)mSqlTag.getDataDriver();
        mSqlTag.setJdbcDriver(DRIVER);
        mSqlTag.setJdbcUrl(URL);
        mSqlTag.setJdbcPassword(PASSWORD);
        mSqlTag.setJdbcUsername(USERNAME);
        mSqlTag.setQuery("select FName as firstName, LName as lastName, Phone from TEST");
        mSqlTag.setupDataDriver();
        try{
            sqld.open();
            executeSql(sqld, CREATE_TABLE_SQL);
            executeSql(sqld, INSERT_SQL);
            sqld.close();
            mSqlTag.executer.executeData(mSqlTag, false);
        }finally{
            sqld.open();
            executeSql(sqld, "Drop Table "+TABLE_NAME);
            sqld.close();
        }
        assertEquals("# of rows executed", 2, mSqlTag.totalNumOfRows);
        HashMap row1 = (HashMap)mSqlTag.rows.get(0);
        HashMap row2 = (HashMap)mSqlTag.rows.get(1);
        assertEquals("# of columns returned in first row", 3, row1.size());
        assertEquals("# of columns returned in second row", 3, row2.size());
        assertEquals("1st row - firstName value ", "Foo", row1.get("FIRSTNAME"));
        assertEquals("1st row - lastName value ", "Bar", row1.get("LASTNAME"));
        assertEquals("1st row - Phone value ", "801-555-9999", row1.get("PHONE"));
        assertEquals("2nd row - firstName value ", "Bar", row2.get("FIRSTNAME"));
        assertEquals("2nd row - lastName value ", "Foo", row2.get("LASTNAME"));
        assertEquals("2nd row - Phone value ", "801-555-1212", row2.get("PHONE"));
    }

    private IOException executeData(){
        sqlTag.setupDataDriver();
        IOException exception = null;
        sqlTag.getTestCaseTag();
        try{
            sqlTag.executer.executeData(sqlTag, false);
            fail("Shouldn't have made it this far");
        }catch(IOException ioe){
            exception = ioe;
        }
        return exception;
    }


    private void executeSql(SqlDataDriver sqld, String createTableSql) throws Exception {
        Statement stmnt = sqld.conn.createStatement();
        stmnt.executeUpdate(createTableSql);
    }
}

