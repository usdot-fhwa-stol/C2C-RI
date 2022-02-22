/*
    Jameleon - An automation testing tool..
    Copyright (C) 2007 Christian W. Hargraves (engrean@hotmail.com)

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

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;


public class JarResourceExtractorTest extends TestCase {

    private JarResourceExtractor extractor;
    private static final File EXTRACT_DIR = new File("tst/_tmp/extractedFiles");

    public JarResourceExtractorTest( String name ) {
        super( name );
    }

    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( JarResourceExtractorTest.class );
    }

    public void setUp() throws Exception{
        extractor = new JarResourceExtractor();
    }

    public void tearDown() throws IOException {
        extractor.getJarFile().close();
        deleteFile(EXTRACT_DIR);
    }

    public void testConstructor() {
        JarFile jFile = extractor.getJarFile();
        assertNotNull("jar file not found!", jFile);
    }

    public void testExtractFile() throws Exception {
        File f = new File(EXTRACT_DIR, JameleonDefaultValues.JAMELEON_VERSION_FILE);
        JameleonUtility.createDirStructure(EXTRACT_DIR);
        assertFalse(f.getPath() + " should not exist yet", f.exists());
        ZipEntry entry = extractor.getJarFile().getEntry(JameleonDefaultValues.JAMELEON_VERSION_FILE);
        extractor.extractFile(entry, EXTRACT_DIR);
        assertTrue(f.getPath() + " does not exist", f.exists());
    }

    public void testExtractFilesInDirectory() throws Exception{
        assertFalse(EXTRACT_DIR.getPath() + " existed", EXTRACT_DIR.exists());
        extractor.extractFilesInDirectory("icons", EXTRACT_DIR);
        assertTrue(EXTRACT_DIR.getPath() +" did not exist", EXTRACT_DIR.exists());
        assertEquals("# of files copied", 11, new File(EXTRACT_DIR, "icons").listFiles().length);
        //do it again on a directory with only one file in it
        extractor.extractFilesInDirectory("ant", EXTRACT_DIR);
        assertEquals("# of files copied", 1, new File(EXTRACT_DIR, "ant").listFiles().length);
    }

    private void deleteFile(File f){
        File[] files = f.listFiles();
        if (files != null){
            for(int i = 0; i <files.length; i++){
                if (files[i].isDirectory()){
                    deleteFile(files[i]);
                }
                files[i].delete();
            }
        }
        f.delete();
    }
}
