package test.transports;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.fhwa.c2cri.java.net.ConnectionsDirectory;
import org.fhwa.c2cri.java.net.TrafficLogger;

public
class EchoServer {
   private static final Logger logger = LogManager.getLogger(EchoServer.class);
    
    public
            static
    void
            main(String[] arstring) {
        

 
        
        
        try {
            TrafficLogger tmpLogger = new TrafficLogger(){

                @Override
                public void log(TrafficLogger.LoggingLevel level, String trafficData) {
                    System.out.println("Logger:log!!!!!");
                    logger.info("\n" + trafficData);
                }
                
            };
            
            ConnectionsDirectory.getInstance().setTrafficLogger(tmpLogger);
//            ConnectionsDirectory.getInstance().setTestCaseName("EchoServer");
            System.setProperty("javax.net.ssl.keyStore", "c:\\inout\\sslTesting\\mySrvKeystore");
            System.setProperty("javax.net.ssl.keyStorePassword", "123456");
//            System.out.println("keyStore " + System.getProperty("javax.net.ssl.keyStore"));
//            System.out.println("password " + System.getProperty("javax.net.ssl.keyStorePassword"));
            org.fhwa.c2cri.applayer.ListenerManager.getInstance().setTestCaseID("EchoServer");
            org.fhwa.c2cri.applayer.ListenerManager.getInstance().createServerModeListener("127.0.0.1", 2224, "127.0.0.1", 2224, false, "ECHO SERVER LISTENER");
       //      SocketImplFactory socketImplFactory = new C2CRISocketImplFactory();
       //      Socket.setSocketImplFactory(socketImplFactory);
       //      ServerSocket.setSocketFactory(socketImplFactory);
//             SSLSocket.setSocketImplFactory(new C2CRISocketImplFactory());
//             SSLServerSocket.setSocketFactory(new C2CRISocketImplFactory());
             //             System.setProperty( "ssl.SocketFactory.provider", "test.net.C2CRISocketImpl" );             
            Integer internalPort = 2224;
            
            SSLServerSocketFactory sslserversocketfactory =
                    (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            try (SSLServerSocket sslserversocket =
                    (SSLServerSocket) sslserversocketfactory.createServerSocket(internalPort))
			{
				SSLSocket sslsocket = (SSLSocket) sslserversocket.accept();
	//           ServerSocketFactory sslserversocketfactory =
	 //                   (ServerSocketFactory) ServerSocketFactory.getDefault();
	//            SocketFactory tmpFactory = (SocketFactory)SocketFactory.getDefault();
	//            Socket tmpSocket = tmpFactory.createSocket("localhost", internalPort);
	 //           ServerSocket sslserversocket = 
	 //                   (ServerSocket) sslserversocketfactory.createServerSocket(internalPort);

	 //           Socket sslsocket = (Socket) sslserversocket.accept();
				try (BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(sslsocket.getInputStream())))
				{

					String string = null;
					while ((string = bufferedreader.readLine()) != null) {
						System.out.println(string);
						System.out.flush();

					}
				}
			}
        } catch (Exception exception) {
            exception.printStackTrace();
        }
            ListenerManager.getInstance().stopListener("ECHO SERVER LISTENER");

    }
}
