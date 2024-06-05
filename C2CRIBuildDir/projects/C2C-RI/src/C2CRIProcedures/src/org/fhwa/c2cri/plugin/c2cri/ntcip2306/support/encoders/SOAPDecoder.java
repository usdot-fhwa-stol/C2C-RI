/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.encoders;

import org.fhwa.c2cri.ntcip2306v109.messaging.MessageSpecificationProcessor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.xmlbeans.XmlError;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.verification.v01_69.MessageValidator;
import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.verification.v01_69.SOAPMessageValidator;
//import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.wsdl.RIWSDL;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;

/**
 *
 * @author TransCore ITS
 */
public class SOAPDecoder implements NTCIP2306Decoder {

    private String decoderType = "SOAP";
    private boolean soapDecoded = false;
    private String soapDecodeResults = "";
    private boolean soapValidatedToSchema = false;
    private boolean validXmlv1 = false;
    private boolean utf8Encoded = false;
    private String messageType = "";
    private int messagePartsReceived = 0;
    private ArrayList<String> parserErrorList = new ArrayList<String>();
    private ArrayList<String> fieldErrorList = new ArrayList<String>();
    private ArrayList<String> valueErrorList = new ArrayList<String>();
    protected static Logger log = LogManager.getLogger(SOAPDecoder.class.getName());

    public SOAPDecoder(String decoderType) {
        this.decoderType = decoderType;
    }

