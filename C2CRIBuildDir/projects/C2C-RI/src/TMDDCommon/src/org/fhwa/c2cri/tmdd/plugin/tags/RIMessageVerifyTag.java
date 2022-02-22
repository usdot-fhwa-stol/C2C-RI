/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import java.util.ArrayList;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.TestStepTag;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * Creates a new message of the specified type (Request, Response, Received)
 *
 * @author TransCore ITS, LLC
 * Last updated: 9/20/2013
 * @jameleon.function name="ri-MessageVerify" type="action"
 * @jameleon.step Verify the message content as specified
 */
public class RIMessageVerifyTag extends C2CRIFunctionTag {

    /**
     * The name of the element to verify
     *
     * @jameleon.attribute required="true"
     */
    protected String elementName;

    /**
     * The alternate name of the element to verify
     *  
     * @jameleon.attribute
     */
    protected String alternateElementName;
    
    /**
     * The type of the message to verify against with (REQUEST, RESPONSE,
     * RECEIVED)
     *
     * @jameleon.attribute required="true"
     */
    protected String msgType;
    /**
     * The name of the instance to verify against
     *
     * @jameleon.attribute required="true"
     */
    protected String instanceName;
    /**
     * The value to which the element should be set to.
     *
     * @jameleon.attribute
     */
    protected String instanceValue;
    /**
     * The value which indicates whether the element being verified is a message, data-frame or data-element.
     *
     * @jameleon.attribute
     */
    protected String elementType = null;

     /**
     * The alternative value to which the element should be set to.
     *
     * @jameleon.attribute
     */
    protected String alternateInstanceValue=null;
    
    /**
     * Test block.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void testBlock() {
        MessageManager theManager = MessageManager.getInstance();
        System.out.println("RIMessageVerifyTag::testBlock MessageManger = " + theManager + "\n");
        try {
            Message newMessage = theManager.getMessage(msgType);

            if (newMessage == null) {
                throw new Exception("The requested message " + msgType + " is null");
            }

            MessageSpecification newMessageSpec = newMessage.getMessageSpecification();
            log.debug("Size of the message specification to be inspected = " + newMessage.getMessageSpecification().getMessageSpec().size());

            if (elementType.equalsIgnoreCase("data-element")) {
                // Add the capability for multiple data elements to be specified and perform
                // an "or" test to see if either of them exists in the messageSpec.
                if (elementName.split(",").length > 1) {
                    if (!newMessageSpec.verifyExistsInEveryInstance(elementName.split(","), instanceName)) {
                        String errorsFound = "";
                        for (String thisError : newMessageSpec.getErrorsFound()) {
                            if (!errorsFound.contains(thisError))
                                errorsFound = errorsFound.concat(thisError + "\n");
                        }
                        newMessageSpec.clearErrorsFound();
                        processError(errorsFound);
                    }
                } else {
                    if (!newMessageSpec.verifyExistsInEveryInstance(elementName, instanceName)) {
                        String errorsFound = "";
                        for (String thisError : newMessageSpec.getErrorsFound()) {
                            errorsFound = errorsFound.concat(thisError + "\n");
                        }
                        if (alternateElementName != null) {
                            errorsFound = "";
                            if (!newMessageSpec.verifyExistsInEveryInstance(elementName, alternateElementName, instanceName)) {
                                for (String thisError : newMessageSpec.getErrorsFound()) {
                                    errorsFound = errorsFound.concat(thisError + "\n");
                                }
                                newMessageSpec.clearErrorsFound();
                                processError(errorsFound);
                            }
                        } else {
                            newMessageSpec.clearErrorsFound();
                            processError(errorsFound);
                        }
                    }

                    if ((instanceValue != null) && (!instanceValue.isEmpty())) {
                        if (!instanceValue.equals(newMessageSpec.getElementValue(instanceName + "." + elementName))) {
                            String errorsFound = "Element " + elementName + " was set to " + newMessageSpec.getElementValue(elementName) + " instead of " + instanceValue;
                            if (alternateInstanceValue != null) {
                                if (!alternateInstanceValue.equals(newMessageSpec.getElementValue(elementName))) {
                                    errorsFound = errorsFound.concat(" or " + alternateInstanceValue + ".");
                                    processError(errorsFound);
                                }
                            } else {
                                errorsFound = errorsFound.concat(".");
                                processError(errorsFound);
                            }
                        }
                    }
                }
            }

            if (elementType.equalsIgnoreCase("message")) {
                ArrayList<String> messagesPresent = newMessageSpec.getMessageTypes();
                boolean messageFound = false;
                for (String message : messagesPresent) {
                    // If message contains a prefix, then strip it.
                    message = message.substring(message.indexOf(":") + 1);
                    if (message.equals(elementName)) {
                        messageFound = true;
                    }
                }
                if (!messageFound) {
                    String errorsFound = "Message " + elementName + " was not present.";
                    processError(errorsFound);
                }

            } else if (elementType.equalsIgnoreCase("data-frame")) {
                if (!newMessageSpec.verifyExistsInEveryInstance(elementName, instanceName)) {
                    String errorsFound = "";
                    for (String thisError : newMessageSpec.getErrorsFound()) {
                        errorsFound = errorsFound.concat(thisError + "\n");
                    }
                    newMessageSpec.clearErrorsFound();
                    processError(errorsFound);                    
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            processError("Message Verify Error: " + ex.getMessage());
        }
        
    }
    
}
