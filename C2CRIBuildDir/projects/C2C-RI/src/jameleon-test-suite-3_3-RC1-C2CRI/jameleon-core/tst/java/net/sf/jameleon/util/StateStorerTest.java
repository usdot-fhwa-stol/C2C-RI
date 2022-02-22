/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003 Christian W. Hargraves (engrean@hotmail.com)
    
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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111AssertLevel.NO_FUNCTION07 USA
*/
package net.sf.jameleon.util;

import net.sf.jameleon.TempText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import java.util.Iterator;
import java.util.Set;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StateStorerTest extends TestCase {

    private StateStorer ss;
    private TempText storable;
    
    public static void main(String args[]) {
        junit.textui.TestRunner.run( suite() );
    }

    public static Test suite() {
        return new TestSuite( StateStorerTest.class );
    }

    public StateStorerTest( String name ) {
        super( name );
    }

    public void setUp() {
        ss = StateStorer.getInstance();
        File storeDir = new File("tst" + File.separator + "_tmp" + File.separator + "StateStorerTest");
        ss.setStoreDir(storeDir);
        storable = new TempText();
        ss.addStorable(storable);
    }

    public void tearDown(){
        ss.reset();
    }

    public void testIsStateStorable(){
        ss.setStorableEvent(StateStorer.ON_NO_EVENT);
        assertFalse("Change event", ss.isStateStorable(StateStorer.ON_STATE_CHANGE_EVENT));
        assertFalse("error event", ss.isStateStorable(StateStorer.ON_ERROR_EVENT));
        assertTrue("command event", ss.isStateStorable(StateStorer.ON_COMMAND_EVENT));

        ss.setStorableEvent(StateStorer.ON_ERROR_EVENT);
        assertFalse("Change event", ss.isStateStorable(StateStorer.ON_STATE_CHANGE_EVENT));
        assertTrue("error event", ss.isStateStorable(StateStorer.ON_ERROR_EVENT));
        assertTrue("command event", ss.isStateStorable(StateStorer.ON_COMMAND_EVENT));

        ss.setStorableEvent(StateStorer.ON_STATE_CHANGE_EVENT);
        assertTrue("Change event", ss.isStateStorable(StateStorer.ON_STATE_CHANGE_EVENT));
        assertTrue("error event", ss.isStateStorable(StateStorer.ON_ERROR_EVENT));
        assertTrue("command event", ss.isStateStorable(StateStorer.ON_COMMAND_EVENT));
    }

    public void testGetInstance(){
        StateStorer ss2 = StateStorer.getInstance();
        assertTrue("two state storers should be the same", ss2 == ss);
    }

    public void testDefaultSettings(){
        ss.reset();
        assertEquals("Default event should have been set",StateStorer.DEFAULT_EVENT, ss.getStorableEvent());
        assertTrue("Default event should be ON_ERROR_EVENT!",ss.isStateStorable(StateStorer.ON_ERROR_EVENT));
        assertFalse("State should not be storable on any state change!",ss.isStateStorable(StateStorer.ON_STATE_CHANGE_EVENT));
        assertEquals("Default store directory: ", "jameleon_test_results", ss.getStoreDir().getPath());
    }

    public void testSetStorableEventWithValidEvent(){
        ss.setStorableEvent(StateStorer.ON_NO_EVENT);
        assertEquals(StateStorer.ON_NO_EVENT, ss.getStorableEvent());
        ss.setStorableEvent(StateStorer.ON_STATE_CHANGE_EVENT);
        assertEquals(StateStorer.ON_STATE_CHANGE_EVENT, ss.getStorableEvent());
    }

    public void testSetStorableEventWithInValidEvent(){
        ss.setStorableEvent(25);
        assertEquals(StateStorer.DEFAULT_EVENT, ss.getStorableEvent());
        ss.setStorableEvent(-55);
        assertEquals(StateStorer.DEFAULT_EVENT, ss.getStorableEvent());
    }

    public void testAddStorable(){
        Set storables = ss.getStorables();
        assertEquals("There should only be one Storable.",1,storables.size());
        Iterator it = storables.iterator();
        Object obj = it.next();
        assertTrue("The only Storable should be of type TempText",obj instanceof TempText);
    }
    
    public void testRemoveStorable(){
        testAddStorable();
        ss.removeStorable(storable);
        assertEquals("No elements should be left in the set after removing the only one!",0,ss.getStorables().size());
    }

    public void testEventOccuredOnErrorEvent(){
        ss.eventOccured(StateStorer.ON_ERROR_EVENT);
        File f = new File(ss.getStoreDir(),TempText.TEMP_FILE_DEFAULT);
        BufferedReader in = null;
        FileReader fr = null;
        try{
            assertTrue("File " + f + " should have been created!", f.exists());
            fr = new FileReader(f);
            in = new BufferedReader(fr);
            String line = null;
            String newContent = new String();
            while ((line = in.readLine()) != null) {
                newContent += line;
            }
            assertEquals(TempText.TEMP_TEXT, newContent);
        }catch(FileNotFoundException fnfe){
            fail(f+" NOT created!");
        }catch(IOException ioe){
            fail(f+ " could not be read!");
        }finally{
            try{
                if (fr != null) {
                    fr.close();
                }
                if (in != null) {
                    in.close();
                }
            }catch(IOException ioe){ 
                System.err.println("Couldn't close file handles");// Nothing to do.
            }
        }
    }

    public void testEventOccuredOnErrorEventWithNonDefaultFileWritten(){
        String f2 = "Some_File_Which_Should_Be_Written_To.jml";
        Set storables = ss.getStorables();
        Iterator it = storables.iterator();
        Object obj = it.next();
        assertTrue("The only Storable should be of type TempText",obj instanceof TempText);
        TempText tt = (TempText)obj;
        tt.setTempFile(f2);
        ss.eventOccured(StateStorer.ON_ERROR_EVENT);
        File f = new File(ss.getStoreDir(),f2);
        try{
            assertTrue("File " + f + " should have been created!", f.exists());
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line = null;
            String newContent = new String();
            while ((line = in.readLine()) != null) {
                newContent += line;
            }
            assertEquals(TempText.TEMP_TEXT, newContent);
        }catch(FileNotFoundException fnfe){
            fail(f+" NOT created!");
        }catch(IOException ioe){
            fail(f+ " could not be read!");
        }
    }

    public void testEventOccuredOnChangeEventWithNonDefaultFileNotWritten(){
        String f2 = "Some_Other_File_Which_Should_Never_Be_Written_To.jml";
        Set storables = ss.getStorables();
        Iterator it = storables.iterator();
        Object obj = it.next();
        assertTrue("The only Storable should be of type TempText",obj instanceof TempText);
        TempText tt = (TempText)obj;
        tt.setTempFile(f2);
        ss.eventOccured(StateStorer.ON_STATE_CHANGE_EVENT);
        File f = new File(ss.getStoreDir(),f2);
        assertFalse("File " + f + " should not have been created!", f.exists());
    }

    public void testEventOccuredOnChangeEventWithNonDefaultFileWritten(){
        String f2 = "Some_Other_File_Which_Should_Also_Be_Written_To.jml";
        ss.setStorableEvent(StateStorer.ON_STATE_CHANGE_EVENT);
        Set storables = ss.getStorables();
        Iterator it = storables.iterator();
        Object obj = it.next();
        assertTrue("The only Storable should be of type TempText",obj instanceof TempText);
        TempText tt = (TempText)obj;
        tt.setTempFile(f2);
        ss.eventOccured(StateStorer.ON_STATE_CHANGE_EVENT);
        File f = new File(ss.getStoreDir(),f2);
        try{
            assertTrue("File " + f + " should have been created!", f.exists());
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line = null;
            String newContent = new String();
            while ((line = in.readLine()) != null) {
                newContent += line;
            }
            assertEquals(TempText.TEMP_TEXT, newContent);
        }catch(FileNotFoundException fnfe){
            fail(f+" NOT created!");
        }catch(IOException ioe){
            fail(f+ " could not be read!");
        }
    }

    public void testEventOccuredOnChangeEventWithNonDefaultFileWrittenOnErrorEvent(){
        String f2 = "Some_Other_File_Which_Should_Also_Be_Written_To2.jml";
        ss.setStorableEvent(StateStorer.ON_STATE_CHANGE_EVENT);
        Set storables = ss.getStorables();
        Iterator it = storables.iterator();
        Object obj = it.next();
        assertTrue("The only Storable should be of type TempText",obj instanceof TempText);
        TempText tt = (TempText)obj;
        tt.setTempFile(f2);
        ss.eventOccured(StateStorer.ON_ERROR_EVENT);
        File f = new File(ss.getStoreDir(),f2);
        try{
            assertTrue("File " + f + " should have been created!", f.exists());
            BufferedReader in = new BufferedReader(new FileReader(f));
            String line = null;
            String newContent = new String();
            while ((line = in.readLine()) != null) {
                newContent += line;
            }
            assertEquals(TempText.TEMP_TEXT, newContent);
        }catch(FileNotFoundException fnfe){
            fail(f+" NOT created!");
        }catch(IOException ioe){
            fail(f+ " could not be read!");
        }
    }

}



