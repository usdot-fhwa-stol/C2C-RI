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
package net.sf.jameleon.bean;

import net.sf.jameleon.util.JameleonUtility;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.FileNotFoundException;

import java.util.MissingResourceException;

import com.thoughtworks.qdox.model.JavaClass;

public class Javadoc2BeanTest extends TestCase{
    private Javadoc2Bean j2b;
    private static final String FP_TAG_NAME = "javadoc2bean-dummy1";
    private static final String FP_CLASS_NAME = "net.sf.jameleon.bean.Dummy1";
    private static final String TST_DIR = JameleonUtility.fixFileSeparators("tst/java");
    private static final String FP_SRC_FILE = JameleonUtility.fixFileSeparators(TST_DIR + "/net/sf/jameleon/bean/Dummy1.java");
    private static final String FP_SRC_FILE_NOT_VALID_FP = JameleonUtility.fixFileSeparators(TST_DIR + "/net/sf/jameleon/bean/AttributeTest.java");
    private static final String DUMMY2_CLASS = "net.sf.jameleon.bean.Dummy2";
    private static final String DUMMY3_CLASS = "net.sf.jameleon.bean.Dummy3";
    private static final String DUMMY2_SUB_CLASS = "net.sf.jameleon.bean.Dummy2Sub";
    private static final String DUMMY_WITH_APPLICATION = "net.sf.jameleon.bean.DummyWithApplication";
    private static final String DUMMY_WITH_APPLICATIONS = "net.sf.jameleon.bean.DummyWithApplications";

    private File tstTagFile;

    public Javadoc2BeanTest( String name ) {
        super( name );
    }

