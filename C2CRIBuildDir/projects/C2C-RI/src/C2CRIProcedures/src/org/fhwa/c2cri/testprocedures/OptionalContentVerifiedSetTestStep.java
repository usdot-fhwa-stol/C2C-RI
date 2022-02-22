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
public class OptionalContentVerifiedSetTestStep extends TestProcedureStep{

    public OptionalContentVerifiedSetTestStep(){
        this.setDescription("CONFIGURE: Set CONTENTVERIFIED to True");
        this.setResults("NA");
        this.setIsSubStep(true);
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
        result = "\n"+"<testStep functionId=\" Step " + this.getId() +" "+this.getDescription()+"\"   passfailResult=\"False\">\n" +
                 "   <jl:set var=\"CONTENTVERIFIED\" value=\"true\"/>\n"+
                 "</testStep>\n";
        for (Section subStep : this.getSubSteps()){
            result = result.concat(subStep.getScriptContent()+"\n");
        }
        return result;
    }

}
