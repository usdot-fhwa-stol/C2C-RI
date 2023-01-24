/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

/**
 *
 * @author TransCore ITS
 */
public interface TMDDSettings {
    public String getTMDD_SETTINGS_GROUP();

    public String getTMDD_PATH_DELAY_PARAMETER();

    public String getTMDD_PATH_DELAY_DEFAULT_VALUE();
    
    public String getTMDD_PERIODIC_UPDATE_BUFFER_PARAMETER();

    public String getTMDD_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE();

    public String getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER();

    public String getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE();

    public String getTMDD_AUTHENTICATIONUSERID();

    public String getTMDD_AUTHENTICATIONUSERID_DEFAULT_VALUE();

    public String getTMDD_AUTHENTICATIONPASSWORD();

    public String getTMDD_AUTHENTICATIONPASSWORD_DEFAULT_VALUE();

    public String getTMDD_AUTHENTICATIONOPERATORID();

    public String getTMDD_AUTHENTICATIONOPERATORID_DEFAULT_VALUE();

    public String getTMDD_USE_NILLABLE_SCHEMA_PARAMETER();

    public String getTMDD_USE_NILLABLE_SCHEMA_DEFAULT_VALUE();     
    
    public String getTMDD_CONTINUE_AFTER_MSG_VERIFY_FAILURE_PARAMETER();
    
    public String getTMDD_CONTINUE_AFTER_MSG_VERIFY_DEFAULT_VALUE();
}
