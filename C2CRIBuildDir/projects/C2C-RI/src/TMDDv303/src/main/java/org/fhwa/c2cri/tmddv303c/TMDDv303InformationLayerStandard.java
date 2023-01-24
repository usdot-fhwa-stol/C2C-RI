/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmddv303c;

/**
 *
 */
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.fhwa.c2cri.tmdd.TMDDInformationLayerStandard;
import org.fhwa.c2cri.tmdd.TMDDSettings;

/**
 * The Class TMDDv303InformationLayerStandard.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
@PluginImplementation
public class TMDDv303InformationLayerStandard extends TMDDInformationLayerStandard {

    /**
     * The name space map.
     */
    private HashMap<String, String> nameSpaceMap = new HashMap();
    
    @Override
    public String getTestSuiteName() {
         return "TMDDv3.03c";
    }

    @Override
    public HashMap<String, String> getTestSuiteNameSpaceMap() {
        nameSpaceMap = new HashMap();
        nameSpaceMap.put("tmdd", "http://www.tmdd.org/303/messages");
        nameSpaceMap.put("c2c", "http://www.ntcip.org/c2c-message-administration");
        return nameSpaceMap;
    }

    @Override
    public String getDatabaseFileName() {
        return "TMDDv303SQLLite.db3";
    }

    @Override
    public String getDatabaseFilePath() {
        return "/org/fhwa/c2cri/tmddv303c/dbase/TMDDv303SQLLite.db3";
    }

    @Override
    public URL getMessageVerifyXSLPath() {
        return this.getClass().getResource("/org/fhwa/c2cri/tmddv303c/TMDDMessageVerify.xsl");
    }

    @Override
    public TMDDSettings getTMDDSettings() {
        return new TMDDv303Settings();
    }

    @Override
    public ArrayList<URL> getSchemaList(){
        ArrayList schemaList = new ArrayList();

        schemaList.add(org.fhwa.c2cri.tmddv303c.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("TMDD.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv303c.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("ATIS.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv303c.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("C2C.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv303c.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("ITIS-Adopted-03-00-02.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv303c.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("ITIS-Local-03-00-02.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv303c.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("LRMS-Adopted-02-00-00.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv303c.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("LRMS-Local-02-00-00.xsd"));
        schemaList.add(org.fhwa.c2cri.tmddv303c.interfaces.ntcip2306.NTCIP2306XMLValidator.class.getResource("NTCIP-References.xsd"));

        return schemaList; 
    }
    
    @Override
    public String getSubPubTableName(){
        return "TMDDV303FinalSubPubLookupTable";
    };    
   
    @Override
    public String getSchemaDetailTableName(){
        return "TMDDv303SchemaDetail";
    };        
    
}
