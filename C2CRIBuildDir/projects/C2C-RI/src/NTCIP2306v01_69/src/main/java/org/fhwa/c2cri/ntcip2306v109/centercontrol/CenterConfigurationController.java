/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.centercontrol;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.java.net.ConnectionsDirectory;
import org.fhwa.c2cri.ntcip2306v109.operations.DefaultOperationListener;
import org.fhwa.c2cri.ntcip2306v109.http.server.NTCIP2306HTTPServer;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueManager;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidatorFactory;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center.RICENTERMODE;
import org.fhwa.c2cri.ntcip2306v109.ftp.client.FTPClientController;
import org.fhwa.c2cri.ntcip2306v109.ftp.server.FTPServerController;
import org.fhwa.c2cri.ntcip2306v109.http.HTTPClientController;
import org.fhwa.c2cri.ntcip2306v109.subpub.SubPubController;
import org.fhwa.c2cri.applayer.NamedThreadFactory;
import org.fhwa.c2cri.ntcip2306v109.subpub.DefaultSubPubMapper;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.ntcip2306v109.wsdl.ServiceSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.ServiceSpecification;

/**
 * Represents a center configured as either an EC or OC as specified by the WSDL.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class CenterConfigurationController {

    /**
     * The logger associated with this class.
     */
    protected static Logger log = Logger.getLogger(CenterConfigurationController.class.getName());
    
    /** the WSDL associated with the standard. */
    private RIWSDL standardWSDL;
    
    /** the WSDL associated with the project. */
    private RIWSDL projectWSDL;
    
    /** the mode that the RI is operating in. */
    private RICENTERMODE riCenterMode;
    
    /** the default listener that handles all operations. */
    private DefaultOperationListener opListener;
    
    /** the controller that handles subscriptions and publications. */
    private SubPubController subController;
    
    /** the queue used for communication between scripts and this controller. */
    private QueueController internalQueueController = new QueueManager();
    
    /** the queue used for communication between protocol handlers and this controller. */
    private QueueManager externalQueueManager;
    
    /** the executor that manages the protocol handler threads. */
    private ExecutorService executor;
    
    /** the HTTP Server service controller. */
    private NTCIP2306HTTPServer theServer;
    
    /** the HTTP Client service controller. */
    private HTTPClientController theClient;
    
    /** the FTP Server service controller. */
    private FTPServerController ftpServer;
    
    /** the FTP Client service controller. */
    private FTPClientController ftpClient;

    /**
     * Constructor.
     *
     * @param standardWSDL - the WSDL associated with a standard
     * @param projectWSDL - the WSDL specified by the project
     * @param riCenterMode - the mode that this center will operate (EC or OC)
     */
    public CenterConfigurationController(RIWSDL standardWSDL, RIWSDL projectWSDL, RICENTERMODE riCenterMode) {
        this.standardWSDL = standardWSDL;
        this.projectWSDL = projectWSDL;
        this.riCenterMode = riCenterMode;
    }

    /**
     * Initializes the center services based on the services defined in the
     * WSDL.
     *
     * @throws InterruptedException - thread was interrupted
     * @throws Exception - other errors
     */
    public void initialzeServices() throws InterruptedException, Exception {
        ConnectionsDirectory.getInstance().clearAllConnections();
        System.out.println("\n\nCenterConfigurationController::initializeServices Started!! @ " + System.currentTimeMillis() + "  Center Mode: " + this.riCenterMode + "\n\n");
        CountDownLatch readySignal = new CountDownLatch(6);

        getAllOperations();
        ArrayList schemaList = projectWSDL.getWsdlSchemaNodeSectionsAndURLs();
        URL soapEnvURL = RIWSDL.class.getResource("/org/fhwa/c2cri/ntcip2306v109/soap/Soap-Envelope.xsd");
        URL soapEncURL = RIWSDL.class.getResource("/org/fhwa/c2cri/ntcip2306v109/soap/soapEncoding.xsd");
        schemaList.add(soapEnvURL.toString());
        schemaList.add(soapEncURL.toString());

        new XMLValidatorFactory().configure(schemaList);
        executor = Executors.newCachedThreadPool(new NamedThreadFactory("CenterConfigControl-" + riCenterMode));
        externalQueueManager = new QueueManager(readySignal);
        executor.submit(externalQueueManager);
        opListener = new DefaultOperationListener(externalQueueManager, internalQueueController, getRRandGetOperations(), riCenterMode, readySignal);
        executor.submit(opListener);
        OperationSpecCollection opSpec;
        if (getRiCenterMode().equals((RICENTERMODE.OC))) {
            opSpec = getServerSubPubOperations();
        } else {
            opSpec = getClientSubPubOperations();
        }
        DefaultSubPubMapper.setCenterMode(riCenterMode);
        DefaultSubPubMapper.setOperationSpecCollection(opSpec);

        subController = new SubPubController(riCenterMode, opSpec, internalQueueController, externalQueueManager, readySignal);
        executor.submit(subController);

        // Enable the logging of any connections that are created during the operation of this service.
        ConnectionsDirectory.getInstance().setEnableLiveLogging(true);

        boolean listenerMode = false;
        if (getRiCenterMode().equals(RICENTERMODE.EC)) {
            listenerMode = true;
        }
        theServer = new NTCIP2306HTTPServer((QueueController) externalQueueManager,
                getHTTPServerServices(), listenerMode, readySignal);
        executor.submit(theServer);

        theClient = new HTTPClientController((QueueController) externalQueueManager, getHTTPClientServices(), readySignal);
        executor.submit(theClient);
        System.out.println("\n\nCenterConfigurationController::initializeServices Starting FTP!! @ " + System.currentTimeMillis() + "\n\n");
        if (getRiCenterMode().equals(RICENTERMODE.OC)) {
            ftpServer = new FTPServerController((QueueController) externalQueueManager, getFTPServerServices(), readySignal);
            executor.submit(ftpServer);
        } else {
            ftpClient = new FTPClientController((QueueController) externalQueueManager, getFTPClientServices(), readySignal);
            executor.submit(ftpClient);
        }

        readySignal.await(20, TimeUnit.SECONDS);
        if (getRiCenterMode().equals(RICENTERMODE.OC)) {
            if ((ftpServer != null) && !ftpServer.isInitializationCompleted()) {
                stopServices();
                throw new Exception("An error was encountered initializing the FTP Server.  The defined Center Services have been shut down.");
            }
        } else {
            if ((ftpClient != null) && !ftpClient.isInitializationCompleted()) {
                stopServices();
                throw new Exception("An error was encountered initializing the FTP Client.  The defined Center Services have been shut down.");
            }

        }
        System.out.println("\n\nCenterConfigurationController::initializeServices Completed with countdownn count " + readySignal.getCount() + "!! @ " + System.currentTimeMillis() + "\n\n");
    }

    /**
     * Start all services.
     */
    public void startServices() {
    }

    /**
     * Stop all services.
     */
    public void stopServices() {
        if (subController != null)subController.shutdown();
        theServer.shutdown();
        theClient.shutdown();
        if (ftpServer != null) {
            ftpServer.shutdown();
        }
        if (ftpClient != null) {
            ftpClient.shutdown();
        }
        if (executor != null)executor.shutdownNow();
        if (opListener != null) opListener.shutdown();
        if (externalQueueManager != null)externalQueueManager.shutdown();
        if (internalQueueController != null)((QueueManager)internalQueueController).shutdown();

        opListener = null;
        subController = null;
        externalQueueManager = null;
        internalQueueController = null;
        
        // Disable logging for any subsequenet connections.
        ConnectionsDirectory.getInstance().setEnableLiveLogging(false);
        ConnectionsDirectory.getInstance().clearAllConnections();

    }

    // Methods for Script Listener Access
    /**
     * Registers a listener (script) to interact with the center for a given
     * operation.
     *
     * @param opId - the service, port and operation name for this operation as
     * defined in the WSDL.
     * @throws Exception the exception
     */
    public void registerForOperation(OperationIdentifier opId) throws Exception {
        OperationSpecification opSpec = getOperationSpecification(opId);

        if (opSpec != null) {
            if (opSpec.isGetOperation() || opSpec.isRequestResponseOperation()) {
                opListener.registerExternalOperationListner(opId);
            } else if (opSpec.isSubscriptionOperation()) {
                subController.setScriptControlForSubscription(opId);
            } else if (opSpec.isPublicationOperation() || opSpec.isPublicationCallbackOperation()) {
                subController.setScriptControlForPublication(opId);
            }
        } else {
            System.err.println("No Such Operation has been defined: " + opId);
            throw new Exception("CenterConfigurationController::registerForOperation -- No Such Operation has been defined: " + opId);
        }
    }

    /**
     * UnRegisters a listener (script) to interact with the center for a given
     * operation.
     *
     * @param opId - the service, port and operation name for this operation as
     * defined in the WSDL.
     * @throws Exception the exception
     */
    public void unregisterForOperation(OperationIdentifier opId) throws Exception {
        OperationSpecification opSpec = getOperationSpecification(opId);

        if (opSpec != null) {
            if (opSpec.isGetOperation() || opSpec.isRequestResponseOperation()) {
                opListener.unregisterExternalOperationListener(opId);
            } else if (opSpec.isSubscriptionOperation()) {
                subController.clearScriptControlForSubscription(opId);
            } else if (opSpec.isPublicationOperation() || opSpec.isPublicationCallbackOperation()) {
                subController.clearScriptControlForPublication(opId);
            }
        } else {
            System.err.println("No Such Operation has been defined: " + opId);
            throw new Exception("CenterConfigurationController::unregisterForOperation -- No Such Operation has been defined: " + opId);
        }
    }

    /**
     * Send a message to the specified operation.
     *
     * @param opId - the identifier for the operation.
     * @param message - the message being sent
     */
    public void sendRequest(OperationIdentifier opId, NTCIP2306Message message) {
//        if (opListener.isRegisteredOperation(opId)){
        System.out.println("CenterConfigurationController::sendRequest Send Request to internalqueueController:" + internalQueueController + " @" + System.currentTimeMillis());
        internalQueueController.addToExtRequestQueue(opId, message);
//        }
    }

    /**
     * Checks whether a request for an given operation is pending.
     *
     * @param opId - the identifier for the operation
     * @return - true if a message is present
     */
    public boolean isRequestPresent(OperationIdentifier opId) {
        return internalQueueController.isAvaliableInRequestQueue(opId);
    }

    /**
     * Returns a message related to the given operation. This procedure will
     * wait indefinitely until a message becomes available.
     *
     * @param opId - the identifier for the operation
     * @return the message that was received
     */
    public NTCIP2306Message getRequest(OperationIdentifier opId) {
        NTCIP2306Message message = null;
        //      if (opListener.isRegisteredOperation(opId)){
        message = internalQueueController.getMessageFromExtRequestQueue(opId);
        //      }
        return message;
    }

    /**
     * Returns a message related to the given operation. This procedure will
     * return null if no message is received before the timeout period.
     *
     * @param opId - the identifier for the operation
     * @param timeoutInMillis - the amount of time to wait for a message
     * @return the message that was received
     */
    public NTCIP2306Message getRequest(OperationIdentifier opId, Integer timeoutInMillis) {
        NTCIP2306Message message = null;
        System.out.println("CenterConfigurationController::getRequest Get Request from internalqueueController:" + internalQueueController + " @" + System.currentTimeMillis());
        message = internalQueueController.getMessageFromExtRequestQueue(opId, timeoutInMillis);
        return message;
    }

    /**
     * Sends a response message related to the given operation.
     *
     * @param opId - the identifier for the operation
     * @param message - the message that is to be sent
     */
    public void sendResponse(OperationIdentifier opId, NTCIP2306Message message) {
        internalQueueController.addToExtResponseQueue(opId, message);
    }

    /**
     * Checks whether a response message is available for a given operation.
     *
     * @param opId - the identifier for the operation
     * @return true if a response message is pending
     */
    public boolean isResponsePresent(OperationIdentifier opId) {
        return internalQueueController.isAvaliableInResponseQueue(opId);
    }

    /**
     * Gets the response message for the given operation identifier. This method
     * will not return until a response message is received.
     *
     * @param opId - the identifier for the operation
     * @return the response message
     */
    public NTCIP2306Message getResponse(OperationIdentifier opId) {
        NTCIP2306Message message = null;
        message = internalQueueController.getMessageFromExtResponseQueue(opId);
        return message;
    }

    /**
     * Gets the response message for the given operation identifier. If the
     * timeout expires before a response message is received a null value is
     * returned.
     *
     * @param opId - the identifier for the operation
     * @param timeoutInMillis - the timeout value in milliseconds
     * @return the message or null if the timeout period expires before a
     * message is returned.
     */
    public NTCIP2306Message getResponse(OperationIdentifier opId, Integer timeoutInMillis) {
        NTCIP2306Message message = null;
        System.out.println("CenterConfigurationController::getResponse Get Response from internalqueueController:" + internalQueueController + " @" + System.currentTimeMillis());
        message = internalQueueController.getMessageFromExtResponseQueue(opId, timeoutInMillis);
        return message;
    }

    /**
     * Checks whether operation results are available for a given operation
     * identifier.
     *
     * @param opId -the operation that is being checked
     *
     * @return true if operation results are available for the given operation
     */
    public boolean isOperationResultsPresent(OperationIdentifier opId) {
        return internalQueueController.isAvaliableInOperationResultsQueue(opId);
    }

    /**
     * Gets the operation results for the given operation identifier. This
     * method will not return until operation results are received from a
     * service handler.
     *
     * @param opId - the identifier for the operation
     * @return the operation results
     */
    public OperationResults getOperationResults(OperationIdentifier opId) {
        OperationResults results = null;
        results = internalQueueController.getResultsFromOperationResultsQueue(opId);
        return results;
    }

    /**
     * Gets the operation results for the given operation identifier. If the
     * timeout expires before operation results are received a null value is
     * returned.
     *
     * @param opId - the identifier for the operation
     * @param timeoutInMillis - the timeout period in milliseconds
     * @return the operation results or null if the timeout period expired
     * before operation results were received
     */
    public OperationResults getOperationResults(OperationIdentifier opId, Integer timeoutInMillis) {
        OperationResults results = null;
        results = internalQueueController.getResultsFromOperationResultsQueue(opId, timeoutInMillis);
        return results;
    }

    /**
     * Set the mappings between subscription operations and their associated
     * publication operations.
     *
     * @param subpubMap - a map of the set of subscription operations and the
     * related publication operations
     */
    public void setSubPubMapping(HashMap<OperationIdentifier, OperationIdentifier> subpubMap) {
        DefaultSubPubMapper.setLocalSubPubMapping(subpubMap);
    }

    /**
     * Initiate a Subscription operation as an EC.
     *
     * @param opId - the operation identifier
     * @param message - the message to be sent
     * @param timeout - the timeout period for the response
     * @return the results of the operation
     */
    public OperationResults subEC(OperationIdentifier opId, NTCIP2306Message message, Integer timeout) {
        OperationResults results = null;
        sendRequest(opId, message);
        // read the response to clear it from the queue
        NTCIP2306Message subResponseMsg = getResponse(opId, timeout);
        results = getOperationResults(opId, timeout);

        OperationSpecification opSpec = getClientSubPubOperations().get(opId.getServiceName(),
                opId.getPortName(), opId.getOperationName());
        subController.processSubscriptionResponse(opSpec, message);
        return results;
    }

    /**
     * Initiates a the receive portion of a publication operation as an EC.
     *
     * @param opId - the identifier of the operation
     * @param timeout - the timeout period to wait for the publication message
     * @return - the message received
     */
    public NTCIP2306Message pubECReceive(OperationIdentifier opId, Integer timeout) {
        NTCIP2306Message message = null;
        message = getRequest(opId, timeout);

        OperationSpecification opSpec = getClientSubPubOperations().get(opId.getServiceName(),
                opId.getPortName(), opId.getOperationName());
        return message;
    }

    /**
     * Initiates the response portion of a publication operation as an EC.
     *
     * @param opId - the identifier of the operation
     * @param message - the message response being sent
     * @param timeout - the timeout period to await operation results
     * @return the operation results
     */
    public OperationResults pubECResponse(OperationIdentifier opId, NTCIP2306Message message, Integer timeout) {
        OperationResults results = null;
        sendResponse(opId, message);
        results = getOperationResults(opId, timeout);

        OperationSpecification opSpec = getClientSubPubOperations().get(opId.getServiceName(),
                opId.getPortName(), opId.getOperationName());

        return results;
    }

    /**
     * Initiate the publication operation as an OC.
     *
     * @param opId - the operation identifier
     * @param message - the message being sent as a publication to an EC
     * @param timeout - the timeout period to wait until a operation is complete
     * @return the results of the operation
     */
    public OperationResults pubOC(OperationIdentifier opId, NTCIP2306Message message, Integer timeout) {
        OperationResults results = null;
        sendRequest(opId, message);
        // read the response queue to clear out the response message
        NTCIP2306Message pubResponseMsg = getResponse(opId, timeout);
        results = getOperationResults(opId, timeout);

        OperationSpecification opSpec = getServerSubPubOperations().get(opId.getServiceName(),
                opId.getPortName(), opId.getOperationName());

        return results;
    }

    /**
     * Initiate the receive portion of a subscription operation for an OC.
     *
     * @param opId - the identifier for the operation
     * @param timeout - the timeout value to wait for a subscription request
     * @return the received message
     */
    public NTCIP2306Message subOCReceive(OperationIdentifier opId, Integer timeout) {
        NTCIP2306Message message = null;
        message = getRequest(opId, timeout);

        OperationSpecification opSpec = getServerSubPubOperations().get(opId.getServiceName(),
                opId.getPortName(), opId.getOperationName());

        return message;
    }

    /**
     * Perform the Response portion of a subscription as an OC.
     *
     * @param opId - the operation identifier
     * @param message - the message that will be sent as a response
     * @param timeout - the timeout period for the operation
     * @return the results of the operation
     */
    public OperationResults subOCResponse(OperationIdentifier opId, NTCIP2306Message message, Integer timeout) {
        OperationResults results = null;
        sendResponse(opId, message);
        results = getOperationResults(opId, timeout);

        OperationSpecification opSpec = getServerSubPubOperations().get(opId.getServiceName(),
                opId.getPortName(), opId.getOperationName());

        return results;
    }

    /**
     * Set the list of publication messages that the OC will send.
     *
     * @param opId - the operation identifier
     * @param messageList - the list of messages
     * @throws Exception the exception
     */
    public void setOCPublicationMessages(OperationIdentifier opId, ArrayList<NTCIP2306Message> messageList) throws Exception {
        this.subController.loadPubMessage(opId, messageList);
    }

    /**
     * Performs a check on whether the subscription related to an operation is
     * active.
     *
     * @param opId - the operation identifier
     * @return true if the subscription is active.
     */
    public boolean isSubscriptionActive(OperationIdentifier opId) {
        try {
            switch (subController.getSubscriptionStatus(opId)) {
                case ACTIVE:
                    return true;
                case INITIALIZING:
                    return true;
                case UPDATING:
                    return true;
                default:
                    return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    /**
     * Get the state of a publication related to the given operation.
     *
     * @param opId - the operation identifier
     * @return true is the publication operation is active
     */
    public boolean isPublicationActive(OperationIdentifier opId) {
        System.out.println("CCC publicationActive Value: " + subController.getPublicationStatus(opId));
        switch (subController.getPublicationStatus(opId)) {
            case COMPLETED:
                return false;
            default:
                return true;
        }
    }

    /**
     * The current publication count for a given publication operation.
     *
     * @param opId - the operation identifier.
     * @return the publication count. Valid values are >= 1. Otherwise there is
     * no publication active.
     */
    public long getPublicationCount(OperationIdentifier opId) {
        return subController.getPublicationCount(opId);
    }

    /**
     * Get the amount of time between the last two publications.
     *
     * @param opId - the operation identifier
     * @return the number of milliseconds since the last publication.
     */
    public long getMillisSinceLastPeriodicPublication(OperationIdentifier opId) {
        return subController.getMillisSinceLastPeriodicPublication(opId);
    }

    /**
     * The current publication count for a given publication operation.
     *
     * @param opId - the operation identifier.
     * @return the publication count. Valid values are >= 1. Otherwise there is
     * no publication active.
     */
    public long getPublicationFrequency(OperationIdentifier opId) {
        return subController.getPublicationFrequency(opId);
    }    
    
    // End Methods for Script Listener Access
    /**
     * Get the collection of operations that were included in the WSDL.
     *
     * @return the collection of operations.
     */
    private OperationSpecCollection getAllOperations() {
        OperationSpecCollection resultingOperations = new OperationSpecCollection();
        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                if (!projectOpSpec.isErroneousSpecification()) {
                    resultingOperations.add(projectOpSpec);
                } else {
                    System.err.println("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName() + "\n" + projectOpSpec.getErrorsEncountered().toString());
                    log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                }
            }
        }
        return resultingOperations;
    }

    /**
     * Get the Request Response and get related operations specified in the
     * WSDL.
     *
     * @return the request-response and get operations.
     */
    private OperationSpecCollection getRRandGetOperations() {
        OperationSpecCollection resultingOperations = new OperationSpecCollection();
        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                if (projectOpSpec.isGetOperation() || projectOpSpec.isRequestResponseOperation()) {
                    if (!projectOpSpec.isErroneousSpecification()) {
                        resultingOperations.add(projectOpSpec);
                    } else {
                        log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                    }
                }
            }
        }
        return resultingOperations;
    }

    /**
     * Get the complete operation specification for a given operation
     * identifier.
     *
     * @param opId - the operation identifier
     * @return the operation specification
     */
    private OperationSpecification getOperationSpecification(OperationIdentifier opId) {
        OperationSpecification resultingSpec = null;
        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                if (projectOpSpec.getRelatedToService().equals(opId.getServiceName())
                        && projectOpSpec.getRelatedToPort().equals(opId.getPortName())
                        && projectOpSpec.getOperationName().equals(opId.getOperationName())) {
                    if (!projectOpSpec.isErroneousSpecification()) {
                        resultingSpec = projectOpSpec;
                        break;
                    }
                }
            }
        }
        return resultingSpec;
    }

    /**
     * Get the Service Specifications for all HTTP services defined in the WSDL.
     *
     * @return the service spec collection.
     */
    private ServiceSpecCollection getHTTPServerServices() {
        ServiceSpecCollection specList = new ServiceSpecCollection();

        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            ServiceSpecification.SERVICETYPE serviceType = ServiceSpecification.SERVICETYPE.SERVER;
            OperationSpecCollection resultingOperations = new OperationSpecCollection();
            try {
                URL thisServiceURL = new URL(thisSpec.getLocation());
                if (thisServiceURL.getProtocol().equalsIgnoreCase("http") || thisServiceURL.getProtocol().equalsIgnoreCase("https")) {
                    if (getRiCenterMode().equals(RICENTERMODE.OC)) {
                        serviceType = ServiceSpecification.SERVICETYPE.SERVER;
                        for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                            if (projectOpSpec.isPublicationCallbackOperation()) {
                                serviceType = ServiceSpecification.SERVICETYPE.CLIENT;
                            }
                            if (!projectOpSpec.isErroneousSpecification()) {
                                resultingOperations.add(projectOpSpec);
                            } else {
                                log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                            }
                        }
                    } else {
                        serviceType = ServiceSpecification.SERVICETYPE.CLIENT;
                        for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                            if (projectOpSpec.isPublicationCallbackOperation()) {
                                serviceType = ServiceSpecification.SERVICETYPE.LISTENER;
                            }
                            if (!projectOpSpec.isErroneousSpecification()) {
                                resultingOperations.add(projectOpSpec);
                            } else {
                                log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                            }
                        }

                    }

                }
            } catch (Exception ex) {
                log.trace("Error encountered processing Service Specification: " + ex.getMessage());
            }

            if (serviceType.equals(ServiceSpecification.SERVICETYPE.LISTENER)
                    || serviceType.equals(ServiceSpecification.SERVICETYPE.SERVER)) {
                ServiceSpecification httpService = new ServiceSpecification(thisSpec.getName(), thisSpec.getLocation(), serviceType, resultingOperations);
                specList.add(httpService);
            }

        }

        return specList;
    }

    /**
     * Get the HTTP Client service specifications defined in the WSDL.
     *
     * @return the service specifications
     */
    private ServiceSpecCollection getHTTPClientServices() {
        ServiceSpecCollection specList = new ServiceSpecCollection();

        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            ServiceSpecification.SERVICETYPE serviceType = ServiceSpecification.SERVICETYPE.CLIENT;
            OperationSpecCollection resultingOperations = new OperationSpecCollection();
            try {
                URL thisServiceURL = new URL(thisSpec.getLocation());
                if (thisServiceURL.getProtocol().equalsIgnoreCase("http") || thisServiceURL.getProtocol().equalsIgnoreCase("https")) {
                    if (getRiCenterMode().equals(RICENTERMODE.OC)) {
                        serviceType = ServiceSpecification.SERVICETYPE.SERVER;
                        for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                            if (projectOpSpec.isPublicationCallbackOperation()) {
                                serviceType = ServiceSpecification.SERVICETYPE.CLIENT;
                            }
                            if (!projectOpSpec.isErroneousSpecification()) {
                                resultingOperations.add(projectOpSpec);
                                System.out.println("Added Client Spec:  " + projectOpSpec.getOperationName());
                            } else {
                                log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                            }
                        }
                    } else {
                        serviceType = ServiceSpecification.SERVICETYPE.CLIENT;
                        for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                            if (projectOpSpec.isPublicationCallbackOperation()) {
                                serviceType = ServiceSpecification.SERVICETYPE.LISTENER;
                            }
                            if (!projectOpSpec.isErroneousSpecification()) {
                                resultingOperations.add(projectOpSpec);
                                System.out.println("Added Client Spec:  " + projectOpSpec.getOperationName());
                            } else {
                                System.err.println("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                                log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                            }
                        }

                    }

                }
            } catch (Exception ex) {
                log.trace("Error encountered processing Service Specification: " + ex.getMessage());
            }

            if (serviceType.equals(ServiceSpecification.SERVICETYPE.CLIENT)) {
                ServiceSpecification httpService = new ServiceSpecification(thisSpec.getName(), thisSpec.getLocation(), serviceType, resultingOperations);
                specList.add(httpService);
            }

        }

        return specList;
    }

    /**
     * Get the FTP Server services that are defined in the WSDL.
     *
     * @return the service specifications
     */
    private ServiceSpecCollection getFTPServerServices() {
        ServiceSpecCollection specList = new ServiceSpecCollection();

        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            ServiceSpecification.SERVICETYPE serviceType = ServiceSpecification.SERVICETYPE.SERVER;
            OperationSpecCollection resultingOperations = new OperationSpecCollection();
            try {
                URL thisServiceURL = new URL(thisSpec.getLocation());
                if (thisServiceURL.getProtocol().equalsIgnoreCase("ftp")) {
                    if (getRiCenterMode().equals(RICENTERMODE.OC)) {
                        serviceType = ServiceSpecification.SERVICETYPE.SERVER;
                        for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                            if (!projectOpSpec.isErroneousSpecification()) {
                                resultingOperations.add(projectOpSpec);
                            } else {
                                log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                            }
                        }
                    }

                }
            } catch (Exception ex) {
                log.trace("Error encountered processing Service Specification: " + ex.getMessage());
            }

            if (serviceType.equals(ServiceSpecification.SERVICETYPE.SERVER)) {
                ServiceSpecification ftpService = new ServiceSpecification(thisSpec.getName(), thisSpec.getLocation(), serviceType, resultingOperations);
                specList.add(ftpService);
            }

        }

        return specList;
    }

    /**
     * Get the FTP Client related services defined in the WSDL file.
     *
     * @return the FTP services
     */
    private ServiceSpecCollection getFTPClientServices() {
        ServiceSpecCollection specList = new ServiceSpecCollection();

        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            ServiceSpecification.SERVICETYPE serviceType = ServiceSpecification.SERVICETYPE.CLIENT;
            OperationSpecCollection resultingOperations = new OperationSpecCollection();
            try {
                URL thisServiceURL = new URL(thisSpec.getLocation());
                if (thisServiceURL.getProtocol().equalsIgnoreCase("ftp")) {
                    if (getRiCenterMode().equals(RICENTERMODE.EC)) {
                        serviceType = ServiceSpecification.SERVICETYPE.CLIENT;
                        for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                            if (!projectOpSpec.isErroneousSpecification()) {
                                resultingOperations.add(projectOpSpec);
                            } else {
                                log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                            }
                        }

                    }

                    if (serviceType.equals(ServiceSpecification.SERVICETYPE.CLIENT)) {
                        ServiceSpecification ftpClientService = new ServiceSpecification(thisSpec.getName(), thisSpec.getLocation(), serviceType, resultingOperations);
                        specList.add(ftpClientService);
                    }

                }
            } catch (Exception ex) {
                log.trace("Error encountered processing Service Specification: " + ex.getMessage());
            }

        }

        return specList;
    }

    /**
     * Get the specifications for the Subscription and Publication Server
     * operations.
     *
     * @return the operation specifications
     */
    private OperationSpecCollection getServerSubPubOperations() {
        OperationSpecCollection resultingOperations = new OperationSpecCollection();

        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            try {
                URL thisServiceURL = new URL(thisSpec.getLocation());
                if (thisServiceURL.getProtocol().equalsIgnoreCase("http") || thisServiceURL.getProtocol().equalsIgnoreCase("https")) {
                    if (getRiCenterMode().equals(RICENTERMODE.OC)) {
                        for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                            if (projectOpSpec.isPublicationCallbackOperation() || projectOpSpec.isSubscriptionOperation()) {
                                if (!projectOpSpec.isErroneousSpecification()) {
                                    resultingOperations.add(projectOpSpec);
                                } else {
                                    log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.trace("Error encountered processing Service Specification: " + ex.getMessage());
            }

        }

        return resultingOperations;
    }

    /**
     * Get the subscription and publication operations from the WSDL for a
     * client.
     *
     * @return the operation specifications
     */
    private OperationSpecCollection getClientSubPubOperations() {
        OperationSpecCollection resultingOperations = new OperationSpecCollection();

        for (ServiceSpecification thisSpec : getProjectWSDL().getServiceSpecifications().getCopyAsList()) {
            try {
                URL thisServiceURL = new URL(thisSpec.getLocation());
                if (thisServiceURL.getProtocol().equalsIgnoreCase("http") || thisServiceURL.getProtocol().equalsIgnoreCase("https")) {
                    if (getRiCenterMode().equals(RICENTERMODE.EC)) {
                        for (OperationSpecification projectOpSpec : thisSpec.getOperations().getCopyAsList()) {
                            if (projectOpSpec.isPublicationOperation() || projectOpSpec.isSubscriptionOperation()) {
                                if (!projectOpSpec.isErroneousSpecification()) {
                                    resultingOperations.add(projectOpSpec);
                                } else {
                                    log.trace("Skipped Erroneous Operation Service Specification: " + projectOpSpec.getOperationName());
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                log.trace("Error encountered processing Service Specification: " + ex.getMessage());
            }

        }

        return resultingOperations;
    }

    /*
     * getter for the project WSDL
     */
    /**
     * Gets the project wsdl.
     *
     * @return the project wsdl
     */
    private RIWSDL getProjectWSDL() {
        return projectWSDL;
    }

    /**
     * getter for the center mode.
     *
     * @return center mode
     */
    private RICENTERMODE getRiCenterMode() {
        return riCenterMode;
    }

    /**
     * Getter for the specified standard WSDL.
     *
     * @return the standard WSDL
     */
    private RIWSDL getStandardWSDL() {
        return standardWSDL;
    }
}
