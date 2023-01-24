/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd;

import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.infolayer.MessageSpecification;
import org.fhwa.c2cri.messagemanager.Message;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityType;
import org.fhwa.c2cri.tmdd.emulation.TMDDNRTMSelections;
import org.fhwa.c2cri.tmdd.emulation.exceptions.MessageAuthenticationException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NRTMViolationException;

/**
 * The Class TMDDAuthenticationProcessor inspects message content to determine
 * whether authentication requirements have been satisfied.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TMDDAuthenticationProcessor {

    private static String userName;
    private static String password;
    private static String operatorId;

    private static TMDDAuthenticationProcessor authenticationProcessor;

    private TMDDAuthenticationProcessor() {
    }

    
    public static TMDDAuthenticationProcessor getInstance() {
        if (authenticationProcessor == null) {
            authenticationProcessor = new TMDDAuthenticationProcessor();
        }
        System.out.println("TMDDAuthenticationProcessor:getInstance() userName="+userName+" password ="+password+" operatorId"+operatorId);
        return authenticationProcessor;
    }

    
    public static TMDDAuthenticationProcessor getInstance(String userNameIn, String passwordIn, String operatorIdIn) {
        TMDDAuthenticationProcessor tap = getInstance();
        tap.userName = userNameIn;
        tap.password = passwordIn;
        tap.operatorId = operatorIdIn;
        
        System.out.println("TMDDAuthenticationProcessor:getInstance(string,string,string) userName="+userName+" password ="+password+" operatorId"+operatorId);
        return tap;
    }
    
    public void processRequestMessageAuthentication(String dialog, MessageSpecification requestMessage, TMDDEntityType.EntityType entityType) throws MessageAuthenticationException, EntityEmulationException {

        System.out.println("TMDDAuthenticationProcessor:processMessageAuthentication userName="+userName+" password ="+password+" operatorId"+operatorId);
        String authenticationUserName = requestMessage.getElementValue("*.authentication.user-id");
        String authenticationPassword = requestMessage.getElementValue("*.authentication.password");
        String authenticationOperatorId = requestMessage.getElementValue("*.authentication.operator-id");

        try {
            if (TMDDNRTMSelections.getInstance().isAuthenticationRequired(dialog, requestMessage, entityType)) {
                    if (!authenticationUserName.equals(userName)&&!authenticationPassword.equals(password)){
                        throw new MessageAuthenticationException("Both the provided User Name ("+authenticationUserName + ") and password provided are not valid.");
                    }
                
                    if (!authenticationUserName.equals(userName)){
                        throw new MessageAuthenticationException("The User Name provided ("+authenticationUserName + ") is not valid.");
                    }

                    if (!authenticationPassword.equals(password)){
                        throw new MessageAuthenticationException("The Password provided is not valid.");
                    }                   
            }

        } catch (NRTMViolationException ex) {
            // We were not able to find a match for the settings in the NRTM.  So we default to pass and don't pass along the exception.
        }


        try {
            if (TMDDNRTMSelections.getInstance().isOperatorIdRequired(dialog, requestMessage, entityType)) {
                    if (!authenticationOperatorId.equals(operatorId)){
                        throw new MessageAuthenticationException("The Operator Id provided ("+authenticationOperatorId + ") is not valid.");
                    }
            }

        } catch (NRTMViolationException ex) {
            // We were not able to find a match for the settings in the NRTM.  So we default to pass and don't pass along the exception.
        }

    }

    /**
     * Process message authentication.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param username the username
     * @param password the password
     * @param operatorId the operator id
     * @param requestMessage the request message
     * @throws Exception the exception
     */
    public static void processMessageAuthentication(String username, String password, String operatorId, Message requestMessage) throws Exception {

        String authenticationUserName = requestMessage.getMessageSpecification().getElementValue("*.authentication.user-id");
        String authenticationPassword = requestMessage.getMessageSpecification().getElementValue("*.authentication.password");
        String authenticationOperatorId = requestMessage.getMessageSpecification().getElementValue("*.authentication.operator-id");

        if (!authenticationUserName.equals(username)) {
            throw new Exception("Authentication Error: The received user name " + authenticationUserName + " does not match the expected user name " + username);
        }

        if (!authenticationPassword.equals(password)) {
            throw new Exception("Authentication Error: The received password " + authenticationPassword + " does not match the expected password " + password);
        }

//TODO:  Currently does not factor in whether the NRTM Requires the Operator-Id element.  Update to factor this in.        
        if (!authenticationOperatorId.equals(operatorId)) {
            throw new Exception("Authentication Error: The received operator id " + authenticationOperatorId + " does not match the expected operator id " + operatorId);
        }

    }
}
