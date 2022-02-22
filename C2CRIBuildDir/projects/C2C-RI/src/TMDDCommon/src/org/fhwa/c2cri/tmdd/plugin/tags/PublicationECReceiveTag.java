/**
 * 
 */
package org.fhwa.c2cri.tmdd.plugin.tags;

import net.sf.jameleon.exception.JameleonScriptException;
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
 * @jameleon.function name="PUBLICATION-EC-RECEIVE" type="action"
 * @jameleon.step Receive the publication message
 */
public class PublicationECReceiveTag extends C2CRIFunctionTag {

    /**
     * The name of the dialog associated with this request
     *
     * @jameleon.attribute required="true"
     */
    protected String Dialog;
    /**
     * A flag which indicates whether a error response is expected
     *
     * @jameleon.attribute required="false"
     */
    protected String PUBLICATIONERRORRESPONSEEXPECTED="False";
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="false"
     */
    protected String PUBLICATIONERRORTYPEEXPECTED;
    /**
     * The number of publications expected
     *
     * @jameleon.attribute required="false"
     */
    protected String PUBLICATIONSTORECEIVE;
    /**
     * The flag for whether there is valid data available
     *
     * @jameleon.attribute required="false" default="True"
     */
    protected String DATAVALID;

    /**
     * The flag for whether to allow the requesting center access
     *
     * @jameleon.attribute required="false" default = "True"
     */
    protected String ALLOWACCESS;

    /**
     * The flag for whether to perform authentication on the received message
     *
     * @jameleon.attribute required="false" default = "False"
     */
    protected String AUTHENTICATIONCHECK;

    /**
     * The time in milliseconds within which an owner center has to send a response
     *
     * @jameleon.attribute required="true"
     */
    protected String RESPONSETIMEREQUIRED;
    

    /** The information layer controller. */
    private InformationLayerController theInformationLayerController;

    /**
     * Test block.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void testBlock() {
        try {

            theInformationLayerController = sessionTag.getTheInformationLayerStandard().getInformationLayerController();

            /**
             * Put Publication Count processing code here ....
             *
             */
            setVariable("CONTINUEPUBLICATION", false);

            InformationLayerOperationResults opResults = theInformationLayerController.performPublicationECReceive(Dialog);
            Message requestMessage = opResults.getRequestMessage();
            requestMessage.setMessageName("REQUEST");
            MessageManager msgManager = MessageManager.getInstance();
            msgManager.addMessage(requestMessage, true);

            DialogResults results = opResults.getDialogResults(Message.MESSAGETYPE.Request);
//            log.info(requestMessage.toXML());

            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            if (errorProcessor.requestContainsError(results, AUTHENTICATIONCHECK, ALLOWACCESS,
                    PUBLICATIONERRORRESPONSEEXPECTED, PUBLICATIONERRORTYPEEXPECTED, DATAVALID, "", "", "", this.getTestCaseTag().getTestCase().getTestCaseId(), requestMessage)) {

                Message errorResponseMessage = errorProcessor.getErrorResponseMsg(Dialog);
                InformationLayerOperationResults errorOpResults = theInformationLayerController.performPublicationECResponse(Dialog, errorResponseMessage, true);
//                log.info(errorResponseMessage.toXML());
                errorResponseMessage.setMessageName("RESPONSE");
                msgManager.addMessage(errorResponseMessage, true);

                if (PUBLICATIONERRORRESPONSEEXPECTED.equalsIgnoreCase("true")) {
                    setVariable("CONTINUEPUBLICATION", false);

                    if (PUBLICATIONERRORTYPEEXPECTED.equals(errorProcessor.getErrorType()) || (PUBLICATIONERRORTYPEEXPECTED.equals(errorProcessor.getErrorTypeText()))) {
                        setVariable("PUBLICATION-EC-RECEIVE-ERROR", false);
                        // OK
                    } else {
                        setVariable("PUBLICATION-EC-RECEIVE-ERROR", true);
                        processError("An error response was sent with error code " + errorProcessor.getErrorType() + "-"
                                + errorProcessor.getErrorTypeText() + " while ErrorTypeExpected was set to " + PUBLICATIONERRORTYPEEXPECTED + ".");
                    }

                } else {
                    setVariable("PUBLICATION-EC-RECEIVE-ERROR", true);
                    processError("An error response was sent when ErrorResponseExpected was set to " + PUBLICATIONERRORRESPONSEEXPECTED + "." + "  The error encountered was " + errorProcessor.getErrorText());
                }

            } else {
                
                long responseTime = opResults.getResponseMessage().getTransportTimeInMillis() - opResults.getRequestMessage().getTransportTimeInMillis();
                long requiredTime = Long.parseLong(RESPONSETIMEREQUIRED);
                String pathValue = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_PATH_DELAY_PARAMETER(),
                        TMDDSettingsImpl.getTMDD_PATH_DELAY_DEFAULT_VALUE());
                long pathTime = Long.parseLong(pathValue);
                if ((responseTime - pathTime) > requiredTime) {
                    processError("The response time " + responseTime + "ms minus the path time value " + pathValue
                            + "ms is greater than the required " + requiredTime + " ms.");
                }

