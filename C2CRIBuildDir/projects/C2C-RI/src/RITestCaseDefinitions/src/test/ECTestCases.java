/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;


/**
 *
 * @author TransCore ITS,LLC
 */
public class ECTestCases {
        private HashMap<Integer,TestCaseSpec> ecTestCases = new HashMap();
        
        
        public ECTestCases(){
            TestCaseSpec ftpGetEC1 = new TestCaseSpec();
                ftpGetEC1.setOperationType(TestCaseSpec.OPERATIONTYPE.FTPGET);
                ftpGetEC1.setWsdlFile("file:/c:/c2cri/testfiles/release3.wsdl");
                ftpGetEC1.setServiceName("tmddOCSoapHttpService");
                ftpGetEC1.setPortName("tmddOCFtpGetServicePort");
                ftpGetEC1.setOperationName("OP_RIXMLFtpDMSInventory");
                ftpGetEC1.setSkipEncoding(false);
                ftpGetEC1.setTransportErrorExpected(false);
                ftpGetEC1.setEncodingErrorExpected(false);
                ftpGetEC1.setMessageErrorExpected(false);
                ftpGetEC1.setMessageValidationErrorExpected(true);
                ftpGetEC1.setOperationErrorExptected(true);

            ecTestCases.put(1, ftpGetEC1);
                
            TestCaseSpec httpGetEC1 = new TestCaseSpec();
                httpGetEC1.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPGET);
                httpGetEC1.setWsdlFile("file:/c:/c2cri/testfiles/release3.wsdl");
                httpGetEC1.setServiceName("tmddOCSoapHttpService");
                httpGetEC1.setPortName("tmddOCHttpGetServicePort");
                httpGetEC1.setOperationName("OP_RIXMLHttpGetDMSInventory");
                httpGetEC1.setSkipEncoding(false);
                httpGetEC1.setTransportErrorExpected(false);
                httpGetEC1.setEncodingErrorExpected(false);
                httpGetEC1.setMessageErrorExpected(false);
                httpGetEC1.setMessageValidationErrorExpected(true);
                httpGetEC1.setOperationErrorExptected(true);
            ecTestCases.put(2, httpGetEC1);
                            
