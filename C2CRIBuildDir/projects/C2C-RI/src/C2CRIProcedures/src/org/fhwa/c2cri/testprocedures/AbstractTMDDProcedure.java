/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import tmddv3verification.DesignElement;
import tmddv3verification.NeedHandler;
import tmddv3verification.Requirement;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public abstract class AbstractTMDDProcedure implements TestProcedure {

    protected ArrayList<String> relatedRequirements = new ArrayList<String>();
    protected ArrayList<Variable> procedureVariables = new ArrayList<Variable>();
    protected ArrayList<TestProcedureSection> subSections = new ArrayList<TestProcedureSection>();
    private String testProcedureID;
    private String testProcedureTitle;
    private String testProcedureDescription;
    private String testProcedureProductionDate;
    private String needID;

    protected void initializeSummaryInformation(String needID, String dialog, String centerMode) {
        TestProcedureNamer procedureNameMaker = new TestProcedureNamer(needID, dialog, centerMode);
        testProcedureID = procedureNameMaker.getProcedureID();
        testProcedureTitle = procedureNameMaker.getProcedureTitle();
        testProcedureDescription = procedureNameMaker.getProcedureDescription();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        testProcedureProductionDate = dateFormat.format(date);
        this.needID = needID;
        
        String sutCenterMode = centerMode.equals(TMDDParameters.TMDD_EC_MODE)?TMDDParameters.TMDD_OC_Mode:TMDDParameters.TMDD_EC_MODE;
        relatedRequirements = NRTM_RTM_Design_Data.getInstance().makeTestProcedureRequirementsList(needID, sutCenterMode, dialog);
        if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)||dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)){
            String partnerDialog = null;
            try{
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)){
                    partnerDialog = TMDDSubPubMapping.getInstance().getSubscriptionDialog(dialog, Integer.parseInt(needID));
                } else {
                    partnerDialog = TMDDSubPubMapping.getInstance().getPublicationDialog(dialog, Integer.parseInt(needID));                    
                }
            } catch (Exception ex){
                ex.printStackTrace();
                partnerDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needID, sutCenterMode, dialog);
