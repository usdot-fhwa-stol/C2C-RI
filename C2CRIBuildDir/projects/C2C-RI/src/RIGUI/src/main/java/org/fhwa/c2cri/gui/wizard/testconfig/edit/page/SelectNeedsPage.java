/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui.wizard.testconfig.edit.page;

import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import org.fhwa.c2cri.domain.testmodel.TestConfigurationController;
import org.fhwa.c2cri.gui.NeedsTableModel;
import org.fhwa.c2cri.gui.SelectionFlagEditor;
import org.fhwa.c2cri.gui.SelectionFlagListener;
import org.fhwa.c2cri.gui.SelectionFlagRenderer;
import org.fhwa.c2cri.gui.TextAreaRenderer;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPage;
import org.fhwa.c2cri.testmodel.DefaultLayerParameters;
import org.fhwa.c2cri.testmodel.Need;
import org.fhwa.c2cri.testmodel.Requirement;
import org.fhwa.c2cri.testmodel.UserNeedsInterface;

/**
 *
 * @author TransCore ITS, LLC
 */
public class SelectNeedsPage extends C2CRIWizardPage implements java.awt.event.ActionListener {

    /**
     * The sorter.
     */
    private TableRowSorter<NeedsTableModel> sorter;
    private final boolean appLayerNeedsPage;
    TestConfigurationController controller;

