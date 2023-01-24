/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.conn.OperatedClientConnection;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.conn.DefaultClientConnectionOperator;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;


/**
 * The Class C2CRIClientConnectionOperator.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class C2CRIClientConnectionOperator extends DefaultClientConnectionOperator {
    
    /**
     * Instantiates a new c2 cri client connection operator.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param schemes the schemes
     */
    public C2CRIClientConnectionOperator(SchemeRegistry schemes){
        super(schemes);
    }
    
    
    /**
     * Open connection.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param conn the conn
     * @param target the target
     * @param local the local
     * @param context the context
     * @param params the params
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void openConnection(OperatedClientConnection conn,
                               HttpHost target,
                               InetAddress local,
                               HttpContext context,
                               HttpParams params)
        throws IOException {

        if (conn == null) {
            throw new IllegalArgumentException
                ("Connection must not be null.");
        }
        if (target == null) {
            throw new IllegalArgumentException
                ("Target host must not be null.");
        }
        if (params == null) {
            throw new IllegalArgumentException
                ("Parameters must not be null.");
        }
        if (conn.isOpen()) {
            throw new IllegalArgumentException
                ("Connection must not be open.");
        }

        SocketFactory sf = null;
        LayeredSocketFactory layeredsf = null;
        
        Scheme schm = schemeRegistry.getScheme(target.getSchemeName());
        sf = schm.getSocketFactory();
        if (sf instanceof LayeredSocketFactory) {
            layeredsf = (LayeredSocketFactory) sf;
            sf = PlainSocketFactory.getSocketFactory();
        }

        InetAddress[] addresses = InetAddress.getAllByName(target.getHostName());
        for (int i = 0; i < addresses.length; i++) {
            InetAddress address = addresses[i];
            boolean last = i == addresses.length - 1;
            Socket sock = sf.createSocket();
            conn.opening(sock, target);
            try {

                if (layeredsf != null) {
                    Socket connsock = layeredsf.createSocket(
                            sock, 
                            target.getHostName(),
                            schm.resolvePort(target.getPort()),
                            true);
                    if (sock != connsock) {
                        sock = connsock;
                        conn.opening(sock, target);
                    }
                    sf = layeredsf;
                }
                prepareSocket(sock, context, params);
                conn.openCompleted(sf.isSecure(sock), params);
                break;
            } catch (ConnectException ex) {
                if (last) {
                    throw new HttpHostConnectException(target, ex);
                }
            } catch (ConnectTimeoutException ex) {
                if (last) {
                    throw ex;
                }
            }
        }
    }
    
}
