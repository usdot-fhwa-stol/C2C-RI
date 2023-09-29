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
package net.sf.jameleon.function;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import net.sf.jameleon.*;
import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.data.AbstractDataDrivableTag;
import net.sf.jameleon.event.BreakPoint;
import net.sf.jameleon.event.FunctionEventHandler;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.result.FunctionResult;
import net.sf.jameleon.result.FunctionResultRecordable;
import net.sf.jameleon.util.AssertLevel;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonUtility;
import net.sf.jameleon.util.StateStorer;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * <p>
 * Represents an abstract function point that does a lot behind the scenes for the function point developer. 
 * The following instance variables are described:
 * <ul>
 *  <li>fResults            - Results for this function point. Basically, the same information is kept in the session results
 *                            as are in these results, except the function results only keep information about this function.</li>
 *  <li>functionId          - This is a brief description of what this function point is used for in the test case. This is a required attribute
 *                            that is set in the macro tag of each function point. This is also used for logging and results.</li>
 *  <li>log                 - @see org.apache.log4j.Logger an easy way to send messages about the function point to the logs.</li>
 * </ul>
 * </p>
 * <p>
 * The steps to creating an abstract function tag for a plugin are as follows:
 * <ol>
 *  <li>Implement the optional {@link #store(java.lang.String,int)} method</li>
 *  <li>Override the default behavior of {@link #setupEnvironment}, being sure to call super first. The new behavior should
 *      be to get the corresponding {@link net.sf.jameleon.SessionTag} for the plug-in if a handle on the application
 *      is kept there. Use the Jelly-provided <b>findAncestorWithClass(class)</b> method to get a handle on the parent
 *      SessionTag. The abstract FunctionTag implementation should set the handle on the application being tested to
 *      a local instance variable for easy manipulation for FunctionTag extenders. If use of the plug-in FunctionTag requires
 *      a handle on the application, then it is good practice to throw a JameleonScriptException if the an ancestor of the
 *      plug-in SessionTag isn't found.</li>
 *  <li>Write your wrapper methods to make life easier for implementers of the plug-in.</l>
 * </ol>
 * <br>
 * Actually, to implement a FunctionTag for a plug-in is as simple as extending this base class and nothing more. The
 * above list is provided as recommendation only.
 * </p>
 * <p>
 * To implement a FunctionTag from an existing plugin, the following methods must be implemented:
 * <ul>
 *     <li>{@link #testBlock()}</li>
 * </ul>
 * The following methods help define the behaviour of the function point and should be implemented not at the plugin level,
 * but at the customization layer the user will do:
 * <ul>
 *     <li>{@link #setup()}</li>
 *     <li>{@link #validate()}</li>
 *     <li>{@link #tearDown()}</li>
 * </ul>
 * </p>
 */
public abstract class FunctionTag extends JameleonTagSupport implements Storable, BreakPoint { 
    /**
     * The Results for this function
     */
    protected FunctionResult fResults;
    /**
     * The id of this function. This is used in the macro as a short description of the function
     * @jameleon.attribute required="true"
     */
    protected String functionId;
    /**
     * Tells the GUI to stop at this functional point
     * @jameleon.attribute required="false"
     */
    protected boolean breakPoint;
    /**
     * Specifies this is a precondition
     * @jameleon.attribute required="false"
     */
    protected boolean precondition;
    /**
     * Specifies this is a postcondition and will therefore get executed even 
     * if a previous functional point or session tag failed.
     * @jameleon.attribute required="false"
     */
    protected boolean postcondition;
    /**
     * Specifies a failure is expected.
     * @jameleon.attribute required="false"
     */
    protected boolean expectFailure;
    /**
     * Specifies an exception is expected.
     * @jameleon.attribute required="false"
     */
    protected boolean expectException;
    /**
     * A list of required attributes for this function point. 
     * @deprecated use getFunctionResults().setErrorFile() instead.
     */
    protected File lastFileWritten;

    /**
     * Added for the C2C Reference Implementation
     * Specifies this is a testStep
     */
    protected boolean testStep;

    /**
     * A list of parameters passed to this functional point via the &lt;param&gt; tag.
     */
    protected ArrayList params;

    /**
     * The parent session tag of this functional point
     */
    protected SessionTag st;
    /**
     * The parent test case of this functional point
     */
    protected TestCaseTag tct;
    /**
     * Stores the state of the application.
     */
    protected StateStorer state;
    
    protected static Logger log = Logger.getLogger(FunctionTag.class.getName());
    
    AssertLevel assertLevelManager = AssertLevel.getInstance();

    protected ParamTypeValidatable vpt = new DefaultValidParamType();
    protected static final long NO_DELAY = -1;
    /**
     * The delay time to use at the end of each functional point
     * @jameleon.attribute default="-1"
     */
    protected long functionDelay = NO_DELAY;

    protected AbstractDataDrivableTag addt;

    /**
     * Gets the id of the function being tested. This is usally a short description of how the function point.is used in the test case.
     * @return functionId - The name of the function
     */
    public String getFunctionId(){
        return functionId;
    }

    /**
     * Sets the id of the function being tested.
     * @param functionId - the id of the function being tested
     */
    public void setFunctionId(String functionId){
        this.functionId = functionId;
    }

    /**
     *  Sets the value for expectFailure. This attrubute defaults to false, so only set it if true is set.
     *  @param expectFailure - set to true to expect a failure. To invoke this functionality must be 'yes' or 'true'.
     */
    public void setExpectFailure( boolean expectFailure ){
        this.expectFailure = expectFailure;
    }

    /**
     *  Sets the value for expectException. This attrubute defaults to false.
     *  @param expectException - a value of "yes" or "true" indicates the function tag should only pass if there is an exception.
     */
    public void setExpectException( boolean expectException ){
        this.expectException = expectException;
    }

    /**
     *  Checks whether this tag is a break point. Used mostly for the GUI.
     *  @return true if this tag is a break point.
     */
    public boolean isBreakPoint( ){
        return breakPoint;
    }

    /**
     * Sets the delay time to occur at the end of this functional point.
     * @param functionDelay - the time in millis.
     */
    public void setFunctionDelay( long functionDelay ){
        this.functionDelay = functionDelay;
    }

    /**
     * Does setup for plugin-required intialization. Functional points that implement the plugin should call the super
     * of method before implementing their own code.
     */
    public void setup(){}

    /**
     * Called when the function is done being executed. Use to clean up resources created in the setup method.
     */
    public void tearDown(){}

    /**
     * Called when the function is done being executed. Use to clean up resources created in the setup method.
     */
    public void pluginTearDown(){}

    /**
     * Contains the actual testing of the function point. Implement this method with
     * any code that would make the FunctionTag unique. The default implementation does nothing.
     * Implement this method to make your function tag useful.
     * @throws Exception when something goes wrong
     */
    public void testBlock() throws Exception{}

    /**
     * Simply calls <code>setupEnvironment()</code>, <code>validate()</code>, <code>setup()</code> and <code>testBlock(XMLOutput out)</code>.
     * If this method is overridden, you will need to call super.doTag() or else none of the logging or error reporting will work!
     */
    public void doTag(XMLOutput out) throws MissingAttributeException{
        setupEnvironment();
        FunctionEventHandler eventHandler = FunctionEventHandler.getInstance();
        if (!addt.getFailedOnCurrentRow() || postcondition) {
            if (tct.isExecuteTestCase()){
                traceMsg("BEGIN: "+functionId);
                long startTime = System.currentTimeMillis();
                boolean failureOccured = false;
                boolean exceptionOccured = false;
                try{
                    testForUnsupportedAttributesCaught();
                    invokeBody(out);
                    setup();
                    broker.transferAttributes(context);
                    eventHandler.beginFunction(this, 1);
                    broker.validate(context);
                    validate();// used mostly for subclasses that override validate.
                    testBlock();
                    if (expectException) {
                        fail("Expected exception, but none was thrown");
                    }
                }catch(AssertionFailedError afe){
                    if( expectFailure ){
                        //When assert fails, then this is called a failure and not an error.
                        failureOccured = true;
                    }else{
                        // this is the case that we got an assert failed exception we did not expect
                        exceptionOccured = true;
                        fResults.setError(afe);
                    }
                } catch(ThreadDeath td){
                    throw td;
                } catch(Throwable e){
                    if (!expectException) {
                        //Record an error because this means something unexpected happened
                        traceMsg(JameleonUtility.getStack(e));
                        fResults.setError(e);
                        exceptionOccured = true;
                    }
                }finally{
                    if ( expectFailure && !failureOccured ) {
                        exceptionOccured = true;
                        JameleonScriptException jse = new JameleonScriptException("Expected a failure that did not happen: \n", this );
                        fResults.setError(jse);
                    }
                    if ( exceptionOccured ){
                        state.eventOccured(StateStorer.ON_ERROR_EVENT);
                        if ( lastFileWritten != null ){
                            fResults.setErrorFile(lastFileWritten);
                        }
                        addt.setFailedOnCurrentRow(true);
                        traceMsg("##################################################################");
                        traceMsg("FAILED: "+functionId);
                        traceMsg("##################################################################");
                        traceMsg("");
                        // If this function is a child of a TestStep set the failed flag.
                        if (this.getParent() instanceof TestStepTag){
                            TestStepTag theParent = (TestStepTag)this.getParent();
                            theParent.getFunctionResults().setFailed();
//                        } else if (this.getParent() instanceof PostconditionTag){
//                            JOptionPane.showMessageDialog(null, "A post condition error was encountered with message \n"+fResults.getErrorMsg(), "Post Condition Error", JOptionPane.INFORMATION_MESSAGE);
//                            log.info("User Acknowledged Postcondition Error Message");
//                        }else if (this.postcondition){
//                            JOptionPane.showMessageDialog(null, "A post condition error was encountered with message \n"+fResults.getErrorMsg(), "Post Condition Error", JOptionPane.INFORMATION_MESSAGE);
//                            log.info("User Acknowledged Postcondition Error Message");

                        }
                        /***********************************************************
                         * Added for RI POC 
                         *
                         */
                          log.info("File Name => " + fResults.getFileName() + "\n Date/Time Executed => " + fResults.getDateTimeExecuted() + "\nIdentifier: " + fResults.getIdentifier() + "\n " + fResults.toString());
                         /***********************************************************
                          * End Added for RI POC 
                          *
                          */
                    }else{
                        traceMsg("SUCCEEDED: "+functionId);
                        /***********************************************************
                         * Added for RI POC 
                         *
                         */
                         log.info("File Name => " + fResults.getFileName() + "\n Date/Time Executed => " + fResults.getDateTimeExecuted() + "\nIdentifier: " + fResults.getIdentifier() + "\n " + fResults.toString());
                        /***********************************************************
                         * End Added for RI POC 
                         *
                         */
                    }
                    delay();
                    recordFunctionResult(startTime);
                    eventHandler.endFunction(this, 1);
                }
            }
        }else{
            removeFunctionResult();
        }
        cleanUp();        
    }

    /**
     * If this method is overridden, it is highly suggested that the super
     * method be called as well.
     */
    protected void cleanUp(){
        expectFailure = false;
        expectException = false;
        tearDown();
        pluginTearDown();
        fResults.setTag(fp.cloneFP());
        resetFunctionalPoint();
        cleanUpEnvironment();
        if (params != null) {
            params.clear();
            params = null;
        }
    }

    protected void cleanUpEnvironment(){
        if (state != null) {
            state.removeStorable(this);
            lastFileWritten = null;
        }
    }

    protected void traceMsg(String msg){
        if (tct != null && tct.getTrace()) {
            System.out.println(msg+"\n");
        }else{
        	log.debug(msg);
        }
    }

    /**
     * Adds a required attribute to the macro tag defined for this function point. This is used to enforce a certain
     * attribute be set in the tag of the function point. The javadoc method should be used instead if possible.
     * @param attribute - The name of the attribute to add to the required list.
     */
    public void addRequiredAttribute(String attribute){
        Attribute attr = fp.getAttribute(attribute);
        if (attr == null) {
            attr = new Attribute();
            attr.setName(attribute);
        }
        attr.setRequired(true);
        broker.registerAttribute(attr);
    }

    // -------------- Begin ContextHelper methods

    /**
     * Sets the variable to a default value in the context. It is recommended that all variables be set
     * in the context to take advantage of all features provided by Jamaleon.
     * This can be used when you want the function point to work with both
     * a CSV file and setting the attributes in the macro. The rule is if the variable is already set ( via 
     * testEnvironment-Applications.properties or the corresponding CSV dile or another funcational point attribute ),
     * then this won't be set. If you want to set variable in the context, without regard as to the CSV file,
     * then simply call context.setVariable(String key, String value) ( through the Jelly API ). <br>
     * <br>
     * For the order context variables are set in Jameleon, please see the class description of TestCaseTag.
     * @see TestCaseTag net.sf.jameleon.TestCaseTag 
     * @param attributeName - the name attribute to set
     * @param attributeValue - the value of the attribute to set.
     */
    public void setDefaultVariableValue(String attributeName, String attributeValue){
        if (isContextVariableNull(attributeName)) {
            context.setVariable(attributeName,attributeValue);
        }else{
            log.debug("setDefaultVariableValue: Not setting variable \""+attributeName+"\" with value \""+attributeValue+"\" because it has already been set");
        }
    }

    /**
     * Sets the variable to a default value in the context. It is recommended that all variables be set
     * in the context to take advantage of all features provided by Jamaleon.
     * This can be used when you want the function point to work with both
     * a CSV file and setting the attributes in the macro. The rule is if the variable is already set ( via 
     * testEnvironment-Applications.properties or the corresponding CSV dile or another funcationl point in the same test case),
     * then this won't be set. If you want to set variable in the context, without regard as to the CSV file,
     * then simply call context.setVariable(String key, String value) ( through the Jelly API ). <br>
     * <br>
     * For the order context variables are set in Jameleon, please see the class description of TestCaseTag.
     * @see TestCaseTag net.sf.jameleon.TestCaseTag 
     * @param attributeName - the name attribute to set
     * @param attributeValue - the value of the attribute to set.
     */
    public void setDefaultVariableValue(String attributeName, Object attributeValue){
        if (isContextVariableNull(attributeName)) {
            context.setVariable(attributeName,attributeValue);
        }else{
            log.debug("setDefaultVariableValue: Not setting variable \""+attributeName+"\" because it has already been set");
        }
    }

    /**
     * Sets the variable, <code>key</code> to the value, <code>value</code>
     * This is simply a wrapper class for Jelly's own context.setVariable();
     * @param key - the name attribute to set
     * @param value - the value of the attribute to set.
     */
    public void setVariable(String key, Object value){
        ContextHelper.setVariable(context, key, value);
    }

    /**
     * Gets the variable, <code>key</code> from the test case context.
     * This is simply a wrapper class for Jelly's own context.getVariable();
     * @param key - the name attribute to set
     * @return the value of <code>key</key>
     */
    public Object getVariable(String key){
        return ContextHelper.getVariable(context, key);
    }

    /**
     * Removes the variable, <code>key</code> from the test case context.
     * This is simply a wrapper class for Jelly's own context.removeVariable();
     * @param key - the name attribute to set
     */
    public void removeVariable(String key){
        ContextHelper.removeVariable(context, key);
    }

    /**
     * @return a variable with name <code>key</code> from the context as a String
     * @param key The variable name
     */
    protected String getVariableAsString(String key){
        return ContextHelper.getVariableAsString(context, key);
    }

    /**
     * @return a variable with name <code>key</code> from the context as a (primitive) boolean
     * @param key The variable name
     */
    protected boolean getVariableAsBoolean(String key) {
        return ContextHelper.getVariableAsBoolean(context, key);
    }

    /**
     * Gets a variable from context and casts it to a list. If the value isn't a 
     * List, then it creates a ArrayList and adds the value to it. 
     * @return a variable with name <code>key</code> from the context as a List even if the value is null.
     * @param key The variable name
     */
    protected List getVariableAsList(String key){
        return ContextHelper.getVariableAsList(context, key);
    }

    /**
     * Checks to see if the variable, <code>key</code>, is null or hasn't been set.
     * @param key - The name of the variable to be checked for a null value. This variable must be a String.
     * @return true If the variable is set to null or "" then.
     */

    protected boolean isContextVariableNull(String key){
        return ContextHelper.isVariableNull(context, key);
    }

    // End ContextHelper methods --------------

    /**
     * This method is called before the testBlock method is called.
     * If you want to override this method, call super first, 
     * followed by the custom validations that need to occur in your TestStep.
     * @throws MissingAttributeException - When a required attribute is not set.
     */
    protected void validate() throws MissingAttributeException {
        checkParamTypes();
    }

    protected void checkParamTypes(){
        if (params != null) {
            Iterator it = params.iterator();
            while (it.hasNext()) {
                ParamTag p = (ParamTag)it.next();
                if (!vpt.isValidType(p.getParamType())) {
                    String errMsg = "Unable to find [" + p.getParamType() + "] in the following types: \n";
                    throw new JameleonScriptException(errMsg + vpt.getValidTypes(), this);
                }
            }
        }
    }
  
    /**
     * This method is called before anything else is called.
     */
    protected void setupEnvironment() {
        getParentTags();
        params = new ArrayList();
        state = StateStorer.getInstance();
        state.addStorable(this);
        fp.setFunctionId(functionId);
        fResults.setPrecondition(precondition);
        fResults.setPostcondition(postcondition);
        fResults.setTestStep(testStep);

        Configurator config = Configurator.getInstance();
        String fDelay = config.getValue("functionDelay");
        if (functionDelay == NO_DELAY && 
            fDelay != null && 
            fDelay.trim().length() > 0) {
            try{
                setAttribute("functionDelay", fDelay);
            }catch(NumberFormatException nfe){
                traceMsg("functionDelay variable in jameleon.conf is not a valid number");
            }

        }
    }

    protected void getParentTags(){
        Object obj =  findAncestorWithClass(TestCaseTag.class);
        if (obj != null){
            tct = (TestCaseTag)obj;
        }else{
            throw new ClassCastException("Can only execute a function tag under the test case tag (testcase)! "+getClass().getName());
        }
        obj = findAncestorWithClass(PostconditionTag.class);
        if (obj != null) {
            postcondition = true;
        }
        obj = findAncestorWithClass(PreconditionTag.class);
        if (obj != null) {
            precondition = true;
        }
/**
 * 		Added this code for the RI Test Step Feature
 * 
 */
        obj = findAncestorWithClass(TestStepTag.class);
        if (obj != null) {
            testStep = true;
        }
 /**
  *     End of Added Code
  */
        obj = findAncestorWithClass(SessionTag.class);
        if (obj != null) {
            st = (SessionTag)obj;
        }
        setUpFunctionResults();
        addt = (AbstractDataDrivableTag)findAncestorWithClass(AbstractDataDrivableTag.class);
    }

    protected void setUpFunctionResults(){
        fResults = new FunctionResult(fp);
        fResults.copyLocationAwareProperties(this);
        Object obj = findAncestorWithClass(FunctionResultRecordable.class);
        if (obj != null) {
            ((FunctionResultRecordable)obj).recordFunctionResult(fResults);
        }
    }

    protected void removeFunctionResult(){
        if (fResults.getParentResults() != null) {
            List results = fResults.getParentResults().getChildrenResults();
            if (results != null) {
                int index = results.lastIndexOf(fResults);
                results.remove(index);
            }
        }
    }

    /**
     * Adds the result for this test step into the session's results
     * and logs this information to the logger.
     * @param startTime - The time this test step started execution.
     */
    protected void recordFunctionResult(long startTime){
        fResults.setExecutionTime(System.currentTimeMillis() - startTime);
        log.debug(fResults);
    }

    /**
     * @return The test results of this test step
     */
    public FunctionResult getFunctionResults(){
        return fResults;
    }

    /**
     * Gets the matching text found in the given string.
     * @param text - The text to match against
     * @param regex - The regex pattern to match. This regex pattern must
     *                  have a single grouping representing the desired text.
     * @param matchingGroup - The group position to match against.
     * 
     * @return The matching text in the searchString
     */
    public String getMatchingRegexText(String text, String regex, int matchingGroup) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        String matched = null;
        try {
            if (matcher.find()) {
                matched = matcher.group(matchingGroup);
            }
        } catch (IndexOutOfBoundsException ie) {
            throw new JameleonScriptException("No group was defined in the following regular expression: ["+regex+"]", this);
        }
        return matched;
    }

    /**
     * Validates that the provided regex matches the provided text
     * @param text - The text to match against
     * @param regex - The regex pattern to match
     * 
     * @return True if the regex matches or false if it doesn't
     */
    public boolean regexMatches(String text, String regex) {
        boolean matches;
        if (regex == null) {
            throw new JameleonScriptException("regex! can not be null", this);
        }
        if (text == null) {
            throw new JameleonScriptException("text! can not be null", this);
        }
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        matches = matcher.find();
        return matches;
    }

    /**
     * Gets a String by its default value if an actual value isn't found.
     * @return a String by its default value if an actual value isn't found..
     * @param value - the value to return
     * @param defaultValue - the value to return if the <code>value</code> parameter is null
     */
    public String getStringOrDefault(String value, String defaultValue){
        String returnValue = defaultValue;
        if (value != null) {
            returnValue = value;
        }
        return returnValue;
    }

    /**
     * Gets the parent test case tag
     * @return the parent test case tag of this tag.
     */
    public TestCaseTag getTestCaseTag(){
        return tct;
    }

    //Storable Methods
    /**
     * <p>
     * Stores the current state of the object to a given <code>File</code>. The default
     * implementation of this method does nothing. Override this method to implement
     * plug-in specific behavior. Some examples might be:</p>
     * <ul>
     *  <li>Saving the HTML from an HTTP plug-in during a state change or error</li>
     *  <li>Taking a screen shot for a GUI application during a state change event</li>
     * </ul>
     * <p>
     * A listener is already registered for each function tag. All that is required is
     * implementing this method.</p>
     * @param filename the name of the if the file to store the contents to.
     * @param event The {@link net.sf.jameleon.util.StateStorer event} that occured (error, state change ...).
     * @throws IOException If the state of the object could not be stored in File <code>f</code>.
     */
    public void store(String filename, int event) throws IOException{
    }

    public SessionTag getSessionTag(){
        return st;
    }

    /**
     * Gets the filename to store the state of the application to. 
     * The default implementation is to simply use timestamps. 
     * If this is not the desired behavior, override this method.
     * @param event - the StateStorer Event
     * @return the appropriate filename which starts with ERROR- if the StateStorer Event was an Error
     */
    public String getStoreToFileName(int event){
        String filename = System.currentTimeMillis() +"";
        if ( event == StateStorer.ON_ERROR_EVENT ) {
            filename = "ERROR-"+filename;
        }
        return filename;
    }

    //End Storable Methods

    /****************************************************************************************
     *Start of Wrapper Methods
    ****************************************************************************************/
                                                                                             
    /**
     * Runs the run method of a Runnable and records the results appropriately.
     * @param r - The block of code you want executed ( defined in the run() method ).
     * @deprecated This method will be removed in the next major release.
     */
    protected void assertMethod(Runnable r){
        r.run();
    }

    /**
     * Runs the run method of a Runnable and records the results appropriately.
     * @param r - The block of code you want executed ( defined in the run() method ).
     * @param assertLevel - The assert level of when to run the method
     */
    protected void assertMethodWithLevel(Runnable r, int assertLevel){
        if (assertLevelManager.assertPerformable(assertLevel)) {
            r.run();
        }
    }

    //JUnit Assertions
    
    /**
     * Fails by throwing an AssertionFailedError.
     */
    public void fail(){
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        fail(message, assertLevel);
    }

    /**
     * Fails by throwing an AssertionFailedError with a <code>message</code>
     * @param message - The message to display on failure.
     */
    public void fail(final String message){
        int assertLevel = AssertLevel.NO_LEVEL;
        fail(message, assertLevel);
    }

    /**
     * Fails by throwing an AssertionFailedError with a <code>message</code>
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void fail(int assertLevel){
        String message = "";
        fail(message, assertLevel);
    }

    /**
     * Fails by throwing an AssertionFailedError with a <code>message</code>
     * @param message - The message to display on failure.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void fail(final String message, int assertLevel){
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.fail(JameleonUtility.createErrMsg(message));
                }
        }, assertLevel);
    }

    /**
     * Asserts that a condition is true. 
     * @param condition - The boolean condition to test.
     */
    public void assertTrue(final boolean condition) {
        String message = "assertTrue: ";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertTrue(message, condition, assertLevel);
    }

    /**
     * Asserts that a condition is true. 
     * @param message - The message to print out if the assert fails
     * @param condition - The boolean condition to test.
     */
    public void assertTrue(final String message, final boolean condition) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertTrue(message, condition, assertLevel);
    }
    
    /**
     * Asserts that a condition is true. 
     * @param condition - The boolean condition to test.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertTrue(final boolean condition, int assertLevel) {
        String message = "assertTrue: ";
        assertTrue(message, condition, assertLevel);
    }

    /**
     * Asserts that a condition is true. 
     * @param message - The message to print out if the assert fails
     * @param condition - The boolean condition to test.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertTrue(final String message, final boolean condition, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertTrue(JameleonUtility.createErrMsg(message),condition);
                }
        },assertLevel);
    }
    
    /**
     * Asserts that a condition is false.
     * @param condition - The boolean condition to test.
     */
    public void assertFalse(final boolean condition) {
        String message = "assertFalse: ";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertFalse(message, condition, assertLevel);
    }
    
    /**
     * Asserts that a condition is false.
     * @param message - The message to print out if the assert fails
     * @param condition - The boolean condition to test.
     */
    public void assertFalse(final String message, final boolean condition) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertFalse(message, condition, assertLevel);
    }

    /**
     * Asserts that a condition is false.
     * @param condition - The boolean condition to test.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertFalse(final boolean condition, int assertLevel) {
        String message = "assertFalse: ";
        assertFalse(message, condition, assertLevel);
    }
    
    /**
     * Asserts that a condition is false.
     * @param message - The message to print out if the assert fails
     * @param condition - The boolean condition to test.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertFalse(final String message, final boolean condition, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertFalse(JameleonUtility.createErrMsg(message),condition);
                }
        },assertLevel);
    }

    /**
     * Asserts that two objects are equal.
     * @param text - The text to be compared
     * @param textContained   - the text to be contained
     */
    public void assertTextContains(final String text, final String textContained) {
        String message = "assertTextContains: '"+textContained+"' not in given text";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertTextContains(message, text, textContained, assertLevel);
    }

    /**
     * Asserts that two objects are equal.
     * @param message - The message to print out if the assert fails
     * @param text - The text to be compared
     * @param textContained   - the text to be contained
     */
    public void assertTextContains(final String message, final String text, final String textContained) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertTextContains(message, text, textContained, assertLevel);
    }

    /**
     * Asserts that two objects are equal.
     * @param text - The text to be compared
     * @param textContained   - the text to be contained
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertTextContains(final String text, final String textContained, int assertLevel) {
        String message = "assertTextContains: '"+textContained+"' not in given text";
        assertTextContains(message, text, textContained, assertLevel);
    }

    /**
     * Asserts text is contained into some given text.
     * @param message - The message to print out if the assert fails
     * @param text - The text to be compared
     * @param textContained   - the text to be contained
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertTextContains(final String message, final String text, final String textContained, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertTrue(JameleonUtility.createErrMsg(message), text.indexOf(textContained) >= 0);
                }
        },assertLevel);
    }

    /**
     * Asserts that the given startingText begins the given text..
     * @param text - The text to be compared
     * @param startingText   - the text that must be at the beginning.
     */
    public void assertTextStartsWith(final String text, final String startingText) {
        String message = "assertTextStartsWith: '"+startingText+"' is not at the beginning of the given text";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertTextStartsWith(message, text, startingText, assertLevel);
    }

    /**
     * Asserts that the given startingText begins the given text..
     * @param message - The message to print out if the assert fails
     * @param text - The text to be compared
     * @param startingText   - the text that must be at the beginning.
     */
    public void assertTextStartsWith(final String message, final String text, final String startingText) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertTextStartsWith(message, text, startingText, assertLevel);
    }
    
    /**
     * Asserts that the given startingText begins the given text..
     * @param text - The text to be compared
     * @param startingText   - the text that must be at the beginning.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertTextStartsWith(final String text, final String startingText, int assertLevel) {
        String message = "assertTextStartsWith: '"+startingText+"' is not at the beginning of the given text";
        assertTextStartsWith(message, text, startingText, assertLevel);
    }
    
    /**
     * Asserts that the given startingText begins the given text..
     * @param message - The message to print out if the assert fails
     * @param text - The text to be compared
     * @param startingText   - the text that must be at the beginning.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertTextStartsWith(final String message, final String text, final String startingText, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertTrue(JameleonUtility.createErrMsg(message), text.startsWith(startingText));
                }
        },assertLevel);
    }

    /**
     * Asserts that the given endingText is at the end of the given text..
     * @param text - The text to be compared
     * @param endingText   - the text that must be at the end.
     */
    public void assertTextEndsWith(final String text, final String endingText) {
        String message = "assertTextEndsWith: '"+endingText+"' is not at the end of the given text";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertTextEndsWith(message, text, endingText, assertLevel);
    }

    /**
     * Asserts that the given endingText is at the end of the given text..
     * @param message - The message to print out if the assert fails
     * @param text - The text to be compared
     * @param endingText   - the text that must be at the end.
     */
    public void assertTextEndsWith(final String message, final String text, final String endingText) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertTextEndsWith(message, text, endingText, assertLevel);
    }

    /**
     * Asserts that the given endingText is at the end of the given text..
     * @param text - The text to be compared
     * @param endingText   - the text that must be at the end.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertTextEndsWith(final String text, final String endingText, int assertLevel) {
        String message = "assertTextEndsWith: '"+endingText+"' is not at the end of the given text";
        assertTextEndsWith(message, text, endingText, assertLevel);
    }

    /**
     * Asserts text is contained into some given text.
     * @param message - The message to print out if the assert fails
     * @param text - The text to be compared
     * @param endingText   - the text that must be at the end.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertTextEndsWith(final String message, final String text, final String endingText, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertTrue(JameleonUtility.createErrMsg(message), text.endsWith(endingText));
                }
        },assertLevel);
    }

    /**
     * Asserts that two objects are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final Object expected, final Object actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String message, final Object expected, final Object actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final Object expected, final Object actual, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final Object expected, final Object actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual);
                }
        },assertLevel);
    }

    /**
     * Asserts that two Strings are equal. 
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String expected, final String actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two Strings are equal. 
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String message, final String expected, final String actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two Strings are equal. 
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String expected, final String actual, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two Strings are equal. 
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final String expected, final String actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual);
                }
        },assertLevel);
    }

    /**
     * Asserts that two doubles are equal concerning a delta. 
     * If the expected value is infinity then the delta value is ignored.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param delta    - The amount the expected and actual can be different from each other.
     */
    public void assertEquals(final double expected, final double actual, final double delta) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, delta, assertLevel);
    }

    /**
     * Asserts that two doubles are equal concerning a delta. 
     * If the expected value is infinity then the delta value is ignored.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param delta    - The amount the expected and actual can be different from each other.
     */
    public void assertEquals(final String message, final double expected, final double actual, final double delta) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, delta, assertLevel);
    }

    /**
     * Asserts that two doubles are equal concerning a delta.
     * If the expected value is infinity then the delta value is ignored.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param delta    - The amount the expected and actual can be different from each other.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final double expected, final double actual, final double delta, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, delta, assertLevel);
    }

    /**
     * Asserts that two doubles are equal concerning a delta. 
     * If the expected value is infinity then the delta value is ignored.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param delta    - The amount the expected and actual can be different from each other.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final double expected, final double actual, final double delta, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual,delta);
                }
        },assertLevel);
    }

    /**
     * Asserts that two floats are equal concerning a delta. 
     * If the expected value is infinity then the delta value is ignored.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param delta    - The amount the expected and actual can be different from each other.
     */
    public void assertEquals(final float expected, final float actual, final float delta) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, delta, assertLevel);
    }

    /**
     * Asserts that two floats are equal concerning a delta. 
     * If the expected value is infinity then the delta value is ignored.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param delta    - The amount the expected and actual can be different from each other.
     */
    public void assertEquals(final String message, final float expected, final float actual, final float delta) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, delta, assertLevel);
    }

    /**
     * Asserts that two floats are equal concerning a delta. 
     * If the expected value is infinity then the delta value is ignored.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param delta    - The amount the expected and actual can be different from each other.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final float expected, final float actual, final float delta, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, delta, assertLevel);
    }

    /**
     * Asserts that two floats are equal concerning a delta. 
     * If the expected value is infinity then the delta value is ignored.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param delta    - The amount the expected and actual can be different from each other.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final float expected, final float actual, final float delta, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual,delta);
                }
        },assertLevel);
    }

    /**
     * Asserts that two longs are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final long expected, final long actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two longs are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String message, final long expected, final long actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two longs are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final long expected, final long actual, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two longs are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final long expected, final long actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual);
                }
        }, assertLevel);
    }

    /**
     * Asserts that two booleans are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final boolean expected, final boolean actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two booleans are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String message, final boolean expected, final boolean actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two booleans are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final boolean expected, final boolean actual, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two booleans are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final boolean expected, final boolean actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual);
                }
        }, assertLevel);
    }

    /**
     * Asserts that two bytes are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final byte expected, final byte actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two bytes are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String message, final byte expected, final byte actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two bytes are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final byte expected, final byte actual, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two bytes are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final byte expected, final byte actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual);
                }
        }, assertLevel);
    }

    /**
     * Asserts that two chars are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final char expected, final char actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two chars are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String message, final char expected, final char actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two chars are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final char expected, final char actual, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two chars are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final char expected, final char actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual);
                }
        }, assertLevel);
    }

    /**
     * Asserts that two shorts are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final short expected, final short actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two shorts are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String message, final short expected, final short actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two shorts are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final short expected, final short actual, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two shorts are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final short expected, final short actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual);
                }
        }, assertLevel);
    }

    /**
     * Asserts that two ints are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final int expected, final int actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two ints are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertEquals(final String message, final int expected, final int actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two ints are equal.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final int expected, final int actual, int assertLevel) {
        String message = "";
        assertEquals(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two ints are equal.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertEquals(final String message, final int expected, final int actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertEquals(JameleonUtility.createErrMsg(message),expected,actual);
                }
        }, assertLevel);
    }

    /**
     * Asserts that an object isn't null.
     * @param object -  The object to test for null
     */
    public void assertNotNull(final Object object) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertNotNull(message, object, assertLevel);
    }

    /**
     * Asserts that an object isn't null.
     * @param message - The message to print out if the assert fails
     * @param object -  The object to test for null
     */
    public void assertNotNull(final String message, final Object object) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertNotNull(message, object, assertLevel);
    }

    /**
     * Asserts that an object isn't null.
     * @param object -  The object to test for null
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertNotNull(final Object object, int assertLevel) {
        String message = "";
        assertNotNull(message, object, assertLevel);
    }

    /**
     * Asserts that an object isn't null.
     * @param message - The message to print out if the assert fails
     * @param object -  The object to test for null
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertNotNull(final String message, final Object object, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertNotNull(JameleonUtility.createErrMsg(message),object);
                }
        }, assertLevel);
    }

    /**
     * Asserts that an object is null.
     * @param object -  The object to test for null
     */
    public void assertNull(final Object object) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertNull(message, object, assertLevel);
    }

    /**
     * Asserts that an object is null.
     * @param message - The message to print out if the assert fails
     * @param object -  The object to test for null
     */
    public void assertNull(final String message, final Object object) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertNull(message, object, assertLevel);
    }

    /**
     * Asserts that an object is null.
     * @param object -  The object to test for null
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertNull(final Object object, int assertLevel) {
        String message = "";
        assertNull(message, object, assertLevel);
    }

    /**
     * Asserts that an object is null.
     * @param message - The message to print out if the assert fails
     * @param object - The object to test for null
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertNull(final String message, final Object object, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertNull(JameleonUtility.createErrMsg(message),object);
                }
        }, assertLevel);
    }

    /**
     * Asserts that given regex matches the provided text
     * @param text   - The text to match the regex against
     * @param regex  - The regex to match.
     */
    public void assertRegexMatches(final String text, final String regex) {
        String message = "assertRegexMatches";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertRegexMatches(message, text, regex, assertLevel);
    }

    /**
     * Asserts that given regex matches the provided text
     * @param message - The message to print out if the assert fails
     * @param text   - The text to match the regex against
     * @param regex  - The regex to match.
     */
    public void assertRegexMatches(final String message, final String text, final String regex) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertRegexMatches(message, text, regex, assertLevel);
    }

    /**
     * Asserts that given regex matches the provided text
     * @param text   - The text to match the regex against
     * @param regex  - The regex to match.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertRegexMatches(final String text, final String regex, int assertLevel) {
        String message = "assertRegexMatches";
        assertRegexMatches(message, text, regex, assertLevel);
    }

    /**
     * Asserts that given regex matches the provided text
     * @param message - The message to print out if the assert fails
     * @param text   - The text to match the regex against
     * @param regex  - The regex to match.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertRegexMatches(final String message, final String text, final String regex, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertTrue(JameleonUtility.createErrMsg(message),regexMatches(text, regex));
                }
        }, assertLevel);
    }

    /**
     * Asserts that two objects refer to the same object.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertSame(final Object expected, final Object actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertSame(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects refer to the same object.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertSame(final String message, final Object expected, final Object actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertSame(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects refer to the same object.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertSame(final Object expected, final Object actual, int assertLevel) {
        String message = "";
        assertSame(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects refer to the same object.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertSame(final String message, final Object expected, final Object actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertSame(JameleonUtility.createErrMsg(message),expected,actual);
                }
        }, assertLevel);
    }

    /**
     * Asserts that two objects refer to the same object.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertNotSame(final Object expected, final Object actual) {
        String message = "";
        int assertLevel = AssertLevel.NO_LEVEL;
        assertNotSame(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects refer to the same object.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     */
    public void assertNotSame(final String message, final Object expected, final Object actual) {
        int assertLevel = AssertLevel.NO_LEVEL;
        assertNotSame(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects refer to the same object.
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertNotSame(final Object expected, final Object actual, int assertLevel) {
        String message = "";
        assertNotSame(message, expected, actual, assertLevel);
    }

    /**
     * Asserts that two objects refer to the same object.
     * @param message - The message to print out if the assert fails
     * @param expected - The expected value.
     * @param actual   - The value to compare against the <code>expected</code> or the actual value.
     * @param assertLevel - Only run this test under the assertLevel as described in {@link net.sf.jameleon.util.AssertLevel}
     */
    public void assertNotSame(final String message, final Object expected, final Object actual, int assertLevel) {
        assertMethodWithLevel(new Runnable() {
                public void run() {
                    Assert.assertNotSame(JameleonUtility.createErrMsg(message),expected,actual);
                }
        }, assertLevel);
    }

    //End of JUnit Assertion Wrappers

    /**
     * Adds a ParamTag to the list of parameters
     * @param param - the ParamTag to add
     */
    public void addParam(Object param){
        params.add(param);
    }

    /**
     * Gets the nth ParamTag.
     * @param index - the index location of the desired ParamTag.
     * @return the ParamTag located at index
     */
    public Object getParam(int index){
        return params.get(index);
    }

    /**
     * Gets the list of ParamTags under this tag.
     * @return the list of ParamTags under this tag.
     */
    public List getParams(){
        return params;
    }

    /**
     * Gets the number of ParamTags under this tag.
     * @return the number of ParamTags under this tag.
     */
    public int getParamLength(){
        return params.size();
    }

    public void delay(){
        delay(functionDelay);
    }

    public void delay(long delayTime){
        if (delayTime > 0) {
            synchronized (this){ 
                try {
					while (delayTime > 0)
					{
						this.wait(delayTime); 
					}
                } catch (InterruptedException e) {
                    e.printStackTrace();
					Thread.currentThread().interrupt();
                }
            } 
        }
    }

    /**
     * 
     * @return the current row.  The value is set to 0 if the abstractDataDrivableResult or datadrivaableResult is null.
     */
    public int getCurrentRow(){
        if (addt != null){
            if (addt.getDataDrivableRowResult() != null){
                return addt.getDataDrivableRowResult().getRowNum();
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

}
