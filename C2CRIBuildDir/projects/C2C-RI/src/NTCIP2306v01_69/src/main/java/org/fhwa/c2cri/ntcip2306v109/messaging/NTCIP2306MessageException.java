/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.messaging;

/**
 * The Class NTCIP2306MessageException provides a mechanism to indicate that an exception is specifically related to NTCIP 2306 message processing.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NTCIP2306MessageException extends Exception {

    /** The message. */
    private String message = null;

    /**
     * Instantiates a new NTCIP2306 message exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public NTCIP2306MessageException() {
        super();
    }

    /**
     * Instantiates a new NTCIP2306 message exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     */
    public NTCIP2306MessageException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Instantiates a new NTCIP2306 message exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param cause the cause
     */
    public NTCIP2306MessageException(Throwable cause) {
        super(cause);
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#toString()
     */
    @Override
    public String toString() {
        return message;
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return message;
    }
}
