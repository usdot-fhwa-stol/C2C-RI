/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class TestResults captures the results from testing.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
@XmlRootElement(name = "testResults")
@XmlAccessorType(XmlAccessType.FIELD)
public class TestResults {

    /** The requirementsresult na. */
    private static String REQUIREMENTSRESULT_NA = "Requirement must be indirectly verified.";
    
    /** The info layer need results. */
    @XmlElementWrapper(name = "informationLevelStandard")
    @XmlElement(name = "need")
    private ArrayList<NeedResult> infoLayerNeedResults = new ArrayList<NeedResult>();
    
    /** The app layer need results. */
    @XmlElementWrapper(name = "applicationLevelStandard")
    @XmlElement(name = "need")
    private ArrayList<NeedResult> appLayerNeedResults = new ArrayList<NeedResult>();
    
    /** The current test case log id. */
    @XmlTransient
    private Integer currentTestCaseLogID;
    
    /** The current test step log id. */
    @XmlTransient
    private Integer currentTestStepLogID;

    @XmlElement(name="testTarget")
    private String testTarget;
    /**
     * Instantiates a new test results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestResults() {
    }

    /**
     * Instantiates a new test results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param config the config
     * @param tcResults the tc results
     */
    public TestResults(TestConfiguration config, TestCaseResults tcResults) {
        String centerMode = config.getTestMode().isExternalCenterOperation()?"EC":"OC";       
        testTarget = RIParameters.getInstance().getParameterValue(RIParameters.TEST_TARGET);
        infoLayerNeedResults = generateResults(config.getInfoLayerParams().getUserNeeds(),
               config.getInfoLayerParams().getApplicableTestCases(centerMode),
                config.getInfoLayerParams().getNrtm(),
                config.getInfoLayerParams().getTestCaseMatrix(),
                config.getInfoLayerParams().getApplicableTestCases(),
                tcResults,
                config.getTestMode().isExternalCenterOperation());

        appLayerNeedResults = generateResults(config.getAppLayerParams().getUserNeeds(),
                config.getAppLayerParams().getApplicableTestCases(centerMode),
                config.getAppLayerParams().getNrtm(),
                config.getAppLayerParams().getTestCaseMatrix(),
                config.getAppLayerParams().getApplicableTestCases(),
                tcResults,
                config.getTestMode().isExternalCenterOperation());

    }

