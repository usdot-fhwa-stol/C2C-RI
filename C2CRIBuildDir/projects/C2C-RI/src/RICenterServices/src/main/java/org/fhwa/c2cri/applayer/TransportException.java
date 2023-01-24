/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.applayer;

/**
 * An Exception Class used to identify exceptions that occur at the Transport Layer
 * within a dialog.
 *
 * @author TransCore ITS
 * Last Updated 10/2/2012
 */
public class TransportException extends Exception {
    
    /** The parameter error type. */
    public static String PARAMETER_ERROR_TYPE = "MISSING PARAMETER ERROR";
    
    /** The general error type. */
    public static String GENERAL_ERROR_TYPE = "GENERAL ERROR";
    
    /** The timeout error type. */
    public static String TIMEOUT_ERROR_TYPE = "TIMEOUT ERROR";
    
    /** The protocol error type. */
    public static String PROTOCOL_ERROR_TYPE = "PROTOCOL ERROR";
    
    /** The login error type. */
    public static String LOGIN_ERROR_TYPE = "LOGIN ERROR";
    
    /** The connection error type. */
    public static String CONNECTION_ERROR_TYPE = "CONNECTION ERROR";

    /** The error type. */
    private String errorType="";

    /**
     * Instantiates a new transport exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public TransportException(){
        super();
    }

    /**
     * Instantiates a new transport exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     */
    public TransportException(String message){
        super(message);
    }

    /**
     * Instantiates a new transport exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     * @param errorType the error type
     */
    public TransportException(String message, String errorType){
        super(message);
        this.errorType = errorType;
    }

    /**
     * Gets the exception error type.
     *
     * @return the exception error type
     */
    public String getExceptionErrorType() {
        return errorType;
    }


}
