/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

import java.io.Serializable;

/**
 * The Class Predicate represents a predicate used in the NRTM to identify relationships between requirements.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class Predicate implements Serializable {



    /** The name. */
    private String name;
    
    /** The section. */
    private String section;
    
    /** The parent need. */
    private String parentNeed="";
    
    /** The global predicate. */
    private boolean globalPredicate=false;


    /**
     * Instantiates a new predicate.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param section the section
     */
    public Predicate( String name, String section){
        if (name.endsWith("*")){
            this.globalPredicate= true;
            this.name = name.substring(0, name.lastIndexOf("*"));            
        } else {
            this.name = name;            
        }
        this.section = section;
        
    }

    /**
     * Instantiates a new predicate.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param name the name
     * @param section the section
     * @param parentNeed the parent need
     */
    public Predicate( String name, String section, String parentNeed){
        if (name.endsWith("*")){
            this.globalPredicate= true;
            this.name = name.substring(0, name.lastIndexOf("*"));            
        } else {
            this.name = name;            
        }
        this.section = section;
        this.parentNeed = parentNeed;
    }
    
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the section.
     *
     * @return the section
     */
    public String getSection() {
        return section;
    }

    /**
     * Gets the parent need.
     *
     * @return the parent need
     */
    public String getParentNeed() {
        return parentNeed;
    }

    /**
     * Checks if is global predicate.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is global predicate
     */
    public boolean isGlobalPredicate() {
        return globalPredicate;
    }

}
