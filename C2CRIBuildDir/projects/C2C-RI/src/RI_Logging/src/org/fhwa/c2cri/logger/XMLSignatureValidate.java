/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.logger;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;


/**
 * This is a simple validation of an XML
 * Signature. 
 *
 * @author TransCore ITS, LLC
 * Last Updated:  11/8/2013
 */
public class XMLSignatureValidate {

    //
    // Synopsis: java XMLSignatureValidate [document]
    //
    //	  where "document" is the name of a file containing the XML document
    //	  to be validated.
    //
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
        String[] myargs = new String[21];
        myargs[0]= "c:\\c2cri\\OSWSDLTest.2022-02-04_15-00-30.xml";


        XMLSignatureValidate xmlValidate = new XMLSignatureValidate();
        xmlValidate.isValidXMLSignedFile(myargs[0]);
    }

    /**
     * Verify that the file provided includes the required digital signature element.
     * @param fileName
     * @return
     * @throws Exception 
     */
    public boolean isValidXMLSignedFile(String fileName) throws Exception{

        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();
        System.out.println("##### Before Verifying "+fileName+" #####");
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
        
        List<QName> namesToSign = new ArrayList<QName>();
        namesToSign.add(new QName("", "logFile"));

        ByteArrayInputStream inputStream = new ByteArrayInputStream(Files.readAllBytes(Paths.get(fileName)));       

       
        LogFileStreamReader xmlStreamReader = new LogFileStreamReader(inputStream);

        boolean found = false;

        while (xmlStreamReader.hasNext()){
            xmlStreamReader.next();
            if (found = xmlStreamReader.isSignedFile()){
                System.out.println("Recognized a QName Match!!");
                break;
            }
        }
        
        xmlStreamReader.close();
        inputStream.close();

        System.out.println("##### After Verifying "+fileName+" #####");
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

        return found;
    }    
   
    

    
    /**
     * Wrapper for the XMLStreamReader which provides confirmation that the file
     * contains the required signature element.
     */
    class LogFileStreamReader implements XMLStreamReader{

        XMLStreamReader reader;
        boolean containsSignature = false;
        QName signatureQName = new QName("http://www.w3.org/2000/09/xmldsig#", "Signature", "dsig");
        
        /**
         * Construct the Log FIle Stream Reader 
         * @param stream
         * @throws XMLStreamException 
         */       
        public LogFileStreamReader(InputStream stream) throws XMLStreamException{
            XMLInputFactory input = XMLInputFactory.newInstance();
            reader =  input.createXMLStreamReader(stream);                  
        }        
        
        /**
         * Provides the results of the check to see if the file contains the appropriate
         * signature element.
         * @return true if the digital signature element was found in the file.
         */
        public boolean isSignedFile(){
            return containsSignature;
        }
        
        @Override
        public Object getProperty(String arg0) throws IllegalArgumentException {
            return reader.getProperty(arg0);
        }

        @Override
        public int next() throws XMLStreamException {
            int nextValue = reader.next();
            if (reader.isStartElement()){
                QName results = getName();
                if (results.equals(signatureQName)){
                    System.out.println("Found a QName Match!!");
                    containsSignature = true;
                }            
            }
            return nextValue;
        }

        @Override
        public void require(int arg0, String arg1, String arg2) throws XMLStreamException {
            reader.require(arg0,arg1,arg2);
        }

        @Override
        public String getElementText() throws XMLStreamException {
            return reader.getElementText();
        }

        @Override
        public int nextTag() throws XMLStreamException {
            return reader.nextTag();
        }

        @Override
        public boolean hasNext() throws XMLStreamException {
            return reader.hasNext();
        }

        @Override
        public void close() throws XMLStreamException {
            reader.close();
        }

        @Override
        public String getNamespaceURI(String arg0) {
            return reader.getNamespaceURI(arg0);
        }

        @Override
        public boolean isStartElement() {
            return reader.isStartElement();
        }

        @Override
        public boolean isEndElement() {
            return reader.isEndElement();
        }

        @Override
        public boolean isCharacters() {
            return reader.isCharacters();
        }

        @Override
        public boolean isWhiteSpace() {
            return reader.isWhiteSpace();
        }

        @Override
        public String getAttributeValue(String arg0, String arg1) {
            return reader.getAttributeValue(arg0, arg1);
        }

        @Override
        public int getAttributeCount() {
            return reader.getAttributeCount();
        }

        @Override
        public QName getAttributeName(int arg0) {
            return reader.getAttributeName(arg0);
        }

        @Override
        public String getAttributeNamespace(int arg0) {
            return reader.getAttributeNamespace(arg0);
        }

        @Override
        public String getAttributeLocalName(int arg0) {
            return reader.getAttributeLocalName(arg0);
        }

        @Override
        public String getAttributePrefix(int arg0) {
            return reader.getAttributePrefix(arg0);
        }

        @Override
        public String getAttributeType(int arg0) {
            return reader.getAttributeType(arg0);
        }

        @Override
        public String getAttributeValue(int arg0) {
            return reader.getAttributeValue(arg0);
        }

        @Override
        public boolean isAttributeSpecified(int arg0) {
            return reader.isAttributeSpecified(arg0);
        }

        @Override
        public int getNamespaceCount() {
            return reader.getNamespaceCount();
        }

        @Override
        public String getNamespacePrefix(int arg0) {
            return reader.getNamespacePrefix(arg0);
        }

        @Override
        public String getNamespaceURI(int arg0) {
            return reader.getNamespaceURI(arg0);
        }

        @Override
        public NamespaceContext getNamespaceContext() {
            return reader.getNamespaceContext();
        }

        @Override
        public int getEventType() {
            return reader.getEventType();
        }

        @Override
        public String getText() {
            return reader.getText();
        }

        @Override
        public char[] getTextCharacters() {
            return reader.getTextCharacters();
        }

        @Override
        public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3) throws XMLStreamException {
            return reader.getTextCharacters(arg0, arg1, arg2, arg3);
        }

        @Override
        public int getTextStart() {
            return reader.getTextStart();
        }

        @Override
        public int getTextLength() {
            return reader.getTextLength();
        }

        @Override
        public String getEncoding() {
            return reader.getEncoding();
        }

        @Override
        public boolean hasText() {
            return reader.hasText();
        }

        @Override
        public Location getLocation() {
            return reader.getLocation();
        }

        @Override
        public QName getName() {
            return reader.getName();
        }

        @Override
        public String getLocalName() {
            return reader.getLocalName();
        }

        @Override
        public boolean hasName() {
            return reader.hasName();
        }

        @Override
        public String getNamespaceURI() {
            return reader.getNamespaceURI();
        }

        @Override
        public String getPrefix() {
            return reader.getPrefix();
        }

        @Override
        public String getVersion() {
            return reader.getVersion();
        }

        @Override
        public boolean isStandalone() {
            return reader.isStandalone();
        }

        @Override
        public boolean standaloneSet() {
            return reader.standaloneSet();
        }

        @Override
        public String getCharacterEncodingScheme() {
            return reader.getCharacterEncodingScheme();
        }

        @Override
        public String getPITarget() {
            return reader.getPITarget();
        }

        @Override
        public String getPIData() {
            return reader.getPIData();
        }
        
    }
    
}

