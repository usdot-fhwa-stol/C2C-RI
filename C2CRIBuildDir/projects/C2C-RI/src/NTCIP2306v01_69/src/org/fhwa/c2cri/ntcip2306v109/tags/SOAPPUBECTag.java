/**
 * 
 */
package org.fhwa.c2cri.ntcip2306v109.tags;

import org.apache.logging.log4j.LogManager;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.C2CRIMessageAdapter;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306FunctionTag;
import org.ntcip.c2CMessageAdministration.C2CMessageReceiptDocument;

/**
 * SOAP Message Publication for an EC.
 *
 * @author TransCore ITS,LLC
 * Last Updated: 9/12/2013
 * @jameleon.function name="SOAP-PUB-EC" type="action"
 * @jameleon.step Receive the SOAP Subscription request
 */
public class SOAPPUBECTag extends NTCIP2306FunctionTag {

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
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String PUBLICATIONOPERATIONNAME;
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
        log = LogManager.getLogger(SOAPPUBECTag.class.getName());

        String responseMsg = "";

        try {
            this.initializeReturnParameters();
            if ((PUBLICATIONSERVICENAME != null) && (PUBLICATIONPORTNAME != null) && (PUBLICATIONOPERATIONNAME != null)) {
                setVariable("CONTINUEPUBLICATION", false);
                NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performSOAPPUBECRequest(PUBLICATIONSERVICENAME, PUBLICATIONPORTNAME, PUBLICATIONOPERATIONNAME);

                if (results != null) {
                    if (results.getRequestMessage() != null) {
                        Message requestMessage = C2CRIMessageAdapter.toC2CRIMessage(PUBLICATIONOPERATIONNAME, results.getRequestMessage());
                        requestMessage.setMessageName("REQUEST");
                        MessageManager.getInstance().addMessage(requestMessage);


                        C2CMessageReceiptDocument receiptDoc = C2CMessageReceiptDocument.Factory.newInstance();
                        receiptDoc.addNewC2CMessageReceipt();
                        receiptDoc.getC2CMessageReceipt().setInformationalText("The Publication was Handled. Result:" + getVariable("MESSAGEERRORTYPE"));
                        responseMsg = receiptDoc.xmlText();


                        boolean skipEncoding = ((SKIPENCODING!=null)&&SKIPENCODING.toUpperCase().equals("TRUE") ? true : false);
                        results = sessionTag.getTheCenterService().performSOAPPUBECResponse(PUBLICATIONSERVICENAME, PUBLICATIONPORTNAME, PUBLICATIONOPERATIONNAME, responseMsg, skipEncoding, false);
                        Message responseMessage = C2CRIMessageAdapter.toC2CRIMessage(PUBLICATIONOPERATIONNAME, results.getResponseMessage());
                        responseMessage.setMessageName("RESPONSE");
                        MessageManager.getInstance().addMessage(responseMessage);

                        System.out.println("CONTINUEPUBLICATION being set to "+!results.isPublicationComplete());
                        setVariable("CONTINUEPUBLICATION", !results.isPublicationComplete());

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
  
                    
                }

            } else {
                setVariable("CONTINUEPUBLICATION", false);                
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            setVariable("CONTINUEPUBLICATION", false);
            log.debug("SOAPPUBECTAG failed with Exception : "+ex.getMessage());


        }

    }
}
