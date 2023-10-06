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

import java.io.IOException;
import java.io.InvalidClassException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sf.jameleon.TestCaseTagLibrary;
import net.sf.jameleon.bean.FunctionalPoint;
import net.sf.jameleon.util.InstanceSerializer;
import net.sf.jameleon.util.SupportedTags;

public class FunctionalPointTree extends JTree {

    private Set usedClasses;

    public FunctionalPointTree(FPDisplayer fpDisplayer) {
        super();
        final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Functional Points");
        buildTree(rootNode);

        DefaultTreeModel tm = new DefaultTreeModel(rootNode);
        setModel(tm);
        FPTreeListener listener = new FPTreeListener(fpDisplayer);
        addTreeWillExpandListener(listener);
        addTreeSelectionListener(listener);
    }

    private void buildTree(final DefaultMutableTreeNode rootNode) {
        Thread t = new Thread() {
            public void run() {
                rootNode.removeAllChildren();
                TestCaseTagLibrary.resetTags();
                SupportedTags st = new SupportedTags();
                st.setWarnOnNoPluginsFile(true);
                //Get all supported tags
                Map tags = st.getSupportedTags();
                Set orderedClasses = Collections.synchronizedSet(new TreeSet(tags.values()));
                createSkeleton(orderedClasses, rootNode);
                populateTree(tags, rootNode, Thread.currentThread().getContextClassLoader());
                ((DefaultTreeModel)getModel()).reload(rootNode);
            }
        };
        t.setContextClassLoader(Utils.createClassLoader());
        t.start();
    }

    protected void createSkeleton(Set classes, DefaultMutableTreeNode root) {
        Iterator it = classes.iterator();
        String packageName = null;

        String dirNode = null;
        DefaultMutableTreeNode node, pNode, childNode = null;

        while ( it.hasNext() ) {
            pNode = root;
            packageName = (String)it.next();
            for ( int index = packageName.indexOf('.'); index > -1; index = packageName.indexOf('.') ) {
                dirNode = packageName.substring(0,index);
                node = new DefaultMutableTreeNode(dirNode, true);
                boolean nodeNotFound = true;
                Enumeration e = pNode.children();
                String childString;
                while ( e.hasMoreElements() && nodeNotFound ) {
                    childNode = (DefaultMutableTreeNode)e.nextElement();
                    childString = (String)childNode.getUserObject();
                    if ( childString.equals(dirNode) ) {
                        nodeNotFound = false;
                    }
                }
                if ( nodeNotFound ) {
                    pNode.add(node);
                    pNode = node;
                } else {
                    pNode = childNode;
                }
                packageName = packageName.substring(index+1);
            }
        }

    }

    protected void populateTree(Map tags, DefaultMutableTreeNode root, ClassLoader cl) {
        usedClasses = new HashSet();
        Iterator it = tags.keySet().iterator();
        String className = null;
        while ( it.hasNext() ) {
            className = (String)tags.get((String)it.next());
            if ( usedClasses.add(className) ) {
                addFunctionalPointToTree(className,root, cl);
            }
        }
    }

    protected void addFunctionalPointToTree(String qName, DefaultMutableTreeNode parent, ClassLoader cl) {
        String dirNode = null;
        String className = qName;
        DefaultMutableTreeNode node = parent;
        DefaultMutableTreeNode pNode = parent;
        for ( int index = className.indexOf('.'); index > -1; index = className.indexOf('.') ) {
            dirNode = className.substring(0,index);
            node = new DefaultMutableTreeNode(dirNode, true);
            DefaultMutableTreeNode nodeFound = null;
            Enumeration e = pNode.children();
            while ( e.hasMoreElements() ) {
                nodeFound = (DefaultMutableTreeNode)e.nextElement();
                if ( !nodeFound.isLeaf() || nodeFound.getUserObject() instanceof String ) {
                    if ( ((String)nodeFound.getUserObject()).equals(dirNode) ) {
                        node = nodeFound;
                    }
                }
            }
            if ( pNode.getIndex(node) == -1 ) {
                pNode.add(node);
            }
            pNode = node;
            className = className.substring(index+1);
        }
        String fileName = qName.replace('.', '/')+InstanceSerializer.SERIALIZED_EXT;
        try {
            FunctionalPoint fp = (FunctionalPoint)InstanceSerializer.deserialize(cl.getResourceAsStream(fileName));
            node = new DefaultMutableTreeNode(fp, false);
            if ( pNode.getIndex(node) == -1 ) {
                pNode.add(node);
            }
        } catch (InvalidClassException ice){
            System.err.println("Could not load tag for "+qName+"!");
            ice.printStackTrace();
        } catch ( IOException ioe ) {
            ioe.printStackTrace();//Simply can't read in the source dat file
        } catch ( ClassNotFoundException cnfe ) {
            cnfe.printStackTrace();
            System.err.print(cnfe.getMessage());
        } 
    }

    public class FPTreeListener implements TreeWillExpandListener, TreeSelectionListener {

        protected FPDisplayer fpDisplayer;

        public FPTreeListener (FPDisplayer fpDisplayer) {
            this.fpDisplayer = fpDisplayer;
        }

        public void treeWillCollapse(TreeExpansionEvent event) {
			// original implementation was empty
        }

        public void treeWillExpand(TreeExpansionEvent event) {
            TreePath path = event.getPath();
            if ( path.getParentPath() == null ) {
                DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)path.getLastPathComponent();
                buildTree(rootNode);
            }
        }

        public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            DefaultMutableTreeNode tn = (DefaultMutableTreeNode)path.getLastPathComponent();
            Object obj = tn.getUserObject();
            if ( obj instanceof FunctionalPoint ) {
                FunctionalPoint fp = (FunctionalPoint)obj;
                fpDisplayer.sendFunctionalPointInfoToUI(fp);
            }
        }
    }

}
