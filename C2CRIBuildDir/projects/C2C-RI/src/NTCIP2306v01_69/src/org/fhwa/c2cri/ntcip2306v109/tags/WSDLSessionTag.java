/*
Jameleon C2C RI plug-in - A plug-in that is used to perform C2C Conformance
Testing
 */
package org.fhwa.c2cri.ntcip2306v109.tags;

import java.util.List;

import net.sf.jameleon.SessionTag;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;
import org.fhwa.c2cri.ntcip2306v109.wsdl.verification.WSDLTestFactory;

/**
 * A Session tag for the C2CRI plug-in.  This Session is geared towards the testing
 * of WSDL files.
 * 
 * An example of its use might:
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *     &lt;WSDL-session infoStd="tmdd-v3.0" beginSession="true"&gt;
 *       &lt;C2CRI-assert-element
 *           functionId="Check that the data element is the input value."
 *           element="Center Name" value = "${TMC_Name}"/&gt;
 *     &lt;/WSDL-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 *
 * Its primary purpose is to store information that needs to be available and shared between various tags.
 * The key information is a WSDL representation, along with test assertion results from verification tests.
 *
 * @jameleon.function name="WSDL-session"
 */
public class WSDLSessionTag extends SessionTag {

    /**
     * The instantiated and stored representation of the WSDL
     */
    private RIWSDL riWSDLInstance;

    /**
     *
     */
    private WSDLTestFactory riWSDLTestFactory;
    /**
     * @jameleon.attribute contextName="RI_WEBSERVICEURL"
     */
    protected String wsdlURL;

    /**
     * @jameleon.attribute
     */
    protected String wsdlFile;


    /**
     * Gets the URL of the WSDL file to be tested.
     *
     * @return the URL of the WSDL file to be tested as a string.
     */
    public String getWsdlURL() {
        return wsdlURL;
    }


    /**
     *
     * @return a flag indicating whether the associated Schema documents were found
     */
    public boolean isWSDLSchemaDocumentsExist() {
        return riWSDLInstance.isSchemaDocumentsExist();
    }

    /**
     *
     * @return a flag indicating whether the WSDL file was successfully validated against the W3C WSDL Schema
     */
    public boolean isWSDLValidatedAgainstSchema() {
        return riWSDLInstance.isWsdlValidatedAgainstSchema();
    }


    /**
     *
     * @return the list of errors discovered when validating the WSDL against the W3C WSDL Schema
     */
    public List<String> getWsdlError() {
        return riWSDLInstance.getWsdlErrors();

    }

    /**
     * Gets the ri wsdl instance.
     *
     * @return the ri wsdl instance
     */
    public RIWSDL getRiWSDLInstance() {
        return riWSDLInstance;
    }

    /**
     * Gets the ri wsdl tester factory.
     *
     * @return the ri wsdl tester factory
     */
    public WSDLTestFactory getRiWSDLTesterFactory() {
        return riWSDLTestFactory;
    }

    /**
     * Sets the up session.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    @Override
    public void setUpSession() {
        // If the script specifies the wsdlFile then use it.  Otherwise use the wsdlURL value which is
        // set by default to a context value.
            String urlValue;
            if ((wsdlFile != null)&&(!wsdlFile.equals(""))){
                urlValue = wsdlFile;
            } else {
                urlValue = wsdlURL;
            }

        try {
            riWSDLInstance = new RIWSDL(urlValue);
            System.out.println("End Result:  schemasExist= " + riWSDLInstance.isSchemaDocumentsExist() + "  isValid = " + riWSDLInstance.isWsdlValidatedAgainstSchema());
            if (!riWSDLInstance.isSchemaDocumentsExist() || !riWSDLInstance.isWsdlValidatedAgainstSchema()) {
                List<String> wsdlResults = riWSDLInstance.getWsdlErrors();
                for (String wsdlError : wsdlResults) {
                    log.warn("WSDL Error:   " + wsdlError);
                }
            }
            riWSDLTestFactory = new WSDLTestFactory(riWSDLInstance,urlValue);

        } catch (Exception ex) {
            throw new JameleonScriptException("Error Setting up the WSDL Session " + ex.getMessage(), this);
        }

    }

    @Override
    public void tearDownSession() {
        riWSDLInstance.clear();
        riWSDLInstance = null;
        riWSDLTestFactory.clear();
        riWSDLTestFactory = null;
    }

    /**
     * Ensures that things are started off correctly.
     */
    @Override
    public void startApplication() {
		// original implementation was empty


    }
}
