/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.fhwa.c2cri.testmodel.verification.MessageResults;

/**
 * The Class TMDDMessageTester test messages against the defined TMDD schemas.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TMDDMessageTester {

    /**
     * The transformer.
     */
    private static Transformer transformer;

    /**
     * The tmdd tester.
     */
    private static TMDDMessageTester tmddTester;

    /**
     * The url for the Message Verification xsl file to use.
     */
    private static URL resourcePath;

    /**
     * Gets the single instance of TMDDMessageTester.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return single instance of TMDDMessageTester
     */
    public static TMDDMessageTester getInstance(URL xslResourcePath) {
        if (tmddTester == null) {
            tmddTester = new TMDDMessageTester(xslResourcePath);
        }
        tmddTester.resourcePath = xslResourcePath;
        return tmddTester;
    }

        /**
     * Gets the single instance of TMDDMessageTester.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return single instance of TMDDMessageTester
     */
    public static TMDDMessageTester getInstance() {
        if (tmddTester == null) {
            tmddTester = new TMDDMessageTester(resourcePath);
        }
        return tmddTester;
    }
    
    /**
     * Instantiates a new tMDD message tester.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    private TMDDMessageTester(URL xslResourcePath) {
//        URL xslURL = this.getClass().getResource(xslResourcePath);        
        resourcePath = xslResourcePath;
        try {
            TransformerFactory xslf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
            transformer = xslf.newTransformer(new StreamSource(xslResourcePath.openStream()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Test message.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param path the path
     * @param fileName the file name
     * @return the message results
     * @throws Exception the exception
     */
    public MessageResults testMessage(String path, String fileName) throws Exception {
        long start = System.currentTimeMillis();
        MessageResults results;
        try ( FileInputStream fis = new FileInputStream(path + "\\" + fileName)) {
            StringWriter output = new StringWriter();
            transformer.transform(new StreamSource(fis), new StreamResult(output));
            results = new MessageResults(output.toString());
            long finish = System.currentTimeMillis();
            System.out.println("Delta Time: " + (finish - start) + " ms");
            for (String result : results.getAllResults()) {
                System.err.println(result);
            }
        }
        return results;

    }

    /**
     * Test message.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param message the message
     * @return the message results
     * @throws Exception the exception
     */
    public MessageResults testMessage(byte[] message) throws Exception {
        URL xslURL = resourcePath;
        TransformerFactory xslf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
        Transformer transformer = xslf.newTransformer(new StreamSource(xslURL.openStream()));
        MessageResults results;
        try ( ByteArrayInputStream bais = new ByteArrayInputStream(message)) {
            long start = System.currentTimeMillis();
            StringWriter output = new StringWriter();
            transformer.transform(new StreamSource(bais), new StreamResult(output));
            results = new MessageResults(output.toString());
            long finish = System.currentTimeMillis();
            System.out.println("Delta Time: " + (finish - start) + " ms");
        }
        return results;

    }

    /**
     * Test message.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param message the message
     * @return the message results
     * @throws Exception the exception
     */
    public MessageResults testMessage(InputStream message) throws Exception {
        URL xslURL = resourcePath;
        TransformerFactory xslf = TransformerFactory.newInstance("net.sf.saxon.TransformerFactoryImpl", null);
        Transformer transformer = xslf.newTransformer(new StreamSource(xslURL.openStream()));
        MessageResults results;
        long start = System.currentTimeMillis();
        StringWriter output = new StringWriter();
        transformer.transform(new StreamSource(message), new StreamResult(output));
        results = new MessageResults(output.toString());
        long finish = System.currentTimeMillis();
        System.out.println("Delta Time: " + (finish - start) + " ms");

        return results;

    }

}
