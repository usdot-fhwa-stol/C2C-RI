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
import org.ntcip.c2CMessageAdministration.C2CMessageReceiptDocument;

/**
 * Send the message get request.
 *
 * @author
 * @jameleon.function name="SOAP-SUB-OC" type="action"
 * @jameleon.step Receive the SOAP Subscription request
 */
public class SOAPSUBOCTag extends NTCIP2306FunctionTag {

    /**
     * The name of the dialog associated with this get request
     *
     * @jameleon.attribute required="true"
     */
    protected String SUBSCRIPTIONSERVICENAME;
    /**
     * The name of the dialog associated with this get request
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
        log = LogManager.getLogger(SOAPSUBOCTag.class.getName());

        String responseMsg = "";

        try {
            this.initializeReturnParameters();
            if ((SUBSCRIPTIONSERVICENAME != null) && (SUBSCRIPTIONPORTNAME != null) && (SUBSCRIPTIONOPERATIONNAME != null)) {
                setVariable("CONTINUEPUBLICATION", false);
                NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performSOAPSUBOCRequest(SUBSCRIPTIONSERVICENAME, SUBSCRIPTIONPORTNAME, SUBSCRIPTIONOPERATIONNAME);

                if (results != null) {
                    if (results.getRequestMessage() != null) {
                        Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(SUBSCRIPTIONOPERATIONNAME, results.getRequestMessage());
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


                        C2CMessageReceiptDocument receiptDoc = C2CMessageReceiptDocument.Factory.newInstance();
                        receiptDoc.addNewC2CMessageReceipt();
                        receiptDoc.getC2CMessageReceipt().setInformationalText("The Subscription was Handled. Result:" + getVariable("MESSAGEERRORTYPE"));
                        responseMsg = receiptDoc.xmlText();


                        boolean skipEncoding = ((SKIPENCODING!=null)&&SKIPENCODING.toUpperCase().equals("TRUE") ? true : false);
                        results = sessionTag.getTheCenterService().performSOAPSUBOCResponse(SUBSCRIPTIONSERVICENAME, SUBSCRIPTIONPORTNAME, SUBSCRIPTIONOPERATIONNAME, responseMsg, skipEncoding, false);
                        Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(SUBSCRIPTIONOPERATIONNAME, results.getResponseMessage());
                        responseMessage.setMessageName("RESPONSE");
                        MessageManager.getInstance().addMessage(responseMessage);

                        setVariable("TRANSPORTERRORRESULT", results.isTransportError());
                        setVariable("TRANSPORTERRORTYPE", results.getTransportErrorType());
                        setVariable("CONTINUEPUBLICATION", !results.isPublicationComplete());

                    }
                }

            }



        } catch (Exception ex) {
            ex.printStackTrace();
            setVariable("CONTINUEPUBLICATION", false);
            throw new JameleonScriptException("Error receiving message SOAP Subscription request: '" + ex.getMessage(), this);
        }

    }
}
