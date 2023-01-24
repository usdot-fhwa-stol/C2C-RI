/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.fhwa.c2cri.testmodel.TestCase;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.TestCaseDataEditor;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestCaseViewJButton extends JButton {

    private TestCase testCase;
    private final TestCaseViewActionListener actionListener;

    public TestCaseViewJButton(TestCase testCase) {
        super("View");
        this.testCase = testCase;
        this.setToolTipText("View the test case data.");
        actionListener = new TestCaseViewActionListener();
        this.addActionListener(actionListener);
        this.setEnabled(true);

    }

    public TestCase getTestCase() {
        return testCase;
    }

    public class TestCaseViewActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            System.out.println("Test Case Button View Event Pressed.");
            try {

                Object frame = SwingUtilities.getRoot((Component) arg0.getSource());
                if (frame instanceof JFrame){
                    viewTestCaseFile((JFrame)frame, testCase.getCustomDataLocation(), testCase.getDataUrlLocation());
                } else {
                    viewTestCaseFile(null, testCase.getCustomDataLocation(), testCase.getDataUrlLocation());
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unable to process the selected data file: " + ex.getMessage());
            }
        }

        private void viewTestCaseFile(JFrame frame, String file, URL baseTestCaseData) {
            try {
                System.out.println("URI = " + baseTestCaseData.toURI().toString());
                TestCaseDataEditor.viewFile(frame, file==null||file.isBlank()?new File ("tmp"):new File(file), baseTestCaseData.toURI());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Unable to process the selected data file: " + ex.getMessage());
            }
        }

    }

}
