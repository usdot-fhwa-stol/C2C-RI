/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.testprocedures;

/**
 *
 * @author TransCore ITS
 */
public interface ProcedureExporter {
    public void toDatabase();
    public void toScriptFile(String path, String fileName);
}
