/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.tmdd.emulation.cctv;

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
 * This class represents the set of CCTV Status entity information for the
 * center. The information that is maintained is described in TMDD Volume II -
 * Section 3.2.2.2.
 *
 * @author TransCore ITS, LLC Created: Jan 31, 2016
 */
public class CCTVStatus extends TMDDDefaultEntityDataType{

    public CCTVStatus (EntityEmulationData.EntityDataType entityDataType){
        super(entityDataType);
    }

    public static void main(String[] args) throws Exception {
        boolean writeFile = Boolean.parseBoolean(args[0]);
        boolean readFile = Boolean.parseBoolean(args[1]);
        boolean updateValues = Boolean.parseBoolean(args[2]);

        CCTVStatus cctv = new CCTVStatus(EntityEmulationData.EntityDataType.CCTVSTATUS);


        if (!readFile) {
            cctv.initialize(null);
        } else {
        System.out.println(EmulationDataFileProcessor.getContent(EntityEmulationData.EntityDataType.CCTVSTATUS + ".out").toString());
            cctv.initialize(EmulationDataFileProcessor.getByteArray(EntityEmulationData.EntityDataType.CCTVSTATUS + ".out"));
        }

        if (updateValues) {
            cctv.addEntity("cctv-123456789123456789012345674");
            cctv.addEntity("cctv-123456789123456789012345675");
            cctv.addEntity("cctv-123456789123456789012345676");
            cctv.addEntity("cctv-123456789123456789012345677");
            cctv.addEntity("cctv-123456789123456789012345678");
            cctv.addEntity("cctv-123456789123456789012345679");
            cctv.deleteEntity("cctv-123456789123456789012345678");
            cctv.deleteEntity("cctv-123456789123456789012345674");
            cctv.addEntityElement("cctv-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image", "jpeg");
            cctv.updateEntityElement("cctv-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image", "tiff");
            System.out.println(cctv.getEntityElementValue("cctv-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image"));
            cctv.deleteEntityElement("cctv-123456789123456789012345675", "tmdd:cCTVStatusMsg.cctv-status-item.cctv-image-list[2].cctv-image");
        }

        ArrayList<DataFilter> theFilters = new ArrayList();
        ArrayList<EntityDataRecord> results = cctv.getEntityElements(theFilters);
        System.out.println("Records Returned = " + results.size());

        ArrayList<String> filterValues = new ArrayList();
        filterValues.add("cctv-123456789123456789012345676");
        filterValues.add("cctv-123456789123456789012345675");
        ValueInSetFilter firstFilter = new ValueInSetFilter(EntityEmulationData.EntityDataType.CCTVSTATUS, "EntityId", filterValues);
        theFilters.add(firstFilter);
        results = cctv.getEntityElements(theFilters);
        System.out.println("Records Returned = " + results.size());

        if (writeFile) {
            EntityDataFile.writeFile(EntityEmulationData.EntityDataType.CCTVSTATUS + ".out", results);
        }
    }

}
