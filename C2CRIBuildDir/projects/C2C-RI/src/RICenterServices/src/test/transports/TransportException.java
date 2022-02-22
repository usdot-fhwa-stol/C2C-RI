/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.transports;

/**
 * An Exception Class used to identify exceptions that occur at the Transport Layer
 * within a dialog.
 *
 * @author TransCore ITS
 */
public class TransportException extends Exception {
    public static String PARAMETER_ERROR_TYPE = "MISSING PARAMETER ERROR";
    public static String GENERAL_ERROR_TYPE = "GENERAL ERROR";
    public static String TIMEOUT_ERROR_TYPE = "TIMEOUT ERROR";
    public static String PROTOCOL_ERROR_TYPE = "PROTOCOL ERROR";
    public static String LOGIN_ERROR_TYPE = "LOGIN ERROR";
    public static String CONNECTION_ERROR_TYPE = "CONNECTION ERROR";

    private String errorType="";

    public TransportException(){
        super();
    }

    public TransportException(String message){
        super(message);
    }

    public TransportException(String message, String errorType){
        super(message);
        this.errorType = errorType;
    }

    public String getExceptionErrorType() {
        return errorType;
    }


}
