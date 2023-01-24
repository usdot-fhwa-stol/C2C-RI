/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.HashMap;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.ApplicationLayerStandardFactory;
import org.fhwa.c2cri.infolayer.InformationLayerController;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.infolayer.InformationLayerStandard;
import org.fhwa.c2cri.infolayer.InformationLayerStandardFactory;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306ControllerResults;
import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import org.fhwa.c2cri.testmodel.verification.MessageResults;
import org.fhwa.c2cri.testmodel.verification.XSLTransformer;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestTMDDControllerServer.
 *
 * @author TransCore ITS, LLC
 * @deprecated
 * Last Updated: 1/8/2014
 */
public class TestTMDDControllerServer
{

    /**
     * The main method.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     *
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception
    {

        RIParameters.getInstance("c:\\c2cri\\c2cri.properties");
        Integer tcId = 6;
        InformationLayerOperationResults results = null;

        OCTestCases ocTC = new OCTestCases();
        TestCaseSpec tc = ocTC.get(tcId);

        URL wsdlURL = null;
        try
        {
            wsdlURL = new URL(tc.getWsdlFile());
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        InformationLayerStandard tmdd = null;
        ApplicationLayerStandard als = null;

        ApplicationLayerStandardFactory theStandardFactory = ApplicationLayerStandardFactory.getInstance();
        theStandardFactory.setApplicationLayerStandard("NTCIP2306v1");
        theStandardFactory.setCenterMode("OC");
        theStandardFactory.setInformationLayerStandard("TMDDv3.03d");
        theStandardFactory.setTestCase("Tester");
        theStandardFactory.setRequestDialog(tc.getOperationName());
        theStandardFactory.setSubscriptionDialog(tc.getOperationName());
        theStandardFactory.setPublicationDialog(tc.getRelatedOperationName());
        theStandardFactory.setTestConfigSpecificationURL(tc.getWsdlFile());
        theStandardFactory.setTestSuiteSpecificationURL(null);

        try
        {
            als = theStandardFactory.getApplicationStandard();
            InformationLayerStandardFactory theInfoStandardFactory = InformationLayerStandardFactory.getInstance();
            theInfoStandardFactory.setApplicationLayerStandard(als);
            theInfoStandardFactory.setCenterMode("OC");
            theInfoStandardFactory.setInformationLayerStandardName("TMDDv3.03d");
            theInfoStandardFactory.setTestCase("Tester");
            tmdd = theInfoStandardFactory.getInformationStandard();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

        }

        tmdd.initializeStandard("FakeName", "OC", als);

        InformationLayerController ilc = tmdd.getInformationLayerController();
        ilc.setDisableAppLayerEncoding(tc.isSkipEncoding());

        try
        {
            if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.FTPGET)
                || tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPGET))
            {
                Message responseMsg = MessageManager.getInstance().createMessage("Response");
                responseMsg.setMessageBody(tc.getResponseMessage());
                results = ilc.performGetOC(tc.getOperationName(),
                                           responseMsg);

            }
            else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPPOST)
                     || tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR)
                     || (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR)))
            {
                results = ilc.performRequestResponseOCReceive(tc.getOperationName());
                System.out.println("\n\nREQUEST RESULTS\n");
                System.out.println(results.toString());
                Message responseMsg = MessageManager.getInstance().createMessage("Response");
                responseMsg.setMessageBody(tc.getResponseMessage());
                results = ilc.performRequestResponseOCResponse(tc.getOperationName(),
                                                               responseMsg, tc.isMessageErrorExpected());

            }
            else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB))
            {
                results = ilc.performSubscriptionOCReceive(tc.getOperationName());
                System.out.println("\n\nSOAP SUB REQUEST RESULTS\n");
                System.out.println("SubscriptionActive: " + results.isSubscriptionActive() + "  PubComplete: " + results.isPublicationComplete() + "  Pub Count" + results.getPublicationCount());

                Message responseMsg = MessageManager.getInstance().createMessage("Response");
                responseMsg.setMessageBody(tc.getResponseMessage());
                results = ilc.performSubscriptionOCResponse(tc.getOperationName(),
                                                            responseMsg, tc.isMessageErrorExpected());
                System.out.println("\n\nSOAP SUB RESPONSE RESULTS\n");
                System.out.println("SubscriptionActive: " + results.isSubscriptionActive() + "  PubComplete: " + results.isPublicationComplete() + "  Pub Count" + results.getPublicationCount());

                while (results.isSubscriptionActive() && (!results.isPublicationComplete()) && results.getPublicationCount() <= tc.getNumPublicationsExpected())
                {
                    System.out.println("Current Subscription Status = " + results.isSubscriptionActive() + " Pub Count: " + results.getPublicationCount());
                    Message requestMsg = MessageManager.getInstance().createMessage("Response");
                    requestMsg.setMessageBody(tc.getRelatedRequestMessage());
                    results = ilc.performPublicationOC(tc.getRelatedOperationName(),
                                                       requestMsg);

                }

            }
            else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPPUB))
            {
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        ilc.shutdown();
        System.gc();

    }

    /**
     * Test message.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param results     the results
     * @param messageType the message type
     *
     * @throws Exception the exception
     */
    private static void testMessage(NTCIP2306ControllerResults results, String messageType) throws Exception
    {

        XSLTransformer transformer = XSLTransformer.getInstance();

        URL xslURL = new URL("file:/c:/data/301XMLSamples/TMDDMessageVerify.xsl");
        MessageResults msgresults;
        if (results != null)
            try (ByteArrayInputStream bais = new ByteArrayInputStream(messageType.equalsIgnoreCase("RESPONSE") ? results.getResponseMessage().getMessagePartContent(1) : results.getRequestMessage().getMessagePartContent(1)))
            {
                HashMap parameterMap = new HashMap();
                long start = System.currentTimeMillis();
                msgresults = new MessageResults(transformer.xslTransform(bais, xslURL, parameterMap));
                long finish = System.currentTimeMillis();
                System.out.println("Delta Time: " + (finish - start));
                System.out.println(msgresults.toString());
            }
    }

