/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verification;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import tmddv3verification.testing.DialogTester;
import tmddv3verification.testing.ElementTester;
import tmddv3verification.testing.FrameTester;
import tmddv3verification.testing.MessageTester;
import tmddv3verification.utilities.MessageElement;
import tmddv3verification.utilities.TMDDDatabase;
import tmddv3verification.utilities.TestCase;
import tmddv3verification.utilities.TestCaseMaker;
import tmddv3verification.utilities.TestCaseOutput;
import tmddv3verification.utilities.TestProcedure;
import tmddv3verification.utilities.TestStep;
import tmddv3verification.utilities.TestStepMaker;

/**
 *
 * @author TransCore ITS
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String wsdlFileName = "c:\\inout\\tmp\\TMDDv302\\tmdd.wsdl";
//        String wsdlFileName = "file:\\c:\\inout\\tmddschemasv9\\tmdd.wsdl";
        DesignDetail tmddDesign = new DesignDetail(wsdlFileName);

        List<Need> needsList = new ArrayList<Need>();
        List<Predicate> predicatesList = new ArrayList<Predicate>();

        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        ResultSet needIDRS = theDatabase.queryReturnRS("Select distinct UNID, UserNeed, UNSelected from NRTM order by UNID");
        ResultSet needIDRS = theDatabase.queryReturnRS("SELECT DISTINCT NeedsList.NeedNumber, NRTM.UNID, NRTM.UserNeed, NRTM.UNSelected " +
                            "FROM NRTM INNER JOIN NeedsList ON NRTM.UNID = NeedsList.UNID ORDER BY NeedsList.NeedNumber");
        try {
            while (needIDRS.next()) {
                Integer needNumber = Integer.parseInt(needIDRS.getString("NeedNumber"));
                String unID = needIDRS.getString("UNID");
                String userNeed = needIDRS.getString("UserNeed");
                String selected = needIDRS.getString("UNSelected");

                Need thisNeed = new Need();
                thisNeed.setNeedNumber(needNumber);
                thisNeed.setUnID(unID);
                thisNeed.setUserNeed(userNeed);
                thisNeed.setUserNeedSelected(selected);

                ResultSet needRS = theDatabase.queryReturnRS("Select * from NRTM where UNID ='" + unID + "' order by ID");
                Integer itemNumber = 1;

                try {
                    while (needRS.next()) {
                        String requirementID = needRS.getString("RequirementID");
                        String requirement = needRS.getString("Requirement");
                        String conformance = needRS.getString("Conformance");
                        String support = needRS.getString("Support");
                        String otherRequirement = needRS.getString("OtherRequirements");

                        System.out.println(unID + " ("+needNumber+") --" + userNeed);


                        Requirement thisRequirement = new Requirement();
                        thisRequirement.setIndex(itemNumber);
                        thisRequirement.setRequirementID(requirementID);
                        thisRequirement.setRequirement(requirement);
                        thisRequirement.setConformance(conformance);
                        thisRequirement.setOtherRequirement(otherRequirement);
                        thisNeed.getRequirementList().add(thisRequirement);
                        itemNumber = itemNumber + 1;

                        ResultSet designRS = theDatabase.queryReturnRS("Select * from RTMforTMDDVerificationQuery where RequirementID ='" + requirementID + "' order by ID asc");
                        Integer designItemNumber = 1;

                        try {
                            while (designRS.next()) {
//                                String designRequirementID = designRS.getString("RequirementID");
//                                String designRequirement = designRS.getString("Requirement");
                                String dialog = designRS.getString("Dialog");
                                String dataConceptName = designRS.getString("StrippedConceptName");
                                String dcType = designRS.getString("DCType");
                                String standardsClause = designRS.getString("StandardsClause");
                                String instanceName = designRS.getString("Data Concept Instance Name");
                                DataConcept thisConcept = new DataConcept();
                                thisConcept.setDataConceptName(dataConceptName);
                                thisConcept.setDataConceptType(dcType);
                                thisConcept.setStandardsClause(standardsClause);
                                thisConcept.setIndex(designItemNumber);
                                thisConcept.setInstanceName(instanceName);

                                thisRequirement.getDataConceptList().add(thisConcept);

                                designItemNumber = designItemNumber + 1;

                            }
                            System.out.println("Number of DataConcepts added to tbis requirement = " + thisRequirement.getDataConceptList().size());
                            designRS.close();
                            designRS = null;

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("Number of Requirements added to this need = " + thisNeed.getRequirementList().size());
                    needsList.add(thisNeed);
                    needRS.close();
                    needRS = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }
            needIDRS.close();
            needIDRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Now Do the Predicates List
        ResultSet predicateRS = theDatabase.queryReturnRS("Select * from Predicates");

        try {
            while (predicateRS.next()) {
                String predicate = predicateRS.getString("Predicate");
                String section = predicateRS.getString("Section");

                System.out.println(predicate + "--" + section);

                Predicate thisPredicate = new Predicate();
                thisPredicate.setPredicateName(predicate);
                thisPredicate.setSection(section);
//                        predicatesList.add(thisPredicate);

                ResultSet designRS = theDatabase.queryReturnRS("Select * from RTMforTMDDVerificationQuery where RequirementID ='" + section + "' order by ID");
                Integer designItemNumber = 1;

                try {
                    while (designRS.next()) {
//                                String designRequirementID = designRS.getString("RequirementID");
//                                String designRequirement = designRS.getString("Requirement");
                        String dialog = designRS.getString("Dialog");
                        String dataConceptName = designRS.getString("StrippedConceptName");
                        String dcType = designRS.getString("DCType");
                        String standardsClause = designRS.getString("StandardsClause");

                        DataConcept thisConcept = new DataConcept();
                        thisConcept.setDataConceptName(dataConceptName);
                        thisConcept.setDataConceptType(dcType);
                        thisConcept.setStandardsClause(standardsClause);
                        thisConcept.setIndex(designItemNumber);

                        thisPredicate.getDataConceptList().add(thisConcept);

                        designItemNumber = designItemNumber + 1;

                    }
                    if (thisPredicate.getDataConceptList() != null) {
                        System.out.println("Number of DataConcepts added to tbis predicate = " + thisPredicate.getDataConceptList().size());
                    }

                    designRS.close();
                    designRS = null;

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                predicatesList.add(thisPredicate);
            }
            predicateRS.close();
            predicateRS = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        theDatabase.disconnectFromDatabase();


        // TODO code application logic here
        System.out.println("Number of Needs Added = " + needsList.size());

        System.out.println("\n\n\n\nRTM Data Concepts not found in the Design Content");
        System.out.println("Need,Requirement,DataConcept");

        // Looking for RTM elements not included in the design Content
        for (Need thisNeed : needsList) {

            for (Requirement thisRequirement : thisNeed.getRequirementList()) {

                for (DataConcept thisConcept : thisRequirement.getDataConceptList()) {
                    if (!tmddDesign.getDesignMap().containsKey(thisConcept.getDataConceptName())) {
                        if (thisConcept.getDataConceptType().equals("dialog")) {
                            String newName = thisConcept.getDataConceptName().replaceFirst("dl", "Dl");
                            if (!tmddDesign.getDesignMap().containsKey(newName)) {
                                System.out.println("Need: " + thisNeed.getUnID() + "  Requirement " + thisRequirement.getRequirementID() + "  DataConcept: " + thisConcept.getDataConceptName() + " does not exist in the design Content.");
                            }
                        } else {
                            System.out.println("Need: " + thisNeed.getUnID() + "  Requirement " + thisRequirement.getRequirementID() + "  DataConcept: " + thisConcept.getDataConceptName() + " does not exist in the design Content.");
                        }
                    }
                }
            }
        }


        System.out.println("\n\n\n\n Design Content not found in RTM");
        System.out.println("DataConceptName,Source,DCType");
        //Looking for Design Content not included in the RTM
        Iterator designIterator = tmddDesign.getDesignMap().keySet().iterator();

        while (designIterator.hasNext()) {
            String currentDesignElement = (String) designIterator.next();
            if (currentDesignElement.startsWith("Dl")) {
            }
            boolean found = false;
            for (Need thisNeed : needsList) {

                for (Requirement thisRequirement : thisNeed.getRequirementList()) {

                    for (DataConcept thisConcept : thisRequirement.getDataConceptList()) {
                        if (currentDesignElement.startsWith("Dl")) {
                            if (thisConcept.getDataConceptName().equalsIgnoreCase(currentDesignElement)) {
                                found = true;
                            }
                        } else if (thisConcept.getDataConceptName().equals(currentDesignElement)) {
                            found = true;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }

            if (!found) {
                DesignElement thisElement = (DesignElement) tmddDesign.getDesignMap().get(currentDesignElement);
                System.out.println(thisElement.getElementName() + "," + thisElement.getElementSource() + "," + thisElement.getElementType());
            }


        }

        System.out.println("\n\nDialog Testing ....");
        DialogTester dialogTester = new DialogTester(predicatesList, tmddDesign);
        for (Need thisNeed : needsList) {
            dialogTester.performDialogTests(thisNeed);
        }

        System.out.println("\n\nMessage Testing ....");
        MessageTester messageTester = new MessageTester(predicatesList, tmddDesign);
        for (Need thisNeed : needsList) {
            messageTester.performMessageTests(thisNeed);
        }

        System.out.println("\n\nFrame Testing ....");
        FrameTester frameTester = new FrameTester(predicatesList, tmddDesign);
        for (Need thisNeed : needsList) {
            frameTester.performMessageTests(thisNeed);
        }

        System.out.println("\n\nElement Testing ....");
        ElementTester elementTester = new ElementTester(predicatesList, tmddDesign);
        for (Need thisNeed : needsList) {
            elementTester.performElementTests(thisNeed);
        }

        System.out.println("\n\n");
        System.out.println("NeedNumber,UNID,Need,Selected,ReqIndex,ReqID,Requirement,Conformance,ConceptIndex,ConceptName,ConceptType,InstanceName,ReferenceName,ReferencePath");
        for (Need thisNeed: needsList){
            for (Requirement thisRequirement : thisNeed.getRequirementList()){
                for (DataConcept thisConcept : thisRequirement.getDataConceptList()){
                    System.out.println(thisNeed.getNeedNumber()+","+thisNeed.getUnID()+","+thisNeed.getUserNeed()+","+thisNeed.getUserNeedSelected()+
                            ","+thisRequirement.getIndex()+","+thisRequirement.getRequirementID()+","+thisRequirement.getRequirement()+","+(thisRequirement.getConformance().equals("M") ? "M":"O")+","+
                            thisConcept.getIndex()+","+thisConcept.getDataConceptName()+","+thisConcept.getDataConceptType()+","+thisConcept.getInstanceName()+","+thisConcept.getConceptReferenceName()+
                            ","+thisConcept.getConceptPath());
                }
            }
        }

        System.out.println("\n\n");
        System.out.println("NeedID,TCID,Title,dialog,CenterMode,ItemType,Precondition,Inputs,Outputs,RequirementID,DataConcepts");

        for(Need thisNeed: needsList){
            TestCaseMaker theMaker = new TestCaseMaker(thisNeed, tmddDesign);
            theMaker.printTestCases();
        }


        System.out.println("\n\n");
        System.out.println("\n\n");
        System.out.println("\n\n");
       for(Need thisNeed: needsList){
            TestCaseMaker theMaker = new TestCaseMaker(thisNeed, tmddDesign);
            theMaker.printTestCaseDataConceptMap();
        }


//       TestStepMaker tstepMaker = new TestStepMaker();
//       for(Need thisNeed: needsList){
//            TestCaseMaker theMaker = new TestCaseMaker(thisNeed, tmddDesign);
//            for (TestCase theTestCase : theMaker.getTestCases()){
//                String requirements = "";
//                for (String theRequirement : theTestCase.getTestItemRequirements()){
//                    requirements = requirements.concat(theRequirement+"\n");
//                }
//                TestProcedure theProcedure = new TestProcedure(theTestCase.getTestCaseID().replaceFirst("TCS", "TPS"),
//                        theTestCase.getTestCaseID().replaceFirst("TCS", "TPS"),
//                        theTestCase.getTestCaseTitle(),
//                        theTestCase.getTestCaseDescription(),
//                        requirements);
//
//                String theMessage = "";
//                for (TestCaseOutput thisOutput : theTestCase.getTestCaseOutputs()){
//                    if (theMessage.isEmpty()){
//                        theMessage = theMessage.concat(thisOutput.getName());
//                    } else{
//                        theMessage = theMessage.concat(", "+thisOutput.getName());
//                    }
//                }
//                theProcedure.addTestStep(tstepMaker.makeDialogSetupStep(theTestCase.getTestItemCenterMode(),theTestCase.getTestCaseDialog()));
//                TestStep thisStep = tstepMaker.makeDialogCommandStep(theTestCase.getTestItemCenterMode(),theTestCase.getTestCaseDialog(),theMessage);
//
//                String commandReqList = "";
//                for (String thisRequirement : theTestCase.getDialogRequirements()){
//                    if (commandReqList.isEmpty()){
//                        commandReqList = commandReqList.concat(thisRequirement);
//                    } else {
//                        commandReqList = commandReqList.concat(", "+thisRequirement);
//                    }
//                }
//                for (MessageElement thisElement: theTestCase.getMessageElements()){
//                    if (!thisElement.isOptional()){
//                        if (commandReqList.isEmpty()){
//                        commandReqList = commandReqList.concat(thisElement.getElementRequirement());
//                        } else {
//                        commandReqList = commandReqList.concat(", "+thisElement.getElementRequirement());
//
//                        }
//                        }
//                }
//                thisStep.setResults(commandReqList.isEmpty()? "PASS/FAIL":"PASS/FAIL {"+commandReqList+"}");
//                theProcedure.addTestStep(thisStep);
//
// //                System.out.println("\n Related Message Elements:");
//                for (MessageElement thisElement : theTestCase.getMessageElements()){
//                    if (thisElement.isOptional()){
//                        TestStep newStep = new TestStep();
//                        newStep.setDescription("IF requirement " + thisElement.getElementRequirement()+ " is selected VERIFY that MESSAGE X contains the data concept "+thisElement.getElementName() + " of type "+ thisElement.getElementType());
//                        newStep.setResults(thisElement.getElementRequirement().isEmpty()? "PASS/FAIL":"PASS/FAIL {"+thisElement.getElementRequirement()+"}");
//                        theProcedure.addTestStep(newStep);
//                    }
////                    System.out.println("MessageElementName: "+thisElement.getElementName()+"  Requirement: "+thisElement.getElementRequirement()+"  Type: "+thisElement.getElementType());
//                }
//
//
//                try{
//                 thisStep = tstepMaker.makeDialogCommandCleanupStep(theTestCase.getTestItemCenterMode(),theTestCase.getTestCaseDialog(),theMessage);
//                 thisStep.setResults("PASS/FAIL");
//                 theProcedure.addTestStep(thisStep);
//                } catch (Exception ex){
//
//                }
//                System.out.println("\n\n");
//                theProcedure.printProcedure();
//                theProcedure.writeResults();
//
// //               System.out.println("\n Related Message Elements:");
// //               for (MessageElement thisElement : theTestCase.getMessageElements()){
// //                   System.out.println("MessageElementName: "+thisElement.getElementName()+"  Requirement: "+thisElement.getElementRequirement()+"  Type: "+thisElement.getElementType());
// //               }
//            }
//       }

    }
}
