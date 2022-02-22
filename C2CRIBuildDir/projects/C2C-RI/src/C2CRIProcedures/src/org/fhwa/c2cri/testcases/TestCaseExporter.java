/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testcases;

/**
 *
 * @author TransCore ITS
 */
public interface TestCaseExporter {
    public void toDatabase();
    public void toTestCaseFile(String path, String fileName);
    public void toTestCaseMatrix();
}
