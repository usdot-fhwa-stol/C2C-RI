/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.encoding;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;
import org.fhwa.c2cri.ntcip2306v109.status.GZIPStatus;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;

/**
 * Encodes the given data in GZip format.  Assumes only one GZip entry is within the byte array.
 *
 * @author TransCore ITS
 */
public class GZIPEncoder {

    /** The gzipped results. */
    private byte[] gzippedResults;
    
    /** The encoding error description. */
    private String encodingErrorDescription = "No GZIP encoding operation has been performed.";
    
    /** The gzip encoded. */
    private boolean gzipEncoded = false;
    
    /** The test assertion list. */
    private ArrayList<TestAssertion> testAssertionList = new ArrayList<>();
    
    /** The gzip status. */
    private GZIPStatus gzipStatus = new GZIPStatus();

    /**
     * Gzip encode.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param unEncodedMessage the un encoded message
     * @return true, if successful
     */
    public boolean gzipEncode(byte[] unEncodedMessage) {

        try {
            ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();

            BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(byteOutputStream));

            bufos.write(unEncodedMessage);
            bufos.close();
            gzippedResults = byteOutputStream.toByteArray();
            byteOutputStream.close();
            setGzipEncoded(true);
            setEncodingErrorDescription("");

        } catch (Exception ex) {
            ex.printStackTrace();
            setGzipEncoded(false);
            setEncodingErrorDescription(ex.getMessage());
        }


        return isGzipEncoded();
    }

    /**
     * Gets the encoding error description.
     *
     * @return the encoding error description
     */
    public String getEncodingErrorDescription() {
        return encodingErrorDescription;
    }

    /**
     * Sets the encoding error description.
     *
     * @param encodingErrorDescription the new encoding error description
     */
    private void setEncodingErrorDescription(String encodingErrorDescription) {
        this.encodingErrorDescription = encodingErrorDescription;
    }

    /**
     * Sets the gzip encoded.
     *
     * @param gzipEncoded the new gzip encoded
     */
    private void setGzipEncoded(boolean gzipEncoded) {
        this.gzipEncoded = gzipEncoded;
    }

    /**
     * Gets the gzipped results.
     *
     * @return the gzipped results
     */
    public byte[] getGzippedResults() {
        return gzippedResults;
    }

    /**
     * Checks if is gzip encoded.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is gzip encoded
     */
    public boolean isGzipEncoded() {
        return gzipEncoded;
    }

    /**
     * Gets the test assertion list.
     *
     * @return the test assertion list
     */
    public ArrayList<TestAssertion> getTestAssertionList() {
        return testAssertionList;
    }

    /**
     * Gets the g zip status.
     *
     * @return the g zip status
     */
    public GZIPStatus getGZipStatus() {
        boolean storedWellFormedXMLStatusFlag = gzipStatus.isWellFormedXML();
        testAssertionList.clear();

        gzipStatus = new GZIPStatus();
        gzipStatus.setWellFormedXML(storedWellFormedXMLStatusFlag);
        gzipStatus.setIsGZIPEncoded(isGzipEncoded());
        gzipStatus.addGZIPError(getEncodingErrorDescription());
        testAssertionList.add(new TestAssertion("4.1.2", isGzipEncoded()&&storedWellFormedXMLStatusFlag,
                "A message may be encoded using the GZIP format. When such a message is uncompressed, the message payload shall be a single well formed XML message meeting the requirement of Section 4.1.1. ",
                getEncodingErrorDescription()));
        gzipStatus.setTestAssertionList(testAssertionList);

        return gzipStatus;
    }
}
