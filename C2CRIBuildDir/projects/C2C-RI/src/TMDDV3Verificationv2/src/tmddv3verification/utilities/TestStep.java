/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verification.utilities;

/**
 *
 * @author TransCore ITS
 */
public class TestStep {
    
    private String id;
    private String description;
    private String results;

    public TestStep(){
    }

    public TestStep(String id, String description, String results){
        this.id = id;
        this.description = description;
        this.results = results;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }


}
