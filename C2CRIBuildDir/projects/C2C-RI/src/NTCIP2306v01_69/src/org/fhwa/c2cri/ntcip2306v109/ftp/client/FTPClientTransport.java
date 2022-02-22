/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.ftp.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.ntcip2306v109.encoding.GZIPDecoder;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidatorFactory;
import org.fhwa.c2cri.applayer.ListenerManager;
import org.fhwa.c2cri.applayer.TransportException;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class FTPClientTransport.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class FTPClientTransport implements Runnable {

    /** The ftp. */
    private RIFTPClient ftp = new RIFTPClient();
    
    /** The get timeout. */
    private int getTimeout = 3000;
    
    /** The username. */
    private String username = "riftp";
    
    /** The password. */
    private String password = "riftp";
    /** The get response content. */
    private byte[] getResponseContent;
    
    /** The get reply string. */
    private String getReplyString;
    
    /** The last reply code. */
    private int lastReplyCode;
    
    /** The passive mode. */
    private boolean passiveMode = false;
    
    /** The destination address. */
    private String destinationAddress = "localhost";
    
    /** The destination port. */
    private int destinationPort = 2221;
    
    /** The log. */
    protected static Logger log = Logger.getLogger(FTPClientTransport.class.getName());
    
    /** The shutdown status. */
    private boolean shutdownStatus = false;
    
    /** The is initialized. */
    private boolean isInitialized = false;
    
    /** The eqc. */
    private QueueController eqc;
    
    /** The location. */
    private String location;
    
    /** The op spec collection. */
    private OperationSpecCollection opSpecCollection;
    
    /** The handler gzip decoder. */
    private GZIPDecoder handlerGZIPDecoder;
    
    /** The command listener id. */
    private Integer commandListenerID = -1;

    /**
     * Instantiates a new fTP client transport.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param location the location
     * @param eqc the eqc
     * @param opsCollection the ops collection
     */
    public FTPClientTransport(String location, final QueueController eqc, OperationSpecCollection opsCollection) {
        this.eqc = eqc;
        this.location = location;
        this.opSpecCollection = opsCollection;
    }

    /**
     * Instantiates a new fTP client transport.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param location the location
     * @param eqc the eqc
     */
    public FTPClientTransport(String location, final QueueController eqc) {
        this.eqc = eqc;
        this.location = location;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

        while (!shutdownStatus) {
            try {
                if (isInitialized) {
                    for (OperationSpecification thisSpec : this.opSpecCollection.getCopyAsList()) {
                        OperationIdentifier opId = new OperationIdentifier(thisSpec.getRelatedToService(), thisSpec.getRelatedToPort(), thisSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
                        if (eqc.isAvaliableInRequestQueue(opId)) {
                            NTCIP2306Message inMessage = eqc.getMessageFromExtRequestQueue(opId);
                            if (inMessage != null) {
                                System.out.println("FTPClientHandler::run:  Calling Get ...");
                                get(thisSpec, inMessage);
                            } else {
                                System.out.println("FTPClientHandler::run inMessage = null!!");
                            }
                        }
                    }
                }
                Thread.currentThread().sleep(300);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(opSpecCollection == null ? "Ops Spec Collection was null!!" : "OK");
            }
        }

    }

//    @Override
    /**
 * Gets the.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param opSpec the op spec
 * @param inMessage the in message
 * @throws Exception the exception
 */
public void get(OperationSpecification opSpec, NTCIP2306Message inMessage) throws Exception {
        NTCIP2306Message respMsg = null;
        if (inMessage != null)inMessage.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
        if (ftp.isConnected()) {
            try {

                System.out.println("Before Executing request get...");
                System.out.println("URL = " + opSpec.getDocumentLocation());
                
            try{
                if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING)) {
                    setFileTypeFromEncoding(RIFTPClient.BINARY_FILE_TYPE);
                } else {
                    setFileTypeFromEncoding(RIFTPClient.ASCII_FILE_TYPE);                    
                }
            } catch (TransportException tex){
                throw new Exception("TransportError: Could not set the FTP server to the correct mode.  The exception returned was: "+tex.getExceptionErrorType());
            }
                
                // Execute FTP Get request
                ByteArrayOutputStream byteOutStr = new ByteArrayOutputStream();
                if (inMessage != null)inMessage.setTransportedTimeInMs(System.currentTimeMillis());
                boolean retrieveResult = ftp.retrieveFile(opSpec.getDocumentLocation(), byteOutStr);
                
                long responseTime = System.currentTimeMillis();
                getReplyString = ftp.getReplyString();
                lastReplyCode = ftp.getReplyCode();
 
                if (retrieveResult) {
                    getResponseContent = byteOutStr.toByteArray();

                    respMsg = processResponse(opSpec, getResponseContent);

                    if (respMsg == null) {
                        respMsg = new NTCIP2306Message("", "", "No Response Message received for operation.".getBytes());
                        respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.ERROR);
                    }
                    respMsg.setTransportedTimeInMs(responseTime);
                    respMsg.getFtpStatus().setStatusCode(lastReplyCode);
                    respMsg.getFtpStatus().setValidFTPProcessing(true);
                    OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
                    
                    try{
                        Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(opId.getOperationName(), respMsg);
                        responseMessage.setViaTransportProtocol(NTCIP2306Message.PROTOCOLTYPE.FTP.name());
                        responseMessage.setMessageName("RESPONSE");
                        responseMessage.setMessageEncoding(opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING)?"GZIP":"XML");                               
                        log.info(responseMessage.toXML());
                    } catch (Exception ex){
                        System.out.println("FTPClientTransport::get Exception logging message: "+ex.getMessage());                        
                    }
 
                    eqc.addToExtResponseQueue(opId, respMsg);
                                   
                    System.out.println("Get is complete and the message has been placed on the Queue.");
                }
                byteOutStr.close();
                if (!retrieveResult) {
                    if (respMsg == null){
                        respMsg = new NTCIP2306Message("", "", "No Response Message received for operation.".getBytes());                        
                        respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.ERROR);
                    }
                    respMsg.getFtpStatus().setStatusCode(lastReplyCode);
                    respMsg.getFtpStatus().setValidFTPProcessing(false);
                    throw new TransportException("FTPClientTransport:get - Retrieve result = false : " + getReplyString, TransportException.PROTOCOL_ERROR_TYPE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                respMsg = new NTCIP2306Message("", "", ("FTPClientHandler::get Exception Encountered - " + ex.getMessage()).getBytes());
                respMsg.setTransportedTimeInMs(System.currentTimeMillis());
                respMsg.setTransportErrorEncountered(true);
                respMsg.setTransportErrorDescription(ex.getMessage());
                respMsg.getFtpStatus().setValidFTPProcessing(false);
                respMsg.getFtpStatus().addFTPError("FTPClientHandler::get Exception Encountered - " + ex.getMessage());
                OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
                eqc.addToExtResponseQueue(opId, respMsg);
                OperationResults opResults = new OperationResults(opSpec, inMessage, respMsg);
                eqc.addToOperationResultsQueue(opId, opResults);
                if ((ex.getMessage()!=null)&&ex.getMessage().contains("timed out")) {
                    throw new TransportException("FTPClientTransport:get - Failed to retrieve file: " + ex.getMessage(), TransportException.TIMEOUT_ERROR_TYPE);
                } else {
                    throw new TransportException("FTPClientTransport:get - Failed to retrieve file: " + ex.getMessage(), TransportException.PROTOCOL_ERROR_TYPE);
                }
            }

        } else {
            respMsg = new NTCIP2306Message("", "", "FTPClientTransport:get - The Client is not connected to the server.".getBytes());
            respMsg.setTransportedTimeInMs(System.currentTimeMillis());
            respMsg.getFtpStatus().setValidFTPProcessing(false);
            respMsg.setTransportErrorEncountered(true);
            respMsg.setTransportErrorDescription("The FTP Client is not connected to the server.");

            respMsg.getFtpStatus().addFTPError("FTPClientTransport:get - The Client is not connected to the server.");
            OperationResults opResults = new OperationResults(opSpec, inMessage, respMsg);
            OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
            eqc.addToOperationResultsQueue(opId, opResults);
            throw new TransportException("FTPClientTransport:get - The Client is not connected to the server", TransportException.GENERAL_ERROR_TYPE);
        }
        OperationResults opResults = new OperationResults(opSpec, inMessage, respMsg);
        OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
        eqc.addToOperationResultsQueue(opId, opResults);

    }

    /**
     * Process the FTP request from an Owner Center
     * 
     * Store the received message in a local variable. If the location
     * specifies that the input message is to be GZIP Encoded, decode the
     * request message content Store the decoding status If no decoding error
     * occurs, store the decoded content in a local variable for use in
     * validation. End If If no decoding error has occurred, validate the
     * received message that is stored in the local variable Store the
     * validation status If no validation error has occurred if the location
     * specifies that the input message is to be SOAP Encoded decode the
     * received message stored in the local variable Store the decoding status
     * End If End If End If
     * 
     * If a decoding error or validation error has occurred, note this status in
     * a request message for the queueController If no decoding or validation
     * errors have occurred, note this status and the message content in a
     * request message for the queueController Pass the decoded request
     * message(s) to the queController.
     *
     * @param opSpec the op spec
     * @param resp the resp
     * @return the NTCIP2306 message
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private NTCIP2306Message processResponse(OperationSpecification opSpec, byte[] resp) throws IOException {
        // Get hold of the response entity
        System.out.println("Response Message Reply String = " + ftp.getReplyString());
        NTCIP2306Message em = null;
        boolean successfullyDecoded = true;
        byte[] decodedBytes = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();


        // If the response does not enclose an entity, there is no need
        // to worry about connection release
        if (resp != null) {
            
            try {
                decodedBytes = resp;
                handlerGZIPDecoder = new GZIPDecoder();
                if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING)) {
                    successfullyDecoded = handlerGZIPDecoder.gzipDecode(decodedBytes);
                    if (successfullyDecoded) {
                        decodedBytes = handlerGZIPDecoder.getGzipDecodedResults();
                    } else {
                        // Store the errors related to the request message.
                    }
                }

                if (successfullyDecoded) {

                    XMLValidator xmlValidator = new XMLValidatorFactory().newXMLValidator();
                    if (xmlValidator.isValidXML(decodedBytes)) {
                        handlerGZIPDecoder.getGZipStatus().setWellFormedXML(xmlValidator.getXMLStatus().isNTCIP2306ValidXML());

                        // just capturing the validation status for the record.
                        // NTCIP 2306 does not require that the message be validated against the referenced schemas.
                       xmlValidator.isXMLValidatedToSchema(new String(decodedBytes, "UTF-8"));                     
                        
                       em = new NTCIP2306Message(xmlValidator.getMessageType().getNamespaceURI(), xmlValidator.getMessageType().getLocalPart(), decodedBytes);
                       em.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
 
                    } else {
                        //  Indicates that the response message has invalid XML
                       em = new NTCIP2306Message(decodedBytes);
                       em.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
                        System.out.println("FTPClientHandler::processResponse XML Validation Errors:  " + xmlValidator.getXMLStatus().getXMLErrors());
                    }  // End If (xmlValidator.isValidXML(decodedBytes)
                    em.setXmlStatus(xmlValidator.getXMLStatus());
                }  // end if (successfullyDecoded)


                if (handlerGZIPDecoder != null) {
                    if (em != null) {
                        em.setGzipStatus(handlerGZIPDecoder.getGZipStatus());
                        em.getGzipStatus().setWellFormedXML(em.getXmlStatus().isNTCIP2306ValidXML());
                    } else {
                        em = new NTCIP2306Message("", "", "Error processing the GZIP message received from the Server.".getBytes());
                        em.setMessageType(NTCIP2306Message.MESSAGETYPE.EXCEPTION);
                        em.setGzipStatus(handlerGZIPDecoder.getGZipStatus());
                    }
                }
                
                OperationIdentifier theId = new OperationIdentifier(opSpec.getRelatedToService(),
                        opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);

                em.getFtpStatus().setValidFTPProcessing(ftp.getReplyCode() == 226 ? true : false);
                em.getFtpStatus().setStatusCode(ftp.getReplyCode());
                if (ftp.getReplyCode() != 226) {
                    em.getFtpStatus().addFTPError("Invalid FTP Protocol Error for response: " + ftp.getReplyString());
                }
                em.getHttpStatus().setSource(ftp.getRemoteAddress().getHostAddress() + ":" + ftp.getRemotePort());
                em.getHttpStatus().setDestination(ftp.getLocalAddress().getHostAddress() + ":" + ftp.getLocalPort());
                em.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);

            } catch (IOException ex) {
                em.setTransportErrorEncountered(true);
                em.setTransportErrorDescription(ex.getMessage());

                // In case of an IOException the connection will be released
                // back to the connection manager automatically
                throw ex;

            } catch (RuntimeException ex) {

                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection and release it back to the connection manager.
                em.getFtpStatus().setValidFTPProcessing(false);
                em.getFtpStatus().addFTPError("FTPClientTransport::processResponse RunTimeException - " + ex.getMessage());
                em.setTransportErrorEncountered(true);
                em.setTransportErrorDescription(ex.getMessage());
                throw ex;

            } finally {

                // Closing the input stream will trigger connection release
                buffer.close();

            }

        }

        return em;
    }

//    @Override
    /**
 * Initialize.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @throws Exception the exception
 */
