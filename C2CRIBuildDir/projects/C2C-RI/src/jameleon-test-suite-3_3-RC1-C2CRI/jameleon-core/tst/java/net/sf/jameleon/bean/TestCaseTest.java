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
package net.sf.jameleon.bean;

import java.io.File;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.XMLHelper;

public class TestCaseTest extends TestCase {

    private net.sf.jameleon.bean.TestCase testCase;
    private static final String NAME = "name";
    private static final String SUMMARY = "summary";
    private static final String AUTHOR = "author";
    private static final String APPLICATION = "application";
    private static final String ORGANIZATION = "organization";
    private static final String BUG1 = "bug 1";
    private static final String BUG2 = "bug 2";
    private static final String FP_TESTED = "fp1";
    private static final String TEST_LEVEL1 = "SMOKE";
    private static final String TEST_LEVEL2 = "REGRESSION";
    private static final String FILE = "file";
    private static final String ENCODING = "UTF-16";
    private static final String REQUIREMENT = "requirement";
    private static final String ID = "1234";
    private static final String ENVIRONMENT = "environment";


    private static final String TAG_NAME = "fp-test"; 
    private static final String DESC = "Does nothing"; 
    private static final String VALID_TYPE = "action"; 
    private static final String APPLICATION1 = "app1"; 
    private static final String APPLICATION2 = "app2"; 
    private static final String STEP1 = "do something"; 
    private static final String STEP2 = "do something else";

