/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.List;
import org.fhwa.c2cri.reporter.RIReports;
import org.fhwa.c2cri.testmodel.Need;
import org.fhwa.c2cri.testmodel.Requirement;
import org.fhwa.c2cri.testmodel.TestCase;
import org.fhwa.c2cri.testmodel.TestConfiguration;

/**
 * The Class TestConfigReportCreation.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestConfigReportCreation {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        HashMap inputParameters = new HashMap();
        TestConfiguration testConfig;

        inputParameters.put("SUBREPORT_DIR", "C:\\projects\\Release2\\projects\\C2C-RI\\src\\RIGUI\\.\\reports\\");
        inputParameters.put("dataSource", "C:\\C2CRI\\FixedTMDDNRTM.ricfg");
        inputParameters.put("reportSource", "./reports/TestScriptsReport.jrxml");
        inputParameters.put("reportDest", "C:\\c2cri\\TestConfig.pdf");

        try {
            ObjectInputStream input = new ObjectInputStream(new FileInputStream("C:\\C2CRI\\FixedTMDDNRTM.ricfg"));
            testConfig = null;
            testConfig = (TestConfiguration) input.readObject();
            testConfig.print();
            input.close();

            
            System.out.println("NeedID,NeedText,NeedFlagValue,NeedFlagName,NeedType,IsNeedExtension,"+
                    "RequirementID, RequirementText,RequirementType,RequirementFlagName,RequirementFlagValue,IsRequirementExtension,"+
                    "OtherRequirement,OtherRequirementValue,OtherRequirementValueName");
            for (Need thisNeed: testConfig.getInfoLayerParams().getNrtm().getUserNeeds().needs){
                if (thisNeed.getFlagValue()){
                    for (Requirement thisRequirement : testConfig.getInfoLayerParams().getNrtm().getNeedRelatedRequirements(thisNeed.getTitle())){
                        if (thisRequirement.getFlagValue()){

                           String otherRequirement = "";
                           String otherRequirementValue = "";
                           String otherRequirementValueName = "";
                           if (thisRequirement.getOtherRequirements().otherRequirements.size()> 0){
                               otherRequirement = thisRequirement.getOtherRequirements().otherRequirements.get(0).getOtherRequirement();
                               otherRequirementValue = thisRequirement.getOtherRequirements().otherRequirements.get(0).getValue();
                               otherRequirementValueName = thisRequirement.getOtherRequirements().otherRequirements.get(0).getValueName();
                           }

                           System.out.println(
                           thisNeed.getOfficialID() + ", "+
                           thisNeed.getText() + ", "+
                           thisNeed.getFlagValue() + ", "+
                           thisNeed.getFlagName() + ", "+
                           thisNeed.getType() + ", "+
                           thisNeed.isExtension() + ", "+
                           thisRequirement.getOfficialID() + ", "+
                           thisRequirement.getText() + ", "+
                           thisRequirement.getType() + ", "+
                           thisRequirement.getFlagName() + ", "+
                           thisRequirement.getFlagValue() + ", "+
                           thisRequirement.isExtension()+ ", "+
                           otherRequirement + ", "+
                           otherRequirementValue +", "+
                           otherRequirementValueName);
                        }

                    }
                }
            }

            System.out.println("\n\n\n");
            
            System.out.println("TestCaseName,TestCaseDescription,TestCaseType,TestCaseDataURL,TestCaseCustomDataURL,TestProcedureScriptURL, IsOverriden");
            List<TestCase> testCases = testConfig.getInfoLayerParams().getApplicableTestCases(testConfig.getTestMode().isExternalCenterOperation()?"EC":"OC");
            for (TestCase thisTestCase : testCases){
                System.out.println(
                thisTestCase.getName()+", "+
                thisTestCase.getDescription()+", "+
                thisTestCase.getType()+", "+
                thisTestCase.getDataUrlLocation().toString()+", "+
                thisTestCase.getCustomDataLocation()+", "+
                thisTestCase.getScriptUrlLocation().toString()+", "+
                thisTestCase.isOverriden());
            }


            RIReports newReport = new RIReports();
            newReport.createTestScriptReport(inputParameters, testConfig, "C:\\C2CRI\\FixedTMDDNRTM.ricfg");

        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
