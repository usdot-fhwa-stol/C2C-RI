/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.operations;

import org.fhwa.c2cri.ntcip2306v109.messaging.DefaultMessageContentGenerator;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.namespace.QName;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.applayer.MessageContentGenerator;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ApplicationLayerOperationResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Controller;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306MessageProvider;
import org.fhwa.c2cri.ntcip2306v109.status.NTCIP2306Status;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;

/**
 * This class is the default "listener" for all operations within this center.
 * It does simple message response and handling. An external listener may
 * register with this class to "take over" handling for a set of operations.
 *
 * The listener interface for receiving defaultOperation events. The class that
 * is interested in processing a defaultOperation event implements this
 * interface. When the defaultOperation event occurs, that object's appropriate
 * method is invoked.
 *
 * @author TransCore ITS,LLC Last Updated: 8/9/2013
 * @see DefaultOperationEvent
 */
public class DefaultOperationListener implements Runnable {

    /**
     * The eqc.
     */
    private final QueueController eqc;

    /**
     * The iqc.
     */
    private final QueueController iqc;

    /**
     * The ops spec.
     */
    private final OperationSpecCollection opsSpec;

    /**
     * The ri center mode.
     */
    private final Center.RICENTERMODE riCenterMode;

    /**
     * The external operations.
     */
    private final CopyOnWriteArrayList<OperationIdentifier> externalOperations;

    /**
     * The default msg generator.
     */
    private MessageContentGenerator defaultMsgGenerator;

    /**
     * The ready signal.
     */
    private final CountDownLatch readySignal;

    /**
     * The lock.
     */
    private final Lock lock = new ReentrantLock();

    /**
     * The stop listener.
     */
    private volatile boolean stopListener = false;

    /**
     * The logger to used for storing message to the log file.
     */
    private Logger log = Logger.getLogger(DefaultOperationListener.class.getName());
    
    /**
     * Instantiates a new default operation listener.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param eqc the eqc
     * @param iqc the iqc
     * @param opsSpec the ops spec
     * @param centerMode the center mode
     */
    public DefaultOperationListener(final QueueController eqc,
            final QueueController iqc,
            final OperationSpecCollection opsSpec,
            final Center.RICENTERMODE centerMode) {
        this.eqc = eqc;
        this.opsSpec = opsSpec;
        this.riCenterMode = centerMode;
        this.iqc = iqc;
        this.defaultMsgGenerator = DefaultMessageContentGenerator.getInstance();
        this.externalOperations = new CopyOnWriteArrayList<>();
        readySignal = null;
    }

    /**
     * Instantiates a new default operation listener.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param eqc the eqc
     * @param iqc the iqc
     * @param opsSpec the ops spec
     * @param centerMode the center mode
     * @param readySignal the ready signal
     */
    public DefaultOperationListener(final QueueController eqc,
            final QueueController iqc,
            final OperationSpecCollection opsSpec,
            final Center.RICENTERMODE centerMode,
            final CountDownLatch readySignal) {
        this.eqc = eqc;
        this.opsSpec = opsSpec;
        this.riCenterMode = centerMode;
        this.iqc = iqc;
        this.defaultMsgGenerator = DefaultMessageContentGenerator.getInstance();
        this.externalOperations = new CopyOnWriteArrayList<>();
        this.readySignal = readySignal;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        System.out.println("DefaultOperationListener has Started ...with queue=" + eqc);
        System.out.println("DefaultOperationListener::run  externalOperations id =  " + externalOperations.hashCode() + "  DefaultListener: " + this);
        if (readySignal != null) {
            readySignal.countDown();
        }
        while (!stopListener) {
            try {
                manageOperations();
                Thread.currentThread().sleep(350);
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopListener = true;
				Thread.currentThread().interrupt();
            }
        }
        System.out.println("DefaultOperationListener has stopped per user request.  Queue=" + eqc);

    }

