/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.automation;

/**
 *
 * @author TransCore ITS, LLC
 */
public interface AutomationListener {
    public enum AutomationEventType{OWNER_CENTER_READY}
    
    /** Each listener will be notified when an event is encountered.  
     * 
     * @param eventType
     * @param message 
     */
    public void processEvent(AutomationEventType eventType, String message);
}
