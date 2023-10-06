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
public class MessageProcedureMaker extends AbstractProcedureMaker{
    public static void main(String[] args) {
        MessageProcedureMaker defaultProcedureMaker = new MessageProcedureMaker("Trial Procedure");
        defaultProcedureMaker.makeProcedure();
        defaultProcedureMaker.writeProcedure();

    }

    public MessageProcedureMaker(String procedureName){
        super(procedureName);
        tmddprocedures.AbstractProcedureMaker.procedureName = procedureName;
    }

    @Override
    public void makeProcedure() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();


        ResultSet messageNameRS = theDatabase.queryReturnRS("Select distinct DataConcept from TMDDBasicFrameAndElementDefsQuery where ConceptType = 'message'");

//        ResultSet rs = theDatabase.queryReturnRS("Select DataConcept, ReqPara, ConceptType from TMDDBasicFrameAndElementDefsQuery where ConceptType = 'data-frame'");
        try {
            while (messageNameRS.next()) {
                Integer testStepNumber = 1;
                String messageName = messageNameRS.getString("DataConcept");
                ResultSet messageDetailRS = theDatabase.queryReturnRS("Select ParentConceptType, ParentItemNumber, ConceptName, Type, ConceptType, RequirementID, Conformance from TMDDFrameContentsDetailQuery where ParentConceptType = '" + messageName + "'");
                while (messageDetailRS.next()) {
                    String parentConceptType = messageDetailRS.getString("ParentConceptType");
                    String conceptName = messageDetailRS.getString("ConceptName");
                    String type = messageDetailRS.getString("Type");
                    String conceptType = messageDetailRS.getString("ConceptType");
                    String requirementID = messageDetailRS.getString("RequirementID");
                    String conformance = messageDetailRS.getString("Conformance");
                    String itemNumber = messageDetailRS.getString("ParentItemNumber");

                    String reqList = "";
                    ResultSet messageReqRS = theDatabase.queryReturnRS("Select distinct RequirementID from TMDDFrameContentsDetailQuery where ParentConceptType = '" + parentConceptType + "'" + " and ParentItemNumber = " + itemNumber +" and ConceptName='"+conceptName + "' and Type='"+type+"'" );

                    while (messageReqRS.next()) {
                        String tmpReq = messageReqRS.getString("RequirementID");
                        if (tmpReq != null)reqList = reqList + reqList.concat(tmpReq + "<>");

                    }
                    messageReqRS.close();
                    messageReqRS = null;

                    ProcedureStep thisStep = new ProcedureStep("TPS-"+messageName + "-Verify");
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
                            thisStep.setAction("VERIFY that element '" + conceptName + "' exists in Message " + parentConceptType);
                            thisStep.setResults("The element is found within the Message.");
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
                messageDetailRS.close();
                messageDetailRS = null;

            }
            messageNameRS.close();
            messageNameRS = null;

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
