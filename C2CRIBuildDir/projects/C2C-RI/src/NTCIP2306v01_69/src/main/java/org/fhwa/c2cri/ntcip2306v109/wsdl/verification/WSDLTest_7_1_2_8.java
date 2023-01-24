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
 * The Class WSDLTest_7_1_2_8.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_1_2_8 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_7_1_2_8.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_1_2_8(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("7.1.2.8");
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

            String xPathStatement = "/wsdl:definitions/wsdl:binding[exists(wsoap11:binding)]/wsdl:operation[exists(wsoap11:operation)]/wsdl:input";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> soapBodyMissingErrors = new ArrayList<String>();
            ArrayList<String> useAttributeErrors = new ArrayList<String>();
            String bindingName = "";
            boolean instancesFound = false;
            while (xmlc.hasNextSelection()) {
                instancesFound = true;
                xmlc.toNextSelection();

                boolean soapBodyElementFound = false;
                boolean useAttributeFound = false;
                boolean noAttributesFound = false;
                if (xmlc.toFirstChild()) {
                    do {
                        if (xmlc.getName().getLocalPart().equals("body") && xmlc.getName().getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/soap/")) {
                            soapBodyElementFound = true;

                            if (xmlc.toFirstAttribute()) {
                                do {
                                    if (xmlc.getName().getLocalPart().equals("use")) {
                                        useAttributeFound = true;
                                        if (!xmlc.getTextValue().equals("literal")) {
                                            String useValue = xmlc.getTextValue();
                                            xmlc.push();
                                            xmlc.toParent(); //soap:body
                                            xmlc.toParent(); // input
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
                                            useAttributeErrors.add("Binding " + bindingName + " contains an operation element " + operation + " containing an input element containing a soap:body element with a use attribute set to "+useValue+".");
                                            xmlc.pop();

                                        }
                                        break;
                                    }
                                } while (xmlc.toNextAttribute());
                            } else {
                                noAttributesFound = true;
                            }


                        }
                    } while (xmlc.toNextSibling());

                    if (!soapBodyElementFound) {
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
                        soapBodyMissingErrors.add("Binding " + bindingName + " contains an operation element " + operation + " that does not contain an soap:body element after input. ");
                        xmlc.pop();
                    } else if (!useAttributeFound){
                        xmlc.push();
                        xmlc.toParent();  // soap:body
                        xmlc.toParent();  // input
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
                        useAttributeErrors.add("Binding " + bindingName + " contains an operation element " + operation + " which contains an input element whose soap:body element does not contain the use attribute. ");
                        xmlc.pop();

                    } else if (noAttributesFound){
                        xmlc.push();
                        xmlc.toParent();  // input
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
                        useAttributeErrors.add("Binding " + bindingName + " contains an operation element " + operation + " which contains an input element whose soap:body element does not contain any attributes. ");
                        xmlc.pop();

                    }

                } else {   // No siblings are present.
                    xmlc.push();
                    bindingName = getParentBindingName(xmlc);
                    soapBodyMissingErrors.add("Binding " + bindingName + " contains a operation element with no siblings. ");
                    xmlc.pop();
                }

            }

            if (soapBodyMissingErrors.isEmpty() &&useAttributeErrors.isEmpty()&& instancesFound) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String soapBodyErrors = "";
                for (String thisName : soapBodyMissingErrors) {
                    if (soapBodyErrors.equals("")) {
                        soapBodyErrors = soapBodyErrors.concat(thisName);
                    } else {
                        soapBodyErrors = soapBodyErrors.concat(", " + thisName);
                    }
                }

                String useAttributeErrorsMsg = "";
                for (String thisName : useAttributeErrors) {
                    if (useAttributeErrorsMsg.equals("")) {
                        useAttributeErrorsMsg = useAttributeErrorsMsg.concat(thisName);
                    } else {
                        useAttributeErrorsMsg = useAttributeErrorsMsg.concat(", " + thisName);
                    }
                }

                String errorMessage = !instancesFound ? "No instances of the binding tag followed by a soap:binding element, operation,soap:operation and input tag were found. \n" : "";
                errorMessage = errorMessage.concat(!soapBodyErrors.equals("") ? " Input Tag Errors: " + soapBodyErrors : "");
                errorMessage = errorMessage.concat(!useAttributeErrorsMsg.equals("") ? " use Attribute Errors: " + useAttributeErrorsMsg : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }


        assertion.setTestAssertionPrescription("The input tag shall be followed by a soap:body tag. The use attribute of the soap:body tag shall be literal. This specifies that the message content of the SOAP message is an XML message that conforms with the XML Schema referenced in the schema section of the WSDL, which in turn is enclosed between a soap:envelope and soap:body tags. ");
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
