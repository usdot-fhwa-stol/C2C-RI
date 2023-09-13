/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.ftp;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
//package org.apache.ftpserver.impl;
import java.io.IOException;
import java.nio.charset.MalformedInputException;

import org.apache.ftpserver.command.Command;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FileSystemView;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftpletcontainer.FtpletContainer;
import org.apache.ftpserver.impl.FtpHandler;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;
import org.apache.ftpserver.impl.ServerFtpStatistics;
import org.apache.ftpserver.listener.Listener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.filter.codec.ProtocolDecoderException;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.encoding.GZIPEncoder;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidatorFactory;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 * Modified for the RI Project to handle GET Requests from test scripts.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a> *
 */
public class RIFtpHandler implements FtpHandler {

    /**
     * The log.
     */
    private final Logger LOG = LoggerFactory.getLogger(RIFtpHandler.class);

    /**
     * The Constant NON_AUTHENTICATED_COMMANDS.
     */
    private final static String[] NON_AUTHENTICATED_COMMANDS = new String[]{
        "USER", "PASS", "AUTH", "QUIT", "PROT", "PBSZ"};

    /**
     * The context.
     */
    private FtpServerContext context;

    /**
     * The listener.
     */
    private Listener listener;

    /**
     * The path.
     */
    private String path = "";

    /**
     * The operations.
     */
    private OperationSpecCollection operations;

    /**
     * The queue controller.
     */
    QueueController queueController;

    /**
     * Instantiates a new rI ftp handler.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public RIFtpHandler() {
    }

    /**
     * Instantiates a new rI ftp handler.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param path the path
     * @param operations the operations
     * @param queue the queue
     */
    public RIFtpHandler(String path, OperationSpecCollection operations, QueueController queue) {
        this.path = path;
        this.operations = operations;
        this.queueController = queue;
    }

    /**
     * Inits the.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param context the context
     * @param listener the listener
     */
    public void init(final FtpServerContext context, final Listener listener) {
        this.context = context;
        this.listener = listener;
    }

    /**
     * Session created.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param session the session
     * @throws Exception the exception
     */
    public void sessionCreated(final FtpIoSession session) throws Exception {
        // Set our own Data Connection Factory for handling the data port transfers of FTP
        session.setListener(listener);
        try {
            session.setAttribute(FtpIoSession.ATTRIBUTE_PREFIX + "data-connection", new RIIODataConnectionFactory(context, session));
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("RIFtpHandler::sessionCreated - Could not set the data-connection attribute.", ex);
        }

        ServerFtpStatistics stats = ((ServerFtpStatistics) context
                .getFtpStatistics());

        if (stats != null) {
            stats.setOpenConnection(session);
        }
    }

    /**
     * Session opened.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param session the session
     * @throws Exception the exception
     */
    public void sessionOpened(final FtpIoSession session) throws Exception {
        FtpletContainer ftplets = context.getFtpletContainer();

        FtpletResult ftpletRet;
        try {
            ftpletRet = ftplets.onConnect(session.getFtpletSession());
        } catch (Exception e) {
            LOG.debug("Ftplet threw exception", e);
            ftpletRet = FtpletResult.DISCONNECT;
        }
        if (ftpletRet == FtpletResult.DISCONNECT) {
            LOG.debug("Ftplet returned DISCONNECT, session will be closed");
            session.close(false).awaitUninterruptibly(10000);
        } else {
            session.updateLastAccessTime();

            session.write(LocalizedFtpReply.translate(session, null, context,
                    FtpReply.REPLY_220_SERVICE_READY, null, null));
        }
    }

    /**
     * Session closed.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param session the session
     * @throws Exception the exception
     */
    public void sessionClosed(final FtpIoSession session) throws Exception {
        LOG.debug("Closing session");
        try {
            context.getFtpletContainer().onDisconnect(
                    session.getFtpletSession());
        } catch (Exception e) {
            // swallow the exception, we're closing down the session anyways
            LOG.warn("Ftplet threw an exception on disconnect", e);
        }

        // make sure we close the data connection if it happens to be open
        try {
            ServerDataConnectionFactory dc = session.getDataConnection();
            if (dc != null) {
                dc.closeDataConnection();
            }
        } catch (Exception e) {
            // swallow the exception, we're closing down the session anyways
            LOG.warn("Data connection threw an exception on disconnect", e);
        }

        FileSystemView fs = session.getFileSystemView();
        if (fs != null) {
            try {
                fs.dispose();
            } catch (Exception e) {
                LOG.warn("FileSystemView threw an exception on disposal", e);
            }
        }

        ServerFtpStatistics stats = ((ServerFtpStatistics) context
                .getFtpStatistics());

        if (stats != null) {
            stats.setLogout(session);
            stats.setCloseConnection(session);
            LOG.debug("Statistics login and connection count decreased due to session close");
        } else {
            LOG.warn("Statistics not available in session, can not decrease login and connection count");
        }
        LOG.debug("Session closed");
    }

