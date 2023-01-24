/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel.verification;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * The Class XSLTransformer provides methods that can be used to transform a xml formatted message using a given xsl document.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class XSLTransformer {

    /** The ri transformer. */
    private static XSLTransformer riTransformer = null;
    
    /** The tfactory. */
    private static TransformerFactory tfactory;
    
    /**
     * Gets the single instance of XSLTransformer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of XSLTransformer
     */
    public static XSLTransformer getInstance(){
        if (riTransformer == null) riTransformer = new XSLTransformer();
        return riTransformer;
    }

    /**
     * Instantiates a new xSL transformer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private XSLTransformer(){
                System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
        // Create a transform factory instance.

        tfactory = TransformerFactory.newInstance();
    }

    /**
     * Xsl transform.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param sourceFileName the source file name
     * @param xslFileName the xsl file name
     * @param parameters the parameters
     * @return the string
     * @throws TransformerException the transformer exception
     * @throws TransformerConfigurationException the transformer configuration exception
     */
    public String xslTransform(String sourceFileName, String xslFileName, Map parameters)
            throws TransformerException, TransformerConfigurationException {


        // Create a transformer for the stylesheet.

        Transformer transformer = tfactory.newTransformer(new StreamSource(new File(xslFileName)));
        Iterator parameterKeysIterator = parameters.keySet().iterator();
        while (parameterKeysIterator.hasNext()){
            String thisKey = (String)parameterKeysIterator.next();
            transformer.setParameter(thisKey, parameters.get(thisKey));
        }

        // Transform the source XML to System.out.

        transformer.transform(new StreamSource(new File(sourceFileName)),
                new StreamResult(System.out));

        StringWriter sw = new StringWriter();
        transformer.transform(new StreamSource(new File(sourceFileName)),
                new StreamResult(sw));
        String results = sw.toString();
//        HashMap<String, TestAssertion> results = TestAssertionResults.getTestAssertionResults(sw.toString());
        return results;
    }

    /**
     * Xsl transform.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param sourceFileName the source file name
     * @param xslFileName the xsl file name
     * @param parameters the parameters
     * @return the string
     * @throws TransformerException the transformer exception
     * @throws TransformerConfigurationException the transformer configuration exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String xslTransform(URL sourceFileName, URL xslFileName,Map parameters)
            throws TransformerException, TransformerConfigurationException, IOException {

 
        // Create a transformer for the stylesheet.
        Transformer transformer = tfactory.newTransformer(new StreamSource(xslFileName.toString()));
        Iterator parameterKeysIterator = parameters.keySet().iterator();
        while (parameterKeysIterator.hasNext()){
            String thisKey = (String)parameterKeysIterator.next();
            transformer.setParameter(thisKey, parameters.get(thisKey));            
        }
        // Transform the source XML to System.out.


        StringWriter sw = new StringWriter();
        transformer.transform(new StreamSource(sourceFileName.openStream()),
                new StreamResult(sw));
        String results = sw.toString();
        sw.close();

        return results;
    }

    /**
     * Xsl transform.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param sourceStream the source stream
     * @param xslFileName the xsl file name
     * @param parameters the parameters
     * @return the string
     * @throws TransformerException the transformer exception
     * @throws TransformerConfigurationException the transformer configuration exception
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public String xslTransform(InputStream sourceStream, URL xslFileName, Map parameters)
            throws TransformerException, TransformerConfigurationException, IOException {


        // Create a transformer for the stylesheet.
        Transformer transformer = tfactory.newTransformer(new StreamSource(xslFileName.toString()));
        Iterator parameterKeysIterator = parameters.keySet().iterator();
        while (parameterKeysIterator.hasNext()){
            String thisKey = (String)parameterKeysIterator.next();
            transformer.setParameter(thisKey, parameters.get(thisKey));
        }

        // Transform the source XML to System.out.


        StringWriter sw = new StringWriter();
        transformer.transform(new StreamSource(sourceStream),
                new StreamResult(sw));
        String results = sw.toString();
        return results;
    }

}
