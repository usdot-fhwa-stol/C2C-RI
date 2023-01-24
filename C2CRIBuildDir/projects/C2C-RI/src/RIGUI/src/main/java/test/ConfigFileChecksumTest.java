/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.fhwa.c2cri.utilities.Checksum;

/**
 * The Class ConfigFileOutput.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class ConfigFileChecksumTest {

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        try {
            Checksum cs1 = new Checksum();
            String cs1_string = cs1.getChecksum("C:\\C2CRI\\TMDDv303cEntityEmuOriginalOCS-C1.ricfg");
            String configFileName = "C:\\C2CRI\\TMDDv303cEntityEmuOriginalOCS-C2.ricfg";
//            ObjectInputStream input = new ObjectInputStream(new FileInputStream(new File(configFileName)));
//            TestConfiguration tc = (TestConfiguration)input.readObject();
            Checksum cs2 = new Checksum();
            String cs2_String = cs2.getChecksum(configFileName);

            System.out.println("Checksum 1 :" + cs1_string + (cs1_string.equals(cs2_String) ? " equals " : " does not equal") + " Checksum 2 : " + cs2_String);
            System.out.println("CheckByByteMatch = " + checkByteMatch("C:\\C2CRI\\TMDDv303cEntityEmuOriginalOCS-C1.ricfg",configFileName));

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean checkByteMatch(String file1Name, String file2Name) throws Exception {
        File file1 = new File(file1Name);
        File file2 = new File(file2Name);

        if (file1.length() != file2.length()) {
            System.out.println("File1 length = "+file1.length() + " and File2 length = "+file2.length());
            return false;
        }

        try (InputStream in1 = new BufferedInputStream(new FileInputStream(file1));
                InputStream in2 = new BufferedInputStream(new FileInputStream(file2));) {

            int retvalue1, retvalue2;
            byte[] value1 = new byte[1];
            byte[] value2 = new byte[1];
            boolean mismatch = false;
            long byteCount = 0;
            long totalMismatches = 0;
            long firstOffByte = 0;
            long lastOffByte = 0;
            long firstTotal = 0;
            long secondTotal = 0;
            long mismatchedSections = 0;
            boolean newMismatchedSection = false;

            do {
                //since we're buffered read() isn't expensive
                retvalue1 = in1.read(value1);
                retvalue2 = in2.read(value2);
                byteCount++;
                firstTotal = firstTotal+value1[0];
                secondTotal = secondTotal+value2[0];
                if (value1[0] != value2[0]) {
                    if (!mismatch){
                        firstOffByte = byteCount;
                    } else {
                        lastOffByte = byteCount;
                    }
                    if (!newMismatchedSection){
                        mismatchedSections++;
                        newMismatchedSection = true;
                    }
                    mismatch = true;
                    totalMismatches++;
//                    System.out.println("Mismatch at byte "+byteCount+ " File1 = "+value1[0] + " File2 = "+value2[0]);                    
                } else {
                    newMismatchedSection = false;
                }
            } while (retvalue1 >= 0);

            //since we already checked that the file sizes are equal 
            //if we're here we reached the end of both files without a mismatch
            if (mismatch) {
                System.out.println(totalMismatches +" bytes did not match each other.  The first was at offset "+(firstOffByte-1)+ "  The last was at offset "+(lastOffByte-1)+ "  Total mismatchSections = "+mismatchedSections);
                System.out.println("First File byte sum = "+firstTotal + "  Second File byte sume = "+secondTotal);
                return false;
            }
            System.out.println("Apparently all "+byteCount+" bytes matched each other.");
            return true;
        }
    }

}
