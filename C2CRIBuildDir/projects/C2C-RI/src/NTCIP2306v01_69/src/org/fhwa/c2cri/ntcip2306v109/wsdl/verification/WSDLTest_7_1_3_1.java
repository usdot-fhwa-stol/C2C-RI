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
 * The Class WSDLTest_7_1_3_1.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_1_3_1 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_7_1_3_1.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_1_3_1(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("7.1.3.1");
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

            String xPathStatement = "/wsdl:definitions/wsdl:service";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> nameAttributeErrors = new ArrayList<String>();
            boolean instancesFound = false;
            while (xmlc.hasNextSelection()) {
                instancesFound = true;
                xmlc.toNextSelection();

                boolean nameAttributeFound = false;
                boolean noAttributesFound = false;

                if (xmlc.toFirstAttribute()) {
                    do {
                        if (xmlc.getName().getLocalPart().equals("name")) {
                            nameAttributeFound = true;
                            break;
                        }
                    } while (xmlc.toNextAttribute());
                } else {
                    noAttributesFound = true;
                }
                if (!nameAttributeFound || noAttributesFound) {
                    nameAttributeErrors.add("No name attribute was found for a service in the WSDL file. ");

                }


            }

            if (nameAttributeErrors.isEmpty() && instancesFound) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");


                String nameAttributeErrorsMsg = "";
                if (!nameAttributeErrors.isEmpty()) {
                    nameAttributeErrorsMsg = "The WSDL contains " + nameAttributeErrors.size() + " instance(s) of a service tag that does not include a name attribute.";
                }

                String errorMessage = !instancesFound ? "No instances of the service tag were found. \n" : "";
                errorMessage = errorMessage.concat(!nameAttributeErrorsMsg.equals("") ? " name Attribute Errors: " + nameAttributeErrorsMsg : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }


        assertion.setTestAssertionPrescription("The service tag shall contain a name attribute of the service. ");
        return assertion;
    }
}
