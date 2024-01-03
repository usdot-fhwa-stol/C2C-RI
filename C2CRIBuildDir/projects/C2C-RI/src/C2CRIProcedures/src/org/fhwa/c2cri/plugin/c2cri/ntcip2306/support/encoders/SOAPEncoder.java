/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.encoders;

import org.fhwa.c2cri.ntcip2306v109.messaging.MessageSpecificationProcessor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import org.apache.log4j.Logger;
import org.fhwa.c2cri.messagemanager.Message;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author TransCore ITS
 */
public class SOAPEncoder implements NTCIP2306Encoder {
    protected static Logger log = Logger.getLogger(SOAPEncoder.class.getName());
    private String encoderType = "SOAP";
    private boolean isEncoded = false;

    public SOAPEncoder(String encoderType) {
        this.encoderType = encoderType;
    }

    /**
     *
     * @param unEncodedMessage
     * @return
     * @throws Exception
     */
    @Override
    public byte[] encode(byte[] unEncodedMessage) throws Exception {
        isEncoded = true;
        MessageFactory mf11 = MessageFactory.newInstance(SOAPConstants.SOAP_1_1_PROTOCOL);

        String encodedMessage = "";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();


        SOAPElement payload = stringToSOAPElement(new String (unEncodedMessage));
        SOAPMessage sm = mf11.createMessage();
        sm.getSOAPBody().addChildElement(payload);
        sm.writeTo(baos);
        return baos.toByteArray();
    }

    @Override
    public String getEncoderType() {
        return encoderType;
    }

    @Override
    public String getEncodingResults() {
        return "";
    }

    @Override
    public boolean isEncoded() {
        return isEncoded;
    }


    public byte[] encode(Message unEncodedMessage) throws Exception {
        byte[] encodeResult;
        // Check for message spec first
        if (unEncodedMessage.getMessageSpecification().getMessageSpec().size()>0){
           MessageSpecificationProcessor msp = new MessageSpecificationProcessor();
           for (String thisElementSpec : unEncodedMessage.getMessageSpecification().getMessageSpec()){
               msp.updateMessage(thisElementSpec);
           }
           encodeResult = msp.getMessageAsSOAP().getBytes("UTF-8");

        } else {          // Second check for the byte array
            encodeResult = unEncodedMessage.getMessageBody();
        }
        isEncoded = true;
        return encodeResult;
    }

    @Override
    public String getGzipEncodingResults() {
        return "";
    }

    @Override
    public boolean isGzipEncoded() {
        return false;
    }


    public SOAPElement stringToSOAPElement(String xmlText) {
        try {
            // Load the XML text into a DOM Document
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            builderFactory.setNamespaceAware(true);
            InputStream stream  = new ByteArrayInputStream(xmlText.getBytes());
            Document doc = builderFactory.newDocumentBuilder().parse(stream);

            // Use SAAJ to convert Document to SOAPElement
            // Create SoapMessage
            MessageFactory msgFactory = MessageFactory.newInstance();
            SOAPMessage    message    = msgFactory.createMessage();
            SOAPBody       soapBody   = message.getSOAPBody();

            // This returns the SOAPBodyElement
            // that contains ONLY the Payload
            return soapBody.addDocument(doc);

        } catch (SOAPException  e) {
            System.out.println("SOAPException : " + e);
            return null;

        } catch (IOException  e) {
            System.out.println("IOException : " + e);
            return null;

        } catch (ParserConfigurationException  e) {
            System.out.println("ParserConfigurationException : " + e);
            return null;

        } catch (SAXException  e) {
            System.out.println("SAXException : " + e);
            return null;

        }
    }

}
