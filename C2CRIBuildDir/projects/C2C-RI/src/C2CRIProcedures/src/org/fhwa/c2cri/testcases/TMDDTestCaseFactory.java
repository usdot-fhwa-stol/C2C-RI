/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testcases;

import java.util.ArrayList;
import org.fhwa.c2cri.testprocedures.*;

/**
 *
 * @author TransCore ITS
 */
public class TMDDTestCaseFactory {

    public static ArrayList<TestCase> makeTestCases(String needID, String dialog, String centerMode, TestProcedure theProcedure) throws Exception {
        ArrayList<TestCase> tmddTestCases = new ArrayList<TestCase>();
        if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
            if (centerMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                TestCase theCase1 = new PUB_EC_TestCase(needID, dialog, centerMode, true, "Any",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase1);
                TestCase theCase2 = new PUB_EC_TestCase(needID, dialog, centerMode,false, "1",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase2);
                TestCase theCase3 = new PUB_EC_TestCase(needID, dialog, centerMode, false, "2",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase3);
                TestCase theCase4 = new PUB_EC_TestCase(needID, dialog, centerMode, false, "3",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase4);
                TestCase theCase5 = new PUB_EC_TestCase(needID, dialog, centerMode, false, "4",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase5);
                TestCase theCase6 = new PUB_EC_TestCase(needID, dialog, centerMode, false, "5",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase6);
                TestCase theCase7 = new PUB_EC_TestCase(needID, dialog, centerMode, false, "6",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase7);
                TestCase theCase8 = new PUB_EC_TestCase(needID, dialog, centerMode, false, "7",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase8);
                TestCase theCase9 = new PUB_EC_TestCase(needID, dialog, centerMode, false, "8",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase9);


            } else {
                TestCase theCase1 = new PUB_OC_TestCase(needID, dialog, centerMode, true, "Any",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase1);
                TestCase theCase2 = new PUB_OC_TestCase(needID, dialog, centerMode,false, "1",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase2);
                TestCase theCase3 = new PUB_OC_TestCase(needID, dialog, centerMode, false, "2",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase3);
                TestCase theCase4 = new PUB_OC_TestCase(needID, dialog, centerMode, false, "3",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase4);
                TestCase theCase5 = new PUB_OC_TestCase(needID, dialog, centerMode, false, "4",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase5);
                TestCase theCase6 = new PUB_OC_TestCase(needID, dialog, centerMode, false, "5",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase6);
                TestCase theCase7 = new PUB_OC_TestCase(needID, dialog, centerMode, false, "6",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase7);
                TestCase theCase8 = new PUB_OC_TestCase(needID, dialog, centerMode, false, "7",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase8);
                TestCase theCase9 = new PUB_OC_TestCase(needID, dialog, centerMode, false, "8",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase9);
            }
        } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
            if (centerMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                TestCase theCase1 = new SUB_EC_TestCase(needID, dialog, centerMode, true, "Any",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase1);
                TestCase theCase2 = new SUB_EC_TestCase(needID, dialog, centerMode,false, "1",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase2);
                TestCase theCase3 = new SUB_EC_TestCase(needID, dialog, centerMode, false, "2",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase3);
                TestCase theCase4 = new SUB_EC_TestCase(needID, dialog, centerMode, false, "3",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase4);
                TestCase theCase5 = new SUB_EC_TestCase(needID, dialog, centerMode, false, "4",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase5);
                TestCase theCase6 = new SUB_EC_TestCase(needID, dialog, centerMode, false, "5",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase6);
                TestCase theCase7 = new SUB_EC_TestCase(needID, dialog, centerMode, false, "6",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase7);
                TestCase theCase8 = new SUB_EC_TestCase(needID, dialog, centerMode, false, "7",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase8);
                TestCase theCase9 = new SUB_EC_TestCase(needID, dialog, centerMode, false, "8",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase9);


            } else {
                TestCase theCase1 = new SUB_OC_TestCase(needID, dialog, centerMode, true, "Any",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase1);
                TestCase theCase2 = new SUB_OC_TestCase(needID, dialog, centerMode,false, "1",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase2);
                TestCase theCase3 = new SUB_OC_TestCase(needID, dialog, centerMode, false, "2",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase3);
                TestCase theCase4 = new SUB_OC_TestCase(needID, dialog, centerMode, false, "3",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase4);
                TestCase theCase5 = new SUB_OC_TestCase(needID, dialog, centerMode, false, "4",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase5);
                TestCase theCase6 = new SUB_OC_TestCase(needID, dialog, centerMode, false, "5",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase6);
                TestCase theCase7 = new SUB_OC_TestCase(needID, dialog, centerMode, false, "6",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase7);
                TestCase theCase8 = new SUB_OC_TestCase(needID, dialog, centerMode, false, "7",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase8);
                TestCase theCase9 = new SUB_OC_TestCase(needID, dialog, centerMode, false, "8",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase9);
            }
        } else if (dialog.endsWith(TMDDParameters.TMDD_REQUEST_SUFFIX)){
            if (centerMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                TestCase theCase1 = new RR_EC_TestCase(needID, dialog, centerMode, true, "Any",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase1);
                TestCase theCase2 = new RR_EC_TestCase(needID, dialog, centerMode,false, "1",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase2);
                TestCase theCase3 = new RR_EC_TestCase(needID, dialog, centerMode, false, "2",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase3);
                TestCase theCase4 = new RR_EC_TestCase(needID, dialog, centerMode, false, "3",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase4);
                TestCase theCase5 = new RR_EC_TestCase(needID, dialog, centerMode, false, "4",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase5);
                TestCase theCase6 = new RR_EC_TestCase(needID, dialog, centerMode, false, "5",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase6);
                TestCase theCase7 = new RR_EC_TestCase(needID, dialog, centerMode, false, "6",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase7);
                TestCase theCase8 = new RR_EC_TestCase(needID, dialog, centerMode, false, "7",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase8);
                TestCase theCase9 = new RR_EC_TestCase(needID, dialog, centerMode, false, "8",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase9);


            } else {
                TestCase theCase1 = new RR_OC_TestCase(needID, dialog, centerMode, true, "Any",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase1);
                TestCase theCase2 = new RR_OC_TestCase(needID, dialog, centerMode,false, "1",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase2);
                TestCase theCase3 = new RR_OC_TestCase(needID, dialog, centerMode, false, "2",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase3);
                TestCase theCase4 = new RR_OC_TestCase(needID, dialog, centerMode, false, "3",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase4);
                TestCase theCase5 = new RR_OC_TestCase(needID, dialog, centerMode, false, "4",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase5);
                TestCase theCase6 = new RR_OC_TestCase(needID, dialog, centerMode, false, "5",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase6);
                TestCase theCase7 = new RR_OC_TestCase(needID, dialog, centerMode, false, "6",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase7);
                TestCase theCase8 = new RR_OC_TestCase(needID, dialog, centerMode, false, "7",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase8);
                TestCase theCase9 = new RR_OC_TestCase(needID, dialog, centerMode, false, "8",theProcedure.getProcedureVariables());
                tmddTestCases.add(theCase9);
            }

        } else {
           throw new Exception("Dialog" + dialog + " is not in named with a recognizable RR, Sub or Pub Suffix format.");

        }
        return tmddTestCases;
    }
}