    //JUnit Methods
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( Javadoc2BeanTest.class );
    }
    
    public void testTest(){
    	assertTrue(true);
    }

    public void setUp(){
        File dir = new File(JameleonUtility.fixFileSeparators(TST_DIR+"/net/sf/jameleon/bean"));
        j2b = new Javadoc2Bean();
        j2b.setIsA("net.sf.jameleon.JameleonTagSupport"); 
        j2b.getJavaDocBuilder().addSourceTree(dir);
        dir = new File(JameleonUtility.fixFileSeparators(TST_DIR+"/net/sf/jameleon/function"));
        j2b.getJavaDocBuilder().addSourceTree(dir);
        tstTagFile = new File(TST_DIR);
        j2b.setSourceDir(tstTagFile);
    }

    public void tearDown(){
        j2b = null;
        tstTagFile = null;
    }
    //End JUnit Methods

    public void testGetShortDescription(){
    	String desc = null;
    	assertNull("short description", j2b.getShortDescription(desc));
    	desc = "";
    	assertEquals("short description", "", j2b.getShortDescription(desc));

    	desc = "First sentence. with a period in it. actually three periods in it.";
    	assertEquals("short description", "First sentence.", j2b.getShortDescription(desc));
    	desc = "First sentence with out a new line or a period in it";
    	assertEquals("short description", "First sentence with out a new line or a period in it.", j2b.getShortDescription(desc));
    	desc = "First sentence with out a period in it\r\nFollowed by another line";
    	assertEquals("short description", "First sentence with out a period in it.", j2b.getShortDescription(desc));
    	desc = "First sentence with out a period in it\n\rFollowed by another line";
    	assertEquals("short description", "First sentence with out a period in it.", j2b.getShortDescription(desc));
    	desc = "First sentence with out a period in it\rFollowed by another line";
    	assertEquals("short description", "First sentence with out a period in it.", j2b.getShortDescription(desc));
    	desc = "First sentence with out a period in it\nFollowed by another line";
    	assertEquals("short description", "First sentence with out a period in it.", j2b.getShortDescription(desc));
    	desc = "First sentence with a period in it.\nFollowed by another line.\n and another line\nand yet another";
    	assertEquals("short description", "First sentence with a period in it.", j2b.getShortDescription(desc));
    }
    
    public void testConvertClassNameToSourceName(){
        String className = "check.to.see.if.this.Works.class";
        String fileName = j2b.convertClassNameToSourceName(className,true);
        assertEquals("Source File",JameleonUtility.fixFileSeparators("check/to/see/if/this/Works.java"), fileName);
        className = "check.to.see.if.this.Works";
        fileName = j2b.convertClassNameToSourceName(className,true);
        assertEquals("Source File",JameleonUtility.fixFileSeparators("check/to/see/if/this/Works.java"), fileName);
    }

    public void testGetFunctionalPointWithClass(){
        try{
            FunctionalPoint fp = j2b.getFunctionalPointWithClass(FP_CLASS_NAME);
            validateFunctionalPoint(fp);
        }catch (FileNotFoundException fnfe){
            fail("File should exist!" + " "+fnfe.getMessage());
        }
    }

    public void testGetFunctionalPointWithSource(){
        try{
            FunctionalPoint fp = j2b.getFunctionalPointWithSource(new File(FP_SRC_FILE));
            validateFunctionalPoint(fp);
        }catch (FileNotFoundException fnfe){
            fail("File should exist!" + " "+fnfe.getMessage());
        }
    }

    public void testGetFunctionalPointWithSourceNotAValidFunctionalPoint(){
        try{
            FunctionalPoint fp = j2b.getFunctionalPointWithSource(new File(FP_SRC_FILE_NOT_VALID_FP));
            assertNull(FP_SRC_FILE_NOT_VALID_FP+ "is not a valid functional point!", fp);
        }catch (FileNotFoundException fnfe){
            fail("File should exist!" + " "+fnfe.getMessage());
        }
    }

    private void validateFunctionalPoint(FunctionalPoint fp){
        assertNotNull("Functional Point Bean shouldn't be null", fp);
        assertEquals("FP Author", "Christian Hargraves", fp.getAuthor());
        assertEquals("FP tag name", FP_TAG_NAME, fp.getDefaultTagName());
        assertEquals("FP description", "Tests Javadoc2Bean.\nSome more text", fp.getDescription());
        assertEquals("FP short description", "Tests Javadoc2Bean.", fp.getShortDescription());
        assertEquals("FP className", FP_CLASS_NAME, fp.getClassName());
        assertEquals("FP Type", "action", fp.getType());
        //MockFunctionTag is not in the source path
        assertTrue("Number of FP Attributes should be greate than 3", fp.getAttributes().size() > 3);
        Attribute attr = (Attribute)fp.getAttribute("testVar1");
        assertEquals("FP Attribute Context Name #1","j2bDummy1Var1" , attr.getContextName());
        assertEquals("FP Attribute Name #1","testVar1" , attr.getName());
        assertEquals("FP Attribute Description #1","Some variable that does something" , attr.getDescription());
        assertTrue("FP Attribute Required #1",attr.isRequired());
        assertEquals("Number of FP Steps", 2, fp.getSteps().size());
        assertEquals("FP Step #1","Do something" , fp.getSteps().get(0));
        assertEquals("FP Step #2","안녕하세요 [Korean Characters]" , fp.getSteps().get(1));
    }


    public void testGetClassNameFromSourceValidSource(){
        String sourceFileName = "net"+File.separator+"sf"+File.separator+"jameleon"+File.separator+"TestCaseTag.java";
        assertEquals("Class name from source file:", "net.sf.jameleon.TestCaseTag", j2b.getClassNameFromSource(sourceFileName));
    }

    public void testAddClassDocs() throws FileNotFoundException {
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY2_CLASS);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addClassDocs(fp, clss);
        assertEquals("FP Author", "ABC", fp.getAuthor());
        assertEquals("FP tag name", "joe-schmoe" , fp.getDefaultTagName());
        assertEquals("FP description", "Joe Schmoe", fp.getDescription());
        assertEquals("FP Type", "validation", fp.getType());
    }

    public void testAddSteps() throws FileNotFoundException {
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY2_CLASS);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addSteps(fp, clss);
        assertEquals("Number of FP Steps", 3, fp.getSteps().size());
        assertEquals("FP Step #1","Find the girl" , fp.getSteps().get(0));
        assertEquals("FP Step #2","Attempt to date the girl" , fp.getSteps().get(1));
        assertEquals("FP Step #2","Beg" , fp.getSteps().get(2));
    }

    public void testAddMethodsNoAddFields() throws FileNotFoundException {
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY2_CLASS);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addMethods(fp, clss);
        assertEquals("Number of FP Attributes", 1, fp.getAttributes().size());
        Attribute attr = (Attribute)fp.getAttributes().get("joeSchmoeSmile");
        assertEquals("FP Attribute Name #1","joeSchmoeSmile" , attr.getName());
        assertEquals("FP Attribute Description #1", "Joe's smile", attr.getDescription());
        assertTrue("FP Attribute should be required", attr.isRequired());
        assertEquals("FP Attribute Type #1","java.lang.String", attr.getType());
        assertEquals("FP Attribute Field Default Value #1","happy", attr.getDefaultValue());
    }

    public void testAddMethodsAddFields() throws FileNotFoundException {
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY3_CLASS);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addFields(fp, clss);
        j2b.addMethods(fp, clss);
        assertEquals("Number of FP Attributes", 3, fp.getAttributes().size());
        Attribute attr = (Attribute)fp.getAttributes().get("brain");
        assertEquals("FP Attribute Name #1","brain" , attr.getName());
        assertEquals("FP Attribute Description #1", "Joe's brain method", attr.getDescription());
        assertTrue("FP Attribute should be required", attr.isRequired());
        assertEquals("FP Attribute Type #1","java.lang.String", attr.getType());
        assertEquals("FP Attribute Field Default Value #1","not happy", attr.getDefaultValue());
        assertFalse("FP Attribute Field isInstaceVariable #1",attr.isInstanceVariable());
        assertEquals("FP Attribute Field contextName #1","ifIOnlyHadA", attr.getContextName());
    }

    public void testAddFields() throws FileNotFoundException {
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY2_CLASS);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addFields(fp, clss);
        assertEquals("Number of FP Attributes", 2, fp.getAttributes().size());
        Attribute attr = (Attribute)fp.getAttributes().get("brain");
        assertEquals("FP Attribute Name #1", "brain" , attr.getName());
        assertEquals("FP Attribute Name #1", "ifIOnlyHadA" , attr.getContextName());
        assertEquals("FP Attribute Description #1", "Joe's brain", attr.getDescription());
        assertFalse("FP Attribute should NOT be required", attr.isRequired());
        assertEquals("FP Attribute Type #1","java.lang.String", attr.getType());
        attr = (Attribute)fp.getAttributes().get("iq");
        assertEquals("FP Attribute Name #1", "joesHighIq" , attr.getContextName());
        assertEquals("FP Attribute Name #1", "iq" , attr.getName());
        assertEquals("FP Attribute Description #1", "Joe's IQ", attr.getDescription());
        assertTrue("FP Attribute should be required", attr.isRequired());
        assertEquals("FP Attribute Type #1","byte", attr.getType());
        assertEquals("FP Attribute Field Default Value #1","75", attr.getDefaultValue());
    }

    public void testInheritance() throws FileNotFoundException{
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY2_SUB_CLASS);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addClassDocs(fp, clss);
        assertEquals("FP Author", "CBS", fp.getAuthor());
        assertEquals("FP tag name", "young-joe-schmoe" , fp.getDefaultTagName());
        assertEquals("FP description", "Joe Schmoe Junior", fp.getDescription());
        assertEquals("FP Type", "validation", fp.getType());

        j2b.addSteps(fp, clss);
        assertEquals("Number of FP Steps", 2, fp.getSteps().size());
        assertEquals("FP Step #1","Go to school" , fp.getSteps().get(0));
        assertEquals("FP Step #2","Look for girls" , fp.getSteps().get(1));

        j2b.addMethods(fp, clss);
        j2b.addFields(fp, clss);
        assertEquals("Number of FP Attributes", 3, fp.getAttributes().size());
        Attribute attr = (Attribute)fp.getAttributes().get("joeSchmoeSmile");
        assertEquals("FP Attribute Name #1","joeSchmoeSmile" , attr.getName());
        assertEquals("FP Attribute Description #1", "Joe's smile", attr.getDescription());
        assertTrue("FP Attribute should not be required", attr.isRequired());
        assertEquals("FP Attribute Type #1","java.lang.String", attr.getType());

        attr = (Attribute)fp.getAttributes().get("brain");
        assertEquals("FP Attribute Name #2", "ifIOnlyHadA" , attr.getContextName());
        assertEquals("FP Attribute Name #2", "brain" , attr.getName());
        assertEquals("FP Attribute Description #2", "Joe's brain", attr.getDescription());
        assertFalse("FP Attribute should NOT be required", attr.isRequired());
        assertEquals("FP Attribute Type #1","java.lang.String", attr.getType());

        attr = (Attribute)fp.getAttributes().get("iq");
        assertEquals("FP Attribute Name #1", "iq" , attr.getName());
        assertFalse("FP Attribute should be required", attr.isRequired());
        assertEquals("FP Attribute Type #1","byte", attr.getType());
        j2b.addDocsFromSerializedFP(fp,clss);
        attr = (Attribute)fp.getAttributes().get("functionId");
        assertNotNull("key 'functionId' should be a valid attribute", attr);
        attr = (Attribute)fp.getAttribute("functionId");
        assertNotNull("functionId should be a valid attribute", attr);
    }

    public void testMultipleTagNames() throws FileNotFoundException  {
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY2_SUB_CLASS);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addClassDocs(fp, clss);
        assertEquals("Number of FP tags", 2, fp.getTagNames().size());
        assertEquals("Tag 0", "young-joe-schmoe", fp.getDefaultTagName());
        assertEquals("Tag 1", "joe-schmoe-jr", (String)fp.getTagNames().get(1));
    }

    public void testGetFunctionalPointFromSerializedFile() throws FileNotFoundException{
        //This must be a file that doesn't exist in the source directory, yet
        //it has been serialized.
        JavaClass clss = j2b.getJavaClassWithClass("net.sf.jameleon.plugin.junit.SimpleFunction");
        FunctionalPoint fp = j2b.getFunctionalPointFromSerializedFile(clss);
        assertNotNull("Every Functional Point should have a functionId",fp.getAttribute("functionId"));
        assertEquals("# of cached serialized FunctionalPoints", 1, j2b.serializedFiles.size());
    }

    public void testAddDocsFromSerializedFP() throws FileNotFoundException{
        JavaClass clss = j2b.getJavaClassWithClass("net.sf.jameleon.plugin.junit.SimpleFunction");
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addFields(fp, clss);
        j2b.addDocsFromSerializedFP(fp, clss);
        assertNotNull("Every Functional Point should have a functionId",fp.getAttribute("functionId"));
    }

    public void testInheritanceNoSource() throws FileNotFoundException{
        JavaClass clss = j2b.getJavaClassWithClass("net.sf.jameleon.plugin.junit.SimpleFunctionWithTwoVars");
        FunctionalPoint fp = j2b.getFunctionalPoint(clss);
        assertNotNull("Every Functional Point should have a functionId",fp.getAttribute("functionId"));
    }

    public void testAddApplicationsSingleApplication() throws FileNotFoundException{
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY_WITH_APPLICATION);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addApplications(fp, clss);
        assertEquals("# of applications", 1, fp.getApplications().size());
        assertEquals("application name", "foo_app", fp.getApplications().get(0));
    }

    public void testAddApplicationsMultipleApplications() throws FileNotFoundException{
        JavaClass clss = j2b.getJavaClassWithClass(DUMMY_WITH_APPLICATIONS);
        FunctionalPoint fp  = new FunctionalPoint();
        j2b.addApplications(fp, clss);
        assertEquals("# of applications", 3, fp.getApplications().size());
        assertEquals("application name", "foo_app", fp.getApplications().get(0));
        assertEquals("application name", "bar_app", fp.getApplications().get(1));
        assertEquals("application name", "foo_bar_app", fp.getApplications().get(2));
    }
    
}