    /**
     * Creates new form SelectNeedsPage
     */
    public SelectNeedsPage(String title, String description, TestConfigurationController controller, boolean configureAppLayer) {
        super(title, description);
        this.controller = controller;
        initComponents();
        initNeedsPanel(standardNeedsTable, configureAppLayer ? controller.getAppLayerParams() : controller.getInfoLayerParams());
        appLayerNeedsPage = configureAppLayer;
        needsClearOptionsButton.addActionListener(this);
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
    private void initNeedsPanel(final JTable standardNeedsTable, final DefaultLayerParameters layerParams) {
        System.out.println(" Loading the Parameter Panel  ....... ");
        //Ensure the tables do not currently have sorters activated
        standardNeedsTable.setRowSorter(null);

        NeedsTableModel needsTableModel = new NeedsTableModel(layerParams.getNrtm());

        standardNeedsTable.setModel(needsTableModel);

        // To prevent tabbing between individual cells of the table disable the 
        // functions in the tables.
        standardNeedsTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
        standardNeedsTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        standardNeedsTable.setFocusCycleRoot(false);

        needsTableModel.fireTableStructureChanged();

        // For Debugging Only -- Safe to remove
        if (standardNeedsTable.getRowCount() != needsTableModel.getRowCount()) {
            System.out.println("NEEDS TABLE and MODEL Rowcount Mismatch");
        }
        ;
        // For Debugging Only -- Safe to remove

        needsTableModel.fireTableDataChanged();

        sorter = new TableRowSorter<NeedsTableModel>(needsTableModel);
        standardNeedsTable.setRowSorter(sorter);
        standardNeedsTable.getColumn(UserNeedsInterface.flagValue_Header).setCellRenderer(new SelectionFlagRenderer(UserNeedsInterface.type_Header));

        SelectionFlagListener unselectedNeedListener = new SelectionFlagListener() {

            @Override
            public void flagValueSetUpdate(int tableRow) {
            }

            @Override
            public void flagValueClearedUpdate(int tableModelRow) {

                String deselectedNeed = layerParams.getNrtm().getUserNeeds().needs.get(tableModelRow).getTitle();
                // Gather a list of optional selected requirements associated with this need
                for (Requirement thisProjectRequirement : layerParams.getNrtm().getUserNeeds().getNeed(deselectedNeed).getProjectRequirements().requirements) {
                    if ((thisProjectRequirement.getFlagValue()) && (!thisProjectRequirement.getType().equals("M") && !thisProjectRequirement.getType().equals("Mandatory"))) {
                        thisProjectRequirement.setFlagValue(false);
                    }
                }
            }
        };
        SelectionFlagEditor infoLayerFlagEditor = new SelectionFlagEditor(UserNeedsInterface.type_Header);
        infoLayerFlagEditor.registerSelectionFlagListener(unselectedNeedListener);
        standardNeedsTable.getColumn(UserNeedsInterface.flagValue_Header).setCellEditor(infoLayerFlagEditor);

        //When selection changes, provide user with row numbers for
        //both view and model.
        standardNeedsTable.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent event) {
                int viewRow = standardNeedsTable.getSelectedRow();
                if (viewRow < 0) {
                    //Selection got filtered away.
                } else {

                }
            }
        });

        TableColumnModel needModel = standardNeedsTable.getColumnModel();
        TextAreaRenderer textAreaRendererNeeds = new TextAreaRenderer();
        needModel.getColumn(NeedsTableModel.Text_Col).setCellRenderer(textAreaRendererNeeds);

    }

    public boolean isAppLayerNeedsPage() {
        return appLayerNeedsPage;
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        if (appLayerNeedsPage) {
            for (Need theNeed : controller.getAppLayerParams().getNrtm().getUserNeeds().needs) {
                if ((!theNeed.getType().equals("M")) && (!theNeed.getType().equals("Mandatory"))) {
                    theNeed.setFlagValue(false);
                    ((NeedsTableModel) standardNeedsTable.getModel()).fireTableDataChanged();
                    for (Requirement theRequirement : controller.getAppLayerParams().getNrtm().getUserNeeds().getNeed(theNeed.getTitle()).getProjectRequirements().requirements) {
                        if ((!theRequirement.getType().equals("M")) && (!theRequirement.getType().equals("Mandatory"))) {
                            theRequirement.setFlagValue(false);
                        }
                    }
                }
            }
        ((NeedsTableModel) standardNeedsTable.getModel()).fireTableDataChanged();
        
        } else {
            for (Need theNeed : controller.getInfoLayerParams().getNrtm().getUserNeeds().needs) {
                if ((!theNeed.getType().equals("M")) && (!theNeed.getType().equals("Mandatory"))) {
                    theNeed.setFlagValue(false);
                    ((NeedsTableModel) standardNeedsTable.getModel()).fireTableDataChanged();
                    for (Requirement theRequirement : controller.getInfoLayerParams().getNrtm().getUserNeeds().getNeed(theNeed.getTitle()).getProjectRequirements().requirements) {
                        if ((!theRequirement.getType().equals("M")) && (!theRequirement.getType().equals("Mandatory"))) {
                            theRequirement.setFlagValue(false);
                        }
                    }
                }
            }
            ((NeedsTableModel) standardNeedsTable.getModel()).fireTableDataChanged();            
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

        selectNeedPanel = new javax.swing.JPanel();
        selectNeedsScrollPane = new javax.swing.JScrollPane();
        standardNeedsTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        needsClearOptionsButton = new javax.swing.JButton();

        selectNeedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Need"));
        selectNeedPanel.setPreferredSize(new java.awt.Dimension(678, 154));

        selectNeedsScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        selectNeedsScrollPane.setViewportBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        selectNeedsScrollPane.setAutoscrolls(true);
        selectNeedsScrollPane.setPreferredSize(new java.awt.Dimension(650, 72));

        standardNeedsTable.setAutoCreateRowSorter(true);
        standardNeedsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"2.3.1.1.1", "Need for Subscription Request", "M"},
                {"2.3.1.1.2", "Need to communicate DMS Inventory", "O"},
                {"2.3.1.1.3", "Need to provide CCTV Control", "M"},
                {"2.3.1.1.4", "Need to process FE Update", "O"},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Need", "Need Text", "Project Requirements"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        standardNeedsTable.setToolTipText("Select Information layer standard needs.");
        standardNeedsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        standardNeedsTable.setFillsViewportHeight(true);
        standardNeedsTable.setName("InfoNeeds"); // NOI18N
        standardNeedsTable.setOpaque(false);
        standardNeedsTable.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        selectNeedsScrollPane.setViewportView(standardNeedsTable);

        jPanel1.setPreferredSize(new java.awt.Dimension(100, 23));

        needsClearOptionsButton.setText("Clear Optional");
        needsClearOptionsButton.setToolTipText("De-select all optional Needs");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(122, Short.MAX_VALUE)
                .addComponent(needsClearOptionsButton)
                .addContainerGap(143, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(needsClearOptionsButton)
        );

        javax.swing.GroupLayout selectNeedPanelLayout = new javax.swing.GroupLayout(selectNeedPanel);
        selectNeedPanel.setLayout(selectNeedPanelLayout);
        selectNeedPanelLayout.setHorizontalGroup(
            selectNeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, selectNeedPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(selectNeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(selectNeedsScrollPane, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))
                .addContainerGap())
        );
        selectNeedPanelLayout.setVerticalGroup(
            selectNeedPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, selectNeedPanelLayout.createSequentialGroup()
                .addComponent(selectNeedsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(selectNeedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(selectNeedPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    protected javax.swing.JButton needsClearOptionsButton;
    private javax.swing.JPanel selectNeedPanel;
    private javax.swing.JScrollPane selectNeedsScrollPane;
    protected javax.swing.JTable standardNeedsTable;
    // End of variables declaration//GEN-END:variables
}
