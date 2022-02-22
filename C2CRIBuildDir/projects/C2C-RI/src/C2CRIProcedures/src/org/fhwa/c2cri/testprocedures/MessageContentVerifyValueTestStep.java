/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testprocedures;

import java.util.ArrayList;

/**
 *
 * @author TransCore ITS
 */
public class MessageContentVerifyValueTestStep extends TestProcedureStep{

    private String messageName;
    public MessageContentVerifyValueTestStep(String messageName){
        this.setDescription("VERIFY that the values within the "+messageName+" message are correct per the TMDD standard and known operational conditions.");
        this.setResults("Pass/Fail");
        this.setIsSubStep(true);
        this.messageName = messageName;
    }

    public ArrayList<TestProcedureStep> getStepsforDatabase() {
        ArrayList<TestProcedureStep> theList = new ArrayList<TestProcedureStep>();
        theList.add(this);
        for (Section subStep : this.getSubSteps()){
            if (subStep instanceof TestProcedureStep){
               theList.add((TestProcedureStep)subStep);
            } else {
                theList.addAll(subStep.getStepsforDatabase());
            }
        }

        return theList;
    }

    public String getScriptContent() {
        String result;
        result = "\n"+"<testStep functionId=\"Step " + this.getId() +" "+this.getDescription()+"\"   >\n" +
    	         "   <AUTO-VERIFY-MESSAGE functionId=\"Automatically Verify message content\" msgName=\""+messageName+"\" verificationSpec=\"${VerificationSpec}\" />\n"+
     	         "   <USER-VERIFY-MESSAGE functionId=\"The User Verifies the message content\" msgName=\""+messageName+"\" optionList=\""+OptionalMessageContent.getInstance().getOptionalList()+"\" userDataFile=\"${RI_USERTCDATAFILE}\"/>\n"+
                 "</testStep>\n";
        for (Section subStep : this.getSubSteps()){
            result = result.concat(subStep.getScriptContent()+"\n");
        }
        return result;
    }

}
