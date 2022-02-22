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
 * The Class WSDLTest_9_1_3_5.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_9_1_3_5 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_9_1_3_5.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_9_1_3_5(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("9.1.3.5");
        try {
            URL wsdlURL = new URL(wsdlSpec.getWsdlFileName());
            XmlObject xmlObj = XmlObject.Factory.parse(wsdlURL);
            XmlCursor xmlc = xmlObj.newCursor();

            String xqNamespace =

                    "declare namespace xsl='http://www.w3.org/1999/XSL/Transform'; "
                    + "declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; "
                    + "declare namespace xsd='http://www.w3.org/2001/XMLSchema'; "
                    + "declare namespace wsdl='http://schemas.xmlsoap.org/wsdl/'; "
                    + "declare namespace ftp='http://schemas.ntcip.org/wsdl/ftp/'; "
                    + "declare namespace soap11='http://schemas.xmlsoap.org/soap/envelope/'; "
                    + "declare namespace soap12='http://www.w3.org/2003/05/soap-envelope'; "
                    + "declare namespace soapenc='http://schemas.xmlsoap.org/soap/encoding/'; "
                    + "declare namespace xenc='http://www.w3.org/2001/04/xmlenc#'; "
                    + "declare namespace wsoap12='http://schemas.xmlsoap.org/wsdl/soap12/'; "
                    + "declare namespace wsoap11='http://schemas.xmlsoap.org/wsdl/soap/'; "
                    + "declare namespace soapbind='http://schemas.xmlsoap.org/wsdl/soap/'; ";

            String xPathStatement = "/wsdl:definitions/wsdl:binding[exists(ftp:binding)]/wsdl:operation[exists(ftp:operation)]/wsdl:input";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> ftpUrlEncodedMissingErrors = new ArrayList<String>();
            String bindingName = "";
            boolean instancesFound = false;
            while (xmlc.hasNextSelection()) {
                instancesFound = true;
                xmlc.toNextSelection();

                boolean ftpUrlEncodedElementFound = false;
                if (xmlc.toFirstChild()) {
                    do {
                        if (xmlc.getName().getLocalPart().equals("urlEncoded") && xmlc.getName().getNamespaceURI().equals("http://schemas.ntcip.org/wsdl/ftp/")) {
                            ftpUrlEncodedElementFound = true;

                        }
                    } while (xmlc.toNextSibling());

                    if (!ftpUrlEncodedElementFound) {
                        xmlc.push();
                        xmlc.toParent();  // operation
                        String operation = "[Not Found]";
                        if (xmlc.toFirstAttribute()) {
                            do {
                                if (xmlc.getName().getLocalPart().equals("name")) {
                                    operation = xmlc.getTextValue();
                                    break;
                                }
                            } while (xmlc.toNextAttribute());
                            xmlc.toParent();  // operation

                        }

                        bindingName = getParentBindingName(xmlc);
                        ftpUrlEncodedMissingErrors.add("Binding " + bindingName + " contains an operation element " + operation + " that does not contain an ftp:urlEncoded element after input. ");
                        xmlc.pop();
                    }

                } else {   // No siblings are present.
                    xmlc.push();
                    bindingName = getParentBindingName(xmlc);
                    ftpUrlEncodedMissingErrors.add("Binding " + bindingName + " contains a operation element with no siblings. ");
                    xmlc.pop();
                }

            }

            if (ftpUrlEncodedMissingErrors.isEmpty() && instancesFound) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String urlEncodedErrors = "";
                for (String thisName : ftpUrlEncodedMissingErrors) {
                    if (urlEncodedErrors.equals("")) {
                        urlEncodedErrors = urlEncodedErrors.concat(thisName);
                    } else {
                        urlEncodedErrors = urlEncodedErrors.concat(", " + thisName);
                    }
                }


                String errorMessage = !instancesFound ? "No instances of the binding tag followed by a ftp:binding element, operation,ftp:operation and input tag were found. \n" : "";
                errorMessage = errorMessage.concat(!urlEncodedErrors.equals("") ? " Input Tag Errors: " + urlEncodedErrors : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }


        assertion.setTestAssertionPrescription("The input tag shall be followed by a ftp:urlEncoded tag.");
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
