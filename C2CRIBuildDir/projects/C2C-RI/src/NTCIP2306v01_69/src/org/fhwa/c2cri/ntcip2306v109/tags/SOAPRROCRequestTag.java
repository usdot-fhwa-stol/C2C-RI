/**
 * 
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
 * Send the message get request
 *
 * @author TransCore ITS,LLC
 * Last Updated: 9/12/2013
 * @jameleon.function name="SOAP-RR-OC-REQUEST" type="action"
 * @jameleon.step Receive the SOAP request
 */
public class SOAPRROCRequestTag extends NTCIP2306FunctionTag {

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
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {
        log = LogManager.getLogger(SOAPRROCRequestTag.class.getName());

        try {
            this.initializeReturnParameters();
            if ((SERVICENAME != null) && (PORTNAME != null) && (OPERATIONNAME != null)) {
                try {
                    NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performSOAPRROCRequest(SERVICENAME, PORTNAME, OPERATIONNAME);

                    if (results != null) {

                        Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(OPERATIONNAME, results.getRequestMessage());
                        requestMessage.setMessageName("REQUEST");
                        MessageManager.getInstance().addMessage(requestMessage);

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
                } catch (Exception ex) {
                    ex.printStackTrace();

                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JameleonScriptException("Error receiving message SOAP request: '" + ex.getMessage(), this);
        }
    }
}
