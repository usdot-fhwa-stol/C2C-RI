/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testcases;

/**
 *
 * @author TransCore ITS
 */
public class TestCaseOutput {
    private String name;
    private String description;

    public TestCaseOutput(String name, String description){
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
