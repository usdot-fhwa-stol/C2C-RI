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
 * The Class WSDLTest_7_1_2_2.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_1_2_2 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_7_1_2_2.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_1_2_2(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("7.1.2.2");
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

            ArrayList<String> styleAttributeErrors = new ArrayList<String>();
            boolean instancesFound = false;
            String bindingName = "";
            while (xmlc.hasNextSelection()) {
                instancesFound = true;
                xmlc.toNextSelection();
                boolean styleAttributeFound = false;
                if (xmlc.toFirstAttribute()) {
                    do {
                        if (xmlc.getName().getLocalPart().equals("style")) {
                            styleAttributeFound = true;
                            if (!xmlc.getTextValue().equals("document")) {
                                String erroneousStyle = xmlc.getTextValue();
                                xmlc.push();
                                xmlc.toParent();  // soap:binding
                                bindingName = getParentBindingName(xmlc);
                                styleAttributeErrors.add("Binding " + bindingName + " contains a soap:binding element with an invalid style attribute: " + erroneousStyle + ". ");
                                xmlc.pop();
                            }
                        }
                    } while (xmlc.toNextAttribute());

                    if (!styleAttributeFound) {
                        xmlc.push();
                        xmlc.toParent();  // soap:binding
                        bindingName = getParentBindingName(xmlc);
                        styleAttributeErrors.add("Binding " + bindingName + " contains a soap:binding element does not contain a style attribute. ");
                        xmlc.pop();
                    }

                } else {   // No attributes are present.
                    xmlc.push();
                    bindingName = getParentBindingName(xmlc);
                    styleAttributeErrors.add("Binding " + bindingName + " contains a soap:binding element with no attributes. ");
                    xmlc.pop();
                }

            }

            if (styleAttributeErrors.isEmpty() && instancesFound) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String styleErrors = "";
                for (String thisName : styleAttributeErrors) {
                    if (styleErrors.equals("")) {
                        styleErrors = styleErrors.concat(thisName);
                    } else {
                        styleErrors = styleErrors.concat(", " + thisName);
                    }
                }

                String errorMessage = !instancesFound ? "No instances of the binding tag followed by a soap:binding tag were found. \n" : "";
                errorMessage = errorMessage.concat(!styleErrors.equals("") ? " Style Attribute Errors: " + styleErrors : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }

        assertion.setTestAssertionPrescription("The style attribute of the soap:binding tag shall be document. ");
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
