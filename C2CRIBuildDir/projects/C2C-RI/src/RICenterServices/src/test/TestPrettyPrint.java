/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestPrettyPrint {
    public static void main(String[] args) throws Exception{
        System.out.println("\n\n");
        System.out.println(prettyPrint(new String (readFile(new File("C:\\xmlsample\\OzOrganizationInformationSample-Unformatted.xml")))));        
    }

    private static String prettyPrint(String xmlInput){
        String result = xmlInput;
        
        try{
            Source xmlInputSource = new StreamSource(new StringReader(xmlInput));
            StringWriter stringwriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringwriter);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
            System.err.println("\n\nTransFormerFactory Class URL = "+transformerFactory.getClass().getProtectionDomain().getCodeSource().getLocation().toString());
            System.err.println("\n\nTransFormerFactory Class Name = "+transformerFactory.getClass().getName());
            System.err.println("\n\nTransFormerFactory Class Loader = "+transformerFactory.getClass().getClassLoader().toString());

            Transformer transformer = transformerFactory.newTransformer();
            System.err.println("\n\nTransFormer Class URL = "+transformer.getClass().getProtectionDomain().getCodeSource().getLocation().toString());
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes"); 
            transformer.transform(xmlInputSource,xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        
        return result;
    }

    public static byte[] readFile (File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");

        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength) throw new IOException("File size >= 2 GB");

            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        }
        finally {
            f.close();
        }
    }
    
}
