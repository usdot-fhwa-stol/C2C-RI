/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.utilities;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * The Class XMLFileReader reads in an XML document source and provides it as a byte array.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class XMLFileReader {
    
    /**
     * Read file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param file the file
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] readFile (String file) throws IOException {
        return readFile(new File(file));
    }

    /**
     * Read file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param file the file
     * @return the byte[]
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] readFile (File file) throws IOException {
        // Open file
        try (RandomAccessFile f = new RandomAccessFile(file, "r"))
		{
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) throw new IOException("File size >= 2 GB");

            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        }
    }

}
