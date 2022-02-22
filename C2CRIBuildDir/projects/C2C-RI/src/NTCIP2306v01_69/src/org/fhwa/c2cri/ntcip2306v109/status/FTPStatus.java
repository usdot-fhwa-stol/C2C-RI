/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.status;

import java.util.ArrayList;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class FTPStatus maintains the results of various NTCIP 2306 FTP requirement inspections.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class FTPStatus {
    
    /** The valid ftp processing. */
    private boolean validFTPProcessing = false;  // NTCIP 2306 5.2.1
                                               // Note: The 'GET' command in the FTP standard is RETR (Retrieve)
    /** The ftp errors. */
    private ArrayList<String> ftpErrors = new ArrayList<String>();
    
    /** The test assertion list. */
    private ArrayList<TestAssertion> testAssertionList = new ArrayList<>();
    
    /** The status code. */
    private int statusCode = -1;
    
    /** The ftp status updated. */
    private boolean ftpStatusUpdated = false;
    
    /**
     * NTCIP 2306 5.1
     *
     * @return true, if is NTCIP2306 valid ftp
     */
    public boolean isNTCIP2306ValidFTP() {
        return (validFTPProcessing);
    }


    /**
     * Adds the ftp error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlError the xml error
     */
    public void addFTPError(String xmlError){
        ftpErrors.add(xmlError);
    }


    /**
     * Gets the fTP errors.
     *
     * @return the fTP errors
     */
    public String getFTPErrors(){
        StringBuilder results = new StringBuilder();

        results.append("FTP Errors Found: \n");
        for (String thisError :ftpErrors){
            results.append(thisError).append("\n");
        }

        return results.toString();
    }

    /**
     * Checks if is valid ftp processing.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid ftp processing
     */
    public boolean isValidFTPProcessing() {
        return validFTPProcessing;
    }

    /**
     * Sets the valid ftp processing.
     *
     * @param validFTPProcessing the new valid ftp processing
     */
    public void setValidFTPProcessing(boolean validFTPProcessing) {
        this.validFTPProcessing = validFTPProcessing;
    }

    /**
     * Gets the status code.
     *
     * @return the status code
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the status code.
     *
     * @param statusCode the new status code
     */
    public void setStatusCode(int statusCode) {
        ftpStatusUpdated = true;
        this.statusCode = statusCode;
    }

    /**
     * Gets the test assertion list.
     *
     * @return the test assertion list
     */
    public ArrayList<TestAssertion> getTestAssertionList() {
        if (ftpStatusUpdated){
        testAssertionList.clear();
        testAssertionList.add(new TestAssertion("5.2.2", validFTPProcessing,
                "The use of FTP shall conform with the requirments of IETF FTP RFC 959.",
                getFTPErrors()));
        }
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
