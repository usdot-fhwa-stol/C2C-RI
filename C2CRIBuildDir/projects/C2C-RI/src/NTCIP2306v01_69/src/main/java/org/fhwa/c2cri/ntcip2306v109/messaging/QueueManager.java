/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.messaging;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationListener;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationResults;


/**
 * The Class QueueManager manages the queuing of messages between the various controller elements in the system.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class QueueManager implements Runnable, QueueController {

    /** The external request queue. */
    private final ConcurrentHashMap<OperationIdentifier, NTCIP2306Message> externalRequestQueue = new ConcurrentHashMap<OperationIdentifier, NTCIP2306Message>(8, 0.9f, 1);

    /** The Backup external request queue. */
    private final ConcurrentHashMap<OperationIdentifier, NTCIP2306Message> backupExternalRequestQueue = new ConcurrentHashMap<OperationIdentifier, NTCIP2306Message>(8, 0.9f, 1);
    
    /** The external response queue. */
    private final ConcurrentHashMap<OperationIdentifier, NTCIP2306Message> externalResponseQueue = new ConcurrentHashMap<OperationIdentifier, NTCIP2306Message>(8, 0.9f, 1);
    
     /** The Backup external response queue. */
    private final ConcurrentHashMap<OperationIdentifier, NTCIP2306Message> backupExternalResponseQueue = new ConcurrentHashMap<OperationIdentifier, NTCIP2306Message>(8, 0.9f, 1);
 
    /** The operation results queue. */
    private final ConcurrentHashMap<OperationIdentifier, OperationResults> operationResultsQueue = new ConcurrentHashMap<OperationIdentifier, OperationResults>(8, 0.9f, 1);

    /** The Backup operation results queue. */
    private final ConcurrentHashMap<OperationIdentifier, OperationResults> backupOperationResultsQueue = new ConcurrentHashMap<OperationIdentifier, OperationResults>(8, 0.9f, 1);
    
    /** The Constant externalQueueSemaphore. */
    private static final BinarySemaphore externalQueueSemaphore = new BinarySemaphore(1);
    
    /** The operation listeners. */
    private final ConcurrentHashMap<OperationIdentifier, OperationListener> operationListeners = new ConcurrentHashMap<OperationIdentifier, OperationListener>(8, 0.9f, 1);
    
    /** The ready signal. */
    private final CountDownLatch readySignal;
    
    /** The stop queue. */
    private volatile boolean stopQueue = false;
    
    
    /**
     * Instantiates a new queue manager.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public QueueManager(){
        this.readySignal = null;
    }

    /**
     * Instantiates a new queue manager.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param readySignal the ready signal
     */
    public QueueManager(CountDownLatch readySignal){
        this.readySignal = readySignal;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {
        System.out.println("QueueManager has Started ..."+this);
        if (readySignal != null)readySignal.countDown();
        while (!stopQueue) {
            try {
                externalQueueSemaphore.waitForNotify();
                manageQueue();
                externalQueueSemaphore.notifyToWakeup();
                Thread.currentThread().sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                stopQueue = true;
            }
        }
        try{
            externalQueueSemaphore.waitForNotify();
            externalRequestQueue.clear();
            backupExternalRequestQueue.clear();
            externalResponseQueue.clear();
            backupExternalResponseQueue.clear();
            operationResultsQueue.clear();
            backupOperationResultsQueue.clear();
            externalQueueSemaphore.notifyToWakeup();        
        } catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("QueueManager has been stopped ..."+this);

    }

    /**
     * Shutdown.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void shutdown(){
        try{
            externalQueueSemaphore.waitForNotify();
            stopQueue = true;
            externalQueueSemaphore.notifyToWakeup();  
        } catch (InterruptedException ex){
            ex.printStackTrace();
            stopQueue = true;
        }
    }
    
    
    /**
     * Manage queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @throws InterruptedException the interrupted exception
     */
    private void manageQueue() throws InterruptedException{
        System.out.println("QueueManager:" + this.toString() + " @"+System.currentTimeMillis());
        System.out.println("QueueManager:  Request Queue                     ResponseQueue                      ResultsQueue");
        if (!externalRequestQueue.isEmpty()) {
            Iterator mapIterator = externalRequestQueue.keySet().iterator();
            while (mapIterator.hasNext()) {
                OperationIdentifier entryId = (OperationIdentifier) mapIterator.next();
                if (entryId.getSource().equals(OperationIdentifier.SOURCETYPE.HANDLER)){
                      System.out.println("QueueManager:  "+entryId+":"+externalRequestQueue.get(entryId));
                }
            }
        } else if (!backupExternalRequestQueue.isEmpty()) {
            Iterator mapIterator = backupExternalRequestQueue.keySet().iterator();
            while (mapIterator.hasNext()) {
                OperationIdentifier entryId = (OperationIdentifier) mapIterator.next();
                if (entryId.getSource().equals(OperationIdentifier.SOURCETYPE.HANDLER)){
                      System.out.println("QueueManager:  "+entryId+":"+backupExternalRequestQueue.get(entryId));
                }
                if (!externalRequestQueue.containsKey(entryId) && backupExternalRequestQueue.containsKey(entryId)){
                    NTCIP2306Message message = backupExternalRequestQueue.get(entryId);
                    backupExternalRequestQueue.remove(entryId, message);
                    externalRequestQueue.put(entryId, message);
                }
            }
        }

        if (!externalResponseQueue.isEmpty()) {
            Iterator mapIterator = externalResponseQueue.keySet().iterator();
            while (mapIterator.hasNext()) {
                OperationIdentifier entryId = (OperationIdentifier) mapIterator.next();
                if (entryId.getSource().equals(OperationIdentifier.SOURCETYPE.HANDLER)){
                      System.out.println("QueueManager:                                    "+entryId+":"+externalResponseQueue.get(entryId));
                }
            }
        } else if (!backupExternalResponseQueue.isEmpty()) {
            Iterator mapIterator = backupExternalResponseQueue.keySet().iterator();
            while (mapIterator.hasNext()) {
                OperationIdentifier entryId = (OperationIdentifier) mapIterator.next();
                if (entryId.getSource().equals(OperationIdentifier.SOURCETYPE.HANDLER)){
                      System.out.println("QueueManager:                                    "+entryId+":"+backupExternalResponseQueue.get(entryId));
                }
                if (!externalResponseQueue.containsKey(entryId) && backupExternalResponseQueue.containsKey(entryId)){
                    NTCIP2306Message message = backupExternalResponseQueue.get(entryId);
                    backupExternalResponseQueue.remove(entryId, message);
                    externalResponseQueue.put(entryId, message);
                }                
            }
        }

        if (!operationResultsQueue.isEmpty()) {
            Iterator mapIterator = operationResultsQueue.keySet().iterator();
            while (mapIterator.hasNext()) {
                OperationIdentifier entryId = (OperationIdentifier) mapIterator.next();
                if (entryId.getSource().equals(OperationIdentifier.SOURCETYPE.HANDLER)){
                      System.out.println("QueueManager:                                                                       "+entryId+":"+operationResultsQueue.get(entryId));
                }
            }
        } else if (!backupOperationResultsQueue.isEmpty()) {
            Iterator mapIterator = backupOperationResultsQueue.keySet().iterator();
            while (mapIterator.hasNext()) {
                OperationIdentifier entryId = (OperationIdentifier) mapIterator.next();
                if (entryId.getSource().equals(OperationIdentifier.SOURCETYPE.HANDLER)){
                      System.out.println("QueueManager:                                                                       "+entryId+":"+backupOperationResultsQueue.get(entryId));
                }
                if (!operationResultsQueue.containsKey(entryId) && backupOperationResultsQueue.containsKey(entryId)){
                    OperationResults results = backupOperationResultsQueue.get(entryId);
                    backupOperationResultsQueue.remove(entryId, results);
                    operationResultsQueue.put(entryId, results);
                }                
            }
        }

    }

//
//  Implement Interfaces
//
    /**
 * Adds the to ext request queue.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param operation the operation
 * @param em the em
 */
public void addToExtRequestQueue(OperationIdentifier operation, NTCIP2306Message em) {
        if (stopQueue) return;
        
        try {
            externalQueueSemaphore.waitForNotify();
            if (externalRequestQueue.containsKey(operation)) {
                if (!backupExternalRequestQueue.containsKey(operation)) {
                    backupExternalRequestQueue.put(operation, em);
                } else backupExternalRequestQueue.replace(operation, em);
            } else {
                externalRequestQueue.put(operation, em);
            }
            externalQueueSemaphore.notifyToWakeup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks if is avaliable in request queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @return true, if is avaliable in request queue
     */
    public boolean isAvaliableInRequestQueue(OperationIdentifier operation) {
        boolean requestQueueStatus = false;
        if (stopQueue) return requestQueueStatus;
        try{
            externalQueueSemaphore.waitForNotify();
            requestQueueStatus= (externalRequestQueue.containsKey(operation)?true:false);
            externalQueueSemaphore.notifyToWakeup();
        } catch (InterruptedException ex){     
            ex.printStackTrace();
        }
        return requestQueueStatus;
    };

    /**
     * Gets the message from ext request queue.
     *
     * @param operation the operation
     * @return the message from ext request queue
     */
    public NTCIP2306Message getMessageFromExtRequestQueue(OperationIdentifier operation) {
        NTCIP2306Message returnMessage = null;
        while (returnMessage == null) {
            try {
                externalQueueSemaphore.waitForNotify();
                if (externalRequestQueue.containsKey(operation)) {
                    returnMessage = externalRequestQueue.get(operation);
                    externalRequestQueue.remove(operation);
                }
                externalQueueSemaphore.notifyToWakeup();
                if (returnMessage == null) {
                    Thread.currentThread().sleep(250);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnMessage;
    }

    /**
     * Gets the message from ext request queue.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the message from ext request queue
     */
    public NTCIP2306Message getMessageFromExtRequestQueue(OperationIdentifier operation, int maxWaitInMillis) {
        NTCIP2306Message returnMessage = null;
        long initialTime = System.currentTimeMillis();

        while ((returnMessage == null) && ((System.currentTimeMillis()-initialTime) < maxWaitInMillis)) {
            try {
                if (externalQueueSemaphore.waitForNotifyWithTimeout(maxWaitInMillis)) {
                    if (externalRequestQueue.containsKey(operation)) {
                        returnMessage = externalRequestQueue.get(operation);
                        externalRequestQueue.remove(operation);
                    }
                    externalQueueSemaphore.notifyToWakeup();
                }
                if (returnMessage == null) {
                    Thread.currentThread().yield();
                    Thread.currentThread().sleep(250);
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnMessage;
    }

    /**
     * Gets the message from ext request queue with interrupt.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the message from ext request queue with interrupt
     * @throws InterruptedException the interrupted exception
     */
    public NTCIP2306Message getMessageFromExtRequestQueueWithInterrupt(OperationIdentifier operation, int maxWaitInMillis) throws InterruptedException {
        NTCIP2306Message returnMessage = null;
        long initialTime = System.currentTimeMillis();

        while ((returnMessage == null) && ((System.currentTimeMillis()-initialTime) < maxWaitInMillis)) {
            try {
                if (externalQueueSemaphore.waitForNotifyWithTimeout(maxWaitInMillis)) {
                    if (externalRequestQueue.containsKey(operation)) {
                        returnMessage = externalRequestQueue.get(operation);
                        externalRequestQueue.remove(operation);
                    }
                    externalQueueSemaphore.notifyToWakeup();
                }
                if (returnMessage == null) {
                    Thread.currentThread().yield();
                    Thread.currentThread().sleep(250);
                }
            } catch (InterruptedException ex) {
                throw ex;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnMessage;
    }
    
    /**
     * Gets the message from ext request queue with interrupt.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @param waitMaxTime flag to indicate whether this routing should wait the maxWaitTime before reading from the queue.  This will allow additional times for scripting processes.
     * @return the message from ext request queue with interrupt
     * @throws InterruptedException the interrupted exception
     */
    public NTCIP2306Message getMessageFromExtRequestQueueWithInterruptAndWait(OperationIdentifier operation, int maxWaitInMillis, boolean waitMaxTime) throws InterruptedException {
        NTCIP2306Message returnMessage = null;
        long initialTime = System.currentTimeMillis();
        boolean waitTimeCompleted = false;

        while ((returnMessage == null) && ((System.currentTimeMillis()-initialTime) < maxWaitInMillis)) {
            if (waitMaxTime && !waitTimeCompleted){
                    Thread.currentThread().yield();
                    Thread.currentThread().sleep(maxWaitInMillis);                
            }
            try {
                if (externalQueueSemaphore.waitForNotifyWithTimeout(maxWaitInMillis)) {
                    if (externalRequestQueue.containsKey(operation)) {
                        returnMessage = externalRequestQueue.get(operation);
                        externalRequestQueue.remove(operation);
                    }
                    externalQueueSemaphore.notifyToWakeup();
                }
                if (returnMessage == null) {
                    Thread.currentThread().yield();
                    Thread.currentThread().sleep(250);
                }
            } catch (InterruptedException ex) {
                throw ex;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnMessage;
    }
    
    /**
     * Adds the to ext response queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operationName the operation name
     * @param em the em
     */
    public void addToExtResponseQueue(OperationIdentifier operationName, NTCIP2306Message em) {
        if (stopQueue) return;
        try {
            externalQueueSemaphore.waitForNotify();
            if (externalResponseQueue.containsKey(operationName)) {
                if (!backupExternalResponseQueue.containsKey(operationName)) {
                    backupExternalResponseQueue.put(operationName, em);
                } else backupExternalResponseQueue.replace(operationName, em);
            } else {
                externalResponseQueue.put(operationName, em);
            }
            externalQueueSemaphore.notifyToWakeup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks if is avaliable in response queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @return true, if is avaliable in response queue
     */
    public boolean isAvaliableInResponseQueue(OperationIdentifier operation){
        boolean responseQueueStatus = false;
        if (stopQueue) return responseQueueStatus;
        try{
            externalQueueSemaphore.waitForNotify();
            responseQueueStatus= (externalResponseQueue.containsKey(operation)?true:false);
            externalQueueSemaphore.notifyToWakeup();
        } catch (InterruptedException ex){     
            ex.printStackTrace();
        }
        return responseQueueStatus;
    };

    /**
     * Gets the message from ext response queue.
     *
     * @param operation the operation
     * @return the message from ext response queue
     */
    public NTCIP2306Message getMessageFromExtResponseQueue(OperationIdentifier operation) {
        NTCIP2306Message returnMessage = null;
        while (returnMessage == null) {
            try {
                externalQueueSemaphore.waitForNotify();
                if (externalResponseQueue.containsKey(operation)) {
                    returnMessage = externalResponseQueue.get(operation);
                    externalResponseQueue.remove(operation);
                }
                externalQueueSemaphore.notifyToWakeup();
                if (returnMessage == null) {
                    Thread.currentThread().yield();
                    Thread.currentThread().sleep(250);
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnMessage;
    }

    /**
     * Gets the message from ext response queue.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the message from ext response queue
     */
    public NTCIP2306Message getMessageFromExtResponseQueue(OperationIdentifier operation, int maxWaitInMillis) {
        NTCIP2306Message returnMessage = null;
        int loopCounter = 0;

        while ((returnMessage == null) && (loopCounter < maxWaitInMillis / 250)) {
            ++loopCounter;
            try {
                if (externalQueueSemaphore.waitForNotifyWithTimeout(maxWaitInMillis)) {
                    if (externalResponseQueue.containsKey(operation)) {
                        returnMessage = externalResponseQueue.get(operation);
                        externalResponseQueue.remove(operation);
                    }
                    externalQueueSemaphore.notifyToWakeup();
                }
                if (returnMessage == null) {
                    Thread.currentThread().sleep(250);
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnMessage;
    }

    /**
     * Gets the message from ext response queue with interrupt.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the message from ext response queue with interrupt
     * @throws InterruptedException the interrupted exception
     */
    public NTCIP2306Message getMessageFromExtResponseQueueWithInterrupt(OperationIdentifier operation, int maxWaitInMillis) throws InterruptedException{
        NTCIP2306Message returnMessage = null;
        int loopCounter = 0;

        while ((returnMessage == null) && (loopCounter < maxWaitInMillis / 250)) {
            ++loopCounter;
            try {
                if (externalQueueSemaphore.waitForNotifyWithTimeout(maxWaitInMillis)) {
                    if (externalResponseQueue.containsKey(operation)) {
                        returnMessage = externalResponseQueue.get(operation);
                        externalResponseQueue.remove(operation);
                    }
                    externalQueueSemaphore.notifyToWakeup();
                }
                if (returnMessage == null) {
                    Thread.currentThread().sleep(250);
                }

            } catch (InterruptedException ex) {
                throw ex;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnMessage;
    }
    
    /**
     * Adds the to operation results queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operationName the operation name
     * @param opResults the op results
     */
    public void addToOperationResultsQueue(OperationIdentifier operationName, OperationResults opResults) {
        if (stopQueue) return;
        try {
            externalQueueSemaphore.waitForNotify();
            if (operationResultsQueue.containsKey(operationName)) {
                if (!backupOperationResultsQueue.containsKey(operationName)){
                    backupOperationResultsQueue.put(operationName, opResults);
                } else backupOperationResultsQueue.replace(operationName, opResults);
            } else {
                operationResultsQueue.put(operationName, opResults);
            }
            externalQueueSemaphore.notifyToWakeup();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Checks if is avaliable in operation results queue.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @return true, if is avaliable in operation results queue
     */
    public boolean isAvaliableInOperationResultsQueue(OperationIdentifier operation){
        boolean operationResultsQueueStatus = false;
        if (stopQueue) return operationResultsQueueStatus;
        try{
            externalQueueSemaphore.waitForNotify();
            operationResultsQueueStatus= (operationResultsQueue.containsKey(operation)?true:false);
            externalQueueSemaphore.notifyToWakeup();
        } catch (InterruptedException ex){     
            ex.printStackTrace();
        }
        return operationResultsQueueStatus;
    };

    /**
     * Gets the results from operation results queue.
     *
     * @param operation the operation
     * @return the results from operation results queue
     */
    public OperationResults getResultsFromOperationResultsQueue(OperationIdentifier operation) {
        OperationResults returnOperation = null;
        while (returnOperation == null) {
            try {
                externalQueueSemaphore.waitForNotify();
                if (operationResultsQueue.containsKey(operation)) {
                    returnOperation = operationResultsQueue.get(operation);
                    operationResultsQueue.remove(operation);
                }
                externalQueueSemaphore.notifyToWakeup();
                if (returnOperation == null) {
                    Thread.currentThread().sleep(250);
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnOperation;
    }

    /**
     * Gets the results from operation results queue.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the results from operation results queue
     */
    public OperationResults getResultsFromOperationResultsQueue(OperationIdentifier operation, int maxWaitInMillis) {
        OperationResults returnOperation = null;
        int loopCounter = 0;

        while ((returnOperation == null) && (loopCounter < maxWaitInMillis / 250)) {
            ++loopCounter;
            try {
                if (externalQueueSemaphore.waitForNotifyWithTimeout(maxWaitInMillis)) {
                    if (operationResultsQueue.containsKey(operation)) {
                        returnOperation = operationResultsQueue.get(operation);
                        operationResultsQueue.remove(operation);
                    }
                    externalQueueSemaphore.notifyToWakeup();
                }
                if (returnOperation == null) {
                    Thread.currentThread().sleep(250);
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
                break;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnOperation;
    }

    /**
     * Gets the results from operation results queue with interrupt.
     *
     * @param operation the operation
     * @param maxWaitInMillis the max wait in millis
     * @return the results from operation results queue with interrupt
     * @throws InterruptedException the interrupted exception
     */
    public OperationResults getResultsFromOperationResultsQueueWithInterrupt(OperationIdentifier operation, int maxWaitInMillis) throws InterruptedException{
        OperationResults returnOperation = null;
        int loopCounter = 0;

        while ((returnOperation == null) && (loopCounter < maxWaitInMillis / 250)) {
            ++loopCounter;
            try {
                if (externalQueueSemaphore.waitForNotifyWithTimeout(maxWaitInMillis)) {
                    if (operationResultsQueue.containsKey(operation)) {
                        returnOperation = operationResultsQueue.get(operation);
                        operationResultsQueue.remove(operation);
                    }
                    externalQueueSemaphore.notifyToWakeup();
                }
                if (returnOperation == null) {
                    Thread.currentThread().sleep(250);
                }

            } catch (InterruptedException ex) {
                throw ex;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return returnOperation;
    }

}
