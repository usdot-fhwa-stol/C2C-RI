/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.transports;

import java.util.ArrayList;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.ApplicationLayerStandardFactory;
import org.fhwa.c2cri.applayer.DialogResults;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 *
 * @author TransCore ITS
 */
public class TestSubscriptionEC {

    protected String Dialog;
    /**
     * The message request to send
     *
     * @jameleon.attribute required="true"
     */
    protected Object SubscriptionMessage;
    /**
     * A flag which indicates whether a error response is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String SubscriptionErrorResponseExpected="false";
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String SubscriptionErrorTypeExpected;
    private ApplicationLayerStandard theApplicationLayerStandard;
    private String subscriptionDialog="dlCenterActiveVerificationSubscription";
    private String wsdlURL="file:/c:/c2cri/testfiles/tmddv301/tmdd-fixed.wsdl";
    private String testCasePath="file:C:/projects/Release2/projects/C2C-RI/src/RIGUI/CustomTestSuites/TMDDV301ExtensionToBlankTestSuite/InfoLayer/Data/TCS-1-dlCenterActiveVerificationRequest-OC-Valid.data";
    private final Logger log = LogManager.getLogger(TestSubscriptionEC.class);

    public static void main(String[] args) {
        TestSubscriptionEC subec= new TestSubscriptionEC();
        try{
        subec.subscriptionEC();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public void subscriptionEC() throws Exception{
        try {

               ApplicationLayerStandardFactory theStandardFactory = ApplicationLayerStandardFactory.getInstance();
                    theStandardFactory.setApplicationLayerStandard("NTCIP2306");
                    theStandardFactory.setCenterMode("EC");
                    theStandardFactory.setInformationLayerStandard("TMDDv301");
                    theStandardFactory.setTestCase("TestMe");
                    theStandardFactory.setSubscriptionDialog(subscriptionDialog);
                    theStandardFactory.setTestConfigSpecificationURL(wsdlURL);
                    theStandardFactory.setTestSuiteSpecificationURL(null);
                    try {
                        theApplicationLayerStandard = theStandardFactory.getApplicationStandard();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        throw new Exception("*C2CRISessionTag: Error Creating Application Layer Standard ->" + ex.getMessage());

                    }


            ArrayList<String> messageSpecArray = new ArrayList<String>();
            MessageManager theManager = MessageManager.getInstance();
            Message newMessage = theManager.createMessage(subscriptionDialog);
//            String errorText = "<fake/>";
//            byte[] errorBytes = errorText.getBytes();
//            newMessage.setMessageBody(errorBytes);
            String request = "#RIMessageSpec#tmdd:centerActiveVerificationRequestMsg.authentication.user-id = string; tmdd:centerActiveVerificationRequestMsg.authentication.password = string; tmdd:centerActiveVerificationRequestMsg.authentication.operator-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-name = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-location = stringstri; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-function = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.contact-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.person-name = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.person-title = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.phone-number = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.phone-alternate = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.mobile-phone-number = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.mobile-phone-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.fax-number = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.pager-number = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.pager-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.email-address = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.radio-unit = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.address-line1 = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.address-line2 = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.city = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.state = st; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.zip-code = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.organization-contact-details.country = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-name = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.latitude = 3; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.longitude = 3; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.horizontalDatum = wgs-84; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.height.altitude.kmDec = 1000.00; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-location.height.verticalDatum = 1; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-description = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-type = fixed; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.contact-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.person-name = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.person-title = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.phone-number = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.phone-alternate = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.mobile-phone-number = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.mobile-phone-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.fax-number = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.pager-number = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.pager-id = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.email-address = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.radio-unit = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.address-line1 = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.address-line2 = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.city = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.state = st; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.zip-code = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.center-contact-list.center-contact-details.center-contact-details.country = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.last-update-time.date = stringst; tmdd:centerActiveVerificationRequestMsg.organization-requesting.last-update-time.time = string; tmdd:centerActiveVerificationRequestMsg.organization-requesting.last-update-time.offset = strin#RIMessageSpec#";

                if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                    request = request.replace("#RIMessageSpec#", "");
                    String[] requestArray = request.split("; ");
                    for (int ii = 0; ii < requestArray.length; ii++) {
                        messageSpecArray.add(requestArray[ii]);
                    }
                    MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                    newMessage.setMessageSpecification(messageSpec);

                } else {
                    newMessage.setMessageBody(((String) SubscriptionMessage).getBytes());
                }

            newMessage.setMessageName("REQUEST");
            theManager.addMessage(newMessage);


            Message responseMessage = theApplicationLayerStandard.subscription_ec(subscriptionDialog, newMessage);
            responseMessage.setMessageName("RESPONSE");

            DialogResults results = theApplicationLayerStandard.getDialogResults();
            log.info(responseMessage.toXML());


            // If the fields contained within the RESPONSE satisfy the information layer standard design then continue
            if (!results.isMessageFieldsVerified()) {
                throw new Exception("The fields contained within the RESPONSE do not match the TMDD Design. " + results.getMessageFieldsVerifiedErrors());
            }

            // If the value of each field contained within the RESPONSE satisfy the information layer standard design then continue
            if (!results.isMessageValuesVerified()) {
                throw new Exception("The value of each field contained within the RESPONSE do not match the TMDD Design." + results.getMessageValuesVerifiedErrors());
            }

            // If the SUT responded with one message per the app layer standard rules then continue  -- Message must be valid first
            if (!results.isOneMessageReceived()) {
                throw new Exception("The SUT responded with more than one message. " + results.getMessageReceivedErrors());
            }

            // If the error response conditions are met then continue
            try {
                TMDDErrorResponseProcessor.processErrorResponseMessage(SubscriptionErrorResponseExpected, SubscriptionErrorTypeExpected, responseMessage);
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("Error sending message request: '" + ex.getMessage());
        }



    }
}
