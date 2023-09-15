/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.subpub;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.applayer.MessageUpdateListener;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ApplicationLayerOperationResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Controller;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.C2CMessagePublication;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.C2CMessageReceipt;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.C2CMessageSubscription;
import org.fhwa.c2cri.ntcip2306v109.c2cadmin.ObjectFactory;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center.RICENTERMODE;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.messaging.DefaultMessageContentGenerator;
import org.fhwa.c2cri.ntcip2306v109.status.NTCIP2306Status;
import org.fhwa.c2cri.ntcip2306v109.subpub.Subscription.SUBSCRIPTIONTYPE;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class Publication manages the operation of a subscription related to
 one-time, periodic or on-change subscriptions for both EC and OC modes. It
 also handles subscription cancellation events.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class Publication implements Runnable, MessageUpdateListener {

    /**
     * The contents.
     */
    private C2CMessagePublication contents;

    /**
     * The Enum PUBLICATIONSTATE.
     */
    public static enum PUBLICATIONSTATE {

        /**
         * The initializing.
         */
        INITIALIZING, /**
         * The active.
         */
        ACTIVE, /**
         * The updating.
         */
        UPDATING, /**
         * The completed.
         */
        COMPLETED
    };

    /**
     * The max publications.
     */
    private final long maxPublications = 4294967295L;

    /**
     * The state.
     */
    private PUBLICATIONSTATE state = PUBLICATIONSTATE.INITIALIZING;

    /**
     * The center mode.
     */
    private Center.RICENTERMODE centerMode;

    /**
     * The subscription id.
     */
    private String subscriptionID;

    /**
     * The subscription name.
     */
    private String subscriptionName;

    /**
     * The subscription frequency.
     */
    private long subscriptionFrequency;

    /**
     * The start time.
     */
    private Date startTime;

    /**
     * The end time.
     */
    private Date endTime;

    /**
     * The subscription type.
     */
    private Subscription.SUBSCRIPTIONTYPE subscriptionType;

    /**
     * The return address.
     */
    private String returnAddress;

    /**
     * The eqc.
     */
    private final QueueController eqc;

    /**
     * The iqc.
     */
    private final QueueController iqc;
//    private Subscription parentSubscription;
//    private C2CMessageSubscription relatedSubscription;
    /**
     * The subscription count.
     */
    private long publicationCount = 0;

    /**
     * The publications transmitted.
     */
    private long publicationsTransmitted = 0;
    
    /** The flag related to the publicationsTransmitted variable */
    private final Object publicationsTransmittedLock = new Object();

//    private OperationSpecification opSpec;
    /**
     * The op id.
     */
    private OperationIdentifier opId;

    /**
     * The op spec.
     */
    private OperationSpecification opSpec;

    /**
     * The subscription error.
     */
    private String publicationError = "";

    /**
     * The subscription error count.
     */
    private long publicationErrorCount = 0;

    /**
     * The c2c object factory.
     */
    ObjectFactory c2cObjectFactory = new ObjectFactory();

    /**
     * The connected to script.
     */
    private boolean connectedToScript = false;

    /**
     * Connected to Script Lock Object
     */
    private final Object connectedToScriptLock = new Object();

    /**
     * The on change update.
     */
    private boolean onChangeUpdate = false;

    /**
     * The shutdown.
     */
    private volatile boolean shutdown = false;

    /**
     * The subscription messages.
     */
    private final ArrayList<NTCIP2306Message> publicationMessages = new ArrayList<NTCIP2306Message>();

    /**
     * The subscription message index.
     */
    private int publicationMessageIndex = 0;

    /**
     * The millis since last subscription.
     */
    private Long millisSinceLastPublication = 0L;

    /**
     * The last subscription time in millis.
     */
    private volatile long lastPublicationTimeInMillis = 0;

    /**
     * The subscription message.
     */
    private final NTCIP2306Message subscriptionMessage;

    /**
     * The related subscription op id.
     */
    private OperationIdentifier subscriptionOpId;
 
    private Logger log = Logger.getLogger(Publication.class.getName());
    
    /**
     *  The periodic publish message Thread
     */
    private Thread periodicPubMessageThread;
    
    /**
     *  Thread for processing publication and response and results
     */
    private Thread nonScriptedPubResponseThread;
	
	private final Object LOCK = new Object();
    
    
    /**
     * Instantiates a new subscription.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param relatedSubscription the related subscription
     * @param eqc the eqc
     * @param iqc the iqc
     * @param opId the op id
     * @param opSpec the op spec
     * @param centerMode the center mode
     * @param connectedToScript the connected to script
     * @param subscriptionMessage the original subscription message
     * @param subOpId the original subscription operation id
     */
    public Publication(C2CMessageSubscription relatedSubscription,
            QueueController eqc,
            QueueController iqc,
            OperationIdentifier opId,
            OperationSpecification opSpec,
            Center.RICENTERMODE centerMode,
            boolean connectedToScript,
            NTCIP2306Message subscriptionMessage,
            OperationIdentifier subOpId) {
        setState(PUBLICATIONSTATE.INITIALIZING);
        System.err.println("Publication::  Center Mode = " + centerMode);
        System.err.println("Publication::  ExternalQueue Object = " + eqc);
        this.subscriptionType = Subscription.getSubscriptionType(relatedSubscription);
        this.subscriptionID = relatedSubscription.getSubscriptionID();
        this.subscriptionName = relatedSubscription.getSubscriptionName();
        this.startTime = Subscription.getSubscriptionStartTime(relatedSubscription);
        this.endTime = Subscription.getSubscriptionEndTime(relatedSubscription);
        this.subscriptionFrequency = relatedSubscription.getSubscriptionFrequency();
        this.returnAddress = relatedSubscription.getReturnAddress();
        this.eqc = eqc;
        System.err.println("Publication::  ExternalQueue Object = " + this.eqc);
        this.iqc = iqc;
        this.opId = opId;
        this.opSpec = opSpec;
        this.centerMode = centerMode;
        this.connectedToScript = connectedToScript;
        this.subscriptionMessage = subscriptionMessage;
        this.subscriptionOpId = subOpId;
        System.err.println("Publication::  ConnectedToScript = " + this.connectedToScript);
    }

    /**
     * Loop while the subscription is in the initializing state

 Loop while the subscription is not in the completed state If the
 subscription is not being updated Perform publications per the
 specifications of the subscription.
     */
    public void run() {

        long publicationStartTimeInMillis = System.currentTimeMillis();
        Date lastPeriodicUpdateTime = null;
        setState(PUBLICATIONSTATE.ACTIVE);
        System.out.println("Publication:: About to register for Message Updates related to operation "+opId.getOperationName()+" @"+System.currentTimeMillis() + ".");
        try {
            DefaultMessageContentGenerator.getInstance().registerMessageUpdateListener(this, opId.getOperationName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        while (!getState().equals(PUBLICATIONSTATE.COMPLETED) && (!shutdown)) {
            boolean bComplete = false;
			if (!getState().equals(PUBLICATIONSTATE.UPDATING)) {
                // Check to see if it's time to publish
                // if so then publish

                if (isWithinSubscriptionPeriod()) {
                    Date currentTime = Calendar.getInstance().getTime();

                    // Be sure at least 2 seconds have elapsed before processing the subscription.  This is to 
                    // give a potential script the ability to take control over this subscription after a subscription.
                    if (!isConnectedToScript() && (System.currentTimeMillis() - publicationStartTimeInMillis) < 2000) {
                        try {
                            // Wait 1 second.
                            Thread.sleep(1000);
                        } catch (InterruptedException iex) {
                            iex.printStackTrace();
                            setState(PUBLICATIONSTATE.COMPLETED);
                            break;
                        }
                    

                    } else if (getSubscriptionType().equals(SUBSCRIPTIONTYPE.PERIODIC)) {
                        boolean publishNow = false;
                        System.out.println("Publication::run  Periodic update - Not Conneccted To Script @ " + System.currentTimeMillis());

                        if (lastPeriodicUpdateTime == null) {
                            publishNow = true;
                        } else if ((currentTime.getTime() - lastPeriodicUpdateTime.getTime()) >= (getSubscriptionFrequency() * 1000)) {
                            publishNow = true;
                        }

                        if (publishNow) {
                            if (this.centerMode.equals(Center.RICENTERMODE.OC)) {
                                try {
                                    NTCIP2306Message thisPublication = null;
                                    if (!isConnectedToScript()) {
                                        System.out.println("Publication::run  Periodic update - Not Conneccted To Script @ " + System.currentTimeMillis());
                                        thisPublication = generatePublicationMsg();
                                        if (thisPublication != null) {
                                            thisPublication.setPublicationURL(getReturnAddress());
                                            thisPublication.setPublicationSoapAction(getSubscriptionID());
                                            thisPublication.setPublicationMessage(true);
                                        }
                                    } else {
                                        System.out.println("Publication::run  Periodic update - Conneccted To Script - checking iqc Request Queue @ " + System.currentTimeMillis());
                                        OperationIdentifier pubId = new OperationIdentifier(getOpId().getServiceName(),
                                                getOpId().getPortName(), getOpId().getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
                                        thisPublication = iqc.getMessageFromExtRequestQueue(pubId, 500);
                                        if (thisPublication != null) {
                                            thisPublication.setPublicationURL(getReturnAddress());
                                            thisPublication.setPublicationSoapAction(getSubscriptionID());
                                            thisPublication.setPublicationMessage(true);
                                            incrementPublicationCount();
                                        }
                                    }
                                    if (thisPublication != null) {
                                        System.out.println("Publication::run  Publication message exists for periodic update @ " + System.currentTimeMillis());
                                        publishMessageOC(getOpId(), thisPublication);
                                        computeMillisSinceLastPublication(System.currentTimeMillis());
                                        lastPeriodicUpdateTime = currentTime;
                                    } else {
                                        System.out.println("Publication::run  No publication message available for periodic update @ " + System.currentTimeMillis());
                                    }
                                } catch (InterruptedException ex) {
                                    setState(PUBLICATIONSTATE.COMPLETED);
                                    break;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    publicationErrorCount++;
                                    publicationError = ex.getMessage();
                                } finally {
                                    try {
                                        Thread.currentThread().sleep(1000);
                                    } catch (InterruptedException iex) {
                                        iex.printStackTrace();
                                        setState(PUBLICATIONSTATE.COMPLETED);
                                        bComplete = true;
                                    }
                                }
                            } else {
                                try {
                                    publishMessageEC(getOpId());
                                    lastPeriodicUpdateTime = currentTime;
                                } catch (InterruptedException iex) {
                                    iex.printStackTrace();
                                    setState(PUBLICATIONSTATE.COMPLETED);
                                    break;
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    publicationErrorCount++;
                                    publicationError = ex.getMessage();
                                } finally {
                                    try {
                                        Thread.currentThread().sleep(1000);
                                    } catch (InterruptedException iex) {
                                        iex.printStackTrace();
                                        setState(PUBLICATIONSTATE.COMPLETED);
                                        bComplete = true;
                                    }
                                }

                            }
                        }

                    } else if (getSubscriptionType().equals(SUBSCRIPTIONTYPE.ONETIME)) {
                        if (this.centerMode.equals(Center.RICENTERMODE.OC)) {
                            try {
                                NTCIP2306Message thisPublication = null;
                                if (!isConnectedToScript()) {
                                    thisPublication = generatePublicationMsg();
                                    if (thisPublication != null) {
                                        System.out.println("Publication::run Start Publication for OC Not Scripted @" + System.currentTimeMillis());
                                        thisPublication.setPublicationURL(getReturnAddress());
                                        thisPublication.setPublicationSoapAction(getSubscriptionID());
                                        thisPublication.setPublicationMessage(true);

                                        publishMessageOC(getOpId(), thisPublication);
                                        setState(PUBLICATIONSTATE.COMPLETED);
                                        try {
                                            Thread.currentThread().sleep(2000);
                                        } catch (InterruptedException iex) {
                                            iex.printStackTrace();
                                        }//                                } else {
                                        System.out.println("Publication::run PublicationCompleted for OC Not Scripted @" + System.currentTimeMillis());
                                        break;
                                    } else {
                                        System.out.println("Publication::run No publication message available @" + System.currentTimeMillis());
                                    }
                                } else {
                                    OperationIdentifier pubId = new OperationIdentifier(getOpId().getServiceName(),
                                            getOpId().getPortName(), getOpId().getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
                                    System.out.println("Publication::run OC Scripted -- Checking for publication on (iqc=" + iqc + ") \n pubId=" + pubId + "\n@" + System.currentTimeMillis());
                                    thisPublication = iqc.getMessageFromExtRequestQueue(pubId, 500);
                                    if (thisPublication != null) {
                                        thisPublication.setPublicationURL(getReturnAddress());
                                        thisPublication.setPublicationSoapAction(getSubscriptionID());
                                        thisPublication.setPublicationMessage(true);
                                        incrementPublicationCount();
                                        System.out.println("Publication::run Start Publication for OC Scripted @" + System.currentTimeMillis());
                                        publishMessageOC(getOpId(), thisPublication);
                                        setState(PUBLICATIONSTATE.COMPLETED);
                                                    /** Give the controlling script a little time to pickup the publication status before exiting */
                                        try {
                                            Thread.currentThread().sleep(2000);
                                        } catch (InterruptedException iex) {
                                            iex.printStackTrace();
                                        }//                                } else {
                                        System.out.println("Publication::run PublicationCompleted for OC Scripted  @" + System.currentTimeMillis());
                                        break;
                                    }
                                }
                            } catch (InterruptedException ex) {
                                setState(PUBLICATIONSTATE.COMPLETED);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                publicationErrorCount++;
                                publicationError = ex.getMessage();
                                setState(PUBLICATIONSTATE.COMPLETED);
                                System.out.println("Publication::run PublicationCompleted  for Exception @" + System.currentTimeMillis());
                                break;
                            }
                        } else {
                            try {
                                System.out.println("Publication::run Start Publication for EC @" + System.currentTimeMillis());
                                publishMessageEC(getOpId());
                                setState(PUBLICATIONSTATE.COMPLETED);
                                System.out.println("Publication::run PublicationCompleted EC @" + System.currentTimeMillis());
                                break;
                            } catch (InterruptedException ex) {
                                setState(PUBLICATIONSTATE.COMPLETED);
                                break;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                publicationErrorCount++;
                                publicationError = ex.getMessage();
                            }

                        }

                    } else if (getSubscriptionType().equals((SUBSCRIPTIONTYPE.ONCHANGE))) {
                        if (this.centerMode.equals(Center.RICENTERMODE.OC)) {
                            try {
                                NTCIP2306Message thisPublication = null;
                                if (isOnChangeUpdate() && (!isConnectedToScript())) {
                                    thisPublication = generatePublicationMsg();
                                    setOnChangeUpdate(false);
                                    if (thisPublication != null) {
                                        thisPublication.setPublicationURL(getReturnAddress());
                                        thisPublication.setPublicationSoapAction(getSubscriptionID());
                                        thisPublication.setPublicationMessage(true);
                                        publishMessageOC(getOpId(), thisPublication);
                                    } else {
                                        System.out.println("Publication:run No publication message available @ " + System.currentTimeMillis());
                                    }
                                } else {
                                    OperationIdentifier pubId = new OperationIdentifier(getOpId().getServiceName(),
                                            getOpId().getPortName(), getOpId().getOperationName(), OperationIdentifier.SOURCETYPE.LISTENER);
                                    thisPublication = iqc.getMessageFromExtRequestQueue(pubId, 500);
                                    if (thisPublication != null) {
                                        thisPublication.setPublicationURL(getReturnAddress());
                                        thisPublication.setPublicationSoapAction(getSubscriptionID());
                                        thisPublication.setPublicationMessage(true);
                                        incrementPublicationCount();
                                        publishMessageOC(getOpId(), thisPublication);

                                    }
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                publicationErrorCount++;
                                publicationError = ex.getMessage();
                                setState(PUBLICATIONSTATE.COMPLETED);
                                break;
                            }
                        } else {
                            try {
                                publishMessageEC(getOpId());
                            } catch (InterruptedException ex) {
                                setState(PUBLICATIONSTATE.COMPLETED);
                                break;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                publicationErrorCount++;
                                publicationError = ex.getMessage();

                            }

                        }
                    }
                } else if (isSubscriptionExpired()) {
                    setState(PUBLICATIONSTATE.COMPLETED);
                    break;
                }
				if (bComplete)
					break;
            }
            try {
                Thread.currentThread().sleep(500);
            } catch (InterruptedException iex) {
                iex.printStackTrace();
                setState(PUBLICATIONSTATE.COMPLETED);
                break;
            }
        }
        
        System.out.println("Publication:: About to unregister for Message Updates related to operation "+opId.getOperationName()+" @"+System.currentTimeMillis() + ".");
        try {
            DefaultMessageContentGenerator.getInstance().unregisterMessageUpdateListener(this);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // If we have a periodic publication thread running we need to stop it.
        if (this.periodicPubMessageThread != null) {
            this.periodicPubMessageThread.interrupt();
        }
        
        if (this.nonScriptedPubResponseThread != null){
            this.nonScriptedPubResponseThread.interrupt();
        }
    }

    /**
     * Publish message oc.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param pubOpId the pub op id
     * @param publishMessage the publish message
     * @throws Exception the exception
     */
    private void publishMessageOC(final OperationIdentifier pubOpId, NTCIP2306Message publishMessage) throws Exception {
        publishMessage.setPublicationURL(getReturnAddress());


        if (isConnectedToScript()) {
            if (this.nonScriptedPubResponseThread != null) {
                // Stop the nonScripted PubResponse Thread so that it does not interfere with scripted operations.
                this.nonScriptedPubResponseThread.interrupt();
            }
        }

        eqc.addToExtRequestQueue(pubOpId, publishMessage);
        System.err.println("Publication::publishMessageOC Just added a message to the queue in case anyone cares...\n" + pubOpId + " @" + System.currentTimeMillis());
        if (isConnectedToScript()) {
            OperationIdentifier respOpID = new OperationIdentifier(
                    pubOpId.getServiceName(), pubOpId.getPortName(), pubOpId.getOperationName(),
                    OperationIdentifier.SOURCETYPE.HANDLER);
            int responseTimeOut = 45000;
            try {
                responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            NTCIP2306Message response = eqc.getMessageFromExtResponseQueueWithInterrupt(respOpID, responseTimeOut);
            System.err.println("Publication::publishMessageOC Just checked for a response in case anyone cares...\n" + respOpID + "\n Response wasnull? = " + (response == null ? "Yes" : new String(response.getMessagePartContent(1))) + " \n@" + System.currentTimeMillis());
            if (response == null) {
                response = new NTCIP2306Message("None", "None", "<Error>The External Center did not provide the response as expected.</Error>".getBytes());
                response.setTransportErrorEncountered(true);
            }
            iqc.addToExtResponseQueue(respOpID, response);
            synchronized (publicationsTransmittedLock) {
                publicationsTransmitted++;
                System.out.println("Publication::publishMessageOC:  incremented publications count to " + publicationsTransmitted);
            }
            System.out.println("Publication::publishMessageOC Added response Message to Q. @" + System.currentTimeMillis());

        } else {

            System.out.println("Publication::publishMessageOC The publication was determined to be unscripted. @" + System.currentTimeMillis());
            if (this.nonScriptedPubResponseThread == null || !this.nonScriptedPubResponseThread.isAlive()) {
                // Create a new thread to create periodic publication messages.
                this.nonScriptedPubResponseThread = new Thread() {

                    @Override
                    public void run() {
                        OperationIdentifier respOpID = new OperationIdentifier(
                                pubOpId.getServiceName(), pubOpId.getPortName(), pubOpId.getOperationName(),
                                OperationIdentifier.SOURCETYPE.HANDLER);
                        while (!this.isInterrupted()) {
                            try {

                                    // remove the results from the results queue.  Give it a short timeout assuming that the response message was actually recieved.
                                    System.out.println("Publication::publishMessageOC About to checked for results in case anyone cares...\n" + respOpID + " responseQueuePopulated? = "+eqc.isAvaliableInResponseQueue(respOpID)+ " resultseQueuePopulated? = "+eqc.isAvaliableInOperationResultsQueue(respOpID)+" QueueId = "+eqc.toString()+" \n@" + System.currentTimeMillis());
                                    OperationResults results = eqc.getResultsFromOperationResultsQueueWithInterrupt(respOpID, 30000);
                                    System.out.println("Publication::publishMessageOC Just checked for results response in case anyone cares...\n" + respOpID);

/**                                 Log messages for unscripted publications should be handled here. */
                                    if (results != null) {

                                        if (results.getRequestMessage()== null){

                                            log.debug("Publication::publishMessageOC  The request message in the results was null @" + System.currentTimeMillis());
                                            System.err.println("Publication::publishMessageOC  The request message in the results was null @" + System.currentTimeMillis());                                            
                                        }
                                        if (results.getResponseMessage() == null) {

                                            log.debug("Publication::publishMessageOC  The response message in the results was null @" + System.currentTimeMillis());                                            
                                            System.err.println("Publication::publishMessageOC  The response message in the results was null @" + System.currentTimeMillis());                                            
                                        }
                                        log.debug("Publication::publishMessageOC Exiting the nonScriptedPubResponseThread after logging results.");
                                        System.err.println("Publication::publishMessageOC Exiting the nonScriptedPubResponseThread after logging results.");
                                        break;
                                    }                                    
                                    
                                    NTCIP2306Message response = eqc.getMessageFromExtResponseQueue(respOpID, 30000);
                                    log.debug("Publication::publishMessageOC Just checked for a response in case anyone cares...\n" + respOpID + " responseQueuePopulated? = "+eqc.isAvaliableInResponseQueue(respOpID)+ " resultseQueuePopulated? = "+eqc.isAvaliableInOperationResultsQueue(respOpID)+" \n@" + System.currentTimeMillis());
                                    System.err.println("Publication::publishMessageOC Just checked for a response in case anyone cares...\n" + respOpID + " responseQueuePopulated? = "+eqc.isAvaliableInResponseQueue(respOpID)+ " resultseQueuePopulated? = "+eqc.isAvaliableInOperationResultsQueue(respOpID)+" \n@" + System.currentTimeMillis());


                            } catch (InterruptedException ex) {
                                log.debug("Publication::publishMessageOC Exiting the nonScriptedPubResponseThread after Thread Interrupt.");
                                System.err.println("Publication::publishMessageOC Exiting the nonScriptedPubResponseThread after Thread Interrupt.");
                                break;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                        System.out.println("Exiting PeriodicPubMessageThread!!");

                    }
                };
                this.nonScriptedPubResponseThread.start();
            }
        }
    }

    /**
     * Publish message ec.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param pubOpId the pub op id
     * @throws Exception the exception
     */
    private void publishMessageEC(OperationIdentifier pubOpId) throws Exception {
        OperationIdentifier reqOpID = new OperationIdentifier(
                pubOpId.getServiceName(), pubOpId.getPortName(), pubOpId.getOperationName(),
                OperationIdentifier.SOURCETYPE.HANDLER);
        NTCIP2306Message request = eqc.getMessageFromExtRequestQueueWithInterruptAndWait(reqOpID, 12000,!isConnectedToScript());
        System.err.println("Publication::publishMessageEC  request Message Receieved is null? " + (request == null ? "Yes" : "No") + "  ConnectedToScript? " + (isConnectedToScript() ? "Yes" : "No") + " \n@" + System.currentTimeMillis());
        if (request == null) {
            throw new Exception("No Publication Request was received.");
        }
        long start = System.currentTimeMillis();
        incrementPublicationCount();
        computeMillisSinceLastPublication(System.currentTimeMillis());
        
        if (!isConnectedToScript()) {

            System.err.println("Publication::publishMessageEC  I'm about to put a message on the response queue. \n pubOpId = " + pubOpId + "\n respOpID=" + reqOpID + "\n@" + System.currentTimeMillis() + "\n");
            eqc.addToExtResponseQueue(pubOpId, generatePublicationResponseMsg(pubOpId, request));
            OperationResults results = eqc.getResultsFromOperationResultsQueueWithInterrupt(pubOpId, 4000);
        } else {

            iqc.addToExtRequestQueue(reqOpID, request);
            System.err.println("Publication::publishMessageEC  I put a message on the internal response queue. \n pubOpId = " + pubOpId + "\n respOpID=" + reqOpID + " \n@" + System.currentTimeMillis() + "\n");
            OperationIdentifier listenerRespOpID = new OperationIdentifier(
                    pubOpId.getServiceName(), pubOpId.getPortName(), pubOpId.getOperationName(),
                    OperationIdentifier.SOURCETYPE.LISTENER);
            int responseTimeOut = 60000;
            try {
                responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_RESPONSE_COMMAND_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_RESPONSE_COMMAND_MAXWAITTIME_DEFAULT_VALUE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            NTCIP2306Message response = iqc.getMessageFromExtResponseQueueWithInterrupt(listenerRespOpID, responseTimeOut);
            if (response == null) {
                response = new NTCIP2306Message("None", "None", "<Error>The Script did not provide the response as expected.</Error>".getBytes());
            }
            eqc.addToExtResponseQueue(listenerRespOpID, response);
            long finish = System.currentTimeMillis();
            System.err.println("Publication::publishMessageEC  Added a response message on the external response queue. \n pubOpId = " + pubOpId + "\n respOpID=" + reqOpID + "\n @: " + finish + "\nDuration = " + (finish - start));
            OperationResults results = eqc.getResultsFromOperationResultsQueueWithInterrupt(reqOpID, 10000);
            synchronized (publicationsTransmittedLock){
                publicationsTransmitted++;
                System.out.println("Publication::publishMessageEC:  incremented publications count to "+publicationsTransmitted);
            }
            if (results != null) {
                iqc.addToOperationResultsQueue(listenerRespOpID, results);
            } else {
                System.err.println("Publication::publishMessageEC  No operation results were returned before timeout. ");
            }
        }
    }

    
    /**
     * Publish message oc.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param subOpId the pub op id
     * @param subscriptionMessage the publish message
     * @throws Exception the exception
     */
    private void cancelPublicationEC(OperationIdentifier subOpId, NTCIP2306Message subscription) throws Exception {

        try {
            String subMessage = new String(subscriptionMessage.getMessagePartContent(1), "UTF-8");
            subMessage = subMessage.replace("newSubscription", "cancelSubscription").replace("<subscriptionAction-item>1</subscriptionAction-item>", "<subscriptionAction-item>3</subscriptionAction-item>");
            byte[] updatedSubMessage = subMessage.getBytes("UTF-8");

            NTCIP2306Message cancelSubscriptionMessage = new NTCIP2306Message("http://www.ntcip.org/c2c-message-administration", "c2cMessageSubscription", updatedSubMessage);
            cancelSubscriptionMessage.addMessagePart(subscription.getMessagePartNameSpace(2), subscription.getMessagePartName(2), subscription.getMessagePartContent(2));
            cancelSubscriptionMessage.getXmlStatus().setUTF8CharSet(true);
            cancelSubscriptionMessage.getXmlStatus().setXMLv1Document(true);
            cancelSubscriptionMessage.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
            cancelSubscriptionMessage.setPublicationMessage(false);
            

            eqc.addToExtRequestQueue(subOpId, cancelSubscriptionMessage);
            System.err.println("Publication::cancelPublicationEC Just added a cancel subscription message to the queue in case anyone cares...\n" + subOpId + " @" + System.currentTimeMillis());
            OperationIdentifier respOpID = new OperationIdentifier(
                    subOpId.getServiceName(), subOpId.getPortName(), subOpId.getOperationName(),
                    OperationIdentifier.SOURCETYPE.HANDLER);
            int responseTimeOut = 45000;
            try {
                responseTimeOut = RIParameters.getInstance().getParameterValueAsInteger(NTCIP2306Settings.HTTP_CLIENT_SETTINGS_GROUP, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER, NTCIP2306Settings.HTTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            NTCIP2306Message response = eqc.getMessageFromExtResponseQueueWithInterrupt(respOpID, responseTimeOut);
            System.err.println("Publication::cancelPublicationEC Just checked for a response in case anyone cares...\n" + respOpID + "\n Response wasnull? = " + (response == null ? "Yes" : new String(response.getMessagePartContent(1))) + " \n@" + System.currentTimeMillis());
            System.err.println("Publication::cancelPublicationEC Just checked for results response in case anyone cares...\n" + respOpID);
            if (isConnectedToScript()) {
                if (response == null) {
                    response = new NTCIP2306Message("None", "None", "<Error>The External Center did not provide the response as expected.</Error>".getBytes());
                    response.setTransportErrorEncountered(true);
                }
                iqc.addToExtResponseQueue(respOpID, response);
                synchronized (publicationsTransmittedLock) {
                    publicationsTransmitted++;
                    System.out.println("Publication::publishMessageOC:  incremented publications count to " + publicationsTransmitted);
                }
                System.out.println("Publication::publishMessageOC Added response Message to Q. @" + System.currentTimeMillis());
            } else {
                eqc.getResultsFromOperationResultsQueueWithInterrupt(respOpID, 500);
                System.out.println("Publication::cancelPublicationEC The publication was determined to be unscripted. @" + System.currentTimeMillis());
            }

        } catch (IOException ioex) {
            ioex.printStackTrace();
        }

    }
    
    /**
     * Shutdown.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void shutdown() {
        this.shutdown = true;
    }

    /**
     * Update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param message the message
     */
    public void update(C2CMessageSubscription message) {
        setState(PUBLICATIONSTATE.UPDATING);
        synchronized (this) {
            if (Subscription.getSubscriptionAction(message).equals(Subscription.SUBSCRIPTIONACTION.REPLACESUBSCRIPTION)) {
                setSubscriptionType(Subscription.getSubscriptionType(message));
                setSubscriptionID(message.getSubscriptionID());
                setSubscriptionName(message.getSubscriptionName());
                setStartTime(Subscription.getSubscriptionStartTime(message));
                setEndTime(Subscription.getSubscriptionEndTime(message));
                setSubscriptionFrequency(message.getSubscriptionFrequency());
                setReturnAddress(message.getReturnAddress());
            }
        }
        setState(PUBLICATIONSTATE.ACTIVE);
    }

    /**
     * Cancel.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void cancel() {
        if (!getState().equals(PUBLICATIONSTATE.COMPLETED)){
            try{
                if (this.centerMode.equals(Center.RICENTERMODE.EC)){
                    cancelPublicationEC(subscriptionOpId, subscriptionMessage);
                    System.out.println("Publication:  completed cancelPublicationEC call.");
                }
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
        
        setState(PUBLICATIONSTATE.COMPLETED);
        System.out.println("Publication::cancel  Publication set to completed @"+System.currentTimeMillis()+".");
    }

    /**
     * Gets the state.
     *
     * @return the state
     */
    public PUBLICATIONSTATE getState() {
        synchronized (LOCK) {
            return state;
        }
    }

    /**
     * Sets the state.
     *
     * @param state the new state
     */
    private void setState(PUBLICATIONSTATE state) {
        synchronized (LOCK) {
            this.state = state;
        }
    }

    /**
     * Generate subscription msg.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the NTCIP2306 message
     * @throws Exception the exception
     */
    private NTCIP2306Message generatePublicationMsg() throws Exception {
        System.out.println("Publication::generatePublicationMsg: Entering!");
        NTCIP2306Message publication = null;

        if (publicationMessages.size() > 0) {
            if (publicationMessages.get(getPublicationMessageIndex()) != null) {
                long pubCount = incrementPublicationCount();
                C2CMessagePublication pubMsg = new C2CMessagePublication();
                pubMsg.setSubscriptionCount(pubCount);
                pubMsg.setSubscriptionFrequency(getSubscriptionFrequency());
                pubMsg.setSubscriptionName(getSubscriptionName());
                pubMsg.setSubscriptionID(getSubscriptionID());
                pubMsg.setInformationalText("Publication " + pubCount + " from " + opId.getServiceName() + ":" + opId.getPortName() + ":" + opId.getOperationName());

                JAXBContext jc = JAXBContext.newInstance(C2CMessagePublication.class.getPackage().getName());

                Marshaller m = jc.createMarshaller();

                m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                m.marshal(pubMsg, buffer);
                JAXBElement je = c2cObjectFactory.createC2CMessagePublication(pubMsg);
                publication = new NTCIP2306Message(je.getName().getNamespaceURI(), je.getName().getLocalPart(), buffer.toByteArray());
                buffer.close();
                publication.getXmlStatus().setUTF8CharSet(true);
                publication.getXmlStatus().setXMLv1Document(true);
                publication.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
                publication.setPublicationURL(getReturnAddress());
                publication.setPublicationSoapAction(getSubscriptionID());
                publication.setPublicationMessage(true);

                String name = publicationMessages.get(getPublicationMessageIndex()).getMessagePartName(2);
                String nameSpace = publicationMessages.get(getPublicationMessageIndex()).getMessagePartNameSpace(2);
                byte[] content = publicationMessages.get(getPublicationMessageIndex()).getMessagePartContent(2);
                publication.addMessagePart(nameSpace, name, content);
                incrementLastPublished();

            } else {
                publication = null;
            }


        } else if (getSubscriptionType().equals(SUBSCRIPTIONTYPE.ONCHANGE) || getSubscriptionType().equals(SUBSCRIPTIONTYPE.ONETIME)){
            System.out.println("Publication::generatePublicationMsg: About to create publication message!");
            long pubCount = incrementPublicationCount();
            C2CMessagePublication pubMsg = new C2CMessagePublication();
            pubMsg.setSubscriptionCount(pubCount);
            pubMsg.setSubscriptionFrequency(getSubscriptionFrequency());
            pubMsg.setSubscriptionName(getSubscriptionName());
            pubMsg.setSubscriptionID(getSubscriptionID());
            pubMsg.setInformationalText("Publication " + pubCount + " from " + opId.getServiceName() + ":" + opId.getPortName() + ":" + opId.getOperationName());

            JAXBContext jc = JAXBContext.newInstance(C2CMessagePublication.class.getPackage().getName());

            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            m.marshal(pubMsg, buffer);
            JAXBElement je = c2cObjectFactory.createC2CMessagePublication(pubMsg);
            publication = new NTCIP2306Message(je.getName().getNamespaceURI(), je.getName().getLocalPart(), buffer.toByteArray());
            buffer.close();
            publication.getXmlStatus().setUTF8CharSet(true);
            publication.getXmlStatus().setXMLv1Document(true);
            publication.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
            publication.setPublicationURL(getReturnAddress());
            publication.setPublicationSoapAction(getSubscriptionID());
            publication.setPublicationMessage(true);

            // A subscription operation input message must have 2 parts - the c2cri subscription message and the information message.  We want to get information on the information message.
            if (opSpec.getInputMessage().size() == 2) {

                NTCIP2306ControllerResults ntcipResults = new NTCIP2306ControllerResults(opSpec.getOperationName());
                if (subscriptionMessage == null) {
                    ntcipResults.setTransportError(true);
                    ntcipResults.setTransportErrorDescription("No Request was received.");
                    ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
                } else // Check out the Transport Results
                 if (!subscriptionMessage.isTransportErrorEncountered()) {
                        ntcipResults.setTransportError(false);
                        ntcipResults.setTransportErrorDescription("");
                        ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                    } else {
                        ntcipResults.setTransportError(true);
                        ntcipResults.setTransportErrorDescription(subscriptionMessage.getTransportErrorDescription());
                        ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                    }

                ntcipResults = NTCIP2306Controller.processMessageResults(opSpec, ntcipResults, subscriptionMessage, NTCIP2306Message.MESSAGETYPE.REQUEST);
                NTCIP2306ApplicationLayerOperationResults appLayerOpResults = new NTCIP2306ApplicationLayerOperationResults(ntcipResults);

                // The list is 0 referenced so the second part is at index 1.
                String name = opSpec.getInputMessage().get(1).getLocalPart();
                String nameSpace = opSpec.getInputMessage().get(1).getNamespaceURI();
                // Send the original subscription message and expect a return of the new subscription message.
                Message pubMessage = DefaultMessageContentGenerator.getInstance().getResponseMessage(opSpec.getOperationName(), nameSpace, name, C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), subscriptionMessage), appLayerOpResults);

                // If the generated message is of an expected type then return the published content.  Otherwise return null.
                if (pubMessage.getMessageType().equals(name)) {
                    publication.addMessagePart(nameSpace, name, pubMessage.getMessageBody());
                    incrementLastPublished();
                    System.out.println("Publication::generatePublicationMsg: Added the generated publication message.");
                } else {
                    publication = null;
                    System.out.println("Publication::generatePublicationMsg: pubMessage type = " + pubMessage.getMessageType() + " not equal name " + name);
                }
            } else {
                publication = null;
            }

        // This is an unscripted periodic message.
        } else if ((this.periodicPubMessageThread == null)&&(!isConnectedToScript())) {
            
            // Create a new thread to create periodic publication messages.
            this.periodicPubMessageThread = new Thread() {

                @Override
                public void run() {
                    long frequency = getSubscriptionFrequency();
                    long lastgenerationDuration = 0;
                    NTCIP2306Message newPublication = null;
                    NTCIP2306ApplicationLayerOperationResults appLayerOpResults = null;
                    // A subscription operation input message must have 2 parts - the c2cri subscription message and the information message.  We want to get information on the information message.
                    if (opSpec.getInputMessage().size() == 2) {

                        NTCIP2306ControllerResults ntcipResults = new NTCIP2306ControllerResults(opSpec.getOperationName());
                        if (subscriptionMessage == null) {
                            ntcipResults.setTransportError(true);
                            ntcipResults.setTransportErrorDescription("No Request was received.");
                            ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
                        } else // Check out the Transport Results
                        {
                            if (!subscriptionMessage.isTransportErrorEncountered()) {
                                ntcipResults.setTransportError(false);
                                ntcipResults.setTransportErrorDescription("");
                                ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                            } else {
                                ntcipResults.setTransportError(true);
                                ntcipResults.setTransportErrorDescription(subscriptionMessage.getTransportErrorDescription());
                                ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                            }
                        }

                        ntcipResults = NTCIP2306Controller.processMessageResults(opSpec, ntcipResults, subscriptionMessage, NTCIP2306Message.MESSAGETYPE.REQUEST);
                        appLayerOpResults = new NTCIP2306ApplicationLayerOperationResults(ntcipResults);

                    }

                    try {
                        // This value will be replaced when the actual publication is transmitted.
                        long pubCount = 1;
                        C2CMessagePublication pubMsg = new C2CMessagePublication();
                        pubMsg.setSubscriptionCount(pubCount);
                        pubMsg.setSubscriptionFrequency(getSubscriptionFrequency());
                        pubMsg.setSubscriptionName(getSubscriptionName());
                        pubMsg.setSubscriptionID(getSubscriptionID());
                        pubMsg.setInformationalText("Publication " + pubCount + " from " + opId.getServiceName() + ":" + opId.getPortName() + ":" + opId.getOperationName());

                        JAXBContext jc = JAXBContext.newInstance(C2CMessagePublication.class.getPackage().getName());

                        Marshaller m = jc.createMarshaller();
                        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
                        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                        m.marshal(pubMsg, buffer);
                        JAXBElement je = c2cObjectFactory.createC2CMessagePublication(pubMsg);
                        newPublication = new NTCIP2306Message(je.getName().getNamespaceURI(), je.getName().getLocalPart(), buffer.toByteArray());
                        buffer.close();
                        newPublication.getXmlStatus().setUTF8CharSet(true);
                        newPublication.getXmlStatus().setXMLv1Document(true);
                        newPublication.setMessageType(NTCIP2306Message.MESSAGETYPE.REQUEST);
                        newPublication.setPublicationURL(getReturnAddress());
                        newPublication.setPublicationSoapAction(getSubscriptionID());
                        newPublication.setPublicationMessage(true);
                    } catch (JAXBException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    while (!this.isInterrupted() && appLayerOpResults != null) {
                        try {
                            long processingStartTime = System.currentTimeMillis();

                            System.out.println("Publication::generatePublicationMsg: About to create publication message!");

                            // The list is 0 referenced so the second part is at index 1.
                            String name = opSpec.getInputMessage().get(1).getLocalPart();
                            String nameSpace = opSpec.getInputMessage().get(1).getNamespaceURI();

                            // Send the original subscription message and expect a return of the new subscription message.
                            Message pubMessage = DefaultMessageContentGenerator.getInstance().getResponseMessage(opSpec.getOperationName(), nameSpace, name, C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), subscriptionMessage), appLayerOpResults);

                            // If the generated message is of an expected type then return the published content.  Otherwise return null.
                            if (pubMessage.getMessageType().equals(name) && newPublication != null) {
                                newPublication.addMessagePart(nameSpace, name, pubMessage.getMessageBody());
                                publicationMessages.add(newPublication);

                                System.out.println("Publication::generatePublicationMsg: Added the generated publication message.");
                            } else {
                                newPublication = null;
                                System.out.println("Publication::generatePublicationMsg: pubMessage type = " + pubMessage.getMessageType() + " not equal name " + name);
                            }
                            lastgenerationDuration = System.currentTimeMillis() - processingStartTime;

                            // We are not going to create new messages at any faster than 10 seconds
                            // if it took less time to create the message than the desired frequency then set the wait time
                            if ((frequency < 10) && (lastgenerationDuration < 10000)) {
                                Thread.sleep(10000);
                            } else if (lastgenerationDuration < frequency * 1000) {
                                Thread.sleep(frequency * 1000 - lastgenerationDuration);
                            } else {
                                Thread.sleep(lastgenerationDuration + 1000);
                            }
                        } catch (InterruptedException ex) {
                            break;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("Exiting PeriodicPubMessageThread!!");
                }
            };
            this.periodicPubMessageThread.start();

        }

        return publication;
    }

    /**
     * Generate receipt msg.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param informationText the information text
     * @return the NTCIP2306 message
     * @throws Exception the exception
     */
    private NTCIP2306Message generateReceiptMsg(String informationText) throws Exception {
        NTCIP2306Message receipt = null;

        ObjectFactory of = new ObjectFactory();
        C2CMessageReceipt receiptMsg = of.createC2CMessageReceipt();


        receiptMsg.setInformationalText("Publication " + getPublicationCount() + " from " + opId.getServiceName() + ":" + opId.getPortName() + ":" + opId.getOperationName() + "\n" + informationText);

        JAXBContext jc = JAXBContext.newInstance(C2CMessageReceipt.class.getPackage().getName());

        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
                Boolean.TRUE);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        m.marshal(receiptMsg, buffer);
        JAXBElement je = c2cObjectFactory.createC2CMessageReceipt(receiptMsg);
        receipt = new NTCIP2306Message(je.getName().getNamespaceURI(), je.getName().getLocalPart(), buffer.toByteArray());
        receipt.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
        receipt.getXmlStatus().setUTF8CharSet(true);
        receipt.getXmlStatus().setXMLv1Document(true);
        buffer.close();

        receipt.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);

        return receipt;
    }

    /**
     * Generate subscription response msg.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param pubOpId the pub op id
     * @param publicationRequest the subscription request
     * @return the NTCIP2306 message
     * @throws Exception the exception
     */
    private NTCIP2306Message generatePublicationResponseMsg(OperationIdentifier pubOpId,
            NTCIP2306Message publicationRequest) throws Exception {
        NTCIP2306Message publicationResponse = null;

        try {
            String responseText = verifyPublicationRequest(opSpec, publicationRequest);
            publicationResponse = generateReceiptMsg(responseText);
            publicationResponse.setMessageType(NTCIP2306Message.MESSAGETYPE.RESPONSE);
            publicationResponse.getXmlStatus().setUTF8CharSet(true);
            publicationResponse.getXmlStatus().setXMLv1Document(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            publicationResponse = generateErrorResponseMsg(ex.getMessage() == null ? "Null Pointer Error" : ex.getMessage());
        }

        return publicationResponse;
    }

    /**
     * Generate error response msg.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param errorText the error text
     * @return the NTCIP2306 message
     * @throws Exception the exception
     */
    private NTCIP2306Message generateErrorResponseMsg(String errorText) throws Exception {
        NTCIP2306Message errorResponse = null;

        String parsedErrorText;
        if (errorText.length() <= 250) {
            parsedErrorText = errorText;
        } else {
            parsedErrorText = errorText.substring(0, 246).concat("...");
        }
        String errorTemplate = "<errorReportMsg><organization-information><organization-id>string</organization-id></organization-information><organization-requesting><organization-id>string</organization-id></organization-requesting><error-code>7</error-code><error-text>" + parsedErrorText + "</error-text></errorReportMsg>";

        errorResponse = new NTCIP2306Message("None", "None", errorTemplate.getBytes());
        errorResponse.setMessageType(NTCIP2306Message.MESSAGETYPE.ERROR);
        errorResponse.getXmlStatus().setUTF8CharSet(true);
        errorResponse.getXmlStatus().setXMLv1Document(true);
        return errorResponse;
    }

    /**
     * Verify subscription request.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param pubRequestMsg the pub request msg
     * @return the string
     * @throws Exception the exception
     */
    private String verifyPublicationRequest(OperationSpecification opSpec, NTCIP2306Message pubRequestMsg) throws Exception {
        String responseText = "OK";

        // Check whether pubRequestMsg was valid XML.
        if (!pubRequestMsg.getXmlStatus().isNTCIP2306ValidXML()) {
            throw new Exception(pubRequestMsg.getSoapStatus().getSOAPErrors() + ";  " + pubRequestMsg.getXmlStatus().getXMLErrors());
        }

        // Check whether pubRequestMsg was valid SOAP.
        if (!pubRequestMsg.getSoapStatus().isNTCIP2306ValidSOAP()) {
            if (!pubRequestMsg.getXmlStatus().isXmlValidatedToWSDLSchemas()) {
                throw new Exception("SOAP Message Parts Validation Errors:" + pubRequestMsg.getXmlStatus().getXMLErrors());
            }
            throw new Exception(pubRequestMsg.getSoapStatus().getSOAPErrors());
        }

        // Check whether the subscription message is the right type for this operation
        if (pubRequestMsg.getNumberMessageParts() != 2) {
            throw new Exception("Publication Message requires 2 message parts.");
        }

        String expectedMsgName = opSpec.getInputMessage().get(1).getLocalPart();
        if (!pubRequestMsg.getMessagePartName(2).equals(expectedMsgName)) {
            throw new Exception("Publication Message should contain a " + expectedMsgName + " message, but instead has a " + pubRequestMsg.getMessagePartName(2) + " messaege.");
        }

        C2CMessagePublication thisPub = null;
        try {
            thisPub = produceC2cMessagePublication(pubRequestMsg);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Error processing Publication: " + ex.getMessage());
        }
        OperationIdentifier opId = new OperationIdentifier(opSpec.getRelatedToService(),
                opSpec.getRelatedToPort(), opSpec.getOperationName(), OperationIdentifier.SOURCETYPE.HANDLER);

        boolean subscriptionMatch = false;
        if (getSubscriptionID().equals(thisPub.getSubscriptionID())) {
            subscriptionMatch = true;

            if (getState().equals(Publication.PUBLICATIONSTATE.ACTIVE)) {
                responseText = "OK";
            } else if (getState().equals(Publication.PUBLICATIONSTATE.INITIALIZING)) {
                throw new Exception("The related Publication is in the initializing state.");
            } else {
                throw new Exception("The related Publication is in an invalid state: " + getState());
            }
        }

        boolean publicationMatch = false;
        if (getSubscriptionID().equals(thisPub.getSubscriptionID())) {
            publicationMatch = true;
            if (getState().equals(Publication.PUBLICATIONSTATE.ACTIVE)) {
                responseText = "OK";
            } else if (getState().equals(Publication.PUBLICATIONSTATE.UPDATING)) {
                throw new Exception("The subscription is updating the associated publication.");
            } else {
                throw new Exception("The publication associated with the subscription id is in an invalid state.");
            }
        }

        if (!publicationMatch) {
            throw new Exception("No publication is associated with the subscription id.");
        } else if (!subscriptionMatch) {
            throw new Exception("No subscription is associated with this publication.");
        }

        return responseText;
    }

    /**
     * Produce c2c message subscription.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param message the message
     * @return the c2 c message subscription
     * @throws Exception the exception
     */
    private C2CMessagePublication produceC2cMessagePublication(NTCIP2306Message message) throws Exception {
        C2CMessagePublication publication = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(C2CMessagePublication.class.getPackage().getName());
            Unmarshaller um = jc.createUnmarshaller();
            ByteArrayInputStream buffer = new ByteArrayInputStream(message.getMessagePartContent(1));
            publication = (C2CMessagePublication) ((javax.xml.bind.JAXBElement) um.unmarshal(buffer)).getValue();
            buffer.close();

            Thread.currentThread().sleep(100);
        } catch (IOException ioex) {
            ioex.printStackTrace();
            throw new Exception("produceC2cMessagePublication Exception:", ioex);
        } catch (JAXBException jaxbex) {
            jaxbex.printStackTrace();
            throw new Exception("produceC2cMessagePublication Exception:", jaxbex);
        } catch (InterruptedException iex) {
            iex.printStackTrace();
            throw new Exception("produceC2cMessagePublication Exception:", iex);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("produceC2cMessagePublication Exception:", ex);
        }
        return publication;
    }

    /**
     * Checks if is within subscription period.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is within subscription period
     */
    private boolean isWithinSubscriptionPeriod() {
        boolean withinSubscriptionPeriod = false;

        Date currentTime = Calendar.getInstance().getTime();
        try {
            if ((currentTime.compareTo(getStartTime()) >= 0)
                    && (currentTime.compareTo(getEndTime()) <= 0)) {
                withinSubscriptionPeriod = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return withinSubscriptionPeriod;
    }

    /**
     * Checks if is subscription expired.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is subscription expired
     */
    private boolean isSubscriptionExpired() {
        boolean subscriptionExpired = false;
        try {
            Date currentTime = Calendar.getInstance().getTime();

            if (currentTime.compareTo(getEndTime()) > 0) {
                subscriptionExpired = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return subscriptionExpired;
    }

    /**
     * Gets the end time.
     *
     * @return the end time
     */
    private Date getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time.
     *
     * @param endTime the new end time
     */
    private void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * Gets the subscription count.
     *
     * @return the subscription count
     */
    public long getPublicationCount() {
        System.out.println("Publication::getPublicationCount:  returning count "+publicationsTransmitted);
        return publicationsTransmitted;

    }

    /**
     * Gets the subscription frequency.
     *
     * @return the periodic publication frequency, if not periodic return -1.
     */
    public long getPublicationFrequency() {
        long pubFrequency = -1;
        if (getSubscriptionType().equals(SUBSCRIPTIONTYPE.PERIODIC)){
            pubFrequency = subscriptionFrequency;
        }
        System.out.println("Publication::getPublicationFrequency:  returning frequency "+pubFrequency);
        return pubFrequency;

    }
    
    
    /**
     * Gets the millis since last periodic subscription.
     *
     * @return the millis since last periodic subscription
     */
    public long getMillisSinceLastPeriodicPublication() {
		synchronized (LOCK) {
			return millisSinceLastPublication;
		}
    }

    /**
     * Compute millis since last subscription.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param currentTimeInMillis the current time in millis
     */
    private void computeMillisSinceLastPublication(long currentTimeInMillis) {
        synchronized (LOCK) {
            millisSinceLastPublication = currentTimeInMillis - lastPublicationTimeInMillis;
            lastPublicationTimeInMillis = currentTimeInMillis;
            System.out.println("Publication::computeMillisSinceLastPublication lastPublicationTimeInMillis = "+lastPublicationTimeInMillis);
        }
    }

    /**
     * Increment subscription count.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the long
     */
    private long incrementPublicationCount() {
        if (this.publicationCount < maxPublications) {
            this.publicationCount = publicationCount + 1;
        } else {
            this.publicationCount = 1;
        }
        return this.publicationCount;
    }

    /**
     * Gets the start time.
     *
     * @return the start time
     */
    private Date getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time.
     *
     * @param startTime the new start time
     */
    private void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * Gets the subscription frequency.
     *
     * @return the subscription frequency
     */
    private long getSubscriptionFrequency() {
        return subscriptionFrequency;
    }

    /**
     * Sets the subscription frequency.
     *
     * @param subscriptionFrequency the new subscription frequency
     */
    private void setSubscriptionFrequency(long subscriptionFrequency) {
        this.subscriptionFrequency = subscriptionFrequency;
    }

    /**
     * Gets the subscription id.
     *
     * @return the subscription id
     */
    public String getSubscriptionID() {
        return subscriptionID;
    }

    /**
     * Sets the subscription id.
     *
     * @param subscriptionID the new subscription id
     */
    private void setSubscriptionID(String subscriptionID) {
        this.subscriptionID = subscriptionID;
    }

    /**
     * Gets the subscription name.
     *
     * @return the subscription name
     */
    public String getSubscriptionName() {
        return subscriptionName;
    }

    /**
     * Sets the subscription name.
     *
     * @param subscriptionName the new subscription name
     */
    private void setSubscriptionName(String subscriptionName) {
        this.subscriptionName = subscriptionName;
    }

    /**
     * Gets the subscription type.
     *
     * @return the subscription type
     */
    private SUBSCRIPTIONTYPE getSubscriptionType() {
        return subscriptionType;
    }

    /**
     * Sets the subscription type.
     *
     * @param subscriptionType the new subscription type
     */
    private void setSubscriptionType(SUBSCRIPTIONTYPE subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    /**
     * Gets the return address.
     *
     * @return the return address
     */
    private String getReturnAddress() {
        return returnAddress;
    }

    /**
     * Sets the return address.
     *
     * @param returnAddress the new return address
     */
    private void setReturnAddress(String returnAddress) {
        this.returnAddress = returnAddress;
    }

    /**
     * Gets the subscription error.
     *
     * @return the subscription error
     */
    public String getPublicationError() {
        return publicationError;
    }

    /**
     * Gets the subscription error count.
     *
     * @return the subscription error count
     */
    public long getPublicationErrorCount() {
        return publicationErrorCount;
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
     * Gets the op id.
     *
     * @return the op id
     */
    public OperationIdentifier getOpId() {
        return opId;
    }

    /**
     * Checks if is connected to script.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is connected to script
     */
    public boolean isConnectedToScript() {
        boolean connectedResponse = false;
        synchronized (connectedToScriptLock) {
            connectedResponse = connectedToScript;
            System.err.println("Publication::isConnectedToScript  ConnectedToScript = " + this.connectedToScript + " @" + System.currentTimeMillis());
        }
        return connectedResponse;
    }

    /**
     * Sets the connected to script.
     *
     * @param connectToScript the new connected to script
     */
    public void setConnectedToScript(boolean connectToScript) {
        synchronized (connectedToScriptLock) {
            connectedToScript = connectToScript;
            System.err.println("Publication::setconnectedToScript  ConnectedToScript = " + connectedToScript + " @" + System.currentTimeMillis());
        }
    }

    /**
     * Produce c2c message subscription xml.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param message the message
     * @return the byte[]
     * @throws Exception the exception
     */
    public static byte[] produceC2cMessagePublicationXML(C2CMessagePublication message) throws Exception {
        byte[] publication = null;
        try {
            JAXBContext jc = JAXBContext.newInstance(C2CMessagePublication.class.getPackage().getName());
            Marshaller mm = jc.createMarshaller();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            mm.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            mm.marshal(message, buffer);
            publication = buffer.toByteArray();
            buffer.close();

        } catch (IOException ioex) {
            ioex.printStackTrace();
            throw new Exception("Publication:produceC2CMessagePublicationXML Error: ", ioex);
        } catch (JAXBException jaxbex) {
            jaxbex.printStackTrace();
            throw new Exception("Publication:produceC2CMessagePublicationXML Error: ", jaxbex);
        }
        return publication;
    }

    /**
     * Checks if is on change update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is on change update
     */
    private synchronized boolean isOnChangeUpdate() {
        return onChangeUpdate;
    }

    /**
     * Sets the on change update.
     *
     * @param onChangeUpdate the new on change update
     */
    public synchronized void setOnChangeUpdate(boolean onChangeUpdate) {
        this.onChangeUpdate = onChangeUpdate;
    }

    /**
     * Adds the subscription message.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param pubMessage the pub message
     */
    public void addPublicationMessage(NTCIP2306Message pubMessage) {
        synchronized (publicationMessages) {
            publicationMessages.add(pubMessage);
        }
    }

    /**
     * Gets the subscription message index.
     *
     * @return the subscription message index
     */
    private int getPublicationMessageIndex() {
        return publicationMessageIndex;
    }

    /**
     * Increment last published.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private void incrementLastPublished() {
        if (this.publicationMessageIndex + 1 < publicationMessages.size()) {
            publicationMessageIndex++;
        } else {
            this.publicationMessageIndex = 0;
        }
    }

    @Override
    public void messageUpdate(String messageName, String nameSpace, byte[] message) {
        System.out.println("Publication::messageUpdate Operation "+opId.getOperationName()+" received notification of an update to message "+messageName);
        setOnChangeUpdate(true);
    }

}
