/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import org.fhwa.c2cri.testmodel.TestCase;
import org.fhwa.c2cri.testmodel.TestConfiguration;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class ConfigFileOutput.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ConfigFileApplicableTestCasesOutput {

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
		String configFileName = "c:\\c2cri\\TempCfg.ricfg";
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(configFileName))))
		{
            RIParameters.getInstance().configure();
            
            
            TestConfiguration tc = (TestConfiguration)input.readObject();

            List<TestCase> tcs = tc.getInfoLayerParams().getApplicableTestCases("EC");


        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
