/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fhwa.c2cri.gui.wizard;

import com.github.cjwizard.C2CRIWizardContainer;
import com.github.cjwizard.StackWizardSettings;
import com.github.cjwizard.WizardListener;
import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import com.github.cjwizard.pagetemplates.TitledPageTemplate;
import java.awt.Component;
import java.awt.Graphics;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.ToolTipManager;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/**
 * Shows a super simple linear wizard with a navigation view (ListView)
 *
 * @since 1.0.9
 * @author Alex O'Ree
 */
public class C2CRIWizardDialog extends javax.swing.JDialog {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        //NetBeans generates stuff
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
        * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(C2CRIWizardDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(C2CRIWizardDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(C2CRIWizardDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(C2CRIWizardDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                C2CRIWizardDialog dialog = new C2CRIWizardDialog(new javax.swing.JFrame(), true, new C2CRWizardFactory(C2CRWizardFactory.FactoryType.TEST_CONFIGURATION));
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    /**
     * Commons logging log instance
     */
    private final transient Logger log = Logger.getLogger(C2CRIWizardDialog.class.getSimpleName());
    final private transient C2CRWizardFactory c2criWizardFactory;

    private javax.swing.JTree jTreeNavigation;
//    private javax.swing.JList<String> jListNavigation;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JFrame parentFrame;
    /**
     * Creates new form WizardNavBar
     */
    public C2CRIWizardDialog(javax.swing.JFrame parent, boolean modal, C2CRWizardFactory factory) {
        super(parent, modal);
        this.c2criWizardFactory = factory;
        this.parentFrame = parent;
        initComponents();
    }

    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
//        jListNavigation = new javax.swing.JList<>();
        jTreeNavigation = new javax.swing.JTree();

//        DefaultMutableTreeNode[] nodes = new DefaultMutableTreeNode[c2criWizardFactory.getPages().size()];
//        for (int i = 0; i < c2criWizardFactory.getPages().size(); i++) {
//            nodes[i] = new DefaultMutableTreeNode(c2criWizardFactory.getPages().get(i));
//        }
//        nodes[0].add(nodes[1]);
//        nodes[1].add(nodes[2]);
//        nodes[1].add(nodes[3]);
//        nodes[0].add(nodes[4]);
//        nodes[0].add(nodes[5]);
//        nodes[0].add(nodes[6]);
//
//        DefaultTreeModel ml = new DefaultTreeModel(nodes[0]);
        jTreeNavigation = new javax.swing.JTree(c2criWizardFactory.getTreeModel());
        jTreeNavigation.setRootVisible(false);
        jTreeNavigation.setUI(new javax.swing.plaf.basic.BasicTreeUI() {
            protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
//        if(showLines) super.paintHorizontalLine(g,c,y,left,right);
            }

            protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {
//        if(showLines) super.paintVerticalLine(g,c,x,top,bottom);
            }
        });

        // Clear the icons from the Tree Display and just show the words.
        javax.swing.tree.DefaultTreeCellRenderer renderer = new javax.swing.tree.DefaultTreeCellRenderer() {
            public Component getTreeCellRendererComponent(javax.swing.JTree tree,
                    Object value, boolean sel, boolean expanded, boolean leaf,
                    int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded,
                        leaf, row, hasFocus);
                DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) value;
                Object tipKey = theNode.getUserObject();
                if (tipKey instanceof C2CRIWizardPage) {
                    C2CRIWizardPage wizPage = (C2CRIWizardPage) theNode.getUserObject();
                    setToolTipText(wizPage.getDescription());
                } else {
                    setToolTipText(null);
                }
                return this;

            }

        };

        renderer.setIcon(null);
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        jTreeNavigation.setCellRenderer(renderer);

        //NOTE: For JTree to properly display tooltips of its renderers, JTree must be a registered component with the ToolTipManager. This can be done by invoking ToolTipManager.sharedInstance().registerComponent(tree). This is not done automatically!
        ToolTipManager.sharedInstance().registerComponent(jTreeNavigation);

//        jTreeNavigation.setCellRenderer(new DefaultTreeCellRenderer() {
//            public Component getTreeCellRendererComponent(javax.swing.JTree tree,
//                    Object value, boolean sel, boolean expanded, boolean leaf,
//                    int row, boolean hasFocus) {
//                super.getTreeCellRendererComponent(tree, value, sel, expanded,
//                        leaf, row, hasFocus);
//                if (!((InvisibleNode) value).isVisible()) {
//                    setForeground(Color.yellow);
//                }
//                return this;
//            }
//        });
        // first, build the wizard.  The TestFactory defines the
        // wizard content and behavior.
        final C2CRIWizardContainer wc
                = new C2CRIWizardContainer(c2criWizardFactory,
                        new TitledPageTemplate(),
                        new StackWizardSettings());

        c2criWizardFactory.registerButtonListener(wc);


        //do you want to store previously visited path and repeat it if you hit back
        //and then go forward a second time?
        //this options makes sense if you have a conditional path where depending on choice of a page
        // you can visit one of two other pages.
        wc.setForgetTraversedPath(
                true);

        // add a wizard listener to update the dialog titles and notify the
        // surrounding application of the state of the wizard:
        wc.addWizardListener(new WizardListener() {
            @Override
            public void onCanceled(List<WizardPage> path, WizardSettings settings
            ) {
                log.fine("settings: " + wc.getSettings());
                if (c2criWizardFactory.isOKToCancel()) C2CRIWizardDialog.this.dispose();  
            }

            @Override
            public void onFinished(List<WizardPage> path, WizardSettings settings
            ) {
                log.fine("settings: " + wc.getSettings());
                if (c2criWizardFactory.isOKToFinish()) C2CRIWizardDialog.this.dispose();   
            }

            @Override
            public void onPageChanged(WizardPage newPage, List<WizardPage> path
            ) {
                log.fine("settings: " + wc.getSettings());
                // Set the dialog title to match the description of the new page:
                C2CRIWizardDialog.this.setTitle(newPage.getDescription());

                //update our nav view
                //               jNavigation.setSelectedValue(newPage.getTitle(), true);
                JTreeUtil.setTreeExpandedState(jTreeNavigation, true);
                jTreeNavigation.setExpandsSelectedPaths(true);
                jTreeNavigation.setSelectionPath(new TreePath(c2criWizardFactory.getTreeModel().getPathToRoot(C2CRIWizardSwingAdapterFactory.findRelatedNode(c2criWizardFactory.getTreeModel(), (C2CRIWizardPage) newPage))));
            }
        }
        );

        //hightlight the first item since that's the start position of the wizard
        jTreeNavigation.setSelectionRow(
                0);
//        jListNavigation.setSelectedIndex(0);

        //TODO this is set to false to prevent users from selecting another list item
        //since there's no clear way to jump to a specific wizard page
//        jListNavigation.setEnabled(false);
//        jScrollPane1.setViewportView(jListNavigation);
        jTreeNavigation.setEnabled(
                false);
        JTreeUtil.setTreeExpandedState(jTreeNavigation,
                true);
        jScrollPane1.setViewportView(jTreeNavigation);

        // Set up the standard bookkeeping stuff for a dialog, and
        // add the wizard to the JDialog:
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        javax.swing.JLabel jProgressLabel = new javax.swing.JLabel("Progress");
        javax.swing.JPanel jPanel = new javax.swing.JPanel();

        jPanel.setLayout(
                new javax.swing.BoxLayout(jPanel, javax.swing.BoxLayout.Y_AXIS));
        jPanel.add(jProgressLabel);

        jPanel.add(jScrollPane1);
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());

