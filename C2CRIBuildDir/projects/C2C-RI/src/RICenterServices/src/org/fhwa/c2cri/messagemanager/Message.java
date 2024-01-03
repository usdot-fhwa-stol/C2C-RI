/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.messagemanager;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.fhwa.c2cri.infolayer.MessageSpecification;

/**
 * The Class Message represents a message that is exchanged between centers.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class Message {

    /**
     * The Enum MESSAGETYPE.
     */
    public static enum MESSAGETYPE {/** The Request. */
Request,/** The Response. */
Response}
    
    /** The request message type. */
    public static String REQUEST_MESSAGE_TYPE = "Request";
    
    /** The response message type. */
    public static String RESPONSE_MESSAGE_TYPE = "Response";
    
    /** The xml message encoding. */
    public static String XML_MESSAGE_ENCODING = "XML";
    
    /** The gzipped xml message encoding. */
    public static String GZIPPED_XML_MESSAGE_ENCODING = "GZipXML";
    
    /** The soap xml message encoding. */
    public static String SOAP_XML_MESSAGE_ENCODING = "SOAP_XML";
    
    /** The AS n1_ messag e_ encoding. */
    public static String ASN1_MESSAGE_ENCODING = "ASN1";
    
    /** The ftp transport protocol. */
    public static String FTP_TRANSPORT_PROTOCOL = "FTP";
    
    /** The http transport protocol. */
    public static String HTTP_TRANSPORT_PROTOCOL = "HTTP";
    
    /** The https transport protocol. */
    public static String HTTPS_TRANSPORT_PROTOCOL = "HTTPS";
    
    /** The message type. */
    private volatile String messageType="";
    
    /** The message name. */
    private volatile String messageName="";
    
    /** The http headers. */
    private HashMap<String, String> httpHeaders;
    
    /** The soap headers. */
    private HashMap<String, String> soapHeaders;
    
    /** The message body. */
    private MessageContent messageBody;
    
    /** The message body parts. */
    private ArrayList<MessageContent> messageBodyParts = new ArrayList<>();
    
    /** The message specification. */
    private MessageSpecification messageSpecification;
    
    /** The message encoding. */
    private volatile String messageEncoding="";
    
    /** The message source address. */
    private volatile String messageSourceAddress="";
    
    /** The message destination address. */
    private volatile String messageDestinationAddress="";
    
    /** The via transport protocol. */
    private volatile String viaTransportProtocol="";
    
    /** The related command. */
    private volatile String relatedCommand="";  //GET (RETRV), POST
    
    /** The storage time. */
    private Date storageTime;
    
    /** The parent dialog. */
    private volatile String parentDialog="";
    
    /** The transport time in millis. */
    private volatile long transportTimeInMillis = -1;
    
    /** The enumerated message type. */
    private volatile MESSAGETYPE enumeratedMessageType;
    
    /** The skip application layer encoding. */
    private volatile boolean skipApplicationLayerEncoding;
    
    /** The encoded message type. */
    private volatile String encodedMessageType = "Unknown";
    
    /**
     * Disable the default constructor.
     */
    private Message() {
    }

    /**
     * This object should be created by the MessageManager.
     *
     * @param parentDialog the parent dialog
     */
    protected Message(String parentDialog) {
        this.parentDialog = parentDialog;
    }

    /**
     * This object should be created by the MessageManager.
     *
     * @param parentDialog the parent dialog
     * @param messageType the message type
     * @param messageName the message name
     * @param httpHeaders the http headers
     * @param soapHeaders the soap headers
     * @param messageBody the message body
     * @param messageEncoding the message encoding
     * @param messageSourceAddress the message source address
     * @param messageDestinationAddress the message destination address
     * @param viaTransportProtocol the via transport protocol
     * @param relatedCommand the related command
     * @param storageTime the storage time
     */
    protected Message(String parentDialog,
            String messageType,
            String messageName,
            HashMap httpHeaders,
            HashMap soapHeaders,
            byte[] messageBody,
            String messageEncoding,
            String messageSourceAddress,
            String messageDestinationAddress,
            String viaTransportProtocol,
            String relatedCommand,
            Date storageTime) {

        // Handle the messageType field
        if (messageType.equalsIgnoreCase(this.REQUEST_MESSAGE_TYPE)) {
            this.messageType = messageType;
            this.enumeratedMessageType = MESSAGETYPE.Request;
        } else {
            this.messageType = this.RESPONSE_MESSAGE_TYPE;
            this.enumeratedMessageType = MESSAGETYPE.Response;
        }

        this.parentDialog = parentDialog;

        this.messageName = messageName;

        this.viaTransportProtocol = viaTransportProtocol;

        this.httpHeaders = httpHeaders;
        ByteArrayInputStream bais = new ByteArrayInputStream(messageBody);
        this.messageBody = MessageContentFactory.create(parentDialog,bais);
        this.messageEncoding = messageEncoding;
        this.messageSourceAddress = messageSourceAddress;
        this.messageDestinationAddress = messageDestinationAddress;
        this.relatedCommand = relatedCommand;
        this.storageTime = storageTime;
    }

    /**
     * To xml.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the string
     */
    public String toXML() {
        String messageXMLRepresentation = "<loggedMessage>\n";
        messageXMLRepresentation = messageXMLRepresentation.concat("   <parentDialog>" + parentDialog + "</parentDialog>\n");
        messageXMLRepresentation = messageXMLRepresentation.concat("   <messageName>" + messageName + "</messageName>\n");
        messageXMLRepresentation = messageXMLRepresentation.concat("   <messageType>" + encodedMessageType + "</messageType>\n");
        messageXMLRepresentation = messageXMLRepresentation.concat("   <messageEncoding>" + messageEncoding + "</messageEncoding>\n");
        messageXMLRepresentation = messageXMLRepresentation.concat("   <viaTransportProtocol>" + viaTransportProtocol + "</viaTransportProtocol>\n");
        messageXMLRepresentation = messageXMLRepresentation.concat("   <messageSourceAddress>" + messageSourceAddress + "</messageSourceAddress>\n");
        messageXMLRepresentation = messageXMLRepresentation.concat("   <messageDestinationAddress>" + messageDestinationAddress + "</messageDestinationAddress>\n");
        messageXMLRepresentation = messageXMLRepresentation.concat("   <relatedCommand>" + this.relatedCommand + "</relatedCommand>\n");
        if ((httpHeaders != null) && (!httpHeaders.isEmpty())) {
            messageXMLRepresentation = messageXMLRepresentation.concat("   <httpHeaders>");
            Iterator iterator = httpHeaders.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                messageXMLRepresentation =
                        messageXMLRepresentation.concat("<httpHeader name=" + key + ">" + httpHeaders.get(key) + "</httpHeader>\n");
            }
            messageXMLRepresentation = messageXMLRepresentation.concat("</httpHeaders>\n");
        }

        if ((soapHeaders != null) && (!soapHeaders.isEmpty())) {
            messageXMLRepresentation = messageXMLRepresentation.concat("   <soapHeaders>");
            Iterator iterator = soapHeaders.keySet().iterator();
            while (iterator.hasNext()) {
                String key = (String) iterator.next();
                messageXMLRepresentation =
                        messageXMLRepresentation.concat("<soapHeader name=" + key + ">" + soapHeaders.get(key) + "</soapHeader>\n");
            }
            messageXMLRepresentation = messageXMLRepresentation.concat("</soapHeaders>\n");
        }

        if (this.messageEncoding != null) { // This value must have been set
            try {
                String retVal = "";
				MessageSpecification oMS = getMessageSpecification();
                if (oMS == null || oMS.getMessageSpec().isEmpty()){
                    retVal =  stripInvalidCharacters(getMessageBody());
           //         retVal = new String(this.messageBody.toByteArray(),"UTF-8");
                    messageXMLRepresentation = messageXMLRepresentation.concat("   <MessageBody>\n<![CDATA[\n" + prettyPrint(retVal) + "]]>   </MessageBody>\n");
                } else {
                   retVal ="";
                   for (String messageElement : oMS.getMessageSpec()){
                       retVal = retVal.concat(messageElement.replaceAll("[^\\x20-\\x7e]", " ")+"\n");
                   }
                   messageXMLRepresentation = messageXMLRepresentation.concat("   <MessageBody>\n<![CDATA[\n" + retVal + "]]>   </MessageBody>\n");   
                }

            } catch (Exception ex) {
                messageXMLRepresentation = messageXMLRepresentation.concat("   <MessageBody>\n<![CDATA[\n" + "***DECODING ERROR *****" + ex.getMessage()+"]]>   </MessageBody>\n");
            }

        } else {
            messageXMLRepresentation = messageXMLRepresentation.concat("   <MessageBody>\n<![CDATA[\n" + "COULD NOT DETERMINE THE ENCODER/DECODER.  IT WAS NOT SET." + "]]>   </MessageBody>\n");
        }
        messageXMLRepresentation = messageXMLRepresentation.concat("</loggedMessage>\n");
        return messageXMLRepresentation;
    }

    /**
     * Pretty print.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param xmlInput the xml input
     * @return the string
     */
    private String prettyPrint(String xmlInput){
        String result = xmlInput;
        System.err.println("Before Transformation:\n\n"+xmlInput+"\n\n");
        try{
            Source xmlInputSource = new StreamSource(new StringReader(xmlInput));
            StringWriter stringwriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringwriter);
            TransformerFactory transformerFactory;
            try{
                transformerFactory = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl",TransformerFactory.class.getClassLoader());
            } catch (Exception ex){
                ex.printStackTrace();
                transformerFactory = TransformerFactory.newInstance();
            }
//            transformerFactory.setAttribute("indent-number", 2);
            Transformer transformer = transformerFactory.newTransformer();
            System.err.println("\n\nTranFormer Class URL = "+transformer.getClass().getProtectionDomain().getCodeSource().getLocation().toString());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); 
            transformer.transform(xmlInputSource,xmlOutput);
            result = xmlOutput.getWriter().toString();
        System.err.println("After Transformation:\n\n"+xmlInput+"\n\n");
            
            return result;
