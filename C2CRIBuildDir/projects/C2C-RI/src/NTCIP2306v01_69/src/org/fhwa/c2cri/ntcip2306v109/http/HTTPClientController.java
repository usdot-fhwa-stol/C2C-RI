/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.http;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.applayer.NamedThreadFactory;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.ServiceSpecCollection;

/**
 * The Class HTTPClientController creates and monitors an HTTP Client Handler for each HTTP URI specified in the WSDL.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class HTTPClientController implements Runnable {

    /** The queue controller. */
    QueueController queueController;
    
    /** The services. */
    ServiceSpecCollection services = new ServiceSpecCollection();
    
    /** The executor. */
    final ExecutorService executor = Executors.newCachedThreadPool(new NamedThreadFactory("HTTPClientController"));
    
    /** The ready signal. */
    private final CountDownLatch readySignal;
    
    /** The client handler list. */
    ArrayList<HTTPClientHandler> clientHandlerList = new ArrayList();

 /**
 * Instantiates a new hTTP client controller.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param queueController the queue controller
 * @param services the services
 * @param readySignal the ready signal
 */
public HTTPClientController(final QueueController queueController, ServiceSpecCollection services, CountDownLatch readySignal) {
        this.queueController = queueController;
        this.services = services;
        this.readySignal = readySignal;
    }

    /**
     * Instantiates a new hTTP client controller.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param queueController the queue controller
     * @param services the services
     */
    public HTTPClientController(final QueueController queueController, ServiceSpecCollection services) {
        this.queueController = queueController;
        this.services = services;
        this.readySignal = null;
    }

    /**
     * Instantiates a new hTTP client controller.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param queueController the queue controller
     */
    public HTTPClientController(QueueController queueController) {
        this.queueController = queueController;
        this.readySignal = null;
    }

    /**
     * Instantiates a new hTTP client controller.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public HTTPClientController() {
        this.queueController = null;
        this.readySignal = null;
    }


    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run() {

        HashMap<String, OperationSpecCollection> opMap = services.getClientSpecsByLocation();
        System.out.println("HTTPClientController::run Number of Service Locations found = " + opMap.size());
        Iterator locationsIterator = opMap.keySet().iterator();
        int clientsCreated = 0;
        while (locationsIterator.hasNext()) {
            try {
                String location = (String) locationsIterator.next();
                URL tmpURL = new URL(location);
                if (tmpURL.getProtocol().equalsIgnoreCase("http") || tmpURL.getProtocol().equalsIgnoreCase("https")) {
                    HTTPClientHandler httpClient1 = new HTTPClientHandler(location, queueController, opMap.get(location));
                    try {
                        System.out.println("HTTPClientController::run  - About to Initialize HTTPClient. for location " + location);
                        httpClient1.initialize();
                        clientHandlerList.add(httpClient1);
                        clientsCreated++;
                        System.out.println("HTTPClientController::run Initialization complete!!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    executor.submit(httpClient1);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (readySignal != null) {
            readySignal.countDown();
        }
        System.out.println("HTTPClientController Created " + clientsCreated + " Client Controller Threads!!");
    }

    /**
     * Shutdown.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void shutdown() {
        System.out.println("HTTPClientController:shutdown will stop " + clientHandlerList.size() + " Client Controller Threads!!");
        for (HTTPClientHandler thisHandler : clientHandlerList) {
            try {
                if (thisHandler != null) {
                    thisHandler.shutdown();
                    Thread.currentThread().sleep(100);
                }
            } catch (Exception ex) {
				if (ex instanceof InterruptedException)
					Thread.currentThread().interrupt();	
                ex.printStackTrace();
            }
        }
        executor.shutdownNow();
    }
}
