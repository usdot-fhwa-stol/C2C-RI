/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.http.server;

import org.fhwa.c2cri.ntcip2306v109.messaging.QueueController;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import org.eclipse.jetty.http.security.Constraint;
import org.eclipse.jetty.http.security.Credential;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.fhwa.c2cri.ntcip2306v109.NTCIP2306Settings;
import org.fhwa.c2cri.ntcip2306v109.http.RISslSelectChannelConnector;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecCollection;
import org.fhwa.c2cri.ntcip2306v109.wsdl.OperationSpecification;
import org.fhwa.c2cri.ntcip2306v109.wsdl.ServiceSpecCollection;
import org.fhwa.c2cri.utilities.RIParameters;

/**
 * The Class NTCIP2306HTTPServer.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class NTCIP2306HTTPServer implements Runnable {

    /** The queue controller. */
    private QueueController queueController;
    
    /** The services. */
    private ServiceSpecCollection services = new ServiceSpecCollection();
    
    /** The listener mode. */
    private boolean listenerMode = false;
    
    /** The ready signal. */
    private final CountDownLatch readySignal;
    
    /** The server. */
    private Server server;

    /**
     * Instantiates a new NTCIP2306 http server.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param queueController the queue controller
     * @param services the services
     * @param listenerMode the listener mode
     * @param readySignal the ready signal
     */
    public NTCIP2306HTTPServer(final QueueController queueController, ServiceSpecCollection services, boolean listenerMode, CountDownLatch readySignal) {
        this.queueController = queueController;
        this.services = services;
        this.listenerMode = listenerMode;
        this.readySignal = readySignal;
        System.out.println("NTCIP2306HTTPServer::Constructor  queuecontroller=" + this.queueController);
    }

    /**
     * Instantiates a new NTCIP2306 http server.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param queueController the queue controller
     * @param services the services
     * @param listenerMode the listener mode
     */
    public NTCIP2306HTTPServer(final QueueController queueController, ServiceSpecCollection services, boolean listenerMode) {
        this.queueController = queueController;
        this.services = services;
        this.listenerMode = listenerMode;
        this.readySignal = null;
        System.out.println("NTCIP2306HTTPServer::Constructor  queuecontroller=" + this.queueController);
    }

    /**
     * Instantiates a new NTCIP2306 http server.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param queueController the queue controller
     */
    public NTCIP2306HTTPServer(final QueueController queueController) {
        this.queueController = queueController;
        this.readySignal = null;
        System.out.println("NTCIP2306HTTPServer::Constructor  queuecontroller=" + this.queueController);
    }

    /**
     * Instantiates a new NTCIP2306 http server.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public NTCIP2306HTTPServer() {
        this.queueController = null;
        this.readySignal = null;
    }

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        NTCIP2306HTTPServer theServer = new NTCIP2306HTTPServer();
        theServer.run();
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        server = new Server();

        ArrayList<Connector> connectorList = new ArrayList();
        ArrayList<Integer> portList = new ArrayList();
        
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        String username = "rihttp";
        try {
            username = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_SERVER_USERNAME_PARAMETER, username);
            System.out.println("RIParameters provided username = " + username);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        String password = "rihttp";
        try {
            password = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_SERVER_PASSWORD_PARAMETER, password);
            System.out.println("RIParameters provided password = " + password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean useAuthentication = false;
        try {
            useAuthentication = Boolean.valueOf(RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_SERVER_AUTHENTICATION_REQUIRED_PARAMETER, String.valueOf(useAuthentication)));
            System.out.println("RIParameters provided authentication = " + useAuthentication);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        useAuthentication = false;
        if (useAuthentication) {
            System.out.println("NTCIPHTTPServer.run Trying to set Security with username = " + username + " and password = " + password);
            context.setSecurityHandler(basicAuth(username, password, "Private!"));
        }

        HashMap<String, OperationSpecCollection> serviceMap = services.getServerSpecsByLocation();
        System.out.println("NTCIP2306HTTPServer::run serviceMap has " + serviceMap.size() + " entries for instance " + this);
        Iterator locationsIterator = serviceMap.keySet().iterator();
        while (locationsIterator.hasNext()) {
            try {
                URL tmpURL;
                String location = (String) locationsIterator.next();
                if (listenerMode) {
                    URL originalURL = null;
                    try {
                        originalURL = new URL(location);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    String publicationListenerPort = "";
                    try {
                        publicationListenerPort = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_SERVER_DEFAULTPUBLICATIONLISTENERPORT_PARAMETER, NTCIP2306Settings.DEFAULT_HTTP_SERVER_PUBLICATIONLISTENERPORT_VALUE);
                        System.out.println("RIParameters provided publication listener port = " + publicationListenerPort);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }


                    String publicationListenerHostAddress = "";
                    try {
                        publicationListenerHostAddress = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_SERVER_LISTENER_HOSTADDRESS_PARAMETER, NTCIP2306Settings.DEFAULT_HTTPSERVER_LISTENER_HOSTADDRESS_PARAMETER_VALUE);
                        System.out.println("RIParameters provided publication listener host address = " + publicationListenerHostAddress);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    tmpURL = new URL((originalURL == null ? "http" : originalURL.getProtocol()) + "://"+publicationListenerHostAddress + (publicationListenerPort.isEmpty() ? "" : ":" + publicationListenerPort) + (originalURL == null ? "/" : originalURL.getPath()));
                    setListenerHandlers(context, connectorList, tmpURL.toString(), serviceMap.get(location), portList);
                } else {
                    tmpURL = new URL(location);

                    if (tmpURL.getProtocol().equalsIgnoreCase("http")) {
                        RISelectChannelConnector connector0 = new RISelectChannelConnector(location);
                        if (tmpURL.getPort() != -1) {
                            connector0.setPort(tmpURL.getPort());
                        }

                        connector0.setRequestHeaderSize(8192);
                        if (!connectorList.contains(connector0)&&!portList.contains(connector0.getPort())) {
                            connectorList.add(connector0);
                            portList.add(connector0.getPort());
                        }
                        context.addServlet(new ServletHolder(new NTCIP2306HTTPServlet("Hello", tmpURL.getPath(), queueController, serviceMap.get(location))), tmpURL.getPath());
                        System.out.println("Added HTTP operations at Host:" + tmpURL.getHost() + " Port:" + tmpURL.getPort() + " Path:  " + tmpURL.getPath() + " by NTCIP2306HTTPServer ID " + this);
                    } else if (tmpURL.getProtocol().equalsIgnoreCase("https")) {


                        
                    String keystorePath = "";
                    try {
                        keystorePath = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_PATH_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_PATH_PARAMETER_VALUE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                        
                    String jetty_home = "";
                    try {
                        jetty_home = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_SERVER_HOME_PATH_PARAMETER, NTCIP2306Settings.DEFAULT_HTTP_SERVER_HOME_PATH_PARAMETER_VALUE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    String keystorePassword = "";
                    try {
                        keystorePassword = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_PASSWORD_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_PASSWORD_PARAMETER_VALUE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    String keyPassword = "";
                    try {
                        keyPassword = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_KEYPASSWORD_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_KEYPASSWORD_PARAMETER_VALUE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                        RISslSelectChannelConnector ssl_connector = new RISslSelectChannelConnector();
                        System.setProperty("jetty.home", jetty_home);

                        if (tmpURL.getPort() != 0) {
                            ssl_connector.setPort(tmpURL.getPort());
                        }
                        ssl_connector.setKeystore(keystorePath);
                        ssl_connector.setPassword(keystorePassword);
                        ssl_connector.setKeyPassword(keyPassword);
                        if (!connectorList.contains(ssl_connector)&&!portList.contains(ssl_connector.getPort())) {
                            connectorList.add(ssl_connector);
                            portList.add(ssl_connector.getPort());
                        }
                        context.addServlet(new ServletHolder(new NTCIP2306HTTPServlet("Hello", tmpURL.getPath(), queueController, serviceMap.get(location))), tmpURL.getPath());
                        System.out.println("Added HTTPS operations at Host:" + ssl_connector.getHost() + " Port:" + ssl_connector.getPort() + " Path:  " + tmpURL.getPath() + " by NTCIP2306HTTPServer ID " + this);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }



        Connector[] connectorArray = connectorList.toArray(new Connector[connectorList.size()]);
        server.setConnectors(connectorArray);


        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context, new DefaultHandler()});

        server.setHandler(contexts);

        boolean countdownPerformed = false;
        try {
            server.start();
            if (readySignal != null) {
                readySignal.countDown();
                countdownPerformed = true;
            }
            server.join();
        } catch (InterruptedException iex) {
            iex.printStackTrace();
            if (!countdownPerformed) {
                if (readySignal != null) {
                    readySignal.countDown();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            if (server.isRunning()) {
                System.out.println("Server Exception occurred, but reports it's still running.");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "The HTTP Server is not running!!\n\n" + ex.getMessage(), "Initialization Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
            if (!countdownPerformed) {
                if (readySignal != null) {
                    readySignal.countDown();
                }
            }
        }
    }

    /**
     * Shutdown.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public void shutdown() {
        try {
            server.stop();
            int maxTries = 100;
            int numTries = 0;
            while ((numTries <= maxTries) && (!server.isStopped())) {
                Thread.currentThread().sleep(100);
                numTries++;
            }
            System.out.println("NTCIP2306HTTPServer::shutdown Done waiting on shutdown.  IsStoppedState=" + server.isStopped());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sets the listener handlers.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param context the context
     * @param connectorList the connector list
     * @param location the location
     * @param operations the operations
     */
    private void setListenerHandlers(ServletContextHandler context, ArrayList<Connector> connectorList, String location, OperationSpecCollection operations, ArrayList<Integer> portList) {
        for (OperationSpecification thisSpec : operations.getCopyAsList()) {
            if (thisSpec.isPublicationCallbackOperation()) {
                OperationSpecCollection thisCollection = new OperationSpecCollection();
                thisCollection.add(thisSpec);
                try {
                    URL tmpURL;
                    tmpURL = new URL(location + "/" + thisSpec.getRelatedToService() + "/"
                            + thisSpec.getRelatedToPort() + "/" + thisSpec.getOperationName());


                    if (tmpURL.getProtocol().equalsIgnoreCase("http")) {
                        RISelectChannelConnector connector0 = new RISelectChannelConnector(location);
                        if (tmpURL.getPort() != -1) {
                            connector0.setPort(tmpURL.getPort());
                        }


                        connector0.setRequestHeaderSize(8192);
                        if (!connectorList.contains(connector0)&&!portList.contains(connector0.getPort())) {
                            connectorList.add(connector0);
                            portList.add(connector0.getPort());
                        }

                        context.addServlet(new ServletHolder(new NTCIP2306HTTPServlet("Hello", tmpURL.getPath(), queueController, thisCollection)), tmpURL.getPath());
                        System.out.println("NTCIP2306HTTPServer::setListenerHandlers Added HTTP operations at Host:" + tmpURL.getHost() + " Port:" + tmpURL.getPort() + " Path:  " + tmpURL.getPath() + " by NTCIP2306HTTPServer ID " + this);
                    } else if (tmpURL.getProtocol().equalsIgnoreCase("https")) {
                        
          
                    String keystorePath = "";
                    try {
                        keystorePath = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_PATH_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_PATH_PARAMETER_VALUE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                        
                    String jetty_home = "";
                    try {
                        jetty_home = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.HTTP_SERVER_SETTINGS_GROUP, NTCIP2306Settings.HTTP_SERVER_HOME_PATH_PARAMETER, NTCIP2306Settings.DEFAULT_HTTP_SERVER_HOME_PATH_PARAMETER_VALUE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    
                    String keystorePassword = "";
                    try {
                        keystorePassword = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_PASSWORD_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_PASSWORD_PARAMETER_VALUE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    String keyPassword = "";
                    try {
                        keyPassword = RIParameters.getInstance().getParameterValue(NTCIP2306Settings.SSL_SETTINGS_GROUP, NTCIP2306Settings.KEYSTORE_KEYPASSWORD_PARAMETER, NTCIP2306Settings.DEFAULT_KEYSTORE_KEYPASSWORD_PARAMETER_VALUE);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                        
                        RISslSelectChannelConnector ssl_connector = new RISslSelectChannelConnector(location);
                        System.setProperty("jetty.home", jetty_home);

                        if (tmpURL.getPort() != 0) {
                            ssl_connector.setPort(tmpURL.getPort());
                        }
                        ssl_connector.setKeystore(keystorePath);
                        ssl_connector.setPassword(keystorePassword);
                        ssl_connector.setKeyPassword(keyPassword);
                        if (!connectorList.contains(ssl_connector)&&!portList.contains(ssl_connector.getPort())) {
                            connectorList.add(ssl_connector);
                            portList.add(ssl_connector.getPort());
                        }
                        context.addServlet(new ServletHolder(new NTCIP2306HTTPServlet("Hello", tmpURL.getPath(), queueController, thisCollection)), tmpURL.getPath());
                        System.out.println("NTCIP2306HTTPServer::setListenerHandlers Added HTTPS operations at Host:" + ssl_connector.getHost() + " Port:" + ssl_connector.getPort() + " Path:  " + tmpURL.getPath() + " by NTCIP2306HTTPServer ID " + this);

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }


    }

    /**
     * Adjust service map for listener mode.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param serviceMap the service map
     */
    private void adjustServiceMapForListenerMode(HashMap<String, OperationSpecCollection> serviceMap) {
        Iterator<String> locationIterator = serviceMap.keySet().iterator();
        while (locationIterator.hasNext()) {
            String thisLocation = locationIterator.next();
            OperationSpecCollection thisSpec = serviceMap.get(thisLocation);
            ArrayList<OperationSpecification> soapSpecifications = thisSpec.getSpecsWithSOAPAction("");
            for (OperationSpecification thisOpSpec : soapSpecifications) {
                thisOpSpec.setSoapAction(thisOpSpec.getRelatedToService() + "." + thisOpSpec.getRelatedToPort() + "." + thisOpSpec.getOperationName());
                System.out.println("NTCIP2306HTTPServer::adjustServiceMapForListenerMode  Set Listener Operation " + thisOpSpec.getOperationName() + "'s SOAPAction");
            }
        }
    }

    /**
     * Basic authentication of HTTP requests.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param username the username
     * @param password the password
     * @param realm the realm
     * @return the security handler
     */
    private static final SecurityHandler basicAuth(String username, String password, String realm) {
        HashLoginService l = new HashLoginService();
        l.putUser(username, Credential.getCredential(password), new String[]{"user"});
        l.setName(realm);
        Constraint constraint = new Constraint();
        constraint.setName(Constraint.__BASIC_AUTH);
        constraint.setRoles(new String[]{"user"});
        constraint.setAuthenticate(true);
        ConstraintMapping cm = new ConstraintMapping();
        cm.setConstraint(constraint);
        cm.setPathSpec("/*");
        ConstraintSecurityHandler csh = new ConstraintSecurityHandler();
        csh.setAuthenticator(new BasicAuthenticator());
        csh.setRealmName("myrealm");
        csh.addConstraintMapping(cm);
        csh.setLoginService(l);
        return csh;
    }
}