    /**
     * Exception caught.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param session the session
     * @param cause the cause
     * @throws Exception the exception
     */
    public void exceptionCaught(final FtpIoSession session,
            final Throwable cause) throws Exception {

        if (cause instanceof ProtocolDecoderException
                && cause.getCause() instanceof MalformedInputException) {
            // client probably sent something which is not UTF-8 and we failed to
            // decode it

            LOG.warn(
                    "Client sent command that could not be decoded: {}",
                    ((ProtocolDecoderException) cause).getHexdump());
            session.write(new DefaultFtpReply(FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS, "Invalid character in command"));
        } else if (cause instanceof WriteToClosedSessionException) {
            WriteToClosedSessionException writeToClosedSessionException
                    = (WriteToClosedSessionException) cause;
            LOG.warn(
                    "Client closed connection before all replies could be sent, last reply was {}",
                    writeToClosedSessionException.getRequest());
            session.close(false).awaitUninterruptibly(10000);
        } else {
            LOG.error("Exception caught, closing session", cause);
            session.close(false).awaitUninterruptibly(10000);
        }

    }

    /**
     * Checks if is command ok without authentication.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param command the command
     * @return true, if is command ok without authentication
     */
    private boolean isCommandOkWithoutAuthentication(String command) {
        boolean okay = false;
        for (String allowed : NON_AUTHENTICATED_COMMANDS) {
            if (allowed.equals(command)) {
                okay = true;
                break;
            }
        }
        return okay;
    }

