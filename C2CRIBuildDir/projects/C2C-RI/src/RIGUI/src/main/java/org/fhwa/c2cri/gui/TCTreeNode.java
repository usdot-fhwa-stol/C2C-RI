/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.gui;

/**
 *  This class acts as a TreeNode object for the Test Case Trees.
 *
 */
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The Class TCTreeNode.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class TCTreeNode extends DefaultMutableTreeNode{

/**
 * Constructor for the TCTreeNode Object.
 *
 * @param userObject - the object being represented on the tree
 * @param allowsChildren - whether this object is a leaf node or not
 */
    public TCTreeNode(Object userObject, boolean allowsChildren){
        super( userObject, allowsChildren );
    }

/**
 * All test cases aree currently leaf nodes.  Only the root is not.
 *
 * @return true, if is leaf
 */
    public boolean isLeaf() {
        return getParent() != null;
    }
}
