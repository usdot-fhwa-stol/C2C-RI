/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.applayer;

import java.net.URL;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.fhwa.c2cri.center.CenterMonitor;


/**
 * A factory for creating ApplicationLayerStandard objects.
 * 
 * @author TransCore ITS, LLC
 * Last Update:  12/13/2012
 */
public class ApplicationLayerStandardFactory {
    
    /** this factory. */
    private static ApplicationLayerStandardFactory thisFactory;
    
    /** The center mode. */
    private String centerMode;
    
    /** The test case. */
    private String testCase;
    
    /** The application layer standard. */
    private String applicationLayerStandard;
    
    /** The information layer standard. */
    private String informationLayerStandard;
    
    /** The request dialog. */
    private String requestDialog;
    
    /** The subscription dialog. */
    private String subscriptionDialog;
    
    /** The publication dialog. */
    private String publicationDialog;
    
    /** The test config specification url. */
    private String testConfigSpecificationURL;
    
    /** The test suite specification url. */
    private String testSuiteSpecificationURL;
    
    /** The log. */
    protected static Logger log = LogManager.getLogger(ApplicationLayerStandardFactory.class.getName());



    /**
     * Instantiates a new application layer standard factory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private ApplicationLayerStandardFactory(){
        
    }
    
    /**
     * Gets the single instance of ApplicationLayerStandardFactory.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return single instance of ApplicationLayerStandardFactory
     */
    public static ApplicationLayerStandardFactory getInstance(){
        if (thisFactory == null){
            thisFactory = new ApplicationLayerStandardFactory();
        }
        return thisFactory;
    }
    
    
    /**
     * Gets the application standard.
     *
     * @return the application standard
     * @throws Exception the exception
     */
    public ApplicationLayerStandard getApplicationStandard() throws Exception{

        if (applicationLayerStandard == null) throw new Exception("Application Layer Standard must be specified.");
        if (centerMode == null) throw new Exception("The SUT Center Mode must be specified.");
        if (testCase == null) throw new Exception("The Test Case Name must be specified.");
//        return new NTCIP2306ApplicationLayerStandard(applicationLayerStandard, informationLayerStandard,
//                                                     requestDialog, subscriptionDialog, publicationDialog,
//                                                     centerMode, testCase, true);
        ApplicationLayerService.clearInstance();
        ApplicationLayerStandard theStandard = ApplicationLayerService.getInstance().getStandard(applicationLayerStandard);
        if (theStandard == null) throw new Exception("No Instances of "+applicationLayerStandard+" were found.");
        URL testConfigURL = new URL(testConfigSpecificationURL);
        URL testSuiteURL=null;

        if (testSuiteSpecificationURL != null){
            testSuiteURL = new URL(testSuiteSpecificationURL);
        }

        log.debug("TestSuiteURL= "+ testSuiteURL+" TestConfigURL= "+testConfigURL);
        theStandard.initializeStandard(testConfigURL, testSuiteURL, centerMode, informationLayerStandard,
                testCase, requestDialog, subscriptionDialog, publicationDialog);
        CenterMonitor.getInstance().registerApplicationStandard(theStandard);

        return theStandard;
    }

    /**
     * Gets the application layer standard.
     *
     * @return the application layer standard
     */
    public String getApplicationLayerStandard() {
        return applicationLayerStandard;
    }

    /**
     * Sets the application layer standard.
     *
     * @param applicationLayerStandard the new application layer standard
     */
    public void setApplicationLayerStandard(String applicationLayerStandard) {
        this.applicationLayerStandard = applicationLayerStandard;
    }

    /**
     * Gets the center mode.
     *
     * @return the center mode
     */
    public String getCenterMode() {
        return centerMode;
    }

    /**
     * Sets the center mode.
     *
     * @param centerMode the new center mode
     */
    public void setCenterMode(String centerMode) {
        this.centerMode = centerMode;
    }

    /**
     * Gets the information layer standard.
     *
     * @return the information layer standard
     */
    public String getInformationLayerStandard() {
        return informationLayerStandard;
    }

    /**
     * Sets the information layer standard.
     *
     * @param informationLayerStandard the new information layer standard
     */
    public void setInformationLayerStandard(String informationLayerStandard) {
        this.informationLayerStandard = informationLayerStandard;
    }

    /**
     * Gets the publication dialog.
     *
     * @return the publication dialog
     */
    public String getPublicationDialog() {
        return publicationDialog;
    }

    /**
     * Sets the publication dialog.
     *
     * @param publicationDialog the new publication dialog
     */
    public void setPublicationDialog(String publicationDialog) {
        this.publicationDialog = publicationDialog;
    }

    /**
     * Gets the request dialog.
     *
     * @return the request dialog
     */
    public String getRequestDialog() {
        return requestDialog;
    }

    /**
     * Sets the request dialog.
     *
     * @param requestDialog the new request dialog
     */
    public void setRequestDialog(String requestDialog) {
        this.requestDialog = requestDialog;
    }

    /**
     * Gets the subscription dialog.
     *
     * @return the subscription dialog
     */
    public String getSubscriptionDialog() {
        return subscriptionDialog;
    }

    /**
     * Sets the subscription dialog.
     *
     * @param subscriptionDialog the new subscription dialog
     */
    public void setSubscriptionDialog(String subscriptionDialog) {
        this.subscriptionDialog = subscriptionDialog;
    }

    /**
     * Gets the test case.
     *
     * @return the test case
     */
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
     * Gets the test config specification url.
     *
     * @return the test config specification url
     */
    public String getTestConfigSpecificationURL() {
        return testConfigSpecificationURL;
    }

    /**
     * Sets the test config specification url.
     *
     * @param testConfigSpecificationURL the new test config specification url
     */
    public void setTestConfigSpecificationURL(String testConfigSpecificationURL) {
        this.testConfigSpecificationURL = testConfigSpecificationURL;
    }

    /**
     * Gets the test suite specification url.
     *
     * @return the test suite specification url
     */
    public String getTestSuiteSpecificationURL() {
        return testSuiteSpecificationURL;
    }

    /**
     * Sets the test suite specification url.
     *
     * @param testSuiteSpecificationURL the new test suite specification url
     */
    public void setTestSuiteSpecificationURL(String testSuiteSpecificationURL) {
        this.testSuiteSpecificationURL = testSuiteSpecificationURL;
    }


}
