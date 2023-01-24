/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.messagemanager;

import java.util.ArrayList;

/**
 * The Interface MessageInterface defines all methods that a Message class must implement.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface MessageInterface {

    /**
     * To xml.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the string
     */
    public String toXML();

    /**
     * Message specification to xml.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the string
     */
    public String messageSpecificationToXML();

    /**
     * Gets the message specification.
     *
     * @return the message specification
     */
    public ArrayList<String> getMessageSpecification();

    /**
     * Sets the message specification.
     *
     * @param messageSpecification the new message specification
     */
    public void setMessageSpecification(ArrayList<String> messageSpecification);

    /**
     * Gets the message body.
     *
     * @return the message body
     */
    public byte[] getMessageBody();

    /**
     * Sets the message body.
     *
     * @param messageBody the new message body
     */
    public void setMessageBody(byte[] messageBody);

    /**
     * Gets the message name.
     *
     * @return the message name
     */
    public String getMessageName();

    /**
     * Sets the message name.
     *
     * @param messageName the new message name
     */
    public void setMessageName(String messageName);

    /**
     * Gets the message type.
     *
     * @return the message type
     */
    public String getMessageType();




}
