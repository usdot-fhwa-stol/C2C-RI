/*
NTCIP2306 plug-in - A plug-in that is used to perform C2C Conformance
Testing
 */
package org.fhwa.c2cri.ntcip2306v109;

import org.fhwa.c2cri.ntcip2306v109.operations.OperationIdentifier;
import java.util.HashMap;
import javax.swing.JOptionPane;

import net.sf.jameleon.SessionTag;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.exception.JameleonScriptException;
import org.fhwa.c2cri.applayer.ListenerManager;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.Center;
import org.fhwa.c2cri.ntcip2306v109.centercontrol.CenterConfigurationController;
import org.fhwa.c2cri.ntcip2306v109.wsdl.RIWSDL;

/**
 * A Session tag for the C2CRI plug-in.
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
 * @jameleon.function name="NTCIP2306-session"
 */
public class NTCIP2306SessionTag extends SessionTag {

    /**
     * @jameleon.attribute contextName="appStd"
     */
    protected String appStd = "NTCIP2306v01_69";
    /**
     * @jameleon.attribute contextName="RIMode"
     */
    protected String riMode;
    /**
     * @jameleon.attribute contextName="RI_WEBSERVICEURL"
     */
    protected String wsdlURL;
    /**
     * A unique identifier for this test case.
     *
     * @jameleon.attribute required="true" contextName="testCaseIdentifier"
     */
    protected String testCaseIdentifier;

    /**
     * A unique identifier for this test case.
     *
     * @jameleon.attribute 
     */
    protected String subPubMapping;
    
    /**
     * the Service object managed by this Session
     */
    private static NTCIP2306Controller theService;

    private RIWSDL wsdlInstance;

    /**
     * Gets the application Layer Standard 
     * 
     * @return the application layer standard selected.
     */
    public String getAppStd() {
        return appStd;
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

    /**
     * Sets the up session.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    @Override
    public void setUpSession() {

            if (this.getParent() instanceof TestCaseTag){
                TestCaseTag testCaseTag = (TestCaseTag)this.getParent();
                String testCaseID = testCaseTag.getTestCase().getTestCaseId();
                MessageManager.getInstance().setParentTestCase(testCaseID);
                ListenerManager.getInstance().setTestCaseID(testCaseID);
            }

            try {
                
                RIWSDL theWSDL = new RIWSDL(wsdlURL);
                wsdlInstance = theWSDL;
                CenterConfigurationController ccc = new CenterConfigurationController(null, theWSDL,Center.RICENTERMODE.valueOf(riMode.toUpperCase()));

                MessageManager theManager = MessageManager.getInstance();
                theManager.setParentTestCase(testCaseIdentifier);

                if (subPubMapping == null)subPubMapping = "";
                ccc.setSubPubMapping(subPubStringMapConverter(subPubMapping,riMode.toUpperCase()));
                
                ccc.initialzeServices();
                theService = new NTCIP2306Controller(theWSDL, ccc);

            } catch (Exception ex) {
				if (ex instanceof InterruptedException)
					Thread.currentThread().interrupt();
                log.debug("*NTCIP2306SessionTag: Error Setting up the Service ->" + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Script Error: \n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                throw new JameleonScriptException("*NTCIP2306SessionTag: Error Setting up the Service ->" + ex.getMessage(), this);
            }


    }

    /**
     * Tear down session.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    @Override
    public void tearDownSession() {
        try {
            MessageManager.getInstance().clear();
            log.debug("NTCIP2306SessionTag.tearDownSession: About to try and clear out the service!");
            if (theService != null) {
               theService.shutdown();
//            theService.dialogHandler.getTransport().shutdown();
                theService = null;
            }
            System.gc();
            log.debug("NTCIP2306SessionTag.tearDownSession: The service has been torn down.!");
        } catch (Exception ex) {
            throw new JameleonScriptException("Error tearing down NTCIP2306SessionTag.  Could not shutdown the Transport. /n" + ex.getMessage(), this);
        }
    }

    /**
     * Ensures that things are started off correctly.
     */
    @Override
    public void startApplication() {
    }

    /**
     * getter for the service
     * @return the NTCIP2306 controller 
     */
    public NTCIP2306Controller getTheCenterService() {
        return theService;
    }

    /**
     * getter for the WSDL
     * @return the WSDL
     */
    public RIWSDL getWsdlInstance() {
        return wsdlInstance;
    }

    /**
     * converts a string which represents a subscription publication mapping to a map of operation identifiers
     * 
     * @param mappingString – a string representing the map between the subscription and publication operation.
     * ; Separates a set of subscription publication operation pairs.
     * : Separates the subscription from a publication operation.
     * , divides the service name port and operation names that represent a particular subscription or publication
     * @param centerMode – the mode of the RI
     * @return a map representing the subscription publication relationships
     */
    private HashMap<OperationIdentifier, OperationIdentifier> subPubStringMapConverter(String mappingString, String centerMode) {
        HashMap<OperationIdentifier, OperationIdentifier> subPubMap = new HashMap();
        String mappingPairSeparator = ";";  // Seperates a set of subscription-publication operation pairs.  Any number of pairs may be included.
        String mapOperationTypeSeparator = ":";  // Divides the Subscription operation specification from the Publication operation specification
        String mapOperationElementSeparator = ","; // Divides the servicename, port and operation names nececarry to specify a particular operation from the WSDL.

        String[] mappingPairStrings = mappingString.split(mappingPairSeparator);

        if (mappingPairStrings.length > 0) {
            String[] mapPairOperations = mappingPairStrings[0].split(mapOperationTypeSeparator);
            // 2 operations must be paired together for a valid mapping.
            if (mapPairOperations.length == 2) {
                String[] subscriptionParts = mapPairOperations[0].split(mapOperationElementSeparator);
                String[] publicationParts = mapPairOperations[1].split(mapOperationElementSeparator);

                if ((subscriptionParts.length == 3) && (publicationParts.length == 3)) {
                    OperationIdentifier subOpId = null;
                    if (centerMode.toUpperCase().contains("OC")) {
                        subOpId = new OperationIdentifier(subscriptionParts[0], subscriptionParts[1], subscriptionParts[2],
                                OperationIdentifier.SOURCETYPE.HANDLER);

                    } else {
                        subOpId = new OperationIdentifier(subscriptionParts[0], subscriptionParts[1], subscriptionParts[2],
                                OperationIdentifier.SOURCETYPE.LISTENER);

                    }
                    OperationIdentifier pubOpId = new OperationIdentifier(publicationParts[0], publicationParts[1], publicationParts[2],
                            OperationIdentifier.SOURCETYPE.LISTENER);

                    subPubMap.put(subOpId, pubOpId);
                }
            }


        }

        return subPubMap;

    }    

}