    /**
     * Generate results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param userNeeds the user needs
     * @param requirements the requirements
     * @param otherRequirementsList the other requirements list
     * @param nrtm the nrtm
     * @param tcMatrix the tc matrix
     * @param testCases the test cases
     * @param tcResults the tc results
     * @param sutIsEC the sut is ec
     * @return the array list
     */
    private ArrayList<NeedResult> generateResults(UserNeeds userNeeds, List<TestCase> applicableTestCases, NRTM nrtm, TestCaseMatrix tcMatrix,
            List<TestCase> testCases, TestCaseResults tcResults, boolean sutIsEC) {
        ArrayList<NeedResult> theResults = new ArrayList<NeedResult>();
//        String testTarget = RIParameters.getInstance().getParameterValue(RIParameters.TEST_TARGET).contains("RI")? "SUT/RI":"SUT";
        List<String> applicableTestCaseNames = new ArrayList<String>();
        
        for (TestCase thisCase : applicableTestCases){
            applicableTestCaseNames.add(thisCase.getName());
        }

        for (Need thisNeed : userNeeds.needs) {
            NeedResult needResult = new NeedResult(thisNeed.getTitle(), thisNeed.getText(), thisNeed.getFlagValue() ? "Yes" : "No", "NA", thisNeed.isExtension());

            boolean isNeedTestable = false;

            for (Requirement thisRequirement : nrtm.getUserNeeds().getNeed(thisNeed.getTitle()).getProjectRequirements().requirements) {
                String otherRequirement = "";
                for (OtherRequirement thisOtherRequirement : thisRequirement.getOtherRequirements().otherRequirements) {
                    otherRequirement = thisOtherRequirement.getOtherRequirement();
                }

                /**
                String reqID = thisRequirement.getTitle();
                if (requirements.requirementsMap.containsKey(reqID)){
                Requirement thisRequirement = (Requirement) requirements.requirementsMap.get(reqID);
                String otherRequirement = "";
                for (OtherRequirement thisOtherRequirement : otherRequirementsList.otherRequirements){
                if (thisOtherRequirement.getReqID().equals(reqID)){
                otherRequirement = thisOtherRequirement.getOtherRequirement()+" : Set to "+thisOtherRequirement.getValue();
                break;
                }
                }
                 */
                RequirementResult thisReqResult = new RequirementResult(thisRequirement.getTitle(),
                        thisRequirement.getText(),
                        thisRequirement.getType(),
                        thisRequirement.getFlagValue() ? "Yes" : "No",
                        otherRequirement,
                        REQUIREMENTSRESULT_NA,
                        thisRequirement.isExtension());


                boolean isRequirementTestable = false;

                List<TestCaseMatrixEntry> tcMatrixEntryList = tcMatrix.getTestCases(thisNeed.getTitle(), thisRequirement.getTitle(), testTarget);

                for (TestCaseMatrixEntry theEntry : tcMatrixEntryList) {
                    ArrayList<TestCaseResult> testCaseResultList = tcResults.getResults(theEntry.getTcID());

                    boolean matchInOtherMode = false;
                    boolean theEntryMatchFound = false;
                    for (TestCase thisTestCase : testCases) {
                        if (thisTestCase.getName().equalsIgnoreCase(theEntry.getTcID())) {
                            theEntryMatchFound = true;
                            if ((thisTestCase.getType().equalsIgnoreCase("EC") && (sutIsEC))
                                    || ((thisTestCase.getType().equalsIgnoreCase("OC") && (!sutIsEC)))) {
                                isRequirementTestable = true;
                                isNeedTestable = true;
//                                break;

                            } else {
                                matchInOtherMode = true;
                            }

                        }
                    }

                    if (!isRequirementTestable) {
                        if ((theEntryMatchFound)&&(matchInOtherMode)){
                           thisReqResult.setResults("No Test Cases Applicable in this Test Mode");
 //                       } else {
 //                          thisReqResult.setResults("Requirement must be indirectly verified.");
                        } else if (theEntryMatchFound){
                           thisReqResult.setResults("Not Tested");
                        }
                    } else if (thisReqResult.getResults().equals(REQUIREMENTSRESULT_NA)) {
                        thisReqResult.setResults("Not Tested");
                    }

                    // If this test case was not performed then the test case results is empty.
                    if (testCaseResultList.isEmpty()){
                        if (matchInOtherMode) {
                           TestCaseResult notPerformedTCResult = new TestCaseResult(theEntry.getTcID(), 0,"No Test Cases Applicable in this Test Mode", "No Test Cases Applicable in this Test Mode");
                           thisReqResult.addTestCaseResult(notPerformedTCResult);
                        } else {
                           TestCaseResult notPerformedTCResult = new TestCaseResult(theEntry.getTcID(), 0,"Not Tested", "Not Tested");
                           if (applicableTestCaseNames.contains(theEntry.getTcID())){
                               thisReqResult.addTestCaseResult(notPerformedTCResult);
                           }

                        }
                    } else {

                       for (TestCaseResult thisResult : testCaseResultList) {
                          if (applicableTestCaseNames.contains(theEntry.getTcID())){
                                 thisReqResult.addTestCaseResult(thisResult);
                           }
                       }
                    }
                }
                needResult.addRequirementResult(thisReqResult);

            }


            theResults.add(needResult);
        }

        return theResults;
    }

