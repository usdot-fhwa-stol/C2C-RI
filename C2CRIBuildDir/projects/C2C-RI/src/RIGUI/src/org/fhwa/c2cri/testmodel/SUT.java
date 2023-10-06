/**
 * 
 */
package org.fhwa.c2cri.testmodel;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * This class maintains the parameters that are associated and define the SUT.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class SUT implements Serializable{
	
	/**
	 * verifies that the parameters that are to be set are valid.
	 */
	public void verifySUTParameters(){
		// original implementation was empty
	}
	
	/** The SUT IP address. */
	private String ipAddress;
	/** The IP port associated with the SUT. */
        @XmlElement(name="Port")
	private String ipPort;
	
	/** The host name of the SUT. */
	private String hostName;
	
	/** represents the primary URL for the SUT web service. */
	private String webServiceURL;
	/** if required for communication, the user name that will be accepted by the SUT. */
	private String userName;
	
	/** if necessary, the password that will be accepted by the SUT. */
	private String password;
        
        /** flag indicating whether a user name is required. */
        private boolean userNameRequired;
        
        /** flag indicating whether a password is required. */
        private boolean passwordRequired;

        /**
         * Constructor for the SUT Class.
         */
        public SUT(){
            this.ipAddress = "127.0.0.1";
            this.ipPort = "8080";
            this.hostName = "localhost";
            this.webServiceURL = "file://localhost/c:/c2cri/testfiles/release2+.wsdl";
            this.userName = "anonymous";
            this.password = "passwordgiven";
            this.userNameRequired = false;
            this.passwordRequired = false;
        }
	
	/**
	 * Gets the host name.
	 *
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}
	
	/**
	 * Sets the host name.
	 *
	 * @param hostName the hostName to set
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	/**
	 * Gets the ip address.
	 *
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}
	
	/**
	 * Sets the ip address.
	 *
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	/**
	 * Gets the ip port.
	 *
	 * @return the ipPort
	 */
        @XmlTransient
	public String getIpPort() {
		return ipPort;
	}
	
	/**
	 * Sets the ip port.
	 *
	 * @param ipPort the ipPort to set
	 */
	public void setIpPort(String ipPort) {
		this.ipPort = ipPort;
	}
	
	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Sets the password.
	 *
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Gets the user name.
	 *
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Sets the user name.
	 *
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Gets the web service url.
	 *
	 * @return the webServiceURL
	 */
	public String getWebServiceURL() {
		return webServiceURL;
	}
	
	/**
	 * Sets the web service url.
	 *
	 * @param webServiceURL the webServiceURL to set
	 */
	public void setWebServiceURL(String webServiceURL) {
		this.webServiceURL = webServiceURL;
	}

 /**
  * Checks if is password required.
  * 
  * Pre-Conditions: N/A
  * Post-Conditions: N/A
  *
  * @return true, if is password required
  */
        public boolean isPasswordRequired() {
        return passwordRequired;
    }

  /**
   * Sets the password required.
   *
   * @param passwordRequired the new password required
   */
  public void setPasswordRequired(boolean passwordRequired) {
        this.passwordRequired = passwordRequired;
    }

 /**
  * Checks if is user name required.
  * 
  * Pre-Conditions: N/A
  * Post-Conditions: N/A
  *
  * @return true, if is user name required
  */
    public boolean isUserNameRequired() {
        return userNameRequired;
    }

 /**
  * Sets the user name required.
  *
  * @param userNameRequired the new user name required
  */
    public void setUserNameRequired(boolean userNameRequired) {
        this.userNameRequired = userNameRequired;
    }

    /**
     * Creates an XML representation of the SUT Object.
     *
     * @return the SUT object as XML.
     */
         public String to_XML(){
            String xmlOutput = "<SUT>\n";
            xmlOutput = xmlOutput+"<ipAddress>"+this.ipAddress+"</ipAddress>\n";
            xmlOutput = xmlOutput+"<port>"+this.ipPort+"</port>\n";
            xmlOutput = xmlOutput+"<hostName>"+this.hostName+"</hostName>\n";
            xmlOutput = xmlOutput+"<webServiceURL>"+this.webServiceURL+"</webServiceURL>\n";
            xmlOutput = xmlOutput+"<userName>"+this.userName+"</userName>\n";
            xmlOutput = xmlOutput+"<password>"+this.password+"</password>\n";
            xmlOutput = xmlOutput + "</SUT>\n";

            return xmlOutput;
            }


}
