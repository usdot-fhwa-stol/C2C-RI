/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

/**
 *
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.fhwa.c2cri.applayer.ApplicationLayerStandard;
import org.fhwa.c2cri.center.CenterDefinitions;
import org.fhwa.c2cri.center.CenterMonitor;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.infolayer.InformationLayerController;
import org.fhwa.c2cri.infolayer.InformationLayerStandard;
import org.fhwa.c2cri.messagemanager.MessageManager;
import org.fhwa.c2cri.tmdd.dbase.TMDDConnectionPool;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageContentGenerator;
import org.fhwa.c2cri.tmdd.emulation.TMDDMessageProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDNRTMSelections;
import org.fhwa.c2cri.tmdd.interfaces.ntcip2306.NTCIP2306XMLValidator;

/**
 * The Class TMDDInformationLayerStandard.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public abstract class TMDDInformationLayerStandard
        implements InformationLayerStandard
{

    /**
     * The app standard.
     */
    private ApplicationLayerStandard appStandard;

    /**
     * The test case name.
     */
    private String testCaseName;

    /**
     * The center mode.
     */
    private CenterDefinitions.CENTERMODE centerMode;

    /**
     * The subscription publication mapping for this standard.
     */
    private TMDDSubPubMapping subPubMapping;

    /**
     * The controller used by this standard.
     */
    private InformationLayerController controller;

    /**
     * The name space map.
     */
    private HashMap<String, String> nameSpaceMap = new HashMap();

    /**
     * Creates a new Instance of TMDDInformationLayerStandard This method is
     * required to support proper ServiceLoader operation.
     */
    public TMDDInformationLayerStandard()
    {
    }

    /**
     * Provide the name for the Test Suite
     * @return The Test Suite Name that uniquely identifies this TMDD Test Suite Version 
     */
    public abstract String getTestSuiteName();
    
    /**
     * Provide the name space map associated with this version of TMDD
     * @return The name space map where the keys are prefixes and values are the associated name space urls.
     */
    public abstract HashMap<String, String> getTestSuiteNameSpaceMap();
    
    /**
     * Provide the name of the database file that is used for this version of TMDD.
     * @return The name of the database file
     */
    public abstract String getDatabaseFileName();
    
    /**
     * Provide the path to the database file.  This should be the classpath which can be used in a resource lookup.
     * @return The path to the database file.
     */
    public abstract String getDatabaseFilePath();

    
    /**
     * Provide the path to the xsl file that will be used for message verification.  This should be the classpath which can be used in a resource lookup.
     * @return The path to the xsl file.
     */
    public abstract URL getMessageVerifyXSLPath();
    
    /**
     * Provide the version specific TMDD settings from the c2cri.properties file or defaults.
     * @return The TMDDSettings
     */
    public abstract TMDDSettings getTMDDSettings();
    
    /**
     * Provide the set of schemas associated with the TMDD version.
     * @return 
     */
    public abstract ArrayList<URL> getSchemaList();
    
    /**
     * Provides the unique name of the sub pub lookup table used by the test suite.
     * @return The SubPub Table Name
     */
    public abstract String getSubPubTableName();
    
    /**
     * Provides the unique name of the schema detail table used by the test suite.
     * @return The SubPub Table Name
     */
    public abstract String getSchemaDetailTableName();
    

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName()
    {
        return getTestSuiteName();
    }

    /**
     * Initialize standard.
     * <p>
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param testCaseName the test case name
     * @param centerMode   the center mode
     * @param appStandard  the app standard
     *
     * @throws Exception the exception
     */
    @Override
    public void initializeStandard(String testCaseName, String centerMode, ApplicationLayerStandard appStandard) throws Exception
    {
        this.testCaseName = testCaseName;
        this.appStandard = appStandard;

        TMDDSettingsImpl.setTMDDSettings(getTMDDSettings());
        if (CenterDefinitions.CENTERMODE.OC.toString().equalsIgnoreCase(centerMode))
        {
            this.centerMode = CenterDefinitions.CENTERMODE.OC;
            TMDDMessageContentGenerator contentGenerator = TMDDMessageContentGenerator.getInstance();
            this.appStandard.setMessageContentGenerator(contentGenerator);
            RIEmulation.getInstance().setEmulationContentGenerator(contentGenerator);

            TMDDAuthenticationProcessor.getInstance("string", "string", "string");
        }
        else
            this.centerMode = CenterDefinitions.CENTERMODE.EC;

        nameSpaceMap = getTestSuiteNameSpaceMap();
        this.appStandard.getInformationLayerAdapter().setNameSpaceMap(nameSpaceMap);
        // Set the default Name Space Map so that messages will be translated as expected.
        MessageManager.getInstance().setNameSpaceMap(nameSpaceMap);
//TODO  I don't think this actually copies a complete DB3 file as well as we hope it does.
        File f = new File(getDatabaseFileName());
        if (!f.exists())
        {
            // Store a copy of the TMDD database to disk for later use
			try (BufferedInputStream inputStream = new BufferedInputStream(this.getClass().getResourceAsStream(getDatabaseFilePath()));
				 BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f)))
			{
				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1)
					outputStream.write(buffer, 0, bytesRead);
			}
        }
        TMDDConnectionPool.Initialize(getDatabaseFileName());
        this.controller = new TMDDController(this.appStandard.getInformationLayerAdapter());
        subPubMapping = new TMDDSubPubMapping(getSubPubTableName());
        this.appStandard.setSubPubMapper(subPubMapping);
        
        TMDDMessageTester.getInstance(getMessageVerifyXSLPath());

        NTCIP2306XMLValidator.setSchemas(getSchemaList());
        
        TMDDNRTMSelections.getInstance().setTMDDSchemaDetailTableName(getSchemaDetailTableName());
        TMDDMessageProcessor.setVersion(getTestSuiteName());
    }

    /**
     * Gets the information layer controller.
     *
     * @return the information layer controller
     */
    @Override
    public InformationLayerController getInformationLayerController()
    {
        return this.controller;
    }

    /**
     * Stop services.
     * <p>
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    @Override
    public void stopServices()
    {
        this.controller.shutdown();
        CenterMonitor.getInstance().unRegisterInformationStandard();
        TMDDConnectionPool.Close();
    }
}
