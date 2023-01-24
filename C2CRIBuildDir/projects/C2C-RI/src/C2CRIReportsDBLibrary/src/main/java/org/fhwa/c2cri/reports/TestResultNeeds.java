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
 * Captures all information related to a test result need in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "TestResultNeeds")
@NamedQueries({
    @NamedQuery(name = "TestResultNeeds.findAll", query = "SELECT t FROM TestResultNeeds t"),
    @NamedQuery(name = "TestResultNeeds.findByNeedID", query = "SELECT t FROM TestResultNeeds t WHERE t.testResultNeedsPK.needID = :needID"),
    @NamedQuery(name = "TestResultNeeds.findByStandardType", query = "SELECT t FROM TestResultNeeds t WHERE t.testResultNeedsPK.standardType = :standardType"),
    @NamedQuery(name = "TestResultNeeds.findByExtension", query = "SELECT t FROM TestResultNeeds t WHERE t.extension = :extension"),
    @NamedQuery(name = "TestResultNeeds.findByResults", query = "SELECT t FROM TestResultNeeds t WHERE t.results = :results"),
    @NamedQuery(name = "TestResultNeeds.findByUnID", query = "SELECT t FROM TestResultNeeds t WHERE t.unID = :unID"),
    @NamedQuery(name = "TestResultNeeds.findByUnSelected", query = "SELECT t FROM TestResultNeeds t WHERE t.unSelected = :unSelected"),
    @NamedQuery(name = "TestResultNeeds.findByUserNeed", query = "SELECT t FROM TestResultNeeds t WHERE t.userNeed = :userNeed")})
public class TestResultNeeds implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected TestResultNeedsPK testResultNeedsPK;
    @Column(name = "extension")
    private String extension;
    @Column(name = "results")
    private String results;
    @Column(name = "unID")
    private String unID;
    @Column(name = "unSelected")
    private String unSelected;
    @Column(name = "userNeed")
    private String userNeed;

    public TestResultNeeds() {
    }

    public TestResultNeeds(TestResultNeedsPK testResultNeedsPK) {
        this.testResultNeedsPK = testResultNeedsPK;
    }

    public TestResultNeeds(Integer needID, String standardType) {
        this.testResultNeedsPK = new TestResultNeedsPK(needID, standardType);
    }

    public TestResultNeedsPK getTestResultNeedsPK() {
        return testResultNeedsPK;
    }

    public void setTestResultNeedsPK(TestResultNeedsPK testResultNeedsPK) {
        this.testResultNeedsPK = testResultNeedsPK;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getUnID() {
        return unID;
    }

    public void setUnID(String unID) {
        this.unID = unID;
    }

    public String getUnSelected() {
        return unSelected;
    }

    public void setUnSelected(String unSelected) {
        this.unSelected = unSelected;
    }

    public String getUserNeed() {
        return userNeed;
    }

    public void setUserNeed(String userNeed) {
        this.userNeed = userNeed;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (testResultNeedsPK != null ? testResultNeedsPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TestResultNeeds)) {
            return false;
        }
        TestResultNeeds other = (TestResultNeeds) object;
        if ((this.testResultNeedsPK == null && other.testResultNeedsPK != null) || (this.testResultNeedsPK != null && !this.testResultNeedsPK.equals(other.testResultNeedsPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.TestResultNeeds[testResultNeedsPK=" + testResultNeedsPK + "]";
    }

}
