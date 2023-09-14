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
import org.fhwa.c2cri.testmodel.TestConfiguration;

/**
 * The Class ConfigFileOutput.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class ConfigFileOutputTransferTestCaseUpdates {

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
            
        // Provide full path for directory(change accordingly)  
        String maindirpath = "C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI";
                  
        // File object
        File maindir = new File(maindirpath);
           
        if(maindir.exists() && maindir.isDirectory())
        {
            // array for files and sub-directories 
            // of directory pointed by maindir
            File arr[] = maindir.listFiles();
              
            System.out.println("**********************************************");
            System.out.println("Files from main directory : " + maindir);
            System.out.println("**********************************************");
              
            // Calling recursive method
            RecursivePrint(arr,0,0); 
       }      
//            String inputConfigFileName = "C:\\C2CRI\\TMDDv303cEntityEmuOriginalECS.ricfg";
//            ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(inputConfigFileName)));
//            TestConfiguration tcIn = (TestConfiguration) input.readObject();
//
//            String outputConfigFileName = "C:\\C2CRIDev\\C2CRIBuildDir\\projects\\C2C-RI\\src\\RIGUI\\TestConfigurationFiles\\TMDDv303cEntityEmuOriginalQueueOCS.ricfg";
//            ObjectInputStream output = new ObjectInputStream(new FileInputStream(new File(outputConfigFileName)));
//            TestConfiguration tcOut = (TestConfiguration) output.readObject();
//
////            System.out.println("Info Layer Processing");
////            for (Need un : tcIn.getInfoLayerParams().getNrtm().getUserNeeds().needs) {
////                // Set the need flag value
////                tcOut.getInfoLayerParams().getNrtm().getUserNeeds().getNeed(un.getTitle()).setFlagValue(un.getFlagValue());
////
////                // Set the requirement flag value
////                for (Requirement req : tcIn.getInfoLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())) {
////                    for (Requirement outReq : tcOut.getInfoLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())) {
////                        if (req.getTitle().equals(outReq.getTitle())) {
////                            outReq.setFlagValue(req.getFlagValue());
////                            break;
////                        }
////                    }
////                }
////            }
////
////            System.out.println("App Layer Processing");
////            for (Need un : tcIn.getAppLayerParams().getNrtm().getUserNeeds().needs) {
////                // Set the need flag value
////                tcOut.getAppLayerParams().getNrtm().getUserNeeds().getNeed(un.getTitle()).setFlagValue(un.getFlagValue());
////
////                // Set the requirement flag value
////                for (Requirement req : tcIn.getAppLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())) {
////                    for (Requirement outReq : tcOut.getAppLayerParams().getNrtm().getNeedRelatedRequirements(un.getTitle())) {
////                        if (req.getTitle().equals(outReq.getTitle())) {
////                            outReq.setFlagValue(req.getFlagValue());
////                            break;
////                        }
////                    }
////                }
////            }
////
////            tcOut.getSutParams().setWebServiceURL(tcIn.getSutParams().getWebServiceURL());
////
//
//
//            updateTestCases(tcIn.getInfoLayerParams().getTestCases(), tcOut.getInfoLayerParams().getTestCases(), tcOut.getSelectedInfoLayerTestSuite());
//
//            tcOut.getEmulationParameters().setCommandQueueLength(tcIn.getEmulationParameters().getCommandQueueLength());
//            tcOut.getEmulationParameters().setEntityDataMap(tcIn.getEmulationParameters().getEntityDataMap());
//            
//            output.close();
//            output = null;
//
//            input.close();
//            input = null;
//
//            ObjectOutputStream outputFile = new ObjectOutputStream(new FileOutputStream(outputConfigFileName));
//            outputFile.writeObject(tcOut);
//            outputFile.flush();
//            outputFile.close();
//            outputFile = null;
////            FileOutputStream output = new FileOutputStream("c:\\c2cri\\EntityEmulationOut.xml");
////            output.write(tc.to_LogFormat().getBytes());
////            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

         static void RecursivePrint(File[] arr,int index,int level) 
     {
         // terminate condition
         if(index == arr.length)
             return;
           
         // tabs for internal levels
         for (int i = 0; i < level; i++)
             System.out.print("\t");
           
         // for files
         if(arr[index].isFile() && arr[index].getName().endsWith(".ricfg"))
             ConvertFile(arr[index].getName(), arr[index].getAbsolutePath());
//             System.out.println(arr[index].getName());
           
         // for sub-directories
         else if(arr[index].isDirectory())
         {
             System.out.println("[" + arr[index].getName() + "]");
               
             // recursion for sub-directories
             RecursivePrint(arr[index].listFiles(), 0, level + 1);
         }
            
         // recursion for main directory
         RecursivePrint(arr,++index, level);
    }
         
    static boolean ConvertFile(String fileName, String fullFileName){
                try {
            String inputConfigFileName = fullFileName;
            
			TestConfiguration tcIn;
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(inputConfigFileName)))
			{
				tcIn = (TestConfiguration) input.readObject();     
			}

            
            
            String prototypeConfigFileName="";
            switch (tcIn.getSelectedInfoLayerTestSuite()){
                case "TMDD v3.03c": 
                    prototypeConfigFileName="C:\\c2cri\\TestTMDD303c.ricfg";
                    break;
                case "TMDD v3.03d":
                    prototypeConfigFileName="C:\\c2cri\\TestTMDDv303d.ricfg";
                    break;
                case "TMDD v3.1":
                    prototypeConfigFileName="C:\\c2cri\\TestTMDDv31.ricfg";
                    break;
                default: break;
            }

            try (ObjectInputStream output = new ObjectInputStream(new FileInputStream(prototypeConfigFileName)))
			{
				TestConfiguration tcOut = (TestConfiguration) output.readObject();
			}
	//            tcIn.getInfoLayerParams().setTestCases(tcOut.getInfoLayerParams().getTestCases());
	//            tcIn.getInfoLayerParams().setTestCaseMatrixProperty(tcOut.getInfoLayerParams().getTestCaseMatrix());
            
            String outputDir = "Regular";
            if (fileName.contains("EntityEmu"))outputDir = "EntityEmu";
            
            try (ObjectOutputStream outputFile = new ObjectOutputStream(new FileOutputStream("C:\\c2cri\\Updates\\"+outputDir+"\\"+fileName)))
			{
				outputFile.writeObject(tcIn);
			}
//            FileOutputStream output = new FileOutputStream("c:\\c2cri\\EntityEmulationOut.xml");
//            output.write(tc.to_LogFormat().getBytes());
//            output.close();            
            
            
            System.out.println(fileName + " " + tcIn.getSelectedInfoLayerTestSuite()+ " " + outputDir);
             
            
            
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    };
//    public static void updateTestCases(TestCases sourceTestCases, TestCases destinationTestCases, String testSuiteName) {
//        for (TestCase sTC : sourceTestCases.testCases) {
//            if (sTC.isOverriden()) {
//                try {
//                    TestCase originalTestCase = getTestCase(sTC.getName(), destinationTestCases);
//                    if (originalTestCase != null) {
//
//                        // Create a new Test Case to replace the one currently in the destination
//                        TestCase newTestCase = new TestCase(sTC.getName(), sTC.getScriptUrlLocation().getFile().substring(sTC.getScriptUrlLocation().getFile().lastIndexOf('/') + 1),
//                                sTC.getDataUrlLocation().getFile().substring(sTC.getDataUrlLocation().getFile().lastIndexOf('/') + 1), sTC.getDescription(), sTC.getType(), testSuiteName);
//                        newTestCase.setCustomDataLocation(sTC.getCustomDataLocation());
//                        
//                        int currentLocation = destinationTestCases.testCases.indexOf(originalTestCase);
//                        destinationTestCases.testCases.set(currentLocation, newTestCase);
//                        destinationTestCases.lh_testCasesMap.replace(sTC.getName(), newTestCase);
//                    }
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }
//            }
//        }
//
//    }
//
//    public static TestCase getTestCase(String testCaseName, TestCases destinationTestCases) {
//        Iterator<TestCase> iterator = destinationTestCases.testCases.iterator();
//        while (iterator.hasNext()) {
//            TestCase testCase = iterator.next();
//            if (testCase.getName().equals(testCaseName)) {
//                return testCase;
//            }
//        }
//        return null;
//    }
}
