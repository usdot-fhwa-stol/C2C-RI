/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.centermodel;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class EmulationDataFileProcessor {

    public static byte[] getByteArray(URL fileURL) throws EntityEmulationException {
        try {        
            InputStream stream = fileURL.openStream();  // throws IOException           
            byte results[] = getBytes(stream);  // throws IOException
            stream.close();
            return results;  
        } catch (Exception ex) {
            throw new EntityEmulationException(ex);
        }
    }

    public static byte[] getByteArray(String fileName) throws EntityEmulationException {
        byte fileContent[] = null;
        try {

            //create file object
            File file = new File(fileName);
            //create FileInputStream object
            FileInputStream fis = new FileInputStream(file);
            fileContent = getBytes(fis);
//            fis.close();
//            /*
//             * Create byte array large enough to hold the content of the file.
//             * Use File.length to determine size of the file in bytes.
//             */
//            fileContent = new byte[(int) file.length()];
//
//            /*
//             * To read content of the file in byte array, use
//             * int read(byte[] byteArray) method of java FileInputStream class.
//             *
//             */
//            fis.read(fileContent);
//
            fis.close();
        } catch (FileNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (IOException ex) {
            throw new EntityEmulationException(ex);
        }

        return fileContent;

    }

    private static byte[] getBytes(InputStream is) throws IOException {

        int len;
        int size = 1024;
        byte[] buf;

        if (is instanceof ByteArrayInputStream) {
            size = is.available();
            buf = new byte[size];
            len = is.read(buf, 0, size);
        } else {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            buf = new byte[size];
            while ((len = is.read(buf, 0, size)) != -1) {
                bos.write(buf, 0, len);
            }
            buf = bos.toByteArray();
        }
        return buf;
    }

    public static StringBuffer getContent(URL fileURL) throws EntityEmulationException {
        try {
            String filePath = fileURL.toURI().getPath();
            return getContent(filePath);
        } catch (URISyntaxException ex) {
            throw new EntityEmulationException(ex);
        }
    }

    public static StringBuffer getContent(String fileName) throws EntityEmulationException {
        byte[] entityData = getByteArray(fileName);

        StringBuffer returnBuffer = getContent(entityData);
        return returnBuffer;

    }

    public static StringBuffer getContent(byte[] entityData) throws EntityEmulationException {
        StringBuffer returnBuffer = new StringBuffer();

        if (entityData != null) {
            try {
                ByteArrayInputStream bInput = new ByteArrayInputStream(entityData);
                BufferedReader bfReader = null;
                bfReader = new BufferedReader(new InputStreamReader(bInput,"UTF-8"));
                String temp = null;

                while ((temp = bfReader.readLine()) != null) {
                    // Each line should have be in an 'elementName = elementValue' format.
                    // For each element, lookup it's SchemaId and determine it's entityId
                    returnBuffer.append(temp).append("\n");
                }
                bInput.close();
            } catch (FileNotFoundException ex) {
                throw new EntityEmulationException(ex);
            } catch (IOException ex) {
                throw new EntityEmulationException(ex);
            }
        }
        return returnBuffer;

    }

}
