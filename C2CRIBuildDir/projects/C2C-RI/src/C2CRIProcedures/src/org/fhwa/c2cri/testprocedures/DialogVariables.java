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
public class DialogVariables {

    private ArrayList<DialogVariable> variableList = new ArrayList<DialogVariable>();

    public DialogVariables() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet procedureVariableRS = theDatabase.queryReturnRS("SELECT * FROM DialogVariables order by ID");
        try {
            while (procedureVariableRS.next()) {

                String variableName = procedureVariableRS.getString("VariableName");
                String relatedCenterMode = procedureVariableRS.getString("RelatedCenterMode");
                String relatedDialogType = procedureVariableRS.getString("RelatedDialogType");
                String source = procedureVariableRS.getString("Source");
                String description = procedureVariableRS.getString("Description");
                String validCaseDefaultValue = procedureVariableRS.getString("ValidCaseDefaultValue");
                String invalidCaseDefaultValue = procedureVariableRS.getString("InvalidCaseDefaultValue");
                String dataType = procedureVariableRS.getString("DataType");
                String validValues = procedureVariableRS.getString("ValidValues");
                String userEditable = procedureVariableRS.getString("IsUserEditable");
                String recordText = procedureVariableRS.getString("RecordText");
                String verifyText = procedureVariableRS.getString("VerifyText");
                String recordScriptText = procedureVariableRS.getString("RecordScriptText");
                String verifyScriptText = procedureVariableRS.getString("VerifyScriptText");
                boolean useInFirstPart = procedureVariableRS.getBoolean("UseInFirstPart");
                boolean useInSecondPart = procedureVariableRS.getBoolean("UseInSecondPart");
                boolean returnVariable = procedureVariableRS.getBoolean("ReturnVariable");
                boolean localVariable = procedureVariableRS.getBoolean("LocalVariable");
                String relatedKeyword = procedureVariableRS.getString("RelatedKeyword");

                DialogVariable thisDialogVariable = new DialogVariable();
                thisDialogVariable.setVariableName(variableName);
                thisDialogVariable.setRelatedCenterMode(relatedCenterMode);
                thisDialogVariable.setRelatedDialogType(relatedDialogType);
                thisDialogVariable.setSource(source);
                thisDialogVariable.setDescription(description);
                thisDialogVariable.setValidCaseDefaultValue(validCaseDefaultValue);
                thisDialogVariable.setInvalidCaseDefaultValue(invalidCaseDefaultValue);
                thisDialogVariable.setDataType(dataType);
                thisDialogVariable.setValidValues(validValues);
                thisDialogVariable.setUserEditable(Boolean.valueOf(userEditable));
                thisDialogVariable.setRecordText(recordText);
                thisDialogVariable.setVerifyText(verifyText);
                thisDialogVariable.setRecordScriptText(recordScriptText);
                thisDialogVariable.setVerifyScriptText(verifyScriptText);
                thisDialogVariable.setUseInFirstPart(useInFirstPart);
                thisDialogVariable.setUseInSecondPart(useInSecondPart);
                thisDialogVariable.setReturnVariable(returnVariable);
                thisDialogVariable.setLocalVariable(localVariable);
                thisDialogVariable.setRelatedKeyword(relatedKeyword);

                variableList.add(thisDialogVariable);
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

    public ArrayList<DialogVariable> getMatchingDialogVariables(String dialog, String centerMode) {
        ArrayList<DialogVariable> returnList = new ArrayList<DialogVariable>();
        String dialogType = "";
        if ((dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) || (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX))) {
            dialogType = "Publication";
            for (DialogVariable thisVariable : variableList) {
                if (thisVariable.getRelatedCenterMode().contains(centerMode)) {
                    if (thisVariable.getRelatedDialogType().contains(dialogType)) {
                        returnList.add(thisVariable);
                    }
                }
            }

            dialogType = "Subscription";
            for (DialogVariable thisVariable : variableList) {
                if (thisVariable.getRelatedCenterMode().contains(centerMode)) {
                    if (thisVariable.getRelatedDialogType().contains(dialogType)) {
                        returnList.add(thisVariable);
                    }
                }
            }

        } else {
            dialogType = "Request";
            for (DialogVariable thisVariable : variableList) {
                if (thisVariable.getRelatedCenterMode().contains(centerMode)) {
                    if (thisVariable.getRelatedDialogType().contains(dialogType)) {
                        returnList.add(thisVariable);
                    }
                }
            }

        }



        return returnList;
    }

    public static void main(String[] args) {
        // Test Out this Class
        ArrayList<DialogVariable> responseList;

        DialogVariables firstOne = new DialogVariables();
        System.out.println("Sent Update, EC");
        responseList = firstOne.getMatchingDialogVariables("Update", "EC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Update, OC");
        responseList = firstOne.getMatchingDialogVariables("Update", "OC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Subscription, OC");
        responseList = firstOne.getMatchingDialogVariables("Subscription", "OC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Subscription, EC");
        responseList = firstOne.getMatchingDialogVariables("Subscription", "EC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Request, EC");
        responseList = firstOne.getMatchingDialogVariables("Request", "EC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Request, OC");
        responseList = firstOne.getMatchingDialogVariables("Request", "OC");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Request, Invalid");
        responseList = firstOne.getMatchingDialogVariables("Request", "Invalid");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Update, Invalid");
        responseList = firstOne.getMatchingDialogVariables("Update", "Invalid");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Subscription, Invalid");
        responseList = firstOne.getMatchingDialogVariables("Subscription", "Invalid");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent Garbage, Invalid");
        responseList = firstOne.getMatchingDialogVariables("Garbage", "Invalid");
        printResult(responseList);
        responseList.clear();

    }

    public static void printResult(ArrayList<DialogVariable> reportList) {
        if (reportList.size() == 0) {
            System.out.println("Nothing returned!!");
        } else {
            for (DialogVariable thisVariable : reportList) {
                System.out.println("Returned " + thisVariable.getVariableName());
            }
        }

    }
}
