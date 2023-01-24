/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.automation;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.fhwa.c2cri.automation.AutomationListener.AutomationEventType;

/**
 *
 * @author TransCore ITS, LLC
 */
public class AutomationHandler {

    private final List<AutomationListener> automationListeners = new CopyOnWriteArrayList<AutomationListener>();
    private static AutomationHandler automationHandler;
    
    private AutomationHandler(){
    }
    
    public static AutomationHandler getInstance(){
        if (automationHandler == null){
            automationHandler = new AutomationHandler();
        }
        return automationHandler;
    }
    
    /**
     * Add the listener to the subscriber list
     * @param listener 
     */
    public synchronized void addAutomationListener(AutomationListener listener){
        automationListeners.add(listener);
    }

    /**
     * Remove the listener from the subscriber list
     * @param listener 
     */
    public synchronized void removeAutomationListener(AutomationListener listener){
        automationListeners.remove(listener);
    }

    /**
     * Remove all the listeners from the subscriber list
     */
    public synchronized void clearAllListeners(){
        automationListeners.clear();
    }
    
    /**
     * Publish the update to all of the current subscribers.
     * @param subType
     * @param deviceIds
     * @param updateTime 
     */
    public synchronized void publishAutomationUpdate(AutomationEventType subType, String message){
        for (AutomationListener thisListener : automationListeners){
            try{
               thisListener.processEvent(subType, message);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }    
}
