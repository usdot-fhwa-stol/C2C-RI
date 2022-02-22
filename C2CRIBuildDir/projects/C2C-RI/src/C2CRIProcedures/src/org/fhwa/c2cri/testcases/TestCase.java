/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testcases;

import java.util.ArrayList;
import org.fhwa.c2cri.testprocedures.Variable;

/**
 *
 * @author TransCore ITS
 */
public interface TestCase extends TestCaseExporter {
    public String getTestCaseID();
    public String getTestCaseTitle();
    public String getTestCaseDescription();
    public ArrayList<Variable> getTestCaseVariables();
}
