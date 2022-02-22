/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.status;

import java.util.ArrayList;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class GZIPStatus maintains the results of various NTCIP 2306 GZIP requirement inspections..
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class GZIPStatus {


    /** The is gzip encoded. */
    private boolean isGZIPEncoded = false;
    
    /** The well formed xml. */
    private boolean wellFormedXML = false;
    
    /** The gzip errors. */
    private ArrayList<String> gzipErrors = new ArrayList<String>();
    
    /** The test assertion list. */
    private ArrayList<TestAssertion> testAssertionList = new ArrayList<>();

    /**
     * NTCIP 2306 4.1.1
     *
     * @return true, if is NTCIP2306 valid gzip
     */
    public boolean isNTCIP2306ValidGZIP() {
        return (isGZIPEncoded&&wellFormedXML);
    }


    /**
     * Adds the gzip error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlError the xml error
     */
    public void addGZIPError(String xmlError){
        gzipErrors.add(xmlError);
    }


    /**
     * Gets the gZIP errors.
     *
     * @return the gZIP errors
     */
    public String getGZIPErrors(){
        StringBuilder results = new StringBuilder();

        results.append("GZIP Errors Found: \n");
        for (String thisError :gzipErrors){
            results.append(thisError).append("\n");
        }

        return results.toString();
    }


    /**
     * Checks if is checks if is gzip encoded.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is checks if is gzip encoded
     */
    public boolean isIsGZIPEncoded() {
        return isGZIPEncoded;
    }

    /**
     * Sets the checks if is gzip encoded.
     *
     * @param isGZIPEncoded the new checks if is gzip encoded
     */
    public void setIsGZIPEncoded(boolean isGZIPEncoded) {
        this.isGZIPEncoded = isGZIPEncoded;
    }

    /**
     * Checks if is well formed xml.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is well formed xml
     */
    public boolean isWellFormedXML() {
        return wellFormedXML;
    }

    /**
     * Sets the well formed xml.
     *
     * @param wellFormedXML the new well formed xml
     */
    public void setWellFormedXML(boolean wellFormedXML) {
        this.wellFormedXML = wellFormedXML;
    }

    /**
     * Gets the test assertion list.
     *
     * @return the test assertion list
     */
    public ArrayList<TestAssertion> getTestAssertionList() {
        return testAssertionList;
    }

    /**
     * Sets the test assertion list.
     *
     * @param testAssertionList the new test assertion list
     */
    public void setTestAssertionList(ArrayList<TestAssertion> testAssertionList) {
        this.testAssertionList = testAssertionList;
    }

    
}
