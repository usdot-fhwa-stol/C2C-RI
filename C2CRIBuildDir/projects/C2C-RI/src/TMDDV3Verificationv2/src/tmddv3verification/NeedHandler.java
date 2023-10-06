/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmddv3verification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import tmddv3verification.testing.TestResult;

/**
 *
 * @author TransCore ITS
 */
public final class NeedHandler {

    private Need need;
    private Integer messagesCount;
    private Integer dialogsCount;
    private Integer framesCount;
    private Integer requirementsCount;
    private Integer elementsCount;
    private HashMap<String, List<DesignItem>> messageMap = new HashMap<String, List<DesignItem>>();
    private HashMap<String, List<DesignItem>> frameMap = new HashMap<String, List<DesignItem>>();
    private HashMap<String, List<DesignItem>> elementMap = new HashMap<String, List<DesignItem>>();
    private List<TestResult> errorList = new ArrayList<TestResult>();

    private NeedHandler() {
    }

    public NeedHandler(Need theNeed) {
        need = theNeed;
        List<String> theMessages = getNeedRelatedMessages();
        messagesCount = theMessages.size();
        dialogsCount = getNeedRelatedDialogs().size();
        framesCount = getNeedRelatedFrames().size();
        requirementsCount = need.getRequirementList().size();
        elementsCount = getNeedRelatedElements().size();
        for (String theMessage : theMessages) {
            errorList.clear();
            buildMessage(theMessage);
        }
        List<String> theFrames = getNeedRelatedFrames();
        for (String theFrame : theFrames) {
//            errorList.clear();
            buildFrame(theFrame);
        }

        List<String> theElements = getNeedRelatedElements();
        for (String theElement : theElements) {
//            errorList.clear();
            buildElement(theElement);
        }

    }

    public List<TestResult> getErrorList() {
        return errorList;
    }

    public Integer getDialogsCount() {
        return dialogsCount;
    }

    public Integer getFramesCount() {
        return framesCount;
    }

    public Integer getMessagesCount() {
        return messagesCount;
    }

    public Integer getElementsCount() {
        return elementsCount;
    }

