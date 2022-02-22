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
 * Captures all information related to a test script event in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "ScriptEvent")
@NamedQueries({
    @NamedQuery(name = "ScriptEvent.findAll", query = "SELECT s FROM ScriptEvent s"),
    @NamedQuery(name = "ScriptEvent.findByEventTypeID", query = "SELECT s FROM ScriptEvent s WHERE s.eventTypeID = :eventTypeID"),
    @NamedQuery(name = "ScriptEvent.findByEventID", query = "SELECT s FROM ScriptEvent s WHERE s.eventID = :eventID"),
    @NamedQuery(name = "ScriptEvent.findBySrc", query = "SELECT s FROM ScriptEvent s WHERE s.src = :src"),
    @NamedQuery(name = "ScriptEvent.findByTag", query = "SELECT s FROM ScriptEvent s WHERE s.tag = :tag"),
    @NamedQuery(name = "ScriptEvent.findByType", query = "SELECT s FROM ScriptEvent s WHERE s.type = :type"),
    @NamedQuery(name = "ScriptEvent.findByLine", query = "SELECT s FROM ScriptEvent s WHERE s.line = :line"),
    @NamedQuery(name = "ScriptEvent.findByColumn", query = "SELECT s FROM ScriptEvent s WHERE s.column = :column"),
    @NamedQuery(name = "ScriptEvent.findByFile", query = "SELECT s FROM ScriptEvent s WHERE s.file = :file"),
    @NamedQuery(name = "ScriptEvent.findByFunctionId", query = "SELECT s FROM ScriptEvent s WHERE s.functionId = :functionId"),
    @NamedQuery(name = "ScriptEvent.findByTestCaseName", query = "SELECT s FROM ScriptEvent s WHERE s.testCaseName = :testCaseName"),
    @NamedQuery(name = "ScriptEvent.findByOutcome", query = "SELECT s FROM ScriptEvent s WHERE s.outcome = :outcome"),
    @NamedQuery(name = "ScriptEvent.findByExecutionTime", query = "SELECT s FROM ScriptEvent s WHERE s.executionTime = :executionTime"),
    @NamedQuery(name = "ScriptEvent.findByExecutionTimeMillis", query = "SELECT s FROM ScriptEvent s WHERE s.executionTimeMillis = :executionTimeMillis")})
public class ScriptEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "eventTypeID")
    private Integer eventTypeID;
    @Column(name = "EventID")
    private Integer eventID;
    @Column(name = "src")
    private String src;
    @Column(name = "tag")
    private String tag;
    @Column(name = "type")
    private String type;
    @Column(name = "line")
    private String line;
    @Column(name = "column")
    private String column;
    @Column(name = "file")
    private String file;
    @Column(name = "functionId")
    private String functionId;
    @Column(name = "test-case-name")
    private String testCaseName;
    @Column(name = "outcome")
    private String outcome;
    @Column(name = "execution-time")
    private String executionTime;
    @Column(name = "execution-time-millis")
    private Integer executionTimeMillis;
    @Column(name = "error")
    private String error;   

    public ScriptEvent() {
    }

    public ScriptEvent(Integer eventTypeID) {
        this.eventTypeID = eventTypeID;
    }

    public Integer getEventTypeID() {
        return eventTypeID;
    }

    public void setEventTypeID(Integer eventTypeID) {
        this.eventTypeID = eventTypeID;
    }

    public Integer getEventID() {
        return eventID;
    }

    public void setEventID(Integer eventID) {
        this.eventID = eventID;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFunctionId() {
        return functionId;
    }

    public void setFunctionId(String functionId) {
        this.functionId = functionId;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getOutcome() {
        return outcome;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }

    public String getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(String executionTime) {
        this.executionTime = executionTime;
    }

    public Integer getExecutionTimeMillis() {
        return executionTimeMillis;
    }

    public void setExecutionTimeMillis(Integer executionTimeMillis) {
        this.executionTimeMillis = executionTimeMillis;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }   
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (eventTypeID != null ? eventTypeID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ScriptEvent)) {
            return false;
        }
        ScriptEvent other = (ScriptEvent) object;
        if ((this.eventTypeID == null && other.eventTypeID != null) || (this.eventTypeID != null && !this.eventTypeID.equals(other.eventTypeID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.ScriptEvent[eventTypeID=" + eventTypeID + "]";
    }

}
