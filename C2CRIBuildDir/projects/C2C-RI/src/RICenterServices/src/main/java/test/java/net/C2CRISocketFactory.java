/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.net;

import java.net.SocketImpl;
import java.net.SocketImplFactory;

/**
 *
 * @author TransCore ITS, LLC
 */
public class C2CRISocketFactory implements SocketImplFactory{

    @Override
    public SocketImpl createSocketImpl() {
        try{
            return new C2CRISocketImpl();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
            
    }
    
}
