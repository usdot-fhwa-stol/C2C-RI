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
package net.sf.jameleon.function;

import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.MockSessionTag;
import net.sf.jameleon.MockTestCaseTag;
import net.sf.jameleon.PostconditionTag;
import net.sf.jameleon.PreconditionTag;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.result.FunctionResult;
import net.sf.jameleon.util.Configurator;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

public class FunctionTagTest extends TestCase {

    protected MockFunctionTag ft;
    protected MockSessionTag st;
    protected MockTestCaseTag tct;

    protected static final String S_VAR = "s_var";
    protected static final String SL_VAR = "sl_var";
    protected static final String IL_VAR = "il_var";

    private static final String UNIT_TEST_CONF = "tst/res/jameleon.unittest";

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(FunctionTagTest.class);
    }

    public FunctionTagTest(String name) {
        super(name);
    }

    public void setUp(){
        ft = new MockFunctionTag();
        st = new MockSessionTag();
        tct = new MockTestCaseTag();
        st.setParent(tct);
        ft.setParent(st);
        tct.setUpDataDrivable();
        Configurator.clearInstance();
        Configurator config = Configurator.getInstance();
        config.setConfigName(UNIT_TEST_CONF);
        ft.setupEnvironment();
    }

    public void testRemoveFunctionResult(){
        assertTrue(st.getSessionResult().getChildrenResults().contains(ft.getFunctionResults()));
        ft.removeFunctionResult();
        assertFalse(st.getSessionResult().getChildrenResults().contains(ft.getFunctionResults()));
    }
    
    public void testSetUpFunctionResult(){
    	FunctionResult oldFr = ft.fResults;
    	st.getSessionResult().getChildrenResults().clear();
    	assertEquals("# of function results", 0, st.getSessionResult().getChildrenResults().size());
    	ft.setUpFunctionResults();
    	assertNotNull("function tag results should no longer be null", ft.fResults);
    	assertFalse("a new function result should have been created", oldFr == ft.fResults);
    	assertEquals("# of function results", 1, st.getSessionResult().getChildrenResults().size());
    }

    public void testGetParentTags(){
        PostconditionTag post = new PostconditionTag();
        post.setParent(st);
        ft.setParent(post);
        ft.tct = null;
        assertNull(ft.tct);
        assertFalse(ft.precondition);
        assertFalse(ft.postcondition);
        ft.getParentTags();
        assertTrue(tct == ft.tct);
        assertTrue(ft.postcondition);
        assertFalse(ft.precondition);

        PreconditionTag pret = new PreconditionTag();
        pret.setParent(st);
        ft.setParent(pret);
        ft.postcondition = false;
        ft.getParentTags();
        assertFalse(ft.postcondition);
        assertTrue(ft.precondition);
    }

    public void testSetupEnvironment(){
        assertFalse(ft.fResults.isPostcondition());
        assertFalse(ft.fResults.isPrecondition());
        ft.postcondition = true;
        ft.precondition = true;
        ft.setFunctionId("some function id");
        ft.setupEnvironment();
        assertTrue("parent result", st.getSessionResult() == ft.fResults.getParentResults());
        assertEquals(0, ft.params.size());
        assertEquals("some function id", ft.fResults.getTag().getFunctionId());
        assertTrue(ft.fResults.isPostcondition());
        assertTrue(ft.fResults.isPrecondition());
        assertEquals(200, ft.functionDelay);
    }

    public void testGetStringOrDefault(){
        String defaultS = "default";
        String value = "value";
        assertEquals(value, ft.getStringOrDefault(value, defaultS));
        value = "";
        assertEquals(value, ft.getStringOrDefault(value, defaultS));
        assertEquals(defaultS, ft.getStringOrDefault(null, defaultS));
        assertNull(ft.getStringOrDefault(null, null));
    }

    public void testAssertRegexMatches(){
        String textToMatch = "some text with 12345";
        String regex = "\\d+";
        ft.expectFailure = false;
        ft.assertRegexMatches(textToMatch, regex);
        regex = "(with (\\d+))";
        ft.assertRegexMatches(textToMatch, regex);
        regex = "no way";
        boolean exceptionThrown = false;
        try{
            ft.assertRegexMatches(textToMatch, regex);
        }catch(AssertionFailedError afe){
            exceptionThrown = true;
        }
        assertTrue("An exception should have been thrown", exceptionThrown);
    }

    public void testRegexMatches(){
        String textToMatch = "some text with 12345";
        String regex = "\\d+";
        assertTrue("The text to match should have a number sequence in it.", ft.regexMatches(textToMatch, regex));
        regex = "(with (\\d+))";
        assertTrue("The text to match should have a number sequence preceeded with 'with' in it.",ft.regexMatches(textToMatch, regex));
        regex = "no way";
        assertFalse("The regex should not match the provided text", ft.regexMatches(textToMatch, regex));
        regex = "";
        assertTrue(ft.regexMatches(textToMatch, regex));
        regex = null;
        boolean exceptionThrown = false;
        try{
            ft.regexMatches(textToMatch, regex);
        }catch(JameleonScriptException e){
           exceptionThrown = true;
        }
        assertTrue("An exception should have been thrown", exceptionThrown);
    }

    public void testGetMatchingRegexText(){
        String textToMatch = "some text with 12345";
        String regex = "[\\d]+";
        String matchingText = ft.getMatchingRegexText(textToMatch, regex, 0);
        assertEquals("Matching text with only one group", "12345", matchingText);
        regex = "(with (\\d+))";
        matchingText = ft.getMatchingRegexText(textToMatch, regex, 2);
        assertEquals("Matching text with two groups", "12345", matchingText);
        regex = "\\d+";
        boolean exceptionThrown = false;
        try{
            ft.getMatchingRegexText(textToMatch, regex, 1);
        }catch(JameleonScriptException e){
           exceptionThrown = true;
        }
        assertTrue("An exception should have been thrown", exceptionThrown);
    }

    public void testResetFunctionalPoint(){
        assertEquals("functionDelay", 200, ft.functionDelay);
        ft.resetFunctionalPoint();
        ft.getBroker().transferAttributes(ft.getContext());
        assertEquals("functionDelay", -1, ft.functionDelay);
    }

    public void testSetFunctionDelay(){
        ft.resetFunctionalPoint();
        ft.getBroker().transferAttributes(ft.getContext());
        ft.setupEnvironment();
        assertEquals("functionDelay set from Environment_unittest", 200, ft.functionDelay);
        ft.setFunctionDelay(1000l);
        ft.setupEnvironment();
        assertEquals("functionDelay set directly", 1000, ft.functionDelay);
    }

    public void testDoTagCallSimplifiedTestBlock(){
        tct.setGenTestCaseDocs(false);
        ft.functionId = "something";
        try {
            ft.doTag(XMLOutput.createDummyXMLOutput());
            assertTrue("The new simplified testBock should have been called.", ft.simplifiedTestBlockCalled);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception not expected, but thrown "+e.getMessage());
        }
    }

    public void testGetTestCaseTag(){
        TestCaseTag tempTct = ft.getTestCaseTag();
        assertEquals("TestCase tag", tempTct, tct);
    }

    public void testGetVariableAsBoolean() {
        ft.setVariable(S_VAR, new Boolean(true));
        boolean value = ft.getVariableAsBoolean(S_VAR);
        assertEquals("Value of boolean", true, value);
        ft.setVariable(S_VAR, new Boolean(false));
        value = ft.getVariableAsBoolean(S_VAR);
        assertEquals("Value of boolean", false, value);
    }

    public void testGetVariableAsListWithString(){
        ft.setVariable(S_VAR,"Hello");
        List l = ft.getVariableAsList(S_VAR);
        assertEquals("Size of list", 1, l.size());
        String value = (String)l.get(0);
        assertEquals("Hello", value);
    }

    public void testGetVariableAsListWithStringList(){
        ArrayList orig = new ArrayList();
        orig.add("some value");
        orig.add("another value");
        ft.setVariable(SL_VAR,orig);
        List l = ft.getVariableAsList(SL_VAR);
        assertEquals("Size of list", 2, l.size());
        String value = (String)l.get(0);
        assertEquals((String)orig.get(0), value);
        value = (String)l.get(1);
        assertEquals((String)orig.get(1), value);
    }

    public void testGetVariableAsListWithIntegerList(){
        ArrayList orig = new ArrayList();
        orig.add(new Integer(0));
        orig.add(new Integer(2));
        orig.add(new Integer(5));
        ft.setVariable(IL_VAR,orig);
        List l = ft.getVariableAsList(IL_VAR);
        assertEquals("Size of list", 3, l.size());
        assertEquals(orig.get(0), l.get(0));
        assertEquals(orig.get(1), l.get(1));
        assertEquals(orig.get(2), l.get(2));
    }

    public void testGetVariableAsListWithNull(){
        List l = ft.getVariableAsList("null_var_name");
        assertNotNull("Should be an empty list, not null!",l);
        assertEquals("Size of list", 0, l.size());
    }

    public void testGenTestCaseDocsTrueSuperClassFunctionTag() throws JellyTagException{
        //Set up TestCaseTag
        tct.setGenTestCaseDocs(true);
        ft.setFunctionId("Testing genTestCase()");
        String key = "someVariable";
        String value = "someValue";
        assertNull("<"+key+"> wasn't set yet, should be null in the context.",ft.getVariable(key));
        ft.setSomeVariable(value);
        assertEquals("<"+key+"e> should be set",value,ft.getVariable(key));
        assertEquals("Size of TestCaseTag's keySet: ",0,tct.getKeySet().size());
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("Size of TestCaseTag's keySet: ",0,tct.getKeySet().size());
    }

    public void testTearDown() throws JellyTagException {
        tct.setGenTestCaseDocs(false);
        ft.setFunctionId("Testing tearDown()");
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("tearDown() should have been called once", 1, ft.numTearDownCalls);
        assertEquals("pluginTearDown() should have been called once", 1, ft.numPluginTearDownCalls);

        tct.setGenTestCaseDocs(true);
        ft.numTearDownCalls = 0;
        ft.numPluginTearDownCalls = 0;
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("tearDown() should have been called once", 1,ft.numTearDownCalls);
        assertEquals("pluginTearDown() should have been called once", 1,ft.numPluginTearDownCalls);
    }

    // For backwards compatibility
    public void testAddRequiredAttribute() throws JellyTagException {
        assertEquals("# of registered attributes", 9, ft.getBroker().getAttributes().size());
        ft.addRequiredAttribute("noSuchAttribute");
        ft.setFunctionId("something");
        tct.setGenTestCaseDocs(false);
        assertEquals("# of registered attributes", 10, ft.getBroker().getAttributes().size());
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertTrue("Exception should say something about attribute noSuchAttribute " + ft.fResults.getErrorMsg(), 
                   ft.fResults.getErrorMsg().indexOf("noSuchAttribute") > -1);
    }

    public void testConstructorCallsBrokeredAttributes() throws JellyTagException {
        assertTrue("# of attributes should be greate than 1", ft.getBroker().getAttributes().size() > 1);
    }

    public void testSetFunctionIdViaScript(){
        ft.setAttribute("functionId","some function description");
        assertEquals("Size of unsupported attributes",0,ft.getUnsupportedAttributes().size());
    }

    public void testNoFunctionId(){
        String mssgAttrMsg = null;
        try{
            ft.getBroker().validate(ft.getContext());
        }catch(MissingAttributeException mae){
            mssgAttrMsg = mae.getMessage();
        }
        assertNotNull("A MissingAttributeException should have been thrown",mssgAttrMsg);
        assertTrue("MissingAttributeException should contain a message about functionId being required", mssgAttrMsg.indexOf("functionId") > -1);
    }

    public void testPostCondition() throws Exception{
        tct.setGenTestCaseDocs(false);
        ft.functionId = "some function";

        assertFalse("testBlock was already called", ft.simplifiedTestBlockCalled);
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertTrue("testBlock wasn't called", ft.simplifiedTestBlockCalled);
        ft.functionId = "some function";
        ft.simplifiedTestBlockCalled = false;

        tct.setFailedOnCurrentRow(true);
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertFalse("testBlock shouldn't have been called", ft.simplifiedTestBlockCalled);
        ft.functionId = "some function";

        assertTrue("failedOnCurrentRow should be true", tct.getFailedOnCurrentRow());
        ft.postcondition = true;
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertTrue("testBlock wasn't called", ft.simplifiedTestBlockCalled);
    }

    public void testDoTagWithUnsupportedAttribute() throws Exception{
        ft.setAttribute("someUnsupportedVariable","blah");
        tct.setGenTestCaseDocs(false);
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertTrue("'someUnsupportedVariable' should be in the Message",ft.fResults.getErrorMsg().indexOf("someUnsupportedVariable") > -1);
    }

    public void testRequiredSetMethodAddRequiredAttribute() throws MissingAttributeException {
        Attribute attr = new Attribute();
        attr.setName("sam");
        attr.setInstanceVariable(false);
        attr.setRequired(true);
        ft.functionId = "some value";
        ft.getFunctionalPoint().getAttributes().put("sam",attr);
        ft.getBroker().registerAttribute(attr);
        ft.addRequiredAttribute("sam");
        ft.setAttribute("sam","some value");
        ft.getBroker().transferAttributes(ft.getContext());
        ft.getBroker().validate(ft.getContext());
    }

    public void testFunctionDelay() throws Exception{
        assertEquals("functionDelay setting from Environment", 200, ft.functionDelay);
        ft.setFunctionId("delay time");
        tct.setGenTestCaseDocs(false);
        long startTime = System.currentTimeMillis();
        ft.doTag(XMLOutput.createDummyXMLOutput());
        long execTime = System.currentTimeMillis() - startTime;
        assertTrue("Execution time with functionDelay "+execTime, execTime >= 150);
    }

    public void testDelay(){
        long startTime = System.currentTimeMillis();
        ft.delay(100);
        long endTime = System.currentTimeMillis() - startTime;
        assertTrue("delay time", endTime >= 80 && endTime < 200);
    }

    public void testPrecondition() throws Exception{
        ft = new FailingMockFunctionTag("some message");
        ft.setParent(tct);
        ft.precondition = true;
        ft.setupEnvironment();
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("Error message",
                     "PRECONDITION FAILURE: some message", ft.fResults.getErrorMsg());

    }

    public void testPreconditionTag() throws Exception{
        PreconditionTag pt = new PreconditionTag();
        pt.setParent(st);
        ft = new FailingMockFunctionTag("some message");
        ft.setParent(pt);
        ft.setupEnvironment();
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("Error message",
                     "PRECONDITION FAILURE: some message", ft.fResults.getErrorMsg());

    }

    public void testPostcondition() throws Exception{
        ft = new FailingMockFunctionTag("some message");
        ft.setParent(tct);
        ft.postcondition = true;
        ft.setupEnvironment();
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("Error message",
                     "POSTCONDITION FAILURE: some message", ft.fResults.getErrorMsg());
    }

    public void testPostconditionTag() throws Exception{
        PostconditionTag pt = new PostconditionTag();
        pt.setParent(st);

        ft = new FailingMockFunctionTag("some message");
        ft.setParent(pt);
        ft.setupEnvironment();
        ft.doTag(XMLOutput.createDummyXMLOutput());
        assertEquals("Error message",
                     "POSTCONDITION FAILURE: some message", ft.fResults.getErrorMsg());
    }

}
