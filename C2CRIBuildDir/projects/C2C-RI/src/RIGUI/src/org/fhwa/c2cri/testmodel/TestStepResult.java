/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Class TestStepResult.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestStepResult {

    /** The test step description. */
    @XmlAttribute
    private String testStepDescription;
    
    /** The error description. */
    @XmlAttribute
    private String errorDescription;
    
    /** The result. */
    @XmlAttribute
    private String result;
    
    /** The start time. */
    @XmlAttribute
    private Long startTime;
    
    /** The end time. */
    @XmlAttribute
    private Long endTime;

    /**
     * Instantiates a new test step result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestStepResult() {
    }


    /**
     * Instantiates a new test step result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testStepDescription the test step description
     * @param startTime the start time
     * @param endTime the end time
     * @param errorDescription the error description
     * @param result the result
     */
    public TestStepResult(String testStepDescription, long startTime, long endTime, String errorDescription, String result) {
        this.testStepDescription = testStepDescription;
        this.startTime = startTime;
        this.endTime = endTime;
        this.errorDescription = errorDescription;
        this.result = result;
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
     * Gets the test step description.
     *
     * @return the test step description
     */
    public String getTestStepDescription() {
        return testStepDescription;
    }

    /**
     * Gets the error description.
     *
     * @return the error description
     */
    public String getErrorDescription() {
        return errorDescription;
    }

    /**
     * Gets the time stamp.
     *
     * @return the time stamp
     */
    @XmlTransient
    public long getTimeStamp() {
        return endTime;
    }

    /**
     * Sets the error description.
     *
     * @param errorDescription the new error description
     */
    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    /**
     * Sets the result.
     *
     * @param result the new result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Sets the end time.
     *
     * @param endTime the new end time
     */
    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
        
}
