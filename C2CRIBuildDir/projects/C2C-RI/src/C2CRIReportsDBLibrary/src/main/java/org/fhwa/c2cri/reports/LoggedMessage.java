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
 * Captures all information related to a logged Message event in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "LoggedMessage")
@NamedQueries({
    @NamedQuery(name = "LoggedMessage.findAll", query = "SELECT l FROM LoggedMessage l"),
    @NamedQuery(name = "LoggedMessage.findById", query = "SELECT l FROM LoggedMessage l WHERE l.id = :id"),
    @NamedQuery(name = "LoggedMessage.findByParentDialog", query = "SELECT l FROM LoggedMessage l WHERE l.parentDialog = :parentDialog"),
    @NamedQuery(name = "LoggedMessage.findByMessageName", query = "SELECT l FROM LoggedMessage l WHERE l.messageName = :messageName"),
    @NamedQuery(name = "LoggedMessage.findByMessageType", query = "SELECT l FROM LoggedMessage l WHERE l.messageType = :messageType"),
    @NamedQuery(name = "LoggedMessage.findByMessageEncoding", query = "SELECT l FROM LoggedMessage l WHERE l.messageEncoding = :messageEncoding"),
    @NamedQuery(name = "LoggedMessage.findByViaTransportProtocol", query = "SELECT l FROM LoggedMessage l WHERE l.viaTransportProtocol = :viaTransportProtocol"),
    @NamedQuery(name = "LoggedMessage.findByMessageSourceAddress", query = "SELECT l FROM LoggedMessage l WHERE l.messageSourceAddress = :messageSourceAddress"),
    @NamedQuery(name = "LoggedMessage.findByMessageDestinationAddress", query = "SELECT l FROM LoggedMessage l WHERE l.messageDestinationAddress = :messageDestinationAddress"),
    @NamedQuery(name = "LoggedMessage.findByRelatedCommand", query = "SELECT l FROM LoggedMessage l WHERE l.relatedCommand = :relatedCommand"),
    @NamedQuery(name = "LoggedMessage.findByMessageBody", query = "SELECT l FROM LoggedMessage l WHERE l.messageBody = :messageBody")})
public class LoggedMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "parentDialog")
    private String parentDialog;
    @Column(name = "messageName")
    private String messageName;
    @Column(name = "messageType")
    private String messageType;
    @Column(name = "messageEncoding")
    private String messageEncoding;
    @Column(name = "viaTransportProtocol")
    private String viaTransportProtocol;
    @Column(name = "messageSourceAddress")
    private String messageSourceAddress;
    @Column(name = "messageDestinationAddress")
    private String messageDestinationAddress;
    @Column(name = "relatedCommand")
    private String relatedCommand;
    @Column(name = "MessageBody")
    private String messageBody;

    public LoggedMessage() {
    }

    public LoggedMessage(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getParentDialog() {
        return parentDialog;
    }

    public void setParentDialog(String parentDialog) {
        this.parentDialog = parentDialog;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageEncoding() {
        return messageEncoding;
    }

    public void setMessageEncoding(String messageEncoding) {
        this.messageEncoding = messageEncoding;
    }

    public String getViaTransportProtocol() {
        return viaTransportProtocol;
    }

    public void setViaTransportProtocol(String viaTransportProtocol) {
        this.viaTransportProtocol = viaTransportProtocol;
    }

    public String getMessageSourceAddress() {
        return messageSourceAddress;
    }

    public void setMessageSourceAddress(String messageSourceAddress) {
        this.messageSourceAddress = messageSourceAddress;
    }

    public String getMessageDestinationAddress() {
        return messageDestinationAddress;
    }

    public void setMessageDestinationAddress(String messageDestinationAddress) {
        this.messageDestinationAddress = messageDestinationAddress;
    }

    public String getRelatedCommand() {
        return relatedCommand;
    }

    public void setRelatedCommand(String relatedCommand) {
        this.relatedCommand = relatedCommand;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof LoggedMessage)) {
            return false;
        }
        LoggedMessage other = (LoggedMessage) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.LoggedMessage[id=" + id + "]";
    }

}
