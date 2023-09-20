/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Jan 11, 2014
 */
public class AutoTester {

    private boolean ownerCenterFlag;
    private String testCaseName;
    private boolean testStartedByOC;
    private static ExecutorService executor;
    public static AutoTester autoTester;
    public AutoTesterClient clientThread;
    public AutoTesterServer serverThread;
    public static String STARTCOMMAND = "START:";
    public static String ACKCOMMAND = "ACK:";
    public static String ENDCOMMAND = "END:";
    
    public static AutoTester getInstance(boolean isOwnerCenter){
        if (autoTester == null){
            autoTester = new AutoTester(isOwnerCenter);
            executor = Executors.newCachedThreadPool();
        }
        return autoTester;
    }
    
    public static AutoTester getInstance(){
        return getInstance(false);
    }
    
    private AutoTester(boolean isOwnerCenter){
        ownerCenterFlag = isOwnerCenter;
        
        if (ownerCenterFlag){
            AutoTesterClient clientThread = new AutoTesterClient();
            executor.submit(clientThread);
        } else{
            AutoTesterServer serverThread = new AutoTesterServer();            
            executor.submit(serverThread);
        }
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }

    public boolean isTestStartedByOC() {
        return testStartedByOC;
    }

    private void setTestStartedByOC(boolean testStartedByOC) {
        this.testStartedByOC = testStartedByOC;
    }
    
    public void startTest(){
		// original implementation did nothing
    }
    
    public void stopAutoTester(){
       executor.shutdownNow();
    }
            
    
    class AutoTesterClient implements Runnable {
        private Socket client;
        
        @Override
        public void run() {
            AutoTester.getInstance().setTestStartedByOC(true);

            
            AutoTester.getInstance().setTestStartedByOC(false);

        
        }
        
        public void sendTestStartCommand(){
            
        }
        
        public boolean isTestMessageSent(){
            boolean results = true;
            
            return results;
        }
    }
    
    class AutoTesterServer implements Runnable {
        private boolean oCSystemConnected;
        private boolean stopServer = false;
        private ServerSocket server;
        private Socket connection = null;
        private ObjectOutputStream output = null;
        private ObjectInputStream input = null;            
        
        @Override
        public void run() {
            boolean serverStarted = false;
            while (!serverStarted&&!stopServer){
                try{
                   Thread.sleep(500);
                   server = new ServerSocket(8083);
                   serverStarted = true;
                } catch (InterruptedException iex){
					Thread.currentThread().interrupt();
                    break;                
                } catch (Exception ex){
                    ex.printStackTrace();
                }
            }
            if (serverStarted){
                while (!stopServer){
                    try{
					if (connection == null)
						connection = server.accept();
					if (output == null)
						output = new ObjectOutputStream(connection.getOutputStream());
					if (input == null)
						input = new ObjectInputStream(connection.getInputStream());
                    

					try{
						String message = (String) input.readObject();

						if (message.startsWith(STARTCOMMAND)){
							AutoTester.getInstance().setTestStartedByOC(true);

						} else if (message.startsWith(ENDCOMMAND)){
							AutoTester.getInstance().setTestStartedByOC(false);

						}
					} catch (ClassNotFoundException classNotFoundException){

					}

                    

            
                    } catch (IOException iex){
                        stopServer = true;                        
                    }
                }
                try{
					if (output != null)
						output.close();
					if (input != null)
						input.close();
					if (connection != null)
						connection.close();
                } catch (IOException ioException){
                    ioException.printStackTrace();
                }
            }
        }
        
        public boolean isTestMessageReceived(){
            boolean results = true;
            
            return results;
        }
        
        public boolean isOCSystemConnected(){
            return oCSystemConnected;
        }
        
        public void stopServer(){
            stopServer = true;
        }
    }
}
