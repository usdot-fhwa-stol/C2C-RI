/**
 *
 */
package org.fhwa.c2cri.testmodel;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.utilities.ErrorMessage;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * This class represents all information and operations associated with a Test
 * Configuration.
 *
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
@XmlRootElement(name = "testConfiguration")
@XmlAccessorType(XmlAccessType.FIELD)
public class TestConfiguration implements Serializable {

    static final long serialVersionUID = -8157028558398958811L;

    /**
     * reference to the application layer parameter information contained within
     * this test configuration.
     */
    private DefaultLayerParameters appLayerParams;

    /**
     * reference to the Test Mode selection information contained within this
     * test configuration.
     */
    private TestMode testMode;

    /**
     * reference to the information layer parameter data contained within this
     * test configuration.
     */
    private DefaultLayerParameters infoLayerParams;

    /**
     * reference to the System Under Test information contained within this test
     * configuration.
     */
    private SUT sutParams;
    /**
     * a reference to the currently available test suites recognized by the RI.
     */
    private transient TestSuites availableSuites;

    /**
     * The RI user that saved/created this test configuration.
     */
    private String configurationAuthor;
    /**
     * The Application Layer test suite selected for this test configuration.
     */
    private String selectedAppLayerTestSuite;
    /**
     * The information Layer test suite selected for this test configuration.
     */
    private String selectedInfoLayerTestSuite;

    /**
     * the format version of this Test Configuration.
     */
    private long configVersion = 1;
    /**
     * description information for this test configuration.
     */
    private String testDescription;

    /**
     * flag indicating whether this test configuration has been validated.
     */
    private boolean validConfiguration = false;

    /**
     * reference to the emulationParameters information contained within this
     * test configuration.
     */
    private RIEmulationParameters emulationParameters;

    /**
     * TestConfiguration basic Constructor.
     */
    public TestConfiguration() {
    }

    /**
     * TestConfiguration basic Constructor.
     *
     * @param modes the modes
     * @param options the options
     * @param sut the sut
     * @param log the log
     * @param apps the apps
     * @param info the info
     */
    public TestConfiguration(TestMode modes, TestOptions options, SUT sut, DefaultLayerParameters apps, DefaultLayerParameters info) {
        this.testMode = modes;
        this.sutParams = sut;
        this.appLayerParams = apps;
        this.infoLayerParams = info;
    }

    /**
     * Creates the.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param configDescription the config description
     * @param infoLayerTestSuite the info layer test suite
     * @param appLayerTestSuite the app layer test suite
     * @return true, if successful
     */
    public boolean create(String configDescription, String infoLayerTestSuite, String appLayerTestSuite, URL[] emulationDataURLs) {
        try {
            this.testDescription = configDescription;
            this.selectedAppLayerTestSuite = appLayerTestSuite;
            this.selectedInfoLayerTestSuite = infoLayerTestSuite;
            this.testMode = new TestMode();
            this.sutParams = new SUT();
            this.infoLayerParams = new DefaultLayerParameters(infoLayerTestSuite);
            this.appLayerParams = new DefaultLayerParameters(appLayerTestSuite);
            String userName = RIParameters.getInstance().getParameterValue(RIParameters.RI_USER_PARAMETER_GROUP, RIParameters.RI_USER_PARAMETER, RIParameters.DEFAULT_RI_USER_PARAMETER_VALUE);
            if (userName.isEmpty()) {
                this.configurationAuthor = System.getProperty("user.name");
            } else {
                this.configurationAuthor = userName + ":" + System.getProperty("user.name");
            }
            if (emulationDataURLs == null) {
                this.emulationParameters = new RIEmulationParameters();
            } else {
                this.emulationParameters = new RIEmulationParameters(emulationDataURLs);
                System.out.println("TestConfiguration:Create wrote out "+this.emulationParameters.getEntityDataMap().size() + " emulation parameters");
                        
            }

            return true;
        } catch (Exception ex) {
            ErrorMessage.showError(ex.getMessage());
            return false;
        }
    }

    /**
     * Gets the app layer params.
     *
     * @return the appLayerParams
     */
    public DefaultLayerParameters getAppLayerParams() {
        return appLayerParams;
    }

    /**
     * Sets the app layer params.
     *
     * @param appLayerParams the appLayerParams to set
     */
    public void setAppLayerParams(DefaultLayerParameters appLayerParams) {
        this.appLayerParams = appLayerParams;
    }

    /**
     * Gets the info layer params.
     *
     * @return the infoLayerParams
     */
    public DefaultLayerParameters getInfoLayerParams() {
        return infoLayerParams;
    }

    /**
     * Sets the info layer params.
     *
     * @param infoLayerParams the infoLayerParams to set
     */
    public void setInfoLayerParams(DefaultLayerParameters infoLayerParams) {
        this.infoLayerParams = infoLayerParams;
    }

    /**
     * Gets the sut params.
     *
     * @return the sutParams
     */
    public SUT getSutParams() {
        return sutParams;
    }

    /**
     * Sets the sut params.
     *
     * @param sutParams the sutParams to set
     */
    public void setSutParams(SUT sutParams) {
        this.sutParams = sutParams;
    }

    /**
     * Gets the test mode.
     *
     * @return the testMode
     */
    public TestMode getTestMode() {
        return testMode;
    }

    /**
     * Sets the test mode.
     *
     * @param testMode the testMode to set
     */
    public void setTestMode(TestMode testMode) {
        this.testMode = testMode;
    }