        getContentPane()
                .setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                //                               .addComponent(jProgressLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(wc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(wc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        //                                        .addComponent(jProgressLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                                        .addComponent(jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE))
                                .addContainerGap())
        );

        pack();

    } 
    
    //    /**
    //     * Implementation of PageFactory to generate the wizard pages needed for the
    //     * wizard.
    //     */
    //    private class TestFactory extends APageFactory {
    //
    //        // To keep things simple, we'll just create an array of wizard pages:
    //        private final WizardPage[] pages = {
    //            new C2CRIWizardPage("One", "First C2CRI Page") {
    //                // this is an instance initializer -- it's a constructor for
    //                // an anonymous class.  WizardPages don't need to be anonymous,
    //                // of course.  It just makes the demo fit in one file if we do it
    //                // this way:
    //                {
    //                    JTextField field = new JTextField();
    //                    // set a name on any component that you want to collect values
    //                    // from.  Be sure to do this *before* adding the component to
    //                    // the WizardPage.
    //                    field.setName("testField");
    //                    field.setPreferredSize(new Dimension(50, 20));
    //                    add(new JLabel("One!"));
    //                    add(field);
    //                }
    //            },
    //            new C2CRIWizardPage("Two", "Second C2CRI Page") {
    //                {
    //                    JCheckBox box = new JCheckBox("testBox");
    //                    box.setName("box");
    //                    add(new JLabel("Two!"));
    //                    add(box);
    //                }
    //
    //                /* (non-Javadoc)
    //                * @see com.github.cjwizard.WizardPage#updateSettings(com.github.cjwizard.WizardSettings)
    //                 */
    //                @Override
    //                public void updateSettings(WizardSettings settings) {
    //                    super.updateSettings(settings);
    //
    //                    // This is called when the user clicks next, so we could do
    //                    // some longer-running processing here if we wanted to, and
    //                    // pop up progress bars, etc.  Once this method returns, the
    //                    // wizard will continue.  Beware though, this runs in the
    //                    // event dispatch thread (EDT), and may render the UI
    //                    // unresponsive if you don't issue a new thread for any long
    //                    // running ops.  Future versions will offer a better way of
    //                    // doing this.
    //                }
    //
    //            },
    //            new C2CRIWizardPage("Three", "Third C2CRI Page") {
    //                {
    //                    add(new JLabel("Three!"));
    //                    setBackground(Color.green);
    //                }
    //
    //            },
    //            new C2CRIWizardPage("Four", "Fourth C2CRI Page") {
    //                {
    //                    add(new JLabel("Four!"));
    //                }
    //
    //            },
    //            new C2CRIWizardPage("Five", "Fifth C2CRI Page") {
    //                {
    //                    add(new JLabel("Five!"));
    //
    //                }
    //
    //            },
    //            new WizardPage("Six", "Sixth C2CRI Page") {
    //                {
    //                    add(new JLabel("Six!"));
    //
    //                }
    //
    //                /**
    //                 * Returns a string representation of this wizard page.
    //                 */
    //                @Override
    //                public String toString() {
    //                    return getTitle();
    //                }
    //            },
    //            new WizardPage("Final", "Final C2CRI Page") {
    //                {
    //                    add(new JLabel("And we're done!"));
    //
    //                }
    //
    //                /**
    //                 * Returns a string representation of this wizard page.
    //                 */
    //                @Override
    //                public String toString() {
    //                    return getTitle();
    //                }
    //
    //                /**
    //                 * This is the last page in the wizard, so we will enable the
    //                 * finish button and disable the "Next >" button just before the
    //                 * page is displayed:
    //                 */
    //                public void rendering(List<WizardPage> path, WizardSettings settings) {
    //                    super.rendering(path, settings);
    //                    setFinishEnabled(true);
    //                    setNextEnabled(false);
    //                }
    //            }
    //        };
    //
    //        /* (non-Javadoc)
    //       * @see com.github.cjwizard.PageFactory#createPage(java.util.List, com.github.cjwizard.WizardSettings)
    //         */
    //        @Override
    //        public WizardPage createPage(List<WizardPage> path,
    //                WizardSettings settings) {
    //            log.fine("creating page " + path.size());
    //
    //            // Get the next page to display.  The path is the list of all wizard
    //            // pages that the user has proceeded through from the start of the
    //            // wizard, so we can easily see which step the user is on by taking
    //            // the length of the path.  This makes it trivial to return the next
    //            // WizardPage:
    //            WizardPage page = pages[path.size()];
    //
    //            // if we wanted to, we could use the WizardSettings object like a
    //            // Map<String, Object> to change the flow of the wizard pages.
    //            // In fact, we can do arbitrarily complex computation to determine
    //            // the next wizard page.
    //            log.fine("Returning page: " + page);
    //            return page;
    //        }
    //
    //    }
    //
    //    class InvisibleTreeModel extends DefaultTreeModel {
    //
    //        protected boolean filterIsActive;
    //
    //        public InvisibleTreeModel(TreeNode root) {
    //            this(root, false);
    //        }
    //
    //        public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren) {
    //            this(root, false, false);
    //        }
    //
    //        public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren,
    //                boolean filterIsActive) {
    //            super(root, asksAllowsChildren);
    //            this.filterIsActive = filterIsActive;
    //        }
    //
    //        public void activateFilter(boolean newValue) {
    //            filterIsActive = newValue;
    //        }
    //
    //        public boolean isActivatedFilter() {
    //            return filterIsActive;
    //        }
    //
    //        public Object getChild(Object parent, int index) {
    //            if (filterIsActive) {
    //                if (parent instanceof InvisibleNode) {
    //                    return ((InvisibleNode) parent).getChildAt(index,
    //                            filterIsActive);
    //                }
    //            }
    //            return ((TreeNode) parent).getChildAt(index);
    //        }
    //
    //        public int getChildCount(Object parent) {
    //            if (filterIsActive) {
    //                if (parent instanceof InvisibleNode) {
    //                    return ((InvisibleNode) parent).getChildCount(filterIsActive);
    //                }
    //            }
    //            return ((TreeNode) parent).getChildCount();
    //        }
    //
    //    }
    //
    //    class InvisibleNode extends DefaultMutableTreeNode {
    //
    //        protected boolean isVisible;
    //
    //        public InvisibleNode() {
    //            this(null);
    //        }
    //
    //        public InvisibleNode(Object userObject) {
    //            this(userObject, true, true);
    //        }
    //
    //        public InvisibleNode(Object userObject, boolean allowsChildren,
    //                boolean isVisible) {
    //            super(userObject, allowsChildren);
    //            this.isVisible = isVisible;
    //        }
    //
    //        public TreeNode getChildAt(int index, boolean filterIsActive) {
    //            if (!filterIsActive) {
    //                return super.getChildAt(index);
    //            }
    //            if (children == null) {
    //                throw new ArrayIndexOutOfBoundsException("node has no children");
    //            }
    //
    //            int realIndex = -1;
    //            int visibleIndex = -1;
    //            Enumeration e = children.elements();
    //            while (e.hasMoreElements()) {
    //                InvisibleNode node = (InvisibleNode) e.nextElement();
    //                if (node.isVisible()) {
    //                    visibleIndex++;
    //                }
    //                realIndex++;
    //                if (visibleIndex == index) {
    //                    return (TreeNode) children.elementAt(realIndex);
    //                }
    //            }
    //
    //            throw new ArrayIndexOutOfBoundsException("index unmatched");
    //            //return (TreeNode)children.elementAt(index);
    //        }
    //
    //        public int getChildCount(boolean filterIsActive) {
    //            if (!filterIsActive) {
    //                return super.getChildCount();
    //            }
    //            if (children == null) {
    //                return 0;
    //            }
    //
    //            int count = 0;
    //            Enumeration e = children.elements();
    //            while (e.hasMoreElements()) {
    //                InvisibleNode node = (InvisibleNode) e.nextElement();
    //                if (node.isVisible()) {
    //                    count++;
    //                }
    //            }
    //
    //            return count;
    //        }
    //
    //        public void setVisible(boolean visible) {
    //            this.isVisible = visible;
    //        }
    //
    //        public boolean isVisible() {
    //            return isVisible;
    //        }
    //    }

    public JFrame getParentFrame() {
        return parentFrame;
    }   
    
}
