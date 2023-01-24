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
 * The Class WSDLTest_6_4_3_4.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_6_4_3_4 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_6_4_3_4.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_6_4_3_4(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("6.4.3.4");



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

            ArrayList<String> dialogMessageList = new ArrayList<String>();

            String xPathStatement = "/wsdl:definitions/wsdl:message[child::wsdl:part/@element=\"c2c:c2cMessagePublication\"]/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            while (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                dialogMessageList.add(xmlc.getTextValue());
            }


            xPathStatement = "/wsdl:definitions/wsdl:message/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            ArrayList<String> definedMessageList = new ArrayList<String>();
            while (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                if (!definedMessageList.contains(xmlc.getTextValue())) {
                    definedMessageList.add(xmlc.getTextValue());
                }
            }

            ArrayList<String> missingPartMessages = new ArrayList<String>();
            ArrayList<String> incorrectAttributeMessages = new ArrayList<String>();

            for (String thisDialogMessage : dialogMessageList) {
                    if (definedMessageList.contains(thisDialogMessage)) {
                        // Confirm that the name attribute of the message part is "message"

                        boolean secondPartExists = false;
                        xPathStatement = "/wsdl:definitions/wsdl:message[@name=\"" + thisDialogMessage + "\"]/wsdl:part[2]/@name";
                        xmlc.toStartDoc();
                        xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                        while (xmlc.hasNextSelection()) {
                            xmlc.toNextSelection();
                            secondPartExists = true;
                            if (!xmlc.getTextValue().equals("message")) {
                                incorrectAttributeMessages.add(thisDialogMessage + " part name attribute is set to " + xmlc.getTextValue());
                            }
                        }

                        xPathStatement = "/wsdl:definitions/wsdl:message[@name=\"" + thisDialogMessage + "\"]/wsdl:part[2]/@element";
                        xmlc.toStartDoc();
                        xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                        while (xmlc.hasNextSelection()) {
                            xmlc.toNextSelection();
                            secondPartExists = true;
                           if (!xmlc.getTextValue().startsWith("tmdd:")) {
                                incorrectAttributeMessages.add(thisDialogMessage + " part element attribute is set to " + xmlc.getTextValue());
                            }
                        }

                        if (!secondPartExists) {
                           missingPartMessages.add(thisDialogMessage + " is missing second part.");
                        }

                    } else {
                        missingPartMessages.add(thisDialogMessage);
                    }
                
            }

            if (incorrectAttributeMessages.isEmpty() && missingPartMessages.isEmpty()) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");

                String missingPartsFound = "";
                for (String thisMessage : missingPartMessages) {
                    if (missingPartsFound.equals("")) {
                        missingPartsFound = missingPartsFound.concat(thisMessage);
                    } else {
                        missingPartsFound = missingPartsFound.concat("," + thisMessage);
                    }
                }

                String errorDescription = missingPartsFound.equals("") ? "" : "\nPublication messages that have missing parts: " + missingPartsFound;

                String incorrectAttributeMessageList = "";
                for (String thisMessage : incorrectAttributeMessages) {
                    if (incorrectAttributeMessageList.equals("")) {
                        incorrectAttributeMessageList = incorrectAttributeMessageList.concat(thisMessage);
                    } else {
                        incorrectAttributeMessageList = incorrectAttributeMessageList.concat("," + thisMessage);
                    }
                }

                errorDescription = errorDescription.concat(incorrectAttributeMessageList.equals("") ? "" : "\nMessages with incorrect name or element attributes: " + incorrectAttributeMessageList);

                assertion.setTestResultDescription(errorDescription);
            }
            xmlc.clearSelections();
            xmlc.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }

        assertion.setTestAssertionPrescription("The name attribute of the second message part shall be message and the element attribute use the form tmdd: followed by the name of the XML Schema element being imported into the WSDL.");
        return assertion;
    }
}
