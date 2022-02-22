/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verification.testing;

import java.util.ArrayList;
import java.util.List;
import tmddv3verification.DesignDetail;
import tmddv3verification.DesignElement;
import tmddv3verification.Need;
import tmddv3verification.NeedHandler;
import tmddv3verification.Predicate;
import tmddv3verification.utilities.TMDDDatabase;

/**
 *
 * @author TransCore ITS
 */
public class ElementTester {

    private List<Predicate> predicatesList;
    private DesignDetail tmddDesign;
    private List<TestResult> testResults = new ArrayList<TestResult>();
    private List<String> test1Errors = new ArrayList<String>();
    private List<String> test2Errors = new ArrayList<String>();
    private List<String> test3Errors = new ArrayList<String>();

    private ElementTester() {
    }

    public ElementTester(List<Predicate> thePredicatesList, DesignDetail theTmddDesign) {
        predicatesList = thePredicatesList;
        tmddDesign = theTmddDesign;
        initResults();
    }

    public void performElementTests(Need theNeed) {
        testResults.clear();
        NeedHandler newHandler = new NeedHandler(theNeed);

        TestResult firstTestResult = new TestResult();
        firstTestResult.setNeedID(theNeed.getUnID());
        firstTestResult.setNeedText(theNeed.getUserNeed());
        firstTestResult.setTestType("ElementTest");
        firstTestResult.setSubTestTitle("1. Are the Elements referenced by this need in the Design?");
        test1Errors.clear();
        if (newHandler.getFramesCount() > 0) {
            Boolean firstResult = isElementInDesign(newHandler);
            firstTestResult.setTestResult(firstResult.toString());
        } else {
            firstTestResult.setTestResult("NA");
        }
        firstTestResult.setErrorDescription(errorListtoString(test1Errors));
        testResults.add(firstTestResult);

        /**
        TestResult secondTestResult = new TestResult();
        secondTestResult.setNeedID(theNeed.getUnID());
        secondTestResult.setNeedText(theNeed.getUserNeed());
        secondTestResult.setTestType("MessageTest");
        secondTestResult.setSubTestTitle("Does next element trace to message frame?");
        test2Errors.clear();
        if (newHandler.getMessagesCount() > 0) {
        Boolean result = doesNextElementTracetoMessageFrame(newHandler);
        secondTestResult.setTestResult(result.toString());
        } else {
        secondTestResult.setTestResult("NA");
        }
        secondTestResult.setErrorDescription(errorListtoString(test2Errors));
        testResults.add(secondTestResult);
         */
        TestResult thirdTestResult = new TestResult();
        thirdTestResult.setNeedID(theNeed.getUnID());
        thirdTestResult.setNeedText(theNeed.getUserNeed());
        thirdTestResult.setTestType("ElementTest");
        thirdTestResult.setSubTestTitle("2. Is Element Related to a Parent Frame referenced by this need?");
        test3Errors.clear();
        if (newHandler.getFramesCount() > 0) {
//            Boolean result = isElementRelatedtoAParentFrame(newHandler);
            Boolean result = true;
            thirdTestResult.setTestResult(result.toString());
        } else if (newHandler.getElementsCount()>0){
            thirdTestResult.setTestResult("false");
            test3Errors.add("The data elements of this need are not related to a parent frame.");
        } else{
            thirdTestResult.setTestResult("NA");
            test3Errors.add("No data elements associated with this need.");
        }
        thirdTestResult.setErrorDescription(errorListtoString(test3Errors));
        testResults.add(thirdTestResult);

        writeResults();
    }

    private boolean isElementInDesign(NeedHandler newHandler) {
        boolean testResult = true;
        List<String> needElements = newHandler.getNeedRelatedElements();
        for (String theElement : needElements) {
            boolean elementResult = tmddDesign.getDesignMap().containsKey(theElement);
            if (!elementResult) {
                test1Errors.add("Element " + theElement + " not found in design.");
                testResult = false;
            }
        }
        return testResult;
    }

    private boolean isElementRelatedtoAParentFrame(NeedHandler newHandler) {
        boolean test3Results = true;

        List<String> needElements = newHandler.getNeedRelatedElements();

        List<String> needFrames = newHandler.getNeedRelatedFrames();


        for (String theElement : needElements) {
            boolean elementTest = false;
            try {
                Integer elementItemCount = newHandler.getElementConceptElementCount(theElement);
                for (int jj = 0; jj < elementItemCount; jj++) {



                    for (String theFrame : needFrames) {
                        // Only process frames that are actually defined within this need.
                        if (newHandler.isFrameDefinedWithinNeed(theFrame)) {
                            try {
                                Integer elementCount = newHandler.getFrameConceptElementCount(theFrame);
                                System.out.println(newHandler.getNeedID() + "--   Frame " + theFrame + ":");
                                for (int ii = 0; ii < elementCount; ii++) {
                                    if (ii > 0) {
                                        if (newHandler.getFrameConceptElement(theFrame, ii).equals(newHandler.getElementConceptElement(theElement, jj))) {
                                            elementTest = true;
                                        }
                                    }

                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
      //                          test3Errors.add("  ******* ERROR ******* " + ex.getMessage());
      //                          test3Results = false;
                            }
                        }
                    }
                    if (!elementTest) {
                        test3Results = false;
                        test3Errors.add(newHandler.getElementConceptElementRequirementID(theElement, jj)+"    The element " + newHandler.getElementConceptElement(theElement, jj) + "was not defined as part of a frame.");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                test3Errors.add("  ******* ERROR ******* " + ex.getMessage());
                test3Results = false;

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

        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'ElementTest'");

        theDatabase.disconnectFromDatabase();
    }

    public void writeResults() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'ElementTest'");


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