    public void buildMessage(String messageName) {
        Integer requirementIndex = 0;
        List<DesignItem> messageList = new ArrayList<DesignItem>();
        Boolean messageStarted = false;
        Boolean messageEnded = false;
        Integer messageIndex = 0;
        Integer needDataConceptIndex = 0;
        String messageType = "";
        boolean messageFound = false;

        for (Requirement theRequirement : need.getRequirementList()) {
            Integer reqDataConceptIndex = 0;

            // If we've reached the next message, break out of the loop
            if (messageEnded) {
                break;
            }
            if (messageType.isEmpty()) {  // This is the first message type encountered
                messageType = theRequirement.getRequirementType();
                messageList.clear();
            } else if (!theRequirement.getRequirementType().equals(messageType)) {  //The message type has changed
                if (messageFound) {
                    messageEnded = true;
                    break;
                } else {
                    messageType = theRequirement.getRequirementType();
                    messageList.clear();
                }
            }

            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                needDataConceptIndex = needDataConceptIndex + 1;

                if (!theRequirement.getRequirementType().isEmpty()) {

                    if ((theConcept.getDataConceptType().equals("message")) && (theConcept.getDataConceptName().equals(messageName))) {
                        DesignItem messageItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                        messageList.add(messageItem);
                        if (messageStarted) {
                            TestResult newResult = new TestResult();
                            newResult.setNeedID(need.getUnID());
                            newResult.setNeedText(need.getUserNeed());
                            newResult.setErrorDescription("The message " + theConcept.getDataConceptName() + " related to requirement " + theRequirement.getRequirementID() + " is a duplicate for satisfaction of need " + need.getUnID());
                            errorList.add(newResult);
                        } else {
                            messageStarted = true;
                        }
                        messageFound = true;
                        messageIndex = needDataConceptIndex;

                    } else if ((theConcept.getDataConceptType().equals("data-frame")) && (theRequirement.getRequirementType().equals(messageType))) {
                        // The data frame should be part of the message.
                        DesignItem frameItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                        messageList.add(frameItem);
                    } else if (theConcept.getDataConceptType().equals("data-element") && (theRequirement.getRequirementType().equals(messageType))) {
                        // The data frame should be part of the message.
                        DesignItem elementItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                        messageList.add(elementItem);
                    }

                } else {

                    if ((theConcept.getDataConceptType().equals("message")) && (theConcept.getDataConceptName().equals(messageName))) {
                        DesignItem messageItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                        messageList.add(messageItem);
                        if (messageStarted) {
                            TestResult newResult = new TestResult();
                            newResult.setNeedID(need.getUnID());
                            newResult.setNeedText(need.getUserNeed());
                            newResult.setErrorDescription("The message " + theConcept.getDataConceptName() + " related to requirement " + theRequirement.getRequirementID() + " is a duplicate for satisfaction of need " + need.getUnID());
                            errorList.add(newResult);
                        } else {
                            messageStarted = true;
                        }
                        messageIndex = needDataConceptIndex;
                    } else if (messageStarted) {
                        if ((theConcept.getDataConceptType().equals("message"))) {
                            messageEnded = true;

                        } else if (needDataConceptIndex == (messageIndex + 1)) {  // Is this the next data concept after the message element?
                            if ((theConcept.getDataConceptType().equals("data-frame")) && (theRequirement.getRequirement().startsWith("Contents of"))) {
                                // The data frame should specify the frame used for the message.
                                DesignItem frameItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                                messageList.add(frameItem);
                            }
                        } else if ((theConcept.getDataConceptType().equals("data-frame")) && (!theRequirement.getRequirement().startsWith("Contents of"))) {
                            // The data frame should be part of the message.
                            DesignItem frameItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                            messageList.add(frameItem);
                        } else if (theConcept.getDataConceptType().equals("data-element")) {
                            // The data frame should be part of the message.
                            DesignItem elementItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                            messageList.add(elementItem);
                        }
                    }
                }
                // If we've reached the next message, break out of the loop
                if (messageEnded) {
                    break;
                }
                reqDataConceptIndex = reqDataConceptIndex + 1;

            }
            requirementIndex = requirementIndex + 1;

        }

