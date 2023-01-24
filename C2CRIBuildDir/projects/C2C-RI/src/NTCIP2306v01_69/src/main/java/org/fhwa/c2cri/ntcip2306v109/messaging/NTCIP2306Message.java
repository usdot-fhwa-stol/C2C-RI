/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.messaging;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.xml.namespace.QName;
import org.fhwa.c2cri.messagemanager.MessageContent;
import org.fhwa.c2cri.messagemanager.MessageContentFactory;
import org.fhwa.c2cri.ntcip2306v109.encoding.NTCIP2306EncodingException;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306TransportException;
import org.fhwa.c2cri.ntcip2306v109.encoding.SOAPDecoder;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidatorFactory;
import org.fhwa.c2cri.ntcip2306v109.status.FTPStatus;
import org.fhwa.c2cri.ntcip2306v109.status.GZIPStatus;
import org.fhwa.c2cri.ntcip2306v109.status.HTTPStatus;
import org.fhwa.c2cri.ntcip2306v109.status.SOAPStatus;
import org.fhwa.c2cri.ntcip2306v109.status.XMLStatus;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class NTCIP2306Message.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NTCIP2306Message {

    /**
     * The Enum MESSAGETYPE.
     */
    public static enum MESSAGETYPE {

        /** The unknown. */
        UNKNOWN, /** The request. */
        REQUEST, /** The response. */
        RESPONSE, /** The error. */
        ERROR, /** The exception. */
        EXCEPTION
    };

    /**
     * The Enum ENCODINGTYPE.
     */
    public static enum ENCODINGTYPE {

        /** The unknown. */
        UNKNOWN, /** The soap. */
        SOAP, /** The gzip. */
        GZIP, /** The xml. */
        XML
    };

    /**
     * The Enum PROTOCOLTYPE.
     */
    public static enum PROTOCOLTYPE {

        /** The unknown. */
        UNKNOWN, /** The ftp. */
        FTP, /** The http. */
        HTTP, /** The https. */
        HTTPS
    };
    
    /** The message type. */
    private MESSAGETYPE messageType = MESSAGETYPE.UNKNOWN;
    
    /** The encoding type. */
    private ENCODINGTYPE encodingType = ENCODINGTYPE.UNKNOWN;
    
    /** The protocol type. */
    private PROTOCOLTYPE protocolType = PROTOCOLTYPE.UNKNOWN;
    
    /** The xml status. */
    private XMLStatus xmlStatus = new XMLStatus();
    
    /** The soap status. */
    private SOAPStatus soapStatus = new SOAPStatus();
    
    /** The gzip status. */
    private GZIPStatus gzipStatus = new GZIPStatus();
    
    /** The http status. */
    private HTTPStatus httpStatus = new HTTPStatus();
    
    /** The ftp status. */
    private FTPStatus ftpStatus = new FTPStatus();
    
    /** The transport error encountered. */
    private boolean transportErrorEncountered = false;
    
    /** The transport error description. */
    private String transportErrorDescription = "";
    
    /** The message payload. */
    private final ArrayList<MessagePart> messagePayload = new ArrayList<>();
    
    /** The skip encoding. */
    private boolean skipEncoding = false;
    
    /** The message history. */
    private final HashMap<BigInteger, String> messageHistory = new HashMap<>();
    
    /** The publication url. */
    private String publicationURL = "";
    
    /** The publication soap action. */
    private String publicationSoapAction = "";
    
    /** The publication message. */
    private boolean publicationMessage = false;
    
    /** The test assertions. */
    private ArrayList<TestAssertion> testAssertions = new ArrayList<>();
    
    /** The transported time in ms. */
    private long transportedTimeInMs = -1;

    /**
     * Instantiates a new NTCIP2306 message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param nameSpace the name space
     * @param name the name
     * @param content the content
     */
    public NTCIP2306Message(String nameSpace, String name, byte[] content) {
        addMessagePart(nameSpace, name, content);
    }

    /**
     * Instantiates a new NTCIP2306 message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param content the content
     */
    public NTCIP2306Message(InputStream content) {
        XMLValidator validator = XMLValidatorFactory.getValidator();
        QName message = validator.getXMLMessageType(content);
        if (message != null) {
            addMessagePart(message.getNamespaceURI(), message.getLocalPart(), content);
        } else {
            addMessagePart("Unknown", "Unknown", content);
        }
    }

    /**
     * Instantiates a new NTCIP2306 message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param content the content
     */
    public NTCIP2306Message(byte[] content, boolean skipEncoding) {
        this.skipEncoding = skipEncoding;
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        XMLValidator validator = XMLValidatorFactory.getValidator();

        SOAPDecoder soapDecoder = new SOAPDecoder();
        boolean decodeResult = false;
        try {
            decodeResult = soapDecoder.isSoapEncoded(content);
            if (decodeResult && (!skipEncoding)) {
                for (int ii = 1; ii <= soapDecoder.getNumMessageParts(); ii++) {
                    QName message = soapDecoder.getMessagePartElementName(ii);
                    addMessagePart(message.getNamespaceURI(), message.getLocalPart(), soapDecoder.getMessagePart(ii));
                }
            } else {
                QName message = validator.getXMLMessageType(bais);
                if (message != null) {
                    addMessagePart(message.getNamespaceURI(), message.getLocalPart(), content);
                } else {
                    addMessagePart("Unknown", "Unknown", content);
                }
            }
        } catch (Exception ex) {
            QName message = validator.getXMLMessageType(bais);
            if (message != null) {
                addMessagePart(message.getNamespaceURI(), message.getLocalPart(), content);
            } else {
                addMessagePart("Unknown", "Unknown", content);
            }
        }
    }

    /**
     * Instantiates a new NTCIP2306 message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param content the content
     */
    public NTCIP2306Message(byte[] content) {
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        XMLValidator validator = XMLValidatorFactory.getValidator();

        SOAPDecoder soapDecoder = new SOAPDecoder();
        boolean decodeResult = false;
        try {
            decodeResult = soapDecoder.isSoapEncoded(content);
            if (decodeResult) {
                for (int ii = 1; ii <= soapDecoder.getNumMessageParts(); ii++) {
                    QName message = soapDecoder.getMessagePartElementName(ii);
                    addMessagePart(message.getNamespaceURI(), message.getLocalPart(), soapDecoder.getMessagePart(ii));
                }
            } else {
                QName message = validator.getXMLMessageType(bais);
                if (message != null) {
                    addMessagePart(message.getNamespaceURI(), message.getLocalPart(), content);
                } else {
                    addMessagePart("Unknown", "Unknown", content);
                }
            }
        } catch (Exception ex) {
            QName message = validator.getXMLMessageType(bais);
            if (message != null) {
                addMessagePart(message.getNamespaceURI(), message.getLocalPart(), content);
            } else {
                addMessagePart("Unknown", "Unknown", content);
            }
        }

    }

    /**
     * Adds the message part.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param nameSpace the name space
     * @param name the name
     * @param content the content
     */
    public final void addMessagePart(String nameSpace, String name, byte[] content) {
        MessagePart mp = new MessagePart(nameSpace, name, content);
        messagePayload.add(mp);
    }

    /**
     * Adds the message part.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param nameSpace the name space
     * @param name the name
     * @param content the content
     */
    public final void addMessagePart(String nameSpace, String name, InputStream content) {
        MessagePart mp = new MessagePart(nameSpace, name, content);
        messagePayload.add(mp);
    }

    
    /**
     * Adds the message part.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param nameSpace the name space
     * @param name the name
     * @param content the content
     */
    public final void addMessagePart(String nameSpace, String name, MessageContent content) {
        MessagePart mp = new MessagePart(nameSpace, name, content);
        messagePayload.add(mp);
    }
    
    /**
     * Forces the encoding of a message.  If the message consists of two parts then the 
     * two separate parts are created.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: Changes the encoded message parts and clears the skipEncoding flag
     *
     */
    public final boolean forceEncode() {
        SOAPDecoder soapDecoder = new SOAPDecoder();
        boolean decodeResult = false;
        try {
            if (this.getNumberMessageParts() == 1){
                decodeResult = soapDecoder.isSoapEncoded(getMessagePartContent(1));
                if (decodeResult) {
                    removeMessagePart(1);
                    for (int ii = 1; ii <= soapDecoder.getNumMessageParts(); ii++) {
                        QName message = soapDecoder.getMessagePartElementName(ii);
                        addMessagePart(message.getNamespaceURI(), message.getLocalPart(), soapDecoder.getMessagePart(ii));
                    }
                    this.skipEncoding = false;
                    return true;
                } else return false;               
            }
            return false;
        } catch (Exception ex){
            return false;
        }
    }
    
    
    /**
     * Gets the number message parts.
     *
     * @return the number message parts
     */
    public int getNumberMessageParts() {
        return messagePayload.size();
    }

    /**
     * Removes the message part.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param partNumber the part number
     */
    public void removeMessagePart(int partNumber) {
        if (getNumberMessageParts() >= partNumber) {
            messagePayload.remove(partNumber - 1);
        }
    }

    /**
     * Gets the message part name space.
     *
     * @param partNumber the part number
     * @return the message part name space
     */
    public String getMessagePartNameSpace(int partNumber) {
        String nameSpace = null;
        if (getNumberMessageParts() >= partNumber) {
            nameSpace = messagePayload.get(partNumber - 1).getNameSpace();
        }
        return nameSpace;
    }

    /**
     * Gets the message part name.
     *
     * @param partNumber the part number
     * @return the message part name
     */
    public String getMessagePartName(int partNumber) {
        String name = "Unknown";
        if (getNumberMessageParts() >= partNumber) {
            name = messagePayload.get(partNumber - 1).getLocalName();
        }
        return name;
    }

    /**
     * Gets the message part content.
     *
     * @param partNumber the part number
     * @return the message part content
     */
    public byte[] getMessagePartContent(int partNumber) {
        byte[] content = null;
        if (getNumberMessageParts() >= partNumber) {
            content = messagePayload.get(partNumber - 1).getContent();
        }
        return content;
    }

    /**
     * Gets the message part content.
     *
     * @param partNumber the part number
     * @return the message part content
     */
    public MessageContent getMessagePartContentReference(int partNumber) {
        MessageContent content = null;
        if (getNumberMessageParts() >= partNumber) {
            content = messagePayload.get(partNumber - 1).getContentReference();
        }
        return content;
    }
    
    /**
     * Checks if is skip encoding.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is skip encoding
     */
    public boolean isSkipEncoding() {
        return skipEncoding;
    }

    /**
     * Sets the skip encoding.
     *
     * @param skipEncoding the new skip encoding
     */
    public void setSkipEncoding(boolean skipEncoding) {
        this.skipEncoding = skipEncoding;
    }

    /**
     * Log history event.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param eventDescription the event description
     */
    public void logHistoryEvent(String eventDescription) {
        BigInteger now = BigInteger.valueOf(new Date().getTime());
        messageHistory.put(now, eventDescription);
    }

    /**
     * Gets the ftp status.
     *
     * @return the ftp status
     */
    public FTPStatus getFtpStatus() {
        return ftpStatus;
    }

    /**
     * Sets the ftp status.
     *
     * @param ftpStatus the new ftp status
     */
    public void setFtpStatus(FTPStatus ftpStatus) {
        this.ftpStatus = ftpStatus;
    }

    /**
     * Gets the gzip status.
     *
     * @return the gzip status
     */
    public GZIPStatus getGzipStatus() {
        return gzipStatus;
    }

    /**
     * Sets the gzip status.
     *
     * @param gzipStatus the new gzip status
     */
    public void setGzipStatus(GZIPStatus gzipStatus) {
        this.gzipStatus = gzipStatus;
    }

    /**
     * Gets the http status.
     *
     * @return the http status
     */
    public HTTPStatus getHttpStatus() {
        return httpStatus;
    }

    /**
     * Sets the http status.
     *
     * @param httpStatus the new http status
     */
    public void setHttpStatus(HTTPStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    /**
     * Gets the soap status.
     *
     * @return the soap status
     */
    public SOAPStatus getSoapStatus() {
        return soapStatus;
    }

    /**
     * Sets the soap status.
     *
     * @param soapStatus the new soap status
     */
    public void setSoapStatus(SOAPStatus soapStatus) {
        this.soapStatus = soapStatus;
    }

    /**
     * Gets the xml status.
     *
     * @return the xml status
     */
    public XMLStatus getXmlStatus() {
        return xmlStatus;
    }

    /**
     * Sets the xml status.
     *
     * @param xmlStatus the new xml status
     */
    public void setXmlStatus(XMLStatus xmlStatus) {
        this.xmlStatus = xmlStatus;
    }

    /**
     * Gets the publication url.
     *
     * @return the publication url
     */
    public String getPublicationURL() {
        return publicationURL;
    }

    /**
     * Sets the publication url.
     *
     * @param publicationURL the new publication url
     */
    public void setPublicationURL(String publicationURL) {
        this.publicationURL = publicationURL;
    }

    /**
     * Gets the publication soap action.
     *
     * @return the publication soap action
     */
    public String getPublicationSoapAction() {
        return publicationSoapAction;
    }

    /**
     * Sets the publication soap action.
     *
     * @param publicationSoapAction the new publication soap action
     */
    public void setPublicationSoapAction(String publicationSoapAction) {
        this.publicationSoapAction = publicationSoapAction;
    }

    /**
     * Gets the test assertions.
     *
     * @return the test assertions
     */
    public ArrayList<TestAssertion> getTestAssertions() {
        return testAssertions;
    }

    /**
     * Adds the test assertion.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testAssertion the test assertion
     */
    public void addTestAssertion(TestAssertion testAssertion) {
        testAssertions.add(testAssertion);
    }

    /**
     * Gets the test assertion report.
     *
     * @param leadingSpaces the leading spaces
     * @return the test assertion report
     */
    public String getTestAssertionReport(int leadingSpaces) {
        String padString = "";
        for (int ii = 0; ii < leadingSpaces; ii++) {
            padString = padString.concat(" ");
        }
        StringBuffer assertionReport = new StringBuffer();
        assertionReport.append(padString).append(messageType.toString()).append(" MESSAGE TYPE \n");
        for (TestAssertion thisAssertion : testAssertions) {
            assertionReport.append("   ").append(thisAssertion.getTestAssertionID()).append(": ").append(thisAssertion.getTestAssertionPrescription()).append(": ").append(thisAssertion.getTestResult()).append("\n");
        }
        assertionReport.append(padString).append("    ").append(" GZIP Results \n");
        for (TestAssertion thisAssertion : gzipStatus.getTestAssertionList()) {
            assertionReport.append("     ").append(thisAssertion.getTestAssertionID()).append(": ").append(thisAssertion.getTestAssertionPrescription()).append(": ").append(thisAssertion.getTestResult()).append("\n");
        }

        assertionReport.append(padString).append("    ").append(" SOAP Results \n");
        for (TestAssertion thisAssertion : soapStatus.getTestAssertionList()) {
            assertionReport.append("     ").append(thisAssertion.getTestAssertionID()).append(": ").append(thisAssertion.getTestAssertionPrescription()).append(": ").append(thisAssertion.getTestResult()).append("\n");
        }

        assertionReport.append(padString).append("    ").append(" XML Results \n");
        for (TestAssertion thisAssertion : xmlStatus.getTestAssertionList()) {
            assertionReport.append("     ").append(thisAssertion.getTestAssertionID()).append(": ").append(thisAssertion.getTestAssertionPrescription()).append(": ").append(thisAssertion.getTestResult()).append("\n");
        }

        assertionReport.append(padString).append("    ").append(" FTP Results \n");
        for (TestAssertion thisAssertion : ftpStatus.getTestAssertionList()) {
            assertionReport.append("     ").append(thisAssertion.getTestAssertionID()).append(": ").append(thisAssertion.getTestAssertionPrescription()).append(": ").append(thisAssertion.getTestResult()).append("\n");
        }

        assertionReport.append(padString).append("    ").append(" HTTP Results \n");
        for (TestAssertion thisAssertion : httpStatus.getTestAssertionList()) {
            assertionReport.append("     ").append(thisAssertion.getTestAssertionID()).append(": ").append(thisAssertion.getTestAssertionPrescription()).append(": ").append(thisAssertion.getTestResult()).append("\n");
        }

        return assertionReport.toString();
    }

    /**
     * Gets the transported time in ms.
     *
     * @return the transported time in ms
     */
    public long getTransportedTimeInMs() {
        return transportedTimeInMs;
    }

    /**
     * Sets the transported time in ms.
     *
     * @param transportedTimeInMs the new transported time in ms
     */
    public void setTransportedTimeInMs(long transportedTimeInMs) {
        this.transportedTimeInMs = transportedTimeInMs;
    }

    /**
     * Verify message to op spec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param opSpec the op spec
     * @param msgType the msg type
     * @throws Exception the exception
     */
    public void verifyMessageToOpSpec(OperationSpecification opSpec, MESSAGETYPE msgType) throws Exception {
        boolean skipGetRequest = false;

        // Check the Input/Request Message
        if (msgType.equals(MESSAGETYPE.REQUEST)) {
            // Only verify for RR operations.  Get operations do not include Request Message XML content.
            if (!opSpec.isGetOperation()) {
                verifyMessageContent(opSpec.getInputMessage());
            } else {
                skipGetRequest = true;
            }
            // Check the Output/Response Message
        } else if (msgType.equals(MESSAGETYPE.RESPONSE)) {
            // The response message is specified as an Input for Get Operations.
            if (opSpec.isGetOperation()) {
                verifyMessageContent(opSpec.getInputMessage());
            } else {
                verifyMessageContent(opSpec.getOutputMessage());
            }

            // Check the Error/Fault Message
        } else if (msgType.equals(MESSAGETYPE.ERROR)) {
            verifyMessageContent(opSpec.getFaultMessage());
        }

        if (!skipGetRequest) {
            // Check the encoding status of the Message
            if (!getXmlStatus().isNTCIP2306ValidXML()) {
                System.err.println("NCTIP2306Message:: Error with " + messagePayload.get(0).getName());
                throw new NTCIP2306EncodingException("The Message contains XML Errors: " + getXmlStatus().getXMLErrors());
            }

            // Check the transmission status of the Request
            if (!getHttpStatus().isSuccessfullyTransmitted() && !getFtpStatus().isNTCIP2306ValidFTP()) {
                throw new NTCIP2306TransportException("The Message was not properly transmitted.");
            }
        }
    }

    /**
     * Verify message content.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param messageParts the message parts
     * @throws Exception the exception
     */
    private void verifyMessageContent(List<QName> messageParts) throws Exception {
        // Check the number of message parts
        if (!(messageParts.size()
                == getNumberMessageParts())) {
            throw new NTCIP2306MessageException("Message part counts don't match.  Expected " + messageParts.size() + " part(s) but received " + getNumberMessageParts());
        }

        for (int ii = 0; ii < messageParts.size(); ii++) {
            // Check the message name
            if (!messageParts.get(ii).getLocalPart().equals(
                    getMessagePartName(ii + 1))) {
                throw new NTCIP2306MessageException("Message part names don't match. "
                        + messageParts.get(ii).getLocalPart() + " <> "
                        + getMessagePartName(ii + 1));
            } else {
                System.err.println("ExternalMessage::verifyMessageContent "
                        + messageParts.get(ii).getLocalPart() + " = "
                        + getMessagePartName(ii + 1));
            }

            // Check the message name space
            if (!messageParts.get(ii).getNamespaceURI().equals(
                    getMessagePartNameSpace(ii + 1))) {
                throw new NTCIP2306MessageException("Message part name space does not match. "
                        + messageParts.get(ii).getNamespaceURI() + " <> "
                        + getMessagePartNameSpace(ii + 1));
            } else {
                System.err.println("ExternalMessage::verifyMessageContent "
                        + messageParts.get(ii).getNamespaceURI() + " = "
                        + getMessagePartNameSpace(ii + 1));
            }
        }

        if (!getXmlStatus().isXmlValidatedToWSDLSchemas()) {
            throw new NTCIP2306MessageValidationException("The message was not properly validated against the schema:  " + getXmlStatus().getXMLSchemaValidationErrors());
        }
    }

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    public MESSAGETYPE getMessageType() {
        return messageType;
    }

    /**
     * Sets the message type.
     *
     * @param messageType the new message type
     */
    public void setMessageType(MESSAGETYPE messageType) {
        this.messageType = messageType;
    }

    /**
     * Checks if is publication message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is publication message
     */
    public boolean isPublicationMessage() {
        return publicationMessage;
    }

    /**
     * Sets the publication message.
     *
     * @param publicationMessage the new publication message
     */
    public void setPublicationMessage(boolean publicationMessage) {
        this.publicationMessage = publicationMessage;
    }

    /**
     * Gets the encoding type.
     *
     * @return the encoding type
     */
    public ENCODINGTYPE getEncodingType() {
        return encodingType;
    }

    /**
     * Sets the encoding type.
     *
     * @param encodingType the new encoding type
     */
    public void setEncodingType(ENCODINGTYPE encodingType) {
        this.encodingType = encodingType;
    }

    /**
     * Gets the protocol type.
     *
     * @return the protocol type
     */
    public PROTOCOLTYPE getProtocolType() {
        return protocolType;
    }

    /**
     * Sets the protocol type.
     *
     * @param protocolType the new protocol type
     */
    public void setProtocolType(PROTOCOLTYPE protocolType) {
        this.protocolType = protocolType;
    }

    /**
     * Checks if is transport error encountered.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is transport error encountered
     */
    public boolean isTransportErrorEncountered() {
        return transportErrorEncountered;
    }

    /**
     * Sets the transport error encountered.
     *
     * @param transportErrorEncountered the new transport error encountered
     */
    public void setTransportErrorEncountered(boolean transportErrorEncountered) {
        this.transportErrorEncountered = transportErrorEncountered;
    }

    /**
     * Gets the transport error description.
     *
     * @return the transport error description
     */
    public String getTransportErrorDescription() {
        return transportErrorDescription;
    }

    /**
     * Sets the transport error description.
     *
     * @param transportErrorDescription the new transport error description
     */
    public void setTransportErrorDescription(String transportErrorDescription) {
        this.transportErrorDescription = transportErrorDescription;
    }

    /**
     * The Class MessagePart.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    private class MessagePart {

        /** The name. */
        private QName name;

        /** The content. */
        private MessageContent content;

        /**
         * Instantiates a new message part.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param nameSpace the name space
         * @param name the name
         * @param content the content
         */
        protected MessagePart(String nameSpace, String name, byte[] content) {
            setName(nameSpace, name);
            ByteArrayInputStream bais = new ByteArrayInputStream(content);

            setContent(bais);
        }

        /**
         * Instantiates a new message part.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param nameSpace the name space
         * @param name the name
         * @param content the content
         */
        protected MessagePart(String nameSpace, String name, InputStream content) {
            setName(nameSpace, name);
            setContent(content);
        }

        /**
         * Instantiates a new message part.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param nameSpace the name space
         * @param name the name
         * @param content the content
         */
        protected MessagePart(String nameSpace, String name, MessageContent content) {
            setName(nameSpace, name);
            this.content = content;
        }        
        
        /**
         * Sets the name.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param nameSpace the name space
         * @param name the name
         */
        protected final void setName(String nameSpace, String name) {
            this.name = new QName(nameSpace, name);
        }

        /**
         * Sets the local name.
         *
         * @param name the new local name
         */
        protected void setLocalName(String name) {
            String nameSpace;
            QName tempQName;
            if (this.name != null) {
                nameSpace = this.name.getNamespaceURI();
                if (nameSpace == null) {
                    tempQName = new QName(name);
                } else {
                    tempQName = new QName(nameSpace, name);
                }
            } else {
                tempQName = new QName(name);
            }
            setName(tempQName);
        }

        /**
         * Gets the name space.
         *
         * @return the name space
         */
        public String getNameSpace() {
            return this.getName().getNamespaceURI();
        }

        /**
         * Gets the local name.
         *
         * @return the local name
         */
        public String getLocalName() {
            return this.getName().getLocalPart();
        }

        /**
         * Gets the content.
         *
         * @return the content
         */
        public byte[] getContent() {
            return content.toByteArray();
        }

        /**
         * Returns the content object reference.
         *
         * @return the content
         */
        public MessageContent getContentReference() {
            return content;
        }        
        /**
         * Sets the content.
         *
         * @param content the new content
         */
        protected final void setContent(InputStream content) {
            this.content = MessageContentFactory.create("ntcip2306Message",content);
        }

        /**
         * Gets the name.
         *
         * @return the name
         */
        private QName getName() {
            return name;
        }

        /**
         * Sets the name.
         *
         * @param name the new name
         */
        private void setName(QName name) {
            this.name = name;
        }
    }
}
