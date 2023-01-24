/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import java.net.URL;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class WSDLTest_7_2_4_3.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_2_4_3 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_7_2_4_3.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_2_4_3(RIWSDL wsdlSpec, String wsdlURL) {
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
    }

    /**
     * Perform.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    @Override
    public TestAssertion perform() {
        TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("7.2.4.3");
        try {
            URL wsdlURL = new URL(wsdlSpec.getWsdlFileName());
            XmlObject xmlObj = XmlObject.Factory.parse(wsdlURL);
            XmlCursor xmlc = xmlObj.newCursor();

            String xqNamespace =

                    "declare namespace xsl='http://www.w3.org/1999/XSL/Transform'; "
                    + "declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; "
                    + "declare namespace xsd='http://www.w3.org/2001/XMLSchema'; "
                    + "declare namespace wsdl='http://schemas.xmlsoap.org/wsdl/'; "
                    + "declare namespace soap11='http://schemas.xmlsoap.org/soap/envelope/'; "
                    + "declare namespace soap12='http://www.w3.org/2003/05/soap-envelope'; "
                    + "declare namespace soapenc='http://schemas.xmlsoap.org/soap/encoding/'; "
                    + "declare namespace xenc='http://www.w3.org/2001/04/xmlenc#'; "
                    + "declare namespace wsoap12='http://schemas.xmlsoap.org/wsdl/soap12/'; "
                    + "declare namespace wsoap11='http://schemas.xmlsoap.org/wsdl/soap/'; "
                    + "declare namespace soapbind='http://schemas.xmlsoap.org/wsdl/soap/'; ";


            ArrayList<String> bindingList = new ArrayList<String>();
            String xPathStatement = "/wsdl:definitions/wsdl:binding/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);
            while (xmlc.hasNextSelection()){
                xmlc.toNextSelection();
                bindingList.add(xmlc.getTextValue());
            }


            xPathStatement = "/wsdl:definitions/wsdl:service";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> childOrderErrors = new ArrayList<String>();
            ArrayList<String> attributeErrors = new ArrayList<String>();

            boolean instancesFound = false;
            while (xmlc.hasNextSelection()){
                xmlc.toNextSelection();


                // Get the service name attribute
                String serviceName = "[Not Defined]";
                xmlc.push();
                if (xmlc.toFirstAttribute()){
                    do {
                        if (xmlc.getName().getLocalPart().equals("name")){
                            serviceName = xmlc.getTextValue();
                        }
                    }
                    while (xmlc.toNextAttribute());
                }
                xmlc.pop();


                boolean portTagEncountered = false;

                if (xmlc.toFirstChild()){
                    do {
                        if (xmlc.getName().getLocalPart().equals("documentation")){
                            if (portTagEncountered){
                                childOrderErrors.add("The documentation tag follows the port tag in service "+serviceName);
                            }

                        } else if (xmlc.getName().getLocalPart().equals("port")){
                            instancesFound = true;
                            portTagEncountered = true;
                            xmlc.push();
                            boolean nameAttributePresent = false;
                            boolean bindingAttributePresent = false;

                            if (xmlc.toFirstAttribute()){
                                do {
                                    if (xmlc.getName().getLocalPart().equals("name")){
                                        nameAttributePresent = true;
                                    } else if (xmlc.getName().getLocalPart().equals("binding")){
                                        bindingAttributePresent = true;
                                        if (!bindingList.contains(xmlc.getTextValue().replaceFirst("tns:", ""))){
                                            attributeErrors.add("The binding name " + xmlc.getTextValue()+"associated with service "+ serviceName + " is not defined in the bindings section of the WSDL.");
                                        }
                                    }
                                }
                                while (xmlc.toNextAttribute());
                            }
                            if (!nameAttributePresent){
                                attributeErrors.add("The name attribute for a port was not set for service " + serviceName);
                            }
                            if (!bindingAttributePresent){
                                attributeErrors.add("The binding attribute for a port was not set for service " + serviceName);
                            }
                            xmlc.pop();

                        }
                    }
                    while (xmlc.toNextSibling());
                }
            }



            if (childOrderErrors.isEmpty()&&attributeErrors.isEmpty() && instancesFound) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String attributeErrorsMsg = "";
                for (String thisName : attributeErrors) {
                    if (attributeErrorsMsg.equals("")) {
                        attributeErrorsMsg = attributeErrorsMsg.concat(thisName);
                    } else {
                        attributeErrorsMsg = attributeErrorsMsg.concat(", " + thisName);
                    }
                }

                String orderErrorsMsg = "";
                for (String thisName : childOrderErrors) {
                    if (orderErrorsMsg.equals("")) {
                        orderErrorsMsg = orderErrorsMsg.concat(thisName);
                    } else {
                        orderErrorsMsg = orderErrorsMsg.concat(", " + thisName);
                    }
                }


                String errorMessage = !instancesFound ? "No instances of the port tag were found as part of a service definition. \n" : "";
                errorMessage = errorMessage.concat(!orderErrorsMsg.equals("") ? " Element Order Errors: " + orderErrorsMsg : "");
                errorMessage = errorMessage.concat(!attributeErrorsMsg.equals("") ? " Attribute Errors: " + attributeErrorsMsg : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }


        assertion.setTestAssertionPrescription("The port tag shall follow the <documentation> tag, if one is present. The name attribute of the port tag will reflect the name of a SOAP port followed by the binding attribute indicating the name of the binding identified in the bindings section which applies. ");
        return assertion;
    }
}