//            return xmlOutput.getWriter().toString();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
        return result;
    }

    /**
     * Strip invalid characters.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     * @return the string
     */
    private String stripInvalidCharacters(byte[] message){
        try {
            byte[] bytes = message;
        byte[] invalidByte = new byte[1];
            invalidByte[0]=0x00;
            String invalidString = new String(invalidByte,"UTF-8");
            invalidByte[0]=0x1f;
            String invalidString2 = new String(invalidByte,"UTF-8");
            invalidByte[0]=0x8;
            String invalidString3 = new String(invalidByte,"UTF-8");
            invalidByte[0]=0x18;
            String invalidString4 = new String(invalidByte,"UTF-8");

            for (int ii = 0; ii<message.length; ii++){
                if (bytes[ii]<0x20 || bytes[ii]>0x7f) bytes[ii]=0x20;
            }
            return new String(bytes, "UTF-8").replaceAll(invalidString, "").replaceAll(invalidString2, "").replaceAll(invalidString3, "").replaceAll(invalidString4, "").replaceAll("[^\\x20-\\x7e]", "");
        } catch (Exception ex){
            return("ERROR Processing Message Body: "+ex.getMessage());
        }
    }

// Getter and Setter Methods
    /**
 * Gets the http headers.
 *
 * @return the http headers
 */
