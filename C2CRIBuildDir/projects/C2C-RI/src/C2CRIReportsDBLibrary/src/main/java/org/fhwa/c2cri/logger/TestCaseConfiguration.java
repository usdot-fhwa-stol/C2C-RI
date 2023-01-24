/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.logger;

import java.net.URL;
import java.util.ArrayList;

/**
 * Represents the configuration information necessary to generate a Test Case Report.
 * 
 * @author TransCore ITS, LLC
 * Created: Oct 16, 2016
 */
public class TestCaseConfiguration {
    /** The URL for the application layer test case file. */
    private URL baseApplicationTestCaseURL;

    /** The URL for the application layer extension test case file. */
    private URL extensionApplicationTestCaseURL;

    /** The list of application layer test cases that are applicable for this test. */
    private ArrayList<TestCaseInformation> applicableAppplicationTestCases;

    /** The URL for the application layer test case file. */
    private URL baseInformationTestCaseURL;

    /** The URL for the information layer extension test case file. */
    private URL extensionInformationTestCaseURL;

    /** The list of information layer test cases that are applicable for this test. */    
    private ArrayList<TestCaseInformation> applicableInformationTestCases;

    public URL getBaseApplicationTestCaseURL() {
        return baseApplicationTestCaseURL;
    }

    public void setBaseApplicationTestCaseURL(URL baseApplicationTestCaseURL) {
        this.baseApplicationTestCaseURL = baseApplicationTestCaseURL;
    }

    public URL getExtensionApplicationTestCaseURL() {
        return extensionApplicationTestCaseURL;
    }

    public void setExtensionApplicationTestCaseURL(URL extensionApplicationTestCaseURL) {
        this.extensionApplicationTestCaseURL = extensionApplicationTestCaseURL;
    }

    public ArrayList<TestCaseInformation> getApplicableAppplicationTestCases() {
        return applicableAppplicationTestCases;
    }

    public void setApplicableAppplicationTestCases(ArrayList<TestCaseInformation> applicableAppplicationTestCases) {
        this.applicableAppplicationTestCases = applicableAppplicationTestCases;
    }

    public URL getBaseInformationTestCaseURL() {
        return baseInformationTestCaseURL;
    }

    public void setBaseInformationTestCaseURL(URL baseInformationTestCaseURL) {
        this.baseInformationTestCaseURL = baseInformationTestCaseURL;
    }

    public URL getExtensionInformationTestCaseURL() {
        return extensionInformationTestCaseURL;
    }

    public void setExtensionInformationTestCaseURL(URL extensionInformationTestCaseURL) {
        this.extensionInformationTestCaseURL = extensionInformationTestCaseURL;
    }

    public ArrayList<TestCaseInformation> getApplicableInformationTestCases() {
        return applicableInformationTestCases;
    }

    public void setApplicableInformationTestCases(ArrayList<TestCaseInformation> applicableInformationTestCases) {
        this.applicableInformationTestCases = applicableInformationTestCases;
    }

    
    

}
