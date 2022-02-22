/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Class Requirement represents the definition of a single requirement in a standard or project specification.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class Requirement implements Serializable{
    
    /** The unique id. */
    private int uniqueID;
    
    /** The official id. */
    private String officialID;
    
    /** The title. */
    @XmlAttribute
    private String title;
    
    /** The text. */
    @XmlAttribute
    private String text;
    
    /** The type. */
    @XmlAttribute(name="conformance")
    private String type;
    
    /** The flag name. */
    @XmlAttribute
    private String flagName;
    
    /** The flag value. */
    @XmlAttribute
    private Boolean flagValue;
    
    /** The extension. */
    @XmlAttribute
    private Boolean extension=false;
    
    /** The other requirements. */
    @XmlElement
    private OtherRequirements otherRequirements = new OtherRequirements();

    /**
     * Instantiates a new requirement.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private Requirement(){
        // For JAXB creation of XML
    }

    /**
     * Instantiates a new requirement.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param uniqueID the unique id
     * @param officialID the official id
     * @param title the title
     * @param text the text
     * @param type the type
     * @param flagName the flag name
     * @param flagValue the flag value
     */
    public Requirement(int uniqueID, String officialID, String title, String text, String type, String flagName, Boolean flagValue) {
        this.uniqueID = uniqueID;
        this.officialID = officialID;
        this.title = title;
        this.text = text;
        this.type = type;
        this.flagName = flagName;
        this.flagValue = flagValue;
    }


    /**
     * Gets the flag name.
     *
     * @return the flag name
     */
    public String getFlagName() {
        return flagName;
    }
    
    /**
     * Gets the flag value.
     *
     * @return the flag value
     */
    @XmlTransient
    public Boolean getFlagValue() {
        return flagValue;
    }

    /**
     * Gets the official id.
     *
     * @return the official id
     */
    public String getOfficialID() {
        return officialID;
    }

    /**
     * Gets the text.
     *
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * Gets the title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the unique id.
     *
     * @return the unique id
     */
    public int getUniqueID() {
        return uniqueID;
    }
    
    /**
     * Sets the flag value.
     *
     * @param flagValue the new flag value
     */
    public void setFlagValue(Boolean flagValue) {
        this.flagValue = flagValue;
    }

    /**
     * Checks if is extension.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the boolean
     */
    @XmlTransient
    public Boolean isExtension() {
        return extension;
    }

    /**
     * Sets the extension.
     *
     * @param extension the new extension
     */
    public void setExtension(Boolean extension) {
        this.extension = extension;
    }

    /**
     * Adds the other requirement.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param thisOtherRequirement the this other requirement
     */
    public void addOtherRequirement(OtherRequirement thisOtherRequirement){
        otherRequirements.otherRequirements.add(thisOtherRequirement);
    }
    
    /**
     * Gets the extension.
     *
     * @return the extension
     */
    @XmlTransient
    public Boolean getExtension() {
        return extension;
    }

    /**
     * Gets the other requirements.
     *
     * @return the other requirements
     */
    public OtherRequirements getOtherRequirements() {
        return otherRequirements;
    }

}
