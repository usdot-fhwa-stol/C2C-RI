/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel;

import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Class TestCaseResult.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCaseResult {

    /** The test case id. */
    @XmlAttribute
    private String testCaseID;
    
    /** The time stamp. */
    @XmlAttribute
    private long timeStamp;
    
    /** The error description. */
    @XmlAttribute
    private String errorDescription;
    
    /** The result. */
    @XmlAttribute
    private String result;
    
    /** The standard layer. */
    @XmlAttribute
    private String standardLayer;
    
    /** The test case run id. */
    @XmlAttribute
    private Integer testCaseRunID;
    
    /** The start time. */
    @XmlAttribute
    private Long startTime;
    
    /** The end time. */
    @XmlAttribute
    private Long endTime;
    
    /** The test step results. */
    private ArrayList<TestStepResult> testStepResults = new ArrayList<TestStepResult>();
    
    /** The description associated with the test case **/
    @XmlTransient
    private String testCaseDescription;

    /**
     * Instantiates a new test case result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestCaseResult() {
    }

    ;

    /**
     * Instantiates a new test case result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testCaseID the test case id
     * @param timeStamp the time stamp
     * @param errorDescription the error description
     * @param result the result
     */
    public TestCaseResult(String testCaseID, long timeStamp, String errorDescription, String result) {
        this.testCaseID = testCaseID;
        this.timeStamp = timeStamp;
        this.errorDescription = errorDescription;
        this.result = result;
    }

    /**
     * Instantiates a new test case result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testCaseID the test case id
     * @param timeStamp the time stamp
     * @param errorDescription the error description
     * @param result the result
     */
    public TestCaseResult(String testCaseID, long timeStamp, String errorDescription, String result, String testCaseDescription) {
        this.testCaseID = testCaseID;
        this.timeStamp = timeStamp;
        this.errorDescription = errorDescription;
        this.result = result;
        this.testCaseDescription = testCaseDescription;
    }
    
    
    /**
     * Update test case result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param endTime the end time
     * @param startTime the start time
     * @param standardLayer the standard layer
     * @param testCaseRunID the test case run id
     * @param result the result
     * @param errorDescription the error description
     */
    protected void updateTestCaseResult(Long endTime, Long startTime, String standardLayer, Integer testCaseRunID, String result, String errorDescription){
        this.endTime = endTime;
        this.startTime = startTime;
        this.standardLayer = standardLayer;
        this.testCaseRunID = testCaseRunID;
        this.result = result;
        this.errorDescription = errorDescription;
    }

    /**
     * Gets the result.
     *
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * Gets the test case id.
     *
     * @return the test case id
     */
    public String getTestCaseID() {
        return testCaseID;
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    public Long getEndTime() {
        return endTime;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    public Long getStartTime() {
        return startTime;
    }

    /**
     * Gets the test case run id.
     *
     * @return the test case run id
     */
    @XmlTransient
    public Integer getTestCaseRunID() {
        return testCaseRunID;
    }

 
    /**
     * Sets the test case run id.
     *
     * @param testCaseRunID the new test case run id
     */
    public void setTestCaseRunID(Integer testCaseRunID) {
        this.testCaseRunID = testCaseRunID;
    }


    /**
     * Adds the new test step.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testStepDescription the test step description
     * @param startTime the start time
     * @param endTime the end time
     * @param errorDescription the error description
     * @param result the result
     * @return the int
     */
    protected int addNewTestStep(String testStepDescription, long startTime, long endTime, String errorDescription, String result){
        TestStepResult thisTestStep = new TestStepResult(testStepDescription, startTime, endTime, errorDescription,result);
        testStepResults.add(thisTestStep);
        return thisTestStep.hashCode();
    }

    /**
     * Update test step.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param endTime the end time
     * @param errorDescription the error description
     * @param result the result
     * @param testStepIdentifier the test step identifier
     */
    protected void updateTestStep(long endTime, String errorDescription, String result, int testStepIdentifier){
        for (TestStepResult thisStep: testStepResults){
            if (thisStep.hashCode()==testStepIdentifier){
               thisStep.setEndTime(endTime);
               thisStep.setErrorDescription(errorDescription);
               thisStep.setResult(result);
                
            }
        }
    }
    
    /**
     * Gets the test step results.
     *
     * @return the test step results
     */
    public ArrayList<TestStepResult> getTestStepResults(){
        return testStepResults;
    }

    /**
     * Gets the error description.
     *
     * @return the error description
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    @XmlTransient
    public String getTestCaseDescription() {
        return testCaseDescription;
    }

    public void setTestCaseDescription(String testCaseDescription) {
        this.testCaseDescription = testCaseDescription;
    }
    
}
