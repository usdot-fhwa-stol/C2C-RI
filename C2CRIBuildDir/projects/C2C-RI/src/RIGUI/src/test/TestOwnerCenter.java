/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.net.URL;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.ApplicationLayerStandardFactory;
import org.fhwa.c2cri.infolayer.InformationLayerController;
import org.fhwa.c2cri.infolayer.InformationLayerOperationResults;
import org.fhwa.c2cri.infolayer.InformationLayerStandard;
import org.fhwa.c2cri.infolayer.InformationLayerStandardFactory;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestOwnerCenter.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestOwnerCenter {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {

        RIParameters.getInstance("c:\\c2cri\\c2cri.properties");
        Integer tcId = 9;
        InformationLayerOperationResults results = null;

        OCTestCases ocTC = new OCTestCases();
        TestCaseSpec tc = ocTC.get(tcId);

        URL wsdlURL = null;
        try {
            wsdlURL = new URL(tc.getWsdlFile());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
          InformationLayerStandard tmdd = null;
          ApplicationLayerStandard als = null;

                    ApplicationLayerStandardFactory theStandardFactory = ApplicationLayerStandardFactory.getInstance();
                    theStandardFactory.setApplicationLayerStandard("NTCIP2306v1");
                    theStandardFactory.setCenterMode("OC");
                    theStandardFactory.setInformationLayerStandard("TMDDv3.03a");
                    theStandardFactory.setTestCase("Tester");
                    theStandardFactory.setRequestDialog(tc.getOperationName());
                    theStandardFactory.setSubscriptionDialog(tc.getOperationName());
                    theStandardFactory.setPublicationDialog(tc.getRelatedOperationName());
                    theStandardFactory.setTestConfigSpecificationURL(tc.getWsdlFile());
                    theStandardFactory.setTestSuiteSpecificationURL(null);

                    try {
                        als = theStandardFactory.getApplicationStandard();
                        InformationLayerStandardFactory theInfoStandardFactory = InformationLayerStandardFactory.getInstance();
                        theInfoStandardFactory.setApplicationLayerStandard(als);
                        theInfoStandardFactory.setCenterMode("OC");
                        theInfoStandardFactory.setInformationLayerStandardName("TMDDv3.03a");
                        theInfoStandardFactory.setTestCase("Tester");
                        tmdd = theInfoStandardFactory.getInformationStandard();   
                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }
        
        
//        ApplicationLayerStandard als = new NTCIP2306ApplicationLayerStandard();
//        InformationLayerStandard tmdd = new TMDDv303InformationLayerStandard();
//        als.initializeStandard(wsdlURL, null, "OC", "TMDD", "FakeTestCase", null, null, null);
        tmdd.initializeStandard("FakeName", "OC", als);
        //       RIWSDL theWSDL = new RIWSDL(tc.getWsdlFile());
        //       CenterConfigurationController ccc2 = new CenterConfigurationController(null, theWSDL, Center.RICENTERMODE.OC);

//        OperationIdentifier pubOpId = null;
//            HashMap<OperationIdentifier, OperationIdentifier> subPubMap = new HashMap();
//        if (tc.getOperationType().equals(tc.getOperationType().HTTPSOAPSUB)) {
//            OperationIdentifier subOpId = new OperationIdentifier(tc.getServiceName(), tc.getPortName(), tc.getOperationName(),
//                    OperationIdentifier.SOURCETYPE.HANDLER);
//            pubOpId = new OperationIdentifier(tc.getRelatedServiceName(), tc.getRelatedPortName(), tc.getRelatedOperationName(),
//                    OperationIdentifier.SOURCETYPE.LISTENER);
////
//            subPubMap.put(subOpId, pubOpId);
////            String subPubMapping = tc.getServiceName()+","+tc.getPortName()+","+tc.getOperationName()+":"+tc.getRelatedServiceName()+","+tc.getRelatedPortName()+","+tc.getRelatedOperationName()+";fakeService,fakePort,fakeOperation:fakeServie,fakePort,fakeOperation;badService,badOperation,badPort;";
////            ccc2.setSubPubMapping(subPubStringMapConverter(subPubMapping, Center.RICENTERMODE.OC.name()));
//        }

//        ccc2.initialzeServices();
//        if (tc.getRelatedRequestMessage() != null) {
//            ArrayList<NTCIP2306lMessage> msgList = new ArrayList();
//            msgList.add(new NTCIP2306Message(tc.getRelatedRequestMessage()));
//            pubOpId = new OperationIdentifier(tc.getRelatedServiceName(), tc.getRelatedPortName(), tc.getRelatedOperationName(),
//                    OperationIdentifier.SOURCETYPE.HANDLER);
//            ccc2.setOCPublicationMessages(pubOpId, msgList);
//        }
//        NTCIP2306Controller ntcip = new NTCIP2306Controller(theWSDL, ccc2);

//        InformationLayerAdapter ila = new NTCIP2306InformationLayerAdapter(ntcip);

        InformationLayerController ilc = tmdd.getInformationLayerController();
        ilc.setDisableAppLayerEncoding(tc.isSkipEncoding());
//        final HashMap<OperationIdentifier,OperationIdentifier> finalHashMap = subPubMap;
//        final String subOperation = tc.getOperationName();
//        final String pubOperation = tc.getRelatedOperationName();
//        als.setSubPubMapper(new SubPubMapper(){
//
//            @Override
//            public String getRelatedPublicationName(String subscriptionOperationName, Message requestMessage) throws SubPubMappingException {
//                return (pubOperation);
//            }
//
//            @Override
//            public String getRelatedSubscriptionName(String publicationOperationName) throws SubPubMappingException {
//                return subOperation;
//            }
//            
//        });
        try {
            if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.FTPGET)
                    || tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPGET)) {
                Message responseMsg = MessageManager.getInstance().createMessage("Response");
                responseMsg.setMessageBody(tc.getResponseMessage());
                results = ilc.performGetOC(tc.getOperationName(),
                        responseMsg);

            } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPPOST)
                    || tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR)
                    || (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR))) {
                results = ilc.performRequestResponseOCReceive(tc.getOperationName());
                System.out.println("\n\nREQUEST RESULTS\n");
                System.out.println(results.toString());
                Message responseMsg = MessageManager.getInstance().createMessage("Response");
                responseMsg.setMessageBody(tc.getResponseMessage());
                results = ilc.performRequestResponseOCResponse(tc.getOperationName(),
                        responseMsg, tc.isMessageErrorExpected());

            } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB)) {
                results = ilc.performSubscriptionOCReceive(tc.getOperationName());
                System.out.println("\n\nSOAP SUB REQUEST RESULTS\n");
                System.out.println("SubscriptionActive: " + results.isSubscriptionActive() + "  PubComplete: " + results.isPublicationComplete() + "  Pub Count" + results.getPublicationCount());

                Message responseMsg = MessageManager.getInstance().createMessage("Response");
                responseMsg.setMessageBody(tc.getResponseMessage());
                results = ilc.performSubscriptionOCResponse(tc.getOperationName(),
                        responseMsg, tc.isMessageErrorExpected());
                System.out.println("\n\nSOAP SUB RESPONSE RESULTS\n");
                System.out.println("SubscriptionActive: " + results.isSubscriptionActive() + "  PubComplete: " + results.isPublicationComplete() + "  Pub Count" + results.getPublicationCount());

                while (results.isSubscriptionActive() && (!results.isPublicationComplete()) && results.getPublicationCount() <= tc.getNumPublicationsExpected()) {
                    System.out.println("Current Subscription Status = " + results.isSubscriptionActive() + " Pub Count: " + results.getPublicationCount());
                    Message requestMsg = MessageManager.getInstance().createMessage("Response");
                    requestMsg.setMessageBody(tc.getRelatedRequestMessage());
                    results = ilc.performPublicationOC(tc.getRelatedOperationName(),
                            requestMsg);
//            Thread.sleep(15000);
                }

            } else if (tc.getOperationType().equals(TestCaseSpec.OPERATIONTYPE.HTTPSOAPPUB)) {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        ilc.shutdown();
//        ccc2.stopServices();
//        System.out.println("\n\n\n");
//        System.out.println(results.toString());
//        try {
//            testMessage(results, "Request");
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
        System.gc();


//        RIWSDL theWSDL = new RIWSDL("file:/c:/c2cri/testfiles/release3.wsdl");
//        String serviceName = "tmddOCSoapHttpService";
////        String portName = "tmddOCFtpGetServicePort";
////        String operationName = "OP_RIXMLFtpDMSInventory";
//        String portName = "tmddOCHttpGetServicePort";
//        String operationName = "OP_RIXMLHttpGetDMSInventory";
//        CenterConfigurationController ccc2 = new CenterConfigurationController(null, theWSDL, Center.RICENTERMODE.OC);
//        ccc2.initialzeServices();
//    
//        NTCIP2306Controller ntcip = new NTCIP2306Controller();
////        NTCIP2306ControllerResults results = ntcip.performFTPGetOC(serviceName, portName, operationName, "<Message>This is the Response Message</Message>",ccc2);
////        NTCIP2306ControllerResults results = ntcip.performFTPGetOC(serviceName, portName, operationName, "<deviceInformationRequestMsg>This is the Response Message</deviceInformationRequestMsg>",ccc2);
////        NTCIP2306ControllerResults results = ntcip.performFTPGetOC(serviceName, portName, operationName, "<deviceInformationRequestMsg xmlns=\"http://www.tmdd.org/3/messages\">This is the Response Message</deviceInformationRequestMsg>",ccc2,false);
//        NTCIP2306ControllerResults results = ntcip.performHTTPGetOC(serviceName, portName, operationName, "<deviceInformationRequestMsg xmlns=\"http://www.tmdd.org/3/messages\">This is the Response Message</deviceInformationRequestMsg>",ccc2,false);


    }


}
