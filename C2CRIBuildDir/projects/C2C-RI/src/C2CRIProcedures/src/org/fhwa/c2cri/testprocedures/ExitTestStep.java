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
public class ExitTestStep extends TestProcedureStep{

    public ExitTestStep(){
        this.setDescription("EXIT");
        this.setResults("NA");
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
        result = "\n"+"<postcondition>\n"+"<testStep functionId=\" Step " + this.getId() +" "+this.getDescription()+"\"   passfailResult=\"False\"/>\n"+"</postcondition>\n";
        return result;
    }

}
