/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.net.URL;
import java.util.ArrayList;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidator;
import org.fhwa.c2cri.ntcip2306v109.encoding.XMLValidatorFactory;
import org.fhwa.c2cri.ntcip2306v109.utilities.XMLFileReader;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;

/**
 * The Class XMLSchemaValidatorTestTool.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class XMLSchemaValidatorTestTool {

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        RIWSDL theWSDL = new RIWSDL("file:/C:/Temp/TMDDSchemasv301d6/temp-tmdd.wsdl");
        ArrayList schemaList = theWSDL.getWsdlSchemaNodeSectionsAndURLs();
        URL soapEnvURL = XMLSchemaValidatorTestTool.class.getResource("/org/fhwa/c2cri/ntcip2306v109/soap/Soap-Envelope.xsd");
        URL soapEncURL = XMLSchemaValidatorTestTool.class.getResource("/org/fhwa/c2cri/ntcip2306v109/soap/soapEncoding.xsd");
        schemaList.add(soapEnvURL.toString());
        schemaList.add(soapEncURL.toString());

        XMLValidatorFactory xf = new XMLValidatorFactory();
        xf.configure(schemaList);
        XMLValidator xmlV = xf.newXMLValidator();

        long totalTime = 0;
        int numberOfRuns = 100;
        for (int jj = 0; jj < numberOfRuns; jj++) {
            long startTime = System.nanoTime();
            boolean result = xmlV.isXMLValidatedToSchema(new String(XMLFileReader.readFile("C:\\temp\\DetectorInventoryMsgLong.xml")));
            long elapsedTime = System.nanoTime() - startTime;
            System.out.println("\n\nXML is valid to Schema? " + result + "\n  Validation Time Duration (ms) = " + elapsedTime / 1000000);
            totalTime = totalTime + elapsedTime;
            for (String thisone : xmlV.getErrors()) {
                System.out.println(thisone);
            }
        }
        System.out.println("\n\n The average validation time was " + (totalTime / 1000000) / numberOfRuns + " ms.");
    }
}
