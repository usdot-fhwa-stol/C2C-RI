/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.operations;

import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message.MESSAGETYPE;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;



/**
 * The Class OperationResults captures the results of an operation performed between centers.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class OperationResults {

    /** The operation specification. */
    private OperationSpecification operationSpecification;
    
    /** The request message. */
    private NTCIP2306Message requestMessage;
    
    /** The response message. */
    private NTCIP2306Message responseMessage;


    /**
     * Instantiates a new operation results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param request the request
     * @param response the response
     */
    public OperationResults(OperationSpecification opSpec, NTCIP2306Message request, NTCIP2306Message response){
        this.operationSpecification = opSpec;
        this.requestMessage = request;
        this.responseMessage = response;
        
        OperationResultsMonitor orm = OperationResultsMonitor.getInstance();
        orm.addOperationResult(this);       
    }

    /**
     * Gets the operation specification.
     *
     * @return the operation specification
     */
    public OperationSpecification getOperationSpecification() {
        return operationSpecification;
    }

    /**
 * Gets the request message.
 *
 * @return the request message
 */
public NTCIP2306Message getRequestMessage() {
        return requestMessage;
    }


    /**
 * Gets the response message.
 *
 * @return the response message
 */
public NTCIP2306Message getResponseMessage() {
        return responseMessage;
    }


    /**
 * Verify operation results.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @throws Exception the exception
 */
public void verifyOperationResults() throws Exception{

        if (this.operationSpecification.isGetOperation()&&(getResponseMessage()!=null)){
            // The response message is specified as the REQUEST in the WSDL
            getResponseMessage().verifyMessageToOpSpec(operationSpecification, MESSAGETYPE.REQUEST);            
        } else if ((getRequestMessage() != null) && (getResponseMessage()!=null)){
           // Check the Input/Response Message
            getRequestMessage().verifyMessageToOpSpec(operationSpecification, MESSAGETYPE.REQUEST);
           // Check the Output/Response Message
            getResponseMessage().verifyMessageToOpSpec(operationSpecification, MESSAGETYPE.RESPONSE);
        } else {
            throw new NTCIP2306OperationException("OperationResults::verifyOperationResults One of the required message types (Request/Response) was null.");
        }

    }
}
