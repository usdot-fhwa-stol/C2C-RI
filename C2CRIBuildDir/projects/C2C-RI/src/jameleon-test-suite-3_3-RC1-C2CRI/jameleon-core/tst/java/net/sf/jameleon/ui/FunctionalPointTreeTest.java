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
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.ui;

import net.sf.jameleon.bean.FunctionalPoint;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.tree.DefaultMutableTreeNode;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FunctionalPointTreeTest extends TestCase {

    protected FunctionalPointTree ui;
    protected File srcDir;
    protected DefaultMutableTreeNode root;

    public static void main(String args[]) {
        junit.textui.TestRunner.run(suite());
    }

    public static Test suite() {
        return new TestSuite(FunctionalPointTreeTest.class);
    }

    public FunctionalPointTreeTest(String name) {
        super(name);
    }

    public void setUp(){
        ui = new FunctionalPointTree(new JameleonTagsPanel());
        root = new DefaultMutableTreeNode("root");
    }

    public void tearDown(){
        ui = null;
        root = null;
    }

    public void testGetTreeNodeForFunctionalPoint(){
        String className = "net.sf.jameleon.ui.Dummy1";
        ui.addFunctionalPointToTree(className, root, ClassLoader.getSystemClassLoader());
        assertEquals("Total directories in package", 5, root.getDepth());
        assertEquals("Tag name", "ui-dummy1",((FunctionalPoint)root.getLastLeaf().getUserObject()).getDefaultTagName());
    }

    public void testCreateTree(){
        Properties props = new Properties();
        props.setProperty("ui-dummy1","net.sf.jameleon.ui.Dummy1");
        props.setProperty("ui-dummy1-sub","net.sf.jameleon.ui.Dummy1Sub");
        ui.populateTree(props, root, ClassLoader.getSystemClassLoader());
        assertEquals("Total directories in package", 5, root.getDepth());
        assertEquals("Total classes",2,root.getLeafCount());
        assertEquals("Total children nodes",1,root.getChildCount());
        Enumeration e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
            if (node.isLeaf()) {
                FunctionalPoint fp = (FunctionalPoint)node.getUserObject();
                if (fp.getDefaultTagName().equals("ui-dummy1")) {
                    assertEquals("validation",fp.getType());
                }else if ( fp.getDefaultTagName().equals("ui-dummy1-sub") ) {
                    assertEquals("action", fp.getType());
                }else{
                    fail("unexpected functional point");
                }
            }
        }
    }
}
