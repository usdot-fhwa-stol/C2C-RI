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
package net.sf.jameleon.ant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.bean.Javadoc2Bean;
import net.sf.jameleon.util.InstanceSerializer;
import net.sf.jameleon.util.JameleonUtility;

import org.apache.tools.ant.BuildException;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;

public class JameleonXDocletTest extends TestCase {

    private static final String TST_DIR = "tst/java/net/sf/jameleon/ant/";
    private static final String TST_PKG = "net.sf.jameleon.ant.";
    private static final String ONE_TAG = "DummyTagOneTagName";
    private static final String NO_TAG = "DummyTagNoTagName";
    private static final String EMPTY_TAG = "DummyTagEmptyTagName";
    private static final String TWO_TAGS = "DummyTagTwoTagNames";
    private static final String FULL_TAG = "DummyTagFullDocs";
    private static final String INVALID_TYPE_TAG = "DummyTagInvalidType";
    private static final String TMP_FILE_NAME = "tst/_tmp/tagDefs.props";

    protected JavaDocBuilder docBuilder;
    protected JameleonXDoclet jxdoc;
    protected Javadoc2Bean j2b;
    protected Properties props;

    public JameleonXDocletTest( String name ) {
        super( name );
    }
    
    //JUnit Methods
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }
    
    public static Test suite() {
        return new TestSuite( JameleonXDocletTest.class );
    }
    public void testTest(){
    	assertTrue(true);
    }

    public void setUp(){
        File dir = new File(JameleonUtility.fixFileSeparators(TST_DIR));
        j2b = new Javadoc2Bean();
        docBuilder = j2b.getJavaDocBuilder();
        docBuilder.addSourceTree(dir);
        jxdoc = new JameleonXDoclet();
        jxdoc.setOutputDir(new File("tst/_tmp"));
        props = new Properties();
    }

    public void tearDown(){
        docBuilder = null;
        jxdoc = null;
        props = null;
        j2b = null;
    }
    //End JUnit Methods

    public void testSetClassAttributes(){
        File serializedFile = null;
        try{
            JavaClass clss = docBuilder.getClassByName(TST_PKG+FULL_TAG);
            jxdoc.setOutputDir(new File("tst/java"));
            serializedFile = new File(TST_DIR+FULL_TAG+InstanceSerializer.SERIALIZED_EXT);
            serializedFile.delete();
            jxdoc.setClassAttributes(clss, props, j2b);
            assertEquals("Number of function points registered", 1, props.size());
            assertContains("Registered tag", FULL_TAG, props.getProperty("tag-full-docs"));
            assertTrue("Serialized File should have been created",serializedFile.exists());
        }finally{
            serializedFile.delete();
        }
    }

    public void testSetClassAttributesDuplicateTagName() {
        JavaClass clss = docBuilder.getClassByName(TST_PKG+ONE_TAG);
        jxdoc.setClassAttributes(clss, props, j2b);
        Exception e = null;
        try{
            jxdoc.setClassAttributes(clss, props, j2b);
        }catch(BuildException be){
        	e = be;
        }
        assertNotNull("An exception should have occured.", e);
    }

    public void testRegisterMultipleNamesSameFunction() {
        JavaClass clss = docBuilder.getClassByName(TST_PKG+TWO_TAGS);
        jxdoc.setClassAttributes(clss, props, j2b);
        assertEquals("Number of function points registered", 2, props.size());
        assertContains("Registered tag", TWO_TAGS, props.getProperty("two-tag-name1"));
        assertContains("Registered tag", TWO_TAGS, props.getProperty("two-tag-name2"));
    }

    public void testGetTagDefsFileName(){
        jxdoc.setOutputDir(new File("tst/_tmp"));
        jxdoc.setOutputFileName("tagDefs.properties");
        assertEquals("Full path of testcase tag defs file", JameleonUtility.fixFileSeparators("tst/_tmp/tagDefs.properties"),jxdoc.getTagDefsFileName());
    }

    public void testSaveTagDefs() throws IOException{
        JameleonUtility.createDirStructure(new File("tst/_tmp"));
        File f = new File(TMP_FILE_NAME);
        try{
            props.setProperty("christian","genius");
            jxdoc.saveTagDefs(props, TMP_FILE_NAME);
            assertTrue("File exists ", f.exists());
            Properties props2 = new Properties();
            props2.load(new FileInputStream(f));
            assertEquals("Number of tags in file",1,props2.size());
            assertEquals("Tag def should have been persisted","genius",(String)props2.get("christian"));
        }finally{
            f.delete();
        }
    }

    public void testConstructFileFromClassName() {
        jxdoc.setOutputDir(new File("tst/_tmp"));
        File f = jxdoc.constructFileFromClassName("net.Class.class");
        assertEquals("Entire path to source file with .class",
                     JameleonUtility.fixFileSeparators("tst/_tmp/net/Class"+
                                                       InstanceSerializer.SERIALIZED_EXT),
                     f.getPath());
        f = jxdoc.constructFileFromClassName("net.Class");
        assertEquals("Entire path to source file without .class",
                     JameleonUtility.fixFileSeparators("tst/_tmp/net/Class"+
                                                       InstanceSerializer.SERIALIZED_EXT),
                     f.getPath());
    }

    public void testSetClassAttributesNoTagName() {
        expectWarningMessageForClass("Please add a @jameleon.function name=\"functionName\"", NO_TAG);
    }

    public void testSetClassAttributesEmptyTagName() {
        expectWarningMessageForClass("Please add a @jameleon.function name=\"functionName\"", EMPTY_TAG);
    }

    public void testSetClassAttributesInvalidType() {
        JavaClass clss = docBuilder.getClassByName(TST_PKG+INVALID_TYPE_TAG);
        boolean exceptionThrown = false;
        try{
            jxdoc.setClassAttributes(clss,props,j2b);
        }catch(BuildException be){
            if (be.getMessage() != null && be.getMessage().indexOf("is not a valid functional point type") > -1) {
                exceptionThrown = true;
            }
        }
        assertTrue("A Build exception with the corroect message was not thrown",exceptionThrown);
    }

    protected void expectWarningMessageForClass(String warning, String className) {
        PrintStream stdout = System.out;
        ByteArrayOutputStream testStdout = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testStdout));
        try {
            JavaClass clss = docBuilder.getClassByName(TST_PKG+className);
            jxdoc.setClassAttributes(clss, props, j2b);
            assertContains("Expected a warning message containing", warning, testStdout.toString());
        } finally {
            System.setOut(stdout);
        }
    }

    protected void assertContains(String msg, String expectedPattern, String actual) {
        assertNotNull(msg + ": The string was null", actual);
        assertTrue(msg + ": <" + expectedPattern + "> not found in: <" + actual+">", actual.indexOf(expectedPattern) >= 0);
    }

}

