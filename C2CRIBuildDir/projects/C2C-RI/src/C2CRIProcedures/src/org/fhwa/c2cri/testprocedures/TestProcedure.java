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
public interface TestProcedure extends ProcedureExporter {
    public String getProcedureID();
    public String getProcedureTitle();
    public String getProcedureDescription();
    public ArrayList<Variable> getProcedureVariables();
}
