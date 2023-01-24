/**
 *
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import javax.swing.JOptionPane;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.infolayer.InformationLayerController;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.tmdd.TMDDErrorResponseProcessor;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * Send the message request
 *
 * @author TransCore ITS
 * Last Updated 9/2/2013
 * @jameleon.function name="SUBSCRIPTION-OC-RECEIVE" type="action"
 * @jameleon.step Receive the message request
 */
public class SubscriptionOCReceiveTag extends C2CRIFunctionTag {

    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String Dialog;
    /**
     * A flag which indicates whether a error response is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String AUTHENTICATIONCHECK;
    /**
     * A flag which indicates whether a error response is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String SUBSCRIPTIONERRORRESPONSEEXPECTED;
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String SUBSCRIPTIONERRORTYPEEXPECTED;
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String USERNAME;
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String PASSWORD;
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String OPERATORID;
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String ALLOWACCESS;
    /**
     * The flag for whether there is valid data available
     *
     * @jameleon.attribute required="false" default="True"
     */
    protected String DATAVALID;
    /**
     * The time in milliseconds within which an owner center has to send a
     * response
     *
     * @jameleon.attribute required="false" default="60000";
     */
    protected String RESPONSETIMEREQUIRED;
    /**
     * The minimum time and maximum time (separated by a ":") in seconds for
     * which an owner center supports periodic publications.
     *
     * @jameleon.attribute required="false" default="1:31536000";
     */
    protected String PERIODICUPDATERANGE;
    private InformationLayerController theInformationLayerController;

    public void testBlock() {
        if (RESPONSETIMEREQUIRED == null) {
            RESPONSETIMEREQUIRED = "60000";
        }
        try {
            setVariable("CONTINUEPUBLICATION", false);

            theInformationLayerController = sessionTag.getTheInformationLayerStandard().getInformationLayerController();

//            JOptionPane.showMessageDialog(null, "Click the OK button when ready to proceed with SUBSCRIPTION-OC-RECEIVE.", "REQUEST-RESPONSE-OC-RECEIVE", JOptionPane.INFORMATION_MESSAGE);

            InformationLayerOperationResults infoResults = theInformationLayerController.performSubscriptionOCReceive(Dialog);
            Message requestMessage = infoResults.getRequestMessage();
            requestMessage.setMessageName("REQUEST");
            MessageManager theManager = MessageManager.getInstance();
            theManager.addMessage(requestMessage, true);
            DialogResults results = infoResults.getDialogResults(Message.MESSAGETYPE.Request);
//            log.info(requestMessage.toXML());
            
            // Verify any Periodic Update Settings within the received Subscription.            
            boolean periodicRangeError = false;
            // Check to confirm that periodic update subscriptions include a period
            // parameter that is within the range supported by this center.
                     String[] periodicRangeValues = PERIODICUPDATERANGE != null ? PERIODICUPDATERANGE.split(":") : "300:31536000".split(":");
            System.out.println("SubscriptionOCReceiveTag::PERIODICUPDATERANGE = "+PERIODICUPDATERANGE+"\n periodicRangeValue.Length = "+periodicRangeValues.length);
            if (periodicRangeValues.length == 2) {
                long minRangeValue = Long.valueOf(periodicRangeValues[0]);
                long maxRangeValue = Long.valueOf(periodicRangeValues[1]);
                System.out.println("SubscriptionOCReceiveTag::SubscriptionFrequency was = " + infoResults.getSubscriptionPeriodicFrequency()+
                                "\n minRangeValue = "+ minRangeValue + "\n maxRangeValue = "+maxRangeValue);
                // The publication frequency has to be 1 or greater to be valid
                if (infoResults.getSubscriptionPeriodicFrequency() > 0) {
                    if ((infoResults.getSubscriptionPeriodicFrequency() < minRangeValue)
                            || infoResults.getSubscriptionPeriodicFrequency() > maxRangeValue) {
                        periodicRangeError = true;
                        System.out.println("SubscriptionOCReceiveTag::periodicRangeError set to true.");
                    } else {
                        System.out.println("SubscriptionOCReceiveTag::periodicRangeError set to false.\n"+
                                "SubscriptionFrequency was = " + infoResults.getSubscriptionPeriodicFrequency()+
                                "\n minRangeValue = "+ minRangeValue + "\n maxRangeValue = "+maxRangeValue);
                    }
                }
            }

            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            if (errorProcessor.requestContainsError(results, AUTHENTICATIONCHECK, ALLOWACCESS,
                    SUBSCRIPTIONERRORRESPONSEEXPECTED, SUBSCRIPTIONERRORTYPEEXPECTED, DATAVALID, USERNAME, PASSWORD, OPERATORID, this.getTestCaseTag().getTestCase().getTestCaseId(), requestMessage, periodicRangeError)) {

                Message errorResponseMessage = errorProcessor.getErrorResponseMsg(Dialog);
                InformationLayerOperationResults errorOpResults = theInformationLayerController.performSubscriptionOCResponse(Dialog, errorResponseMessage, true);
                errorResponseMessage.setMessageName("RESPONSE");
//                log.info(errorResponseMessage.toXML());

                if (SUBSCRIPTIONERRORRESPONSEEXPECTED.equalsIgnoreCase("true")) {

                    if (SUBSCRIPTIONERRORTYPEEXPECTED.equals(errorProcessor.getErrorType()) || (SUBSCRIPTIONERRORTYPEEXPECTED.equals(errorProcessor.getErrorTypeText()))) {
                        setVariable("SUBSCRIPTION-OC-RECEIVE-ERROR", false);
                        // OK
                    } else {
                        setVariable("SUBSCRIPTION-OC-RECEIVE-ERROR", true);
                        processError("An error response was sent with error code " + errorProcessor.getErrorType() + "-"
                                + errorProcessor.getErrorTypeText() + " while ErrorTypeExpected was set to " + SUBSCRIPTIONERRORTYPEEXPECTED + ".");
                    }

                } else {
                    setVariable("SUBSCRIPTION-OC-RECEIVE-ERROR", true);
                    processError("An error response was sent when ErrorResponseExpected was set to " + SUBSCRIPTIONERRORRESPONSEEXPECTED + "." + "  The error encountered was " + errorProcessor.getErrorText());
                }
                long responseTime = errorOpResults.getResponseMessage().getTransportTimeInMillis() - errorOpResults.getRequestMessage().getTransportTimeInMillis();
                long requiredTime = Long.parseLong(RESPONSETIMEREQUIRED);
                String pathValue = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_PATH_DELAY_PARAMETER(),
                        TMDDSettingsImpl.getTMDD_PATH_DELAY_DEFAULT_VALUE());
                long pathTime = Long.parseLong(pathValue);
                if ((responseTime - pathTime) > requiredTime) {
                    processError("The response time " + responseTime + "ms minus the path time value " + pathValue
                            + "ms is greater than the required " + requiredTime + " ms.");
                }

            } else {
                setVariable("SUBSCRIPTION-OC-RECEIVE-ERROR", false);
            };


            setVariable("CONTINUEPUBLICATION", true);

        } catch (Exception ex) {
            ex.printStackTrace();
            setVariable("SUBSCRIPTION-OC-RECEIVE-ERROR", true);
            throw new JameleonScriptException(ex.getMessage(), this);
        }



    }
}
