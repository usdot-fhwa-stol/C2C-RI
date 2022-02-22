/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.ftp;

import java.net.Socket;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.IODataConnection;
import org.apache.ftpserver.impl.ServerDataConnectionFactory;


/**
 * The Class RIIODataConnection.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class RIIODataConnection extends IODataConnection{

    /**
     * Instantiates a new RI IO data connection.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param socket the socket
     * @param session the session
     * @param factory the factory
     */
    public RIIODataConnection(final Socket socket, final FtpIoSession session,
            final ServerDataConnectionFactory factory) {
        super(socket,session,factory);
    }
}