public void initialize() throws Exception {
        boolean isConnected = false;
        boolean isLoggedIn = false;
        isInitialized = false;
        try {
            URL locationURL = new URL(location);
            destinationAddress = locationURL.getHost();
            destinationPort = locationURL.getPort();
            if (destinationAddress == null) {
                throw new TransportException("FTPClient:initialize - No destinationAddress provided.", TransportException.PARAMETER_ERROR_TYPE);
            }
            if (destinationPort < 0) {
                destinationPort = 21;
            }

            commandListenerID = ListenerManager.getInstance().createClientModeListener(destinationAddress, destinationPort, false, "FTP CLIENT COMMAND LISTENER");

            if (!passiveMode) {

                InetAddress thisIp;
                String ipAddress;
                try {
                    thisIp = InetAddress.getLocalHost();
                    ipAddress = thisIp.getHostAddress();
                    log.debug("FTPClientTransport:  Set the Local IP address to " + ipAddress);
                } catch (Exception e) {
                    ipAddress = "127.0.0.1";
                    log.debug("FTPClientTransport:  Could not get the host IP address, setting to local host address ->" + ipAddress + "  Exception =" + e.getMessage());
                }
            }

            // Set up the Listener

            ftp.setDataTimeout(getTimeout);
            ftp.setDefaultTimeout(getTimeout);

            // Connect and logon to FTP Server  (via the local forwarder address)
            System.out.println("FTP Address: "+ListenerManager.getInstance().getListenerInternalServerAddress(commandListenerID));
            System.out.println("FTP Port: "+ListenerManager.getInstance().getListenerInternalServerPort(commandListenerID));
            System.out.println("CommandListenerID: "+commandListenerID);
            ftp.connect(ListenerManager.getInstance().getListenerInternalServerAddress(commandListenerID),
                    ListenerManager.getInstance().getListenerInternalServerPort(commandListenerID));
            isConnected = true;
            System.out.println("Connected to "
                    + this.destinationAddress + ".");

        try {
            this.username = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.FTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.FTP_CLIENT_USERNAME_PARAMETER, this.username);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            this.password = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.FTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.FTP_CLIENT_PASSWORD_PARAMETER, this.password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        boolean useAnonymousLogin = true;
        try {
            useAnonymousLogin = Boolean.valueOf(RIParameters.getInstance().getParameterValue(NTCIP2306Settings.FTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.FTP_CLIENT_ANONYMOUS_LOGIN_PARAMETER, String.valueOf(useAnonymousLogin)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

            // try logging in with available username and password
            if (useAnonymousLogin){
                this.password = System.getProperty("user.name")+"@"+InetAddress.getLocalHost().getHostName();
                this.username = "anonymous";
            } 
            isLoggedIn = loginToServer(this.username, this.password);                
            
            System.out.print(ftp.getReplyString());
            if (!isLoggedIn) {
                throw new TransportException("FTPClientTransport:initialize - Unable to login to FTP server with Username " + username + " Password " + password, TransportException.LOGIN_ERROR_TYPE);
            } else {
                System.out.println("Connected to "
                        + this.destinationAddress + ".");
                lastReplyCode = ftp.getReplyCode();
                getReplyString = ftp.getReplyString();
                System.out.print(ftp.getReplyString());
            }


            if (passiveMode) {

                ftp.enterLocalPassiveMode();
                // Disable remote verification, since the forwarder addresses will not necessarily equal the remote ftp server address.
                ftp.setRemoteVerificationEnabled(false);
            }


            lastReplyCode = ftp.getReplyCode();
            getReplyString = ftp.getReplyString();
            isInitialized = true;

        } catch (SocketTimeoutException stoex) {
            ListenerManager.getInstance().stopListener(commandListenerID);
            throw new TransportException("[FTPClientTransport]:initialize " + "\nError: " + stoex.getMessage(), TransportException.TIMEOUT_ERROR_TYPE);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            try {
                ListenerManager.getInstance().stopListener(commandListenerID);
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Closing the Listeners didn't work either");
            }
            if (e instanceof TransportException) {
                throw new TransportException("[FTPClientTransport]:initialize " + "  Type: " + ((TransportException) e).getExceptionErrorType() + "\nError: " + e.getMessage() + "\nUnable to initialize the FTP client with Connected State: " + isConnected + "  Login State: " + isLoggedIn);
            } else {
                throw new TransportException("[FTPClientTransport]:initialize " + "  Error: " + e.getMessage() + "\nUnable to initialize the FTP client with Connected State: " + isConnected + "  Login State: " + isLoggedIn);
            }
        }

    }

    /**
     * Gets the last reply code.
     *
     * @return the last FTP reply code returned from the server
     */
    public int getLastReplyCode() {
        return lastReplyCode;
    }

    /**
     * Gets the last reply string.
     *
     * @return the last FTP reply string returned from the server
     */
    public String getLastReplyString() {
        return getReplyString;
    }

    /**
     * Gets the the response content.
     *
     * @return the content returned from the Server
     */
    public byte[] getTheResponseContent() {
        return getResponseContent;
    }

//    @Override
    /**
 * Shutdown.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @throws Exception the exception
 */
public void shutdown() throws Exception {
        try {
            if (ftp.isConnected()) {
                ftp.logout();
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                ftp.disconnect();
            }
            ListenerManager.getInstance().stopListener(commandListenerID);
            shutdownStatus = true;
        }



    }

    /**
     * Login to server.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param userName the user name
     * @param password the password
     * @return true, if successful
     * @throws TransportException the transport exception
     */
    public boolean loginToServer(String userName, String password) throws TransportException {
        // try logging in with available username and password
        try {
            boolean isLoggedIn = ftp.login(userName, password);
            return isLoggedIn;
        } catch (Exception ex) {
            throw new TransportException("FTPClientTransport:loginToServer - Unable to login to server - " + ex.getMessage(), TransportException.LOGIN_ERROR_TYPE);
        }
    }

    /**
     * Based on the input encoding type determine the file type for any
     * potential file transfers.
     *
     * @param encodingType - NTCIP2306SOAPEncoding or NTCIP2306GZIPEncoding
     * @throws TransportException the transport exception
     */
    public void setFileTypeFromEncoding(int encodingType) throws TransportException {
        try {
            if (encodingType==RIFTPClient.BINARY_FILE_TYPE) {
                System.out.println("FTPClientTransport get:  Processing as Binary");
                ftp.setFileType(RIFTPClient.BINARY_FILE_TYPE);
            } else {
                ftp.setFileType(RIFTPClient.ASCII_FILE_TYPE);
            }
        } catch (Exception ex) {
            throw new TransportException("FTPClientTransport:setFileTypeFromEncoding - Unable to set the file type - " + ex.getMessage());
        }
    }

    /**
     * Based on the input encoding type determine the file type for any
     * potential file transfers.
     */
    public void setPassiveConnectionMode() {
        this.passiveMode = true;
    }

    /**
     * Gets the location.
     *
     * @return the location
     */
    private String getLocation() {
        return this.location;
    }
}
