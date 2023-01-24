/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.tmdd.emulation.exceptions;

import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Jan 31, 2016
 */
public class DuplicateEntityIdException extends EntityEmulationException{
    
    public DuplicateEntityIdException(String message){
        super(message);
    }

    
    public DuplicateEntityIdException(Throwable throwable){
        super(throwable);
    }
    
}
