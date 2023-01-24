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
 * The Class WSDLTest_7_2_2_4.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_2_2_4 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_7_2_2_4.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_2_2_4(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("7.2.2.4");
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

            ArrayList<String> updateOperations = new ArrayList<String>();
            for (String thisOperation : wsdlSpec.getSOAPServiceOperations()) {
                if (thisOperation.endsWith("Update")) {
                    updateOperations.add(thisOperation);
                }
            }

            ArrayList<String> missingInputErrors = new ArrayList<String>();
            boolean instanceFound = false;
            for (String thisOperation : updateOperations) {
                String xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation[@name=\"" + thisOperation + "\"]/wsdl:input";

                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                if (!xmlc.hasNextSelection()) {
                    missingInputErrors.add("No input element specified for operation " + thisOperation);
                } else {
                    instanceFound = true;
                }
            }

            if (instanceFound && missingInputErrors.isEmpty()) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String inputErrorsMsg = "";
                for (String thisName : missingInputErrors) {
                    if (inputErrorsMsg.equals("")) {
                        inputErrorsMsg = inputErrorsMsg.concat(thisName);
                    } else {
                        inputErrorsMsg = inputErrorsMsg.concat(", " + thisName);
                    }
                }

                String errorMessage = "";
                errorMessage = errorMessage.concat(!instanceFound ? " No Instances of update operations were found. " : "");
                errorMessage = errorMessage.concat(!inputErrorsMsg.equals("") ? " Missing Input Errors: " + inputErrorsMsg : "");


                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }

        assertion.setTestAssertionPrescription("The callback message (sent from the publisher to subscriber) shall be defined as an input message (from the subscriber’s perspective). ");
        return assertion;
    }
}
