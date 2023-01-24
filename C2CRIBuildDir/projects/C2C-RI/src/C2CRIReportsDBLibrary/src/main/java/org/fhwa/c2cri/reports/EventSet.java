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
 * Captures all information related to an event in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "eventSet")
@NamedQueries({
    @NamedQuery(name = "EventSet.findAll", query = "SELECT e FROM EventSet e"),
    @NamedQuery(name = "EventSet.findByEventID", query = "SELECT e FROM EventSet e WHERE e.eventID = :eventID"),
    @NamedQuery(name = "EventSet.findByLevel", query = "SELECT e FROM EventSet e WHERE e.level = :level"),
    @NamedQuery(name = "EventSet.findByLogger", query = "SELECT e FROM EventSet e WHERE e.logger = :logger"),
    @NamedQuery(name = "EventSet.findByThread", query = "SELECT e FROM EventSet e WHERE e.thread = :thread"),
    @NamedQuery(name = "EventSet.findByTimestamp", query = "SELECT e FROM EventSet e WHERE e.timestamp = :timestamp"),
    @NamedQuery(name = "EventSet.findByEventType", query = "SELECT e FROM EventSet e WHERE e.eventType = :eventType"),
    @NamedQuery(name = "EventSet.findByEventTypeID", query = "SELECT e FROM EventSet e WHERE e.eventTypeID = :eventTypeID"),
    @NamedQuery(name = "EventSet.findByDebugInfo", query = "SELECT e FROM EventSet e WHERE e.debugInfo = :debugInfo")})
public class EventSet implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "EventID")
    private Integer eventID;
    @Column(name = "level")
    private String level;
    @Column(name = "logger")
    private String logger;
    @Column(name = "thread")
    private String thread;
    @Column(name = "timestamp")
    private String timestamp;
    @Column(name = "eventType")
    private String eventType;
    @Column(name = "eventTypeID")
    private Integer eventTypeID;
    @Column(name = "debugInfo")
    private String debugInfo;

    public EventSet() {
    }

    public EventSet(Integer eventID) {
        this.eventID = eventID;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
        hash += (eventID != null ? eventID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof EventSet)) {
            return false;
        }
        EventSet other = (EventSet) object;
        if ((this.eventID == null && other.eventID != null) || (this.eventID != null && !this.eventID.equals(other.eventID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.EventSet[eventID=" + eventID + "]";
    }

}
