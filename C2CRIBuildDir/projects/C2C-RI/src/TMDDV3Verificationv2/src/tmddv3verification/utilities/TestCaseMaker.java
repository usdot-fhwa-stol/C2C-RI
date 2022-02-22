/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verification.utilities;

import java.util.ArrayList;
import java.util.List;
import tmddv3verification.DataConcept;
import tmddv3verification.DesignDetail;
import tmddv3verification.DesignElement;
import tmddv3verification.Need;
import tmddv3verification.NeedHandler;
import tmddv3verification.Requirement;

/**
 *
 * @author TransCore ITS
 */
public class TestCaseMaker {

    private Need need;
    private Integer testCaseCount;
    private List<TestCase> testCases = new ArrayList<TestCase>();
    private DesignDetail theDesignContent;
    private NeedHandler theHandler;

    public TestCaseMaker(Need thisNeed, DesignDetail thisDesign) {
        testCaseCount = 1;
        need = thisNeed;
        theDesignContent = thisDesign;
        theHandler = new NeedHandler(thisNeed);

        for (Requirement theRequirement : need.getRequirementList()) {
            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                if (theConcept.getDataConceptType().equals("dialog")) {
                    makeECTestCases(theConcept.getDataConceptName(), theRequirement);
                    makeOCTestCases(theConcept.getDataConceptName(), theRequirement);
                }
            }
        }
     //   System.out.println("NeedID,TCID,Title,CenterMode,ItemType,Precondition,Inputs,Outputs");
        for (TestCase theCase : testCases) {
            for (String theRequirement : theCase.getTestItemRequirements()){
              String dataConcepts = "";
              for (DataConcept theConcept : theHandler.getRequirementRelatedDataConcepts(theRequirement)){
                  dataConcepts = dataConcepts.concat(theConcept.getDataConceptName()+"; ");
              }
 //             System.out.println(theCase.getTestItemNeedID() + "," + theCase.getTestCaseID() + "," + theCase.getTestCaseTitle() + "," + theCase.getTestItemCenterMode() + ","
 //                   +theCase.getTestItemType()+ ","+theCase.getPreConditionRequirement()+","+theCase.getTestCaseInputs().toString().replace(",", "|") + "," + theCase.getTestCaseOutputs().toString().replace(",", "|")+","+theRequirement+","+dataConcepts);

            }
        }
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }


    public void printTestCases(){
        for (TestCase theCase : testCases) {
            for (String theRequirement : theCase.getTestItemRequirements()){
              String dataConcepts = "";
              for (DataConcept theConcept : theHandler.getRequirementRelatedDataConcepts(theRequirement)){
                  dataConcepts = dataConcepts.concat(theConcept.getDataConceptName()+"; ");
              }
              System.out.println(theCase.getTestItemNeedID() + "," + theCase.getTestCaseID() + "," + theCase.getTestCaseTitle() + "," + theCase.getTestCaseDialog()+","+theCase.getTestItemCenterMode() + ","
                    +theCase.getTestItemType()+ ","+theCase.getPreConditionRequirement()+","+theCase.getTestCaseInputs().toString().replace(",", "|") + "," + theCase.getTestCaseOutputs().toString().replace(",", "|")+","+theRequirement+","+dataConcepts);

            }
        }

    }

    public void printTestCaseDataConceptMap(){
         for (TestCase theCase : testCases) {
            for (String theRequirement : theCase.getTestItemRequirements()){
              for (DataConcept theConcept : theHandler.getRequirementRelatedDataConcepts(theRequirement)){
                  System.out.println(theCase.getTestCaseID() + "," + theCase.getTestItemNeedID() +","+ theCase.getTestCaseDialog()+","+theRequirement+ "," + theCase.getTestItemCenterMode() +","+theConcept.getDataConceptName()+","+theConcept.getConceptReferenceName()+","+theConcept.getConceptPath());
              }
            }
        }

    }
    /**
     * test case ID
     * test Case Title
     * test Case Description
     * test Item Type (SUT or RI, RI)
     * test ITem Center Mode
     * test ITem Related Need ID
     * test Item Related Requirement ID(s)
     * ApplicationLayer testCase Precondition  (if precondition not true, then test case not required to satisfy need for this project.)
     *      R-R - None
     *      Sub - Sub Dialog Requirement ID to check for Selected
     *      Pub - Pub Dialog Requirement ID to check for Selected
     *
     */
    public void makeECTestCases(String dialog, Requirement theRequirement) {
        /** Create Test Cases for SUT as EC */
        /** Valid Test Case */
            List<Requirement> requirementsList = theHandler.getDialogSpecificRequirements(dialog);
            List<String> dialogRequirements = new ArrayList<String>();
            for (Requirement thisRequirement:theHandler.getDialogSpecificRequirements(dialog)){
                if (!dialogRequirements.contains(thisRequirement.getRequirementID())){
                    dialogRequirements.add(thisRequirement.getRequirementID());
                }
            }
        if (!dialog.endsWith("Update")) {
            TestCase validECTestCase = new TestCase();
            validECTestCase.setTestCaseID(makeTestCaseID("EC"));
            validECTestCase.setTestCaseTitle("Verify that the SUT can " + theRequirement.getRequirement() + " as an EC");
            validECTestCase.setTestCaseDescription("This test case verifies that the SUT performs the " + dialog + " dialog as an EC as specified by the Standard.");
            validECTestCase.setTestItemType("SUT/RI");
            validECTestCase.setTestItemCenterMode("EC");
            validECTestCase.setTestItemNeedID(need.getUnID());
            validECTestCase.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, false));
            validECTestCase.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            validECTestCase.setTestCaseInputs(makeTestCaseInputs("EC", dialog, false));
            validECTestCase.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, false));
            validECTestCase.setTestCaseDialog(dialog);
            List<TestCaseOutput> theOutputs = makeTestCaseOutputs("EC", dialog, false);
            String theMessage = "";
            for (TestCaseOutput thisOutput: theOutputs){
                theMessage = thisOutput.getName();
            }
            validECTestCase.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            validECTestCase.setDialogRequirements(dialogRequirements);
            testCases.add(validECTestCase);
            testCaseCount = testCaseCount + 1;
        } else {  // This is a publication Dialog
            TestCase validECTestCase = new TestCase();
            validECTestCase.setTestCaseID(makeTestCaseID("EC"));
            validECTestCase.setTestCaseTitle("Verify that the SUT supports " + theRequirement.getRequirement() + " as an EC");
            validECTestCase.setTestCaseDescription("This test case verifies that the SUT supports the " + dialog + " dialog as an EC as specified by the Standard.");
            validECTestCase.setTestItemType("SUT/RI");
            validECTestCase.setTestItemCenterMode("EC");
            validECTestCase.setTestItemNeedID(need.getUnID());
            validECTestCase.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, false));
            validECTestCase.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            validECTestCase.setTestCaseInputs(makeTestCaseInputs("EC", dialog, false));
            validECTestCase.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, false));
            validECTestCase.setTestCaseDialog(dialog);
            List<TestCaseOutput> theOutputs = makeTestCaseOutputs("EC", dialog, false);
            String theMessage = "";
            for (TestCaseOutput thisOutput: theOutputs){
                theMessage = thisOutput.getName();
            }
            validECTestCase.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            validECTestCase.setDialogRequirements(dialogRequirements);
            testCases.add(validECTestCase);
            testCaseCount = testCaseCount + 1;
        }
        /** InValid Test Cases */
        if (dialog.endsWith("Update")) {
            TestCase invalidECTestCase1 = new TestCase();
            invalidECTestCase1.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase1.setTestCaseTitle("Verify that the SUT provides a Type 1 Error Response to an invalid publication message from an OC.");
            invalidECTestCase1.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an EC and provides a Type 1 Error Response as specified by the Standard.");
            invalidECTestCase1.setTestItemType("SUT/RI");
            invalidECTestCase1.setTestItemCenterMode("EC");
            invalidECTestCase1.setTestItemNeedID(need.getUnID());
            invalidECTestCase1.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase1.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase1.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase1.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase1.setTestCaseDialog(dialog);
            List<TestCaseOutput> theOutputs = makeTestCaseOutputs("EC", dialog, true);
            String theMessage = "";
            for (TestCaseOutput thisOutput: theOutputs){
                theMessage = thisOutput.getName();
            }
            invalidECTestCase1.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase1.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase1);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase2 = new TestCase();
            invalidECTestCase2.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase2.setTestCaseTitle("Verify that the SUT provides a Type 2 Error Response to an invalid publication message from an OC.");
            invalidECTestCase2.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an EC and provides a Type 2 Error Response as specified by the Standard.");
            invalidECTestCase2.setTestItemType("SUT/RI");
            invalidECTestCase2.setTestItemCenterMode("EC");
            invalidECTestCase2.setTestItemNeedID(need.getUnID());
            invalidECTestCase2.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase2.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase2.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase2.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase2.setTestCaseDialog(dialog);
            invalidECTestCase2.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase2.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase2);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase3 = new TestCase();
            invalidECTestCase3.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase3.setTestCaseTitle("Verify that the SUT provides a Type 3 Error Response to an invalid publication message from an OC.");
            invalidECTestCase3.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an EC and provides a Type 3 Error Response as specified by the Standard.");
            invalidECTestCase3.setTestItemType("SUT/RI");
            invalidECTestCase3.setTestItemCenterMode("EC");
            invalidECTestCase3.setTestItemNeedID(need.getUnID());
            invalidECTestCase3.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase3.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase3.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase3.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase3.setTestCaseDialog(dialog);
            invalidECTestCase3.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase3.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase3);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase4 = new TestCase();
            invalidECTestCase4.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase4.setTestCaseTitle("Verify that the SUT provides a Type 4 Error Response to an invalid publication message from an OC.");
            invalidECTestCase4.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an EC and provides a Type 4 Error Response as specified by the Standard.");
            invalidECTestCase4.setTestItemType("SUT/RI");
            invalidECTestCase4.setTestItemCenterMode("EC");
            invalidECTestCase4.setTestItemNeedID(need.getUnID());
            invalidECTestCase4.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase4.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase4.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase4.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase4.setTestCaseDialog(dialog);
            invalidECTestCase4.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase4.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase4);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase5 = new TestCase();
            invalidECTestCase5.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase5.setTestCaseTitle("Verify that the SUT provides a Type 5 Error Response to an invalid publication message from an OC.");
            invalidECTestCase5.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an EC and provides a Type 5 Error Response as specified by the Standard.");
            invalidECTestCase5.setTestItemType("SUT/RI");
            invalidECTestCase5.setTestItemCenterMode("EC");
            invalidECTestCase5.setTestItemNeedID(need.getUnID());
            invalidECTestCase5.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase5.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase5.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase5.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase5.setTestCaseDialog(dialog);
            invalidECTestCase5.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase5.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase5);
            testCaseCount = testCaseCount + 1;


            TestCase invalidECTestCase6 = new TestCase();
            invalidECTestCase6.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase6.setTestCaseTitle("Verify that the SUT provides a Type 6 Error Response to an invalid publication message from an OC.");
            invalidECTestCase6.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an EC and provides a Type 6 Error Response as specified by the Standard.");
            invalidECTestCase6.setTestItemType("SUT/RI");
            invalidECTestCase6.setTestItemCenterMode("EC");
            invalidECTestCase6.setTestItemNeedID(need.getUnID());
            invalidECTestCase6.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase6.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase6.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase6.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase6.setTestCaseDialog(dialog);
            invalidECTestCase6.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase6.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase6);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase7 = new TestCase();
            invalidECTestCase7.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase7.setTestCaseTitle("Verify that the SUT provides a Type 7 Error Response to an invalid publication message from an OC.");
            invalidECTestCase7.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an EC and provides a Type 7 Error Response as specified by the Standard.");
            invalidECTestCase7.setTestItemType("SUT/RI");
            invalidECTestCase7.setTestItemCenterMode("EC");
            invalidECTestCase7.setTestItemNeedID(need.getUnID());
            invalidECTestCase7.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase7.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase7.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase7.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase7.setTestCaseDialog(dialog);
            invalidECTestCase7.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase7.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase7);
            testCaseCount = testCaseCount + 1;

        } /** Create RI as EC Specific Test Cases */
        /** InValid Test Cases - Request */
        /** InValid Test Cases - Subscription*/
        // Only for Request-Response and Subscription dialogs
        else if (!dialog.endsWith("Update")) {
            TestCase invalidECTestCase1 = new TestCase();
            invalidECTestCase1.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase1.setTestCaseTitle("Verify that the RI can initiate a Type 1 Error Response from the SUT as an OC.");
            invalidECTestCase1.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an EC and trigger a Type 1 Error Response as specified by the Standard.");
            invalidECTestCase1.setTestItemType("RI");
            invalidECTestCase1.setTestItemCenterMode("EC");
            invalidECTestCase1.setTestItemNeedID(need.getUnID());
            invalidECTestCase1.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase1.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase1.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase1.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase1.setTestCaseDialog(dialog);
            List<TestCaseOutput> theOutputs = makeTestCaseOutputs("EC", dialog, true);
            String theMessage = "";
            for (TestCaseOutput thisOutput: theOutputs){
                theMessage = thisOutput.getName();
            }
            invalidECTestCase1.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase1.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase1);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase2 = new TestCase();
            invalidECTestCase2.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase2.setTestCaseTitle("Verify that the RI can initiate a Type 2 Error Response from the SUT as an OC.");
            invalidECTestCase2.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an EC and trigger a Type 2 Error Response as specified by the Standard.");
            invalidECTestCase2.setTestItemType("RI");
            invalidECTestCase2.setTestItemCenterMode("EC");
            invalidECTestCase2.setTestItemNeedID(need.getUnID());
            invalidECTestCase2.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase2.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase2.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase2.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase2.setTestCaseDialog(dialog);
            invalidECTestCase2.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase2.setDialogRequirements(dialogRequirements);
             testCases.add(invalidECTestCase2);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase3 = new TestCase();
            invalidECTestCase3.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase3.setTestCaseTitle("Verify that the RI can initiate a Type 3 Error Response from the SUT as an OC.");
            invalidECTestCase3.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an EC and trigger a Type 3 Error Response as specified by the Standard.");
            invalidECTestCase3.setTestItemType("RI");
            invalidECTestCase3.setTestItemCenterMode("EC");
            invalidECTestCase3.setTestItemNeedID(need.getUnID());
            invalidECTestCase3.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase3.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase3.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase3.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase3.setTestCaseDialog(dialog);
            invalidECTestCase3.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase3.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase3);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase4 = new TestCase();
            invalidECTestCase4.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase4.setTestCaseTitle("Verify that the RI can initiate a Type 4 Error Response from the SUT as an OC.");
            invalidECTestCase4.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an EC and trigger a Type 4 Error Response as specified by the Standard.");
            invalidECTestCase4.setTestItemType("RI");
            invalidECTestCase4.setTestItemCenterMode("EC");
            invalidECTestCase4.setTestItemNeedID(need.getUnID());
            invalidECTestCase4.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase4.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase4.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase4.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase4.setTestCaseDialog(dialog);
            invalidECTestCase4.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase4.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase4);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase5 = new TestCase();
            invalidECTestCase5.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase5.setTestCaseTitle("Verify that the RI can initiate a Type 5 Error Response from the SUT as an OC.");
            invalidECTestCase5.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an EC and trigger a Type 5 Error Response as specified by the Standard.");
            invalidECTestCase5.setTestItemType("RI");
            invalidECTestCase5.setTestItemCenterMode("EC");
            invalidECTestCase5.setTestItemNeedID(need.getUnID());
            invalidECTestCase5.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase5.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase5.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase5.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase5.setTestCaseDialog(dialog);
            invalidECTestCase5.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase5.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase5);
            testCaseCount = testCaseCount + 1;


            TestCase invalidECTestCase6 = new TestCase();
            invalidECTestCase6.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase6.setTestCaseTitle("Verify that the RI can initiate a Type 6 Error Response from the SUT as an OC.");
            invalidECTestCase6.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an EC and trigger a Type 6 Error Response as specified by the Standard.");
            invalidECTestCase6.setTestItemType("RI");
            invalidECTestCase6.setTestItemCenterMode("EC");
            invalidECTestCase6.setTestItemNeedID(need.getUnID());
            invalidECTestCase6.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase6.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase6.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase6.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase6.setTestCaseDialog(dialog);
            invalidECTestCase6.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase6.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase6);
            testCaseCount = testCaseCount + 1;

            TestCase invalidECTestCase7 = new TestCase();
            invalidECTestCase7.setTestCaseID(makeTestCaseID("EC"));
            invalidECTestCase7.setTestCaseTitle("Verify that the RI can initiate a Type 7 Error Response from the SUT as an OC.");
            invalidECTestCase7.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an EC and trigger a Type 7 Error Response as specified by the Standard.");
            invalidECTestCase7.setTestItemType("RI");
            invalidECTestCase7.setTestItemCenterMode("EC");
            invalidECTestCase7.setTestItemNeedID(need.getUnID());
            invalidECTestCase7.setTestItemRequirements(makeTestCaseRequirementsList("EC", dialog, true));
            invalidECTestCase7.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidECTestCase7.setTestCaseInputs(makeTestCaseInputs("EC", dialog, true));
            invalidECTestCase7.setTestCaseOutputs(makeTestCaseOutputs("EC", dialog, true));
            invalidECTestCase7.setTestCaseDialog(dialog);
            invalidECTestCase7.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidECTestCase7.setDialogRequirements(dialogRequirements);
            testCases.add(invalidECTestCase7);
            testCaseCount = testCaseCount + 1;

        }
    }

    public void makeOCTestCases(String dialog, Requirement theRequirement) {
        /** Create Test Cases for SUT as EC */
        /** Valid Test Case */
            List<Requirement> requirementsList = theHandler.getDialogSpecificRequirements(dialog);
            List<String> dialogRequirements = new ArrayList<String>();
            for (Requirement thisRequirement:theHandler.getDialogSpecificRequirements(dialog)){
                if (!dialogRequirements.contains(thisRequirement.getRequirementID())){
                    dialogRequirements.add(thisRequirement.getRequirementID());
                }
            }

        if (dialog.endsWith("Update")) {
            TestCase validOCTestCase = new TestCase();
            validOCTestCase.setTestCaseID(makeTestCaseID("OC"));
            validOCTestCase.setTestCaseTitle("Verify that the SUT can " + theRequirement.getRequirement() + " as an OC");
            validOCTestCase.setTestCaseDescription("This test case verifies that the SUT performs the " + dialog + " dialog as an OC as specified by the Standard.");
            validOCTestCase.setTestItemType("SUT/RI");
            validOCTestCase.setTestItemCenterMode("OC");
            validOCTestCase.setTestItemNeedID(need.getUnID());
            validOCTestCase.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, false));
            validOCTestCase.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            validOCTestCase.setTestCaseInputs(makeTestCaseInputs("OC", dialog, false));
            validOCTestCase.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, false));
            validOCTestCase.setTestCaseDialog(dialog);
            List<TestCaseOutput> theOutputs = makeTestCaseOutputs("OC", dialog, false);
            String theMessage = "";
            for (TestCaseOutput thisOutput: theOutputs){
                theMessage = thisOutput.getName();
            }
            validOCTestCase.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            validOCTestCase.setDialogRequirements(dialogRequirements);
            testCases.add(validOCTestCase);
            testCaseCount = testCaseCount + 1;
        } else {
            TestCase validOCTestCase = new TestCase();
            validOCTestCase.setTestCaseID(makeTestCaseID("OC"));
            validOCTestCase.setTestCaseTitle("Verify that the SUT supports " + theRequirement.getRequirement() + " as an OC");
            validOCTestCase.setTestCaseDescription("This test case verifies that the SUT performs the " + dialog + " dialog as an OC as specified by the Standard.");
            validOCTestCase.setTestItemType("SUT/RI");
            validOCTestCase.setTestItemCenterMode("OC");
            validOCTestCase.setTestItemNeedID(need.getUnID());
            validOCTestCase.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, false));
            validOCTestCase.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            validOCTestCase.setTestCaseInputs(makeTestCaseInputs("OC", dialog, false));
            validOCTestCase.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, false));
            validOCTestCase.setTestCaseDialog(dialog);
            List<TestCaseOutput> theOutputs = makeTestCaseOutputs("OC", dialog, false);
            String theMessage = "";
            for (TestCaseOutput thisOutput: theOutputs){
                theMessage = thisOutput.getName();
            }
            validOCTestCase.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            validOCTestCase.setDialogRequirements(dialogRequirements);
            testCases.add(validOCTestCase);
            testCaseCount = testCaseCount + 1;

        }
        /** InValid Test Cases */
        if (!dialog.endsWith("Update")) {
            TestCase invalidOCTestCase1 = new TestCase();
            invalidOCTestCase1.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase1.setTestCaseTitle("Verify that the SUT provides a Type 1 Error Response to an invalid message from an EC.");
            invalidOCTestCase1.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an OC and provides a Type 1 Error Response as specified by the Standard.");
            invalidOCTestCase1.setTestItemType("SUT/RI");
            invalidOCTestCase1.setTestItemCenterMode("OC");
            invalidOCTestCase1.setTestItemNeedID(need.getUnID());
            invalidOCTestCase1.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase1.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase1.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase1.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase1.setTestCaseDialog(dialog);
            List<TestCaseOutput> theOutputs = makeTestCaseOutputs("OC", dialog, true);
            String theMessage = "";
            for (TestCaseOutput thisOutput: theOutputs){
                theMessage = thisOutput.getName();
            }
            invalidOCTestCase1.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase1.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase1);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase2 = new TestCase();
            invalidOCTestCase2.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase2.setTestCaseTitle("Verify that the SUT provides a Type 2 Error Response to an invalid message from an EC.");
            invalidOCTestCase2.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an OC and provides a Type 2 Error Response as specified by the Standard.");
            invalidOCTestCase2.setTestItemType("SUT/RI");
            invalidOCTestCase2.setTestItemCenterMode("OC");
            invalidOCTestCase2.setTestItemNeedID(need.getUnID());
            invalidOCTestCase2.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase2.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase2.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase2.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase2.setTestCaseDialog(dialog);
            invalidOCTestCase2.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase2.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase2);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase3 = new TestCase();
            invalidOCTestCase3.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase3.setTestCaseTitle("Verify that the SUT provides a Type 3 Error Response to an invalid message from an EC.");
            invalidOCTestCase3.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an OC and provides a Type 3 Error Response as specified by the Standard.");
            invalidOCTestCase3.setTestItemType("SUT/RI");
            invalidOCTestCase3.setTestItemCenterMode("OC");
            invalidOCTestCase3.setTestItemNeedID(need.getUnID());
            invalidOCTestCase3.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase3.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase3.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase3.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase3.setTestCaseDialog(dialog);
            invalidOCTestCase3.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase3.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase3);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase4 = new TestCase();
            invalidOCTestCase4.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase4.setTestCaseTitle("Verify that the SUT provides a Type 4 Error Response to an invalid message from an EC.");
            invalidOCTestCase4.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an OC and provides a Type 4 Error Response as specified by the Standard.");
            invalidOCTestCase4.setTestItemType("SUT/RI");
            invalidOCTestCase4.setTestItemCenterMode("OC");
            invalidOCTestCase4.setTestItemNeedID(need.getUnID());
            invalidOCTestCase4.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase4.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase4.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase4.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase4.setTestCaseDialog(dialog);
            invalidOCTestCase4.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase4.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase4);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase5 = new TestCase();
            invalidOCTestCase5.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase5.setTestCaseTitle("Verify that the SUT provides a Type 5 Error Response to an invalid message from an EC.");
            invalidOCTestCase5.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an OC and provides a Type 5 Error Response as specified by the Standard.");
            invalidOCTestCase5.setTestItemType("SUT/RI");
            invalidOCTestCase5.setTestItemCenterMode("OC");
            invalidOCTestCase5.setTestItemNeedID(need.getUnID());
            invalidOCTestCase5.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase5.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase5.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase5.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase5.setTestCaseDialog(dialog);
            invalidOCTestCase5.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase5.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase5);
            testCaseCount = testCaseCount + 1;


            TestCase invalidOCTestCase6 = new TestCase();
            invalidOCTestCase6.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase6.setTestCaseTitle("Verify that the SUT provides a Type 6 Error Response to an invalid message from an EC.");
            invalidOCTestCase6.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an OC and provides a Type 6 Error Response as specified by the Standard.");
            invalidOCTestCase6.setTestItemType("SUT/RI");
            invalidOCTestCase6.setTestItemCenterMode("OC");
            invalidOCTestCase6.setTestItemNeedID(need.getUnID());
            invalidOCTestCase6.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase6.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase6.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase6.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase6.setTestCaseDialog(dialog);
            invalidOCTestCase6.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase6.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase6);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase7 = new TestCase();
            invalidOCTestCase7.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase7.setTestCaseTitle("Verify that the SUT provides a Type 7 Error Response to an invalid message from an EC.");
            invalidOCTestCase7.setTestCaseDescription("This test case verifies that the SUT properly executes errorResponse portion of the " + dialog + " dialog as an OC and provides a Type 7 Error Response as specified by the Standard.");
            invalidOCTestCase7.setTestItemType("SUT/RI");
            invalidOCTestCase7.setTestItemCenterMode("OC");
            invalidOCTestCase7.setTestItemNeedID(need.getUnID());
            invalidOCTestCase7.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase7.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase7.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase7.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase7.setTestCaseDialog(dialog);
            invalidOCTestCase7.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase7.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase7);
            testCaseCount = testCaseCount + 1;

        } /** Create RI as OC Specific Test Cases */
        /** InValid Test Cases - Request */
        /** InValid Test Cases - Subscription*/
        // Only for Request-Response and Subscription dialogs
        else if (dialog.endsWith("Update")) {
            TestCase invalidOCTestCase1 = new TestCase();
            invalidOCTestCase1.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase1.setTestCaseTitle("Verify that the RI can initiate a Type 1 Error Response from the SUT as an OC.");
            invalidOCTestCase1.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an OC and trigger a Type 1 Error Response as specified by the Standard.");
            invalidOCTestCase1.setTestItemType("RI");
            invalidOCTestCase1.setTestItemCenterMode("OC");
            invalidOCTestCase1.setTestItemNeedID(need.getUnID());
            invalidOCTestCase1.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase1.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase1.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase1.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase1.setTestCaseDialog(dialog);
            List<TestCaseOutput> theOutputs = makeTestCaseOutputs("OC", dialog, true);
            String theMessage = "";
            for (TestCaseOutput thisOutput: theOutputs){
                theMessage = thisOutput.getName();
            }
            invalidOCTestCase1.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase1.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase1);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase2 = new TestCase();
            invalidOCTestCase2.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase2.setTestCaseTitle("Verify that the RI can initiate a Type 2 Error Response from the SUT as an OC.");
            invalidOCTestCase2.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an OC and trigger a Type 2 Error Response as specified by the Standard.");
            invalidOCTestCase2.setTestItemType("RI");
            invalidOCTestCase2.setTestItemCenterMode("OC");
            invalidOCTestCase2.setTestItemNeedID(need.getUnID());
            invalidOCTestCase2.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase2.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase2.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase2.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase2.setTestCaseDialog(dialog);
            invalidOCTestCase2.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase2.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase2);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase3 = new TestCase();
            invalidOCTestCase3.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase3.setTestCaseTitle("Verify that the RI can initiate a Type 3 Error Response from the SUT as an OC.");
            invalidOCTestCase3.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an OC and trigger a Type 3 Error Response as specified by the Standard.");
            invalidOCTestCase3.setTestItemType("RI");
            invalidOCTestCase3.setTestItemCenterMode("OC");
            invalidOCTestCase3.setTestItemNeedID(need.getUnID());
            invalidOCTestCase3.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase3.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase3.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase3.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase3.setTestCaseDialog(dialog);
            invalidOCTestCase3.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase3.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase3);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase4 = new TestCase();
            invalidOCTestCase4.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase4.setTestCaseTitle("Verify that the RI can initiate a Type 4 Error Response from the SUT as an OC.");
            invalidOCTestCase4.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an OC and trigger a Type 4 Error Response as specified by the Standard.");
            invalidOCTestCase4.setTestItemType("RI");
            invalidOCTestCase4.setTestItemCenterMode("OC");
            invalidOCTestCase4.setTestItemNeedID(need.getUnID());
            invalidOCTestCase4.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase4.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase4.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase4.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase4.setTestCaseDialog(dialog);
            invalidOCTestCase4.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase4.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase4);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase5 = new TestCase();
            invalidOCTestCase5.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase5.setTestCaseTitle("Verify that the RI can initiate a Type 5 Error Response from the SUT as an OC.");
            invalidOCTestCase5.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an OC and trigger a Type 5 Error Response as specified by the Standard.");
            invalidOCTestCase5.setTestItemType("RI");
            invalidOCTestCase5.setTestItemCenterMode("OC");
            invalidOCTestCase5.setTestItemNeedID(need.getUnID());
            invalidOCTestCase5.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase5.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase5.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase5.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase5.setTestCaseDialog(dialog);
            invalidOCTestCase5.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase5.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase5);
            testCaseCount = testCaseCount + 1;


            TestCase invalidOCTestCase6 = new TestCase();
            invalidOCTestCase6.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase6.setTestCaseTitle("Verify that the RI can initiate a Type 6 Error Response from the SUT as an OC.");
            invalidOCTestCase6.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an OC and trigger a Type 6 Error Response as specified by the Standard.");
            invalidOCTestCase6.setTestItemType("RI");
            invalidOCTestCase6.setTestItemCenterMode("OC");
            invalidOCTestCase6.setTestItemNeedID(need.getUnID());
            invalidOCTestCase6.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase6.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase6.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase6.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase6.setTestCaseDialog(dialog);
            invalidOCTestCase6.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase6.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase6);
            testCaseCount = testCaseCount + 1;

            TestCase invalidOCTestCase7 = new TestCase();
            invalidOCTestCase7.setTestCaseID(makeTestCaseID("OC"));
            invalidOCTestCase7.setTestCaseTitle("Verify that the RI can initiate a Type 7 Error Response from the SUT as an OC.");
            invalidOCTestCase7.setTestCaseDescription("This test case verifies that the RI can initiate the errorResponse portion of the " + dialog + " dialog as an OC and trigger a Type 7 Error Response as specified by the Standard.");
            invalidOCTestCase7.setTestItemType("RI");
            invalidOCTestCase7.setTestItemCenterMode("OC");
            invalidOCTestCase7.setTestItemNeedID(need.getUnID());
            invalidOCTestCase7.setTestItemRequirements(makeTestCaseRequirementsList("OC", dialog, true));
            invalidOCTestCase7.setPreConditionRequirement(dialog.endsWith("Update") || dialog.endsWith("Subscription") ? theRequirement.getRequirementID() : "");
            invalidOCTestCase7.setTestCaseInputs(makeTestCaseInputs("OC", dialog, true));
            invalidOCTestCase7.setTestCaseOutputs(makeTestCaseOutputs("OC", dialog, true));
            invalidOCTestCase7.setTestCaseDialog(dialog);
            invalidOCTestCase7.setMessageElements(getConfirmedMessageElementList(theHandler,theMessage));
            invalidOCTestCase7.setDialogRequirements(dialogRequirements);
            testCases.add(invalidOCTestCase7);
            testCaseCount = testCaseCount + 1;

        }
        /** Create Test Cases for SUT as OC */
        /** Create RI as OC Specific Test Cases */
        /** InValid Test Cases - Publication*/
    }

    public String makeTestCaseID(String mode) {
        return "TCS_" + need.getNeedNumber() + "_" + testCaseCount;
    }

