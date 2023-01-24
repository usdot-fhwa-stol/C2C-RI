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
 * Captures all information related to a user event in a log file.
 *
 * @author TransCore ITS, LLC
 */
@Entity
@Table(name = "UserEvent")
@NamedQueries({
    @NamedQuery(name = "UserEvent.findAll", query = "SELECT u FROM UserEvent u"),
    @NamedQuery(name = "UserEvent.findById", query = "SELECT u FROM UserEvent u WHERE u.id = :id"),
    @NamedQuery(name = "UserEvent.findByDescription", query = "SELECT u FROM UserEvent u WHERE u.description = :description")})
public class UserEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "Description")
    private String description;

    public UserEvent() {
    }

    public UserEvent(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof UserEvent)) {
            return false;
        }
        UserEvent other = (UserEvent) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.fhwa.c2cri.reports.UserEvent[id=" + id + "]";
    }

}
