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
 * @author TransCore ITS, LLC
 * Last Updated: 10/2/2012
 * 
 * @jameleon.function name="SOAP-SUB-EC" type="action"
 * @jameleon.step Send the message SOAP Subscription
 */
public class SOAPSUBECTag extends NTCIP2306FunctionTag {

    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String SUBSCRIPTIONSERVICENAME;
    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String SUBSCRIPTIONPORTNAME;
    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String SUBSCRIPTIONOPERATIONNAME;
    /**
     * The message being sent as a Subscription request.
     *
     * @jameleon.attribute required="true"
     */
    protected String SUBSCRIPTIONMESSAGE;
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
        log = Logger.getLogger(SOAPSUBECTag.class.getName());

        try {

            if ((SUBSCRIPTIONSERVICENAME != null) && (SUBSCRIPTIONPORTNAME != null) && (SUBSCRIPTIONOPERATIONNAME != null)) {
                this.initializeReturnParameters();
                try {
                    boolean skipEncoding = ((SKIPENCODING!=null)&&SKIPENCODING.toUpperCase().equals("TRUE") ? true : false);
                    NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performSOAPSUBEC(SUBSCRIPTIONSERVICENAME, SUBSCRIPTIONPORTNAME, SUBSCRIPTIONOPERATIONNAME,
                            SUBSCRIPTIONMESSAGE, skipEncoding);

                    if (results != null && (results.getRequestMessage() != null)) {

                        Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(SUBSCRIPTIONOPERATIONNAME, results.getRequestMessage());
                        requestMessage.setMessageName("REQUEST");
                        MessageManager.getInstance().addMessage(requestMessage);


                        if (results.getResponseMessage() != null) {
                            Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(SUBSCRIPTIONOPERATIONNAME, results.getResponseMessage());
                            responseMessage.setMessageName("RESPONSE");
                            MessageManager.getInstance().addMessage(responseMessage);

                        }
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
                        setVariable("CONTINUEPUBLICATION", !results.isPublicationComplete());

                    } else {
                        setVariable("CONTINUEPUBLICATION", false);

                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                    setVariable("CONTINUEPUBLICATION", false);

                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            setVariable("CONTINUEPUBLICATION", false);
            throw new JameleonScriptException("Error sending message SOAP Subscription request: '" + ex.getMessage(), this);
        }


    }
}
