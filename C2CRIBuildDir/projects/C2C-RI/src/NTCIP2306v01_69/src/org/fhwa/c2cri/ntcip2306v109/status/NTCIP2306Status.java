/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.status;


/**
 * The Interface NTCIP2306Status specifies the various error types reported by NTCIP 2306.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public interface NTCIP2306Status {
    
    /**
     * The Enum TRANSPORTERRORTYPES.
     */
    public enum TRANSPORTERRORTYPES {
/** The none. */
NONE, 
 /** The socketconnecterror. */
 SOCKETCONNECTERROR, 
 /** The socketreceiveerror. */
 SOCKETRECEIVEERROR, 
 /** The internalservererror. */
 INTERNALSERVERERROR, 
 /** The datatransfererror. */
 DATATRANSFERERROR}
    
    /**
     * The Enum ENCODINGERRORTYPES.
     */
    public enum ENCODINGERRORTYPES {
/** The none. */
NONE, 
 /** The xmlerror. */
 XMLERROR, 
 /** The gziperror. */
 GZIPERROR, 
 /** The soaperror. */
 SOAPERROR, 
 /** The schemavalidationerror. */
 SCHEMAVALIDATIONERROR}
    
    /**
     * The Enum MESSAGEERRORTYPES.
     */
    public enum MESSAGEERRORTYPES {
/** The none. */
NONE, 
 /** The invalidmessage. */
 INVALIDMESSAGE}    
}
