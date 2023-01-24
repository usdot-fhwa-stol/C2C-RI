/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.net;

import java.net.SocketImplFactory;


/**
 *
 * @author Transcore ITS
 */
public class C2CRISSLSocketImplFactory implements SocketImplFactory {
    
    @Override
    public java.net.SocketImpl createSocketImpl() {
        try {
            return new C2CRISSLSocketImpl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
