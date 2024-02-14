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
public abstract class Parameter {

	private Parameter()
	{
	}
	
    /** The config_file_path. */
    public static String config_file_path = "Config_File_Path";
    
    /** The default_config_file_path. */
    public static String default_config_file_path = "./";

    /** The log_file_path. */
    public static String log_file_path = "Log_File_Path";
    
    /** The default_log_file_path. */
    public static String default_log_file_path = "./";

    /** The custom_test_suites_ path. */
    public static String custom_test_suites_Path = "Custom_Test_Suites_Path";
    
    /** The default_test_suites_ path. */
    public static String default_test_suites_Path = "./";

    /** The report_file_ path. */
    public static String report_file_Path = "Report_File_Path";
    
    /** The default_report_file_ path. */
    public static String default_report_file_Path = "./";
}
