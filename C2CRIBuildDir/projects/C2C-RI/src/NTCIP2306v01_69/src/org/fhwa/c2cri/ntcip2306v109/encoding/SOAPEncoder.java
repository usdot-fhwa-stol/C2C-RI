/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.Detail;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.ntcip2306v109.status.SOAPStatus;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * The Class SOAPEncoder.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class SOAPEncoder {

    /** The log. */
    protected static Logger log = Logger.getLogger(SOAPEncoder.class.getName());
    
    /** The senderfaultcode. */
    public static String SENDERFAULTCODE = "Sender";
    
    /** The receiverfaultcode. */
    public static String RECEIVERFAULTCODE = "Receiver";
    
    /** The mustunderstandfaultcode. */
    public static String MUSTUNDERSTANDFAULTCODE = "mustUnderstand";
    
    /** The versionmismatchfaultcode. */
    public static String VERSIONMISMATCHFAULTCODE = "VersionMismatch";
    
    /** The C2 cmessagereceipt. */
    private static String C2CMESSAGERECEIPT = "c2cMessageReceipt";
    
    /** The C2 cmessagepublication. */
    private static String C2CMESSAGEPUBLICATION = "c2cMessagePublication";
    
    /** The C2 cmessagesubscription. */
    private static String C2CMESSAGESUBSCRIPTION = "c2cMessageSubscription";
    
    /** The encoded message. */
    private byte[] encodedMessage;
    
    /** The encoding error results. */
    private String encodingErrorResults;
    
    /** The soap encoded. */
    private boolean soapEncoded = false;
    
    /** The num message parts. */
    private int numMessageParts = 0;
    
    /** The message part names. */
    private ArrayList<QName> messagePartNames = new ArrayList<QName>();
    
    /** The soap encoder status. */
    private SOAPStatus soapEncoderStatus = new SOAPStatus();
    
    /** The valid to selected profile. */
    private boolean validToSelectedProfile = false;
    
    /** The valid sub pub receipt encoding. */
    private boolean validSubPubReceiptEncoding = false;
    
    /**
     * Encode.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param unEncodedMessages the un encoded messages
     * @return true, if successful
     */
    public boolean encode(ArrayList<byte[]> unEncodedMessages) {
        soapEncoded = false;

        try {
            MessageFactory mf11 = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SOAPMessage sm = mf11.createMessage();
            SOAPBody sb = sm.getSOAPBody();

            for (byte[] unEncodedMessage: unEncodedMessages){
                Document payload = stringToDocument(new String(unEncodedMessage));
                sb.addDocument(payload);
                setNumMessageParts(getNumMessageParts()+1);
                addMessagePartName(sb.getElementQName());
            }
            sm.writeTo(baos);
            setEncodedMessage(baos.toByteArray());
            baos.close();
            soapEncoded = true;
            setEncodingErrorResults("");
        } catch (SOAPException e) {
            e.printStackTrace();
            setEncodingErrorResults("SOAPException : " + e.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
            setEncodingErrorResults("IOException : " + e.getMessage());

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            setEncodingErrorResults("ParserConfigurationException : " + e.getMessage());

        } catch (SAXException e) {
            e.printStackTrace();
            setEncodingErrorResults("SAXException : " + e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            setEncodingErrorResults(e.getMessage());
        }
        return soapEncoded;
    }
    
    /**
     * Encode as fault.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param unEncodedMessages the un encoded messages
     * @param faultCode the fault code
     * @param faultString the fault string
     * @return true, if successful
     */
    public boolean encodeAsFault(ArrayList<byte[]> unEncodedMessages, String faultCode, String faultString) {
        soapEncoded = false;

        try {
            MessageFactory mf11 = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            SOAPMessage sm = mf11.createMessage();
            SOAPBody sb = sm.getSOAPBody();
            SOAPFault sf = sb.addFault();
            Name faultCodeName = sf.getFaultCodeAsName();
            QName newName = sf.createQName(faultCode, faultCodeName.getPrefix());
            sf.setFaultCode(newName);
            sf.setFaultString(faultString);
            Detail msgDetail;
            if (sf.getDetail() != null){
                msgDetail = sf.getDetail();
            } else {
                msgDetail = sf.addDetail();
            }
            msgDetail.removeContents();
            String payloadNameSpaceURI[] =new String[unEncodedMessages.size()];
            String elementURI[] = new String[unEncodedMessages.size()];
            int ii = 0;
            
            for (byte[] unEncodedMessage: unEncodedMessages){
                SOAPElement payload = stringToSOAPElement(new String(unEncodedMessage));
                payloadNameSpaceURI[ii] = payload.getNamespaceURI();
                elementURI[ii] = payload.getElementQName().getNamespaceURI();
                msgDetail.addChildElement(payload);
                setNumMessageParts(getNumMessageParts()+1);
                addMessagePartName(sb.getElementQName());
                ii++;
            }
            sm.writeTo(baos);
            
            // With Java 11 the SOAP Message adds xmlns="" or an unnecessary xmlns="[namespace]" to elements in the Detail element.
            // Clear these out to be consistent with prior versions of the C2C RI.
            // Remove this feature if a way is found to adjust the serialization being performed by SAAJ.
            String tmpString = unEncodedMessages.size()==1?new String(baos.toByteArray()).replace(" xmlns=\""+payloadNameSpaceURI[0]+"\"","").replace(" xmlns=\"\"",""):new String(baos.toByteArray()).replace(" xmlns=\"\"","");
            setEncodedMessage(tmpString.getBytes());
            baos.close();
            soapEncoded = true;
            setEncodingErrorResults("");
        } catch (SOAPException e) {
            e.printStackTrace();
            setEncodingErrorResults("SOAPException : " + e.getMessage());

        } catch (IOException e) {
            e.printStackTrace();
            setEncodingErrorResults("IOException : " + e.getMessage());

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            setEncodingErrorResults("ParserConfigurationException : " + e.getMessage());

        } catch (SAXException e) {
            e.printStackTrace();
            setEncodingErrorResults("SAXException : " + e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            setEncodingErrorResults(e.getMessage());
        }
        return soapEncoded;
    }    

    /**
     * String to soap element.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlText the xml text
     * @return the sOAP element
     * @throws Exception the exception
     */
    private SOAPElement stringToSOAPElement(String xmlText) throws Exception {
        // Load the XML text into a DOM Document
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        InputStream stream = new ByteArrayInputStream(xmlText.getBytes());
        Document doc = builderFactory.newDocumentBuilder().parse(stream);
        stream.close();
        // Use SAAJ to convert Document to SOAPElement
        // Create SoapMessage
        MessageFactory msgFactory = MessageFactory.newInstance();
        SOAPMessage message = msgFactory.createMessage();
        SOAPBody soapBody = message.getSOAPBody();

        // This returns the SOAPBodyElement
        // that contains ONLY the Payload
        return soapBody.addDocument(doc);

    }

    
    private Document stringToDocument(String xmlText) throws Exception {
        // Load the XML text into a DOM Document
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);
        InputStream stream = new ByteArrayInputStream(xmlText.getBytes());
        Document doc = builderFactory.newDocumentBuilder().parse(stream);
        stream.close();

        return doc;
    }
    
    /**
     * Adds the message part name.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param messagePartName the message part name
     */
    private void addMessagePartName(QName messagePartName){
        getMessagePartNames().add(messagePartName);
    }

    /**
     * Gets the message part names.
     *
     * @return the message part names
     */
    private ArrayList<QName> getMessagePartNames() {
        return messagePartNames;
    }



     /**
      * Gets the message part element name.
      *
      * @param messagePart the message part
      * @return the message part element name
      * @throws Exception the exception
      */
     public QName getMessagePartElementName(int messagePart) throws Exception {

        if ((messagePart < 0) || (messagePart > getSoapEncodedMessageElementNames().size())) {
            throw new Exception("Message Part number given (" + messagePart + ") exceeds the available message parts available (" + getSoapEncodedMessageElementNames().size() + "). ");
        }

        return getSoapEncodedMessageElementNames().get(messagePart - 1);
    }

    /**
     * Gets the soap encoded message element names.
     *
     * @return the soap encoded message element names
     */
    private ArrayList<QName> getSoapEncodedMessageElementNames() {
        return messagePartNames;
    }


    /**
     * Gets the encoded message.
     *
     * @return the encoded message
     */
    public byte[] getEncodedMessage() {
        return encodedMessage;
    }

    /**
     * Sets the encoded message.
     *
     * @param encodedMessage the new encoded message
     */
    private void setEncodedMessage(byte[] encodedMessage) {
        this.encodedMessage = encodedMessage;
    }

    /**
     * Gets the encoding error results.
     *
     * @return the encoding error results
     */
    public String getEncodingErrorResults() {
        return encodingErrorResults;
    }

    /**
     * Sets the encoding error results.
     *
     * @param encodingErrorResults the new encoding error results
     */
    private void setEncodingErrorResults(String encodingErrorResults) {
        this.encodingErrorResults = encodingErrorResults;
    }

    /**
     * Gets the num message parts.
     *
     * @return the num message parts
     */
    private int getNumMessageParts() {
        return numMessageParts;
    }

    /**
     * Sets the num message parts.
     *
     * @param numMessageParts the new num message parts
     */
    private void setNumMessageParts(int numMessageParts) {
        this.numMessageParts = numMessageParts;
    }

    /**
     * Gets the test assertion list.
     *
     * @param subProfile the sub profile
     * @param serverFlag the server flag
     * @return the test assertion list
     */
    public ArrayList<TestAssertion> getTestAssertionList(OperationSpecification.ProfileType subProfile, boolean serverFlag) {
        ArrayList<TestAssertion> testAssertionList = new ArrayList<>();

        switch (subProfile) {
            case RR:
                testAssertionList.add(new TestAssertion("4.2.1.a", soapEncoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getEncodingErrorResults()));
                testAssertionList.add(new TestAssertion("4.2.1.c", soapEncoderStatus.isValidAgainstSchemas(), "The <soap:Body> open and close tags encapsulate an XML Message that shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapEncoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                validToSelectedProfile = soapEncoded && soapEncoderStatus.isValidAgainstSchemas();
                break;
            case PUB:
                if (getNumMessageParts() == 1) {
                    testAssertionList.add(new TestAssertion("4.2.2.3.a", soapEncoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getEncodingErrorResults()));

                    boolean receiptResults = false;
                    try {
                        if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGERECEIPT)) {
                            if (serverFlag) {
                                testAssertionList.add(new TestAssertion("4.2.2.3.c", false, "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "SubProfile Selection Error:  An OC must send a 2 part publication message, but a 1 part receipt message was received. \n" + getEncodingErrorResults()));
                            } else {
                                testAssertionList.add(new TestAssertion("4.2.2.3.c", soapEncoderStatus.isValidAgainstSchemas(), "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapEncoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                                receiptResults = soapEncoderStatus.isValidAgainstSchemas();
                            }
                        } else receiptResults = true;

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        testAssertionList.add(new TestAssertion("4.2.2.3.c", false, "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "Test Error Encountered:  " + ex.getMessage() + "\n" + getEncodingErrorResults()));
                    }
                    validSubPubReceiptEncoding = receiptResults;
                    validToSelectedProfile = soapEncoded && receiptResults;


                } else {
                    testAssertionList.add(new TestAssertion("4.2.2.2.a", soapEncoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getEncodingErrorResults()));

                    boolean publicationResults = false;
                    try {
                        if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGEPUBLICATION)) {
                            if (serverFlag) {
                                testAssertionList.add(new TestAssertion("4.2.2.2.c", soapEncoderStatus.isValidAgainstSchemas(), "The <soap:Body> shall contain two child tags:  <c2cMessagePublication>, and one containing the message set standard XML, ... XML Messages shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapEncoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                                publicationResults = soapEncoderStatus.isValidAgainstSchemas();
                            } else {
                                testAssertionList.add(new TestAssertion("4.2.2.2.c", false, "The <soap:Body> shall contain two child tags:  <c2cMessagePublication>, and one containing the message set standard XML, ... XML Messages shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "SubProfile Selection Error:  An EC must send a 1 part receipt message, but a 2 part message was received. \n" + getEncodingErrorResults()));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        testAssertionList.add(new TestAssertion("4.2.2.2.c", false, "The <soap:Body> shall contain two child tags:  <c2cMessagePublication>, and one containing the message set standard XML, ... XML Messages shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "Test Error Encountered:  " + ex.getMessage() + "\n" + getEncodingErrorResults()));
                    }
                    validToSelectedProfile = soapEncoded && publicationResults;
                }

                break;
            case SUB:
                if (getNumMessageParts() == 1) {
                    testAssertionList.add(new TestAssertion("4.2.2.3.a", soapEncoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getEncodingErrorResults()));
                    boolean receiptResults = false;
                    try {
                        if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGERECEIPT)) {
                            if (serverFlag) {
                                testAssertionList.add(new TestAssertion("4.2.2.3.c", false, "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "SubProfile Selection Error:  An OC must send a 2 part publication message, but a 1 part receipt message was received. \n" + getEncodingErrorResults()));
                            } else {
                                testAssertionList.add(new TestAssertion("4.2.2.3.c", soapEncoderStatus.isValidAgainstSchemas(), "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapEncoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                                receiptResults = soapEncoderStatus.isValidAgainstSchemas();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        testAssertionList.add(new TestAssertion("4.2.2.3.c", false, "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "Test Error Encountered:  " + ex.getMessage() + "\n" + getEncodingErrorResults()));
                    }
                    validSubPubReceiptEncoding = receiptResults;
                    validToSelectedProfile = soapEncoded && receiptResults;
                } else {
                    testAssertionList.add(new TestAssertion("4.2.2.1.a", soapEncoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getEncodingErrorResults()));

                    boolean subscriptionResults = false;
                    try {
                        if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGESUBSCRIPTION)) {
                            if (serverFlag) {
                                testAssertionList.add(new TestAssertion("4.2.2.1.c", soapEncoderStatus.isValidAgainstSchemas(), "The <soap:Body> shall contain two child tags:  <c2cMessageSubscription>, and one containing the message set standard XML. The <soap:Body> open and close tags encapsulate an XML Message that shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapEncoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                                subscriptionResults = soapEncoderStatus.isValidAgainstSchemas();
                            } else {
                                testAssertionList.add(new TestAssertion("4.2.2.1.c", false, "The <soap:Body> shall contain two child tags:  <c2cMessageSubscription>, and one containing the message set standard XML. The <soap:Body> open and close tags encapsulate an XML Message that shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "SubProfile Selection Error:  An EC must respond with a 1 part receipt message, but a 2 part message was received. \n" + getEncodingErrorResults()));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        testAssertionList.add(new TestAssertion("4.2.2.1.c", false, "The <soap:Body> shall contain two child tags:  <c2cMessageSubscription>, and one containing the message set standard XML. The <soap:Body> open and close tags encapsulate an XML Message that shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "Test Error Encountered:  " + ex.getMessage() + "\n" + getEncodingErrorResults()));
                    }
                    validToSelectedProfile = soapEncoded && subscriptionResults;
                }
                break;
            default:
                break;
        }

        return testAssertionList;
    }

    /**
     * Gets the soap status.
     *
     * @return the soap status
     */
    public SOAPStatus getSoapStatus() {
        boolean validToSchemasStatus = soapEncoderStatus.isValidAgainstSchemas();
        soapEncoderStatus = new SOAPStatus();
        if (soapEncoded) {
            if (getNumMessageParts() == 1) {
                try {
                    if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGERECEIPT)) {
                        soapEncoderStatus.setValidSubPubReceiptEncoding(true);
                    } else if (!((getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGESUBSCRIPTION))
                            && getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGEPUBLICATION))) {
                        soapEncoderStatus.setValidRRSOAPEncoding(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else if (getNumMessageParts() == 2) {
                try {
                    if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGESUBSCRIPTION)) {
                        soapEncoderStatus.setValidSubSOAPEncoding(true);
                    } else if ((getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGEPUBLICATION))) {
                        soapEncoderStatus.setValidPubSOAPEncoding(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }

        if (!getEncodingErrorResults().isEmpty())soapEncoderStatus.addSOAPError(getEncodingErrorResults());
        soapEncoderStatus.setValidAgainstSchemas(validToSchemasStatus);
        if (!validToSchemasStatus)soapEncoderStatus.addSOAPError("The message was not valid related to the referenced schemas.");
        
        return soapEncoderStatus;
    }

    /**
     * Gets the soap status.
     *
     * @param subProfile the sub profile
     * @param serverFlag the server flag
     * @return the soap status
     */
    public SOAPStatus getSoapStatus(OperationSpecification.ProfileType subProfile, boolean serverFlag) {
        SOAPStatus soapStatus = getSoapStatus();
        soapStatus.setTestAssertionList(getTestAssertionList(subProfile, serverFlag));     
        switch (subProfile) {
            case RR:
                soapStatus.setValidRRSOAPEncoding(validToSelectedProfile);
				break;
            case SUB:
                soapStatus.setValidSubSOAPEncoding(validToSelectedProfile);
                soapStatus.setValidSubPubReceiptEncoding(validSubPubReceiptEncoding);
				break;
            case PUB:
                soapStatus.setValidPubSOAPEncoding(validToSelectedProfile);
                soapStatus.setValidSubPubReceiptEncoding(validSubPubReceiptEncoding);
				break;
            default:
                break;
        }
        
        return soapStatus;
    }
    
}
