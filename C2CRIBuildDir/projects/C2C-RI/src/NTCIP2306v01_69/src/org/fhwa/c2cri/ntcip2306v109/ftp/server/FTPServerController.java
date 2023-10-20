/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.ftp.server;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.log4j.Logger;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authentication;
import org.apache.ftpserver.ftplet.AuthenticationFailedException;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.ftplet.User;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.Listener;
import org.apache.ftpserver.usermanager.AnonymousAuthentication;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.PasswordEncryptor;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.UserManagerFactory;
import org.apache.ftpserver.usermanager.UsernamePasswordAuthentication;
import org.apache.ftpserver.usermanager.impl.AbstractUserManager;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.ConcurrentLoginPermission;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import org.fhwa.c2cri.ntcip2306v109.ftp.RIFtpHandler;
import org.fhwa.c2cri.ntcip2306v109.ftp.RIFtplet;
import org.fhwa.c2cri.ntcip2306v109.ftp.RIListenerFactory;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.ServiceSpecCollection;
import org.fhwa.c2cri.utilities.RIParameters;


/**
 *
 * @author TransCore ITS, LLC
 */
public class FTPServerController implements Runnable{
    private FtpServer server;
    private Listener listener;
    private RIFtplet riFtplet;
    protected static Logger log = Logger.getLogger(FTPServerController.class.getName());
    private final CountDownLatch readySignal;

    QueueController queueController;
    ServiceSpecCollection services = new ServiceSpecCollection();
    private String username = "riftp";
    private String password = "riftp";
    private volatile boolean shutdown = false;
    private volatile boolean initializationCompleted = false;

    public FTPServerController(QueueController queueController, ServiceSpecCollection services, CountDownLatch readySignal) {
        this.queueController = queueController;
        this.services = services;
        this.readySignal = readySignal;
    }

    public FTPServerController(QueueController queueController, ServiceSpecCollection services) {
        this.queueController = queueController;
        this.services = services;
        this.readySignal = null;
    }

    public void run() {
        try{
            initialize();
            start();
            if (readySignal != null)readySignal.countDown();
            while (!isShutdown()){
                Thread.currentThread().sleep(500);
            }
            shutdown();
        } catch (Exception ex){
			if (ex instanceof InterruptedException)
				Thread.currentThread().interrupt();
            ex.printStackTrace();
        }

    }

