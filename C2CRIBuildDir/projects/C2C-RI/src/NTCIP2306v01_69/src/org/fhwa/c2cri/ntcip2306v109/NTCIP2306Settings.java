/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.ntcip2306v109;

/**
 * The Interface NTCIP2306Settings.
 *
 * @author TransCore ITS, LLC
 * 11/15/2013
 */
public interface NTCIP2306Settings {
    
    /** The role. */
    public static String ROLE = "ROLE";
    
    /** The transport. */
    public static String TRANSPORT = "TRANSPORT";
    
    /** The server address. */
    public static String SERVER_ADDRESS = "SERVER_ADDRESS";
    
    /** The user name. */
    public static String USER_NAME = "USER_NAME";
    
    /** The password. */
    public static String PASSWORD = "PASSWORD";
    
    /** The local folder. */
    public static String LOCAL_FOLDER = "LOCAL_FOLDER";
    
    /** The destination folder. */
    public static String DESTINATION_FOLDER = "DESTINATION_FOLDER";
    
    /** The filename. */
    public static String FILENAME = "FILENAME";
    
    /** The start time. */
    public static String START_TIME = "START_TIME";
    
    /** The end time. */
    public static String END_TIME = "END_TIME";
    
    /** The ftp port. */
    public static String FTP_PORT="FTP_PORT";
    
    /** The user file. */
    public static String USER_FILE= "USER_FILE";
    
    /** The home directory. */
    public static String HOME_DIRECTORY = "HOME_DIRECTORY";
    
    /** The soapaction. */
    public static String SOAPACTION = "SOAPACTION";
    
    /** The http url. */
    public static String HTTP_URL = "HTTP_URL";
    
    /** The httpport. */
    public static String HTTPPORT = "HTTPPORT";
    
    /** The httpsport. */
    public static String HTTPSPORT = "HTTPSPORT";
    
    /** The encoding. */
    public static String ENCODING = "ENCODING";
    
    /** The binding name. */
    public static String BINDING_NAME = "BINDING_NAME";
    
    /** The port name. */
    public static String PORT_NAME = "PORT_NAME";
    
    /** The return address. */
    public static String RETURN_ADDRESS = "RETURN_ADDRESS";
    
    /** The service name. */
    public static String SERVICE_NAME = "SERVICE_NAME";
    
    /** The soap address url. */
    public static String SOAP_ADDRESS_URL = "SOAP_ADDRESS_URL";
    
    /** The return handler. */
    public static String RETURN_HANDLER = "RETURN_HANDLER";
    
    /** The subaction. */
    public static String SUBACTION = "SUBACTION";
    
    /** The subfrequency. */
    public static String SUBFREQUENCY = "SUBFREQUENCY";
    
    /** The subid. */
    public static String SUBID = "SUBID";
    
    /** The subname. */
    public static String SUBNAME = "SUBNAME";
    
    /** The subtimeframe. */
    public static String SUBTIMEFRAME = "SUBTIMEFRAME";
    
    /** The subtype. */
    public static String SUBTYPE = "SUBTYPE";
    
    /** The timeout. */
    public static String TIMEOUT = "TIMEOUT";


    /** The gzip encoding value. */
    public static String GZIP_ENCODING_VALUE = "GZIP";
    
    /** The xml encoding value. */
    public static String XML_ENCODING_VALUE = "XML";
    
    /** The soap encoding value. */
    public static String SOAP_ENCODING_VALUE = "SOAP";
    
    /** The NTCIP2306 gzip encoding. */
    public static String NTCIP2306GZIPEncoding = "GZipXML";
    
    /** The NTCIP2306 xml encoding. */
    public static String NTCIP2306XMLEncoding = "XML";
    
    /** The NTCIP2306 soap encoding. */
    public static String NTCIP2306SOAPEncoding = "SOAP_XML";
    
    /** The server role value. */
    public static String SERVER_ROLE_VALUE = "SERVER";
    
    /** The client role value. */
    public static String CLIENT_ROLE_VALUE = "CLIENT";
    
    /** The ftp transport value. */
    public static String FTP_TRANSPORT_VALUE = "ftp:operation";
    
    /** The http transport value. */
    public static String HTTP_TRANSPORT_VALUE = "http:binding";
    
    /** The soap transport value. */
    public static String SOAP_TRANSPORT_VALUE = "soap:binding";
    
    /** The default server address value. */
    public static String DEFAULT_SERVER_ADDRESS_VALUE = "127.0.0.1";
    
    /** The default user name value. */
    public static String DEFAULT_USER_NAME_VALUE = "anonymous";
    
    /** The default password value. */
    public static String DEFAULT_PASSWORD_VALUE = "secret";
    
    /** The default local folder value. */
    public static String DEFAULT_LOCAL_FOLDER_VALUE = "c:\\inout";
    
