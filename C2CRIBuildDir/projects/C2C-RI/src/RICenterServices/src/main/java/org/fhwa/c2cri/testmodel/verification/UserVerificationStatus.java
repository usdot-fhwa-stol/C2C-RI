/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testmodel.verification;

/**
 * The Class UserVerificationStatus.
 *
 * @author TransCore ITS, LLC
 * Created: October 8, 2013
 */
public class UserVerificationStatus {

    /** The user verification selection holder. */
    private static UserVerificationStatus userVerificationSelectionHolder;
    
    /** The skip user verification. */
    private static Boolean skipUserVerification = false;
    
    
    /**
     * Returns an instance of UserVerificationStatus.
     *
     * @return UserVerificationStatus
     */
    public static UserVerificationStatus getInstance(){
        if (userVerificationSelectionHolder == null){
            userVerificationSelectionHolder = new UserVerificationStatus();
        }
        return userVerificationSelectionHolder;
    }
    
    /**
     * Constructor.
     */
    private UserVerificationStatus(){        
    }
    
    /**
     * Sets the UserVerificationStatus to skip future verification steps.
     *
     * @param skip - a true value indicates that user verification steps should be skipped.
     */
    public void skipVerification(boolean skip){
        synchronized (skipUserVerification){
            skipUserVerification = skip;            
        }
    }
    
    /**
     * Getter for the user verification status.
     *
     * @return - true indicates that user verification steps should be skipped.
     */
    public boolean isSkipVerificatonSet(){
       boolean results;
        synchronized (skipUserVerification){
            results = skipUserVerification;            
        }
       return results;
   }
}
