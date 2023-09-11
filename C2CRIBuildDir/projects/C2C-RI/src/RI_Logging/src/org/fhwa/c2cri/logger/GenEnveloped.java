/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.xml.security.stax.ext.OutboundXMLSec;
import org.apache.xml.security.stax.ext.SecurePart;
import org.apache.xml.security.stax.ext.XMLSec;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.securityToken.SecurityTokenConstants;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Strings;
import org.w3c.dom.Document;

/**
 * This is a simple example of generating an Enveloped XML
 * Signature using the JSR 105 API. The resulting signature will look
 * like (key and signature values will be different):
 * @author TransCore ITS, LLC
 * Last Update 8/2/2012
 * 
 * <pre><code>
 *<Envelope xmlns="urn:envelope">
 * <Signature xmlns="http://www.w3.org/2000/09/xmldsig#">
 *   <SignedInfo>
 *     <CanonicalizationMethod Algorithm="http://www.w3.org/TR/2001/REC-xml-c14n
-20010315"/>
 *     <SignatureMethod Algorithm="http://www.w3.org/2000/09/xmldsig#dsa-sha1"/>
 *     <Reference URI="">
 *       <Transforms>
 *         <Transform Algorithm="http://www.w3.org/2000/09/xmldsig#enveloped-signature"/>
 *       </Transforms>
 *       <DigestMethod Algorithm="http://www.w3.org/2000/09/xmldsig#sha1"/>
 *       <DigestValue>K8M/lPbKnuMDsO0Uzuj75lQtzQI=<DigestValue>
 *     </Reference>
 *   </SignedInfo>
 *   <SignatureValue>
 *     DpEylhQoiUKBoKWmYfajXO7LZxiDYgVtUtCNyTgwZgoChzorA2nhkQ==
 *   </SignatureValue>
 *   <KeyInfo>
 *     <KeyValue>
 *       <DSAKeyValue>
 *         <P>
 *           rFto8uPQM6y34FLPmDh40BLJ1rVrC8VeRquuhPZ6jYNFkQuwxnu/wCvIAMhukPBL
 *           FET8bJf/b2ef+oqxZajEb+88zlZoyG8g/wMfDBHTxz+CnowLahnCCTYBp5kt7G8q
 *           UobJuvjylwj1st7V9Lsu03iXMXtbiriUjFa5gURasN8=
 *         </P>
 *         <Q>
 *           kEjAFpCe4lcUOdwphpzf+tBaUds=
 *         </Q>
 *         <G>
 *           oe14R2OtyKx+s+60O5BRNMOYpIg2TU/f15N3bsDErKOWtKXeNK9FS7dWStreDxo2
 *           SSgOonqAd4FuJ/4uva7GgNL4ULIqY7E+mW5iwJ7n/WTELh98mEocsLXkNh24HcH4
 *           BZfSCTruuzmCyjdV1KSqX/Eux04HfCWYmdxN3SQ/qqw=
 *         </G>
 *         <Y>
 *           pA5NnZvcd574WRXuOA7ZfC/7Lqt4cB0MRLWtHubtJoVOao9ib5ry4rTk0r6ddnOv
 *           AIGKktutzK3ymvKleS3DOrwZQgJ+/BDWDW8kO9R66o6rdjiSobBi/0c2V1+dkqOg
 *           jFmKz395mvCOZGhC7fqAVhHat2EjGPMfgSZyABa7+1k=
 *         </Y>
 *       </DSAKeyValue>
 *     </KeyValue>
 *   </KeyInfo>
 * </Signature>
 *</Envelope>
 * </code></pre>
 */
public class GenEnveloped {

    //
    // Synopsis: java GenEnveloped [document] 
    //
    //    where "document" is the name of a file containing the XML document
    //    to be signed. 
    //
    public static void main(String[] args) throws Exception {
        String[] myargs = new String[1];
        myargs[0]= "c:\\c2cri\\OSWSDLTest.2022-02-04_15-00-30.xml";

        Thread.sleep(10000);
        GenEnveloped thisEnvelope = new GenEnveloped();
        try{
        thisEnvelope.signFile(myargs[0]);
        } catch (Exception ex){
           System.out.println(" Exception: "+ex.getMessage());
                
       }  catch (Error ex){
           System.out.println(" Error: "+ex.getMessage());
           System.exit(1);
       }

    }
   

