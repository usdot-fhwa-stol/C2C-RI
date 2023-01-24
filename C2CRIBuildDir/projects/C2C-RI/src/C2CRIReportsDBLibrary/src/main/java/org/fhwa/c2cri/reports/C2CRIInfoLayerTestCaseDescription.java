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
 * Captures all information related to information layer test case descriptions.
 * 
 * @author TransCore ITS, LLC
 * Created: Oct 14, 2016
 */
@Entity
@Table(name = "C2CRI_InfoLayerTestCaseDescriptions")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findAll", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findById", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.id = :id"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByTestCaseName", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.testCaseName = :testCaseName"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByTestCaseDescription", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.testCaseDescription = :testCaseDescription"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByItemList", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.itemList = :itemList"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findBySpecialProcedureRequirements", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.specialProcedureRequirements = :specialProcedureRequirements"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByDependencyDescription", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.dependencyDescription = :dependencyDescription"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByHardwareEnvironmental", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.hardwareEnvironmental = :hardwareEnvironmental"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findBySoftwareEnvironmental", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.softwareEnvironmental = :softwareEnvironmental"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByOtherEnvironmental", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.otherEnvironmental = :otherEnvironmental"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByInputs", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.inputs = :inputs"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByInputProcedures", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.inputProcedures = :inputProcedures"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByOutputs", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.outputs = :outputs"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseDescriptions.findByOutputProcedures", query = "SELECT c FROM C2CRIInfoLayerTestCaseDescriptions c WHERE c.outputProcedures = :outputProcedures")})
public class C2CRIInfoLayerTestCaseDescription implements Serializable {

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

    public C2CRIInfoLayerTestCaseDescription() {
    }

    public C2CRIInfoLayerTestCaseDescription(Integer id) {
        this.id = id;
    }

    public C2CRIInfoLayerTestCaseDescription(Integer id, String testCaseName, String testCaseDescription, String itemList, String specialProcedureRequirements, String dependencyDescription, String hardwareEnvironmental, String softwareEnvironmental, String otherEnvironmental, String inputs, String inputProcedures, String outputs, String outputProcedures) {
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
        if (!(object instanceof C2CRIInfoLayerTestCaseDescription)) {
            return false;
        }
        C2CRIInfoLayerTestCaseDescription other = (C2CRIInfoLayerTestCaseDescription) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "C2CRIInfoLayerTestCaseDescriptions[ id=" + id + " ]";
    }

}
