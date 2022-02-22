package org.fhwa.c2cri.plugin.c2cri.tags;

import org.fhwa.c2cri.plugin.c2cri.data.TestCaseDataDriver;
import java.io.File;
import java.net.URL;
import org.apache.log4j.Logger;

import net.sf.jameleon.data.AbstractFileDrivableTag;
import net.sf.jameleon.data.DataDriver;

/**
 * Iterates over all nested tags one time per "row" of test case properties file.
 *
 * @author TransCore ITS, LLC
 * Last Modified:  2/13/2012
 * 
 * For example, to execute the opening of an application and doing something <b>n</b> number of times:
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *   &lt;tcData name="some_file_name"&gt;
 *     &lt;some-session application="someApp" beginSession="true"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."/&gt;
 *     &lt;/some-session&gt;
 *   &lt;/tcData&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 *
 * Maybe opening the application <b>n</b> number of times takes too long, but
 * each of the scenarios still need to be executed. Try putting the tcData tag inside
 * the session tag:
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *   &lt;some-session application="someApp" beginSession="true"&gt;
 *     &lt;tcData name="some_file_name"&gt;
 *       &lt;some-tag-that-uses-context-variables
 *           functionId="Verify successful navigation, using a different variable."/&gt;
 *     &lt;/tcData&gt;
 *   &lt;/some-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 *
 *
 * @jameleon.function name="tcData"
 */
public class TestCaseDataTag extends AbstractFileDrivableTag {

    /** The test case properties. */
    protected TestCaseDataDriver testCaseProperties;
    
    /** The name. */
    protected String name;
    
    /** The user test case data file name. */
    protected String userTestCaseDataFileName;
    
    /** The base test case url. */
    protected URL baseTestCaseURL;

    /**
     * Calculates the location of the state to be stored for any tags under this tag.
     * The result should be a relative path that simply has the row number in it along
     * with some unique identifier for this tag like the handle name or something.
     *
     * @param rowNum the row num
     * @return the location of the state to be stored for any tags under this tag minus the baseDir calculation stuff.
     */
    protected String getNewStateStoreLocation(int rowNum) {
        String fName = getTestCasePropertiesFile().getName();
        int index = fName.lastIndexOf(".");
        if (index == -1) {
            index = fName.length();
        }
        fName = fName.substring(0, index);
        if (rowNum > 0) {
            fName = fName + File.separator + rowNum;
        }
        return fName;
    }

    /**
     * Gets an error message to be displayed when a error occurs due to the DataDriver.
     * @return an error message to be displayed when a error occurs due to the DataDriver.
     */
    protected String getDataExceptionMessage() {
        return "Trouble reading file " + getTestCasePropertiesFile();
    }

    /**
     * Sets up the DataDriver by calling any implementation-dependent
     * methods.
     */
    protected void setupDataDriver() {
        if (userTestCaseDataFileName != null){
            testCaseProperties.setBaseTCFile(baseTestCaseURL);
            testCaseProperties.setContextMap(this.getContext().getVariables());
            testCaseProperties.setFile(new File(userTestCaseDataFileName));
        }else {
            testCaseProperties.setBaseTCFile(baseTestCaseURL);
            testCaseProperties.setContextMap(this.getContext().getVariables());
        }

    }

    /**
     * Gets the file that will be used as a data source.
     * @return The test case properties file to run the test against
     */
    public File getTestCasePropertiesFile() {
        File testCasePropertiesFile = null;
        testCasePropertiesFile = new File(name);
        return testCasePropertiesFile;
    }

    /**
     * Gets the data driver.
     *
     * @return the data driver
     */
    protected DataDriver getDataDriver() {
        testCaseProperties = new TestCaseDataDriver();
        return testCaseProperties;
    }

    /**
     * Gets the relative path to the name of the test case properties file. This does
     * not use any logic in considering testEnvironment nor organization.
     * @return the relative path and name of the test case properties file to read in.
     */
    public String getUserTestCaseDataFileName() {
        return this.userTestCaseDataFileName;
    }

    /**
     * Sets the relative path to the name of the test case properties file. This does
     * not use any logic in considering testEnvironment nor organization.
     *
     * @param testCasePropertiesFileName the new user test case data file name
     * @jameleon.attribute
     */
    public void setUserTestCaseDataFileName(String testCasePropertiesFileName) {
        this.userTestCaseDataFileName = testCasePropertiesFileName;
    }

    /**
     * Gets the name.
     *
     * @return the name of the test case properties file to read in.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the name of the test case properties file to read in.
     * @param name - The url for the test case properties file.
     * @jameleon.attribute
     */
    public void setName(String name) {
        this.name = name;
        try {
            baseTestCaseURL = new URL(name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the logger used for this tag.
     *
     * @return the logger used for this tag.
     */
    protected Logger getLogger() {
        return Logger.getLogger(TestCaseDataTag.class.getName());
    }

    /**
     * Gets the trace message when the execution is beginning and ending.
     * The message displayed will already start with BEGIN: or END:
     * @return the trace message when the execution is just beginning and ending.
     */
    protected String getTagTraceMsg() {
        return "parsing " + getTestCasePropertiesFile();
    }

    /**
     * Describe the tag when error messages occur.
     * The most appropriate message might be the tag name itself.
     * @return A brief description of the tag or the tag name itself.
     */
    public String getTagDescription() {
        return "tcData tag";
    }
}
