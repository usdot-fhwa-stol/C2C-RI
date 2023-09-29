/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.java.net;

import java.io.UnsupportedEncodingException;

/**
 * The Class OverTheWireLogger formats the bytes sent and received from a SUT and logs them in the test log.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  12/1/2013
 */
public class OverTheWireLogger {

    /** The Constant BUF_SIZE. */
    public static final int BUF_SIZE = 1024;

    /**
     * Stream update.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param currentBuffer the current buffer
     * @param bufferCount the buffer count
     * @param sequenceCount the sequence count
     * @param inputStream the input stream
     * @param connInfo the conn info
     */
    public void streamUpdate(byte[] currentBuffer, int bufferCount, int sequenceCount, boolean inputStream, ConnectionInformation connInfo) {
        String testCase = connInfo.getTestCaseName();
        String connectionName = connInfo.getConnectionName();
        if (!connectionName.isEmpty()) {
            String processType;
            if (inputStream) {
                processType = connInfo.isServerSocket() ? "Request" : "Response";
            } else {
                processType = connInfo.isServerSocket() ? "Response" : "Request";
            }
            long timeStamp = System.currentTimeMillis();
            StringBuffer fullFile = new StringBuffer();

            int width = 16;
            for (int index = 0; index < bufferCount; index += width) {
                fullFile = fullFile.append(printHex(currentBuffer, index, width));
                try {
                    fullFile = fullFile.append(printAscii(currentBuffer, index, width));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fullFile = fullFile.append(": Error - " + ex.getMessage() + "\n");
                }
            }

            int cdataLocation = fullFile.indexOf("![CDATA[");
            int indexLocation = fullFile.indexOf("]]>");
            while ((cdataLocation != -1) && (indexLocation != -1)) {
                fullFile.replace(indexLocation, indexLocation + 2, "]]'>");
                cdataLocation = fullFile.indexOf("![CDATA[", indexLocation);
                indexLocation = fullFile.indexOf("]]>", indexLocation);
            }
            String messageToXML = "<rawOTWMessage>\n";
            messageToXML = messageToXML.concat("<testCase>" + testCase + "</testCase>\n");
            messageToXML = messageToXML.concat("<connectionName>" + connectionName + "</connectionName>\n");
            messageToXML = messageToXML.concat("<ProcessType>" + processType + "</ProcessType>\n");
            messageToXML = messageToXML.concat("<SourceAddress>" + (inputStream ? connInfo.getRemoteIPAddress() + ":" + connInfo.getRemotePort() : connInfo.getLocalIPAddress() + ":" + connInfo.getLocalPort()) + "</SourceAddress>\n");
            messageToXML = messageToXML.concat("<DestinationAddress>" + (!inputStream ? connInfo.getRemoteIPAddress() + ":" + String.valueOf(connInfo.getRemotePort()) : connInfo.getLocalIPAddress() + ":" + connInfo.getLocalPort()) + "</DestinationAddress>\n");
            messageToXML = messageToXML.concat("<timestampInMillis>" + timeStamp + "</timestampInMillis>\n");
            messageToXML = messageToXML.concat("<sequenceCount>" + connInfo.getSequenceCount() + "</sequenceCount>\n");
            messageToXML = messageToXML.concat("<message>\n<![CDATA[\n" + fullFile + "]]>\n</message>\n");
            messageToXML = messageToXML.concat("</rawOTWMessage>\n");

			TrafficLogger oTL = ConnectionsDirectory.getInstance().getTrafficLogger();
            if (oTL != null) {
                oTL.log(TrafficLogger.LoggingLevel.INFO, messageToXML);
            }
        } else {
            System.out.println("OverTheWireLogger::streamUpdate Look Here ");            
        }
    }

    /**
     * Prints the hex.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param bytes the bytes
     * @param offset the offset
     * @param width the width
     * @return the string
     */
    private String printHex(byte[] bytes, int offset, int width) {
        String fs = "";
        for (int index = 0; index < width; index++) {
            if (index + offset < bytes.length) {
                fs = fs.concat(String.format("%02x ", bytes[index + offset]).toUpperCase());
            } else {
                fs = fs.concat(String.format("   "));

            }
        }
        return fs;
    }

    /**
     * Prints the ascii.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param bytes the bytes
     * @param index the index
     * @param width the width
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     */
    private String printAscii(byte[] bytes, int index, int width)
            throws UnsupportedEncodingException {
        byte[] invalidByte = new byte[1];
        invalidByte[0] = 0x00;
        String invalidString = new String(invalidByte, "UTF-8");
        invalidByte[0] = 0x1f;
        String invalidString2 = new String(invalidByte, "UTF-8");
        invalidByte[0] = 0x8;
        String invalidString3 = new String(invalidByte, "UTF-8");
        invalidByte[0] = 0x18;
        String invalidString4 = new String(invalidByte, "UTF-8");


        if (index < bytes.length) {
            width = Math.min(width, bytes.length - index);
            byte[] tmpBytes = new byte[width];
            int tmpByteCounter = 0;
        for (int ii = index; ii < index+width; ii++) {
            if (bytes[ii] < 0x20 || bytes[ii] > 0x7f) {
                tmpBytes[tmpByteCounter] = 0x2e;
            } else {
                tmpBytes[tmpByteCounter]= bytes[ii];
            }
            tmpByteCounter++;
        }
            return ":"
                    + new String(tmpBytes, 0, width, "UTF-8").replace("\r\n", ".").replace(
                    "\n",
                    ".").replaceAll(invalidString, ".").replaceAll(invalidString2, ".").replaceAll(invalidString3, ".").replaceAll(invalidString4, ".").replaceAll("[^\\x20-\\x7e]", ".") + "\n";
        } else {
            return "\n";
        }
    }
}