public HashMap<String, String> getHttpHeaders() {
        return httpHeaders;
    }

    /**
     * Sets the http headers.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param httpHeaders the http headers
     */
    public void setHttpHeaders(HashMap<String, String> httpHeaders) {
        this.httpHeaders = httpHeaders;
    }

    /**
     * Gets the message body.
     *
     * @return the message body
     */
    public synchronized byte[] getMessageBody() {
        if (messageBody == null){
            ByteArrayInputStream bais = new ByteArrayInputStream("".getBytes());
            this.messageBody = MessageContentFactory.create("",bais);            
        }
        return messageBody.toByteArray();
    }

    /**
     * Sets the message body.
     *
     * @param messageBody the new message body
     */
    public synchronized void setMessageBody(byte[] messageBody) {
        ByteArrayInputStream bais = new ByteArrayInputStream(messageBody);
        this.messageBody = MessageContentFactory.create("",bais);
    }

    /**
     * Gets the message destination address.
     *
     * @return the message destination address
     */
    public String getMessageDestinationAddress() {
        return messageDestinationAddress;
    }

    /**
     * Sets the message destination address.
     *
     * @param messageDestinationAddress the new message destination address
     */
    public void setMessageDestinationAddress(String messageDestinationAddress) {
        this.messageDestinationAddress = messageDestinationAddress;
    }

    /**
     * Gets the message encoding.
     *
     * @return the message encoding
     */
    public String getMessageEncoding() {
        return messageEncoding;
    }

    /**
     * Sets the message encoding.
     *
     * @param messageEncoding the new message encoding
     */
    public void setMessageEncoding(String messageEncoding) {
        this.messageEncoding = messageEncoding;
    }

    /**
     * Gets the message name.
     *
     * @return the message name
     */
    public String getMessageName() {
        return messageName;
    }

    /**
     * Sets the message name.
     *
     * @param messageName the new message name
     */
    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    /**
     * Gets the message source address.
     *
     * @return the message source address
     */
    public String getMessageSourceAddress() {
        return messageSourceAddress;
    }

    /**
     * Sets the message source address.
     *
     * @param messageSourceAddress the new message source address
     */
    public void setMessageSourceAddress(String messageSourceAddress) {
        this.messageSourceAddress = messageSourceAddress;
    }

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    public String getMessageType() {
        return messageType;
    }

    /**
     * Sets the message type.
     *
     * @param messageType the new message type
     */
    public void setMessageType(String messageType) {
        this.messageType = messageType;
        if (messageType.equalsIgnoreCase("Request")){
            this.enumeratedMessageType = MESSAGETYPE.Request;
        } else if (messageType.equalsIgnoreCase("Response")){
            this.enumeratedMessageType = MESSAGETYPE.Response;
        }
    }

    /**
     * Gets the enumerated message type.
     *
     * @return the enumerated message type
     */
    public MESSAGETYPE getEnumeratedMessageType() {
        return enumeratedMessageType;
    }

    /**
     * Sets the enumerated message type.
     *
     * @param enumeratedMessageType the new enumerated message type
     */
    public void setEnumeratedMessageType(MESSAGETYPE enumeratedMessageType) {
        this.enumeratedMessageType = enumeratedMessageType;
        this.messageType = enumeratedMessageType.name();
    }


    /**
     * Gets the related command.
     *
     * @return the related command
     */
    public String getRelatedCommand() {
        return relatedCommand;
    }

    /**
     * Sets the related command.
     *
     * @param relatedCommand the new related command
     */
    public void setRelatedCommand(String relatedCommand) {
        this.relatedCommand = relatedCommand;
    }

    /**
     * Gets the storage time.
     *
     * @return the storage time
     */
    public synchronized Date getStorageTime() {
        return storageTime;
    }

    /**
     * Sets the storage time.
     *
     * @param storageTime the new storage time
     */
    public synchronized void setStorageTime(Date storageTime) {
        this.storageTime = storageTime;
    }

    /**
     * Gets the via transport protocol.
     *
     * @return the via transport protocol
     */
    public String getViaTransportProtocol() {
        return viaTransportProtocol;
    }

    /**
     * Sets the via transport protocol.
     *
     * @param viaTransportProtocol the new via transport protocol
     */
    public void setViaTransportProtocol(String viaTransportProtocol) {
        this.viaTransportProtocol = viaTransportProtocol;
    }

    /**
     * Gets the parent dialog.
     *
     * @return the parent dialog
     */
    public String getParentDialog() {
        return parentDialog;
    }

    /**
     * Sets the parent dialog.
     *
     * @param parentDialog the new parent dialog
     */
    public void setParentDialog(String parentDialog) {
        this.parentDialog = parentDialog;
    }

    /**
     * Gets the soap headers.
     *
     * @return the soap headers
     */
    public HashMap<String, String> getSoapHeaders() {
        return soapHeaders;
    }

    /**
     * Sets the soap headers.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param soapHeaders the soap headers
     */
    public void setSoapHeaders(HashMap<String, String> soapHeaders) {
        this.soapHeaders = soapHeaders;
    }

    /**
     * Gets the message specification.
     *
     * @return the message specification
     */
    public synchronized MessageSpecification getMessageSpecification() {
        return messageSpecification;
    }

    /**
     * Sets the message specification.
     *
     * @param messageSpecification the new message specification
     */
    public synchronized void setMessageSpecification(MessageSpecification messageSpecification) {
        this.messageSpecification = messageSpecification;
    }

    /**
     * Gets the transport time in millis.
     *
     * @return the transport time in millis
     */
    public long getTransportTimeInMillis() {
        return transportTimeInMillis;
    }

    /**
     * Sets the transport time in millis.
     *
     * @param transportTimeInMillis the new transport time in millis
     */
    public void setTransportTimeInMillis(long transportTimeInMillis) {
        this.transportTimeInMillis = transportTimeInMillis;
    }

    /**
     * Gets the message body parts.
     *
     * @return the message body parts
     */
    public synchronized ArrayList<byte[]> getMessageBodyParts() {
        ArrayList<byte[]> tmpList = new ArrayList();
        for (MessageContent tmpDatabase : messageBodyParts){
            tmpList.add(tmpDatabase.toByteArray());
        }
        return tmpList;
    }

    /**
     * Gets the message body parts by reference.
     *
     * @return the message body parts
     */
    public synchronized ArrayList<MessageContent> getMessageBodyPartsReferences() {
        return messageBodyParts;
    }    
    /**
     * Sets the message body parts.
     *
     * @param messageBodyParts the new message body parts
     */
    public synchronized void setMessageBodyParts(ArrayList<byte[]> messageBodyParts) {
        ArrayList<MessageContent> tmpList = new ArrayList();
        for (byte[] thisArray : messageBodyParts){
            ByteArrayInputStream bais = new ByteArrayInputStream(thisArray);
            tmpList.add(MessageContentFactory.create("",bais));
        }
        this.messageBodyParts = tmpList;
    }

    /**
     * Sets the message body parts.
     *
     * @param messageBodyParts the new message body parts
     */
    public synchronized void setMessageBodyPartsFromContent(ArrayList<MessageContent> messageBodyParts) {
        this.messageBodyParts = messageBodyParts;
    }    
    /**
     * Checks if is skip application layer encoding.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is skip application layer encoding
     */
    public boolean isSkipApplicationLayerEncoding() {
        return skipApplicationLayerEncoding;
    }

    /**
     * Sets the skip application layer encoding.
     *
     * @param skipApplicationLayerEncoding the new skip application layer encoding
     */
    public void setSkipApplicationLayerEncoding(boolean skipApplicationLayerEncoding) {
        this.skipApplicationLayerEncoding = skipApplicationLayerEncoding;
    }

    /**
     * Gets the encoded message type.
     *
     * @return the encoded message type
     */
    public String getEncodedMessageType() {
        return encodedMessageType;
    }

    /**
     * Sets the encoded message type.
     *
     * @param encodedMessageType the new encoded message type
     */
    public void setEncodedMessageType(String encodedMessageType) {
        this.encodedMessageType = encodedMessageType;
        StringBuffer stackTrace = new StringBuffer();
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()){
            stackTrace.append(ste.toString()+"\n");
        }
      
        System.out.println("SetEncodedMessageType call with Message Type = "+encodedMessageType+" + by:\n" +stackTrace);
    }


}