    /** The default destination folder value. */
    public static String DEFAULT_DESTINATION_FOLDER_VALUE = "c:\\inout\\tmp";
    
    /** The default filename value. */
    public static String DEFAULT_FILENAME_VALUE = "tmdd.xml.gz";
    
    /** The default ftp port value. */
    public static String DEFAULT_FTP_PORT_VALUE="2221";
    
    /** The default ftp internalcommand port value. */
    public static String DEFAULT_FTP_INTERNALCOMMAND_PORT_VALUE="2220";
    
    /** The default ftp clienttransfer internal port value. */
    public static String DEFAULT_FTP_CLIENTTRANSFER_INTERNAL_PORT_VALUE="2222";
    
    /** The default ftp clienttransfer externalif port value. */
    public static String DEFAULT_FTP_CLIENTTRANSFER_EXTERNALIF_PORT_VALUE="2223";
    
    /** The default ftp servertransfer internalif port value. */
    public static String DEFAULT_FTP_SERVERTRANSFER_INTERNALIF_PORT_VALUE="2219";
    
    /** The default ftp servercommand internalif port value. */
    public static String DEFAULT_FTP_SERVERCOMMAND_INTERNALIF_PORT_VALUE="2224";
    
    /** The default user file value. */
    public static String DEFAULT_USER_FILE_VALUE= "myusers.properties";
    
    /** The default home directory value. */
    public static String DEFAULT_HOME_DIRECTORY_VALUE = "c:\\inout";
    
    /** The default soapaction value. */
    public static String DEFAULT_SOAPACTION_VALUE = "";
    
    /** The default http url value. */
    public static String DEFAULT_HTTP_URL_VALUE = "";
    
    /** The default httpport value. */
    public static String DEFAULT_HTTPPORT_VALUE = "";
    
    /** The default httpport internal port value. */
    public static String DEFAULT_HTTPPORT_INTERNAL_PORT_VALUE = "2225";
    
    /** The default httpsport value. */
    public static String DEFAULT_HTTPSPORT_VALUE = "";
    
    /** The default encoding value. */
    public static String DEFAULT_ENCODING_VALUE = "";
    
    /** The default binding name value. */
    public static String DEFAULT_BINDING_NAME_VALUE = "";
    
    /** The default port name value. */
    public static String DEFAULT_PORT_NAME_VALUE = "";
    
    /** The default return address value. */
    public static String DEFAULT_RETURN_ADDRESS_VALUE = "";
    
    /** The default service name value. */
    public static String DEFAULT_SERVICE_NAME_VALUE = "";
    
    /** The default soap address url value. */
    public static String DEFAULT_SOAP_ADDRESS_URL_VALUE = "";
    
    /** The default return handler value. */
    public static String DEFAULT_RETURN_HANDLER_VALUE = "";
    
    /** The default subaction value. */
    public static String DEFAULT_SUBACTION_VALUE = "";
    
    /** The default subfrequency value. */
    public static String DEFAULT_SUBFREQUENCY_VALUE = "";
    
    /** The default subid value. */
    public static String DEFAULT_SUBID_VALUE = "";
    
    /** The default subname value. */
    public static String DEFAULT_SUBNAME_VALUE = "";
    
    /** The default subtimeframe value. */
    public static String DEFAULT_SUBTIMEFRAME_VALUE = "";
    
    /** The default subtype value. */
    public static String DEFAULT_SUBTYPE_VALUE = "";
    
    /** The default timeout value. */
    public static String DEFAULT_TIMEOUT_VALUE = "10000";  // 10 seconds (in milliseconds)
    
    /** The ftp namespace uri. */
    public static String FTP_NAMESPACE_URI = "http://schemas.ntcip.org/wsdl/ftp/";

    /** The http server settings group. */
    public static String HTTP_SERVER_SETTINGS_GROUP = "HTTPServerSettings";
    
    /** The http receive maxwaittime parameter. */
    public static String HTTP_RECEIVE_MAXWAITTIME_PARAMETER = "HTTPReceiveMaxWaitTime";
    
    /** The http receive maxwaittime default value. */
    public static String HTTP_RECEIVE_MAXWAITTIME_DEFAULT_VALUE = "10000";
    
    /** The http response maxwaittime parameter. */
    public static String HTTP_RESPONSE_MAXWAITTIME_PARAMETER = "HTTPResponseMaxWaitTime";
    
    /** The http response maxwaittime default value. */
    public static String HTTP_RESPONSE_MAXWAITTIME_DEFAULT_VALUE = "4000";
    
    /** The http response command maxwaittime parameter. */
    public static String HTTP_RESPONSE_COMMAND_MAXWAITTIME_PARAMETER = "HTTPResponseCommandMaxWaitTime";
    
