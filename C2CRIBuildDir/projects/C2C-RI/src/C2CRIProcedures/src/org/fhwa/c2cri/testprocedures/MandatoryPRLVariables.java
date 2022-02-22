/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

import java.sql.ResultSet;
import java.util.ArrayList;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class MandatoryPRLVariables {

    private static MandatoryPRLVariables mandatoryPRLVariables;

    private static ArrayList<MandatoryPRLVariable> variableList = new ArrayList<MandatoryPRLVariable>();

    public static MandatoryPRLVariables getInstance(){
        if (mandatoryPRLVariables==null){
            mandatoryPRLVariables = new MandatoryPRLVariables();
        }
        return mandatoryPRLVariables;
    }

    private MandatoryPRLVariables() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet mandatoryPRLVariableRS = theDatabase.queryReturnRS("SELECT * FROM MandatoryPRLVariables order by ID");
        try {
            while (mandatoryPRLVariableRS.next()) {

                String variableName = mandatoryPRLVariableRS.getString("VariableName");
                String needNumber = mandatoryPRLVariableRS.getString("NeedNumber");
                String unID = mandatoryPRLVariableRS.getString("UN ID");
                String requirementID = mandatoryPRLVariableRS.getString("RequirementID");
                String source = mandatoryPRLVariableRS.getString("Source");
                String description = mandatoryPRLVariableRS.getString("Description");
                String recordText = mandatoryPRLVariableRS.getString("RecordText");
                String verifyText = mandatoryPRLVariableRS.getString("VerifyText");
                String recordScriptText = mandatoryPRLVariableRS.getString("RecordScript");
                String verifyScriptText = mandatoryPRLVariableRS.getString("VerifyScript");


                MandatoryPRLVariable thisMandatoryPRLVariable = new MandatoryPRLVariable();
                thisMandatoryPRLVariable.setVariableName(variableName);
                thisMandatoryPRLVariable.setNeedNumber(needNumber);
                thisMandatoryPRLVariable.setUnID(unID);
                thisMandatoryPRLVariable.setRequirementID(requirementID);
                thisMandatoryPRLVariable.setSource(source);
                thisMandatoryPRLVariable.setDescription(description);
                thisMandatoryPRLVariable.setRecordText(recordText);
                thisMandatoryPRLVariable.setVerifyText(verifyText);
                thisMandatoryPRLVariable.setRecordScriptText(recordScriptText);
                thisMandatoryPRLVariable.setVerifyScriptText("<SubSteps>");
                
                
                variableList.add(thisMandatoryPRLVariable);
            }
            mandatoryPRLVariableRS.close();
            mandatoryPRLVariableRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        theDatabase.disconnectFromDatabase();


        // TODO code application logic here
        System.out.println("Number of Variables Added = " + variableList.size());

    }

    public static ArrayList<MandatoryPRLVariable> getMatchingMandatoryPRLVariables(String needNumber, String requirementID) {
        ArrayList<MandatoryPRLVariable> returnList = new ArrayList<MandatoryPRLVariable>();

        for (MandatoryPRLVariable thisVariable : variableList) {
            if (thisVariable.getNeedNumber().equals(needNumber)) {
                if (thisVariable.getRequirementID().equals(requirementID)) {
                    returnList.add(thisVariable);
                }
            }
        }


        return returnList;
    }

    public static void main(String[] args) {
        // Test Out this Class
        ArrayList<MandatoryPRLVariable> responseList;

        MandatoryPRLVariables firstOne = new MandatoryPRLVariables();
        System.out.println("Sent 2.3.1.1, 3.3.1.1.4");
        responseList = firstOne.getMatchingMandatoryPRLVariables("1", "3.3.1.1.4");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.3, 3.3.1.3.1");
        responseList = firstOne.getMatchingMandatoryPRLVariables("2.3.1.3", "3.3.1.3.1");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.X, 3.3.1.3.1");
        responseList = firstOne.getMatchingMandatoryPRLVariables("2.3.1.X", "3.3.1.3.1");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.3, 3.3.1.3.1.X");
        responseList = firstOne.getMatchingMandatoryPRLVariables("2.3.1.X", "3.3.1.3.1");
        printResult(responseList);
        responseList.clear();

    }

    public static void printResult(ArrayList<MandatoryPRLVariable> reportList) {
        if (reportList.size() == 0) {
            System.out.println("Nothing returned!!");
        } else {
            for (MandatoryPRLVariable thisVariable : reportList) {
                System.out.println("Returned " + thisVariable.getVariableName());
            }
        }

    }
}
