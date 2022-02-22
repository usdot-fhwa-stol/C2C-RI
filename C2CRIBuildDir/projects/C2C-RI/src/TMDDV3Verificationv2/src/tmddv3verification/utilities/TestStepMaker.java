/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities;

/**
 *
 * @author TransCore ITS
 */
public class TestStepMaker {


    public TestStep makeDialogSetupStep(String centerMode, String dialog){
        TestStep theStep = new TestStep();
        if ((centerMode.equals("EC")&&!dialog.endsWith("Update"))||
            (centerMode.equals("OC")&&dialog.endsWith("Update"))){
            theStep.setDescription("SETUP: Prepare the SUT to initiate the "+dialog+" dialog.");
            theStep.setResults("");
        } else if (centerMode.equals("OC")){
            if (dialog.endsWith("Subscription")){
             theStep.setDescription("SETUP: Prepare the SUT to process the "+dialog+" dialog.");
             theStep.setResults("");

            } else {
             theStep.setDescription("SETUP: Prepare the SUT to process the "+dialog+" dialog.");
             theStep.setResults("");

            }
        } else if (centerMode.equals("EC")){
             theStep.setDescription("SETUP: Prepare the SUT to process the "+dialog+" dialog.");
             theStep.setResults("");

        }
        return theStep;
    }

    public TestStep makeDialogCommandStep(String centerMode, String dialog, String message){
        TestStep theStep = new TestStep();
        if ((centerMode.equals("EC")&&!dialog.endsWith("Update"))||
            (centerMode.equals("OC")&&dialog.endsWith("Update"))){
            theStep.setDescription("RECEIVE the "+message+" message(s) using the "+dialog+" dialog.");
            theStep.setResults("");
        } else if (centerMode.equals("OC")){
            if (dialog.endsWith("Subscription")){
             theStep.setDescription("SUBSCRIBE-RESPONSE with user Input X with "+dialog+" dialog, expecting "+message+".");
             theStep.setResults("");

            } else {
             theStep.setDescription("REQUEST-RESPONSE with user Input X with "+dialog+" dialog, expecting "+message+".");
             theStep.setResults("");

            }
        } else if (centerMode.equals("EC")){
             theStep.setDescription("PUBLICATION-RESPONSE with user Input X with "+dialog+" dialog, expecting "+message+".");
             theStep.setResults("");

        }
        return theStep;
    }

        public TestStep makeDialogCommandCleanupStep(String centerMode, String dialog, String message)throws Exception{
        TestStep theStep = new TestStep();
        if ((centerMode.equals("EC")&&!dialog.endsWith("Update"))||
            (centerMode.equals("OC")&&dialog.endsWith("Update"))){
            theStep.setDescription("RESPOND using the "+dialog+" dialog.");
            theStep.setResults("");
        } else {
            throw new Exception ("Not applicable");
        }
        return theStep;
    }

}
