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
 * Perform an FTP GET operation as an OC.
 *
 * @author TransCore ITS,LLC
 * Last Upated: 9/12/2013
 * @jameleon.function name="FTP-GET-OC" type="action"
 * @jameleon.step Respond to the message get request
 */
public class FTPGetOCTag extends NTCIP2306FunctionTag {
   
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
        try {
            this.initializeReturnParameters();
            if ((SERVICENAME != null) && (PORTNAME != null) && (OPERATIONNAME != null)) {

                if (sessionTag.getTheCenterService() != null) {
                    boolean skipEncoding = (SKIPENCODING.toUpperCase().equals("TRUE") ? true : false);
                    NTCIP2306ControllerResults results = sessionTag.getTheCenterService().performFTPGetOC(SERVICENAME, PORTNAME, OPERATIONNAME, RESPONSEMESSAGE, skipEncoding);

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
            throw new JameleonScriptException("Error sending message GET request: '" + ex.getMessage(), this);
        }


    }
}
