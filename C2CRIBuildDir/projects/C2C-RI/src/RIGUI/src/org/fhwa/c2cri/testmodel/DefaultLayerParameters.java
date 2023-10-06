/**
 * 
 */
package org.fhwa.c2cri.testmodel;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import org.fhwa.c2cri.utilities.OptionGroupChecker;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * This class captures the layer specific parameters (information or application) that must be defined for a test.  The parameters include a requirements-traceability-matrix and a properties map which are both pulled from the selected test suite.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class DefaultLayerParameters implements Serializable {

    /** The nrtm. */
 @XmlElement
    private NRTM nrtm;
    
    /** The predicates. */
    @XmlElement
    private Predicates predicates;
    
    /** The test cases. */
    private TestCases testCases;
    
    /** The test case matrix. */
    @XmlElement
    private TestCaseMatrix testCaseMatrix;

    static final long serialVersionUID = -873768927586528699L;
//    static final long serialVersionUID = 2490116678409943980L;
    /**
     * Instantiates a new default layer parameters.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public DefaultLayerParameters() {
        // Created for JAXB XML serialization
    }

    /**
     * Constructor for creation of InfoLayerParameters object.
     *
     * @param testSuiteSelected the test suite selected
     * @throws Exception the exception
     */
    public DefaultLayerParameters(String testSuiteSelected) throws Exception {
        boolean predefinedTestSuite = TestSuites.getInstance().isPredefined(testSuiteSelected);
        String baselineTestSuite = TestSuites.getInstance().getBaselineTestSuite(testSuiteSelected);


        try {
            if (!predefinedTestSuite) {

                           nrtm = new NRTM(TestSuites.getInstance().getTestSuiteNRTMURL(baselineTestSuite),
                                   TestSuites.getInstance().getTestSuiteNRTMURL(testSuiteSelected));

            } else {
                           nrtm = new NRTM(TestSuites.getInstance().getTestSuiteNRTMURL(testSuiteSelected));

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error processing NRTM CSV: " + e.getMessage());
        }

        try {
            if (!predefinedTestSuite) {
                predicates = new Predicates(TestSuites.getInstance().getTestSuitePredicatesURL(baselineTestSuite),
                        TestSuites.getInstance().getTestSuitePredicatesURL(testSuiteSelected));

            } else {
                predicates = new Predicates(TestSuites.getInstance().getTestSuitePredicatesURL(testSuiteSelected));

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error processing Predicates CSV: " + e.getMessage());
        }
        try {
            if (!predefinedTestSuite) {
                testCases = new TestCases(baselineTestSuite, testSuiteSelected);

            } else {
                testCases = new TestCases(testSuiteSelected);

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error processing TestCases CSV: " + e.getMessage());
        }

        try {
            if (!predefinedTestSuite) {
                testCaseMatrix = new TestCaseMatrix(TestSuites.getInstance().getTestSuiteTestCaseMatrixPathURL(baselineTestSuite),
                        TestSuites.getInstance().getTestSuiteTestCaseMatrixPathURL(testSuiteSelected));

            } else {
                testCaseMatrix = new TestCaseMatrix(TestSuites.getInstance().getTestSuiteTestCaseMatrixPathURL(testSuiteSelected));

            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error processing TestCaseMatrix CSV: " + e.getMessage());
        }

    }

    /**
     * returns the current information layer RTM.
     *
     * @return the nrtm
     */
    public NRTM getNrtm() {
        return nrtm;
    }

    /**
     * returns the current information layer Other Requirements.
     *
     * @return the other requirements
     */
    public OtherRequirements getOtherRequirements() {
		OtherRequirements oReturn = null;
		
        for (Need theNeed: nrtm.getUserNeeds().needs)
		{
            if (oReturn == null && !theNeed.getProjectRequirements().requirements.isEmpty())
			{
				oReturn = theNeed.getProjectRequirements().requirements.get(0).getOtherRequirements();
				break;
			}
        }
        return oReturn;
    }

    /**
     * returns the current information layer Project Requirements.
     *
     * @return the project requirements
     */
    public ProjectRequirements getProjectRequirements() {
        for (Need theNeed: nrtm.getUserNeeds().needs){
            theNeed.getProjectRequirements();
        }
        return null;
    }

    /**
     * returns the current information layer User Needs.
     *
     * @return the user needs
     */
    public UserNeeds getUserNeeds() {
        return nrtm.getUserNeeds();
    }

    /**
     * returns the current information layer predicates.
     *
     * @return the predicates
     */
    public Predicates getPredicates() {
        return predicates;
    }

    /**
     * returns the current information layer testCases.
     *
     * @return the test cases
     */
    public TestCases getTestCases() {
        return testCases;
    }

    /**
     * Gets the applicable test cases.
     *
     * @return the applicable test cases
     */
    public List<TestCase> getApplicableTestCases() {
        List<TestCase> tempList = new ArrayList<TestCase>();
//        String testTarget = RIParameters.getInstance().getParameterValue(RIParameters.TEST_TARGET).contains("RI")? "SUT/RI":"SUT";
        String testTarget = RIParameters.getInstance().getParameterValue(RIParameters.TEST_TARGET);

        for (Need thisNeed : nrtm.getUserNeeds().needs) {
            if (thisNeed.getFlagValue()) {  // This need is selected
                List<String> relatedRequirementsList = nrtm.getRequirementsList(thisNeed.getTitle());

                for (String thisRequirementID : relatedRequirementsList) {
                    if (nrtm.getUserNeeds().getNeed(thisNeed.getTitle()).getProjectRequirements().lh_requirementsMap.containsKey(thisRequirementID)) {
                        Requirement thisRequirement = (Requirement) nrtm.getUserNeeds().getNeed(thisNeed.getTitle()).getProjectRequirements().lh_requirementsMap.get(thisRequirementID);
                        if (thisRequirement.getFlagValue()) {
                            List<TestCaseMatrixEntry> testCaseMatrixList =
                                    testCaseMatrix.getTestCases(thisNeed.getTitle(), thisRequirementID, testTarget);
                            for (TestCaseMatrixEntry matrixEntry : testCaseMatrixList) {
                                if (testCases.lh_testCasesMap.containsKey(matrixEntry.getTcID())&& (isPredicateSatisfied(matrixEntry.getPrecondition(),thisNeed.getTitle()))) {
                                    TestCase theTestCase = (TestCase) testCases.lh_testCasesMap.get(matrixEntry.getTcID());
                                    if (!tempList.contains(theTestCase)) {
                                        tempList.add(theTestCase);
                                    }
                                }
                            }

                        }

                    }



                }

            }
        }

        return tempList;
    }

    /**
     * Gets the applicable test cases.
     *
     * @param sutCenterMode the sut center mode
     * @return the applicable test cases
     */
    public List<TestCase> getApplicableTestCases(String sutCenterMode) {
        List<TestCase> tempList = new ArrayList<TestCase>();

        String testTarget = RIParameters.getInstance().getParameterValue(RIParameters.TEST_TARGET);
        for (Need thisNeed : nrtm.getUserNeeds().needs) {
            if (thisNeed.getFlagValue()) {  // This need is selected
                List<String> relatedRequirementsList = nrtm.getRequirementsList(thisNeed.getTitle());

                for (String thisRequirementID : relatedRequirementsList) {
                    if (nrtm.getUserNeeds().getNeed(thisNeed.getTitle()).getProjectRequirements().lh_requirementsMap.containsKey(thisRequirementID)) {
                        Requirement thisRequirement = (Requirement) nrtm.getUserNeeds().getNeed(thisNeed.getTitle()).getProjectRequirements().lh_requirementsMap.get(thisRequirementID);
                        if (thisRequirement.getFlagValue()) {
                            List<TestCaseMatrixEntry> testCaseMatrixList =
                                    testCaseMatrix.getTestCases(thisNeed.getTitle(), thisRequirementID, testTarget);
                            for (TestCaseMatrixEntry matrixEntry : testCaseMatrixList) {
                                if (testCases.lh_testCasesMap.containsKey(matrixEntry.getTcID()) && (isPredicateSatisfied(matrixEntry.getPrecondition(),thisNeed.getTitle()))) {
                                    TestCase theTestCase = (TestCase) testCases.lh_testCasesMap.get(matrixEntry.getTcID());
                                    if (!tempList.contains(theTestCase) && theTestCase.getType().equals(sutCenterMode)) {
                                        System.out.println("getApplicableTestCases Added Test Case: "+theTestCase.getName()+ " with Precondition"+matrixEntry.getPrecondition());
                                        tempList.add(theTestCase);
                                    }
                                }
                            }

                        }

                    }



                }

            }
        }

        return tempList;
    }

    /**
     * returns the current information layer properties.
     *
     * @return the properties
     */
    public HashMap getProperties() {
        HashMap layerMap = new HashMap();
        for (Need userNeed : nrtm.getUserNeeds().needs) {
            if (userNeed != null) {
                layerMap.put(userNeed.getFlagName(), userNeed.getFlagValue());
				for (Requirement theRequirement : userNeed.getProjectRequirements().requirements) {
					if (theRequirement != null) {
						layerMap.put(theRequirement.getFlagName(), theRequirement.getFlagValue());

						for (OtherRequirement theOtherRequirement : theRequirement.getOtherRequirements().otherRequirements) {
							if (theOtherRequirement != null) {
								layerMap.put(theOtherRequirement.getValueName(), theOtherRequirement.getValue());
							}
						}
					}
				}
			}
        }

        return layerMap;
    }

    /**
     * returns the current information layer RTM.
     *
     * @return the rtm
     */
    public ArrayList getRtm() {
        return new ArrayList();
    }

    /**
     * returns the current information layer properties to what is provided.
     *
     * @param rtm the new rtm
     */
//    public void setInfoProperties(HashMap appProperties) {
//    }
    /** sets the current information layer RTM
     * @param rtm
     */
    public void setRtm(ArrayList rtm) {
		// original implementation was empty
    }

    /**
     * verifies that the parameters that are to be set are valid.
     *
     * @param layerType the layer type
     * @return the array list
     */
    public ArrayList<String> verifyLayerParameters(String layerType) {
        ArrayList<String> errorList = new ArrayList<String>();
        ArrayList<String> unDefinedPredicates = new ArrayList<>();
        ArrayList<String> unresolvableRequirements = new ArrayList<>();

        boolean userNeedSelected = false;
        boolean projectRequirementSelected = false;

        for (Need userNeed : nrtm.getUserNeeds().needs) {
            System.out.println(layerType + " Parameters -- Inspecting Need: " + userNeed.getTitle() + " - " + userNeed.getFlagValue());
            // We only care about needs that are selected - either true flag or mandatory
            if ((userNeed.getFlagValue()) || (userNeed.getType().startsWith("M"))) {
                userNeedSelected = true;
                ArrayList<String> reqGroup = nrtm.getRequirementsList(userNeed.getTitle());

                //Create a list of the unique conformance strings within the requirement group
                HashMap<String, String> optGroup = new HashMap<String, String>();
                for (String thisRequirementID : reqGroup) {
                    Requirement thisRequirement = null;
                    if (nrtm.getUserNeeds().getNeed(userNeed.getTitle()).getProjectRequirements().lh_requirementsMap.containsKey(thisRequirementID)) {
                        thisRequirement = (Requirement) nrtm.getUserNeeds().getNeed(userNeed.getTitle()).getProjectRequirements().lh_requirementsMap.get(thisRequirementID);
                        // Some conformance strings include multiple conformance status conditions (seperated by a ";" - break them up and handle seperately.
                    } else {

                        //                       System.out.println(" Problem encountered with "+ thisRequirement.getTitle());
                        errorList.add("*** Unable to verify requirement " + thisRequirementID + " associated with Need " + userNeed.getTitle());
                        break;  // Go to the next requirement
                    }

                    String conformanceString = thisRequirement.getType();
                    if (conformanceString.equalsIgnoreCase("M") ||conformanceString.equalsIgnoreCase("Mandatory")|| thisRequirement.getFlagValue()) {
                        projectRequirementSelected = true;
                    }

                    String[] conformanceParts = conformanceString.split(";");
                    for (int ii = 0; ii < conformanceParts.length; ii++) {
                        // If it's not the standard Mandatory or Optional statement then add to group list
                        if ((!conformanceString.equalsIgnoreCase("M"))
                                && (!conformanceString.equalsIgnoreCase("O"))
                                && (!conformanceString.equalsIgnoreCase("Mandatory"))) {
                            conformanceParts[ii] = OptionGroupChecker.prepareOptionGroupString(conformanceParts[ii]);
                            boolean skipPredicate = false;
                    if (conformanceParts[ii].contains(":")) {
                        // Confirm that the predicate is known
                        String predicate = conformanceParts[ii].substring(0, conformanceParts[ii].indexOf(":"));
                        // See if the associated requirement is selected
                        String predicateReqId = (String) predicates.getPredicatesMap().get(predicate);
                        if (predicateReqId==null){
                            skipPredicate = true;
                            if (!unDefinedPredicates.contains(predicate))unDefinedPredicates.add(predicate);                            
                        } else {
                            if (!isPredicateSatisfied(predicate,userNeed.getTitle())){
                                if (thisRequirement.getFlagValue()){
                                    errorList.add("Need " + userNeed.getTitle() + " Requirement " + thisRequirement.getTitle() + " is selected but predicate '" + predicate + "' is not satisfied for clause " + conformanceParts[ii]);                                                                                 
                                }
                                skipPredicate = true;
                            }
                        }
                    }
                        if ((!skipPredicate)&&(!optGroup.containsKey(conformanceParts[ii]))) {
                                optGroup.put(conformanceParts[ii], "");
                            }
                        }
                    }
                }


                // Iterate through the OptionGroup Map to verify whether each item is satisfied within the requirement Group
                Collection c = optGroup.keySet();
                Iterator itr = c.iterator();

                while (itr.hasNext()) {
                    String thisKey = (String) itr.next();
                    thisKey = thisKey.trim();
                    System.out.println(" Working on selected user need" + userNeed.getTitle() + " with Conformance Option: " + thisKey + " size=" + thisKey.length());
                    int thisOptionGroupSelectedCount = 0;
                    int thisOptionGroupMinCount = 0;
                    int thisOptionGroupMaxCount = 0;
                    boolean predicatePrecondition = false;
 
                    try {
                        // min is a number between characters "(" and "..", or between "(" and ")"
                        // max is a number after ".." and before ")".  If there is no ".." max is equal to min
                    } catch (Exception e) {
                    }


                    // Check for Predicates
                    if (thisKey.contains(":")) {
                        // Confirm that the predicate is known
                        String predicate = thisKey.substring(0, thisKey.indexOf(":"));
                        System.out.println(" Found Predicate " + predicate + " ?  " + predicates.getPredicatesMap().containsKey(predicate));
                        // See if the associated requirement is selected
                        String predicateReqId = (String) predicates.getPredicatesMap().get(predicate);
                        predicatePrecondition =false;
                        for (Need thisNeed : nrtm.getUserNeeds().needs){
                            if (thisNeed.getFlagValue()){
                            for (Requirement thisRequirement : thisNeed.getProjectRequirements().requirements){
                                if ((thisRequirement.getFlagValue())){
                                    if (thisRequirement.getTitle().equals(predicateReqId)) {
                                       predicatePrecondition = true;
                                       break;  // exit requirements for loop ...
                                    } else if ((predicateReqId!=null) && predicateReqId.contains(" OR ")){
                                        String[] predicateParts = predicateReqId.split("OR");
                                        boolean matchFound = false;
                                        for (int ii =0; ii<predicateParts.length; ii++){
                                           if (thisRequirement.getTitle().equals(predicateParts[ii].trim())){
                                               matchFound = true;
                                           }
                                        }
                                        if (matchFound){
                                            predicatePrecondition = true;
                                            break;  // exit requirements for loop ...
                                        }
                                    } else if (predicateReqId==null){
                                        if (!unresolvableRequirements.contains(thisRequirement.getTitle()))unresolvableRequirements.add(thisRequirement.getTitle());
                                        break;
                                    }
                                }
                            }
                            if (predicatePrecondition)break;  // exit needs for loop ...
                            }
                        }
                    }
                    
                    thisOptionGroupMinCount = OptionGroupChecker.getMin(thisKey);
                    thisOptionGroupMaxCount = OptionGroupChecker.getMax(thisKey);

                    // Check for standard option Group Ranges and Stuff
                    for (String thisRequirementID : reqGroup) {
                        Requirement thisRequirement = (Requirement) nrtm.getUserNeeds().getNeed(userNeed.getTitle()).getProjectRequirements().lh_requirementsMap.get(thisRequirementID);
                        String conformanceString = thisRequirement.getType();
                        if (OptionGroupChecker.prepareOptionGroupString(conformanceString).contains(thisKey)) {
                            // Make sure a mandatory conformance requirement is set
                            if (thisKey.contains(":M")) {
                                if ((!thisRequirement.getFlagValue()) && predicatePrecondition) {
                                    errorList.add("Need " + userNeed.getTitle() + " Requirement " + thisRequirement.getTitle() + " must be selected due to clause " + thisKey);
                                }
                            } else if ((thisKey.endsWith(":O")) && (!predicatePrecondition)) {
                                if (thisRequirement.getFlagValue()) {
                                    String predicate = thisKey.substring(0, thisKey.indexOf(":"));
                                    errorList.add("Need " + userNeed.getTitle() + " Requirement " + thisRequirement.getTitle() + " is selected but predicate '" + predicate + "' is not satisfied for clause " + thisKey);
                                }
                            } else {
                                // Only Option group phrases including (X) or (X..Y) remain
                                if (thisRequirement.getFlagValue()) {
                                    //if this requirement satisfies the condition increment the counter
                                    thisOptionGroupSelectedCount++;
                                    if ((thisOptionGroupMaxCount > 0) && (thisOptionGroupSelectedCount > thisOptionGroupMaxCount)) {
                                        // A Max Condition has been exceeded.  Provide Error Notification
                                        errorList.add("Need " + userNeed.getTitle() + " Requirement " + thisRequirement.getTitle() + " is selected and exceeds range in clause " + thisKey);
                                    }
                                    if (thisKey.contains(":")){
                                        String predicate = thisKey.substring(0, thisKey.indexOf(":"));
                                        if (!isPredicateSatisfied(predicate,userNeed.getTitle()))
                                            errorList.add("Need " + userNeed.getTitle() + " Requirement " + thisRequirement.getTitle() + " is selected but predicate '" + predicate + "' is not satisfied for clause " + thisKey);                                             
                                    }
                                }
                        }
                        

                        System.out.println(" Detailed OptionGroup Test:   Checking Requirement " + thisRequirement.getTitle() + " " + thisRequirement.getFlagValue() + " with Conformance Phrase " + conformanceString + " -- TODO");
                        //if this requirement satisfies the condition increment the counter

                        }

                    }

                    if (((thisOptionGroupSelectedCount < thisOptionGroupMinCount) && (!OptionGroupChecker.isMaxOrZeroOption(thisKey)))
                            || ((OptionGroupChecker.isMaxOrZeroOption(thisKey)) && (thisOptionGroupSelectedCount > 0) && (thisOptionGroupSelectedCount < thisOptionGroupMaxCount))) {
//                            || ((OptionGroupChecker.isMaxOrZeroOption(thisKey)) && (thisOptionGroupSelectedCount > 0) && (thisOptionGroupSelectedCount < thisOptionGroupMaxCount))) {
                        // Make sure not to report minimum selection error for a predicate that's not selected
                        // A Max Condition has been exceeded.  Provide Error Notification
                        if ((!predicatePrecondition) && (!thisKey.contains(":"))) {
                            errorList.add("Need " + userNeed.getTitle() + " is selected and insufficient requirements are selected to satisfy clause " + thisKey);
                        } else if (predicatePrecondition) {
                            errorList.add("Need " + userNeed.getTitle() + " is selected and insufficient requirements are selected to satisfy clause " + thisKey);                            
                        }
                    }

                }
            }
        }
        if (userNeedSelected && !projectRequirementSelected) {
            errorList.add(layerType + ": No requirements selected for identified need(s)");
        }
        if (!userNeedSelected && !projectRequirementSelected) {
            errorList.add(layerType + ": No needs or requirements were selected");
        }
        for (String line : errorList) {
            System.out.println(line);
        }
        
        if (unDefinedPredicates.size()>0){
            String undefinedList = "";
            for (String thisPredicate : unDefinedPredicates){
                undefinedList = undefinedList.concat(thisPredicate+"\n");
            }
            javax.swing.JOptionPane.showMessageDialog(null, "The following predicates are used within the NRTM but not defined within the Test Suite: \n"+undefinedList);                   
        }
        return errorList;
    }




    /**
 * Checks if is predicate satisfied.
 * 
 * Pre-Conditions: N/A
 * Post-Conditions: N/A
 *
 * @param predicate the predicate
 * @param needID the need id
 * @return true, if is predicate satisfied
 */
public boolean isPredicateSatisfied(String predicate, String needID) {
        boolean predicatePrecondition = false;


        if (predicate.isEmpty()) {
            return true;
        }

        System.out.println(" isPredicateSatisfied: Checking Predicate " + predicate + " ?  " + predicates.getPredicatesMap().containsKey(predicate));

        // If predicate is "Global", check through all the needs.  Otherwise check locally to the need provided.                  
        HashMap<String, Predicate> tmpMap = predicates.getPredicatesMapNew();
        if (tmpMap.containsKey(predicate)) {
            Predicate thisPredicate = tmpMap.get(predicate);
            if (thisPredicate.isGlobalPredicate()) {
                Need needReferenced = nrtm.getUserNeeds().getNeed(thisPredicate.getParentNeed());
                if (needReferenced.getFlagValue()) {
                    for (Requirement thisRequirement : needReferenced.getProjectRequirements().requirements) {
                        if (thisRequirement.getFlagValue()) {
                            if (thisRequirement.getTitle().equals(thisPredicate.getSection())) {
                                predicatePrecondition = true;
                                break;  // exit requirements for loop ...
                            } else if (thisPredicate.getSection().contains(" OR ")) {
                                String[] predicateParts = thisPredicate.getSection().split("OR");
                                boolean matchFound = false;
                                for (int ii = 0; ii < predicateParts.length; ii++) {
                                    if (thisRequirement.getTitle().equals(predicateParts[ii].trim())) {
                                        matchFound = true;
                                    }
                                }
                                if (matchFound) {
                                    return true;  // exit requirements for loop ...
                                }
                            }
                        }
                    }
                }
            } else {   // The predicate is local to the given need.
                Need needReferenced = nrtm.getUserNeeds().getNeed(needID);
                for (Requirement thisRequirement : needReferenced.getProjectRequirements().requirements) {
                    if ((thisRequirement.getFlagValue())) {
                        if (thisRequirement.getTitle().equals(thisPredicate.getSection())) {
                            predicatePrecondition = true;
                            break;  // exit requirements for loop ...
                        } else if (thisPredicate.getSection().contains(" OR ")) {
                            String[] predicateParts = thisPredicate.getSection().split("OR");
                            boolean matchFound = false;
                            for (int ii = 0; ii < predicateParts.length; ii++) {
                                if (thisRequirement.getTitle().equals(predicateParts[ii].trim())) {
                                    matchFound = true;
                                }
                            }
                            if (matchFound) {
                                predicatePrecondition = true;
                                break;  // exit requirements for loop ...
                            }
                        }
                    }
                }
            }
        }
        return predicatePrecondition;
    }
    
    
    /**
     * Sets the test cases.
     *
     * @param testCases the new test cases
     */
    public void setTestCases(TestCases testCases) {
        this.testCases = testCases;
    }


    /**
     * Gets the test case matrix.
     *
     * @return the test case matrix
     */
    public TestCaseMatrix getTestCaseMatrix() {
        return testCaseMatrix;
    }
}
