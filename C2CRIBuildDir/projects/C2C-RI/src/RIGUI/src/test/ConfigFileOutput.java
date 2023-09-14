/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import org.fhwa.c2cri.centermodel.RINRTMSelection;
import org.fhwa.c2cri.testmodel.Need;
import org.fhwa.c2cri.testmodel.Requirement;
import org.fhwa.c2cri.testmodel.TestConfiguration;

/**
 * The Class ConfigFileOutput.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ConfigFileOutput {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
		 String configFileName = "C:\\C2CRI-Phase2\\Installations\\PreRelease_2_23\\TestConfigurationFiles\\TMDDv303cEntityEmuOriginalOCS.ricfg";
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(configFileName)))
		{
           
            TestConfiguration tc = (TestConfiguration)input.readObject();

            System.out.println("NeedID,NeedText,ReqID,ReqText");
            for (Need un : tc.getInfoLayerParams().getNrtm().getUserNeeds().needs){
                if (un.getFlagValue()){
                    for (Requirement req : tc.getInfoLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())){
                        if (req.getFlagValue()){
                            System.out.println(un.getTitle()+","+un.getText()+","+req.getTitle()+","+req.getText());
//                            System.out.println("nrtmSelections.addNRTM(\""+un.getTitle()+"\", \""+req.getTitle()+"\");");
                        }
                    }
                }
            }

        ArrayList<RINRTMSelection> nrtmSelections = new ArrayList();
         
         for (Need thisNeed : tc.getInfoLayerParams().getNrtm().getUserNeeds().needs) {
            if (thisNeed.getFlagValue()) {  // This need is selected
                List<String> relatedRequirementsList = tc.getInfoLayerParams().getNrtm().getRequirementsList(thisNeed.getTitle());

                for (String thisRequirementID : relatedRequirementsList) {
                    if (tc.getInfoLayerParams().getNrtm().getUserNeeds().getNeed(thisNeed.getTitle()).getProjectRequirements().lh_requirementsMap.containsKey(thisRequirementID)) {
                        Requirement thisRequirement = (Requirement) tc.getInfoLayerParams().getNrtm().getUserNeeds().getNeed(thisNeed.getTitle()).getProjectRequirements().lh_requirementsMap.get(thisRequirementID);
                        if (thisRequirement.getFlagValue()) {
                            nrtmSelections.add(new RINRTMSelection(thisNeed.getOfficialID(), thisRequirementID));
                        }
                    }
                }
            }
         }
         System.out.println("RITestEngine::NRTM Selections Map Size = "+nrtmSelections.size());
           
            
//            FileOutputStream output = new FileOutputStream("c:\\c2cri\\EntityEmulationOut.xml");
//            output.write(tc.to_LogFormat().getBytes());
//            output.close();

        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
