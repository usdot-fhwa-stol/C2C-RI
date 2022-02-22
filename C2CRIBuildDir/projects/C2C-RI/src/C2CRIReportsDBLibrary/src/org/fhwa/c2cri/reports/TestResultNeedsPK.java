/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Captures all information related to a test result need primary key.
 *
 * @author TransCore ITS, LLC
 */
@Embeddable
public class TestResultNeedsPK implements Serializable {
    @Column(name = "needID")
    private Integer needID;
    @Column(name = "standardType")
    private String standardType;

    public TestResultNeedsPK() {
    }

    public TestResultNeedsPK(Integer needID, String standardType) {
        this.needID = needID;
        this.standardType = standardType;
    }

    public Integer getNeedID() {
        return needID;
    }

    public void setNeedID(Integer needID) {
        this.needID = needID;
    }

    public String getStandardType() {
        return standardType;
    }

    public void setStandardType(String standardType) {
        this.standardType = standardType;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (needID != null ? needID.hashCode() : 0);
        hash += (standardType != null ? standardType.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TestResultNeedsPK)) {
            return false;
        }
        TestResultNeedsPK other = (TestResultNeedsPK) object;
        if ((this.needID == null && other.needID != null) || (this.needID != null && !this.needID.equals(other.needID))) {
            return false;
        }
        if ((this.standardType == null && other.standardType != null) || (this.standardType != null && !this.standardType.equals(other.standardType))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.TestResultNeedsPK[needID=" + needID + ", standardType=" + standardType + "]";
    }

}
