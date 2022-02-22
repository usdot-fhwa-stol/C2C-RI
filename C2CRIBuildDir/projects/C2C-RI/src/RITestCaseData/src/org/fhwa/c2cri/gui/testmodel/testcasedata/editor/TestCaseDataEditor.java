package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseDataParser;
import org.fhwa.c2cri.testmodel.testcasedata.TestCaseFile;

/**
 * The Class TestCaseDataEditor.
 *
 * @author TransCore ITS, LLC Last Updated: 1/8/2014
 */
public class TestCaseDataEditor {

    /**
     * The tc file.
     */
    public static TestCaseFile tcFile;

    /**
     * Edits the file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param f the f
     */
    public static void editFile(Frame f) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogTitle("Choose a property file...");

        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                openFile(f, fc.getSelectedFile().toURI());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            //System.exit(0);
        }
    }

    /**
     * Open file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param f the f
     * @param selectedFile the selected file
     */
    public static void openFile(Frame f, URI selectedFile) {
        try {
            tcFile = new TestCaseFile(selectedFile);

            tcFile.init();

            int n = TestCaseDataParser.parsePropertyFile(tcFile);

            if (n > 0) {
                TestCaseEditorDialog ed = new TestCaseEditorDialog(f, tcFile);
                ed.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Could not parse property file:\n" + selectedFile.getPath(),
                        "Error in Property File",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Could not parse property file:\n" + selectedFile.getPath(),
                    "Error in Property File: " + ex.getMessage(),
                    JOptionPane.ERROR_MESSAGE);

        }
    }

    /**
     * Open file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param f the f
     * @param selectedFile the selected file
     */
    public static void openFile(Frame f, File selectedFile) {
        tcFile = new TestCaseFile(selectedFile.getPath());

        tcFile.init();

        int n = TestCaseDataParser.parsePropertyFile(tcFile);

        if (n > 0) {
            TestCaseEditorDialog ed = new TestCaseEditorDialog(f, tcFile);
            ed.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Could not parse property file:\n" + selectedFile.getAbsolutePath(),
                    "Error in Property File",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Open file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param f the f
     * @param selectedFile the selected file
     */
    public static void openFile(Frame f, File userFile, URI baselineFile) {

        TestCaseFile baseFile = new TestCaseFile(baselineFile);
        TestCaseFile newFile = new TestCaseFile(userFile.getPath());
        baseFile.init();
        newFile.init();
        int n = TestCaseDataParser.parsePropertyFile(baseFile);
        int n2 = TestCaseDataParser.parsePropertyFile(newFile);

        tcFile = TestCaseFile.mergeFiles(newFile, baseFile);

        n = n + n2;

//		tcFile.init();
//		int n = TestCaseDataParser.parsePropertyFile(tcFile);
        if (n > 0) {
            TestCaseEditorDialog ed = new TestCaseEditorDialog(f, tcFile);
            ed.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Could not parse property file:\n" + tcFile.getAbsolutePath(),
                    "Error in Property File",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Open file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param f the f
     * @param selectedFile the selected file
     */
    public static void viewFile(Frame f, File userFile, URI baselineFile) {

        TestCaseFile baseFile = new TestCaseFile(baselineFile);
        TestCaseFile newFile = new TestCaseFile(userFile.getPath());
        baseFile.init();
        newFile.init();
        int n = TestCaseDataParser.parsePropertyFile(baseFile);
        int n2 = TestCaseDataParser.parsePropertyFile(newFile);

        tcFile = TestCaseFile.mergeFiles(newFile, baseFile);

        n = n + n2;

//		tcFile.init();
//		int n = TestCaseDataParser.parsePropertyFile(tcFile);
        if (n > 0) {
            TestCaseViewDialog vd = new TestCaseViewDialog(f, tcFile);
            vd.setVisible(true);
            vd.dispose();
        } else {
            JOptionPane.showMessageDialog(null,
                    "Could not parse property file:\n" + tcFile.getAbsolutePath(),
                    "Error in Property File",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Close file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @return the int
     */
    public static int closeFile() {
        /*    	
        if(eFile.isModified()) {
            Object[] options = {"Save", "Don't Save", "Cancel"};
            
            int n = JOptionPane.showOptionDialog(null,
            "Are you sure you want to close the file without saving?", "Warning",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
            null, options, options[0]
            );
            
            return n;
        }
         */
        return 1;
    }

    /**
     * Save file.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     */
    public static void saveFile() {
        if (tcFile.exists() && tcFile.isModified()) {
            try {
                String newLine = System.getProperty("line.separator");
                BufferedWriter bw = new BufferedWriter(new FileWriter(tcFile));
                for (int i = 0; i < tcFile.numIteration(); i++) {
                    System.out.println(tcFile.numGroup());
                    bw.write(tcFile.iterationAt(i).toText() + newLine);
                    for (int j = 0; j < tcFile.numGroup(); j++) {
                        bw.write(tcFile.iterationAt(i).groupAt(j).toText(true) + newLine);
                    }
                }
                bw.close();

                tcFile.setModified(false);
            } catch (Exception e) {
                //System.out.println("Error when saving");
                JOptionPane.showMessageDialog(null,
                        "Could not save properties:\n" + tcFile.getAbsolutePath()
                        + ".\nPlease contact IPSP Support.",
                        "Error in Saving Properties",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String args[]) throws Exception {

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        if (args.length > 1) {
            openFile(null, new File(args[0]), new URL(args[1]).toURI());
            viewFile(null, new File(args[0]), new URL(args[1]).toURI());
        } else {

            //Create a Frame to demo the Propertyeditor component
            JFrame.setDefaultLookAndFeelDecorated(true);
            JFrame frame = new JFrame("Property Editor Demo");

            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            frame.setLocation((d.width - frame.getWidth()) / 2, (d.height - frame.getHeight()) / 2);

            JMenuBar menuBar = new JMenuBar();
            frame.setJMenuBar(menuBar);

            JMenu projectMenu = new JMenu("Property Editor");
            menuBar.add(projectMenu);

            JMenuItem loadProject = new JMenuItem("Load Property Editor");
            projectMenu.add(loadProject);
            loadProject.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {

                    editFile(null);
                }
            }
            );

            JMenuItem exit = new JMenuItem("Exit");
            projectMenu.add(exit);
            exit.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    // TODO Auto-generated method stub
                    System.exit(0);

                }

            }
            );

            frame.getContentPane().setLayout(new BorderLayout());
            frame.setVisible(true);
        }
    }

}
