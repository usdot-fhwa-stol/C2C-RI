package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;


import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.event.CaretEvent;

import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable.FormattedComboBox;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable.FormattedFileChooser;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable.FormattedLabel;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable.FormattedTableEditor;
import org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable.FormattedTextField;

/**
 * The Class EditorTableCellEditor.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class EditorTableCellEditor extends FormattedTableEditor {

	/** The cell value. */
	private transient Object cellValue = null;

	/** The o. */
	private JComponent o = null;

	/**
	 * Creates the component.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param table the table
	 * @param value the value
	 * @param isSelected the is selected
	 * @param hasFocus the has focus
	 * @param row the row
	 * @param column the column
	 * @return the j component
	 */
	public JComponent createComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		cellValue = value;

		if (cellValue instanceof ParameterString) {
			o = new FormattedTextField(cellValue) {

				public void caretUpdate(CaretEvent e, FormattedTextField textField) {
                                        String beforeValue = ((ParameterString) cellValue).getValue();
					((ParameterString) cellValue).setValue(textField.getText());
                                        if (!beforeValue.equals(textField.getText())) TestCaseDataEditor.tcFile.setModifiedNoRefresh(true);				}

                            @Override
                            public void actionUpdate(ActionEvent e, FormattedTextField textField) {
                                TestCaseDataEditor.tcFile.setModified(true);
                            }
                                
                                
			};

		}
		else if (cellValue instanceof ParameterJDBCDriver) {

			o = new FormattedComboBox(cellValue, ((ParameterJDBCDriver) cellValue)
					.getAllowedValues(), value.toString()) {

				public void itemStateChanged(ItemEvent e) {
					((ParameterJDBCDriver) cellValue).setValue(getSelectedItem()
							.toString());
					TestCaseDataEditor.tcFile.setModified(true);
				}
			};
		}
		else if (cellValue instanceof ParameterSymbol) {
			o = new FormattedComboBox(cellValue, ((ParameterSymbol) cellValue)
					.getAllowedValues(), value.toString()) {

				public void itemStateChanged(ItemEvent e) {
					((ParameterSymbol) cellValue).setValue(getSelectedItem()
							.toString());
					TestCaseDataEditor.tcFile.setModified(true);
				}
			};

		} else if (cellValue instanceof ParameterFile) {
			o = new FormattedFileChooser(cellValue, ((ParameterFile) cellValue)
					.getFType(), table.getColumnModel().getColumn(column)
					.getWidth(), false) {

				public void inputVerifier(FormattedLabel label) {
					// original implementation was empty
				}

				public void caretUpdate(CaretEvent e, FormattedTextField textField) {
					((ParameterFile) cellValue).setValue(textField.getText());
					TestCaseDataEditor.tcFile.setModified(true);

				}

				public void fileChooserAction(ActionEvent e,
						FormattedFileChooser formattedFileChooser) {

					JFileChooser fc = formattedFileChooser
							.getFileChooserDialog();
					if (fc == null) {
						formattedFileChooser.update();
						return;
					}

					((ParameterFile) cellValue).setValue(fc.getSelectedFile()
							.getPath());
					formattedFileChooser.update();
				}
			};
		}

		return o;
	}

}