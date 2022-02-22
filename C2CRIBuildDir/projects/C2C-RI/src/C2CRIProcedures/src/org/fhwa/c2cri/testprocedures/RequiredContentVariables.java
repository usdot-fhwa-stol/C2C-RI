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
public class RequiredContentVariables {

    private static RequiredContentVariables requiredContentVariables;

    private static ArrayList<RequiredContentVariable> variableList = new ArrayList();

    public static RequiredContentVariables getInstance(){
        if (requiredContentVariables==null){
            requiredContentVariables = new RequiredContentVariables();
        }
        return requiredContentVariables;
    }

    private RequiredContentVariables() {

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        ResultSet mandatoryPRLVariableRS = theDatabase.queryReturnRS("SELECT * FROM TMDDDataValuesLookupQuery order by NeedNumber");
        try {
            while (mandatoryPRLVariableRS.next()) {

                String variableName = mandatoryPRLVariableRS.getString("Element");
                String needNumber = mandatoryPRLVariableRS.getString("NeedNumber");
                String unID = mandatoryPRLVariableRS.getString("UNID");
                String requirementID = mandatoryPRLVariableRS.getString("RequirementID");
                String message = mandatoryPRLVariableRS.getString("Message");
                System.out.println(variableName + "  " + needNumber+" "+requirementID);
                String source ="TMDD volume 1";
                String description = "This element is required to be set to the value specified by the standard.";
                String recordText = "";
                String variableValue = mandatoryPRLVariableRS.getString("Value");
                String verifyText = "Verify that element "+variableName + " is set to "+variableValue+".";
                String recordScriptText = "";
                String verifyScriptText = "";


                RequiredContentVariable thisMandatoryPRLVariable = new RequiredContentVariable();
                thisMandatoryPRLVariable.setVariableName(variableName);
                thisMandatoryPRLVariable.setValidValue(variableValue);
                thisMandatoryPRLVariable.setNeedNumber(needNumber);
                thisMandatoryPRLVariable.setUnID(unID);
                thisMandatoryPRLVariable.setRequirementID(requirementID);
                thisMandatoryPRLVariable.setSource(source);
                thisMandatoryPRLVariable.setDescription(description);
                thisMandatoryPRLVariable.setRecordText(recordText);
                thisMandatoryPRLVariable.setVerifyText(verifyText);
                thisMandatoryPRLVariable.setMessage(message);
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

    public static ArrayList<RequiredContentVariable> getMatchingVariables(String needNumber) {
        ArrayList<RequiredContentVariable> returnList = new ArrayList();

        for (RequiredContentVariable thisVariable : variableList) {
            if (thisVariable.getNeedNumber().equals(needNumber)) {
                    returnList.add(thisVariable);
            }
        }


        return returnList;
    }

    public static ArrayList<String> getRelatedRequirements(String needNumber) {
        ArrayList<String> returnList = new ArrayList();

        for (RequiredContentVariable thisVariable : variableList) {
            if (thisVariable.getNeedNumber().equals(needNumber)) {
                    returnList.add(thisVariable.getRequirementID());
            }
        }

        return returnList;
    }
    
    
    public static void main(String[] args) {
        // Test Out this Class
        ArrayList<RequiredContentVariable> responseList;

        RequiredContentVariables firstOne = new RequiredContentVariables();
        System.out.println("Sent 2.3.4.1.1, 3.3.4.2.1.4");
        responseList = firstOne.getMatchingVariables("1");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.3, 3.3.1.3.1");
        responseList = firstOne.getMatchingVariables("2.3.1.3");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.X, 3.3.1.3.1");
        responseList = firstOne.getMatchingVariables("2.3.1.X");
        printResult(responseList);
        responseList.clear();
        System.out.println("Sent 2.3.1.3, 3.3.1.3.1.X");
        responseList = firstOne.getMatchingVariables("2.3.1.X");
        printResult(responseList);
        responseList.clear();

    }

    public static void printResult(ArrayList<RequiredContentVariable> reportList) {
        if (reportList.size() == 0) {
            System.out.println("Nothing returned!!");
        } else {
            for (RequiredContentVariable thisVariable : reportList) {
                System.out.println("Returned " + thisVariable.getVariableName());
            }
        }

    }
}