    /**
     * Shutdown.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void shutdown() {
        stopListener = true;
    }

    /**
     * Manage operations.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @throws InterruptedException the interrupted exception
     */
    private void manageOperations() throws InterruptedException {
        for (OperationSpecification thisSpec : opsSpec.getCopyAsList()) {
            OperationIdentifier opId = new OperationIdentifier(thisSpec.getRelatedToService(),
                    thisSpec.getRelatedToPort(), thisSpec.getOperationName(),
                    OperationIdentifier.SOURCETYPE.HANDLER);

            // Check the External Request Queue
            if (eqc.isAvaliableInRequestQueue(opId)) {

                NTCIP2306Message extReqMsg = eqc.getMessageFromExtRequestQueue(opId, 500);
                if (extReqMsg != null) {
                    System.out.println("DefaultOperationListener::manageOperations - Completed check of Operation " + opId.toString());
                    externalRequestUpdate(opId, extReqMsg);
                }
            }
            // Check the External Response Queue
            if (eqc.isAvaliableInResponseQueue(opId)) {
                NTCIP2306Message extRespMsg = eqc.getMessageFromExtResponseQueue(opId, 500);
                if (extRespMsg != null) {
                    System.out.println("DefaultOperationListener::manageOperations - Completed check of Operation " + opId.toString());
                    externalResponseUpdate(opId, extRespMsg);
                }
            }

            // Check the External OperationResponse Queue
            if (eqc.isAvaliableInOperationResultsQueue(opId)) {
                OperationResults opsResults = eqc.getResultsFromOperationResultsQueue(opId, 500);
                if (opsResults != null) {
                    System.out.println("DefaultOperationListener::manageOperations - Completed check of Operation " + opId.toString());
                    operationResultsUpdate(opId, opsResults);
                }
            }

            if (isRegisteredOperation(opId)) {
                // Check internal queue for Operations from listeners
                opId = new OperationIdentifier(thisSpec.getRelatedToService(),
                        thisSpec.getRelatedToPort(), thisSpec.getOperationName(),
                        OperationIdentifier.SOURCETYPE.LISTENER);

                // Check the Internal Request Queue
                if (iqc.isAvaliableInRequestQueue(opId)) {

                    NTCIP2306Message extReqMsg = iqc.getMessageFromExtRequestQueue(opId, 500);
                    if (extReqMsg != null) {
                        System.out.println("DefaultOperationListener::manageOperations - Completed check of Operation " + opId.toString());
                        internalRequestUpdate(opId, extReqMsg);
                    }
                }

                // Check the Internal Response Queue
                if (iqc.isAvaliableInResponseQueue(opId)) {
                    NTCIP2306Message extRespMsg = iqc.getMessageFromExtResponseQueue(opId, 500);
                    if (extRespMsg != null) {
                        System.out.println("DefaultOperationListener::manageOperations - Completed check of Operation " + opId.toString());
                        internalResponseUpdate(opId, extRespMsg);
                    }
                }

            }
        }
    }

    /**
     * Operation results update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param operationId the operation id
     * @param opResults the op results
     */
    private void operationResultsUpdate(OperationIdentifier operationId, OperationResults opResults) {
        if (isRegisteredOperation(operationId)) {
            iqc.addToOperationResultsQueue(operationId, opResults);
        } else {
            System.out.println("The DefaultOperationListener received Operation Results for "
                    + opResults.getOperationSpecification().toString());
            try {
                if (opResults.getRequestMessage() == null) {

                    System.err.println("DefaultOperationsListener::operationResultsUpdate  The request message in the results was null @" + System.currentTimeMillis());
                }
                if (opResults.getResponseMessage() == null) {

                    System.err.println("DefaultOperationsListener::operationResultsUpdate  The response message in the results was null @" + System.currentTimeMillis());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * External request update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param operationId the operation id
     * @param em the em
     */
    private void externalRequestUpdate(OperationIdentifier operationId, NTCIP2306Message em) {
        String messageErrors = "";
        boolean messageOK = false;

        OperationSpecification opSpec = opsSpec.get(operationId.getServiceName(),
                operationId.getPortName(), operationId.getOperationName());
        try {
            if (!opSpec.isGetOperation()) {
                em.verifyMessageToOpSpec(opSpec, NTCIP2306Message.MESSAGETYPE.REQUEST);
            }
        } catch (Exception ex) {
            messageErrors = ex.getMessage();
        }

        if (messageErrors.equals("")) {
            messageOK = true;
        }

        System.out.println("DefaultOperationListener::externalRequestUpdate externalOperations id =  " + externalOperations.hashCode() + "  size = " + externalOperations.size() + "  DefaultListener: " + this);
        if (isRegisteredOperation(operationId)) {
            iqc.addToExtRequestQueue(operationId, em);
        } else {

            NTCIP2306Message lem = null;
            try {

                    for (QName msgQName : opSpec.getInputMessage()) {
                        if (defaultMsgGenerator.isMessageSupported(opSpec.getOperationName(),
                                msgQName.getNamespaceURI(), msgQName.getLocalPart(), new NTCIP2306MessageProvider(opSpec.getOperationName(), em))) {

                            NTCIP2306ControllerResults ntcipResults = new NTCIP2306ControllerResults(opSpec.getOperationName());
                            if (em == null) {
                                ntcipResults.setTransportError(true);
                                ntcipResults.setTransportErrorDescription("No Request was received.");
                                ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.SOCKETRECEIVEERROR.toString());
                            } else // Check out the Transport Results
                            {
                                if (!em.isTransportErrorEncountered()) {
                                    ntcipResults.setTransportError(false);
                                    ntcipResults.setTransportErrorDescription("");
                                    ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.NONE.toString());
                                } else {
                                    ntcipResults.setTransportError(true);
                                    ntcipResults.setTransportErrorDescription(em.getTransportErrorDescription());
                                    ntcipResults.setTransportErrorType(NTCIP2306Status.TRANSPORTERRORTYPES.INTERNALSERVERERROR.toString());
                                }
                            }

                            ntcipResults = NTCIP2306Controller.processMessageResults(opSpec, ntcipResults, em, NTCIP2306Message.MESSAGETYPE.REQUEST);
                            NTCIP2306ApplicationLayerOperationResults appLayerOpResults = new NTCIP2306ApplicationLayerOperationResults(ntcipResults);

                            if (lem == null) {
                                lem = new NTCIP2306Message(msgQName.getNamespaceURI(), msgQName.getLocalPart(),
                                        defaultMsgGenerator.getResponseMessage(opSpec.getOperationName(),
                                                msgQName.getNamespaceURI(), msgQName.getLocalPart(), C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), em), appLayerOpResults).getMessageBody());
                                System.out.println("Returned Message Part1:" + new String(lem.getMessagePartContent(1)));
                            } else {
                                lem.addMessagePart(msgQName.getNamespaceURI(), msgQName.getLocalPart(),
                                        defaultMsgGenerator.getResponseMessage(opSpec.getOperationName(),
                                                msgQName.getNamespaceURI(), msgQName.getLocalPart(), C2CRIMessageAdapter.toC2CRIMessage(opSpec.getOperationName(), em), appLayerOpResults).getMessageBody());
                                System.out.println("Returned Message Part 2:" + new String(lem.getMessagePartContent(2)));
                            }
                        } else {
                            if (lem == null) {
                                lem = new NTCIP2306Message("", "",
                                    ("Error: Operation - " + opSpec.getOperationName() + " namespace - " + msgQName.getNamespaceURI() + " message - " + msgQName.getLocalPart() + " generation is not currently supported.").getBytes());
                            } else {
                                lem.addMessagePart("", "",
                                    ("Error: Operation - " + opSpec.getOperationName() + " namespace - " + msgQName.getNamespaceURI() + " message - " + msgQName.getLocalPart() + " generation is not currently supported.").getBytes());
                            }
                        }
                    }


            } catch (Exception ex) {
                ex.printStackTrace();
                if (lem == null) {
                    lem = new NTCIP2306Message("", "",
                            ("Error Encountered Generating Message: " + ex.getMessage()).getBytes());
                } else {
                    lem.addMessagePart("", "",
                            ("Error Encountered Generating Message: " + ex.getMessage()).getBytes());
                }
            }
            OperationIdentifier returnId = new OperationIdentifier(operationId.getServiceName(), operationId.getPortName(), operationId.getOperationName(),
                    OperationIdentifier.SOURCETYPE.LISTENER);
            eqc.addToExtResponseQueue(returnId, lem);

        }
    }

