/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification;

/**
 *
 * @author TransCore ITS
 */
public class DataConcept {
    private Integer index;
    private String dataConceptName="";
    private String dataConceptType="";
    private String standardsClause="";
    private String conceptReferenceName="";
    private String conceptPath="";
    private String instanceName="";

    public String getConceptPath() {
        return conceptPath;
    }

    public void setConceptPath(String conceptPath) {
        this.conceptPath = conceptPath;
    }

    public String getConceptReferenceName() {
        return conceptReferenceName;
    }

    public void setConceptReferenceName(String conceptReferenceName) {
        this.conceptReferenceName = conceptReferenceName;
    }

    public String getDataConceptName() {
        return dataConceptName;
    }

    public void setDataConceptName(String dataConceptName) {
        this.dataConceptName = dataConceptName;
    }

    public String getDataConceptType() {
        return dataConceptType;
    }

    public void setDataConceptType(String dataConceptType) {
        this.dataConceptType = dataConceptType;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getStandardsClause() {
        return standardsClause;
    }

    public void setStandardsClause(String standardsClause) {
        this.standardsClause = standardsClause;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }


}
