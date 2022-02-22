/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.plugin.c2cri.ntcip2306.support.verification.v01_69;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author TransCore ITS
 */
public class XMLDeclarationChecker {

    private static String Declaration_String = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    public static String checkerResultReason = "";

    public static boolean validDeclarationHeader(byte[] inputDocument) {
        checkerResultReason = "";
        boolean result = false;
        try {
            // Open the file that is the first
            // command line parameter
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inputDocument);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(inputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            if ((strLine = br.readLine()) != null) {
                // Print the content on the console
                result = strLine.equals(Declaration_String);
                System.out.println("result is " + result + " for " + strLine);
            }
            //Close the input stream
            in.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }

    public static boolean validDeclarationHeader(String inputDocument) {
        checkerResultReason = "";
        boolean result = false;
        try {
            // Open the file that is the first
            // command line parameter
            ByteArrayInputStream inputStream = new ByteArrayInputStream(inputDocument.getBytes("utf-8"));
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(inputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            if ((strLine = br.readLine()) != null) {
                // Print the content on the console
                result = strLine.equals(Declaration_String);
                if (!result){
                    checkerResultReason = "The XML message should contain the following header: "+Declaration_String+
                            "   followed by the message content, but instead contains "+strLine;
                }
                System.out.println("result is " + result + " for " + strLine);
            }
            //Close the input stream
            in.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }

    public static boolean validDeclarationHeader(URL inputDocument) {
        checkerResultReason = "";
        boolean result = false;
        try {
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(inputDocument.openStream());
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            if ((strLine = br.readLine()) != null) {
                // Print the content on the console
                result = strLine.equals(Declaration_String);
                if (!result){
                    checkerResultReason = "The XML message should contain the following header: "+Declaration_String+
                            "   followed by the message content, but instead contains "+strLine;
                }
                System.out.println("result is " + result + " for " + strLine);
            }
            //Close the input stream
            in.close();

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return result;
    }

}
