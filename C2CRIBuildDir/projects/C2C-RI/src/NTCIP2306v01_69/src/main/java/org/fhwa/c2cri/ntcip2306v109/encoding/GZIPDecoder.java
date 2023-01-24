/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.encoding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import org.fhwa.c2cri.ntcip2306v109.status.GZIPStatus;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;

/**
 * Processes GZIPPED Messages.  Assumes only one GZip entry is within the byte array.
 *
 * @author TransCore ITS
 */
public class GZIPDecoder {

    /** the decoded bytes. */
    private byte[] gzipDecodedResults;
    
    /** errors that were discovered during decoding. */
    private String gzipDecodingErrorResults = "No GZIP decoding operation was performed.";
    
    /** flag indicating whether the message was decoded. */
    private boolean gzipDecoded = false;
    
    /** the list of tests completed as part of the decoding. */
    private ArrayList<TestAssertion> testAssertionList = new ArrayList<>();
    
    /** the gzip status. */
    GZIPStatus gzipStatus = new GZIPStatus();

    /**
     * Decode the given message.
     *
     * @param messageContent - the message byte array
     * @return true if gzip decoding was successful
     */
    public boolean gzipDecode(byte[] messageContent) {

        GZIPInputStream gzipInputStream;

        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(messageContent);
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

        try {
            gzipInputStream = new GZIPInputStream(byteInputStream);
            byte[] buf = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buf, 0, 1024)) > 0) {
                byteOutputStream.write(buf, 0, len);
            }
            setGzipDecodedResults(byteOutputStream.toByteArray());
            gzipInputStream.close();
            byteInputStream.close();
            byteOutputStream.close();
            setGzipDecoded(true);
            setGzipDecodingErrorResults("");

        } catch (IOException ioex) {
            ioex.printStackTrace();
            setGzipDecodingErrorResults(ioex.getMessage());
        }


        return isGzipDecoded();
    }

    /**
     * Get the decoded bytes.
     *
     * @return the decoded gzip bytes
     */
    public byte[] getGzipDecodedResults() {
        return gzipDecodedResults;
    }

    /**
     * Get the error results.
     *
     * @return the error results
     */
    public String getGzipDecodingErrorResults() {
        return gzipDecodingErrorResults;
    }

    /**
     * Getter for decoding status.
     *
     * @return true if successfully decoded
     */
    public boolean isGzipDecoded() {
        return gzipDecoded;
    }

    /**
     * setter for the gip decode status.
     *
     * @param gzipDecoded the new gzip decoded
     */
    private void setGzipDecoded(boolean gzipDecoded) {
        this.gzipDecoded = gzipDecoded;
    }

    /**
     * setter for the gzip decoded message.
     *
     * @param gzipDecodedResults the new gzip decoded results
     */
    private void setGzipDecodedResults(byte[] gzipDecodedResults) {
        this.gzipDecodedResults = gzipDecodedResults;
    }

    /**
     * setter for the gzip decoding error results.
     *
     * @param gzipDecodingErrorResults the new gzip decoding error results
     */
    private void setGzipDecodingErrorResults(String gzipDecodingErrorResults) {
        this.gzipDecodingErrorResults = gzipDecodingErrorResults;
    }

    /**
     * getter for the test assertion list.
     *
     * @return the resulting test assertions
     */
    public ArrayList<TestAssertion> getTestAssertionList() {
        return testAssertionList;
    }

    /**
     * getter for the gzip status.
     *
     * @return the gzip status
     */
    public GZIPStatus getGZipStatus() {
        boolean storedWellFormedXMLFlag = gzipStatus.isWellFormedXML();
        testAssertionList.clear();
        
        gzipStatus = new GZIPStatus();
        gzipStatus.setWellFormedXML(storedWellFormedXMLFlag);
        gzipStatus.setIsGZIPEncoded(isGzipDecoded());
        gzipStatus.addGZIPError(getGzipDecodingErrorResults());
        testAssertionList.add(new TestAssertion("4.1.2", isGzipDecoded()&&storedWellFormedXMLFlag,
                "A message may be encoded using the GZIP format. When such a message is uncompressed, the message payload shall be a single well formed XML message meeting the requirement of Section 4.1.1. ",
                getGzipDecodingErrorResults()));
        gzipStatus.setTestAssertionList(testAssertionList);

        return gzipStatus;
    }
}
