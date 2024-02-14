/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

/**
 * The Interface TestCaseMatrixInterface defines the headers required for the TestCaseMatrix.csv file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public abstract class TestCaseMatrixInterface {
    
	private TestCaseMatrixInterface()
	{
	}
	
    /** The Constant NEEDID_Header. */
    public final static String NEEDID_Header = "NeedID";
    
    /** The Constant REQUIREMENTID_Header. */
    public final static String REQUIREMENTID_Header = "RequirementID";
    
    /** The Constant TCID_Header. */
    public final static String TCID_Header = "TCID";
    
    /** The Constant ITEMTYPE_Header. */
    public final static String ITEMTYPE_Header = "ItemType";
    
    /** The Constant PRECONDITION_Header. */
    public final static String PRECONDITION_Header = "Precondition";

}
