/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.utilities;

/**
 * The Interface Parameter stores a set of constants for the RI.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 9/2/2012
 */
public interface Parameter {

    /** The config_file_path. */
    static String config_file_path = "Config_File_Path";
    
    /** The default_config_file_path. */
    static String default_config_file_path = "./";

    /** The log_file_path. */
    static String log_file_path = "Log_File_Path";
    
    /** The default_log_file_path. */
    static String default_log_file_path = "./";

    /** The custom_test_suites_ path. */
    static String custom_test_suites_Path = "Custom_Test_Suites_Path";
    
    /** The default_test_suites_ path. */
    static String default_test_suites_Path = "./";

    /** The report_file_ path. */
    static String report_file_Path = "Report_File_Path";
    
    /** The default_report_file_ path. */
    static String default_report_file_Path = "./";
}
