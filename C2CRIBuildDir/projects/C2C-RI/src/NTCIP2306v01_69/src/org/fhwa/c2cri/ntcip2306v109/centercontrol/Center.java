/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.centercontrol;


/**
 * Defines the Center Type
 * 
 * @author TransCore ITS, LLC
 * September 12, 2013
 */
public interface Center {
    /**
     * Represents an Owner or ExternalCenter
     */
    public static enum RICENTERMODE {
        /**
         * External Center
         */
        EC,
        /**
         * Owner Center
         */
        OC};
}
