/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.tmdd.errorcodes;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Mar 26, 2016
 */
public class TMDDUnknownProcessingError extends Exception {
    private int errorValue = 1;
    private String errorCode = "unknown processing error";
    public TMDDUnknownProcessingError(String message) {
        super(message);
    }
    public TMDDUnknownProcessingError(Throwable throwable){
        super(throwable);
    }

    public int getErrorValue() {
        return errorValue;
    }

    public String getErrorCode() {
        return errorCode;
    }
    
    
}
