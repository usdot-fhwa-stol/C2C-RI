/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author TransCore ITS, LLC
 */
public class XMLFileReader {
    public static byte[] readFile (String file) throws IOException {
        return readFile(new File(file));
    }

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
