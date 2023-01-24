/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fhwa.c2cri.gui;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The Class ConfigurationTreeNode acts as a TreeNode object for the Test Configuration Tree.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class ConfigurationTreeNode extends DefaultMutableTreeNode{

/**
 * Constructor for the TCTreeNode Object.
 *
 * @param userObject - the object being represented on the tree
 * @param allowsChildren - whether this object is a leaf node or not
 */
    public ConfigurationTreeNode(Object userObject, boolean allowsChildren){
        super( userObject, allowsChildren );
    }

/**
 * Provides an indication of whether the node can contain other leaf nodes.
 *
 * @return true, if is leaf
 */
    public boolean isLeaf() {
        return getParent() != null;
    }
}
