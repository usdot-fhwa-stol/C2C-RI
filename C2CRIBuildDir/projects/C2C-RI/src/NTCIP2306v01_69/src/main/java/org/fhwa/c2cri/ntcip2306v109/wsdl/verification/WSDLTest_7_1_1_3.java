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
 * The Class WSDLTest_7_1_1_3.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_1_1_3 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;
    
    /** The request response test. */
    boolean requestResponseTest = true;

    /**
     * Instantiates a new wSDL test_7_1_1_3.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_1_1_3(RIWSDL wsdlSpec, String wsdlURL) {
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
    }

    /**
     * Instantiates a new wSDL test_7_1_1_3.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     * @param requestResponseTest the request response test
     */
    public WSDLTest_7_1_1_3(RIWSDL wsdlSpec, String wsdlURL, boolean requestResponseTest) {
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
        this.requestResponseTest = requestResponseTest;
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
        assertion.setTestAssertionID("7.1.1.3");
        try {
            ArrayList<String> soapOperations = new ArrayList();
            if (this.requestResponseTest){
                soapOperations = wsdlSpec.getSOAPServiceRROperations();
            } else {
                soapOperations = wsdlSpec.getSOAPServiceSPOperations();                
            }

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

            ArrayList<String> operationTagErrors = new ArrayList<String>();
            for (String thisOperation : soapOperations) {
                String xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation[@name=\"" + thisOperation + "\"]/*";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                boolean inputFound = false;
                boolean outputFound = false;
                boolean operationFound = xmlc.hasNextSelection();

                while (xmlc.hasNextSelection()) {
                    xmlc.toNextSelection();

                    String elementName = xmlc.getName().getLocalPart();

                    if (elementName.equals("input")) {
                        if (inputFound){
                            operationTagErrors.add(thisOperation+" has multiple input tags.");
                        }
                        inputFound = true;

                        if (outputFound) {
                            operationTagErrors.add(thisOperation+" input/output tags are out of sequence.");
                        }

                    } else if (elementName.equals("output")) {
                        if (outputFound){
                            operationTagErrors.add(thisOperation+" has multiple output tags.");
                        }
                        outputFound = true;
                        if (!inputFound) {
                            operationTagErrors.add(thisOperation+" input/output tags are out of sequence.");
                        }

                    }

                }
                if (operationFound && (!inputFound || !outputFound)){
                    operationTagErrors.add(thisOperation+" is missing a required input or output tag.");                    
                }
            }


            if (operationTagErrors.isEmpty()) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");
                String inputOutputParamErrors = "";
                for (String thisName : operationTagErrors) {
                    if (inputOutputParamErrors.equals("")) {
                        inputOutputParamErrors = inputOutputParamErrors.concat(thisName);
                    } else {
                        inputOutputParamErrors = inputOutputParamErrors.concat(", " + thisName);
                    }
                }
                String errorMessage = "Operations with input/output tag errors: "+inputOutputParamErrors;
                assertion.setTestResultDescription(errorMessage);
            }
            xmlc.dispose();


        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }
        assertion.setTestAssertionPrescription("Each operation shall have one input and one output tag. The input tag is specified first followed by the output tag. ");
        return assertion;
    }
}