    /**
     * Load the specified test configuration file. If no file name is provided
     * then initialize the test configuration using all default parameters. If
     * there is a problem encountered loading the test configuration throw an
     * exception and indicate what problems were encountered.
     *
     * @param configurationFile the configuration file
     */
    public void initializeConfiguration(String configurationFile) {
		// original implementation was empty
    }

    /**
     * Check the elements of the configuration file to confirm that all elements
     * are valid.
     */
    public void verifyConfiguration() {
		// original implementation was empty
    }

    /**
     * Save the test configuration to the specified test configuration file. If
     * no file name is provided then throw an exception. If the file exists,
     * warn the user about an overwrite condition. If there is a problem
     * encountered while storing the test configuration throw an exception and
     * indicate what problems were encountered.
     *
     * Set the configurationAuthor field to the current RI users user name.
     *
     * The TestConfiguration is saved as an object, so all local and associated
     * information will be stored in the file.
     *
     * @param configurationFile the configuration file
     */
    public void saveConfiguration(String configurationFile) {
		// original implementation was empty
    }

    /**
     * Checks whether the confiig file provided is valid.
     *
     * @param fileName the file name
     * @return true, if is valid file name
     */
    public boolean isValidFileName(String fileName) {
        return true;
    }

    /**
     * Converts the Test Configuration into a form that can be written to and
     * read from the Test Log.
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
            final JAXBContext context = JAXBContext.newInstance(TestConfiguration.class);

            // =============================================================================================================
            // Marshalling OBJECT to XML
            // =============================================================================================================
            // Create the marshaller, this is the nifty little thing that will actually transform the object into XML
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, java.nio.charset.StandardCharsets.UTF_8.name());

            // Create a stringWriter to hold the XML
            final StringWriter stringWriter = new StringWriter();

            // Marshal the javaObject and write the XML to the stringWriter
            marshaller.marshal(this, stringWriter);

            // Print out the contents of the stringWriter
            logOutput = stringWriter.toString();
//            logOutput.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" , "");
            return logOutput;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "Config File Conversion Error - " + ex.getMessage();
        }
    }

    /**
     * Gets the selected app layer test suite.
     *
     * @return the selected app layer test suite
     */
    public String getSelectedAppLayerTestSuite() {
        return selectedAppLayerTestSuite;
    }

    /**
     * Gets the selected info layer test suite.
     *
     * @return the selected info layer test suite
     */
    public String getSelectedInfoLayerTestSuite() {
        return selectedInfoLayerTestSuite;
    }

    /**
     * Gets the test description.
     *
     * @return the test description
     */
    public String getTestDescription() {
        return testDescription;
    }

    /**
     * Gets the configuration author.
     *
     * @return the configuration author
     */
    public String getConfigurationAuthor() {
        return configurationAuthor;
    }

    /**
     * Sets the configuration author.
     *
     * @param configurationAuthor the new configuration author
     */
    public void setConfigurationAuthor(String configurationAuthor) {
        this.configurationAuthor = configurationAuthor;
    }

    /**
     * Sets the test description.
     *
     * @param testDescription the new test description
     */
    public void setTestDescription(String testDescription) {
        this.testDescription = testDescription;
    }

    /**
     * Checks if is valid configuration.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return true, if is valid configuration
     */
    public boolean isValidConfiguration() {
        return validConfiguration;
    }

    /**
     * Sets the valid configuration.
     *
     * @param validConfiguration the new valid configuration
     */
    public void setValidConfiguration(boolean validConfiguration) {
        this.validConfiguration = validConfiguration;
    }

    /**
     * Obtain the current Emulation Parameters
     *
     * @return the emulationParameters
     */
    public RIEmulationParameters getEmulationParameters() {
        return emulationParameters;
    }

    /**
     * Set the Emulation Parameters for this configuration file.
     *
     * @param emulationParameters
     */
    public void setEmulationParameters(RIEmulationParameters emulationParameters) {
        this.emulationParameters = emulationParameters;
    }

    /**
     * Prints the.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public void print() {
        System.out.println("-----------------------------------------");
        System.out.println(" Test Description: " + this.testDescription);
        System.out.println(" Selected App Layer Test Suite: " + this.selectedAppLayerTestSuite);
        System.out.println(" Selected Info Layer Test Suite: " + this.selectedInfoLayerTestSuite);
        System.out.println(" External Center Mode: " + this.testMode.externalCenterOperation);
        System.out.println(" Owner Center Mode: " + this.testMode.ownerCenterOperation);
//        System.out.println(" Range Testing: " + this.testingOptions.isRangeTesting());
//        System.out.println(" Negative Testing: " + this.testingOptions.isNegativeTesting());
        //       System.out.println(" Other Testing: " + this.testingOptions.isOtherTestingOptions());
        //       System.out.println(" Log Actions and Messages: " + this.logInfo.isLogActionsAndMessages());
        //       System.out.println(" Log Failures: " + this.logInfo.isLogFailuresOnly());
//        System.out.println(" Log Messages: " + this.logInfo.isLogMessagesOnly());
        System.out.println(" Test Configuration Author: " + this.configurationAuthor);
        System.out.println("-----------------------------------------");

    }

    // using readObject method to set default values
    private void readObject(ObjectInputStream input)
            throws IOException, ClassNotFoundException {
        // deserialize the non-transient data members first;
        input.defaultReadObject();
        if (getEmulationParameters() == null){
            // Set a RIEmulation parameters Value
            RIEmulationParameters temp = new RIEmulationParameters();
            setEmulationParameters(temp);
        }

    }
}
