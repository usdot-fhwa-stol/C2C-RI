/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.SwingWorker;
import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.testmodel.TestSuites;

/**
 * The Class TestConfigCreateAction.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestConfigCreateAction extends SwingWorker<TestConfiguration, String> {

    /** The test config ui. */
    private TestConfigCreateUI testConfigUI;
    
    /** The test configuration. */
    private TestConfiguration testConfiguration;

    /**
     * Instantiates a new test config create action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestConfigCreateAction() {
    }

    /**
     * Instantiates a new test config create action.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testConfigUI the test config ui
     */
    public TestConfigCreateAction(TestConfigCreateUI testConfigUI) {
        this.testConfigUI = testConfigUI;
    }

    /* (non-Javadoc)
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected TestConfiguration doInBackground() throws Exception {

        this.testConfiguration = new TestConfiguration();
        this.testConfiguration.create(testConfigUI.getTestDescription(), testConfigUI.getInfoLayerTestSuiteSelected(), testConfigUI.getAppLayerTestSuiteSelected(), TestSuites.getInstance().getTestSuiteEmulationDataURLs(testConfigUI.getInfoLayerTestSuiteSelected()));
        String fileName = testConfigUI.getTestConfigPath() + "\\" + testConfigUI.getTestConfigName() + ".ricfg";
        System.out.println("The file name is " + fileName);
        try {
            ObjectOutputStream output;

            output = new ObjectOutputStream(new FileOutputStream(fileName));
            output.writeObject(this.testConfiguration);
            output.flush();
            output.close();
            output=null;
            return testConfiguration;
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, "Invalid File Name", "File Name Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            throw new UnsupportedOperationException("Error creating Configuration File.");
        }
    }


}
