/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.infolayer;

/**
 * The Class MessageNRTMItem represents a single element within a message and indicates whether it is mandatory for the project or not.
 *
 * @author TransCore ITS
 */
public class MessageNRTMItem {

    /** The selected. */
    private boolean selected;
    
    /** The element. */
    private String element;
    
    /** The parent instance. */
    private String parentInstance;

    /**
     * Instantiates a new message nrtm item.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param selectionFlag the selection flag
     * @param element the element
     * @param parentInstance the parent instance
     */
    public MessageNRTMItem(boolean selectionFlag, String element, String parentInstance){
        this.selected = selectionFlag;
        this.element = element;
        this.parentInstance = parentInstance;
    }

    /**
     * Gets the element.
     *
     * @return the element
     */
    public String getElement() {
        return element;
    }

    /**
     * Sets the element.
     *
     * @param element the new element
     */
    public void setElement(String element) {
        this.element = element;
    }


    /**
     * Gets the parent instance.
     *
     * @return the parent instance
     */
    public String getParentInstance() {
        return parentInstance;
    }

    /**
     * Sets the parent instance.
     *
     * @param parentInstance the new parent instance
     */
    public void setParentInstance(String parentInstance) {
        this.parentInstance = parentInstance;
    }

    /**
     * Checks if is selected.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is selected
     */
    public boolean isSelected() {
        return selected;
    }

}
