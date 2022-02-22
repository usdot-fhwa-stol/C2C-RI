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
package net.sf.jameleon.data;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class SqlDataDriverTest extends TestCase {

    protected DataExecuter dd;
    protected SqlDataDriver sqlD2;
    protected SqlDataDriver sqlD;
    protected final static String DRIVER = "org.h2.Driver";
    protected final static String URL = "jdbc:h2:tst/_tmp/jameleon_test";
    protected final static String USERNAME = "sa";
    protected final static String PASSWORD = "";
    protected final static String createTableSql = "Create Table TEST (ID int, FName varchar(15), LName varchar(25), Phone varchar(15), Birth_Date TIMESTAMP(0))";
    protected final static String insertSql = "Insert INTO TEST values (1, 'Foo', 'Bar', '801-555-9999', '2005-12-12 10:24:00')";
    protected final static String tableName = "TEST";
    protected final static String query = "SELECT * FROM TEST";


    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(SqlDataDriverTest.class);
    }

    public SqlDataDriverTest(String name) {
        super(name);
    }

    public void setUp() {
        sqlD = new SqlDataDriver();
        sqlD2 = new SqlDataDriver(URL, DRIVER, USERNAME, PASSWORD);
    }

    public void tearDown(){
        sqlD2.close();
        sqlD.close();

        sqlD = null;
        sqlD2 = null;
    }

    public void testOpenDefaultConstructor() {
        SqlDataDriver sqlD = new SqlDataDriver();
        boolean exceptionThrown = false;
        try {
            sqlD.open();
        } catch (IOException ioe){
            fail("An IOException should not be thrown here");
        } catch ( NullPointerException npe ) {
            exceptionThrown = true;
        }
        assertTrue("No connection can be established. An exception should have been thrown",exceptionThrown);
    }

    public void testRegisterJdbcDriver(){
        try{
            sqlD2.registerJDBCDriver();
        }catch(IOException ioe){
            fail(DRIVER +" is a valid JDBC Driver and should have not caused any exceptions");
        }
        sqlD2.setJDBCDriver("foo.bar.Driver");
        boolean exceptionThrown = false;
        try{
            sqlD2.registerJDBCDriver();
        }catch(IOException ioe){
            exceptionThrown = true;
        }
        assertTrue(DRIVER +" is an invalid JDBC Driver and should have caused an IOException", exceptionThrown);
    }

    public void testOpenValid () throws Exception{
        try {
            sqlD2.open();
            assertNotNull("Connection should not be null", sqlD2.conn);
            assertFalse("Connection should be open", sqlD2.conn.isClosed());
        } catch (IOException ioe ) {
            fail("No exception should have been thrown");
        }
    }

    public void testOpenInvalid () throws Exception{
        sqlD2.setJDBCUrl("some bad url");
        IOException exception = null;
        try {
            sqlD2.open();
            fail("An exception should have been thrown");
        } catch (IOException ioe ) {
            exception = ioe;
        }
        assertEquals("The error message when a connection can't be made", "Could not create connection to the database located at 'some bad url'!", exception.getMessage());
    }

    public void testOpenValidateNoUrl () throws Exception{
        sqlD2.setJDBCUrl(null);
        Exception exception = null;
        try {
            sqlD2.open();
            fail("An exception should have been thrown");
        } catch (NullPointerException npe ) {
            exception = npe;
        }
        assertNotNull("A NullPointerException should have been thrown", exception);
    }

    public void testValidateNoDriver () {
        sqlD2.setJDBCDriver(null);
        callValidate("jdbcDriver");
        sqlD2.setJDBCDriver("");
        callValidate("jdbcDriver");
        sqlD2.setJDBCDriver(DRIVER);

        sqlD2.setJDBCUsername(null);
        callValidate("jdbcUsername");
        sqlD2.setJDBCUsername("");
        callValidate("jdbcUsername");
        sqlD2.setJDBCUsername(USERNAME);

        sqlD2.setJDBCUrl(null);
        callValidate("jdbcUrl");
        sqlD2.setJDBCUrl("");
        callValidate("jdbcUrl");
        sqlD2.setJDBCUrl(URL);

        sqlD2.setJDBCPassword(null);

        try {
            sqlD2.validate();
        } catch (NullPointerException npe ) {
            fail("An exception should not have been thrown");
        }
    }

    public void testClose() throws IOException{
        sqlD2.open();
        sqlD2.close();
        assertNull("the Connection should now be Null", sqlD2.conn);
        
    }

    public void testSetJDBCDriver () {
        sqlD.setJDBCDriver(DRIVER);
        assertEquals("The JDBC Driver was not set properly",DRIVER,sqlD.jdbcDriver);
    }

    public void testSetJDBCUrl () {
        sqlD.setJDBCUrl(URL);
        assertEquals("The JDBC url was not set properly",URL,sqlD.url);
    }

    public void testSetJDBCUsername () {
        sqlD.setJDBCUsername(USERNAME);
        assertEquals("The JDBC Username was not set properly",USERNAME,sqlD.username);
    }

    public void testSetJDBCPassword () {
        sqlD.setJDBCPassword(PASSWORD);
        assertEquals("The JDBC Password was not set properly",PASSWORD,sqlD.password);
    }

    public void testSetQuery () {
        sqlD.setQuery( query );
        assertEquals("The query was not set properly", query, sqlD.query);
    }

    public void testExecuteQueryValidSql () throws IOException {
        boolean exceptionThrown = false;
        String error = "";
        try {
            //test the successfull execution of a query.
            createTableStructure(createTableSql);
            insertDataInTable (insertSql);
            sqlD2.open();
            sqlD2.setQuery( query );
            sqlD2.executeQuery();
            assertNotNull ( "The Query failed to execute.", sqlD2.rs );
        } catch ( SQLException sqle ) {
            exceptionThrown = true;
            error = sqle.toString();
        } finally {
            sqlD2.close();
            dropTableStructure(tableName);
        }
        assertFalse("The SQL Select is Valid and should NOT throw an Exception -- " + error, exceptionThrown);
    }

    public void testExecuteQueryInvalidSql () throws IOException {
        boolean exceptionThrown = false;
        try {
            //test the query is invalid being executed.
            createTableStructure(createTableSql);
            insertDataInTable (insertSql);
            sqlD2.open();
            sqlD2.setQuery( "Select * From KKKKKK" );
            sqlD2.executeQuery();
            
        } catch ( SQLException sqle ) {
            exceptionThrown = true;
        } finally {
            sqlD2.close();
            dropTableStructure(tableName);
        }
        assertTrue("The SQL Select is invalid and should throw an Exception ", exceptionThrown);
    }

    public void testSetKeys () throws IOException {
        //#1 valid query with at least one result
        boolean exceptionThrown = false;
        try{
            createTableStructure ( createTableSql );
            insertDataInTable ( insertSql );
            sqlD2.open();
            sqlD2.setQuery( query );
            sqlD2.executeQuery();  
            sqlD2.setKeys();

            assertEquals (5, sqlD2.keys.size() );
            assertEquals ("ID", sqlD2.keys.get(0) );
            assertEquals ("FNAME", sqlD2.keys.get(1) );
            assertEquals ("LNAME", sqlD2.keys.get(2) );
            assertEquals ("PHONE", sqlD2.keys.get(3) );
            assertEquals ("BIRTH_DATE", sqlD2.keys.get(4) );
        } catch ( SQLException sqle ) {
            exceptionThrown = true;
        } finally {
            sqlD2.close();
            dropTableStructure(tableName);
        }
        assertFalse("setKeys should not throw an error with a valid result set", exceptionThrown);
    }

    public void testSetKeysEmptyResultSet () throws IOException {
        boolean exceptionThrown = false;
        try{
            createTableStructure ( createTableSql );
            sqlD2.open();
            sqlD2.setQuery( query );
            sqlD2.executeQuery();  
            sqlD2.setKeys();

            assertEquals (5, sqlD2.keys.size() );
            assertEquals ("ID", sqlD2.keys.get(0) );
            assertEquals ("FNAME", sqlD2.keys.get(1) );
            assertEquals ("LNAME", sqlD2.keys.get(2) );
            assertEquals ("PHONE", sqlD2.keys.get(3) );
            assertEquals ("BIRTH_DATE", sqlD2.keys.get(4) );
            assertFalse("No results should be returned", sqlD2.hasMoreRows());
        } catch ( SQLException sqle ) {
            exceptionThrown = true;
        } finally {
            sqlD2.close();
            dropTableStructure(tableName);
        }
        assertFalse("setKeys should not throw an error with a valid result set", exceptionThrown);
    }

    public void testSetKeysInvalidSelect () throws IOException {
        boolean exceptionThrown = false;
        try{
            createTableStructure ( createTableSql );
            sqlD2.open();
            sqlD2.setQuery( "Select * from KKKKKKK" );
            sqlD2.executeQuery();  
            sqlD2.setKeys();

        } catch ( SQLException sqle ) {
            exceptionThrown = true;
        } finally {
            sqlD2.close();
            dropTableStructure(tableName);
        }
        
        assertTrue("setKeys should throw an error with a invalid result set", exceptionThrown);
    }

    public void testGetNextRowDefaultConstructor() throws IOException{
        createTableStructure ( createTableSql );
        insertDataInTable ( insertSql );
        sqlD2.open();
        sqlD2.setQuery( query );
        Map m = sqlD2.getNextRow();

        assertNotNull ( "There should be one row found", m );
        assertEquals ("1", m.get(sqlD2.keys.get(0)).toString());
        assertEquals ("Foo", m.get(sqlD2.keys.get(1)).toString());
        assertEquals ("Bar", m.get(sqlD2.keys.get(2)).toString());
        assertEquals ("801-555-9999", m.get(sqlD2.keys.get(3)).toString());
        assertEquals ("2005-12-12 10:24:00.0", m.get(sqlD2.keys.get(4)).toString());
        assertNull ( "Should return Null after the one row ", sqlD2.getNextRow());

        m.clear();
        sqlD2.close();
        dropTableStructure(tableName);
    }

    public void testGetNextRowEmptyResultSet() throws IOException{
        createTableStructure ( createTableSql );
        sqlD2.open();
        sqlD2.setQuery( query );

        assertNull ( "There should be ZERO row found", sqlD2.getNextRow() );

        sqlD2.close();
        dropTableStructure(tableName);
    }

    public void testGetNextRowNullData() throws IOException{
        createTableStructure ( createTableSql );
        insertDataInTable ( "Insert INTO TEST (ID, FName, LName) values (1, 'Foo', 'Bar')" );
        sqlD2.open();
        sqlD2.setQuery( query );
        Map m = sqlD2.getNextRow();
        
        assertNotNull ( "There should be one row found", m );
        assertEquals ("1", m.get(sqlD2.keys.get(0)).toString());
        assertEquals ("Foo", m.get(sqlD2.keys.get(1)).toString());
        assertEquals ("Bar", m.get(sqlD2.keys.get(2)).toString());
        assertNull ("this should be null", m.get(sqlD2.keys.get(3)));
        assertNull ("this should be null", m.get(sqlD2.keys.get(4)));
        dropTableStructure(tableName);
    }

    public void testGetNextRowEmptyData() throws IOException{
        createTableStructure ( createTableSql );
        insertDataInTable ( "Insert INTO TEST values (1, 'Foo', 'Bar', '', null)" );
        insertDataInTable ( "Insert INTO TEST values (2, 'Bar', 'Foo', 'some number', null)" );
        sqlD2.open();
        sqlD2.setQuery( query );
        assertTrue("Should have a row", sqlD2.hasMoreRows());
        Map m = sqlD2.getNextRow();
        
        assertNotNull ( "The row was null", m );
        assertEquals ("1", m.get(sqlD2.keys.get(0)).toString());
        assertEquals ("Foo", m.get(sqlD2.keys.get(1)).toString());
        assertEquals ("Bar", m.get(sqlD2.keys.get(2)).toString());
        assertEquals ("", m.get(sqlD2.keys.get(3)).toString());
        assertNull (m.get(sqlD2.keys.get(4)));

        assertTrue("Should have a row", sqlD2.hasMoreRows());
        m = sqlD2.getNextRow();
        assertNotNull ( "The row was null", m );
        assertEquals ("2", m.get(sqlD2.keys.get(0)).toString());
        assertEquals ("Bar", m.get(sqlD2.keys.get(1)).toString());
        assertEquals ("Foo", m.get(sqlD2.keys.get(2)).toString());
        assertEquals ("some number", m.get(sqlD2.keys.get(3)).toString());
        assertNull (m.get(sqlD2.keys.get(4)));
        dropTableStructure(tableName);
    }

    
    public void testHasMoreRowsDefaultConstructor () throws IOException {
        createTableStructure ( createTableSql );
        insertDataInTable ( insertSql );
        sqlD2.open();
        sqlD2.setQuery( query );
        assertTrue("Should have rows. ", sqlD2.hasMoreRows());
        dropTableStructure(tableName);
    }

    public void testHasMoreRowsNo () throws IOException {
        createTableStructure ( createTableSql );
        sqlD2.open();
        sqlD2.setQuery( query );
        assertFalse("Should have no rows. ", sqlD2.hasMoreRows());
        dropTableStructure(tableName);
    }

    private void createTableStructure(String createTableSql) {
        try {
            sqlD2.open();
            Statement stmnt = sqlD2.conn.createStatement();
            stmnt.executeUpdate(createTableSql);
        } catch ( SQLException sqle ) {
            fail ( "SQLException Creating a Table " + sqle );

        } catch ( IOException ioe ) {
            fail ("Error opening Database Connection " + ioe );

        } finally {
            sqlD2.close();
        }
        
    }

    private void insertDataInTable(String insertSql) {
        try {
            sqlD2.open();
            Statement stmnt = sqlD2.conn.createStatement();
            stmnt.executeUpdate(insertSql);
        } catch ( SQLException sqle ) {
            fail ( "SQLException Inserting Row into the Table " + sqle );

        } catch ( IOException ioe ) {
            fail ("Error opening Database Connection " + ioe );

        } finally {
            sqlD2.close();
        }
    }

    private void dropTableStructure(String tableName) {
        try {
            sqlD2.open();
            Statement stmnt = sqlD2.conn.createStatement();
            stmnt.executeUpdate("Drop Table " + tableName);
        } catch ( SQLException sqle ) {
            System.out.println ( "SQLException Dropping the Table " + sqle );

        } catch ( IOException ioe ) {
            fail ("Error opening Database Connection " + ioe );

        } finally {
            sqlD2.close();
        }
    }

    private void callValidate(String fieldName){
        Exception exception = null;
        try {
            sqlD2.validate();
            fail("An exception should have been thrown");
        } catch (NullPointerException npe ) {
            exception = npe;
        }
        assertEquals("The error message when the "+fieldName+" isn't given", "'"+fieldName+"' is a required field!", exception.getMessage());
    }
}
