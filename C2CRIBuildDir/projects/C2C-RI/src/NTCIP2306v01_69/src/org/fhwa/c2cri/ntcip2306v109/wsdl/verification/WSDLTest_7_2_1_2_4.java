/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import java.util.ArrayList;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class WSDLTest_7_2_1_2_4.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_2_1_2_4 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_7_2_1_2_4.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_2_1_2_4(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("7.2.1.2.4");
        try {
            ArrayList<OperationSpecification> soapSubscriptionOperations = new ArrayList();
            ArrayList<OperationSpecification> soapPublicationOperations = new ArrayList();
            ArrayList<String> xmlSubscriptionOperations = new ArrayList<String>();
            ArrayList<String> xmlPublicationOperations = new ArrayList<String>();

            for (OperationSpecification thisOperation : wsdlSpec.getSOAPServiceSPOperationSpecifications()) {
                if (thisOperation.isSubscriptionOperation()) {
                    soapSubscriptionOperations.add(thisOperation);
                } else if (thisOperation.isPublicationOperation()) {
                    soapPublicationOperations.add(thisOperation);
                }
            }

            // NTCIP 2306 does not specify how XML HTTP Encoding specifies a subscription or publication


            ArrayList<String> soapAnomolies = new ArrayList<String>();
            if (soapSubscriptionOperations.isEmpty() && soapPublicationOperations.isEmpty()){
                    soapAnomolies.add("No SOAP Subscription or Publication Operations were found within the WSDL.");                
            }


            ArrayList<String> xmlAnomolies = new ArrayList<String>();


            if (soapAnomolies.isEmpty() && xmlAnomolies.isEmpty()) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");


                String soapErrorsMsg = "";
                for (String thisName : soapAnomolies) {
                    if (soapErrorsMsg.equals("")) {
                        soapErrorsMsg = soapErrorsMsg.concat(thisName);
                    } else {
                        soapErrorsMsg = soapErrorsMsg.concat(", " + thisName);
                    }
                }

                String xmlErrorsMsg = "";
                for (String thisName : xmlAnomolies) {
                    if (xmlErrorsMsg.equals("")) {
                        xmlErrorsMsg = xmlErrorsMsg.concat(thisName);
                    } else {
                        xmlErrorsMsg = xmlErrorsMsg.concat(", " + thisName);
                    }
                }

                String errorMessage = "";
                errorMessage = errorMessage.concat(!soapErrorsMsg.equals("") ? " SOAP Encoding and Transport Errors: " + soapErrorsMsg : "");
                errorMessage = errorMessage.concat(!xmlErrorsMsg.equals("") ? " XML Encoding and Transport Errors: " + xmlErrorsMsg : "");

                assertion.setTestResultDescription(errorMessage);

            }
        } catch (Exception ex) {
            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }
        assertion.setTestAssertionPrescription("The WSDL (created by the publication center) shall show that subscription and publication use the same XML encoding and transmission message patterns (SOAP over HTTP or XML over HTTP).");
        return assertion;
    }
}