    public void start(){
        try{
           server.start();
           setShutdown(false);
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void shutdown(){
        try {
            server.stop();
            if (listener != null){
                if (!listener.isStopped()){
                    listener.stop();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    private void initialize() throws Exception {
        try {
            this.username = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.FTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.FTP_SERVER_USERNAME_PARAMETER, this.username);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        try {
            this.password = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.FTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.FTP_SERVER_PASSWORD_PARAMETER, this.password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        boolean loginRequired = false;
        try {
            loginRequired = Boolean.valueOf(RIParameters.getInstance().getParameterValue(NTCIP2306Settings.FTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.FTP_SERVER_LOGIN_REQUIRED_PARAMETER, String.valueOf(loginRequired)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            FtpServerFactory serverFactory = new FtpServerFactory();
            RIListenerFactory factory = new RIListenerFactory();

            HashMap<String, OperationSpecCollection> serviceMap = services.getServerSpecsByLocation();
            System.out.println("TrialFTPServer::run serviceMap has " + serviceMap.size() + " entries for instance " + this);
            Iterator locationsIterator = serviceMap.keySet().iterator();
            while (locationsIterator.hasNext()) {
                try {
                    String location = (String) locationsIterator.next();
                    URL tmpURL = new URL(location);
                    if (tmpURL.getProtocol().equalsIgnoreCase("ftp")) {
                        factory.setServerAddress(tmpURL.getHost());
                        factory.setPort(tmpURL.getPort());

                        // replace the default listener

                        RIFtpHandler handler = new RIFtpHandler(tmpURL.getPath(), serviceMap.get(location), queueController);

                        listener = factory.createListener(handler);

                        serverFactory.addListener(location, listener);

                    } else if (tmpURL.getProtocol().equalsIgnoreCase("ftps")) {




                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
                File tmpFile = new File("myusers.properties");
                System.out.println("Directory Path for User Properties file = " + tmpFile.getAbsolutePath());

            if (!loginRequired) {
                ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
                connectionConfigFactory.setAnonymousLoginEnabled(true);

                serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
                serverFactory.setUserManager(new TestUserManagerFactory().createUserManager());
            } else {
                PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();

                userManagerFactory.setFile(tmpFile);
                userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
                UserManager um = userManagerFactory.createUserManager();

                BaseUser user = new BaseUser();
                user.setName(this.username); 
                user.setPassword(this.password);
                String userDir = System.getProperty("user.dir");  // Get the directory the application was started from
                user.setHomeDirectory(userDir + "\\testfiles");  
                System.out.println("Get the home directory for the user =>" + user.getHomeDirectory());
                um.save(user);
                serverFactory.setUserManager(um);

            }
                
                Map<String, Ftplet> ftplets = new HashMap<String, Ftplet>();
                riFtplet = new RIFtplet();
                ftplets.put("c2criftplet", riFtplet);
                serverFactory.setFtplets(ftplets);


                // start the server
                server = serverFactory.createServer();


            } catch  (Exception ex) {
            log.debug("*FTPServerTransport: Exception Error - " + ex.getMessage());
            System.out.println(ex.getMessage());
            try {
//                this.ftpCommandListener.close();
            } catch (Exception e) {
                System.out.println("Closing the Listener didn't work either: " + e.getMessage());
            }
            throw new Exception("[FTPServerTransport] Unable to initialize the FTP Server with Error: " + ex.getMessage());

        }
        initializationCompleted = true;

    }

    public boolean isShutdown() {
        return shutdown;
    }

    public boolean isInitializationCompleted() {
        return initializationCompleted;
    }

   
    public void setShutdown(boolean shutdown) {
        this.shutdown = shutdown;
    }

/**
 * Manages user authentication for the FTP Server.
 */    
private class TestUserManagerFactory implements UserManagerFactory {

    @Override
    public UserManager createUserManager() {
        return new TestUserManager("admin", new ClearTextPasswordEncryptor());
    }
}

private class TestUserManager extends AbstractUserManager {
    private BaseUser testUser;
    private BaseUser anonUser;

    public TestUserManager(String adminName, PasswordEncryptor passwordEncryptor) {
        super(adminName, passwordEncryptor);

        testUser = new BaseUser();
        testUser.setAuthorities(Arrays.asList(new Authority[] {new ConcurrentLoginPermission(1, 1)}));
        testUser.setEnabled(true);
        String userDir = System.getProperty("user.dir");  // Get the directory the application was started from
        testUser.setHomeDirectory(userDir + "\\testfiles");         
        testUser.setMaxIdleTime(10000);
        testUser.setName("TEST_USERNAME");
        testUser.setPassword("TEST_PASSWORD");

        anonUser = new BaseUser(testUser);
        anonUser.setName("anonymous");
    }

    @Override
    public User getUserByName(String username) throws FtpException {
        if("TEST_USERNAME".equals(username)) {
            return testUser;
        } else if(anonUser.getName().equals(username)) {
            return anonUser;
        }

        return null;
    }

    @Override
    public String[] getAllUserNames() throws FtpException {
        return new String[] {"TEST_USERNAME", anonUser.getName()};
    }

    @Override
    public void delete(String username) throws FtpException {
        //no opt
    }

    @Override
    public void save(User user) throws FtpException {
        //no opt
        System.out.println("save");
    }

    @Override
    public boolean doesExist(String username) throws FtpException {
        return ("TEST_USERNAME".equals(username) || anonUser.getName().equals(username)) ? true : false;
    }

    @Override
    public User authenticate(Authentication authentication) throws AuthenticationFailedException {
        if(UsernamePasswordAuthentication.class.isAssignableFrom(authentication.getClass())) {
            UsernamePasswordAuthentication upAuth = (UsernamePasswordAuthentication) authentication;

            if("TEST_USERNAME".equals(upAuth.getUsername()) && "TEST_PASSWORD".equals(upAuth.getPassword())) {
                return testUser;
            }

            if(anonUser.getName().equals(upAuth.getUsername())) {
                return anonUser;
            }
        } else if(AnonymousAuthentication.class.isAssignableFrom(authentication.getClass())) {
            return anonUser;
        }

        return null;
    }
}
}
