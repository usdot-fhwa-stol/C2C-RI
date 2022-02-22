/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verificationold;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TransCore ITS
 */
public class Requirement {
    private Integer index;
    private String requirementID;
    private String requirement;
    private String conformance;
    private String selected;
    private String otherRequirement;
    private List<DataConcept> dataConceptList = new ArrayList<DataConcept>();

    public String getConformance() {
        return conformance;
    }

    public void setConformance(String conformance) {
        this.conformance = conformance;
    }

    public List<DataConcept> getDataConceptList() {
        return dataConceptList;
    }

    public void setDataConceptList(List<DataConcept> dataConceptList) {
        this.dataConceptList = dataConceptList;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getOtherRequirement() {
        return otherRequirement;
    }

    public void setOtherRequirement(String otherRequirement) {
        this.otherRequirement = otherRequirement;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public String getRequirementID() {
        return requirementID;
    }

    public void setRequirementID(String requirementID) {
        this.requirementID = requirementID;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }


}
