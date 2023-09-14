/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel.verification;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * The Class TestAssertionResults represents the results of testing.  The test results
 * are initially provided as an xml document.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestAssertionResults {

    /**
     * Instantiates a new test assertion results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public TestAssertionResults() {
    }

    /**
     * Gets the test assertion results.
     *
     * @param testResults the test results
     * @return the test assertion results
     */
    public static HashMap<String, TestAssertion> getTestAssertionResults(String testResults) {
        HashMap<String, TestAssertion> assertionResults = new HashMap<String, TestAssertion>();
        TestAssertion ta = null;

        // create the XMLInputFactory object
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		inputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        try {
            /*
             * Convert String to InputStream using ByteArrayInputStream
             * class. This class constructor takes the string byte array
             * which can be done by calling the getBytes() method.
             */

            InputStream is = new ByteArrayInputStream(testResults.getBytes("UTF-8"));


            // create a XMLStreamReader object
            XMLStreamReader reader = inputFactory.createXMLStreamReader(is);

            // read the test assertions from the testResults String
            while (reader.hasNext()) {
                int eventType = reader.getEventType();
                switch (eventType) {
                    case XMLStreamConstants.START_ELEMENT:
                        String elementName = reader.getLocalName();
                        if (elementName.equals("assertionResult")) {
                            ta = new TestAssertion();

                            boolean allAttributesProcessed = false;
                            int ii = 0;
                            while (!allAttributesProcessed) {
                                try {
                                    String code = reader.getAttributeValue(ii);
                                    String attribute = reader.getAttributeLocalName(ii);
                                    if (attribute.equalsIgnoreCase("id")) {
                                        ta.setTestAssertionID(code);
                                    } else if (attribute.equalsIgnoreCase("prescription")) {
                                        ta.setTestAssertionPrescription(code);
                                    } else if (attribute.equalsIgnoreCase("result")) {
                                        ta.setTestResult(code);
                                    }
                                    ii++;
                                } catch (Exception ex) {
                                    allAttributesProcessed = true;
                                }
                            }

                        } else if (elementName.equals("failureMessage")) {
                            reader.next();
                            ta.setTestResultDescription(reader.getText());
                        }
                        break;
                    case XMLStreamConstants.END_ELEMENT:
                        elementName = reader.getLocalName();
                        if (elementName.equals("assertionResult")) {
                            if (assertionResults.containsKey(ta.getTestAssertionID())) {
                                assertionResults.remove(ta.getTestAssertionID());
                                assertionResults.put(ta.getTestAssertionID(), ta);
                            } else {
                                assertionResults.put(ta.getTestAssertionID(), ta);
                            }
                        }
                        break;
                    default:
                        break;
                }
                reader.next();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        } catch (XMLStreamException e) {
            e.printStackTrace();
            return null;
        }

        return assertionResults;
    }
}
