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

import net.sf.jameleon.bean.TestCase;
import net.sf.jameleon.data.DataDrivable;
import net.sf.jameleon.event.TestCaseEventHandler;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.result.*;
import net.sf.jameleon.util.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.jelly.*;
import org.apache.commons.jelly.expression.CompositeExpression;
import org.apache.commons.jelly.expression.Expression;
import org.apache.commons.jelly.expression.jexl.JexlExpressionFactory;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Every test case script must have at least one testcase tag containing all other Jameleon tags. 
 * <p>
 * Some of this tags attribute may affect every nested Jameleon tag. 
 * Many of the attributes in this tag can be set globally via a jameleon.conf file.
 * </p>
 * <p>
 * The order of setting variables in the context follows:
 * <ol>
 * <li> Load the CSV file variables and put them in the context.</li>
 * <li> Load the $testEnvironment-Applications.properties and then Applications.properties and 
 *      only set the variables that aren't set in the previous files.  In other words, if there
 *      are variables that are going to be the same ( like the page title ) across multiple test 
 *      cases, then first variable set wins.</li>
 * <li> Execute the function tag and set the attributes in the context. If you want key/values in
 *      the CSV and properties files to override the script attribute, then the function point 
 *      author uses the setDefaultVariableValue() method in the set method for that attribute.</li>
 * <li> If the function point is using a map-variable, then override all settings to set the 
 *      variable to the mapFrom variable name.</li>
 * </ol>
 * </p>
 * @jameleon.function name="testcase"
 * @jameleon.function name="test-case"
 */
public class TestCaseTag extends AbstractCsvTag {

    protected long maxExecutionTime = 0;
    protected String assertGreaterThanLevel;
    protected String assertLessThanLevel;
    protected String assertLevel;
    protected String assertLevels;
    protected String testEnvironment = "";
    protected String organization = "";
    protected String csvValueSeparator;
    protected String bugTrackerUrl;
    protected String genTestCaseDocsEncoding = JameleonDefaultValues.FILE_CHARSET;
    protected String propsName;
    protected boolean useCSV;
    protected boolean trace;
    protected boolean genTestCaseDocs = true;
    protected boolean executeTestCase = true;

    protected String testCaseResultDataRowTemplate = JameleonDefaultValues.TEST_CASE_RESULT_DATA_ROW_TEMPLATE;
    protected String testCaseResultSessionTemplate = JameleonDefaultValues.TEST_CASE_RESULT_SESSION_TEMPLATE;
    protected String testCaseResultFunctionTemplate = JameleonDefaultValues.TEST_CASE_RESULT_FUNCTION_TEMPLATE;
    protected String testCaseMainPageTemplate = JameleonDefaultValues.TEST_CASE_RESULT_MAIN_PAGE_TEMPLATE;
    protected String testCaseSummaryTemplate = JameleonDefaultValues.TEST_CASE_SUMMARY_TEMPLATE;
    protected String testCaseResultSummaryTemplate = JameleonDefaultValues.TEST_CASE_RESULT_SUMMARY_TEMPLATE;
    protected String testCaseResultTemplate = JameleonDefaultValues.TEST_CASE_RESULT_TEMPLATE;

    protected TestCaseEventHandler eventHandler = TestCaseEventHandler.getInstance();
    protected long startTime;
    protected File resultsFile;
    protected CountableDataDrivableResultContainer rowResultContainer;

    /**
     * DEFAULT - true.
     * If set to false, then don't error and don't even log the test case. If a file is found, then go ahead and log test case results
     */
    protected boolean failOnCSVFileNotFound = true;
    /**
     * Used to flag if a CSV file is not found. This is used only when failOnCSVFileNotFound is set to false
     */
    protected boolean failedOnDataDriver = false;
    /**
     * The test case results which are a complete set of results for every tag executed.
     */
    protected TestCaseResult results;

    //This is a workaround to get correct failed and passed results
    protected DataDrivableRowResult ddRowResult;

    //Additional getter required to get row number in session tag if useCSV=true
    public DataDrivableRowResult getDdResult() {
        return ddRowResult;
    }

    protected ArrayList keysSet = new ArrayList();
    protected TestCase testCase = new TestCase();

    /**
     * Only store the displayed screen being tested to a file on an error.
     */
    protected boolean storeDisplayOnError = true; 
    /**
     * Store all displayed screens to a file..
     */
    protected boolean storeEveryDisplay = false;
    /**
     * The baseDir where everything else is based
     */
    protected File baseDir = JameleonDefaultValues.BASE_DIR;
    /**
     * The directory name to store the results to. Defaults to ./jameleon_test_results
     */
    protected File resultsDir = new File(baseDir, JameleonDefaultValues.RESULTS_DIR);
    /**
     * The timestamped directory to store the results to.
     */
    protected File timestampedResultsDir;
    /**
     * The name of the Jameleon configuration file. (default is defined in {@link net.sf.jameleon.util.Configurator})
     */
    protected String jameleonConfigName = Configurator.DEFAULT_CONFIG_NAME;
    /**
     * Enable/disable validity checking for SSL certificates. Default is true (enabled).
     * Set to false to prevent exceptions from being thrown for invalid SSL certs.
     */
    protected boolean enableSslCertCheck = true;

    protected final static String APPLICATIONS_PROPERTIES = "Applications";
    protected final static int DEFAULT_ROW = 0;
    protected final static String DEFAULT_VALUE_SEPARATOR = ",";


     /**
     * The needs verified by the successful completion of this test case (separated by semi-colon).
     * @jameleon.attribute
     */
    protected String needsVerified;
    /**
     * The requirements verified by the successful completion of this test case (separated by semi-colon).
     * @jameleon.attribute
     */
    protected String requirementsVerified;

