/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

/**
 * The Interface UserNeedsInterface.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public abstract class UserNeedsInterface {
    
	private UserNeedsInterface()
	{
	}
	
    /** The Constant uniqueID_Header. */
    public final static String uniqueID_Header = "UniqueID";
    
    /** The Constant officialID_Header. */
    public final static String officialID_Header = "OfficialID";
    
    /** The Constant title_Header. */
    public final static String title_Header = "Title";
    
    /** The Constant text_Header. */
    public final static String text_Header = "Text";
    
    /** The Constant type_Header. */
    public final static String type_Header = "UN Selected";
    
    /** The Constant flagName_Header. */
    public final static String flagName_Header = "FlagName";
    
    /** The Constant flagValue_Header. */
    public final static String flagValue_Header = "FlagValue";

}
