/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.ftp.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueManager;
import org.fhwa.c2cri.applayer.NamedThreadFactory;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.ntcip2306v109.wsdl.ServiceSpecCollection;



/**
 * The Class FTPClientController creates and manages all ftp clients for each unique FTP path specified in the WSDL file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class FTPClientController implements Runnable {

    /** The queue controller. */
    QueueController queueController;
    
    /** The services. */
    ServiceSpecCollection services = new ServiceSpecCollection();
    
    /** The executor. */
    final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("FTPClientController"));
    
    /** The transport list. */
    private ArrayList<FTPClientTransport> transportList = new ArrayList();
    
    /** The ready signal. */
    private final CountDownLatch readySignal;
    
    /** The initialization error encountered. */
    private volatile boolean initializationErrorEncountered = false;
    
//    static final ExecutorService executor = Executors.newCachedThreadPool();
    /**
 * Instantiates a new fTP client controller.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param queueController the queue controller
 * @param services the services
 * @param readySignal the ready signal
 */
public FTPClientController(final QueueController queueController, ServiceSpecCollection services, CountDownLatch readySignal) {
        this.queueController = queueController;
        this.services = services;
        this.readySignal = readySignal;
    }

    /**
     * Instantiates a new fTP client controller.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param queueController the queue controller
     * @param services the services
     */
    public FTPClientController(final QueueController queueController, ServiceSpecCollection services) {
        this.queueController = queueController;
        this.services = services;
        this.readySignal = null;
    }

    /**
     * Instantiates a new fTP client controller.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param queueController the queue controller
     */
    public FTPClientController(QueueController queueController) {
        this.queueController = queueController;
        this.readySignal = null;
    }

    /**
     * Instantiates a new fTP client controller.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public FTPClientController() {
        this.queueController = null;
        this.readySignal = null;
    }

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {

        RIWSDL theWSDL = new RIWSDL("file:/c:/inout/tmddschemasv302/NTCIP2306Testing.wsdl");
        QueueManager queManager = new QueueManager();

        FTPClientController theServer = new FTPClientController(queManager, theWSDL.getServiceSpecifications());
        theServer.run();

    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

        HashMap<String, OperationSpecCollection> opMap = services.getClientSpecsByLocation();
        Iterator locationsIterator = opMap.keySet().iterator();
        while (locationsIterator.hasNext()) {
            try {
                String location = (String) locationsIterator.next();
                FTPClientTransport ftpClient = new FTPClientTransport(location, queueController, opMap.get(location));
                transportList.add(ftpClient);
                try {
                    System.out.println("FTPClientController::run  - About to Initialize FTPClient. for location "+location);
                    ftpClient.initialize();
                    System.out.println("FTPClientController::run Initialization complete!!");
                } catch (Exception ex) {
                    initializationErrorEncountered = true;
                    ex.printStackTrace();
                }
                executor.submit(ftpClient);

            } catch (Exception ex) {
                initializationErrorEncountered = true;
                ex.printStackTrace();
            }
        }
        if (readySignal != null)readySignal.countDown();
    }
    
    /**
     * Shutdown.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void shutdown(){
        for (FTPClientTransport thisClient : transportList){
            try{
            thisClient.shutdown();
            } catch (Exception ex){
                ex.printStackTrace();
            }
                
        }
        executor.shutdown();
    }
    
    /**
     * Checks if is initialization completed.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is initialization completed
     */
    public boolean isInitializationCompleted(){
        return !initializationErrorEncountered;
    }
}
