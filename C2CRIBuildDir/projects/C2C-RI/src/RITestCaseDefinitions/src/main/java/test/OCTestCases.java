/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;

/**
 *
 * @author TransCore ITS, LLC
 */
public class OCTestCases {
        private HashMap<Integer,TestCaseSpec> ocTestCases = new HashMap();
        
        public OCTestCases(){
            
            TestCaseSpec ftpGetOC1 = new TestCaseSpec();
                ftpGetOC1.setOperationType(TestCaseSpec.OPERATIONTYPE.FTPGET);
                ftpGetOC1.setWsdlFile("file:/c:/c2cri/testfiles/release3.wsdl");
                ftpGetOC1.setServiceName("tmddOCSoapHttpService");
                ftpGetOC1.setPortName("tmddOCFtpGetServicePort");
                ftpGetOC1.setOperationName("OP_RIXMLFtpDMSInventory");
                ftpGetOC1.setResponseMessage("<mes:deviceInformationRequestMsg xmlns:mes=\"http://www.tmdd.org/3/messages\"><authentication><user-id>string</user-id><password>string</password><operator-id>string</operator-id></authentication><organization-information><organization-id>string</organization-id><organization-name>string</organization-name><organization-location>stringstri</organization-location><organization-function>string</organization-function><organization-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></organization-contact-details><center-contact-list><center-contact-details><center-id>string</center-id><center-name>string</center-name><center-location xmlns:lrms=\"http://www.LRMS-Adopted-02-00-00\"><latitude>3</latitude><longitude>3</longitude><horizontalDatum>3</horizontalDatum><height><verticalLevel>2</verticalLevel></height></center-location><center-description>string</center-description><center-type>2</center-type><center-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></center-contact-details></center-contact-details></center-contact-list><last-update-time><date>stringst</date><time>string</time><offset>strin</offset></last-update-time></organization-information><organization-requesting><organization-id>string</organization-id><organization-name>string</organization-name><organization-location>stringstri</organization-location><organization-function>string</organization-function><organization-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></organization-contact-details><center-contact-list><center-contact-details><center-id>string</center-id><center-name>string</center-name><center-location xmlns:lrms=\"http://www.LRMS-Adopted-02-00-00\"><latitude>3</latitude><longitude>3</longitude><horizontalDatum>nad83</horizontalDatum><height><verticalLevel>2</verticalLevel></height></center-location><center-description>string</center-description><center-type>2</center-type><center-contact-details><contact-id>string</contact-id><person-name>string</person-name><person-title>string</person-title><phone-number>string</phone-number><phone-alternate>string</phone-alternate><mobile-phone-number>string</mobile-phone-number><mobile-phone-id>string</mobile-phone-id><fax-number>string</fax-number><pager-number>string</pager-number><pager-id>string</pager-id><email-address>string</email-address><radio-unit>string</radio-unit><address-line1>string</address-line1><address-line2>string</address-line2><city>string</city><state>st</state><zip-code>string</zip-code><country>string</country></center-contact-details></center-contact-details></center-contact-list><last-update-time><date>stringst</date><time>string</time><offset>strin</offset></last-update-time></organization-requesting><device-type>7</device-type><device-information-type>insert-extension-values-here</device-information-type><device-filter><device-id-list><device-id>string</device-id></device-id-list><network-id-list><network-id>string</network-id></network-id-list><link-id-list><link>string</link></link-id-list><link-designator-list><link-designator>string</link-designator></link-designator-list><linear-reference>string</linear-reference><section-id-list><section-id>string</section-id></section-id-list><pattern-id-list><pattern-id>string</pattern-id></pattern-id-list></device-filter></mes:deviceInformationRequestMsg>".getBytes());
                ftpGetOC1.setSkipEncoding(false);
                ftpGetOC1.setTransportErrorExpected(false);
                ftpGetOC1.setEncodingErrorExpected(false);
                ftpGetOC1.setMessageErrorExpected(false);
                ftpGetOC1.setMessageValidationErrorExpected(true);
                ftpGetOC1.setOperationErrorExptected(true);
            ocTestCases.put(1, ftpGetOC1);
                
            TestCaseSpec httpGetOC1 = new TestCaseSpec();
                httpGetOC1.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPGET);
                httpGetOC1.setWsdlFile("file:/c:/c2cri/testfiles/release3.wsdl");
                httpGetOC1.setServiceName("tmddOCSoapHttpService");
                httpGetOC1.setPortName("tmddOCHttpGetServicePort");
                httpGetOC1.setOperationName("OP_RIXMLHttpGetDMSInventory");
                httpGetOC1.setResponseMessage("<deviceInformationRequestMsg xmlns=\"http://www.tmdd.org/3/messages\">This is the Response Message</deviceInformationRequestMsg>".getBytes());
                httpGetOC1.setSkipEncoding(false);
                httpGetOC1.setTransportErrorExpected(false);
                httpGetOC1.setEncodingErrorExpected(false);
                httpGetOC1.setMessageErrorExpected(false);
                httpGetOC1.setMessageValidationErrorExpected(true);
                httpGetOC1.setOperationErrorExptected(true);
            ocTestCases.put(2, httpGetOC1);
            
