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
 * The Class Need represents a single need as defined in the NRTM.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class Need implements Serializable {


    /** The unique id. */
    private int uniqueID;
    
    /** The official id. */
    private String officialID;
    
    /** The title. */
    @XmlAttribute
    private String title;
    
    /** The text. */
    private String text;
    
    /** The type. */
    @XmlAttribute(name="selected")
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
    
    /** The project requirements. */
    @XmlElement
    private ProjectRequirements projectRequirements = new ProjectRequirements();

    /**
     * Instantiates a new need.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private Need(){
        // For JAXB creation of XML
    }

    /**
     * Instantiates a new need.
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
    public Need(int uniqueID, String officialID, String title, String text, String type, String flagName, Boolean flagValue){
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
     * Adds the requirement.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param thisRequirement the this requirement
     */
    public void addRequirement(Requirement thisRequirement){
        projectRequirements.requirements.add(thisRequirement);
        projectRequirements.lh_requirementsMap.put(thisRequirement.getTitle(), thisRequirement);
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
     * Gets the project requirements.
     *
     * @return the project requirements
     */
    public ProjectRequirements getProjectRequirements() {
        return projectRequirements;
    }
    
//    public void setFlagName(String flagName) {
//        this.flagName = flagName;
//    }

}
