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
public class TestSubscriptionOC {

    protected String Dialog;
    /**
     * The message request to send
     *
     * @jameleon.attribute required="true"
     */
    protected Object RequestMessage;
    /**
     * A flag which indicates whether a error response is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String SubscriptionErrorResponseExpected="False";
    /**
     * The type error response that is expected
     *
     * @jameleon.attribute required="true"
     */
    protected String SubscriptionErrorTypeExpected;
    /**
     * The indicates whether data is available
     *
     * @jameleon.attribute required="true"
     */
    protected String DataAvailable="true";
    private ApplicationLayerStandard theApplicationLayerStandard;
    private String authenticationCheck = "false";
    private String allowAccess = "true";
    private String username = "newuser";
    private String password = "newpassword";
    private String organizationID = "orgID";
    private String subscriptionDialog = "dlCenterActiveVerificationSubscription";
    private String wsdlURL = "file:/c:/c2cri/testfiles/tmddv301/tmdd-fixed.wsdl";
    private String testCasePath = "file:C:/projects/Release2/projects/C2C-RI/src/RIGUI/CustomTestSuites/TMDDV301ExtensionToBlankTestSuite/InfoLayer/Data/TCS-1-dlCenterActiveVerificationRequest-EC-Valid.data";
    private final Logger log = LogManager.getLogger(TestSubscriptionOC.class);

    public static void main(String[] args)  {
        TestSubscriptionOC rrec = new TestSubscriptionOC();
        try{
        rrec.subscriptionOC();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        System.exit(0);
    }

    public void subscriptionOC() throws Exception {
        try {

            ApplicationLayerStandardFactory theStandardFactory = ApplicationLayerStandardFactory.getInstance();
            theStandardFactory.setApplicationLayerStandard("NTCIP2306");
            theStandardFactory.setCenterMode("OC");
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

            Message requestMessage = theApplicationLayerStandard.subcription_oc_receive(subscriptionDialog);
            requestMessage.setMessageName("REQUEST");

            DialogResults results = theApplicationLayerStandard.getDialogResults();
            log.info(requestMessage.toXML());


            String AuthenticationCheck = "false";
            String AllowAccess = "true";
            String Username = "1";
            String Password = "1";
            String OrganizationID = "1";
            TMDDErrorResponseProcessor errorProcessor = new TMDDErrorResponseProcessor();
            if (errorProcessor.requestContainsError(results, AuthenticationCheck, AllowAccess,
                    DataAvailable, Username, Password, OrganizationID, "TestMe", requestMessage)) {

                Message errorResponseMessage = errorProcessor.getErrorResponseMsg("ERRORRESPONSE");
                theApplicationLayerStandard.subscription_oc_reply(Dialog, errorResponseMessage);
                log.info(errorResponseMessage.toXML());

                if (SubscriptionErrorResponseExpected.equalsIgnoreCase("true")) {

                    if (SubscriptionErrorTypeExpected.equals(errorProcessor.getErrorType()) || (SubscriptionErrorTypeExpected.equals(errorProcessor.getErrorTypeText()))) {
                        // OK
                    } else {
                        throw new Exception("An error response was sent with error code " + errorProcessor.getErrorType() + "-"
                                + errorProcessor.getErrorTypeText() + " while ErrorTypeExpected was set to " + SubscriptionErrorTypeExpected + ".");
                    }

                } else {
                    throw new Exception("An error response was sent when ErrorResponseExpected was set to " + SubscriptionErrorResponseExpected + "." + "  The error encountered was " + errorProcessor.getErrorText());
                }

            } else {
                ArrayList<String> messageSpecArray = new ArrayList<String>();
                MessageManager theManager = MessageManager.getInstance();
                Message newMessage = theManager.createMessage(Dialog);

                String request = "#RIMessageSpec#tmdd:centerActiveVerificationResponseMsg.organization-information.organization-name = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-location = stringstri; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-function = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.person-name = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.person-title = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.phone-number = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.phone-alternate = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.mobile-phone-number = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.mobile-phone-id = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.fax-number = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.pager-number = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.pager-id = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.email-address = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.radio-unit = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.address-line1 = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.address-line2 = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.city = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.state = st; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.zip-code = string; tmdd:centerActiveVerificationResponseMsg.organization-information.organization-contact-details.country = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-id = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-name = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-location.longitude = 3; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-location.horizontalDatum = 3; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-location.height.altitude.kmDec = 1000.00; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-location.height.verticalDatum = 1; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-description = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-type = fixed; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.person-name = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.person-title = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.phone-number = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.phone-alternate = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.mobile-phone-number = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.mobile-phone-id = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.fax-number = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.pager-number = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.pager-id = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.email-address = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.radio-unit = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.address-line1 = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.address-line2 = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.city = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.state = st; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.zip-code = string; tmdd:centerActiveVerificationResponseMsg.organization-information.center-contact-list.center-contact-details.center-contact-details.country = string; tmdd:centerActiveVerificationResponseMsg.organization-information.last-update-time.time = string; tmdd:centerActiveVerificationResponseMsg.organization-information.last-update-time.offset = strin; tmdd:centerActiveVerificationResponseMsg.center-id = string; tmdd:centerActiveVerificationResponseMsg.center-name = string#RIMessageSpec#";

                if (request.startsWith("#RIMessageSpec#") && (request.endsWith("#RIMessageSpec#"))) {
                    request = request.replace("#RIMessageSpec#", "");
                    String[] requestArray = request.split("; ");
                    for (int ii = 0; ii < requestArray.length; ii++) {
                        messageSpecArray.add(requestArray[ii]);
                    }
                    MessageSpecification messageSpec = new MessageSpecification(messageSpecArray);
                    newMessage.setMessageSpecification(messageSpec);

                    theApplicationLayerStandard.subscription_oc_reply(Dialog, newMessage);
                }
            }




        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception(ex.getMessage());
        }


    }
}
