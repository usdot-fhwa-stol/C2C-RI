/**
 * 
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.TestStepTag;
import org.fhwa.c2cri.gui.VerificationDialog;
import org.fhwa.c2cri.infolayer.MessageValueTester;
import org.fhwa.c2cri.infolayer.ValueSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.plugin.c2cri.util.ValueSpecConverter;

/**
 * Verifies the message content based on the provided verificationSpecification.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  10/2/2013
 * @jameleon.function name="AUTO-VERIFY-MESSAGE" type="action"
 * @jameleon.step Allow the user to verify a message
 */
public class AutoVerifyMessageTag extends C2CRIFunctionTag {

    /**
     * The name of the message to create
     *  
     * @jameleon.attribute required="true"
     */
    protected String msgName;
    /**
     * The name of the message to create
     *
     * @jameleon.attribute required="true"
     */
    protected Object verificationSpec = null;

    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {

        MessageManager theManager = MessageManager.getInstance();
        try {
            boolean result = true;
            if (verificationSpec != null) {
                Message newMessage = theManager.getMessage(msgName);

                ValueSpecification thisSpecification = ValueSpecConverter.convertToValueSpec(verificationSpec);
                if (thisSpecification != null) {
                    if (newMessage.getMessageSpecification() != null) {
                        result = MessageValueTester.performValueTest(thisSpecification, newMessage.getMessageSpecification());
                        log.debug("Result of performValueTest = " + result + "\n ValueSpec was " + thisSpecification.toString());
                    } else {
                        log.debug("newMessage.getMessageSpecification associated with " + msgName + " was null.");
                    }
                } else {
                    log.debug("thisSpecification was = null");
                }

                if (!result) {
                    processError("The AutoVerification Specification result was false. \nResults: " + MessageValueTester.getErrorList());
                }

            } else {
                log.debug("verificationSpec = null");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new JameleonScriptException("AutoVerifyMessageTag: " + ex.getMessage(), this);
        }
    }
}
