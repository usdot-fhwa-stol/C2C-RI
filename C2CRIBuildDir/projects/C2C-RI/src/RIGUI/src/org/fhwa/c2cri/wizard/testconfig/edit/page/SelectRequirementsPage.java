/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard.testconfig.edit.page;

import com.github.cjwizard.WizardPage;
import com.github.cjwizard.WizardSettings;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.fhwa.c2cri.domain.testmodel.TestConfigurationController;
import org.fhwa.c2cri.gui.SelectionFlagEditor;
import org.fhwa.c2cri.gui.SelectionFlagListener;
import org.fhwa.c2cri.gui.SelectionFlagRenderer;
import org.fhwa.c2cri.gui.TextAreaRenderer;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPage;
import org.fhwa.c2cri.testmodel.DefaultLayerParameters;
import org.fhwa.c2cri.testmodel.ProjectRequirementsInterface;
import org.fhwa.c2cri.testmodel.Requirement;
import org.fhwa.c2cri.testmodel.UserNeedsInterface;

/**
 *
 * @author TransCore ITS, LLC
 */
public class SelectRequirementsPage extends C2CRIWizardPage implements java.awt.event.ActionListener {

    /**
     * The sorter2.
     */
    private TableRowSorter<RequirementsTableModel> sorter2;

    private String currentNeed;

    private boolean appLayerSelected;

    TestConfigurationController controller;

    /**
     * Creates new form SelectNeedsPage
     */
    public SelectRequirementsPage(String title, String description, TestConfigurationController controller, boolean appLayerSelected, String selectedNeed) {
        super(title, description);
        initComponents();
        this.controller = controller;
        initRequirementsPanel(standardRequirementsTable, parametersTable, appLayerSelected ? controller.getAppLayerParams() : controller.getInfoLayerParams(), selectedNeed);
        this.appLayerSelected = appLayerSelected;
        currentNeed = selectedNeed;
        requirementsClearAllButton.addActionListener(this);
    }

