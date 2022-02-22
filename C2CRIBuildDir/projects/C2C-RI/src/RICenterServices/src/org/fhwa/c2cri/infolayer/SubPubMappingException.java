/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.infolayer;


/**
 * An Exception Class used to identify exceptions that occur when a subscription
 * operation can not be associated with a publication operation.
 *
 * @author TransCore ITS
 * Last Updated: 10/2/2013
 */
public class SubPubMappingException extends Exception {
    
    /** The subscription error type. */
    public static String SUBSCRIPTION_ERROR_TYPE = "CAN NOT FIND MATCHING PUBLICATION";
    
    /** The publication error type. */
    public static String PUBLICATION_ERROR_TYPE = "CAN NOT FIND MATCHING SUBSCRIPTION";

    /** The error type. */
    private String errorType="";

    /**
     * Instantiates a new sub pub mapping exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public SubPubMappingException(){
        super();
    }

    /**
     * Instantiates a new sub pub mapping exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     */
    public SubPubMappingException(String message){
        super(message);
    }

    /**
     * Instantiates a new sub pub mapping exception.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     * @param errorType the error type
     */
    public SubPubMappingException(String message, String errorType){
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
