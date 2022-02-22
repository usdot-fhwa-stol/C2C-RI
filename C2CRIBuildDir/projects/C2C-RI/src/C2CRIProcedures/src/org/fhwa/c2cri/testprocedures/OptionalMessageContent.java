/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testprocedures;

/**
 *
 * @author TransCore ITS
 */
public class OptionalMessageContent {
    private static OptionalMessageContent thisInstance;
    private static String optionalList = "";
    private OptionalMessageContent(){
    }
    
    public static OptionalMessageContent getInstance(){
        if (thisInstance == null){
            thisInstance = new OptionalMessageContent();
        }
        return thisInstance;
    }

    public String getOptionalList(){
        return optionalList;
    }

    public void setOptionalList(String optionalList){
        this.optionalList = this.optionalList.concat(optionalList);
    }

    public void clearOptionalList(){
        optionalList="";
    }

}
