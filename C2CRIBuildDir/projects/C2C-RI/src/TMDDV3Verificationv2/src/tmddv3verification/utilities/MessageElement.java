/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities;

/**
 *
 * @author TransCore ITS
 */
public class MessageElement {
    String parentMessage;
    String elementName;
    String elementRequirement;
    boolean optional;
    boolean messageLevel;
    String parentElement;
    String elementType;

    public String getParentMessage() {
        return parentMessage;
    }

    public void setParentMessage(String parentMessage) {
        this.parentMessage = parentMessage;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementRequirement() {
        return elementRequirement;
    }

    public void setElementRequirement(String elementRequirement) {
        this.elementRequirement = elementRequirement;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public boolean isMessageLevel() {
        return messageLevel;
    }

    public void setMessageLevel(boolean messageLevel) {
        this.messageLevel = messageLevel;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getParentElement() {
        return parentElement;
    }

    public void setParentElement(String parentElement) {
        this.parentElement = parentElement;
    }


}
