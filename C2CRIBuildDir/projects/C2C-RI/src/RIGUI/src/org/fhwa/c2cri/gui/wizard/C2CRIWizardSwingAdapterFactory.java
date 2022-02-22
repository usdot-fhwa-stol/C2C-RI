/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 */
public class C2CRIWizardSwingAdapterFactory {

    public static DefaultTreeModel toTreeModel(List<C2CRIWizardPage> wizardPages) {
        List<DefaultMutableTreeNode> nodes = new ArrayList<>();
        for (C2CRIWizardPage parentWizardPage : wizardPages) {
            DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(parentWizardPage);
            nodes.add(parentNode);
            addChildWizardPageNodes(parentNode, parentWizardPage);
//            for (C2CRIWizardPage childWizardPage : parentWizardPage.getSubPages()) {
//                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childWizardPage);
////                nodes.add(childNode);
//                parentNode.add(childNode);
//                for (C2CRIWizardPage subchildWizardPage : childWizardPage.getSubPages()) {
//                    DefaultMutableTreeNode subchildNode = new DefaultMutableTreeNode(subchildWizardPage);
////                    nodes.add(subchildNode);
//                    childNode.add(subchildNode);
//                }
//            }
        }

        DefaultMutableTreeNode[] results = nodes.toArray(new DefaultMutableTreeNode[0]);

        return new DefaultTreeModel(results[0]);
    }

    public static void updateTreeModel(DefaultTreeModel treeModel, List<C2CRIWizardPage> wizardPages) {
        List<DefaultMutableTreeNode> nodes = new ArrayList<>();
        for (C2CRIWizardPage parentWizardPage : wizardPages) {
            DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(parentWizardPage);
            nodes.add(parentNode);
            addChildWizardPageNodes(parentNode, parentWizardPage);
//            for (C2CRIWizardPage childWizardPage : parentWizardPage.getSubPages()) {
//                DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(childWizardPage);
////                nodes.add(childNode);
//                parentNode.add(childNode);
//                for (C2CRIWizardPage subchildWizardPage : childWizardPage.getSubPages()) {
//                    DefaultMutableTreeNode subchildNode = new DefaultMutableTreeNode(subchildWizardPage);
////                    nodes.add(subchildNode);
//                    childNode.add(subchildNode);
//                }
//            }
        }

        DefaultMutableTreeNode[] results = nodes.toArray(new DefaultMutableTreeNode[0]);
//        treeModel = new C2CRIWizardTreeModel(results[0],results);
        treeModel.setRoot(results[0]);
        treeModel.reload(results[0]);
        treeModel.nodeChanged(results[0]);
//        System.out.println("updateTreeModel has " + results.length + " entries ");
    }

    private static void addChildWizardPageNodes(DefaultMutableTreeNode node, C2CRIWizardPage wizardPage) {
        for (C2CRIWizardPage thisPage : wizardPage.getSubPages()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(thisPage);
            node.add(childNode);
//            System.out.println("addChildWizardPageNodes has added " + thisPage.toString() + " to node  " + node.toString());
            addChildWizardPageNodes(childNode, thisPage);
        }
    }

    /**
     * Iterate through the nodes of the treemodel searching for a treenode that
     * has a userobject that matches the wizard page provided.
     *
     * @param treeModel - The C2CRIWizardTreeModel which represents the wizard
     * pages
     * @param desiredPage - The wizard page for which we want to find the
     * treenode.
     * @return - the tree node as a DefaultMutableTreeNode if found, otherwise
     * null is returned.
     */
    public static DefaultMutableTreeNode findRelatedNode(DefaultTreeModel treeModel, C2CRIWizardPage desiredPage) {

//        for (TreeNode node : treeModel.getNodeList()) {
            DefaultMutableTreeNode results = (findRelatedSubNode((DefaultMutableTreeNode) treeModel.getRoot(), desiredPage));
            if (results != null) {
                return results;
            }
//        }
        return null;
    }

    private static DefaultMutableTreeNode findRelatedSubNode(DefaultMutableTreeNode startNode, C2CRIWizardPage desiredPage) {
        C2CRIWizardPage currentPage = (C2CRIWizardPage) ((DefaultMutableTreeNode) startNode).getUserObject();
//        System.out.println("Checking Node for page " + currentPage.toString());
        if (currentPage.equals(desiredPage)) {
            return (DefaultMutableTreeNode) startNode;
        }

        Iterator childIterator = startNode.children().asIterator();
        while (childIterator.hasNext()) {
            DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) childIterator.next();
            C2CRIWizardPage childPage = (C2CRIWizardPage) ((DefaultMutableTreeNode) startNode).getUserObject();
//            System.out.println("Checking childNode page " + childPage.toString() + " for page " + currentPage.toString());
            if (childPage.equals(desiredPage)) {
                return childNode;
            }
            DefaultMutableTreeNode result = findRelatedSubNode(childNode, desiredPage);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static void addNode(DefaultTreeModel treeModel, C2CRIWizardPage parentPage, C2CRIWizardPage desiredPage, int index) {
        DefaultMutableTreeNode parentNode = findRelatedNode(treeModel, parentPage);
        DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(desiredPage);
        treeModel.insertNodeInto(childNode, parentNode, index);
    }

    public static void removeNode(DefaultTreeModel treeModel, C2CRIWizardPage desiredPage) {
        DefaultMutableTreeNode desiredNode = findRelatedNode(treeModel, desiredPage);
        treeModel.removeNodeFromParent(desiredNode);
    }

}
