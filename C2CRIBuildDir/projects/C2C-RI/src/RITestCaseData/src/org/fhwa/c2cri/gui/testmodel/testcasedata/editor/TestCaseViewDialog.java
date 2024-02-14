package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.StringWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.utils.UIUtils;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseFile;

/**
 * The Class TestCaseEditorDialog.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestCaseViewDialog
        extends JDialog {

    /**
     * The tc file.
     */
    private TestCaseFile tcFile;

    /**
     * The doc editor pane.
     */
    private JEditorPane docEditorPane;

    /**
     * Instantiates a new test case editor dialog.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param f the f
     * @param mytcFile the mytc file
     */
    public TestCaseViewDialog(Frame f, TestCaseFile mytcFile) {
        super(f);
        this.tcFile = mytcFile;
        StringWriter sw = new StringWriter();

        if (tcFile.exists() || tcFile.numIteration() > 0 || tcFile.numGroup() > 0) {
            try {
                String newLine = System.getProperty("line.separator");
                BufferedWriter bw = new BufferedWriter(sw);
                System.out.println(tcFile.numGroup());
                for (int i = 0; i < tcFile.numIteration(); i++) {
                    bw.write(tcFile.iterationAt(i).toText() + newLine);
                    for (int j = 0; j < tcFile.numGroup(); j++) {
                        bw.write(tcFile.groupAt(j).toText() + newLine);
                    }
                }
                bw.flush();
                bw.close();

            } catch (Exception e) {
                //System.out.println("Error when saving");
                JOptionPane.showMessageDialog(null,
                        "Could not View properties:\n" + tcFile.getAbsolutePath(),
                        "Error in Viewing Properties",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        setSize(700, 750);
        setLocation(UIUtils.getCenterScreenLocation(400, 500));

        setResizable(true);
        setModal(true);

        setTitle("Test Case Parameters Viewer");
        StringBuffer sb = sw.getBuffer();
        updateContainer(sb.toString());

        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

    }

    public void updateContainer(String content) {

        JPanel cPane = (JPanel) getContentPane();

        cPane.removeAll();
        cPane.setLayout(new GridBagLayout());

        cPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        docEditorPane = new JEditorPane();
        docEditorPane.setName("TestCaseViewDialog");
        //       docEditorPane.setEditorKit(new HTMLEditorKit());
        docEditorPane.setEditable(false);
        docEditorPane.setText(content);
        docEditorPane.setFocusable(false);

        final JScrollPane docScrollPane = new JScrollPane(docEditorPane);
        docScrollPane.setBorder(BorderFactory.createEtchedBorder());
        docScrollPane.setVerticalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        docScrollPane.setHorizontalScrollBarPolicy(
                javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        docScrollPane.setBounds(0, 0, 700, 500);
        docScrollPane.setFocusable(true);
        docScrollPane.setName("TestCaseViewDialogScrollPane");
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                docScrollPane.getViewport().setViewPosition( new java.awt.Point(0, 0) );
            }
        });
        JButton jbClose = new JButton("Close");
        jbClose.setToolTipText("Close Property Viewer");
        jbClose.setName("TestCaseViewDialog:CloseButton");

        jbClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();

            }
        });

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.weighty = 0.15;
        c.ipadx = 600;
        c.ipady = 750;
        cPane.add(docScrollPane, c);

        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.gridwidth = 1;
        c.weighty = 0;
        c.weightx = 1;
        c.ipady = 0;
        c.insets = new Insets(10, 0, 0, 10);

        c.weightx = 0;
        cPane.add(jbClose, c);

        cPane.validate();

    }

}
