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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CsvDataDriverTest extends TestCase {

    protected DataExecuter dd;
    protected CsvDataDriver csvD;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(CsvDataDriverTest.class);
    }

    public CsvDataDriverTest(String name) {
        super(name);
    }

    public void setUp() {
        csvD = new CsvDataDriver();
    }

    public void testOpenDefaultConstructor() {
        boolean exceptionThrown = false;
        try {
            csvD.open();
        } catch ( IOException ioe ) {
            exceptionThrown = true;
        }
        assertTrue("An exception should have been thrown",exceptionThrown);
    }

    public void testHasMoreRowsDefaultConstructor() {
        assertFalse("should not have ANY rows when first instantiated",csvD.hasMoreRows());
    }

    public void testGetNextRowDefaultConstructor() {
        Map vars = csvD.getNextRow();
        assertNull("Data row should be null when first instantiated",vars);
    }

    public void testConstructorWithFile() {
        csvD = new CsvDataDriver(new File("tst/data/CsvDriver.csv"));
        assertNotNull("File should not be null",csvD.csvFile);
    }

    public void testOpenValidFile() throws IOException{
        testConstructorWithFile();
        boolean exceptionNotThrown = true;
        try {
            csvD.open();
        } catch ( IOException ioe ) {
            exceptionNotThrown = false;
        }
        assertTrue("No IOException should have been thrown with valid file", exceptionNotThrown);
        assertTrue("File should be a file",csvD.csvFile.isFile());
        assertTrue("File should be able to be read",csvD.csvFile.canRead());
    }

    public void testCloseValidFile() throws IOException{
        testOpenValidFile();
        csvD.getNextRow();
        assertEquals("Keys", 2, csvD.keys.size());
        csvD.close();
        assertEquals("Keys", 0, csvD.keys.size());
        IOException e = null;
        try {
            csvD.in.ready();
        } catch ( IOException ioe ) {
            e = ioe;
        }
        assertNotNull("An IOException should have been thrown", e);
        assertTrue("The IOException should contain 'Stream closed'", e.getMessage().indexOf("Stream closed") > -1);
    }

    public void testCloseNoOpen(){
        csvD.close();
    }

    public void testOpenInvalidFile() throws IOException{
        csvD = new CsvDataDriver(new File("tst/data/SomeNonExistentFile.csv"));
        assertNotNull("File should not be null",csvD.csvFile);
        boolean exceptionThrown = false;
        try {
            csvD.open();
        } catch ( FileNotFoundException fnfe ) {
            exceptionThrown = true;
        }
        assertTrue("An IOException should have been thrown due to invalid file", exceptionThrown);
        assertFalse("File should not be a file",csvD.csvFile.isFile());
        assertFalse("File should not be able to be read",csvD.csvFile.canRead());
    }

    public void testHasMoreRowsValidFile() throws IOException{
        testConstructorWithFile();
        csvD.open();
        assertTrue("No rows read in; should return true",csvD.hasMoreRows());
        csvD.getNextRow();
        assertFalse("One row read in; should return false",csvD.hasMoreRows());
    }

    public void testGetNextRowInvalidFile() throws IOException{
        testOpenInvalidFile();
        Map vars = csvD.getNextRow();
        assertNull("Data row should be null when read from invalid file",vars);
    }

    public void testParseValueLine(){
        String str = "var1,var2";
        doLineTest(csvD.parseLine(str,false));
        str = "\"var1\",\"var2\"";
        doLineTest(csvD.parseLine(str,false));
        str = "var1,\"var2\"";
        doLineTest(csvD.parseLine(str,false));
        str = "\"var1\",var2";
        doLineTest(csvD.parseLine(str,false));
        str = "var1,";
        List values = csvD.parseLine(str,false);
        assertEquals("Number of values in line", 2, values.size());
        assertEquals("var1",(String)values.get(0));
        assertEquals("",(String)values.get(1));
        str = ",,,";
        values = csvD.parseLine(str,false);
        assertEquals("Number of values in line", 4, values.size());
        assertEquals("",(String)values.get(0));
        assertEquals("",(String)values.get(1));
        assertEquals("",(String)values.get(2));
        assertEquals("",(String)values.get(3));
        str = " var1,var2 ";
        values = csvD.parseLine(str,false);
        assertEquals("Number of values in line", 2, values.size());
        assertEquals(" var1",(String)values.get(0));
        assertEquals("var2 ",(String)values.get(1));
        str = "var1 , var2";
        values = csvD.parseLine(str,false);
        assertEquals("Number of values in line", 2, values.size());
        assertEquals("var1 ",(String)values.get(0));
        assertEquals(" var2",(String)values.get(1));
        str = "  var1 , var2  ";
        values = csvD.parseLine(str,false);
        assertEquals("Number of values in line", 2, values.size());
        assertEquals("  var1 ",(String)values.get(0));
        assertEquals(" var2  ",(String)values.get(1));
        str = " \" var1 \", \" var2 \" ";
        values = csvD.parseLine(str,false);
        assertEquals("Number of values in line", 2, values.size());
        assertEquals(" var1 ",(String)values.get(0));
        assertEquals(" var2 ",(String)values.get(1));
    }

    public void testParseKeyLine(){
        String str = "var1 , var2";
        doLineTest(csvD.parseLine(str,true));
        str = " var1,var2 ";
        doLineTest(csvD.parseLine(str,true));
        str = "  var1 ,   var2  ";
        doLineTest(csvD.parseLine(str,true));
        str = "  \"var1\",\"var2\"  ";
        doLineTest(csvD.parseLine(str,true));
        str = "\"var1\" , \"var2\"";
        doLineTest(csvD.parseLine(str,true));
        str = "var1 ,  \"var2\" ";
        doLineTest(csvD.parseLine(str,true));
        str = "\"var1\"  ,  var2 ";
        doLineTest(csvD.parseLine(str,true));
        str = "var1,";
        List values = csvD.parseLine(str,true);
        assertEquals("Number of values in line", 2, values.size());
        assertEquals("var1",(String)values.get(0));
        assertEquals("",(String)values.get(1));
        str = ",,,";
        values = csvD.parseLine(str,true);
        assertEquals("Number of values in line", 4, values.size());
        assertEquals("",(String)values.get(0));
        assertEquals("",(String)values.get(1));
        assertEquals("",(String)values.get(2));
        assertEquals("",(String)values.get(3));
        str = " \" var1 \"     , \" var2 \" ";
        values = csvD.parseLine(str,false);
        assertEquals("Number of values in line", 2, values.size());
        assertEquals(" var1 ",(String)values.get(0));
        assertEquals(" var2 ",(String)values.get(1));
    }

    public void testParseLineWithComment(){
        String str = "#var1,var2";
        List values = csvD.parseLine(str,false);
        assertEquals("No values in list", 0, values.size());
        str = "var1,#var2";
        values = csvD.parseLine(str,false);
        assertEquals("No values in list", 2, values.size());
    }

    protected void doLineTest(List values){
        assertEquals("Number of values in line", 2, values.size());
        assertEquals("var1",(String)values.get(0));
        assertEquals("var2",(String)values.get(1));
    }

    public void testSetKeys() throws IOException{
        testOpenValidFile();
        csvD.setKeys();
        assertNotNull("csv file should contain the first row",csvD.keys);
        assertEquals("Should contain var1 key", "var1", (String)csvD.keys.get(0));
        assertEquals("Should contain var2 key", "var2", (String)csvD.keys.get(1));
    }

    public void testIsLineCommented(){
        String line = "#some comment here";
        assertTrue("line is commented", csvD.isLineCommented(line));
        line = "#value1, value2, value3";
        assertTrue("line is commented", csvD.isLineCommented(line));
        line = "value1, #value2, value3";
        assertFalse("line is not commented", csvD.isLineCommented(line));
        line = "value1, value2, value3";
        assertFalse("line is not commented", csvD.isLineCommented(line));
    }

    public void testReadLine() throws IOException{
        csvD = new CsvDataDriver(new File("tst/data/CsvDriverComments.csv"));
        assertEquals("# of keys", 0, csvD.keys.size());
        assertNull("line of text", csvD.line);
        csvD.open();
        csvD.readLine();
        assertEquals("# of keys", 2, csvD.keys.size());
        assertEquals("first value row", "val3,val4", csvD.line);
        csvD = new CsvDataDriver(new File("tst/data/CsvDriverTwoRows.csv"));
        assertEquals("# of keys", 0, csvD.keys.size());
        assertNull("line of text", csvD.line);
        csvD.open();
        csvD.readLine();
        assertEquals("# of keys", 2, csvD.keys.size());
        assertEquals("first value row", "val1,val2", csvD.line);
        csvD.readLine();
        assertEquals("# of keys", 2, csvD.keys.size());
        assertEquals("second value row", "val3,val4", csvD.line);
    }

    public void testGetNextRowValidFile() throws IOException{
        csvD = new CsvDataDriver(new File("tst/data/CsvDriverTwoRows.csv"));
        csvD.open();
        assertTrue("should have more rows",csvD.hasMoreRows());
        Map vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("var1 value","val1",(String)vars.get("var1"));
        assertEquals("var2 value","val2",(String)vars.get("var2"));
        assertTrue("should have more rows",csvD.hasMoreRows());
        vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("var1 value","val3",(String)vars.get("var1"));
        assertEquals("var2 value","val4",(String)vars.get("var2"));
    }

    public void testGetNextRowValidFileComments() throws IOException{
        csvD = new CsvDataDriver(new File("tst/data/CsvDriverComments.csv"));
        csvD.open();
        assertTrue("should have more rows",csvD.hasMoreRows());
        Map vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("var1 value","val3",(String)vars.get("var1"));
        assertEquals("var2 value","val4",(String)vars.get("var2"));
    }

    public void testGetNextRowValidFileComments1stRow() throws IOException{
        csvD = new CsvDataDriver(new File("tst/data/CsvDriverComments1stRow.csv"));
        csvD.open();
        assertTrue("should have more rows",csvD.hasMoreRows());
        Map vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("# of keys",2, vars.size());
        assertEquals("var1 value","val3",(String)vars.get("var1"));
        assertEquals("var2 value","val4",(String)vars.get("var2"));
    }

    public void testGetNextRowValidFileNullValues() throws IOException{
        csvD = new CsvDataDriver(new File("tst/data/CsvDriverNullValues.csv"));
        csvD.open();
        assertTrue("should have more rows",csvD.hasMoreRows());
        Map vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("# of variables in map", 4, vars.size());
        assertEquals("somevar1 value",null,(String)vars.get("somevar1"));
        assertEquals("somevar2 value","someval2",(String)vars.get("somevar2"));
        assertEquals("somevar3 value","someval3",(String)vars.get("somevar3"));
        assertEquals("somevar4 value",null,(String)vars.get("somevar4"));
        assertTrue("should have more rows",csvD.hasMoreRows());
        vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("somevar1 value",null,(String)vars.get("somevar1"));
        assertEquals("somevar2 value",null,(String)vars.get("somevar2"));
        assertEquals("somevar3 value",null,(String)vars.get("somevar3"));
        assertEquals("somevar4 value",null,(String)vars.get("somevar4"));
        assertTrue("should have more rows",csvD.hasMoreRows());
        vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("somevar1 value","someval1",(String)vars.get("somevar1"));
        assertEquals("somevar2 value",null,(String)vars.get("somevar2"));
        assertEquals("somevar3 value",null,(String)vars.get("somevar3"));
        assertEquals("somevar4 value","someval4",(String)vars.get("somevar4"));
    }

    public void testGetNextRowValidFileTooManyValues() throws IOException{
        File f = new File("tst/data/CsvDriverTooManyValues.csv");
        csvD = new CsvDataDriver(f);
        csvD.open();
        assertTrue("should have more rows",csvD.hasMoreRows());
        IllegalStateException exception = null;
        try{
            csvD.getNextRow();
        }catch(IllegalStateException ise){
            exception = ise;
        }
        assertNotNull("An exception should have been thrown",exception);
        assertTrue("Error message should say something about too many columns:",
                   exception.getMessage().indexOf(f.getPath()+" has more values than keys") > -1);
    }

    public void testConstructorWithDelimeter() throws IOException{
        csvD = new CsvDataDriver(new File("tst/data/CsvDriverPipeDelimited.csv"), '|');
        csvD.open();
        assertTrue("should have more rows",csvD.hasMoreRows());
        Map vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("var1 value","value1",(String)vars.get("var1"));
        assertEquals("var2 value","val2",(String)vars.get("var2"));
        assertTrue("should have more rows",csvD.hasMoreRows());
        vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("var1 value","val3",(String)vars.get("var1"));
        assertEquals("var2 value","value4",(String)vars.get("var2"));

    }

    public void testSetDelimeter() throws IOException{
        csvD = new CsvDataDriver(new File("tst/data/CsvDriverPipeDelimited.csv"));
        csvD.setDelimiter('|');
        csvD.open();
        assertTrue("should have more rows",csvD.hasMoreRows());
        Map vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("var1 value","value1",(String)vars.get("var1"));
        assertEquals("var2 value","val2",(String)vars.get("var2"));
        assertTrue("should have more rows",csvD.hasMoreRows());
        vars = csvD.getNextRow();
        assertNotNull("Data row should not be null when read from valid file",vars);
        assertEquals("var1 value","val3",(String)vars.get("var1"));
        assertEquals("var2 value","value4",(String)vars.get("var2"));

    }

    public void testSetFile(){
        File f = new File("tst/data/CsvDriver.csv");
        csvD.setFile(f);
        boolean exceptionNotThrown = true;
        try {
            csvD.open();
        } catch ( IOException ioe ) {
            exceptionNotThrown = false;
        }
        assertTrue("No IOException should have been thrown with valid file", exceptionNotThrown);
        assertEquals("CSV File", f, csvD.getFile());
    }

    public void testSetEncodingGetEncoding(){
        csvD.setEncoding("KOR");
        assertEquals("Encoding", "KOR", csvD.getEncoding());
    }

    public void testGetFile(){
        File f = new File("tst/data/CsvDriver.csv");
        csvD.setFile(f);
        assertEquals("CSV File", f ,csvD.getFile());
    }

}
