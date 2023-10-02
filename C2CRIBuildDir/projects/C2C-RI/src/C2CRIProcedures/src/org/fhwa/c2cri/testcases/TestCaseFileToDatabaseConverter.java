/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testcases;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.Parameter;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseDataParser;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseDataSource;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseFile;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestCaseFileToDatabaseConverter {

    static TestCaseFile baseTCFile;
    /** The iteration list. */
    static ArrayList<ArrayList<LinkedHashMap<String, Parameter>>> iterationGroupParameterList = new ArrayList<ArrayList<LinkedHashMap<String, Parameter>>>();
//    ArrayList<HashMap<String, Parameter>> groupList = new ArrayList<HashMap<String, Parameter>>();
    /** The group array list. */
    static ArrayList<String[]> iterationGroupArrayList = new ArrayList<String[]>();

    /** The user tc file. */
    TestCaseFile userTCFile;
    
    /** The more rows available. */
    boolean moreRowsAvailable;
        
    
    public static void main(String[] args) {

        String baseFolder = "C:\\C2CRI-Phase2\\C2CRIBuildDir\\projects\\C2C-RI\\src\\TMDDv303\\src\\InfoLayer\\Data";
        String baseStandard = "TMDDv303c";

        File[] dataFiles = finder(baseFolder);
        ArrayList<VariableDefinition> definitionsList = new ArrayList<VariableDefinition>();

        for (int ii = 0; ii < dataFiles.length; ii++) {
            try {
                baseTCFile = new TestCaseFile(dataFiles[ii].toURI());

                baseTCFile.init();

                System.out.println("Parsing " + dataFiles[ii].toURI() + " ...");
                int n = TestCaseDataParser.parsePropertyFile(baseTCFile);

                if (n > 0) {

                    for (int ll = 0; ll < baseTCFile.numIteration(); ll++) {
                        for (int jj = 0; jj < baseTCFile.iterationAt(ll).numGroups(); jj++) {
                            for (int kk = 0; kk < baseTCFile.iterationAt(ll).groupAt(jj).numParameters(); kk++) {
                                if (jj == 0) {
                                    Parameter theParameter = baseTCFile.iterationAt(ll).groupAt(jj).parameterAt(kk);
//                                    System.out.println("Parsed:" + baseStandard + ", " + dataFiles[ii].getName() + ", " + ll + ", " + theParameter.getName() + ", " + theParameter.getDoc() + ", " + theParameter.getType() + ", " + theParameter.isEditable());
                                    String finalValue = theParameter.getValue();
                                    if (theParameter.getValue().contains(TestCaseDataSource.MESSAGESPECVALUE)) {
                                        finalValue = getMessageStringFromSpec(theParameter.getValue(), ll);
                                    } else if (theParameter.getValue().contains(TestCaseDataSource.VALUESPECVALUE)) {
                                        finalValue = getValueStringFromSpec(theParameter.getValue(), ll);
                                    }

                                    VariableDefinition theDefinition = new VariableDefinition();
                                    theDefinition.setStandard(baseStandard);
                                    theDefinition.setTestCase(dataFiles[ii].getName().replace(".data", ""));
                                    theDefinition.setIteration(Integer.toString(ll));
                                    theDefinition.setVariableName(theParameter.getName());
                                    theDefinition.setDescription(theParameter.getDoc());
                                    theDefinition.setDataType(theParameter.getType());
                                    theDefinition.setVariableValue(finalValue);
                                    theDefinition.setValidValues("");
                                    definitionsList.add(theDefinition);
                                }
                            }

                        }
                    }

                } else {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Could not parse property file:\n" + dataFiles[ii].toURI().toString(),
                            "Error in Property File",
                            javax.swing.JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }



        }
        toDatabase(definitionsList);
        System.exit(0);
    }

    public static void toDatabase(ArrayList<VariableDefinition> definitionList) {
        // Write the TestProcedure Summary Info to the Test Procedure Table

        // Write the TestProcedure "steps" to the test procedure steps table

        TMDDDatabase theDatabase = new TMDDDatabase();
        int numWrites = 1;
        int writesBeforeReconnect = 75;
        ArrayList<String> testCaseNames = new ArrayList<String>();
        theDatabase.connectToDatabase();
        for (VariableDefinition definition : definitionList) {
            if (!testCaseNames.contains(definition.getTestCase())) {
//                theDatabase.connectToDatabase();
                theDatabase.queryNoResult("DELETE * FROM TestCasesDataTable WHERE TCName = " + "'" + definition.getTestCase() + "'");
                testCaseNames.add(definition.getTestCase());
//                theDatabase.disconnectFromDatabase();
                if (numWrites % writesBeforeReconnect==0){
                    theDatabase.disconnectFromDatabase();
                    theDatabase.connectToDatabase();
                }
                numWrites++;
            }
        }

        for (VariableDefinition definition : definitionList) {
            theDatabase.queryNoResult("INSERT INTO TestCasesDataTable (Standard,TCName,Iteration,Variable,Description,"
                    + "DataType,"
                    + "ValidValues,VariableValue) VALUES ("
                    + "'" + definition.getStandard() + "',"
                    + "'" + definition.getTestCase() + "',"
                    + "'" + definition.getIteration() + "',"
                    + "'" + definition.getVariableName() + "',"
                    + "'" + definition.getDescription() + "',"
                    + "'" + definition.getDataType() + "',"
                    + "'" + definition.getValidValues() + "',"
                    + "'" + definition.getVariableValue().replace("'", "''") + "')");
            if (numWrites % writesBeforeReconnect==0){
                theDatabase.disconnectFromDatabase();
                theDatabase.connectToDatabase();
            }
                numWrites++;
        }
        theDatabase.disconnectFromDatabase();
    }

 
    /**
     * Merge files.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private void mergeFiles() {
        // Create a Map of the Base Test Case Data File
        for (int ii = 0; ii < baseTCFile.numIteration(); ii++) {

            // Create an Array of groups and a HashMap of parameters (maintaining order) for each iteration.
            String[] iterationGroupArray = new String[baseTCFile.iterationAt(ii).numGroups()];
            ArrayList<LinkedHashMap<String, Parameter>> groupParameterList = new ArrayList<LinkedHashMap<String, Parameter>>();

            // Iterate through each group that exists within the current iteration of the base Test Case Data File.
            for (int jj = 0; jj < baseTCFile.iterationAt(ii).numGroups(); jj++) {
                // Add the group to the array
                iterationGroupArray[jj] = baseTCFile.iterationAt(ii).groupAt(jj).getName();

                // Create a parameter map which stores each parameter defined within this group and this iteration.
                LinkedHashMap<String, Parameter> parameterMap = new LinkedHashMap<String, Parameter>();
                for (int kk = 0; kk < baseTCFile.iterationAt(ii).groupAt(jj).numParameters(); kk++) {
                    Parameter theParameter = baseTCFile.iterationAt(ii).groupAt(jj).parameterAt(kk);
                    parameterMap.put(theParameter.getName(), theParameter);
                }

                // Add the parameter name and all of its details to the group list.
                groupParameterList.add(parameterMap);
            }
            // Store the current set of groups and parameters.
            iterationGroupArrayList.add(iterationGroupArray);
            iterationGroupParameterList.add(groupParameterList);
        }


        // The user can not add additional iterations or groups to those in the base file, but the user can rename a group (for message specification changes).
        for (int ii = 0; ii < baseTCFile.numIteration(); ii++) {
            for (int jj = 0; jj < baseTCFile.iterationAt(ii).numGroups(); jj++) {

                
                if (userTCFile != null && userTCFile.numIteration() > ii) {
                    // The number of groups should be 1 greater than the group (zero-based) index.
                    if (userTCFile.iterationAt(ii).numGroups() > jj) { //

                        
                        // If the user file changes this group name, then update the groupname stored in the array.
                        // Also, clear the existing parameter list at this group index and this iteration.
                        if (!iterationGroupArrayList.get(ii)[jj].equals(userTCFile.iterationAt(ii).groupAt(jj).getName())){
                            iterationGroupArrayList.get(ii)[jj] = userTCFile.iterationAt(ii).groupAt(jj).getName();
                            iterationGroupParameterList.get(ii).get(jj).clear();
                        } 

                        // Get a copy of the parametr map fir this index and group.
                        LinkedHashMap<String, Parameter> parameterMap = iterationGroupParameterList.get(ii).get(jj);                                                    

                        // loop through all parameters for this group in the user test case file.
                        for (int kk = 0; kk < userTCFile.iterationAt(ii).groupAt(jj).numParameters(); kk++) {
                            Parameter theParameter = userTCFile.iterationAt(ii).groupAt(jj).parameterAt(kk);

                            // If the user file attempts to replace a base variable then verify that it is editable
                            if (parameterMap.containsKey(theParameter.getName())) {
                                Parameter originalParameter = parameterMap.get(theParameter.getName());
                                if (originalParameter.isEditable()) {
                                    iterationGroupParameterList.get(ii).get(jj).put(theParameter.getName(), theParameter);
                                    System.out.println("Updating a parameter = " + theParameter.getName() + " ii =" + ii + " jj=" + jj + " kk=" + kk);
                                }
                            } else {  // Add the new user file parameter
                                iterationGroupParameterList.get(ii).get(jj).put(theParameter.getName(), theParameter);
                                System.out.println("Adding a brand new user parameter = " + theParameter.getName() + " ii =" + ii + " jj=" + jj + " kk=" + kk);
                            }
                        }

                    } else {  // Skip extra groups
                    }
                } else { // Skip extra iterations
                }
            }
        }
    }
    

    public static File[] finder(String dirName) {
        File dir = new File(dirName);
        return dir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String filename) {
                return filename.endsWith(".data");
            }
        });
    }

    private static String getValueStringFromSpec(String inputValue, int iterationNumber) {
        String result = "";
        String groupName = inputValue.replace(TestCaseDataSource.VALUESPECVALUE, "");
        for (int jj = 0; jj < baseTCFile.iterationAt(iterationNumber).numGroups(); jj++) {
            if (baseTCFile.iterationAt(iterationNumber).groupAt(jj).getName().equals(groupName)) {
                for (int kk = 0; kk < baseTCFile.iterationAt(iterationNumber).groupAt(jj).numParameters(); kk++) {
                    Parameter thisParameter = baseTCFile.iterationAt(iterationNumber).groupAt(jj).parameterAt(kk);
                    result = result.concat(result.isEmpty() ? "ValueSpecification: [ " + thisParameter.getName() + " = " + thisParameter.getValue() : "; " + thisParameter.getName() + " = " + thisParameter.getValue());
                }
            }
        }
        result = result.concat(!result.isEmpty() ? "]" : "");
        return result;
    }

    private static String getMessageStringFromSpec(String inputValue, int iterationNumber) {
        String result = "";
        String groupName = inputValue.replace(TestCaseDataSource.MESSAGESPECVALUE, "");
        for (int jj = 0; jj < baseTCFile.iterationAt(iterationNumber).numGroups(); jj++) {
            if (baseTCFile.iterationAt(iterationNumber).groupAt(jj).getName().equals(groupName)) {
                for (int kk = 0; kk < baseTCFile.iterationAt(iterationNumber).groupAt(jj).numParameters(); kk++) {
                    Parameter thisParameter = baseTCFile.iterationAt(iterationNumber).groupAt(jj).parameterAt(kk);
                    result = result.concat(result.isEmpty() ? "MessageSpecification: [ " + thisParameter.getName() + " = " + thisParameter.getValue() : "; " + thisParameter.getName() + " = " + thisParameter.getValue());
                }

            }
        }
        result = result.concat(!result.isEmpty() ? "]" : "");

        return result;
    }

    static class VariableDefinition {

        String standard = "";
        String testCase = "";
        String variableName = "";
        String dataType = "";
        String description = "";
        String validValues = "";
        String iteration = "";
        String variableValue = "";

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getIteration() {
            return iteration;
        }

        public void setIteration(String iteration) {
            this.iteration = iteration;
        }

        public String getStandard() {
            return standard;
        }

        public void setStandard(String standard) {
            this.standard = standard;
        }

        public String getTestCase() {
            return testCase;
        }

        public void setTestCase(String testCase) {
            this.testCase = testCase;
        }

        public String getValidValues() {
            return validValues;
        }

        public void setValidValues(String validValues) {
            this.validValues = validValues;
        }

        public String getVariableName() {
            return variableName;
        }

        public void setVariableName(String variableName) {
            this.variableName = variableName;
        }

        public String getVariableValue() {
            return variableValue;
        }

        public void setVariableValue(String variableValue) {
            this.variableValue = variableValue;
        }
    }
}
