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
 * The Class WSDLTest_7_1_2_10.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_1_2_10 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;
    
    /** The test sub pub. */
    boolean testSubPub = false;

    /**
     * Instantiates a new wSDL test_7_1_2_10.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_1_2_10(RIWSDL wsdlSpec, String wsdlURL) {
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
    }

    /**
     * Instantiates a new wSDL test_7_1_2_10.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     * @param subPubTest the sub pub test
     */
    public WSDLTest_7_1_2_10(RIWSDL wsdlSpec, String wsdlURL, boolean subPubTest) {
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
        this.testSubPub = subPubTest;
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
        assertion.setTestAssertionID("7.1.2.10");
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

            String xPathStatement = "/wsdl:definitions/wsdl:binding[exists(wsoap11:binding)]/wsdl:operation[exists(wsoap11:operation)]/wsdl:output";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> soapBodyMissingErrors = new ArrayList<String>();
            ArrayList<String> useAttributeErrors = new ArrayList<String>();
            ArrayList<String> soapRROperations = wsdlSpec.getSOAPServiceRROperations();
            ArrayList<String> soapSPOperations = wsdlSpec.getSOAPServiceSPOperations();
            
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
                                            if ((this.testSubPub && soapSPOperations.contains(operation))||(!this.testSubPub && soapRROperations.contains(operation)))
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
                        if ((this.testSubPub && soapSPOperations.contains(operation))||(!this.testSubPub && soapRROperations.contains(operation)))
                            soapBodyMissingErrors.add("Binding " + bindingName + " contains an operation element " + operation + " that does not contain an soap:body element after output. ");
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
                        if ((this.testSubPub && soapSPOperations.contains(operation))||(!this.testSubPub && soapRROperations.contains(operation)))
						{
							useAttributeErrors.add("Binding " + bindingName + " contains an operation element " + operation + " which contains an output element whose soap:body element does not contain the use attribute. ");
						}
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
                        if ((this.testSubPub && soapSPOperations.contains(operation))||(!this.testSubPub && soapRROperations.contains(operation)))
                            useAttributeErrors.add("Binding " + bindingName + " contains an operation element " + operation + " which contains an output element whose soap:body element does not contain any attributes. ");
                        xmlc.pop();

                    }

                } else {   // No siblings are present.
                    xmlc.push();
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
                    if ((this.testSubPub && soapSPOperations.contains(operation)) || (!this.testSubPub && soapRROperations.contains(operation))) {
                        soapBodyMissingErrors.add("Binding " + bindingName + " contains a operation element (" + operation + ") with no siblings. ");
                    }
                    xmlc.pop();
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

                String errorMessage = !instancesFound ? "No instances of the binding tag followed by a soap:binding element, operation,soap:operation and output tag were found. \n" : "";
                errorMessage = errorMessage.concat(!soapBodyErrors.equals("") ? " Output Tag Errors: " + soapBodyErrors : "");
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


        assertion.setTestAssertionPrescription("The output tag shall be followed by a soap:body tag. The use attribute of the soap:body tag shall be literal.");
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
