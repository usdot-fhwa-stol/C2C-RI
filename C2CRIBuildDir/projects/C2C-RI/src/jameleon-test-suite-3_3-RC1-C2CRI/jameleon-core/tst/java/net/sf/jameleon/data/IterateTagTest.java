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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.sf.jameleon.MockTestCaseTag;

import org.apache.commons.jelly.JellyContext;
import org.apache.log4j.Logger;

public class IterateTagTest extends TestCase {

    private IterateTag iterateTag;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(IterateTagTest.class);
    }

    public IterateTagTest(String name) {
        super(name);
    }

    public void setUp(){
        iterateTag = new IterateTag();
        iterateTag.setParent(new MockTestCaseTag());
    }

    public void testGetLogger(){
        Logger log = iterateTag.getLogger();
        assertEquals("Name of the logger", "net.sf.jameleon.data.IterateTag", log.getName());
    }

    public void testGetDataDriver(){
        DataDriver dd = iterateTag.getDataDriver();
        assertTrue("Should be of type CollectionDataDriver", dd instanceof CollectionDataDriver);
    }

    public void testGetDataExceptionMessage(){
        assertEquals("data exception message", "Trouble iterating over the collection.", iterateTag.getDataExceptionMessage());
    }

    public void testSetupDataDriver(){
        LinkedList list = new LinkedList();
        list.add("one");
        iterateTag.setItems(list);
        iterateTag.setVarName("key");
        iterateTag.setupDataDriver();
        CollectionDataDriver cdd = (CollectionDataDriver)iterateTag.dataDriver;
        assertEquals("# of items", 1, cdd.items.size());
        assertEquals("key", "key", cdd.key);
    }

    public void testGetTagTraceMsg(){
        assertEquals("data trace message", "Iterating over the collection of size 0", iterateTag.getTagTraceMsg());
    }

    public void testGetTagDescription(){
        assertEquals("data tag description", "iterate tag", iterateTag.getTagDescription());
    }

    public void testGetStateStoreLocation(){
        assertEquals("The state store location", "iterate---12", iterateTag.getNewStateStoreLocation(12));
        iterateTag.setId("foo");
        assertEquals("The state store location", "iterate-foo-12", iterateTag.getNewStateStoreLocation(12));
    }

    public void testExecuteDrivableRow() throws Exception{
        List keys = new LinkedList();
        keys.add("one");
        keys.add("two");
        MockIterateTag mIterateTag = new MockIterateTag();
        mIterateTag.setContext(new JellyContext());
        MockTestCaseTag mtct = new MockTestCaseTag();
        mtct.setContext(new JellyContext());
        mIterateTag.setParent(mtct);
        mIterateTag.tct = mtct;
        mIterateTag.setItems(keys);
        mIterateTag.setVarName("key");
        mIterateTag.setupDataDriver();
        mIterateTag.executer.executeData(mIterateTag, false);

        assertEquals("# of rows executed", 2, mIterateTag.totalNumOfRows);
        Map row1 = (Map)mIterateTag.rows.get(0);
        Map row2 = (Map)mIterateTag.rows.get(1);
        assertEquals("# of columns returned in first row", 1, row1.size());
        assertEquals("# of columns returned in second row", 1, row2.size());
        assertEquals("value 1", "one", (String)row1.get("key"));
        assertEquals("value 2", "two", (String)row2.get("key"));
    }

}

