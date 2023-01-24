/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Captures all information related to a test result requirement primary key.
 *
 * @author TransCore ITS, LLC
 */
@Embeddable
public class TestResultRequirementsPK implements Serializable {
    @Column(name = "requirementID")
    private Integer requirementID;
    @Column(name = "needID")
    private Integer needID;

    public TestResultRequirementsPK() {
    }

    public TestResultRequirementsPK(Integer requirementID, Integer needID) {
        this.requirementID = requirementID;
        this.needID = needID;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (requirementID != null ? requirementID.hashCode() : 0);
        hash += (needID != null ? needID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TestResultRequirementsPK)) {
            return false;
        }
        TestResultRequirementsPK other = (TestResultRequirementsPK) object;
        if ((this.requirementID == null && other.requirementID != null) || (this.requirementID != null && !this.requirementID.equals(other.requirementID))) {
            return false;
        }
        if ((this.needID == null && other.needID != null) || (this.needID != null && !this.needID.equals(other.needID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.TestResultRequirementsPK[requirementID=" + requirementID + ", needID=" + needID + "]";
    }

}
