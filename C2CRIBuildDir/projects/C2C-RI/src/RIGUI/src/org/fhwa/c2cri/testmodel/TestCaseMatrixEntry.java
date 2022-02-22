/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Class TestCaseMatrixEntry represents one entry in the TestCaseMatrix csv file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TestCaseMatrixEntry implements Serializable{
    
    /** The need id. */
    @XmlAttribute
    private String needID;
    
    /** The tc id. */
    @XmlAttribute
    private String tcID;
    
    /** The requirement id. */
    @XmlAttribute
    private String requirementID;
    
    /** The item type. */
    @XmlAttribute
    private String itemType;
    
    /** The precondition. */
    @XmlAttribute
    private String precondition;

    
    /**
     * Instantiates a new test case matrix entry.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private TestCaseMatrixEntry(){
        // For JAXB creation of XML
    }

    /**
     * Instantiates a new test case matrix entry.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param needID the need id
     * @param tcID the tc id
     * @param requirementID the requirement id
     * @param itemType the item type
     * @param precondition the precondition
     */
    public TestCaseMatrixEntry(String needID, String tcID, String requirementID, String itemType, String precondition) {
        this.needID = needID;
        this.tcID = tcID;
        this.requirementID = requirementID;
        this.itemType = itemType;
        this.precondition = precondition;
    }
    
    /**
     * Gets the item type.
     *
     * @return the item type
     */
    @XmlTransient
    public String getItemType() {
        return itemType;
    }

    /**
     * Sets the item type.
     *
     * @param itemType the new item type
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * Gets the need id.
     *
     * @return the need id
     */
    @XmlTransient
    public String getNeedID() {
        return needID;
    }

    /**
     * Sets the need id.
     *
     * @param needID the new need id
     */
    public void setNeedID(String needID) {
        this.needID = needID;
    }

    /**
     * Gets the precondition.
     *
     * @return the precondition
     */
    @XmlTransient
    public String getPrecondition() {
        return precondition;
    }

    /**
     * Sets the precondition.
     *
     * @param precondition the new precondition
     */
    public void setPrecondition(String precondition) {
        this.precondition = precondition;
    }

    /**
     * Gets the requirement id.
     *
     * @return the requirement id
     */
    @XmlTransient
    public String getRequirementID() {
        return requirementID;
    }

    /**
     * Sets the requirement id.
     *
     * @param requirementID the new requirement id
     */
    public void setRequirementID(String requirementID) {
        this.requirementID = requirementID;
    }

    /**
     * Gets the tc id.
     *
     * @return the tc id
     */
    @XmlTransient
    public String getTcID() {
        return tcID;
    }

    /**
     * Sets the tc id.
     *
     * @param tcID the new tc id
     */
    public void setTcID(String tcID) {
        this.tcID = tcID;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString(){
        return ("Entry Details: needID="+needID+
                "  requirementID="+ requirementID+
                "  tcID="+tcID+
                "  itemType="+ itemType+
                "  precondition="+precondition);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
        public boolean equals(Object other){
            if (other == this) return true;
            if (other==null) return false;
            if (getClass() != other.getClass()) return false;
            TestCaseMatrixEntry theEntry = (TestCaseMatrixEntry)other;
            return(needID.equals(theEntry.needID)&&
                   requirementID.equals(theEntry.requirementID)&&
                   itemType.equals(theEntry.itemType)&&
                   tcID.equals(theEntry.tcID)&&
                   precondition.equals(theEntry.precondition));
        }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.needID != null ? this.needID.hashCode() : 0);
        hash = 53 * hash + (this.tcID != null ? this.tcID.hashCode() : 0);
        hash = 53 * hash + (this.requirementID != null ? this.requirementID.hashCode() : 0);
        hash = 53 * hash + (this.itemType != null ? this.itemType.hashCode() : 0);
        hash = 53 * hash + (this.precondition != null ? this.precondition.hashCode() : 0);
        return hash;
    }

}
