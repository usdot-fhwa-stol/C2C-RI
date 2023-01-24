/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.status;

import java.util.ArrayList;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;

/**
 * The Class SOAPStatus maintains the results of various NTCIP 2306 SOAP requirement inspections.
 *
 * @author TransCore ITS, LLC
 */
public class SOAPStatus {
    
    /** The soap errors. */
    private ArrayList<String> soapErrors = new ArrayList<String>();
    
    /** The valid rrsoap encoding. */
    private boolean validRRSOAPEncoding = false; // NTCIP 2306 4.2.1 a,b,c
    
    /** The valid sub soap encoding. */
    private boolean validSubSOAPEncoding = false; // NTCIP 2306 4.2.2.1 a,b,c
    
    /** The valid pub soap encoding. */
    private boolean validPubSOAPEncoding = false;  // NTCIP 2306 4.2.2.2 a,b,c
    
    /** The valid sub pub receipt encoding. */
    private boolean validSubPubReceiptEncoding = false; // NTCIP 2306 4.2.2.3 a,b,c
    
    /** The valid against schemas. */
    private boolean validAgainstSchemas = false;  // NTCIP 4.2.1c, 4.2.2.1.c, 4.2.2.2.c, 4.2.2.3.c
    
    /** The test assertion list. */
    private ArrayList<TestAssertion> testAssertionList = new ArrayList<>();
    
    /**
     * NTCIP 2306 4.2
     *
     * @return true, if is NTCIP2306 valid soap
     */
    public boolean isNTCIP2306ValidSOAP() {
        return validAgainstSchemas&&(isValidRRSOAPEncoding()||isValidSubSOAPEncoding()||isValidPubSOAPEncoding()||isValidSubPubReceiptEncoding());
    }

    /**
     * Sets the valid against schemas.
     *
     * @param msgValidAgainstSchemas the new valid against schemas
     */
    public void setValidAgainstSchemas(boolean msgValidAgainstSchemas) {
       validAgainstSchemas = msgValidAgainstSchemas;
    }

    /**
     * Checks if is valid against schemas.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid against schemas
     */
    public boolean isValidAgainstSchemas() {
        return validAgainstSchemas;
    }

    /**
     * Adds the soap error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlError the xml error
     */
    public void addSOAPError(String xmlError){
        soapErrors.add(xmlError);
    }


    /**
     * Gets the sOAP errors.
     *
     * @return the sOAP errors
     */
    public String getSOAPErrors(){
        StringBuilder results = new StringBuilder();

        results.append("SOAP Errors Found: \n");
        for (String thisError : soapErrors){
            results.append(thisError).append("\n");
        }
        if (soapErrors.isEmpty())results.append("None");
        return results.toString();
    }

    /**
     * Checks if is valid pub soap encoding.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid pub soap encoding
     */
    public boolean isValidPubSOAPEncoding() {
        return validPubSOAPEncoding;
    }

    /**
     * Sets the valid pub soap encoding.
     *
     * @param validPubSOAPEncoding the new valid pub soap encoding
     */
    public void setValidPubSOAPEncoding(boolean validPubSOAPEncoding) {
        this.validPubSOAPEncoding = validPubSOAPEncoding;
    }

    /**
     * Checks if is valid rrsoap encoding.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid rrsoap encoding
     */
    public boolean isValidRRSOAPEncoding() {
        return validRRSOAPEncoding;
    }

    /**
     * Sets the valid rrsoap encoding.
     *
     * @param validRRSOAPEncoding the new valid rrsoap encoding
     */
    public void setValidRRSOAPEncoding(boolean validRRSOAPEncoding) {
        this.validRRSOAPEncoding = validRRSOAPEncoding;
    }

    /**
     * Checks if is valid sub pub receipt encoding.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid sub pub receipt encoding
     */
    public boolean isValidSubPubReceiptEncoding() {
        return validSubPubReceiptEncoding;
    }

    /**
     * Sets the valid sub pub receipt encoding.
     *
     * @param validSubPubReceiptEncoding the new valid sub pub receipt encoding
     */
    public void setValidSubPubReceiptEncoding(boolean validSubPubReceiptEncoding) {
        this.validSubPubReceiptEncoding = validSubPubReceiptEncoding;
    }

    /**
     * Checks if is valid sub soap encoding.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid sub soap encoding
     */
    public boolean isValidSubSOAPEncoding() {
        return validSubSOAPEncoding;
    }

    /**
     * Sets the valid sub soap encoding.
     *
     * @param validSubSOAPEncoding the new valid sub soap encoding
     */
    public void setValidSubSOAPEncoding(boolean validSubSOAPEncoding) {
        this.validSubSOAPEncoding = validSubSOAPEncoding;
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
