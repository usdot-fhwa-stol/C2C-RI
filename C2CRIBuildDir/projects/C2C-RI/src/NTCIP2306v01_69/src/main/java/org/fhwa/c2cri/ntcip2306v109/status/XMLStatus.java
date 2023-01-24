/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.status;

import java.util.ArrayList;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class XMLStatus maintains the results of various NTCIP 2306 xml requirement inspections.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class XMLStatus {
    
    /** The xml v1 document. */
    private boolean xmlV1Document = false;
    
    /** The utf8 char set. */
    private boolean utf8CharSet = false;
    
    /** The xml v1 header. */
    private boolean xmlV1Header = false;
    
    /** The xml validated to wsdl schemas. */
    private boolean xmlValidatedToWSDLSchemas = false;
    
    /** The xml errors. */
    private ArrayList<String> xmlErrors = new ArrayList<String>();
    
    /** The xml schema validation errors. */
    private ArrayList<String> xmlSchemaValidationErrors = new ArrayList<String>();
    
    /** The test assertion list. */
    private ArrayList<TestAssertion> testAssertionList = new ArrayList<>();

    /**
     * NTCIP 2306 4.1.1
     *
     * @return true, if is NTCIP2306 valid xml
     */
    public boolean isNTCIP2306ValidXML() {
        return (xmlV1Document&&utf8CharSet);
    }


    /**
     * Adds the xml error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlError the xml error
     */
    public void addXMLError(String xmlError){
        xmlErrors.add(xmlError);
    }

    /**
     * Gets the xML errors.
     *
     * @return the xML errors
     */
    public String getXMLErrors(){
        StringBuilder results = new StringBuilder();

        results.append("XML Errors Found: \n");
        for (String thisError : xmlErrors){
            results.append(thisError).append("\n");
        }
        if (xmlErrors.size() == 0)results.append("None");
        return results.toString();
    }

    /**
     * Adds the xml schema validation error.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlError the xml error
     */
    public void addXMLSchemaValidationError(String xmlError){
        xmlSchemaValidationErrors.add(xmlError);
    }

    
       /**
        * Gets the xML schema validation errors.
        *
        * @return the xML schema validation errors
        */
       public String getXMLSchemaValidationErrors(){
        StringBuilder results = new StringBuilder();

        results.append("XML Errors Found: \n");
        for (String thisError : xmlSchemaValidationErrors){
            results.append(thisError).append("\n");
        }
        if (xmlSchemaValidationErrors.size() == 0)results.append("None");
        return results.toString();
    }
 
    /**
     * NTCIP 2306 4.1.1.c (Optional due to should.)
     *
     * @return true, if is xM lv1 header
     */
    public boolean isXMLv1Header() {
        return xmlV1Header;
    }

    /**
     * Sets the xM lv1 header.
     *
     * @param hasXMLv1Header the new xM lv1 header
     */
    public void setXMLv1Header(boolean hasXMLv1Header) {
        this.xmlV1Header = hasXMLv1Header;
    }

    /**
     * NTCIP 2306 4.1.1.b
     *
     * @return true, if is uT f8 char set
     */
    public boolean isUTF8CharSet() {
        return utf8CharSet;
    }

    /**
     * Sets the uT f8 char set.
     *
     * @param isUTF8CharSet the new uT f8 char set
     */
    public void setUTF8CharSet(boolean isUTF8CharSet) {
        this.utf8CharSet = isUTF8CharSet;
    }

    /**
     * NTCIP 2306 4.1.1.a
     *
     * @return true, if is xM lv1 document
     */
    public boolean isXMLv1Document() {
        return xmlV1Document;
    }

    /**
     * Sets the xM lv1 document.
     *
     * @param isXMLv1Document the new xM lv1 document
     */
    public void setXMLv1Document(boolean isXMLv1Document) {
        this.xmlV1Document = isXMLv1Document;
    }

    /**
     * Checks if is xml validated to wsdl schemas.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is xml validated to wsdl schemas
     */
    public boolean isXmlValidatedToWSDLSchemas() {
        return xmlValidatedToWSDLSchemas;
    }

    /**
     * Sets the xml validated to wsdl schemas.
     *
     * @param xmlValidatedToWSDLSchemas the new xml validated to wsdl schemas
     */
    public void setXmlValidatedToWSDLSchemas(boolean xmlValidatedToWSDLSchemas) {
        this.xmlValidatedToWSDLSchemas = xmlValidatedToWSDLSchemas;
    }

    /**
     * Gets the test assertion list.
     *
     * @return the test assertion list
     */
    public ArrayList<TestAssertion> getTestAssertionList() {
        return testAssertionList;
    }

    /**
     * Sets the test assertion list.
     *
     * @param testAssertionList the new test assertion list
     */
    public void setTestAssertionList(ArrayList<TestAssertion> testAssertionList) {
        this.testAssertionList = testAssertionList;
    }

}
