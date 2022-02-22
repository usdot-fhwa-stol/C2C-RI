/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.domain.testmodel;

import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.testmodel.DefaultLayerParameters;
import org.fhwa.c2cri.testmodel.SUT;
import org.fhwa.c2cri.testmodel.TestMode;

/**
 *
 */
public interface TestConfigurationController {
    public String getFileName();
    public void saveConfig(String fileName) throws Exception;
    public void openConfig(String fileName) throws Exception;
    public void createConfig(String fileName, String testDescription, String infoLayerTestSuite, String appLayerTestSuite) throws Exception;
    public DefaultLayerParameters getAppLayerParams();
    public void setAppLayerParams(DefaultLayerParameters appLayerParams);
    public DefaultLayerParameters getInfoLayerParams();
    public void setInfoLayerParams(DefaultLayerParameters infoLayerParams);
    public void setSutParams(SUT sutParams);
    public SUT getSutParams();    
    public TestMode getTestMode();
    public void setTestMode(TestMode testMode);
    public String getSelectedAppLayerTestSuite();   
    public String getSelectedInfoLayerTestSuite();
    public String getTestDescription();
    public void setTestDescription(String testDescription);
    public String getConfigurationAuthor();
    public RIEmulationParameters getEmulationParameters();   
    public void setEmulationParameters(RIEmulationParameters emulationParameters);
    public String getCheckSumValue();
    public void addConfigChangeListener(TestConfigChangeListener listener);
    public void removeConfigChangeListener(TestConfigChangeListener listener);
    public String[] validateConfiguration();
}
