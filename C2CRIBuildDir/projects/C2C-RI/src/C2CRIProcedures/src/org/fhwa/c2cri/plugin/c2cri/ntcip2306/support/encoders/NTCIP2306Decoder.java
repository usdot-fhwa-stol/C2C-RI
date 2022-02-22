/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.encoders;

import java.util.ArrayList;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
//import org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.wsdl.RIWSDL;

/**
 *
 * @author Transcore ITS
 */
public interface NTCIP2306Decoder {

//    protected static String decoderType;

    public byte[] decode(byte[] encodedMessage, RIWSDL userWSDL, RIWSDL infoStdWSDL) throws Exception;

    public String getDecoderType();

    public String getMessageType();

    public boolean isUtf8Encoded();

    public boolean isValidXmlv1();

    public String getXmlDecodeResults();

    public boolean isXmlDecoded();

    public boolean isXmlValidatedToSchema();
    
    public abstract boolean isGzipDecoded();

    public abstract String getGzipDecodingResults();

    public abstract ArrayList<String> getFieldErrorList();

    public abstract ArrayList<String> getParserErrorList();

    public abstract ArrayList<String> getValueErrorList();

    public abstract int getNumberOfMessagePartsReceived();

}
