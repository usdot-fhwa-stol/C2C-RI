/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.operations;


/**
 * The Class NTCIP2306OperationException which indicates that an exception was encountered in the processing of an NTCIP2306 related operation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NTCIP2306OperationException extends Exception {

    /** The message. */
    private String message = null;

    /**
     * Instantiates a new NTCIP2306 operation exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public NTCIP2306OperationException() {
        super();
    }

    /**
     * Instantiates a new NTCIP2306 operation exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     */
    public NTCIP2306OperationException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * Instantiates a new NTCIP2306 operation exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param cause the cause
     */
    public NTCIP2306OperationException(Throwable cause) {
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