    /**
     * Message received.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param session the session
     * @param request the request
     * @throws Exception the exception
     */
    public void messageReceived(final FtpIoSession session,
            final FtpRequest request) throws Exception {

        long receiveTime = System.currentTimeMillis();
        System.out.println("RIFTPHandler::messageReceived  " + request.getRequestLine());
        NTCIP2306Message getMsg = null;
        NTCIP2306Message responseMsg = null;
        boolean retrieveProcessed = false;
        OperationIdentifier opId = null;
        OperationSpecification opSpec = null;
        try {
            session.updateLastAccessTime();

            String commandName = request.getCommand();

            CommandFactoryFactory factoryCreator = new CommandFactoryFactory();
            CommandFactory commandFactory = factoryCreator.createCommandFactory();
            Command command = commandFactory.getCommand(commandName);

            // make sure the user is authenticated before he issues commands
            if (!session.isLoggedIn()
                    && !isCommandOkWithoutAuthentication(commandName)) {
                session.write(LocalizedFtpReply.translate(session, request,
                        context, FtpReply.REPLY_530_NOT_LOGGED_IN,
                        "permission", null));
                return;
            }

            FtpletContainer ftplets = context.getFtpletContainer();

            FtpletResult ftpletRet;
            try {
                ftpletRet = ftplets.beforeCommand(session.getFtpletSession(),
                        request);
            } catch (Exception e) {
                LOG.debug("Ftplet container threw exception", e);
                ftpletRet = FtpletResult.DISCONNECT;
            }
            if (ftpletRet == FtpletResult.DISCONNECT) {
                LOG.debug("Ftplet returned DISCONNECT, session will be closed");
                session.close(false).awaitUninterruptibly(10000);
                return;
            } else if (ftpletRet != FtpletResult.SKIP) {

                Ftplet thisFtplet = ftplets.getFtplet("c2criftplet");
                if ((thisFtplet != null) && (thisFtplet instanceof RIFtplet)) {
                    System.out.println("***YEP I'm an RIFTPLet****");
                    if ((command != null) && (command instanceof RIRETRResponseReceiver)) {
                        System.out.println("***YEP I Can receive Message Inputs TOO!!!****");
                        //                   byte[]response = ((RIFtplet)thisFtplet).getResponseMessage();

                        boolean filePathMismatchError = true;
                        for (OperationSpecification thisSpec : operations.getCopyAsList()) {
                            if (request.getArgument().equalsIgnoreCase(thisSpec.getDocumentLocation())) {
                                filePathMismatchError = false;
                                opSpec = thisSpec;
                                opId = new OperationIdentifier(thisSpec.getRelatedToService(),
                                        thisSpec.getRelatedToPort(),
                                        thisSpec.getOperationName(),
                                        OperationIdentifier.SOURCETYPE.HANDLER);

                                getMsg = new NTCIP2306Message("", "", request.getRequestLine().getBytes());
                                getMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
                                getMsg.getFtpStatus().setValidFTPProcessing(true);
                                getMsg.setTransportedTimeInMs(receiveTime);
                                queueController.addToExtRequestQueue(opId, getMsg);
                                
                                String localAddress = getAddress(session.getLocalAddress().toString());
                                String remoteAddress = getAddress(session.getRemoteAddress().toString());
                                
                                try {
                                    Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(opId.getOperationName(), getMsg);
                                    requestMessage.setViaTransportProtocol(NTCIP2306Message.PROTOCOLTYPE.FTP.name());
                                    requestMessage.setMessageName("REQUEST");
                                    requestMessage.setMessageSourceAddress(remoteAddress);
                                    requestMessage.setMessageDestinationAddress(localAddress);
                                    LOG.info(requestMessage.toXML());
                                } catch (Exception ex) {
                                    System.out.println("RIFtpHandler::messageReceived Exception logging message: " + ex.getMessage());
                                }

                                OperationIdentifier respOpId = new OperationIdentifier(thisSpec.getRelatedToService(),
                                        thisSpec.getRelatedToPort(),
                                        thisSpec.getOperationName(),
                                        OperationIdentifier.SOURCETYPE.LISTENER);

                                responseMsg = queueController.getMessageFromExtResponseQueue(respOpId, 5000);
                                responseMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
                                if (responseMsg != null) {
                                    byte[] response = responseMsg.getMessagePartContent(1);
                                    if (thisSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING)
                                            && !responseMsg.isSkipEncoding()) {
                                        GZIPEncoder gzipEncoder = new GZIPEncoder();
                                        boolean gzipSuccess = gzipEncoder.gzipEncode(response);
                                        responseMsg.setGzipStatus(gzipEncoder.getGZipStatus());
                                        if (gzipSuccess) {
                                            try {
                                                XMLValidator xmlValidator = XMLValidatorFactory.getValidator();
                                                xmlValidator.isValidXML(response);
                                                xmlValidator.isXMLValidatedToSchema(new String(response, "UTF-8"));
                                                responseMsg.setXmlStatus(xmlValidator.getXMLStatus());
                                                gzipEncoder.getGZipStatus().setWellFormedXML(responseMsg.getXmlStatus().isNTCIP2306ValidXML());
                                                responseMsg.setGzipStatus(gzipEncoder.getGZipStatus());
                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                                responseMsg.getXmlStatus().addXMLError(ex.getMessage());
                                            }
                                            ((RIRETRResponseReceiver) command).setResponseMessage(gzipEncoder.getGzippedResults());
                                            try {
                                                Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(opId.getOperationName(), responseMsg);
                                                responseMessage.setViaTransportProtocol(NTCIP2306Message.PROTOCOLTYPE.FTP.name());
                                                responseMessage.setMessageName("RESPONSE");
                                                responseMessage.setMessageDestinationAddress(remoteAddress);
                                                responseMessage.setMessageSourceAddress(localAddress);
                                                responseMessage.setMessageEncoding("GZIP");
                                                LOG.info(responseMessage.toXML());                                                
                                            } catch (Exception ex) {
                                                System.out.println("RIFtpHandler::messageReceived Exception logging message: " + ex.getMessage());
                                            }
                                        } else {
                                            responseMsg.setTransportErrorEncountered(true);
                                            responseMsg.setTransportErrorDescription("Server could not Gzip the requested file and the transfer was not performed.");
                                            responseMsg.getFtpStatus().setValidFTPProcessing(false);
                                            OperationResults opResult = new OperationResults(opSpec, getMsg, responseMsg);
                                            queueController.addToOperationResultsQueue(opId, opResult);
                                            session.write(LocalizedFtpReply.translate(session, request,
                                                    context,
                                                    FtpReply.REPLY_450_REQUESTED_FILE_ACTION_NOT_TAKEN,
                                                    "GZIP Error in Server", null));
                                        }
                                    } else {
                                        ((RIRETRResponseReceiver) command).setResponseMessage(response);
                                        try {
                                            Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(opId.getOperationName(), responseMsg);
                                            responseMessage.setViaTransportProtocol(NTCIP2306Message.PROTOCOLTYPE.FTP.name());
                                            responseMessage.setMessageName("RESPONSE");
                                            responseMessage.setMessageDestinationAddress(remoteAddress);
                                            responseMessage.setMessageSourceAddress(localAddress);
                                            responseMessage.setMessageEncoding("XML");
                                            LOG.info(responseMessage.toXML());
                                        } catch (Exception ex) {
                                            System.out.println("RIFtpHandler::messageReceived Exception logging message: " + ex.getMessage());
                                        }

                                    }
                                }
                            } else {
                                System.out.println("RIFtpHandler::messageReceived request argument: " + request.getArgument() + " does not match operation Document Location: " + thisSpec.getDocumentLocation());
                            }
                        }
                        ((RIRETRResponseReceiver) command).setFilePathMismatchError(filePathMismatchError);
                    } else {
                        System.out.println("Command was " + command.toString());
                    }
                }

                if (command != null) {
                        if (responseMsg != null) {
                            responseMsg.setTransportedTimeInMs(System.currentTimeMillis());
                            if (!responseMsg.isTransportErrorEncountered()) {
                                command.execute(session, context, request);
                            }
                        } else {
                            command.execute(session, context, request);
                        }
                } else {
                    session.write(LocalizedFtpReply.translate(session, request,
                            context,
                            FtpReply.REPLY_502_COMMAND_NOT_IMPLEMENTED,
                            "not.implemented", null));
                }

                try {
                    ftpletRet = ftplets.afterCommand(
                            session.getFtpletSession(), request, session
                            .getLastReply());
                    if (responseMsg != null) {
                        if (retrieveProcessed) {
                            responseMsg.getFtpStatus().setValidFTPProcessing(
                                    (session.getLastReply().getCode() == session.getLastReply().REPLY_200_COMMAND_OKAY ? true : false));
                            responseMsg.getFtpStatus().setStatusCode(session.getLastReply().getCode());
                        } else {
                            responseMsg.getFtpStatus().setValidFTPProcessing(
                                    (session.getLastReply().getCode() == session.getLastReply().REPLY_200_COMMAND_OKAY ? true : false));
                            responseMsg.getFtpStatus().setStatusCode(session.getLastReply().getCode());
                        }
                    }
                } catch (Exception e) {
                    LOG.debug("Ftplet container threw exception", e);
                    if (responseMsg != null) {
                        responseMsg.getFtpStatus().setValidFTPProcessing(false);
                        responseMsg.setTransportErrorEncountered(true);
                        responseMsg.setTransportErrorDescription(e.getMessage());
                    }
                    ftpletRet = FtpletResult.DISCONNECT;
                } finally {
                    if (responseMsg != null) {
                        responseMsg.getFtpStatus().setValidFTPProcessing(true);
                        OperationResults opResult = new OperationResults(opSpec, getMsg, responseMsg);
                        queueController.addToOperationResultsQueue(opId, opResult);
                    }
                }
                if (ftpletRet == FtpletResult.DISCONNECT) {
                    LOG.debug("Ftplet returned DISCONNECT, session will be closed");

                    session.close(false).awaitUninterruptibly(10000);
                    return;
                }
            }

        } catch (Exception ex) {

            // send error reply
            try {
                session.write(LocalizedFtpReply.translate(session, request,
                        context, FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        null, null));
            } catch (Exception ex1) {
            }

            if (ex instanceof java.io.IOException) {
                throw (IOException) ex;
            } else {
                LOG.warn("RequestHandler.service()", ex);
            }
        }

    }

    /**
     * Session idle.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param session the session
     * @param status the status
     * @throws Exception the exception
     */
    public void sessionIdle(final FtpIoSession session, final IdleStatus status)
            throws Exception {
        LOG.info("Session idle, closing");
        session.close(false).awaitUninterruptibly(10000);
    }

    /**
     * Message sent.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param session the session
     * @param reply the reply
     * @throws Exception the exception
     */
    public void messageSent(final FtpIoSession session, final FtpReply reply)
            throws Exception {
        System.out.println("FTPHandler::messageSent " + reply.getMessage());

        // do nothing
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * Process the provided socketAddress description.  Strip off the host name and return ip address and port.
     * 
     * @param ipAddress
     * @return 
     */
    private String getAddress(String ipAddress){
        String updatedAddress = "";
        
        // Not really certain that we'll always get an address.
        if (ipAddress == null) return updatedAddress;
        
        if (ipAddress.contains("/") && (ipAddress.length() >= ipAddress.indexOf("/")+1)){
            // Strip off the host name leading up to the / character
            int slashIndex = ipAddress.indexOf("/");
            updatedAddress = ipAddress.substring(slashIndex + 1);
        } else {
            updatedAddress = ipAddress;
        }
        
        return updatedAddress;
    }
}
