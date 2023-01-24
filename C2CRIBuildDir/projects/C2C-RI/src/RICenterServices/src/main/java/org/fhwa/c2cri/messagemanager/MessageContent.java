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
public interface MessageContent {


    /**
     * Converts the content of this message to byte array.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the byte[]
     */
    byte[] toByteArray();

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    String toString();
    
    /**
     * Provides the content of this message via an InputStream
     * @return the content via an InputStream
     */
    InputStream toInputStream();
}
