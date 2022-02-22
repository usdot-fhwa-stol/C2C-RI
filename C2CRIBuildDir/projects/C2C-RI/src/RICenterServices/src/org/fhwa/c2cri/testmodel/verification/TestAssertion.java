/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel.verification;

/**
 * The Class TestAssertion represents information about a single test check that was performed.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestAssertion {
    
    /** The test assertion id. */
    private String testAssertionID;
    
    /** The test result. */
    private String testResult;
    
    /** The test result description. */
    private String testResultDescription;
    
    /** The test assertion prescription. */
    private String testAssertionPrescription;
    
    /** The test type. */
    private TestType testType = TestType.UNDEFINED;
    
    /** The test passed. */
    private boolean testPassed;

    /** Indicates whether testing should still continue after this test failure. */
    private boolean continueAfterFailure = false;
    
    /**
     * The Enum TestType.
     */
    public static enum TestType {
/** The transport. */
TRANSPORT,
/** The encoding. */
ENCODING,
/** The message. */
MESSAGE,
/** The dialog. */
DIALOG,
/** The undefined. */
UNDEFINED};

    /**
     * Instantiates a new test assertion.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public TestAssertion(){        
    }
    
    /**
     * Instantiates a new test assertion.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testId the test id
     * @param passed the passed
     * @param prescription the prescription
     * @param resultDescription the result description
     */
    public TestAssertion(String testId,boolean passed,String prescription, String resultDescription){
        this.testAssertionID = testId;
        this.testPassed = passed;
        this.testAssertionPrescription = prescription;
        this.testResultDescription = resultDescription;
    }
    
    /**
     * Gets the test assertion id.
     *
     * @return the test assertion id
     */
    public String getTestAssertionID() {
        return testAssertionID;
    }

    /**
     * Sets the test assertion id.
     *
     * @param testAssertionID the new test assertion id
     */
    public void setTestAssertionID(String testAssertionID) {
        this.testAssertionID = testAssertionID;
    }

    /**
     * Gets the test assertion prescription.
     *
     * @return the test assertion prescription
     */
    public String getTestAssertionPrescription() {
        return testAssertionPrescription;
    }

    /**
     * Sets the test assertion prescription.
     *
     * @param testAssertionPrescription the new test assertion prescription
     */
    public void setTestAssertionPrescription(String testAssertionPrescription) {
        this.testAssertionPrescription = testAssertionPrescription;
    }

    /**
     * Gets the test result.
     *
     * @return the test result
     */
    public String getTestResult() {
        return (testResult==null?(testPassed?"Passed":"Failed"):testResult);
    }

    /**
     * Sets the test result.
     *
     * @param testResult the new test result
     */
    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    /**
     * Gets the test result description.
     *
     * @return the test result description
     */
    public String getTestResultDescription() {
        return testResultDescription;
    }

    /**
     * Sets the test result description.
     *
     * @param testResultDescription the new test result description
     */
    public void setTestResultDescription(String testResultDescription) {
        this.testResultDescription = testResultDescription;
    }

    /**
     * Gets the test type.
     *
     * @return the test type
     */
    public TestType getTestType() {
        return testType;
    }

    /**
     * Sets the test type.
     *
     * @param testType the new test type
     */
    public void setTestType(TestType testType) {
        this.testType = testType;
    }

    /**
     * Checks if is test passed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is test passed
     */
    public boolean isTestPassed() {
        return testPassed;
    }

    /**
     * Sets the test passed.
     *
     * @param testPassed the new test passed
     */
    public void setTestPassed(boolean testPassed) {
        this.testPassed = testPassed;
    }

    /**
     * Checks if testing should be continued if this test fails.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return 
     */
    public boolean isContinueAfterFailure() {
        return continueAfterFailure;
    }

    /**
     * Sets whether testing should be continued if this test fails.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A     *
     * 
     * @param continueAfterFailure 
     */
    public void setContinueAfterFailure(boolean continueAfterFailure) {
        this.continueAfterFailure = continueAfterFailure;
    }    
}
