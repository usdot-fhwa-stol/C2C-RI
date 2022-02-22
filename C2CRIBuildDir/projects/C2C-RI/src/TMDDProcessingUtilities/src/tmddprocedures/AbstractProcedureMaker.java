/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddprocedures;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TransCore ITS
 */
public abstract class AbstractProcedureMaker {
    List<ProcedureStep> testProcedure = new ArrayList<ProcedureStep>();
    static String procedureName;

    private AbstractProcedureMaker(){};


    public AbstractProcedureMaker(String procedureName){
        this.procedureName = procedureName;
    }

    public abstract void makeProcedure();


    public void writeDefaultProcedureStep(){
        ProcedureStep defaultStep = new ProcedureStep(procedureName);
        defaultStep.setStepNumber("1.");
        defaultStep.setStepText("No Steps Defined");
        defaultStep.setAction("No Actions Defined for this Procedure");
        defaultStep.setResults("");
        defaultStep.setRequirements("No Optional Requirements");
        defaultStep.setPassFail("");
        testProcedure.add(defaultStep);
    }
    public void writeProcedure(){
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        theDatabase.queryNoResult("DELETE * FROM TestProcedureStepsTable WHERE Procedure = '"+procedureName+"'");

        // If no procedures were created for this procedure, create the default test step content.
        if (testProcedure.size() == 0)writeDefaultProcedureStep();

        for (ProcedureStep thisStep : testProcedure){
            theDatabase.queryNoResult("INSERT INTO TestProcedureStepsTable ([Procedure],StepNumber,"
                    + "Step,[Action],Result,Requirements,PassFail) VALUES ("+
                    "'"+thisStep.procedureName+"'," +
                    ""+thisStep.getStepNumber()+","+
                    "'"+thisStep.getStepText()+"',"+
                    "'"+thisStep.getAction()+"',"+
                    "'"+thisStep.getResults()+"',"+
                    "'"+thisStep.getRequirements()+"',"+
                    "'"+thisStep.getPassFail()+"')");
        }

        theDatabase.disconnectFromDatabase();
    }
}
