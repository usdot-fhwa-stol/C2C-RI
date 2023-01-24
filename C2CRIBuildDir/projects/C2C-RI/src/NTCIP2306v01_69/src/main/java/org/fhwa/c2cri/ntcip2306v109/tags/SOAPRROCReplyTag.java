/**
 * 
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
 * Send the message get request
 *
 * @author TransCore ITS,LLC
 * Last Updated: 9/12/2013
 * @jameleon.function name="SOAP-RR-OC-REPLY" type="action"
 * @jameleon.step Respond to the message Post request
 */
public class SOAPRROCReplyTag extends NTCIP2306FunctionTag {

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
        log = Logger.getLogger(SOAPRROCReplyTag.class.getName());
        try {
            this.initializeReturnParameters();
            if ((SERVICENAME != null) && (PORTNAME != null) && (OPERATIONNAME != null)) {

                try {
                    boolean skipEncoding = ((SKIPENCODING!=null)&&SKIPENCODING.toUpperCase().equals("TRUE") ? true : false);
                    NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performSOAPRROCResponse(SERVICENAME, PORTNAME, OPERATIONNAME, RESPONSEMESSAGE, skipEncoding, false);

                    if (results != null) {

                        if (results.getResponseMessage() != null) {

                            Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(OPERATIONNAME, results.getResponseMessage());
                            responseMessage.setMessageName("RESPONSE");
                            MessageManager.getInstance().addMessage(responseMessage);
                            setVariable("TRANSPORTERRORRESULT", results.isTransportError());
                            setVariable("TRANSPORTERRORTYPE", results.getTransportErrorType());
                            setVariable("ENCODINGERRORRESULT", results.isEncodingError());
                            setVariable("ENCODINGERRORTYPE", results.getEncodingErrorType());
                            if (!results.isEncodingError()){
                                setVariable("ENCODINGERRORRESULT", results.isMessageValidationError());
                                setVariable("ENCODINGERRORTYPE", results.getMessageValidationErrorType());
                            }
                            setVariable("MESSAGEERRORRESULT", results.isMessageError());
                            setVariable("MESSAGEERRORTYPE", results.getMessageErrorType());

                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JameleonScriptException("Error sending message SOAP response: '" + ex.getMessage(), this);
        }
    }
}
