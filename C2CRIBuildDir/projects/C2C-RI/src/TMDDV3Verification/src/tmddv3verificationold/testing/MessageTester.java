/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verificationold.testing;

import java.util.ArrayList;
import java.util.List;
import tmddv3verificationold.DesignDetail;
import tmddv3verificationold.DesignElement;
import tmddv3verificationold.Need;
import tmddv3verificationold.NeedHandler;
import tmddv3verificationold.Predicate;
import tmddv3verificationold.Requirement;
import tmddv3verificationold.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class MessageTester {

    private List<Predicate> predicatesList;
    private DesignDetail tmddDesign;
    private List<TestResult> testResults = new ArrayList<TestResult>();
    private List<String> test1Errors = new ArrayList<String>();
    private List<String> test2Errors = new ArrayList<String>();
    private List<String> test3Errors = new ArrayList<String>();

    private MessageTester() {
    }

    public MessageTester(List<Predicate> thePredicatesList, DesignDetail theTmddDesign) {
        predicatesList = thePredicatesList;
        tmddDesign = theTmddDesign;
        initResults();
    }

    public void performMessageTests(Need theNeed) {
        testResults.clear();
        NeedHandler newHandler = new NeedHandler(theNeed);
        if (newHandler.getErrorList().size() > 0) {
            for (TestResult thisResult : newHandler.getErrorList()) {
//                thisResult.setNeedID(theNeed.getUnID());
//                thisResult.setNeedText(theNeed.getUserNeed());
                thisResult.setTestType("MessageTest");
                thisResult.setSubTestTitle("General");
                thisResult.setTestResult("Other");
                testResults.add(thisResult);
                System.out.println(thisResult.getErrorDescription());
            }
        }


        TestResult firstTestResult = new TestResult();
        firstTestResult.setNeedID(theNeed.getUnID());
        firstTestResult.setNeedText(theNeed.getUserNeed());
        firstTestResult.setTestType("MessageTest");
        firstTestResult.setSubTestTitle("1. Are all Messages related to this need in the Design?");
        test1Errors.clear();
        if (newHandler.getMessagesCount() > 0) {
            Boolean firstResult = isMessageInDesign(newHandler);
            firstTestResult.setTestResult(firstResult.toString());
        } else {
            firstTestResult.setTestResult("NA");
        }
        firstTestResult.setErrorDescription(errorListtoString(test1Errors));
        testResults.add(firstTestResult);


        TestResult secondTestResult = new TestResult();
        secondTestResult.setNeedID(theNeed.getUnID());
        secondTestResult.setNeedText(theNeed.getUserNeed());
        secondTestResult.setTestType("MessageTest");
        secondTestResult.setSubTestTitle("2. Do message frames always follow each message element within rtm content referenced by this need?");
        test2Errors.clear();
        if (newHandler.getMessagesCount() > 0) {
            Boolean result = doesNextElementTracetoMessageFrame(newHandler);
            secondTestResult.setTestResult(result.toString());
        } else {
            secondTestResult.setTestResult("NA");
        }
        secondTestResult.setErrorDescription(errorListtoString(test2Errors));
        testResults.add(secondTestResult);

        TestResult thirdTestResult = new TestResult();
        thirdTestResult.setNeedID(theNeed.getUnID());
        thirdTestResult.setNeedText(theNeed.getUserNeed());
        thirdTestResult.setTestType("MessageTest");
        thirdTestResult.setSubTestTitle("3. Can we match the RTM List of Message Elements with the Design List of message elements for messages referenced by this need?");
        test3Errors.clear();
        if (newHandler.getMessagesCount() > 0) {
            Boolean result = doesRTMListofElementsMatchDesign(newHandler);
            thirdTestResult.setTestResult(result.toString());
        } else {
            thirdTestResult.setTestResult("NA");
        }
        thirdTestResult.setErrorDescription(errorListtoString(test3Errors));
        testResults.add(thirdTestResult);

        writeResults();
    }

    private boolean isMessageInDesign(NeedHandler newHandler) {
        boolean testResult = true;
        List<String> needMessages = newHandler.getNeedRelatedMessages();
        for (String theMessage : needMessages) {
            boolean messageResult = tmddDesign.getDesignMap().containsKey(theMessage);
            if (!messageResult) {
                test1Errors.add("Message " + theMessage + " not found in design.");
                testResult = false;
            }
        }
        return testResult;
    }

    private boolean doesNextElementTracetoMessageFrame(NeedHandler newHandler) {
        boolean testResult = false;

        List<String> needMessages = newHandler.getNeedRelatedMessages();
        for (String theMessage : needMessages) {
            try {
                Integer elementCount = newHandler.getMessageConceptElementCount(theMessage);
                for (int ii = 0; ii < elementCount; ii++) {
                    if (ii == 1) {

                        if (newHandler.getMessageConceptElementType(theMessage, ii).equals("data-frame")) {
                            testResult = true;
                        } else {
                            test2Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + " Element after the message data concept in RTM is not a Frame.");
                        }
                    }
                }
            } catch (Exception ex) {
                test2Errors.add(ex.getMessage());
            }

        }
        return testResult;


    }

    private boolean doesRTMListofElementsMatchDesign(NeedHandler newHandler) {
        boolean test3Results = true;
        boolean sizeMismatchErrorReported = false;

        List<String> needMessages = newHandler.getNeedRelatedMessages();
        for (String theMessage : needMessages) {
            try {
                DesignElement messageElement = (DesignElement) tmddDesign.getDesignMap().get(theMessage);
                Integer elementCount = newHandler.getMessageConceptElementCount(theMessage);
                System.out.println(newHandler.getNeedID() + "--   Message " + theMessage + ":");
                String currentPath = "";
                for (int ii = 0; ii < elementCount; ii++) {
                    if (ii == 0) {
                        System.out.println(" Message  " + theMessage + " = " + messageElement.getElementName() + "   "
                                + newHandler.getMessageConceptElement(theMessage, ii).equals(messageElement.getElementName()));
                        if (!newHandler.getMessageConceptElement(theMessage, ii).equals(messageElement.getElementName())) {
                            test3Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage + "    Element " + newHandler.getMessageConceptElement(theMessage, ii)
                                    + " does not match " + messageElement.getElementName() + " from design.");
                            test3Results = false;
                        } else {
                            newHandler.getMessageDataConcept(theMessage, ii).setConceptReferenceName(messageElement.getReferenceName());
                            newHandler.getMessageDataConcept(theMessage, ii).setConceptPath(messageElement.getReferenceName());
                            currentPath = messageElement.getReferenceName();
                        }
                    } else if (ii == 1) {
                        System.out.println("    Frame  " + newHandler.getMessageConceptElement(theMessage, ii) + " = " + messageElement.getSubType() + "   "
                                + newHandler.getMessageConceptElement(theMessage, ii).equals(messageElement.getSubType()));
                        if (!newHandler.getMessageConceptElement(theMessage, ii).equals(messageElement.getSubType())) {
                            test3Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage + "    Element " + newHandler.getMessageConceptElement(theMessage, ii)
                                    + " does not match " + messageElement.getSubType() + " from design.");
                            test3Results = false;
                        } else {
                            newHandler.getMessageDataConcept(theMessage, ii).setConceptReferenceName(messageElement.getReferenceName());
                            newHandler.getMessageDataConcept(theMessage, ii).setConceptPath(messageElement.getReferenceName());
                        }
                    } else {
                        DesignElement frameElement = (DesignElement) tmddDesign.getDesignMap().get(newHandler.getMessageConceptElement(theMessage, 1));
                        if (frameElement != null) {
                            if ((frameElement.getSubElements().size() > 0) && (ii - 2 < frameElement.getSubElements().size())) {
                                System.out.println(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage + "     Element " + newHandler.getMessageConceptElement(theMessage, ii)
                                        + " = " + frameElement.getSubElements().get(ii - 2).getElementName() + " "
                                        + newHandler.getMessageConceptElement(theMessage, ii).equals(frameElement.getSubElements().get(ii - 2).getElementName()));
                                if (!newHandler.getMessageConceptElement(theMessage, ii).equals(frameElement.getSubElements().get(ii - 2).getElementName())) {
                                    test3Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage + "    Element " + newHandler.getMessageConceptElement(theMessage, ii)
                                            + " does not match " + frameElement.getSubElements().get(ii - 2).getElementName() + " from design.");
                                    test3Results = false;
                                } else {

                                    Requirement relatedRequirement = newHandler.getRequirement(newHandler.getMessageConceptElementRequirementID(theMessage, ii));
                                    if (((!relatedRequirement.getConformance().equals("M")) && (frameElement.getMinOccurs() != 0))
                                            || ((relatedRequirement.getConformance().equals("M")) && (frameElement.getMinOccurs() == 0))) {
                                        test3Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage + "    Element " + newHandler.getMessageConceptElement(theMessage, ii)
                                                + "**Conformance Setting Error**   Conformance setting \"" + relatedRequirement.getConformance() + "\" does not match design element " + frameElement.getSubElements().get(ii - 2).getElementName() + " setting of " + (frameElement.getMinOccurs() == 0 ? "\"O\"" : "\"M"));
                                        test3Results = false;
                                        System.out.println("*** Optional Mismatch *** " + relatedRequirement.getConformance() + " <> " + (frameElement.getMinOccurs() == 0 ? "O" : "M"));
//                                    } else {
//                                        if (!relatedRequirement.getConformance().equals("M"))System.out.println("--> Optional Match "+ relatedRequirement.getConformance()+ " = "+(frameElement.getMinOccurs() == 0 ? "O" : "M"));
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
                                    test3Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage 
                                            + "  **Count Mismatch** RTM has more elements than available in the Design  "  + (elementCount - 2) + " versus " + frameElement.getSubElements().size()
                                            + " :: Extra Elements=> " + extraElements);
                                    test3Results = false;
                                    System.out.println(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "--  TargetMessage= " + theMessage +  "  **Count Mismatch** RTM has more elements than available in the Design  " + (elementCount - 2) + " versus " + frameElement.getSubElements().size()
                                            + " :: Extra Elements=> " + extraElements);
                                    sizeMismatchErrorReported = true;
                                    //                                test3Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "  ******* ERROR ******* More elements than available in the Design  " + (ii - 2) + " versus " + frameElement.getSubElements().size());
//                                test3Results = false;
//                                System.out.println(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "   ******* ERROR ******* More elements than available in the Design  " + (ii - 2) + " versus " + frameElement.getSubElements().size());
                                }
                            }

                        }

                    }


                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
        if (test3Results) {
            System.out.println(newHandler.getNeedID() + " Test PASSED");
        }
        return test3Results;
    }

    public String errorListtoString(List<String> errorList) {
        StringBuffer errorDescription = new StringBuffer();

        if (errorList.size() > 0) {
            for (String thisError : errorList) {
                errorDescription = errorDescription.append(thisError).append("<LF>");
            }

            return errorDescription.toString();
        } else {
            return " ";
        }


    }

    public final void initResults() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'MessageTest'");

        theDatabase.disconnectFromDatabase();
    }

    public void writeResults() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'MessageTest'");


        for (TestResult thisResult : testResults) {
            theDatabase.queryNoResult("INSERT INTO TMDDTestResultsTable ([NeedID],NeedText,"
                    + "TestType,[SubTestTitle],TestResult,ErrorDescription) VALUES ("
                    + "'" + thisResult.getNeedID() + "',"
                    + "'" + thisResult.getNeedText() + "',"
                    + "'" + thisResult.getTestType() + "',"
                    + "'" + thisResult.getSubTestTitle() + "',"
                    + "'" + thisResult.getTestResult() + "',"
                    + "'" + thisResult.getErrorDescription() + "')");

            if (thisResult.getErrorDescription().startsWith("false")) {
                System.out.println("Stop the Presses!!!");
            }
        }

        theDatabase.disconnectFromDatabase();
    }
}