                // Verify OC Periodic Update Frequency
                if ((opResults.getPublicationCount() > 0) && (opResults.getSubscriptionPeriodicFrequency() > 0)) {
                    long updateTime = opResults.getSubscriptionPeriodicFrequency();  // in Seconds              
                    String bufferValue = RIParameters.getInstance().getParameterValue(TMDDSettingsImpl.getTMDD_SETTINGS_GROUP(), TMDDSettingsImpl.getTMDD_PERIODIC_UPDATE_BUFFER_PARAMETER(),
                            TMDDSettingsImpl.getTMDD_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE());
                    Double bufferPercentage = Double.parseDouble(bufferValue)/100;

                    long bufferTime = 5;
                    if (updateTime < 60) {
                        bufferTime = 5;
                    } else {
                        bufferTime = Math.round(updateTime * bufferPercentage);
                    }

                    if ((Math.abs(opResults.getMillisSinceLastPeriodicPublication() / 1000 - updateTime) >= bufferTime)) {
                        processError("The periodic update time " + opResults.getMillisSinceLastPeriodicPublication() / 1000 + "s exceeds the required " + updateTime + "s (including a " + bufferTime + " s buffer).");
                    } else {
                        System.out.println("PublicationECReceiveTag:testBlock: Publication was within the buffer range of " + bufferTime + " s @" + (opResults.getMillisSinceLastPeriodicPublication() / 1000) +"seconds since last publication expected time = "+ updateTime + " real Calculation value = "+(Math.abs(opResults.getMillisSinceLastPeriodicPublication() / 1000 - updateTime)));
                    }
                } else {
                    System.out.println("PublicationECReceiveTag:testBlock: Publication count =  " + opResults.getPublicationCount() + "  with frequency " + opResults.getSubscriptionPeriodicFrequency());
                }
                
                setVariable("PUBLICATION-EC-RECEIVE-ERROR", false);
            };
            
            if (Integer.parseInt(PUBLICATIONSTORECEIVE) > opResults.getPublicationCount() + 1) {  // Count does not increment until after reply is sent.  So add 1 here
                setVariable("CONTINUEPUBLICATION", true);
            }
            System.out.println("PublicationECReceiveTag:: PUBLICATIONSTORERECEIVE = " + Integer.parseInt(PUBLICATIONSTORECEIVE) + " PubCount = " + opResults.getPublicationCount() + " CONTINUEPUBLICATION = " + getVariable("CONTINUEPUBLICATION"));

        } catch (Exception ex) {
            ex.printStackTrace();
            setVariable("CONTINUEPUBLICATION", false);
            setVariable("PUBLICATION-EC-RECEIVE-ERROR", true);
            throw new JameleonScriptException(ex.getMessage(), this);
        }

    }

}
