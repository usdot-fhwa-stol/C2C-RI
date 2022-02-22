/*
Jameleon C2C RI plug-in - A plug-in that is used to perform C2C Conformance
Testing
 */
package org.fhwa.c2cri.plugin.c2cri;

import java.util.ArrayList;
import javax.swing.JOptionPane;

import net.sf.jameleon.SessionTag;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.applayer.ApplicationLayerStandardFactory;
import org.fhwa.c2cri.infolayer.InformationLayerStandard;
import org.fhwa.c2cri.infolayer.InformationLayerStandardFactory;
import org.fhwa.c2cri.messagemanager.MessageManager;
import test.transports.ListenerManager;

/**
 * A Session tag for the C2CRI plug-in.
 * 
 * @author TransCore ITS, LLC
 * Last Updated:  11/10/2012
 * 
 * An example of its use might:
 * 
 * <pre><source>
 * &lt;testcase xmlns="jelly:jameleon"&gt;
 *     &lt;C2CRI-session infoStd="tmdd-v3.0" beginSession="true"&gt;
 *       &lt;C2CRI-assert-element
 *           functionId="Check that the data element is the input value."
 *           element="Center Name" value = "${TMC_Name}"/&gt;
 *     &lt;/C2CRI-session&gt;
 * &lt;/testcase&gt;
 * </source></pre>
 *
 * @jameleon.function name="RI-session"
 */
public class RISessionTag extends SessionTag {

    /**
     * @jameleon.attribute contextName="infoStd"
     */
    protected String infoStd;
    /**
     * @jameleon.attribute contextName="appStd"
     */
    protected String appStd;
    /**
     * If provided, the name of the request dialog that will implemented by this session.
     * @jameleon.attribute contextName="requestDialog"
     */
    protected String requestDialog;
    /**
     * If provided, the name of the subscription dialog that will implemented by this session.
     * @jameleon.attribute contextName="subscriptionDialog"
     */
    protected String subscriptionDialog;
    /**
     * If provided, the name of the publication dialog that will implemented by this session.
     * @jameleon.attribute contextName="publicationDialog"
     */
    protected String publicationDialog;
    /**
     * @jameleon.attribute contextName="lclPort"
     */
    protected String lclPort;
    /**
     * @jameleon.attribute contextName="lclAddress"
     */
    protected String lclAddress;
    /**
     * @jameleon.attribute contextName="SUTPort"
     */
    protected String sutPort;
    /**
     * @jameleon.attribute contextName="SUTAddress"
     */
    protected String sutAddress;
    /**
     * @jameleon.attribute contextName="RIMode"
     */
    protected String riMode;
    /**
     * @jameleon.attribute contextName="RI_WEBSERVICEURL"
     */
    protected String wsdlURL;
    /**
     * @jameleon.attribute contextName="INFOSTD_SUITEURL"
     */
    protected String infoStdSuiteURL;
    /**
     * @jameleon.attribute contextName="APPSTD_SUITEURL"
     */
    protected String appStdSuiteURL;
    /**
     * Flag indicating whether the service will be defined from a WSDL file.
     *
     * @jameleon.attribute 
     */
    protected boolean useWSDL = true;
    /**
     * The service name to be tested.  Must match the WSDL definition
     * @jameleon.attribute
     */
    protected String serviceName;
    /**
     * The port name of the service to be tested.  Must match the WSDL definition
     * @jameleon.attribute
     */
    protected String portName;
    /**
     * The operation name of the portType name of the service to be tested.  Must match the WSDL definition
     * @jameleon.attribute
     */
    protected String operationName;
    /**
     * Flag indicating whether the operation specified is a publication.
     *
     * @jameleon.attribute
     */
    protected boolean isPublicationOperation = false;
    /**
     * The transport type that will be used for this service. Only applied when WSDL not used.
     * @jameleon.attribute
     */
    protected String transportType;
    /**
     * The encoding type that will be used for this service. Only applied when WSDL not used.
     * @jameleon.attribute
     */
    protected String encodingType;
    /**
     * A unique identifier for this test case.
     *
     * @jameleon.attribute required="true"
     */
    protected String testCaseIdentifier;
    /**
     * A user name to be applied.
     *
     * @jameleon.attribute
     */
    protected String userName = "";
    /**
     * A password to be applied.
     *
     * @jameleon.attribute
     */
    protected String password = "";
    /**
     * A handle for the C2C Center Client
     */
//    protected C2CClient externalCenter;
    /**
     * A handle for the C2C messages
     */
    protected ArrayList messageList;
    /**
     * the Service object managed by this Session
     */
    private ApplicationLayerStandard theApplicationLayerStandard;
    private InformationLayerStandard theInformationLayerStandard;
    /**
     * Gets the information Layer Standard.
     * 
     * @return the information layer standard selected.
     */
    public String getInfoStd() {
        return infoStd;
    }

    /**
     * Gets the port to be used for the RI
     * 
     * @return the port to be used for the RI.
     */
    public int getlclPort() {
        return Integer.parseInt(lclPort);
    }

