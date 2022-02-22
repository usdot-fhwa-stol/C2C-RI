/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import org.fhwa.c2cri.testmodel.TestCase;
import org.fhwa.c2cri.testmodel.TestCases;
import org.fhwa.c2cri.testmodel.TestConfiguration;

/**
 * The Class ConfigFileOutput.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class ConfigFileOutputTransferNRTM {

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        String[] cfgList = new String[]{"TMDDv303cEntityEmuOriginalECS.ricfg","TMDDv303cEntityEmuOriginalOCS.ricfg","TMDDv303dEntityEmuOriginalECS.ricfg","TMDDv303dEntityEmuOriginalOCS.ricfg","TMDDv31EntityEmuOriginalECS.ricfg","TMDDv31EntityEmuOriginalOCS.ricfg",
                                        "TMDDv303cEntityEmuOriginalQueueECS.ricfg","TMDDv303cEntityEmuOriginalQueueOCS.ricfg","TMDDv303dEntityEmuOriginalQueueECS.ricfg","TMDDv303dEntityEmuOriginalQueueOCS.ricfg","TMDDv31EntityEmuOriginalQueueECS.ricfg","TMDDv31EntityEmuOriginalQueueOCS.ricfg"};
        for (String cfgFile : cfgList){
            try {
                System.out.println("Processing "+cfgFile+" ...");
                String inputConfigFileName = "C:\\C2CRIInstallers\\Enhancements-trial8\\TestConfigurationFiles\\"+cfgFile;
                ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(inputConfigFileName)));
                TestConfiguration tcIn = (TestConfiguration) input.readObject();

                String outputConfigFileName = "C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\TestConfigurationFiles\\"+cfgFile;
                ObjectInputStream output = new ObjectInputStream(new FileInputStream(new File(outputConfigFileName)));
                TestConfiguration tcOut = (TestConfiguration) output.readObject();

    //            System.out.println("Info Layer Processing");
    //            for (Need un : tcIn.getInfoLayerParams().getNrtm().getUserNeeds().needs) {
    //                // Set the need flag value
    //                tcOut.getInfoLayerParams().getNrtm().getUserNeeds().getNeed(un.getTitle()).setFlagValue(un.getFlagValue());
    //
    //                // Set the requirement flag value
    //                for (Requirement req : tcIn.getInfoLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())) {
    //                    for (Requirement outReq : tcOut.getInfoLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())) {
    //                        if (req.getTitle().equals(outReq.getTitle())) {
    //                            outReq.setFlagValue(req.getFlagValue());
    //                            break;
    //                        }
    //                    }
    //                }
    //            }
    //
    //            System.out.println("App Layer Processing");
    //            for (Need un : tcIn.getAppLayerParams().getNrtm().getUserNeeds().needs) {
    //                // Set the need flag value
    //                tcOut.getAppLayerParams().getNrtm().getUserNeeds().getNeed(un.getTitle()).setFlagValue(un.getFlagValue());
    //
    //                // Set the requirement flag value
    //                for (Requirement req : tcIn.getAppLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())) {
    //                    for (Requirement outReq : tcOut.getAppLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())) {
    //                        if (req.getTitle().equals(outReq.getTitle())) {
    //                            outReq.setFlagValue(req.getFlagValue());
    //                            break;
    //                        }
    //                    }
    //                }
    //            }
    //
    //            tcOut.getSutParams().setWebServiceURL(tcIn.getSutParams().getWebServiceURL());
    //
                updateTestCases(tcIn.getInfoLayerParams().getTestCases(), tcOut.getInfoLayerParams().getTestCases(), tcOut.getSelectedInfoLayerTestSuite());

                tcOut.getEmulationParameters().setCommandQueueLength(tcIn.getEmulationParameters().getCommandQueueLength());
                tcOut.getEmulationParameters().setEntityDataMap(tcIn.getEmulationParameters().getEntityDataMap());

                output.close();
                output = null;

                input.close();
                input = null;

                ObjectOutputStream outputFile = new ObjectOutputStream(new FileOutputStream(outputConfigFileName));
                outputFile.writeObject(tcOut);
                outputFile.flush();
                outputFile.close();
                outputFile = null;
    //            FileOutputStream output = new FileOutputStream("c:\\c2cri\\EntityEmulationOut.xml");
    //            output.write(tc.to_LogFormat().getBytes());
    //            output.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void updateTestCases(TestCases sourceTestCases, TestCases destinationTestCases, String testSuiteName) {
        for (TestCase sTC : sourceTestCases.testCases) {
            if (sTC.isOverriden()) {
                try {
                    TestCase originalTestCase = getTestCase(sTC.getName(), destinationTestCases);
                    if (originalTestCase != null) {

                        // Create a new Test Case to replace the one currently in the destination
                        TestCase newTestCase = new TestCase(sTC.getName(), sTC.getScriptUrlLocation().getFile().substring(sTC.getScriptUrlLocation().getFile().lastIndexOf('/') + 1),
                                sTC.getDataUrlLocation().getFile().substring(sTC.getDataUrlLocation().getFile().lastIndexOf('/') + 1), sTC.getDescription(), sTC.getType(), testSuiteName);
                        newTestCase.setCustomDataLocation(sTC.getCustomDataLocation());
                        
                        int currentLocation = destinationTestCases.testCases.indexOf(originalTestCase);
                        destinationTestCases.testCases.set(currentLocation, newTestCase);
                        destinationTestCases.lh_testCasesMap.replace(sTC.getName(), newTestCase);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

    }

    public static TestCase getTestCase(String testCaseName, TestCases destinationTestCases) {
        Iterator<TestCase> iterator = destinationTestCases.testCases.iterator();
        while (iterator.hasNext()) {
            TestCase testCase = iterator.next();
            if (testCase.getName().equals(testCaseName)) {
                return testCase;
            }
        }
        return null;
    }
}
