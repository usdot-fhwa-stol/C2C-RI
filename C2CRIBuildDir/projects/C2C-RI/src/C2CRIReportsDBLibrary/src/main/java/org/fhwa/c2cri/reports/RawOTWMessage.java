/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import java.math.BigInteger;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Captures all information related to a Raw Over the Wire Message event in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "RawOTWMessage")
@NamedQueries({
    @NamedQuery(name = "RawOTWMessage.findAll", query = "SELECT r FROM RawOTWMessage r"),
    @NamedQuery(name = "RawOTWMessage.findById", query = "SELECT r FROM RawOTWMessage r WHERE r.id = :id"),
    @NamedQuery(name = "RawOTWMessage.findByTestCase", query = "SELECT r FROM RawOTWMessage r WHERE r.testCase = :testCase"),
    @NamedQuery(name = "RawOTWMessage.findByConnectionName", query = "SELECT r FROM RawOTWMessage r WHERE r.connectionName = :connectionName"),
    @NamedQuery(name = "RawOTWMessage.findByProcessType", query = "SELECT r FROM RawOTWMessage r WHERE r.processType = :processType"),
    @NamedQuery(name = "RawOTWMessage.findBySourceAddress", query = "SELECT r FROM RawOTWMessage r WHERE r.sourceAddress = :sourceAddress"),
    @NamedQuery(name = "RawOTWMessage.findByDestinationAddress", query = "SELECT r FROM RawOTWMessage r WHERE r.destinationAddress = :destinationAddress"),
    @NamedQuery(name = "RawOTWMessage.findBySequenceCount", query = "SELECT r FROM RawOTWMessage r WHERE r.sequenceCount = :sequenceCount"),
    @NamedQuery(name = "RawOTWMessage.findBySequenceCount", query = "SELECT r FROM RawOTWMessage r WHERE r.timeStampInMillis = :timeStampInMillis"),
    @NamedQuery(name = "RawOTWMessage.findByMessage", query = "SELECT r FROM RawOTWMessage r WHERE r.message = :message")})
public class RawOTWMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "testCase")
    private String testCase;
    @Column(name = "connectionName")
    private String connectionName;
    @Column(name = "ProcessType")
    private String processType;
    @Column(name = "SourceAddress")
    private String sourceAddress;
    @Column(name = "DestinationAddress")
    private String destinationAddress;
    @Column(name = "sequenceCount")
    private Integer sequenceCount;
    @Column(name = "message")
    private String message;
    @Column(name = "TimeStampInMillis")
    private BigInteger timeStampInMillis;

    public RawOTWMessage() {
    }

    public RawOTWMessage(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Integer getSequenceCount() {
        return sequenceCount;
    }

    public void setSequenceCount(Integer sequenceCount) {
        this.sequenceCount = sequenceCount;
    }

    public BigInteger getTimeStampInMillis() {
        return timeStampInMillis;
    }

    public void setTimeStampInMillis(BigInteger timeStampInMillis) {
        this.timeStampInMillis = timeStampInMillis;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RawOTWMessage)) {
            return false;
        }
        RawOTWMessage other = (RawOTWMessage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.RawOTWMessage[id=" + id + "]";
    }

}
