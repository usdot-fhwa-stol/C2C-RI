/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

/**
 * The Interface PredicatesInterface defines the headers required in a Predicates.csv file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public abstract class PredicatesInterface {
	
	private PredicatesInterface()
	{
	}
    
    /** The Constant Predicate_Header. */
    public final static String Predicate_Header = "Predicate";
    
    /** The Constant Section_Header. */
    public final static String Section_Header = "Section";
    
    /** The Constant ParentNeed_Header. */
    public final static String ParentNeed_Header = "ParentNeed";

}
