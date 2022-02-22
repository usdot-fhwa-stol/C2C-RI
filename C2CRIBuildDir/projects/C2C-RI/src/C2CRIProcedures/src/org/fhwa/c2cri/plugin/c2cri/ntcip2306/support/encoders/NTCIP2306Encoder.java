/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.encoders;

/**
 *
 * @author Transcore ITS
 */
public interface NTCIP2306Encoder {

    public abstract byte[] encode(byte[] unEncodedMessage) throws Exception;

    public abstract String getEncoderType();

    public abstract boolean isEncoded();

    public abstract String getEncodingResults();

    public abstract boolean isGzipEncoded();

    public abstract String getGzipEncodingResults();

}
