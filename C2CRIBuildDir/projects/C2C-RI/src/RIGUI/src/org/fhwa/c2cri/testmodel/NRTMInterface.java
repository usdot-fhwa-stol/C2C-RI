/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel;

/**
 * The Interface NRTMInterface defines the fields that are required within the NRTM.csv file.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public abstract class NRTMInterface {
    
	private NRTMInterface()
	{
	}
	
    /** The Constant ID_HEADER. */
    public final static String ID_HEADER = "ID";
    
    /** The Constant UN_ID_HEADER. */
    public final static String UN_ID_HEADER="UN ID";
    
    /** The Constant USER_NEED_HEADER. */
    public final static String USER_NEED_HEADER ="User Need";
    
    /** The Constant NEEDCONFORMANCE_HEADER. */
    public final static String NEEDCONFORMANCE_HEADER="NeedConformance";
    
    /** The Constant NEEDSUPPORT_HEADER. */
    public final static String NEEDSUPPORT_HEADER="NeedSupport";
    
    /** The Constant REQUIREMENT_ID_HEADER. */
    public final static String REQUIREMENT_ID_HEADER="Requirement ID";
    
    /** The Constant REQUIREMENT_HEADER. */
    public final static String REQUIREMENT_HEADER="Requirement";
    
    /** The Constant CONFORMANCE_HEADER. */
    public final static String CONFORMANCE_HEADER="Conformance";
    
    /** The Constant SUPPORT_HEADER. */
    public final static String SUPPORT_HEADER="Support";
    
    /** The Constant OTHER_REQUIREMENTS_HEADER. */
    public final static String OTHER_REQUIREMENTS_HEADER="Other Requirements";
    
    /** The Constant NEEDFLAG_HEADER. */
    public final static String NEEDFLAG_HEADER="NeedFlag";
    
    /** The Constant NEEDFLAGVALUE_HEADER. */
    public final static String NEEDFLAGVALUE_HEADER="NeedFlagValue";
    
    /** The Constant REQFLAG_HEADER. */
    public final static String REQFLAG_HEADER="ReqFlag";
    
    /** The Constant REQFLAGVALUE_HEADER. */
    public final static String REQFLAGVALUE_HEADER="ReqFlagValue";
    
    /** The Constant OTHERREQPARAMETER_HEADER. */
    public final static String OTHERREQPARAMETER_HEADER="OtherReqParameter";
    
    /** The Constant OTHERREQUIREMENTVALUES_HEADER. */
    public final static String OTHERREQUIREMENTVALUES_HEADER="OtherRequirementValues";

}
