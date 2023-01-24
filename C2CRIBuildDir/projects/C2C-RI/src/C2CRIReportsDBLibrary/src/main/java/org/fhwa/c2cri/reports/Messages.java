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
 * Captures all information related to Message event in a log file. *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "Messages")
@NamedQueries({
    @NamedQuery(name = "Messages.findAll", query = "SELECT m FROM Messages m"),
    @NamedQuery(name = "Messages.findByMessageID", query = "SELECT m FROM Messages m WHERE m.messageID = :messageID"),
    @NamedQuery(name = "Messages.findByEventID", query = "SELECT m FROM Messages m WHERE m.eventID = :eventID"),
    @NamedQuery(name = "Messages.findByEventType", query = "SELECT m FROM Messages m WHERE m.eventType = :eventType"),
    @NamedQuery(name = "Messages.findByEventTypeID", query = "SELECT m FROM Messages m WHERE m.eventTypeID = :eventTypeID"),
    @NamedQuery(name = "Messages.findByDebugInfo", query = "SELECT m FROM Messages m WHERE m.debugInfo = :debugInfo")})
public class Messages implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "MessageID")
    private Integer messageID;
    @Column(name = "EventID")
    private Integer eventID;
    @Column(name = "eventType")
    private String eventType;
    @Column(name = "eventTypeID")
    private Integer eventTypeID;
    @Column(name = "debugInfo")
    private String debugInfo;

    public Messages() {
    }

    public Messages(Integer messageID) {
        this.messageID = messageID;
    }

    public Integer getMessageID() {
        return messageID;
    }

    public void setMessageID(Integer messageID) {
        this.messageID = messageID;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Integer getEventTypeID() {
        return eventTypeID;
    }

    public void setEventTypeID(Integer eventTypeID) {
        this.eventTypeID = eventTypeID;
    }

    public String getDebugInfo() {
        return debugInfo;
    }

    public void setDebugInfo(String debugInfo) {
        this.debugInfo = debugInfo;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (messageID != null ? messageID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Messages)) {
            return false;
        }
        Messages other = (Messages) object;
        if ((this.messageID == null && other.messageID != null) || (this.messageID != null && !this.messageID.equals(other.messageID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.Messages[messageID=" + messageID + "]";
    }

}
