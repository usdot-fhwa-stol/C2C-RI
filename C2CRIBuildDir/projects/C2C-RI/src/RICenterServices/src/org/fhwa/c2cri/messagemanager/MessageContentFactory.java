/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.messagemanager;

import java.io.InputStream;

/**
 *
 * @author TransCore ITS, LLC
 */
public class MessageContentFactory {

    /**
     * Creates Message Content from an InputStream.Pre-Conditions: N/A Post-Conditions: N/A
     *
     *
     * @param namespace
     * @param inMessage the in message
     */
    public static MessageContent create(String namespace, InputStream inMessage) {
        return new MessageContentDBCache(namespace, inMessage);
    }
    
    public static MessageContent create(String namespace, byte[] inMessage) {
        return new MessageContentDBCache(namespace, inMessage);
    }    
    
}
