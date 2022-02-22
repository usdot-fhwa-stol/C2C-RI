/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.components;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.fhwa.c2cri.testmodel.TestCase;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.TestCaseDataEditor;

/**
 *
 * @author TransCore ITS, LLC
 */
public class TestCaseEditJButton extends JButton {

    private TestCase testCase;
    private final TestCaseEditActionListener actionListener;
    private final TestCaseCreationListener creationListener;
    private final int modelRow;
    
    public TestCaseEditJButton(TestCase testCase, TestCaseCreationListener listener, int modelRow) {
        super("Edit");
        this.testCase = testCase;
        this.setToolTipText("Edit the test case data.");
        this.creationListener = listener;
        this.modelRow = modelRow;
        actionListener = new TestCaseEditActionListener();
        this.addActionListener(actionListener);
        this.setEnabled(true);

    }

    public TestCase getTestCase() {
        return testCase;
    }

    public class TestCaseEditActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            System.out.println("Test Case Button Edit Event Pressed.");
            try {
                if ((testCase.getCustomDataLocation() == null)
                        || (testCase.getCustomDataLocation().equals(""))) {
                    Object frame = SwingUtilities.getRoot((Component) arg0.getSource());
                    JFileChooser j = new JFileChooser();
                    if (frame instanceof JFrame) {
                        int response = j.showOpenDialog((JFrame) frame);

                        if (response == JFileChooser.APPROVE_OPTION) {
                            String fileName = j.getSelectedFile().getAbsolutePath();
                            // Create the new file if it does not already exist.
                            File tcFile = new File(fileName);
                            if (tcFile.exists()){
                                // Create the new file if it does not already exist.
                                if (!isTextFile(fileName))throw new Exception("File "+fileName+" does not appear to be a valid text file.");
                            }
                            tcFile.createNewFile();
                            testCase.setCustomDataLocation(fileName);
                            editTestCaseFile((JFrame) frame, fileName, testCase.getDataUrlLocation().toURI());
                            creationListener.testCaseCreatedUpdate(modelRow);
                        }
                    } else {
                        int response = j.showOpenDialog(null);

                        if (response == JFileChooser.APPROVE_OPTION) {
                            String fileName = j.getSelectedFile().getAbsolutePath();
                            // Create the new file if it does not already exist.
                            File tcFile = new File(fileName);
                            // Check to see if the file appears to be valid if it exists.
                            if (tcFile.exists()){
                                if (!isTextFile(fileName))throw new Exception("File "+fileName+" does not appear to be a valid text file.");
                            }
                            tcFile.createNewFile();
                            testCase.setCustomDataLocation(fileName);
                            editTestCaseFile(null, fileName, testCase.getDataUrlLocation().toURI());
                            creationListener.testCaseCreatedUpdate(modelRow);
                        }

                    }
                } else {
                    Object frame = SwingUtilities.getRoot((Component) arg0.getSource());
                    if (!isTextFile(testCase.getCustomDataLocation()))throw new Exception("File "+testCase.getCustomDataLocation()+" does not appear to be a valid text file.");
                    if (frame instanceof JFrame) {
                        editTestCaseFile((JFrame) frame, testCase.getCustomDataLocation(), testCase.getDataUrlLocation().toURI());
                    } else {
                        editTestCaseFile(null, testCase.getCustomDataLocation(), testCase.getDataUrlLocation().toURI());
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unable to process the selected data file: " + ex.getMessage());
            }
        }

        private void editTestCaseFile(JFrame frame, String file, URI baseTestCaseFile) {
            try {
                TestCaseDataEditor.openFile(frame, new File(file), baseTestCaseFile);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Unable to process the selected data file: " + ex.getMessage());
            }
        }
        
        // CTCRI-787 Addressed by the use of this method.  The user should select a valid test parameter file.  An empty file is valid.
        private boolean isTextFile(String file){
            try{
                BufferedReader in = new BufferedReader(new FileReader(file)); 
                StringBuilder sb = new StringBuilder();
                int lineCount = 0;
                char[] characterArray = new char[1024];
                int charactersRead = 0;
                while (((charactersRead = in.read(characterArray,0,1023))>0)&&(lineCount<10)){
                    lineCount++;
                    sb.append(characterArray,0,charactersRead);
                }
                if (sb.length() == 0) return true;                
                String newString = sb.toString().replaceAll("[^\\p{Print}]", "");
                // If at least 90% of the characters are printable, then the file is likely a valid text file.
                if (newString.length() >= sb.length()*.90) return true;
                
            } catch (Exception ex){
                return false;
            }
            return false;
        }

    }

}
