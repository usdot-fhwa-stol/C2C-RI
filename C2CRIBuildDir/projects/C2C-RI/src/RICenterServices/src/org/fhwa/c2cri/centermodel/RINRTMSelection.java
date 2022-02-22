/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.centermodel;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Nov 30, 2016
 */
public class RINRTMSelection {
    private String needId;
    
    private String requirementId;
    
    private RINRTMSelection(){};
    
    public RINRTMSelection(String needID, String requirementID){
        this.needId = needID;
        this.requirementId = requirementID;
    }

    public String getNeedId() {
        return needId;
    }

    public String getRequirementId() {
        return requirementId;
    }    
    
}