    public List<String> getNeedsVerified() {
        List<String> needsList = new ArrayList<String>();
        if (needsVerified != null) {
            String[] needsArray = needsVerified.split(";");
            for (int ii = 0; ii < needsArray.length; ii++) {
                needsList.add(needsArray[ii]);
            }
        }
        return needsList;
    }

    public List<String> getRequirementsVerified() {
        List<String> requirementsList = new ArrayList<String>();
        if (requirementsVerified != null) {
            String[] requirementsArray = requirementsVerified.split(";");
            for (int ii = 0; ii < requirementsArray.length; ii++) {
                requirementsList.add(requirementsArray[ii]);
            }
        }
        return requirementsList;
    }



    /**
     * Gets the logger used for this tag
     * @return the logger used for this tag.
     */
    protected Logger getLogger(){
        return Logger.getLogger(TestCaseTag.class.getName());
    }

    /**
     * Gets the trace message when the execution is beginning and ending.
     * The message displayed will already start with BEGIN: or END:
     * @return the trace message when the execution is just beginning and ending.
     */
    protected String getTagTraceMsg(){
        return "parsing " + getCsvFile();
    }

    /**
     * Describe the tag when error messages occur.
     * The most appropriate message might be the tag name itself.
     * @return A brief description of the tag or the tag name itself.
     */
    public String getTagDescription(){
        return "testcase tag";
    }

    protected TestCaseTag getTestCaseTag(){
        return this;
    }

    /**
     * Used to add key/values to a local context for multiple variable
     * substitution. Since a test case can have multiple sessions and sessions
     * are application specific, then variables with ${varName} in them can
     * be different values depending on the application settings in the Applications.properties.
     */
    public void addVariablesToRowData(Map vars){
        rowData.putAll(vars);
        substituteKeyValues();
        traceMsg(getTraceKeyValuePairs(vars.keySet(), context.getVariables()));
    }

    /**
     * Gets the session template to be used to generate the session result.
     * This is searched for in the classpath.
     * @return the session template to be used to generate the test case docs.
     */
    public String getTestCaseResultSessionTemplate() {
        return testCaseResultSessionTemplate;
    }

    /**
     * Sets the session template to be used to generate the session result.
     * This is searched for in the classpath.
     * @param testCaseResultSessionTemplate the session template to be used to generate the test case docs.
     * @jameleon.attribute
     */
    public void setTestCaseResultSessionTemplate(String testCaseResultSessionTemplate) {
        this.testCaseResultSessionTemplate = testCaseResultSessionTemplate;
    }

    /**
     * Gets the data row template to be used to generate the data drivable result.
     * This is searched for in the classpath.
     * @return the data-drivable template to be used to generate the test case docs.
     */
    public String getTestCaseResultDataRowTemplate() {
        return testCaseResultDataRowTemplate;
    }

    /**
     * Sets the data row template to be used to generate the data drivable result.
     * This is searched for in the classpath.
     * @param testCaseResultDataRowTemplate the data-drivable template to be used to generate the test case docs.
     * @jameleon.attribute
     */
    public void setTestCaseResultDataRowTemplate(String testCaseResultDataRowTemplate) {
        this.testCaseResultDataRowTemplate = testCaseResultDataRowTemplate;
    }

    /**
     * Gets the template to be used to generate the function result.
     * This is searched for in the classpath.
     * @return the entry leaf template to be used to generate the test case docs.
     */
    public String getTestCaseResultFunctionTemplate() {
        return testCaseResultFunctionTemplate;
    }

    /**
     * Sets the function result template to be used to generate the test case result.
     * This is searched for in the classpath.
     * @param testCaseResultFunctionTemplate The entry leaf template to be used to generate the test case docs.
     * @jameleon.attribute
     */    
    public void setTestCaseResultFunctionTemplate(String testCaseResultFunctionTemplate) {
        this.testCaseResultFunctionTemplate = testCaseResultFunctionTemplate;
    }

    /**
     * Gets the template to be used to generate the main page of the results..
     * This is searched for in the classpath.
     * @return the template to be used to generate the test case main page.
     */
    public String getTestCaseMainPageTemplate() {
        return testCaseMainPageTemplate;
    }

    /**
     * Sets the template to be used to generate the main page of the results..
     * This is searched for in the classpath.
     * @param testCaseMainPageTemplate The template.
     * @jameleon.attribute
     */
    public void setTestCaseMainPageTemplate(String testCaseMainPageTemplate) {
        this.testCaseMainPageTemplate = testCaseMainPageTemplate;
    }

    /**
     * Gets the template to be used to generate the test case summary page of the results..
     * This is searched for in the classpath.
     * @return the template to be used to generate the test case summary docs.
     */
    public String getTestCaseSummaryTemplate() {
        return testCaseSummaryTemplate;
    }

    /**
     * Sets the template to be used to generate the test case summary page of the results.
     * This is searched for in the classpath.
     * @param testCaseSummaryTemplate The template to be used to generate the test case summary docs.
     * @jameleon.attribute
     */
    public void setTestCaseSummaryTemplate(String testCaseSummaryTemplate) {
        this.testCaseSummaryTemplate = testCaseSummaryTemplate;
    }

    /**
     * Gets the template to be used to generate the test case result summary page.
     * This is searched for in the classpath.
     * @return The template to be used to generate the test case result summary page.
     */
    public String getTestCaseResultSummaryTemplate() {
        return testCaseResultSummaryTemplate;
    }

    /**
     * Sets the template to be used to generate the test case result summary page.
     * This is searched for in the classpath.
     * @param testCaseResultTemplate The template to be used to generate the test case result page.
     * @jameleon.attribute
     */
    public void setTestCaseResultTemplate(String testCaseResultTemplate) {
        this.testCaseResultTemplate = testCaseResultTemplate;
    }

    /**
     * Gets the template to be used to generate the test case result page.
     * This is searched for in the classpath.
     * @return The template to be used to generate the test case result page.
     */
    public String getTestCaseResultTemplate() {
        return testCaseResultTemplate;
    }