    /** The http response command maxwaittime default value. */
    public static String HTTP_RESPONSE_COMMAND_MAXWAITTIME_DEFAULT_VALUE = "60000";
    
    /** The http internalserver port parameter. */
    public static String HTTP_INTERNALSERVER_PORT_PARAMETER = "HTTPInternalServerPort";
    
    /** The default httpport internalserver port value. */
    public static String DEFAULT_HTTPPORT_INTERNALSERVER_PORT_VALUE = "2226";
    
    /** The http internalserverssl port parameter. */
    public static String HTTP_INTERNALSERVERSSL_PORT_PARAMETER = "HTTPInternalSSLServerPort";
    
    /** The default httpport internalserverssl port value. */
    public static String DEFAULT_HTTPPORT_INTERNALSERVERSSL_PORT_VALUE = "2227";
    
    /** The http internalserverpub port parameter. */
    public static String HTTP_INTERNALSERVERPUB_PORT_PARAMETER = "HTTPInternalServerPubPort";
    
    /** The default httpport internalserverpub port value. */
    public static String DEFAULT_HTTPPORT_INTERNALSERVERPUB_PORT_VALUE = "2228";
    
    /** The http internalclientpub port parameter. */
    public static String HTTP_INTERNALCLIENTPUB_PORT_PARAMETER = "HTTPInternalClientPubPort";
    
    /** The default httpport internalclientpub port value. */
    public static String DEFAULT_HTTPPORT_INTERNALCLIENTPUB_PORT_VALUE = "2229";
    
    /** The http internalserversslpub port parameter. */
    public static String HTTP_INTERNALSERVERSSLPUB_PORT_PARAMETER = "HTTPInternalSSLServerPubPort";
    
    /** The default httpport internalserversslpub port value. */
    public static String DEFAULT_HTTPPORT_INTERNALSERVERSSLPUB_PORT_VALUE = "2230";
    
    /** The http server username parameter. */
    public static String HTTP_SERVER_USERNAME_PARAMETER = "ServerUserName";
    
    /** The http server password parameter. */
    public static String HTTP_SERVER_PASSWORD_PARAMETER = "ServerPassword";
    
    /** The http server authentication required parameter. */
    public static String HTTP_SERVER_AUTHENTICATION_REQUIRED_PARAMETER = "ServerAuthenticationRequired";
    
    /** The http server defaultpublicationlistenerport parameter. */
    public static String HTTP_SERVER_DEFAULTPUBLICATIONLISTENERPORT_PARAMETER = "HTTPServerPublicationListenerPort";
    
    /** The default http server publicationlistenerport value. */
    public static String DEFAULT_HTTP_SERVER_PUBLICATIONLISTENERPORT_VALUE = "8086";
    
    /** The http server home path parameter. */
    public static String HTTP_SERVER_HOME_PATH_PARAMETER = "HTTPServerHomePath";
    
    /** The default http server home path parameter value. */
    public static String DEFAULT_HTTP_SERVER_HOME_PATH_PARAMETER_VALUE = "c:/c2cri";
    
    /** The http server listener hostaddress parameter. */
    public static String HTTP_SERVER_LISTENER_HOSTADDRESS_PARAMETER = "HTTPServerListenerHostAddress";
    
    /** The default httpserver listener hostaddress parameter value. */
    public static String DEFAULT_HTTPSERVER_LISTENER_HOSTADDRESS_PARAMETER_VALUE = "C2CRIEC";
    
    /** The http client settings group. */
    public static String HTTP_CLIENT_SETTINGS_GROUP = "HTTPClientSettings";
    
    /** The http client response maxwaittime parameter. */
    public static String HTTP_CLIENT_CONNECTION_MAXWAITTIME_PARAMETER = "HTTPConnectionMaxWaitTime";
    
    /** The http client response maxwaittime default value. */
    public static String HTTP_CLIENT_CONNECTION_MAXWAITTIME_DEFAULT_VALUE = "10000";

    /** The http client TLS Protocol parameter. */
    public static String HTTP_CLIENT_CONNECTION_TLS_PARAMETER = "HTTPConnectionTLSProtocol";
    
    /** The http client TLS Protocol default value. */
    public static String HTTP_CLIENT_CONNECTION_TLS_DEFAULT_VALUE = "TLSv1.2";   
    
    /** The http client response maxwaittime parameter. */
    public static String HTTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER = "HTTPResponseMaxWaitTime";
    
    /** The http client response maxwaittime default value. */
    public static String HTTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE = "310000";
    
    /** The http client publication maxwaittime parameter. */
    public static String HTTP_CLIENT_PUBLICATION_MAXWAITTIME_PARAMETER = "HTTPPublicationMaxWaitTime";
    
    /** The http client publication maxwaittime default value. */
    public static String HTTP_CLIENT_PUBLICATION_MAXWAITTIME_DEFAULT_VALUE = "310000";
    
