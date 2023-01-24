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
 * Captures all information related to an application layer test procedure step.
 * 
 * @author TransCore ITS, LLC
 * Created: Oct 14, 2016
 */
@Entity
@Table(name = "C2CRI_AppLayerTestProcedureSteps")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "C2CRIAppLayerTestProcedureSteps.findAll", query = "SELECT c FROM C2CRIAppLayerTestProcedureSteps c"),
    @NamedQuery(name = "C2CRIAppLayerTestProcedureSteps.findByKeyid", query = "SELECT c FROM C2CRIAppLayerTestProcedureSteps c WHERE c.keyid = :keyid"),
    @NamedQuery(name = "C2CRIAppLayerTestProcedureSteps.findById", query = "SELECT c FROM C2CRIAppLayerTestProcedureSteps c WHERE c.id = :id"),
    @NamedQuery(name = "C2CRIAppLayerTestProcedureSteps.findByProcedure", query = "SELECT c FROM C2CRIAppLayerTestProcedureSteps c WHERE c.procedure = :procedure"),
    @NamedQuery(name = "C2CRIAppLayerTestProcedureSteps.findByStep", query = "SELECT c FROM C2CRIAppLayerTestProcedureSteps c WHERE c.step = :step"),
    @NamedQuery(name = "C2CRIAppLayerTestProcedureSteps.findByAction", query = "SELECT c FROM C2CRIAppLayerTestProcedureSteps c WHERE c.action = :action"),
    @NamedQuery(name = "C2CRIAppLayerTestProcedureSteps.findByPassFail", query = "SELECT c FROM C2CRIAppLayerTestProcedureSteps c WHERE c.passFail = :passFail")})
public class C2CRIAppLayerTestProcedureStep implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "keyid")
    private Integer keyid;
    @Basic(optional = false)
    @Column(name = "Id")
    private int id;
    @Basic(optional = false)
    @Column(name = "Procedure")
    private String procedure;
    @Basic(optional = false)
    @Column(name = "Step")
    private String step;
    @Basic(optional = false)
    @Column(name = "Action")
    private String action;
    @Basic(optional = false)
    @Column(name = "PassFail")
    private String passFail;

    public C2CRIAppLayerTestProcedureStep() {
    }

    public C2CRIAppLayerTestProcedureStep(Integer keyid) {
        this.keyid = keyid;
    }

    public C2CRIAppLayerTestProcedureStep(Integer keyid, int id, String procedure, String step, String action, String passFail) {
        this.keyid = keyid;
        this.id = id;
        this.procedure = procedure;
        this.step = step;
        this.action = action;
        this.passFail = passFail;
    }

    public Integer getKeyid() {
        return keyid;
    }

    public void setKeyid(Integer keyid) {
        this.keyid = keyid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPassFail() {
        return passFail;
    }

    public void setPassFail(String passFail) {
        this.passFail = passFail;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (keyid != null ? keyid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof C2CRIAppLayerTestProcedureStep)) {
            return false;
        }
        C2CRIAppLayerTestProcedureStep other = (C2CRIAppLayerTestProcedureStep) object;
        if ((this.keyid == null && other.keyid != null) || (this.keyid != null && !this.keyid.equals(other.keyid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "C2CRIAppLayerTestProcedureSteps[ keyid=" + keyid + " ]";
    }

}
