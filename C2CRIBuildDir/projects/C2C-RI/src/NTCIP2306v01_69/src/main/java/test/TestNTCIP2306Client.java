/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Controller;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.CenterConfigurationController;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.MessageResults;
import org.fhwa.c2cri.testmodel.verification.XSLTransformer;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestNTCIP2306Client.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestNTCIP2306Client {

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        RIParameters.getInstance("c:\\c2cri\\c2cri.properties");

        Integer tcId = 10;
        ECTestCases ecTC = new ECTestCases();
        TestCaseSpec tc = ecTC.get(tcId);
        NTCIP2306ControllerResults results = null;

        RIWSDL theWSDL = new RIWSDL(tc.getWsdlFile());
        CenterConfigurationController ccc2 = new CenterConfigurationController(null, theWSDL, Center.RICENTERMODE.EC);
        if (tc.getOperationType().equals(tc.getOperationType().HTTPSOAPSUB)) {
            OperationIdentifier subOpId = new OperationIdentifier(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    OperationIdentifier.SOURCETYPE.LISTENER);
            OperationIdentifier pubOpId = new OperationIdentifier(tc.getRelatedServiceName(), tc.getRelatedPortName(), tc.getRelatedOperationName(),
                    OperationIdentifier.SOURCETYPE.LISTENER);

            HashMap<OperationIdentifier, OperationIdentifier> subPubMap = new HashMap();
            subPubMap.put(subOpId, pubOpId);
            ccc2.setSubPubMapping(subPubMap);
        }

        ccc2.initialzeServices();

        NTCIP2306Controller ntcip = new NTCIP2306Controller(theWSDL, ccc2);

        if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.FTPGET)) {
            results = ntcip.performFTPGetEC(tc.getServiceName(), tc.getPortName(), tc.getOperationName());

        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPGET)) {
            results = ntcip.performHTTPGetEC(tc.getServiceName(), tc.getPortName(), tc.getOperationName());

        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPPOST)) {
            results = ntcip.performHTTPPostEC(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    new String(tc.getRequestMessage()), tc.isSkipEncoding());
        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR)) {
            results = ntcip.performSOAPRREC(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    new String(tc.getRequestMessage()), tc.isSkipEncoding());
        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB)) {
            results = ntcip.performSOAPSUBEC(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    new String(tc.getRequestMessage()), tc.isSkipEncoding());

            while (results.isSubscriptionActive() && (!results.isPublicationComplete()) && (results.getPublicationCount() <= tc.getNumPublicationsExpected())) {
                System.out.println("Current Subscription Status = " + results.isSubscriptionActive() + " Pub Count: " + results.getPublicationCount());
                results = ntcip.performSOAPPUBECRequest(tc.getRelatedServiceName(), tc.getRelatedPortName(), tc.getRelatedOperationName());
                System.out.println("\n\nSOAP PUB REQUEST RESULTS\n");
                System.out.println(results.toString());

                System.out.println("Response Message = " + new String(tc.getRelatedResponseMessage()) + "\n\n");
                results = ntcip.performSOAPPUBECResponse(tc.getRelatedServiceName(), tc.getRelatedPortName(), tc.getRelatedOperationName(),
                        new String(tc.getRelatedResponseMessage()), tc.isSkipEncoding(), tc.isMessageErrorExpected());
                System.out.println("\n\nSOAP PUB RESPONSE RESULTS\n");
                System.out.println(results.toString());
            }

        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPPUB)) {
        }

        ccc2.stopServices();
        System.out.println("\n\n\n");
        System.out.println("Message Received: \n");
        if (results.getResponseMessage().getMessagePartContent(1) != null) {
            System.out.println(new String(results.getResponseMessage().getMessagePartContent(1)));
        }
        if (results.getResponseMessage().getMessagePartContent(2) != null) {
            System.out.println(new String(results.getResponseMessage().getMessagePartContent(0)));
        }
        System.out.println("\n\n\n");
        System.out.println(results.toString());

        try {
            MessageResults msgResults = testMessage(results, "Response");

            NRTMMatcher matcher = new NRTMMatcher("C:\\RI\\InitialDeployment\\TMDD\\TMDDv303apsr\\NRTMUpdateTable.xml");
            ArrayList<String> matchresults = matcher.getRelatedRequirements("2.3.3", "Response Message");
            System.out.println("Message Verification Errors:");
            for (String matchRequirement : matchresults) {
                ArrayList<String> verifyResults = msgResults.getRequirementErrors(matchRequirement);
                if (!verifyResults.isEmpty()) {
                    for (String error : verifyResults) {
                        System.out.println(error);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        System.gc();

    }

    /**
     * Test message.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param results the results
     * @param messageType the message type
     * @return the message results
     * @throws Exception the exception
     */
    private static MessageResults testMessage(NTCIP2306ControllerResults results, String messageType) throws Exception {

        XSLTransformer transformer = XSLTransformer.getInstance();

        URL xslURL = new URL("file:/c:/temp/data/301XMLSamples/TMDDMessageVerify.xsl");
        MessageResults msgresults = null;
        if (results != null) {
            String testString = new String(messageType.equalsIgnoreCase("RESPONSE") ? results.getResponseMessage().getMessagePartContent(1) : results.getRequestMessage().getMessagePartContent(1));
            try ( ByteArrayInputStream bais = new ByteArrayInputStream(messageType.equalsIgnoreCase("RESPONSE") ? results.getResponseMessage().getMessagePartContent(1) : results.getRequestMessage().getMessagePartContent(1))) {
                HashMap parameterMap = new HashMap();
                long start = System.currentTimeMillis();
                msgresults = new MessageResults(transformer.xslTransform(bais, xslURL, parameterMap));
                long finish = System.currentTimeMillis();
                System.out.println("Delta Time: " + (finish - start));
                System.out.println(msgresults.toString());
            }
        }
        return msgresults;
    }

}
