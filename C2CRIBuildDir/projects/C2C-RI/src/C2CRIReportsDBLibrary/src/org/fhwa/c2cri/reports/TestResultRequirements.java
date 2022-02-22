/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Captures all information related to a test result requirements in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "TestResultRequirements")
@NamedQueries({
    @NamedQuery(name = "TestResultRequirements.findAll", query = "SELECT t FROM TestResultRequirements t"),
    @NamedQuery(name = "TestResultRequirements.findByRequirementID", query = "SELECT t FROM TestResultRequirements t WHERE t.testResultRequirementsPK.requirementID = :requirementID"),
    @NamedQuery(name = "TestResultRequirements.findByNeedID", query = "SELECT t FROM TestResultRequirements t WHERE t.testResultRequirementsPK.needID = :needID"),
    @NamedQuery(name = "TestResultRequirements.findByConformance", query = "SELECT t FROM TestResultRequirements t WHERE t.conformance = :conformance"),
    @NamedQuery(name = "TestResultRequirements.findByExtension", query = "SELECT t FROM TestResultRequirements t WHERE t.extension = :extension"),
    @NamedQuery(name = "TestResultRequirements.findByOtherReq", query = "SELECT t FROM TestResultRequirements t WHERE t.otherReq = :otherReq"),
    @NamedQuery(name = "TestResultRequirements.findByReqID", query = "SELECT t FROM TestResultRequirements t WHERE t.reqID = :reqID"),
    @NamedQuery(name = "TestResultRequirements.findByReqText", query = "SELECT t FROM TestResultRequirements t WHERE t.reqText = :reqText"),
    @NamedQuery(name = "TestResultRequirements.findByResults", query = "SELECT t FROM TestResultRequirements t WHERE t.results = :results"),
    @NamedQuery(name = "TestResultRequirements.findBySupported", query = "SELECT t FROM TestResultRequirements t WHERE t.supported = :supported")})
public class TestResultRequirements implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TestResultRequirementsPK testResultRequirementsPK;
    @Column(name = "conformance")
    private String conformance;
    @Column(name = "extension")
    private String extension;
    @Column(name = "otherReq")
    private String otherReq;
    @Column(name = "reqID")
    private String reqID;
    @Column(name = "reqText")
    private String reqText;
    @Column(name = "results")
    private String results;
    @Column(name = "supported")
    private String supported;

    public TestResultRequirements() {
    }

    public TestResultRequirements(TestResultRequirementsPK testResultRequirementsPK) {
        this.testResultRequirementsPK = testResultRequirementsPK;
    }

    public TestResultRequirements(Integer requirementID, Integer needID) {
        this.testResultRequirementsPK = new TestResultRequirementsPK(requirementID, needID);
    }

    public TestResultRequirementsPK getTestResultRequirementsPK() {
        return testResultRequirementsPK;
    }

    public void setTestResultRequirementsPK(TestResultRequirementsPK testResultRequirementsPK) {
        this.testResultRequirementsPK = testResultRequirementsPK;
    }

    public String getConformance() {
        return conformance;
    }

    public void setConformance(String conformance) {
        this.conformance = conformance;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getOtherReq() {
        return otherReq;
    }

    public void setOtherReq(String otherReq) {
        this.otherReq = otherReq;
    }

    public String getReqID() {
        return reqID;
    }

    public void setReqID(String reqID) {
        this.reqID = reqID;
    }

    public String getReqText() {
        return reqText;
    }

    public void setReqText(String reqText) {
        this.reqText = reqText;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getSupported() {
        return supported;
    }

    public void setSupported(String supported) {
        this.supported = supported;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (testResultRequirementsPK != null ? testResultRequirementsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TestResultRequirements)) {
            return false;
        }
        TestResultRequirements other = (TestResultRequirements) object;
        if ((this.testResultRequirementsPK == null && other.testResultRequirementsPK != null) || (this.testResultRequirementsPK != null && !this.testResultRequirementsPK.equals(other.testResultRequirementsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.TestResultRequirements[testResultRequirementsPK=" + testResultRequirementsPK + "]";
    }

}
