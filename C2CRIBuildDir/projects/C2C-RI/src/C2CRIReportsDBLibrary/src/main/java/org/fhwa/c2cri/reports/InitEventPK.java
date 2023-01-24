/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Represents the primary key of an initialization event.
 * 
 * @author TransCore ITS, LLC
 */
@Embeddable
public class InitEventPK implements Serializable {
    @Column(name = "ID")
    private Integer id;
    @Column(name = "EventID")
    private Integer eventID;

    public InitEventPK() {
    }

    public InitEventPK(Integer id, Integer eventID) {
        this.id = id;
        this.eventID = eventID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        hash += (eventID != null ? eventID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof InitEventPK)) {
            return false;
        }
        InitEventPK other = (InitEventPK) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        if ((this.eventID == null && other.eventID != null) || (this.eventID != null && !this.eventID.equals(other.eventID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.InitEventPK[id=" + id + ", eventID=" + eventID + "]";
    }

}
