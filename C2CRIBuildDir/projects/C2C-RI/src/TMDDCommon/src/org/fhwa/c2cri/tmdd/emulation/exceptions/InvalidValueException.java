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
public class InvalidValueException extends EntityEmulationException {

    public InvalidValueException(String message) {
        super(message);
    }
    public InvalidValueException(Throwable throwable){
        super(throwable);
    }
    
}
