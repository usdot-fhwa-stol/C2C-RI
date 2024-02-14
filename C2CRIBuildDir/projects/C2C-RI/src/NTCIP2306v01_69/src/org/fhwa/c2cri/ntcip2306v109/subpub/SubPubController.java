/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.subpub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.C2CMessageReceipt;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.C2CMessageSubscription;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center.RICENTERMODE;
import org.fhwa.c2cri.applayer.NamedThreadFactory;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ApplicationLayerOperationResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Controller;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306TransportException;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.messaging.DefaultMessageContentGenerator;
import org.fhwa.c2cri.ntcip2306v109.status.NTCIP2306Status;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;

/**
 * The Class SubPubController handles the generation of subscriptions and
 * publications for both owner center and external center modes of operation.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class SubPubController implements Runnable {

    /**
     * The op spec collection.
     */
    private final OperationSpecCollection opSpecCollection;

    /**
     * The iqc.
     */
    private final QueueController iqc;

    /**
     * The eqc.
     */
    private final QueueController eqc;

    /**
     * The shutdown.
     */
    private volatile boolean shutdown = false;

    /**
     * The center mode.
     */
    private Center.RICENTERMODE centerMode;

    /**
     * The subscriptions.
     */
    private final ConcurrentHashMap<OperationIdentifier, ArrayList<Subscription>> subscriptions = new ConcurrentHashMap<OperationIdentifier, ArrayList<Subscription>>();

    /**
     * The publications.
     */
    private final ConcurrentHashMap<OperationIdentifier, ArrayList<Publication>> publications = new ConcurrentHashMap<OperationIdentifier, ArrayList<Publication>>();

    /**
     * The default sub pub map.
     */
    private final ConcurrentHashMap<OperationIdentifier, OperationIdentifier> defaultSubPubMap = new ConcurrentHashMap<OperationIdentifier, OperationIdentifier>();

    /**
     * The publication message map.
     */
    private final ConcurrentHashMap<OperationIdentifier, ArrayList<NTCIP2306Message>> publicationMessageMap = new ConcurrentHashMap();

    /**
     * The scripted subscriptions.
     */
    private final CopyOnWriteArrayList<OperationIdentifier> scriptedSubscriptions = new CopyOnWriteArrayList<OperationIdentifier>();

    /**
     * The scripted publications.
     */
    private final CopyOnWriteArrayList<OperationIdentifier> scriptedPublications = new CopyOnWriteArrayList<OperationIdentifier>();

    /**
     * The defined subscriptions.
     */
    private final CopyOnWriteArrayList<OperationIdentifier> definedSubscriptions = new CopyOnWriteArrayList<OperationIdentifier>();

    /**
     * The defined publications.
     */
    private final CopyOnWriteArrayList<OperationIdentifier> definedPublications = new CopyOnWriteArrayList<OperationIdentifier>();

    /**
     * The executor.
     */
    private final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("SubPubControl-" + getCenterMode()));

    /**
     * The ready signal.
     */
    private final CountDownLatch readySignal;

    /**
     * The shutdown signal.
     */
    private volatile boolean shutdownSignal = false;
 
    /**
     * The logger to used for storing message to the log file.
     */
    private Logger log = Logger.getLogger(SubPubController.class.getName());
    
    /**
     * Instantiates a new sub pub controller.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param centerMode the center mode
     * @param opSpec the op spec
     * @param iqc the iqc
     * @param eqc the eqc
     */
    public SubPubController(Center.RICENTERMODE centerMode, OperationSpecCollection opSpec, final QueueController iqc, final QueueController eqc) {
        this.centerMode = centerMode;
        this.opSpecCollection = opSpec;
        this.iqc = iqc;
        this.eqc = eqc;
        this.readySignal = null;
        this.subscriptions.clear();
        this.publications.clear();
        this.defaultSubPubMap.clear();
        this.scriptedPublications.clear();
        this.scriptedSubscriptions.clear();
        this.definedSubscriptions.clear();
        this.definedPublications.clear();
    }

    /**
     * Instantiates a new sub pub controller.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param centerMode the center mode
     * @param opSpec the op spec
     * @param iqc the iqc
     * @param eqc the eqc
     * @param readySignal the ready signal
     */
    public SubPubController(Center.RICENTERMODE centerMode, OperationSpecCollection opSpec, final QueueController iqc, final QueueController eqc, final CountDownLatch readySignal) {
        this.centerMode = centerMode;
        this.opSpecCollection = opSpec;
        this.iqc = iqc;
        this.eqc = eqc;
        this.subscriptions.clear();
        this.publications.clear();
        this.defaultSubPubMap.clear();
        this.scriptedPublications.clear();
        this.scriptedSubscriptions.clear();
        this.definedSubscriptions.clear();
        this.definedPublications.clear();
        this.readySignal = readySignal;
    }

    /**
     * Initialize.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private void initialize() {
        for (OperationSpecification thisSpec : opSpecCollection.getCopyAsList()) {
            OperationIdentifier handlerOpId = new OperationIdentifier(thisSpec.getRelatedToService(),
                    thisSpec.getRelatedToPort(), thisSpec.getOperationName(),
                    OperationIdentifier.SOURCETYPE.HANDLER);
            OperationIdentifier listenerOpId = new OperationIdentifier(thisSpec.getRelatedToService(),
                    thisSpec.getRelatedToPort(), thisSpec.getOperationName(),
                    OperationIdentifier.SOURCETYPE.LISTENER);
            if (thisSpec.isSubscriptionOperation()) {
                definedSubscriptions.add(handlerOpId);
                definedSubscriptions.add(listenerOpId);
            } else {
                definedPublications.add(handlerOpId);
                definedPublications.add(listenerOpId);
            }
        }
    }

    /**
     * Initialize the controller by registering sub and pub operations with the
     * default listener
     *
     * If this Center is an owner center For each sub or pub operation loop
     * check for sub requests if there is a sub request operation from a handler
     * process the subscription request check for pub responses if there is a
     * pub response operation from a handler process the publication response
     * check for sub operation results if there is a sub operation result from a
     * handler process the sub operation result Else if this Center is an
     * external center check for sub responses if there is a sub response
     * operation from a handler process the subscription response check for pub
     * requests if there is a pub request operation from a handler process the
     * publication request check for pub operation results if there is a pub
     * operation result from a handler process the pub operation result.
     */
    public void run() {


        initialize();
        if (readySignal != null) {
            readySignal.countDown();
        }
        while (!isShutdown()) {
            try {
                manageOperations();

                Thread.currentThread().sleep(200);
            } catch (InterruptedException iex) {
                iex.printStackTrace();
                setShutdown(true);
				Thread.currentThread().interrupt();
            }
        }
        
        Iterator<OperationIdentifier> pubIterator = getPublications().keySet().iterator();
        while (pubIterator.hasNext()) {
            for (Publication thisPub : getPublications().get(pubIterator.next())) {
                if (thisPub != null) {
                    System.out.println("SubPubController::run  Cancelling Publication - " + thisPub.getSubscriptionName());
                    thisPub.cancel();
                }
            }
            for (Publication thisPub : getPublications().get(pubIterator.next())) {
                if (thisPub != null) {
                    System.out.println("SubPubController::run  Shutting down Publication - " + thisPub.getSubscriptionName());
                    thisPub.shutdown();
                }
            }
        }                
        shutdownSignal = true;
        
        if (!executor.isShutdown()) {
            executor.shutdownNow();
        }

    }

    /**
     * Manage operations.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @throws InterruptedException the interrupted exception
     */
    private void manageOperations() throws InterruptedException {

        for (OperationSpecification thisSpec : opSpecCollection.getCopyAsList()) {
            OperationIdentifier opId = new OperationIdentifier(thisSpec.getRelatedToService(),
                    thisSpec.getRelatedToPort(), thisSpec.getOperationName(),
                    OperationIdentifier.SOURCETYPE.HANDLER);

            // Remove old publications
            if (getPublications().containsKey(opId)) {
                ArrayList<Publication> removePubList = new ArrayList();

                for (Publication thisPub : getPublications().get(opId)) {
                    if (thisPub.getState().equals(Publication.PUBLICATIONSTATE.COMPLETED)) {
                        setRelatedSubscriptionCompletion(thisPub.getOpId(), thisPub.getSubscriptionID());
                        removePubList.add(thisPub);
                    }
                }

                for (Publication removePub : removePubList) {
                    getPublications().get(opId).remove(removePub);
                    System.out.println("SubPubController::manageOperations  Removed Publication for operation " + opId);
                }
            }

            // Remove old subscriptions
            if (getSubscriptions().containsKey(opId)) {
                ArrayList<Subscription> removeSubList = new ArrayList();

                for (Subscription thisSub : getSubscriptions().get(opId)) {
                    if (thisSub.getState().equals(Subscription.SUBSCRIPTIONSTATE.COMPLETED)) {
                        removeSubList.add(thisSub);
                    }
                }

                for (Subscription removeSub : removeSubList) {
                    getSubscriptions().get(opId).remove(removeSub);
                    System.out.println("SubPubController::manageOperations  Removed Subscription for operation " + opId);
                }
            }

            if (!thisSpec.isPublicationOperation() && !thisSpec.isPublicationCallbackOperation()) {
                // Active Publications Req/Resp on external queues are handled by Publications

                // Check the External Request Queue
                if (eqc.isAvaliableInRequestQueue(opId)) {

                    NTCIP2306Message extReqMsg = eqc.getMessageFromExtRequestQueue(opId, 600);
                    if (extReqMsg != null) {
                        System.out.println("SubPubController::manageOperations - Completed check of Operation - RequestQ - " + opId.toString());
                        System.out.println("SubPubController::manageOperations  Publications arraylist size = " + getPublications().size());
                        if (definedSubscriptions.contains(opId)) {
                            processSubscriptionRequest(thisSpec, extReqMsg);
                        } else {
                            processPublicationRequest(thisSpec, extReqMsg);
                        }
                    }
                }
                // Check the External Response Queue
                if (eqc.isAvaliableInResponseQueue(opId)) {
                    NTCIP2306Message extRespMsg = eqc.getMessageFromExtResponseQueue(opId, 600);
                    if (extRespMsg != null) {
                        System.out.println("SubPubController::manageOperations - Completed check of Operation - ResponseQ - " + opId.toString());
                        if (definedSubscriptions.contains(opId)) {
                            processSubscriptionResponse(thisSpec, extRespMsg);
                        } else {
                            processPublicationResponse(thisSpec, extRespMsg);
                        }
                    }
                }

                // Check the External OperationResponse Queue
                if (eqc.isAvaliableInOperationResultsQueue(opId)) {
                    OperationResults opsResults = eqc.getResultsFromOperationResultsQueue(opId, 600);
                    if (opsResults != null) {
                        System.out.println("SubPubController::manageOperations - Completed check of Operation - OperationQ - " + opId.toString());
                        if (definedSubscriptions.contains(opId)) {
                            processSubscriptionResults(thisSpec, opsResults);
                        } else {
                            processPublicationResults(thisSpec, opsResults);
                        }
                    }
                }
            }


            if (scriptedSubscriptions.contains(opId)) {
                // Check internal queue for Operations from listeners
                opId = new OperationIdentifier(thisSpec.getRelatedToService(),
                        thisSpec.getRelatedToPort(), thisSpec.getOperationName(),
                        OperationIdentifier.SOURCETYPE.LISTENER);

                // Check the Internal Request Queue
                if (iqc.isAvaliableInRequestQueue(opId)) {

                    NTCIP2306Message extReqMsg = iqc.getMessageFromExtRequestQueue(opId, 600);
                    if (extReqMsg != null) {
                        System.out.println("SubPubController::manageOperations - Completed check of Operation InternalRequestQ" + opId.toString());
                        internalRequestUpdate(opId, extReqMsg);
                    }
                }

                // Check the Internal Response Queue
                if (iqc.isAvaliableInResponseQueue(opId)) {
                    NTCIP2306Message extRespMsg = iqc.getMessageFromExtResponseQueue(opId, 600);
                    if (extRespMsg != null) {
                        System.out.println("SubPubController::manageOperations - Completed check of Operation -InternalResponseQ" + opId.toString());
                        internalResponseUpdate(opId, extRespMsg);
                    }
                }

            }
        }

    }

    /**
     * Internal request update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param operationId the operation id
     * @param em the em
     */
    private void internalRequestUpdate(OperationIdentifier operationId, NTCIP2306Message em) {

        eqc.addToExtRequestQueue(operationId, em);
        System.out.println("SubPubController::internalRequestUpdate Moved the request message to the external request queue for operation: " + operationId);
    }

    /**
     * Internal response update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param operationId the operation id
     * @param em the em
     */
    private void internalResponseUpdate(OperationIdentifier operationId, NTCIP2306Message em) {
        eqc.addToExtResponseQueue(operationId, em);
    }

    /**
     * Shutdown.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void shutdown() {
        setShutdown(true);

        // Wait up to five(?) seconds for any current subscriptions to be cancelled.
        try {
            long startTime = System.currentTimeMillis();
            do {
                Thread.currentThread().sleep(50);
            } while (!shutdownSignal && ((System.currentTimeMillis() - startTime) < 2000));
            System.out.println("SubPubController::run  Shutdown complete after " + (System.currentTimeMillis() - startTime)+" ms.");
        } catch (InterruptedException ex) {
            ex.printStackTrace();
			Thread.currentThread().interrupt();
        }        
    }

    /**
     * Clear out any completed or cancelled subscriptions and publications from the lists.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     */
    private void clearExpiredSubPubs(){
        for (OperationSpecification thisSpec : opSpecCollection.getCopyAsList()) {
            OperationIdentifier opId = new OperationIdentifier(thisSpec.getRelatedToService(),
                    thisSpec.getRelatedToPort(), thisSpec.getOperationName(),
                    OperationIdentifier.SOURCETYPE.HANDLER);

            // Remove old publications
            if (getPublications().containsKey(opId)) {
                ArrayList<Publication> removePubList = new ArrayList();

                for (Publication thisPub : getPublications().get(opId)) {
                    if (thisPub.getState().equals(Publication.PUBLICATIONSTATE.COMPLETED)) {
                        setRelatedSubscriptionCompletion(thisPub.getOpId(), thisPub.getSubscriptionID());
                        removePubList.add(thisPub);
                    }
                }

                for (Publication removePub : removePubList) {
                    getPublications().get(opId).remove(removePub);
                    System.out.println("SubPubController::clearExpiredSubPubs  Removed Publication for operation " + opId);
                }
            }

            // Remove old subscriptions
            if (getSubscriptions().containsKey(opId)) {
                ArrayList<Subscription> removeSubList = new ArrayList();

                for (Subscription thisSub : getSubscriptions().get(opId)) {
                    if (thisSub.getState().equals(Subscription.SUBSCRIPTIONSTATE.COMPLETED)) {
                        removeSubList.add(thisSub);
                    }
                }

                for (Subscription removeSub : removeSubList) {
                    getSubscriptions().get(opId).remove(removeSub);
                    System.out.println("SubPubController::clearExpiredSubPubs  Removed Subscription for operation " + opId);
                }
            }
        }
    }
    
    /**
     * Produce c2c message subscription.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param message the message
     * @return the c2 c message subscription
     */
    private C2CMessageSubscription produceC2cMessageSubscription(NTCIP2306Message message) {
        C2CMessageSubscription subscription = null;
        if (message.isSkipEncoding())message.forceEncode();
        try {
            JAXBContext jc = JAXBContext.newInstance(C2CMessageSubscription.class.getPackage().getName());
            Unmarshaller um = jc.createUnmarshaller();
            System.out.println("SubPubController::produceC2cMessageSubscription --- length = " + message.getMessagePartContent(1).length + "\n"
                    + new String(message.getMessagePartContent(1)) + "\n Bytes: \n" + message.getMessagePartContent(1));
            ByteArrayInputStream buffer = new ByteArrayInputStream(message.getMessagePartContent(1));

            subscription = (C2CMessageSubscription) ((javax.xml.bind.JAXBElement) um.unmarshal(buffer)).getValue();


            buffer.close();

            Thread.currentThread().sleep(100);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } catch (JAXBException jaxbex) {
            jaxbex.printStackTrace();
            System.out.println("Error Unmarshalling message: " + new String(message.getMessagePartContent(1)));
        } catch (InterruptedException iex) {
            iex.printStackTrace();
            shutdown();
			Thread.currentThread().interrupt();
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Error Unmarshalling message: Exception-> " + ex.getMessage());
        }
        return subscription;
    }

    /**
     * If the messages are valid to schema and valid xml If both message parts
     * are correct message types Create a c2cSubscription object from the
     * subscription message If the subscription ID doesn't already exist send a
     * receipt message back to the requester Else update the existing
     * subscription with the c2cSubscriptionObject send a receipt message back
     * to the requester Else Create an Invalid Response Message and send back to
     * the source if a script is listening Store the operation result? Else
     * Create an Invalid Response Message and send back to the source if a
     * script is listening Store the operation result? End If.
     *
     * @param opSpec the op spec
     * @param subRequestMsg the sub request msg
     */
    public void processSubscriptionRequest(OperationSpecification opSpec, NTCIP2306Message subRequestMsg) {
        OperationIdentifier opId;
        System.out.println("SubPubController::processSubscriptionRequest Starting @"+System.currentTimeMillis() + " ms.");
        if (getCenterMode().equals(Center.RICENTERMODE.OC)) {
            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);

            if (scriptedSubscriptions.contains(opId)) {
                System.out.println("SubPubController::processSubscriptionRequest Sending request to internal Queue.");
                iqc.addToExtRequestQueue(opId, subRequestMsg);
            } else {
                try {
                    System.out.println("SubPubController::processSubscriptionRequest Verifying Subscription Request.");
                    // Perform NTCIP 2306 Level verification of the subscription request.
                    boolean verificationResult = false;
                    String verificationMessage = "";
                    try{
                        String responseText = verifySubscriptionRequest(opSpec, subRequestMsg);
                        verificationResult = true;
                    } catch (Exception ex){
                        verificationMessage = ex.getMessage();                        
                    }
                    
                    System.out.println("SubPubController::processSubscriptionRequest Verifying Completed with result = "+verificationResult + ".");

                    Message subMessage = C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), subRequestMsg);

                    // A subscription operation input message must have 2 parts - the c2cri subscription message and the information message.  We want to get information on the information message.
                    if (opSpec.getInputMessage().size() == 2) {

                        NTCIP2306ControllerResults ntcipResults = new NTCIP2306ControllerResults(opSpec.getOperationName());
                        if (subRequestMsg == null) {
                            ntcipResults.setTransportError(true);
                            ntcipResults.setTransportErrorDescription("No Request was received.");
                            ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
                        } else // Check out the Transport Results
                         if (!subRequestMsg.isTransportErrorEncountered()) {
                                ntcipResults.setTransportError(false);
                                ntcipResults.setTransportErrorDescription("");
                                ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                            } else {
                                ntcipResults.setTransportError(true);
                                ntcipResults.setTransportErrorDescription(subRequestMsg.getTransportErrorDescription());
                                ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                            }

                        ntcipResults = NTCIP2306Controller.processMessageResults(opSpec, ntcipResults, subRequestMsg, NTCIP2306Message.MESSAGETYPE.REQUEST);
                        NTCIP2306ApplicationLayerOperationResults appLayerOpResults = new NTCIP2306ApplicationLayerOperationResults(ntcipResults);

                        // The list is 0 referenced so the second part is at index 1.
                        String name = opSpec.getInputMessage().get(1).getLocalPart();
                        String nameSpace = opSpec.getInputMessage().get(1).getNamespaceURI();
                        // Send the original subscription message and expect a return of the c2c receipt message.
                        Message subMessageResponse = DefaultMessageContentGenerator.getInstance().getResponseMessage(opSpec.getOperationName(), nameSpace, name, C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), subRequestMsg), appLayerOpResults);

                        opId = new OperationIdentifier(opSpec.getRelatedToService(),
                                opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);

                        String responseName;
                        String responseNameSpace;
                        boolean errorResponse = false;
                        // If the generated message is of an expected type then return the published content.  Otherwise return null.
                        if (subMessageResponse.getMessageType().equals(opSpec.getOutputMessage().get(0).getLocalPart())) {
                            // If the subscription was valid and the response message is valid then send the response message.                            
                            responseName = opSpec.getOutputMessage().get(0).getLocalPart();
                            responseNameSpace = opSpec.getOutputMessage().get(0).getNamespaceURI();
                       } else {
                            responseName = opSpec.getFaultMessage().get(0).getLocalPart();
                            responseNameSpace = opSpec.getFaultMessage().get(0).getNamespaceURI();
                            errorResponse = true;
                        }
                        // If the response message is not reporting an error and the verification results were good then send the generated message.
                        // Or if the response message is reporting an error then send the generated message.
                        if((!errorResponse && verificationResult)||errorResponse){
                            NTCIP2306Message rcptMessage = new NTCIP2306Message(responseNameSpace, responseName, subMessageResponse.getMessageBody());
                            rcptMessage.getXmlStatus().setUTF8CharSet(true);
                            rcptMessage.getXmlStatus().setXMLv1Document(true);
                            rcptMessage.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);

                            eqc.addToExtResponseQueue(opId, rcptMessage);
                            System.out.println("SubPubController::processSubscriptionRequest Message Receipt placed back on Queue @"+System.currentTimeMillis()+" ms.");
                        // We encountered a verification error.  Create and send a new message related to this condition.
                        } else {
                            NTCIP2306Message errMessage = new NTCIP2306Message("", "", ("<errorMessage>" + verificationMessage + "</errorMessage>").getBytes());

                            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                            opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);

                            eqc.addToExtResponseQueue(opId, errMessage);
                            System.out.println("SubPubController::processSubscriptionRequest Error Message Placed on Queue.");
                        }                                                     
                        
                    } else {
                        throw new Exception("The operation specification does not describe 2 parts as required for subscription.");
                    }

                } catch (Exception ex) {
                    NTCIP2306Message errMessage = new NTCIP2306Message("", "", ("<errorMessage>" + ex.getMessage() + "</errorMessage>").getBytes());

                    opId = new OperationIdentifier(opSpec.getRelatedToService(),
                            opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);

                    eqc.addToExtResponseQueue(opId, errMessage);
                    System.out.println("SubPubController::processSubscriptionRequest Error Message Placed on Queue.");
                }
            }
        }

        System.out.println("SubPubController::processSubscriptionRequest Finishing @"+System.currentTimeMillis() + " ms.");
    }

    /**
     * Produce c2c message receipt xml.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param message the message
     * @return the byte[]
     * @throws Exception the exception
     */
    public static byte[] produceC2cMessageReceiptXML(C2CMessageReceipt message) throws Exception {
        byte[] receipt = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(C2CMessageReceipt.class.getPackage().getName());
            Marshaller mm = jc.createMarshaller();
            mm.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mm.marshal(message, buffer);
            receipt = buffer.toByteArray();
            buffer.close();

        } catch (IOException ioex) {
            ioex.printStackTrace();
            throw new Exception("Subscription:produceC2CMessageReceiptXML Error: IOException - " + ioex.getMessage(), ioex);
        } catch (JAXBException jaxbex) {
            jaxbex.printStackTrace();
            throw new Exception("Subscription:produceC2CMessageReceiptXML Error: JAXBException - " + jaxbex.getMessage(), jaxbex);
        }
        return receipt;
    }

    /**
     * Verify subscription request.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param subRequestMsg the sub request msg
     * @return the string
     * @throws Exception the exception
     */
    private String verifySubscriptionRequest(OperationSpecification opSpec, NTCIP2306Message subRequestMsg) throws Exception {
        String responseText = "";
        C2CMessageSubscription thisSub = null;
        System.out.println("SubPubController::verifySubscriptionRequest Creating Subscription.");
        try {
            thisSub = produceC2cMessageSubscription(subRequestMsg);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Error processing Subscription: " + ex.getMessage());
        }
        System.out.println("SubPubController::verifySubscriptionRequest Creating opId.");
        OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(),
                opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);

        // Make sure that the list of existing subscriptions and publications are up to date.
        clearExpiredSubPubs();
        
        System.out.println("SubPubController::verifySubscriptionRequest Looking for Subscription Match.");
        boolean subscriptionMatch = false;
		if (thisSub != null)
		{
			if (getSubscriptions().containsKey(opId)) {
				for (Subscription thisSubscription : getSubscriptions().get(opId)) {
					if (thisSubscription.getSubscriptionID().equals(thisSub.getSubscriptionID())) {
						if (!thisSubscription.getState().equals(Subscription.SUBSCRIPTIONSTATE.COMPLETED)){
							subscriptionMatch = true;                        
						}                                                                        
					}
				}
			}
		}
        System.out.println("SubPubController::verifySubscriptionRequest Checking Subscription Action.");
        if (Subscription.getSubscriptionAction(thisSub).equals(
                Subscription.SUBSCRIPTIONACTION.NEWSUBSCRIPTION)) {
            if (subscriptionMatch) {
                throw new Exception("This Subscription already exists and can not be created again.");
            } else {
                responseText = "New Subscription request was valid.";
            }
        } else if (Subscription.getSubscriptionAction(thisSub).equals(
                Subscription.SUBSCRIPTIONACTION.REPLACESUBSCRIPTION)) {

            if (!subscriptionMatch) {
                throw new Exception("No Subscription exists to match the Subscription Request.");
            } else {
                responseText = "The replacement subscription request was valid.";
            }

        } else if (Subscription.getSubscriptionAction(thisSub).equals(
                Subscription.SUBSCRIPTIONACTION.CANCELSUBSCRIPTION)) {
            if (!subscriptionMatch) {
                throw new Exception("No Subscription exists to match the Subscription Cancellation Request.");
            } else {
                responseText = "The cancel subscription request was valid.";
            }

        } else if (Subscription.getSubscriptionAction(thisSub).equals(
                Subscription.SUBSCRIPTIONACTION.CANCELLALLPRIORSUBSCRIPTIONS)) {
            if (getSubscriptions().size() > 0) {
                responseText = "The cancel subscription request was valid.";
            } else {
                throw new Exception("No Subscriptions are currently defined.");
            }

        } else if (Subscription.getSubscriptionAction(thisSub).equals(
                Subscription.SUBSCRIPTIONACTION.RESERVED)) {
            throw new Exception("The reserve Subscription Action is not valid.");
        }

        System.out.println("SubPubController::verifySubscriptionRequest Completed.");
        return responseText;
    }

    /**
     * If the messages are valid to schema and valid xml If both message parts
     * are correct message types Create a c2cSubscription object from the
     * subscription message If the subscription ID doesn't already exist send a
     * receipt message back to the requester.
     *
     * @param opSpec the op spec
     * @param subResponseMsg the sub response msg
     */
    public void processSubscriptionResponse(OperationSpecification opSpec, NTCIP2306Message subResponseMsg) {
        OperationIdentifier opId;
        if (getCenterMode().equals(Center.RICENTERMODE.EC)) {
            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);

            if (scriptedSubscriptions.contains(opId)) {
                iqc.addToExtResponseQueue(opId, subResponseMsg);
            }

        }
    }

    /**
     * Process subscription results.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param subOperationResults the sub operation results
     */
    public void processSubscriptionResults(OperationSpecification opSpec, OperationResults subOperationResults) {
        System.out.println("SubPubController::processSubscriptionResults starting @" + System.currentTimeMillis() + " ms.");
        C2CMessageSubscription thisSub = produceC2cMessageSubscription(subOperationResults.getRequestMessage());
        OperationIdentifier opId;
        if (getCenterMode().equals(Center.RICENTERMODE.OC)) {
            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
        } else {
            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
        }
        
        // Log the request and response messages if a script is not responsible for this operation.
        OperationIdentifier tmpOpID = new OperationIdentifier(opId.getServiceName(),  // Added this variable because the log message was being stored in a scripted EC Mode
                        opId.getPortName(), opId.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
        if (!scriptedSubscriptions.contains(tmpOpID)) {
            try {
                if (subOperationResults.getRequestMessage() == null) {

                    System.out.println("SubPubController::processSubscriptionResults  The request message in the results was null @" + System.currentTimeMillis());
                }
                if (subOperationResults.getResponseMessage() == null) {

                    System.out.println("SubPubController::processSubscriptionResults  The response message in the results was null @" + System.currentTimeMillis());
                }
            } catch (Exception ex1) {
                ex1.printStackTrace();
            }

        }

        OperationIdentifier pubOpId = null;
        OperationIdentifier registerPubId1 = null;
        OperationIdentifier registerPubID2 = null;
        try {
            OperationIdentifier thisOpId
                    = DefaultSubPubMapper.getRelatedPublicationOperationId(opId.getOperationName(), subOperationResults.getRequestMessage());

            pubOpId = new OperationIdentifier(thisOpId.getServiceName(),
                    thisOpId.getPortName(), thisOpId.getOperationName(),
                    OperationIdentifier.SOURCETYPE.LISTENER);
            registerPubId1 = new OperationIdentifier(thisOpId.getServiceName(),
                    thisOpId.getPortName(), thisOpId.getOperationName(),
                    OperationIdentifier.SOURCETYPE.LISTENER);
            registerPubID2 = new OperationIdentifier(thisOpId.getServiceName(),
                    thisOpId.getPortName(), thisOpId.getOperationName(),
                    OperationIdentifier.SOURCETYPE.HANDLER);
        } catch (Exception ex) {
            System.out.println("SubPubController::processSubscriptionResults No Matching Publication was found for operationId: " + opId);
            if (getCenterMode().equals(Center.RICENTERMODE.OC)) {
                if (scriptedSubscriptions.contains(opId)) {
                    iqc.addToOperationResultsQueue(opId, subOperationResults);
                }
            } else {
                OperationIdentifier tmpID = new OperationIdentifier(opId.getServiceName(),
                        opId.getPortName(), opId.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
                if (scriptedSubscriptions.contains(tmpID)) {
                    System.out.println("SubPubController::processSubscriptionResults : Operation is scripted.");
                    iqc.addToOperationResultsQueue(tmpID, subOperationResults);
                }

            }

            return;
        }

        try {
            // First check to confirm that the Subscription operation was successful
            System.out.println("SubPubController::processSubscriptionResults  About to verify operation results!!");
            subOperationResults.verifyOperationResults();
            System.out.println("SubPubController::processSubscriptionResults  Made it past verify operation results!!");
			if (thisSub == null)
				throw new NTCIP2306TransportException("Null C2CMessageSubscription");
            if (Subscription.getSubscriptionAction(thisSub).equals(
                    Subscription.SUBSCRIPTIONACTION.NEWSUBSCRIPTION)) {
                System.out.println("SubPubController::processSubscriptionResults  operation Verified about to add new subscription!!");
                Subscription newSubscription = new Subscription(produceC2cMessageSubscription(subOperationResults.getRequestMessage()));
                if (!getSubscriptions().containsKey(opId)) {
                    ArrayList<Subscription> subList = new ArrayList<Subscription>();
                    subList.add(newSubscription);
                    getSubscriptions().put(opId, subList);
                } else {

                    boolean okToAdd = true;
                    for (Subscription replacedSubscription : getSubscriptions().get(opId)) {
                        if (replacedSubscription.getSubscriptionID().equals(thisSub.getSubscriptionID())) {
                            okToAdd = false;
                        }
                    }
                    if (okToAdd) {
                        getSubscriptions().get(opId).add(newSubscription);
                    }
                }

                // Create publication ....
                System.out.println("SubPubControl::processSubscriptionResults  ExternalQueue Object = " + eqc);
                OperationSpecification pubOpSpec = opSpecCollection.get(pubOpId.getServiceName(),
                        pubOpId.getPortName(), pubOpId.getOperationName());
                Publication newPublication;
                // In case a script has claimed this operation before the publication was created.
                if (scriptedPublications.contains(registerPubID2)) {
                    newPublication = new Publication(produceC2cMessageSubscription(subOperationResults.getRequestMessage()),
                            eqc, iqc, pubOpId, pubOpSpec, getCenterMode(), true, subOperationResults.getRequestMessage(), opId);

                    System.out.println("SubPubController::processSubscriptionResults  Set Publication Connected to script value= " + newPublication.isConnectedToScript() + " for operation " + registerPubID2);
                } else {
                    newPublication = new Publication(produceC2cMessageSubscription(subOperationResults.getRequestMessage()),
                            eqc, iqc, pubOpId, pubOpSpec, getCenterMode(), false, subOperationResults.getRequestMessage(), opId);
                    System.out.println("SubPubController::processSubscriptionResults  New Publication is not on scriptedPublications list (script value= " + newPublication.isConnectedToScript() + ") for operation " + registerPubID2
                            + "\n    scriptedPublications size =" + scriptedPublications.size() + "    contains "
                            + (scriptedPublications.size() > 0 ? scriptedPublications.get(0).toString() : ""));
                }

                if (publicationMessageMap.containsKey(registerPubID2)) {
                    for (NTCIP2306Message thisMessage : publicationMessageMap.get(registerPubID2)) {
                        newPublication.addPublicationMessage(thisMessage);
                    }
                    publicationMessageMap.remove(registerPubID2);
                }

                if (!getPublications().containsKey(pubOpId)) {
                    ArrayList<Publication> pubList = new ArrayList<Publication>();
                    pubList.add(newPublication);
                    System.out.println("SubPubController::processSubscriptionResults  Publications arraylist size before = " + getPublications().size());
                    getPublications().put(registerPubId1, pubList);
                    getPublications().put(registerPubID2, pubList);
                    System.out.println("SubPubController::processSubscriptionResults  Publications arraylist size after = " + getPublications().size());
                    executor.submit(newPublication);
                } else {

                    boolean okToAdd = true;
                    for (Publication replacedPublication : getPublications().get(pubOpId)) {
                        if (replacedPublication.getSubscriptionID().equals(thisSub.getSubscriptionID())) {
                            okToAdd = false;
                        }
                    }
                    if (okToAdd) {
                        System.out.println("SubPubController::processSubscriptionResults  Publications arraylist size before = " + getPublications().size());
                        getPublications().get(registerPubId1).add(newPublication);
                        getPublications().get(registerPubID2).add(newPublication);
                        System.out.println("SubPubController::processSubscriptionResults  Publications arraylist size after = " + getPublications().size());
                        executor.submit(newPublication);
                    }
                }

            } else if (Subscription.getSubscriptionAction(thisSub).equals(
                    Subscription.SUBSCRIPTIONACTION.CANCELSUBSCRIPTION)) {
                ArrayList<Subscription> subList = getSubscriptions().get(opId);
                for (Subscription cancelledSubscription : subList) {
                    if (cancelledSubscription.getSubscriptionID().equals(thisSub.getSubscriptionID())) {
                        cancelledSubscription.cancel();
                        System.out.println("SubPubController::processSubscriptionResults  Canceled Subscription " + thisSub.getSubscriptionID() + " for operation " + opId);
                    }
                }
                for (Publication cancelledPublication : getPublications().get(pubOpId)) {
                    if (cancelledPublication.getSubscriptionID().equals(thisSub.getSubscriptionID())) {
                        cancelledPublication.cancel();
                        System.out.println("SubPubController::processSubscriptionResults  Canceled Publication " + thisSub.getSubscriptionID() + " for operation " + pubOpId);
                    }
                }

            } else if (Subscription.getSubscriptionAction(thisSub).equals(
                    Subscription.SUBSCRIPTIONACTION.CANCELLALLPRIORSUBSCRIPTIONS)) {
                ArrayList<Subscription> subList = getSubscriptions().get(opId);
                for (Subscription replacedSubscription : subList) {
                    replacedSubscription.cancel();
                }
                for (Publication cancelledPublication : getPublications().get(pubOpId)) {
                    cancelledPublication.cancel();
                }
            } else if (Subscription.getSubscriptionAction(thisSub).equals(
                    Subscription.SUBSCRIPTIONACTION.REPLACESUBSCRIPTION)) {
                if (getSubscriptions().containsKey(opId)) {
                    ArrayList<Subscription> subList = getSubscriptions().get(opId);
                    for (Subscription replacedSubscription : subList) {
                        if (replacedSubscription.getSubscriptionID().equals(thisSub.getSubscriptionID())) {
                            replacedSubscription.replace(thisSub);

                            for (Publication replacedPublication : getPublications().get(pubOpId)) {
                                if (replacedPublication.getSubscriptionID().equals(thisSub.getSubscriptionID())) {
                                    replacedPublication.update(thisSub);
                                }
                            }

                        }
                    }
                }
            }

        } catch (Exception ex) {
            System.out.println("SubPubController::processSubscriptionResults : Exception Encountered with error " + ex.getMessage());
            ex.printStackTrace();
        }

        if (getCenterMode().equals(Center.RICENTERMODE.OC)) {
            if (scriptedSubscriptions.contains(opId)) {
                iqc.addToOperationResultsQueue(opId, subOperationResults);
            }
        } else {
            OperationIdentifier tmpID = new OperationIdentifier(opId.getServiceName(),
                    opId.getPortName(), opId.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
            if (scriptedSubscriptions.contains(tmpID)) {
                System.out.println("SubPubController::processSubscriptionResults : Operation is scripted.");
                iqc.addToOperationResultsQueue(tmpID, subOperationResults);
            }

        }
        System.out.println("SubPubController::processSubscriptionResults : Exiting @" + System.currentTimeMillis() + " ms.");
    }

    /**
     * Process publication request.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param pubRequestMsg the pub request msg
     */
    public void processPublicationRequest(OperationSpecification opSpec, NTCIP2306Message pubRequestMsg) {
        OperationIdentifier opId;
        if (getCenterMode().equals(Center.RICENTERMODE.EC)) {
            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);

            if (scriptedPublications.contains(opId)) {
                iqc.addToExtRequestQueue(opId, pubRequestMsg);
            }

        }

    }

    /**
     * Process publication response.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param pubResponseMsg the pub response msg
     */
    public void processPublicationResponse(OperationSpecification opSpec, NTCIP2306Message pubResponseMsg) {
        OperationIdentifier opId;
        if (getCenterMode().equals(Center.RICENTERMODE.OC)) {
            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);

            if (scriptedPublications.contains(opId)) {
                iqc.addToExtResponseQueue(opId, pubResponseMsg);
            }

        }

    }

    /**
     * Process publication results.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param pubOperationResults the pub operation results
     */
    public void processPublicationResults(OperationSpecification opSpec, OperationResults pubOperationResults) {

        OperationIdentifier opId;
        if (getCenterMode().equals(Center.RICENTERMODE.OC)) {
            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
        } else {
            opId = new OperationIdentifier(opSpec.getRelatedToService(),
                    opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);
        }

        if (scriptedPublications.contains(opId)) {
            iqc.addToOperationResultsQueue(opId, pubOperationResults);
        }

    }

    /**
     * Load pub message.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opId the op id
     * @param messageList the message list
     */
    public void loadPubMessage(OperationIdentifier opId, ArrayList<NTCIP2306Message> messageList) {

        publicationMessageMap.put(opId, messageList);
        Iterator<OperationIdentifier> pubIterator = getPublications().keySet().iterator();
        while (pubIterator.hasNext()) {
            for (Publication thisPub : getPublications().get(pubIterator.next())) {
                if (thisPub != null) {
                    for (NTCIP2306Message thisMessage : publicationMessageMap.get(opId)) {
                        thisPub.addPublicationMessage(thisMessage);
                    }
                    // Since the messages have been loaded, then remove them from the map.
                    publicationMessageMap.remove(opId);
                }
            }
        }

    }

    public void setRelatedSubscriptionCompletion(OperationIdentifier pubOpId, String subscriptionId) {
        try {
            OperationIdentifier subOpId = DefaultSubPubMapper.getRelatedSubscriptionOperationId(pubOpId.getOperationName());
            for (Subscription thisSub : subscriptions.get(subOpId)) {
                if (thisSub.getSubscriptionID().equals(subscriptionId)) {
                    thisSub.setState(Subscription.SUBSCRIPTIONSTATE.COMPLETED);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    /**
     * Gets the subscription status.
     *
     * @param opId the op id
     * @return the subscription status
     */
    public Subscription.SUBSCRIPTIONSTATE getSubscriptionStatus(OperationIdentifier opId) {
        Subscription.SUBSCRIPTIONSTATE subState = Subscription.SUBSCRIPTIONSTATE.ERROR;

        if (getSubscriptions().containsKey(opId)) {
            ArrayList<Subscription> subscriptionsList = getSubscriptions().get(opId);
            for (Subscription thisSubscription : subscriptionsList) {
                subState = thisSubscription.getState();
            }
        }

        return subState;
    }

    /**
     * Gets the publication status.
     *
     * @param opId the op id
     * @return the publication status
     */
    public Publication.PUBLICATIONSTATE getPublicationStatus(OperationIdentifier opId) {
        Publication.PUBLICATIONSTATE pubState = Publication.PUBLICATIONSTATE.COMPLETED;

        if (getPublications().containsKey(opId)) {
            ArrayList<Publication> publicationsList = getPublications().get(opId);
            for (Publication thisPublication : publicationsList) {
                pubState = thisPublication.getState();
            }
        }

        return pubState;
    }

    /**
     * Gets the publication count.
     *
     * @param opId the op id
     * @return the publication count
     */
    public long getPublicationCount(OperationIdentifier opId) {
        long pubCount = 0;

        if (getPublications().containsKey(opId)) {
            ArrayList<Publication> publicationsList = getPublications().get(opId);
            for (Publication thisPublication : publicationsList) {
                pubCount = thisPublication.getPublicationCount();
            }
        }

        return pubCount;
    }

    /**
     * Gets the publication frequency.
     * 
     * @param opId the op id
     * @return the publication frequency if it is periodic otherwise -1.
     */
    public long getPublicationFrequency(OperationIdentifier opId) {
        long pubFrequency = -1;

        if (getPublications().containsKey(opId)) {
            ArrayList<Publication> publicationsList = getPublications().get(opId);
            for (Publication thisPublication : publicationsList) {
                pubFrequency = thisPublication.getPublicationFrequency();
            }
        }

        return pubFrequency;
    }
    
    
    /**
     * Gets the millis since last periodic publication.
     *
     * @param opId the op id
     * @return the millis since last periodic publication
     */
    public long getMillisSinceLastPeriodicPublication(OperationIdentifier opId) {
        long pubDelta = -1;

        if (getPublications().containsKey(opId)) {
            ArrayList<Publication> publicationsList = getPublications().get(opId);
            for (Publication thisPublication : publicationsList) {
                pubDelta = thisPublication.getMillisSinceLastPeriodicPublication();
            }
        }

        return pubDelta;
    }

    /**
     * Checks if is shutdown.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is shutdown
     */
    private boolean isShutdown() {
        return shutdown;
    }

    /**
     * Sets the shutdown.
     *
     * @param shutdown the new shutdown
     */
    private void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

    /**
     * Gets the center mode.
     *
     * @return the center mode
     */
    private RICENTERMODE getCenterMode() {
        return centerMode;
    }

    /**
     * Gets the default sub pub map.
     *
     * @return the default sub pub map
     */
    public ConcurrentHashMap<OperationIdentifier, OperationIdentifier> getDefaultSubPubMap() {
        return defaultSubPubMap;
    }

    /**
     * Update default sub pub mapping.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param subPubMap the sub pub map
     */
    public void updateDefaultSubPubMapping(HashMap<OperationIdentifier, OperationIdentifier> subPubMap) {
        this.defaultSubPubMap.putAll(subPubMap);
    }

    /**
     * Gets the subscriptions.
     *
     * @return the subscriptions
     */
    private synchronized ConcurrentHashMap<OperationIdentifier, ArrayList<Subscription>> getSubscriptions() {
        return subscriptions;
    }

    /**
     * Gets the publications.
     *
     * @return the publications
     */
    private synchronized ConcurrentHashMap<OperationIdentifier, ArrayList<Publication>> getPublications() {
        return publications;
    }

    /**
     * Sets the script control for subscription.
     *
     * @param opId the new script control for subscription
     */
    public void setScriptControlForSubscription(OperationIdentifier opId) {
        if (!scriptedSubscriptions.contains(opId)) {
            scriptedSubscriptions.add(opId);
        }
        System.out.println("SubPubController::setScriptControlForSubscription Listener is subscribed to operation " + opId);
    }

    /**
     * Clear script control for subscription.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opId the op id
     */
    public void clearScriptControlForSubscription(OperationIdentifier opId) {
        if (scriptedSubscriptions.contains(opId)) {
            scriptedSubscriptions.remove(opId);
        }
        System.out.println("SubPubController::clearScriptControlForSubscription Listener removed subscription to operation " + opId);
    }

    /**
     * Sets the script control for publication.
     *
     * @param opId the new script control for publication
     */
    public void setScriptControlForPublication(OperationIdentifier opId) {
        if (!scriptedPublications.contains(opId)) {
            scriptedPublications.add(opId);
        }
        if (getPublications().containsKey(opId)) {
            System.out.println("SubPubController::setScriptControlForPublication Listener is subscribed to operation " + opId);
            for (Publication thisPub : getPublications().get(opId)) {
                thisPub.setConnectedToScript(true);
            }
        } else {
            System.out.println("SubPubController::setScriptControlForPublication No publication is currently defined for operation " + opId + "  Size:" + getPublications().size());
        }
    }

    /**
     * Clear script control for publication.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opId the op id
     */
    public void clearScriptControlForPublication(OperationIdentifier opId) {
        if (scriptedPublications.contains(opId)) {
            scriptedPublications.remove(opId);
        }
        if (getPublications().containsKey(opId)) {
            for (Publication thisPub : getPublications().get(opId)) {
                thisPub.setConnectedToScript(false);
                System.out.println("SubPubController::clearScriptControlForPublication Listener removed subscription to operation " + opId);
            }
        } else {
            System.out.println("SubPubController::clearScriptControlForPublication No publication is currently defined for operation " + opId);
        }
    }
}
