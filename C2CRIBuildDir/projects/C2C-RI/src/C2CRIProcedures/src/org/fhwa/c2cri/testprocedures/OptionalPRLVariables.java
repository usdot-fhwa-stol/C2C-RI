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
public class OptionalPRLVariables {

    private static OptionalPRLVariables optionalPRLVariables;

    private static ArrayList<OptionalPRLVariable> variableList = new ArrayList<OptionalPRLVariable>();

    public static OptionalPRLVariables getInstance(){
        if (optionalPRLVariables==null){
            optionalPRLVariables = new OptionalPRLVariables();
        }
        return optionalPRLVariables;
    }

    private OptionalPRLVariables() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet optionalPRLVariableRS = theDatabase.queryReturnRS("SELECT * FROM OptionalPRLVariables order by ID");
        try {
            while (optionalPRLVariableRS.next()) {

                String variableName = optionalPRLVariableRS.getString("VariableName");
                String needNumber = optionalPRLVariableRS.getString("NeedNumber");
                String unID = optionalPRLVariableRS.getString("UN ID");
                String requirementID = optionalPRLVariableRS.getString("RequirementID");
                String source = optionalPRLVariableRS.getString("Source");
                String description = optionalPRLVariableRS.getString("Description");
                String recordText = optionalPRLVariableRS.getString("RecordText");
                String verifyText = optionalPRLVariableRS.getString("VerifyText");
                String recordScriptText = optionalPRLVariableRS.getString("RecordScript");
                String verifyScriptText = optionalPRLVariableRS.getString("VerifyScript");


                OptionalPRLVariable thisOptionalPRLVariable = new OptionalPRLVariable();
                thisOptionalPRLVariable.setVariableName(variableName);
                thisOptionalPRLVariable.setNeedNumber(needNumber);
                thisOptionalPRLVariable.setUnID(unID);
                thisOptionalPRLVariable.setRequirementID(requirementID);
                thisOptionalPRLVariable.setSource(source);
                thisOptionalPRLVariable.setDescription(description);
                thisOptionalPRLVariable.setRecordText(recordText);
                thisOptionalPRLVariable.setVerifyText(verifyText);
                thisOptionalPRLVariable.setRecordScriptText(recordScriptText);
                thisOptionalPRLVariable.setVerifyScriptText(verifyScriptText);

                variableList.add(thisOptionalPRLVariable);
            }
            optionalPRLVariableRS.close();
            optionalPRLVariableRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        theDatabase.disconnectFromDatabase();


        // TODO code application logic here
        System.out.println("Number of Variables Added = " + variableList.size());

    }

    public static ArrayList<OptionalPRLVariable> getMatchingOptionalPRLVariables(String needNumber, String requirementID) {
        ArrayList<OptionalPRLVariable> returnList = new ArrayList<OptionalPRLVariable>();

        for (OptionalPRLVariable thisVariable : variableList) {
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
        ArrayList<OptionalPRLVariable> responseList;

        OptionalPRLVariables firstOne = new OptionalPRLVariables();
        System.out.println("Sent 2.3.1.1, 3.3.1.1.2");
        responseList = firstOne.getMatchingOptionalPRLVariables("2.3.1.1", "3.3.1.1.2");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.3, 3.3.1.3.1");
        responseList = firstOne.getMatchingOptionalPRLVariables("2.3.1.3", "3.3.1.3.1");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.X, 3.3.1.3.1");
        responseList = firstOne.getMatchingOptionalPRLVariables("2.3.1.X", "3.3.1.3.1");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.3, 3.3.1.3.1.X");
        responseList = firstOne.getMatchingOptionalPRLVariables("2.3.1.X", "3.3.1.3.1");
        printResult(responseList);
        responseList.clear();

    }

    public static void printResult(ArrayList<OptionalPRLVariable> reportList) {
        if (reportList.size() == 0) {
            System.out.println("Nothing returned!!");
        } else {
            for (OptionalPRLVariable thisVariable : reportList) {
                System.out.println("Returned " + thisVariable.getVariableName());
            }
        }

    }
}
