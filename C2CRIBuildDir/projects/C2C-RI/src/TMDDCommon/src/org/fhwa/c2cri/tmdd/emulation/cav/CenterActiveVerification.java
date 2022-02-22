/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.cav;

import java.util.ArrayList;
import org.fhwa.c2cri.centermodel.emulation.exceptions.EntityEmulationException;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.tmdd.emulation.TMDDDefaultEntityDataType;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataFile;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataInformationCollector;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;
import org.fhwa.c2cri.tmdd.emulation.exceptions.DuplicateEntityIdException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityElementException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidEntityIdValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.InvalidValueException;
import org.fhwa.c2cri.tmdd.emulation.exceptions.NoMatchingDataException;

/**
 * This class represents the set of CCTV Inventory entity information for the
 * center. The information that is maintained is described in TMDD Volume II -
 * Section 3.2.2.2.
 *
 * @author TransCore ITS, LLC Created: Jan 31, 2016
 */
public class CenterActiveVerification extends TMDDDefaultEntityDataType{

    public CenterActiveVerification(EntityEmulationData.EntityDataType entityDataType){
        super(entityDataType);
    }

    public static void main(String[] args) throws Exception {
        boolean writeFile = false;
        boolean readFile = true;
        boolean updateValues = false;

        CenterActiveVerification cav = new CenterActiveVerification(EntityEmulationData.EntityDataType.CENTERACTIVEVERIFICATION);

        System.out.println(EmulationDataFileProcessor.getContent(EntityEmulationData.EntityDataType.CENTERACTIVEVERIFICATION + ".out").toString());

        if (!readFile) {
            cav.initialize(null);
        } else {
            cav.initialize(EmulationDataFileProcessor.getByteArray(EntityEmulationData.EntityDataType.CENTERACTIVEVERIFICATION + ".out"));
        }

        if (updateValues) {
            cav.updateEntityElement("cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image", "tiff");
            System.out.println(cav.getEntityElementValue("cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image"));
            cav.deleteEntityElement("cctv-123456789123456789012345675", "tmdd:cCTVInventoryMsg.cctv-inventory-item.cctv-image-list[2].cctv-image");
        }

        ArrayList<DataFilter> theFilters = new ArrayList();
        ArrayList<EntityDataRecord> results = cav.getEntityElements(theFilters);
        System.out.println("Records Returned = " + results.size());

        ArrayList<String> filterValues = new ArrayList();
        ValueInSetFilter firstFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.CENTERACTIVEVERIFICATION, "EntityId", filterValues);
        theFilters.add(firstFilter);
        results = cav.getEntityElements(theFilters);
        System.out.println("Records Returned = " + results.size());

        if (writeFile) {
            EntityDataFile.writeFile(EntityEmulationData.EntityDataType.CENTERACTIVEVERIFICATION + ".out", results);
        }
    }
}
