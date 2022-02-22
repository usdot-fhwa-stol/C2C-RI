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
public abstract class TestProcedureSection implements Section {

    protected String sectionType;
    protected ArrayList<Variable> variables = new ArrayList<Variable>();
    protected ArrayList<Section> subSections = new ArrayList<Section>();
    private HashMap<String, String> sectionElementMap = new HashMap<String, String>();
    private Integer initialStepNumber=1;
    private Integer nextStepNumber=2;
    private String parentStepPath="";

    public abstract String getScriptContent();
    
    public abstract ArrayList<TestProcedureStep> getStepsforDatabase();

    public final ArrayList<Variable> getVariablesList() {
        return variables;
    }

    public void setStartStepNumber(Integer stepNumber) {
        initialStepNumber = stepNumber;
        Integer currentStep = initialStepNumber;
        for (Section thisSection : subSections){
                thisSection.setParentStepPath(parentStepPath);
                thisSection.setStartStepNumber(currentStep);

//                for (TestProcedureStep thisProcedureStep : thisSection.getStepsforDatabase()){
//                    for (Section thisSubSection : thisProcedureStep.getSubSteps()){
//                        thisSubSection.setStartStepNumber(currentStep);
//                    }
//                }
                currentStep = currentStep + thisSection.getStepsCount();
        }
        nextStepNumber = currentStep+1;
    }

    public final Integer getInitialStepNumber(){
        return initialStepNumber;
    }

    public final Integer getNextStepNumber(){
        Integer theNumber = nextStepNumber;
        nextStepNumber++;
        return theNumber;
    }

    public final void addSection(Section newSection){
        newSection.setParentStepPath(parentStepPath);
        subSections.add(newSection);
        Integer currentStep = nextStepNumber;
        newSection.setStartStepNumber(currentStep);
        nextStepNumber = currentStep + newSection.getStepsCount();

    }

    public final Integer getStepsCount() {
        Integer stepsCount = 0;
        for (Section thisSection : subSections){
            stepsCount = stepsCount + thisSection.getStepsCount();
        }

        return stepsCount;
    }

    public String getParentStepPath() {
        return parentStepPath;
    }

    public void setParentStepPath(String parentStepPath) {
        this.parentStepPath = parentStepPath;
    }


}