    /** The http client username parameter. */
    public static String HTTP_CLIENT_USERNAME_PARAMETER = "ClientUserName";
    
    /** The http client password parameter. */
    public static String HTTP_CLIENT_PASSWORD_PARAMETER = "ClientPassword";
    
    /** The http client authentication required parameter. */
    public static String HTTP_CLIENT_AUTHENTICATION_REQUIRED_PARAMETER = "ClientAuthenticationRequired";

    /** The NTCIP 2306 Ensure SOAPAction Header Field is enclosed in Quotes. */
    public static String NTCIP2306_ENSURE_SOAPACTION_QUOTES_PARAMETER = "EnsureQuotesOnSOAPActionField";

    /** The default NTCIP 2306 Ensure SOAPAction Header Field is enclosed in Quotes parameter value. */
    public static String DEFAULT_NTCIP2306_ENSURE_SOAPACTION_QUOTES_PARAMETER_VALUE = "false";
    
    /** The ftp server settings group. */
    public static String FTP_SERVER_SETTINGS_GROUP = "FTPServerSettings";
    
    /** The ftp server receive maxwaittime parameter. */
    public static String FTP_SERVER_RECEIVE_MAXWAITTIME_PARAMETER = "FTPReceiveMaxWaitTime";
    
    /** The ftp server receive maxwaittime default value. */
    public static String FTP_SERVER_RECEIVE_MAXWAITTIME_DEFAULT_VALUE = "20000";
    
    /** The ftp server username parameter. */
    public static String FTP_SERVER_USERNAME_PARAMETER = "UserName";
    
    /** The ftp server password parameter. */
    public static String FTP_SERVER_PASSWORD_PARAMETER = "Password";
    
    /** The ftp server login required parameter. */
    public static String FTP_SERVER_LOGIN_REQUIRED_PARAMETER = "LoginRequired";

    /** The ftp client settings group. */
    public static String FTP_CLIENT_SETTINGS_GROUP = "FTPClientSettings";
    
    /** The ftp client response maxwaittime parameter. */
    public static String FTP_CLIENT_RESPONSE_MAXWAITTIME_PARAMETER = "FTPResponseMaxWaitTime";
    
    /** The ftp client response maxwaittime default value. */
    public static String FTP_CLIENT_RESPONSE_MAXWAITTIME_DEFAULT_VALUE = "20000";
    
    /** The ftp client username parameter. */
    public static String FTP_CLIENT_USERNAME_PARAMETER = "UserName";
    
    /** The ftp client password parameter. */
    public static String FTP_CLIENT_PASSWORD_PARAMETER = "Password";
    
    /** The ftp client anonymous login parameter. */
    public static String FTP_CLIENT_ANONYMOUS_LOGIN_PARAMETER = "AnonymousLogin";
 
    
    /** The ssl settings group. */
    public static String SSL_SETTINGS_GROUP = "SSLSettings";
    
    /** The keystore path parameter. */
    public static String KEYSTORE_PATH_PARAMETER = "KeystorePath";
    
    /** The default keystore path parameter value. */
    public static String DEFAULT_KEYSTORE_PATH_PARAMETER_VALUE = "C:/c2cri/keystore/keystore";
    
    /** The keystore password parameter. */
    public static String KEYSTORE_PASSWORD_PARAMETER = "KeystorePassword";
    
    /** The default keystore password parameter value. */
    public static String DEFAULT_KEYSTORE_PASSWORD_PARAMETER_VALUE = "c2cri1";
    
    /** The keystore keypassword parameter. */
    public static String KEYSTORE_KEYPASSWORD_PARAMETER = "KeystoreKeyPassword";
    
    /** The default keystore keypassword parameter value. */
    public static String DEFAULT_KEYSTORE_KEYPASSWORD_PARAMETER_VALUE = "c2cri1";
    
    /** The keystore keyname parameter. */
    public static String KEYSTORE_KEYNAME_PARAMETER = "KeystoreKeyNameParameter";
    
    /** The default keystore keyname parameter value. */
    public static String DEFAULT_KEYSTORE_KEYNAME_PARAMETER_VALUE = "c2cri1";

    /** The NTCIP2306 Test settings group. */
    public static String NTCIP2306_TEST_SETTINGS_GROUP = "NTCIP2306TestSettings";
    
    /** The NTCIP 2306 Op_ Test Continue after Failure parameter. */
    public static String NTCIP2306_CONTINUE_AFTER_OP_FAILURE_PARAMETER = "ContinueAfterWSDLOperationNameErrors";

    /** The default NTCIP 2306 Op_ Test Continue after Failure parameter value. */
    public static String DEFAULT_NTCIP2306_CONTINUE_AFTER_OP_FAILURE_PARAMETER_VALUE = "false";

}
