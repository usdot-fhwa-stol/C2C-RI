/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.Header;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.VersionInfo;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.ntcip2306v109.encoding.GZIPDecoder;
import org.fhwa.c2cri.ntcip2306v109.encoding.GZIPEncoder;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.encoding.SOAPDecoder;
import org.fhwa.c2cri.ntcip2306v109.encoding.SOAPEncoder;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidatorFactory;
import org.fhwa.c2cri.applayer.ListenerManager;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class HTTPClientHandler performs HTTP Client get and post commands as requested.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class HTTPClientHandler implements Runnable {

    /** The destination folder. */
    private String destinationFolder;
    
    /** The get timeout. */
    private int getTimeout = 15000;
    
    /** The username. */
    private String username;
    
    /** The password. */
    private String password;
    
    /** The file name. */
    private String fileName;
    
    /** The local name. */
    private String localName = "";
    
    /** The soap action. */
    private String soapAction = "";
    
    /** The encoding type. */
    private String encodingType;
    
    /** The httpclient. */
    private DefaultHttpClient httpclient = new DefaultHttpClient();
    
    /** The request message content. */
    private byte[] requestMessageContent;
    
    /** The received response message. */
    private byte[] receivedResponseMessage;
    
    /** The received reply string. */
    private String receivedReplyString;
    
    /** The http headers. */
    private HashMap<String, String> httpHeaders;
    
    /** The log. */
    protected static Logger log = Logger.getLogger(HTTPClientHandler.class.getName());
    
    /** The is initialized. */
    private boolean isInitialized = false;
    
    /** The shutdown status. */
    private volatile boolean shutdownStatus = false;
    
    /** The eqc. */
    private QueueController eqc;
    
    /** The operation. */
    private String operation;
    
    /** The op spec collection. */
    private OperationSpecCollection opSpecCollection;
    
    /** The handler gzip encoder. */
    private GZIPEncoder handlerGZIPEncoder;
    
    /** The handler gzip decoder. */
    private GZIPDecoder handlerGZIPDecoder;
    
    /** The handler soap encoder. */
    private SOAPEncoder handlerSOAPEncoder;
    
    /** The handler soap decoder. */
    private SOAPDecoder handlerSOAPDecoder;
    
    /** The sf. */
    private static NTCIP2306SocketFactory sf;
    
    /** The sslsf. */
    private static NTCIP2306SSLSocketFactory sslsf;
    
    /** The response time. */
    private long responseTime = -1;
    
    /** Ensure that transmitted SOAPAction Header fields are enclosed in quotes */
    private boolean encloseInQuotes = false;

    /**
     * Instantiates a new hTTP client handler.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param eqc the eqc
     * @param opsCollection the ops collection
     */
    public HTTPClientHandler(String operation, final QueueController eqc, OperationSpecCollection opsCollection) {
        this.eqc = eqc;
        this.operation = operation;
        this.opSpecCollection = opsCollection;
    }

    /**
     * Instantiates a new hTTP client handler.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param eqc the eqc
     */
    public HTTPClientHandler(String operation, final QueueController eqc) {
        this.eqc = eqc;
        this.operation = operation;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        System.out.println("HTTPClientHandler::run Handler for operation: " + this.operation + " has started with QueueController " + this.eqc);
        while (!shutdownStatus) {
            try {
                for (OperationSpecification thisSpec : this.opSpecCollection.getCopyAsList()) {
                    OperationIdentifier opId = new OperationIdentifier(thisSpec.getRelatedToService(), thisSpec.getRelatedToPort(), thisSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
                    if (eqc.isAvaliableInRequestQueue(opId)) {
                        NTCIP2306Message inMessage = eqc.getMessageFromExtRequestQueue(opId);
                        if (inMessage != null) {
                            if (thisSpec.isGetOperation()) {
                                System.out.println("HTTPClientController::run:  Calling Get ...");
                                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                                get(thisSpec, inMessage);
                                Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
                            } else {
                                System.out.println("HTTPClientController::run:  Calling Post ...");
                                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                                post(thisSpec, inMessage);
                                Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
                            }
                        } else {
                            System.out.println("HTTPClientHandler::run inMessage = null!!");
                        }
                    }
                }
                Thread.currentThread().sleep(300);
            } catch (Exception ex) {
				if (ex instanceof InterruptedException)
					Thread.currentThread().interrupt();
                ex.printStackTrace();
                System.out.println(opSpecCollection == null ? "Ops Spec Collection was null!!" : "OK");
            }
        }
        System.out.println("HTTPClientHandler:run Exiting ....");
    }

    /**
     * Initialize.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @throws Exception the exception
     */
    public void initialize() throws Exception {
// Borrowed from the DefaultClient Class.  Added the Timeout parameters so that the client
// doesn't lock up when no server is found.
        isInitialized = false;
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params,
                HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params,
                HTTP.DEFAULT_CONTENT_CHARSET);
        HttpConnectionParams.setTcpNoDelay(params,
                true);
        HttpConnectionParams.setSocketBufferSize(params,
                8192);
        
        try {
            getTimeout = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_CONNECTION_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_CONNECTION_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        HttpConnectionParams.setConnectionTimeout(params, getTimeout);

        try {
            getTimeout = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        
        HttpConnectionParams.setSoTimeout(params, getTimeout);
        HttpProtocolParams.setUseExpectContinue(params,
                true);

        // determine the release version from packaged version info
        final VersionInfo vi = VersionInfo.loadVersionInfo("org.apache.http.client", getClass().getClassLoader());
        final String release = (vi != null)
                ? vi.getRelease() : VersionInfo.UNAVAILABLE;
        HttpProtocolParams.setUserAgent(params,
                "Apache-HttpClient/" + release + " (java 1.5)");



        String keystorePath = "";
        try {
            keystorePath = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_PATH_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_PATH_PARAMETER_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String keystorePassword = "";
        try {
            keystorePassword = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_PASSWORD_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_PASSWORD_PARAMETER_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String keyPassword = "";
        try {
            keyPassword = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_KEYPASSWORD_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_KEYPASSWORD_PARAMETER_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        String keyName = "";
        try {
            keyName = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_KEYNAME_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_KEYNAME_PARAMETER_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // load the keystore containing the client certificate - keystore type is probably jks or pkcs12
        final KeyStore keystore = KeyStore.getInstance("jks");
        InputStream keystoreInput = null;
        File keystoreFile = new File(keystorePath);
        keystoreInput = new FileInputStream(keystoreFile);

        keystore.load(keystoreInput, keystorePassword.toCharArray());

        // load the trustore, leave it null to rely on cacerts distributed with the JVM - truststore type is probably jks or pkcs12
        KeyStore truststore = KeyStore.getInstance("jks");
        InputStream truststoreInput = null;
        truststoreInput = new FileInputStream(keystoreFile);
        truststore.load(truststoreInput, keyPassword.toCharArray());


        // End Borrowed Code
        SchemeRegistry schreg = new SchemeRegistry();

        if (getLocation().contains("https")) {
            sslsf = new NTCIP2306SSLSocketFactory(keystore, keyName, truststore);

            sslsf.setHostnameVerifier(getVerifier());

            schreg.register(new Scheme("https", sslsf, 443));
            httpclient = new DefaultHttpClient(new NTCIP2306ClientConnectionManger(params, schreg), params);

        } else {
            sf = NTCIP2306SocketFactory.getSocketFactory();
            sf.setSecureMode(false);
            sf.setListenerName(this.operation);
            schreg.register(new Scheme("http", sf, 80));
            NTCIP2306ClientConnectionManger ccm = new NTCIP2306ClientConnectionManger(params, schreg);
            httpclient = new DefaultHttpClient(ccm, params);
        }

        String username = "rihttp";
        try {
            username = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_USERNAME_PARAMETER, username);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String password = "rihttp";
        try {
            password = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_PASSWORD_PARAMETER, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean useAuthentication = false;
        try {
            useAuthentication = Boolean.valueOf(RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_AUTHENTICATION_REQUIRED_PARAMETER, String.valueOf(useAuthentication)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        useAuthentication = false;
        if (useAuthentication) {
            System.out.println("HTTPClientHandler::Initialize Trying to authenticate with username " + username + " and password " + password);
            httpclient.getCredentialsProvider().setCredentials(
                    new AuthScope(null, -1),
                    new UsernamePasswordCredentials(username, password));

        }
        
        try {
            encloseInQuotes = Boolean.valueOf(RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.NTCIP2306_ENSURE_SOAPACTION_QUOTES_PARAMETER, String.valueOf(encloseInQuotes)));
            System.out.println("RIParameters provided encloseInQuotesParameter = " + encloseInQuotes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        isInitialized = true;
        System.out.println("HTTPClientHandler::Initialize - Operation->" + this.operation + " COMPLETED");

    }

    /**
     * Shutdown.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @throws Exception the exception
     */
    public void shutdown() throws Exception {
        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources
        httpclient.getConnectionManager().shutdown();
        if (sf != null) {
            ListenerManager.getInstance().stopListener(sf.getListenerId());
        }
        if (sslsf != null) {
            ListenerManager.getInstance().stopListener(sslsf.getListenerId());
        }
        this.shutdownStatus = true;
        System.out.println("HTTPClientHandler::shutdown The HTTP Client for operation" + this.operation + "has been shutdown and it's listener connection was stopped.");
    }

    /**
     * Gets the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param reqMsg the req msg
     * @throws Exception the exception
     */
    public void get(OperationSpecification opSpec, NTCIP2306Message reqMsg) throws Exception {
        System.out.println("Checking queue for message ..." + this.operation);
        System.out.println("Before Executing request get...");
        System.out.println("URL = " + getLocation() + (opSpec.getDocumentLocation() == null ? "" : "?" + opSpec.getDocumentLocation()));
        reqMsg.getHttpStatus().setSource(getRemoteAddress());
        reqMsg.getHttpStatus().setDestination(getLocalAddress());
        HttpGet httpget = new HttpGet(getLocation() + (opSpec.getDocumentLocation() == null ? "" : "?" + opSpec.getDocumentLocation()));
        // Execute HTTP Get request
        try {
            HttpResponse response = httpclient.execute(httpget);
            NTCIP2306Message respMsg = processResponse(opSpec, response);
            if (respMsg == null) {
                respMsg = new NTCIP2306Message("", "", "No Response Message received for operation.".getBytes());
            }
            OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
            eqc.addToExtResponseQueue(opId, respMsg);
            reqMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            eqc.addToOperationResultsQueue(opId, opResults);
            System.out.println("Get is complete and the message has been placed on the Queue.");
        } catch (ClientProtocolException ex) {
            ex.printStackTrace();
            NTCIP2306Message respMsg = new NTCIP2306Message("", "", "No Response Message received for operation.".getBytes());
            respMsg.getHttpStatus().setSuccessfullyTransmitted(false);
            respMsg.getHttpStatus().addHTTPError(ex.getMessage());
            respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.EXCEPTION);
            respMsg.setTransportErrorEncountered(true);
            respMsg.setTransportErrorDescription(ex.getMessage());
            OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
            eqc.addToExtResponseQueue(opId, respMsg);
            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            eqc.addToOperationResultsQueue(opId, opResults);

        } catch (IOException ex) {
            ex.printStackTrace();
            NTCIP2306Message respMsg = new NTCIP2306Message("", "", "No Response Message received for operation.".getBytes());
            respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.EXCEPTION);
            respMsg.getHttpStatus().setSuccessfullyTransmitted(false);
            respMsg.setTransportErrorEncountered(true);
            respMsg.setTransportErrorDescription(ex.getMessage());
            OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
            eqc.addToExtResponseQueue(opId, respMsg);
            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            eqc.addToOperationResultsQueue(opId, opResults);

        } catch (Exception ex) {
            ex.printStackTrace();
            NTCIP2306Message respMsg = new NTCIP2306Message("", "", "No Response Message received for operation.".getBytes());
            respMsg.setMessageType(NTCIP2306Message.MESSAGETYPE.EXCEPTION);
            respMsg.getHttpStatus().setSuccessfullyTransmitted(false);
            respMsg.getHttpStatus().addHTTPError(ex.getMessage());
            respMsg.setTransportErrorEncountered(true);
            respMsg.setTransportErrorDescription(ex.getMessage());
            OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
            eqc.addToExtResponseQueue(opId, respMsg);
            OperationResults opResults = new OperationResults(opSpec, reqMsg, respMsg);
            eqc.addToOperationResultsQueue(opId, opResults);
        }

    }

    /**
     * Post.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param inMessage the in message
     * @throws Exception the exception
     */
    public void post(OperationSpecification opSpec, NTCIP2306Message inMessage) throws Exception {

        NTCIP2306Message respMessage = null;
        OperationResults opResults = null;
        OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(), opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
        try {
            System.out.println("HTTPClientHandler::post Calling processRequest. @" + System.currentTimeMillis());
            HttpResponse postResult = processRequest(opSpec, inMessage);
            
                    Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), inMessage);
                    requestMessage.setMessageName("REQUEST");
                    log.info(requestMessage.toXML());
            
            System.out.println("HTTPClientHandler::post Calling processResponse. @" + System.currentTimeMillis());
            respMessage = processResponse(opSpec, postResult);
            if (respMessage == null) {
                respMessage = new NTCIP2306Message("", "", "No Response Message received for operation.".getBytes());
                respMessage.getHttpStatus().setSuccessfullyTransmitted(false);
            } else {
                respMessage.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
                respMessage.getHttpStatus().setSuccessfullyTransmitted(true);
            }
            eqc.addToExtResponseQueue(opId, respMessage);
            opResults = new OperationResults(opSpec, inMessage, respMessage);
            eqc.addToOperationResultsQueue(opId, opResults);

                    Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), respMessage);
                    responseMessage.setMessageName("RESPONSE");
                    log.info(responseMessage.toXML());
            
            System.out.println("HTTPClientHandler::post Post is complete and the message for opId " + opId + "has been placed on the Queue. @" + System.currentTimeMillis());
        } catch (Exception ex) {
            ex.printStackTrace();
            if (respMessage == null) {
                respMessage = new NTCIP2306Message("", "", ("Exception Encountered: " + ex.getMessage() + ".").getBytes());
                respMessage.getHttpStatus().setSuccessfullyTransmitted(false);
                respMessage.setMessageType(NTCIP2306Message.MESSAGETYPE.EXCEPTION);
                respMessage.setTransportErrorEncountered(true);
                respMessage.setTransportErrorDescription(ex.getMessage());
            }
            opResults = new OperationResults(opSpec, inMessage, respMessage);
            eqc.addToOperationResultsQueue(opId, opResults);
            eqc.addToExtResponseQueue(opId, respMessage);
        }

    }

    /**
     * This procedure performs the primary activities involved in perform a get
     * operation.
     * 
     * Pass the get request to the queController. A get request does not have
     * message content.
     * 
     * Call processResponse.
     * 
     * Send the Operation Completion status to the queController. Include any
     * success/failure status.
     *
     * @param opSpec the op spec
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private void processGet(OperationSpecification opSpec) throws IOException {
        if (eqc != null) {
            NTCIP2306Message em = new NTCIP2306Message("", "", "No Content for get request".getBytes());
            em.getHttpStatus().setValidHTTPHeaders(true);
            em.getHttpStatus().setSuccessfullyTransmitted(true);
            em.getHttpStatus().setSource(getRemoteAddress());
            em.getHttpStatus().setDestination(getLocalAddress());
            OperationIdentifier theId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
            eqc.addToExtRequestQueue(theId, em);
        }
    }

    /**
     * Process the HTTP request from an External Center
     * 
     * Store the received message in a local variable. If the operation
     * specifies that the input message is to be GZIP Encoded, decode the
     * request message content Store the decoding status If no decoding error
     * occurs, store the decoded content in a local variable for use in
     * validation. End If If no decoding error has occurred, validate the
     * received message that is stored in the local variable Store the
     * validation status If no validation error has occurred if the operation
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
    private NTCIP2306Message processResponse(OperationSpecification opSpec, HttpResponse resp) throws IOException {
        // Get hold of the response entity
        System.out.println("Response Message Name = " + resp.getStatusLine().toString());
        NTCIP2306Message em = null;
        System.out.println("executing request get " + getLocation() + "?" + opSpec.getDocumentLocation() + " Length: " + resp.getEntity().getContentLength());
        boolean successfullyDecoded = true;
        byte[] decodedBytes = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        NTCIP2306Message.ENCODINGTYPE encoding = NTCIP2306Message.ENCODINGTYPE.UNKNOWN;


        // If the response does not enclose an entity, there is no need
        // to worry about connection release
        try {
            // The sockets for the input stream were being used by subsequent calls to the same
            // address and port.  Ensure the socket is closed so that we don't pick up logging information
            // from these activities.
            InputStream responseStream = resp.getEntity().getContent();
            decodedBytes = getBytes(responseStream);
            responseStream.close();
            
            handlerGZIPDecoder = new GZIPDecoder();
            handlerSOAPDecoder = new SOAPDecoder();
            if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING)) {
                encoding = NTCIP2306Message.ENCODINGTYPE.GZIP;
                successfullyDecoded = handlerGZIPDecoder.gzipDecode(decodedBytes);
                if (successfullyDecoded) {
                    decodedBytes = handlerGZIPDecoder.getGzipDecodedResults();
                } else {
                    // In the future we may want to store the errors related to the request message.
                }
            }

            if (successfullyDecoded) {

                XMLValidator xmlValidator = new XMLValidatorFactory().newXMLValidator();
                if (xmlValidator.isValidXML(decodedBytes)) {
                    handlerGZIPDecoder.getGZipStatus().setWellFormedXML(xmlValidator.getXMLStatus().isNTCIP2306ValidXML());
                    if (encoding.equals(NTCIP2306Message.ENCODINGTYPE.UNKNOWN)) {
                        encoding = NTCIP2306Message.ENCODINGTYPE.XML;
                    }

                    System.err.println("HTTPClientHandler::processResponse About to Validate to Schema.");

                    xmlValidator.isXMLValidatedToSchema(new String(decodedBytes, "UTF-8"));
                    System.err.println("HTTPClientHandler::processResponse About to check SOAP Encoding.");
                    if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.SOAP_ENCODING)) {
                        encoding = NTCIP2306Message.ENCODINGTYPE.SOAP;
                        successfullyDecoded = handlerSOAPDecoder.isSoapEncoded(decodedBytes);
                        System.err.println("HTTPClientHandler::processResponse   Check if SOAP Encoding OK.");

                        System.err.println("HTTPClientHandler::processResponse  SOAP Encoding is OK.");
                        XMLValidator subxmlValidator = XMLValidatorFactory.getValidator();
                        for (int ii = 1; ii <= handlerSOAPDecoder.getNumMessageParts(); ii++) {
                            try {
                                System.err.println("HTTPClientHandler::processResponse  Validating message part " + ii + ".");
                                if (subxmlValidator.isValidXML(handlerSOAPDecoder.getMessagePart(ii))) {
                                    if (em == null) {
                                        em = new NTCIP2306Message(subxmlValidator.getMessageType().getNamespaceURI(), subxmlValidator.getMessageType().getLocalPart(), handlerSOAPDecoder.getMessagePart(ii));
                                    } else {
                                        em.addMessagePart(subxmlValidator.getMessageType().getNamespaceURI(), subxmlValidator.getMessageType().getLocalPart(), handlerSOAPDecoder.getMessagePart(ii));
                                    }
                                } else {
                                    System.err.println("HTTPClientHandler::processResponse  Message part " + ii + " was invalid.\n" + xmlValidator.getXmlValidErrors());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                successfullyDecoded = false;
                                // In the future we may want to store the XML Parsing errors related extracting the request message from SOAP.
                            }
                        }
                        if (!successfullyDecoded) {
                            if (em == null) {
                                em = new NTCIP2306Message(decodedBytes);
                            }
                        }

                        // The SOAP body must be validated against the schema.
                        em.setSoapStatus(handlerSOAPDecoder.getSoapStatus());
                        em.setXmlStatus(xmlValidator.getXMLStatus());
                        em.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());
                    } else {
                        if (em == null) {
                            em = new NTCIP2306Message(xmlValidator.getMessageType().getNamespaceURI(), xmlValidator.getMessageType().getLocalPart(), decodedBytes);
                        } else {
                            em.addMessagePart(xmlValidator.getMessageType().getNamespaceURI(), xmlValidator.getMessageType().getLocalPart(), decodedBytes);
                        }
                    }   // End If SOAP Encoding

                } else {
                    //  Indicate that the request message has invalid XML
                    System.out.println("HTTPClientHandler::processResponse XML Validation Errors:  " + xmlValidator.getXMLStatus().getXMLErrors());
                }  // End If (xmlValidator.isValidXML(decodedBytes)
                if (em == null) {
                    em = new NTCIP2306Message("", "", resp.getStatusLine().toString().getBytes());
                }
                em.setXmlStatus(xmlValidator.getXMLStatus());
            }  // end if (successfullyDecoded)


            if (em == null) {
                em = new NTCIP2306Message("", "", resp.getStatusLine().toString().getBytes());
            }

            if ((handlerGZIPDecoder == null) || (!opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING))) {
            } else {
                em.setGzipStatus(handlerGZIPDecoder.getGZipStatus());
                em.getGzipStatus().setWellFormedXML(em.getXmlStatus().isNTCIP2306ValidXML());
            }
            if ((handlerSOAPDecoder == null) || (!opSpec.getOperationOutputEncoding().equals(OperationSpecification.SOAP_ENCODING))) {
            } else {
                em.setSoapStatus(handlerSOAPDecoder.getSoapStatus(opSpec.getProfileType(), false));
                em.getSoapStatus().setValidAgainstSchemas(em.getXmlStatus().isXmlValidatedToWSDLSchemas());
            }
            OperationIdentifier theId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);


            em.getHttpStatus().setValidHTTPHeaders(isValidHTTPProtocol(resp));
            if (!isValidHTTPProtocol(resp)) {
                em.getHttpStatus().addHTTPError("Invalid HTTP Protocol for response: " + resp.getStatusLine().getProtocolVersion().getProtocol());
            }
            em.getHttpStatus().setSource(getRemoteAddress());
            em.getHttpStatus().setDestination(getLocalAddress());

            em.getHttpStatus().setSuccessfullyTransmitted(true);
            em.getHttpStatus().setStatusCode(resp.getStatusLine().getStatusCode());

        } catch (IOException ex) {
            ex.printStackTrace();
            em.getHttpStatus().setSuccessfullyTransmitted(false);
            em.getHttpStatus().setStatusCode(resp.getStatusLine().getStatusCode());
            em.setMessageType(NTCIP2306Message.MESSAGETYPE.EXCEPTION);
            // In case of an IOException the connection will be released
            // back to the connection manager automatically
            throw ex;

        } catch (RuntimeException ex) {
            ex.printStackTrace();
            em.getHttpStatus().setSuccessfullyTransmitted(false);
            em.getHttpStatus().setStatusCode(resp.getStatusLine().getStatusCode());
            em.setMessageType(NTCIP2306Message.MESSAGETYPE.EXCEPTION);

            // In case of an unexpected exception you may want to abort
            // the HTTP request in order to shut down the underlying
            // connection and release it back to the connection manager.
            throw ex;

        } finally {

            // Closing the input stream will trigger connection release
            buffer.close();

        }

        // When HttpClient instance is no longer needed,
        // shut down the connection manager to ensure
        // immediate deallocation of all system resources





        em.setEncodingType(encoding);
        System.out.println("Protocol Version Output: " + resp.getProtocolVersion().getProtocol());
        if (resp.getProtocolVersion().getProtocol().equalsIgnoreCase("http")) {
            em.setProtocolType(NTCIP2306Message.PROTOCOLTYPE.HTTP);
        } else {
            em.setProtocolType(NTCIP2306Message.PROTOCOLTYPE.HTTPS);
        }
        em.setTransportedTimeInMs(responseTime);
        em.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
        return em;
    }

    /**
     * Process the HTTP response to an External Center
     * 
     * Get the response message from the queController. If the response message
     * does not specify that encoding be skipped then If GZip Encoding is
     * specified by the operation, then GZIP the response message. Set the HTTP
     * content type for GZIP If SOAP Encoding is specified by the operation,
     * then SOAP Encode the response message. Set the HTTP content type for SOAP
     * If an encoding error occurred send a System Processing error response to
     * the requester, otherwise Send the response message back to the requesting
     * system.
     *
     * @param opSpec the op spec
     * @param em the em
     * @return the http response
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws Exception the exception
     */
    private HttpResponse processRequest(OperationSpecification opSpec, NTCIP2306Message em) throws IOException, Exception {
        NTCIP2306Message.ENCODINGTYPE encoding = NTCIP2306Message.ENCODINGTYPE.UNKNOWN;
        // Read the respones Message available on the Queue.  Timeout after X seconds
        OperationIdentifier theId = new OperationIdentifier(opSpec.getRelatedToService(),
                opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
        XMLValidator xmlValidator = new XMLValidatorFactory().newXMLValidator();
        handlerSOAPEncoder = new SOAPEncoder();
        if (em != null) {
            HttpPost httppost;

            if (em.isPublicationMessage()) {
                httppost = new HttpPost(em.getPublicationURL());
            } else {
                httppost = new HttpPost(getLocation() + (opSpec.getDocumentLocation() == null ? "" : "?" + opSpec.getDocumentLocation()));
            }

            if (!em.isSkipEncoding()) {
                if ((opSpec.isGetOperation() && opSpec.getOperationInputEncoding().equals(OperationSpecification.GZIP_ENCODING))
                        || (!opSpec.isGetOperation() && opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING))) {
                    xmlValidator.isValidXML(em.getMessagePartContent(1));
                    xmlValidator.isXMLValidatedToSchema(new String(em.getMessagePartContent(1), "UTF-8"));
                    em.setXmlStatus(xmlValidator.getXMLStatus());
                    encoding = NTCIP2306Message.ENCODINGTYPE.GZIP;
                    handlerGZIPEncoder = new GZIPEncoder();
                    if (handlerGZIPEncoder.gzipEncode(em.getMessagePartContent(1))) {
                        httppost.setHeader("ContentType", "application/x-gzip");
                        ByteArrayEntity baEnt = new ByteArrayEntity(handlerGZIPEncoder.getGzippedResults());
                        baEnt.setContentEncoding("gzip");
                        baEnt.setContentType("text/xml; charset=utf-8");
                        httppost.setEntity(baEnt);
                        em.setGzipStatus(handlerGZIPEncoder.getGZipStatus());

                    } else {
                        em.setGzipStatus(handlerGZIPEncoder.getGZipStatus());
                        throw new Exception("GZIP Encoding Exception Encountered: " + handlerGZIPEncoder.getEncodingErrorDescription());

                    }
                    em.setGzipStatus(handlerGZIPEncoder.getGZipStatus());

                } else if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.SOAP_ENCODING)) {
                    encoding = NTCIP2306Message.ENCODINGTYPE.SOAP;
                    handlerSOAPEncoder = new SOAPEncoder();
                    ArrayList<byte[]> messageParts = new ArrayList<byte[]>();
                    for (int ii = 0; ii < em.getNumberMessageParts(); ii++) {
                        messageParts.add(em.getMessagePartContent(ii + 1));
                        System.err.println("HTTPClientHandler::processRequest  message part " + (ii + 1) + " = " + new String(em.getMessagePartContent(ii + 1)));
                    }
                    if (handlerSOAPEncoder.encode(messageParts)) {
                        if (em.isPublicationMessage()) {
                            httppost.addHeader("SOAPAction", processSOAPActionField(em.getPublicationSoapAction(),encloseInQuotes));
                        } else {
                            httppost.addHeader("SOAPAction", processSOAPActionField(opSpec.getSoapAction(),encloseInQuotes));
                            em.getHttpStatus().setMatchingSOAPAction(true);
                        }
                        xmlValidator.isValidXML(handlerSOAPEncoder.getEncodedMessage());
                        xmlValidator.isXMLValidatedToSchema(new String(handlerSOAPEncoder.getEncodedMessage(), "UTF-8"));
                        em.setXmlStatus(xmlValidator.getXMLStatus());

                        ByteArrayEntity baEnt = new ByteArrayEntity(handlerSOAPEncoder.getEncodedMessage());
                        baEnt.setContentType("text/xml; charset=utf-8");

                        if (em.isPublicationMessage()) {
                            httppost.setHeader("SOAPAction", processSOAPActionField(em.getPublicationSoapAction(),encloseInQuotes));
                        } else {
                            httppost.setHeader("SOAPAction", processSOAPActionField(opSpec.getSoapAction(),encloseInQuotes));
                            em.getHttpStatus().setMatchingSOAPAction(true);
                        }
                        httppost.setEntity(baEnt);
                        em.setSoapStatus(handlerSOAPEncoder.getSoapStatus(opSpec.getProfileType(), false));
                        em.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());
                    } else {
                        if (em.isPublicationMessage()) {
                            httppost.addHeader("SOAPAction", processSOAPActionField(em.getPublicationSoapAction(),encloseInQuotes));
                        } else {
                            httppost.addHeader("SOAPAction", processSOAPActionField(opSpec.getSoapAction(),encloseInQuotes));
                            em.getHttpStatus().setMatchingSOAPAction(true);
                        }
                        xmlValidator.isValidXML(messageParts.get(0));
                        xmlValidator.isXMLValidatedToSchema(new String(messageParts.get(0), "UTF-8"));
                        em.setXmlStatus(xmlValidator.getXMLStatus());

                        ByteArrayEntity baEnt = new ByteArrayEntity(messageParts.get(0));
                        baEnt.setContentType("text/xml; charset=utf-8");

                        if (em.isPublicationMessage()) {
                            httppost.setHeader("SOAPAction", processSOAPActionField(em.getPublicationSoapAction(),encloseInQuotes));
                        } else {
                            httppost.setHeader("SOAPAction", processSOAPActionField(opSpec.getSoapAction(),encloseInQuotes));
                            em.getHttpStatus().setMatchingSOAPAction(true);
                        }
                        httppost.setEntity(baEnt);
                        em.setSoapStatus(handlerSOAPEncoder.getSoapStatus(opSpec.getProfileType(), false));
                        em.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());

                    }
                } else {
                    encoding = NTCIP2306Message.ENCODINGTYPE.XML;
                    xmlValidator.isValidXML(em.getMessagePartContent(1));
                    xmlValidator.isXMLValidatedToSchema(new String(em.getMessagePartContent(1), "UTF-8"));
                    em.setXmlStatus(xmlValidator.getXMLStatus());
                    ByteArrayEntity baEnt = new ByteArrayEntity(em.getMessagePartContent(1));
                    baEnt.setContentType("text/xml; charset=utf-8");
                    httppost.setEntity(baEnt);

                }
            } else {
                System.out.println("HTTPClientHandler::processRequest Skipped Encoding as Requested.");
                xmlValidator.isValidXML(em.getMessagePartContent(1));
                xmlValidator.isXMLValidatedToSchema(new String(em.getMessagePartContent(1), "UTF-8"));
                em.setXmlStatus(xmlValidator.getXMLStatus());
                if ((opSpec.isGetOperation() && opSpec.getOperationInputEncoding().equals(OperationSpecification.GZIP_ENCODING))
                        || (!opSpec.isGetOperation() && opSpec.getOperationOutputEncoding().equals(OperationSpecification.GZIP_ENCODING))) {
                    handlerGZIPEncoder.gzipEncode(em.getMessagePartContent(1));
                    em.setGzipStatus(handlerGZIPEncoder.getGZipStatus());
                    encoding = NTCIP2306Message.ENCODINGTYPE.GZIP;
                    httppost.setHeader("ContentType", "application/x-gzip");
                    ByteArrayEntity baEnt = new ByteArrayEntity(em.getMessagePartContent(1));
                    baEnt.setContentEncoding("gzip");
                    baEnt.setContentType("text/xml; charset=utf-8");
                    httppost.setEntity(baEnt);
                } else if (opSpec.getOperationOutputEncoding().equals(OperationSpecification.SOAP_ENCODING)) {
                    encoding = NTCIP2306Message.ENCODINGTYPE.SOAP;
                    if (em.isPublicationMessage()) {
                        httppost.addHeader("SOAPAction", processSOAPActionField(em.getPublicationSoapAction(),encloseInQuotes));
                    } else {
                        httppost.addHeader("SOAPAction", processSOAPActionField(opSpec.getSoapAction(),encloseInQuotes));
                        em.getHttpStatus().setMatchingSOAPAction(true);
                    }
                    ArrayList<byte[]> messageParts = new ArrayList();
                    messageParts.add(em.getMessagePartContent(1));
                    try {
                        xmlValidator.isValidXML(em.getMessagePartContent(1));
                        boolean validationResults = xmlValidator.isValidToSchemas();
                        handlerSOAPDecoder = new SOAPDecoder();
                        boolean decoded = handlerSOAPDecoder.isSoapEncoded(em.getMessagePartContent(1));
                        handlerSOAPDecoder.getSoapStatus().setValidAgainstSchemas(validationResults);
                        
                        xmlValidator.isValidXML(em.getMessagePartContent(1));
                        xmlValidator.isXMLValidatedToSchema(new String(em.getMessagePartContent(1), "UTF-8"));                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    ByteArrayEntity baEnt = new ByteArrayEntity(em.getMessagePartContent(1));
                    baEnt.setContentType("text/xml; charset=utf-8");
                    if (em.isPublicationMessage()) {
                        httppost.setHeader("SOAPAction", processSOAPActionField(em.getPublicationSoapAction(),encloseInQuotes));
                    } else {
                        httppost.setHeader("SOAPAction", processSOAPActionField(opSpec.getSoapAction(),encloseInQuotes));
                    }
                    httppost.setEntity(baEnt);
                    em.setSoapStatus(handlerSOAPDecoder != null?handlerSOAPDecoder.getSoapStatus(opSpec.getProfileType(), false):
                            handlerSOAPEncoder.getSoapStatus(opSpec.getProfileType(), false));
                    em.setXmlStatus(xmlValidator.getXMLStatus());
                    em.getSoapStatus().setValidAgainstSchemas(xmlValidator.isValidToSchemas());
                    
                }
            }

            try {
                System.out.println("HTTPClientHandler::processRequest Operation->" + this.operation + "  About to send post with url " + httppost.getRequestLine().toString());
                em.setTransportedTimeInMs(System.currentTimeMillis());
                HttpResponse response = httpclient.execute(httppost);
                responseTime = System.currentTimeMillis();
                
                if (em.isSkipEncoding() && handlerSOAPDecoder != null){
                    for (int ii = 1; ii<= em.getNumberMessageParts(); ii++){
                        em.removeMessagePart(ii);
                    }
                    for (int jj = 1; jj<= handlerSOAPDecoder.getNumMessageParts(); jj++){
                        String namespace = handlerSOAPDecoder.getMessagePartElementName(jj).getNamespaceURI();
                        String elementName = handlerSOAPDecoder.getMessagePartElementName(jj).getLocalPart();
                        em.addMessagePart(namespace, elementName, handlerSOAPDecoder.getMessagePart(jj));
                    }
                    System.out.println("HTTPClientHandler::processRequest Finished SOAP Decoder processing for skipEncoding message");
                }

                log.debug("[HTTPClientTransport] Executing HTTPPOST");
                em.getHttpStatus().setSuccessfullyTransmitted(true);
                em.getHttpStatus().setValidHTTPHeaders(true);
                // Address get switched up after the response...
                em.getHttpStatus().setSource(getLocalAddress());
                em.getHttpStatus().setDestination(getRemoteAddress());
                em.getHttpStatus().setStatusCode(response.getStatusLine().getStatusCode());
                em.setEncodingType(encoding);
                if (response.getProtocolVersion().getProtocol().contains("HTTPS")
                        || response.getProtocolVersion().getProtocol().contains("https")) {
                    em.setProtocolType(NTCIP2306Message.PROTOCOLTYPE.HTTPS);
                } else {
                    em.setProtocolType(NTCIP2306Message.PROTOCOLTYPE.HTTP);
                }
                em.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
                return response;


            } catch (ClientProtocolException ex) {
                em.getHttpStatus().setSuccessfullyTransmitted(false);
                em.getHttpStatus().addHTTPError(ex.getMessage());
                em.setTransportErrorEncountered(true);
                em.setTransportErrorDescription(ex.getMessage());
                log.debug("*HTTPClientHandler:processRequest: ClientProtocolException attempting to abort httppost ");
                httppost.abort();
                log.debug("*HTTPClientHandler:processRequest: ClientProtocolException = " + ex.getMessage());
                ex.printStackTrace();
                throw ex;
            } catch (IOException ex) {
                log.debug("*HTTPClientHandler:processRequest: IOException attempting to abort httppost ");
                httppost.abort();
                log.debug("*HTTPClientHandler:processRequest: IOException = " + ex.getMessage());
                em.getHttpStatus().setSuccessfullyTransmitted(false);
                em.getHttpStatus().addHTTPError(ex.getMessage());
                ex.printStackTrace();
                em.setTransportErrorEncountered(true);
                em.setTransportErrorDescription(ex.getMessage());
                throw ex;

            } catch (RuntimeException ex) {
                // In case of an unexpected exception you may want to abort
                // the HTTP request in order to shut down the underlying
                // connection immediately.
                log.debug("*HTTPClientHandler:processRequest: RunTimeException attempting to abort httppost ");
                httppost.abort();
                log.debug("*HTTPClientHandler:processRequest: RunTimeException " + ex.getMessage());
                em.getHttpStatus().setSuccessfullyTransmitted(false);
                em.getHttpStatus().addHTTPError(ex.getMessage());
                em.setTransportErrorEncountered(true);
                em.setTransportErrorDescription(ex.getMessage());
                ex.printStackTrace();
                throw ex;
            }



        }
        throw new Exception("Unexpected exit from HTTPClientHandler::processRequest method.");
    }

    /**
     * Process the SOAPAction header field value and ensure that it is enclosed in quotes if the flag is set.
     * @param soapAction the initial SOAPAction Header Field value
     * @param ensureEnclosedInQuotes indicates whether the SOAPAction header field should be enclosed in quotes
     * @return fully processed SOAPAction Header field value
     */
    private String processSOAPActionField(String soapAction, boolean ensureEnclosedInQuotes){
        String updatedSOAPAction = soapAction;
        if(ensureEnclosedInQuotes){
            if((!soapAction.startsWith("\""))&&!soapAction.endsWith("\"")){
                updatedSOAPAction = "\""+soapAction+"\"";
            }
        }
        return updatedSOAPAction;
    }
    /**
     * Checks if is valid http protocol.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param resp the resp
     * @return true, if is valid http protocol
     */
    private boolean isValidHTTPProtocol(HttpResponse resp) {
        System.out.println("isValidHTTPProtocol -- version response = " + resp.getStatusLine().getProtocolVersion());
        return resp.getStatusLine().getProtocolVersion().toString().equals("HTTP/1.1");
    }

    /**
     * Checks if is sOAP action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param resp the resp
     * @return true, if is sOAP action
     */
    private boolean isSOAPAction(HttpResponse resp) {
        boolean isPresent = false;
        for (Header thisHeader : resp.getAllHeaders()) {
            if ("SOAPAction".equals(thisHeader.getName())) {
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
     * @param resp the resp
     * @return the string
     */
    private String includesSOAPAction(HttpResponse resp) {
        String soapActionValue = null;
        for (Header thisHeader : resp.getAllHeaders()) {
            if ("SOAPAction".equals(thisHeader.getName())) {
                soapActionValue = thisHeader.getValue();
                break;
            }
        }
        return soapActionValue;
    }

    /**
     * Gets the location.
     *
     * @return the location
     */
    private String getLocation() {
        return this.operation;
    }

    /**
     * Gets the local address.
     *
     * @return the local address
     */
    private String getLocalAddress() {
        if (httpclient != null) {
            if (sf != null) {
                System.out.println("HTTPClientHandler::LocalAddress: " + sf.getLocalAddress());
                return sf.getLocalAddress();
            } else if (sslsf != null) {
                System.out.println("HTTPClientHandler::LocalAddress (Secure): " + sslsf.getLocalAddress());
                return sslsf.getLocalAddress();

            }
        }
        return "";
    }

    /**
     * Gets the local port.
     *
     * @return the local port
     */
    private String getLocalPort() {
        if (httpclient != null) {
            if (sf != null) {
                return sf.getLocalPort();
            } else if (sslsf != null) {
                return sslsf.getLocalPort();
            }
        }
        return "";
    }

    /**
     * Gets the remote address.
     *
     * @return the remote address
     */
    private String getRemoteAddress() {
        if (httpclient != null) {
            if (sf != null) {
                System.out.println("HttpClientHandler::RemoteAddress: " + sf.getRemoteAddress());
                return sf.getRemoteAddress();
            } else if (sslsf != null) {
                System.out.println("HttpClientHandler::RemoteAddress (Secure): " + sslsf.getRemoteAddress());
                return sslsf.getRemoteAddress();

            }
        }
        return "";
    }

    /**
     * Gets the remote port.
     *
     * @return the remote port
     */
    private String getRemotePort() {
        if (httpclient != null) {
            if (sf != null) {
                return sf.getRemotePort();
            } else if (sslsf != null) {
                return sslsf.getRemotePort();
            }
        }
        return "";
    }

    /**
     * Gets the bytes.
     *
     * @param is the is
     * @return the bytes
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private byte[] getBytes(InputStream is) throws IOException {

        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
            buf = bos.toByteArray();
        }
        return buf;
    }

    /**
     * Gets the verifier.
     *
     * @return the verifier
     */
    private X509HostnameVerifier getVerifier() {
        class LocalVerifier implements X509HostnameVerifier {

            @Override
            public void verify(String string, SSLSocket ssls) throws IOException {
				// original implementation was empty
            }

            @Override
            public void verify(String string, X509Certificate xc) throws SSLException {
				// original implementation was empty
            }

            @Override
            public void verify(String string, String[] strings, String[] strings1) throws SSLException {
				// original implementation was empty
            }

            @Override
            public boolean verify(String string, SSLSession ssls) {
                System.out.println("HTTPClientHandler:Verifier  All hostnames are currently passed.");
                return true;
            }
        }
        return new LocalVerifier();
    }
}
