/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.utilities;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Class RIVerification performs an analysis of the test including the needs/requirements that were exercised through testing.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class RIVerification {

    /** The verification need type. */
    public static String VERIFICATION_NEED_TYPE = "Need";
    
    /** The verification requirement type. */
    public static String VERIFICATION_REQUIREMENT_TYPE = "Requirement";
    
    /** The verification ta type. */
    public static String VERIFICATION_TA_TYPE = "Test Assertion";
    
    /** The verification pass result. */
    public static String VERIFICATION_PASS_RESULT = "PASS";
    
    /** The verification fail result. */
    public static String VERIFICATION_FAIL_RESULT = "FAIL";
    
    /** The verification na result. */
    public static String VERIFICATION_NA_RESULT = "NA";
    
    /** The id. */
    @XmlAttribute
    private String id;
    
    /** The type. */
    private String type;
    
    /** The result. */
    private String result;
    
    /** The prescription. */
    private String prescription;
    
    /** The error description. */
    private String errorDescription;

    /**
     * Gets the error description.
     *
     * @return the error description
     */
    public String getErrorDescription() {
        return errorDescription;
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
     * Gets the id.
     *
     * @return the id
     */
    @XmlTransient
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the prescription.
     *
     * @return the prescription
     */
    public String getPrescription() {
        return prescription;
    }

    /**
     * Sets the prescription.
     *
     * @param prescription the new prescription
     */
    public void setPrescription(String prescription) {
        this.prescription = prescription;
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
     * Sets the result.
     *
     * @param result the new result
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type the new type
     */
    public void setType(String type) {
        this.type = type;
    }
}
