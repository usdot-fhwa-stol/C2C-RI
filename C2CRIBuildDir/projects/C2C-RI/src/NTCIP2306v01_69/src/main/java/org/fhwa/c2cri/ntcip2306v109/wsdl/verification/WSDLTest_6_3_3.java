/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import java.net.URL;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class WSDLTest_6_3_3.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_6_3_3 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_6_3_3.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_6_3_3(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("6.3.3");
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

            String xPathStatement = "/wsdl:definitions/wsdl:types/xsd:schema";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            if (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                // Move to the end of the current token.
                if (xmlc.toFirstChild()) {
                    QName tokenName = xmlc.getName();

                    assertion.setTestResult(tokenName.getLocalPart().equals("import") ? "Passed" : "Failed");
                    assertion.setTestResultDescription("");

                    if (assertion.getTestResult().equals("Failed")) {
                        assertion.setTestResultDescription("The schema tag is immediately followed by the " + tokenName.getLocalPart() + " tag.");
                    }

                } else {
                    assertion.setTestResult("Failed");
                    assertion.setTestResultDescription("No child token exists after the schema tag.");
                }

            } else {
                assertion.setTestResult("Failed");
                assertion.setTestResultDescription("Testing Error - No result returned on search for the schema tag.");
            }




            if (assertion.getTestResult().equals("Passed")) {

                xPathStatement = "every $importdef in /wsdl:definitions/wsdl:types/xsd:schema/xsd:import satisfies (exists($importdef/@schemaLocation) and exists($importdef/@namespace))";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + xPathStatement);

                if (xmlc.hasNextSelection()) {
                    xmlc.toNextSelection();
                    assertion.setTestResult(xmlc.getTextValue().equals("true") ? "Passed" : "Failed");
                    assertion.setTestResultDescription(xmlc.getTextValue().equals("true") ? "" : "The WSDL definitions does not contain the required 5 child sections.");
                } else {
                    assertion.setTestResult("Failed");
                    assertion.setTestResultDescription("Testing Error - No result returned.");
                }
            }
            xmlc.dispose();
        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();
            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }

        assertion.setTestAssertionPrescription("The import tag shall follow and specify the namespace being imported and the schemaLocation, i.e., the name (or URL) of the XML Schema file.");
        return assertion;
    }
}