            TestCaseSpec httpPostEC1 = new TestCaseSpec();
                httpPostEC1.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPPOST);
                httpPostEC1.setWsdlFile("file:/c:/c2cri/testfiles/release3.wsdl");
                httpPostEC1.setServiceName("tmddOCSoapHttpService");
                httpPostEC1.setPortName("tmddOCHttpPostServicePort");
                httpPostEC1.setOperationName("OP_RIXMLHttpPostDMSInventory");
                httpPostEC1.setRequestMessage("<deviceInformationRequestMsg xmlns=\"http://www.tmdd.org/3/messages\">This is the Response Message</deviceInformationRequestMsg>".getBytes());
                httpPostEC1.setSkipEncoding(false);
                httpPostEC1.setTransportErrorExpected(false);
                httpPostEC1.setEncodingErrorExpected(false);
                httpPostEC1.setMessageErrorExpected(false);
                httpPostEC1.setMessageValidationErrorExpected(true);
                httpPostEC1.setOperationErrorExptected(true);
            ecTestCases.put(3, httpPostEC1);
            
            TestCaseSpec httpSOAPRREC1 = new TestCaseSpec();
                httpSOAPRREC1.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
                httpSOAPRREC1.setWsdlFile("file:/c:/c2cri/testfiles/release3.wsdl");
                httpSOAPRREC1.setServiceName("tmddOCSoapHttpService");
                httpSOAPRREC1.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPRREC1.setOperationName("DlDMSInventoryRequest");
                httpSOAPRREC1.setRequestMessage("<deviceInformationRequestMsg xmlns=\"http://www.tmdd.org/3/messages\">This is the Response Message</deviceInformationRequestMsg>".getBytes());
                httpSOAPRREC1.setSkipEncoding(false);
                httpSOAPRREC1.setTransportErrorExpected(false);
                httpSOAPRREC1.setEncodingErrorExpected(false);
                httpSOAPRREC1.setMessageErrorExpected(false);
                httpSOAPRREC1.setMessageValidationErrorExpected(true);
                httpSOAPRREC1.setOperationErrorExptected(true);
            ecTestCases.put(4, httpSOAPRREC1);

            
            
            
            
             TestCaseSpec httpSOAPSUBEC1 = new TestCaseSpec();
                httpSOAPSUBEC1.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB);
                httpSOAPSUBEC1.setWsdlFile("file:/c:/c2cri/support/TDEX-TMDD-tcore.wsdl");
                httpSOAPSUBEC1.setServiceName("tmddOCSoapHttpService");
                httpSOAPSUBEC1.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPSUBEC1.setOperationName("dlCenterActiveVerificationSubscription");
                httpSOAPSUBEC1.setRelatedServiceName("tmddECSoapHttpService");
                httpSOAPSUBEC1.setRelatedPortName("tmddECSoapHttpServicePort");
                httpSOAPSUBEC1.setRelatedOperationName("dlCenterActiveVerificationUpdate");
                try{
                    String subMessage = TestCaseSpec.SOAPENVELOPE.replace(TestCaseSpec.BODYPLACEHOLDERTEXT, new String(XMLFileReader.readFile("C:\\data\\301XMLSamples\\TCS-1-dlCenterActiveVerificationSubscription-OC-Valid.data.xml"),"UTF-8").replace("208.206.232.40", "localhost"));
                    httpSOAPSUBEC1.setRequestMessage(subMessage.getBytes("UTF-8"));
                    httpSOAPSUBEC1.setRelatedResponseMessage("<c2c:c2cMessageReceipt xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><informationalText>Nothing Really EC</informationalText></c2c:c2cMessageReceipt>".getBytes());
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPSUBEC1.setRequestMessage(ex.getMessage().getBytes());                    
                    httpSOAPSUBEC1.setRelatedResponseMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPSUBEC1.setNumPublicationsExpected(2);
                httpSOAPSUBEC1.setSkipEncoding(false);
                httpSOAPSUBEC1.setTransportErrorExpected(false);
                httpSOAPSUBEC1.setEncodingErrorExpected(false);
                httpSOAPSUBEC1.setMessageErrorExpected(false);
                httpSOAPSUBEC1.setMessageValidationErrorExpected(true);
                httpSOAPSUBEC1.setOperationErrorExptected(true);
            ecTestCases.put(6, httpSOAPSUBEC1);
   
            
            
              TestCaseSpec httpSOAPRREC3 = new TestCaseSpec();
                httpSOAPRREC3.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
                httpSOAPRREC3.setWsdlFile("file:/c:/c2cri/testfiles/TMDDv303a/tmdd.wsdl");
                httpSOAPRREC3.setServiceName("tmddOCSoapHttpService");
                httpSOAPRREC3.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPRREC3.setOperationName("dlCenterActiveVerificationRequest");
                try{
                    httpSOAPRREC3.setRequestMessage(XMLFileReader.readFile("C:\\TMDDv303a\\data\\XMLSamples\\TCS-1-dlCenterActiveVerificationRequest-OC-Valid.data.xml"));
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPRREC3.setRequestMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPRREC3.setSkipEncoding(false);
                httpSOAPRREC3.setTransportErrorExpected(false);
                httpSOAPRREC3.setEncodingErrorExpected(false);
                httpSOAPRREC3.setMessageErrorExpected(false);
                httpSOAPRREC3.setMessageValidationErrorExpected(true);
                httpSOAPRREC3.setOperationErrorExptected(true);
            ecTestCases.put(8, httpSOAPRREC3);            
            
            
     TestCaseSpec httpSOAPSUBEC2 = new TestCaseSpec();
                httpSOAPSUBEC2.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB);
                httpSOAPSUBEC2.setWsdlFile("file:/c:/c2cri/testfiles/tmddv303a/tmdd.wsdl");
                httpSOAPSUBEC2.setServiceName("tmddOCSoapHttpService");
                httpSOAPSUBEC2.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPSUBEC2.setOperationName("dlCenterActiveVerificationSubscription");
                httpSOAPSUBEC2.setRelatedServiceName("tmddECSoapHttpService");
                httpSOAPSUBEC2.setRelatedPortName("tmddECSoapHttpServicePort");
                httpSOAPSUBEC2.setRelatedOperationName("dlCenterActiveVerificationUpdate");
                try{
                    String subMessage = TestCaseSpec.SOAPENVELOPE.replace(TestCaseSpec.BODYPLACEHOLDERTEXT, new String(XMLFileReader.readFile("C:\\TMDDv303a\\data\\XMLSamples\\TCS-1-dlCenterActiveVerificationSubscription-OC-Valid.data.xml"),"UTF-8").replace("208.206.232.40", "localhost"));
                    httpSOAPSUBEC2.setRequestMessage(subMessage.getBytes("UTF-8"));
                    httpSOAPSUBEC2.setRelatedResponseMessage("<c2c:c2cMessageReceipt xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><informationalText>Nothing Really EC</informationalText></c2c:c2cMessageReceipt>".getBytes());
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPSUBEC2.setRequestMessage(ex.getMessage().getBytes());                    
                    httpSOAPSUBEC2.setRelatedResponseMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPSUBEC2.setNumPublicationsExpected(2);
                httpSOAPSUBEC2.setSkipEncoding(false);
                httpSOAPSUBEC2.setTransportErrorExpected(false);
                httpSOAPSUBEC2.setEncodingErrorExpected(false);
                httpSOAPSUBEC2.setMessageErrorExpected(false);
                httpSOAPSUBEC2.setMessageValidationErrorExpected(true);
                httpSOAPSUBEC2.setOperationErrorExptected(true);
            ecTestCases.put(9, httpSOAPSUBEC2);
            
            
     TestCaseSpec httpSOAPSUBEC3 = new TestCaseSpec();
                httpSOAPSUBEC3.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB);
                httpSOAPSUBEC3.setWsdlFile("file:/c:/c2cri/testfiles/WSDLFiles/2306TstData1Sd0.wsdl");
                httpSOAPSUBEC3.setServiceName("tmddOCSoapHttpService");
                httpSOAPSUBEC3.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPSUBEC3.setOperationName("OP_DeviceInformationSubscription");
                httpSOAPSUBEC3.setRelatedServiceName("tmddECSoapHttpService");
                httpSOAPSUBEC3.setRelatedPortName("tmddECSoapHttpServicePort");
                httpSOAPSUBEC3.setRelatedOperationName("OP_DeviceInformationUpdate");
                try{
                    String subMessage = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><c2c:c2cMessageSubscription xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><informationalText>string</informationalText><returnAddress>https://C2CRIEC:8086/SoapTry2/tmddECSoapHttpService/tmddECSoapHttpServicePort/OP_DeviceInformationUpdate</returnAddress><subscriptionAction><subscriptionAction-item>newSubscription</subscriptionAction-item></subscriptionAction><subscriptionType><subscriptionType-item>1</subscriptionType-item></subscriptionType><subscriptionID>string</subscriptionID><subscriptionName>string</subscriptionName><subscriptionTimeFrame><start>2006-09-18T19:18:33</start><end>2014-08-19T13:27:14-04:00</end></subscriptionTimeFrame><subscriptionFrequency>7</subscriptionFrequency><broadcastAlerts><broadcastAlert>broadcastAlertsAccepted</broadcastAlert></broadcastAlerts></c2c:c2cMessageSubscription><mes:deviceInformationRequestMsg xmlns:mes=\"http://www.tmdd.org/3/messages\"><authentication><user-id>string</user-id><password>string</password><operator-id>string</operator-id></authentication><organization-information><organization-id>string</organization-id><organization-name>string</organization-name><organization-location>stringstri</organization-location><organization-function>string</organization-function><organization-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></organization-contact-details><center-contact-list><center-contact-details><center-id>string</center-id><center-name>string</center-name><center-location xmlns:lrms=\"http://www.LRMS-Adopted-02-00-00\"><latitude>3</latitude><longitude>3</longitude><horizontalDatum>3</horizontalDatum><height><verticalLevel>2</verticalLevel></height></center-location><center-description>string</center-description><center-type>2</center-type><center-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></center-contact-details></center-contact-details></center-contact-list><last-update-time><date>stringst</date><time>string</time><offset>strin</offset></last-update-time></organization-information><organization-requesting><organization-id>string</organization-id><organization-name>string</organization-name><organization-location>stringstri</organization-location><organization-function>string</organization-function><organization-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></organization-contact-details><center-contact-list><center-contact-details><center-id>string</center-id><center-name>string</center-name><center-location xmlns:lrms=\"http://www.LRMS-Adopted-02-00-00\"><latitude>3</latitude><longitude>3</longitude><horizontalDatum>nad83</horizontalDatum><height><verticalLevel>2</verticalLevel></height></center-location><center-description>string</center-description><center-type>2</center-type><center-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></center-contact-details></center-contact-details></center-contact-list><last-update-time><date>stringst</date><time>string</time><offset>strin</offset></last-update-time></organization-requesting><device-type>7</device-type><device-information-type>insert-extension-values-here</device-information-type><device-filter><device-id-list><device-id>string</device-id></device-id-list><network-id-list><network-id>string</network-id></network-id-list><link-id-list><link>string</link></link-id-list><link-designator-list><link-designator>string</link-designator></link-designator-list><linear-reference>string</linear-reference><section-id-list><section-id>string</section-id></section-id-list><pattern-id-list><pattern-id>string</pattern-id></pattern-id-list></device-filter></mes:deviceInformationRequestMsg></SOAP-ENV:Body></SOAP-ENV:Envelope>";
                    httpSOAPSUBEC3.setRequestMessage(subMessage.getBytes("UTF-8"));
                    httpSOAPSUBEC3.setRelatedResponseMessage("<c2c:c2cMessageReceipt xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><informationalText>Nothing Really EC</informationalText></c2c:c2cMessageReceipt>".getBytes());
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPSUBEC3.setRequestMessage(ex.getMessage().getBytes());                    
                    httpSOAPSUBEC3.setRelatedResponseMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPSUBEC3.setNumPublicationsExpected(1);
                httpSOAPSUBEC3.setSkipEncoding(false);
                httpSOAPSUBEC3.setTransportErrorExpected(false);
                httpSOAPSUBEC3.setEncodingErrorExpected(false);
                httpSOAPSUBEC3.setMessageErrorExpected(false);
                httpSOAPSUBEC3.setMessageValidationErrorExpected(true);
                httpSOAPSUBEC3.setOperationErrorExptected(true);
            ecTestCases.put(10, httpSOAPSUBEC3);
            
            
            
            
            
            
            
            
            TestCaseSpec httpSOAPRREC20 = new TestCaseSpec();
                httpSOAPRREC20.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
                httpSOAPRREC20.setWsdlFile("file:/c:/c2cri/support/TDEX-TMDD-Real.wsdl");
                httpSOAPRREC20.setServiceName("tmddOCSoapHttpService");
                httpSOAPRREC20.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPRREC20.setOperationName("dlCenterActiveVerificationRequest");                
                try{
                    httpSOAPRREC20.setRequestMessage(XMLFileReader.readFile("C:\\data\\301XMLSamples\\TCS-1-dlCenterActiveVerificationRequest-OC-Valid.data.xml"));
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPRREC20.setRequestMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPRREC20.setSkipEncoding(false);
                httpSOAPRREC20.setTransportErrorExpected(false);
                httpSOAPRREC20.setEncodingErrorExpected(false);
                httpSOAPRREC20.setMessageErrorExpected(false);
                httpSOAPRREC20.setMessageValidationErrorExpected(false);
                httpSOAPRREC20.setOperationErrorExptected(false);
            ecTestCases.put(20, httpSOAPRREC20);

            TestCaseSpec httpSOAPRREC21 = new TestCaseSpec();
                httpSOAPRREC21.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
                httpSOAPRREC21.setWsdlFile("file:/c:/c2cri/support/TMDD-Real.wsdl");
                httpSOAPRREC21.setServiceName("tmddOCSoapHttpService");
                httpSOAPRREC21.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPRREC21.setOperationName("dlOrganizationInformationRequest");                
                try{
                    httpSOAPRREC21.setRequestMessage(XMLFileReader.readFile("C:\\data\\301XMLSamples\\TCS-5-dlOrganizationInformationRequest-OC-Valid.data.xml"));
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPRREC21.setRequestMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPRREC21.setSkipEncoding(false);
                httpSOAPRREC21.setTransportErrorExpected(false);
                httpSOAPRREC21.setEncodingErrorExpected(false);
                httpSOAPRREC21.setMessageErrorExpected(false);
                httpSOAPRREC21.setMessageValidationErrorExpected(false);
                httpSOAPRREC21.setOperationErrorExptected(false);
            ecTestCases.put(21, httpSOAPRREC21);
            
        }
        
        public TestCaseSpec get(Integer testCaseID){
            return ecTestCases.get(testCaseID);
        }
    
    
}
