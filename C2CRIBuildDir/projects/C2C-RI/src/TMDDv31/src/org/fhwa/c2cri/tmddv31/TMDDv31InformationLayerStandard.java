/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmddv31;

/**
 *
 */
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.fhwa.c2cri.tmdd.TMDDInformationLayerStandard;
import org.fhwa.c2cri.tmdd.TMDDSettings;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TMDDv31InformationLayerStandard.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
@PluginImplementation
public class TMDDv31InformationLayerStandard
        extends TMDDInformationLayerStandard
{
    /**
     * The name space map.
     */
    private HashMap<String, String> nameSpaceMap = new HashMap();

    /**
     * Creates a new Instance of TMDDv31InformationLayerStandard This method is
     * required to support proper ServiceLoader operation.
     */
    public TMDDv31InformationLayerStandard()
    {
		// original implementation was empty
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    @Override
    public String getName()
    {
        return "TMDDv3.1";
    }

    @Override
    public String getTestSuiteName() {
         return "TMDDv3.1";
    }

    @Override
    public HashMap<String, String> getTestSuiteNameSpaceMap() {
        nameSpaceMap = new HashMap();
        nameSpaceMap.put("tmdd", "http://www.tmdd.org/303/messages");
        nameSpaceMap.put("c2c", "http://www.ntcip.org/c2c-message-administration");
        nameSpaceMap.put("tmddX", "http://www.tmdd.org/X");
        return nameSpaceMap;
    }

    @Override
    public String getDatabaseFileName() {
        return "TMDDv31SQLLite.db3";
    }

    @Override
    public String getDatabaseFilePath() {
        return "/org/fhwa/c2cri/tmddv31/dbase/TMDDv31SQLLite.db3";
    }

    @Override
    public URL getMessageVerifyXSLPath() {
        return this.getClass().getResource("/org/fhwa/c2cri/tmddv31/TMDDMessageVerify.xsl");
    }

    @Override
    public TMDDSettings getTMDDSettings() {
        return new TMDDv31Settings();
    }

    @Override
    public ArrayList<URL> getSchemaList(){
        ArrayList schemaList = new ArrayList();
        String useNillableSchema = RIParameters.getInstance().getParameterValue(TMDDv31Settings.TMDDv31_SETTINGS_GROUP, TMDDv31Settings.TMDDv31_USE_NILLABLE_SCHEMA_PARAMETER,
                        TMDDv31Settings.TMDDv31_USE_NILLABLE_SCHEMA_DEFAULT_VALUE);

        if (!Boolean.parseBoolean(useNillableSchema)){
            schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("TMDD.xsd"));
            schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("TMDDvX.xsd"));
        } else {
            schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("TMDDNILL.xsd"));
            schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("TMDDvXNILL.xsd"));            
        }
        schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("ATIS.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("C2C.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("ITIS-Adopted-03-00-02.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("ITIS-Local-03-00-02.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("LRMS-Adopted-02-00-00.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("LRMS-Local-02-00-00.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv31.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("NTCIP-References.xsd"));

        return schemaList; 
    }
    
    @Override
    public String getSubPubTableName(){
        return "TMDDv31FinalSubPubLookupTable";
    };    
   
    @Override
    public String getSchemaDetailTableName(){
        return "TMDDv31SchemaDetail";
    };    
}