//    public String makeTestCaseTitle(String mode, String itemType) {
//    }
//    public String makeTestCaseDescription(String mode, String itemType) {
//    }
    public List<TestCaseInput> makeTestCaseInputs(String mode, String dialog, boolean errorCase) {
        List<TestCaseInput> theInputs = new ArrayList<TestCaseInput>();
        DesignElement theElement = (DesignElement) theDesignContent.getDesignMap().get(dialog);
        if (theElement != null) {
            if (mode.equals("EC")) {  // The SUT is an EC
                if (!dialog.endsWith("Update")) { // Req/Response or Subscription Request
                    theInputs.add(new TestCaseInput("Operator Initiatiation of the " + dialog + " dialog.", "From the Test Plan"));
                } else {
                    if (dialog.endsWith("Update")) {  // Publication dialog
                        DesignElement inputMessageHeader = theElement.getSubElements().get(0);
                        theInputs.add(new TestCaseInput(inputMessageHeader.getElementName(), "From the Test Plan"));
                        DesignElement inputMessage = theElement.getSubElements().get(1);
                        theInputs.add(new TestCaseInput(inputMessage.getElementName(), "From the Test Plan"));

//                    } else {
//                        DesignElement outputMessage = theElement.getSubElements().get(0);
//                        theInputs.add(new TestCaseInput(outputMessage.getElementName(), "From the Test Plan"));

                    }
                }
            } else {  // The SUT is an OC
                if (dialog.endsWith("Update")) {  // Publication
                    theInputs.add(new TestCaseInput("Operator Initiatiation of the " + dialog + " dialog.", "From the Test Plan"));
                } else {
                    if (dialog.endsWith("Subscription")) {  // Subscription Dialogs
                        DesignElement outputMessageHeader = theElement.getSubElements().get(0);
                        theInputs.add(new TestCaseInput(outputMessageHeader.getElementName(), "From the Test Plan"));
                        DesignElement outputMessage = theElement.getSubElements().get(1);
                        theInputs.add(new TestCaseInput(outputMessage.getElementName(), "From the Test Plan"));

                    } else {  // Request/Response
                        DesignElement outputMessage = theElement.getSubElements().get(0);
                        theInputs.add(new TestCaseInput(outputMessage.getElementName(), "From the Test Plan"));

                    }
                }
            }
        }
        return theInputs;
    }

    public List<TestCaseOutput> makeTestCaseOutputs(String mode, String dialog, boolean errorCase) {
        List<TestCaseOutput> theOutputs = new ArrayList<TestCaseOutput>();
        DesignElement theElement = (DesignElement) theDesignContent.getDesignMap().get(dialog);
        if (theElement != null) {
            if (mode.equals("EC")) {
                if (dialog.endsWith("Update")) {  // Publication
                    if (errorCase) {
                        if (theElement.getSubElements().size() == 4) {
                            DesignElement outputMessage = theElement.getSubElements().get(3);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));
                        } else {
                            theOutputs.add(new TestCaseOutput("***Error in WSDL Specification of " + dialog + "***", "Error in WSDL Specification of " + dialog));
//                            System.out.println("Here's the problem!!!");
                        }

                    } else {
                        DesignElement outputMessage;
                        try{
                            outputMessage = theElement.getSubElements().get(2);
                        } catch (Exception ex){
                            outputMessage = theElement.getSubElements().get(1);

                        }
                        theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));
                    }
