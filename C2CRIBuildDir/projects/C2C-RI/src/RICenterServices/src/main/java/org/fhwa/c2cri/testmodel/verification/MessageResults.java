/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.testmodel.verification;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;


/**
 * The Class MessageResults captures the results from the inspection of a message.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class MessageResults {
    
    /** The num test result fields. */
    private static int NUM_TEST_RESULT_FIELDS = 6;
    
    /** The message results. */
    private ArrayList<MessageResult> messageResults = new ArrayList();   
    
    
    /**
     * Instantiates a new message results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testResults the test results
     */
    public MessageResults(String testResults){
        BufferedReader bfReader = new BufferedReader(new StringReader(testResults));
        try{
        String line = bfReader.readLine();
        while (line != null){
            MessageResult msgResult = new MessageResult();
            String[] stringArray = line.split(",");
            if (stringArray.length == NUM_TEST_RESULT_FIELDS){
//                System.out.println("Requirement: "+stringArray[0]);
                msgResult.setRequirement(stringArray[0]);
//                System.out.println("Requirement Result: "+stringArray[1]);
                msgResult.setRequirementResult(Boolean.valueOf(stringArray[1]));
//                System.out.println("Message: "+stringArray[2]);
                msgResult.setMessage(stringArray[2]);
//                System.out.println("Element: "+stringArray[3]);
                msgResult.setElement(stringArray[3]);
//                System.out.println("Element Result: "+stringArray[4]);
                msgResult.setElementResult(Boolean.valueOf(stringArray[4]));
//                System.out.println("Error Description: "+(stringArray[5]==null?"":stringArray[5]));
                msgResult.setErrorDescription(stringArray[5]==null?"":stringArray[5]);
                getMessageResults().add(msgResult);
            }
//            System.out.println(stringArray.length);
            line = bfReader.readLine();
        }
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }    

    /**
     * Gets the requirement errors.
     *
     * @param requirementID the requirement id
     * @return the requirement errors
     */
    public ArrayList<String> getRequirementErrors(String requirementID){
        ArrayList<String> returnList = new ArrayList();
        for (MessageResult thisResult: messageResults){
            if (thisResult.getRequirement().equals(requirementID)){
                if (!thisResult.isRequirementResult()){
                    if (!thisResult.isElementResult()){
                        returnList.add(thisResult.toString());
                    }
                }
            }
        }
        return returnList;
    }

    /**
     * Gets the all results.
     *
     * @return the all results
     */
    public ArrayList<String> getAllResults(){
        ArrayList<String> returnList = new ArrayList();
        for (MessageResult thisResult: messageResults){
           returnList.add(thisResult.toString());
        }
        return returnList;        
    }
    
    /**
     * Gets the message results.
     *
     * @return the message results
     */
    private ArrayList<MessageResult> getMessageResults() {
        return messageResults;
    }
    
    
    
    /**
     * The Class MessageResult.
     *
     * @author TransCore ITS, LLC
     * Last Updated:  1/8/2014
     */
    class MessageResult{
        
        /** The requirement. */
        private String requirement;
        
        /** The requirement result. */
        private boolean requirementResult;
        
        /** The message. */
        private String message;
        
        /** The element. */
        private String element;
        
        /** The element result. */
        private boolean elementResult;
        
        /** The error description. */
        private String errorDescription;

        /* (non-Javadoc)
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString(){
            StringBuffer resultString = new StringBuffer();
            resultString.append("Message: "+message);
            resultString.append("  Requirement: "+requirement);
            resultString.append("  Requirement Testing Passed?: "+requirementResult);
            resultString.append("  Element: "+element);
            resultString.append("  Element Testing Passed?: "+elementResult);
            if(!elementResult)
            resultString.append("  Error Description: "+errorDescription);            
            
            return resultString.toString();
        }
        
        /**
         * Gets the requirement.
         *
         * @return the requirement
         */
        public String getRequirement() {
            return requirement;
        }

        /**
         * Sets the requirement.
         *
         * @param requirement the new requirement
         */
        public void setRequirement(String requirement) {
            this.requirement = requirement;
        }

        /**
         * Checks if is requirement result.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @return true, if is requirement result
         */
        public boolean isRequirementResult() {
            return requirementResult;
        }

        /**
         * Sets the requirement result.
         *
         * @param requirementResult the new requirement result
         */
        public void setRequirementResult(boolean requirementResult) {
            this.requirementResult = requirementResult;
        }

        /**
         * Gets the message.
         *
         * @return the message
         */
        public String getMessage() {
            return message;
        }

        /**
         * Sets the message.
         *
         * @param message the new message
         */
        public void setMessage(String message) {
            this.message = message;
        }

        /**
         * Gets the element.
         *
         * @return the element
         */
        public String getElement() {
            return element;
        }

        /**
         * Sets the element.
         *
         * @param element the new element
         */
        public void setElement(String element) {
            this.element = element;
        }

        /**
         * Checks if is element result.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @return true, if is element result
         */
        public boolean isElementResult() {
            return elementResult;
        }

        /**
         * Sets the element result.
         *
         * @param elementResult the new element result
         */
        public void setElementResult(boolean elementResult) {
            this.elementResult = elementResult;
        }

        /**
         * Gets the error description.
         *
         * @return the error description
         */
        public String getErrorDescription() {
            return errorDescription;
        }

        /**
         * Sets the error description.
         *
         * @param errorDescription the new error description
         */
        public void setErrorDescription(String errorDescription) {
            this.errorDescription = errorDescription;
        }
        
        
        
    }
}
