/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import java.net.URL;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class WSDLTest_6_4_2_2.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_6_4_2_2 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_6_4_2_2.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_6_4_2_2(RIWSDL wsdlSpec, String wsdlURL) {
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
        assertion.setTestAssertionID("6.4.2.2");
        if (wsdlSpec.isSchemaDocumentsExist()) {
            ArrayList<QName> schemaMessageList = wsdlSpec.getSchemaMessageNames();


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
                dialogMessageList = wsdlSpec.getDialogRelatedMessages("Subscription");


                String xPathStatement = "/wsdl:definitions/wsdl:message/@name";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                ArrayList<String> definedMessageList = new ArrayList<String>();
                while (xmlc.hasNextSelection()) {
                    xmlc.toNextSelection();
                    if (!definedMessageList.contains(xmlc.getTextValue())) {
                        definedMessageList.add(xmlc.getTextValue());
                    }
                }

                ArrayList<String> missingOperationMessages = new ArrayList<String>();
                ArrayList<String> missingSchemaMessages = new ArrayList<String>();
                ArrayList<String> misnamedMessages = new ArrayList<String>();

                for (String thisDialogMessage : dialogMessageList) {
                    if (thisDialogMessage.startsWith("MSG_")) {
                        if (definedMessageList.contains(thisDialogMessage)) {

                            // Get the list of message part element attributes that are defined for this message element
                            xPathStatement = "/wsdl:definitions/wsdl:message[@name=\"" + thisDialogMessage + "\"]/wsdl:part/@element";
                            xmlc.toStartDoc();
                            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

                            ArrayList<String> messagePartElements = new ArrayList<String>();
                            while (xmlc.hasNextSelection()) {
                                xmlc.toNextSelection();
                                if (!messagePartElements.contains(xmlc.getTextValue())) {
                                    messagePartElements.add(xmlc.getTextValue());
                                }

                            }

                            for (String thisPartElement : messagePartElements) {
                                boolean matchedToSchema = false;
                                for (QName thisQName : schemaMessageList) {
                                    // divide the element name attribute into its schema prefix and element name parts
                                    String[] elementParts = thisPartElement.split(":");
                                    String prefix = (elementParts.length > 1 ? (elementParts[0] == null ? "" : elementParts[0]) : "");  // If two parts exist then gather the prefix.  Look out for null
                                    String messageName = (elementParts.length > 1 ? (elementParts[1] == null ? "" : elementParts[1]) : (elementParts[0] == null ? "" : elementParts[0])); // Handle the case of 1 or 2 parts.  Look out for null

                                    if (prefix.equals(thisQName.getPrefix())||messageName.equals(thisQName.getLocalPart())) {
                                        matchedToSchema = true;
                                       break;
                                     }
                                }
                                if (!matchedToSchema) {
                                    missingSchemaMessages.add(thisDialogMessage);
                                    break;
                                }

                            }

                        } else {
                            missingOperationMessages.add(thisDialogMessage);
                        }
                    } else {
                        misnamedMessages.add(thisDialogMessage);
                    }
                }

                if (misnamedMessages.isEmpty() && missingOperationMessages.isEmpty() && missingSchemaMessages.isEmpty()) {
                    assertion.setTestResult("Passed");
                    assertion.setTestResultDescription("");
                } else {
                    // Allow the ambiguous errors to still allow the test to pass.

                    String missingFromSchema = "";
                    for (String thisMessage : missingSchemaMessages) {
                        if (missingFromSchema.equals("")) {
                            missingFromSchema = missingFromSchema.concat(thisMessage);
                        } else {
                            missingFromSchema = missingFromSchema.concat("," + thisMessage);
                        }
                    }

                    String errorDescription = missingFromSchema.equals("") ? "" : "Messages missing from schemas: " + missingFromSchema;

                    String missingFromOperations = "";
                    for (String thisMessage : missingOperationMessages) {
                        if (missingFromOperations.equals("")) {
                            missingFromOperations = missingFromOperations.concat(thisMessage);
                        } else {
                            missingFromOperations = missingFromOperations.concat("," + thisMessage);
                        }
                    }

                    errorDescription = errorDescription.concat(missingFromOperations.equals("") ? "" : "\nSubscription messages not included in Message Section: " + missingFromOperations);

                    String misnamedMessageList = "";
                    for (String thisMessage : misnamedMessages) {
                        if (misnamedMessageList.equals("")) {
                            misnamedMessageList = misnamedMessageList.concat(thisMessage);
                        } else {
                            misnamedMessageList = misnamedMessageList.concat("," + thisMessage);
                        }
                    }

                    errorDescription = errorDescription.concat(misnamedMessageList.equals("") ? "" : "\nMessages that don't begin with MSG_: " + misnamedMessageList);

                    assertion.setTestResultDescription(errorDescription);
                }

                xmlc.dispose();

            } catch (Exception ex) {
                ex.printStackTrace();
                assertion.setTestResult("Failed");
                assertion.setTestResultDescription(ex.getMessage());
            }
        } else {
            assertion.setTestResult("Failed");
            assertion.setTestResultDescription("Test could not be run due to one or more schema files referenced by the WSDL file missing.");
        }
        assertion.setTestAssertionPrescription("The message name shall be defined by the name of the message type, as defined in the schema, with the prefix MSG_ at the front part of the name. ");
        return assertion;
    }
}
