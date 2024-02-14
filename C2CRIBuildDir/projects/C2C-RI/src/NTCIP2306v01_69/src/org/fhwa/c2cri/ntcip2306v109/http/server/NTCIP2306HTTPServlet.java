/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.http.server;

import org.fhwa.c2cri.ntcip2306v109.encoding.SOAPDecoder;
import org.fhwa.c2cri.ntcip2306v109.encoding.GZIPEncoder;
import org.fhwa.c2cri.ntcip2306v109.encoding.SOAPEncoder;
import org.fhwa.c2cri.ntcip2306v109.encoding.GZIPDecoder;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidatorFactory;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class NTCIP2306HTTPServlet.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NTCIP2306HTTPServlet extends HttpServlet {

    /** The log. */
    protected static Logger log = Logger.getLogger(NTCIP2306HTTPServlet.class.getName());
    
    /** The operation. */
    private String operation = "Hello World";
    
    /** The uri. */
    private String uri = "/en*/";
    
    /** The handler gzip encoder. */
    private GZIPEncoder handlerGZIPEncoder;
    
    /** The handler gzip decoder. */
    private GZIPDecoder handlerGZIPDecoder;
    
    /** The handler soap encoder. */
    private SOAPEncoder handlerSOAPEncoder;
    
    /** The handler soap decoder. */
    private SOAPDecoder handlerSOAPDecoder;
    
    /** The queue controller. */
    private QueueController queueController;
    
    /** The op spec collection. */
    private OperationSpecCollection opSpecCollection;
    
    /** The receive time. */
    private long receiveTime;
    
    /**
     * Instantiates a new NTCIP2306 http servlet.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public NTCIP2306HTTPServlet() {
        this.queueController = null;
    }

    /**
     * Instantiates a new NTCIP2306 http servlet.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param queueController the queue controller
     */
    public NTCIP2306HTTPServlet(final QueueController queueController) {
        this.queueController = queueController;
    }

    /**
     * Instantiates a new NTCIP2306 http servlet.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param uri the uri
     */
    public NTCIP2306HTTPServlet(String operation, String uri) {
        this.operation = operation;
        this.uri = uri;
        this.queueController = null;
        this.opSpecCollection = new OperationSpecCollection();
    }

    /**
     * Instantiates a new NTCIP2306 http servlet.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param uri the uri
     * @param queueController the queue controller
     */
    public NTCIP2306HTTPServlet(String operation, String uri, final QueueController queueController) {
        this.operation = operation;
        this.uri = uri;
        this.queueController = queueController;
        this.opSpecCollection = new OperationSpecCollection();
    }

    /**
     * Instantiates a new NTCIP2306 http servlet.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param uri the uri
     * @param queueController the queue controller
     * @param opSpecCollection the op spec collection
     */
    public NTCIP2306HTTPServlet(String operation, String uri, final QueueController queueController, OperationSpecCollection opSpecCollection) {
        this.operation = operation;
        this.uri = uri;
        this.queueController = queueController;
        this.opSpecCollection = opSpecCollection;
    }

    /**
     * Note:  This procedure may be called in order to process a number of defined
     * operations.  The first step in processing is to determine which operation
     * is being attempted.
     * If the operationspeclist size = 1 then
     * use the operationspec for the call to processGet.
     * else
     * look through the operations to find all matching locations
     * if operationsMatching == 1
     * use this matching operation for the call to processGet
     * else if operationsMatching > 1
     * log the matches that were eliminated
     * use the first matching operation for the call to processGet
     * else if operationsMatching == 0
     * Send an HTTP Error Response Back
     * Log that no matches were found.
     * End If
     * End If
     *
     * @param request the request
     * @param response the response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long receiveTime = System.currentTimeMillis();
        try{
        if (opSpecCollection.size() == 1) {
            processGet(opSpecCollection.get(0), request, response);
        } else {
            System.out.println("Checking for an operation with location: " + request.getQueryString());
            ArrayList<OperationSpecification> opsList = opSpecCollection.getSpecsWithLocation(request.getQueryString());
            if (opsList.size() == 1) {
                if (opsList.get(0).isGetOperation()) {
                    processGet(opsList.get(0), request, response);
                }
            } else if (opsList.size() > 1) {
                if (opsList.get(0).isGetOperation()) {
                    processGet(opsList.get(0), request, response);
                }
                log.trace("There are " + opsList.size() + " operations that match this get request.");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String getResponse = "<h1>" + "HTTP GET Request Error" + "</h1>\n"
                        + "<h2>" + "There are no operations that match this HTTP GET Request" + "</h2>";
                response.getWriter().println(getResponse);
                log.trace("There are no operations that match this get request.");
            }
        }
        }catch (IOException ex){
            ex.printStackTrace();
            log.trace(ex);
        } catch (ServletException ex){
            
        }
    }

    /**
     * This procedure performs the primary activities involved in perform a get operation.
     * 
     * Pass the get request to the queController.  A get request does not have message content.
     * 
     * Call processResponse.
     * 
     * Send the Operation Completion status to the queController.  Include any success/failure status.
     *
     * @param opSpec the op spec
     * @param request the request
     * @param response the response
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void processGet(OperationSpecification opSpec, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (queueController != null) {

            NTCIP2306Message em = new NTCIP2306Message("", "", request.getRequestURI().getBytes());
            em.setTransportedTimeInMs(receiveTime);
            em.getHttpStatus().setValidHTTPHeaders(true);
            em.getHttpStatus().setSuccessfullyTransmitted(true);
            em.getHttpStatus().setSource(request.getRemoteAddr());
            em.getHttpStatus().setDestination(request.getLocalAddr());
            em.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
            OperationIdentifier theId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
            queueController.addToExtRequestQueue(theId, em);
            System.out.println("NTCIP2306HTTPServlet::processGet Placed the operation " + theId + " on the queue.");
            NTCIP2306Message respEm = null;
            try{
                respEm = processResponse(opSpec, request, response);
                respEm.getHttpStatus().setSuccessfullyTransmitted(true);
                respEm.getHttpStatus().setValidHTTPHeaders(true);
                
                    Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), respEm);
                    responseMessage.setMessageName("RESPONSE");
                    log.info(responseMessage.toXML());
                
            } catch (ServletException ex){
                respEm = new NTCIP2306Message("", "", ex.getMessage().getBytes());
                respEm.getHttpStatus().setSuccessfullyTransmitted(false);
                respEm.getHttpStatus().addHTTPError(ex.getMessage());
            } catch (IOException ex){
                respEm = new NTCIP2306Message("", "", ex.getMessage().getBytes());
                respEm.setTransportErrorEncountered(true);
                respEm.setTransportErrorDescription(ex.getMessage());
            } finally {
                OperationResults opResults = new OperationResults(opSpec, em, respEm);
                queueController.addToOperationResultsQueue(theId, opResults);            
        }

        }


    }

    /**
     * Note:  This procedure may be called in order to process a number of defined
     * operations.  The first step in processing is to determine which operation
     * is being attempted.  Much of the logic for SOAP handling portion is driven by
     * the SOAP 1.1 Spec section 6 which describes Using SOAP in HTTP.
     * If the operationspeclist size = 1 then
     * use the operationspec for the call to processPost.
     * else
     * Check the HTTP Headers for a SOAPAction Header
     * If a SOAPAction Header Exists loop through the operation specs for operations with matching soapAction Entries
     * if operationsMatching == 1
     * use this matching operation for the call to processPost
     * else if operationsMatching > 1
     * log the matches that were eliminated
     * use the first matching operation for the call to processPost
     * else if operationsMatching == 0
     * Send an HTTP Error Response Back
     * Log that no matches were found.
     * End If
     * else loop through the operation specs for operation with matching Request URI entries
     * if operationsMatching == 1
     * use this matching operation for the call to processPost
     * else if operationsMatching > 1
     * log the matches that were eliminated
     * use the first matching operation for the call to processPost
     * else if operationsMatching == 0
     * Send an HTTP Error Response Back
     * Log that no matches were found.
     * End If
     * End If
     * End If
     *
     * @param req the req
     * @param resp the resp
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        receiveTime = System.currentTimeMillis();
        try {
            if (opSpecCollection.size() == 1) {
                processPost(opSpecCollection.get(0), req, resp);
            } else {
                if (isSOAPAction(req)) {
                    ArrayList<OperationSpecification> matchSOAPList = new ArrayList<OperationSpecification>();
                    matchSOAPList = opSpecCollection.getSpecsWithSOAPAction(includesSOAPAction(req));
                    // Catch an exception if there is an error within the SOAP encoding.
                    try{
                        if (matchSOAPList.size() == 1) {
                            processPost(matchSOAPList.get(0), req, resp);
                        } else if (matchSOAPList.size() > 1) {
                            processPost(matchSOAPList.get(0), req, resp);
                            log.trace("There are " + matchSOAPList.size() + " operations that match this SOAP Post request.");
                        } else {
                            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        String getResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n"
                                + "   \n"
                                + "   <SOAP-ENV:Body>\n"
                                + "   \n"
                                + "      <SOAP-ENV:Fault>\n"
                                + "         <faultcode>SOAP-ENV:Client</faultcode>\n"
                                + "\t\t \n"
                                + "         <faultstring>\n"
                                + "         SOAP POST Request Error" + "\n"
                                + "         There are no operations that match this SOAP Post Request"                                 
                                + "         </faultstring>\n"
                                + "\t\t \n"
                                + "      </SOAP-ENV:Fault>\n"
                                + "   </SOAP-ENV:Body>\n"
                                + "   \n"
                                + "</SOAP-ENV:Envelope>";
                            resp.getWriter().println(getResponse);
                            log.trace("There are no operations that match this SOAP Post request.");
                            NTCIP2306Message respMsg = new NTCIP2306Message("", "", getResponse.getBytes());
                            respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
                            Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(matchSOAPList.get(0).getOperationName(), respMsg);
                            responseMessage.setMessageName("RESPONSE");
                            log.info(responseMessage.toXML());
                                                                                         
                        }
                    } catch (Exception ex){
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        String getResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n"
                                + "   \n"
                                + "   <SOAP-ENV:Body>\n"
                                + "   \n"
                                + "      <SOAP-ENV:Fault>\n"
                                + "         <faultcode>SOAP-ENV:Client</faultcode>\n"
                                + "\t\t \n"
                                + "         <faultstring>\n"
                                + ex.getMessage()
                                + "         </faultstring>\n"
                                + "\t\t \n"
                                + "      </SOAP-ENV:Fault>\n"
                                + "   </SOAP-ENV:Body>\n"
                                + "   \n"
                                + "</SOAP-ENV:Envelope>";
                            resp.getWriter().println(getResponse);
                            log.trace("There are no operations that match this SOAP Post request.");
                            NTCIP2306Message respMsg = new NTCIP2306Message("", "", getResponse.getBytes());
                            respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
                            Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(matchSOAPList.get(0).getOperationName(), respMsg);
                            responseMessage.setMessageName("RESPONSE");
                            log.info(responseMessage.toXML());
                        
                    }
                } else {
                    ArrayList<OperationSpecification> matchLocationList = new ArrayList<OperationSpecification>();
                    matchLocationList = opSpecCollection.getSpecsWithLocation(req.getQueryString());
                    System.out.println("NTCIP2306HTTPServlet::doPost Matches for Location " + req.getQueryString() + " = " + matchLocationList.size());
                    if (matchLocationList.size() == 1) {
                        processPost(matchLocationList.get(0), req, resp);
                    } else if (matchLocationList.size() > 1) {
                        processPost(matchLocationList.get(0), req, resp);
                        log.trace("There are " + matchLocationList.size() + " operations that match this HTTP Post request.");
                    } else {
                        String getResponse = "<error><h1>" + "HTTP Post Request Error" + "</h1>\n"
                                + "<h2>" + "There are no operations that match this HTTP Post Request" + "</h2></error>";
                        resp.getWriter().println(getResponse);
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        log.trace("There are no operations that match this HTTP Post request.");
                            NTCIP2306Message respMsg = new NTCIP2306Message("", "", getResponse.getBytes());
                            respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
                            Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(includesSOAPAction(req), respMsg);
                            responseMessage.setMessageName("RESPONSE");
                            log.info(responseMessage.toXML());
                    }

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            log.trace(ex);
        }
    }

    /**
     * This procedure performs the primary activities involved in performing a POST operation.
     * 
     * processRequest
     * 
     * Send the Operation Completion status to the queController.  Include any success/failure status.
     *
     * @param opSpec the op spec
     * @param req the req
     * @param resp the resp
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void processPost(OperationSpecification opSpec, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        OperationIdentifier theId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
        NTCIP2306Message reqMsg = new NTCIP2306Message("", "", (req.getRequestURI() + " preloaded request message").getBytes());
        reqMsg.setTransportedTimeInMs(receiveTime);
        NTCIP2306Message respMsg = new NTCIP2306Message("", "", (req.getRequestURI() + " preloaded response message").getBytes());
        respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
        try {
            System.out.println("NTCIP2306HTTPServlet::processPost  Before processRequest. @"+System.currentTimeMillis());
            reqMsg = processRequest(opSpec, req, resp);
            reqMsg.getHttpStatus().setMatchingSOAPAction(isSOAPAction(req));
            
                try{
                    Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), reqMsg);
                    requestMessage.setMessageName("REQUEST");
                    log.info(requestMessage.toXML());
                } catch (Exception ex){
                    System.out.println("NTCIP2306HTTPServlet::processPost  Exception: "+ex.getMessage());                                        
                }
            
            System.out.println("NTCIP2306HTTPServlet::processPost  Completed processRequest. @"+System.currentTimeMillis());
        } catch (ServletException ex) {
            reqMsg.getHttpStatus().addHTTPError("Exception encountered in processRequest: " + ex.getMessage());
            reqMsg.getHttpStatus().setMatchingSOAPAction(isSOAPAction(req));
            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            queueController.addToOperationResultsQueue(theId, opResults);
            System.out.println("NTCIP2306HTTPServlet::processPost  ServletException - " + ex.getMessage());
            throw new ServletException(ex);
        } catch (IOException ex) {
            reqMsg.getHttpStatus().addHTTPError("Exception encountered in processRequest: " + ex.getMessage());
            reqMsg.getHttpStatus().setMatchingSOAPAction(isSOAPAction(req));
            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            queueController.addToOperationResultsQueue(theId, opResults);
            System.out.println("NTCIP2306HTTPServlet::processPost  IOException - " + ex.getMessage());
            throw new IOException(ex);
        } catch (Exception ex){
            throw new ServletException(ex);            
        }

        try {
            System.out.println("NTCIP2306HTTPServlet::processPost  Calling processResponse. @"+System.currentTimeMillis());
            respMsg = processResponse(opSpec, req, resp);
            respMsg.getHttpStatus().setSuccessfullyTransmitted(true);
            respMsg.getHttpStatus().setValidHTTPHeaders(true);
            System.out.println("NTCIP2306HTTPServlet::processPost  Completed processResponse. @"+System.currentTimeMillis());

                try{
                    Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), respMsg);
                    responseMessage.setMessageName("RESPONSE");
                    log.info(responseMessage.toXML());
                } catch (Exception ex){
                    System.out.println("NTCIP2306HTTPServlet::processPost  Exception: "+ex.getMessage());                    
                }

            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            queueController.addToOperationResultsQueue(theId, opResults);
        } catch (ServletException ex) {
            respMsg.getHttpStatus().setSuccessfullyTransmitted(false);
            respMsg.getHttpStatus().addHTTPError("Exception encountered in processResponse: " + ex.getMessage());
            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            queueController.addToOperationResultsQueue(theId, opResults);
            System.out.println("NTCIP2306HTTPServlet::processPost  ServletException. @"+System.currentTimeMillis()+" "+ex.getMessage());
            ex.printStackTrace();
            throw new ServletException(ex);
        } catch (IOException ex) {
            respMsg.setTransportErrorEncountered(true);
            respMsg.setTransportErrorDescription(ex.getMessage());
            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            queueController.addToOperationResultsQueue(theId, opResults);
            System.out.println("NTCIP2306HTTPServlet::processPost  IOException. @"+System.currentTimeMillis()+" "+ex.getMessage());
            ex.printStackTrace();
            throw new IOException(ex);
        } catch (Exception ex){
            System.out.println("NTCIP2306HTTPServlet::processPost  Exception. @"+System.currentTimeMillis()+" "+ex.getMessage());
            ex.printStackTrace();    
            throw new ServletException(ex);
        }

    }

    /**
     * Process the HTTP request from an External Center
     * 
     * Store the received message in a local variable.
     * If the operation specifies that the input message is to be GZIP Encoded, decode the request message content
     * Store the decoding status
     * If no decoding error occurs, store the decoded content in a local variable for use in validation.
     * End If
     * If no decoding error has occurred, validate the received message that is stored in the local variable
     * Store the validation status
     * If no validation error has occurred
     * if the operation specifies that the input message is to be SOAP Encoded decode the received message stored in the local variable
     * Store the decoding status
     * End If
     * End If
     * End If
     * 
     * If a decoding error or validation error has occurred, note this status in a request message for the queueController
     * If no decoding or validation errors have occurred, note this status and the message content in a request message for the queueController
     * Pass the decoded request message(s) to the queController.
     *
     * @param opSpec the op spec
     * @param req the req
     * @param resp the resp
     * @return the NTCIP2306 message
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private NTCIP2306Message processRequest(OperationSpecification opSpec, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException, Exception {
        NTCIP2306Message em = null;
        handlerGZIPDecoder = new GZIPDecoder();
        handlerSOAPDecoder = new SOAPDecoder();

        boolean successfullyDecoded = true;
        byte[] decodedBytes = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = req.getInputStream().read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        decodedBytes = buffer.toByteArray();

        if (opSpec.getOperationInputEncoding().equals(OperationSpecification.GZIP_ENCODING)) {
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

                    xmlValidator.isXMLValidatedToSchema(new String(decodedBytes, "UTF-8"));
                    if (opSpec.getOperationInputEncoding().equals(OperationSpecification.SOAP_ENCODING)) {
                        successfullyDecoded = handlerSOAPDecoder.isSoapEncoded(buffer.toByteArray());
                        handlerSOAPDecoder.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());
                        if (successfullyDecoded) {
                            XMLValidator subxmlValidator = new XMLValidatorFactory().newXMLValidator();
                            for (int ii = 1; ii <= handlerSOAPDecoder.getNumMessageParts(); ii++) {
                                System.err.println("NTCIP2306HTTPServlet::processRequest  Checking Message Part = " + ii);
                                try {
                                    subxmlValidator.isValidXML(handlerSOAPDecoder.getMessagePart(ii));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    successfullyDecoded = false;
                                    // Store the XML Parsing errors related extracting the request message from SOAP.
                                }
                                
                                try{
                                        if (em == null) {
                                            em = new NTCIP2306Message(subxmlValidator.getMessageType().getNamespaceURI(), subxmlValidator.getMessageType().getLocalPart(), handlerSOAPDecoder.getMessagePart(ii));
                                        } else {
                                            em.addMessagePart(subxmlValidator.getMessageType().getNamespaceURI(), subxmlValidator.getMessageType().getLocalPart(), handlerSOAPDecoder.getMessagePart(ii));
                                        }
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    successfullyDecoded = false;
                                    // Store the XML Parsing errors related extracting the request message from SOAP.
                                }
                            }
							if (em != null)
								em.setXmlStatus(xmlValidator.getXMLStatus());
                        } else {
                              throw new Exception("SOAP Encoding Error");
  
                        }  // End If successfullyDecoded (SOAP)

                        // The SOAP body must be validated against the schema.
                    } else {
                        if (em == null) {
                            em = new NTCIP2306Message(xmlValidator.getMessageType().getNamespaceURI(), xmlValidator.getMessageType().getLocalPart(), decodedBytes);
                        } else {
                            em.addMessagePart(xmlValidator.getMessageType().getNamespaceURI(), xmlValidator.getMessageType().getLocalPart(), decodedBytes);
                        }
                        em.setXmlStatus(xmlValidator.getXMLStatus());
                        
                    }   // End If SOAP Encoding

            } else {
                //  Indicate that the request message has invalid XML
                System.err.println("NTCIP2306HTTPServlet::processRequest TODO xmlError:\n" + xmlValidator.getErrors());
                        if (em == null) {
                            em = new NTCIP2306Message("Unknown", "Unknown", decodedBytes);
                        }
                            em.setXmlStatus(xmlValidator.getXMLStatus());
                        
            }  // End If (xmlValidator.isValidXML(decodedBytes)
            if (em != null) {
                em.setXmlStatus(xmlValidator.getXMLStatus());
            } else {
                em = new NTCIP2306Message("None", "None", ("No message content for URI" + req.getRequestURI()).getBytes());
            }
        }  // end if (successfullyDecoded)

        if (em == null) {
            em = new NTCIP2306Message("None", "None", ("No message content for URI" + req.getRequestURI()).getBytes());
        }

        em.getHttpStatus().setValidHTTPHeaders(isValidHTTPProtocol(req));
        if (!isValidHTTPProtocol(req)) {
            em.getHttpStatus().addHTTPError("Invalid HTTP Protocol for request: " + req.getProtocol());
        }

        em.getHttpStatus().setSuccessfullyTransmitted(true);
        em.getHttpStatus().setSource(req.getRemoteAddr()+":"+req.getRemotePort());
        em.getHttpStatus().setDestination(req.getLocalAddr()+":"+req.getLocalPort());
        System.out.println("processRequest: Source - "+req.getRemoteAddr()+":"+req.getRemotePort()+"  Destination - "+req.getLocalAddr()+":"+req.getLocalPort());
        
        em.setEncodingType(NTCIP2306Message.ENCODINGTYPE.XML);
        if (opSpec.getOperationInputEncoding().equals(OperationSpecification.GZIP_ENCODING)) {
            handlerGZIPDecoder.getGZipStatus().setWellFormedXML(em.getXmlStatus().isNTCIP2306ValidXML());
            em.setEncodingType(NTCIP2306Message.ENCODINGTYPE.GZIP);
            em.setGzipStatus(handlerGZIPDecoder.getGZipStatus());
        }
        
        if (opSpec.getOperationInputEncoding().equals(OperationSpecification.SOAP_ENCODING)) {
            em.setEncodingType(NTCIP2306Message.ENCODINGTYPE.SOAP);
            em.setSoapStatus(handlerSOAPDecoder.getSoapStatus(opSpec.getProfileType(),true));
            em.getHttpStatus().setMatchingSOAPAction(true);
        }
        em.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
        em.setTransportedTimeInMs(receiveTime);
        if (req.isSecure()){
            em.setProtocolType(NTCIP2306Message.PROTOCOLTYPE.HTTPS);
        } else {
            em.setProtocolType(NTCIP2306Message.PROTOCOLTYPE.HTTP);            
        }
        System.out.println("NTCIP2306HTTPServlet::processRequest  Check that handlerSOAPDecoder SOAP Status setValidAgainstSchemas flag is "+handlerSOAPDecoder.getSoapStatus().isValidAgainstSchemas());
        System.out.println("NTCIP2306HTTPServlet::processRequest  Confirm the message SOAP Status setValidAgainstSchemas flag is "+em.getSoapStatus().isValidAgainstSchemas());
        OperationIdentifier theId = new OperationIdentifier(opSpec.getRelatedToService(),
                opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
        System.out.println("NTCIP2306HTTPServlet::processRequest Placed message on queue: @"+System.currentTimeMillis());
        queueController.addToExtRequestQueue(theId, em);
        return em;

    }

    /**
     * Process the HTTP response to an External Center
     * 
     * Get the response message from the queController.
     * If the response message does not specify that encoding be skipped then
     * If GZip Encoding is specified by the operation, then GZIP the response message.
     * Set the HTTP content type for GZIP
     * If SOAP Encoding is specified by the operation, then SOAP Encode the response message.
     * Set the HTTP content type for SOAP
     * If an encoding error occurred send a System Processing error response to the requester, otherwise
     * Send the response message back to the requesting system.
     *
     * @param opSpec the op spec
     * @param req the req
     * @param resp the resp
     * @return the NTCIP2306 message
     * @throws ServletException the servlet exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private NTCIP2306Message processResponse(OperationSpecification opSpec, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Read the respones Message available on the Queue.  Timeout after X seconds
        OperationIdentifier theId = new OperationIdentifier(opSpec.getRelatedToService(),
                opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
        System.out.println("NTCIP2306HTTPServlet::processResponse Looking for message on queue: theID="+theId);

        Integer responseTimeOut = 60000;
        try {
            responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RESPONSE_COMMAND_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RESPONSE_COMMAND_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        NTCIP2306Message em = queueController.getMessageFromExtResponseQueue(theId, responseTimeOut);
        handlerGZIPEncoder = new GZIPEncoder();
        handlerSOAPEncoder = new SOAPEncoder();
        XMLValidator xmlValidator = new XMLValidatorFactory().newXMLValidator();

        if (em != null) {
            if (!em.isSkipEncoding()) {
                if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING)) {
                        xmlValidator.isValidXML(em.getMessagePartContent(1));
                        xmlValidator.isXMLValidatedToSchema(new String(em.getMessagePartContent(1), "UTF-8"));
                        em.setXmlStatus(xmlValidator.getXMLStatus());
                    if (handlerGZIPEncoder.gzipEncode(em.getMessagePartContent(1))) {
                        em.setTransportedTimeInMs(System.currentTimeMillis());
                        resp.setContentType("application/x-gzip");
                        resp.getOutputStream().write(handlerGZIPEncoder.getGzippedResults());
                        em.getHttpStatus().setStatusCode(HttpServletResponse.SC_OK);
                        em.getHttpStatus().setSuccessfullyTransmitted(true);
                    } else {
                        em.setTransportedTimeInMs(System.currentTimeMillis());
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        String getResponse = "<error><h1>" + "GZIP Encoding Error" + "</h1>\n"
                                + "<h2>" + handlerGZIPEncoder.getEncodingErrorDescription() + "</h2></error>";
                        resp.getWriter().println(getResponse);
                        em.getHttpStatus().setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        em.getHttpStatus().setSuccessfullyTransmitted(true);
                    }
                    em.setGzipStatus(handlerGZIPEncoder.getGZipStatus());

                } else if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.SOAP_ENCODING)) {
                    ArrayList<byte[]> messageParts = new ArrayList<byte[]>();
                    for (int ii = 0; ii < em.getNumberMessageParts(); ii++) {
                        messageParts.add(em.getMessagePartContent(ii + 1));
                    }
                    if (em.getMessageType().equals(NTCIP2306Message.MESSAGETYPE.ERROR)){
                        em.setTransportedTimeInMs(System.currentTimeMillis());
                        resp.setContentType("text/xml");
                        resp.setHeader("SOAPAction", opSpec.getSoapAction());
                        if (handlerSOAPEncoder.encodeAsFault(messageParts, SOAPEncoder.SENDERFAULTCODE, "General SOAP Error")){
                            resp.getOutputStream().write(handlerSOAPEncoder.getEncodedMessage());                        
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);                            
                            xmlValidator.isValidXML(handlerSOAPEncoder.getEncodedMessage());
                            xmlValidator.isXMLValidatedToSchema(new String(handlerSOAPEncoder.getEncodedMessage(), "UTF-8"));
                            handlerSOAPEncoder.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());
                            em.setXmlStatus(xmlValidator.getXMLStatus());
                            em.getHttpStatus().setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            em.getHttpStatus().setSuccessfullyTransmitted(true);
                        } else {
                            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            String getResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n"
                                + "   \n"
                                + "   <SOAP-ENV:Body>\n"
                                + "   \n"
                                + "      <SOAP-ENV:Fault>\n"
                                + "         <faultcode>SOAP-ENV:Client</faultcode>\n"
                                + "\t\t \n"
                                + "         <faultstring>\n"
                                + "         SOAP POST Request Error\n"
                                + handlerSOAPEncoder.getEncodingErrorResults()                                
                                + "         </faultstring>\n"
                                + "\t\t \n"
                                + "      </SOAP-ENV:Fault>\n"
                                + "   </SOAP-ENV:Body>\n"
                                + "   \n"
                                + "</SOAP-ENV:Envelope>";
                            SOAPEncoder tmpSOAPEncoder = new SOAPEncoder();
                            ArrayList<byte[]>errorParts = new ArrayList<byte[]>();
                            errorParts.add(getResponse.getBytes());
                            tmpSOAPEncoder.encodeAsFault(errorParts,SOAPEncoder.RECEIVERFAULTCODE, "Error encoding SOAP Response");
                            resp.getWriter().println(new String(tmpSOAPEncoder.getEncodedMessage()));                            
                            em.getHttpStatus().setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            em.getHttpStatus().setSuccessfullyTransmitted(true);
                        };
                    } else if (handlerSOAPEncoder.encode(messageParts)) {
                        em.setTransportedTimeInMs(System.currentTimeMillis());
                        resp.setContentType("text/xml");
                        resp.setHeader("SOAPAction", opSpec.getSoapAction());
                        resp.getOutputStream().write(handlerSOAPEncoder.getEncodedMessage());
                        xmlValidator.isValidXML(handlerSOAPEncoder.getEncodedMessage());
                        xmlValidator.isXMLValidatedToSchema(new String(handlerSOAPEncoder.getEncodedMessage(), "UTF-8"));
                        handlerSOAPEncoder.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());
                        em.setXmlStatus(xmlValidator.getXMLStatus());
                        em.getHttpStatus().setStatusCode(HttpServletResponse.SC_OK);
                        em.getHttpStatus().setSuccessfullyTransmitted(true);
                    } else {
                        em.setTransportedTimeInMs(System.currentTimeMillis());
                        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        String getResponse = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/1999/XMLSchema\">\n"
                                + "   \n"
                                + "   <SOAP-ENV:Body>\n"
                                + "   \n"
                                + "      <SOAP-ENV:Fault>\n"
                                + "         <faultcode>SOAP-ENV:Client</faultcode>\n"
                                + "\t\t \n"
                                + "         <faultstring>\n"
                                + "         SOAP POST Request Error\n"
                                + handlerSOAPEncoder.getEncodingErrorResults()                                
                                + "         </faultstring>\n"
                                + "\t\t \n"
                                + "      </SOAP-ENV:Fault>\n"
                                + "   </SOAP-ENV:Body>\n"
                                + "   \n"
                                + "</SOAP-ENV:Envelope>";
                        resp.getWriter().println(getResponse);
                        xmlValidator.isValidXML(getResponse.getBytes());
                        xmlValidator.isXMLValidatedToSchema(new String(getResponse.getBytes(), "UTF-8"));
                        em.setXmlStatus(xmlValidator.getXMLStatus());
                        em.getHttpStatus().setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        em.getHttpStatus().setSuccessfullyTransmitted(true);
                    }
                    em.setSoapStatus(handlerSOAPEncoder.getSoapStatus(opSpec.getProfileType(),true));
                    em.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());
                } else {  // Process plain HTTP requests not using GZIP or SOAP
                    em.setTransportedTimeInMs(System.currentTimeMillis());
                    resp.setContentType("text/xml");

                    for (int ii = 0; ii < em.getNumberMessageParts(); ii++) {
                        resp.getOutputStream().write(em.getMessagePartContent(ii + 1));
                        xmlValidator.isValidXML(em.getMessagePartContent(ii + 1));
                        xmlValidator.isXMLValidatedToSchema(new String(em.getMessagePartContent(ii + 1),"UTF-8"));
                    }
                    em.getHttpStatus().setStatusCode(HttpServletResponse.SC_OK);
                    em.setXmlStatus(xmlValidator.getXMLStatus());
                    em.getHttpStatus().setSuccessfullyTransmitted(true);
                }  // End If Output Should be Encoded

            } else {
                em.setTransportedTimeInMs(System.currentTimeMillis());
                resp.setContentType("text/xml");
                for (int ii = 0; ii < em.getNumberMessageParts(); ii++) {
                    resp.getOutputStream().write(em.getMessagePartContent(ii + 1));
                    xmlValidator.isValidXML(em.getMessagePartContent(ii + 1));
                    xmlValidator.isXMLValidatedToSchema(new String(em.getMessagePartContent(ii + 1), "UTF-8"));
                    em.setXmlStatus(xmlValidator.getXMLStatus());
                    em.getHttpStatus().setSuccessfullyTransmitted(true);
                    
                    if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING)) {
                        GZIPDecoder gz = new GZIPDecoder();
                        gz.gzipDecode(em.getMessagePartContent(ii+1));
                        em.setGzipStatus(gz.getGZipStatus());
                        em.getGzipStatus().setWellFormedXML(xmlValidator.getXMLStatus().isNTCIP2306ValidXML());
                    } else if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.SOAP_ENCODING)) {
                        SOAPDecoder sd = new SOAPDecoder();
                        sd.isSoapEncoded(em.getMessagePartContent(ii+1));
                        em.setSoapStatus(sd.getSoapStatus(opSpec.getProfileType(),true));
                        em.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());
                    }
                    
               }
                em.getHttpStatus().setStatusCode(HttpServletResponse.SC_OK);
            }


        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String getResponse = "<errorResponseMsg><h1>" + "HTTP Processing Error" + "</h1>\n"
                    + "<h2>" + "No Response message available before timeout." + "</h2></errorResponseMsg>";
            long transmitTime = System.currentTimeMillis();
            resp.getWriter().println(getResponse);
            NTCIP2306Message tmpEm = new NTCIP2306Message(getResponse.getBytes());
            tmpEm.getHttpStatus().setSuccessfullyTransmitted(true);
            em = tmpEm;
            em.setTransportedTimeInMs(transmitTime);
            em.getHttpStatus().setStatusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            xmlValidator.isValidXML(getResponse.getBytes());
            xmlValidator.isXMLValidatedToSchema(new String(getResponse.getBytes(), "UTF-8"));
            em.setXmlStatus(xmlValidator.getXMLStatus());
            em.getHttpStatus().setSuccessfullyTransmitted(true);

        }
        em.setEncodingType(NTCIP2306Message.ENCODINGTYPE.XML);
        if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING)) {
            handlerGZIPEncoder.getGZipStatus().setWellFormedXML(em.getXmlStatus().isNTCIP2306ValidXML());
            em.setGzipStatus(handlerGZIPEncoder.getGZipStatus());
            em.setEncodingType(NTCIP2306Message.ENCODINGTYPE.GZIP);
        }
        if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.SOAP_ENCODING)) {
            em.setEncodingType(NTCIP2306Message.ENCODINGTYPE.SOAP);
            em.getHttpStatus().setMatchingSOAPAction(true);
        }

        if (req.isSecure()){
            em.setProtocolType(NTCIP2306Message.PROTOCOLTYPE.HTTPS);
        } else {
            em.setProtocolType(NTCIP2306Message.PROTOCOLTYPE.HTTP);            
        }
        
        em.getHttpStatus().setSource(req.getLocalAddr()+":"+req.getLocalPort());
        em.getHttpStatus().setDestination(req.getRemoteAddr()+":"+req.getRemotePort());
        em.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);

        System.out.println("processResponse: Source - "+req.getLocalAddr()+":"+req.getLocalPort()+"  Destination - "+req.getRemoteAddr()+":"+req.getRemotePort());

        return em;
    }

    /**
     * Checks if is valid http protocol.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param req the req
     * @return true, if is valid http protocol
     */
    private boolean isValidHTTPProtocol(HttpServletRequest req) {
        return req.getProtocol().equals("HTTP/1.1");
    }

    /**
     * Checks if is sOAP action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param req the req
     * @return true, if is sOAP action
     */
    private boolean isSOAPAction(HttpServletRequest req) {
        boolean isPresent = false;
        Enumeration headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            if ("SOAPAction".equals((String) headerNames.nextElement())) {
                isPresent = true;
                break;
            }
        }
        return isPresent;
    }

    /**
     * Includes soap action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param req the req
     * @return the string
     */
    private String includesSOAPAction(HttpServletRequest req) {
        String soapActionValue = null;
        Enumeration headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //CTCRI-748 Loosen restrictions on SOAPAction header to conform with HTTP specifications
            String headerName = (String) headerNames.nextElement();
            if ("SOAPAction".equalsIgnoreCase(headerName)) {
                soapActionValue = req.getHeader("SOAPAction");
                break;
            }
        }
        return soapActionValue;
    }

    /**
     * Gets the operation.
     *
     * @return the operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Gets the uri.
     *
     * @return the uri
     */
    public String getUri() {
        return uri;
    }

    /**
     * Gets the request content as byte array.
     *
     * @param req the req
     * @return the request content as byte array
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private byte[] getRequestContentAsByteArray(HttpServletRequest req) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[16384];
        while ((nRead = req.getInputStream().read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] receivedRequest = buffer.toByteArray();
        return receivedRequest;
    }
}
