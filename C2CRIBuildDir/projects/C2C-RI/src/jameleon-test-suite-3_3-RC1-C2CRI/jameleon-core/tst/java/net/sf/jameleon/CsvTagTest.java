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
package net.sf.jameleon;

import java.io.File;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.function.MockFunctionTag;
import net.sf.jameleon.result.JameleonTestResult;
import net.sf.jameleon.result.TestCaseResult;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonUtility;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;

public class CsvTagTest extends TestCase {

    private CsvTag csvTag;
    private MockCsvTag mockCsvTag;
    private TestCaseTag testCaseTag;
    private JellyContext context;

    private static final String NON_EXISTENT_CONF = "jameleon.nowaythisexists";
    private static final String UNIT_TEST_CONF = "tst/res/jameleon.unittest";

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(CsvTagTest.class);
    }

    public CsvTagTest(String name) {
        super(name);
    }

    public void setUp() throws JellyTagException{
        context = new JellyContext();
        testCaseTag = new TestCaseTag();
        testCaseTag.setContext(context);
        testCaseTag.setUpDataDrivable();
        testCaseTag.setGenTestCaseDocs(false);
        testCaseTag.setName("some name");
        testCaseTag.init();
        csvTag = new CsvTag();
        csvTag.setContext(context);
        csvTag.setParent(testCaseTag);
        csvTag.setUpDataDrivable();
        csvTag.init();
        MockFunctionTag mft = new MockFunctionTag();
        mft.setParent(csvTag);
        mockCsvTag = new MockCsvTag();
        mockCsvTag.setContext(context);
        mockCsvTag.init();
        MockSessionTag mockST = new MockSessionTag();
        mockST.setParent(testCaseTag);
        mockCsvTag.setParent(mockST);
        mockCsvTag.setUpDataDrivable();

        Configurator.clearInstance();
    }

    public void testGetTraceKeyValuePairs(){
        HashMap vars = new HashMap();
        vars.put("key", "value");
        String keyValueS = csvTag.getTraceKeyValuePairs(vars.keySet(), vars);
        assertEquals("key value pairs trace msg", "key=>value: 'key'=>'value' ", keyValueS);
    }

    public void testGetNewStateStoreLocation(){
        csvTag.setName("csv_dir");
        String newLocation = csvTag.getNewStateStoreLocation(0);
        assertEquals("csv results dir", "csv_dir", JameleonUtility.fixFileSeparators(newLocation));
        newLocation = csvTag.getNewStateStoreLocation(1);
        assertEquals("csv results dir", JameleonUtility.fixFileSeparators("csv_dir/1"), JameleonUtility.fixFileSeparators(newLocation));
    }

    public void testGetCsvFileWithoutOrganization() {
        testCaseTag.setTestEnvironment("test");
        csvTag.setName("CsvTagTest");
        assertEquals("Csv file", JameleonUtility.fixFileSeparators("./data/test/CsvTagTest.csv"), csvTag.getCsvFile().getPath());
    }

    public void testGetCsvFileNoOrgNoEnv() {
        csvTag.setName("CsvTagTest");
        assertEquals("Csv file", JameleonUtility.fixFileSeparators("./data/CsvTagTest.csv"), csvTag.getCsvFile().getPath());
    }

    public void testGetCsvFileWithOrganization() {
        testCaseTag.setTestEnvironment("dev");
        testCaseTag.setOrganization("sourceforge");
        csvTag.setName("AnotherTest");
        assertEquals("Csv file", JameleonUtility.fixFileSeparators("./data/dev/sourceforge/AnotherTest.csv"), csvTag.getCsvFile().getPath());
    }
    
    public void testGetCsvFileWithCsvFileName() {
        csvTag.setCsvFileName("some/dumb/dir/file.csv");
        assertEquals("Csv file", JameleonUtility.fixFileSeparators("./data/some/dumb/dir/file.csv"), csvTag.getCsvFile().getPath());
    }
    
    public void testGetCsvFileCsvNameIsPath() {
        testCaseTag.setTestEnvironment("test");
        csvTag.setName("a/b/c/MyCsvFile");
        assertEquals("Csv file", JameleonUtility.fixFileSeparators("./data/test/a/b/c/MyCsvFile.csv"), csvTag.getCsvFile().getPath());
    }

    public void testGetCsvFileOverrideBaseDir() {
        testCaseTag.setTestEnvironment("test");
        testCaseTag.setBaseDir(new File("base"));
        csvTag.setName("MyCsvFile");
        assertEquals("Csv file", JameleonUtility.fixFileSeparators("base/data/test/MyCsvFile.csv"), csvTag.getCsvFile().getPath());
    }

    public void testGetCsvFileOverrideCsvDir() {
        testCaseTag.setTestEnvironment("test");
        testCaseTag.setBaseDir(new File("base"));
        csvTag.setName("MyCsvFile");
        testCaseTag.setCsvDir(new File("csv"));
        assertEquals("Csv file", JameleonUtility.fixFileSeparators("base/csv/test/MyCsvFile.csv"), csvTag.getCsvFile().getPath());
    }

    public void testFailOnCSVFileNotFound() throws Exception{
        testCaseTag.setTestEnvironment("test");
        testCaseTag.setGenTestCaseDocs(false);
        testCaseTag.results = new TestCaseResult(testCaseTag.getFunctionalPoint());
        csvTag = new MockCsvTag2();
        csvTag.setContext(context);
        csvTag.setParent(testCaseTag);
        csvTag.setUpDataDrivable();
        csvTag.init();

        csvTag.setName("_MyCsvFile_");
        csvTag.doTag(XMLOutput.createDummyXMLOutput());
        assertTrue("test case should be marked as failed", testCaseTag.getResults().failed());
        String errMsg = ((JameleonTestResult)testCaseTag.getResults().getFailedResults().get(0)).getError().getMessage();
        assertTrue("Message should be something about file not found, but was " + errMsg, errMsg.indexOf(JameleonUtility.fixFileSeparators("./data/test/_MyCsvFile_.csv")) >= 0);
    }

    public void testNoFailOnCSVFileNotFound() throws Exception{
        testCaseTag.setFailOnCSVFileNotFound(false);
        testCaseTag.setGenTestCaseDocs(false);
        testCaseTag.setUp();
        csvTag = new MockCsvTag2();
        csvTag.setContext(context);
        csvTag.setParent(testCaseTag);
        csvTag.setUpDataDrivable();
        csvTag.init();
        csvTag.setName("_MyCsvFile_");
        csvTag.doTag(XMLOutput.createDummyXMLOutput());
        assertTrue("TestCaseTag should have been updated that the CSV file was not found",testCaseTag.failedOnDataDriver);
    }

    public void testSubstituteCSVValuesParentSessionTag() throws Exception{
        testCaseTag.setJameleonConfigName(NON_EXISTENT_CONF);
        testCaseTag.setTestEnvironment("dev");
        testCaseTag.loadJameleonConfig();
        testCaseTag.loadApplicationProperties();
        testCaseTag.getPropertiesForApplication("props");
        testCaseTag.setCsvDir(new File("etc"));
        testCaseTag.setGenTestCaseDocs(false);
        testCaseTag.setUp();
        mockCsvTag.setName("substituteCSV_Value");
        mockCsvTag.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("cool", (String)context.getVariable("var1"));
        assertEquals("dude", (String)context.getVariable("var2"));
        assertEquals("hello cool dude!", (String)context.getVariable("var3"));
    }

    public void testSubstituteCSVValuesChildSessionTag() throws Exception{
        MockCsvTag mCsvTag = new MockCsvTag();
        mCsvTag.init();
        mCsvTag.setContext(context);
        mCsvTag.setParent(testCaseTag);
        testCaseTag.setJameleonConfigName(NON_EXISTENT_CONF);
        testCaseTag.setCsvDir(new File("etc"));
        testCaseTag.setTestEnvironment("dev");
        testCaseTag.loadJameleonConfig();
        testCaseTag.loadApplicationProperties();
        testCaseTag.getPropertiesForApplication("props");
        testCaseTag.setGenTestCaseDocs(false);
        testCaseTag.setUp();
        mCsvTag.setUpDataDrivable();
        mCsvTag.setName("substituteCSV_Value");
        mCsvTag.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("cool", (String)context.getVariable("var1"));
        assertEquals("dude", (String)context.getVariable("var2"));
        assertEquals("hello cool dude!", (String)context.getVariable("var3"));
    }

    public void testCsvCharset(){
        testCaseTag.setJameleonConfigName(UNIT_TEST_CONF);
        testCaseTag.loadJameleonConfig();
        csvTag.setupDataDriver();
        assertEquals("KFC", csvTag.getCsvCharset());
    }

    public void testSetVariablesInContext(){
        HashMap vars = new HashMap();
        vars.put("one", "1");
        vars.put("two", "2");
        vars.put("three", "3");
        mockCsvTag.setVariablesInContext(vars);
        assertEquals("context value for <one>", "1", context.getVariable("one"));
        assertEquals("context value for <two>", "2", context.getVariable("two"));
        assertEquals("context value for <three>", "3", context.getVariable("three"));

        vars.put("one", "1");
        vars.put("two", null);
        vars.put("three", "3");
        mockCsvTag.setVariablesInContext(vars);
        assertEquals("context value for <one>", "1", context.getVariable("one"));
        assertEquals("context value for <two>", null, context.getVariable("two"));
        assertEquals("context value for <three>", "3", context.getVariable("three"));
    }

    public void testExecuteDrivableRow() throws Exception{
        HashMap vars = new HashMap();
        vars.put("one", "1");
        vars.put("two", "2");
        vars.put("three", "3");

        testCaseTag.setUp();
        mockCsvTag.addVariablesToRowData(vars);
        mockCsvTag.setUpDataDrivable();
        mockCsvTag.executeDrivableRow(1);
        assertEquals("context value for <one>", "1", context.getVariable("one"));
        assertEquals("context value for <two>", "2", context.getVariable("two"));
        assertEquals("context value for <three>", "3", context.getVariable("three"));

        assertEquals("rowData value for <one>", "1", testCaseTag.getRowData().get("one"));
        assertEquals("rowData value for <two>", "2", testCaseTag.getRowData().get("two"));
        assertEquals("rowData value for <three>", "3", testCaseTag.getRowData().get("three"));
        mockCsvTag.destroyVariables(vars.keySet());
        vars.put("one", "1");
        vars.put("two", null);
        vars.put("three", "3");
        mockCsvTag.addVariablesToRowData(vars);
        mockCsvTag.executeDrivableRow(1);
        assertEquals("context value for <one>", "1", context.getVariable("one"));
        assertEquals("context value for <two>", null, context.getVariable("two"));
        assertEquals("context value for <three>", "3", context.getVariable("three"));

        assertEquals("rowData value for <one>", "1", testCaseTag.getRowData().get("one"));
        assertEquals("rowData value for <two>", null, testCaseTag.getRowData().get("two"));
        assertEquals("rowData value for <three>", "3", testCaseTag.getRowData().get("three"));
        mockCsvTag.destroyVariables(vars.keySet());
    }

    public void testDetFailedOnCurrentRow(){
        testCaseTag.setUpDataDrivable();
        testCaseTag.setFailedOnCurrentRow(true);
        csvTag.setUpDataDrivable();
        assertTrue("failedOnCurrentRow should be true", csvTag.getFailedOnCurrentRow());
    }

}
