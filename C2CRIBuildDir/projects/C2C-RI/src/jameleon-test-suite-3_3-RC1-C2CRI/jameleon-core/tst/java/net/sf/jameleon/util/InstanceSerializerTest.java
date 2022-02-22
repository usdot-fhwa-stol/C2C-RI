/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2007 Christian W. Hargraves (engrean@hotmail.com)
    
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.bean.FunctionalPoint;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class InstanceSerializerTest extends TestCase {

    protected final static String SER_FILE = "foobarFile.txt";
    protected final static String PACKAGE_DIR = "tst/java/net/sf/jameleon/util/";

    protected FunctionalPoint fp;

    public InstanceSerializerTest( String name ) {
        super( name );
    }

    //JUnit Methods
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( InstanceSerializerTest.class );
    }

    public void setUp() {
        File dir = new File("tst/_tmp");
        dir.mkdir();
        fp = new FunctionalPoint();
    }

    public void tearDown() {
        File f = new File(SER_FILE);
        f.delete();
        f = new File(PACKAGE_DIR+SER_FILE);
        f.delete();
        fp = null;
    }

    public void testSerializeWithUncreatedDir() throws IOException{
        File f = new File(JameleonUtility.fixFileSeparators("tst/_tmp/serialize/uncreated/someFile.txt"));
        try{
        	InstanceSerializer.serialize(fp, f);
        }catch (FileNotFoundException fnfe){
            fail("The directory should be auto-created");
        }finally{
            f.delete();
        }
    }

    public void testSerializeWithNoParentDir() throws Exception{
        File f = new File("uncreatedFile.txt");
        try{
        	InstanceSerializer.serialize(fp, f);
        }finally{
            f.delete();
        }
    }

    public void testDeserializeFunctionalPoint() 
        throws IOException, ClassNotFoundException{
        fp.setAuthor("Kay");
        fp.addStep("Serialize Me");
        File f = new File(JameleonUtility.fixFileSeparators("tst/_tmp/"+SER_FILE));
        InstanceSerializer.serialize(fp, f);
        //Just to be sure.
        fp = null;
        fp = (FunctionalPoint)InstanceSerializer.deserialize(JameleonUtility.fixFileSeparators("tst/_tmp/"+SER_FILE));
        assertNotNull("Object should not be null", fp);
        assertEquals("Object author is not the same", "Kay", fp.getAuthor());
        assertEquals("Object steps", 1, fp.getSteps().size());
    }

    public void testDeserializeFunctionalPointFromInputStream() 
        throws IOException, ClassNotFoundException{
        fp.setAuthor("Kay");
        fp.addStep("Serialize Me");
        File f = new File(JameleonUtility.fixFileSeparators(PACKAGE_DIR+SER_FILE));
        InstanceSerializer.serialize(fp, f);
        assertTrue(f.getAbsolutePath() + " does not exist", f.exists());
        //Just to be sure.
        fp = null;
        FileInputStream fis = new FileInputStream(f);
        fp = (FunctionalPoint)InstanceSerializer.deserialize(fis);
        assertNotNull("Object should not be null", fp);
        assertEquals("Object author", "Kay", fp.getAuthor());
        assertEquals("Object steps", 1, fp.getSteps().size());
    }

    public void testDeserializeFunctionalPointWithUTF8Characters() 
        throws IOException, ClassNotFoundException{
        fp.setAuthor("Christian");
        fp.addStep("한굴 [Korean]");
        File f = new File(JameleonUtility.fixFileSeparators("tst/_tmp/"+SER_FILE));
        InstanceSerializer.serialize(fp, f);
        //Just to be sure.
        fp = null;
        fp = (FunctionalPoint)InstanceSerializer.deserialize(JameleonUtility.fixFileSeparators("tst/_tmp/"+SER_FILE));
        assertNotNull("Object should not be null", fp);
        assertEquals("Object author is not the same", "Christian", fp.getAuthor());
        assertEquals("Object steps", 1, fp.getSteps().size());
        assertEquals("Steps text", "한굴 [Korean]", fp.getSteps().get(0));
    }

}

