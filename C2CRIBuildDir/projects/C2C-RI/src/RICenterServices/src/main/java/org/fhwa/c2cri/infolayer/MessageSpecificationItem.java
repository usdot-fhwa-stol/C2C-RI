/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.infolayer;

/**
 * The Class MessageSpecificationItem represents one element within a message.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MessageSpecificationItem {

    /** The value name. */
    private String valueName;
    
    /** The value. */
    private String value;
    
    /** The project optional. */
    private boolean projectOptional = false;

    /**
     * Instantiates a new message specification item.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param valueSpecEntry the value spec entry
     * @throws Exception the exception
     */
    public MessageSpecificationItem(String valueSpecEntry) throws Exception{
        if (valueSpecEntry.contains("=")){
            int index = valueSpecEntry.indexOf("=");
            valueName = valueSpecEntry.substring(0, index).trim();
            value = valueSpecEntry.substring(index+1).trim();
            
        } else {
            throw new Exception (valueSpecEntry + " is not valid.  (Missing =)");
        }
        
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
     * Sets the value name.
     *
     * @param valueName the new value name
     */
    public void setValueName(String valueName) {
        this.valueName = valueName;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Checks if is project optional.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is project optional
     */
    public boolean isProjectOptional() {
        return projectOptional;
    }

    /**
     * Sets the project optional.
     *
     * @param projectOptional the new project optional
     */
    public void setProjectOptional(boolean projectOptional) {
        this.projectOptional = projectOptional;
    }


}
