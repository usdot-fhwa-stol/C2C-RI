/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2007 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestSuite;
import net.sf.jameleon.bean.TestCase;
import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.event.TestTestCaseListener;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.util.AssertLevel;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.StateStorer;
import net.sf.jameleon.result.TestCaseResult;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.LocationAware;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

public class TestCaseTagTest extends junit.framework.TestCase {

    private TestCaseTag tct;
    private static final String TEST = "test";
    private static final String DEV = "dev";
    private static final String LOCALHOST_9123 = "http://localhost:9123/";
    private static final String LOCALHOST_8081 = "http://localhost:8081/";

    private Properties props;
    private AssertLevel al;

    private static final String JUNIT = "junit";
    private static final String APP_NAME = "appName";
    private static final String JUNIT1 = "junit1";
    private static final String SF = "sf";
    private static String[] valid = {"SMOKE","LEVEL1","LEVEL2",
                                          "FUNCTION","LEVEL4","LEVEL5",
                                          "REGRESSION","LEVEL7","LEVEL8",
                                          "ACCEPTANCE",
                                          "smoke","level1","level2",
                                          "function","level4","level5",
                                          "regression","level7","level8",
                                          "acceptance"};
    private static String[] invalid = {"Foobar","LEVEL0","LEVEL10","LEVEL20"};
    private static final String NON_EXISTENT_CONF = "jameleon.nowaythisexists";
    private static final String UNIT_TEST_CONF = "tst/res/jameleon.unittest";
    private static final String NO_FAIL_CONF = "tst/res/jameleon.noFailureOnCSVFileNotFound";

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestCaseTagTest.class );
    }

    public TestCaseTagTest( String name ) {
        super( name );
    }

    public void setUp() throws Exception{
        tct = new TestCaseTag();
        tct.setUpDataDrivable();
        tct.init();
        try{
            tct.setContext(new JellyContext());
        }catch (JellyTagException jte){
            System.err.println(jte);
        }
        al = AssertLevel.getInstance();
        al.clearAll();
        Configurator.clearInstance();
    }

    public void tearDown(){
        Configurator.clearInstance();
    }

    public void testGetResultsFile(){
        tct.setName("name");
        assertNull("results file should be null", tct.resultsFile);
        tct.setGenTestCaseDocs(false);
        assertNull("results file should be null", tct.getResultsFile());
        tct.setGenTestCaseDocs(true);
        File f = tct.getResultsFile();
        assertTrue("results file path " + f.getPath(),f.getPath().endsWith("jameleon_test_results"+File.separator+"name"+File.separator+"index.html"));
        assertEquals("results file", f, tct.resultsFile);
    }

    public void testExecuteDrivableRow() throws Exception{
    	MockTestCase2Tag mTct = new MockTestCase2Tag();
        mTct.setCountRow(true);
    	mTct.setContext(new JellyContext());
    	mTct.initResults();
    	assertEquals("verify default value",-1, mTct.invokeChildrenRowNum);
    	assertNull("verify default value", mTct.mockTagDDResult);
    	Map vars = new HashMap();
    	vars.put("var1", "val1");
    	vars.put("var2", "val2");
    	vars.put("var3", "val3");
        mTct.setName("some name");
        mTct.addVariablesToRowData(vars);
    	mTct.executeDrivableRow(2);
    	assertEquals("rowNum after call", 2, mTct.invokeChildrenRowNum);
    	assertNotNull("dd result after call", mTct.mockTagDDResult);
    	assertTrue("dd result should be the same", mTct.ddRowResult == mTct.mockTagDDResult);
    	assertEquals("row number", 2, mTct.ddRowResult.getRowNum());
    	Map rowData = mTct.ddRowResult.getRowData();
    	assertEquals("# of row variables", 3, rowData.size());
    	assertEquals("row variable 1", "val1", rowData.get("var1"));
    	assertEquals("row variable 2", "val2", rowData.get("var2"));
    	assertEquals("row variable 3", "val3", rowData.get("var3"));
    }

    public void testSetLocationAware(){
        LocationAware la = new JameleonScriptException();
        la.setColumnNumber(54);
        la.setLineNumber(12);
        la.setFileName("some file");

        tct.setColumnNumber(56);
        tct.setLineNumber(14);
        tct.setFileName("another file");
        tct.setLocationAware(la);

        assertEquals("line #", 12, la.getLineNumber());
        assertEquals("col #", 54, la.getColumnNumber());
        assertEquals("file name", "some file", la.getFileName());

        la.setColumnNumber(-1);
        //Only change the value of the line and column if the line number is -1
        tct.setLocationAware(la);
        assertEquals("line #", 12, la.getLineNumber());
        assertEquals("col #", -1, la.getColumnNumber());

        la.setLineNumber(-1);
        //Only change the value of the line and column if the line number is -1
        tct.setLocationAware(la);
        assertEquals("line #", 14, la.getLineNumber());
        assertEquals("col #", 56, la.getColumnNumber());
        assertEquals("file name", "some file", la.getFileName());

        la.setFileName(null);
        //Only change the value of the file name if it isn't set already
        tct.setLocationAware(la);
        assertEquals("line #", 14, la.getLineNumber());
        assertEquals("col #", 56, la.getColumnNumber());
        assertEquals("file name", "another file", la.getFileName());

    }

    public void testSubstituteKeyValuesWithStringValues(){
        tct.getRowData().put("someName", "some value");
        tct.getRowData().put("anotherName", "another ${someName}");
        tct.substituteKeyValues();
        assertEquals("some value", tct.getContext().getVariable("someName"));
        assertEquals("another some value", tct.getContext().getVariable("anotherName"));
    }

    public void testSubstituteKeyValuesWithObjectValue(){
        tct.getRowData().put("someName", new java.util.Date());
        tct.substituteKeyValues();
        assertTrue("If I got this far, it means everything worked as expected", true);
    }

    public void testSetUp() throws Exception{
        tct.setFileName("file:./tst/xml/acceptance/baseDir.xml");
        Configurator.getInstance().getValue("organization");
        String org = "testOrg";
        Configurator.getInstance().setValue("organization", org);
        tct.setUp();
        assertEquals("organization", org, tct.getContext().getVariable("organization"));
        assertEquals("organization", org, tct.getOrganization());
        TestCase testCase = tct.getTestCase();
        assertEquals("test-case-summary", "Tests the baseDir in testcase tag works properly", testCase.getSummary());
        assertEquals("organization", org, testCase.getOrganization());
        assertEquals("testEnironment", "test", testCase.getTestEnvironment());
        assertEquals("name", "baseDir", testCase.getName());
    }

    public void testLoadPropsName() throws Exception{
        tct.setPropsName(APP_NAME);
        tct.setName("PropsName Test");
        tct.setUp();
        String value = (String)tct.getContext().getVariable("var1");
        assertEquals("var1 from appName.properties", "val1", value);
        tct.setOrganization("sf");
        tct.setUp();
        value = (String)tct.getContext().getVariable("var3");
        assertEquals("var1 from appName.properties", "val3", value);
    }

    public void testSetGetPropsName(){
        String PROPS_NAME = "test";
        tct.setPropsName(PROPS_NAME);
        assertEquals("propsName", PROPS_NAME, tct.getPropsName());
    }

    public void testSetBaseDir() {
        File f = new File("tst/_tmp");
        tct.setBaseDir(f);
        File baseDir = tct.getBaseDir();
        assertEquals("baseDir", f, baseDir);
        File resultsDir = tct.getResultsDir();

        assertTrue("resultsDir should begin with "+baseDir.getPath(), resultsDir.getAbsolutePath().startsWith(baseDir.getAbsolutePath()));
    }

    public void testTestCaseEventHandlerCalls() throws Exception{
        TestCaseEventHandler tceh = TestCaseEventHandler.getInstance();
        TestTestCaseListener listener = new TestTestCaseListener();
        tceh.addTestCaseListener(listener);

        //Test that the begin and end are called
        tct.setGenTestCaseDocs(false);
        
        tct.setExecuteTestCase(true);
        tct.setName("tct name");
        try{
            tct.doTag(XMLOutput.createDummyXMLOutput());
        }catch(Throwable t){
            //Don't care.
        }
        assertTrue("beginTestCase was not called!", listener.beginTestCaseCalled);
        assertTrue("endTestCase was not called!", listener.endTestCaseCalled);
        TestCaseTag tct2 = (TestCaseTag)listener.beginTestCaseEvent.getSource();
        assertEquals("tag name", "tct name", tct2.getName());
    }

    public void testAddValuesFromResourceBundle(){
        Properties p = new Properties();
        String startOfKey = "junit";
        try{
            ResourceBundle baseProps = ResourceBundle.getBundle("Applications");
            tct.addValuesFromResourceBundle(baseProps, p, startOfKey, false);
        }catch(MissingResourceException mre){
            //Do nothing since this file is optional
        }
        assertEquals("title variable", "Some title", p.getProperty("title"));
        startOfKey = "";
        try{
            ResourceBundle baseProps = ResourceBundle.getBundle("Applications");
            tct.addValuesFromResourceBundle(baseProps, p, startOfKey, false);
        }catch(MissingResourceException mre){
            //Do nothing since this file is optional
        }
        assertEquals("title variable", "Some title", p.getProperty("junit.title"));
    }

    public void testAddValuesFromResourceBundleWithOrg(){
        Properties p = new Properties();
        String startOfKey = "sf";
        try{
            ResourceBundle baseProps = ResourceBundle.getBundle(APP_NAME);
            tct.addValuesFromResourceBundle(baseProps, p, startOfKey, false);
        }catch(MissingResourceException mre){
            //Do nothing since this file is optional
        }
        assertEquals("var3 variable", "val3", p.getProperty("var3"));
    }

    public void testGetPropertiesForApplicationProperties(){
        tct.setTestEnvironment(null);
        props = tct.getPropertiesForApplication(APP_NAME);
        assertNotNull("The properties for the provided application.",props);
        assertNotNull("The value for the provided variable",props.getProperty("var1"));
        assertEquals("val1",props.getProperty("var1"));
    }

    public void testGetPropertiesForApplicationInEnv(){
        tct.setTestEnvironment(TEST);
        props = tct.getPropertiesForApplication(JUNIT);
        assertNotNull(props);
        assertNotNull(props.getProperty("baseUrl"));
        assertEquals(LOCALHOST_9123,props.getProperty("baseUrl"));
        assertNotNull(props.getProperty("beginAt"));
        assertEquals("/",props.getProperty("beginAt"));
    }

    public void testGetPropertiesForApplicationInEnv2() {
        tct.setTestEnvironment(DEV);
        props = tct.getPropertiesForApplication(JUNIT);
        assertNotNull(props);
        assertNotNull(props.getProperty("baseUrl"));
        assertEquals("http://www-dvlp.foo.com/",props.getProperty("baseUrl"));
        assertNotNull(props.getProperty("beginAt"));
        assertEquals("/jsp/start.jsp",props.getProperty("beginAt"));
    }

    public void testGetPropertiesForApplicationInEnvAndOrg(){
        tct.setTestEnvironment(TEST);
        tct.setOrganization(SF);
        props = tct.getPropertiesForApplication(JUNIT1);
        assertNotNull(props);
        assertNotNull(props.getProperty("baseUrl"));
        assertEquals(LOCALHOST_8081,props.getProperty("baseUrl"));
        assertNotNull(props.getProperty("beginAt"));
        assertEquals("/jsp",props.getProperty("beginAt"));
    }

    public void testGetPropertiesForApplicationInEnvAndOrgWithInvalidAppName(){
        tct.setTestEnvironment(TEST);
        tct.setOrganization(SF);
        boolean test = true;
        try{
            props = tct.getPropertiesForApplication("SomeNonExistentAppName");
        }catch(NullPointerException e){
            test = false;
        }
        assertTrue("Shouldn't have failed as properties are not required.",test);
    }

    public void testGetPropertiesForApplicationInEnvAndApp() {
        tct.setTestEnvironment(DEV);
        props = tct.getPropertiesForApplication(JUNIT);
        assertNotNull(props);
        assertNotNull(props.getProperty("baseUrl"));
        assertEquals("http://www-dvlp.foo.com/",props.getProperty("baseUrl"));
        assertNotNull(props.getProperty("beginAt"));
        assertEquals("/jsp/start.jsp",props.getProperty("beginAt"));
        assertEquals("Some title", props.getProperty("title"));
        assertEquals("Jameleon Rocks", props.getProperty("pageTextOnSomeDumbPage"));
    }

    public void testGetPropertiesForApplicationInEnvAndOrgAndApp() {
        tct.setTestEnvironment(TEST);
        tct.setOrganization("jmln");
        props = tct.getPropertiesForApplication("default");
        assertNotNull(props);
        assertNotNull(props.getProperty("baseUrl"));
        assertEquals("http://www-dvlp.foo.com/",props.getProperty("baseUrl"));
        assertEquals("Another title", props.getProperty("title"));
        assertEquals("Jameleon Is Cool!", props.getProperty("pageTextOnSomeDumbPage"));
    }

    public void testValidateAttributesValid() {
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        tct.setOrganization("nsbank");
        try{
            tct.validateAttributes();
        }catch(MissingAttributeException e){
            fail("Passed Valid Attributes, but still failed:\n"+e.toString());
        }
        assertEquals(DEV,tct.getTestEnvironment());
    }

    public void testValidateAttributesNoName() {
        tct.setTestEnvironment("foo");
        boolean error = false;
        try{
            tct.validateAttributes();
        }catch(MissingAttributeException e){
            error = true;
        }
        assertTrue("Didn't pass in a name, but no error thrown.",error);
    }

    public void testValidateAttributesInvalidGreaterThanLevel() {
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        for (int i = 0; i < invalid.length; i++) {
            tct.setAssertGreaterThanLevel(invalid[i]);
            boolean error = false;
            try{
                tct.validateAttributes();
            }catch(MissingAttributeException e){
                error = true;
            }
            assertTrue(invalid[i] + " is an invalid assertGreaterThanLevel, but no error thrown.",error);
        }
    }

    public void testValidateAttributesValidGreaterThanLevel() {
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        for (int i = 0; i < valid.length; i++) {
            tct.setAssertGreaterThanLevel(valid[i]);
            try{
                tct.validateAttributes();
            }catch(MissingAttributeException e){
                fail(valid[i] + " is a valid level, but still failed "+ e.toString());
            }
            assertEquals(valid[i],tct.getAssertGreaterThanLevel());
        }
    }

    public void testValidateAttributesInvalidLessThanLevel(){
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        for (int i = 0; i < invalid.length; i++) {
            tct.setAssertLessThanLevel(invalid[i]);
            boolean error = false;
            try{
                tct.validateAttributes();
            }catch(MissingAttributeException e){
                error = true;
            }
            assertTrue(invalid[i] + " is an invalid assertLessThanLevel, but no error thrown.",error);
        }
    }

    public void testValidateAttributesValidLessThanLevel() {
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        for (int i = 0; i < valid.length; i++) {
            tct.setAssertLessThanLevel(valid[i]);
            try{
                tct.validateAttributes();
            }catch(MissingAttributeException e){
                fail(valid[i] + " is a valid level, but still failed "+ e.toString());
            }
            assertEquals(valid[i],tct.getAssertLessThanLevel());
        }
    }

    public void testValidateAttributesInvalidLevel() {
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        for (int i = 0; i < invalid.length; i++) {
            tct.setAssertLevel(invalid[i]);
            boolean error = false;
            try{
                tct.validateAttributes();
            }catch(MissingAttributeException e){
                error = true;
            }
            assertTrue(invalid[i] + " is an invalid assertLevel, but no error thrown.",error);
        }
    }

    public void testValidateAttributesValidLevel() {
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        for (int i = 0; i < valid.length; i++) {
            tct.setAssertLevel(valid[i]);
            try{
                tct.validateAttributes();
            }catch(MissingAttributeException e){
                fail(valid[i] + " is a valid level, but still failed "+ e.toString());
            }
            assertEquals(valid[i],tct.getAssertLevel());
        }
    }

    public void testValidateAttributesInvalidLevels() {
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        String invalidStr = "Foobar,LEVEL0,LEVEL10,LEVEL20";
        tct.setAssertLevels(invalidStr);
        boolean error = false;
        try{
            tct.validateAttributes();
        }catch(MissingAttributeException e){
            error = true;
        }
        assertTrue(invalidStr + " is an invalid assertLevel, but no error thrown.",error);
        invalidStr = "Foobar,LEVEL0,LEVEL10,  LEVEL20";
        tct.setAssertLevels(invalidStr);
        error = false;
        try{
            tct.validateAttributes();
        }catch(MissingAttributeException e){
            error = true;
        }
        assertTrue(invalidStr + " is an invalid assertLevel, but no error thrown.",error);
    }

    public void testValidateAttributesValidLevels(){
        tct.setTestEnvironment(DEV);
        tct.setName("foo");
        String validStr = "SMOKE,LEVEL1,LEVEL2,FUNCTION,LEVEL4,LEVEL5,REGRESSION,LEVEL7,LEVEL8,ACCEPTANCE,level1";
        tct.setAssertLevels(validStr);
        try{
            tct.validateAttributes();
        }catch(MissingAttributeException e){
            fail(validStr + " is a valid level, but still failed "+ e.toString());
        }
        assertEquals(validStr,tct.getAssertLevels());
        validStr = "SMOKE,LEVEL1, LEVEL2, FUNCTION, LEVEL4, LEVEL5, REGRESSION, LEVEL7";
        tct.setAssertLevels(validStr);
        try{
            tct.validateAttributes();
        }catch(MissingAttributeException e){
            fail(validStr + " is a valid level, but still failed "+ e.toString());
        }
        assertEquals(validStr,tct.getAssertLevels());
    }

    public void testIsValidAssertLevelValidLevels(){
        for (int i=0; i < 10; i++) {
            assertTrue(valid[i]+ " should be a valid assert level!",tct.isValidateAssertLevel(valid[i]));
        }
    }

    public void testIsValidAssertLevelInValidLevels(){
        for (int i=0; i < invalid.length; i++) {
            assertFalse(invalid[i] + " should be an INVALID assert level!",tct.isValidateAssertLevel(invalid[i]));
        }
    }

    public void testSetAssertLevelsGreaterThanLevel(){
        for (int i=0; i < 10; i++) {
            tct.setAssertGreaterThanLevel(valid[i]);
            tct.setAssertLevels();
            assertNotPerformable(i-1);
            assertPerformable(i);
            assertPerformable(i+1);
        }
    }

    public void testSetAssertLevelsLessThanLevel(){
        for (int i=0; i < 10; i++) {
            tct.setAssertLessThanLevel(valid[i]);
            tct.setAssertLevels();
            assertPerformable(i-1);
            assertPerformable(i);
            assertNotPerformable(i+1);
        }
    }

    public void testSetAssertLevelsLevel(){
        for (int i=0; i < 10; i++) {
            tct.setAssertLevel(valid[i]);
            tct.setAssertLevels();
            assertNotPerformable(i-1);
            assertPerformable(i);
            assertNotPerformable(i+1);
        }
    }

    public void testGetAssertLevelFromString(){
        for (int i=0; i < 10; i++) {
            assertEquals(i,tct.getAssertLevelFromString(valid[i]));
        }
    }

    public void testSetMultipleAssertLevels() {
        tct.setAssertGreaterThanLevel("LEVEL8");
        tct.setAssertLessThanLevel("LEVEL1");
        tct.setAssertLevel("FUNCTION");
        tct.setAssertLevels("SMOKE, LEVEL5, REGRESSION");
        tct.setAssertLevels();
        assertPerformable(AssertLevel.SMOKE);
        assertPerformable(AssertLevel.LEVEL1);  
        assertNotPerformable(AssertLevel.LEVEL2);  
        assertPerformable(AssertLevel.FUNCTION);  
        assertNotPerformable(AssertLevel.LEVEL4);  
        assertPerformable(AssertLevel.LEVEL5);  
        assertPerformable(AssertLevel.REGRESSION);  
        assertNotPerformable(AssertLevel.LEVEL7);  
        assertPerformable(AssertLevel.LEVEL8);  
        assertPerformable(AssertLevel.ACCEPTANCE);  
    }

    public void testDefaultEnvironmentProperties() {
        tct.setName("nameOfTestCaseTag");
        tct.setTestEnvironment("foo");

        assertEquals("genTestCaseDocsEncoding", "UTF-8", tct.getGenTestCaseDocsEncoding());
        assertEquals("trace", false, tct.getTrace());  // should be isTrace() ? 
        assertEquals("genTestCaseDocs", true, tct.genTestCaseDocs);
        assertEquals("executeTestCase", true, tct.isExecuteTestCase());
        assertEquals("baseDir", ".", tct.getBaseDir().getPath());
        assertEquals("csvFile", JameleonUtility.fixFileSeparators("./data/foo/nameOfTestCaseTag.csv"), tct.getCsvFile().getPath());
        assertTrue("resultsDir ", tct.getResultsDir().getPath().endsWith(JameleonUtility.fixFileSeparators("jameleon_test_results/"+tct.getName())));
        assertNull("csvCharset should be null "+tct.getCsvCharset(), tct.getCsvCharset());
        assertEquals("enableSslCertChecking", true, tct.isEnableSslCertCheck());
        assertEquals("failOnCSVFileNotFound", true, tct.failOnCSVFileNotFound);
    }

    public void testLoadEnvironmentProperties(){
        JameleonUtility.createDirStructure(new File("tst/_tmp/csv"));
        JameleonUtility.createDirStructure(new File("tst/_tmp/results"));
        JameleonUtility.createDirStructure(new File("tst/_tmp/source"));
        JameleonUtility.createDirStructure(new File("tst/_tmp/xsl"));

        tct.setName("nameOfTestCaseTag");
        tct.setJameleonConfigName(UNIT_TEST_CONF);
        tct.loadJameleonConfig();
        tct.setAssertLevels();

        assertEquals("testEnvironment", "test", tct.getTestEnvironment());
        assertEquals("organization", "sanders", tct.getOrganization());
        assertEquals("assertGreaterThanLevel", AssertLevel.SMOKE, al.getGreaterThanLevel());
        assertEquals("assertLessThanLevel", AssertLevel.REGRESSION, al.getLessThanLevel());
        assertEquals("assertLevel", AssertLevel.ACCEPTANCE, al.getLevel());
        Set levels = al.getLevels();
        assertEquals("assertLevels should have 5 entries", 5, levels.size());
        assertTrue("assertLevels should contain SMOKE", levels.contains(new Integer(AssertLevel.SMOKE)));
        assertTrue("assertLevels should contain LEVEL1", levels.contains(new Integer(AssertLevel.LEVEL1)));
        assertTrue("assertLevels should contain FUNCTION", levels.contains(new Integer(AssertLevel.FUNCTION)));
        assertTrue("assertLevels should contain LEVEL4", levels.contains(new Integer(AssertLevel.LEVEL4)));
        assertTrue("assertLevels should contain ACCEPTANCE", levels.contains(new Integer(AssertLevel.ACCEPTANCE)));
        assertEquals("bugTrackerUrl", "this is my bug tracker url", tct.getBugTrackerUrl());
        assertEquals("genTestCaseDocsEncoding", "KFC-XC", tct.getGenTestCaseDocsEncoding());
        assertEquals("trace", true, tct.getTrace());  // should be isTrace() ? 
        assertEquals("genTestCaseDocs", false, tct.genTestCaseDocs);
        assertEquals("executeTestCase", false, tct.isExecuteTestCase());
        assertEquals("baseDir", JameleonUtility.fixFileSeparators("tst/_tmp"), tct.getBaseDir().getPath());
        assertEquals("csvDir", JameleonUtility.fixFileSeparators("tst/_tmp/csv/test/sanders/"), tct.getCsvDir().getPath());
        assertEquals("csvFile", JameleonUtility.fixFileSeparators("tst/_tmp/csv/test/sanders/nameOfTestCaseTag.csv"), tct.getCsvFile().getPath());
        assertEquals("resultsDir", JameleonUtility.fixFileSeparators("tst/_tmp/results/nameOfTestCaseTag"), tct.getResultsDir().getPath());
        assertEquals("csvCharset", "KFC", tct.getCsvCharset());
        tct.setStateStoreOptions();
        assertEquals("valid storeStateEvent", StateStorer.ON_STATE_CHANGE_EVENT, tct.getStateStorer().getStorableEvent());
        assertEquals("enableSslCertChecking", false, tct.isEnableSslCertCheck());
        assertEquals("failOnCSVFileNotFound", true, tct.failOnCSVFileNotFound);
    }

    public void testDoTagNonExistentCsvFile(){
        tct.setTestEnvironment(DEV);
        tct.setJameleonConfigName(NON_EXISTENT_CONF);
        tct.setName("NonExistentCsvFile");
        tct.setOrganization("sf");
        tct.setGenTestCaseDocs(false);
        tct.setUseCSV(true);
        String jellyTagException = "";
        try{
            tct.doTag(XMLOutput.createDummyXMLOutput());
            fail("An exception should have been thrown.");
        }catch (JellyTagException jte){
            jellyTagException = jte.getMessage();
        }
        assertTrue("Error message should say the csv file does not exist, but was: " + jellyTagException, 
                   jellyTagException.indexOf(JameleonUtility.fixFileSeparators("./data/dev/sf/NonExistentCsvFile.csv")) > -1);
    }

    public void testDoTagNonExistentCsvFileFailOnCSVFileNotFoundFalse() {
        tct.setTestEnvironment(DEV);
        tct.setJameleonConfigName(NON_EXISTENT_CONF);
        tct.setName("foo_no_way_this_exits00324");
        tct.setFailOnCSVFileNotFound(false);
        tct.setGenTestCaseDocs(false);
        tct.setUseCSV(true);
        try{
            tct.doTag(XMLOutput.createDummyXMLOutput());
        }catch(Exception e){
            fail("An exception should not have been thrown when failOnCSVFileNotFound was disabled.");
        }
        assertTrue("a dummy assert", true);
    }
    
    public void testDoTagNonExistentCsvFileFailOnCSVFileNotFoundFromEnvironment() throws JellyTagException{
        tct.setJameleonConfigName(NO_FAIL_CONF);
        tct.setName("foo_no_way_this_exits00324");
        tct.setUseCSV(true);
        try{
            tct.doTag(XMLOutput.createDummyXMLOutput());
        }catch(RuntimeException rte){
            rte.printStackTrace();
            fail("An exception should not have been thrown when failOnCSVFileNotFound was disabled.");
        }
        assertTrue("a dummy assert", true);
    }

    public void testPropertiesSubstitution() throws JellyTagException{
        tct.setJameleonConfigName(NON_EXISTENT_CONF);
        tct.setTestEnvironment(DEV);
        JellyContext context = new JellyContext();
        tct.setContext(context);
        tct.getPropertiesForApplication("props");

        assertEquals("world",(String)context.getVariable("audience"));
        assertEquals("hello",(String)context.getVariable("hi"));
        assertEquals("hello world!",(String)context.getVariable("msg"));
    }

    public void testPropertiesSubstitutionTwoTimes() throws JellyTagException{
        tct.setJameleonConfigName(NON_EXISTENT_CONF);
        tct.setTestEnvironment(DEV);
        JellyContext context = new JellyContext();
        tct.setContext(context);
        tct.getPropertiesForApplication("props3");

        assertEquals("Yoyo Yeahyeah ",(String)context.getVariable("entire"));

        String part3 = "uhhuh";
        context.setVariable("part3", part3);
        tct.getRowData().put("part3", part3);
        tct.getPropertiesForApplication("props3");

        assertEquals("Yoyo Yeahyeah uhhuh",(String)context.getVariable("entire"));
    }

    public void testGetApplicationsPropertiesNullValue() throws JellyTagException{
        tct.setJameleonConfigName(NON_EXISTENT_CONF);
        tct.setTestEnvironment(DEV);
        JellyContext context = new JellyContext();
        tct.setContext(context);
        String part3 = "uhhuh";
        context.setVariable("part3", part3);
        tct.getRowData().put("part3", null);

        try{
            tct.getPropertiesForApplication("props3");
        }catch(NullPointerException npe){
            fail("NullPointerException should not have been thrown");
        }
    }

    public void testPropertiesSubstitutionWithOrgandNoOrgOneEnvAppsProps() throws JellyTagException{
        tct.setJameleonConfigName(NON_EXISTENT_CONF);
        tct.setTestEnvironment(DEV);
        tct.setOrganization("someOrg");
        tct.setName("propertiesSub");
        tct.setGenTestCaseDocs(false);
        JellyContext context = new JellyContext();
        tct.setContext(context);
        tct.loadJameleonConfig();
        tct.getPropertiesForApplication("prop2");

        assertEquals("dude",(String)context.getVariable("end"));
        assertEquals("yo",(String)context.getVariable("begin"));
        assertEquals("yo dude",(String)context.getVariable("sub"));
    }

    public void testPropertiesSubstitutionWithOrgandNoOrgBothEnvAppsProps() throws JellyTagException{
        tct.setJameleonConfigName(NON_EXISTENT_CONF);
        tct.setTestEnvironment(DEV);
        tct.setOrganization("someOrg");
        tct.setName("propertiesSub");
        tct.setGenTestCaseDocs(false);
        JellyContext context = new JellyContext();
        tct.setContext(context);
        tct.loadJameleonConfig();
        tct.getPropertiesForApplication("prop2");

        assertEquals("dude",(String)context.getVariable("end"));
        assertEquals("yo",(String)context.getVariable("begin"));
        assertEquals("yo, you are a dude!",(String)context.getVariable("sub2"));
    }

    public void testLoadApplicationPropertiesNoFile() throws Exception{
        tct.setTestEnvironment("some_env_set");
        tct.setName("propertiesSub");
        tct.setGenTestCaseDocs(false);
        JellyContext context = new JellyContext();
        tct.setContext(context);
        tct.loadJameleonConfig();
        tct.loadApplicationProperties();
        //Made it this far and no exception was thrown.
        assertTrue(true);
    }

    public void testGetCsvFileWithOrgAndTestEnv() {
        tct.setTestEnvironment("some_env_set");
        tct.setOrganization("sf");
        tct.setName("propertiesSub");
        File f = tct.getCsvFile();
        assertEquals("Csv File: ",JameleonUtility.fixFileSeparators("./data/some_env_set/sf/propertiesSub.csv"),f.getPath());
    }

    public void testGetCsvFileWithTestEnv(){
        tct.setTestEnvironment("some_env_set");
        tct.setName("propertiesSub");
        File f = tct.getCsvFile();
        assertEquals("Csv File: ",JameleonUtility.fixFileSeparators("./data/some_env_set/propertiesSub.csv"),f.getPath());
    }

    public void testGetCsvFileWithOrg(){
        tct.setOrganization("sf");
        tct.setName("propertiesSub");
        File f = tct.getCsvFile();
        assertEquals("Csv File: ",JameleonUtility.fixFileSeparators("./data/sf/propertiesSub.csv"),f.getPath());
    }

    public void testGetCsvDirWithNoOrgAndNoEnv() {
        tct.setName("propertiesSub");
        File f = tct.getCsvDir(true);
        assertEquals("Csv File: ",JameleonUtility.fixFileSeparators("./data/"),f.getPath());
    }

    public void testGetCsvDirWithOrgAndTestEnv(){
        tct.setTestEnvironment("some_env_set");
        tct.setOrganization("sf");
        File f = tct.getCsvDir(true);
        assertEquals("Csv File: ",JameleonUtility.fixFileSeparators("./data/some_env_set/sf/"),f.getPath());
    }

    public void testGetCsvDirWithTestEnv(){
        tct.setTestEnvironment("some_env_set");
        File f = tct.getCsvDir(true);
        assertEquals("Csv File: ",JameleonUtility.fixFileSeparators("./data/some_env_set/"),f.getPath());
    }

    public void testGetCsvDirWithOrg(){
        tct.setOrganization("sf");
        File f = tct.getCsvDir();
        assertEquals("Csv File: ",JameleonUtility.fixFileSeparators("./data/sf/"),f.getPath());
    }

    public void testGetCsvFileWithCsvFileName() {
        tct.setCsvFileName("some/dumb/dir/file.csv");
        assertEquals("Csv file", JameleonUtility.fixFileSeparators("./data/some/dumb/dir/file.csv"), tct.getCsvFile().getPath());
    }
    
    protected void assertNotPerformable(int assertLevel) {
        assertPerformable(assertLevel, false);
    }

    protected void assertPerformable(int assertLevel) {
        assertPerformable(assertLevel, true);
    }

    protected void assertPerformable(int assertLevel, boolean performable) {
        if (assertLevel >=0 && assertLevel < 10) {
            String message = performable ? " should " : " should not ";
            message = valid[assertLevel] + message + "be performable";
            assertEquals(message, performable, al.assertPerformable(assertLevel));
        }
    }

    public void testSetValueFromEnvironment(){
        Configurator config = Configurator.getInstance();
        config.setConfigName(NO_FAIL_CONF);
        String key = "testEnvironment";
        tct.setValueFromEnvironment(config, key);
        assertEquals(key, "dev", tct.getTestEnvironment());
        key = "failOnCSVFileNotFound";
        tct.setValueFromEnvironment(config, key);
        assertFalse(key, tct.failOnCSVFileNotFound);
    }

    public void testSetFileFromEnvironment(){
        Configurator config = Configurator.getInstance();
        config.setConfigName(NO_FAIL_CONF);
        String key = "csvDir";
        tct.setFileFromEnvironment(config, key);
        assertEquals(key, "etc", tct.getDataDir(false).getPath());
    }

}
