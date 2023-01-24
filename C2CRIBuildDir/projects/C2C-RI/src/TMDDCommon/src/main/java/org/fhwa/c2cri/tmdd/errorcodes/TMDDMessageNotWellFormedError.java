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
public class TMDDMessageNotWellFormedError extends Exception {
    private int errorValue = 4;
    private String errorCode = "message is not well formed or cannot be parsed";
    public TMDDMessageNotWellFormedError(String message) {
        super(message);
    }
    public TMDDMessageNotWellFormedError(Throwable throwable){
        super(throwable);
    }

    public int getErrorValue() {
        return errorValue;
    }

    public String getErrorCode() {
        return errorCode;
    }
    
    
}
