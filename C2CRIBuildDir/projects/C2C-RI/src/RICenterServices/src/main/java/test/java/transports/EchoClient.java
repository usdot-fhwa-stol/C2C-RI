package test.transports;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.XMLLayout;
import org.fhwa.c2cri.java.net.ConnectionsDirectory;
import org.fhwa.c2cri.java.net.TrafficLogger;
public
class EchoClient {
   private static final Logger logger = Logger.getLogger(EchoClient.class);

   public
            static
    void
            main(String[] arstring) {

            FileAppender riAppender = new FileAppender();
            logger.addAppender(riAppender);
            riAppender.setName("STDOUT");
            riAppender.setFile("EchoClientFile");
            XMLLayout xmlLayout = new XMLLayout();
            riAppender.setLayout(xmlLayout);
            riAppender.setThreshold(Level.INFO);
            riAppender.setBufferedIO(true);
            riAppender.activateOptions();
       
        try {

            TrafficLogger tmpLogger = new TrafficLogger(){

                @Override
                public void log(TrafficLogger.LoggingLevel level, String trafficData) {
                      System.out.println("Logger:log!!!!!");
                      logger.info("\n" + trafficData);
                }
                
            };
            
            ConnectionsDirectory.getInstance().setTrafficLogger(tmpLogger);
//            ConnectionsDirectory.getInstance().setTestCaseName("EchoClient");
            
            System.setProperty("javax.net.ssl.trustStore", "c:\\inout\\sslTesting\\mySrvKeystore");
            System.setProperty("javax.net.ssl.trustStorePassword", "123456");
//            System.out.println("keyStore" + System.getProperty("javax.net.ssl.trustStore"));
//            System.out.println("password" + System.getProperty("javax.net.ssl.trustStorePassword"));


            org.fhwa.c2cri.applayer.ListenerManager.getInstance().setTestCaseID("EchoClient");
            org.fhwa.c2cri.applayer.ListenerManager.getInstance().createClientModeListener("127.0.0.1", 2224, false, "ECHO TEST LISTENER");
            
            SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
//////            int port = ListenerManager.getInstance().getListenerInternalServerPort("ECHO TEST LISTENER");
            int port = 2224;
            SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket("127.0.0.1", port);
//            SocketFactory sslsocketfactory = (SocketFactory) SocketFactory.getDefault();
//            int port = ListenerManager.getInstance().getListenerInternalServerPort("ECHO TEST LISTENER");
//            Socket sslsocket = (Socket) sslsocketfactory.createSocket("localhost", port);

//            InputStream inputstream = System.in;
//            InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
	    StringReader stringreader = new StringReader("Line1\nLine2\nLine3");
//            BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
            BufferedReader bufferedreader = new BufferedReader(stringreader);

            OutputStream outputstream = sslsocket.getOutputStream();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(outputstream);
            BufferedWriter bufferedwriter = new BufferedWriter(outputstreamwriter);

            String string = null;
            while ((string = bufferedreader.readLine()) != null) {
                bufferedwriter.write(string + '\n');
                bufferedwriter.flush();
            }
            
            bufferedwriter.close();
            bufferedreader.close();
            sslsocket.close();
            logger.removeAllAppenders();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        
            ListenerManager.getInstance().stopListener("ECHO TEST LISTENER");

    }
}
