/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import java.io.PrintWriter;
import java.io.StringWriter;
import net.sf.jameleon.exception.JameleonScriptException;
import net.sf.jameleon.function.TestStepTag;
import org.fhwa.c2cri.infolayer.VerificationDialog;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.infolayer.MessageNRTMList;
import org.fhwa.c2cri.testmodel.verification.UserVerificationStatus;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * Presents a dialog for the user to verify a message.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/5/2012
 * @jameleon.function name="USER-VERIFY-MESSAGE" type="action"
 * @jameleon.step Allow the user to verify a message
 */
public class UserVerifyMessageTag extends C2CRIFunctionTag {

    /** The name of the message to create. 
     * @jameleon.attribute required="true" */
    protected String msgName;
    
    /** The set of optional NRTM selections, data elements, instances. 
     * @jameleon.attribute required="false" */
    protected String optionList = "";
    
    /** The name/location of the test case data file. 
     * @jameleon.attribute required="false" */
    protected String userDataFile = "";

    /**
     * Process the input variables, present the user verification dialog and
     * process results.
     */
    public void testBlock() {
        boolean skipStep = UserVerificationStatus.getInstance().isSkipVerificatonSet();
        
        // Skip further processing if we are testing in an automated mode.
        if(RIParameters.getInstance().getParameterValue(RIParameters.RI_TESTING_PARAMETER_GROUP, RIParameters.RI_AUTOTEST_PARAMETER, "false").equalsIgnoreCase("true")) {
            System.out.println("TMDD UserVerifyMessageTag setting skipStep to true");
            skipStep = true;
        }


        if (!skipStep) {
            MessageManager theManager = MessageManager.getInstance();
            try {
                log.debug("Looking for Message: " + msgName);
                Message newMessage = theManager.getMessage(msgName);
                log.debug("newMessage returned with name = " + (newMessage == null ? "null" : newMessage.getMessageName()));
                if (newMessage != null) {
                    MessageNRTMList theList = new MessageNRTMList();
                    String[] nrtmList = optionList.split(";");
                    for (int ii = 0; ii < nrtmList.length; ii++) {
                        String[] nrtmListParts = nrtmList[ii].split(",");
                        if (nrtmListParts.length == 3) {
                            boolean setting = this.getVariableAsBoolean(nrtmListParts[0]);
                            System.out.println("UserVerifyMessageTag: " + setting + "  " + nrtmList[ii]);
                            theList.addItem(setting, nrtmListParts[1], nrtmListParts[2]);
                        }
                    }

                    newMessage.getMessageSpecification().updateSpecificationOptionalFlags(theList);


                    VerificationDialog thisDialog = new VerificationDialog(null, true);
                    thisDialog.setVerificationInstruction(this.getFunctionId());
                    thisDialog.setRawMessage(new String(newMessage.getMessageBody()));

                    thisDialog.setMessageSpecification(newMessage.getMessageSpecification());
                    thisDialog.setUserTCFile(userDataFile);
                    thisDialog.setVisible(true);

                    if (!thisDialog.isPassed()) {
                        throw new JameleonScriptException("The User Specification result was false.", this);
                    }

                    if (thisDialog.isSkipRemainingUserVerifications()) {
                        UserVerificationStatus.getInstance().skipVerification(true);
                    }
                } else {
                    throw new JameleonScriptException("No message was found with name " + msgName, this);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                StringWriter sw = null;
                PrintWriter pw = null;
                sw = new StringWriter();
                pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                log.debug(sw.toString());
                throw new JameleonScriptException("UserVerifyMessageTag: " + ex.getMessage(), this);
            }
        } else {
            if (this.parent instanceof TestStepTag) {
                TestStepTag thisTag = (TestStepTag) this.parent;
                thisTag.setSkipped(true);
            }
        }
    }
}
