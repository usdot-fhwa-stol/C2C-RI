/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.logger;

import java.net.URL;

/**
 * Represents details about the Test Case and its related data files.
 * 
 * @author TransCore ITS, LLC
 * Created: Oct 16, 2016
 */
public class TestCaseInformation {

    /** The name assigned to this test case. */
    private String testCaseName;
    
    /** The URL representing the location of the base Test Case Data File. */
    private URL baseTestCaseDataFile;
    
    /** The path for the extension test case data file. (if applicable) */
    private String extensionTestCaseDataFile;

    /** The test procedure that is used to execute this test case */
    private String relatedTestProcedure;
    
    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public URL getBaseTestCaseDataFile() {
        return baseTestCaseDataFile;
    }

    public void setBaseTestCaseDataFile(URL baseTestCaseDataFile) {
        this.baseTestCaseDataFile = baseTestCaseDataFile;
    }

    public String getExtensionTestCaseDataFile() {
        return extensionTestCaseDataFile;
    }

    public void setExtensionTestCaseDataFile(String extensionTestCaseDataFile) {
        this.extensionTestCaseDataFile = extensionTestCaseDataFile;
    }

    public String getRelatedTestProcedure() {
        return relatedTestProcedure;
    }

    public void setRelatedTestProcedure(String relatedTestProcedure) {
        this.relatedTestProcedure = relatedTestProcedure;
    }
        
}