//            String partnerDialog = NRTM_RTM_Design_Data.getInstance().getPartnerDialog(needID, sutCenterMode, dialog);
            }
            ArrayList<String> partnerRequirements = NRTM_RTM_Design_Data.getInstance().makeTestProcedureRequirementsList(needID, sutCenterMode, partnerDialog);
            for (String thisRequirement : partnerRequirements){
                if (!relatedRequirements.contains(thisRequirement)) relatedRequirements.add(thisRequirement);
            }
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

    private ArrayList<String> makeTestProcedureRequirementsList(String sutCenterMode, String dialog) {
        ArrayList<String> theRequirements = new ArrayList<String>();
        Integer needNumber = Integer.parseInt(this.needID) - 1;
        NeedHandler theHandler = new NeedHandler(NRTM_RTM_Design_Data.getInstance().getNeedsList().get(needNumber));

        // We are always testing the requirement related to the dialog in each test case.
        List<String> theDialogs = theHandler.getNeedRelatedDialogs();
        for (String thisDialog : theDialogs) {
            if (thisDialog.equals(dialog)) {
                List<Requirement> theReqList = theHandler.getDialogSpecificRequirements(dialog);
                for (Requirement theRequirement : theReqList) {
                    if (!theRequirements.contains(theRequirement.getRequirementID())) {
                        theRequirements.add(theRequirement.getRequirementID());
                    }
                }
            }
        }

        DesignElement theElement = (DesignElement) NRTM_RTM_Design_Data.getInstance().getTmddDesign().getDesignMap().get(dialog);
        if (theElement != null) {
            String messageName = "";
            if (sutCenterMode.equals(TMDDParameters.TMDD_EC_MODE)) {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    messageName = theElement.getSubElements().get(1).getElementName();
                } else {
                    messageName = theElement.getSubElements().get(0).getElementName();
                }


            } else // mode is OC
            {
                if (dialog.endsWith(TMDDParameters.TMDD_PUBLICATION_SUFFIX)) {
                    messageName = theElement.getSubElements().get(1).getElementName();
                } else if (dialog.endsWith(TMDDParameters.TMDD_SUBSCRIPTION_SUFFIX)) {
                    if (theElement.getSubElements().size() >= 3) {
                        messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        System.err.println("*** Could not retrieve SubElement(Message) 2 for dialog: " + dialog + " for mode " + sutCenterMode);
                    }
                } else {
                    messageName = theElement.getSubElements().get(1).getElementName();
                }
            }

            if (!messageName.equals("")) {
                try {
                    int reqCount = theHandler.getMessageConceptElementCount(messageName);
                    for (int ii = 0; ii < reqCount; ii++) {
                        String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                        if (!theRequirements.contains(reqID)) {
                            theRequirements.add(reqID);
                        }
                    }

                } catch (Exception ex) {
                    System.err.println("For need: " + theHandler.getNeedID() + "(" + this.needID + ") -- there does not appear to be a " + messageName + " element specified in the NRTM related to dialog " + dialog + "(" + sutCenterMode + ")");
//                        ex.printStackTrace();
                }
            }

        }
        return theRequirements;

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

        try {
        for (Section thisSection : subSections) {
            Integer stepNumber = 1;
            for (TestProcedureStep thisStep : thisSection.getStepsforDatabase()) {
                theDatabase.queryNoResult("INSERT INTO TestProcedureStepsTable ([Procedure],StepNumber,"
                        + "Step,[Action],Result,Requirements,PassFail) VALUES ("
                        + "'" + this.testProcedureID + "',"
                        + "'" + stepNumber + "',"
                        + "'" + thisStep.getId() + "',"
                        + "'" + thisStep.getDescription().replace("<LF>", "\n") + "',"
                        + "'" + thisStep.getResults() + "',"
                        + "'" + thisStep.getId() + "',"
                        + "'" + thisStep.getResults() + "')");
            stepNumber++;
            }
            System.gc();
        }
        } catch (Exception ex) {
          ex.printStackTrace();
        }

        theDatabase.disconnectFromDatabase();
    }

    public final void toScriptFile(String path, String fileName) {
        File newFile = new File(path + File.separatorChar + "tempFile");
        if (newFile.exists()) {
            newFile.delete();
            newFile = new File(path + File.separatorChar + "tempFile");

        }
        String header = "<?xml version=\"1.0\" ?> \n"
                + "<!--   \n"
                + "       Title:  " + this.testProcedureID + "\n"
                + "       Description:  " + this.testProcedureDescription + "\n"
                + "       Version/Date: " + this.testProcedureProductionDate + "\n"
                + "-->\n\n";

        String intro = "<testprocedure xmlns=\"jelly:jameleon\"  xmlns:jl=\"jelly:core\" >\n"
                + "<test-case-id>${C2CRITestCaseID}</test-case-id>\n"
                + "<test-case-summary>" + this.testProcedureDescription + "</test-case-summary>\n\n";

        String finalLine = "</testprocedure>";
		try
		{
			try (Writer out = new OutputStreamWriter(new FileOutputStream(path + File.separatorChar + "tempFile"), StandardCharsets.UTF_8))
			{
				out.write(header);
				out.write(intro);
				for (Section thisSection : subSections) {
					out.write(thisSection.getScriptContent());
				}
				out.write(finalLine);
			}

            newFile = new File(path + File.separatorChar + fileName);
            if (newFile.exists()) {
            newFile.delete();
            newFile = new File(path + File.separatorChar + fileName);

			}
            XmlOptions newOptions = new XmlOptions();
            newOptions.setSavePrettyPrint();
            newOptions.setSavePrettyPrintIndent(5);
            XmlObject thisObject1 = XmlObject.Factory.parse(new File(path + File.separatorChar + "tempFile"), newOptions);
            thisObject1.save(newFile, newOptions);

            newFile = new File(path + File.separatorChar + "tempFile");
            if (newFile.exists()) {
                newFile.delete();
            }


        } catch (Exception ex) {
            System.out.println("****** Writing Error:*******\n");
            ex.printStackTrace();
        } finally {

        }



    }
}
