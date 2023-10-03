/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.messaging;

import java.util.ArrayList;
import java.util.HashMap;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.messagemanager.MessageContent;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.messaging.NTCIP2306Message.PROTOCOLTYPE;
import org.fhwa.c2cri.ntcip2306v109.encoding.SOAPDecoder;


/**
 * The Class C2CRIMessageAdapter.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class C2CRIMessageAdapter {

    /**
     * To c2crimessage creates a Message from the input.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param inputMessage the input message
     * @return the message
     */
    public static Message toC2CRIMessage(String operation, NTCIP2306Message inputMessage) {
        MessageManager manager = MessageManager.getInstance();

        Message newMessage = manager.createMessage(operation);

        String primaryMessageName = "Unknown";
        int totalByteLength = 0;
        ArrayList<MessageContent> msgList = new ArrayList();
        if (inputMessage != null) {
            for (int ii = 0; ii < inputMessage.getNumberMessageParts(); ii++) {
                msgList.add(inputMessage.getMessagePartContentReference(ii + 1));
                primaryMessageName = inputMessage.getMessagePartName(ii + 1);
                totalByteLength = totalByteLength + inputMessage.getMessagePartContent(ii + 1).length;
            }

            int previousLength = 0;
            byte[] result = new byte[totalByteLength];
            for (int ii = 0; ii < inputMessage.getNumberMessageParts(); ii++) {
                System.arraycopy(inputMessage.getMessagePartContent(ii + 1), 0, result, previousLength, inputMessage.getMessagePartContent(ii + 1).length);
                previousLength = previousLength + inputMessage.getMessagePartContent(ii + 1).length;
            }
            newMessage.setMessageBody(result);

            String sourceAddress;
            String destinationAddress;

            if (inputMessage.getProtocolType().equals(PROTOCOLTYPE.FTP)) {
                sourceAddress = "FTP does not set address yet";
                destinationAddress = "FTP does not set address yet";
            } else {
                sourceAddress = inputMessage.getHttpStatus().getSource();
                destinationAddress = inputMessage.getHttpStatus().getDestination();
            }

            MessageSpecificationProcessor msp;
            if (MessageManager.getInstance().getNameSpaceMap()!= null){
                msp = new MessageSpecificationProcessor(MessageManager.getInstance().getNameSpaceMap());
            } else {
                msp = new MessageSpecificationProcessor();                
            }
            MessageSpecification messageSpec = null;
            for (MessageContent messagePart : msgList) {
                if (messageSpec == null) {
                    messageSpec = msp.convertXMLtoMessageSpecification(messagePart.toByteArray());
                } else {
                    MessageSpecification tmpSpec = msp.convertXMLtoMessageSpecification(messagePart.toByteArray());
                    for (String element : tmpSpec.getMessageSpec()) {
                        messageSpec.addElementToSpec(element);
                    }
                }
            }

            String updatedMessageName = "";
            if (messageSpec != null){
                for (int ii = 0; ii< messageSpec.getMessageTypes().size(); ii++){
                    updatedMessageName = messageSpec.getMessageTypes().get(ii);
                }
                // TODO Figure out why this might be happening.
                if (!primaryMessageName.equals(updatedMessageName)){
                    System.err.println("C2CRIMessageAdapter::toC2CRIMessage Message name from 2306 message "+primaryMessageName + " does not match message from spec "+updatedMessageName+ " setting to the updated name.");
                    primaryMessageName = updatedMessageName.indexOf(":")>=0 ? updatedMessageName.substring(updatedMessageName.indexOf(":")+1) : updatedMessageName;
                }
            }
            newMessage.setMessageBodyPartsFromContent(msgList);
            newMessage.setMessageDestinationAddress(destinationAddress);
            newMessage.setMessageEncoding(inputMessage.getEncodingType().name());
            newMessage.setMessageName(primaryMessageName);
            newMessage.setEncodedMessageType(primaryMessageName);
            newMessage.setMessageSourceAddress(sourceAddress);
            newMessage.setMessageSpecification(messageSpec);
            newMessage.setMessageType(inputMessage.getMessageType().name());
            newMessage.setTransportTimeInMillis(inputMessage.getTransportedTimeInMs());
            newMessage.setViaTransportProtocol(inputMessage.getProtocolType().name());
        }
        return newMessage;
    }

    /**
     * Update c2crimessage.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     * @param inputMessage the input message
     */
    public static void updateC2CRIMessage(Message message, NTCIP2306Message inputMessage) {
        ArrayList<MessageContent> msgList = new ArrayList();
        if (inputMessage != null) {

            String sourceAddress;
            String destinationAddress;

			// FTP does not set address, So it wass stored under the HTTP Status
			sourceAddress = inputMessage.getHttpStatus().getSource();
			destinationAddress = inputMessage.getHttpStatus().getDestination();

			message.setMessageBodyPartsFromContent(msgList);
            message.setMessageDestinationAddress(destinationAddress);
            message.setMessageEncoding(inputMessage.getEncodingType().name());
            message.setMessageSourceAddress(sourceAddress);
            message.setMessageName(inputMessage.getMessageType().name());
            for (int ii = 0; ii<inputMessage.getNumberMessageParts();ii++){
                message.setMessageType(inputMessage.getMessagePartName(ii+1));
            }
            message.setTransportTimeInMillis(inputMessage.getTransportedTimeInMs());
            message.setViaTransportProtocol(inputMessage.getProtocolType().name());
            if (inputMessage.getNumberMessageParts() > 0)
			{
				message.setEncodedMessageType(inputMessage.getMessagePartName(inputMessage.getNumberMessageParts()));
			}
        }       
    }
    
    
    /**
     * To basic c2crimessage.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param operation the operation
     * @param messageName the message name
     * @param messageType the message type
     * @param message the message
     * @return the message
     */
    public static Message toBasicC2CRIMessage(String operation, String messageName,
            String messageType, byte[] message) {
        MessageManager manager = MessageManager.getInstance();

        Message newMessage = manager.createMessage(operation);

        String primaryMessageName;
        ArrayList<byte[]> msgList = new ArrayList();
        msgList.add(message);
        primaryMessageName = messageName;

        String sourceAddress;
        String destinationAddress;

        sourceAddress = "This message has not been transmitted.";
        destinationAddress = "This message has not been transmitted.";

        newMessage.setMessageBody(message);

        MessageSpecificationProcessor msp = new MessageSpecificationProcessor();
        MessageSpecification messageSpec = null;
        for (byte[] messagePart : msgList) {
            if (messageSpec == null) {
                messageSpec = msp.convertXMLtoMessageSpecification(messagePart);
                if (messageSpec.getMessageTypes().size() > 0)
				{
					primaryMessageName = messageSpec.getMessageTypes().get(messageSpec.getMessageTypes().size()-1);
				}
            } else {
                MessageSpecification tmpSpec = msp.convertXMLtoMessageSpecification(messagePart);
                for (String element : tmpSpec.getMessageSpec()) {
                    messageSpec.addElementToSpec(element);
                }
                if (tmpSpec.getMessageTypes().size() > 0)
				{
					primaryMessageName = tmpSpec.getMessageTypes().get(tmpSpec.getMessageTypes().size()-1);
				}
            }
        }

        newMessage.setMessageBodyParts(msgList);
        newMessage.setMessageDestinationAddress(destinationAddress);
        newMessage.setMessageEncoding(Message.XML_MESSAGE_ENCODING);
        newMessage.setMessageName(primaryMessageName);
        newMessage.setEncodedMessageType(primaryMessageName);
        newMessage.setMessageSourceAddress(sourceAddress);
        newMessage.setMessageSpecification(messageSpec);
        newMessage.setMessageType(messageType);
        newMessage.setTransportTimeInMillis(-1);
        newMessage.setViaTransportProtocol("Not Specified");
        newMessage.setEncodedMessageType(primaryMessageName);

        return newMessage;
    }

    /**
     * Update message from spec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     * @param nameSpaceMap the name space map
     */
    public static void updateMessageFromSpec(Message message, HashMap<String, String> nameSpaceMap) {
        // If message has specification but no body or body parts then try to create them.
        if ((message.getMessageSpecification()!=null) && (message.getMessageSpecification().getMessageSpec().size() > 0)) {
            MessageSpecificationProcessor msp = new MessageSpecificationProcessor(nameSpaceMap);
            msp.updateMessagefromElementSpecList(message.getMessageSpecification().getMessageSpec());


            try {           
                message.setMessageBody(msp.getMessage().getBytes("UTF-8"));
                message.setMessageBodyParts(msp.getMessageParts());
                if (message.getMessageSpecification().getMessageTypes().size()>0)
                 message.setEncodedMessageType(message.getMessageSpecification().getMessageTypes().get(message.getMessageSpecification().getMessageTypes().size()-1));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * Update sub pub message from spec.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param message the message
     * @param nameSpaceMap the name space map
     */
    public static void updateSubPubMessageFromSpec(Message message, HashMap<String, String> nameSpaceMap) {
        // If message has specification but no body or body parts then try to create them.
        if ((message.getMessageSpecification()!=null) && (message.getMessageSpecification().getMessageSpec().size() > 0)) {
            MessageSpecificationProcessor msp = new MessageSpecificationProcessor(nameSpaceMap);

            msp.updateMessagefromElementSpecList(message.getMessageSpecification().getMessageSpec());

            try {         
                message.setMessageBody(msp.getMessageAsSOAP().getBytes("UTF-8"));
                SOAPDecoder thisDecoder = new SOAPDecoder();
                thisDecoder.isSoapEncoded(message.getMessageBody());
                ArrayList<byte[]> tmpArray = new ArrayList();
                for (int ii = 0; ii<thisDecoder.getNumMessageParts();ii++){
                    tmpArray.add(thisDecoder.getMessagePart(ii+1));
                }
                message.setMessageBodyParts(tmpArray);
                if (message.getMessageSpecification().getMessageTypes().size()>0)
                 message.setEncodedMessageType(message.getMessageSpecification().getMessageTypes().get(message.getMessageSpecification().getMessageTypes().size()-1));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }

}