   /**
    * Add the signature element to the file provided.
    * @param fileName
    * @throws Exception 
    */
    public void signFile(String fileName) throws Exception{
         Security.addProvider(new BouncyCastleProvider());

        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        System.out.println("##### Before Signing "+fileName+" #####");
        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:" 
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:" 
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);         
         // Read in plaintext document
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream sourceDocument = new FileInputStream(fileName))
		{
			XMLStreamWriter xmlStreamWriter = new LogFileStreamWriter(baos, "UTF-8");

			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			xmlInputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			xmlInputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(sourceDocument);

			XmlReaderToWriter.writeAll(xmlStreamReader, xmlStreamWriter);
		}

        try ( OutputStream outputStream = new FileOutputStream(fileName)) {
            baos.writeTo(outputStream);
        }               
        
        System.out.println("##### After Signing "+fileName+" #####");
        System.out.println("##### Heap utilization statistics [MB] #####");

        //Print used memory
        System.out.println("Used Memory:" 
                + (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        System.out.println("Free Memory:" 
                + runtime.freeMemory() / mb);

        //Print total available memory
        System.out.println("Total Memory:" + runtime.totalMemory() / mb);

        //Print Maximum available memory
        System.out.println("Max Memory:" + runtime.maxMemory() / mb);         
        
    }
    

    
    /**
     * Wrapper for the XMLStreamWriter which adds the required signature element
     * to the log file.
     */    
    class LogFileStreamWriter implements XMLStreamWriter{
        XMLStreamWriter writer;
               
        /**
         * Construct the Log File Writer
         * @param stream the output file stream for writing
         * @param encoding the type of encoding to be applied in writing the file.
         * @throws XMLStreamException 
         */
        public LogFileStreamWriter(OutputStream stream, String encoding) throws XMLStreamException{
            XMLOutputFactory output = XMLOutputFactory.newInstance();
            writer =  output.createXMLStreamWriter(stream, encoding);                  
        }
        
        @Override
        public void writeStartElement(String arg0) throws XMLStreamException {
            writer.writeStartElement(arg0);     
            // Insert the digital signature element after the logFile element.
            if (arg0.equals("logFile")){
                writer.writeStartElement("dsig","Signature","http://www.w3.org/2000/09/xmldsig#");  
                writer.writeNamespace("dsig", "http://www.w3.org/2000/09/xmldsig#");
                writer.writeEndElement();
            }
        }

        @Override
        public void writeStartElement(String arg0, String arg1) throws XMLStreamException {
            writer.writeStartElement(arg0, arg1);
        }

        @Override
        public void writeStartElement(String arg0, String arg1, String arg2) throws XMLStreamException {
            writer.writeStartElement(arg0, arg1, arg2);
        }

        @Override
        public void writeEmptyElement(String arg0, String arg1) throws XMLStreamException {
            writer.writeEmptyElement(arg0, arg1);
        }

        @Override
        public void writeEmptyElement(String arg0, String arg1, String arg2) throws XMLStreamException {
            writer.writeEmptyElement(arg0, arg1, arg2);
        }

        @Override
        public void writeEmptyElement(String arg0) throws XMLStreamException {
            writer.writeEmptyElement(arg0);
        }

        @Override
        public void writeEndElement() throws XMLStreamException {
            writer.writeEndElement();
        }

        @Override
        public void writeEndDocument() throws XMLStreamException {
            writer.writeEndDocument();
        }

        @Override
        public void close() throws XMLStreamException {
            writer.close();
        }

        @Override
        public void flush() throws XMLStreamException {
            writer.flush();
        }

        @Override
        public void writeAttribute(String arg0, String arg1) throws XMLStreamException {
            writer.writeAttribute(arg0, arg1);
        }

        @Override
        public void writeAttribute(String arg0, String arg1, String arg2, String arg3) throws XMLStreamException {
            writer.writeAttribute(arg0, arg1, arg2, arg3);
        }

        @Override
        public void writeAttribute(String arg0, String arg1, String arg2) throws XMLStreamException {
            writer.writeAttribute(arg0, arg1, arg2);
        }

        @Override
        public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
            writer.writeNamespace(arg0, arg1);
        }

        @Override
        public void writeDefaultNamespace(String arg0) throws XMLStreamException {
            writer.writeDefaultNamespace(arg0);
        }

        @Override
        public void writeComment(String arg0) throws XMLStreamException {
            writer.writeComment(arg0);
        }

        @Override
        public void writeProcessingInstruction(String arg0) throws XMLStreamException {
            writer.writeProcessingInstruction(arg0);
        }

        @Override
        public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
            writer.writeProcessingInstruction(arg0, arg1);
        }

        @Override
        public void writeCData(String arg0) throws XMLStreamException {
            writer.writeCData(arg0);
        }

        @Override
        public void writeDTD(String arg0) throws XMLStreamException {
            writer.writeDTD(arg0);
        }

        @Override
        public void writeEntityRef(String arg0) throws XMLStreamException {
            writer.writeEntityRef(arg0);
        }

        @Override
        public void writeStartDocument() throws XMLStreamException {
            writer.writeStartDocument();
        }

        @Override
        public void writeStartDocument(String arg0) throws XMLStreamException {
            writer.writeStartDocument(arg0);
        }

        @Override
        public void writeStartDocument(String arg0, String arg1) throws XMLStreamException {
            writer.writeStartDocument(arg0, arg1);
        }

        @Override
        public void writeCharacters(String arg0) throws XMLStreamException {
            writer.writeCharacters(arg0);
        }

        @Override
        public void writeCharacters(char[] arg0, int arg1, int arg2) throws XMLStreamException {
            writer.writeCharacters(arg0, arg1, arg2);
        }

        @Override
        public String getPrefix(String arg0) throws XMLStreamException {
            return writer.getPrefix(arg0);
        }

        @Override
        public void setPrefix(String arg0, String arg1) throws XMLStreamException {
            writer.setPrefix(arg0, arg1);
        }

        @Override
        public void setDefaultNamespace(String arg0) throws XMLStreamException {
            writer.setDefaultNamespace(arg0);
        }

        @Override
        public void setNamespaceContext(NamespaceContext arg0) throws XMLStreamException {
            writer.setNamespaceContext(arg0);
        }

        @Override
        public NamespaceContext getNamespaceContext() {
            return writer.getNamespaceContext();
        }

        @Override
        public Object getProperty(String arg0) throws IllegalArgumentException {
            return writer.getProperty(arg0);
        }
        
    }
}

