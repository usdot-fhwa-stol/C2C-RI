/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)
    
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
package net.sf.jameleon.data;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CollectionDataDriverTest extends TestCase {

    protected DataExecuter dd;
    protected CollectionDataDriver cdd;
    public static final String KEY = "key";

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(CollectionDataDriverTest.class);
    }

    public CollectionDataDriverTest(String name) {
        super(name);
    }

    public void setUp() {
        cdd = new CollectionDataDriver();
        cdd.setKey(KEY);
    }

    public void tearDown(){
        cdd.close();
        cdd = null;
    }

    public void testConstructor(){
        Collection items = createCollection();
        cdd = new CollectionDataDriver(KEY, items);
        assertEquals("key field", KEY, cdd.key);
        assertEquals("# of items", 3, cdd.items.size());
    }

    public void testOpenValid () throws Exception{
        cdd.setItems(createCollection());
        try {
            cdd.open();
            assertNotNull("Connection should not be null", cdd.iterator);
            assertTrue("Connection should be open", cdd.iterator.hasNext());
        } catch (IOException ioe ) {
            fail("No exception should have been thrown");
        }
    }

    public void testClose() throws IOException{
        cdd.setItems(createCollection());
        cdd.open();
        cdd.close();
        assertNull("the Collection's iterator should now be null", cdd.iterator);
    }

    public void testSetKey () throws Exception {
        cdd.setKey("key2");
        assertEquals("key", "key2", cdd.key);
    }

    public void testGetNextRowDefaultConstructor() throws IOException{
        cdd.setItems(createCollection());
        cdd.open();

        Map m = cdd.getNextRow();
        assertNotNull ( "A row should have been returned", m );
        assertEquals ("one", m.get(KEY).toString());

        m = cdd.getNextRow();
        assertNotNull ( "A row should have been returned", m );
        assertEquals ("two", m.get(KEY).toString());

        m = cdd.getNextRow();
        assertNotNull ( "A row should have been returned", m );
        assertEquals ("three", m.get(KEY).toString());

        m = cdd.getNextRow();
        assertNull ( "A row should not have been returned", m );

        cdd.close();
    }

    public void testGetNextRowEmptyResultSet() throws IOException{
        cdd.setItems(new LinkedList());
        cdd.open();
        assertNull ( "There should be ZERO row found", cdd.getNextRow() );
        cdd.close();
    }

    public void testOpenNullData() throws IOException{
        boolean exceptionThrown = false;
        try{
            cdd.open();
        }catch (IOException ioe){
            exceptionThrown = true;
        }
        assertTrue ( "IOException should have been thrown", exceptionThrown );
    }

    public void testGetNextRowNullKey() throws IOException{
        boolean exceptionThrown = false;
        cdd.setItems(new LinkedList());
        cdd.setKey(null);
        try{
            cdd.open();
        }catch (IOException ioe){
            exceptionThrown = true;
        }
        assertTrue ( "IOException should have been thrown", exceptionThrown );
    }

    public void testHasMoreRowsDefaultConstructor () throws IOException {
        cdd.setItems(createCollection());
        cdd.setKey("key");
        cdd.open();
        assertTrue("Should have rows. ", cdd.hasMoreRows());
    }

    public void testHasMoreRowsNo () throws IOException {
        cdd.setItems(new LinkedList());
        cdd.setKey("key");
        cdd.open();
        assertFalse("Should have rows. ", cdd.hasMoreRows());
    }

    private Collection createCollection(){
        List strings = new LinkedList();
        strings.add("one");
        strings.add("two");
        strings.add("three");
        return strings;
    }

}