    /**
     * Sets the template to be used to generate the test case result summary page.
     * This is searched for in the classpath.
     * @param testCaseResultSummaryTemplate The template to be used to generate the test case result summary page.
     * @jameleon.attribute
     */
    public void setTestCaseResultSummaryTemplate(String testCaseResultSummaryTemplate) {
        this.testCaseResultSummaryTemplate = testCaseResultSummaryTemplate;
    }

    /**
     * Sets the directory where the results will be written to.
     * @param resultsDir - the directory where the results will be written to.
     * @jameleon.attribute
     */
    public void setResultsDir(File resultsDir){
        this.resultsDir = new File(resultsDir.getPath());
    }

    /**
     * Sets the timestamped results directory where the test case result documentation will be stored.
     * @param timestampedResultsDir The timestamped results directory where the test case result
     * documentation will be stored.
     */
    public void setTimestampedResultsDir(File timestampedResultsDir){
        this.timestampedResultsDir = timestampedResultsDir;
    }

    /**
     * Gets the timestamped results directory where the test case result documentation will be stored.
     * @return the timestamped results directory where the test case result documentation will be stored.
     */
    public File getTimestampedResultsDir(){
        return timestampedResultsDir;
    }

    /**
     * Sets the test case to record the state of the application at a defined <code>event</code>
     * @param event - An event at which the state of the application will be stored. Valid values are:
     * <ul>
     *      <li><code>storeStateNever</code> - never store the state of the application.</li>
     *      <li><code>storeStateOnChange</code> - store the state of the applcation on any state change.</li>
     *      <li><code>storeStateOnError</code> - store the state of the application only on an error. DEFAULT</li>
     * </ul>
     * If the <code>event</code> is not valid, then it is not set.
     * @jameleon.attribute
     */
    public void setStoreStateEvent(String event){
        if ("storeStateNever".equalsIgnoreCase(event) ){
            setStoreStateNever(true);
        }else if ("storeStateOnChange".equalsIgnoreCase(event)) {
            setStoreStateOnChange(true);
        }else if ("storeStateOnError".equalsIgnoreCase(event) ){
            setStoreStateOnError(true);
        }else{
            log.warn(event +" not recognized as a valid option for storeStateEvent! Valid options are: storeStateOnError, storeStateOnChange, storeStateNever.");
        }
    }

    /**
     * Sets the test case to record the state of the application whenever the application's state changes.
     * @param all - Set to <code>true</code> to record all responses. Defaults to <code>false</code>
     * @jameleon.attribute
     */
    public void setStoreStateOnChange(boolean all){
        if (all) {
            stateStorer.setStorableEvent(StateStorer.ON_STATE_CHANGE_EVENT);
        }
    }

    /**
     * Sets the test case to record the state of the application on errors.
     * @param onError - Set to <code>true</code> to record responses ONLY on errors. Defaults to <code>true</code>
     * @jameleon.attribute
     */
    public void setStoreStateOnError(boolean onError){
        if (onError) {
            stateStorer.setStorableEvent(StateStorer.ON_ERROR_EVENT);
        }
    }

    /**
     * Sets the test case to never record the state of the application.
     * @param none - Set to <code>true</code> to never record responses. Defaults to <code>false</code>
     * @jameleon.attribute
     */
    public void setStoreStateNever(boolean none){
        if (none) {
            stateStorer.setStorableEvent(StateStorer.ON_NO_EVENT);
        }
    }

    /**
     * Gets the directory where the results will be written to.
     * @return the directory where the results will be written to.
     */
    public File getResultsDir(){
        return getResultsDir(true);
    }

    /**
     * Gets the directory where the results will be written to.
     * @param includeName set to true to include the test case name in the results directory.
     * @return the directory where the results will be written to.
     */
    public File getResultsDir(boolean includeName){
        File dir = getTimestampedResultsDir();
        if (dir == null){
            if (includeName){
                if (baseDir.equals(resultsDir.getParentFile())){
                    dir = new File(resultsDir, getName());
                }else{
                    dir = new File(baseDir, resultsDir.getPath() + File.separator + getName());
                }
            }else{
                dir = resultsDir;
            }
        }
        return dir;
    }

    /**
     * @return the directory where the results will be stored.
     * @param rowCount The row number the test case is on
     */
    protected File getResultsDir(int rowCount){
        return new File(getResultsDir(),""+rowCount);
    }

    /**
     * Gets the maximum execution time before the test case fails
     * @return the maximum execution time before the test case fails
     */
    public long getMaxExecutionTime(){
        return maxExecutionTime;
    }

    /**
     * Sets the maximum execution time before the test case fails
     * @param maxExecutionTime - the maximum execution time before the test case fails
     * @jameleon.attribute
     */
    public void setMaxExecutionTime(long maxExecutionTime){
        this.maxExecutionTime = maxExecutionTime;
    }

    /**
     * @return the <code>StateStorer</code> instance created by this TestCaseTag.
     */
    public StateStorer getStateStorer(){
        return stateStorer;
    }
    
    /**
     * Used internally to mark whether the DataDrivable had a problem. This is used for the failOnCSVFileNotFound option only
     * @param failedOnDataDriver - Set to <code>true</code> if a problem due to DataDrivable occured.
     * @jameleon.attribute
     */
    public void setFailedOnDataDriver(boolean failedOnDataDriver){
        this.failedOnDataDriver = failedOnDataDriver;
    }

    /**
     * Sets the failOnCSVFileNotFound property
     * @param failOnCSVFileNotFound - Set to <code>false</code> to not log a failure due to a CSV FileNotFoundException
     * @jameleon.attribute
     */
    public void setFailOnCSVFileNotFound(boolean failOnCSVFileNotFound){
        this.failOnCSVFileNotFound = failOnCSVFileNotFound;
    }

    /**
     * @return the list of keys that have been put into the context.
     */
    public List getKeySet() {
        return keysSet;
    }

