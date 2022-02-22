/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.messaging;

import org.fhwa.c2cri.infolayer.MessageProvider;
import org.fhwa.c2cri.messagemanager.Message;

/**
 * Acts as a MessageProvider to objects that need to access an NTCIP2306Message object as a standard Message object.
 * @author TransCore ITS
 */
public class NTCIP2306MessageProvider implements MessageProvider{

    NTCIP2306Message ntcip2306Message;
    String operationName;
    
    public NTCIP2306MessageProvider(String operation, NTCIP2306Message message){
        ntcip2306Message = message;
        operationName = operation;
    }
    @Override
    public Message getMessage() {
        return C2CRIMessageAdapter.toC2CRIMessage(operationName, ntcip2306Message);
    }
    
}
