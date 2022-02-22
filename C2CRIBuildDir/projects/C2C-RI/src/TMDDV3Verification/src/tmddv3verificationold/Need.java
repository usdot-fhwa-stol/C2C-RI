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
public class Need {
    private Integer needNumber;
    private String unID;
    private String userNeed;
    private String userNeedSelected;
    private List<Requirement> requirementList = new ArrayList<Requirement>();

    public List<Requirement> getRequirementList() {
        return requirementList;
    }

    public void setRequirementList(List<Requirement> requirementList) {
        this.requirementList = requirementList;
    }

    public String getUnID() {
        return unID;
    }

    public void setUnID(String unID) {
        this.unID = unID;
    }

    public String getUserNeed() {
        return userNeed;
    }

    public void setUserNeed(String userNeed) {
        this.userNeed = userNeed;
    }

    public String getUserNeedSelected() {
        return userNeedSelected;
    }

    public void setUserNeedSelected(String userNeedSelected) {
        this.userNeedSelected = userNeedSelected;
    }

    public Integer getNeedNumber() {
        return needNumber;
    }

    public void setNeedNumber(Integer needNumber) {
        this.needNumber = needNumber;
    }


}
