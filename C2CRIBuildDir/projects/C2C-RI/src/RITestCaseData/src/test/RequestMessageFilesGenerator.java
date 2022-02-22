/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseDataSource;

/**
 * Creates test files for entity testing
 * 
 * @author TransCore ITS, LLC Created: Mar 24, 2016
 */
public class RequestMessageFilesGenerator {

    public static void main(String[] args) throws Exception {
        File folder = new File("..\\TMDDv303\\src\\InfoLayer\\Data");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains("-OC-")) {
                TestCaseDataSource thisSource = new TestCaseDataSource(listOfFiles[i].toURI().toURL());
                String testFileName = listOfFiles[i].getName().replace("-OC-", "").replace(".data", "").replace("TCS-", "Need-");

                File output = new File("..\\TMDDv303\\test\\" + testFileName + ".in");
//                System.out.println("File " + listOfFiles[i].getName() + "\n" + thisSource.getMessageSpec(thisSource.getIterationCount() - 1, "Message").replace("#RIMessageSpec#", "").replace("; ", "\n"));

                PrintWriter writer = new PrintWriter("..\\TMDDv303\\test\\" + testFileName + ".in", "UTF-8");
                writer.println(thisSource.getMessageSpec(thisSource.getIterationCount() - 1, "Message").replace("#RIMessageSpec#", "").replace("; ", "\n"));
                writer.close();
                System.out.println("Wrote "+output.getPath());

            } else if (listOfFiles[i].isDirectory()) {
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }

    }
}
