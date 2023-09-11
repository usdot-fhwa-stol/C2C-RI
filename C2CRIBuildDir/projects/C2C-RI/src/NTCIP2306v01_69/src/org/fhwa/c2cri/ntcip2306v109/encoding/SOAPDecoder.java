/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.encoding;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.ntcip2306v109.status.SOAPStatus;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;


/**
 * The Class SOAPDecoder.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class SOAPDecoder {

    /** The log. */
    protected static Logger log = Logger.getLogger(SOAPDecoder.class.getName());
    
    /** The soap decoded byte arrays. */
    private ArrayList<byte[]> soapDecodedByteArrays = new ArrayList<byte[]>();
    
    /** The C2 cmessagereceipt. */
    private static String C2CMESSAGERECEIPT = "c2cMessageReceipt";
    
    /** The C2 cmessagepublication. */
    private static String C2CMESSAGEPUBLICATION = "c2cMessagePublication";
    
    /** The C2 cmessagesubscription. */
    private static String C2CMESSAGESUBSCRIPTION = "c2cMessageSubscription";
    
    /** The soap decoded message elements. */
    private ArrayList<QName> soapDecodedMessageElements = new ArrayList<QName>();
    
    /** The num message parts. */
    private int numMessageParts;
    
    /** The soap decoder error results. */
    private String soapDecoderErrorResults = "";
    
    /** The soap decoded. */
    private boolean soapDecoded = false;
    
    /** The soap fault msg. */
    private boolean soapFaultMsg = false;
    
    /** The soap decoder status. */
    private SOAPStatus soapDecoderStatus = new SOAPStatus();
    
    /** The valid to selected profile. */
    private boolean validToSelectedProfile = false;
    
    /** The valid sub pub receipt encoding. */
    private boolean validSubPubReceiptEncoding = false;

    /**
     * Checks if is soap encoded.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param encodedMessage the encoded message
     * @return true, if is soap encoded
     */
    public boolean isSoapEncoded(byte[] encodedMessage) {
        soapDecoded = false;

        try {
            MimeHeaders mhs = new MimeHeaders();
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(encodedMessage);
            SOAPMessage sm = MessageFactory.newInstance().createMessage(mhs, byteInputStream);
            int elementCount = 0;

            if (sm.getSOAPHeader() != null) {
                Iterator<SOAPHeaderElement> shElementIterator = sm.getSOAPHeader().examineAllHeaderElements();
                while (shElementIterator.hasNext()) {
                    SOAPHeaderElement thisElement = shElementIterator.next();
                    if (thisElement.getMustUnderstand()) {
                        throw new Exception("Soap Decoding failed because the SOAP Message contained the following mustUnderstand header which is not handled by the C2C RI: "
                                + thisElement.getElementName().getQualifiedName());
                    }
                }
            } else {
                getSoapStatus().addSOAPError("Soap Decoding failed because the SOAP Message is missing the required SOAP Header element.");
                throw new Exception("Soap Decoding failed because the SOAP Message is missing the required SOAP Header element. ");
            }

            if (!sm.getSOAPBody().hasFault()) {
                Iterator<Node> childElementIterator = sm.getSOAPBody().getChildElements();
                while (childElementIterator.hasNext()) {
                    Object thisElement = childElementIterator.next();
                    if (thisElement instanceof SOAPElement){

                    TransformerFactory tf = TransformerFactory.newInstance();
					tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
					tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                    Transformer trans = tf.newTransformer();
                    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                    StringWriter sw = new StringWriter();
                    trans.transform(new DOMSource((SOAPElement)thisElement), new StreamResult(sw));
                    addSoapDecodedByteArrays(sw.toString().getBytes());
                    soapDecodedMessageElements.add(((SOAPElement)thisElement).getElementQName());

                    elementCount++;
                }
                }

            } else {
                setSoapFaultMsg(true);
                SOAPFault sf = sm.getSOAPBody().getFault();
                Iterator<Node> childElementIterator = sf.getDetail().getChildElements();
                while (childElementIterator.hasNext()) {
                    Object thisElement = childElementIterator.next();
                    if (thisElement instanceof SOAPElement){

                    TransformerFactory tf = TransformerFactory.newInstance();
					tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
					tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
                    Transformer trans = tf.newTransformer();
                    trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

                    StringWriter sw = new StringWriter();
                    trans.transform(new DOMSource((SOAPElement)thisElement), new StreamResult(sw));
                    addSoapDecodedByteArrays(sw.toString().getBytes());
                    soapDecodedMessageElements.add(((SOAPElement)thisElement).getElementQName());

                    elementCount++;
                    }
                }


            }
            setNumMessageParts(elementCount);

            byteInputStream.close();

            soapDecoded = true;
            setSoapDecoderErrorResults("");
        } catch (Exception e) {
            e.printStackTrace();
            setSoapDecoderErrorResults(e.getMessage());
        }

        return soapDecoded;
    }

    
    
    /**
 * Gets the message part.
 *
 * @param messagePart the message part
 * @return the message part
 * @throws Exception the exception
 */
