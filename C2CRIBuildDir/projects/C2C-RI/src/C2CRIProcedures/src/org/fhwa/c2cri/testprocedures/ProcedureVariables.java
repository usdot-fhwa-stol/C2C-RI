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
public class ProcedureVariables {

    private ArrayList<ProcedureVariable> variableList = new ArrayList<ProcedureVariable>();

    public ProcedureVariables() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM ProcedureVariables order by ID");
        try {
            while (procedureVariableRS.next()) {

                String variableName = procedureVariableRS.getString("VariableName");
                String relatedCenterMode = procedureVariableRS.getString("RelatedCenterMode");
                String relatedDialogType = procedureVariableRS.getString("RelatedDialogType");
                String source = procedureVariableRS.getString("Source");
                String description = procedureVariableRS.getString("Description");
                String validCaseDefaultValue = procedureVariableRS.getString("ValidCaseDefaultValue");
                String dataType = procedureVariableRS.getString("DataType");
                String validValues = procedureVariableRS.getString("ValidValues");
                String userEditable = procedureVariableRS.getString("IsUserEditable");
                String invalidCaseDefaultValue = procedureVariableRS.getString("InvalidCaseDefaultValue");
                String recordText = procedureVariableRS.getString("RecordText");
                String verifyText = procedureVariableRS.getString("VerifyText");
                String recordScriptText = procedureVariableRS.getString("RecordScriptText");
                String verifyScriptText = procedureVariableRS.getString("VerifyScriptText");

                ProcedureVariable thisProcedureVariable = new ProcedureVariable();
                thisProcedureVariable.setVariableName(variableName);
                thisProcedureVariable.setRelatedCenterMode(relatedCenterMode);
                thisProcedureVariable.setRelatedDialogType(relatedDialogType);
                thisProcedureVariable.setSource(source);
                thisProcedureVariable.setDescription(description);
                thisProcedureVariable.setValidCaseDefaultValue(validCaseDefaultValue);
                thisProcedureVariable.setDataType(dataType);
                thisProcedureVariable.setValidValues(validValues);
                thisProcedureVariable.setUserEditable(Boolean.valueOf(userEditable));
                thisProcedureVariable.setInvalidCaseDefaultValue(invalidCaseDefaultValue);
                thisProcedureVariable.setRecordText(recordText);
                thisProcedureVariable.setVerifyText(verifyText);
                thisProcedureVariable.setRecordScriptText(recordScriptText);
                thisProcedureVariable.setVerifyScriptText(verifyScriptText);

                variableList.add(thisProcedureVariable);
            }
            procedureVariableRS.close();
            procedureVariableRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        theDatabase.disconnectFromDatabase();


        // TODO code application logic here
        System.out.println("Number of Variables Added = " + variableList.size());

    }

    public ArrayList<ProcedureVariable> getMatchingProcedureVariables(String dialog, String centerMode) {
        ArrayList<ProcedureVariable> returnList = new ArrayList<ProcedureVariable>();
        String dialogType = "";
        if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
            dialogType = "Publication";
        } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
            dialogType = "Subscription";
        } else {
            dialogType = "Request";
        }

        for (ProcedureVariable thisVariable : variableList) {
            if (thisVariable.getRelatedCenterMode().contains(centerMode)) {
                if (thisVariable.getRelatedDialogType().contains(dialogType)) {
                    returnList.add(thisVariable);
                }
            }
        }


        return returnList;
    }

    public static void main(String[] args) {
        // Test Out this Class
        ArrayList<ProcedureVariable> responseList;

        ProcedureVariables firstOne = new ProcedureVariables();
        System.out.println("Sent Update, EC");
        responseList = firstOne.getMatchingProcedureVariables("Update", "EC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Update, OC");
        responseList = firstOne.getMatchingProcedureVariables("Update", "OC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Subscription, OC");
        responseList = firstOne.getMatchingProcedureVariables("Subscription", "OC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Subscription, EC");
        responseList = firstOne.getMatchingProcedureVariables("Subscription", "EC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Request, EC");
        responseList = firstOne.getMatchingProcedureVariables("Request", "EC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Request, OC");
        responseList = firstOne.getMatchingProcedureVariables("Request", "OC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Request, Invalid");
        responseList = firstOne.getMatchingProcedureVariables("Request", "Invalid");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Update, Invalid");
        responseList = firstOne.getMatchingProcedureVariables("Update", "Invalid");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Subscription, Invalid");
        responseList = firstOne.getMatchingProcedureVariables("Subscription", "Invalid");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Garbage, Invalid");
        responseList = firstOne.getMatchingProcedureVariables("Garbage", "Invalid");
        printResult(responseList);
        responseList.clear();

    }

    public static void printResult(ArrayList<ProcedureVariable> reportList) {
        if (reportList.size() == 0) {
            System.out.println("Nothing returned!!");
        } else {
            for (ProcedureVariable thisVariable : reportList) {
                System.out.println("Returned " + thisVariable.getVariableName());
            }
        }

    }
}
