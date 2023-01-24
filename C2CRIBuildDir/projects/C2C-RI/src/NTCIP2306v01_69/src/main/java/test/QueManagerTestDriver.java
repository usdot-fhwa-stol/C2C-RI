/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.fhwa.c2cri.ntcip2306v109.http.server.NTCIP2306HTTPServer;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueManager;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;


/**
 * The Class QueManagerTestDriver.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class QueManagerTestDriver {

/** The Constant executor. */
static final ExecutorService executor = Executors.newCachedThreadPool();


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
        QueueManager queManager = new QueueManager();
        executor.submit(queManager);
        RIWSDL theWSDL = new RIWSDL("file:/c:/inout/tmddschemasv302/NTCIP2306Testing.wsdl");
        NTCIP2306HTTPServer theServer = new NTCIP2306HTTPServer((QueueController)queManager, theWSDL.getServiceSpecifications(),false);
        executor.submit(theServer);
        executor.shutdown();
    }
}
