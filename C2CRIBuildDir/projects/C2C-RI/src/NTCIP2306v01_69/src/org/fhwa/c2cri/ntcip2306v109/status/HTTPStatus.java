/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.status;

import java.util.ArrayList;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class HTTPStatus maintains the results of various NTCIP 2306 http requirement inspections.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class HTTPStatus {

    /** The valid http headers. */
    private boolean validHTTPHeaders = false;  // NTCIP 2306 5.1.1 and 5.1.2
    // Note 5.1.3 is not covered by the referenced standard
    /** The http errors. */
    private ArrayList<String> httpErrors = new ArrayList<String>();
    
    /** The successfully transmitted. */
    private boolean successfullyTransmitted = false;
    
    /** The source. */
    private String source = "";
    
    /** The destination. */
    private String destination = "";
    
    /** The use secure sockets. */
    private boolean useSecureSockets = false;
    
    /** The status code. */
    private int statusCode = -1;
    
    /** The matching soap action. */
    private boolean matchingSOAPAction = false; // NTCIP2306 7.1.2.f
    
    /** The http status updated. */
    private boolean httpStatusUpdated = false;
    
    /** The http soap status updated. */
    private boolean httpSOAPStatusUpdated = false;
    
    /** The test assertion list. */
    private ArrayList<TestAssertion> testAssertionList = new ArrayList<>();

    /**
     * NTCIP 2306 5.1
     *
     * @return true, if is NTCIP2306 valid http
     */
    public boolean isNTCIP2306ValidHTTP() {
        return (validHTTPHeaders);
    }

    /**
     * Adds the http error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlError the xml error
     */
    public void addHTTPError(String xmlError) {
        httpStatusUpdated = true;
        httpErrors.add(xmlError);
    }

    /**
     * Gets the hTTP errors.
     *
     * @return the hTTP errors
     */
    public String getHTTPErrors() {
        StringBuilder results = new StringBuilder();

        results.append("HTTP Errors Found: \n");
        for (String thisError : httpErrors) {
            results.append(thisError).append("\n");
        }

        return results.toString();
    }

    /**
     * Checks if is valid http headers.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is valid http headers
     */
    public boolean isValidHTTPHeaders() {
        return validHTTPHeaders;
    }

    /**
     * Sets the valid http headers.
     *
     * @param validHTTPHeaders the new valid http headers
     */
    public void setValidHTTPHeaders(boolean validHTTPHeaders) {
        httpStatusUpdated = true;
        this.validHTTPHeaders = validHTTPHeaders;
    }

    /**
     * Gets the destination.
     *
     * @return the destination
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the destination.
     *
     * @param destination the new destination
     */
    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source the new source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Checks if is successfully transmitted.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is successfully transmitted
     */
    public boolean isSuccessfullyTransmitted() {
        return successfullyTransmitted;
    }

    /**
     * Sets the successfully transmitted.
     *
     * @param successfullyTransmitted the new successfully transmitted
     */
    public void setSuccessfullyTransmitted(boolean successfullyTransmitted) {
        httpStatusUpdated = true;
        this.successfullyTransmitted = successfullyTransmitted;
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
        httpStatusUpdated = true;
        this.statusCode = statusCode;
    }

    /**
     * Checks if is matching soap action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is matching soap action
     */
    public boolean isMatchingSOAPAction() {
        return matchingSOAPAction;
    }

    /**
     * Sets the matching soap action.
     *
     * @param matchingSOAPAction the new matching soap action
     */
    public void setMatchingSOAPAction(boolean matchingSOAPAction) {
        httpSOAPStatusUpdated = true;
        this.matchingSOAPAction = matchingSOAPAction;
    }

    /**
     * Checks if is use secure sockets.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is use secure sockets
     */
    public boolean isUseSecureSockets() {
        return useSecureSockets;
    }

    /**
     * Sets the use secure sockets.
     *
     * @param useSecureSockets the new use secure sockets
     */
    public void setUseSecureSockets(boolean useSecureSockets) {
        httpStatusUpdated = true;
        this.useSecureSockets = useSecureSockets;
    }

    /**
     * Gets the test assertion list.
     *
     * @return the test assertion list
     */
    public ArrayList<TestAssertion> getTestAssertionList() {
        if (httpStatusUpdated) {
            testAssertionList.clear();
            testAssertionList.add(new TestAssertion("5.1.1", validHTTPHeaders,
                    "The HTTP Headers shall conform with IETF HTTP/1.1 RFC 2616.",
                    getHTTPErrors()));
            if (httpSOAPStatusUpdated)
			{
				testAssertionList.add(new TestAssertion("5.1.2", matchingSOAPAction,
                    "The SOAP Message shall conform with the requirements of W3C SOAP 1.1.",
                    getHTTPErrors()));
			}
            
            if (useSecureSockets) {
                testAssertionList.add(new TestAssertion("5.1.3", successfullyTransmitted,
                        "The use of Secure Sockets message shall conform with the requirements of IETF HTTP/1.1 RFC 2616.",
                        getHTTPErrors()));
            }
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
