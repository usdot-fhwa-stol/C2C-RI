/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities;

import java.util.ArrayList;

/**
 *
 * @author TransCore ITS
 */
public class TestProcedure {

    private String name;
    private String id;
    private String title;
    private String description;
    private ArrayList<Variable> variables = new ArrayList<Variable>();
    private String requirements;
    private String pass_fail_Criteria = "The SUT shall pass every verification step included within the Test Procedure to pass the Test Procedure.";
    private ArrayList<TestStep> testSteps = new ArrayList<TestStep>();
    private Integer stepCount = 1;

/**
 *
 * @param name
 * @param id
 * @param title
 * @param description
 * @param requirements
 */    public TestProcedure(String name, String id, String title, String description, String requirements){
        this.name = name;
        this.id = id;
        this.title = title;
        this.description = description;
        this.requirements = requirements;

    }


    public void printProcedure(){
        System.out.println("Name: "+ this.name);
        System.out.println("ID: "+ this.id);
        System.out.println("Title: "+ this.title);
        System.out.println("Description: "+ this.description);
        System.out.println("Requirements: "+ this.requirements);
        System.out.println("Variables: "+variables.toString());
        System.out.println("Pass/Fail Criteria: "+ this.pass_fail_Criteria);
        for (TestStep thisStep : testSteps){
            System.out.println(thisStep.getId()+" | "+thisStep.getDescription()+" | "+thisStep.getResults());
        }
    }
    public void addVariable(Variable newVariable){
        variables.add(newVariable);
    }

    public void addTestStep(TestStep testStep){
        testStep.setId(stepCount.toString()+".");
        testSteps.add(testStep);
        stepCount++;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass_fail_Criteria() {
        return pass_fail_Criteria;
    }

    public void setPass_fail_Criteria(String pass_fail_Criteria) {
        this.pass_fail_Criteria = pass_fail_Criteria;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public ArrayList<TestStep> getTestSteps() {
        return testSteps;
    }

    public void setTestSteps(ArrayList<TestStep> testSteps) {
        this.testSteps = testSteps;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Variable> getVariables() {
        return variables;
    }

    public void setVariables(ArrayList<Variable> variables) {
        this.variables = variables;
    }


    public void writeResults() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'DialogTest'");


            theDatabase.queryNoResult("INSERT INTO TestProceduresTable ([Procedure],ProcedureID,"
                    + "Description,[Variables],requirements,ProcedureTitle) VALUES ("
                    + "'" + this.name + "',"
                    + "'" + this.id + "',"
                    + "'" + this.description + "',"
                    + "'Variables',"
                    + "'" + this.getRequirements() + "',"
                    + "'" + this.title + "')");

        for (TestStep thisStep : this.getTestSteps()) {
            theDatabase.queryNoResult("INSERT INTO TestProcedureStepsTable ([Procedure],StepNumber,"
                    + "Step,[Action],Result,Requirements,PassFail) VALUES ("
                    + "'" + this.name + "',"
                    + "'" + thisStep.getId() + "',"
                    + "'" + thisStep.getId() + "',"
                    + "'" + thisStep.getDescription() + "',"
                    + "'" + thisStep.getResults() + "',"
                    + "'" + thisStep.getId() + "',"
                    + "'" + thisStep.getResults() + "')");
        }

        theDatabase.disconnectFromDatabase();
    }

}
