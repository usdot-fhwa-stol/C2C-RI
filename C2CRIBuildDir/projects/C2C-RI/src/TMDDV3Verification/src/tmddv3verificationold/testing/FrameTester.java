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
public class FrameTester {

    private List<Predicate> predicatesList;
    private DesignDetail tmddDesign;
    private List<TestResult> testResults = new ArrayList<TestResult>();
    private List<String> test1Errors = new ArrayList<String>();
    private List<String> test2Errors = new ArrayList<String>();
    private List<String> test3Errors = new ArrayList<String>();

    private FrameTester() {
    }

    public FrameTester(List<Predicate> thePredicatesList, DesignDetail theTmddDesign) {
        predicatesList = thePredicatesList;
        tmddDesign = theTmddDesign;
        initResults();
    }

    public void performMessageTests(Need theNeed) {
        testResults.clear();
        NeedHandler newHandler = new NeedHandler(theNeed);

        TestResult firstTestResult = new TestResult();
        firstTestResult.setNeedID(theNeed.getUnID());
        firstTestResult.setNeedText(theNeed.getUserNeed());
        firstTestResult.setTestType("FrameTest");
        firstTestResult.setSubTestTitle("1. Are the Frames referenced by this need in the Design?");
        test1Errors.clear();
        if (newHandler.getFramesCount() > 0) {
            Boolean firstResult = isFrameInDesign(newHandler);
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
        thirdTestResult.setTestType("FrameTest");
        thirdTestResult.setSubTestTitle("2. Can we match the RTM list of Frame Elements and the designs list of Frame elements for every Frame referenced by this need?");
        test3Errors.clear();
        if (newHandler.getFramesCount() > 0) {
            Boolean result = doesRTMListofElementsMatchDesign(newHandler);
            thirdTestResult.setTestResult(result.toString());
        } else {
            thirdTestResult.setTestResult("NA");
        }
        thirdTestResult.setErrorDescription(errorListtoString(test3Errors));
        testResults.add(thirdTestResult);

        writeResults();
    }

    private boolean isFrameInDesign(NeedHandler newHandler) {
        boolean testResult = true;
        List<String> needFrames = newHandler.getNeedRelatedFrames();
        for (String theFrame : needFrames) {
            boolean messageResult = tmddDesign.getDesignMap().containsKey(theFrame);
            if (!messageResult) {
                test1Errors.add("Frame " + theFrame + " not found in design.");
                testResult = false;
            }
        }
        return testResult;
    }

    private boolean doesRTMListofElementsMatchDesign(NeedHandler newHandler) {
        boolean test3Results = true;
        boolean sizeMismatchErrorReported = false;

        List<String> needFrames = newHandler.getNeedRelatedFrames();
        for (String theFrame : needFrames) {
            // Only process frames that are actually defined within this need.
            if (newHandler.isFrameDefinedWithinNeed(theFrame)) {
                try {
                    DesignElement frameDesignElement = (DesignElement) tmddDesign.getDesignMap().get(theFrame);
                    Integer elementCount = newHandler.getFrameConceptElementCount(theFrame);
                    System.out.println(newHandler.getNeedID() + "--   Frame " + theFrame + ":");
                    for (int ii = 0; ii < elementCount; ii++) {
                        if (ii == 0) {
                            System.out.println(" Frame  " + theFrame + " = " + frameDesignElement.getElementName() + "   "
                                    + newHandler.getFrameConceptElement(theFrame, ii).equals(frameDesignElement.getElementName()));
                            if (!newHandler.getFrameConceptElement(theFrame, ii).equals(frameDesignElement.getElementName())) {
                                test3Errors.add(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "  TargetFrame= " + theFrame + "   Element " + newHandler.getFrameConceptElement(theFrame, ii)
                                        + " <> " + frameDesignElement.getElementName() + " from design.");
                                test3Results = false;
                            }
                        } else {
                            DesignElement frameElement = (DesignElement) tmddDesign.getDesignMap().get(newHandler.getFrameConceptElement(theFrame, 0));
                            if (frameElement != null) {
                                if ((frameElement.getSubElements().size() > 0) && (ii - 1 < frameElement.getSubElements().size())) {
                                    System.out.println(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "--    Element " + newHandler.getFrameConceptElement(theFrame, ii)
                                            + " = " + frameElement.getSubElements().get(ii - 1).getElementName() + " "
                                            + newHandler.getFrameConceptElement(theFrame, ii).equals(frameElement.getSubElements().get(ii - 1).getElementName()));
                                    if (!newHandler.getFrameConceptElement(theFrame, ii).equals(frameElement.getSubElements().get(ii - 1).getElementName())) {
                                        test3Errors.add(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "  TargetFrame= " + theFrame + "    Element= " + ii + "  " + newHandler.getFrameConceptElement(theFrame, ii)
                                                + " <> " + frameElement.getSubElements().get(ii - 1).getElementName() + " from design.");
                                        test3Results = false;
                                    } else {
                                                     Requirement relatedRequirement = newHandler.getRequirement(newHandler.getFrameConceptElementRequirementID(theFrame, ii));
                                    if (((!relatedRequirement.getConformance().equals("M")) && (frameElement.getMinOccurs() != 0))
                                            || ((relatedRequirement.getConformance().equals("M")) && (frameElement.getMinOccurs() == 0))) {
                                        test3Errors.add(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "--  TargetFrame= " + theFrame + "    Element " + newHandler.getFrameConceptElement(theFrame, ii)
                                                + "**Conformance Setting Error**  Conformance setting \"" + relatedRequirement.getConformance() + "\" does not match design element " + frameElement.getSubElements().get(ii - 1).getElementName() + " setting of " + (frameElement.getMinOccurs() == 0 ? "\"O\"" : "\"M\""));
                                        test3Results = false;
                                        System.out.println("*** Optional Mismatch *** " + relatedRequirement.getConformance() + " <> " + (frameElement.getMinOccurs() == 0 ? "O" : "M"));
//                                    } else {
//                                        if (!relatedRequirement.getConformance().equals("M"))System.out.println("--> Optional Match "+ relatedRequirement.getConformance()+ " = "+(frameElement.getMinOccurs() == 0 ? "O" : "M"));
                                    }

                                        if (newHandler.getFrameDataConcept(theFrame, ii).getConceptReferenceName() == null) {
                                            newHandler.getFrameDataConcept(theFrame, ii).setConceptReferenceName(frameElement.getSubElements().get(ii - 1).getReferenceName());
                                            newHandler.getFrameDataConcept(theFrame, ii).setConceptPath(frameElement.getSubElements().get(ii - 1).getReferenceName());
                                        }
                                    }

                                } else {
                                    if (!sizeMismatchErrorReported) {
                                        String extraElements = "";
                                        for (int kk = ii; kk < elementCount; kk++) {
                                            extraElements = extraElements.concat("(" + (kk) + ") [" + newHandler.getFrameConceptElementRequirementID(theFrame, kk) + "] " + newHandler.getFrameConceptElement(theFrame, kk) + "; ");
                                        }
                                        test3Errors.add(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "--  TargetFrame= " + theFrame
                                                + "  **Count Mismatch** RTM has more elements than available in the Design  " + (elementCount - 1) + " versus " + frameElement.getSubElements().size()
                                                + " :: Extra Elements=> " + extraElements);
                                        test3Results = false;
                                        System.out.println(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "--  TargetFrame= " + theFrame + "  **Count Mismatch** RTM has more elements than available in the Design  " + (elementCount - 1) + " versus " + frameElement.getSubElements().size()
                                                + " :: Extra Elements=> " + extraElements);
                                        sizeMismatchErrorReported = true;
                                        //                                test3Errors.add(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "  ******* ERROR ******* More elements than available in the Design  " + (ii - 2) + " versus " + frameElement.getSubElements().size());
//                                test3Results = false;
//                                System.out.println(newHandler.getMessageConceptElementRequirementID(theMessage, ii) + "   ******* ERROR ******* More elements than available in the Design  " + (ii - 2) + " versus " + frameElement.getSubElements().size());
                                    }
                                    //                                   test3Errors.add(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "--  TargetFrame= "+theFrame+"  Element= "+ii+"  "+newHandler.getFrameConceptElement(theFrame, ii)+"  ******* ERROR ******* RTM has more elements than available in the Design  [" + (ii+1) + " of "+elementCount+ "] versus " + frameElement.getSubElements().size());
                                    //                                   test3Results = false;
                                    //                                   System.out.println(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "--  TargetFrame= "+theFrame+"  Element= "+ii+"  "+newHandler.getFrameConceptElement(theFrame, ii)+"  ******* ERROR ******* RTM has more elements than available in the Design  [" + (ii+1) + " of "+elementCount+ "] versus " + frameElement.getSubElements().size());
//                                    System.out.println(newHandler.getFrameConceptElementRequirementID(theFrame, ii) + "   ******* ERROR ******* More elements than available in the Design  [" + (ii - 1) +"of"+elementCount+ "] versus " + frameElement.getSubElements().size());
                                }

                            }

                        }


                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    test3Errors.add("  ******* ERROR ******* " + ex.getMessage());
                    test3Results = false;
                }
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

        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'FrameTest'");

        theDatabase.disconnectFromDatabase();
    }

    public void writeResults() {
        TMDDDatabase theDatabase = new TMDDDatabase();
        theDatabase.connectToDatabase();

//        theDatabase.queryNoResult("DELETE * FROM TMDDTestResultsTable WHERE TestType = 'FrameTest'");


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
