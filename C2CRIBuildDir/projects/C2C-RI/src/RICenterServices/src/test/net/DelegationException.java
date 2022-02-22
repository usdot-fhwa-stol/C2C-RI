/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test.net;

/**
 * http://www.javaspecialists.eu/archive/Issue168.html
 * 
 * @author TransCore ITS
 */
public class DelegationException extends RuntimeException {
  public DelegationException(String message) {
    super(message);
  }

  public DelegationException(String message, Throwable cause) {
    super(message, cause);
  }

  public DelegationException(Throwable cause) {
    super(cause);
  }
}

