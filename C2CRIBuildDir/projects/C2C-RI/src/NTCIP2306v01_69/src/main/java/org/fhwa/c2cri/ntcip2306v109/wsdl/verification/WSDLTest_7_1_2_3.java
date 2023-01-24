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
 * The Class WSDLTest_7_1_2_3.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_1_2_3 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_7_1_2_3.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_1_2_3(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("7.1.2.3");
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

            String xPathStatement = "/wsdl:definitions/wsdl:binding/wsoap11:binding";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> transportAttributeErrors = new ArrayList<String>();
            boolean instancesFound = false;
            String bindingName = "";
            while (xmlc.hasNextSelection()) {
                instancesFound = true;
                xmlc.toNextSelection();
                boolean transportAttributeFound = false;
                if (xmlc.toFirstAttribute()) {
                    do {
                        if (xmlc.getName().getLocalPart().equals("transport")) {
                            transportAttributeFound = true;
                            if (!xmlc.getTextValue().equals("http://schemas.xmlsoap.org/soap/http")) {
                                String erroneousTransport = xmlc.getTextValue();
                                xmlc.push();
                                xmlc.toParent();  // soap:binding
                                bindingName = getParentBindingName(xmlc);
                                transportAttributeErrors.add("Binding " + bindingName + " contains a soap:binding element with an invalid transpoort attribute: " + erroneousTransport + ". ");
                                xmlc.pop();
                            }
                        }
                    } while (xmlc.toNextAttribute());

                    if (!transportAttributeFound) {
                        xmlc.push();
                        xmlc.toParent();  // soap:binding
                        bindingName = getParentBindingName(xmlc);
                        transportAttributeErrors.add("Binding " + bindingName + " contains a soap:binding element does not contain a transport attribute. ");
                        xmlc.pop();
                    }

                } else {   // No attributes are present.
                    xmlc.push();
                    bindingName = getParentBindingName(xmlc);
                    transportAttributeErrors.add("Binding " + bindingName + " contains a soap:binding element with no attributes. ");
                    xmlc.pop();
                }

            }

            if (transportAttributeErrors.isEmpty() && instancesFound) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String transportErrors = "";
                for (String thisName : transportAttributeErrors) {
                    if (transportErrors.equals("")) {
                        transportErrors = transportErrors.concat(thisName);
                    } else {
                        transportErrors = transportErrors.concat(", " + thisName);
                    }
                }

                String errorMessage = !instancesFound ? "No instances of the binding tag followed by a soap:binding tag were found. \n" : "";
                errorMessage = errorMessage.concat(!transportErrors.equals("") ? " Transport Attribute Errors: " + transportErrors : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }

        assertion.setTestAssertionPrescription("The transport attribute of the soap:binding tag shall be http://schemas.xmlsoap.org/soap/http ");
        return assertion;
    }

    /**
     * Gets the parent binding name.
     *
     * @param xmlc the xmlc
     * @return the parent binding name
     */
    private String getParentBindingName(XmlCursor xmlc) {
        String bindingName = "";
        xmlc.toParent();  // binding

        if (xmlc.toFirstAttribute()) {
            do {
                if (xmlc.getName().getLocalPart().equals("name")) {
                    bindingName = xmlc.getTextValue();
                    break;
                }
            } while (xmlc.toNextAttribute());
        }
        return bindingName;
    }
}
