/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verification.testing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import tmddv3verification.DataConcept;
import tmddv3verification.DesignDetail;
import tmddv3verification.DesignElement;
import tmddv3verification.Need;
import tmddv3verification.Predicate;
import tmddv3verification.Requirement;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class DialogTester {

    private List<Predicate> predicatesList;
    private DesignDetail tmddDesign;
    private List<String> dialogList = new ArrayList<String>();
    private List<String> test1Errors = new ArrayList<String>();
    private List<String> test2Errors = new ArrayList<String>();
    private List<String> test3Errors = new ArrayList<String>();
    private List<TestResult> testResults = new ArrayList<TestResult>();

    public DialogTester(List<Predicate> thePredicatesList, DesignDetail theTmddDesign) {
        predicatesList = thePredicatesList;
        tmddDesign = theTmddDesign;
        initResults();
    }

    public void performDialogTests(Need theNeed) {
        testResults.clear();
        TestResult dialogTestResult = new TestResult();
        dialogTestResult.setNeedID(theNeed.getUnID());
        dialogTestResult.setNeedText(theNeed.getUserNeed());
        dialogTestResult.setTestType("DialogTest");

        test1Errors.clear();
        Boolean test1Result = doesNeedReferenceADialog(theNeed);
        System.out.println(theNeed.getUnID() + "," + theNeed.getUserNeed() + "," + "DialogTest" + "," + "Does Need reference a dialog?" + "," + test1Result + "," + (test1Errors.isEmpty() ? "" : test1Errors.toString()));
        dialogTestResult.setSubTestTitle("1. Does this need reference at least one dialog?");
        dialogTestResult.setTestResult(test1Result.toString());
        dialogTestResult.setErrorDescription(errorListtoString(test1Errors));
        testResults.add(dialogTestResult);

        if (test1Result) {
            test2Errors.clear();
            Boolean test2Result = isDialogInDesign(theNeed);
            System.out.println(theNeed.getUnID() + "," + theNeed.getUserNeed() + "," + "DialogTest" + "," + "Is Dialog in the design?" + "," + test2Result + "," + (test2Errors.isEmpty() ? "" : test2Errors.toString()));
            TestResult dialogTestResult2 = new TestResult();
            dialogTestResult2.setNeedID(theNeed.getUnID());
            dialogTestResult2.setNeedText(theNeed.getUserNeed());
            dialogTestResult2.setTestType("DialogTest");
            dialogTestResult2.setSubTestTitle("2. Can each dialog referenced by this need be directly found in the design?");
            dialogTestResult2.setTestResult(test2Result.toString());
            dialogTestResult2.setErrorDescription(errorListtoString(test2Errors));
            testResults.add(dialogTestResult2);

            test3Errors.clear();
            Boolean test3Result = areDialogRelatedMessagesInRTM(theNeed);
            System.out.println(theNeed.getUnID() + "," + theNeed.getUserNeed() + "," + "DialogTest" + "," + "Are Dialog Messages in the RTM?" + "," + test3Result + "," + (test3Errors.isEmpty() ? "" : test3Errors.toString()));
            TestResult dialogTestResult3 = new TestResult();
            dialogTestResult3.setNeedID(theNeed.getUnID());
            dialogTestResult3.setNeedText(theNeed.getUserNeed());
            dialogTestResult3.setTestType("DialogTest");
            dialogTestResult3.setSubTestTitle("3. Are only messages related to the referenced dialogs included in the RTM?");
            dialogTestResult3.setTestResult(test3Result.toString());
            dialogTestResult3.setErrorDescription(errorListtoString(test3Errors));
            testResults.add(dialogTestResult3);
            dialogList.clear();

        } else {
            System.out.println(theNeed.getUnID() + "," + theNeed.getUserNeed() + "," + "DialogTest" + "," + "Is Dialog in the design?" + "," + "NA");
            test2Errors.clear();
            TestResult dialogTestResult2 = new TestResult();
            dialogTestResult2.setNeedID(theNeed.getUnID());
            dialogTestResult2.setNeedText(theNeed.getUserNeed());
            dialogTestResult2.setTestType("DialogTest");
            dialogTestResult2.setSubTestTitle("2. Can each dialog referenced by this need be directly found in the design?");
            dialogTestResult2.setTestResult("NA");
            dialogTestResult2.setErrorDescription(errorListtoString(test2Errors));
            testResults.add(dialogTestResult2);
            System.out.println(theNeed.getUnID() + "," + theNeed.getUserNeed() + "," + "DialogTest" + "," + "Are Dialog Messages in the RTM?" + "," + "NA");

            test3Errors.clear();
            TestResult dialogTestResult3 = new TestResult();
            dialogTestResult3.setNeedID(theNeed.getUnID());
            dialogTestResult3.setNeedText(theNeed.getUserNeed());
            dialogTestResult3.setTestType("DialogTest");
            dialogTestResult3.setSubTestTitle("3. Are only messages related to the referenced dialogs included in the RTM?");
            dialogTestResult3.setTestResult("NA");
            dialogTestResult3.setErrorDescription(errorListtoString(test3Errors));
            testResults.add(dialogTestResult3);
        }
        writeResults();
    }

    public boolean doesNeedReferenceADialog(Need theNeed) {
        boolean testResult = false;

        // Is a dialog referenced directly?
        // Check through the design contents of the need for data concepts of a dialog type.
        for (Requirement thisRequirement : theNeed.getRequirementList()) {
            for (DataConcept thisConcept : thisRequirement.getDataConceptList()) {
                if (thisConcept.getDataConceptType().equals("dialog")) {
                    dialogList.add(thisConcept.getDataConceptName());
                    testResult = true;
//                    break;
                }
            }
//            if (testResult) {
//                break;
//            }
        }

        // If there isn't a direct trace to a dialog check to see if one is referenced through a predicate?
        if (!testResult) {
            for (Requirement thisRequirement : theNeed.getRequirementList()) {
                for (Predicate thisPredicate : predicatesList) {
                    if (thisRequirement.getConformance().contains(thisPredicate.getPredicateName())) {
//                                System.out.println("****************"+thisRequirement.getConformance()+" contains "+thisPredicate.getPredicateName()+"*****************");
                        for (DataConcept thisDataConcept : thisPredicate.getDataConceptList()) {
                            if (thisDataConcept.getDataConceptType().equals("dialog")) {
//                                System.out.println("****************OK*****************");
                                dialogList.add(thisDataConcept.getDataConceptName());
                                testResult = true;
                                break;
                            }
                        }
                        if (testResult) {
                            break;
                        }

                    }
                    if (testResult) {
                        break;
                    }

                }
                if (testResult) {
                    break;
                }

            }

        }
        if (!testResult) {
            test1Errors.add("No dialogs are currently referenced to this Need.");
        }
        return testResult;
    }

    public boolean isDialogInDesign(Need theNeed) {
        boolean testResult = false;

        // Is a dialog referenced directly?
        // Check through the design contents of the need for data concepts of a dialog type.
        for (Requirement thisRequirement : theNeed.getRequirementList()) {
            for (DataConcept thisConcept : thisRequirement.getDataConceptList()) {
                if (thisConcept.getDataConceptType().equals("dialog")) {
                    if (tmddDesign.getDesignMap().containsKey(thisConcept.getDataConceptName())) {
                        testResult = true;
                    } else {
                        test2Errors.add("No dialog named " + thisConcept.getDataConceptName() + " found in Design.");
                        testResult = false;
                    }
                }
            }
            if (testResult) {
                break;
            }
        }

        // If there isn't a direct trace to a dialog check to see if one is referenced through a predicate?
        if (!testResult) {
            for (Requirement thisRequirement : theNeed.getRequirementList()) {
                for (Predicate thisPredicate : predicatesList) {
                    if (thisRequirement.getConformance().contains(thisPredicate.getPredicateName())) {
//                                System.out.println("****************"+thisRequirement.getConformance()+" contains "+thisPredicate.getPredicateName()+"*****************");
                        for (DataConcept thisDataConcept : thisPredicate.getDataConceptList()) {
                            if (thisDataConcept.getDataConceptType().equals("dialog")) {
//                                System.out.println("****************OK*****************");
                                if (tmddDesign.getDesignMap().containsKey(thisDataConcept.getDataConceptName())) {
                                    testResult = true;
                                } else {
                                    test2Errors.add("No dialog named " + thisDataConcept.getDataConceptName() + " found in Design associated with Predicate" + thisPredicate.getPredicateName() + ".");
                                    testResult = false;
                                }
                            }
                        }
                        if (testResult) {
                            break;
                        }

                    }
                    if (testResult) {
                        break;
                    }

                }
                if (testResult) {
                    break;
                }

            }

        }

        if (!testResult) {
            test1Errors.add("No matches for dialogs related to this need were found within the design.");
        }
        return testResult;

    }

    public boolean areDialogRelatedMessagesInRTM(Need theNeed) {
        boolean messagesMatched = true;
        Map<String, Boolean> messageMap = new HashMap<String, Boolean>();
        for (Requirement theRequirement : theNeed.getRequirementList()) {
            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                if (theConcept.getDataConceptType().equals("message")) {
                    messageMap.put(theConcept.getDataConceptName(), false);
                }
            }

        }


        if (dialogList.size() > 0) {
            for (String theDialog : dialogList) {
                if (tmddDesign.getDesignMap().containsKey(theDialog)) {
                    DesignElement theElement = (DesignElement) tmddDesign.getDesignMap().get(theDialog);

                    for (DesignElement theMessage : theElement.getSubElements()) {

                        if (messageMap.containsKey(theMessage.getElementName())) {
                            messageMap.put(theMessage.getElementName(), true);
                        }

                    }


                } else {
                    test3Errors.add("Could not find an instance of dialog [" + theDialog + "] within the design.");
                }
            }
        }

        Iterator messagesIterator = messageMap.keySet().iterator();
        while (messagesIterator.hasNext()) {
            String messageName = (String) messagesIterator.next();
            if (!(Boolean) messageMap.get(messageName)) {
                test3Errors.add("Could not find Message " + messageName + " within the dialogs related to this Need.");
                messagesMatched = false;
            }
        }

        return messagesMatched;
    }

    public String errorListtoString(List<String> errorList) {
        StringBuffer errorDescription = new StringBuffer();

        if (errorList.size() > 0) {
            for (String thisError : errorList) {
                errorDescription = errorDescription.append(thisError).append("<LF>");
            }
            return errorDescription.toString();
        } else {
            return "";
        }
    }

    public final void initResults() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'DialogTest'");

        theDatabase.disconnectFromDatabase();
    }

    public void writeResults() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'DialogTest'");


        for (TestResult thisResult : testResults) {
            theDatabase.queryNoResult("INSERT INTO TMDDTestResultsTable ([NeedID],NeedText,"
                    + "TestType,[SubTestTitle],TestResult,ErrorDescription) VALUES ("
                    + "'" + thisResult.getNeedID() + "',"
                    + "'" + thisResult.getNeedText() + "',"
                    + "'" + thisResult.getTestType() + "',"
                    + "'" + thisResult.getSubTestTitle() + "',"
                    + "'" + thisResult.getTestResult() + "',"
                    + "'" + thisResult.getErrorDescription() + "')");
        }

        theDatabase.disconnectFromDatabase();
    }
}