    /**
     * @return the organization or affiliate this application will be tested against.
     * This would used for when there is one application for many different datasources
     * like our banking applications that one day will run for multiple banks. This is also
     * important because the URLs change between organizations as well.
     */
    public String getOrganization() {
        return organization;
    }

    /**
     * Sets the organziation or company that this test will be run against.
     * This is used the same as the testEnvironment to find the CSV file or use values
     * from a properties file specific to an organization.
     * @param organization - The organziation or company that this test will be run against.
     * @jameleon.attribute
     */
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * @return the environment the test cases will be run under
     */
    public String getTestEnvironment() {
        return this.testEnvironment;
    }

    /**
     * Sets the environment to which testing system the testcase will be run in.
     * Information like the starting url, can be based on this. This is 
     * also used to find the CSV file if one is used for the test case. 
     * This can be set as a global variable.
     * @param testEnvironment Some examples might include, localhost, dev, test, stage, production ...:
     * @jameleon.attribute
     */
    public void setTestEnvironment(String testEnvironment) {
        this.testEnvironment = testEnvironment;
    }

    /**
     * @return the test cases that were run under this environment
     */
    public TestCaseResult getResults() {
        return results;
    }

    public void setResults(TestCaseResult results){
        this.results = results;
    }

    /**
     * Sets whether a CSV file should be used for this testcase or not.
     * @param useCSV - If set to true, then this test case will grab all of it's data from a CSV file
     * @jameleon.attribute
     */
    public void setUseCSV(boolean useCSV) {
        this.useCSV = useCSV;
    }

    /**
     * Sets whether a std out message should be sent before and after the execution of a functional point.
     * @param trace - If set to true, then this test case will show messages before and after execution of each functional point.
     * @jameleon.attribute
     */
    public void setTrace(boolean trace) {
        this.trace = trace;
    }

    /**
     * Gets whether a std out message should be sent before and after the execution of a functional point.
     * @return true, if this test case will show messages before and after execution of each functional point.
     */
    public boolean getTrace() {
        return trace;
    }

