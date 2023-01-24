/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.fhwa.c2cri.centermodel.RIEmulation;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;
import org.fhwa.c2cri.testmodel.TestSuites;

/**
 * The Class TestNRTM.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestEmulationData {


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
           Executor taskExecutor = Executors.newFixedThreadPool(1);
           taskExecutor.execute(TestSuites.getInstance());
           URL[] urlList = TestSuites.getInstance().getTestSuiteEmulationDataURLs("*TMDD v3.03c Emulation");
           RIEmulationParameters parameters = new RIEmulationParameters(urlList);
           String returnedSuiteName = TestSuites.getInstance().getBaselineTestSuite("*TMDD v3.03c Emulation");
           String returnedSuiteName2 = TestSuites.getInstance().getBaselineTestSuite("TMDD v3.03c");
           RIEmulation.getInstance().initialize(returnedSuiteName, parameters);
           RIEmulation.getInstance().setEmulationEnabled(true);
           RIEmulation.getInstance().addEntity("CCTVINVENTORY", "cctv-5");
           RIEmulation.getInstance().initialize();
           System.out.println("All Done!");
           System.exit(0);
           
           
        } catch (Throwable t) {
            t.printStackTrace();
        }
            


    }
}
