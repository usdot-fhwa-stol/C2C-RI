/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.java.net;

/**
 * The Interface TrafficLogger.
 *
 * @author TransCore ITS, LLC
 * Last Updated: 10/14/2013
 */
public interface TrafficLogger {
    
    /**
     * The Enum LoggingLevel.
     */
    public enum LoggingLevel{
/** The debug level. */
DEBUG, 
 /** The info level. */
 INFO, 
 /** The trace level. */
 TRACE}
    
    /**
     * Log.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param level the level
     * @param trafficData the traffic data
     */
    public void log(LoggingLevel level, String trafficData);
    
}
