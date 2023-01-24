/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.tmdd.emulation;

import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Feb 13, 2016
 */
public abstract class TMDDControlRequest {

    private MessageSpecification controlRequest;
    
    private String requestStatus;
    
    public final void store() throws EntityEmulationException{
        if (controlRequest != null){
            
            
        }
    }
    
    public final void load(String requestId){
        
    }
    
    public final void generateResponse(){
        
    }
    
    public abstract void verifyRequest();
    public abstract void executeControlCommands();
    public abstract void cancelControl();
    public abstract void getControlStatus();
}
