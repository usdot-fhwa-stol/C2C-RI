/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.net.URLClassLoader;
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
import org.fhwa.c2cri.testmodel.verification.MessageResults;
import org.fhwa.c2cri.testmodel.verification.XSLTransformer;
import org.fhwa.c2cri.tmdd.TMDDMessageTester;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestTMDDControllerClient.
 *
 * @deprecated
 * @author TransCore ITS, LLC
 * Last Updated: 1/8/2014
 */
public class TestTMDDControllerClient
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
        ECTestCases ecTC = new ECTestCases();
        TestCaseSpec tc = ecTC.get(tcId);
        InformationLayerOperationResults results = null;

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
        theStandardFactory.setCenterMode("EC");
        theStandardFactory.setInformationLayerStandard("TMDDv.303d");
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
            theInfoStandardFactory.setCenterMode("EC");
            theInfoStandardFactory.setInformationLayerStandardName("TMDDv3.03d");
            theInfoStandardFactory.setTestCase("Tester");
            tmdd = theInfoStandardFactory.getInformationStandard();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

        }

        tmdd.initializeStandard("FakeName", "EC", als);

        InformationLayerController ilc = tmdd.getInformationLayerController();
        ilc.setDisableAppLayerEncoding(tc.isSkipEncoding());

        try
        {
            if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.FTPGET)
                || tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPGET))
                results = ilc.performGetEC(tc.getOperationName());
            else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPPOST)
                     || tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR)
                     || (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR)))
            {
                Message requestMsg = MessageManager.getInstance().createMessage("Request");
                requestMsg.setMessageBody(tc.getRequestMessage());
                results = ilc.performRequestResponseEC(tc.getOperationName(),
                                                       requestMsg);

            }
            else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB))
            {
                Message requestMsg = MessageManager.getInstance().createMessage("Request");
                requestMsg.setMessageBody(tc.getRequestMessage());
                results = ilc.performSubscriptionEC(tc.getOperationName(),
                                                    requestMsg);
                System.out.println("\n\nSUB RESULTS\n");
                System.out.println("SubscriptionActive: " + results.isSubscriptionActive() + "  PubComplete: " + results.isPublicationComplete() + "  Pub Count" + results.getPublicationCount());

                while (results.isSubscriptionActive() && (!results.isPublicationComplete()) && (results.getPublicationCount() <= tc.getNumPublicationsExpected()))
                {
                    System.out.println("Current Subscription Status = " + results.isSubscriptionActive() + " Pub Count: " + results.getPublicationCount());
                    results = ilc.performPublicationECReceive(tc.getRelatedOperationName());
                    System.out.println("\n\nPUB REQUEST RESULTS\n");
                    System.out.println("SubscriptionActive: " + results.isSubscriptionActive() + "  PubComplete: " + results.isPublicationComplete() + "  Pub Count" + results.getPublicationCount());
//
                    Message relatedResponseMsg = MessageManager.getInstance().createMessage("Response");
                    relatedResponseMsg.setMessageBody(tc.getRelatedResponseMessage());
                    results = ilc.performPublicationECResponse(tc.getRelatedOperationName(),
                                                               relatedResponseMsg, tc.isMessageErrorExpected());
                    System.out.println("\n\nPUB RESPONSE RESULTS\n");
                    System.out.println("SubscriptionActive: " + results.isSubscriptionActive() + "  PubComplete: " + results.isPublicationComplete() + "  Pub Count" + results.getPublicationCount());
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
     * @return the message results
     *
     * @throws Exception the exception
     */
    private static MessageResults testMessage(NTCIP2306ControllerResults results, String messageType) throws Exception
    {

        XSLTransformer transformer = XSLTransformer.getInstance();
        URL xslURL = TMDDMessageTester.class.getResource("TMDDMessageVerify.xsl");
        MessageResults msgresults = null;
        if (results != null)
        {
            String testString = new String(messageType.equalsIgnoreCase("RESPONSE") ? results.getResponseMessage().getMessagePartContent(1) : results.getRequestMessage().getMessagePartContent(1));
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
        return msgresults;
    }

    /**
     * Test app layer factory.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public static void testAppLayerFactory()
    {
        try
        {
            ClassLoader sysClassLoader = ClassLoader.getSystemClassLoader();
            URL[] urls = ((URLClassLoader)sysClassLoader).getURLs();
            String urlList = "";
            for (int i = 0; i < urls.length; i++)
                urlList = urlList.concat(urls[i].getFile() + "\n");
            System.out.println("TestAppLayerFactory:  List of URLS Available: \n" + urlList);

            ApplicationLayerStandardFactory theStandardFactory = ApplicationLayerStandardFactory.getInstance();
            theStandardFactory.setApplicationLayerStandard("NTCIP2306v1.69");
            theStandardFactory.setCenterMode("EC");
            theStandardFactory.setInformationLayerStandard("TMDD");
            theStandardFactory.setTestCase("TestCaseID");

            ApplicationLayerStandard theApplicationLayerStandard = theStandardFactory.getApplicationStandard();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
