/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testprocedures;

/**
 *
 * @author TransCore ITS
 */
public class MessageDetailDesignElement {

    private String message;
    private String elementName;
    private String dataConcept;
    private String dataConceptType;
    private String path;
    private String prefix;
    private String parentType;
    private String baseType;
    private String dataType;
    private String minOccurs;
    private String maxOccurs;
    private String minLength;
    private String maxLengh;
    private String minInclusive;
    private String maxInclusive;
    private String enumeration;
    private String valueStored;
    private String value;
    private String relatedRequirement;
    private String requirementID;
    private Integer requirementListID;
    private String needID;
    private Integer needListID;

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public String getDataConcept() {
        return dataConcept;
    }

    public void setDataConcept(String dataConcept) {
        this.dataConcept = dataConcept;
    }

    public String getDataConceptType() {
        return dataConceptType;
    }

    public void setDataConceptType(String dataConceptType) {
        this.dataConceptType = dataConceptType;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(String enumeration) {
        this.enumeration = enumeration;
    }

    public String getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(String maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public String getMaxLengh() {
        return maxLengh;
    }

    public void setMaxLengh(String maxLengh) {
        this.maxLengh = maxLengh;
    }

    public String getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(String maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(String minInclusive) {
        this.minInclusive = minInclusive;
    }

    public String getMinLength() {
        return minLength;
    }

    public void setMinLength(String minLength) {
        this.minLength = minLength;
    }

    public String getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(String minOccurs) {
        this.minOccurs = minOccurs;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueStored() {
        return valueStored;
    }

    public void setValueStored(String valueStored) {
        this.valueStored = valueStored;
    }

    public String getRelatedRequirement() {
        return relatedRequirement;
    }

    public String getRequirementID() {
        return requirementID;
    }

    public void setRequirementID(String requirementID) {
        this.requirementID = requirementID;
    }

    public Integer getRequirementListID() {
        return requirementListID;
    }

    public void setRequirementListID(Integer requirementListID) {
        this.requirementListID = requirementListID;
    }

    public String getNeedID() {
        return needID;
    }

    public void setNeedID(String needID) {
        this.needID = needID;
    }

    public Integer getNeedListID() {
        return needListID;
    }

    public void setNeedListID(Integer needListID) {
        this.needListID = needListID;
    }

    public void setRelatedRequirement(String relatedRequirement) {
        this.relatedRequirement = relatedRequirement;
    }

    
    public String getFullParentVariableName() {
        String returnString = getFullVariableName();

        String[] stringPortions = returnString.split("\\.");
        // Remove the last element
        if (stringPortions.length > 1) {
            returnString = returnString.substring(0, returnString.lastIndexOf(stringPortions[stringPortions.length - 1])-1);
        } else {
            return "";
        }

        return returnString;
    }

    public String getFullVariableName() {
        String returnString = this.path;
        // Trim off stuff before the namespace
        returnString = returnString.substring(returnString.indexOf(":")+1);

        // Trim off any [*] portions
        returnString = returnString.replace("[*]", "");
        // Replace "/" with "."
        returnString = returnString.replace("/", ".");


        return returnString;
    }
}
