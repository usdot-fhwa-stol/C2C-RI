/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.tags;


import net.sf.jameleon.exception.JameleonScriptException;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306FunctionTag;
/**
 * Perform an HTTP Get Operation as an EC.
 *
 * @author TransCore ITS,LLC
 * Last Upated: 9/12/2013
 * @jameleon.function name="HTTP-GET-EC" type="action"
 * @jameleon.step Send the message get request
 */
public class HTTPGetECTag extends NTCIP2306FunctionTag{
    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String SERVICENAME;
    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String PORTNAME;
    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String OPERATIONNAME;


    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {
        log = Logger.getLogger(HTTPGetECTag.class.getName());
        try {
            this.initializeReturnParameters();
            if ((SERVICENAME != null) && (PORTNAME != null) && (OPERATIONNAME != null)) {
                NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performHTTPGetEC(SERVICENAME, PORTNAME, OPERATIONNAME);
                if (results != null) {
                    if (results.getResponseMessage() != null) {
                        Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(OPERATIONNAME, results.getResponseMessage());
                        responseMessage.setMessageName("RESPONSE");
                        MessageManager.getInstance().addMessage(responseMessage);
                        setVariable("TRANSPORTERRORRESULT", results.isTransportError());
                        setVariable("TRANSPORTERRORTYPE", results.getTransportErrorType());
                        setVariable("ENCODINGERRORRESULT", results.isEncodingError());
                        setVariable("ENCODINGERRORTYPE", results.getEncodingErrorType());
                        setVariable("MESSAGEERRORRESULT", results.isMessageError());
                        setVariable("MESSAGEERRORTYPE", results.getMessageErrorType());


                    }
                }
            }
                
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JameleonScriptException("Error sending message GET request: '" + ex.getMessage(), this);
        }
    }
    
}
