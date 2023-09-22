/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard.testconfig.edit.page;

import java.awt.KeyboardFocusManager;
import javax.swing.JCheckBox;
import javax.swing.table.TableCellRenderer;
import org.fhwa.c2cri.domain.testmodel.TestConfigurationController;
import org.fhwa.c2cri.gui.AppLayerTestCasePanel;
import org.fhwa.c2cri.gui.InfoLayerTestCasePanel;
import org.fhwa.c2cri.gui.TestCasesTableModel;
import org.fhwa.c2cri.gui.components.TestCaseEditJButton;
import org.fhwa.c2cri.gui.components.TestCaseViewJButton;
import org.fhwa.c2cri.gui.components.TestCasesTableButtonEditor;
import org.fhwa.c2cri.gui.components.TestCasesTableButtonRenderer;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPage;

/**
 *
 * @author TransCore ITS, LLC
 */
public class ConfigureTestCaseDataPage extends C2CRIWizardPage {

    AppLayerTestCasePanel appTestCasePanel;
    InfoLayerTestCasePanel infoTestCasePanel;
    TestConfigurationController controller;
    
    /**
     * Creates new form SelectNeedsPage
     */
    public ConfigureTestCaseDataPage(String title, String description, TestConfigurationController controller, boolean isAppLayerTestCase) {
        super(title,description);
        this.controller = controller;
        if (isAppLayerTestCase){
            appTestCasePanel= new AppLayerTestCasePanel();
            add(appTestCasePanel);
            initAppLayerTestCasesPanel();
        }  else {
            infoTestCasePanel = new InfoLayerTestCasePanel();
            add(infoTestCasePanel);
            initInfoLayerTestCasesPanel();
        }
        
        
        
//        initComponents();
    }

    
    /**
     * Sets the Test Cases Parameters on the Information Layer Standard
     * Parameters Config Panel.
     */
    private void initInfoLayerTestCasesPanel() {
        System.out.println(" Loading the Info Layer Test Cases Panel  ....... ");
        //Ensure the tables do not currently have sorters activated

        TestCasesTableModel testCasesTableModel = new TestCasesTableModel(controller.getInfoLayerParams().getTestCases());

        infoTestCasePanel.getTestCasesTable().setModel(testCasesTableModel);
        
        // Add a renderer to allow the Edit and View buttons to be available in the Test Cases Table.
        TableCellRenderer tableRenderer;
        tableRenderer = infoTestCasePanel.getTestCasesTable().getDefaultRenderer(TestCaseEditJButton.class);
        infoTestCasePanel.getTestCasesTable().setDefaultRenderer(TestCaseEditJButton.class, new TestCasesTableButtonRenderer(tableRenderer));
        infoTestCasePanel.getTestCasesTable().setDefaultEditor(TestCaseEditJButton.class, new TestCasesTableButtonEditor(new JCheckBox()));
        infoTestCasePanel.getTestCasesTable().setDefaultRenderer(TestCaseViewJButton.class, new TestCasesTableButtonRenderer(tableRenderer));
        infoTestCasePanel.getTestCasesTable().setDefaultEditor(TestCaseViewJButton.class, new TestCasesTableButtonEditor(new JCheckBox()));
        // Allow the created tables to be edited using the keyboard only.
        infoTestCasePanel.getTestCasesTable().setSurrendersFocusOnKeystroke(true);

        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.
        infoTestCasePanel.getTestCasesTable().setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        infoTestCasePanel.getTestCasesTable().setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        infoTestCasePanel.getTestCasesTable().setFocusCycleRoot(false);

        testCasesTableModel.fireTableStructureChanged();
    }

    /**
     * Sets the Test Cases Parameters on the Application Layer Standard
     * Parameters Config Panel.
     */
    private void initAppLayerTestCasesPanel() {
        System.out.println(" Loading the App Layer Test Cases Panel  ....... ");
        //Ensure the tables do not currently have sorters activated

        TestCasesTableModel testCasesTableModel = new TestCasesTableModel(controller.getAppLayerParams().getTestCases());

        appTestCasePanel.getTestCasesTable().setModel(testCasesTableModel);
        // Add a renderer to allow the Edit and View buttons to be available in the Test Cases Table.
        TableCellRenderer tableRenderer;
        tableRenderer = appTestCasePanel.getTestCasesTable().getDefaultRenderer(TestCaseEditJButton.class);
        appTestCasePanel.getTestCasesTable().setDefaultRenderer(TestCaseEditJButton.class, new TestCasesTableButtonRenderer(tableRenderer));
        appTestCasePanel.getTestCasesTable().setDefaultEditor(TestCaseEditJButton.class, new TestCasesTableButtonEditor(new JCheckBox()));
        appTestCasePanel.getTestCasesTable().setDefaultRenderer(TestCaseViewJButton.class, new TestCasesTableButtonRenderer(tableRenderer));
        appTestCasePanel.getTestCasesTable().setDefaultEditor(TestCaseViewJButton.class, new TestCasesTableButtonEditor(new JCheckBox()));

        // Allow the created tables to be edited using the keyboard only.
        appTestCasePanel.getTestCasesTable().setSurrendersFocusOnKeystroke(true);

        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.
        appTestCasePanel.getTestCasesTable().setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        appTestCasePanel.getTestCasesTable().setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        appTestCasePanel.getTestCasesTable().setFocusCycleRoot(false);
        
        
        testCasesTableModel.fireTableStructureChanged();
    }    
    
    @Override
    public boolean isCheckRequiredBeforeCancel() {
        return true;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