        if (messageList.size() > 0) {
            messageMap.put(messageName, messageList);
        }
    }

    public void buildFrame(String frameName) {
        Integer requirementIndex = 0;
        List<DesignItem> frameList = new ArrayList<DesignItem>();
        Boolean frameStarted = false;
        Boolean frameEnded = false;
        Integer frameIndex = 0;
        Integer needDataConceptIndex = 0;

        for (Requirement theRequirement : need.getRequirementList()) {
            Integer reqDataConceptIndex = 0;

            // If we've reached the next frame, break out of the loop
            if (frameEnded) {
                break;
            }

            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                needDataConceptIndex = needDataConceptIndex + 1;
                if ((theConcept.getDataConceptType().equals("data-frame")) && (theConcept.getDataConceptName().equals(frameName)) && (theRequirement.getRequirement().startsWith("Contents of"))) {
                    DesignItem frameItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                    frameList.add(frameItem);
                    if (frameStarted) {
                        TestResult newResult = new TestResult();
                        newResult.setNeedID(need.getUnID());
                        newResult.setNeedText(need.getUserNeed());
                        newResult.setErrorDescription("The frame " + theConcept.getDataConceptName() + " related to requirement " + theRequirement.getRequirementID() + " is a duplicate for satisfaction of need " + need.getUnID());
                        errorList.add(newResult);
                    } else {
                        frameStarted = true;
                    }
                    frameIndex = needDataConceptIndex;
                } else if (frameStarted) {
                    if ((theConcept.getDataConceptType().equals("data-frame")) && (theRequirement.getRequirement().startsWith("Contents of"))) {
                        frameEnded = true;

                    } else if (theConcept.getDataConceptType().equals("message")) {
                        frameEnded = true;

                    } else if (theConcept.getDataConceptType().equals("dialog")) {
                        frameEnded = true;


                    } else if ((theConcept.getDataConceptType().equals("data-frame")) && (!theRequirement.getRequirement().startsWith("Contents of"))) {
                        // The data frame should be part of the message.
                        DesignItem frameItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                        frameList.add(frameItem);
                    } else if (theConcept.getDataConceptType().equals("data-element")) {
                        // The data frame should be part of the message.
                        DesignItem elementItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                        frameList.add(elementItem);
                    }
                }
                // If we've reached the next message, break out of the loop
                if (frameEnded) {
                    break;
                }
                reqDataConceptIndex = reqDataConceptIndex + 1;

            }
            requirementIndex = requirementIndex + 1;

        }

        if (frameList.size() > 0) {
            frameMap.put(frameName, frameList);
        }
    }

    public void buildElement(String elementName) {
        Integer requirementIndex = 0;
        List<DesignItem> elementList = new ArrayList<DesignItem>();
        Integer elementIndex = 0;
        Integer needDataConceptIndex = 0;

        for (Requirement theRequirement : need.getRequirementList()) {
            Integer reqDataConceptIndex = 0;

            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                needDataConceptIndex = needDataConceptIndex + 1;
                if ((theConcept.getDataConceptType().equals("data-element")) && (theConcept.getDataConceptName().equals(elementName))) {
                    DesignItem elementItem = new DesignItem(requirementIndex, reqDataConceptIndex);
                    elementList.add(elementItem);

                    elementIndex = needDataConceptIndex;

                }

                reqDataConceptIndex = reqDataConceptIndex + 1;

            }
            requirementIndex = requirementIndex + 1;

        }

        if (elementList.size() > 0) {
            elementMap.put(elementName, elementList);
        }
    }

    public Integer getRequirementsCount() {
        return requirementsCount;
    }

    public Requirement getRequirement(Integer requirementIndex) {
        return need.getRequirementList().get(requirementIndex);
    }

    public Requirement getRequirement(String requirementID) throws Exception {
        for (Requirement thisRequirement : need.getRequirementList()) {
            if (thisRequirement.getRequirementID().equals(requirementID)) {
                return thisRequirement;
            }
        }
        throw new Exception("The requirement ID " + requirementID + " does not exist for this need.");
    }

    public DataConcept getDataConcept(Integer requirementIndex, Integer dataConceptIndex) {
        return need.getRequirementList().get(requirementIndex).getDataConceptList().get(dataConceptIndex);
    }

    public String getMessageConceptElement(String messageName, Integer index) throws Exception {
        if (messageMap.containsKey(messageName)) {
            List<DesignItem> messageList = messageMap.get(messageName);
            Integer reqIndex = messageList.get(index).getRequirementIndex();
            Integer dataConceptIndex = messageList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getDataConceptList().get(dataConceptIndex).getDataConceptName();
        } else {
            throw new Exception("No Message Element Match found.");
        }

    }

    public DataConcept getMessageDataConcept(String messageName, Integer index) throws Exception {
        if (messageMap.containsKey(messageName)) {
            List<DesignItem> messageList = messageMap.get(messageName);
            Integer reqIndex = messageList.get(index).getRequirementIndex();
            Integer dataConceptIndex = messageList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getDataConceptList().get(dataConceptIndex);
        } else {
            throw new Exception("No Message Element Match found.");
        }

    }

    public String getFrameConceptElement(String frameName, Integer index) throws Exception {
        if (frameMap.containsKey(frameName)) {
            List<DesignItem> frameList = frameMap.get(frameName);
            Integer reqIndex = frameList.get(index).getRequirementIndex();
            Integer dataConceptIndex = frameList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getDataConceptList().get(dataConceptIndex).getDataConceptName();
        } else {
            throw new Exception("No Frame Element Match found.");
        }

    }

    public DataConcept getFrameDataConcept(String frameName, Integer index) throws Exception {
        if (frameMap.containsKey(frameName)) {
            List<DesignItem> frameList = frameMap.get(frameName);
            Integer reqIndex = frameList.get(index).getRequirementIndex();
            Integer dataConceptIndex = frameList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getDataConceptList().get(dataConceptIndex);
        } else {
            throw new Exception("No Frame Element Match found.");
        }

    }

    public String getElementConceptElement(String elementName, Integer index) throws Exception {
        if (frameMap.containsKey(elementName)) {
            List<DesignItem> elementList = frameMap.get(elementName);
            Integer reqIndex = elementList.get(index).getRequirementIndex();
            Integer dataConceptIndex = elementList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getDataConceptList().get(dataConceptIndex).getDataConceptName();
        } else {
            throw new Exception("No Element Match found for element " + elementName + " with index " + index + ".");
        }

    }

    public DataConcept getElementConcept(String elementName, Integer index) throws Exception {
        if (frameMap.containsKey(elementName)) {
            List<DesignItem> elementList = frameMap.get(elementName);
            Integer reqIndex = elementList.get(index).getRequirementIndex();
            Integer dataConceptIndex = elementList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getDataConceptList().get(dataConceptIndex);
        } else {
            throw new Exception("No Element Match found for element " + elementName + " with index " + index + ".");
        }

    }

    public String getMessageConceptElementType(String messageName, Integer index) throws Exception {
        if (messageMap.containsKey(messageName)) {
            List<DesignItem> messageList = messageMap.get(messageName);
            Integer reqIndex = messageList.get(index).getRequirementIndex();
            Integer dataConceptIndex = messageList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getDataConceptList().get(dataConceptIndex).getDataConceptType();
        } else {
            throw new Exception("No Message Element Match found.");
        }

    }

    public String getMessageConceptElementConformance(String messageName, Integer index) throws Exception {
        if (messageMap.containsKey(messageName)) {
            List<DesignItem> messageList = messageMap.get(messageName);
            Integer reqIndex = messageList.get(index).getRequirementIndex();
            Integer dataConceptIndex = messageList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getConformance();
        } else {
            throw new Exception("No Message Element Match found.");
        }

    }

    public String getMessageConceptElementRequirementID(String messageName, Integer index) throws Exception {
        if (messageMap.containsKey(messageName)) {
            List<DesignItem> messageList = messageMap.get(messageName);
            Integer reqIndex = messageList.get(index).getRequirementIndex();
            Integer dataConceptIndex = messageList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getRequirementID();
        } else {
            throw new Exception("No Message Element Match found.");
        }

    }

    public String getFrameConceptElementRequirementID(String frameName, Integer index) throws Exception {
        if (frameMap.containsKey(frameName)) {
            List<DesignItem> frameList = frameMap.get(frameName);
            Integer reqIndex = frameList.get(index).getRequirementIndex();
            Integer dataConceptIndex = frameList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getRequirementID();
        } else {
            throw new Exception("No Frame Element Match found.");
        }

    }

    public String getElementConceptElementRequirementID(String elementName, Integer index) throws Exception {
        if (elementMap.containsKey(elementName)) {
            List<DesignItem> elementList = elementMap.get(elementName);
            Integer reqIndex = elementList.get(index).getRequirementIndex();
            Integer dataConceptIndex = elementList.get(index).getDataConceptIndex();

            return need.getRequirementList().get(reqIndex).getRequirementID();
        } else {
            throw new Exception("No Element Match found.");
        }

    }

    public Integer getMessageConceptElementCount(String messageName) throws Exception {
        if (messageMap.containsKey(messageName)) {
            List<DesignItem> messageList = messageMap.get(messageName);
            return messageList.size();
        } else {
            throw new Exception("No Message Element Match found for " + messageName + ".");
        }
    }

    public Integer getFrameConceptElementCount(String frameName) throws Exception {
        if (frameMap.containsKey(frameName)) {
            List<DesignItem> frameList = frameMap.get(frameName);
            return frameList.size();
        } else {
            throw new Exception("No Frame Element Match found for " + frameName + ".");
        }
    }

    public Integer getElementConceptElementCount(String elementName) throws Exception {
        if (elementMap.containsKey(elementName)) {
            List<DesignItem> elementList = elementMap.get(elementName);
            return elementList.size();
        } else {
            throw new Exception("No Element Match found for " + elementName + ".");
        }
    }

    public boolean isFrameDefinedWithinNeed(String frameName) {
        if (frameMap.containsKey(frameName)) {
            List<DesignItem> frameList = frameMap.get(frameName);
            return (frameList.size() > 0);
        } else {
            return false;
        }
    }

    public List<String> getNeedRelatedMessages() {
        List<String> messageList = new ArrayList();
        for (Requirement theRequirement : need.getRequirementList()) {
            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                if (theConcept.getDataConceptType().equals("message")) {
                    if (!messageList.contains(theConcept.getDataConceptName()))
                        messageList.add(theConcept.getDataConceptName());                    
                }
            }
        }
        return messageList;
    }

    public List getNeedRelatedFrames() {
        List<String> frameList = new ArrayList();
        for (Requirement theRequirement : need.getRequirementList()) {
            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                if (theConcept.getDataConceptType().equals("data-frame")) {
                    if (!frameList.contains(theConcept.getDataConceptName()))
                        frameList.add(theConcept.getDataConceptName());
                }
            }
        }
        return frameList;
    }

    public List getNeedRelatedElements() {
        List<String> elementList = new ArrayList();
        for (Requirement theRequirement : need.getRequirementList()) {
            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                if (theConcept.getDataConceptType().equals("data-element")) {
                    if (!elementList.contains(theConcept.getDataConceptName()))
					{
						elementList.add(theConcept.getDataConceptName());
					}
                }
            }
        }
        return elementList;
    }

    public List getNeedRelatedDialogs() {
        List<String> dialogList = new ArrayList();
        for (Requirement theRequirement : need.getRequirementList()) {
            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                if (theConcept.getDataConceptType().equals("dialog")) {
                    if (!dialogList.contains(theConcept.getDataConceptName()))
                       dialogList.add(theConcept.getDataConceptName());
                }
            }
        }
        return dialogList;

    }

    public List<Requirement> getDialogSpecificRequirements(String dialogName) {
        List<Requirement> requirementList = new ArrayList();
        for (Requirement theRequirement : need.getRequirementList()) {
            for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                if ((theConcept.getDataConceptType().equals("dialog"))
                        && (theConcept.getDataConceptName().equals(dialogName))) {
                    requirementList.add(theRequirement);
                }
            }
        }
        return requirementList;
    }

    public List<DataConcept> getRequirementRelatedDataConcepts(String reqID) {
        List<DataConcept> dataConceptList = new ArrayList();
        for (Requirement theRequirement : need.getRequirementList()) {
            if (theRequirement.getRequirementID().equals(reqID)) {
                for (DataConcept theConcept : theRequirement.getDataConceptList()) {
                    dataConceptList.add(theConcept);
                }
            }
        }
        return dataConceptList;
    }

    public String getNeedID() {
        return need.getUnID();
    }

    public String getNeedText() {
        return need.getUserNeed();
    }

    public String getNeedSelection() {
        return need.getUserNeedSelected();
    }

    /**
     * Represents a combination of a specific requirement and dataconcept item
     * for a given need.
     */
    class DesignItem {

        private Integer requirementIndex;
        private Integer dataConceptIndex;

        private DesignItem() {
        }

        /**
         * Constructor for the DesignItem Object
         *
         * @param requirementIndex - the index of the specific requirement
         * related to the need
         * @param reqDataConceptIndex - the index of the specific dataConcept
         * related to the requirement
         */
        public DesignItem(Integer requirementIndex, Integer dataConceptIndex) {
            this.requirementIndex = requirementIndex;
            this.dataConceptIndex = dataConceptIndex;
        }

        /**
         *
         * @return the index for this dataconcept within the total list of
         * related dataconcepts
         */
        public Integer getDataConceptIndex() {
            return dataConceptIndex;
        }

        /**
         *
         * @param reqDataConceptIndex - the index for this dataconcept within
         * the total list of related dataconcepts
         */
        public void setDataConceptIndex(Integer dataConceptIndex) {
            this.dataConceptIndex = dataConceptIndex;
        }

        /**
         *
         * @return the index for this requirement within the total list of
         * related requirements
         */
        public Integer getRequirementIndex() {
            return requirementIndex;
        }

        /**
         *
         * @param requirementIndex - the index for this requirement within the
         * total list of related requirements
         */
        public void setRequirementIndex(Integer requirementIndex) {
            this.requirementIndex = requirementIndex;
        }
    }
}
