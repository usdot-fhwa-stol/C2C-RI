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
 * The Class WSDLTest_7_2_3_1.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 1/8/2014
 */
public class WSDLTest_7_2_3_1 implements WSDLTest
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
     * Instantiates a new wSDL test_7_2_3_1.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL  the wsdl url
     */
    public WSDLTest_7_2_3_1(RIWSDL wsdlSpec, String wsdlURL)
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
        assertion.setTestAssertionID("7.2.3.1");

        TestAssertion results7231a = soapActionTest();

        WSDLTest_7_1_2_1 wsdlTest7121 = new WSDLTest_7_1_2_1(wsdlSpec, wsdlURL);
        TestAssertion results7121 = wsdlTest7121.perform();

        WSDLTest_7_1_2_2 wsdlTest7122 = new WSDLTest_7_1_2_2(wsdlSpec, wsdlURL);
        TestAssertion results7122 = wsdlTest7122.perform();

        WSDLTest_7_1_2_3 wsdlTest7123 = new WSDLTest_7_1_2_3(wsdlSpec, wsdlURL);
        TestAssertion results7123 = wsdlTest7123.perform();

        WSDLTest_7_1_2_4 wsdlTest7124 = new WSDLTest_7_1_2_4(wsdlSpec, wsdlURL);
        TestAssertion results7124 = wsdlTest7124.perform();

        WSDLTest_7_1_2_5 wsdlTest7125 = new WSDLTest_7_1_2_5(wsdlSpec, wsdlURL);
        TestAssertion results7125 = wsdlTest7125.perform();

        WSDLTest_7_1_2_7 wsdlTest7127 = new WSDLTest_7_1_2_7(wsdlSpec, wsdlURL);
        TestAssertion results7127 = wsdlTest7127.perform();

        WSDLTest_7_1_2_8 wsdlTest7128 = new WSDLTest_7_1_2_8(wsdlSpec, wsdlURL);
        TestAssertion results7128 = wsdlTest7128.perform();

        WSDLTest_7_1_2_9 wsdlTest7129 = new WSDLTest_7_1_2_9(wsdlSpec, wsdlURL);
        TestAssertion results7129 = wsdlTest7129.perform();

        WSDLTest_7_1_2_10 wsdlTest71210 = new WSDLTest_7_1_2_10(wsdlSpec, wsdlURL, true);
        TestAssertion results71210 = wsdlTest71210.perform();

        if (results7121.getTestResult().equals("Passed")
            && results7122.getTestResult().equals("Passed")
            && results7123.getTestResult().equals("Passed")
            && results7124.getTestResult().equals("Passed")
            && results7125.getTestResult().equals("Passed")
            && results7231a.getTestResult().equals("Passed")
            && results7127.getTestResult().equals("Passed")
            && results7128.getTestResult().equals("Passed")
            && results7129.getTestResult().equals("Passed")
            && results71210.getTestResult().equals("Passed"))
        {
            assertion.setTestResult("Passed");
            assertion.setTestResultDescription("");

        }
        else
        {
            String failureResult = "";

            assertion.setTestResult("Failed");
            if (results7231a.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat("7.2.3.1: " + results7231a.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7231a.isContinueAfterFailure());
            }
            if (results7121.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7121.getTestAssertionID() + ": " + results7121.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7121.isContinueAfterFailure());
            }
            if (results7122.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7122.getTestAssertionID() + ": " + results7122.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7122.isContinueAfterFailure());
            }
            if (results7123.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7123.getTestAssertionID() + ": " + results7123.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7123.isContinueAfterFailure());
            }
            if (results7124.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7124.getTestAssertionID() + ": " + results7124.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7124.isContinueAfterFailure());
            }
            if (results7125.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7125.getTestAssertionID() + ": " + results7125.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7125.isContinueAfterFailure());
            }
            if (results7127.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7127.getTestAssertionID() + ": " + results7127.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7127.isContinueAfterFailure());
            }
            if (results7128.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7128.getTestAssertionID() + ": " + results7128.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7128.isContinueAfterFailure());
            }
            if (results7129.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results7129.getTestAssertionID() + ": " + results7129.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results7129.isContinueAfterFailure());
            }
            if (results71210.getTestResult().equals("Failed"))
            {
                failureResult = failureResult.concat(results71210.getTestAssertionID() + ": " + results71210.getTestResultDescription() + "\n");
                assertion.setContinueAfterFailure(results71210.isContinueAfterFailure());
            }
            assertion.setTestResultDescription(failureResult);
        }
        assertion.setTestAssertionPrescription("The normative rules from the SOAP Request-Response Binding defined in Section 7.1.3 shall apply with the following exception—the soapAction attribute shall be left blank, to be specified during subscription. ");

        return assertion;
    }

    /**
     * Soap action test.
     * <p>
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    private TestAssertion soapActionTest()
    {
        TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("7.2.3.1a");

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
                    + "declare namespace soap12='http://www.w3.org/2003/05/soap-envelope'; "
                    + "declare namespace soapenc='http://schemas.xmlsoap.org/soap/encoding/'; "
                    + "declare namespace xenc='http://www.w3.org/2001/04/xmlenc#'; "
                    + "declare namespace wsoap12='http://schemas.xmlsoap.org/wsdl/soap12/'; "
                    + "declare namespace wsoap11='http://schemas.xmlsoap.org/wsdl/soap/'; "
                    + "declare namespace soapbind='http://schemas.xmlsoap.org/wsdl/soap/'; ";

            ArrayList<String> publicationOperations = new ArrayList<String>();
            ArrayList<String> publicationMessages = new ArrayList<String>();
            // Gather the list of publication messages
            String xPathStatement = "/wsdl:definitions/wsdl:message[exists(child::wsdl:part[2])][child::wsdl:part[1]/@element=\"c2c:c2cMessagePublication\"]/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            while (xmlc.hasNextSelection())
            {
                xmlc.toNextSelection();
                publicationMessages.add(xmlc.getTextValue());
            }

            // Gather the list of publication operations
            for (String publicationMessage: publicationMessages)
            {
                xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation[child::wsdl:input/@message=\"" + "tns:" + publicationMessage + "\"]/@name";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                while (xmlc.hasNextSelection())
                {
                    xmlc.toNextSelection();
                    publicationOperations.add(xmlc.getTextValue());
                }

            }

            xPathStatement = "/wsdl:definitions/wsdl:binding[exists(wsoap11:binding)]/wsdl:operation/wsoap11:operation";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> soapActionMissingErrors = new ArrayList<String>();
            String bindingName = "";
            boolean instancesFound = false;
            while (xmlc.hasNextSelection())
            {
                instancesFound = true;
                xmlc.toNextSelection();

                boolean soapActionAttributeFound = false;
                if (xmlc.toFirstAttribute())
                {
                    do
                        if (xmlc.getName().getLocalPart().equals("soapAction"))
                        {
                            soapActionAttributeFound = true;

                            if (!xmlc.getTextValue().equals(""))
                            {

                                String erroneousURL = xmlc.getTextValue();

                                xmlc.push();
                                xmlc.toParent(); // soap:operation
                                xmlc.toParent(); // operation
                                String operation = "[Not Found]";
                                if (xmlc.toFirstAttribute())
                                {
                                    do
                                        if (xmlc.getName().getLocalPart().equals("name"))
                                        {
                                            operation = xmlc.getTextValue();
                                            break;
                                        }
                                    while (xmlc.toNextAttribute());
                                    xmlc.toParent(); // operation
                                }


                                bindingName = getParentBindingName(xmlc);
                                if (publicationOperations.contains(operation))
                                    soapActionMissingErrors.add("Binding " + bindingName + " contains an operation element " + operation + " with an non-blank soapAction attribute: \"" + erroneousURL + "\". ");
                                xmlc.pop();

                            }

                        }
                    while (xmlc.toNextAttribute());

                    if (!soapActionAttributeFound)
                    {
                        xmlc.push();
                        xmlc.toParent();  // operation
                        String operation = "[Not Found]";
                        if (xmlc.toFirstAttribute())
                            do
                                if (xmlc.getName().getLocalPart().equals("name"))
                                {
                                    operation = xmlc.getTextValue();
                                    break;
                                }
                            while (xmlc.toNextAttribute());

                        bindingName = getParentBindingName(xmlc);
                        soapActionMissingErrors.add("Binding " + bindingName + " contains an operation element " + operation + " that does not contain a soapAction attribute. ");
                        xmlc.pop();
                    }

                }
                else
                {   // No attributes are present.
                    xmlc.push();
                    bindingName = getParentBindingName(xmlc);
                    soapActionMissingErrors.add("Binding " + bindingName + " contains a operation element with no attributes. ");
                    xmlc.pop();
                }

            }

            if (soapActionMissingErrors.isEmpty() && instancesFound)
            {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            }
            else
            {
                assertion.setTestResult("Failed");

                String actionErrors = "";
                for (String thisName: soapActionMissingErrors)
                    if (actionErrors.equals(""))
                        actionErrors = actionErrors.concat(thisName);
                    else
                        actionErrors = actionErrors.concat(", " + thisName);

                String errorMessage = !instancesFound ? "No instances of the binding tag followed by a soap:binding element, operation and soap:operation tag were found. \n" : "";
                errorMessage = errorMessage.concat(!actionErrors.equals("") ? " SoapAction Errors: " + actionErrors : "");
                assertion.setTestResultDescription(errorMessage);
            }

            xmlc.dispose();

        }
        catch (Exception ex)
        {
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
     *
     * @return the parent binding name
     */
    private String getParentBindingName(XmlCursor xmlc)
    {
        String bindingName = "";
        xmlc.toParent();  // binding

        if (xmlc.toFirstAttribute())
            do
                if (xmlc.getName().getLocalPart().equals("name"))
                {
                    bindingName = xmlc.getTextValue();
                    break;
                }
            while (xmlc.toNextAttribute());
        return bindingName;
    }
}
