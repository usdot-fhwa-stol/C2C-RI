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
public interface Section {
    public ArrayList<TestProcedureStep> getStepsforDatabase();
    public String getScriptContent();
    public ArrayList<Variable> getVariablesList();
    public void setStartStepNumber(Integer stepNumber);
    public Integer getStepsCount();
    public void setParentStepPath(String parentStepPath);
    public String getParentStepPath();
}
