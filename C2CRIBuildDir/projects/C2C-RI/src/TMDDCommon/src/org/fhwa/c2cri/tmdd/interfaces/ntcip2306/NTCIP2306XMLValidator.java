/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.interfaces.ntcip2306;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.fhwa.c2cri.ntcip2306v109.encoding.SOAPDecoder;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.tmdd.TMDDSettingsImpl;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class NTCIP2306XMLValidator validates the message using the TMDD xml
 * representation which conforms to NTCIP 2306.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class NTCIP2306XMLValidator {

    /**
     * The this validator.
     */
    private static NTCIP2306XMLValidator thisValidator;

    /**
     * The xml validator.
     */
    private static XMLValidator xmlValidator;

    /**
     * The parse result.
     */
    private boolean parseResult = false;

    /**
     * The parser error list.
     */
    private final ArrayList<String> parserErrorList = new ArrayList<String>();

    /**
     * The field error list.
     */
    private final ArrayList<String> fieldErrorList = new ArrayList<String>();

    /**
     * The value error list.
     */
    private final ArrayList<String> valueErrorList = new ArrayList<String>();

    /**
     * The schema validation content error type list.
     */
    private final ArrayList<String> schemaValidationContentErrorList = new ArrayList<String>();

    /**
     * The schema validation value error type list.
     */
    private final ArrayList<String> schemaValidationValueErrorList = new ArrayList<String>();

    /**
     * The schema validation other error type list.
     */
    private final ArrayList<String> schemaValidationOtherErrorList = new ArrayList<String>();

    /**
     * The soap decoding processor in case a NTCIP 2306 message has its
     * skipEncoding flag set
     */
    private static SOAPDecoder soapDecoder = new SOAPDecoder();

    /**
     * The schema validation errors.
     */
    private static String schemaValidationErrors = "";

    private static ArrayList<URL> schemaList = new ArrayList();

    /**
     * Instantiates a new nTCI p2306 xml validator.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private NTCIP2306XMLValidator() {
        super();
    }

    /**
     * Gets the single instance of NTCIP2306XMLValidator.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return single instance of NTCIP2306XMLValidator
     */
    public static NTCIP2306XMLValidator getInstance() {
        if (thisValidator == null) {
            thisValidator = new NTCIP2306XMLValidator();

            xmlValidator = new XMLValidator();
            xmlValidator.setSchemaReferenceList(schemaList);
        }
        return thisValidator;
    }

    /**
     * Sets the set of schemas that will be used to validate messages
     *
     * @param tmddSchemaList
     */
    public static void setSchemas(ArrayList<URL> tmddSchemaList) {
        schemaList = tmddSchemaList;
        if (xmlValidator != null)xmlValidator.setSchemaReferenceList(schemaList);
    }

    /**
     * Checks if is xML validated to schema.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param xmlSource the xml source
     * @return true, if is xML validated to schema
     */
    public boolean isXMLValidatedToSchema(byte[] xmlSource) {

        boolean validationResults = false;

        parserErrorList.clear();
        valueErrorList.clear();
        fieldErrorList.clear();
        schemaValidationValueErrorList.clear();
        schemaValidationContentErrorList.clear();
        schemaValidationOtherErrorList.clear();
        schemaValidationErrors = "";

        xmlValidator.isValidXML(xmlSource);
        parserErrorList.addAll(xmlValidator.getParserErrorList());
        valueErrorList.addAll(xmlValidator.getValueErrorList());
        fieldErrorList.addAll(xmlValidator.getFieldErrorList());

        xmlValidator.isXMLValidatedToSchema(xmlSource);
        schemaValidationErrors = xmlValidator.getXMLStatus().getXMLSchemaValidationErrors();
        schemaValidationContentErrorList.addAll(xmlValidator.getSchemaValidationContentErrorList());
        schemaValidationValueErrorList.addAll(xmlValidator.getSchemaValidationValueErrorList());
        schemaValidationOtherErrorList.addAll(xmlValidator.getSchemaValidationOtherErrorList());
        return xmlValidator.isValidToSchemas();
    }

    /**
     * Gets the xML message type.
     *
     * @param encodedMessage the encoded message
     * @return the xML message type
     */
    private QName getXMLMessageType(InputStream encodedMessage) {
        return xmlValidator.getXMLMessageType(encodedMessage);
    }

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    private QName getMessageType() {
        return xmlValidator.getMessageType();
    }

    /**
     * Gets the message type name.
     *
     * @return the message type name
     * @throws Exception the exception
     */
    public String getMessageTypeName() throws Exception {
        return xmlValidator.getMessageType().getLocalPart();
    }

    /**
     * Gets the message type name space.
     *
     * @return the message type name space
     * @throws Exception the exception
     */
    public String getMessageTypeNameSpace() throws Exception {
        return xmlValidator.getMessageType().getNamespaceURI();
    }

    /**
     * Checks if is field validation ok.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is field validation ok
     */
    public boolean isFieldValidationOK() {
        System.out.println("isFieldValidationOK:: getFieldErrorList Length = " + xmlValidator.getFieldErrorList().size());
        System.out.println("isFieldValidationOK:: getSchemaValidationContentErrorList Length = " + xmlValidator.getSchemaValidationContentErrorList().size());
        if ((xmlValidator.getFieldErrorList().isEmpty()) && (xmlValidator.getSchemaValidationContentErrorList().isEmpty())) {
            return true;
        }
        System.out.println("isFieldValidationOK = false");
        return false;
    }

    /**
     * Gets the field validation errors.
     *
     * @return the field validation errors
     */
    public ArrayList<String> getFieldValidationErrors() {
        return xmlValidator.getSchemaValidationContentErrorList();
    }

    /**
     * Checks if is value validation ok.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is value validation ok
     */
    public boolean isValueValidationOK() {
        System.out.println("isValueValidationOK:: getValueErrorList Length = " + xmlValidator.getValueErrorList().size());
        System.out.println("isValueValidationOK:: getSchemaValidationValueErrorList Length = " + xmlValidator.getSchemaValidationValueErrorList().size());

        if ((xmlValidator.getValueErrorList().isEmpty()) && (xmlValidator.getSchemaValidationValueErrorList().isEmpty())) {
            return true;
        }
        System.out.println("isValueValidationOK = false");
        return false;
    }

    /**
     * Gets the value validation errors.
     *
     * @return the value validation errors
     */
    public ArrayList<String> getValueValidationErrors() {
        return xmlValidator.getSchemaValidationValueErrorList();
    }

    /**
     * Checks if is parser validation ok.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is parser validation ok
     */
    public boolean isparserValidationOK() {
        if (xmlValidator.getParserErrorList().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Gets the parser validation errors.
     *
     * @return the parser validation errors
     */
    public ArrayList<String> getParserValidationErrors() {
        return xmlValidator.getParserErrorList();
    }

    /**
     * Gets the schema validation errors.
     *
     * @return the schema validation errors
     */
    public String getSchemaValidationErrors() {
        return schemaValidationErrors;
    }

    /**
     * Gets the schema validation errors related to Content.
     *
     * @return the schema content validation errors
     */
    public ArrayList<String> getSchemaValidationContentErrorList() {
        return schemaValidationContentErrorList;
    }

    /**
     * Gets the schema validation errors related to Values..
     *
     * @return the schema value validation errors
     */
    public ArrayList<String> getSchemaValidationValueErrorList() {
        return schemaValidationValueErrorList;
    }

    /**
     * Gets the schema validation errors related to other issues (not content or
     * values).
     *
     * @return the schema other validation errors
     */
    public ArrayList<String> getSchemaValidationOtherErrorList() {
        return schemaValidationOtherErrorList;
    }

    public byte[] getSOAPMessagePart(byte[] messageContent, int messagePart) throws Exception {

        boolean result = soapDecoder.isSoapEncoded(messageContent);

        if (result) {
            return soapDecoder.getMessagePart(messagePart);
        }

        return null;

    }

    public String getSOAPBodyMessageName(int messagePart) throws Exception {
        return soapDecoder.getMessagePartElementName(messagePart).getLocalPart();
    }

    public String getSOAPBodyMessageNameSpace(int messagePart) throws Exception {
        return soapDecoder.getMessagePartElementName(messagePart).getNamespaceURI();
    }
}
