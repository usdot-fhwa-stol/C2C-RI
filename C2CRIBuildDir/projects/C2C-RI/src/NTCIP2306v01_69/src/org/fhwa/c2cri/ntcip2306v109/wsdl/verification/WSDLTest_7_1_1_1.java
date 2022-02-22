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
 * The Class WSDLTest_7_1_1_1.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_7_1_1_1 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;
    
    /** The request response test. */
    boolean requestResponseTest = true;

    /**
     * Instantiates a new wSDL test_7_1_1_1.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_7_1_1_1(RIWSDL wsdlSpec, String wsdlURL){
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
    }

    /**
     * Instantiates a new wSDL test_7_1_1_1.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     * @param requestResponseTest the request response test
     */
    public WSDLTest_7_1_1_1(RIWSDL wsdlSpec, String wsdlURL, boolean requestResponseTest){
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
        assertion.setTestAssertionID("7.1.1.1");
        try {
            ArrayList<String> soapOperations = new ArrayList();
            
            if (requestResponseTest){
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

            String xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            boolean soapOperationFound = false;
            while (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                if (soapOperations.contains(xmlc.getTextValue())){
                    soapOperationFound = true;
                }
            }
            if (soapOperationFound){
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");
                if (this.wsdlSpec.isHasSchemaTypes()) {
                    assertion.setTestResultDescription("No operations supported by a SOAP Service were found listed under a parent portType tag.");
                } else {
                    assertion.setTestResultDescription("No operations supported by a SOAP Service were found listed under a parent portType tag." + "  This may be due to an error (" +this.wsdlSpec.getSchemaTypeError()+") processing the schema files associated with the WSDL.");                    
                }
            }
            xmlc.dispose();


        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }
        assertion.setTestAssertionPrescription("The portType tag shall be the parent tag for a list of operations supported by the SOAP Service for the specific project implementation. ");
        return assertion;
    }
    

}
