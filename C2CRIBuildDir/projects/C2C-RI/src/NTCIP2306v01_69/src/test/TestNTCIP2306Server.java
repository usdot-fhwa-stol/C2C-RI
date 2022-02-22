/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Controller;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.CenterConfigurationController;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.MessageResults;
import org.fhwa.c2cri.testmodel.verification.XSLTransformer;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestNTCIP2306Server.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestNTCIP2306Server {

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

        OCTestCases ocTC = new OCTestCases();
        TestCaseSpec tc = ocTC.get(tcId);

        NTCIP2306ControllerResults results = null;
        RIWSDL theWSDL = new RIWSDL(tc.getWsdlFile());
        CenterConfigurationController ccc2 = new CenterConfigurationController(null, theWSDL, Center.RICENTERMODE.OC);

        OperationIdentifier pubOpId = null;
        if (tc.getOperationType().equals(tc.getOperationType().HTTPSOAPSUB)) {
            OperationIdentifier subOpId = new OperationIdentifier(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    OperationIdentifier.SOURCETYPE.HANDLER);
            pubOpId = new OperationIdentifier(tc.getRelatedServiceName(), tc.getRelatedPortName(), tc.getRelatedOperationName(),
                    OperationIdentifier.SOURCETYPE.LISTENER);

            HashMap<OperationIdentifier, OperationIdentifier> subPubMap = new HashMap();
            subPubMap.put(subOpId, pubOpId);
            ccc2.setSubPubMapping(subPubMap);
        }

        ccc2.initialzeServices();

        NTCIP2306Controller ntcip = new NTCIP2306Controller(theWSDL, ccc2);

        if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.FTPGET)) {
            results = ntcip.performFTPGetOC(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    new String(tc.getResponseMessage()), tc.isSkipEncoding());

        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPGET)) {
            results = ntcip.performHTTPGetOC(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    new String(tc.getResponseMessage()), tc.isSkipEncoding());
        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPPOST)) {
            results = ntcip.performHTTPPostOCRequest(tc.getServiceName(), tc.getPortName(), tc.getOperationName());
            System.out.println("\n\nHTTP POST REQUEST RESULTS\n");
            System.out.println(results.toString());
            results = ntcip.performHTTPPostOCResponse(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    new String(tc.getResponseMessage()), tc.isSkipEncoding());

        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR)) {
            results = ntcip.performSOAPRROCRequest(tc.getServiceName(), tc.getPortName(), tc.getOperationName());
            System.out.println("\n\nSOAP RR REQUEST RESULTS\n");
            System.out.println(results.toString());
            results = ntcip.performSOAPRROCResponse(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    new String(tc.getResponseMessage()), tc.isSkipEncoding(), tc.isMessageErrorExpected());
        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB)) {
            results = ntcip.performSOAPSUBOCRequest(tc.getServiceName(), tc.getPortName(), tc.getOperationName());
            System.out.println("\n\nSOAP SUB REQUEST RESULTS\n");
            System.out.println(results.toString());

            results = ntcip.performSOAPSUBOCResponse(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
                    new String(tc.getResponseMessage()), tc.isSkipEncoding(), tc.isMessageErrorExpected());

            while (results.isSubscriptionActive() && (!results.isPublicationComplete()) && results.getPublicationCount() <= tc.getNumPublicationsExpected()) {
                System.out.println("Current Subscription Status = " + results.isSubscriptionActive() + " Pub Count: " + results.getPublicationCount());
                results = ntcip.performSOAPPUBOC(tc.getRelatedServiceName(), tc.getRelatedPortName(), tc.getRelatedOperationName(),
                        new String(tc.getRelatedRequestMessage()), tc.isSkipEncoding());

            }

        } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPPUB)) {
        }

        ccc2.stopServices();
        System.out.println("\n\n\n");
        System.out.println(results.toString());
        try {
            testMessage(results, "Request");
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
     * @throws Exception the exception
     */
    private static void testMessage(NTCIP2306ControllerResults results, String messageType) throws Exception {

        XSLTransformer transformer = XSLTransformer.getInstance();

        URL xslURL = new URL("file:/c:/temp/data/301XMLSamples/TMDDMessageVerify.xsl");
        MessageResults msgresults;
        if (results != null) {
            NTCIP2306Message tmpMessage = messageType.equalsIgnoreCase("RESPONSE") ? results.getResponseMessage() : results.getRequestMessage();
            if (tmpMessage != null) {
                try ( ByteArrayInputStream bais = new ByteArrayInputStream(messageType.equalsIgnoreCase("RESPONSE") ? results.getResponseMessage().getMessagePartContent(1) : results.getRequestMessage().getMessagePartContent(1))) {
                    HashMap parameterMap = new HashMap();
                    long start = System.currentTimeMillis();
                    msgresults = new MessageResults(transformer.xslTransform(bais, xslURL, parameterMap));
                    long finish = System.currentTimeMillis();
                    System.out.println("Delta Time: " + (finish - start));
                    System.out.println(msgresults.toString());
                }
            }
        }
    }
}