    /**
     * Inits the parameters panel.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param standardNeedsTable the standard needs table
     * @param standardRequirementsTable the standard requirements table
     * @param parametersTable the parameters table
     * @param layerParams the layer params
     */
    private void initRequirementsPanel(final JTable standardRequirementsTable, final JTable parametersTable, final DefaultLayerParameters layerParams, final String needId) {
        System.out.println(" Loading the Parameter Panel  ....... ");
        //Ensure the tables do not currently have sorters activated
        standardRequirementsTable.setRowSorter(null);
        parametersTable.setRowSorter(null);

        RequirementsTableModel requirementsTableModel = new RequirementsTableModel(layerParams.getNrtm(), needId);
        OtherRequirementsTableModel otherRequirementsTableModel = new OtherRequirementsTableModel(layerParams.getNrtm(), needId);

        standardRequirementsTable.setModel(requirementsTableModel);
        parametersTable.setModel(otherRequirementsTableModel);

        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.      
        standardRequirementsTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        standardRequirementsTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        standardRequirementsTable.setFocusCycleRoot(false);

        parametersTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        parametersTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        parametersTable.setFocusCycleRoot(false);

        requirementsTableModel.fireTableStructureChanged();
        otherRequirementsTableModel.fireTableStructureChanged();



        requirementsTableModel.fireTableDataChanged();
        otherRequirementsTableModel.fireTableDataChanged();

        SelectionFlagListener unselectedNeedListener = new SelectionFlagListener() {

            @Override
            public void flagValueSetUpdate(int tableRow) {
                standardRequirementsTable.setEnabled(true);
                parametersTable.setEnabled(true);
            }

            @Override
            public void flagValueClearedUpdate(int tableModelRow) {

                String deselectedNeed = layerParams.getNrtm().getUserNeeds().needs.get(tableModelRow).getTitle();

                List<String> selectedRequirementsList = new ArrayList<String>();
                // Gather a list of optional selected requirements associated with this need
                for (Requirement thisProjectRequirement : layerParams.getNrtm().getUserNeeds().getNeed(deselectedNeed).getProjectRequirements().requirements) {
                    if ((thisProjectRequirement.getFlagValue()) && (!thisProjectRequirement.getType().equals("M") && !thisProjectRequirement.getType().equals("Mandatory"))) {

                        thisProjectRequirement.setFlagValue(false);
                        ((RequirementsTableModel) standardRequirementsTable.getModel()).fireTableDataChanged();
                    }

                }

                int currentRow = standardRequirementsTable.getSelectionModel().getLeadSelectionIndex();
                standardRequirementsTable.getSelectionModel().removeSelectionInterval(currentRow, currentRow);
                standardRequirementsTable.setEnabled(false);
                parametersTable.setEnabled(false);
            }
        };
        SelectionFlagEditor infoLayerFlagEditor = new SelectionFlagEditor(UserNeedsInterface.type_Header);
        infoLayerFlagEditor.registerSelectionFlagListener(unselectedNeedListener);

        //When selection changes, provide user with row numbers for
        //both view and model.
        //When selection changes, provide user with row numbers for
        //both view and model.
        standardRequirementsTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                int viewRow = standardRequirementsTable.getSelectedRow();

                if (parametersTable.isEditing()) {
                    parametersTable.getCellEditor().cancelCellEditing();
                }

                if (viewRow < 0) {
                    //Selection got filtered away.
                } else {
                    Boolean requirementSelected = (Boolean) standardRequirementsTable.getValueAt(standardRequirementsTable.getSelectedRow(), standardRequirementsTable.getColumn(ProjectRequirementsInterface.flagValue_Header).getModelIndex());
                    if (requirementSelected) {
                        parametersTable.setEnabled(true);
                    } else {
                        parametersTable.setEnabled(false);
                    }
                }
            }
        });

        otherRequirementsTableModel.setRequirementListSelectionTable(standardRequirementsTable);
        standardRequirementsTable.getSelectionModel().addListSelectionListener(otherRequirementsTableModel);

        sorter2 = new TableRowSorter<RequirementsTableModel>(requirementsTableModel);
        SelectionFlagListener projectRequirementSelectedListener = new SelectionFlagListener() {

            @Override
            public void flagValueSetUpdate(int tableRow) {
                parametersTable.setEnabled(true);
            }

            @Override
            public void flagValueClearedUpdate(int tableModelRow) {
                if (parametersTable.isEditing()) {
                    parametersTable.getCellEditor().cancelCellEditing();
                }

                parametersTable.setEnabled(false);
            }
        };

        SelectionFlagEditor projectRequirementsFlagEditor = new SelectionFlagEditor(ProjectRequirementsInterface.type_Header);
        projectRequirementsFlagEditor.registerSelectionFlagListener(projectRequirementSelectedListener);
        standardRequirementsTable.getColumn(ProjectRequirementsInterface.flagValue_Header).setCellRenderer(new SelectionFlagRenderer(ProjectRequirementsInterface.type_Header));
        standardRequirementsTable.getColumn(ProjectRequirementsInterface.flagValue_Header).setCellEditor(projectRequirementsFlagEditor);

        TableColumnModel cmodel = standardRequirementsTable.getColumnModel();
        TextAreaRenderer textAreaRenderer = new TextAreaRenderer();
        cmodel.getColumn(RequirementsTableModel.Text_Col).setCellRenderer(textAreaRenderer);

        TableColumnModel parameterModel = parametersTable.getColumnModel();
        TextAreaRenderer textAreaParameters = new TextAreaRenderer();
        parameterModel.getColumn(OtherRequirementsTableModel.Text_Col).setCellRenderer(textAreaParameters);

        requirementsTableModel.fireTableDataChanged();
        otherRequirementsTableModel.fireTableDataChanged();

    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        for (Requirement theRequirement : appLayerSelected ? controller.getAppLayerParams().getNrtm().getUserNeeds().getNeed(currentNeed).getProjectRequirements().requirements
                : controller.getInfoLayerParams().getNrtm().getUserNeeds().getNeed(currentNeed).getProjectRequirements().requirements) {
            if ((!theRequirement.getType().equals("M")) && (!theRequirement.getType().equals("Mandatory"))) {
                theRequirement.setFlagValue(false);
            }
        }
        ((RequirementsTableModel) standardRequirementsTable.getModel()).fireTableDataChanged();

    }

    @Override
    public void rendering(List<WizardPage> path, WizardSettings settings) {
        super.rendering(path, settings); 
       ((RequirementsTableModel) standardRequirementsTable.getModel()).fireTableDataChanged();
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

        testParametersPanel = new javax.swing.JPanel();
        parametersScrollPane = new javax.swing.JScrollPane();
        parametersTable = new javax.swing.JTable();
        selectRequirementsPanel = new javax.swing.JPanel();
        standardRequirementsScrollPane = new javax.swing.JScrollPane();
        standardRequirementsTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        requirementsClearAllButton = new javax.swing.JButton();

        testParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Test Parameters"));
        testParametersPanel.setPreferredSize(new java.awt.Dimension(678, 99));

        parametersScrollPane.setPreferredSize(new java.awt.Dimension(452, 0));

        parametersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"SubscriptionDelay", "The maximum time (in milliseconds to await a response", "1000"}
            },
            new String [] {
                "Parameter", "Description", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        parametersTable.setToolTipText("Set information layer standard parameters.");
        parametersTable.setFillsViewportHeight(true);
        parametersScrollPane.setViewportView(parametersTable);

        javax.swing.GroupLayout testParametersPanelLayout = new javax.swing.GroupLayout(testParametersPanel);
        testParametersPanel.setLayout(testParametersPanelLayout);
        testParametersPanelLayout.setHorizontalGroup(
            testParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testParametersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(parametersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addContainerGap())
        );
        testParametersPanelLayout.setVerticalGroup(
            testParametersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testParametersPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(parametersScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                .addContainerGap())
        );

        selectRequirementsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Requirements"));
        selectRequirementsPanel.setPreferredSize(new java.awt.Dimension(678, 142));

        standardRequirementsScrollPane.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        standardRequirementsScrollPane.setPreferredSize(new java.awt.Dimension(452, 110));

        standardRequirementsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"3.3.1.1.1", "Subscription Request", "M",  new Boolean(true)},
                {"3.3.1.1.2", "DMS Inventory", "O",  new Boolean(true)},
                {"3.3.1.1.3", "CCTV Control", "M",  new Boolean(true)},
                {"3.3.1.1.4", "FE Update", "O", null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Requirement", "Description", "Project Requirements", "Selected"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        standardRequirementsTable.setToolTipText("Selection information layer standard requirements.");
        standardRequirementsTable.setFillsViewportHeight(true);
        standardRequirementsTable.setMinimumSize(new java.awt.Dimension(60, 60));
        standardRequirementsScrollPane.setViewportView(standardRequirementsTable);

        jPanel2.setPreferredSize(new java.awt.Dimension(100, 23));

        requirementsClearAllButton.setText("Clear Optional");
        requirementsClearAllButton.setToolTipText("De-select all optional Requirements");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(227, Short.MAX_VALUE)
                .addComponent(requirementsClearAllButton)
                .addContainerGap(246, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(requirementsClearAllButton)
        );

        javax.swing.GroupLayout selectRequirementsPanelLayout = new javax.swing.GroupLayout(selectRequirementsPanel);
        selectRequirementsPanel.setLayout(selectRequirementsPanelLayout);
        selectRequirementsPanelLayout.setHorizontalGroup(
            selectRequirementsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, selectRequirementsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selectRequirementsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(standardRequirementsScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE))
                .addContainerGap())
        );
        selectRequirementsPanelLayout.setVerticalGroup(
            selectRequirementsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, selectRequirementsPanelLayout.createSequentialGroup()
                .addComponent(standardRequirementsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(testParametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                    .addComponent(selectRequirementsPanel, javax.swing.GroupLayout.Alignment.TRAILING, 0, 608, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(selectRequirementsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(testParametersPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel2;
    protected javax.swing.JScrollPane parametersScrollPane;
    protected javax.swing.JTable parametersTable;
    protected javax.swing.JButton requirementsClearAllButton;
    private javax.swing.JPanel selectRequirementsPanel;
    private javax.swing.JScrollPane standardRequirementsScrollPane;
    protected javax.swing.JTable standardRequirementsTable;
    private javax.swing.JPanel testParametersPanel;
    // End of variables declaration//GEN-END:variables
}
