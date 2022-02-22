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
public class Predicate {
    private String predicateName;
    private String section;
    private List<DataConcept> dataConceptList = new ArrayList<DataConcept>();

    public List<DataConcept> getDataConceptList() {
        return dataConceptList;
    }

    public void setDataConceptList(List<DataConcept> dataConceptList) {
        this.dataConceptList = dataConceptList;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public void setPredicateName(String predicateName) {
        this.predicateName = predicateName;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }


}
