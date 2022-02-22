/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.centermodel.emulation.exceptions;

/**
 * This is the highest level Entity Emulation Exception class.
 * 
 * @author TransCore ITS, LLC
 * Created: Jan 31, 2016
 */
public class EntityEmulationException extends Exception {
    public EntityEmulationException(String message){
        super(message);
    }
    
    public EntityEmulationException(Throwable throwable){
        super(throwable);
    }
    
}
