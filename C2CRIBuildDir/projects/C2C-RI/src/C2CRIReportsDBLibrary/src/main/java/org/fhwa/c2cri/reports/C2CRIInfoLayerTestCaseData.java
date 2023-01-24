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
 * Captures all information related to information layer test case data. 
 * 
 * @author TransCore ITS, LLC
 * Created: Oct 27, 2016
 */
@Entity
@Table(name = "C2CRI_InfoLayerTestCaseData")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findAll", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findById", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.id = :id"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findByStandard", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.standard = :standard"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findByTestCase", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.testCase = :testCase"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findByIteration", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.iteration = :iteration"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findByVariableName", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.variableName = :variableName"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findByDescription", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.description = :description"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findByDataType", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.dataType = :dataType"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findByVariableValue", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.variableValue = :variableValue"),
    @NamedQuery(name = "C2CRIInfoLayerTestCaseData.findByValidValues", query = "SELECT c FROM C2CRIInfoLayerTestCaseData c WHERE c.validValues = :validValues")})
public class C2CRIInfoLayerTestCaseData implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "Standard")
    private String standard;
    @Basic(optional = false)
    @Column(name = "TestCase")
    private String testCase;
    @Basic(optional = false)
    @Column(name = "Iteration")
    private String iteration;
    @Basic(optional = false)
    @Column(name = "VariableName")
    private String variableName;
    @Basic(optional = false)
    @Column(name = "Description")
    private String description;
    @Basic(optional = false)
    @Column(name = "DataType")
    private String dataType;
    @Basic(optional = false)
    @Column(name = "VariableValue")
    private String variableValue;
    @Basic(optional = false)
    @Column(name = "ValidValues")
    private String validValues;
    @Basic(optional = false)
    @Column(name = "TestCaseIndex")
    private Integer testCaseIndex;

    public C2CRIInfoLayerTestCaseData() {
    }

    public C2CRIInfoLayerTestCaseData(Integer id) {
        this.id = id;
    }

    public C2CRIInfoLayerTestCaseData(Integer id, String standard, String testCase, String iteration, String variableName, String description, String dataType, String variableValue, String validValues, Integer testCaseIndex) {
        this.id = id;
        this.standard = standard;
        this.testCase = testCase;
        this.iteration = iteration;
        this.variableName = variableName;
        this.description = description;
        this.dataType = dataType;
        this.variableValue = variableValue;
        this.validValues = validValues;
        this.testCaseIndex = testCaseIndex;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getTestCase() {
        return testCase;
    }

    public void setTestCase(String testCase) {
        this.testCase = testCase;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    public String getValidValues() {
        return validValues;
    }

    public void setValidValues(String validValues) {
        this.validValues = validValues;
    }

    public Integer getTestCaseIndex() {
        return testCaseIndex;
    }

    public void setTestCaseIndex(Integer testCaseIndex) {
        this.testCaseIndex = testCaseIndex;
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
        if (!(object instanceof C2CRIInfoLayerTestCaseData)) {
            return false;
        }
        C2CRIInfoLayerTestCaseData other = (C2CRIInfoLayerTestCaseData) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "C2CRIInfoLayerTestCaseData[ id=" + id + " ]";
    }

}
