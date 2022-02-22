/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109.http;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.annotation.ThreadSafe;

import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

/**
 * The Class NTCIP2306ClientConnectionManger.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
@ThreadSafe
public class NTCIP2306ClientConnectionManger extends SingleClientConnManager{
    /**
     * Creates a new simple connection manager.
     *
     * @param params    the parameters for this manager
     * @param schreg    the scheme registry, or
     *                  <code>null</code> for the default registry
     *
     * @deprecated use {@link SingleClientConnManager#SingleClientConnManager(SchemeRegistry)}
     */
//    @Deprecated
    public NTCIP2306ClientConnectionManger(HttpParams params,
                                   SchemeRegistry schreg) {
       super(params,schreg);

    }



    /**
 * Gets the local address.
 *
 * @return the local address
 */
    public String getLocalAddress(){
          return super.managedConn != null? super.managedConn.getLocalAddress().getHostAddress():"NA";
    }
    
    /**
     * Gets the local port.
     *
     * @return the local port
     */
    public String getLocalPort(){
          return super.managedConn != null? String.valueOf(super.managedConn.getLocalPort()):"";
    }
    
    /**
     * Gets the remote address.
     *
     * @return the remote address
     */
    public String getRemoteAddress(){
          return super.managedConn != null? super.managedConn.getRemoteAddress().getHostAddress():"NA";
    }
    
    /**
     * Gets the remote port.
     *
     * @return the remote port
     */
    public String getRemotePort(){
          return super.managedConn != null? String.valueOf(super.managedConn.getRemotePort()):"";
    }

}
