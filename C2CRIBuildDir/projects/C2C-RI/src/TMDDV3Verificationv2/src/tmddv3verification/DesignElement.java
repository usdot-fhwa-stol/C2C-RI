/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TransCore ITS
 */
public class DesignElement {
    private String elementName;
    private String elementSource;
    private String elementType;
    private String referenceName;
    private String subType;
    private String baseType;
    private int minOccurs;
    private int maxOccurs;
    private int minInclusive;
    private int maxInclusive;

    private List<DesignElement> subElements = new ArrayList<DesignElement>();

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementSource() {
        return elementSource;
    }

    public void setElementSource(String elementSource) {
        this.elementSource = elementSource;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public List<DesignElement> getSubElements() {
        return subElements;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public int getMaxInclusive() {
        return maxInclusive;
    }

    public void setMaxInclusive(int maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public int getMaxOccurs() {
        return maxOccurs;
    }

    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public int getMinInclusive() {
        return minInclusive;
    }

    public void setMinInclusive(int minInclusive) {
        this.minInclusive = minInclusive;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }




}
