/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.ftp;

/**
 *
 * @author TransCore ITS, LLC
 */
public interface RIRETRResponseReceiver {
    /**
     *   Sets the response data that will be send to the requesting center
     * 
     * @param responseMessage - the message data to transmit.
     */
    public void setResponseMessage(byte[] responseMessage);

    /**
     *   Sets the flag indicating that there was a filepath error detected.
     *
     * @param filePathError
     */
    public void setFilePathMismatchError(boolean filePathError);

}
