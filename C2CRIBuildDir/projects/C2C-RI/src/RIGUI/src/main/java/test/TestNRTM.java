/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.net.URL;
import org.fhwa.c2cri.testmodel.NRTM;
import org.fhwa.c2cri.testmodel.Need;
import org.fhwa.c2cri.testmodel.OtherRequirement;
import org.fhwa.c2cri.testmodel.Requirement;

/**
 * The Class TestNRTM.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestNRTM {


    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {

        try{
            
        URL nrtmPath = new URL("file:/c:/temp/RI_NRTM_QUERY.csv");
        URL nrtmPath2 = new URL("file:/c:/temp/2306_NRTM_QUERY.csv");
            NRTM thisNRTM = new NRTM(nrtmPath, nrtmPath2);
            System.out.println("\n\n");
            int index = 0;
            for (Need thisNeed: thisNRTM.getUserNeeds().needs){
                index++;
                System.out.println(index+" Need->"+thisNeed.getTitle()+" "+(thisNeed.isExtension()?"Extension":""));
                for (Requirement thisRequirement : thisNeed.getProjectRequirements().requirements){
                    for (OtherRequirement theOtherRequirement: thisRequirement.getOtherRequirements().otherRequirements){
                        System.out.println(thisNeed.getProjectRequirements().lh_requirementsMap.containsKey(thisRequirement.getTitle())+"   "+thisRequirement.getTitle()+" -- "+theOtherRequirement.getOtherRequirement());
                    }
                }
            }
        } catch (Exception ex){
            ex.printStackTrace();
        }


    }
}
