/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package test.generictests;

import java.util.ArrayList;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityDataRecord;
import org.fhwa.c2cri.tmdd.emulation.entitydata.EntityEmulationData;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.DataFilter;
import org.fhwa.c2cri.tmdd.emulation.entitydata.filters.ValueInSetFilter;

/**
 * 
 * @author TransCore ITS, LLC
 * Created: Mar 24, 2016
 */
public class EntityCountTest {
    public static boolean test(int expectedCount, EntityEmulationData.EntityDataType dataType, boolean printDetails){
        boolean result = false;

        try {
            int entityCount = EntityEmulationData.getEntityCount(dataType);
            if ( entityCount == expectedCount) result = true;
            if (!result && printDetails){
                System.out.println("EntityCountTest::  Expected Count = "+ expectedCount + " Actual Count = "+entityCount);
            }
        } catch (Exception ex){            
            result = false;
        }
        
        return result;
    }
}
