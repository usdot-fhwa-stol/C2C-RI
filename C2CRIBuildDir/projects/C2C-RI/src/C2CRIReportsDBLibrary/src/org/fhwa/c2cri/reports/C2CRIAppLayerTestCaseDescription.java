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
 * Captures all information related to an application layer test case description.
 * 
 * @author TransCore ITS, LLC
 * Created: Oct 14, 2016
 */
@Entity
@Table(name = "C2CRI_AppLayerTestCaseDescriptions")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findAll", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findById", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.id = :id"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByTestCaseName", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.testCaseName = :testCaseName"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByTestCaseDescription", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.testCaseDescription = :testCaseDescription"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByItemList", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.itemList = :itemList"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findBySpecialProcedureRequirements", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.specialProcedureRequirements = :specialProcedureRequirements"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByDependencyDescription", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.dependencyDescription = :dependencyDescription"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByHardwareEnvironmental", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.hardwareEnvironmental = :hardwareEnvironmental"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findBySoftwareEnvironmental", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.softwareEnvironmental = :softwareEnvironmental"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByOtherEnvironmental", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.otherEnvironmental = :otherEnvironmental"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByInputs", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.inputs = :inputs"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByInputProcedures", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.inputProcedures = :inputProcedures"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByOutputs", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.outputs = :outputs"),
    @NamedQuery(name = "C2CRIAppLayerTestCaseDescriptions.findByOutputProcedures", query = "SELECT c FROM C2CRIAppLayerTestCaseDescriptions c WHERE c.outputProcedures = :outputProcedures")})
public class C2CRIAppLayerTestCaseDescription implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "TestCaseName")
    private String testCaseName;
    @Basic(optional = false)
    @Column(name = "TestCaseDescription")
    private String testCaseDescription;
    @Basic(optional = false)
    @Column(name = "ItemList")
    private String itemList;
    @Basic(optional = false)
    @Column(name = "SpecialProcedureRequirements")
    private String specialProcedureRequirements;
    @Basic(optional = false)
    @Column(name = "DependencyDescription")
    private String dependencyDescription;
    @Basic(optional = false)
    @Column(name = "HardwareEnvironmental")
    private String hardwareEnvironmental;
    @Basic(optional = false)
    @Column(name = "SoftwareEnvironmental")
    private String softwareEnvironmental;
    @Basic(optional = false)
    @Column(name = "OtherEnvironmental")
    private String otherEnvironmental;
    @Basic(optional = false)
    @Column(name = "Inputs")
    private String inputs;
    @Basic(optional = false)
    @Column(name = "InputProcedures")
    private String inputProcedures;
    @Basic(optional = false)
    @Column(name = "Outputs")
    private String outputs;
    @Basic(optional = false)
    @Column(name = "OutputProcedures")
    private String outputProcedures;

    public C2CRIAppLayerTestCaseDescription() {
    }

    public C2CRIAppLayerTestCaseDescription(Integer id) {
        this.id = id;
    }

    public C2CRIAppLayerTestCaseDescription(Integer id, String testCaseName, String testCaseDescription, String itemList, String specialProcedureRequirements, String dependencyDescription, String hardwareEnvironmental, String softwareEnvironmental, String otherEnvironmental, String inputs, String inputProcedures, String outputs, String outputProcedures) {
        this.id = id;
        this.testCaseName = testCaseName;
        this.testCaseDescription = testCaseDescription;
        this.itemList = itemList;
        this.specialProcedureRequirements = specialProcedureRequirements;
        this.dependencyDescription = dependencyDescription;
        this.hardwareEnvironmental = hardwareEnvironmental;
        this.softwareEnvironmental = softwareEnvironmental;
        this.otherEnvironmental = otherEnvironmental;
        this.inputs = inputs;
        this.inputProcedures = inputProcedures;
        this.outputs = outputs;
        this.outputProcedures = outputProcedures;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public String getTestCaseDescription() {
        return testCaseDescription;
    }

    public void setTestCaseDescription(String testCaseDescription) {
        this.testCaseDescription = testCaseDescription;
    }

    public String getItemList() {
        return itemList;
    }

    public void setItemList(String itemList) {
        this.itemList = itemList;
    }

    public String getSpecialProcedureRequirements() {
        return specialProcedureRequirements;
    }

    public void setSpecialProcedureRequirements(String specialProcedureRequirements) {
        this.specialProcedureRequirements = specialProcedureRequirements;
    }

    public String getDependencyDescription() {
        return dependencyDescription;
    }

    public void setDependencyDescription(String dependencyDescription) {
        this.dependencyDescription = dependencyDescription;
    }

    public String getHardwareEnvironmental() {
        return hardwareEnvironmental;
    }

    public void setHardwareEnvironmental(String hardwareEnvironmental) {
        this.hardwareEnvironmental = hardwareEnvironmental;
    }

    public String getSoftwareEnvironmental() {
        return softwareEnvironmental;
    }

    public void setSoftwareEnvironmental(String softwareEnvironmental) {
        this.softwareEnvironmental = softwareEnvironmental;
    }

    public String getOtherEnvironmental() {
        return otherEnvironmental;
    }

    public void setOtherEnvironmental(String otherEnvironmental) {
        this.otherEnvironmental = otherEnvironmental;
    }

    public String getInputs() {
        return inputs;
    }

    public void setInputs(String inputs) {
        this.inputs = inputs;
    }

    public String getInputProcedures() {
        return inputProcedures;
    }

    public void setInputProcedures(String inputProcedures) {
        this.inputProcedures = inputProcedures;
    }

    public String getOutputs() {
        return outputs;
    }

    public void setOutputs(String outputs) {
        this.outputs = outputs;
    }

    public String getOutputProcedures() {
        return outputProcedures;
    }

    public void setOutputProcedures(String outputProcedures) {
        this.outputProcedures = outputProcedures;
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
        if (!(object instanceof C2CRIAppLayerTestCaseDescription)) {
            return false;
        }
        C2CRIAppLayerTestCaseDescription other = (C2CRIAppLayerTestCaseDescription) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "C2CRIAppLayerTestCaseDescriptions[ id=" + id + " ]";
    }

}