    /**
     * Sets the base directory of the project
     * @param baseDir - The base directory of the project
     * @jameleon.attribute
     */ 
    public void setBaseDir(File baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * @return The base directory of the project
     */ 
    public File getBaseDir() {
        return baseDir;
    }

    /**
     * Gets the directory of where csv files should be read from,
     * given the environment settings.
     * @param calculate - set to true to calculate in testEnvironment and organization if they apply
     * @return The csv file to run the test against
     */
    public File getCsvDir(boolean calculate){
        File dir;
        if (calculate) {
            dir = getCsvDir();
        }else{
            String filename = baseDir.getPath() + File.separator + dataDir.getPath() + File.separator;
            dir = new File(filename);
        }
        return dir;
    }


    /**
     * @return the level of the asserts to run that are greater than this number.
     */
    public String getAssertGreaterThanLevel() {
        return this.assertGreaterThanLevel;
    }

    /**
     * @param assertGreaterThanLevel the level of the asserts to run that are greater than this number.
     * @jameleon.attribute
     */
    public void setAssertGreaterThanLevel(String assertGreaterThanLevel) {
        this.assertGreaterThanLevel = assertGreaterThanLevel;
    }

    /**
     * @return the level of the asserts to run that are less than this number.
     */
    public String getAssertLessThanLevel() {
        return this.assertLessThanLevel;
    }

    /**
     * @param assertLessThanLevel the level of the asserts to run that are less than this number.
     * @jameleon.attribute
     */
    public void setAssertLessThanLevel(String assertLessThanLevel) {
        this.assertLessThanLevel = assertLessThanLevel;
    }

    /**
     * @return the level of the asserts to run that are equal to this number.
     */
    public String getAssertLevel() {
        return this.assertLevel;
    }

    /**
     * @param assertLevel the level of the asserts to run that are equal to this number.
     * @jameleon.attribute
     */
    public void setAssertLevel(String assertLevel) {
        this.assertLevel = assertLevel;
    }

    /**
     * @return the level of the asserts to run that are equal to this list of numbers.
     */
    public String getAssertLevels() {
        return this.assertLevels;
    }

    /**
     * Sets the level of the asserts to run that are equal to this list of numbers.
     * @param assertLevels A list of assert levels to run in the this test case
     * @jameleon.attribute
     */
    public void setAssertLevels(String assertLevels) {
        this.assertLevels = assertLevels;
    }

    /**
     * Sets the test case to generate the test case docs based on the javadocs of the functional points
     * and the functionId's of the functional points in the test case.
     * @param genTestCaseDocs - set to true if the test case docs are to be generated
     * @jameleon.attribute
     */
    public void setGenTestCaseDocs(boolean genTestCaseDocs){
        this.genTestCaseDocs = genTestCaseDocs;
    }

    /**
     * @return The test case for documentation purposes.
     */
    public TestCase getTestCase(){
        return testCase;
    }

    /**
     * @return true if the test is actually supposed to be executed. The default is <code>true</code>
     */
    public boolean isExecuteTestCase(){
        return executeTestCase;
    }

    /**
     * @return true if the test case results docs are to be generated.
     * The default is <code>true</code>
     */
    public boolean isGenTestCaseDocs(){
        return genTestCaseDocs;
    }

    /**
     * Sets the test case to be executed or not.
     * @param executeTestCase - Set to <code>false</code> if the functionality of the test case is not to be executed.
     * The default is <code>true</code>. This would be set to false if only the test case docs are to be generated.
     * @jameleon.attribute
     */
    public void setExecuteTestCase(boolean executeTestCase){
        this.executeTestCase = executeTestCase;
    }

    /**
     * Sets the url of the bugtracking tool used so the bug's listed for the test case are linked.
     * @return the bug tracker url
     */
    public String getBugTrackerUrl(){
        return this.bugTrackerUrl;
    }

    /**
     * Sets the url of the bugtracking tool used so the bug's listed for the test case are linked.
     * @param bugTrackerUrl The url of the bugtracking tool used so the bug's listed for the test case are linked. 
     * @jameleon.attribute
     */
    public void setBugTrackerUrl(String bugTrackerUrl){
        this.bugTrackerUrl = bugTrackerUrl;
    }

    /**
     * @return the character set encoding to use for the test case in XML.
     */
    public String getGenTestCaseDocsEncoding(){
        return genTestCaseDocsEncoding;
    }

    /**
     * Sets the charset encoding.
     * @param encoding - the character set encoding to use for the test case in XML.
     * @jameleon.attribute
     */
    public void setGenTestCaseDocsEncoding(String encoding){
        this.genTestCaseDocsEncoding = encoding;
    }

    /**
     * Gets the name of the properties file (minus the .properties) to read in into the context
     * @return the name of the properties file (minus the .properties) to read in into the context
     */
    public String getPropsName(){
        return propsName;
    }

    /**
     * Sets the name of the properties file (minus the .properties) to read in into the context
     * @param propsName - name of the properties file (minus the .properties) to read in into the context
     * @jameleon.attribute
     */
    public void setPropsName(String propsName){
        this.propsName = propsName;
    }

    /**
     * Sets the configuration file Jameleon uses to configure itself.
     * (default is defined in {@link net.sf.jameleon.util.Configurator})
     * Do not use. This is used for internal testing purposes only.
     * @param configName - the configuration file Jameleon uses to configure itself.
     * @jameleon.attribute
    */
    public void setJameleonConfigName(String configName) {
        jameleonConfigName = configName;
        Configurator.clearInstance();
        Configurator config = Configurator.getInstance();
        config.setConfigName(jameleonConfigName);
    }

    public String getJameleonConfigName(){
        return jameleonConfigName;
    }

    /**
     * Query whether SSL cert validity checking is enabled.
     * @return - If true, an exception will be thrown when an invalid SSL cert is encountered.
     * If false, invalid SSL certs will be accepted.
    */
    public boolean isEnableSslCertCheck() {
        return enableSslCertCheck;
    }
    /**
     * Enable or disable validity checking of SSL certificates.
     * @param enable - If "true", an exception will be thrown when an invalid SSL cert is encountered.
     * If "false", invalid SSL certs will be accepted. If not set, the default is "true".
     * @jameleon.attribute
     */
    public void setEnableSslCertCheck(boolean enable) {
        enableSslCertCheck = enable;
    }

    protected void setLocationAware(LocationAware la){
        if (la.getLineNumber() == -1) {
            la.setColumnNumber(getColumnNumber());
            la.setLineNumber(getLineNumber());
        }
        if (la.getFileName() == null) {
            la.setFileName(getFileName());
        }
    }

    /**
     * Execute the rest of the test script.
     * @param rowNum - if a csv file is used, then this pertains to the row number
     *                 the test case is being executed against. If a CSV file is not
     *                 used, then 0 is sent.
     * @param result - the results to run with.
     */
    public void invokeChildren(int rowNum, JameleonTestResult result){
        try {
            invokeBody(xmlOut);
        } catch (JellyTagException jte){
            Throwable err = jte;
            LocationAware la = jte;
            if (err.getCause() != null && err.getCause() instanceof LocationAware) {
                err = err.getCause();
                la = (LocationAware)err;
            }
            setLocationAware(la);
            JameleonScriptException jse = new JameleonScriptException(err.getMessage(), la);
            logError(err);
            result.setError(jse);
        } catch (ThreadDeath td){
            throw td;
        } catch (Throwable t) {
            if (trace){
                log.info(JameleonUtility.getStack(t));
            }
            LocationAware la = null;
            Throwable err = t;
            if (t.getCause() != null && t.getCause() instanceof LocationAware) {
                err = t.getCause();
                la = (LocationAware)err;
            }else if (t instanceof LocationAware) {
                la = (LocationAware)t;
            }

            JameleonScriptException jse = null;
            if (la != null) {
                setLocationAware(la);
                jse = new JameleonScriptException(err.getMessage(), la);
            }
            if (jse != null) {
                err = jse;
            }
            logError(err);
            result.setError(t);
        } finally{
            result.setExecutionTime(System.currentTimeMillis() - startTime);
        }
        failedOnCurrentRow = false;
    }

    /**
     * A CsvExecutable implementation method that gets called once for every row in the csv file.
     * This does not include the top row which should only define the variable names
     */
    public void executeDrivableRow(int rowNum){
        traceMsg("BEGIN executing row number "+rowNum);
        TestResultWithChildren res = results;
        if (rowResultContainer != null) {
            res = rowResultContainer;
        }
        ddRowResult = new CountableDataDrivableRowResult(fp, res);
        ddRowResult.copyLocationAwareProperties(this);
        ddRowResult.setRowData(new HashMap(rowData));
        ddRowResult.setRowNum(rowNum);
        stateStorer.setStoreDir(getResultsDir(rowNum));
        invokeChildren(rowNum, ddRowResult);
        traceMsg("END executing row number "+rowNum);
    }

    protected void initResults(){
        results = new TestCaseResult(getFunctionalPoint());
        results.copyLocationAwareProperties(this);
        if (useCSV) {
            rowResultContainer = new CountableDataDrivableResultContainer(getFunctionalPoint(), results);
            rowResultContainer.copyLocationAwareProperties(this);
        }
    }

    /**
     * Set up the test environment.
     *
     * @throws JellyTagException if the tag is not in the correct state.
     */
    public void setUp() throws JellyTagException{
        initResults();
        String script = getFileName();
        if (script != null) {
            testCase.readFromScript(script);
        }
        //Some strangeness here. The problem is I want the logic to figure out the 
        // test case name in only one spot.
        if (testCase.getName() != null) {
            //testCase.getName() should only be null for the unit tests
            name = testCase.getName();
        }
        results.setTestName(name);
        loadJameleonConfig();
        //Must load this after loading the values from jameleon.conf
        //so that it can be set from both places.
        testCase.setOrganization(organization);
        testCase.setTestEnvironment(testEnvironment);

        if (propsName != null) {
            Properties p = new Properties();
            getPropertiesForName(propsName, p);
            inilializeProps(p);
        }
        validateAttributes();
        setAssertLevels();
        if (organization != null && organization.trim().length() > 0) {
            context.setVariable("organization", organization);
        }
        setUpDataDrivable();
    }
    /**
     * Records a child result. Used as a helper method for the ResultRecordable
     * implementation methods. This method overrides the default behavior of 
     * AbstractDataDrivableTag.
     * @param result - the result to record
     */
    protected void recordResult(JameleonTestResult result){
        if (ddRowResult != null) {
            ddRowResult.addChildResult(result);
            result.setParentResults(ddRowResult);
        }else if (results != null) {
            results.addChildResult(result);
            result.setParentResults(results);
        }
    }

    /**
     * This method executes the tags inside the test-case tag. If <code>useCSV=true</code>, then the data for
     * the functional points will be grabbed from a CSV file. Otherwise the attributes are all expected to be set
     * the contained functional points. If the functional points' attributes are set and useCSV is set to true,
     * then the data in the CSV file will override the data set in the function points' attributes.
     */
    public void doTag(XMLOutput out) throws MissingAttributeException, JellyTagException{
        xmlOut = out;
        init();
        setUp();
        eventHandler.beginTestCase(this);
        //Call this after beginTestCase so that the timestamp directory can be set up
        setStateStoreOptions();
        try{
            testForUnsupportedAttributesCaught();
            broker.transferAttributes(context);
            broker.validate(context);
        }catch(JameleonScriptException jse){
            results.setError(jse);
            log.info(results);
            eventHandler.endTestCase(this);
            tearDown();
            throw jse;
        }
        try{
            if (executeTestCase) {
                if (useCSV) {
                    spanCSV();
                }else{
                    executeNoCSV();
                }
                if (maxExecutionTime > 0 && maxExecutionTime < results.getExecutionTime()) {
                    String msg = "The maximum execution time <"+maxExecutionTime+"> was exceeded <"+results.getExecutionTime()+">!";
                    results.setError(new JameleonScriptException(msg, this));
                }
                if ( failOnCSVFileNotFound || (!failOnCSVFileNotFound && !failedOnDataDriver) ) {
                    if (results.failed()) {
                        JellyTagException jte = null;
                        if (results.getError() != null) {
                            jte = createExceptionFromResult(results);
                        }else{
                            StringBuffer sb = new StringBuffer();
                            Iterator it = results.getFailedResults().iterator();
                            JameleonTestResult result;
                            while (it.hasNext()) {
                                result = (JameleonTestResult)it.next();
                                sb.append(result.getTag().getDefaultTagName()).append(":");
                                sb.append(result.getErrorMsg());
                                if (jte == null) {
                                    jte = createExceptionFromResult(result);
                                }
                            }
                        }
                        if (jte != null) {
                            if (results.getError() != null) {
                                results.setError(jte);
                            }
                            throw jte;
                        }
                    }
                }else{
                    setGenTestCaseDocs(false);                    
                }
            }
        }finally{
            eventHandler.endTestCase(this);
            if ( failOnCSVFileNotFound || (!failOnCSVFileNotFound && !failedOnDataDriver) ) {
                log.info(results);
            }
            tearDown();
        }
    }

    protected JellyTagException createExceptionFromResult(JameleonTestResult jtr){
        return createExceptionFromResult(jtr.getErrorMsg(), jtr);
    }

    protected JellyTagException createExceptionFromResult(String message, JameleonTestResult jtr){
        return new JellyTagException(message, jtr.getError(), jtr.getFileName(), jtr.getElementName(), jtr.getColumnNumber(), jtr.getLineNumber());
    }

    protected void executeNoCSV(){
        executeBody(new Runnable(){
                public void run() {
                    invokeChildren(1, results);
                }});
    }

    /**
     * useCSV is set to true, data-drive this CSV file.
     */
    protected void spanCSV(){
        final DataDrivable dd = this;
        csv.setFile(getCsvFile());
        executeBody(new Runnable(){
                public void run() {
                    try{
                        traceMsg("Begin parsing: \""+getCsvFile()+"\"");
                        executer.executeData(dd, false);
                        traceMsg("End parsing \""+getCsvFile()+"\"");
                    }catch(FileNotFoundException fnfe){
                        failedOnDataDriver = true;
                        results.setError(fnfe);
                    }catch(IOException ioe){
                        failedOnDataDriver = true;
                        results.setError(new JameleonScriptException(" Trouble parsing "+getCsvFile()));
                    }catch(IllegalStateException ise){
                        failedOnDataDriver = true;
                        results.setError(ise);
                    }catch(RuntimeException re){
                        if (re.getCause() != null) {
                            results.setError(re.getCause());
                        }
                    }
                }});
    }

    /**
     * Clean things up after the test case has been executed.
     */
    public void tearDown(){
        Configurator.clearInstance();
        Iterator it = keysSet.iterator();
        while (it.hasNext()) {
            context.removeVariable((String)it.next());
        }
        results.setTag(fp.cloneFP());
        resetFunctionalPoint();
    }

    protected void setStateStoreOptions(){
        stateStorer.setStoreDir(getTimestampedResultsDir());
    }

    protected void executeBody(Runnable r){
        startTime = System.currentTimeMillis();
        try{
            r.run();
        }finally{
            results.setExecutionTime(System.currentTimeMillis() - startTime);
        }
    }

    /**
     * Gets the results file that represents the test case execution.
     * @return the results file that represents the test case execution.
     */
    public File getResultsFile(){
        if (genTestCaseDocs && resultsFile == null) {
            resultsFile = new File(getResultsDir(), File.separator+"index.html");
        }
        return resultsFile;
    }

    private InputStream getInputStream(String fileName){
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream input = null;
        if (classLoader == null) {
            classLoader = this.getClass().getClassLoader();
        } else {
            if (classLoader.getResourceAsStream( fileName ) == null) {
                classLoader = this.getClass().getClassLoader();
            }
        }
        if (classLoader != null) {
            input = classLoader.getResourceAsStream(fileName);
        }
        return input;
    }


    /**
     * This method creates a new Properties Object populated only with the
     * properties for application desired. The keys for these properties are
     * no longer start with the application name. For example, if a property
     * named "personalBanking.host" exists in the original Properties Object
     * and this method is called with "personalBanking" as the <code>applicationName</code>
     * then the new property name will be "host".
     * @param applicationName The name of the application that you want properties for.
     * @return a Properties Object populated only with the
     * properties for application desired
     */ 
    public Properties getPropertiesForApplication(String applicationName) {
        ResourceBundle props = null;
        try {
            props = loadApplicationProperties();
        } catch (IOException mre) {
            if (testEnvironment != null && testEnvironment.length() > 1) {
                traceMsg("not reading in "+testEnvironment+"-Applications.properties");
            }
        }
        Properties p = new Properties();
        String keyWithOrg = null;
        if (organization != null && !organization.trim().equals("")) {
            keyWithOrg = organization+"."+applicationName;
        }
        if (props != null) {
            addValuesFromResourceBundle(props, p, keyWithOrg, true);
            addValuesFromResourceBundle(props, p, applicationName, false);
        }
        getPropertiesForName(applicationName, p);

        try{
            ResourceBundle appProps = getResourceBundle(applicationName);
            if (organization != null && organization.trim().length() > 0) {
                addValuesFromResourceBundle(appProps, p, organization, false);
            }
            addValuesFromResourceBundle(appProps, p, "", false);
        }catch(IOException mre){
            //Do nothing since this file is optional
        }
        try{
            ResourceBundle baseProps = getResourceBundle("Applications");
            addValuesFromResourceBundle(baseProps, p, keyWithOrg, false);
            addValuesFromResourceBundle(baseProps, p, applicationName, false);
        }catch(IOException mre){
            //Do nothing since this file is optional
        }
        inilializeProps(p);
        substituteValues(p);
        return p;
    }

    private void inilializeProps(Properties p){
        Iterator it = rowData.keySet().iterator();
        String key, value;
        while (it.hasNext()) {
            key = (String)it.next();
            if (rowData.get(key) instanceof ArrayList){
                value = ((ArrayList)rowData.get(key)).toString();
            } else {
                value = (String)rowData.get(key);
            }
            if (value != null) {
                p.setProperty(key, value);
            }
        }
    }

    private void getPropertiesForName(String propsName, Properties p){
        try{
            ResourceBundle appProps = getResourceBundle(propsName);
            if (organization != null && organization.trim().length() > 0) {
                addValuesFromResourceBundle(appProps, p, organization, false);
            }
            addValuesFromResourceBundle(appProps, p, "", false);
        }catch(IOException mre){
            //Do nothing since this file is optional
        }
    }

    /**
     * Do variable substition based on keys in the iterator.
     * @param p - the properties to be substituted.
     */
    protected void substituteValues(Properties p){
        JexlExpressionFactory exfact = new JexlExpressionFactory();
        String key, value;
        Iterator it = p.keySet().iterator();
        while (it.hasNext()) {
            key = (String) it.next();
            value = p.getProperty(key);
            if (value != null) {
                try{
                    Expression ex = CompositeExpression.parse(value, exfact);
                    context.setVariable(key, ex.evaluateAsString(context));
                }catch(JellyException je){
                    je.printStackTrace();
                }
            }
        }
    }

    /**
     * Do variable subsitution on values stored from data file.
     */
    public void substituteKeyValues(){
        JexlExpressionFactory exfact = new JexlExpressionFactory();
        String key, value;
        Object obj;
        Set keys = rowData.keySet();
        Iterator it = keys.iterator();
        while (it.hasNext()) {
            key = (String) it.next();
            obj = rowData.get(key);
            //Only try to substitute values on String objects
            if (obj instanceof String) {
                value = (String)rowData.get(key);
                if (value != null) {
                    try{
                        Expression ex = CompositeExpression.parse(value, exfact);
                        context.setVariable(key, ex.evaluateAsString(context));
                    }catch(JellyException je){
                        je.printStackTrace();
                    }
                }
            }
        }
    }


    protected void addValuesFromResourceBundle(ResourceBundle props, Properties p, String startOfKey, boolean overrideValue){
        Enumeration e = props.getKeys();
        String key, newKey;
        String keyToFind = (startOfKey != null && startOfKey.length() > 0 ) ? startOfKey+"." : "";
        while (e.hasMoreElements()) {
            key = (String)e.nextElement();
            if (key != null && key.startsWith(keyToFind)) {
                newKey = key.substring(keyToFind.length());
                if (p.getProperty(newKey) == null || overrideValue) {
                    p.setProperty(newKey, props.getString(key));
                }
                //Put the variables in the session if they haven't already been set.
                if ( context != null && (overrideValue || context.getVariable(newKey) == null) ) {
                    keysSet.add(newKey);
                    context.setVariable(newKey, props.getString(key));
                }
            }
        }
    }

    /**
     * Validate the parameters
     * @throws MissingAttributeException If a parameter is invalid.
     */
    protected void validateAttributes() throws MissingAttributeException{
        if (name == null) {
            throw new MissingAttributeException("name");
        }
        if (!isValidateAssertLevel(assertGreaterThanLevel)) {
            throw new MissingAttributeException("assertGreaterThanLevel must be between SMOKE and REGRESSION");
        }
        if (!isValidateAssertLevel(assertLessThanLevel)) {
            throw new MissingAttributeException("assertLessThanLevel must be between SMOKE and REGRESSION");
        }
        if (!isValidateAssertLevel(assertLevel)) {
            throw new MissingAttributeException("assertLevel must be between SMOKE and REGRESSION");
        }
        if (assertLevels != null) {
            String[] levels = parseAssertLevels();
            for (int i = 0; i < levels.length; i++) {
                if (!isValidateAssertLevel(levels[i])) {
                    throw new MissingAttributeException("assertLevels must be between SMOKE and REGRESSION");
                }
            }
        }
    }

    protected String[] parseAssertLevels() {
        return assertLevels.split(", ?");
    }

    /**
     * @param level - the assert level to check
     * @return true if the assertLevel is a valid assert level. Must begin with "LEVEL" followed by a digit 1-9.
     */
    protected boolean isValidateAssertLevel(String level) {
        return(getAssertLevelFromString(level) != AssertLevel.INVALID_LEVEL);
    }

    protected void setAssertLevels() {
        AssertLevel al = AssertLevel.getInstance();
        if (assertGreaterThanLevel != null) {
            al.setGreaterThanLevel(getAssertLevelFromString(assertGreaterThanLevel));
        }
        if (assertLessThanLevel != null) {
            al.setLessThanLevel(getAssertLevelFromString(assertLessThanLevel));
        } 
        if (assertLevel != null) {
            al.setLevel(getAssertLevelFromString(assertLevel));
        } 
        if (assertLevels != null && assertLevels.length() > 0) {
            String[] levels = parseAssertLevels();
            for (int i = 0; i < levels.length; i++) {
                al.addLevel(getAssertLevelFromString(levels[i]));
            }
        }
    }

    protected int getAssertLevelFromString(String assertLevel) {
        int lvl = -1;

        if (assertLevel == null || "".equals(assertLevel)) {
            lvl = AssertLevel.NO_LEVEL;
        } else if ("SMOKE".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.SMOKE;
        } else if ("LEVEL1".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.LEVEL1;
        } else if ("LEVEL2".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.LEVEL2;
        } else if ("FUNCTION".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.FUNCTION;
        } else if ("LEVEL4".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.LEVEL4;
        } else if ("LEVEL5".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.LEVEL5;
        } else if ("REGRESSION".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.REGRESSION;
        } else if ("LEVEL7".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.LEVEL7;
        } else if ("LEVEL8".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.LEVEL8;
        } else if ("ACCEPTANCE".equalsIgnoreCase(assertLevel)) {
            lvl = AssertLevel.ACCEPTANCE;
        } else {
            lvl = AssertLevel.INVALID_LEVEL;
        }
        return lvl;
    }

    /**
     * Loads the properties for all applications.
     * @return The ResourceBundle representing the given application.
     * @throws IOException When the given file can not be found in the classpath
     */
    protected ResourceBundle loadApplicationProperties() throws IOException{
        String resName = testEnvironment+"-"+APPLICATIONS_PROPERTIES;
        return getResourceBundle(resName);
    }

    /**
     * Loads the properties for all applications.
     * @param filename - the name of the file
     * @return a ResourceBundle loaded from the class path.
     * @throws IOException when the file can not be found or loaded
     */
    private ResourceBundle getResourceBundle(String filename) throws IOException{
        String resName = filename+".properties";
        InputStream in = getInputStream(resName);
        if (in != null) {
            return new PropertyResourceBundle (getInputStream(resName));
        }else{
            throw new IOException("Couldn't find "+filename);
        }
    }

    protected void setValueFromEnvironment(Configurator config, String key){
            String value = config.getValue(key);
            if (value != null){
                try{
                    BeanUtils.copyProperty(this, key, value);
                }catch (Exception e){
                    //Do nothing
                }
            }
    }

    protected void setFileFromEnvironment(Configurator config, String key){
        String value = config.getValue(key);
        if (value != null && key != null){
            try{
                File file = new File(value);
                File tmpFile;
                if (key.equals("baseDir")) {
                    tmpFile = file;
                }else{
                    tmpFile = new File(baseDir, value);
                }
                if ( tmpFile.exists() ) {
                    BeanUtils.copyProperty(this, key, file);
                }
                if (!tmpFile.exists()) {
                    log.warn("The " + value + " directory does not exist for "+key);
                    log.warn("Leaving "+key+" to it's default value!");
                }
            }catch(Exception iae){
                //Just means I couldn't set the variable.
            }
        }
    }

    /**
     * Loads the properties for all applications.
     */
    protected void loadJameleonConfig() {
        String[] vars = {"testEnvironment","organization","assertGreaterThanLevel",
                         "assertLessThanLevel","assertLevels","assertLevel","bugTrackerUrl",
                         "genTestCaseDocsEncoding", "csvCharset", "trace","genTestCaseDocs",
                         "failOnCSVFileNotFound","executeTestCase", "enableSslCertCheck",
                         "storeStateEvent","genTestCaseDocsTemplate", "includeTagDetailsInResults"};
        String[] fileVars = {"baseDir","resultsDir","csvDir"};
        try {
            Configurator config = Configurator.getInstance();
            config.setConfigName(getJameleonConfigName());
            for (int i = 0; i < vars.length; i++) {
                setValueFromEnvironment(config, vars[i]);
            }
            for (int i = 0; i < fileVars.length; i++) {
                setFileFromEnvironment(config, fileVars[i]);
            }
        } catch (ThreadDeath td){
            throw td;
        } catch (Throwable t)  {
                //Simply means jameleon.conf is not being used.
        }
    }

    /**
     * Logs an error in XML format for easy interpreting later. The error message prints out all known
     * environment variables and information.
     * @param t The Error to log and set in the result
     */
    protected void logError(Throwable t) {
        results.setError(t);
        log.debug(JameleonUtility.getStack(t));
    }

}
