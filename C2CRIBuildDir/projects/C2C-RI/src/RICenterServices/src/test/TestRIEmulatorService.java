/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.net.URL;
import java.util.Arrays;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.centermodel.RIEmulationParameters;

/**
 *
 * @author TransCore ITS, LLC Created: Mar 1, 2016
 */
public class TestRIEmulatorService {

    public static void main(String[] args) throws Exception {

        
URL urlList[] = new URL[2];
urlList[0]=new URL("jar:file:/C:/C2CRI-Phase2/C2CRIBuildDir/projects/C2C-RI/src/TMDDv303/dist/TMDDv303.jar!/InfoLayer/EmulationData/CCTVINVENTORY.out");
urlList[1]=new URL("jar:file:/C:/C2CRI-Phase2/C2CRIBuildDir/projects/C2C-RI/src/TMDDv303/dist/TMDDv303.jar!/InfoLayer/EmulationData/CCTVSTATUS.out");
RIEmulationParameters temp = new RIEmulationParameters(urlList);
for (RIEmulationEntityValueSet thisOne : temp.getEntityDataMap()){
System.out.println(" Output = "+thisOne.getValueSetName() + "  bytelength = "+thisOne.getEntityDataSet().length + "\n\n"+Arrays.toString(thisOne.getEntityDataSet()));
    
}


    }
}
