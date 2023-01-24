/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package test.transports;

import org.fhwa.c2cri.messagemanager.Message;

/**
 *
 * @author TransCore ITS
 */
public class TMDDAuthenticationProcessor {


    public static void processMessageAuthentication(String username, String password, String organizationId, Message requestMessage) throws Exception{

        String authenticationUserName = requestMessage.getMessageSpecification().getElementValue("*.authentication.user-id");
        String authenticationPassword = requestMessage.getMessageSpecification().getElementValue("*.authentication.user-id");
        String authenticationOrganizationId = requestMessage.getMessageSpecification().getElementValue("*.authentication.user-id");

        if (!authenticationUserName.equals(username)){
            throw new Exception("Authentication Error: The received user name "+ authenticationUserName + " does not match the expected user name "+ username);
        }

        if (!authenticationUserName.equals(password)){
            throw new Exception("Authentication Error: The received password "+ authenticationPassword + " does not match the expected password "+ password);
        }

        if (!authenticationUserName.equals(organizationId)){
            throw new Exception("Authentication Error: The received organization id "+ authenticationOrganizationId + " does not match the expected organization id "+ organizationId);
        }

    }
}
