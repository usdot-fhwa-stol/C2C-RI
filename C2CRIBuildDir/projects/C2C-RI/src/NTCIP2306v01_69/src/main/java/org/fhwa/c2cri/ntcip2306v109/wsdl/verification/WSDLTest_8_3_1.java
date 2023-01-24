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
 * The Class WSDLTest_8_3_1.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 1/8/2014
 */
public class WSDLTest_8_3_1 implements WSDLTest
{

    /**
     * The wsdl spec.
     */
    RIWSDL wsdlSpec;

    /**
     * The wsdl url.
     */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_8_3_1.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL  the wsdl url
     */
    public WSDLTest_8_3_1(RIWSDL wsdlSpec, String wsdlURL)
    {
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
    }

    /**
     * Perform.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    @Override
    public TestAssertion perform()
    {
        TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("8.3.1");

        WSDLTest_7_1_3_1 wsdl7131Test = new WSDLTest_7_1_3_1(wsdlSpec, wsdlURL);
        TestAssertion results7131 = wsdl7131Test.perform();

        WSDLTest_7_1_3_3 wsdl7133Test = new WSDLTest_7_1_3_3(wsdlSpec, wsdlURL);
        TestAssertion results7133 = wsdl7133Test.perform();

        WSDLTest_7_1_3_5 wsdl7135Test = new WSDLTest_7_1_3_5(wsdlSpec, wsdlURL);
        TestAssertion results7135 = wsdl7135Test.perform();

        if (results7131.getTestResult().equals("Passed")
            && results7133.getTestResult().equals("Passed")
            && results7135.getTestResult().equals("Passed"))
        {
            assertion.setTestResult("Passed");
            assertion.setTestResultDescription("");

        }
        else
        {
            String failureResult = "";

            assertion.setTestResult("Failed");
            if (results7131.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat("7.1.3.1: " + results7131.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7131.isContinueAfterFailure());
            }
            if (results7133.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7133.getTestAssertionID() + ": " + results7133.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7133.isContinueAfterFailure());
            }
            if (results7135.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7135.getTestAssertionID() + ": " + results7135.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7135.isContinueAfterFailure());
            }
            assertion.setTestResultDescription(failureResult);
        }
        assertion.setTestAssertionPrescription("The same requirement of the Request Only Binding shall apply. ");

        return assertion;
    }

    /**
     * Perform7135.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    public TestAssertion perform7135()
    {
        TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("7.1.3.5");
        try
        {
            URL wsdlURL = new URL(wsdlSpec.getWsdlFileName());
            XmlObject xmlObj = XmlObject.Factory.parse(wsdlURL);
            XmlCursor xmlc = xmlObj.newCursor();

            String xqNamespace
                   = 
                    "declare namespace xsl='http://www.w3.org/1999/XSL/Transform'; "
                    + "declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; "
                    + "declare namespace xsd='http://www.w3.org/2001/XMLSchema'; "
                    + "declare namespace wsdl='http://schemas.xmlsoap.org/wsdl/'; "
                    + "declare namespace soap11='http://schemas.xmlsoap.org/soap/envelope/'; "
                    + "declare namespace http='http://schemas.xmlsoap.org/wsdl/http/'; "
                    + "declare namespace soap12='http://www.w3.org/2003/05/soap-envelope'; "
                    + "declare namespace soapenc='http://schemas.xmlsoap.org/soap/encoding/'; "
                    + "declare namespace xenc='http://www.w3.org/2001/04/xmlenc#'; "
                    + "declare namespace wsoap12='http://schemas.xmlsoap.org/wsdl/soap12/'; "
                    + "declare namespace wsoap11='http://schemas.xmlsoap.org/wsdl/soap/'; "
                    + "declare namespace soapbind='http://schemas.xmlsoap.org/wsdl/soap/'; ";

            String xPathStatement = "/wsdl:definitions/wsdl:service/wsdl:port[exists(child::http:address)]";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> locationAttributeErrors = new ArrayList<String>();
            ArrayList<String> locationURLErrors = new ArrayList<String>();

            boolean instancesFound = false;
            while (xmlc.hasNextSelection())
            {
                xmlc.toNextSelection();

                // Get the service name attribute
                String serviceName = "[Not Defined]";
                xmlc.push();
                xmlc.toParent();
                if (xmlc.toFirstAttribute())
                    do
                        if (xmlc.getName().getLocalPart().equals("name"))
                            serviceName = xmlc.getTextValue();
                    while (xmlc.toNextAttribute());
                xmlc.pop();

                if (xmlc.toFirstChild())
                    do
                        if (xmlc.getName().getLocalPart().equals("address") && xmlc.getName().getNamespaceURI().equals("http://schemas.xmlsoap.org/wsdl/http/"))
                        {
                            instancesFound = true;
                            xmlc.push();
                            boolean locationAttributePresent = false;

                            if (xmlc.toFirstAttribute())
                                do
                                    if (xmlc.getName().getLocalPart().equals("location"))
                                    {
                                        locationAttributePresent = true;
                                        try
                                        {
                                            URL tmpURL = new URL(xmlc.getTextValue());

                                        }
                                        catch (Exception ex)
                                        {
                                            locationURLErrors.add("The location attribute " + xmlc.getTextValue() + " for service " + serviceName + " does not represent a valid URL. " + ex.getMessage());
                                        }
                                    }
                                while (xmlc.toNextAttribute());
                            if (!locationAttributePresent)
                                locationAttributeErrors.add("The location attribute for http:address tag in a port was not set for service " + serviceName);
                            xmlc.pop();

                        }
                    while (xmlc.toNextSibling());
            }

            if (locationAttributeErrors.isEmpty() && locationURLErrors.isEmpty() && instancesFound)
            {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            }
            else
            {
                assertion.setTestResult("Failed");

                String attributeErrorsMsg = "";
                for (String thisName: locationAttributeErrors)
                    if (attributeErrorsMsg.equals(""))
                        attributeErrorsMsg = attributeErrorsMsg.concat(thisName);
                    else
                        attributeErrorsMsg = attributeErrorsMsg.concat(", " + thisName);

                String urlErrorsMsg = "";
                for (String thisName: locationURLErrors)
                    if (urlErrorsMsg.equals(""))
                        urlErrorsMsg = urlErrorsMsg.concat(thisName);
                    else
                        urlErrorsMsg = urlErrorsMsg.concat(", " + thisName);

                String errorMessage = !instancesFound ? "No instances of the http:address tag were found as part of a service definition. \n" : "";
                errorMessage = errorMessage.concat(!attributeErrorsMsg.equals("") ? " Attribute Errors: " + attributeErrorsMsg : "");
                errorMessage = errorMessage.concat(!urlErrorsMsg.equals("") ? " URL Errors: " + urlErrorsMsg : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        }
        catch (Exception ex)
        {
            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }

        assertion.setTestAssertionPrescription("The service endpoint must specify a valid URL. ");
        return assertion;
    }
}
