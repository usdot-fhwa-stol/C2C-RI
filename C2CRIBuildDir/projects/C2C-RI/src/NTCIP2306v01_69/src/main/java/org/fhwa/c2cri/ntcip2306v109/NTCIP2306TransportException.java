/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109;

/**
 * An exception thrown when transport errors occur.
 *
 * @author TransCore ITS, LLC
 * 11/15/2013
 */
public class NTCIP2306TransportException extends Exception {

    /** the message that describes the exception. */
    private String message = null;

    /**
     * constructor.
     */
    public NTCIP2306TransportException() {
        super();
    }

    /**
     * constructor.
     *
     * @param message the message
     */
    public NTCIP2306TransportException(String message) {
        super(message);
        this.message = message;
    }

    /**
     * constructor.
     *
     * @param cause – an exception that caused this exception
     */
    public NTCIP2306TransportException(Throwable cause) {
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
