/**
 *
 */
package org.fhwa.c2cri.ntcip2306v109.tags;

import org.apache.log4j.Logger;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306FunctionTag;

/**
 * SOAP Message Publication for an OC
 *
 * @author TransCore ITS,LLC
 * Last Updated: 9/12/2013
 * @jameleon.function name="SOAP-PUB-OC" type="action"
 * @jameleon.step Send the message SOAP Publication
 */
public class SOAPPUBOCTag extends NTCIP2306FunctionTag {

    /**
     * The name of the service associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String PUBLICATIONSERVICENAME;
    /**
     * The name of the port associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String PUBLICATIONPORTNAME;
    /**
     * The name of the dialog associated with this publication
     *
     * @jameleon.attribute required="true"
     */
    protected String PUBLICATIONOPERATIONNAME;
    /**
     * The message being sent as a Subscription request.
     *
     * @jameleon.attribute required="true"
     */
    protected String PUBLICATIONMESSAGE;
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
        log = Logger.getLogger(SOAPPUBOCTag.class.getName());

        if ((PUBLICATIONSERVICENAME != null) && (PUBLICATIONPORTNAME != null) && (PUBLICATIONOPERATIONNAME != null)) {
            this.initializeReturnParameters();
            try {
                boolean skipEncoding = ((SKIPENCODING!=null)&&SKIPENCODING.toUpperCase().equals("TRUE") ? true : false);
                setVariable("CONTINUEPUBLICATION", false);
                NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performSOAPPUBOC(PUBLICATIONSERVICENAME, PUBLICATIONPORTNAME, PUBLICATIONOPERATIONNAME,
                        PUBLICATIONMESSAGE, skipEncoding);

                if (results != null && (results.getRequestMessage() != null)) {

                    Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(PUBLICATIONOPERATIONNAME, results.getRequestMessage());
                    requestMessage.setMessageName("REQUEST");
                    MessageManager.getInstance().addMessage(requestMessage);

                    if (results.getResponseMessage() != null) {
                        Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(PUBLICATIONOPERATIONNAME, results.getResponseMessage());
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
                    System.out.println("CONTINUEPUBLICATION being set to "+!results.isPublicationComplete());
                    setVariable("CONTINUEPUBLICATION", !results.isPublicationComplete());


                } else if (results != null) {
                    setVariable("CONTINUEPUBLICATION", false);
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

                } else {
                    setVariable("CONTINUEPUBLICATION", false);                    
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                setVariable("CONTINUEPUBLICATION", false);

            }

        } else {
            setVariable("CONTINUEPUBLICATION", false);
        }
    }
}
