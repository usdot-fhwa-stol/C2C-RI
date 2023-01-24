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
 * The Class WSDLTest_8_1_2_5.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_8_1_2_5 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_8_1_2_5.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_8_1_2_5(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("8.1.2.5");
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
                    + "declare namespace http='http://schemas.xmlsoap.org/wsdl/http/'; "
                    + "declare namespace wsoap12='http://schemas.xmlsoap.org/wsdl/soap12/'; "
                    + "declare namespace wsoap11='http://schemas.xmlsoap.org/wsdl/soap/'; "
                    + "declare namespace soapbind='http://schemas.xmlsoap.org/wsdl/soap/'; ";
            String xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> getRelatedOperations = wsdlSpec.getXMLHTTPOperations("GET");
            ArrayList<String> currentDefinedOperations = new ArrayList<String>();
            ArrayList<String> missingOperationDefinitions = new ArrayList<String>();
            ArrayList<String> missingLocations = new ArrayList<String>();

            while (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                currentDefinedOperations.add(xmlc.getTextValue());

            }

            for (String thisOperation : getRelatedOperations) {
                if (!currentDefinedOperations.contains(thisOperation)) {
                    missingOperationDefinitions.add(thisOperation);
                } else {
                    xPathStatement = "/wsdl:definitions/wsdl:binding/wsdl:operation[@name=\"" + thisOperation + "\"]/http:operation/@location";
                    xmlc.toStartDoc();
                    xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);
                    if (!xmlc.hasNextSelection()) {
                        missingLocations.add(thisOperation);

                    } else {
                        // Check that the value of the location attribute is not blank (after trimming)
                        while (xmlc.hasNextSelection()) {

                            xmlc.toNextSelection();

                                do {
                                    if (xmlc.getName().getLocalPart().equals("location")) {

                                        if (xmlc.getTextValue().trim().isEmpty()) {

                                            String erroneousURL = xmlc.getTextValue();

                                            xmlc.push();
                                            xmlc.toParent(); // http:operation
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
                                            missingLocations.add(operation + ":[No File Specified]");
                                            xmlc.pop();

                                        }

                                    }
                                } while (xmlc.toNextAttribute());

                        }

                    }
                }
            }


            if (missingLocations.isEmpty() && !getRelatedOperations.isEmpty() && missingOperationDefinitions.isEmpty()) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String operationErrorsMsg = "";
                for (String operationError : missingOperationDefinitions) {
                    if (operationErrorsMsg.equals("")) {
                        operationErrorsMsg = operationErrorsMsg.concat(operationError);
                    } else {
                        operationErrorsMsg = operationErrorsMsg.concat(", " + operationError);
                    }
                }


                String locationsErrorsMsg = "";
                for (String bindingsError : missingLocations) {
                    if (locationsErrorsMsg.equals("")) {
                        locationsErrorsMsg = locationsErrorsMsg.concat(bindingsError);
                    } else {
                        locationsErrorsMsg = locationsErrorsMsg.concat(", " + bindingsError);
                    }
                }
                String errorMessage = getRelatedOperations.isEmpty() ? "No instances of operations related to a HTTP GET Service were found. \n" : "";
                errorMessage = errorMessage.concat(!operationErrorsMsg.equals("") ? " Operations Missings Errors: " + operationErrorsMsg : "");
                errorMessage = errorMessage.concat(!locationsErrorsMsg.equals("") ? " Missing Location Attribute Errors: " + locationsErrorsMsg : "");
                assertion.setTestResultDescription(errorMessage);

            }
            xmlc.dispose();


        } catch (Exception ex) {
            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }
        assertion.setTestAssertionPrescription("The location attribute shall be the name of the file. The name of the file is to be defined by each center. However, it is recommended that the file name reflect the name of the message or messages contained. Documentation outlining these naming rules should be established and a link provided to it in the meta-data of the documentation section. ");
        return assertion;
    }
}
