/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.testing;

/**
 *
 * @author TransCore ITS
 */
public class TestResult {
    private String needID;
    private String needText;
    private String testType;
    private String subTestTitle;
    private String testResult;
    private String errorDescription;

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getNeedID() {
        return needID;
    }

    public void setNeedID(String needID) {
        this.needID = needID;
    }

    public String getNeedText() {
        return needText;
    }

    public void setNeedText(String needText) {
        this.needText = needText;
    }

    public String getTestResult() {
        return testResult;
    }

    public void setTestResult(String testResult) {
        this.testResult = testResult;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getSubTestTitle() {
        return subTestTitle;
    }

    public void setSubTestTitle(String subTestTitle) {
        this.subTestTitle = subTestTitle;
    }


}
