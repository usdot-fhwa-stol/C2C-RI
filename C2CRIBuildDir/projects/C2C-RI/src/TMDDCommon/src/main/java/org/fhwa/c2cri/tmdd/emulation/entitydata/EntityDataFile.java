/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.entitydata;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;

/**
 *
 * @author TransCore ITS, LLC Created: Feb 9, 2016
 */
public class EntityDataFile {

    
    public static void writeFile(String fileName, ArrayList<EntityDataRecord> entityRecords) throws EntityEmulationException {
        try {
            ByteArrayOutputStream bOutput = new ByteArrayOutputStream();
            FileOutputStream fos = new FileOutputStream(fileName);
            boolean firstLine = true;
            for (EntityDataRecord thisRecord : entityRecords) {
                String tmpString = (firstLine ? "" : "\n") + thisRecord.getEntityElement() + " = " + thisRecord.getEntityElementValue();
                bOutput.write(tmpString.getBytes());
                firstLine = false;
            };
            bOutput.writeTo(fos);
            fos.close();
        } catch (FileNotFoundException ex) {
            throw new EntityEmulationException(ex);
        } catch (IOException ex) {
            throw new EntityEmulationException(ex);
        }
    }
}
