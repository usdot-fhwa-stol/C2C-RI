/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TransCore ITS
 */
public class TestCase {

 private String testCaseID;
 private String testCaseTitle;
 private String testCaseDescription;
 private String testItemType;
 private String testItemCenterMode;
 private String testItemNeedID;
 private String testCaseDialog;
 private String testCaseType="TMDD";
 private List<String> dialogRequirements = new ArrayList<String>();
 private List<MessageElement> messageElements = new ArrayList<MessageElement>();
 private List<String> testItemRequirements = new ArrayList<String>();
 private List<TestCaseInput> testCaseInputs = new ArrayList<TestCaseInput>();
 private List<TestCaseOutput> testCaseOutputs = new ArrayList<TestCaseOutput>();
 private List<TestVariable> testCaseVariables = new ArrayList<TestVariable>();
 private String preConditionRequirement;

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

 public String getPreConditionRequirement() {
        return preConditionRequirement;
    }

    public void setPreConditionRequirement(String preConditionRequirement) {
        this.preConditionRequirement = preConditionRequirement;
    }

    public String getTestCaseDescription() {
        return testCaseDescription;
    }

    public void setTestCaseDescription(String testCaseDescription) {
        this.testCaseDescription = testCaseDescription;
    }

    public String getTestCaseID() {
        return testCaseID;
    }

    public void setTestCaseID(String testCaseID) {
        this.testCaseID = testCaseID;
    }

    public String getTestCaseTitle() {
        return testCaseTitle;
    }

    public void setTestCaseTitle(String testCaseTitle) {
        this.testCaseTitle = testCaseTitle;
    }

    public String getTestItemCenterMode() {
        return testItemCenterMode;
    }

    public void setTestItemCenterMode(String testItemCenterMode) {
        this.testItemCenterMode = testItemCenterMode;
    }

    public String getTestItemNeedID() {
        return testItemNeedID;
    }

    public void setTestItemNeedID(String testItemNeedID) {
        this.testItemNeedID = testItemNeedID;
    }

    public List<String> getTestItemRequirements() {
        return testItemRequirements;
    }

    public void setTestItemRequirements(List<String> testItemRequirements) {
        this.testItemRequirements = testItemRequirements;
    }

    public String getTestItemType() {
        return testItemType;
    }

    public void setTestItemType(String testItemType) {
        this.testItemType = testItemType;
    }

    public List<TestVariable> getTestCaseVariables() {
        return testCaseVariables;
    }

    public void setTestCaseVariables(List<TestVariable> testCaseVariables) {
        this.testCaseVariables = testCaseVariables;
    }

    public List<TestCaseInput> getTestCaseInputs() {
        return testCaseInputs;
    }

    public void setTestCaseInputs(List<TestCaseInput> testCaseInputs) {
        this.testCaseInputs = testCaseInputs;
    }

    public List<TestCaseOutput> getTestCaseOutputs() {
        return testCaseOutputs;
    }

    public void setTestCaseOutputs(List<TestCaseOutput> testCaseOutputs) {
        this.testCaseOutputs = testCaseOutputs;
    }

    public String getTestCaseDialog() {
        return testCaseDialog;
    }

    public void setTestCaseDialog(String testCaseDialog) {
        this.testCaseDialog = testCaseDialog;
    }

    public List<String> getDialogRequirements() {
        return dialogRequirements;
    }

    public void setDialogRequirements(List<String> dialogRequirements) {
        this.dialogRequirements = dialogRequirements;
    }

    public List<MessageElement> getMessageElements() {
        return messageElements;
    }

    public void setMessageElements(List<MessageElement> messageElements) {
        this.messageElements = messageElements;
    }

    public String getTestCaseType() {
        return testCaseType;
    }

    public void setTestCaseType(String testCaseType) {
        this.testCaseType = testCaseType;
    }


}