            TestCaseSpec httpPostOC1 = new TestCaseSpec();
                httpPostOC1.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPPOST);
                httpPostOC1.setWsdlFile("file:/c:/c2cri/testfiles/release3.wsdl");
                httpPostOC1.setServiceName("tmddOCSoapHttpService");
                httpPostOC1.setPortName("tmddOCHttpPostServicePort");
                httpPostOC1.setOperationName("OP_RIXMLHttpPostDMSInventory");
                httpPostOC1.setResponseMessage("<dMSInventoryMsg xmlns=\"http://www.tmdd.org/3/messages\">This is the Response Message</dMSInventoryMsg>".getBytes());
                httpPostOC1.setSkipEncoding(false);
                httpPostOC1.setTransportErrorExpected(false);
                httpPostOC1.setEncodingErrorExpected(false);
                httpPostOC1.setMessageErrorExpected(false);
                httpPostOC1.setMessageValidationErrorExpected(true);
                httpPostOC1.setOperationErrorExptected(true);
            ocTestCases.put(3, httpPostOC1);
            
              TestCaseSpec httpSOAPRROC1 = new TestCaseSpec();
                httpSOAPRROC1.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
                httpSOAPRROC1.setWsdlFile("file:/c:/c2cri/testfiles/release3.wsdl");
                httpSOAPRROC1.setServiceName("tmddOCSoapHttpService");
                httpSOAPRROC1.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPRROC1.setOperationName("DlDMSInventoryRequest");
                httpSOAPRROC1.setResponseMessage("<dMSInventoryMsg xmlns=\"http://www.tmdd.org/3/messages\">This is the Response Message</dMSInventoryMsg>".getBytes());
                httpSOAPRROC1.setSkipEncoding(false);
                httpSOAPRROC1.setTransportErrorExpected(false);
                httpSOAPRROC1.setEncodingErrorExpected(false);
                httpSOAPRROC1.setMessageErrorExpected(false);
                httpSOAPRROC1.setMessageValidationErrorExpected(true);
                httpSOAPRROC1.setOperationErrorExptected(true);
            ocTestCases.put(4, httpSOAPRROC1);
          
              TestCaseSpec httpSOAPRROC2 = new TestCaseSpec();
                httpSOAPRROC2.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
                httpSOAPRROC2.setWsdlFile("file:/c:/c2cri/support/TDEX-TMDD-tcore.wsdl");
                httpSOAPRROC2.setServiceName("tmddOCSoapHttpService");
                httpSOAPRROC2.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPRROC2.setOperationName("dlCenterActiveVerificationRequest");
                try{
                    httpSOAPRROC2.setResponseMessage(XMLFileReader.readFile("C:\\data\\301XMLSamples\\TCS-1-dlCenterActiveVerificationRequest-EC-Valid.data.xml"));
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPRROC2.setResponseMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPRROC2.setSkipEncoding(false);
                httpSOAPRROC2.setTransportErrorExpected(false);
                httpSOAPRROC2.setEncodingErrorExpected(false);
                httpSOAPRROC2.setMessageErrorExpected(false);
                httpSOAPRROC2.setMessageValidationErrorExpected(true);
                httpSOAPRROC2.setOperationErrorExptected(true);
            ocTestCases.put(5, httpSOAPRROC2);

              TestCaseSpec httpSOAPSUBOC1 = new TestCaseSpec();
                httpSOAPSUBOC1.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB);
                httpSOAPSUBOC1.setWsdlFile("file:/c:/c2cri/support/TDEX-TMDD-tcore.wsdl");
                httpSOAPSUBOC1.setServiceName("tmddOCSoapHttpService");
                httpSOAPSUBOC1.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPSUBOC1.setOperationName("dlCenterActiveVerificationSubscription");
                httpSOAPSUBOC1.setRelatedServiceName("tmddECSoapHttpService");
                httpSOAPSUBOC1.setRelatedPortName("tmddECSoapHttpServicePort");
                httpSOAPSUBOC1.setRelatedOperationName("dlCenterActiveVerificationUpdate");
                try{
               httpSOAPSUBOC1.setResponseMessage("<c2c:c2cMessageReceipt xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><informationalText>Nothing Really</informationalText></c2c:c2cMessageReceipt>".getBytes());
                   String pubMessage = TestCaseSpec.SOAPENVELOPE.replace(TestCaseSpec.BODYPLACEHOLDERTEXT, new String(XMLFileReader.readFile("C:\\data\\301XMLSamples\\TCS-1-dlCenterActiveVerificationUpdate-EC-Valid.data.xml"),"UTF-8"));
                   httpSOAPSUBOC1.setRelatedRequestMessage(pubMessage.getBytes("UTF-8"));
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPSUBOC1.setResponseMessage(ex.getMessage().getBytes());                    
                    httpSOAPSUBOC1.setRelatedRequestMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPSUBOC1.setNumPublicationsExpected(2);
                httpSOAPSUBOC1.setSkipEncoding(false);
                httpSOAPSUBOC1.setTransportErrorExpected(false);
                httpSOAPSUBOC1.setEncodingErrorExpected(false);
                httpSOAPSUBOC1.setMessageErrorExpected(false);
                httpSOAPSUBOC1.setMessageValidationErrorExpected(true);
                httpSOAPSUBOC1.setOperationErrorExptected(true);
            ocTestCases.put(6, httpSOAPSUBOC1);
            
            
            
              TestCaseSpec httpSOAPRROC3 = new TestCaseSpec();
                httpSOAPRROC3.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
                httpSOAPRROC3.setWsdlFile("file:/c:/c2cri/testfiles/TMDDv303a/tmdd.wsdl");
                httpSOAPRROC3.setServiceName("tmddOCSoapHttpService");
                httpSOAPRROC3.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPRROC3.setOperationName("dlCenterActiveVerificationRequest");
                try{
                    httpSOAPRROC3.setResponseMessage(XMLFileReader.readFile("C:\\TMDDv303a\\data\\XMLSamples\\TCS-1-dlCenterActiveVerificationRequest-EC-Valid.data.xml"));
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPRROC3.setResponseMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPRROC3.setSkipEncoding(false);
                httpSOAPRROC3.setTransportErrorExpected(false);
                httpSOAPRROC3.setEncodingErrorExpected(false);
                httpSOAPRROC3.setMessageErrorExpected(false);
                httpSOAPRROC3.setMessageValidationErrorExpected(true);
                httpSOAPRROC3.setOperationErrorExptected(true);
            ocTestCases.put(8, httpSOAPRROC3);
            
              TestCaseSpec httpSOAPSUBOC2 = new TestCaseSpec();
                httpSOAPSUBOC2.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB);
                httpSOAPSUBOC2.setWsdlFile("file:/c:/c2cri/testfiles/tmddv303a/tmdd.wsdl");
                httpSOAPSUBOC2.setServiceName("tmddOCSoapHttpService");
                httpSOAPSUBOC2.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPSUBOC2.setOperationName("dlCenterActiveVerificationSubscription");
                httpSOAPSUBOC2.setRelatedServiceName("tmddECSoapHttpService");
                httpSOAPSUBOC2.setRelatedPortName("tmddECSoapHttpServicePort");
                httpSOAPSUBOC2.setRelatedOperationName("dlCenterActiveVerificationUpdate");
                try{
               httpSOAPSUBOC2.setResponseMessage("<c2c:c2cMessageReceipt xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><informationalText>Nothing Really</informationalText></c2c:c2cMessageReceipt>".getBytes());
                   String pubMessage = TestCaseSpec.SOAPENVELOPE.replace(TestCaseSpec.BODYPLACEHOLDERTEXT, new String(XMLFileReader.readFile("C:\\TMDDv303a\\data\\XMLSamples\\TCS-1-dlCenterActiveVerificationUpdate-EC-Valid.data.xml"),"UTF-8"));
                   httpSOAPSUBOC2.setRelatedRequestMessage(pubMessage.getBytes("UTF-8"));
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPSUBOC2.setResponseMessage(ex.getMessage().getBytes());                    
                    httpSOAPSUBOC2.setRelatedRequestMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPSUBOC2.setNumPublicationsExpected(2);
                httpSOAPSUBOC2.setSkipEncoding(false);
                httpSOAPSUBOC2.setTransportErrorExpected(false);
                httpSOAPSUBOC2.setEncodingErrorExpected(false);
                httpSOAPSUBOC2.setMessageErrorExpected(false);
                httpSOAPSUBOC2.setMessageValidationErrorExpected(true);
                httpSOAPSUBOC2.setOperationErrorExptected(true);
            ocTestCases.put(9, httpSOAPSUBOC2);
            
            
            
              TestCaseSpec httpSOAPSUBOC3 = new TestCaseSpec();
                httpSOAPSUBOC3.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPSUB);
                httpSOAPSUBOC3.setWsdlFile("file:/c:/c2cri/testfiles/WSDLFiles/2306TstData1Sd0.wsdl");
                httpSOAPSUBOC3.setServiceName("tmddOCSoapHttpService");
                httpSOAPSUBOC3.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPSUBOC3.setOperationName("OP_DeviceInformationSubscription");
                httpSOAPSUBOC3.setRelatedServiceName("tmddECSoapHttpService");
                httpSOAPSUBOC3.setRelatedPortName("tmddECSoapHttpServicePort");
                httpSOAPSUBOC3.setRelatedOperationName("OP_DeviceInformationUpdate");
                try{
               httpSOAPSUBOC3.setResponseMessage("<c2c:c2cMessageReceipt xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><informationalText>Nothing Really</informationalText></c2c:c2cMessageReceipt>".getBytes());
                   httpSOAPSUBOC3.setRelatedRequestMessage("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header/><SOAP-ENV:Body><c2c:c2cMessagePublication xmlns:c2c=\"http://www.ntcip.org/c2c-message-administration\"><subscriptionID>string</subscriptionID><subscriptionCount>55</subscriptionCount></c2c:c2cMessagePublication><centerActiveVerificationResponseMsg xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns=\"http://www.tmdd.org/3/messages\"><restrictions xmlns=\"\"><organization-information-forwarding-restrictions>unrestricted</organization-information-forwarding-restrictions></restrictions><organization-information xmlns=\"\"><organization-id>orgid</organization-id></organization-information><center-id xmlns=\"\">CenterID</center-id><center-name xmlns=\"\">Test Center Name</center-name></centerActiveVerificationResponseMsg></SOAP-ENV:Body></SOAP-ENV:Envelope>".getBytes());
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPSUBOC3.setResponseMessage(ex.getMessage().getBytes());                    
                    httpSOAPSUBOC3.setRelatedRequestMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPSUBOC3.setNumPublicationsExpected(1);
                httpSOAPSUBOC3.setSkipEncoding(false);
                httpSOAPSUBOC3.setTransportErrorExpected(false);
                httpSOAPSUBOC3.setEncodingErrorExpected(false);
                httpSOAPSUBOC3.setMessageErrorExpected(false);
                httpSOAPSUBOC3.setMessageValidationErrorExpected(true);
                httpSOAPSUBOC3.setOperationErrorExptected(true);
            ocTestCases.put(10, httpSOAPSUBOC3);
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
              TestCaseSpec httpSOAPRROC20 = new TestCaseSpec();
                httpSOAPRROC20.setOperationType(TestCaseSpec.OPERATIONTYPE.HTTPSOAPRR);
                httpSOAPRROC20.setWsdlFile("file:/c:/c2cri/support/TMDD-core.wsdl");
                httpSOAPRROC20.setServiceName("tmddOCSoapHttpService");
                httpSOAPRROC20.setPortName("tmddOCSoapHttpServicePort");
                httpSOAPRROC20.setOperationName("dlOrganizationInformationRequest");
                try{
                    httpSOAPRROC20.setResponseMessage(XMLFileReader.readFile("C:\\data\\301XMLSamples\\TCS-5-dlOrganizationInformationRequest-EC-Valid.data.xml"));
                } catch (Exception ex){
                    ex.printStackTrace();
                    httpSOAPRROC20.setResponseMessage(ex.getMessage().getBytes());                    
                }
                httpSOAPRROC20.setSkipEncoding(false);
                httpSOAPRROC20.setTransportErrorExpected(false);
                httpSOAPRROC20.setEncodingErrorExpected(false);
                httpSOAPRROC20.setMessageErrorExpected(false);
                httpSOAPRROC20.setMessageValidationErrorExpected(true);
                httpSOAPRROC20.setOperationErrorExptected(true);
            ocTestCases.put(20, httpSOAPRROC2);
            
        }    
 
                
        
        public TestCaseSpec get(Integer testCaseID){
            return ocTestCases.get(testCaseID);
        }
}