    /**
     * Gets the address to be used for the RI
     * 
     * @return the address to be used for the RI.
     */
    public String getlclAddress() {
        return lclAddress;
    }

    /**
     * Gets the port to be used for the SUT
     * 
     * @return the port to be used for the SUT.
     */
    public int getSUTPort() {
        return Integer.parseInt(sutPort);
    }

    /**
     * Gets the address to be used for the SUT
     * 
     * @return the address to be used for the SUT.
     */
    public String getSUTAddress() {
        return sutAddress;
    }

    /**
     * Gets the application Layer Standard 
     * 
     * @return the application layer standard selected.
     */
    public String getAppStd() {
        return appStd;
    }

    /**
     * Gets the information layer standard to request
     * 
     * @return The info standard to use in startApplication.
     */
    protected String getRequestInfoStd() {
        String info = getInfoStd();
        return info;
    }

    /**
     * Gets the application layer standard to request
     * 
     * @return The application layer standard to use in startApplication.
     */
    protected String getRequestAppStd() {
        String app = getAppStd();
        return app;
    }

    
    @Override
    public void setUpSession() {
        if ((getAppStd() != null)&&(getInfoStd()!=null)) {


            if (this.getParent() instanceof TestCaseTag){
                TestCaseTag testCaseTag = (TestCaseTag)this.getParent();
                String testCaseID = testCaseTag.getTestCase().getTestCaseId();
                MessageManager.getInstance().setParentTestCase(testCaseID);
                ListenerManager.getInstance().setTestCaseID(testCaseID);
            }

            try {

                ApplicationLayerStandardFactory theStandardFactory = ApplicationLayerStandardFactory.getInstance();
                theStandardFactory.setApplicationLayerStandard(appStd);
                theStandardFactory.setCenterMode(riMode);
                theStandardFactory.setInformationLayerStandard(infoStd);
                theStandardFactory.setTestCase(testCaseIdentifier);
                theStandardFactory.setRequestDialog(requestDialog);
                theStandardFactory.setSubscriptionDialog(subscriptionDialog);
                theStandardFactory.setPublicationDialog(publicationDialog);
                log.debug("The wsdlURL file is "+wsdlURL);
                theStandardFactory.setTestConfigSpecificationURL(wsdlURL);
                theStandardFactory.setTestSuiteSpecificationURL(wsdlURL);

                try {
                    theApplicationLayerStandard = theStandardFactory.getApplicationStandard();
                    InformationLayerStandardFactory theInfoStandardFactory = InformationLayerStandardFactory.getInstance();
                    theInfoStandardFactory.setApplicationLayerStandard(theApplicationLayerStandard);
                    theInfoStandardFactory.setCenterMode(riMode);
                    theInfoStandardFactory.setInformationLayerStandardName(infoStd);
                    theInfoStandardFactory.setTestCase(testCaseIdentifier);
                    theInformationLayerStandard = theInfoStandardFactory.getInformationStandard();   
                } catch (Exception ex) {
                    ex.printStackTrace();
                    log.debug("*C2CRISessionTag: Error Creating Application Layer Standard ->" + ex.getMessage());
                    throw new JameleonScriptException("*C2CRISessionTag: Error Creating Application Layer Standard ->" + ex.getMessage(), this);

                }

                MessageManager theManager = MessageManager.getInstance();
                theManager.setParentTestCase(testCaseIdentifier);

            } catch (Exception ex) {
                log.debug("*C2CRISessionTag: Error processing WSDL ->" + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Script Error: \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new JameleonScriptException("*C2CRISessionTag: Error processing WSDL ->" + ex.getMessage(), this);
            }


        } else if ((getAppStd() != null) && (!getAppStd().toUpperCase().contains("NTCIP2306"))) {
            JOptionPane.showMessageDialog(null, "Script Error: \n" + "'appStd' " + getAppStd() + " is not yet supported by the C2C RI", "Error", JOptionPane.ERROR_MESSAGE);
            throw new JameleonScriptException("'appStd' " + getAppStd() + " is not yet supported by the C2C RI", this);
        }

    }

    @Override
    public void tearDownSession() {
        try {
            log.debug("C2CRISessionTag.tearDownSession: About to try and clear out the service!");
            if (theInformationLayerStandard != null){
                theInformationLayerStandard.stopServices();
                theInformationLayerStandard = null;
            }
             if (theApplicationLayerStandard != null){
                theApplicationLayerStandard.stopServices();
                theApplicationLayerStandard = null;
            }
        } catch (Exception ex) {
            throw new JameleonScriptException("Error tearing down C2CRISessionTag.  Could not shutdown the Transport. /n" + ex.getMessage(), this);
        }
    }

    /**
     * Ensures that things are started off correctly.
     */
    @Override
    public void startApplication() {
    }


    public ApplicationLayerStandard getTheApplicationLayerStandard() {
        return theApplicationLayerStandard;
    }

    public InformationLayerStandard getTheInformationLayerStandard() {
        return theInformationLayerStandard;
    }

    public String getOperationName() {
        if (operationName != null) {
            return operationName;
        } else {
            return "None Specified";
        }
    }
}
