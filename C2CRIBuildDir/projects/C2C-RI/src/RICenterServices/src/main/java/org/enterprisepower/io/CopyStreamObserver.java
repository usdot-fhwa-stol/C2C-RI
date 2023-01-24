/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.enterprisepower.io;

/**
 * An asynchronous update interface for receiving notifications
 * about CopyStream information as the CopyStream is constructed.
 *
 * @author Open source community
 */
public interface CopyStreamObserver {

    /**
     * This method is called when information about an CopyStream
     * which was previously requested using an asynchronous
     * interface becomes available.
     *
     * @param currentBuffer the current buffer
     * @param bufferCount the buffer count
     * @param sequenceCount the sequence count
     */
    public void streamUpdate(byte[] currentBuffer, int bufferCount, int sequenceCount);

}
