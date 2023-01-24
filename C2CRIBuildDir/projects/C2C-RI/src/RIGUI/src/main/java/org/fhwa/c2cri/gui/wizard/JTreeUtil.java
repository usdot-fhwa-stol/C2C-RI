/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard;


import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.tree.TreeNode;

/**
 *
 */
public class JTreeUtil {
  public static void setTreeExpandedState(JTree tree, boolean expanded) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getModel().getRoot();
      setNodeExpandedState(tree, node, expanded);
  }

  public static void setNodeExpandedState(JTree tree, DefaultMutableTreeNode node, boolean expanded) {
      ArrayList<TreeNode> list = Collections.list(node.children());
      for (TreeNode treeNode : list) {
          setNodeExpandedState(tree, (DefaultMutableTreeNode)treeNode, expanded);
      }
      if (!expanded && node.isRoot()) {
          return;
      }
      TreePath path = new TreePath(node.getPath());
      if (expanded) {
          tree.expandPath(path);
      } else {
          tree.collapsePath(path);
      }
  }    
}
