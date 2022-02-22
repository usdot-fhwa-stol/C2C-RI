/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Class TestCaseResults captures the results of a single test case.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCaseResults {

    /** The test case map. */
    private  HashMap<String, ArrayList<TestCaseResult>> testCaseMap = new HashMap<String, ArrayList<TestCaseResult>>();
    
    /** The full results list. */
    private  ArrayList<TestCaseResult> fullResultsList = new ArrayList<TestCaseResult>();
    
    /** The test case results listeners. */
    private  ArrayList<TestCaseResultsListener> testCaseResultsListeners = new ArrayList<TestCaseResultsListener>();

    /**
     * Adds the result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testCaseID the test case id
     * @param timeStamp the time stamp
     * @param errorDescription the error description
     * @param result the result
     */
    public void addResult(String testCaseID, long timeStamp, String errorDescription, String result, String description) {
        TestCaseResult theResult = new TestCaseResult(testCaseID, timeStamp, errorDescription, result, description);

        if (testCaseMap.containsKey(testCaseID)) {
            ArrayList testCaseList = testCaseMap.get(testCaseID);
            testCaseList.add(theResult);
            fullResultsList.add(theResult);
            for (TestCaseResultsListener thisListener : testCaseResultsListeners) {
                thisListener.testCaseUpdate();
                thisListener.testStepUpdate();
            }
        } else {
            ArrayList<TestCaseResult> testCaseList = new ArrayList<TestCaseResult>();
            testCaseList.add(theResult);
            testCaseMap.put(testCaseID, testCaseList);
            fullResultsList.add(theResult);
            for (TestCaseResultsListener thisListener : testCaseResultsListeners) {
                thisListener.testCaseUpdate();
                thisListener.testStepUpdate();
            }
        }
        theResult.setTestCaseRunID(getCurrentRunID(theResult.getTestCaseID()));
    }

    /**
     * Gets the results.
     *
     * @param testCaseID the test case id
     * @return the results
     */
    public ArrayList<TestCaseResult> getResults(String testCaseID) {
        ArrayList<TestCaseResult> tmpList = new ArrayList<TestCaseResult>();
        if (testCaseMap.containsKey(testCaseID)) {
            return testCaseMap.get(testCaseID);
        }
        return tmpList;
    }

    /**
     * Gets the full results list.
     *
     * @return the full results list
     */
    public ArrayList<TestCaseResult> getFullResultsList() {
        return fullResultsList;
    }

    /**
     * Gets the last test case result.
     *
     * @return the last test case result
     */
    public TestCaseResult getLastTestCaseResult() {
        if (!fullResultsList.isEmpty()) {
            return fullResultsList.get(fullResultsList.size() - 1);
        }
        return null;
    }

    /**
     * Gets the current run id.
     *
     * @param testCaseID the test case id
     * @return the current run id
     */
    public Integer getCurrentRunID(String testCaseID) {
        Integer runID = 0;
        for (TestCaseResult thisResult : fullResultsList) {
            if (thisResult.getTestCaseID().equals(testCaseID)) {
                runID++;
            }
        }
        return runID;
    }

    /**
     * Gets the run count.
     *
     * @param testCaseID the test case id
     * @return the run count
     */
    public Integer getRunCount(String testCaseID) {
        Integer runID = 0;
        for (TestCaseResult thisResult : fullResultsList) {
            if (thisResult.getTestCaseID().equals(testCaseID)) {
                runID++;
            }
        }
        return runID;
    }

    /**
     * Gets the fail count.
     *
     * @param testCaseID the test case id
     * @return the fail count
     */
    public Integer getFailCount(String testCaseID) {
        Integer failCount = 0;
        for (TestCaseResult thisResult : fullResultsList) {
            if (thisResult.getTestCaseID().equals(testCaseID)) {
                if (thisResult.getResult().equalsIgnoreCase("Failed")){
                    failCount++;
                }
            }
        }
        return failCount;
    }

    /**
     * Update test case result.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param tcResult the tc result
     * @param endTime the end time
     * @param startTime the start time
     * @param standardLayer the standard layer
     * @param testCaseRunID the test case run id
     * @param result the result
     * @param errorDescription the error description
     */
    public void updateTestCaseResult(TestCaseResult tcResult, Long endTime, Long startTime, String standardLayer, Integer testCaseRunID, String result, String errorDescription) {
        tcResult.updateTestCaseResult(endTime, startTime, standardLayer, testCaseRunID, result, errorDescription);
        for (TestCaseResultsListener thisListener : testCaseResultsListeners) {
            thisListener.testCaseUpdate();
        }
    }

    /**
     * Adds the new test step.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param tcResult the tc result
     * @param testStepDescription the test step description
     * @param startTime the start time
     * @param endTime the end time
     * @param errorDescription the error description
     * @param result the result
     * @return the int
     */
    public int addNewTestStep(TestCaseResult tcResult, String testStepDescription, long startTime, long endTime, String errorDescription, String result) {
        int identifier = tcResult.addNewTestStep(testStepDescription, startTime, endTime, errorDescription, result);
        for (TestCaseResultsListener thisListener : testCaseResultsListeners) {
            thisListener.testStepUpdate();
        }
        return identifier;
    }

    /**
     * Update existing test step.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param tcResult the tc result
     * @param endTime the end time
     * @param errorDescription the error description
     * @param result the result
     * @param testStepIdentifier the test step identifier
     */
    public void updateExistingTestStep(TestCaseResult tcResult, long endTime, String errorDescription, String result, int testStepIdentifier) {
        tcResult.updateTestStep(endTime, errorDescription, result, testStepIdentifier);
        for (TestCaseResultsListener thisListener : testCaseResultsListeners) {
            thisListener.testStepUpdate();
        }
    }
    
    /**
     * Register listner.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param listener the listener
     */
    public void registerListner(TestCaseResultsListener listener) {
        if (!testCaseResultsListeners.contains(listener)) {
            testCaseResultsListeners.add(listener);
        }
    }

    /**
     * Un register listner.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param listener the listener
     */
    public void unRegisterListner(TestCaseResultsListener listener) {
        if (testCaseResultsListeners.contains(listener)) {
            testCaseResultsListeners.remove(listener);
        }
    }

    /**
     * Clear.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void clear(){
        testCaseMap.clear();
        fullResultsList.clear();
        testCaseResultsListeners.clear();
    }
}
