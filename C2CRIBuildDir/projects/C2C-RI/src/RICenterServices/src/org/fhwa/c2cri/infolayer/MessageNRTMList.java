/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.infolayer;

import java.util.ArrayList;


/**
 * Represents the collection of NRTM items that are related to a message.  This helps in identifying mandatory versus optional content.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MessageNRTMList {

    /** The message nrtm list. */
    private ArrayList<MessageNRTMItem> messageNRTMList = new ArrayList<MessageNRTMItem>();

    /**
     * Instantiates a new message nrtm list.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public MessageNRTMList(){
    }

    /**
     * Adds the item.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param selection the selection
     * @param element the element
     * @param instanceName the instance name
     */
    public void addItem(boolean selection, String element, String instanceName){
        MessageNRTMItem thisItem = new MessageNRTMItem(selection, element, instanceName);
        messageNRTMList.add(thisItem);
    }

    /**
     * Inits the.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void init(){
        messageNRTMList.clear();
    }

    /**
     * Gets the message nrtm list.
     *
     * @return the message nrtm list
     */
    public ArrayList<MessageNRTMItem> getMessageNRTMList() {
        return messageNRTMList;
    }


}
