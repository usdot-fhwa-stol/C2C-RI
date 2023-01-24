/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.wsdl.verification;

import java.net.URL;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.testmodel.verification.TestAssertion;
import org.fhwa.c2cri.utilities.RIParameters;


/**
 * The Class WSDLTest_9_1_2_1.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class WSDLTest_9_1_2_1 implements WSDLTest {

    /** The wsdl spec. */
    RIWSDL wsdlSpec;
    
    /** The wsdl url. */
    String wsdlURL;

    /**
     * Instantiates a new wSDL test_9_1_2_1.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param wsdlSpec the wsdl spec
     * @param wsdlURL the wsdl url
     */
    public WSDLTest_9_1_2_1(RIWSDL wsdlSpec, String wsdlURL) {
        this.wsdlSpec = wsdlSpec;
        this.wsdlURL = wsdlURL;
    }

    /**
     * Perform.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    @Override
    public TestAssertion perform() {
        TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("9.1.2.1");


        TestAssertion results7112 = perform7112();

        TestAssertion results7114 = perform7114();





        if (results7112.getTestResult().equals("Passed")
                && results7114.getTestResult().equals("Passed")) {
            assertion.setTestResult("Passed");
            assertion.setTestResultDescription("");

        } else {
            String failureResult = "";
            assertion.setTestResult("Failed");
            boolean allFailuresSetForOverride = true;

            if (results7112.getTestResult().equals("Failed")) {
                failureResult = failureResult.concat("7.1.3.1: " + results7112.getTestResultDescription() + "\n");
                allFailuresSetForOverride = results7112.isContinueAfterFailure() & allFailuresSetForOverride;
            }

            if (results7114.getTestResult().equals("Failed")) {
                failureResult = failureResult.concat(results7114.getTestAssertionID() + ": " + results7114.getTestResultDescription() + "\n");
                allFailuresSetForOverride = results7114.isContinueAfterFailure() & allFailuresSetForOverride;
            }

            assertion.setContinueAfterFailure(allFailuresSetForOverride);
            assertion.setTestResultDescription(failureResult);
        }
        assertion.setTestAssertionPrescription("The operation name and output message follow the same rules as those defined for a SOAP Service portType. ");
        return assertion;
    }


    /**
     * Perform7112.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    public TestAssertion perform7112() {
        TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("7.1.1.2");
        try {
            ArrayList<String> ftpOperations = wsdlSpec.getFTPServiceOperations();

            URL wsdlURL = new URL(wsdlSpec.getWsdlFileName());
            XmlObject xmlObj = XmlObject.Factory.parse(wsdlURL);
            XmlCursor xmlc = xmlObj.newCursor();

            String xqNamespace =
                    "declare namespace xsl='http://www.w3.org/1999/XSL/Transform'; "
                    + "declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; "
                    + "declare namespace xsd='http://www.w3.org/2001/XMLSchema'; "
                    + "declare namespace wsdl='http://schemas.xmlsoap.org/wsdl/'; "
                    + "declare namespace soap11='http://schemas.xmlsoap.org/soap/envelope/'; "
                    + "declare namespace soap12='http://www.w3.org/2003/05/soap-envelope'; "
                    + "declare namespace soapenc='http://schemas.xmlsoap.org/soap/encoding/'; "
                    + "declare namespace xenc='http://www.w3.org/2001/04/xmlenc#'; "
                    + "declare namespace wsoap12='http://schemas.xmlsoap.org/wsdl/soap12/'; "
                    + "declare namespace wsoap11='http://schemas.xmlsoap.org/wsdl/soap/'; "
                    + "declare namespace soapbind='http://schemas.xmlsoap.org/wsdl/soap/'; ";

            String xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation/@name";
            xmlc.toStartDoc();
            xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);

            boolean ftpOperationFound = false;
            ArrayList<String> nonStandardOperationNames = new ArrayList<String>();
            while (xmlc.hasNextSelection()) {
                xmlc.toNextSelection();
                if (ftpOperations.contains(xmlc.getTextValue())){
                    ftpOperationFound = true;
                    if (!xmlc.getTextValue().startsWith("OP_")||(xmlc.getTextValue().length()==3)){
                        nonStandardOperationNames.add(xmlc.getTextValue());
                    }
                }
            }
            if (ftpOperationFound && nonStandardOperationNames.isEmpty()){
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");
                String nonStandardNames = "";
                for (String thisName : nonStandardOperationNames){
                    if (nonStandardNames.equals("")){
                    nonStandardNames = nonStandardNames.concat(thisName);
                    } else {
                    nonStandardNames = nonStandardNames.concat(", "+thisName);
                    }
                }
                String errorMessage = !ftpOperationFound? "No operations supported by a FTP Service were found listed under a parent portType tag.":"";
                errorMessage = errorMessage.concat(nonStandardNames.equals("")?"":"NonStandard Operation Names Found: \n"+nonStandardNames+"\n Please note that this error will not likely affect subsequent testing of Information Layer standards.");
                assertion.setTestResultDescription(errorMessage);
            }
            xmlc.dispose();


        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }
        try {
            assertion.setContinueAfterFailure(Boolean.valueOf(RIParameters.getInstance().getParameterValue(NTCIP2306Settings.NTCIP2306_TEST_SETTINGS_GROUP, NTCIP2306Settings.NTCIP2306_CONTINUE_AFTER_OP_FAILURE_PARAMETER, NTCIP2306Settings.DEFAULT_NTCIP2306_CONTINUE_AFTER_OP_FAILURE_PARAMETER_VALUE)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        assertion.setTestAssertionPrescription("The operation name shall begin with the prefix OP_ followed by a descriptive name for the operation. ");
        return assertion;
    }

    /**
     * Perform7114.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the test assertion
     */
    public TestAssertion perform7114() {
       TestAssertion assertion = new TestAssertion();
        assertion.setTestAssertionID("7.1.1.4");
        try {
            ArrayList<String> ftpOperations = wsdlSpec.getFTPServiceOperations();

            URL wsdlURL = new URL(wsdlSpec.getWsdlFileName());
            XmlObject xmlObj = XmlObject.Factory.parse(wsdlURL);
            XmlCursor xmlc = xmlObj.newCursor();

            String xqNamespace =

                    "declare namespace xsl='http://www.w3.org/1999/XSL/Transform'; "
                    + "declare namespace xsi='http://www.w3.org/2001/XMLSchema-instance'; "
                    + "declare namespace xsd='http://www.w3.org/2001/XMLSchema'; "
                    + "declare namespace wsdl='http://schemas.xmlsoap.org/wsdl/'; "
                    + "declare namespace soap11='http://schemas.xmlsoap.org/soap/envelope/'; "
                    + "declare namespace soap12='http://www.w3.org/2003/05/soap-envelope'; "
                    + "declare namespace soapenc='http://schemas.xmlsoap.org/soap/encoding/'; "
                    + "declare namespace xenc='http://www.w3.org/2001/04/xmlenc#'; "
                    + "declare namespace wsoap12='http://schemas.xmlsoap.org/wsdl/soap12/'; "
                    + "declare namespace wsoap11='http://schemas.xmlsoap.org/wsdl/soap/'; "
                    + "declare namespace soapbind='http://schemas.xmlsoap.org/wsdl/soap/'; ";

            ArrayList<String> messageList = new ArrayList<String>();
                String xPathStatement = "/wsdl:definitions/wsdl:message/@name";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);


                while (xmlc.hasNextSelection()) {
                    xmlc.toNextSelection();
                    messageList.add(xmlc.getTextValue());
            }


            ArrayList<String> outputMessageErrors = new ArrayList<String>();
            for (String thisOperation : ftpOperations) {
                xPathStatement = "/wsdl:definitions/wsdl:portType/wsdl:operation[@name=\"" + thisOperation + "\"]/wsdl:output/@message";
                xmlc.toStartDoc();
                xmlc.selectPath(xqNamespace + "$this/" + xPathStatement);


                while (xmlc.hasNextSelection()) {
                    xmlc.toNextSelection();

                    String messageName = xmlc.getTextValue();

                    if (messageName.startsWith("tns:")) {
                        String localMessageName = messageName.substring(4);
                        if (!messageList.contains(localMessageName)){
                           outputMessageErrors.add("Operation " + thisOperation+"'s output message name "+messageName+" does not match any defined messages in the message section.");

                        }


                    } else {
                        outputMessageErrors.add(thisOperation+"'s output message name "+messageName+" does not start with tns:.");

                    }

                }
            }


            if (outputMessageErrors.isEmpty()) {
                assertion.setTestResult("Passed");
                assertion.setTestResultDescription("");
            } else {
                assertion.setTestResult("Failed");
                String inputOutputParamErrors = "";
                for (String thisName : outputMessageErrors) {
                    if (inputOutputParamErrors.equals("")) {
                        inputOutputParamErrors = inputOutputParamErrors.concat(thisName);
                    } else {
                        inputOutputParamErrors = inputOutputParamErrors.concat(", " + thisName);
                    }
                }
                String errorMessage = "Messages with name errors: "+inputOutputParamErrors;
                assertion.setTestResultDescription(errorMessage);
            }
            xmlc.dispose();


        } catch (Exception ex) {
            System.out.println("Premature WSDL Test Exception.");
            ex.printStackTrace();

            assertion.setTestResult("Failed");
            assertion.setTestResultDescription(ex.getMessage());
        }
        assertion.setTestAssertionPrescription("**The output message shall reference a message defined in the Message section of the WSDL. To reference a message inside the WSDL, the message part of the operation shall begin with the prefix â€œtns:â€� followed by the name of the message specified in the Message section of the WSDL. ");
        return assertion;
    }
}
