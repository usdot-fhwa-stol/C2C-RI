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
public class C2CRISocketImplFactory implements SocketImplFactory {
    
    @Override
    public java.net.SocketImpl createSocketImpl() {
        try {
            return new C2CRISocketImpl();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
