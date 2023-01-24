/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;


/**
 * The Class TMDDMessageResults.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TMDDMessageResults {
    
    /** The num test result fields. */
    private static int NUM_TEST_RESULT_FIELDS = 6;
    
    /** The message results. */
    private ArrayList<MessageResult> messageResults = new ArrayList();   
    
    
    /**
     * Instantiates a new tMDD message results.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param testResults the test results
     */
    public TMDDMessageResults(String testResults){
        BufferedReader bfReader = new BufferedReader(new StringReader(testResults));
        try{
        String line = bfReader.readLine();
        while (line != null){
            MessageResult msgResult = new MessageResult();
            String[] stringArray = line.split(",");
            if (stringArray.length == NUM_TEST_RESULT_FIELDS){
                System.out.println("Requirement: "+stringArray[0]);
                msgResult.setRequirement(stringArray[0]);
                System.out.println("Requirement Result: "+stringArray[1]);
                msgResult.setRequirementResult(Boolean.valueOf(stringArray[1]));
                System.out.println("Message: "+stringArray[2]);
                msgResult.setMessage(stringArray[2]);
                System.out.println("Element: "+stringArray[3]);
                msgResult.setElement(stringArray[3]);
                System.out.println("Element Result: "+stringArray[4]);
                msgResult.setElementResult(Boolean.valueOf(stringArray[4]));
                System.out.println("Error Description: "+(stringArray[5]==null?"":stringArray[5]));
                msgResult.setErrorDescription(stringArray[5]==null?"":stringArray[5]);
                getMessageResults().add(msgResult);
            }
            System.out.println(stringArray.length);
            line = bfReader.readLine();
        }
        } catch(Exception ex){
            ex.printStackTrace();
        }
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
