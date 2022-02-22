/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author TransCore ITS, LLC
 */
public class C2CRIServerSocket extends ServerSocket{
    
    public C2CRIServerSocket() throws IOException {       
    }

   
    public C2CRIServerSocket(int port) throws IOException {
        super(port);
    }
    
    @Override
    public Socket accept() throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (!isBound())
            throw new SocketException("Socket is not bound yet");
        
        Socket s = new Socket();
        implAccept(s);
        return s;
    }

//    @Override
//    public Socket accept() throws IOException {
//        return super.accept(); //To change body of generated methods, choose Tools | Templates.
//    }
    
    
}
