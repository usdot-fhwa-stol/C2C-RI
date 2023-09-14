/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.utilities;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;


/**
 * The Class Checksum computes a checksum of the given object.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class Checksum {
	
	/** The md5. */
	private MessageDigest md5;
	
	/** The buffer. */
	private byte[] buffer = new byte[1024];
	
	/** The num read. */
	private int numRead;
	
	/** The fis. */
	private FileInputStream fis;
	
	/** The ois. */
	private ObjectInputStream ois;
	
	/**
	 * Instantiates a new checksum.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 */
	public Checksum(){
            try{
               md5 = MessageDigest.getInstance("MD5");
            } catch (Exception ex){

            }
        }

	/**
	 * get checksum from a FILE on DISK.
	 *
	 * @param filename the filename
	 * @return the checksum
	 * @throws Exception the exception
	 */
	public String getChecksum(String filename) throws Exception{
            System.out.println("Checksum::getChecksum filename = "+filename);
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(filename)))
			{
				Object obj = (Object) input.readObject();
				return getChecksum(obj);
			}
//                                input.close();
//                                input = null;
//            
//	    ByteArrayInputStream bais = new ByteArrayInputStream(Files.readAllBytes(Paths.get(filename)));
////		fis =  new FileInputStream(filename);
//		updateMD5WithStream(bais);
//                byte[] digest = md5.digest();
//                BigInteger bigInt = new BigInteger(1,digest);
//                String hashtext = bigInt.toString(16);
//                while (hashtext.length()<32){
//                    hashtext = "0"+hashtext;
//                }
//		return hashtext.toUpperCase();
	}

	/**
	 * Gets the checksum.
	 *
	 * @param o the o
	 * @return the checksum
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private String getChecksum(Object o) throws IOException {
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    ObjectOutputStream oos = new ObjectOutputStream(baos);
	    oos.writeObject(o);
	    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
	    updateMD5WithStream(bais);
                byte[] digest = md5.digest();
                BigInteger bigInt = new BigInteger(1,digest);
                String hashtext = bigInt.toString(16);
                while (hashtext.length()<32){
                    hashtext = "0"+hashtext;
                }
		return hashtext.toUpperCase();
	}

	/**
	 * Update m d5 with stream.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param st the st
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void updateMD5WithStream(InputStream st) throws IOException{
		md5.reset();
                long totalBytesRead = 0;
	    do {
			numRead = st.read(buffer);
			if (numRead > 0) {
                                totalBytesRead = totalBytesRead + numRead;
				md5.update(buffer, 0, numRead);
			}
	    } while (numRead != -1);
            System.out.println("CheckSum::updateMD5WithStream totalBytesRead = "+totalBytesRead + " by provider "+md5.getProvider().toString() + " and reported digest length = "+md5.getDigestLength());
	    st.close();
	}

    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     */
    public static void main(String args[]) {
        Checksum thisCheckSum = new Checksum();
        try{
        System.out.println("The CheckSum is : "+ thisCheckSum.getChecksum("c:\\c2cri\\ExtendedBlankRIAsEC.ricfg"));
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