    /**
     * External response update.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param operationId the operation id
     * @param em the em
     */
    private void externalResponseUpdate(OperationIdentifier operationId, NTCIP2306Message em) {
        if (isRegisteredOperation(operationId)) {

            iqc.addToExtResponseQueue(operationId, em);
        } else {
            OperationSpecification opSpec = opsSpec.get(operationId.getServiceName(),
                    operationId.getPortName(), operationId.getOperationName());

            String errors = "";
            try {
                em.verifyMessageToOpSpec(opSpec, em.getMessageType());
            } catch (Exception ex) {
                errors = ex.getMessage();
            }

            System.out.println("The DefaultOperationListener received the Response Content for "
                    + em.getMessagePartName(1));
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
     * Register external operation listner.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opId the op id
     */
    public void registerExternalOperationListner(OperationIdentifier opId) {
        lock.lock();
        try {
            if (!externalOperations.contains(opId)) {
                System.out.println("DefaultOperationListener::registerExternalOperationListener Before externalOperations id =  " + externalOperations.hashCode() + "  DefaultListener: " + this);
                externalOperations.add(opId);
                System.out.println("DefaultOperationListener::registerExternalOperationListener After externalOperations id =  " + externalOperations.hashCode() + "  DefaultListener: " + this);

            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Unregister external operation listener.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opId the op id
     */
    public void unregisterExternalOperationListener(OperationIdentifier opId) {
        lock.lock();
        try {
            if (externalOperations.contains(opId)) {
                System.out.println("DefaultOperationListener::unregisterExternalOperationListener Before externalOperations id =  " + externalOperations.hashCode() + "  DefaultListener: " + this);
                externalOperations.remove(opId);
                System.out.println("DefaultOperationListener::unregisterExternalOperationListener After externalOperations id =  " + externalOperations.hashCode() + "  DefaultListener: " + this);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Checks if is registered operation.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param opId the op id
     * @return true, if is registered operation
     */
    public boolean isRegisteredOperation(OperationIdentifier opId) {
        lock.lock();
        boolean results = false;
        try {
            results = externalOperations.contains(opId);
        } finally {
            lock.unlock();
        }
        return results;
    }
}
