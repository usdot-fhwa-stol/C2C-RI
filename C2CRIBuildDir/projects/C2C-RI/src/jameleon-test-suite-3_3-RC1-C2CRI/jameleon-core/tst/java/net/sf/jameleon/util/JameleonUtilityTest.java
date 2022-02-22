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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.bean.FunctionalPoint;

import java.io.*;

public class JameleonUtilityTest extends TestCase{

    private static final String TMP_DIR = new String("tst"+File.separator+"_tmp");
    private static final String TEST_DIR = TMP_DIR + File.separator+"another" + File.separator + "dir";
    private static final String TEST_FILE = TMP_DIR + File.separator + "some" + File.separator + "dumb"
                                            + File.separator+"dir"+File.separator+"test.txt";

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( JameleonUtilityTest.class );
    }

    public JameleonUtilityTest( String name ) {
        super( name );
    }

    public void testGetEndingPath(){
        String f = "./some/dir/with/a/file.txt";
        assertEquals("file ending", JameleonUtility.fixFileSeparators("some/dir/with/a/file.txt"), JameleonUtility.getEndingPath((String)null, f));
        assertEquals("file ending", "", JameleonUtility.getEndingPath("some", (String) null));
        assertEquals("file ending", JameleonUtility.fixFileSeparators("dir/with/a/file.txt"), JameleonUtility.getEndingPath("some",f));
        f = "some/dir/with/a/file.txt";        
        assertEquals("file ending", JameleonUtility.fixFileSeparators("dir/with/a/file.txt"), JameleonUtility.getEndingPath("some",f));
        assertEquals("file ending", JameleonUtility.fixFileSeparators("dir/with/a/file.txt"), JameleonUtility.getEndingPath("./some",f));
        assertEquals("file ending", JameleonUtility.fixFileSeparators("with/a/file.txt"), JameleonUtility.getEndingPath("some/dir", f));
        assertEquals("file ending", "file.txt", JameleonUtility.getEndingPath("some/dir/with/a", f));        
    }
    
    public void testLoadFunctionalPoint(){
        String className = "net.sf.jameleon.DummyJameleonTagSupport";
        FunctionalPoint fp = JameleonUtility.loadFunctionalPoint(className, this);
        assertNotNull("Functional Point should not be null", fp);
        assertEquals("Tag Name", "mock-jameleon-tag-support", (String)fp.getTagNames().get(0));
        assertEquals("# of attributes", 2, fp.getAttributes().size());
    }
    
    public void testLoadFunctionalPointNoDatFile(){
        Exception ex = null;
        try{
            JameleonUtility.loadFunctionalPoint("java.lang.String", this);
        }catch (RuntimeException re){
            ex = re;
        }
        assertNotNull("A RuntimeException should have been thrown!", ex);
        assertTrue("Some message about String.dat not found should be displayed! " + ex.getMessage(),
                   ex.getMessage().indexOf("Could not find 'java/lang/String.dat'!") >= 0);
    }

    public void testDecodeXMLToText(){
        String escapedXML = "&lt;xml&gt;";
        String xml = JameleonUtility.decodeXMLToText(escapedXML);
        assertEquals("xml converted text", "<xml>", xml);
        escapedXML = "&lt;value&gt;automation &amp; testing&lt;/value&gt;";
        xml = JameleonUtility.decodeXMLToText(escapedXML);
        assertEquals("xml converted text", "<value>automation & testing</value>", xml);
        escapedXML = "<value>automation &amp; testing</value>";
        xml = JameleonUtility.decodeXMLToText(escapedXML);
        assertEquals("xml converted text", "<value>automation & testing</value>", xml);
    }

    public void testDecodeTextToXML(){
        String xml = "<xml>";
        String text = JameleonUtility.decodeTextToXML(xml);
        assertEquals("xml converted text", "&lt;xml&gt;", text);
        xml = "<value>automation & testing</value>";
        text = JameleonUtility.decodeTextToXML(xml);
        assertEquals("xml converted text", "&lt;value&gt;automation &amp; testing&lt;/value&gt;", text);
    }

    public void testCreateDirStructure(){
        File dirToCreate = new File(TEST_DIR);
        JameleonUtility.createDirStructure(dirToCreate);
        assertTrue(TEST_DIR +" not created!",dirToCreate.isDirectory());
        assertTrue(TEST_DIR +" not created!",dirToCreate.getAbsolutePath().indexOf(TEST_DIR) >= 0);
    }

    public void testDeleteDirStructure() throws IOException{
        File dirToCreate = new File(TEST_DIR);
        JameleonUtility.createDirStructure(dirToCreate);
        JameleonUtility.recordResultsToFile(new File(TEST_FILE), "hello world");
        File tmpDir = new File(TMP_DIR);
        JameleonUtility.deleteDirStructure(tmpDir);
        assertFalse(TMP_DIR +" not deleted!",tmpDir.exists());
    }

    public void testRecordResultsToFile(){
        String content = "Some more text";
        File f = new File(TEST_FILE);
        try{
            JameleonUtility.recordResultsToFile(f,content);
        }catch(IOException ioe){
            ioe.printStackTrace();
            fail("File should have been created");
        }
        try{
            BufferedReader in = new BufferedReader(new FileReader(TEST_FILE));
            String line = null;
            String newContent = "";
            while ((line = in.readLine()) != null) {
                newContent += line;
            }
            assertEquals(content, newContent);
        }catch(FileNotFoundException fnfe){
            fail(TEST_FILE+" NOT created!");
        }catch(IOException ioe){
            fail(TEST_FILE+ " could not be read!");
        }
    }

    public void testExecutionTimeToStringMS1(){
        long time = 55L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:", "0.055s", timeS);
    }

    public void testExecutionTimeToStringMS2(){
        long time = 555L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:", "0.555s", timeS);
    }

    public void testExecutionTimeToStringMS3(){
        long time = 5L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:", "0.005s", timeS);
    }

    public void testExecutionTimeToStringMinMS1(){
        long time = 5005L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:", "5.005s", timeS);
    }

    public void testExecutionTimeToStringMinMS2(){
        long time = 15005L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:", "15.005s", timeS);
    }

    public void testExecutionTimeToStringMinMS3(){
        long time = 60000L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time", "1m", timeS);
    }

    public void testExecutionTimeToStringMinMS4(){
        long time = 61505L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:" + timeS, "1m 1.505s", timeS);
    }

    public void testExecutionTimeToStringMinMS5(){
        long time = 111505L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:" + timeS, "1m 51.505s", timeS);
    }

    public void testExecutionTimeToStringHH1(){
        long time = 3600000L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:" + timeS, "1h", timeS);
    }

    public void testExecutionTimeToStringHHMin1MS1(){
        long time = 7305402L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:" + timeS, "2h 1m 45.402s", timeS);
    }

    public void testExecutionTimeToStringHHMin0MS1(){
        long time = 7245402L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:" + timeS, "2h 45.402s", timeS);
    }

    public void testExecutionTimeToString0(){
        long time = 0L;
        String timeS = JameleonUtility.executionTimeToString(time);
        assertEquals("Execution time in MS:" + timeS, "0.000s", timeS);
    }

    public void testStripHtmlCommentsOneComment(){
        String html = "<head><!--some text--></head>";
        String stripped = JameleonUtility.stripHtmlComments(html);
        assertTrue(stripped.indexOf("some text") == -1);
        assertTrue(stripped.indexOf("<!--") == -1);
        assertTrue(stripped.indexOf("-->") == -1);
        assertEquals("<head></head>", stripped); 
    }

    public void testStripHtmlCommentsNewLines(){
        String html = "cell<!-- With a comment\n after new lines-->";
        String stripped = JameleonUtility.stripHtmlComments(html);
        assertTrue("With a comment should not exist",stripped.indexOf("With a comment") == -1);
        assertTrue("<!-- should not exist",stripped.indexOf("<!--") == -1);
        assertTrue("--> should not exist",stripped.indexOf("-->") == -1);
    }

    public void testStripHtmlCommentsTwoComments(){
        String html = "<head><!--some text--></head><body>some more text here<!--NO COMMENT--></body>";
        String stripped = JameleonUtility.stripHtmlComments(html);
        assertTrue(stripped.indexOf("some text") == -1);
        assertTrue(stripped.indexOf("NO COMMENT") == -1);
        assertTrue(stripped.indexOf("<!--") == -1);
        assertTrue(stripped.indexOf("-->") == -1);
        assertEquals("Got "+stripped, "<head></head><body>some more text here</body>", stripped); 
    }

    public void testStripHtmlCommentsThreeComments(){
        String html = "<head><!--some text--></head><body>some more text here<!--NO COMMENT-->"+
                      "<table><tr><td>Some cell<!-- With a comment\n\n\n\r after new lines-->"+
                      "</td></tr></table></body>";
        String stripped = JameleonUtility.stripHtmlComments(html);
        assertTrue("some text should not exist",stripped.indexOf("some text") == -1);
        assertTrue("NO COMMENT should not exist",stripped.indexOf("NO COMMENT") == -1);
        assertTrue("With a comment should not exist",stripped.indexOf("With a comment") == -1);
        assertTrue("<!-- should not exist",stripped.indexOf("<!--") == -1);
        assertTrue("--> should not exist",stripped.indexOf("-->") == -1);
        assertEquals("Got "+stripped, "<head></head><body>some more text here<table><tr><td>Some cell</td>" +
                     "</tr></table></body>", stripped); 
    }

    public void testFixFileSeparators() {
        String before = "this/is/a/path";
        String after = "this\\is\\a\\path";
        assertEquals("convert / to \\", after, JameleonUtility.fixFileSeparators(before, "\\"));
        assertEquals("convert \\ to /", before, JameleonUtility.fixFileSeparators(after, "/"));
    }

    public void testGetFileNameFromPath(){
        String name = JameleonUtility.getFileNameFromPath(
            JameleonUtility.fixFileSeparators("tst/xml/httpunit/somefile.xml"));
        assertEquals("somefile",name);
        name = JameleonUtility.getFileNameFromPath("tst/xml/httpunit/somefile.xml");
        assertEquals("somefile",name);
    }

    public void testGetFileNameFromScriptPath(){
        String name = JameleonUtility.getFileNameFromScriptPath("file:/some/dir/tst/somefile.xml");
        assertEquals("somefile.xml",name);
        name = JameleonUtility.getFileNameFromScriptPath("file:/C:/tst/xml/httpunit/some file.xml");
        assertEquals("some file.xml",name);
        name = JameleonUtility.getFileNameFromScriptPath("file:\\C:\\tst\\xml\\httpunit\\somefile.xml");
        assertEquals("somefile.xml",name);
    }

    public void testTetFileNameFromPathDotBak(){
        String name = JameleonUtility.getFileNameFromPath(
            JameleonUtility.fixFileSeparators("tst/xml/httpunit/somefile.xml.bak"));
        assertEquals("somefile.xml",name);
    }

}
