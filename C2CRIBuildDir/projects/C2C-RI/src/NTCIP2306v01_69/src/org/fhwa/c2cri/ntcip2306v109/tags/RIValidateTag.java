/**
 * 
 */
package org.fhwa.c2cri.ntcip2306v109.tags;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306FunctionTag;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;

/**
 * Validates the requested message
 *
 * @author TransCore ITS,LLC
 * Last Updated: 9/12/2013
 * @jameleon.function name="ri-validate" type="action"
 * @jameleon.step Validate the requested message
 */
public class RIValidateTag extends NTCIP2306FunctionTag {

    /**
     * The name of the message to validate
     *        
     * @jameleon.attribute contextName="RIMessageName" required="true"
     */
    protected String messageName;
    /**
     * Future:  Flag indicating whether the message should be validated against the testConfig WSDL
     * or the Test Suite WSDL.
     *
     * @jameleon.attribute
     */
    protected boolean useTheProvidedWSDL;

    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {

        RIWSDL riWSDLInstance;
        try {
            MessageManager theManager = MessageManager.getInstance();
            Message newMessage = theManager.getMessage(messageName);
            String wsdlErrors="";
            String soapErrors="";
            String messageErrors="";

            riWSDLInstance = sessionTag.getWsdlInstance();

            if (!riWSDLInstance.isSchemaDocumentsExist() || !riWSDLInstance.isWsdlValidatedAgainstSchema()) {
                List<String> wsdlResults = riWSDLInstance.getWsdlErrors();

                for (String wsdlError : wsdlResults) {
                    log.debug("WSDL Error:   " + wsdlError);
                    wsdlErrors = wsdlErrors.concat(wsdlError + "\n");
                }
                throw new JameleonScriptException("The WSDL contains the following errors: \n" + wsdlErrors, this);
            } else {

                XMLValidator theValidator = new XMLValidator();

                Map<String, URL> schemaMap = riWSDLInstance.getWsdlSchemas();
                ArrayList<String> schemaList = new ArrayList();
                Iterator schemaIterator = schemaMap.values().iterator();
                while (schemaIterator.hasNext()) {
                    schemaList.add(((URL) schemaIterator.next()).getPath());
                }

                Thread.currentThread().setContextClassLoader( RIValidateTag.class.getClassLoader() );
                theValidator.setSchemaReferenceList(schemaList);


                String messageContent = new String(newMessage.getMessageBody());
               
                        if (!messageContent.isEmpty()) {

                            theValidator.isValidXML(messageContent);
                            theValidator.isXMLValidatedToSchema(messageContent);

                            if ((theValidator.getErrors().size() > 0)||(theValidator.getSchemaValidationValueErrorList().size()>0)||(theValidator.getSchemaValidationContentErrorList().size()>0)) {
                                int numberOfErrors = theValidator.getErrors().size()+theValidator.getSchemaValidationContentErrorList().size()+theValidator.getSchemaValidationValueErrorList().size();
                                if (numberOfErrors > 50) {
                                    messageErrors = "Printing the first 50 of " + numberOfErrors + " Errors Found.\n";
                                }
                                int ii = 0;
                                for (String theError : theValidator.getErrors()) {
                                    if (ii < 50) {
                                        messageErrors = messageErrors.concat(theError + "\n");
                                    }
                                    ii++;
                                }
                                for (String theError : theValidator.getSchemaValidationContentErrorList()) {
                                    if (ii < 50) {
                                        messageErrors = messageErrors.concat(theError + "\n");
                                    }
                                    ii++;
                                }
                                for (String theError : theValidator.getSchemaValidationValueErrorList()) {
                                    if (ii < 50) {
                                        messageErrors = messageErrors.concat(theError + "\n");
                                    }
                                    ii++;
                                }
                                throw new JameleonScriptException("Error Validating Message:  The following errors were found.\n" + messageErrors, this);
                            }

                        } else {
                            throw new JameleonScriptException("Error Validating Message:  There is no message content to validate.", this);
                        }


                }
 
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JameleonScriptException("Error validating the message: '" + ex.getMessage(), this);
        }

    }
}