    public byte[] decode(byte[] encodedMessage, RIWSDL userWSDL, RIWSDL infoStdWSDL) throws Exception {
        soapDecoded = false;
        soapDecodeResults = "";
        parserErrorList.clear();
        fieldErrorList.clear();
        valueErrorList.clear();
        messageType = "";
        utf8Encoded = false;
        validXmlv1 = false;
        soapValidatedToSchema = false;
        // Check for XML version 1.0 document
        MessageValidator theValidator = new MessageValidator();
        validXmlv1 = theValidator.isValidXML(new String(encodedMessage));
        String xmlValidErrors = "";
        for (String xmlError : theValidator.getErrors()) {
            xmlValidErrors = xmlValidErrors.concat(xmlError) + "\n";
        }
        if (!xmlValidErrors.contains("Fatal Error"))validXmlv1 = true;
        log.debug("validXmlv1 = "+validXmlv1 + " : with errors "+xmlValidErrors);
        //       String xmlSchemaValidErrors = "";


        if (infoStdWSDL != null) {
            Map<String, URL> schemaMap = infoStdWSDL.getWsdlSchemas();
            List<String> schemaList = new ArrayList<String>();
            Iterator schemaIterator = schemaMap.values().iterator();
            while (schemaIterator.hasNext()) {
                schemaList.add(((URL) schemaIterator.next()).getPath());
            }
            URL soapEnvURL = SOAPDecoder.class.getResource("/org/fhwa/c2cri/plugin/c2cri/ntcip2306/support/verification/v01_69/Soap-Envelope.xsd");
//            schemaList.add(soapEnvURL.toString());
            schemaList.add((soapEnvURL.getProtocol().equalsIgnoreCase("jar")?"jar:":"")+soapEnvURL.getPath());

//            String schemaValidDir = System.getProperty("user.dir") + "/testfiles/";  // Get the directory the application was started from
//            schemaList.add(schemaValidDir + "Soap-Envelope.xsd");

            theValidator.addSchemas(schemaList);
            try {
                soapValidatedToSchema = theValidator.checkValidString(new String(encodedMessage,  "UTF-8"));
                parserErrorList = theValidator.getParserErrorList();
                fieldErrorList = theValidator.getFieldErrorList();
                valueErrorList = theValidator.getValueErrorList();

                if (soapValidatedToSchema) {
                    List<XmlError> soapErrors = new ArrayList<XmlError>();
                    SOAPMessageValidator soapValidator = new SOAPMessageValidator(infoStdWSDL);
                    try {
                        soapValidator.validateSoapEnvelope(new String(encodedMessage,  "UTF-8"), soapErrors);
                        for (XmlError thisError : soapErrors) {
                            parserErrorList.add(thisError.toString());
                        }
                    } catch (Exception ex) {
                        parserErrorList.add("Error encountered verifying the SOAP Encoding of the message. Exception:" + ex.getMessage());
                        theValidator.getErrors().add("Error encountered verifying the SOAP Encoding of the message.");

                    }
                    if (soapErrors.isEmpty()) {
                        String[] messageTypes = soapValidator.getSoapMessageTypes(new String(encodedMessage,  "UTF-8"));
                        if (messageTypes.length == 1) {
                            messageType = messageTypes[0];
                            messagePartsReceived = 1;
                        } else if (messageTypes.length == 2) {
                            messageType = messageTypes[1];
                            messagePartsReceived = 1;
                        } else if (messageTypes.length > 2) {
                            messagePartsReceived = messageTypes.length;
                            messageType = messageTypes[messageTypes.length - 1];
                        }
                        soapDecoded = true;

                    }
                }
            } catch (Exception ex) {
                soapDecoded = false;
                theValidator.getErrors().add("Error encountered verifying the SOAP Encoding of the message.");
                System.out.println(" Exception trying to validate the xml file using the info standard WSDL " + ex.getMessage());
            } finally {
                if (theValidator.getErrors().size() > 0) {
                    for (String xmlSchemaError : theValidator.getErrors()) {
                        xmlValidErrors = xmlValidErrors.concat(xmlSchemaError) + "\n";
                    }
                }

            }

        }

        if (userWSDL != null) {
            Map<String, URL> schemaMap = userWSDL.getWsdlSchemas();
            List<String> schemaList = new ArrayList<String>();
            Iterator schemaIterator = schemaMap.values().iterator();
            while (schemaIterator.hasNext()) {
                schemaList.add(((URL) schemaIterator.next()).getPath());
            }
//            String schemaValidDir = System.getProperty("user.dir") + "/testfiles/";  // Get the directory the application was started from
            URL soapEnvURL = SOAPDecoder.class.getResource("/org/fhwa/c2cri/plugin/c2cri/ntcip2306/support/verification/v01_69/Soap-Envelope.xsd");

            schemaList.add((soapEnvURL.getProtocol().equalsIgnoreCase("jar")?"jar:":"")+soapEnvURL.getPath());

//   Add only if Soap Encoding is selected for this service.  Otherwise a SOAP message could validate
//   when a basic XML message is required.
            theValidator.addSchemas(schemaList);
            try {
                if (infoStdWSDL == null) {
                    soapValidatedToSchema = true;
                }
                log.debug("SOAPDecoder: About to validate the encoded message with null status: " + ((encodedMessage == null) ? "True" : "False"));
                soapValidatedToSchema = soapValidatedToSchema & theValidator.checkValidString(new String(encodedMessage,  "UTF-8"));
                parserErrorList.addAll(theValidator.getParserErrorList());
                fieldErrorList.addAll(theValidator.getFieldErrorList());
                valueErrorList.addAll(theValidator.getValueErrorList());
                log.debug("Added errors to the main list.");

                if (soapValidatedToSchema) {
                    List<XmlError> soapErrors = new ArrayList<XmlError>();
                    log.debug("Creating soapValidator ...");
                    SOAPMessageValidator soapValidator = new SOAPMessageValidator(userWSDL);
                    log.debug("Validating SoapEnvelope ...");
                    try {
                        soapValidator.validateSoapEnvelope(new String(encodedMessage, "UTF-8"), soapErrors);
                        if (!soapErrors.isEmpty())log.debug("Error in \n"+new String(encodedMessage));
                        for (XmlError thisError : soapErrors) {
                            log.debug(thisError.toString()+" : Message="+thisError.getMessage() + "@Column "+thisError.getColumn());
                            parserErrorList.add(thisError.toString());
                        }
                    } catch (Exception ex) {
                        log.debug("Exception " +ex.getMessage());
                        parserErrorList.add("Error encountered verifying the SOAP Encoding of the message. Exception:" + ex.getMessage());
                        theValidator.getErrors().add("Error encountered verifying the SOAP Encoding of the message.");

                    }
                    if (soapErrors.isEmpty()) {
                        log.debug("Getting Message Types ...");
                        String[] messageTypes = soapValidator.getSoapMessageTypes(new String(encodedMessage,  "UTF-8"));
                        log.debug("Message Types => " + messageTypes);
                        if (messageTypes.length == 1) {
                            messageType = messageTypes[0];
                            messagePartsReceived = 1;
                        } else if (messageTypes.length == 2) {
                            messageType = messageTypes[1];
                            messagePartsReceived = 1;
                        } else if (messageTypes.length > 2) {
                            messagePartsReceived = messageTypes.length;
                            messageType = messageTypes[messageTypes.length - 1];
                        }
                        log.debug("SOAPDecoder: MessageParts Received = " + messagePartsReceived + " MessageType = " + messageType);
//                    System.out.println("SOAPDecoder: MessageParts Received = " + messagePartsReceived + " MessageType = " + messageType);
                        soapDecoded = true;
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                soapDecoded = false;
                theValidator.getErrors().add("Error encountered verifying the SOAP Encoding of the message.");
                log.debug(" Exception trying to validate the xml file using user supplied WSDL " + ex.getMessage());
//                System.out.println(" Exception trying to validate the xml file " + ex.getMessage());
            } finally {
                if (theValidator.getErrors().size() > 0) {
                    for (String xmlSchemaError : theValidator.getErrors()) {
                        xmlValidErrors = xmlValidErrors.concat(xmlSchemaError) + "\n";
                    }
                }

            }

        }

        // Check for UTF-8 encoding
        utf8Encoded = theValidator.isUTF8Encoded(encodedMessage);


//        MessageSpecificationProcessor msp = new MessageSpecificationProcessor();
//        MessageSpecification thisSpecification = msp.convertXMLtoMessageSpecification(encodedMessage);
        soapDecodeResults = xmlValidErrors;
        byte[] emptyBytes = null;
		if (encodedMessage != null)
			log.debug("validXmlv1 = "+validXmlv1 + " : with Message Length "+(validXmlv1?encodedMessage.length:0));
		else
			log.debug("validXmlv1 = "+validXmlv1 + " : with Message Length 0");
        return validXmlv1 ? encodedMessage : emptyBytes;
    }

    public MessageSpecification decodeToSpec(byte[] encodedMessage, RIWSDL userWSDL, RIWSDL infoStdWSDL) {
        soapDecoded = false;
        soapDecodeResults = "";
        parserErrorList.clear();
        fieldErrorList.clear();
        valueErrorList.clear();
        messageType = "";
        utf8Encoded = false;
        validXmlv1 = false;
        soapValidatedToSchema = false;

        // Check for XML version 1.0 document
        MessageValidator theValidator = new MessageValidator();
        validXmlv1 = theValidator.isValidXML(new String(encodedMessage));
        String xmlValidErrors = "";
        for (String xmlError : theValidator.getErrors()) {
            xmlValidErrors = xmlValidErrors.concat(xmlError) + "\n";
        }
        log.debug("validXmlv1 = "+validXmlv1 + " : with errors "+xmlValidErrors);

        String xmlSchemaValidErrors = "";


        if (infoStdWSDL != null) {
            Map<String, URL> schemaMap = infoStdWSDL.getWsdlSchemas();
            List<String> schemaList = new ArrayList<String>();
            Iterator schemaIterator = schemaMap.values().iterator();
            while (schemaIterator.hasNext()) {
                schemaList.add(((URL) schemaIterator.next()).getPath());
            }

//            String schemaValidDir = System.getProperty("user.dir") + "/testfiles/";  // Get the directory the application was started from
//            schemaList.add(schemaValidDir + "Soap-Envelope.xsd");

            theValidator.addSchemas(schemaList);
            try {
                List<XmlError> soapErrors = new ArrayList<XmlError>();
                soapValidatedToSchema = theValidator.checkValidString(new String(encodedMessage,  "UTF-8"));

                SOAPMessageValidator soapValidator = new SOAPMessageValidator(infoStdWSDL);
                try {
                    soapValidator.validateSoapEnvelope(new String(encodedMessage,  "UTF-8"), soapErrors);
                    for (XmlError thisError : soapErrors) {
                        parserErrorList.add(thisError.toString());
                    }
                } catch (Exception ex) {
                    parserErrorList.add("Error encountered verifying the SOAP Encoding of the message. Exception:" + ex.getMessage());
                    theValidator.getErrors().add("Error encountered verifying the SOAP Encoding of the message.");

                }
                if (soapErrors.isEmpty()) {
                    String[] messageTypes = soapValidator.getSoapMessageTypes(new String(encodedMessage,  "UTF-8"));
                    if (messageTypes.length == 1) {
                        messageType = messageTypes[0];
                        messagePartsReceived = 1;
                    } else if (messageTypes.length == 2) {
                        messageType = messageTypes[1];
                        messagePartsReceived = 1;
                    } else if (messageTypes.length > 2) {
                        messagePartsReceived = messageTypes.length;
                        messageType = messageTypes[messageTypes.length - 1];
                    }
                    soapDecoded = true;
                    log.debug("SOAPDecoder: MessageParts Received = " + messagePartsReceived + " MessageType = " + messageType);
                }

            } catch (Exception ex) {
                soapDecoded = false;
                soapValidatedToSchema = false;
                theValidator.getErrors().add("Error encountered verifying the SOAP Encoding of the message.");
                log.debug(" Exception trying to validate the xml file " + ex.getMessage());
            } finally {
                if (theValidator.getErrors().size() > 0) {
                    for (String xmlSchemaError : theValidator.getErrors()) {
                        xmlSchemaValidErrors = xmlValidErrors.concat(xmlSchemaError) + "\n";
                    }
                }

            }

        }

        if (userWSDL != null) {
            Map<String, URL> schemaMap = userWSDL.getWsdlSchemas();
            List<String> schemaList = new ArrayList<String>();
            Iterator schemaIterator = schemaMap.values().iterator();
            while (schemaIterator.hasNext()) {
                schemaList.add(((URL) schemaIterator.next()).getPath());
            }
//            String schemaValidDir = System.getProperty("user.dir") + "/testfiles/";  // Get the directory the application was started from
//            schemaList.add(schemaValidDir + "Soap-Envelope.xsd");

//   Add only if Soap Encoding is selected for this service.  Otherwise a SOAP message could validate
//   when a basic XML message is required.
            theValidator.addSchemas(schemaList);
            try {
                List<XmlError> soapErrors = new ArrayList<XmlError>();
                soapValidatedToSchema = soapValidatedToSchema & theValidator.checkValidString(new String(encodedMessage,  "UTF-8"));
                SOAPMessageValidator soapValidator = new SOAPMessageValidator(userWSDL);
                try {
                    soapValidator.validateSoapEnvelope(new String(encodedMessage,  "UTF-8"), soapErrors);
                    for (XmlError thisError : soapErrors) {
                        parserErrorList.add(thisError.toString());
                    }
                } catch (Exception ex) {
                    parserErrorList.add("Error encountered verifying the SOAP Encoding of the message. Exception:" + ex.getMessage());
                    theValidator.getErrors().add("Error encountered verifying the SOAP Encoding of the message.");

                }
                if (soapErrors.isEmpty()) {
                    String[] messageTypes = soapValidator.getSoapMessageTypes(new String(encodedMessage,  "UTF-8"));
                    if (messageTypes.length == 1) {
                        messageType = messageTypes[0];
                        messagePartsReceived = 1;
                    } else if (messageTypes.length == 2) {
                        messageType = messageTypes[1];
                        messagePartsReceived = 1;
                    } else if (messageTypes.length > 2) {
                        messagePartsReceived = messageTypes.length;
                        messageType = messageTypes[messageTypes.length - 1];
                    }
                    soapDecoded = true;
                    log.debug("SOAPDecoder: MessageParts Received = " + messagePartsReceived + " MessageType = " + messageType);
                }
            } catch (Exception ex) {
                soapDecoded = false;
                theValidator.getErrors().add("Error encountered verifying the SOAP Encoding of the message.");
                log.debug(" Exception trying to validate the xml file " + ex.getMessage());
            } finally {
                if (theValidator.getErrors().size() > 0) {
                    for (String xmlSchemaError : theValidator.getErrors()) {
                        xmlSchemaValidErrors = xmlValidErrors.concat(xmlSchemaError) + "\n";
                    }
                }

            }

        }

        // Check for UTF-8 encoding
        utf8Encoded = theValidator.isUTF8Encoded(encodedMessage);


        MessageSpecificationProcessor msp = new MessageSpecificationProcessor();
        MessageSpecification thisSpecification = msp.convertXMLtoMessageSpecification(encodedMessage);
        soapDecodeResults = xmlSchemaValidErrors;

        return thisSpecification;
    }

    public String getDecoderType() {
        return decoderType;
    }

    public static Logger getLog() {
        return log;
    }

    public String getMessageType() {
        return messageType;
    }

    public String getXmlDecodeResults() {
        return soapDecodeResults;
    }

    public boolean isXmlDecoded() {
        return soapDecoded;
    }

    public boolean isXmlValidatedToSchema() {
        return soapValidatedToSchema;
    }

    public boolean isUtf8Encoded() {
        return utf8Encoded;
    }

    public boolean isValidXmlv1() {
        return validXmlv1;
    }

    @Override
    public String getGzipDecodingResults() {
        return "";
    }

    @Override
    public boolean isGzipDecoded() {
        return false;
    }

    @Override
    public ArrayList<String> getFieldErrorList() {
        return fieldErrorList;
    }

    @Override
    public ArrayList<String> getParserErrorList() {
        return parserErrorList;
    }

    @Override
    public ArrayList<String> getValueErrorList() {
        return valueErrorList;
    }

    public int getNumberOfMessagePartsReceived() {
        return messagePartsReceived;
    }
}