//                    theOutputs.add(new TestCaseOutput("Operator Initiatiation of the " + dialog + " dialog.", "Per TMDD v3.1"));
                } else {  // Request or Subscription dialogs
                    if (dialog.endsWith("Subscription")) {
                        DesignElement outputMessageHeader = theElement.getSubElements().get(0);
                        theOutputs.add(new TestCaseOutput(outputMessageHeader.getElementName(), "Per TMDD v3.1"));
                        DesignElement outputMessage = theElement.getSubElements().get(1);
                        theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));

                    } else { // Request dialog
                        DesignElement outputMessage = theElement.getSubElements().get(0);
                        theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));

                    }
                }
            } else {  // SUT is an OC
                if (dialog.endsWith("Update")) {  // Publication
                    DesignElement outputMessageHeader = theElement.getSubElements().get(0);
                    theOutputs.add(new TestCaseOutput(outputMessageHeader.getElementName(), "Per TMDD v3.1"));
                    DesignElement outputMessage = theElement.getSubElements().get(1);
                    theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));
//                    theOutputs.add(new TestCaseOutput("Operator Initiatiation of the " + dialog + " dialog.", "Per TMDD v3.1"));
                } else {
                    if (dialog.endsWith("Subscription")) {  // Subscription
                        if (errorCase) {
                            DesignElement outputMessage = theElement.getSubElements().get(3);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));

                        } else {
                            DesignElement outputMessage = theElement.getSubElements().get(2);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));

                        }

                    } else {  // Request Dialog
                        if (errorCase) {
                            DesignElement outputMessage = theElement.getSubElements().get(2);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));

                        } else {
                            DesignElement outputMessage = theElement.getSubElements().get(1);
                            theOutputs.add(new TestCaseOutput(outputMessage.getElementName(), "Per TMDD v3.1"));

                        }
                    }
                }
            }
        }
        return theOutputs;
    }

    public List<TestVariable> makeTestCaseVariables(String mode, String dialog) {
        return null;
    }


    public List<String> makeTestCaseRequirementsList(String mode, String dialog, boolean errorCase) {
        List<String> theRequirements = new ArrayList<String>();

        // We are always testing the requirement related to the dialog in each test case.
        List<String> theDialogs = theHandler.getNeedRelatedDialogs();
        for (String thisDialog : theDialogs) {
            if (thisDialog.equals(dialog)) {
                List<Requirement> theReqList = theHandler.getDialogSpecificRequirements(dialog);
                for (Requirement theRequirement : theReqList) {
                    theRequirements.add(theRequirement.getRequirementID());
                }
                break;
            }
        }

        if (!errorCase) {
            DesignElement theElement = (DesignElement) theDesignContent.getDesignMap().get(dialog);
            if (theElement != null) {
                String messageName = "";
                if (mode.equals("EC")) {
                    if (dialog.endsWith("Update")) {
//                    messageName = theElement.getSubElements().get(2).getElementName();
                    } else if (dialog.endsWith("Subscription")) {
                        messageName = theElement.getSubElements().get(1).getElementName();
                    } else {
                        messageName = theElement.getSubElements().get(0).getElementName();
                    }


                } else // mode is OC
                {
                    if (dialog.endsWith("Update")) {
                        messageName = theElement.getSubElements().get(1).getElementName();
                    } else if (dialog.endsWith("Subscription")) {
//                    messageName = theElement.getSubElements().get(2).getElementName();
                    } else {
                        messageName = theElement.getSubElements().get(1).getElementName();
                    }
                }

                if (!messageName.equals("")) {
                    try {
                        int reqCount = theHandler.getMessageConceptElementCount(messageName);
                        for (int ii = 0; ii < reqCount; ii++) {
                            String reqID = theHandler.getMessageConceptElementRequirementID(messageName, ii);
                            if (!theRequirements.contains(reqID)) theRequirements.add(reqID);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            }
        }
        return theRequirements;

    }

    private List<MessageElement> getConfirmedMessageElementList(NeedHandler newHandler, String targetMessage) {
         List<MessageElement> matchedElements = new ArrayList<MessageElement>();
         boolean sizeMismatchErrorReported = false;

        List<String> needMessages = newHandler.getNeedRelatedMessages();
        for (String theMessage : needMessages) {
            if (theMessage.equalsIgnoreCase(targetMessage)){
            try {
                DesignElement messageElement = (DesignElement) theDesignContent.getDesignMap().get(theMessage);
                Integer elementCount = newHandler.getMessageConceptElementCount(theMessage);
           //     System.out.println(newHandler.getNeedID() + "--   Message " + theMessage + ":");
                DesignElement messageFrameElement= (DesignElement) theDesignContent.getDesignMap().get(messageElement.getSubType());
                MessageSpecificDesignElementHandler msgDsgnHandler = new MessageSpecificDesignElementHandler(targetMessage, messageFrameElement);
                String currentPath = "";
                for (int ii = 0; ii < elementCount; ii++) {
                    if (ii == 0) {
           //             System.out.println(" Message  " + theMessage + " = " + messageElement.getElementName() + "   "
           //                     + newHandler.getMessageConceptElement(theMessage, ii).equals(messageElement.getElementName()));
                        if (!newHandler.getMessageConceptElement(theMessage, ii).equals(messageElement.getElementName())) {
                        } else {
                            newHandler.getMessageDataConcept(theMessage, ii).setConceptReferenceName(messageElement.getReferenceName());
                            newHandler.getMessageDataConcept(theMessage, ii).setConceptPath(messageElement.getReferenceName());
                            currentPath = messageElement.getReferenceName();
                        }
     //               } else if (ii == 1) {
            //            System.out.println("    Frame  " + newHandler.getMessageConceptElement(theMessage, ii) + " = " + messageElement.getSubType() + "   "
            //                    + newHandler.getMessageConceptElement(theMessage, ii).equals(messageElement.getSubType()));
     //                   if (!newHandler.getMessageConceptElement(theMessage, ii).equals(messageElement.getSubType())) {
     //                   } else {
     //                       newHandler.getMessageDataConcept(theMessage, ii).setConceptReferenceName(messageElement.getReferenceName());
     //                       newHandler.getMessageDataConcept(theMessage, ii).setConceptPath(messageElement.getReferenceName());
     //                   }
                    } else {

                        List<ElementMatch> matchingElements = msgDsgnHandler.getMatchingElements(newHandler.getMessageDataConcept(theMessage, ii).getInstanceName());
                        if (!matchingElements.isEmpty()){
                            for (ElementMatch thisMatch : matchingElements){   
                                DesignElement frameElement = thisMatch.getTheElement();
                                Requirement relatedRequirement = newHandler.getRequirement(newHandler.getMessageConceptElementRequirementID(theMessage, ii));
                                    if (((!relatedRequirement.getConformance().equals("M")) && (frameElement.getMinOccurs() != 0))
                                            || ((relatedRequirement.getConformance().equals("M")) && (frameElement.getMinOccurs() == 0))) {
                                } else{
                                        MessageElement thisElement = new MessageElement();
                                        thisElement.setElementName(frameElement.getReferenceName().replace("\\", ""));
                                        thisElement.setElementRequirement(relatedRequirement.getRequirementID());
                                        thisElement.setParentMessage(targetMessage);
                                        thisElement.setOptional(!relatedRequirement.getConformance().equals("M"));
                                        thisElement.setElementType(newHandler.getMessageConceptElement(theMessage, ii));
                                        thisElement.setParentElement(thisMatch.getFullPath());
                                        matchedElements.add(thisElement);                                                         
                                }
                            }
                        }
/**
                        DesignElement frameElement = (DesignElement) theDesignContent.getDesignMap().get(newHandler.getMessageConceptElement(theMessage, 1));
                        if (frameElement != null) {
                            if ((frameElement.getSubElements().size() > 0) && (ii - 2 < frameElement.getSubElements().size())) {
            //                    System.out.println(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage + "     Element " + newHandler.getMessageConceptElement(theMessage, ii)
            //                            + " = " + frameElement.getSubElements().get(ii - 2).getElementName() + " "
            //                            + newHandler.getMessageConceptElement(theMessage, ii).equals(frameElement.getSubElements().get(ii - 2).getElementName()));
                                if (!newHandler.getMessageConceptElement(theMessage, ii).equals(frameElement.getSubElements().get(ii - 2).getElementName())) {
                                } else {

                                    Requirement relatedRequirement = newHandler.getRequirement(newHandler.getMessageConceptElementRequirementID(theMessage, ii));
                                    if (((!relatedRequirement.getConformance().equals("M")) && (frameElement.getMinOccurs() != 0))
                                            || ((relatedRequirement.getConformance().equals("M")) && (frameElement.getMinOccurs() == 0))) {
            //                            System.out.println("*** Optional Mismatch *** " + relatedRequirement.getConformance() + " <> " + (frameElement.getMinOccurs() == 0 ? "O" : "M"));
//                                    } else {
//                                        if (!relatedRequirement.getConformance().equals("M"))System.out.println("--> Optional Match "+ relatedRequirement.getConformance()+ " = "+(frameElement.getMinOccurs() == 0 ? "O" : "M"));
                                    } else {
                                        MessageElement thisElement = new MessageElement();
                                        thisElement.setElementName(frameElement.getSubElements().get(ii - 2).getReferenceName().replace("\\",""));
                                        thisElement.setElementRequirement(relatedRequirement.getRequirementID());
                                        thisElement.setParentMessage(targetMessage);
                                        thisElement.setOptional(!relatedRequirement.getConformance().equals("M"));
                                        thisElement.setElementType(newHandler.getMessageConceptElement(theMessage, ii));
                                        matchedElements.add(thisElement);
                                    }

                                    newHandler.getMessageDataConcept(theMessage, ii).setConceptReferenceName(frameElement.getSubElements().get(ii - 2).getReferenceName());
                                    newHandler.getMessageDataConcept(theMessage, ii).setConceptPath(currentPath + frameElement.getSubElements().get(ii - 2).getReferenceName());
                                }

                            } else {  // We don't want to clutter the error report with excessive error information
                                if (!sizeMismatchErrorReported) {
                                    String extraElements = "";
                                    for (int kk = ii; kk < elementCount; kk++) {
                                        extraElements = extraElements.concat("(" + (kk - 1) + ") [" + newHandler.getMessageConceptElementRequirementID(theMessage, kk) + "] " + newHandler.getMessageConceptElement(theMessage, kk) + "; ");
                                    }
          //                          System.out.println(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage +  "  **Count Mismatch** RTM has more elements than available in the Design  " + (elementCount - 2) + " versus " + frameElement.getSubElements().size()
          //                                  + " :: Extra Elements=> " + extraElements);
                                    sizeMismatchErrorReported = true;
                                    //                                test3Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "  ******* ERROR ******* More elements than available in the Design  " + (ii - 2) + " versus " + frameElement.getSubElements().size());
//                                test3Results = false;
//                                System.out.println(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "   ******* ERROR ******* More elements than available in the Design  " + (ii - 2) + " versus " + frameElement.getSubElements().size());
                                }
                            }

                        }
*/
                    }


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            }
        }
        return matchedElements;
    }

}




