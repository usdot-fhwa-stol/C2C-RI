/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.reports;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Captures all information related to information layer test procedures.
 * 
 * @author TransCore ITS, LLC
 * Created: Oct 14, 2016
 */
@Entity
@Table(name = "C2CRI_InfoLayerTestProcedures")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "C2CRIInfoLayerTestProcedures.findAll", query = "SELECT c FROM C2CRIInfoLayerTestProcedures c"),
    @NamedQuery(name = "C2CRIInfoLayerTestProcedures.findById", query = "SELECT c FROM C2CRIInfoLayerTestProcedures c WHERE c.id = :id"),
    @NamedQuery(name = "C2CRIInfoLayerTestProcedures.findByProcedure", query = "SELECT c FROM C2CRIInfoLayerTestProcedures c WHERE c.procedure = :procedure"),
    @NamedQuery(name = "C2CRIInfoLayerTestProcedures.findByProcedureId", query = "SELECT c FROM C2CRIInfoLayerTestProcedures c WHERE c.procedureId = :procedureId"),
    @NamedQuery(name = "C2CRIInfoLayerTestProcedures.findByDescription", query = "SELECT c FROM C2CRIInfoLayerTestProcedures c WHERE c.description = :description"),
    @NamedQuery(name = "C2CRIInfoLayerTestProcedures.findByVariables", query = "SELECT c FROM C2CRIInfoLayerTestProcedures c WHERE c.variables = :variables"),
    @NamedQuery(name = "C2CRIInfoLayerTestProcedures.findByRequirements", query = "SELECT c FROM C2CRIInfoLayerTestProcedures c WHERE c.requirements = :requirements"),
    @NamedQuery(name = "C2CRIInfoLayerTestProcedures.findByProcedureTitle", query = "SELECT c FROM C2CRIInfoLayerTestProcedures c WHERE c.procedureTitle = :procedureTitle")})
public class C2CRIInfoLayerTestProcedure implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Procedure")
    private String procedure;
    @Basic(optional = false)
    @Column(name = "ProcedureId")
    private String procedureId;
    @Basic(optional = false)
    @Column(name = "Description")
    private String description;
    @Basic(optional = false)
    @Column(name = "Variables")
    private String variables;
    @Basic(optional = false)
    @Column(name = "Requirements")
    private String requirements;
    @Basic(optional = false)
    @Column(name = "PassFailCriteria")
    private String passFailCriteria;

    public C2CRIInfoLayerTestProcedure() {
    }

    public C2CRIInfoLayerTestProcedure(Integer id) {
        this.id = id;
    }

    public C2CRIInfoLayerTestProcedure(Integer id, String procedure, String procedureId, String description, String variables, String requirements, String procedureTitle) {
        this.id = id;
        this.procedure = procedure;
        this.procedureId = procedureId;
        this.description = description;
        this.variables = variables;
        this.requirements = requirements;
        this.passFailCriteria = procedureTitle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(String procedureId) {
        this.procedureId = procedureId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVariables() {
        return variables;
    }

    public void setVariables(String variables) {
        this.variables = variables;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getPassFailCriteria() {
        return passFailCriteria;
    }

    public void setPassFailCriteria(String passFailCriteria) {
        this.passFailCriteria = passFailCriteria;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof C2CRIInfoLayerTestProcedure)) {
            return false;
        }
        C2CRIInfoLayerTestProcedure other = (C2CRIInfoLayerTestProcedure) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "C2CRIInfoLayerTestProcedures[ id=" + id + " ]";
    }

}
