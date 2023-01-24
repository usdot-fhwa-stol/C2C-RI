/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

/**
 * The Class TMDDSettingsImpl.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TMDDSettingsImpl {

    private static TMDDSettings settings = new DefaultTMDDSettingsImpl();

    public static void setTMDDSettings(TMDDSettings tmddSettings) {
        settings = tmddSettings;
    }

    public static String getTMDD_SETTINGS_GROUP() {
        return settings.getTMDD_SETTINGS_GROUP();
    }

    public static String getTMDD_PATH_DELAY_PARAMETER() {
        return settings.getTMDD_PATH_DELAY_PARAMETER();
    }

    public static String getTMDD_PATH_DELAY_DEFAULT_VALUE() {
        return settings.getTMDD_PATH_DELAY_DEFAULT_VALUE();
    }

    public static String getTMDD_PERIODIC_UPDATE_BUFFER_PARAMETER() {
        return settings.getTMDD_PERIODIC_UPDATE_BUFFER_PARAMETER();
    }

    public static String getTMDD_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE() {
        return settings.getTMDD_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE();
    }

    public static String getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER() {
        return settings.getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER();
    }

    public static String getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE() {
        return settings.getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE();
    }

    public static String getTMDD_AUTHENTICATIONUSERID() {
        return settings.getTMDD_AUTHENTICATIONUSERID();
    }

    public static String getTMDD_AUTHENTICATIONUSERID_DEFAULT_VALUE() {
        return settings.getTMDD_AUTHENTICATIONUSERID_DEFAULT_VALUE();
    }

    public static String getTMDD_AUTHENTICATIONPASSWORD() {
        return settings.getTMDD_AUTHENTICATIONPASSWORD();
    }

    public static String getTMDD_AUTHENTICATIONPASSWORD_DEFAULT_VALUE() {
        return settings.getTMDD_AUTHENTICATIONPASSWORD_DEFAULT_VALUE();
    }

    public static String getTMDD_AUTHENTICATIONOPERATORID() {
        return settings.getTMDD_AUTHENTICATIONOPERATORID();
    }

    public static String getTMDD_AUTHENTICATIONOPERATORID_DEFAULT_VALUE() {
        return settings.getTMDD_AUTHENTICATIONOPERATORID_DEFAULT_VALUE();
    }

    public static String getTMDD_USE_NILLABLE_SCHEMA_PARAMETER() {
        return settings.getTMDD_USE_NILLABLE_SCHEMA_PARAMETER();
    }

    public static String getTMDD_USE_NILLABLE_SCHEMA_DEFAULT_VALUE() {
        return settings.getTMDD_USE_NILLABLE_SCHEMA_DEFAULT_VALUE();
    }
    
    public static String getTMDD_CONTINUE_AFTER_MSG_VERIFY_FAILURE_PARAMETER(){
        return settings.getTMDD_CONTINUE_AFTER_MSG_VERIFY_FAILURE_PARAMETER();
    }
    
    public static String getTMDD_CONTINUE_AFTER_MSG_VERIFY_DEFAULT_VALUE(){
        return settings.getTMDD_CONTINUE_AFTER_MSG_VERIFY_DEFAULT_VALUE();
    }    
    
    /**
     * 
     */
    static class DefaultTMDDSettingsImpl implements TMDDSettings {

        @Override
        public String getTMDD_SETTINGS_GROUP() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_PATH_DELAY_PARAMETER() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_PATH_DELAY_DEFAULT_VALUE() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_PERIODIC_UPDATE_BUFFER_PARAMETER() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_PERIODIC_UPDATE_BUFFER_DEFAULT_VALUE() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_PARAMETER() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_THROW_ERROR_ON_MISMATCHED_FILTER_REQUEST_DEFAULT_VALUE() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_AUTHENTICATIONUSERID() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_AUTHENTICATIONUSERID_DEFAULT_VALUE() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_AUTHENTICATIONPASSWORD() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_AUTHENTICATIONPASSWORD_DEFAULT_VALUE() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_AUTHENTICATIONOPERATORID() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_AUTHENTICATIONOPERATORID_DEFAULT_VALUE() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_USE_NILLABLE_SCHEMA_PARAMETER() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_USE_NILLABLE_SCHEMA_DEFAULT_VALUE() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_CONTINUE_AFTER_MSG_VERIFY_FAILURE_PARAMETER() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public String getTMDD_CONTINUE_AFTER_MSG_VERIFY_DEFAULT_VALUE() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }

}
