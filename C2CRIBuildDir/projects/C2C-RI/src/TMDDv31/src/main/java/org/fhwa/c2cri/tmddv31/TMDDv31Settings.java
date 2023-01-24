/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmddv31;

import org.fhwa.c2cri.tmdd.TMDDSettings;

/**
 * The Class TMDDv31Settings.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TMDDv31Settings implements TMDDSettings{
    
    /** The TMDDv31_ settings_ group. */
    public static String TMDDv31_SETTINGS_GROUP = "TMDDv31Settings";
    
    /** The TMDDv31_ path_ delay_ parameter. */
    public static String TMDDv31_PATH_DELAY_PARAMETER = "PathDelayConstantInMillis";
    
    /** The TMDDv31_path_ delay_ default_ value. */
    public static String TMDDv31_PATH_DELAY_DEFAULT_VALUE = "1000";     
    
    /** The TMDDv31_ periodic_ update_ buffer_ parameter. */
    public static String TMDDv31_PERIODIC_UPDATE_BUFFER_PARAMETER = "PeriodicPubBufferConstantPercentage";
    
    /** The TMDDv31_ periodic_ update_ buffer_ default_value. */
    public static String TMDDv31_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE = "5";     

    /** This parameter determines whether an error will be thrown if the response data does not support a requested filter type. */
    public static String TMDDv31_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER = "ThrowErrorOnMismatchedFilterRequest";     

    /** This parameter determines whether an error will be thrown if the response data does not support a requested filter type. */
    public static String TMDDv31_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE = "False";     

    /** The TMDDv31_AuthenticationUserID parameter. */
    public static String TMDDv31_AUTHENTICATIONUSERID = "AuthenticationUserID";

    /** The default value for the AuthenticationUserID parameter */
    public static String TMDDv31_AUTHENTICATIONUSERID_DEFAULT_VALUE = "UserId";
    
    /** The TMDDv31_AuthenticationPassword parameter. */
    public static String TMDDv31_AUTHENTICATIONPASSWORD = "AuthenticationPassword";

    /** The default value for the AuthenticationPassword parameter */
    public static String TMDDv31_AUTHENTICATIONPASSWORD_DEFAULT_VALUE = "P@ssW0rd";

    /** The TMDDv31_AuthenticationOperatorID parameter. */
    public static String TMDDv31_AUTHENTICATIONOPERATORID = "AuthenticationOperatorID";

    /** The default value for the AuthenticationPassword parameter */
    public static String TMDDv31_AUTHENTICATIONOPERATORID_DEFAULT_VALUE = "StateDOT_OPRef";
 
    /** This parameter determines whether an the nillable version of the TMDD schema will be used in testing. */
    public static String TMDDv31_USE_NILLABLE_SCHEMA_PARAMETER = "UseNillableTMDDSchema";     

    /** This parameter determines whether an the nillable version of the TMDD schema will be used in testing. */
    public static String TMDDv31_USE_NILLABLE_SCHEMA_DEFAULT_VALUE = "False";     

    /** The TMDDv31 Continue after Message Verification Failure parameter. */ 
    public static String TMDDv31_CONTINUE_AFTER_MSG_VERIFY_FAILURE_PARAMETER = "ContinueAfterMsgVerifyFailure";
    
    @Override
    public String getTMDD_SETTINGS_GROUP() {
        return TMDDv31_SETTINGS_GROUP;
    }

    @Override
    public String getTMDD_PATH_DELAY_PARAMETER() {
        return TMDDv31_PATH_DELAY_PARAMETER;
    }

    @Override
    public String getTMDD_PATH_DELAY_DEFAULT_VALUE() {
        return TMDDv31_PATH_DELAY_DEFAULT_VALUE;
    }

    @Override
    public String getTMDD_PERIODIC_UPDATE_BUFFER_PARAMETER() {
        return TMDDv31_PERIODIC_UPDATE_BUFFER_PARAMETER;
    }

    @Override
    public String getTMDD_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE() {
        return TMDDv31_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE;
    }

    @Override
    public String getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER() {
        return TMDDv31_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER;
    }

    @Override
    public String getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE() {
        return TMDDv31_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE;
    }

    @Override
    public String getTMDD_AUTHENTICATIONUSERID() {
        return TMDDv31_AUTHENTICATIONUSERID;
    }

    @Override
    public String getTMDD_AUTHENTICATIONUSERID_DEFAULT_VALUE() {
        return TMDDv31_AUTHENTICATIONUSERID_DEFAULT_VALUE;
    }

    @Override
    public String getTMDD_AUTHENTICATIONPASSWORD() {
        return TMDDv31_AUTHENTICATIONPASSWORD;
    }

    @Override
    public String getTMDD_AUTHENTICATIONPASSWORD_DEFAULT_VALUE() {
        return TMDDv31_AUTHENTICATIONPASSWORD_DEFAULT_VALUE;
    }

    @Override
    public String getTMDD_AUTHENTICATIONOPERATORID() {
        return TMDDv31_AUTHENTICATIONOPERATORID;
    }

    @Override
    public String getTMDD_AUTHENTICATIONOPERATORID_DEFAULT_VALUE() {
        return TMDDv31_AUTHENTICATIONOPERATORID_DEFAULT_VALUE;
    }

    @Override
    public String getTMDD_USE_NILLABLE_SCHEMA_PARAMETER() {
        return TMDDv31_USE_NILLABLE_SCHEMA_PARAMETER;
    }

    @Override
    public String getTMDD_USE_NILLABLE_SCHEMA_DEFAULT_VALUE() {
        return TMDDv31_USE_NILLABLE_SCHEMA_DEFAULT_VALUE;
    }
    
    @Override
    public String getTMDD_CONTINUE_AFTER_MSG_VERIFY_FAILURE_PARAMETER() {
        return TMDDv31_CONTINUE_AFTER_MSG_VERIFY_FAILURE_PARAMETER;
    }

    @Override
    public String getTMDD_CONTINUE_AFTER_MSG_VERIFY_DEFAULT_VALUE() {
        return "false"; 
    }
    
}
