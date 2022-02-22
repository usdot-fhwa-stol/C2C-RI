/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.applayer;


/**
 *
 * This interface standardizes the interface to the various types of
 * transports (FTP, HTTP, HTTPS, etc) supported by the application.
 *
 * @author TransCore ITS
 * Last Updated: 10/2/2012
 */


public interface Transport {
    
    /** The server role. */
    public static String SERVER_ROLE = "SERVER";
    
    /** The client role. */
    public static String CLIENT_ROLE = "CLIENT";
    
    /** The ftp transport. */
    public static String FTP_TRANSPORT = "ftp:operation";
    
    /** The http transport. */
    public static String HTTP_TRANSPORT = "http:binding";
    
    /** The soap transport. */
    public static String SOAP_TRANSPORT = "soap:binding";
    
    /** The get verb. */
    public static String GET_VERB = "GET";
    
    /** The post verb. */
    public static String POST_VERB = "POST";
    
    /** The binary file. */
    public static String BINARY_FILE = "BINARY";
    
    /** The ascii file. */
    public static String ASCII_FILE = "ASCII";

    /**
     * Initialize.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param role - server or client
     * @param operationType - indicates ftp, http or soap
     * @param action - GET, POST or other provided web action
     * @param operationLocation - address of the file or operation
     * @param addressLocation - address of the host service
     */
    public void initialize(String role, String operationType, String action, String operationLocation, String addressLocation);


    /**
     * Send.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     * @param timeout the timeout
     * @return the byte[]
     * @throws Exception the exception
     */
    public byte[] send(byte[] message, Long timeout) throws Exception;

    /**
     * Login.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param userName the user name
     * @param password the password
     * @param timeout the timeout
     * @throws Exception the exception
     */
    public void login(String userName, String password, Long timeout) throws Exception;

    /**
     * Gets the operation type.
     *
     * @return the operation type
     */
    public String getOperationType();

    /**
     * Gets the action.
     *
     * @return the action
     */
    public String getAction();
    
    /**
     * Gets the role.
     *
     * @return the role
     */
    public String getRole();
    
    /**
     * Gets the endpoint.
     *
     * @return the endpoint
     */
    public String getEndpoint();

    /**
     * Stop.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void stop();

}
