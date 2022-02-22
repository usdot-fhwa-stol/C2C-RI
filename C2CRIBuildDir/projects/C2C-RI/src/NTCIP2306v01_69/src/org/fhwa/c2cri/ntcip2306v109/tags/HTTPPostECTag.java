/**
 *
 */
package org.fhwa.c2cri.ntcip2306v109.tags;


import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306FunctionTag;

/**
 * Perform a HTTP Post operation as an EC.
 *
 * @author TransCore ITS,LLC
 * Last Upated: 9/12/2013
 * @jameleon.function name="HTTP-POST-EC" type="action"
 * @jameleon.step Send the message POST request
 */
public class HTTPPostECTag extends NTCIP2306FunctionTag {

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
     * The message being sent as a request.
     *
     * @jameleon.attribute required="true"
     */
    protected String REQUESTMESSAGE;
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
        try {
            this.initializeReturnParameters();
            if ((SERVICENAME != null) && (PORTNAME != null) && (OPERATIONNAME != null)) {

                try {
                    boolean skipEncoding = (SKIPENCODING.toUpperCase().equals("TRUE") ? true : false);
                    NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performHTTPPostEC(SERVICENAME, PORTNAME, OPERATIONNAME,
                            REQUESTMESSAGE, skipEncoding);

                    if (results != null) {

                        Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(OPERATIONNAME, results.getRequestMessage());
                        requestMessage.setMessageName("REQUEST");
                        MessageManager.getInstance().addMessage(requestMessage);


                        if (results.getResponseMessage() != null) {
                            Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(OPERATIONNAME, results.getResponseMessage());
                            responseMessage.setMessageName("RESPONSE");
                            MessageManager.getInstance().addMessage(responseMessage);

                        }
                        setVariable("TRANSPORTERRORRESULT", results.isTransportError());
                        setVariable("TRANSPORTERRORTYPE", results.getTransportErrorType());
                        setVariable("ENCODINGERRORRESULT", results.isEncodingError());
                        setVariable("ENCODINGERRORTYPE", results.isEncodingError());
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
            throw new JameleonScriptException("Error sending message POST request: '" + ex.getMessage(), this);
        }
    }
}
