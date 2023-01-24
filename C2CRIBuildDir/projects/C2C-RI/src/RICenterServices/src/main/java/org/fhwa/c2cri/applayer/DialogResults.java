/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.applayer;

import java.util.ArrayList;

/**
 * The results of a message dialog.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class DialogResults {

    /** The one message received. */
    private boolean oneMessageReceived = false;
    
    /** The message fields verified. */
    private boolean messageFieldsVerified = false;
    
    /** The message values verified. */
    private boolean messageValuesVerified = false;
    
    /** The parsing errors encountered. */
    private boolean parsingErrorsEncountered = false;
    
    /** The other errors encountered. */
    private boolean otherErrorsEncountered = false;
    
    /** The wrong message type received. */
    private boolean wrongMessageTypeReceived = false;
    
    /** The number of message parts received. */
    private int numberOfMessagePartsReceived=0;
    
    /** The message received errors. */
    private ArrayList<String> messageReceivedErrors=new ArrayList<String>();
    
    /** The message fields verified errors. */
    private ArrayList<String> messageFieldsVerifiedErrors=new ArrayList<String>();
    
    /** The message values verified errors. */
    private ArrayList<String> messageValuesVerifiedErrors=new ArrayList<String>();
    
    /** The parsing errors. */
    private ArrayList<String> parsingErrors=new ArrayList<String>();
    
    /** The other errors. */
    private ArrayList<String> otherErrors=new ArrayList<String>();
    
    /** The wrong message type errors. */
    private ArrayList<String> wrongMessageTypeErrors=new ArrayList<String>();

    /**
     * Reset the flag parameters for this dialog.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: All values are reset
     */
    public void reset() {
        oneMessageReceived = false;
        messageFieldsVerified = false;
        messageValuesVerified = false;
        parsingErrorsEncountered = false;
        otherErrorsEncountered = false;
        wrongMessageTypeReceived = false;
        numberOfMessagePartsReceived = 0;
        messageReceivedErrors.clear();
        messageFieldsVerifiedErrors.clear();
        messageValuesVerifiedErrors.clear();
        parsingErrors.clear();
        otherErrors.clear();
        wrongMessageTypeErrors.clear();
    }

    /**
     * Checks if is message fields verified.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is message fields verified
     */
    public boolean isMessageFieldsVerified() {
        return messageFieldsVerified;
    }

    /**
     * Sets the message fields verified.
     *
     * @param messageFieldsVerified the new message fields verified
     */
    public void setMessageFieldsVerified(boolean messageFieldsVerified) {
        this.messageFieldsVerified = messageFieldsVerified;
    }

    /**
     * Gets the message fields verified errors.
     *
     * @return the message fields verified errors
     */
    public ArrayList<String> getMessageFieldsVerifiedErrors() {
        return messageFieldsVerifiedErrors;
    }

    /**
     * Sets the message fields verified errors.
     *
     * @param messageFieldsVerifiedErrors the new message fields verified errors
     */
    public void setMessageFieldsVerifiedErrors(ArrayList<String> messageFieldsVerifiedErrors) {
        this.messageFieldsVerifiedErrors = messageFieldsVerifiedErrors;
    }

    /**
     * Gets the message received errors.
     *
     * @return the message received errors
     */
    public ArrayList<String> getMessageReceivedErrors() {
        return messageReceivedErrors;
    }

    /**
     * Sets the message received errors.
     *
     * @param messageReceivedErrors the new message received errors
     */
    public void setMessageReceivedErrors(ArrayList<String> messageReceivedErrors) {
        this.messageReceivedErrors = messageReceivedErrors;
    }

    /**
     * Checks if is message values verified.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is message values verified
     */
    public boolean isMessageValuesVerified() {
        return messageValuesVerified;
    }

    /**
     * Sets the message values verified.
     *
     * @param messageValuesVerified the new message values verified
     */
    public void setMessageValuesVerified(boolean messageValuesVerified) {
        this.messageValuesVerified = messageValuesVerified;
    }

    /**
     * Gets the message values verified errors.
     *
     * @return the message values verified errors
     */
    public ArrayList<String> getMessageValuesVerifiedErrors() {
        return messageValuesVerifiedErrors;
    }

    /**
     * Sets the message values verified errors.
     *
     * @param messageValuesVerifiedErrors the new message values verified errors
     */
    public void setMessageValuesVerifiedErrors(ArrayList<String> messageValuesVerifiedErrors) {
        this.messageValuesVerifiedErrors = messageValuesVerifiedErrors;
    }

    /**
     * Checks if is one message received.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is one message received
     */
    public boolean isOneMessageReceived() {
        return oneMessageReceived;
    }

    /**
     * Sets the one message received.
     *
     * @param oneMessageReceived the new one message received
     */
    public void setOneMessageReceived(boolean oneMessageReceived) {
        this.oneMessageReceived = oneMessageReceived;
    }

    /**
     * Gets the other errors.
     *
     * @return the other errors
     */
    public ArrayList<String> getOtherErrors() {
        return otherErrors;
    }

    /**
     * Sets the other errors.
     *
     * @param otherErrors the new other errors
     */
    public void setOtherErrors(ArrayList<String> otherErrors) {
        this.otherErrors = otherErrors;
    }

    /**
     * Checks if is other errors encountered.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is other errors encountered
     */
    public boolean isOtherErrorsEncountered() {
        return otherErrorsEncountered;
    }

    /**
     * Sets the other errors encountered.
     *
     * @param otherErrorsEncountered the new other errors encountered
     */
    public void setOtherErrorsEncountered(boolean otherErrorsEncountered) {
        this.otherErrorsEncountered = otherErrorsEncountered;
    }

    /**
     * Gets the parsing errors.
     *
     * @return the parsing errors
     */
    public ArrayList<String> getParsingErrors() {
        return parsingErrors;
    }

    /**
     * Sets the parsing errors.
     *
     * @param parsingErrors the new parsing errors
     */
    public void setParsingErrors(ArrayList<String> parsingErrors) {
        this.parsingErrors = parsingErrors;
    }

    /**
     * Checks if is parsing errors encountered.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is parsing errors encountered
     */
    public boolean isParsingErrorsEncountered() {
        return parsingErrorsEncountered;
    }

    /**
     * Sets the parsing errors encountered.
     *
     * @param parsingErrorsEncountered the new parsing errors encountered
     */
    public void setParsingErrorsEncountered(boolean parsingErrorsEncountered) {
        this.parsingErrorsEncountered = parsingErrorsEncountered;
    }

    /**
     * Gets the wrong message type errors.
     *
     * @return the wrong message type errors
     */
    public ArrayList<String> getWrongMessageTypeErrors() {
        return wrongMessageTypeErrors;
    }

    /**
     * Sets the wrong message type errors.
     *
     * @param wrongMessageTypeErrors the new wrong message type errors
     */
    public void setWrongMessageTypeErrors(ArrayList<String> wrongMessageTypeErrors) {
        this.wrongMessageTypeErrors = wrongMessageTypeErrors;
    }

    /**
     * Checks if is wrong message type received.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return true, if is wrong message type received
     */
    public boolean isWrongMessageTypeReceived() {
        return wrongMessageTypeReceived;
    }

    /**
     * Sets the wrong message type received flag.
     *
     * @param wrongMessageTypeReceived the new wrong message type received
     */
    public void setWrongMessageTypeReceived(boolean wrongMessageTypeReceived) {
        this.wrongMessageTypeReceived = wrongMessageTypeReceived;
    }

    /**
     * Gets the number of message parts received.
     *
     * @return the number of message parts received
     */
    public int getNumberOfMessagePartsReceived() {
        return numberOfMessagePartsReceived;
    }

    /**
     * Sets the number of message parts received.
     *
     * @param numberOfMessagePartsReceived the new number of message parts received
     */
    public void setNumberOfMessagePartsReceived(int numberOfMessagePartsReceived) {
        this.numberOfMessagePartsReceived = numberOfMessagePartsReceived;
    }


}
