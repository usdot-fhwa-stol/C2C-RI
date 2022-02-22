/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The Class OtherRequirement an additional constraint added to a requirement in the NRTM.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class OtherRequirement implements Serializable{
    
    /** The req id. */
    @XmlAttribute
    private String reqID;
    
    /** The other requirement. */
    @XmlAttribute
    private String otherRequirement;
    
    /** The value. */
    @XmlAttribute
    private String value;
    
    /** The value name. */
    @XmlAttribute
    private String valueName;
    
    /** The minimum. */
    private int minimum;
    
    /** The maximum. */
    private int maximum;
    
    /** The type. */
    private String type;

    /**
     * Instantiates a new other requirement.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    private OtherRequirement(){
        // For JAXB creation of XML
    }

    /**
     * Instantiates a new other requirement.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param reqID the req id
     * @param otherRequirement the other requirement
     * @param value the value
     * @param valueName the value name
     * @param minimum the minimum
     * @param maximum the maximum
     * @param type the type
     */
    public OtherRequirement(String reqID, String otherRequirement, String value, String valueName, int minimum, int maximum, String type) {
        this.reqID = reqID;
        this.otherRequirement = otherRequirement;
        this.value = value;
        this.valueName = valueName;
        this.type = type;
        this.minimum = minimum;
        this.maximum = maximum;

    }

    /**
     * Gets the maximum.
     *
     * @return the maximum
     */
    public int getMaximum() {
        return maximum;
    }

    /**
     * Gets the minimum.
     *
     * @return the minimum
     */
    public int getMinimum() {
        return minimum;
    }

    /**
     * Gets the other requirement.
     *
     * @return the other requirement
     */
    public String getOtherRequirement() {
        return otherRequirement;
    }

    /**
     * Gets the req id.
     *
     * @return the req id
     */
    public String getReqID() {
        return reqID;
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
     * Gets the value.
     *
     * @return the value
     */
    @XmlTransient
    public String getValue() {
        return value;
    }

    /**
     * Gets the value name.
     *
     * @return the value name
     */
    public String getValueName() {
        return valueName;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }


}
