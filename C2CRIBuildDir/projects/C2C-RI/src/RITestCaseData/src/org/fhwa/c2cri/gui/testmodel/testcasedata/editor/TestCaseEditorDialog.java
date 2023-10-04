package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.AbstractAction;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;

import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.utils.UIUtils;
import org.fhwa.c2cri.testmodel.testcasedata.Iteration;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseFile;

/**
 * The Class TestCaseEditorDialog.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestCaseEditorDialog
        extends JDialog
        implements Observer, ListSelectionListener {

    /**
     * The tc file.
     */
    private TestCaseFile tcFile;

    /**
     * The tables.
     */
    private JTable tables[];

    /**
     * The j tp.
     */
    private JTabbedPane jTp;

    /**
     * The j iteration tp.
     */
    private JTabbedPane jIterationTp;

    /**
     * The doc editor pane.
     */
    private JEditorPane docEditorPane;

    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    // toolbar 
    JToolBar tb;

    private String selectedIteration;
    private String selectedGroup;
    private Parameter selectedParameter;

    private static final String RENAME_ITERATION = "Rename Iteration";
    private static final String REPLACE_GROUP = "Replace Group";
    private static final String GROUP_ADD = "Add Group";
    private static final String GROUP_REMOVE = "Remove Group";
    private static final String PARAMETER_ADD = "Add Parameter";
    private static final String PARAMETER_REMOVE = "Remove Parameter";
    private static final String PARAMETER_UP = "Move Parameter Up";
    private static final String PARAMETER_DOWN = "Move Parameter Down";
    ImageIcon tmpIcon = new ImageIcon("RenameIteration.png");

    private final AbstractAction _renameIterationAction = new AbstractAction(RENAME_ITERATION, createIcon("RenameIteration")) {
        {
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            final JLabel l = new JLabel("The existing name is " + selectedIteration);

            JPanel p = new JPanel(new GridLayout(2, 1, 10, 10));
            p.setPreferredSize(new Dimension(400, 50));
            final JTextField t = new JTextField("");
            t.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    try {
                        String newName = t.getText();
                        l.setText(newName.isBlank() ? "The existing name is " + selectedIteration : "The existing name " + selectedIteration + " will be changed to: \n" + newName);
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                    }
                }
            });
            p.add(t);
            p.add(l);

            int option = JOptionPane.showConfirmDialog(null, p, "Rename Iteration: Enter new Iteration Name", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == 0) {
                tcFile.renameIteration(selectedIteration, t.getText());
                System.out.println("ok clicked");
            } else {
                System.out.println("cancel clicked");
            }
        }
    };

    private final AbstractAction _replaceGroupAction = new AbstractAction(REPLACE_GROUP, createIcon("ReplaceGroup")) {

        {
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final JLabel l = new JLabel("The existing name is " + selectedGroup);

            JPanel p = new JPanel(new GridLayout(2, 1, 10, 10));
            p.setPreferredSize(new Dimension(400, 50));
            final JTextField t = new JTextField("");
            t.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent evt) {
                    try {
                        String newName = t.getText();
                        l.setText(newName.isBlank() ? "The existing name is " + selectedGroup : "The existing name " + selectedGroup + " will be changed to: \n" + newName);
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                    }
                }
            });
            p.add(t);
            p.add(l);

            int option = JOptionPane.showConfirmDialog(null, p, "Replace Group: Enter new Group Name", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == 0) {
                System.out.println("ok clicked");
                if (tcFile.isGroupNameAvailable(selectedIteration, t.getText().trim())){
                    int option2 = JOptionPane.showConfirmDialog(null, "Clear existing parameters from group?", "Group Parameters Action", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
                    if (option2 == 0) {
                        tcFile.replaceGroup(selectedIteration, selectedGroup, t.getText(), true);
                        System.out.println("ok clicked");
                    } else {
                        tcFile.replaceGroup(selectedIteration, selectedGroup, t.getText(), false);
                        System.out.println("cancel clicked");
                    }               
                } else {
                    JOptionPane.showMessageDialog(rootPane, t.getText() +" already exists in baseline test case file.", "Group Rename Error", JOptionPane.ERROR_MESSAGE);                    
                }
            } else {
                System.out.println("cancel clicked");
            }
        }
    };

    private final AbstractAction _addGroupAction = new AbstractAction(GROUP_ADD, createIcon("RenameGroup")) {

        {
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            JPanel p = new JPanel(new GridLayout(2, 1, 10, 10));
            p.setPreferredSize(new Dimension(400, 50));
            final JTextField t = new JTextField("");
            p.add(t);

            int option = JOptionPane.showConfirmDialog(null, p, "Add Group: Enter new Group Name", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (option == 0) {
                tcFile.addGroup(selectedIteration, t.getText());
                System.out.println("ok clicked");
            } else {
                System.out.println("cancel clicked");
            }
        }
    };

    private final AbstractAction _removeGroupAction = new AbstractAction(GROUP_REMOVE, createIcon("RenameGroup")) {

        {
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(null, "Do you wish to delete the group: " + selectedGroup + "?", "Confirm Group Deletion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == 0) {
                tcFile.removeGroup(selectedIteration, selectedGroup);
                System.out.println("ok clicked");
            } else {
                System.out.println("cancel clicked");
            }
            
        }
    };
    
    
    private final AbstractAction _parameterAddAction = new AbstractAction(PARAMETER_ADD, createIcon("AddParameter")) {

        {
            setEnabled(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

//            final JTextField t = new JTextField("");
//            t.addKeyListener(new java.awt.event.KeyAdapter() {
//                public void keyReleased(java.awt.event.KeyEvent evt) {
//                    try {
//                        String newName = t.getText();
//                        l.setText(newName.isBlank()?"The existing name is " + selectedGroup:"The existing name " + selectedGroup + " will be changed to: \n" + newName);
//                    } catch (Exception ex) {
//                        // ex.printStackTrace();
//                    }
//                }
//            });
            AddParameterPanel p = new AddParameterPanel();
            p.setSymbolPanelEnabled(false);
            p.setStringPanelEnabled(true);
            boolean okToExit = false;
            while (!okToExit) {
                int option = JOptionPane.showConfirmDialog(null, p, "Add Parameter", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (option == 0) {
                    System.out.println("ok clicked");
                    Parameter param = null;
                    try {
                        if (p.getParameterType().equals("String")) {
                            if (!p.isValidParameterLength()) {
                                throw new Exception("Parameter Maximum Length can not be less than Minimum Length");
                            }
                            String pMin = p.isParameterMinLengthSelected() ? String.valueOf(p.getParameterMinLength()) : null;
                            String pMax = p.isParameterMaxLengthSelected() ? String.valueOf(p.getParameterMaxLength()) : null;

                            param = new ParameterString(p.getParameterName(), p.getParameterValue(), true, p.getParameterDescription(), pMin, pMax);

                        } else if (p.getParameterType().equals("Boolean")) {
                            param = new ParameterBoolean(p.getParameterName(), p.getParameterValue(), true, p.getParameterDescription());

                        } else if (p.getParameterType().equals("Symbol")) {
                            param = new ParameterSymbol(p.getParameterName(), p.getParameterValue(), p.getParameterTypeValues(), true, p.getParameterDescription());

                        } else if (p.getParameterType().equals("File")) {
                            File tempFile = null;
                            try {
                                tempFile = new File(new URI(p.getParameterValue()));
                            } catch (URISyntaxException ex) {
                                System.out.println("Invalid URI:" + p.getParameterValue());
                                ex.printStackTrace();
                                // Try if the path is normal path and not a URI
                                tempFile = new File(p.getParameterValue());
                            } catch (IllegalArgumentException ex) {
                                System.out.println("Invalid URI:" + p.getParameterValue());
                                ex.printStackTrace();
                                // Try if the path is normal path and not a URI
                                tempFile = new File(p.getParameterValue());
                            }
                            String pValue = p.getParameterValue();
                            if (tempFile != null) {
                                pValue = tempFile.getAbsolutePath();
                            }
                            param = new ParameterFile(p.getParameterName(), pValue, ParameterFile.FILE, true, p.getParameterDescription());
                        }

                        tcFile.addParameter(selectedIteration, selectedGroup, selectedParameter, param);
                        okToExit = true;
                    } catch (Exception nex) {
                        nex.printStackTrace();
                        JOptionPane.showConfirmDialog(rootPane, nex.getMessage(), "Parameter Definition Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    System.out.println("cancel clicked");
                    okToExit = true;
                }
            }
        }
    };

    private final AbstractAction _parameterRemoveAction = new AbstractAction(PARAMETER_REMOVE, createIcon("RemoveParameter")) {

        {
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int option = JOptionPane.showConfirmDialog(null, "Do you wish to delete the parameter: " + selectedParameter.getName() + "?", "Confirm Parameter Deletion", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            if (option == 0) {
                tcFile.removeParameter(selectedIteration, selectedGroup, selectedParameter);
                System.out.println("ok clicked");
            } else {
                System.out.println("cancel clicked");
            }

        }
    };

    private final AbstractAction _parameterUpAction = new AbstractAction(PARAMETER_UP, createIcon("ParameterUp")) {

        {
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tcFile.moveParameterUp(selectedIteration, selectedGroup, selectedParameter);
        }
    };

    private final AbstractAction _parameterDownAction = new AbstractAction(PARAMETER_DOWN, createIcon("ParameterDown")) {

        {
            setEnabled(false);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            tcFile.moveParameterDown(selectedIteration, selectedGroup, selectedParameter);
        }
    };

    private final ChangeListener iterationChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent changeEvent) {
            JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            int index = sourceTabbedPane.getSelectedIndex();
            selectedIteration = sourceTabbedPane.getTitleAt(index);
            System.out.println("Iteration Tab changed to: " + sourceTabbedPane.getTitleAt(index));
            updateDocumentationPane();
            updateActions();
        }
    };

    private final ChangeListener groupChangeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent changeEvent) {
            JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            int index = sourceTabbedPane.getSelectedIndex();
            selectedGroup = sourceTabbedPane.getTitleAt(index);
            System.out.println("Group Tab changed to: " + sourceTabbedPane.getTitleAt(index));
            updateDocumentationPane();
            updateActions();
        }
    };

    /**
     * Instantiates a new test case editor dialog.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param f the f
     * @param mytcFile the mytc file
     */
    public TestCaseEditorDialog(Frame f, TestCaseFile mytcFile) {
        super(f);
        this.tcFile = mytcFile;

        tcFile.addObserver(this);

        setSize(400, 500);
        setLocation(UIUtils.getCenterScreenLocation(400, 500));

        setResizable(true);
        setModal(true);

//Where the GUI is created:
//Create the menu bar.
        menuBar = new JMenuBar();

//Build the first menu.
        menu = new JMenu("Edit");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

//a group of JMenuItems
        menuItem = new JMenuItem(_renameIterationAction);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        menu.add(menuItem);

        JMenuItem menuItem2 = new JMenuItem(_replaceGroupAction);
//        JMenuItem menuItem3 = new JMenuItem(_addGroupAction);
//        JMenuItem menuItem4 = new JMenuItem(_removeGroupAction);
        JMenuItem menuItem5 = new JMenuItem(_parameterAddAction);
        JMenuItem menuItem6 = new JMenuItem(_parameterRemoveAction);
        JMenuItem menuItem7 = new JMenuItem(_parameterUpAction);
        JMenuItem menuItem8 = new JMenuItem(_parameterDownAction);
        menu.add(menuItem2);
//        menu.add(menuItem3);
//        menu.add(menuItem4);
        menu.add(menuItem5);
        menu.add(menuItem6);
        menu.add(menuItem7);
        menu.add(menuItem8);

        setJMenuBar(menuBar);

        // create a toolbar 
        tb = new JToolBar();

        // create new buttons 
        JButton b1 = new JButton(_renameIterationAction);
        JButton b2 = new JButton(_replaceGroupAction);
//        JButton b3 = new JButton(_addGroupAction);
//        JButton b4 = new JButton(_removeGroupAction);
        JButton b5 = new JButton(_parameterAddAction);
        JButton b6 = new JButton(_parameterRemoveAction);
        JButton b7 = new JButton(_parameterUpAction);
        JButton b8 = new JButton(_parameterDownAction);
        b1.setToolTipText(b1.getText());
        b1.setText("");
        b2.setToolTipText(b2.getText());
        b2.setText("");
//        b3.setToolTipText(b3.getText());
//        b3.setText("");
//        b4.setToolTipText(b4.getText());
//        b4.setText("");
        b5.setToolTipText(b5.getText());
        b5.setText("");
        b6.setToolTipText(b6.getText());
        b6.setText("");
        b7.setToolTipText(b7.getText());
        b7.setText("");
        b8.setToolTipText(b8.getText());
        b8.setText("");

        tb.add(b1);
        tb.add(b2);
//        tb.add(b3);
//        tb.add(b4);
        tb.add(b5);
        tb.add(b6);
        tb.add(b7);
        tb.add(b8);
        
        setTitle("Test Case Parameters Editor");
        updateContainer();

//    iterationChangeListener = new ChangeListener() {
//      public void stateChanged(ChangeEvent changeEvent) {
//        JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
//        int index = sourceTabbedPane.getSelectedIndex();
//        selectedIteration = sourceTabbedPane.getTitleAt(index);
//        System.out.println("Iteration Tab changed to: " + sourceTabbedPane.getTitleAt(index));
//      }
//    };        
//    groupChangeListener = new ChangeListener() {
//      public void stateChanged(ChangeEvent changeEvent) {
//        JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
//        int index = sourceTabbedPane.getSelectedIndex();
//        selectedGroup = sourceTabbedPane.getTitleAt(index);
//        System.out.println("Group Tab changed to: " + sourceTabbedPane.getTitleAt(index));
//      }
//    };         
//        JPanel cPane = (JPanel) getContentPane();
//
//        cPane.setLayout(new GridBagLayout());
//
//        cPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//       
//        jIterationTp = new JTabbedPane();
//        int numI = tcFile.numIteration();  // number of Iterations
//
//        for (int iterationIndex = 0; iterationIndex < numI; iterationIndex++) {
//
//            Iteration thisIteration = tcFile.iterationAt(iterationIndex);
//
//            int numG = tcFile.iterationAt(iterationIndex).numGroups();  //number of groups
//
//            tables = new JTable[numG];
//
//            //TODO windowForComponent PARTOUT
//            jTp = new JTabbedPane();
//
//            //jTp.setCloseIcon(false);
//            //jTp.setMaxIcon(false);
//            for (int i = 0; i < numG; i++) {
//                Group g = tcFile.iterationAt(iterationIndex).groupAt(i);
//                tables[i] = g.toVisual();
//                tables[i].getSelectionModel().addListSelectionListener(this);
//
//                JScrollPane scrollPane = new JScrollPane(tables[i]);
//
//                scrollPane.setBorder(BorderFactory.createEmptyBorder());
//
//                jTp.addTab(g.getName(), scrollPane);
//
//            }
//
//            jIterationTp.addTab(thisIteration.getName(), jTp);
//
//        }
//
//        docEditorPane = new JEditorPane();
//        docEditorPane.setEditorKit(new HTMLEditorKit());
//        docEditorPane.setEditable(false);
//
//        JScrollPane docScrollPane = new JScrollPane(docEditorPane);
//        docScrollPane.setBorder(BorderFactory.createEtchedBorder());
//        docScrollPane.setVerticalScrollBarPolicy(
//                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
//
//        JButton jbSave = new JButton("Save");
//        jbSave.setToolTipText("Save Properties");
//        JButton jbCancel = new JButton("Cancel");
//        jbCancel.setToolTipText("Cancel Property Edits");
//
//        jbCancel.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                dispose();
//
//            }
//        });
//
//        jbSave.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                TestCaseDataEditor.saveFile();
//                dispose();
//            }
//        });
//
//        GridBagConstraints c = new GridBagConstraints();
//
//        c.anchor = GridBagConstraints.CENTER;
//        c.fill = GridBagConstraints.BOTH;
//        c.gridwidth = GridBagConstraints.REMAINDER;
//        
//        // Add the toolbar to the Pane.
//        cPane.add(tb, c);
//        
//        
//        c.weightx = 1;
//        c.weighty = 0.85;
//        c.insets = new Insets(0, 0, 10, 0);
//        cPane.add(jIterationTp, c);
//
//        c.weighty = 0.15;
//        cPane.add(docScrollPane, c);
//
//        c.fill = GridBagConstraints.NONE;
//        c.anchor = GridBagConstraints.EAST;
//        c.gridwidth = 1;
//        c.weighty = 0;
//        c.weightx = 1;
//        c.insets = new Insets(10, 0, 0, 10);
//
//        cPane.add(jbSave, c);
//        c.weightx = 0;
//        cPane.add(jbCancel, c);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int n = TestCaseDataEditor.closeFile();
                switch (n) {
                    case 0:
                        TestCaseDataEditor.saveFile();
						break;
                    case 1:
                        break;
					default:
						// do nothing
						break;
                }
            }
        });

    }

    public void updateContainer() {

        JPanel cPane = (JPanel) getContentPane();

        cPane.removeAll();
        cPane.setLayout(new GridBagLayout());

        cPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        jIterationTp = new JTabbedPane();
        int numI = tcFile.numIteration();  // number of Iterations

        for (int iterationIndex = 0; iterationIndex < numI; iterationIndex++) {

            Iteration thisIteration = tcFile.iterationAt(iterationIndex);

            int numG = tcFile.iterationAt(iterationIndex).numGroups();  //number of groups

            tables = new JTable[numG];

            //TODO windowForComponent PARTOUT
            jTp = new JTabbedPane();
            //jTp.setCloseIcon(false);
            //jTp.setMaxIcon(false);
            for (int i = 0; i < numG; i++) {
                Group g = tcFile.iterationAt(iterationIndex).groupAt(i);
                tables[i] = g.toVisual();
                tables[i].getSelectionModel().addListSelectionListener(this);

                JScrollPane scrollPane = new JScrollPane(tables[i]);

                scrollPane.setBorder(BorderFactory.createEmptyBorder());

                jTp.addTab(g.getName(), scrollPane);

            }

            jIterationTp.addTab(thisIteration.getName(), jTp);

        }

        docEditorPane = new JEditorPane();
        docEditorPane.setEditorKit(new HTMLEditorKit());
        docEditorPane.setEditable(false);

        JScrollPane docScrollPane = new JScrollPane(docEditorPane);
        docScrollPane.setBorder(BorderFactory.createEtchedBorder());
        docScrollPane.setVerticalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JButton jbSave = new JButton("Save");
        jbSave.setToolTipText("Save Properties");
        JButton jbCancel = new JButton("Cancel");
        jbCancel.setToolTipText("Cancel Property Edits");

        jbCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();

            }
        });

        jbSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TestCaseDataEditor.saveFile();
                dispose();
            }
        });

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;

        // Add the toolbar to the Pane.
        cPane.add(tb, c);

        c.weightx = 1;
        c.weighty = 0.85;
        c.insets = new Insets(0, 0, 10, 0);
        cPane.add(jIterationTp, c);

        c.weighty = 0.15;
        cPane.add(docScrollPane, c);

        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = 1;
        c.weighty = 0;
        c.weightx = 1;
        c.insets = new Insets(10, 0, 0, 10);

        cPane.add(jbSave, c);
        c.weightx = 0;
        cPane.add(jbCancel, c);

        jIterationTp.addChangeListener(iterationChangeListener);
        jTp.addChangeListener(groupChangeListener);

        updateSelections();
        updateDocumentationPane();
        cPane.validate();

    }

    private void updateSelections() {
        String previousIteration = selectedIteration;
        String previousGroup = selectedGroup;
        Parameter previousParameter = selectedParameter;

        for (int ii = 0; ii < jIterationTp.getTabCount(); ii++) {
            if ((previousIteration != null) && previousIteration.equals(jIterationTp.getTitleAt(ii))) {
                jIterationTp.setSelectedIndex(ii);
                break;
            }
        }

        for (int ii = 0; ii < jTp.getTabCount(); ii++) {
            if ((previousGroup != null) && previousGroup.equals(jTp.getTitleAt(ii))) {
                jTp.setSelectedIndex(ii);
                break;
            }
        }

        selectedIteration = jIterationTp.getTitleAt(jIterationTp.getSelectedIndex());
        System.out.println("Iteration Tab set to: " + selectedIteration);

        selectedGroup = jTp.getTitleAt(jTp.getSelectedIndex());
        System.out.println("Group Tab set to: " + selectedGroup);

        updateActions();

    }

    private void updateDocumentationPane() {

        int i = jTp.getSelectedIndex();

        if (i == -1 || i > tables.length) {
            docEditorPane.setText("");
            return;
        }

        JTable currentTable = tables[i];
        if (currentTable.getSelectedRowCount() < 1) {
            docEditorPane.setText("");
            return;
        }

        Object value
                = currentTable.getValueAt(currentTable.getSelectedRow(), 1);
        if (value instanceof Parameter) {
            docEditorPane.setText(
                    "<div style='font-family: Arial; font-size:11pt'>"
                    + "<b>"
                    + ((Parameter) value).getName()
                    + "</b>"
                    + (((Parameter) value).isBaselineProvided()?"<br>Baseline Parameter ":"")
                    + (((Parameter) value).isEditable()?"":"Not Editable")
                    + "<br><br>"        
                    + ((Parameter) value).getDoc()
                    + "</div>");
            selectedParameter = (Parameter) value;
        }
    }

    /* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
     */
    public void update(Observable o, Object arg) {
        if (tcFile.isModified()) {
            setTitle("*Properties Editor (Edited)");
            if (tcFile.isRefreshDesired())updateContainer();
        } else {
            setTitle("Properties Editor");
        }
    }

    private void updateActions() {
//        if (tcFile.isGroupRemovable(selectedIteration, selectedGroup)){
//            _removeGroupAction.setEnabled(true);
//        } else {
//            _removeGroupAction.setEnabled(false);            
//        }
        
        _parameterRemoveAction.setEnabled(false);
        _parameterUpAction.setEnabled(false);
        _parameterDownAction.setEnabled(false);

        if (selectedParameter != null) {
            int i = jTp.getSelectedIndex();

            if (i == -1 || i > tables.length) {
                _parameterRemoveAction.setEnabled(false);
                _parameterUpAction.setEnabled(false);
                _parameterDownAction.setEnabled(false);
                return;
            }

            JTable currentTable = tables[i];
            if (currentTable.getSelectedRowCount() < 1) {
                _parameterRemoveAction.setEnabled(false);
                _parameterUpAction.setEnabled(false);
                _parameterDownAction.setEnabled(false);
                return;
            }

            int currentSelectedRow = currentTable.getSelectedRow();

            Object value
                    = currentTable.getValueAt(currentTable.getSelectedRow(), 1);
            if (value instanceof Parameter) {
                Parameter thisParameter = (Parameter) value;
                if (!thisParameter.isBaselineProvided() && thisParameter.isEditable()) {
                    _parameterRemoveAction.setEnabled(true);
                    
                    // Check the currently selected parameter against the previous parameter to see if it can be moved up.
                    if (currentSelectedRow > 0) {
                        Object previousValue = currentTable.getValueAt(currentSelectedRow - 1, 1);
                        if (previousValue instanceof Parameter) {
                            Parameter previousParameter = (Parameter) previousValue;
                            if (!previousParameter.isBaselineProvided()) {
                                _parameterUpAction.setEnabled(true);
                            }
                        }
                    }

                    // Check the currently selected parameter against the next parameter to see if it can be moved up.
                    if (currentSelectedRow >= 0 && (currentTable.getRowCount() > currentSelectedRow + 1)) {
                        Object nextValue = currentTable.getValueAt(currentSelectedRow + 1, 1);
                        if (nextValue instanceof Parameter) {
                            Parameter nextParameter = (Parameter) nextValue;
                            if (!nextParameter.isBaselineProvided()) {
                                _parameterDownAction.setEnabled(true);
                            }
                        }
                    }                    
                } else if (thisParameter.isBaselineProvided()) {
                    return;
                }

            }

        } else {
            _parameterAddAction.setEnabled(true);            
        }
    }

    /* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        updateDocumentationPane();
        updateActions();

//        int i = jTp.getSelectedIndex();
//
//        if (i == -1 || i > tables.length) {
//            return;
//        }
//
//        JTable currentTable = tables[i];
//        if (currentTable.getSelectedRowCount() < 1) {
//            return;
//        }
//
//        Object value
//                = currentTable.getValueAt(currentTable.getSelectedRow(), 1);
//        if (value instanceof Parameter) {
//            docEditorPane.setText(
//                    "<div style='font-family: Arial; font-size:11pt'>"
//                    + "<b>"
//                    + ((Parameter) value).getName()
//                    + "</b><br><br>"
//                    + ((Parameter) value).getDoc()
//                    + "</div>");
//        }
    }

    /**
     * Returns an ImageIcon, or null if the path was invalid.
     */
    protected static ImageIcon createIcon(String imageName) {
        String imgLocation = imageName
                + ".png";
        java.net.URL imageURL = TestCaseEditorDialog.class.getResource(imgLocation);

        if (imageURL == null) {
            System.err.println("Resource not found: "
                    + imgLocation);
            return null;
        } else {
            return new ImageIcon(imageURL);
        }
    }
}
