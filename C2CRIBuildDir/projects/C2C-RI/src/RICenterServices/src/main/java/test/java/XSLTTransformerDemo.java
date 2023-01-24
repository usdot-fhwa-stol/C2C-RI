/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;
import org.fhwa.c2cri.testmodel.verification.TestAssertionResults;
import org.fhwa.c2cri.testmodel.verification.XSLTransformer;

/**
 *
 * @author TransCore ITS
 */
public class XSLTTransformerDemo {

    public static void main(String args[]) {

// set the TransformFactory to use the Saxon TransformerFactoryImpl method

 //       System.setProperty("javax.xml.transform.TransformerFactory",
 //               "net.sf.saxon.TransformerFactoryImpl");

//        	ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
//		Thread.currentThread().setContextClassLoader( XSLTTransformerDemo.class.getClassLoader() );

//		InputStream inputXslStream = XSLTTransformerDemo.class.getResourceAsStream("/org/fhwa/c2cri/applayer/ntcip2306/verification/v01_69/tadriver.xsl");
 //               URL inputXslURL = XSLTTransformerDemo.class.getResource("/org/fhwa/c2cri/applayer/ntcip2306/verification/v01_69/tadriver.xsl");
//		Thread.currentThread().setContextClassLoader( contextClassLoader );

//        String baseTestSuitePath = "jar:file:/c:/inout/tmp/TestAssertions.jar!";
//        String foo_xml = "/TestAssertions/TMDD-wec.wsdl"; //input xml
//
//        String foo_xsl = "/TestAssertions/WSDLVerify.xsl"; //input xsl

        String baseTestSuitePath = "file:/c:/tmp/TMDDTestAssertions";
//        String foo_xml = "/release3.wsdl"; //input xml
        String foo_xml = "/TMDD-wec.wsdl"; //input xml

        String foo_xsl = "/WSDLVerify.xsl"; //input xsl


        Map parameterMap = new HashMap<String,String>();
        parameterMap.put("validwsdl", "true");
        parameterMap.put("validwsdldeclaraion", "true");
        parameterMap.put("IsNOTUSEDATALL", true);


        try {
            URL inputURL = new URL("file:/c:/c2cri/testfiles/tmddv301/tmdd-fixed.wsdl");
            URL inputXslURL = new URL(baseTestSuitePath+foo_xsl);
//            myTransformer(foo_xml, foo_xsl);
            XSLTransformer transformer = XSLTransformer.getInstance();
//            String transformResults = transformer.xslTransform(foo_xml, foo_xsl);
            System.out.println(Calendar.getInstance().getTimeInMillis());
            String transformResults = transformer.xslTransform(inputURL, inputXslURL, parameterMap);
 //           String transformResults = transformer.xslTransform(inputURL.openStream(), inputXslURL, parameterMap);
            System.out.println(Calendar.getInstance().getTimeInMillis());

            HashMap<String, TestAssertion> results = TestAssertionResults.getTestAssertionResults(transformResults);
            if (!(results == null)) {
                Collection c = results.values();
                //obtain an Iterator for Collection
                Iterator itr = c.iterator();
                //iterate through HashMap values iterator
                System.out.println();
                while (itr.hasNext()) {
                    TestAssertion thisOne = (TestAssertion) itr.next();
                    String failureText = thisOne.getTestResult().equals("Failed") ? thisOne.getTestResultDescription():"";
                    System.out.println("ID = " + thisOne.getTestAssertionID()
                            + "  Prescription = " + thisOne.getTestAssertionPrescription()
                            + "  Result = " + thisOne.getTestResult()
                            + "  Description = " + failureText);
                }
            }

        } catch (Exception ex) {

            handleException(ex);

        }

        System.out.println("FOR REAL DEMO ************");
        testTransform();
    }


    private static void testTransform(){
        String level = "predefined";
//        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
//        Thread.currentThread().setContextClassLoader(WSDLSessionTag.class.getClassLoader());
//        Thread.currentThread().setContextClassLoader(contextClassLoader);


        String wsdlVerificationFilePath;

        String foo_xml = "file:/c:/c2cri/testfiles/tmddv301/tmdd-fixed.wsdl"; //input xml

//        String foo_xsl = "/TestAssertions/WSDLVerify.xsl"; //input xsl

        Map parameterMap = new HashMap<String, String>();
        parameterMap.put("validwsdl", "true");
        parameterMap.put("validwsdldeclaraion", "false");

        if (level.equalsIgnoreCase("predefined")) {
            wsdlVerificationFilePath = foo_xml;
        } else {
            wsdlVerificationFilePath = foo_xml;
        }

        try {
            URL inputURL = new URL(foo_xml);
            URL inputXslURL;
            if (level.equalsIgnoreCase("predefined")){
               inputXslURL = XSLTTransformerDemo.class.getResource("/org/fhwa/c2cri/applayer/ntcip2306/verification/v01_69/WSDLVerify.xsl");
               System.out.println("Internal URL: "+inputXslURL.toString());
 //              inputXslURL = new URL("file:/C:/projects/Release2/projects/C2C-RI/src/RICenterServices/src/org/fhwa/c2cri/applayer/ntcip2306/verification/v01_69/WSDLVerify.xsl");
            } else {
               inputXslURL = new URL(wsdlVerificationFilePath);
            }

//            myTransformer(foo_xml, foo_xsl);
            XSLTransformer transformer = XSLTransformer.getInstance();
//            String transformResults = transformer.xslTransform(foo_xml, foo_xsl);
            System.out.println(Calendar.getInstance().getTimeInMillis());
            String transformResults = transformer.xslTransform(inputURL, inputXslURL, parameterMap);
            //           String transformResults = transformer.xslTransform(inputURL.openStream(), inputXslURL, parameterMap);
            System.out.println(Calendar.getInstance().getTimeInMillis());

            HashMap<String, TestAssertion> results = TestAssertionResults.getTestAssertionResults(transformResults);
            if (!(results == null)) {
                Collection c = results.values();
                //obtain an Iterator for Collection
                Iterator itr = c.iterator();
                //iterate through HashMap values iterator
                System.out.println();
                while (itr.hasNext()) {
                    TestAssertion thisOne = (TestAssertion) itr.next();
                    String failureText = thisOne.getTestResult().equals("Failed") ? thisOne.getTestResultDescription():"";
                    System.out.println("ID = " + thisOne.getTestAssertionID()
                            + "  Prescription = " + thisOne.getTestAssertionPrescription()
                            + "  Result = " + thisOne.getTestResult()
                            + "  Description = " + failureText);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    private static void handleException(Exception ex) {

        System.out.println("EXCEPTION: " + ex);

        ex.printStackTrace();

    }
}
