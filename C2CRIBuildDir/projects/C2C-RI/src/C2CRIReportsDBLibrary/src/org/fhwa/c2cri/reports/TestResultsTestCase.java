/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Captures all information related to a test result test case in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "TestResultsTestCase")
@NamedQueries({
    @NamedQuery(name = "TestResultsTestCase.findAll", query = "SELECT t FROM TestResultsTestCase t"),
    @NamedQuery(name = "TestResultsTestCase.findByTcid", query = "SELECT t FROM TestResultsTestCase t WHERE t.tcid = :tcid"),
    @NamedQuery(name = "TestResultsTestCase.findByRequirementID", query = "SELECT t FROM TestResultsTestCase t WHERE t.requirementID = :requirementID"),
    @NamedQuery(name = "TestResultsTestCase.findByNeedID", query = "SELECT t FROM TestResultsTestCase t WHERE t.needID = :needID"),
    @NamedQuery(name = "TestResultsTestCase.findByErrorDescription", query = "SELECT t FROM TestResultsTestCase t WHERE t.errorDescription = :errorDescription"),
    @NamedQuery(name = "TestResultsTestCase.findByResult", query = "SELECT t FROM TestResultsTestCase t WHERE t.result = :result"),
    @NamedQuery(name = "TestResultsTestCase.findByTestCaseID", query = "SELECT t FROM TestResultsTestCase t WHERE t.testCaseID = :testCaseID"),
    @NamedQuery(name = "TestResultsTestCase.findByTimeStamp", query = "SELECT t FROM TestResultsTestCase t WHERE t.timeStamp = :timeStamp")})
public class TestResultsTestCase implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "TCID")
    private Integer tcid;
    @Column(name = "RequirementID")
    private Integer requirementID;
    @Column(name = "NeedID")
    private Integer needID;
    @Column(name = "errorDescription")
    private String errorDescription;
    @Column(name = "result")
    private String result;
    @Column(name = "testCaseID")
    private String testCaseID;
    @Column(name = "timeStamp")
    private String timeStamp;

    public TestResultsTestCase() {
    }

    public TestResultsTestCase(Integer tcid) {
        this.tcid = tcid;
    }

    public Integer getTcid() {
        return tcid;
    }

    public void setTcid(Integer tcid) {
        this.tcid = tcid;
    }

    public Integer getRequirementID() {
        return requirementID;
    }

    public void setRequirementID(Integer requirementID) {
        this.requirementID = requirementID;
    }

    public Integer getNeedID() {
        return needID;
    }

    public void setNeedID(Integer needID) {
        this.needID = needID;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTestCaseID() {
        return testCaseID;
    }

    public void setTestCaseID(String testCaseID) {
        this.testCaseID = testCaseID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (tcid != null ? tcid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TestResultsTestCase)) {
            return false;
        }
        TestResultsTestCase other = (TestResultsTestCase) object;
        if ((this.tcid == null && other.tcid != null) || (this.tcid != null && !this.tcid.equals(other.tcid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.TestResultsTestCase[tcid=" + tcid + "]";
    }

}
