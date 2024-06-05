/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.tags;


import net.sf.jameleon.exception.JameleonScriptException;
import org.apache.logging.log4j.LogManager;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306FunctionTag;
/**
 * Perform an HTTP Get operation as an OC.
 *
 * @author TransCore ITS,LLC
 * Last Upated: 9/12/2013
 * @jameleon.function name="HTTP-GET-OC" type="action"
 * @jameleon.step Send the message get request
 */
public class HTTPGetOCTag extends NTCIP2306FunctionTag{
    /**
     * The name of the dialog associated with this get request
     *
     * @jameleon.attribute required="true"
     */
    protected String SERVICENAME;
    /**
     * The name of the dialog associated with this get request
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
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String RESPONSEMESSAGE;
    /**
     * A flag indicating whether the system should not apply encoding to the
     * response message provided.
     *
     * @jameleon.attribute
     */
    protected String SKIPENCODING = "FALSE";
    
    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {
        log = LogManager.getLogger(HTTPGetOCTag.class.getName());

        try {
            this.initializeReturnParameters();
            if ((SERVICENAME != null) && (PORTNAME != null) && (OPERATIONNAME != null)) {
               if (sessionTag.getTheCenterService() != null) {
                    boolean skipEncoding = (SKIPENCODING.toUpperCase().equals("TRUE") ? true : false);
                    NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performHTTPGetOC(SERVICENAME, PORTNAME, OPERATIONNAME, RESPONSEMESSAGE, skipEncoding);

                    if (results != null) {

                        if (results.getResponseMessage() != null) {

                            Message stdResponseMessage = C2CRIMessageAdapter.toC2CRIMessage(OPERATIONNAME, results.getResponseMessage());
                            MessageManager.getInstance().addMessage(stdResponseMessage);


                            Message requestMessage = MessageManager.getInstance().createMessage(OPERATIONNAME);
                            requestMessage.setMessageName("REQUEST");
                            MessageManager.getInstance().addMessage(requestMessage);

                            setVariable("TRANSPORTERRORRESULT", results.isTransportError());
                            setVariable("TRANSPORTERRORTYPE", results.getTransportErrorType());
                        } else {
                            Message stdRequestMessage = C2CRIMessageAdapter.toC2CRIMessage(OPERATIONNAME, results.getRequestMessage());

                            setVariable("TRANSPORTERRORRESULT", results.isTransportError());
                            setVariable("TRANSPORTERRORTYPE", results.getTransportErrorType());
                        }
                    }
                }

            }
 

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JameleonScriptException("Error receiving message GET request: '" + ex.getMessage(), this);
        }
    }    
}
