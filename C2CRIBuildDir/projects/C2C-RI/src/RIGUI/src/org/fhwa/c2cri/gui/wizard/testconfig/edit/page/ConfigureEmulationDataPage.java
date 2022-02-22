/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard.testconfig.edit.page;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import org.fhwa.c2cri.centermodel.EmulationDataFileProcessor;
import org.fhwa.c2cri.centermodel.RIEmulationEntityValueSet;
import org.fhwa.c2cri.domain.testmodel.TestConfigurationController;
import org.fhwa.c2cri.gui.EmulationParametersPanel;
import org.fhwa.c2cri.gui.EmulationParametersTableModel;
import org.fhwa.c2cri.gui.EntityDataViewer;
import org.fhwa.c2cri.gui.LoadEntityDataForm;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardDialog;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPage;

/**
 *
 * @author TransCore ITS, LLC
 */
public class ConfigureEmulationDataPage extends C2CRIWizardPage implements java.awt.event.ActionListener {

    EmulationParametersPanel emulationParametersPanel;
    TestConfigurationController controller;

    /**
     * Creates new form SelectNeedsPage
     */
    public ConfigureEmulationDataPage(String title, String description, TestConfigurationController controller) {
        super(title, description);
        this.controller = controller;
        emulationParametersPanel = new EmulationParametersPanel();
        add(emulationParametersPanel);
        initEmulationParametersPanel();
        emulationParametersPanel.getUpdateButton().addActionListener(this);
        emulationParametersPanel.getViewButton().addActionListener(this);
//        initComponents();
    }

    /**
     * Sets the Emulation Parameters on the Emulation Parameters Config Panel.
     */
    private void initEmulationParametersPanel() {
        System.out.println(" Loading the Emulation Parameters Panel  ....... ");
        EmulationParametersTableModel emulationParametersTableModel = new EmulationParametersTableModel(controller.getEmulationParameters());
        emulationParametersPanel.getEmulationParametersTable().setModel(emulationParametersTableModel);
        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.
        emulationParametersPanel.getEmulationParametersTable().setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        emulationParametersPanel.getEmulationParametersTable().setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        emulationParametersPanel.getEmulationParametersTable().setFocusCycleRoot(false);

        emulationParametersPanel.setCommandQueueLengthTextField(Integer.toString(controller.getEmulationParameters().getCommandQueueLength()));
//        emulationParametersPanel.getEmulationParametersTable().removeColumn(emulationParametersPanel.getEmulationParametersTable().getColumn(EmulationParametersTableModel.Data));

        /**
         * The addDocumentListener is used to determine what happens when the
         * CommandQueueLengthTextField is updated on the EntityEmulationParametersPanel.
         */
        emulationParametersPanel.getCommandQueueLengthTextField().setInputVerifier(new InputVerifier() {
            public boolean verify(JComponent input) {
                JTextField text = (JTextField) input;
                String value = text.getText().trim();
                try {
                    Integer newValue = Integer.parseInt(value);
//            int lastGood = controller.getEmulationParameters().getCommandQueueLength();
                    controller.getEmulationParameters().setCommandQueueLength(newValue);
                    if (controller.getEmulationParameters().getCommandQueueLength() != newValue) {
                        javax.swing.JOptionPane.showMessageDialog(null,
                                "The command queue length entered is invalid and has been set to the nearest valid value.",
                                "Command Queue Length Validation",
                                javax.swing.JOptionPane.INFORMATION_MESSAGE);
                        text.setText(String.valueOf(controller.getEmulationParameters().getCommandQueueLength()));
                    }

                } catch (NumberFormatException e) {
                    text.setText(String.valueOf(controller.getEmulationParameters().getCommandQueueLength()));
                    // assumed it should return false
                    return false;
                }
                return true;
            }

        });

        emulationParametersTableModel.fireTableStructureChanged();
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        Object source = arg0.getSource();

        if (source == emulationParametersPanel.getUpdateButton()) {
            try {
                Component component = (Component) source;
                JFrame frame = null;
                Object result = SwingUtilities.getRoot(component);                
                if (result instanceof JFrame){
                    frame = (JFrame) result;                    
                } else if (result instanceof C2CRIWizardDialog){
                    frame = ((C2CRIWizardDialog)result).getParentFrame();
                }
                LoadEntityDataForm load = new LoadEntityDataForm(frame, true);
                load.setTitle("Load Entity Data");
                int convertedRow = emulationParametersPanel.getEmulationParametersTable().convertRowIndexToModel(emulationParametersPanel.getEmulationParametersTable().getSelectedRow());
                load.EntityNameTextField.setText(emulationParametersPanel.getEmulationParametersTable().getModel().getValueAt(convertedRow, EmulationParametersTableModel.Title_Col).toString());
                load.setVisible(true);
//                    load.setDefaultCloseOperation(2);
                if (load.isDataUpdated()) {
                    emulationParametersPanel.getEmulationParametersTable().getModel().setValueAt(load.getDataSource(), convertedRow, EmulationParametersTableModel.Data);
                    emulationParametersPanel.getEmulationParametersTable().getModel().setValueAt(RIEmulationEntityValueSet.ENTITYDATASTATE.Updated.name(), convertedRow, EmulationParametersTableModel.Source);
                    ((EmulationParametersTableModel) emulationParametersPanel.getEmulationParametersTable().getModel()).fireTableDataChanged();
                }
                load.dispose();

            } catch (Exception Ex) {
                Ex.printStackTrace();
                System.out.println("The error occured clicking the \"Update\" button in the \"Entity Emulation Parameter Selection\" UI and the error is: \n" + Ex.getMessage());
            }

        } else if (source == emulationParametersPanel.getViewButton()) {
            try {
                EntityDataViewer dataView = new EntityDataViewer(true);
                dataView.setTitle("Entity Data Viewer");
                dataView.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

                int selectedTableRow = emulationParametersPanel.getEmulationParametersTable().getSelectedRow();
                int modelRow = emulationParametersPanel.getEmulationParametersTable().convertRowIndexToModel(selectedTableRow);
                byte[] emulationData = (byte[]) emulationParametersPanel.getEmulationParametersTable().getModel().getValueAt(modelRow, EmulationParametersTableModel.Data);
                dataView.EntityDataViewerTextArea.setText(EmulationDataFileProcessor.getContent(emulationData).toString());
                dataView.setVisible(true);

            } catch (Exception Ex) {
                System.out.println("The error occured clicking the \"View\" button in the \"Entity Emulation Parameter Selection\" UI and the error is: \n" + Ex.getMessage());
                Ex.printStackTrace();
            }

        }
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
