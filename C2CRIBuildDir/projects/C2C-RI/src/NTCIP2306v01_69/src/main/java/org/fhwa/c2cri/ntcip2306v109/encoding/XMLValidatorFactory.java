/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.encoding;

import java.util.ArrayList;

/**
 * A factory for creating XMLValidator objects.
 * @author TransCore ITS,LLC
 * Last Update:  9/30/2012
 */
public class XMLValidatorFactory {

    /** The Constant schemaReferences. */
    private static final ArrayList schemaReferences = new ArrayList();

    /**
     * Configure.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param schemaReferenceList the schema reference list
     */
    public void configure (ArrayList schemaReferenceList){
        this.schemaReferences.clear();
        for (Object thisObj : schemaReferenceList){
            this.schemaReferences.add(thisObj);
        }
    }


    /**
     * New xml validator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the xML validator
     */
    public XMLValidator newXMLValidator(){
        XMLValidator tmpValidator = new XMLValidator();
        tmpValidator.setSchemaReferenceList(schemaReferences);
        return tmpValidator;
    }

    /**
     * Gets the validator.
     *
     * @return the validator
     */
    public static XMLValidator getValidator(){
        XMLValidator tmpValidator = new XMLValidator();
        tmpValidator.setSchemaReferenceList(schemaReferences);
        return tmpValidator;        
    }
}
