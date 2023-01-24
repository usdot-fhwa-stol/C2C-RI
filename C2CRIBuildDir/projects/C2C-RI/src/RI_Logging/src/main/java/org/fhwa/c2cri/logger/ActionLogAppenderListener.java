/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.logger;

/**
 * Provides an update method for Action Logging.
 * @author TransCore ITS, LLC
 */
public interface ActionLogAppenderListener {
    public void appenderUpdate(long timestamp, String message);
}
