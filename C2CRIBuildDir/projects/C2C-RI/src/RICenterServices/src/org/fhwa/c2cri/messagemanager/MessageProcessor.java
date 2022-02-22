/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.messagemanager;

/**
 * Defines the interface for the processing of Messages.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface MessageProcessor {

    /**
     * Returns the value within the message associated with the given fieldIdentifier.
     *
     * @param targetMessage the target message
     * @param fieldIdentifier the field identifier
     * @return the value associated with the fieldIdentifier provided
     */
    public String getFieldValue(Message targetMessage, String fieldIdentifier);

    /**
     * Sets the value within the message associated with the given fieldIdentifier.
     *
     * @param targetMessage the target message
     * @param fieldIdentifier the field identifier
     * @param value the value
     */
    public void setFieldValue(Message targetMessage,String fieldIdentifier, String value);

    /**
     * Returns a flag indicating whether the message contains the provided messageType.
     *
     * @param targetMessage the target message
     * @param messageType the message type
     * @return a flag indicating whether the message contains the provided message type.
     */
    public boolean containsMessageType(Message targetMessage, String messageType);
}
