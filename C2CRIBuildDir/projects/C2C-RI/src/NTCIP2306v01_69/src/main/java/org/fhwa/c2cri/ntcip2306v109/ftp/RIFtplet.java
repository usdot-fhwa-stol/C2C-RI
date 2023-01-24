/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.ftp;

import java.io.IOException;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletResult;

/**
 * The Class RIFtplet acts as a servlet for the FTP server.  It handles the processing of Get Requests and aids in logging and message transmission.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class RIFtplet extends DefaultFtplet {

    /** The request file. */
    private String requestFile;
    
    /** The response message. */
    private byte[] responseMessage;
    
    /** The response message set. */
    private boolean responseMessageSet = false;
    
    /** The client address. */
    private String clientAddress;
    
    /** The last response code. */
    private Integer lastResponseCode;
    
    /** The last response line. */
    private String lastResponseLine;
    
    /** The last request line. */
    private String lastRequestLine;
    
    /** The file path mismatch error. */
    private boolean filePathMismatchError = false;

    /**
     * Instantiates a new rI ftplet.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public RIFtplet() {
        super();
    }


    /**
     * Before command.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param request the request
     * @return the ftplet result
     * @throws FtpException the ftp exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public FtpletResult beforeCommand(FtpSession session, FtpRequest request) throws FtpException, IOException {

            System.out.println("beforeCommand:  Host= " + session.getClientAddress().getHostName()
                    + " Port= " + session.getClientAddress().getPort()
                    + " Address= " + session.getClientAddress().getAddress()
                    + " RequestLine= " + request.getRequestLine());

        // If the command is a retrieve command, verify that the file name matches before processing.
        // otherwise skip it.
        if (request.getCommand().equalsIgnoreCase("RETR")) {

            if ("badfile".equalsIgnoreCase(request.getArgument())) {
                // Wait for the file response then set the "file" response

                clientAddress = session.getClientAddress().getAddress().getHostAddress();

                if (responseMessageSet) {

                    responseMessageSet = false;
                    System.out.println("***RIFtpLet: Response Message Set*****");
                    return super.beforeCommand(session, request);
                } else {
                    System.out.println("***RIFtpLet: No Response Message Was Set*****");
                }

                return FtpletResult.SKIP;

            } else {  // file names don't match
                filePathMismatchError = true;
                System.out.println("***RIFtpLet: FilePath Mismatch *****" + requestFile);
                return super.beforeCommand(session, request);
            }


        } else {
            System.out.println("RIFtplet::beforeCommand Command Received = " + request.getCommand());
        }
        // All other commands can be executed without an issue
        return super.beforeCommand(session, request);

    }

    /**
     * After command.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param request the request
     * @param reply the reply
     * @return the ftplet result
     * @throws FtpException the ftp exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public FtpletResult afterCommand(FtpSession session, FtpRequest request, FtpReply reply) throws FtpException, IOException {

        System.out.println("afterCommand:  Host= " + session.getClientAddress().getHostName()
                + " Port= " + session.getClientAddress().getPort()
                + " Address= " + session.getClientAddress().getAddress()
                + " RequestLine= " + request.getRequestLine() + " Reply Code=" + reply.getCode() + " Reply= " + reply.getMessage());


        lastResponseCode = reply.getCode();
        lastResponseLine = reply.getMessage();
        lastRequestLine = request.getRequestLine();
        return super.afterCommand(session, request, reply);
    }

    /**
     * On connect.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @return the ftplet result
     * @throws FtpException the ftp exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public FtpletResult onConnect(FtpSession session) throws FtpException, IOException {

        return super.onConnect(session);
    }

    /**
     * On login.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param request the request
     * @return the ftplet result
     * @throws FtpException the ftp exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {

        return super.onLogin(session, request);
    }

    /**
     * On download end.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param request the request
     * @return the ftplet result
     * @throws FtpException the ftp exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public FtpletResult onDownloadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {

        FtpletResult tempResult = super.onDownloadEnd(session, request);
        return tempResult;
    }

    /**
     * On download start.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param request the request
     * @return the ftplet result
     * @throws FtpException the ftp exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public FtpletResult onDownloadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {

        try {

            requestFile = request.getArgument();

        } catch (Exception e) {
//             logger.error("Could not upload file", e);
        }



        return super.onDownloadStart(session, request);




    }

    /**
     * Gets the last request line.
     *
     * @return the last request line
     */
    public String getLastRequestLine() {
        return lastRequestLine;
    }

    /**
     * Gets the last response code.
     *
     * @return the last response code
     */
    public Integer getLastResponseCode() {
        return lastResponseCode;
    }

    /**
     * Gets the last response line.
     *
     * @return the last response line
     */
    public String getLastResponseLine() {
        return lastResponseLine;
    }

    /**
     * On upload end.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param request the request
     * @return the ftplet result
     * @throws FtpException the ftp exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return super.onUploadEnd(session, request);
    }

    /**
     * On upload start.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param request the request
     * @return the ftplet result
     * @throws FtpException the ftp exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public FtpletResult onUploadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
        return super.onUploadStart(session, request);
    }

    /**
     * Sets the response message.
     *
     * @param responseMessage the new response message
     */
    public void setResponseMessage(byte[] responseMessage) {
        this.responseMessage = responseMessage;
        this.responseMessageSet = true;
    }

    /**
     * Gets the response message.
     *
     * @return the response message
     */
    public byte[] getResponseMessage() {
        return this.responseMessage;
    }

    /**
     * Checks if is file path mismatch error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is file path mismatch error
     */
    public boolean isFilePathMismatchError() {
        return filePathMismatchError;
    }

    /**
     * Sets the request file.
     *
     * @param fileName the new request file
     */
    public void setRequestFile(String fileName) {
        this.requestFile = fileName;
    }

    /**
     * Gets the client address.
     *
     * @return the client address
     */
    public String getClientAddress() {
        return this.clientAddress;
    }
}
