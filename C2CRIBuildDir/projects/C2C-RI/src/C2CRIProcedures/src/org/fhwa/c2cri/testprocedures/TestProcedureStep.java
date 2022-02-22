/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testprocedures;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author TransCore ITS
 */
public abstract class TestProcedureStep implements Section{
    
    private String id;
    private String description;
    private String results;
    private String stepType;
    private HashMap<String,String> inputVariableMap = new HashMap<String,String>();
    private HashMap<String,String> outputVariableMap = new HashMap<String,String>();
    private ArrayList<Section> subSteps = new ArrayList<Section>();
    private String primaryStepId;
    private boolean isSubStep=false;
    private Integer stepNumber;
    private String parentStepPath="";

    public TestProcedureStep(){
    }

    public TestProcedureStep(String id, String description, String results){
        this.id = id;
        this.description = description;
        this.results = results;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return (parentStepPath.equals("")||(parentStepPath==null))?stepNumber.toString():parentStepPath+"."+stepNumber.toString();
    }

    public void setId(String id) {
        this.id = id;
        stepNumber = Integer.parseInt(id);
        System.err.println("WARNING *****Someone just called setID in TestProcedureStep******");
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public abstract ArrayList<TestProcedureStep> getStepsforDatabase();

    public abstract String getScriptContent();

    public ArrayList<Variable> getVariablesList() {
        ArrayList<Variable> thisVariableList = new ArrayList<Variable>();
        return thisVariableList;
    }

    public void setStartStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
        id = stepNumber.toString();
        
        Integer subStepCount = 1;
        for (Section thisSubStep : this.subSteps){
            if (thisSubStep != null){
              thisSubStep.setParentStepPath((this.parentStepPath.equals(""))?stepNumber.toString():this.parentStepPath+"."+stepNumber.toString());
              thisSubStep.setStartStepNumber(subStepCount);
              subStepCount++;
            } else{
                System.err.println("WARNING ***** Referenced Substep is null?**********");
            }
        }

    }

    public Integer getStepsCount() {
        return 1;
    }

    public boolean isIsSubStep() {
        return isSubStep;
    }

    public void setIsSubStep(boolean isSubStep) {
        this.isSubStep = isSubStep;
    }

    public String getPrimaryStepId() {
        return primaryStepId;
    }

    public void setPrimaryStepId(String primaryStepId) {
        this.primaryStepId = primaryStepId;
    }

    public ArrayList<Section> getSubSteps() {
        return subSteps;
    }

    public void setSubSteps(ArrayList<Section> subSteps) {
        this.subSteps = subSteps;
    }

    public final String getParentStepPath() {
        return parentStepPath;
    }

    public final void setParentStepPath(String parentStepPath) {
        this.parentStepPath = (parentStepPath==null)?"":parentStepPath;
        if (stepNumber == null)stepNumber = 0;
        for (Section thisSubStep : this.subSteps){
            if (thisSubStep != null){
            thisSubStep.setParentStepPath((this.parentStepPath.equals(""))?stepNumber.toString():this.parentStepPath+"."+stepNumber.toString());
            } else{
                System.err.println("WARNING ***** Referenced Substep is null?**********");
            }
        }
    }


}
