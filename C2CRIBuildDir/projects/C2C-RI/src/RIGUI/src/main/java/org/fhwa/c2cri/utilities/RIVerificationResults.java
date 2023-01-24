/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.utilities;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Class RIVerificationResults captures the results of the testing and stores is stored in the log file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
@XmlRootElement(name = "verification-results")
@XmlAccessorType(XmlAccessType.FIELD)
public class RIVerificationResults {

    /** The verification results. */
    @XmlElement(name = "verification")
    private List<RIVerification> verificationResults = new ArrayList<RIVerification>();
    
    /** The test case. */
    @XmlAttribute
    private String testCase;
    
    /** The test step. */
    @XmlAttribute
    private String testStep;

    /**
     * Adds the verification.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param thisVerification the this verification
     */
    public void addVerification(RIVerification thisVerification) {
        verificationResults.add(thisVerification);
    }

    /**
     * Gets the verification results.
     *
     * @return the verification results
     */
    public List<RIVerification> getVerificationResults() {
        return verificationResults;
    }

    /**
     * Gets the test case.
     *
     * @return the test case
     */
    @XmlTransient
    public String getTestCase() {
        return testCase;
    }

    /**
     * Sets the test case.
     *
     * @param testCase the new test case
     */
    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    /**
     * Gets the test step.
     *
     * @return the test step
     */
    @XmlTransient
    public String getTestStep() {
        return testStep;
    }

    /**
     * Sets the test step.
     *
     * @param testStep the new test step
     */
    public void setTestStep(String testStep) {
        this.testStep = testStep;
    }

    /**
     * Converts the VerificationResults into a form that can be written to and read from the Test Log.
     *
     * @return the string
     */
    public String to_LogFormat() {

        String logOutput = "";
        try {
            // =============================================================================================================
            // Setup JAXB
            // =============================================================================================================

            // Create a JAXB context passing in the class of the object we want to marshal/unmarshal
            final JAXBContext context = JAXBContext.newInstance(RIVerificationResults.class);

            // =============================================================================================================
            // Marshalling OBJECT to XML
            // =============================================================================================================

            // Create the marshaller, this is the nifty little thing that will actually transform the object into XML
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            // Create a stringWriter to hold the XML
            final StringWriter stringWriter = new StringWriter();


            // Marshal the javaObject and write the XML to the stringWriter
            marshaller.marshal(this, stringWriter);

            // Print out the contents of the stringWriter
            logOutput = stringWriter.toString();
//            logOutput.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>", "");
            return logOutput;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Verification Results Conversion Error - " + ex.getMessage();
        }
    }
}
