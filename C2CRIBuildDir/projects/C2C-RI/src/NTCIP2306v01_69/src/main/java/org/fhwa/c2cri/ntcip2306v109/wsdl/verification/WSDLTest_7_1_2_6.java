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
 * The Class WSDLTest_7_1_2_6.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_1_2_6 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_7_1_2_6.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_1_2_6(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("7.1.2.6");
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

            ArrayList<String> subscriptionOperations = new ArrayList<String>();
            ArrayList<String> subscriptionMessages = new ArrayList<String>();
            ArrayList<String> publicationOperations = new ArrayList<String>();
            ArrayList<String> publicationMessages = new ArrayList<String>();
            ArrayList<String> multipartMessages = new ArrayList();
            ArrayList<String> multipartOperations = new ArrayList();
            
            // Gather the list of subscription messages
            String xPathStatement = "/wsdl:definitions/wsdl:message[exists(child::wsdl:part[2])][child::wsdl:part[1]/@element=\"c2c:c2cMessageSubscription\"]/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            while (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                subscriptionMessages.add(xmlc.getTextValue());
            }

            // Gather the list of subscription operations
            for (String subscriptionMessage : subscriptionMessages) {
                xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation[child::wsdl:input/@message=\"" + "tns:" + subscriptionMessage + "\"]/@name";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                while (xmlc.hasNextSelection()) {
                    xmlc.toNextSelection();
                    subscriptionOperations.add(xmlc.getTextValue());
                }

            }

            // Gather the list of publication messages
            xPathStatement = "/wsdl:definitions/wsdl:message[exists(child::wsdl:part[2])][child::wsdl:part[1]/@element=\"c2c:c2cMessagePublication\"]/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            while (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                publicationMessages.add(xmlc.getTextValue());
            }

            // Gather the list of publication operations
            for (String publicationMessage : publicationMessages) {
                xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation[child::wsdl:input/@message=\"" + "tns:" + publicationMessage + "\"]/@name";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                while (xmlc.hasNextSelection()) {
                    xmlc.toNextSelection();
                    publicationOperations.add(xmlc.getTextValue());
                }

            }

            // This code was added to avoid throwing errors for multipart messages that may not be specified consistent with NTCIP 2306.
            // Gather the list of multipart messages
            xPathStatement = "/wsdl:definitions/wsdl:message[exists(child::wsdl:part[2])]/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            while (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                multipartMessages.add(xmlc.getTextValue());
            }

            // Gather the list of multipart operations
            for (String multipartMessage : multipartMessages) {
                xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation[child::wsdl:input/@message=\"" + "tns:" + multipartMessage + "\"]/@name";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                while (xmlc.hasNextSelection()) {
                    xmlc.toNextSelection();
                    multipartOperations.add(xmlc.getTextValue());
                }

            }


            xPathStatement = "/wsdl:definitions/wsdl:binding[exists(wsoap11:binding)]/wsdl:operation/wsoap11:operation";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> soapActionMissingErrors = new ArrayList<String>();
            String bindingName = "";
            boolean instancesFound = false;
            while (xmlc.hasNextSelection()) {
                instancesFound = true;
                xmlc.toNextSelection();

                boolean soapActionAttributeFound = false;
                if (xmlc.toFirstAttribute()) {
                    do {
                        if (xmlc.getName().getLocalPart().equals("soapAction")) {
                            soapActionAttributeFound = true;

                            if (!xmlc.getTextValue().equals("''")) {
                                try {
                                    URL tmpURL = new URL(xmlc.getTextValue());
                                } catch (Exception ex) {

                                    String erroneousURL = xmlc.getTextValue();

                                    xmlc.push();
                                    xmlc.toParent(); // soap:operation
                                    xmlc.toParent(); // operation
                                    String operation = "[Not Found]";
                                    if (xmlc.toFirstAttribute()) {
                                        do {
                                            if (xmlc.getName().getLocalPart().equals("name")) {
                                                operation = xmlc.getTextValue();
                                                break;
                                            }
                                        } while (xmlc.toNextAttribute());
                                        xmlc.toParent(); // operation
                                    }


                                    bindingName = getParentBindingName(xmlc);
                                    // Only capture errors related to Request-Response Operations on this Requirement.
                                    if (!subscriptionOperations.contains(operation) && (!publicationOperations.contains(operation))&&(!multipartOperations.contains(operation))) {
                                        soapActionMissingErrors.add("Binding " + bindingName + " contains an operation element " + operation + " with an invalid soapAction attribute: \"" + erroneousURL + "\". ");
                                    }
                                    xmlc.pop();

                                }

                            }

                        }
                    } while (xmlc.toNextAttribute());

                    if (!soapActionAttributeFound) {
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
                        }

                        bindingName = getParentBindingName(xmlc);
                        soapActionMissingErrors.add("Binding " + bindingName + " contains an operation element " + operation + " that does not contain a soapAction attribute. ");
                        xmlc.pop();
                    }

                } else {   // No attributes are present.
                    xmlc.push();
                    bindingName = getParentBindingName(xmlc);
                    soapActionMissingErrors.add("Binding " + bindingName + " contains a operation element with no attributes. ");
                    xmlc.pop();
                }

            }

            if (soapActionMissingErrors.isEmpty() && instancesFound) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String actionErrors = "";
                for (String thisName : soapActionMissingErrors) {
                    if (actionErrors.equals("")) {
                        actionErrors = actionErrors.concat(thisName);
                    } else {
                        actionErrors = actionErrors.concat(", " + thisName);
                    }
                }

                String errorMessage = !instancesFound ? "No instances of the binding tag followed by a soap:binding element, operation and soap:operation tag were found. \n" : "";
                errorMessage = errorMessage.concat(!actionErrors.equals("") ? " SoapAction Errors: " + actionErrors : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }


        assertion.setTestAssertionPrescription("The soapAction attribute specifies the soapAction which the external center requester must included within an HTTP header. There is no requirement for a soapAction, only that the attribute be present. However, if no soapAction is specified, the soapAction attribute must by written as a double quote followed by two consecutive single quote characters followed by a double quote. The soapAction shall be a URL that indicates the message handler for the endpoint. ");
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