    private static final String ATTR_NAME1 = "Attr name 1"; 
    private static final String ATTR_NAME2 = "Attr name 2"; 

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( TestCaseTest.class );
    }

    public TestCaseTest( String name ) {
        super( name );
    }

    public void setUp(){
        testCase = new net.sf.jameleon.bean.TestCase();
        testCase.setName(NAME);
        testCase.setSummary(SUMMARY);
        testCase.setAuthor(AUTHOR);
        testCase.setApplication(APPLICATION);
        testCase.setFile(FILE);
        testCase.setEncoding(ENCODING);
        testCase.setOrganization(ORGANIZATION);
        testCase.setTestCaseId(ID);
        testCase.setTestCaseRequirement(REQUIREMENT);
        testCase.setFunctionalPointTested(FP_TESTED);
        testCase.setTestEnvironment(ENVIRONMENT);
        testCase.addTestLevel(TEST_LEVEL1);
        testCase.addTestLevel(TEST_LEVEL2);
        testCase.addBug(BUG1);
        testCase.addBug(BUG2);
        testCase.addSession(createSession(APPLICATION1));
        testCase.addSession(createSession(APPLICATION2));
    }

    public void tearDown(){
        testCase = null;
    }

    public void testGetScriptContents(){
        File f = new File("tst/xml/acceptance/testsuite.xml");
        String s = f.getAbsolutePath();
        //Jelly adds a '/' at the beginning under windows. Let's that it works
        testCase.setFile(File.separator+s);
        String scriptContents = testCase.getScriptContents();
        assertTrue("no > symbols should exist", scriptContents.indexOf(">") == -1);
        assertTrue("no < symbols should exist", scriptContents.indexOf("<") == -1);
    }

    public void testAddTestCaseLevel(){
    	assertEquals("# of test case levels", 2, testCase.getTestLevels().size());
    	testCase.addTestLevel(TEST_LEVEL1);
    	assertEquals("# of test case levels", 2, testCase.getTestLevels().size());
    }
    
    public void testReadFromScriptAllValuesSet(){
        testCase = new net.sf.jameleon.bean.TestCase();
        String testEnv = "lala";
        String organization ="fafa";
        Configurator config = Configurator.getInstance();
        config.setValue("organization", organization);
        config.setValue("testEnvironment", testEnv);

        String fName = "file:tst/res/testCase01.xml";
        testCase.readFromScript(fName);
        assertEquals("testEnvironment", testEnv, testCase.getTestEnvironment());
        assertEquals("organization", organization, testCase.getOrganization());
        assertEquals("test-case-author", "Christian", testCase.getAuthor());
        assertEquals("Total # of test-case-bugs", 2, testCase.getBugs().size());
        assertTrue("test-case-bugs #1", testCase.getBugs().contains("1"));
        assertTrue("test-case-bugs #2", testCase.getBugs().contains("2"));
        assertEquals("functional-point-tested", "JMLN", testCase.getFunctionalPointTested());
        assertEquals("testcase name", "tc name", testCase.getName());
        assertEquals("test-case-summary", "tc summary", testCase.getSummary());
        assertEquals("test-case-id", "tc id", testCase.getTestCaseId());
        assertEquals("test-case-requirement", "req #", testCase.getTestCaseRequirement());
        assertEquals("test-case-level", "SMOKE", testCase.getTestLevels().get(0));
        assertEquals("application-tested", "APP", testCase.getApplication());
        assertEquals("script file", "tst/res/testCase01.xml", testCase.getFile());
        List sessions = testCase.getSessions();
        //Even though two sessions are listed in the test case, there is no real way for me to know 
        //all of the types of session tags out. Therefore, the basic docs will not include the different
        //sessions and all functional points will go into a single session.
        assertEquals("# of sessions", 1, sessions.size());
        List fpList = ((Session)sessions.get(0)).getFunctionalPoints();
        assertEquals("# of functional points", 4, fpList.size());
    }

    public void testReadFromScriptNoTestname(){
        testCase = new net.sf.jameleon.bean.TestCase();
        String fName = "file:tst/res/testCase02.xml";
        testCase.readFromScript(fName);
        assertEquals("testcase name", "testCase02", testCase.getName());
    }

    public void testToXML(){
        String xml = testCase.toXML();
        XMLHelper xmlHelper = new XMLHelper(xml);
        assertTrue("encoding", xml.indexOf("encoding=\""+ENCODING+"\"") >= 0);
        assertEquals("name", NAME, xmlHelper.getValueFromXPath("/test-case/test-case-name"));
        assertEquals("application", APPLICATION, xmlHelper.getValueFromXPath("/test-case/application-tested"));
        assertEquals("summary", SUMMARY, xmlHelper.getValueFromXPath("/test-case/test-case-summary"));
        assertEquals("testEnvironment", ENVIRONMENT, xmlHelper.getValueFromXPath("/test-case/test-environment"));
        assertEquals("author", AUTHOR, xmlHelper.getValueFromXPath("/test-case/test-case-author"));
        assertEquals("functionalPointTested", FP_TESTED, xmlHelper.getValueFromXPath("/test-case/functional-point-tested"));
        assertEquals("testCaseId", ID, xmlHelper.getValueFromXPath("/test-case/test-case-id"));
        assertEquals("testCaseRequirement", REQUIREMENT, xmlHelper.getValueFromXPath("/test-case/test-case-requirement"));

        String value = xmlHelper.getValueFromXPath("/test-case/test-case-level[position()=1]");
        assertEquals("1st testCaseLevel", TEST_LEVEL1, value);
        value = xmlHelper.getValueFromXPath("/test-case/test-case-level[position()=2]");
        assertEquals("2nd testCaseLevel", TEST_LEVEL2, value);

        value = xmlHelper.getValueFromXPath("/test-case/test-case-bug[text()='"+BUG1+"']");
        assertNotNull("1st bug", value);
        value = xmlHelper.getValueFromXPath("/test-case/test-case-bug[text()='"+BUG2+"']");
        assertNotNull("2nd bug", value);

        value = xmlHelper.getValueFromXPath("/test-case/session[position()=1]/application");
        assertEquals("1st session app", APPLICATION1, value);
        value = xmlHelper.getValueFromXPath("/test-case/session[position()=2]/application");
        assertEquals("1st session app", APPLICATION2, value);
    }

    public void testGetSetOrganization(){
        assertEquals("organization", ORGANIZATION, testCase.getOrganization());
    }

    public void testGetSetTestEnvironment(){
        assertEquals("organization", ENVIRONMENT, testCase.getTestEnvironment());
    }

    private Session createSession(String app){
        Session s = new Session();
        s.setApplication(app);
        s.setOrganization(ORGANIZATION);

        FunctionalPoint fp = new FunctionalPoint();
        fp.setAuthor(AUTHOR);
        fp.addTagName(TAG_NAME);
        fp.setDescription(DESC);
        fp.setType(VALID_TYPE);
        fp.addApplication(APPLICATION1);
        fp.addApplication(APPLICATION2);
        fp.addStep(STEP1);
        fp.addStep(STEP2);
        Attribute attr1 = new Attribute();
        attr1.setName(ATTR_NAME1);
        Attribute attr2 = new Attribute();
        attr2.setName(ATTR_NAME2);
        fp.addAttribute(attr1);
        fp.addAttribute(attr2);
        s.addFunctionalPoint(fp);
        return s;
    }

}
