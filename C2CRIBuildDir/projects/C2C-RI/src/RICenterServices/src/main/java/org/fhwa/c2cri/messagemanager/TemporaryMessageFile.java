/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.messagemanager;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The Class TemporaryMessageFile stores message content in a temporary file on disk.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TemporaryMessageFile {

    /** The file path. */
    String filePath;
    
    /** The file stream. */
    ResetOnCloseInputStream fileStream;

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        tmpTest();
        System.gc();
        tmpTest();
        System.gc();
        System.out.println("Exiting Now.");
    }
    
    /**
     * Tmp test.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private static void tmpTest(){
        String output = "This is the message.";

        ByteArrayInputStream bais = new ByteArrayInputStream(output.getBytes());
        TemporaryMessageFile ttm = new TemporaryMessageFile(bais);
        String byteString = new String(ttm.toByteArray());
        System.out.println("Byte Array: "+ byteString);
        System.out.println("String:  "+ttm.toString());        
        
    }
    
    /**
     * Instantiates a new temporary message file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inMessage the in message
     */
    public TemporaryMessageFile(InputStream inMessage) {

        createMessageFile(inMessage);
    }

    /**
     * To byte array.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the byte[]
     */
    public byte[] toByteArray() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        
        int nRead;
        byte[] data = new byte[16384];

        try {
            while ((nRead = fileStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            fileStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return buffer.toByteArray();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString(){
        String result = new String(toByteArray());
        return result;
    }
    
    /**
     * Creates the message file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param inMessage the in message
     */
    private void createMessageFile(InputStream inMessage) {
        boolean fileNotFound = true;
        while (fileNotFound) {
            String testPath = generateFilename();

            try {
                File tmpFile = new File (testPath);
                if (tmpFile.exists()){
                    fileNotFound = false; 
                    this.filePath = testPath;
                }
//                RandomAccessFile f = new RandomAccessFile(
//                        new File(testPath), "r");
            } catch (Exception e) {
                // exception thrown by RandomAccessFile if 
                // testPath doesn't exist (ie: it can't be read)

                this.filePath = testPath;
                fileNotFound = false;
            }
        }

        try {
            OutputStream f = new FileOutputStream(
                    new File(this.filePath));

            boolean keepReading = true;
            while (keepReading) {
                int inputByte = inMessage.read();
                if (inputByte == -1) {
                    keepReading = false;
                } else {
                    f.write(inputByte);
                }
            }
            f.close();
            System.out.println("Temp File is "+this.filePath);
            fileStream = new ResetOnCloseInputStream(new BufferedInputStream(new FileInputStream(new File(this.filePath)),2048));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //now create your file with filePath    
    }

    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalize() throws Throwable {
        super.finalize();
        System.out.println("Finalize was called");

        if (filePath != null) {
            File tmpFile = new File(filePath);
            if (tmpFile.exists()) {
                System.out.println("Finalize is deleting the file " + filePath);
                tmpFile.delete();
            }
        }
    }

    /**
     * Generate filename.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the string
     */
    public String generateFilename() {
        String fileName = "NotGenerated.tmp";
        try {
            File dirPath = new File(".\\");
            fileName = File.createTempFile("Message", "tmp", dirPath).getAbsolutePath();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return fileName;
    }

    /**
     * The Class ResetOnCloseInputStream.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class ResetOnCloseInputStream extends InputStream {

        /** The decorated. */
        private final InputStream decorated;

        /**
         * Instantiates a new reset on close input stream.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param anInputStream the an input stream
         */
        private ResetOnCloseInputStream(InputStream anInputStream) {
            if (!anInputStream.markSupported()) {
                throw new IllegalArgumentException("marking not supported");
            }

            anInputStream.mark(Integer.MAX_VALUE); 
            decorated = anInputStream;
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#close()
         */
        @Override
        public void close() throws IOException {
            decorated.reset();
        }

        /* (non-Javadoc)
         * @see java.io.InputStream#read()
         */
        @Override
        public int read() throws IOException {
            return decorated.read();
        }

        /**
         * Release.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @throws IOException Signals that an I/O exception has occurred.
         */
        public void release() throws IOException {
            decorated.close();

        }
    }
}
