/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.messaging;

import org.fhwa.c2cri.ntcip2306v109.operations.OperationResultsQueueController;


/**
 * The Interface QueueController ensures the functions provided by the RequestQueue, ResponseQueue and OperationResultsQueue are available through a single interface.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface QueueController extends RequestQueueController, 
                                                 ResponseQueueController,
                                                 OperationResultsQueueController{

}
