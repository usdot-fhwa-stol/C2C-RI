/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.messagemanager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * The Class MessageManager maintains all messages that are created and stored during a test.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MessageManager {

    /** The map containing the Messages referenced by the given message name. */
    private static HashMap<String, Message> messageMap;
    
    /** The parent test case. */
    private static String parentTestCase;
    
    /** The manager. */
    private static MessageManager theManager;

    /**  A mapping of prefixes to namespaces */
    private HashMap <String, String> nameSpaceMap;
    
    /**
     * Gets the single instance of MessageManager.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of MessageManager
     */
    public static MessageManager getInstance() {
        if (theManager == null) {
            theManager = new MessageManager();
            return theManager;
        } else {
            return theManager;
        }
    }

    /**
     * Instantiates a new message manager.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private MessageManager() {
        this.parentTestCase = "NO TEST CASE DEFINED";
        this.messageMap = new HashMap<String, Message>();
    }

    /**
     * Gets the parent test case.
     *
     * @return the parent test case
     */
    public String getParentTestCase() {
        return parentTestCase;
    }

    /**
     * Sets the parent test case.
     *
     * @param parentTestCase the new parent test case
     */
    public void setParentTestCase(String parentTestCase) {
        this.parentTestCase = parentTestCase;
        clear();
    }

    public HashMap<String, String> getNameSpaceMap() {
        return nameSpaceMap;
    }

    public void setNameSpaceMap(HashMap<String, String> nameSpaceMap) {
        this.nameSpaceMap = nameSpaceMap;
    }    
    
    /**
     * Adds the message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inMessage the in message
     * @throws Exception the exception
     */
    public void addMessage(Message inMessage) throws Exception {
        messageMap.put(inMessage.getMessageName(), inMessage);
    }

    /**
     * Adds the message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inMessage the in message
     * @param replaceExisting the replace existing
     */
    public void addMessage(Message inMessage, boolean replaceExisting) {
        if (messageMap.containsKey(inMessage.getMessageName())) {
            if (replaceExisting) {
                messageMap.remove(inMessage.getMessageName());
                messageMap.put(inMessage.getMessageName(), inMessage);
            }
        } else {
            messageMap.put(inMessage.getMessageName(), inMessage);
        }
    }

    /**
     * Gets the message.
     *
     * @param messageName the message name
     * @return the message
     * @throws Exception the exception
     */
    public Message getMessage(String messageName) throws Exception {
        return messageMap.get(messageName);
    }

    /**
     * Creates the message.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param parentDialog the parent dialog
     * @return the message
     */
    public Message createMessage(String parentDialog) {
        return new Message(parentDialog);
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
        String xmlResult = "<MessageManager>\n";
        xmlResult = xmlResult.concat("<ParentTestCase>" + parentTestCase + "</ParentTestCase>\n");

        /**
         * get Collection of values contained in HashMap using Collection
         * values() method of HashMap class
         */
        Collection<Message> c = messageMap.values();
        //obtain an Iterator for Collection
        Iterator itr = c.iterator();

        //iterate through HashMap values iterator
        while (itr.hasNext()) {
            Message thisMessage = (Message) itr.next();
            xmlResult = xmlResult.concat(thisMessage.toXML());
        }

        return xmlResult.concat("</MessageManager>");
    }
    
    /**
     * Clear.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void clearMessage(String messageName) {
        /**
         * Clear the specified message from the MessageManager
         */
        try{
            messageMap.remove(messageName);
        } catch (Exception ex){
            System.out.println("MessageManager::clearMessage Error removing requested Message: "+ex.getMessage());
        }
    }
    

    /**
     * Clear.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void clear() {
        /**
         * Clear any existing messages from the MessageManager
         */
        messageMap.clear();

    }
}
