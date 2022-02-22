/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package tmddv3verificationold.utilities;

/**
 *
 * @author TransCore ITS
 */
public class TestCaseInput {
    private String name;
    private String description;

    public TestCaseInput(String name, String description){
        this.name = name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString(){
        return name+": "+ description;
    }

}