public byte[] getMessagePart(int messagePart) throws Exception {

        if ((messagePart < 0) || (messagePart > getSoapDecodedByteArrays().size())) {
            throw new Exception("Message Part number given (" + messagePart + ") exceeds the available message parts available (" + getSoapDecodedByteArrays().size() + "). ");
        }

        return getSoapDecodedByteArrays().get(messagePart - 1);
    }

    /**
     * Gets the message part element name.
     *
     * @param messagePart the message part
     * @return the message part element name
     * @throws Exception the exception
     */
    public QName getMessagePartElementName(int messagePart) throws Exception {

        if ((messagePart < 0) || (messagePart > getSoapDecodedMessageElementNames().size())) {
            throw new Exception("Message Part number given (" + messagePart + ") exceeds the available message parts available (" + getSoapDecodedMessageElementNames().size() + "). ");
        }

        return getSoapDecodedMessageElementNames().get(messagePart - 1);
    }

    /**
     * Gets the num message parts.
     *
     * @return the num message parts
     */
    public int getNumMessageParts() {
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
     * Gets the soap decoded byte arrays.
     *
     * @return the soap decoded byte arrays
     */
    private ArrayList<byte[]> getSoapDecodedByteArrays() {
        return soapDecodedByteArrays;
    }

    /**
     * Sets the soap decoded byte arrays.
     *
     * @param soapDecodedByteArrays the new soap decoded byte arrays
     */
    private void setSoapDecodedByteArrays(ArrayList<byte[]> soapDecodedByteArrays) {
        this.soapDecodedByteArrays = soapDecodedByteArrays;
    }

    /**
     * Adds the soap decoded byte arrays.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param soapDecodedByteArray the soap decoded byte array
     */
    private void addSoapDecodedByteArrays(byte[] soapDecodedByteArray) {
        getSoapDecodedByteArrays().add(soapDecodedByteArray);
    }

    /**
     * Gets the soap decoded message element names.
     *
     * @return the soap decoded message element names
     */
    private ArrayList<QName> getSoapDecodedMessageElementNames() {
        return soapDecodedMessageElements;
    }

    /**
     * Adds the soap decoded message elements.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param messageElementName the message element name
     */
    private void addSoapDecodedMessageElements(QName messageElementName) {
        getSoapDecodedMessageElementNames().add(messageElementName);
    }

    /**
     * Gets the soap decoder error results.
     *
     * @return the soap decoder error results
     */
    public String getSoapDecoderErrorResults() {
        return soapDecoderErrorResults;
    }

    /**
     * Sets the soap decoder error results.
     *
     * @param soapDecoderErrorResults the new soap decoder error results
     */
    private void setSoapDecoderErrorResults(String soapDecoderErrorResults) {
        this.soapDecoderErrorResults = soapDecoderErrorResults;
    }

    /**
     * Sets the soap fault msg.
     *
     * @param soapFaultMsg the new soap fault msg
     */
    private void setSoapFaultMsg(boolean soapFaultMsg) {
        this.soapFaultMsg = soapFaultMsg;
    }

    /**
     * Checks if is soap fault msg.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is soap fault msg
     */
    public boolean isSoapFaultMsg() {
        return soapFaultMsg;
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
                testAssertionList.add(new TestAssertion("4.2.1.a", soapDecoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getSoapDecoderErrorResults()));
                testAssertionList.add(new TestAssertion("4.2.1.c", soapDecoderStatus.isValidAgainstSchemas(), "The <soap:Body> open and close tags encapsulate an XML Message that shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapDecoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                validToSelectedProfile = soapDecoded&soapDecoderStatus.isValidAgainstSchemas();
                break;
            case PUB:
                if (getNumMessageParts() == 1) {
                    testAssertionList.add(new TestAssertion("4.2.2.3.a", soapDecoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getSoapDecoderErrorResults()));
                    boolean receiptResults = false;
                    try {
                        if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGERECEIPT)) {
                            if (serverFlag) {
                                testAssertionList.add(new TestAssertion("4.2.2.3.c", false, "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "SubProfile Selection Error:  An OC must send a 2 part publication message, but a 1 part receipt message was received. \n" + getSoapDecoderErrorResults()));
                            } else {
                                testAssertionList.add(new TestAssertion("4.2.2.3.c", soapDecoderStatus.isValidAgainstSchemas(), "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapDecoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                                receiptResults = soapDecoderStatus.isValidAgainstSchemas();
                            }
                        } else receiptResults = true;

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        testAssertionList.add(new TestAssertion("4.2.2.3.c", false, "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "Test Error Encountered:  " + ex.getMessage() + "\n" + getSoapDecoderErrorResults()));
                    }
                    validSubPubReceiptEncoding = receiptResults;
                    validToSelectedProfile = soapDecoded&receiptResults;


                } else {
                    testAssertionList.add(new TestAssertion("4.2.2.2.a", soapDecoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getSoapDecoderErrorResults()));
                    boolean publicationResults = false;
                    try {
                        if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGEPUBLICATION)) {
                            if (serverFlag) {
                                testAssertionList.add(new TestAssertion("4.2.2.2.c", soapDecoderStatus.isValidAgainstSchemas(), "The <soap:Body> shall contain two child tags:  <c2cMessagePublication>, and one containing the message set standard XML, ... XML Messages shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapDecoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                                publicationResults = soapDecoderStatus.isValidAgainstSchemas();
                            } else {
                                testAssertionList.add(new TestAssertion("4.2.2.2.c", false, "The <soap:Body> shall contain two child tags:  <c2cMessagePublication>, and one containing the message set standard XML, ... XML Messages shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "SubProfile Selection Error:  An EC must send a 1 part receipt message, but a 2 part message was received. \n" + getSoapDecoderErrorResults()));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        testAssertionList.add(new TestAssertion("4.2.2.2.c", false, "The <soap:Body> shall contain two child tags:  <c2cMessagePublication>, and one containing the message set standard XML, ... XML Messages shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "Test Error Encountered:  " + ex.getMessage() + "\n" + getSoapDecoderErrorResults()));
                    }
                    validToSelectedProfile = soapDecoded&publicationResults;
                }

                break;
            case SUB:
                if (getNumMessageParts() == 1) {
                    testAssertionList.add(new TestAssertion("4.2.2.3.a", soapDecoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getSoapDecoderErrorResults()));
                    boolean receiptResults = false;
                    try {
                        if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGERECEIPT)) {
                            if (serverFlag) {
                                testAssertionList.add(new TestAssertion("4.2.2.3.c", false, "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "SubProfile Selection Error:  An OC must send a 2 part publication message, but a 1 part receipt message was received. \n" + getSoapDecoderErrorResults()));
                            } else {
                                testAssertionList.add(new TestAssertion("4.2.2.3.c", soapDecoderStatus.isValidAgainstSchemas(), "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapDecoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                                receiptResults = soapDecoderStatus.isValidAgainstSchemas();
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        testAssertionList.add(new TestAssertion("4.2.2.3.c", false, "The <soap:Body> shall contain a single child tag:  <c2cMessageReceipt>. The c2cMessageReceipt shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "Test Error Encountered:  " + ex.getMessage() + "\n" + getSoapDecoderErrorResults()));
                    }
                    validSubPubReceiptEncoding = receiptResults;
                    validToSelectedProfile = soapDecoded&receiptResults;
                } else {
                    testAssertionList.add(new TestAssertion("4.2.2.1.a", soapDecoded, "The SOAP message shall consist of a <soap:Envelope> tag with two internal tags:  a <soap:Header> tag followed by a <soap:Body> tag. ", getSoapDecoderErrorResults()));
                    boolean subscriptionResults = false;
                    try {
                        if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGESUBSCRIPTION)) {
                            if (serverFlag) {
                                testAssertionList.add(new TestAssertion("4.2.2.1.c", soapDecoderStatus.isValidAgainstSchemas(), "The <soap:Body> shall contain two child tags:  <c2cMessageSubscription>, and one containing the message set standard XML. The <soap:Body> open and close tags encapsulate an XML Message that shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", soapDecoderStatus.isValidAgainstSchemas() ? "" : "The Message did not validate against the Schemas."));
                                subscriptionResults = soapDecoderStatus.isValidAgainstSchemas();
                            } else {
                                testAssertionList.add(new TestAssertion("4.2.2.1.c", false, "The <soap:Body> shall contain two child tags:  <c2cMessageSubscription>, and one containing the message set standard XML. The <soap:Body> open and close tags encapsulate an XML Message that shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "SubProfile Selection Error:  An EC must respond with a 1 part receipt message, but a 2 part message was received. \n" + getSoapDecoderErrorResults()));
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        testAssertionList.add(new TestAssertion("4.2.2.1.c", false, "The <soap:Body> shall contain two child tags:  <c2cMessageSubscription>, and one containing the message set standard XML. The <soap:Body> open and close tags encapsulate an XML Message that shall be capable of being validated using the XML Schema(s) referenced in the WSDL. ", "Test Error Encountered:  " + ex.getMessage() + "\n" + getSoapDecoderErrorResults()));
                    }
                    validToSelectedProfile = soapDecoded&subscriptionResults;
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
        boolean validToSchemasStatus = soapDecoderStatus.isValidAgainstSchemas();
        SOAPStatus soapStatus = new SOAPStatus();
        if (soapDecoded) {
            if (getNumMessageParts() == 1) {
                try {
                    if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGERECEIPT)) {
                        soapStatus.setValidSubPubReceiptEncoding(true);
                    } else if (!((getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGESUBSCRIPTION))
                            && getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGEPUBLICATION))) {
                        soapStatus.setValidRRSOAPEncoding(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else if (getNumMessageParts() == 2) {
                try {
                    if (getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGESUBSCRIPTION)) {
                        soapStatus.setValidSubSOAPEncoding(true);
                    } else if ((getMessagePartElementName(1).getLocalPart().equals(C2CMESSAGEPUBLICATION))) {
                        soapStatus.setValidPubSOAPEncoding(true);
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }

        if (!getSoapDecoderErrorResults().equals(""))soapStatus.addSOAPError(getSoapDecoderErrorResults());
        soapStatus.setValidAgainstSchemas(validToSchemasStatus);
        if (!validToSchemasStatus)soapStatus.addSOAPError("The message was not valid related to the referenced schemas.");
        soapDecoderStatus = soapStatus;
        return soapStatus;
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
            case SUB:
                soapStatus.setValidSubSOAPEncoding(validToSelectedProfile);
                soapStatus.setValidSubPubReceiptEncoding(validSubPubReceiptEncoding);
            case PUB:
                soapStatus.setValidPubSOAPEncoding(validToSelectedProfile);
                soapStatus.setValidSubPubReceiptEncoding(validSubPubReceiptEncoding);
            default:
                break;
        }
        
        return soapStatus;
    }
    
}
