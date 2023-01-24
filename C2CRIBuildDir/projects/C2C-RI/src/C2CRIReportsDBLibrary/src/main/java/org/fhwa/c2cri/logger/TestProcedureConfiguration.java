/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.logger;

import java.net.URL;
import java.util.ArrayList;

/**
 * Represents the configuration information necessary to generate a test procedure Report.
 * 
 * @author TransCore ITS, LLC
 * Created: Oct 16, 2016
 */
public class TestProcedureConfiguration {
    /** The URL for the application layer test procedure file. */
    private URL baseApplicationTestProcedureURL;

    /** The URL for the application layer extension test procedure file. */
    private URL extensionApplicationTestProcedureURL;

    /** The URL for the application layer test procedure steps file. */
    private URL baseApplicationTestProcedureStepsURL;

    /** The URL for the application layer extension test procedure steps file. */
    private URL extensionApplicationTestProcedureStepsURL;
    
    /** The list of application layer test procedures that are applicable for this test. */
    private ArrayList<TestCaseInformation> applicableAppplicationTestProcedures;

    /** The URL for the application layer test procedure file. */
    private URL baseInformationTestProcedureURL;

    /** The URL for the information layer extension test procedure file. */
    private URL extensionInformationTestProcedureURL;

    /** The URL for the application layer test procedure steps file. */
    private URL baseInformationTestProcedureStepsURL;

    /** The URL for the information layer extension test procedure steps file. */
    private URL extensionInformationTestProcedureStepsURL;

    /** The list of information layer test procedures that are applicable for this test. */    
    private ArrayList<TestCaseInformation> applicableInformationTestProcedures;

    public URL getBaseApplicationTestProcedureURL() {
        return baseApplicationTestProcedureURL;
    }

    public void setBaseApplicationTestProcedureURL(URL baseApplicationTestProcedureURL) {
        this.baseApplicationTestProcedureURL = baseApplicationTestProcedureURL;
    }

    public URL getExtensionApplicationTestProcedureURL() {
        return extensionApplicationTestProcedureURL;
    }

    public void setExtensionApplicationTestProcedureURL(URL extensionApplicationTestProcedureURL) {
        this.extensionApplicationTestProcedureURL = extensionApplicationTestProcedureURL;
    }

    public ArrayList<TestCaseInformation> getApplicableAppplicationTestProcedures() {
        return applicableAppplicationTestProcedures;
    }

    public void setApplicableAppplicationTestProcedures(ArrayList<TestCaseInformation> applicableAppplicationTestProcdures) {
        this.applicableAppplicationTestProcedures = applicableAppplicationTestProcdures;
    }

    public URL getBaseInformationTestProcedureURL() {
        return baseInformationTestProcedureURL;
    }

    public void setBaseInformationTestProcedureURL(URL baseInformationTestProcedureURL) {
        this.baseInformationTestProcedureURL = baseInformationTestProcedureURL;
    }

    public URL getExtensionInformationTestProcedureURL() {
        return extensionInformationTestProcedureURL;
    }

    public void setExtensionInformationTestProcedureURL(URL extensionInformationTestProcedureURL) {
        this.extensionInformationTestProcedureURL = extensionInformationTestProcedureURL;
    }

    public ArrayList<TestCaseInformation> getApplicableInformationTestProcedures() {
        return applicableInformationTestProcedures;
    }

    public void setApplicableInformationTestProcedures(ArrayList<TestCaseInformation> applicableInformationTestProcedures) {
        this.applicableInformationTestProcedures = applicableInformationTestProcedures;
    }

    public URL getBaseApplicationTestProcedureStepsURL() {
        return baseApplicationTestProcedureStepsURL;
    }

    public void setBaseApplicationTestProcedureStepsURL(URL baseApplicationTestProcedureStepsURL) {
        this.baseApplicationTestProcedureStepsURL = baseApplicationTestProcedureStepsURL;
    }

    public URL getExtensionApplicationTestProcedureStepsURL() {
        return extensionApplicationTestProcedureStepsURL;
    }

    public void setExtensionApplicationTestProcedureStepsURL(URL extensionApplicationTestProcedureStepsURL) {
        this.extensionApplicationTestProcedureStepsURL = extensionApplicationTestProcedureStepsURL;
    }

    public URL getBaseInformationTestProcedureStepsURL() {
        return baseInformationTestProcedureStepsURL;
    }

    public void setBaseInformationTestProcedureStepsURL(URL baseInformationTestProcedureStepsURL) {
        this.baseInformationTestProcedureStepsURL = baseInformationTestProcedureStepsURL;
    }

    public URL getExtensionInformationTestProcedureStepsURL() {
        return extensionInformationTestProcedureStepsURL;
    }

    public void setExtensionInformationTestProcedureStepsURL(URL extensionInformationTestProcedureStepsURL) {
        this.extensionInformationTestProcedureStepsURL = extensionInformationTestProcedureStepsURL;
    }

    
    

}
