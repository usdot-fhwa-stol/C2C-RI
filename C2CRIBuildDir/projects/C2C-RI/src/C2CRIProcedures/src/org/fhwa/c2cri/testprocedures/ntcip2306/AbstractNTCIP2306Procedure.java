/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures.ntcip2306;

import org.fhwa.c2cri.testprocedures.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import tmddv3verification.DesignElement;
import tmddv3verification.NeedHandler;
import tmddv3verification.Requirement;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public abstract class AbstractNTCIP2306Procedure implements TestProcedure {

    protected ArrayList<String> relatedRequirements = new ArrayList<String>();
    protected ArrayList<Variable> procedureVariables = new ArrayList<Variable>();
    protected ArrayList<TestProcedureSection> subSections = new ArrayList<TestProcedureSection>();
    private String testProcedureID;
    private String testProcedureTitle;
    private String testProcedureDescription;
    private String testProcedureProductionDate;

    protected void initializeSummaryInformation(String target, String profile, String centerMode) {
        NTCIP2306TestProcedureNamer procedureNameMaker = new NTCIP2306TestProcedureNamer(target, profile, centerMode);
        testProcedureID = procedureNameMaker.getProcedureID();
        testProcedureTitle = procedureNameMaker.getProcedureTitle();
        testProcedureDescription = procedureNameMaker.getProcedureDescription();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        testProcedureProductionDate = dateFormat.format(date);
        if (target.equals(NTCIP2306Parameters.NTCIP2306_WSDL_TARGET)){
            relatedRequirements = NTCIP2306Specifications.getInstance().getWSDLRequirements();
        } else {
            relatedRequirements = NTCIP2306Specifications.getInstance().getRequirements(target, profile, centerMode);
        }
    }

    public String getProcedureDescription() {
        return testProcedureDescription;
    }

    public String getProcedureID() {
        return testProcedureID;
    }

    public String getProcedureTitle() {
        return testProcedureTitle;
    }

    public ArrayList<Variable> getProcedureVariables() {
        return procedureVariables;
    }

    public String getTestProcedureProductionDate() {
        return testProcedureProductionDate;
    }


    public final void toDatabase() {
        // Write the TestProcedure Summary Info to the Test Procedure Table

        // Write the TestProcedure "steps" to the test procedure steps table

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        theDatabase.queryNoResult("DELETE * FROM TestProceduresTable WHERE ProcedureID = " + "'" + this.testProcedureID + "'");
        theDatabase.queryNoResult("DELETE * FROM TestProcedureStepsTable WHERE [Procedure] = " + "'" + this.testProcedureID + "'");

        String requirements = "";
        for (String theRequirement : this.relatedRequirements) {
            requirements = requirements.concat(theRequirement + "\n");
        }

        String variables = "";
        for (Variable theVariable : this.procedureVariables) {
//            variables = variables.concat(theVariable.getName() + " : " + theVariable.getSource() + "\n");
            variables = variables.concat(theVariable.getName() + " [" + theVariable.getDataType() + "]\n");
        }

        theDatabase.queryNoResult("INSERT INTO TestProceduresTable ([Procedure],ProcedureID,"
                + "Description,[Variables],requirements,ProcedureTitle) VALUES ("
                + "'" + this.testProcedureID + "',"
                + "'" + this.testProcedureID + "',"
                + "'" + this.getProcedureDescription() + "',"
                + "'" + variables + "',"
                + "'" + requirements + "',"
                + "'" + this.testProcedureTitle + "')");


        for (Section thisSection : subSections) {
            for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
                theDatabase.queryNoResult("INSERT INTO TestProcedureStepsTable ([Procedure],StepNumber,"
                        + "Step,[Action],Result,Requirements,PassFail) VALUES ("
                        + "'" + this.testProcedureID + "',"
                        + "'" + thisStep.getId() + "',"
                        + "'" + thisStep.getId() + "',"
                        + "'" + thisStep.getDescription() + "',"
                        + "'" + thisStep.getResults() + "',"
                        + "'" + thisStep.getId() + "',"
                        + "'" + thisStep.getResults() + "')");
            }

        }

        theDatabase.disconnectFromDatabase();
    }

    public final void toScriptFile(String path, String fileName) {
		Path newFile = Paths.get(path + File.separatorChar + fileName);

        if (Files.exists(newFile)) 
		{
            try
			{
				Files.delete(newFile);
			}
			catch (IOException oEx)
			{
				LogManager.getLogger(getClass()).error(oEx, oEx);
			}

        }
        String header = "<?xml version=\"1.0\" ?> \n"
                + "<!--   \n"
                + "       Title:  " + this.testProcedureID + "\n"
                + "       Description:  " + this.testProcedureDescription + "\n"
                + "       Version/Date: " + this.testProcedureProductionDate + "\n"
                + "-->\n\n";

        String intro = "<testprocedure xmlns=\"jelly:jameleon\"  xmlns:jl=\"jelly:core\" >\n"
                + "<test-case-id>${C2CRITestCaseID}</test-case-id>\n\n";

        String finalLine = "</testprocedure>";

        try (Writer out = new OutputStreamWriter(new FileOutputStream(path + File.separatorChar + fileName), StandardCharsets.UTF_8))
		{
            out.write(header);
            out.write(intro);
            for (Section thisSection : subSections) {
                out.write(thisSection.getScriptContent());
            }
            out.write(finalLine);
        } catch (Exception ex) {
            System.out.println("****** Writing Error:*******\n");
            ex.printStackTrace();
        } finally {
        }



    }
}
