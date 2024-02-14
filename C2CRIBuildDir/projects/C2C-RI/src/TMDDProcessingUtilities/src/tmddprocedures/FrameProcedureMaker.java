/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddprocedures;

import java.sql.ResultSet;

/**
 *
 * @author TransCore ITS
 */
public class FrameProcedureMaker extends AbstractProcedureMaker {

    public static void main(String[] args) {
        FrameProcedureMaker defaultProcedureMaker = new FrameProcedureMaker("Trial Frame Procedure");
        defaultProcedureMaker.makeProcedure();
        defaultProcedureMaker.writeProcedure();

    }

    public FrameProcedureMaker(String procedureName) {
        super(procedureName);
        tmddprocedures.AbstractProcedureMaker.procedureName = procedureName;
    }

    @Override
    public void makeProcedure() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();


        ResultSet frameNameRS = theDatabase.queryReturnRS("Select distinct DataConcept from TMDDBasicFrameAndElementDefsQuery where ConceptType = 'data-frame'");

//        ResultSet rs = theDatabase.queryReturnRS("Select DataConcept, ReqPara, ConceptType from TMDDBasicFrameAndElementDefsQuery where ConceptType = 'data-frame'");
        try {
            while (frameNameRS.next()) {
                Integer testStepNumber = 1;
                String frameName = frameNameRS.getString("DataConcept");
                ResultSet frameDetailRS = theDatabase.queryReturnRS("Select ParentConceptType, ParentItemNumber, ConceptName, Type, ConceptType, RequirementID, Conformance from TMDDFrameContentsDetailQuery where ParentConceptType = '" + frameName + "'");
                while (frameDetailRS.next()) {
                    String parentConceptType = frameDetailRS.getString("ParentConceptType");
                    String conceptName = frameDetailRS.getString("ConceptName");
                    String type = frameDetailRS.getString("Type");
                    String conceptType = frameDetailRS.getString("ConceptType");
                    String requirementID = frameDetailRS.getString("RequirementID");
                    String conformance = frameDetailRS.getString("Conformance");
                    String itemNumber = frameDetailRS.getString("ParentItemNumber");

                    String reqList = "";
                    ResultSet frameReqRS = theDatabase.queryReturnRS("Select distinct RequirementID from TMDDFrameContentsDetailQuery where ParentConceptType = '" + parentConceptType + "'" + " and ParentItemNumber = " + itemNumber +" and ConceptName='"+conceptName + "' and Type='"+type+"'" );

                    while (frameReqRS.next()) {
                        String tmpReq = frameReqRS.getString("RequirementID");
                        if (tmpReq != null)reqList = reqList + reqList.concat(tmpReq + "<>");

                    }
                    frameReqRS.close();
                    frameReqRS = null;

                    ProcedureStep thisStep = new ProcedureStep("TPS-"+frameName + "-Verify");
                    if (reqList != null){
                        thisStep.setRequirements(reqList);
                    } else thisStep.setRequirements(" ");

                    thisStep.setStepNumber(testStepNumber.toString());
                    if (conceptType.equals("data-frame")) {  // Process as a Data Frame
                        thisStep.setStepText("PERFORM");  // Verify Here!!
                        thisStep.setAction("PERFORM TPS-" + type + "-Verify Using " + conceptName);
                        thisStep.setResults("The frame verification procedure is performed.");
                        thisStep.setPassFail(" ");
                        testProcedure.add(thisStep);
                        testStepNumber++;
 //                       writeProcedure();
 //                       this.testProcedure.clear();
                    } else {  // Process as a Data Element
                        if ((conformance !=null) &&(!conformance.equals("M"))) {
                            thisStep.setStepText("VERIFY");  // Verify Here!!
                            thisStep.setAction("VERIFY that element '" + conceptName + "' exists in Frame " + parentConceptType);
                            thisStep.setResults("The element is found within the Frame.");
                            thisStep.setPassFail("Pass");
                            testProcedure.add(thisStep);
                            testStepNumber++;
 //                           writeProcedure();
 //                           this.testProcedure.clear();
                        } else {
                            System.out.println("Skipping "+ parentConceptType+":"+conceptName+" with list"+reqList+ "  arraySize="+this.testProcedure.size());
                            thisStep = null;
                        }

                    }

                }
                frameDetailRS.close();
                frameDetailRS = null;

            }
            frameNameRS.close();
            frameNameRS = null;

//            while (rs.next()) {
//                ProcedureStep thisStep = new ProcedureStep(rs.getString("DataConcept"));



//                System.out.println(rs.getString("DataConcept"));
//            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        theDatabase.disconnectFromDatabase();

    }
}
