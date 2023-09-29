/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.domain.testmodel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.LogManager;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.testmodel.DefaultLayerParameters;
import org.fhwa.c2cri.testmodel.SUT;
import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.testmodel.TestMode;
import org.fhwa.c2cri.testmodel.TestSuites;
import org.fhwa.c2cri.utilities.Checksum;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 *
 */
public class DefaultTestConfigController implements TestConfigurationController {

    private TestConfiguration testConfig;
    private String fileName;
    private String checkSum;
    private List<String> applicationLayerErrors=new ArrayList();
    private List<String> informationLayerErrors= new ArrayList();
    private List<String> configInputErrors=new ArrayList();
    private List<String> sutInputErrors= new ArrayList();
    final private List<TestConfigChangeListener> listeners;

    /**
     * The validation errors found.
     */
    private boolean validationErrorsFound = false;

    public DefaultTestConfigController() {
        this.listeners = new ArrayList<>();
    }

    public DefaultTestConfigController(TestConfiguration testConfig) {
        this.listeners = new ArrayList<>();
        this.testConfig = testConfig;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    
    @Override
    public void saveConfig(String file) throws Exception {
        if (isValidTestConfig()) {
            // Per customer request, the RI will allow a configuration File to be saveable,
            // if the errors are within the Information Layer and Application Layer NRTM selections.
            // The user is notified about these errors.  If the user clicks "Continue" the isValidTestConfig()
            // method will actually return true - meaning it's ok to save the configuration.  The validationErrorsFound flag will
            // indicate that there were actually some information or application layer errors found.
            if (!validationErrorsFound) {
                testConfig.setValidConfiguration(true);
            } else {
                testConfig.setValidConfiguration(false);
            }

            fileName = file;
            try {
				try
				{
					Files.delete(Paths.get(fileName));
				}
				catch (IOException oEx)
				{
					LogManager.getLogger(getClass()).error(oEx, oEx);
				}
                

                String userName = RIParameters.getInstance().getParameterValue(RIParameters.RI_USER_PARAMETER_GROUP, RIParameters.RI_USER_PARAMETER, RIParameters.DEFAULT_RI_USER_PARAMETER_VALUE);
                if (userName.isEmpty()) {
                    testConfig.setConfigurationAuthor(System.getProperty("user.name"));
                } else {
                    testConfig.setConfigurationAuthor(userName + ":" + System.getProperty("user.name"));
                }

                try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName)))
				{
					output.writeObject(testConfig);
				}
                Checksum cs = new Checksum();
                try {
                    checkSum = (cs.getChecksum(fileName));
                } catch (Exception ex) {
                    throw new Exception("Error computing checksum for file " + fileName);
                }

            } catch (Exception e4) {
                e4.printStackTrace();
                throw new Exception("Error encountered saving the configuration file " + fileName);
            }

//        } else {
//            throw new Exception("The Configuration File will not be saved due to the reported Errors.");
        }
    }

    @Override
    public void openConfig(String fileName) throws Exception {
        File file = new File(fileName);

        if (file == null || file.getName().equals("")) {
            throw new Exception("Invalid File Name");
        } else {

			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName)))
			{
				testConfig = null;
				testConfig = (TestConfiguration) input.readObject();
				testConfig.print();

				Checksum cs = new Checksum();
				try {
					checkSum = cs.getChecksum(fileName);
				} catch (Exception ex) {
					throw new Exception("Error encountered computing checksum for file " + fileName);
				}
				this.fileName = fileName;
			} catch (Exception e1) {
				e1.printStackTrace();
				throw new Exception("Error encountered reading the configuration file.");
			}


        }
    }

    @Override
    public void createConfig(String fileName, String testDescription, String infoLayerTestSuite, String appLayerTestSuite) throws Exception {
        testConfig = new TestConfiguration();
        testConfig.create(testDescription, infoLayerTestSuite, appLayerTestSuite, TestSuites.getInstance().getTestSuiteEmulationDataURLs(infoLayerTestSuite));
        String file = fileName + ".ricfg";
        System.out.println("The file name is " + file);
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))){
            output.writeObject(testConfig);
            output.flush();
            this.fileName = file;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error Creating Configuration File");
        }
    }

    @Override
    public DefaultLayerParameters getAppLayerParams() {
        return testConfig.getAppLayerParams();
    }

    @Override
    public void setAppLayerParams(DefaultLayerParameters appLayerParams) {
        testConfig.setAppLayerParams(appLayerParams);
        sendConfigurationValueChangeNotification();
        if (!isValidAppLayerParamsInput()) {
            sendConfigurationInvalidNotification();
        }
    }

    @Override
    public DefaultLayerParameters getInfoLayerParams() {
        return testConfig.getInfoLayerParams();
    }

    @Override
    public void setInfoLayerParams(DefaultLayerParameters infoLayerParams) {
        testConfig.setInfoLayerParams(infoLayerParams);
        sendConfigurationValueChangeNotification();
        if (!isValidInfoLayerParamsInput()) {
            sendConfigurationInvalidNotification();
        }
    }

    @Override
    public void setSutParams(SUT sutParams) {
        testConfig.setSutParams(sutParams);
        sendConfigurationValueChangeNotification();
        if (!isValidSUTInput()) {
            sendConfigurationInvalidNotification();
        }
    }

    @Override
    public SUT getSutParams() {
        return testConfig.getSutParams();
    }

    @Override
    public TestMode getTestMode() {
        return testConfig.getTestMode();
    }

    @Override
    public void setTestMode(TestMode testMode) {
        testConfig.setTestMode(testMode);
        sendConfigurationValueChangeNotification();
    }

    @Override
    public String getSelectedAppLayerTestSuite() {
        return testConfig.getSelectedAppLayerTestSuite();
    }

    @Override
    public String getSelectedInfoLayerTestSuite() {
        return testConfig.getSelectedInfoLayerTestSuite();
    }

    @Override
    public String getTestDescription() {
        return testConfig.getTestDescription();
    }

    @Override
    public void setTestDescription(String testDescription) {
        testConfig.setTestDescription(testDescription);
        sendConfigurationValueChangeNotification();
    }

    @Override
    public String getConfigurationAuthor() {
        return testConfig.getConfigurationAuthor();
    }

    @Override
    public RIEmulationParameters getEmulationParameters() {
        return testConfig.getEmulationParameters();
    }

    @Override
    public void setEmulationParameters(RIEmulationParameters emulationParameters) {
        testConfig.setEmulationParameters(emulationParameters);
        sendConfigurationValueChangeNotification();
    }

    @Override
    public String getCheckSumValue() {
        return checkSum;
    }

    @Override
    public void addConfigChangeListener(TestConfigChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeConfigChangeListener(TestConfigChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public String[] validateConfiguration() {
        List<String> results = new ArrayList();
        try {
            if (!isValidTestConfig()) {

                if (!configInputErrors.isEmpty()){
                    results.add("Configuration Input Errors \n");
                    results.addAll(configInputErrors);
                    results.add("\n");
                }

                if (!sutInputErrors.isEmpty()){
                    results.add("SUT Input Errors \n");
                    results.addAll(sutInputErrors);
                    results.add("\n");
                }
                
                if (!applicationLayerErrors.isEmpty()){
                    results.add("Application Layer Errors \n");
                    results.addAll(applicationLayerErrors);
                    results.add("\n");
                }
                if (!informationLayerErrors.isEmpty()){
                    results.add("Information Layer Errors \n");
                    results.addAll(informationLayerErrors);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return results.toArray(new String[results.size()]);
    }

    private void sendConfigurationValueChangeNotification() {
        for (TestConfigChangeListener tcl : listeners) {
            tcl.testConfigurationValueUpdate();
        }
    }

    private void sendConfigurationInvalidNotification() {
        for (TestConfigChangeListener tcl : listeners) {
            tcl.testConfigurationInvalidUpdate();
        }
    }

    
    /**
     * Tests all areas of Test Configuration user input.
     *
     * @return true, if is valid test config
     */
    private boolean isValidTestConfig() throws Exception {
        validationErrorsFound = false;
        boolean result = 
                 isValidConfigInput()
                && isValidSUTInput()
                && isValidAppLayerParamsInput()
                && isValidInfoLayerParamsInput();
        
        return result;
    }

    /**
     * Tests user input on the RI Mode panel.
     *
     * @return true, if is valid config input
     */
    private boolean isValidConfigInput() {
        configInputErrors.clear();
        if (testConfig.getTestMode().isExternalCenterOperation() || testConfig.getTestMode().isOwnerCenterOperation()) {
            return true;
        } else {
            configInputErrors.add("The test mode has not been specified.");
            return false;
        }
    }

    /**
     * Tests user SUT input.
     *
     * @return true, if is valid sut input
     */
    private boolean isValidSUTInput() {
        sutInputErrors.clear();
        
        if (testConfig.getSutParams().getHostName().isEmpty()|| !isHostName(testConfig.getSutParams().getHostName(), "SUT Tab -- Host Name")) sutInputErrors.add("A valid Host Name must be provided.");
        if (testConfig.getSutParams().getIpAddress().isEmpty() || !isIPAddress(testConfig.getSutParams().getIpAddress(), "SUT Tab -- IP Address")) sutInputErrors.add("A valid IP Address must be provided.");
        if (testConfig.getSutParams().getIpPort().isEmpty() || !isPort(testConfig.getSutParams().getIpPort(), "SUT Tab -- Port")) sutInputErrors.add("A valid Port must be provided.");
        if (testConfig.getSutParams().getWebServiceURL().isEmpty() || !isURL(testConfig.getSutParams().getWebServiceURL(), "SUT Tab -- Web Service URL")) sutInputErrors.add("A Web Service URL value must be provided.");
        if ((testConfig.getSutParams().isUserNameRequired() && testConfig.getSutParams().getUserName().isEmpty())) sutInputErrors.add("A user name must be provided.");

        return !testConfig.getSutParams().getHostName().isEmpty()
                && !testConfig.getSutParams().getIpAddress().isEmpty()
                && !testConfig.getSutParams().getIpPort().isEmpty()
                && !testConfig.getSutParams().getWebServiceURL().isEmpty()
                //                && SwingValidator.isPresent(corePanel.sutPanel.userNameTextField, "SUT Tab -- User Name")
                //                && SwingValidator.isPresent(corePanel.sutPanel.passwordTextField, "SUT Tab -- Password")
                && isHostName(testConfig.getSutParams().getHostName(), "SUT Tab -- Host Name")
                && isIPAddress(testConfig.getSutParams().getIpAddress(), "SUT Tab -- IP Address")
                && isPort(testConfig.getSutParams().getIpPort(), "SUT Tab -- Port")
                && isURL(testConfig.getSutParams().getWebServiceURL(), "SUT Tab -- Web Service URL")
                && ((testConfig.getSutParams().isUserNameRequired() && !testConfig.getSutParams().getUserName().isEmpty()) || !testConfig.getSutParams().isUserNameRequired());
        // No Need to check the password field as it can be anything.
        //&& SwingValidator.isAlphanumeric(corePanel.sutPanel.passwordTextField, "SUT Tab -- Password");
    }

    /**
     * Tests user input on the appLayerParams Panel.
     *
     * @return true, if is valid app layer params input
     */
    private boolean isValidAppLayerParamsInput() {
        ArrayList<String> errorLog = testConfig.getAppLayerParams().verifyLayerParameters("Application Layer");
        if (errorLog.size() > 0) {
            applicationLayerErrors = errorLog;
            validationErrorsFound = true;
            return false;  // True means Continue, False means Return
        }
        applicationLayerErrors.clear();
        return true;
    }

    /**
     * Tests user input on the infoLayerParams Panel.
     *
     * @return true, if is valid info layer params input
     */
    private boolean isValidInfoLayerParamsInput() {
        ArrayList<String> errorLog = testConfig.getInfoLayerParams().verifyLayerParameters("Information Layer");
        if (errorLog.size() > 0) {
            informationLayerErrors = errorLog;
            validationErrorsFound = true;
            return false;  // True means Continue, False means Return
        }
        informationLayerErrors.clear();
        return true;
    }

    /**
     * Tests whether text included within a text component is in the proper IP
     * Address form Present a warning to the user if the field is not valid.
     *
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isIPAddress(String c, String title) {

        String[] parts = c.split("\\.");
        if (parts.length != 4) {
//            showMessage(c, title + " is not valid.  Must have format: [0-255].[0-255].[0-255].[0-255] \n"
//                    + "Please re-enter.");
            return false;
        }
        for (String s : parts) {
            int i = Integer.parseInt(s);
            if ((i < 0) || (i > 255)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Tests whether text included within a text component is in the proper URL
     * form Present a warning to the user if the field is not valid.
     *
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isURL(String c, String title) {
        try {
            URL url = new URL(c);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    /**
     * Tests whether text included within a text component is in the proper port
     * number form.
     *
     * A valid port value is between 0 and 65535.
     *
     * Present a warning to the user if the field is not valid.
     *
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isPort(String c, String title) {

        try {
            Integer portNumber = Integer.parseInt(c);

            if (!((portNumber >= 0) && (portNumber <= 65535))) {
                return false;

            }
        } catch (Exception ex) {
            return false;

        }

        return true;
    }

    /**
     * Tests whether text included within a text component is in the proper Host
     * Name form
     *
     * 1). It can contain only dots, dash and alphanumeric characters. 2). It
     * cannot be more than 63 characters in length. 3). The first character must
     * be an alphanumeric. 4). The last character cannot be a dot or dash. 5).
     * There should at least be one alphabet (This is for Linux).
     *
     * Present a warning to the user if the field is not valid.
     *
     * @param c the text component to be checked
     * @param title the title of the component
     * @return flag indicating whether the text in the field is valid
     */
    public static boolean isHostName(String c, String title) {
        String domainIdentifier = "((\\p{Alnum})([-]|(\\p{Alnum}))*(\\p{Alnum}))|(\\p{Alnum})";
        String domainNameRule = "(" + domainIdentifier + ")((\\.)(" + domainIdentifier + "))*";
        String oneAlpha = "(.)*((\\p{Alpha})|[-])(.)*";

        if ((c == null) || (c.length() > 63)) {
            return false;
        }

        if (!(c.matches(domainNameRule) && c.matches(oneAlpha))) {
            return false;
        }
        return true;
    }

}
