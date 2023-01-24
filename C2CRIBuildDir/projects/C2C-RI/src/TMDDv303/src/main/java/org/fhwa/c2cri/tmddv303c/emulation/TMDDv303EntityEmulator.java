/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmddv303c.emulation;

import java.io.InputStream;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import org.fhwa.c2cri.tmdd.emulation.TMDDEntityEmulator;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
@PluginImplementation
public class TMDDv303EntityEmulator
        extends TMDDEntityEmulator {

    @Override
    public String getDatabaseFileName() {
        return "TMDDv303SQLLite.db3";
    }

    @Override
    public InputStream getDatabaseStream() {
        return this.getClass().getResourceAsStream("/org/fhwa/c2cri/tmddv303c/dbase/TMDDv303SQLLite.db3");
    }

    @Override
    protected String getTMDDEmulatorStandard() {
        return "TMDD v3.03c"; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTMDDSchemaDetailTableName(){
        return "TMDDv303SchemaDetail";
    }
}