    /**
     * Sub pub string map converter.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param mappingString the mapping string
     * @param centerMode    the center mode
     *
     * @return the hash map
     */
    private static HashMap<OperationIdentifier, OperationIdentifier> subPubStringMapConverter(String mappingString, String centerMode)
    {
        HashMap<OperationIdentifier, OperationIdentifier> subPubMap = new HashMap();
        String mappingPairSeparator = ";";
        String mapOperationTypeSeparator = ":";
        String mapOperationElementSeparator = ",";

        String[] mappingPairStrings = mappingString.split(mappingPairSeparator);

        if (mappingPairStrings.length > 0)
        {
            String[] mapPairOperations = mappingPairStrings[0].split(mapOperationTypeSeparator);

            if (mapPairOperations.length == 2)
            {
                String[] subscriptionParts = mapPairOperations[0].split(mapOperationElementSeparator);
                String[] publicationParts = mapPairOperations[1].split(mapOperationElementSeparator);

                if ((subscriptionParts.length == 3) && (publicationParts.length == 3))
                {
                    OperationIdentifier subOpId = null;
                    if (centerMode.toUpperCase().contains("OC"))
                        subOpId = new OperationIdentifier(subscriptionParts[0], subscriptionParts[1], subscriptionParts[2],
                                                          OperationIdentifier.SOURCETYPE.HANDLER);
                    else
                        subOpId = new OperationIdentifier(subscriptionParts[0], subscriptionParts[1], subscriptionParts[2],
                                                          OperationIdentifier.SOURCETYPE.LISTENER);
                    OperationIdentifier pubOpId = new OperationIdentifier(publicationParts[0], publicationParts[1], publicationParts[2],
                                                                          OperationIdentifier.SOURCETYPE.LISTENER);

                    subPubMap.put(subOpId, pubOpId);
                }
            }

        }

        return subPubMap;

    }
}
