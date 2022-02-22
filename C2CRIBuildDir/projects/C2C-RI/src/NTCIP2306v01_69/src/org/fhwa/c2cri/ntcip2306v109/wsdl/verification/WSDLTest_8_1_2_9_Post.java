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
 * The Class WSDLTest_8_1_2_9_Post.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_8_1_2_9_Post implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_8_1_2_9_ post.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_8_1_2_9_Post(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("8.1.2.9");
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
                    + "declare namespace http='http://schemas.xmlsoap.org/wsdl/http/'; "
                    + "declare namespace mime='http://schemas.xmlsoap.org/wsdl/mime/'; "
                    + "declare namespace soapenc='http://schemas.xmlsoap.org/soap/encoding/'; "
                    + "declare namespace xenc='http://www.w3.org/2001/04/xmlenc#'; "
                    + "declare namespace wsoap12='http://schemas.xmlsoap.org/wsdl/soap12/'; "
                    + "declare namespace wsoap11='http://schemas.xmlsoap.org/wsdl/soap/'; "
                    + "declare namespace soapbind='http://schemas.xmlsoap.org/wsdl/soap/'; ";

            String xPathStatement = "/wsdl:definitions/wsdl:binding[exists(http:binding)]/wsdl:operation[exists(http:operation) and (preceding-sibling::http:binding/@verb=\"POST\")]/wsdl:output";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> mimeContentMissingErrors = new ArrayList<String>();
            String bindingName = "";
            boolean instancesFound = false;
            while (xmlc.hasNextSelection()) {
                instancesFound = true;
                xmlc.toNextSelection();

                boolean mimeContentElementFound = false;
                if (xmlc.toFirstChild()) {
                    do {
                        if (xmlc.getName().getLocalPart().equals("content") && xmlc.getName().getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/mime/")) {
                            mimeContentElementFound = true;


                        }
                    } while (xmlc.toNextSibling());

                    if (!mimeContentElementFound) {
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
                        mimeContentMissingErrors.add("Binding " + bindingName + " contains an operation element " + operation + " that does not contain an http:urlEncoded element after output. ");
                        xmlc.pop();
                    } 

                } else {   // No siblings are present.
                    xmlc.push();
                    bindingName = getParentBindingName(xmlc);
                    mimeContentMissingErrors.add("Binding " + bindingName + " contains a operation element with no siblings. ");
                    xmlc.pop();
                }

            }

            if (mimeContentMissingErrors.isEmpty() && instancesFound) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String mimeContentErrors = "";
                for (String thisName : mimeContentMissingErrors) {
                    if (mimeContentErrors.equals("")) {
                        mimeContentErrors = mimeContentErrors.concat(thisName);
                    } else {
                        mimeContentErrors = mimeContentErrors.concat(", " + thisName);
                    }
                }


                String errorMessage = !instancesFound ? "No instances of the binding tag followed by a http:binding element, operation, http:operation and output tag were found. \n" : "";
                errorMessage = errorMessage.concat(!mimeContentErrors.equals("") ? " Output Tag Errors: " + mimeContentErrors : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }


        assertion.setTestAssertionPrescription("The output tag shall be followed by a mime:content tag. ");
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
