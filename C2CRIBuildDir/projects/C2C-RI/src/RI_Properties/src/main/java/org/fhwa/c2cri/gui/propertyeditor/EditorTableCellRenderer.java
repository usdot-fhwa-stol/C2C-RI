package org.fhwa.c2cri.gui.propertyeditor;


import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;

import org.fhwa.c2cri.gui.propertyeditor.formattedtable.FormattedComboBox;
import org.fhwa.c2cri.gui.propertyeditor.formattedtable.FormattedFileChooser;
import org.fhwa.c2cri.gui.propertyeditor.formattedtable.FormattedLabel;
import org.fhwa.c2cri.gui.propertyeditor.formattedtable.FormattedTableRenderer;
import org.fhwa.c2cri.gui.propertyeditor.formattedtable.FormattedTextField;

/**
 * The Class EditorTableCellRenderer.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class EditorTableCellRenderer extends FormattedTableRenderer {

	/** The cell value. */
	private Object cellValue;

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

		if (value != null) {
			if (cellValue instanceof ParameterString) {
				o = new FormattedTextField(cellValue) {
					public void caretUpdate(CaretEvent e,
							FormattedTextField textField) {
					}
				};

			}
			else if (cellValue instanceof ParameterJDBCDriver) {
				

				o = new FormattedComboBox(cellValue,
						((ParameterJDBCDriver) cellValue).getAllowedValues(), value
								.toString()) {

					public void itemStateChanged(ItemEvent e) {
					}

				
			
				};

			}
			else if (cellValue instanceof ParameterSymbol) {
				o = new FormattedComboBox(cellValue,
						((ParameterSymbol) cellValue).getAllowedValues(), value
								.toString()) {

					public void itemStateChanged(ItemEvent e) {
					}
				};

			} else if (cellValue instanceof ParameterFile) {
				o = new FormattedFileChooser(cellValue,
						((ParameterFile) cellValue).getFType(), table
								.getColumnModel().getColumn(column).getWidth(),
						true) {

					public void caretUpdate(CaretEvent e,
							FormattedTextField textField) {
					}

					public void inputVerifier(FormattedLabel label) {
						String labelText = label.getText();
						File f = new File(labelText);
						if (labelText.equals(""))
							label.setText("(edit)");
						else if (!f.exists()) {
							label.setForeground(Color.red);
							label.setText(label.getText() + " (invalid)");
						}

					}

					public void fileChooserAction(ActionEvent e,
							FormattedFileChooser formattedFileChooser) {
					}

				};
			}

			// To set the tooltip text

			if (value instanceof Parameter) {
				Parameter p = (Parameter) value;
				String doc = p.getDoc();
				if (doc == null || doc.equals("")) {

					doc = "No documentation available for parameter "
							+ p.getName();
				}

				Font ttFont = UIManager.getFont("ToolTip.font");

				int ttWidth = SwingUtilities.computeStringWidth(table
						.getGraphics().getFontMetrics(ttFont), doc);

				ttWidth = Math.min(ttWidth + 10, 300);

				String toolTipText = "<html><body width='" + ttWidth
						+ "' style='margin: 3'>" + doc + "</body></html>";

				o.setToolTipText(toolTipText);

			}

		}

		return o;

	}

}