    /**
     * Converts the Test Results into a form that can be written to and read from the Test Log.
     *
     * @return the string
     */
    public String to_LogFormat() {

        String logOutput = "";
        try {
            // =============================================================================================================
            // Setup JAXB
            // =============================================================================================================

            // Create a JAXB context passing in the class of the object we want to marshal/unmarshal
            final JAXBContext context = JAXBContext.newInstance(TestResults.class);

            // =============================================================================================================
            // Marshalling OBJECT to XML
            // =============================================================================================================

            // Create the marshaller, this is the nifty little thing that will actually transform the object into XML
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, new Boolean(true));
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

            // Create a stringWriter to hold the XML
            final StringWriter stringWriter = new StringWriter();


            // Marshal the javaObject and write the XML to the stringWriter
            marshaller.marshal(this, stringWriter);

            // Print out the contents of the stringWriter
            logOutput = stringWriter.toString();
//            logOutput.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>" , "");
            return logOutput;

        } catch (Exception ex) {
            ex.printStackTrace();
            return "TestResult Object Conversion Error - " + ex.getMessage();
        }
    }

    /**
     * The Class NeedResult.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    static class NeedResult {

        /** The un id. */
        @XmlAttribute
        private String unID;
        
        /** The user need. */
        @XmlAttribute
        private String userNeed;
        
        /** The un selected. */
        @XmlAttribute
        private String unSelected;
        
        /** The results. */
        @XmlAttribute
        private String results;
        
        /** The extension. */
        @XmlAttribute
        private boolean extension;
        
        /** The requirement results. */
        @XmlElement(name = "requirement")
        private ArrayList<RequirementResult> requirementResults = new ArrayList<RequirementResult>();

        /**
         * Instantiates a new need result.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        private NeedResult() {
        }

        /**
         * Instantiates a new need result.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param unID the un id
         * @param userNeed the user need
         * @param unSelected the un selected
         * @param results the results
         * @param extension the extension
         */
        public NeedResult(String unID, String userNeed, String unSelected, String results, boolean extension) {
            this.unID = unID;
            this.userNeed = userNeed;
            this.unSelected = unSelected;
            this.results = results;
            this.extension = extension;
        }

        /**
         * Adds the requirement result.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param reqResult the req result
         */
        public void addRequirementResult(RequirementResult reqResult) {
            if (this.unSelected.equals("Yes")) {
                if (reqResult.results.equalsIgnoreCase("Failed")) {
                    this.results = "Failed";
                } else if (reqResult.results.equals("Requirement must be indirectly verified.")&& !this.results.equalsIgnoreCase("Failed")){
                    this.results = "Need must be indirectly verified.";
                } else if (!reqResult.results.equals(REQUIREMENTSRESULT_NA) && !reqResult.results.equals("Not Tested") && !this.results.equalsIgnoreCase("Failed")) {
                    this.results = "Passed";
                }
            }
            requirementResults.add(reqResult);

            int requirementsPassed = 0;
            int requirementsFailed = 0;
            int requirementsNA = 0;
            int requirementsNotTested = 0;
            int requirementsWithNoTestCase = 0;
            int requirementsIncomplete = 0;
            for (RequirementResult thisReqResult : requirementResults){
                if (thisReqResult.supported.equalsIgnoreCase("No")){
                  // Do nothing since this requirement is not included in the project specification ....  
                } else if (thisReqResult.getResults().equalsIgnoreCase("Passed")){
                    requirementsPassed++;
                } else if (thisReqResult.getResults().equalsIgnoreCase("Failed")){
                    requirementsFailed++;
                } else if (thisReqResult.getResults().equalsIgnoreCase(REQUIREMENTSRESULT_NA)||thisReqResult.getResults().equalsIgnoreCase("Requirement must be indirectly verified.")){
                    requirementsNA++;
                } else if (thisReqResult.getResults().equalsIgnoreCase("Not Tested")){
                    requirementsNotTested++;
                } else if (thisReqResult.getResults().equalsIgnoreCase("No Test Cases Applicable in this Test Mode")){
                    requirementsWithNoTestCase++;
                } else if (thisReqResult.getResults().equalsIgnoreCase("Not Fully Tested")){
                    requirementsIncomplete++;
                }
            }

            if (requirementsPassed > 0){
                if ((requirementsFailed ==0)&&(requirementsNotTested==0)&&(requirementsIncomplete==0)){
                    this.results = "Passed";
                } else if ((requirementsFailed ==0)&&((requirementsNotTested>0)||(requirementsIncomplete>0))){
                    this.results = "Partial Pass";
                } else {
                    this.results = "Failed";
                }
            } else if (requirementsFailed > 0){
                this.results = "Failed";
            } else if (requirementsIncomplete > 0){
                this.results = "Not Fully Tested.";
            } else if (requirementsNotTested>0){
                this.results = "Not Tested";
            } else if (requirementsNA > 0){
                this.results = "Need must be indirectly verified.";
            } else if (requirementsWithNoTestCase >0){
                this.results = "No Test Cases Applicable in this Test Mode";
            } else {
                this.results = "UNDEFINED";
            }
        }

        /**
         * Sets the results.
         *
         * @param results the new results
         */
        public void setResults(String results) {
            this.results = results;
        }
    }

    /**
     * The Class RequirementResult.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    static class RequirementResult {

        /** The req id. */
        @XmlAttribute
        private String reqID;
        
        /** The req text. */
        @XmlAttribute
        private String reqText;
        
        /** The conformance. */
        @XmlAttribute
        private String conformance;
        
        /** The supported. */
        @XmlAttribute
        private String supported;
        
        /** The other req. */
        @XmlAttribute
        private String otherReq;
        
        /** The results. */
        @XmlAttribute
        private String results;
        
        /** The extension. */
        @XmlAttribute
        private boolean extension;
        
        /** The tc results. */
        @XmlElement(name = "testCase")
        private ArrayList<TestCaseResult> tcResults = new ArrayList<TestCaseResult>();

        /**
         * Instantiates a new requirement result.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         */
        private RequirementResult() {
        }

        /**
         * Instantiates a new requirement result.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param reqID the req id
         * @param reqText the req text
         * @param conformance the conformance
         * @param supported the supported
         * @param otherReq the other req
         * @param results the results
         * @param extension the extension
         */
        public RequirementResult(String reqID, String reqText, String conformance, String supported, String otherReq, String results, boolean extension) {
            this.reqID = reqID;
            this.reqText = reqText;
            this.conformance = conformance;
            this.supported = supported;
            this.otherReq = otherReq;
            this.results = results;
            this.extension = extension;
        }

        /**
         * Adds the test case result.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param tcResult the tc result
         */
        public void addTestCaseResult(TestCaseResult tcResult) {
            if (this.supported.equals("Yes")) {
//                if ((tcResult.getResult().equalsIgnoreCase("Passed")) && ((this.results.equals("NA")) || (this.results.equals("Not Tested"))) && !this.results.equalsIgnoreCase("Failed")) {
//                    this.results = "Passed";
//                } else if (tcResult.getResult().equalsIgnoreCase("Failed")) {
//                    this.results = "Failed";
//                } else if (this.results.equalsIgnoreCase("No Test Cases Applicable in this Test Mode")){
////                    this.results = "Not Fully Tested";
//                } else {
//                    this.results = "Not Fully Tested";
//                }
                tcResults.add(tcResult);

                int numPassed = 0;
                int numFailed = 0;
                int notPerformed = 0;
                for (TestCaseResult thisTCResult : tcResults){
                    if (thisTCResult.getResult().equalsIgnoreCase("Passed")){
                        numPassed++;
                    } else if (thisTCResult.getResult().equalsIgnoreCase("Failed")){
                        numFailed++;
                    } else if (!thisTCResult.getResult().equalsIgnoreCase("No Test Cases Applicable in this Test Mode")){
                        notPerformed++;
                    }
                }

                if ((numPassed > 0)&&(numFailed==0)&&(notPerformed==0)){
                    this.results = "Passed";
                } else if (numFailed >0){
                    this.results = "Failed";
                } else if ((numPassed>0)&&(notPerformed>0)){
                    this.results = "Not Fully Tested";
                } else if ((notPerformed>0)){
                        this.results = "Not Tested";
                } else {
                    this.results = "No Test Cases Applicable in this Test Mode";
                }

            }
        }

        /**
         * Gets the results.
         *
         * @return the results
         */
        public String getResults() {
            return results;
        }

        /**
         * Sets the results.
         *
         * @param results the new results
         */
        @XmlTransient
        public void setResults(String results) {
            this.results = results;
        }
    }
}
