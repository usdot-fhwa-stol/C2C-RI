/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import java.net.URL;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;
import org.fhwa.c2cri.utilities.RIParameters;


/**
 * The Class WSDLTest_8_1_1_4.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_8_1_1_4 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_8_1_1_4.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_8_1_1_4(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("8.1.1.4");
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

            String xPathStatement = "/wsdl:definitions/wsdl:portType[count(child::wsdl:operation)=1]/wsdl:operation[exists(wsdl:input)][not(exists(wsdl:output))]/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> operationNameErrors = new ArrayList<String>();
            boolean instanceFound = false;
            while (xmlc.hasNextSelection()) {
                instanceFound = true;
                xmlc.toNextSelection();

                if (!xmlc.getTextValue().startsWith("OP_")){
                    operationNameErrors.add("Operation "+xmlc.getTextValue()+" does not begin with \"OP_\".");
                }
            }

            if (instanceFound && operationNameErrors.isEmpty()){
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String operationErrorsMsg = "";
                for (String operationError : operationNameErrors) {
                    if (operationErrorsMsg.equals("")){
                        operationErrorsMsg = operationErrorsMsg.concat(operationError);
                    } else {
                        operationErrorsMsg = operationErrorsMsg.concat(", " + operationError);
                    }
                }

                String errorMessage = !instanceFound ? "No instances of one way operations were found. \n" : "";
                errorMessage = errorMessage.concat(!operationErrorsMsg.equals("") ? " Operation Name Errors: " + operationErrorsMsg +"\n Please note that this error will not likely affect subsequent testing of Information Layer standards.": "");
                assertion.setTestResultDescription(errorMessage);

            }
            xmlc.dispose();


        } catch (Exception ex) {
            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }
        assertion.setTestAssertionPrescription("The operation name shall begin with the prefix OP_ followed by a descriptive name for the operation. ");

        try {
            assertion.setContinueAfterFailure(Boolean.valueOf(RIParameters.getInstance().getParameterValue(NTCIP2306Settings.NTCIP2306_TEST_SETTINGS_GROUP, NTCIP2306Settings.NTCIP2306_CONTINUE_AFTER_OP_FAILURE_PARAMETER, NTCIP2306Settings.DEFAULT_NTCIP2306_CONTINUE_AFTER_OP_FAILURE_PARAMETER_VALUE)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return assertion;
    }
}
