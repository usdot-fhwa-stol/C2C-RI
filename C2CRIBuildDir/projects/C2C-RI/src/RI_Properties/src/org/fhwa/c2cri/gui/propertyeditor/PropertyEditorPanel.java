package org.fhwa.c2cri.gui.propertyeditor;




import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.html.HTMLEditorKit;

import org.fhwa.c2cri.gui.propertyeditor.utils.UIUtils;


/**
 * The Class PropertyEditorPanel.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class PropertyEditorPanel
	extends JPanel
	implements ListSelectionListener {

	/** The e file. */
	private EditorFile eFile;
	
	/** The tables. */
	private JTable tables[];
	
	/** The j tp. */
	private JTabbedPane jTp;
	
	/** The doc editor pane. */
	private JEditorPane docEditorPane;

	/**
	 * Instantiates a new property editor panel.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param parentPanel the parent panel
	 * @param myeFile the mye file
	 */
	public PropertyEditorPanel(JPanel parentPanel, EditorFile myeFile) {
		this.eFile = myeFile;

		setSize(400, 300);
		setLocation(UIUtils.getCenterScreenLocation(400, 300));

		JPanel cPane = this;

		cPane.setLayout(new GridBagLayout());

//		cPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		cPane.setBorder(BorderFactory.createTitledBorder("Editor Panel"));

		int numG = eFile.numGroup(); //number of groups
		tables = new JTable[numG];


		jTp = new JTabbedPane();

		//jTp.setCloseIcon(false);
		//jTp.setMaxIcon(false);

		for (int i = 0; i < numG; i++) {
			Group g = eFile.groupAt(i);
			tables[i] = g.toVisual();
			tables[i].getSelectionModel().addListSelectionListener(this);

			JScrollPane scrollPane = new JScrollPane(tables[i]);

			scrollPane.setBorder(BorderFactory.createEmptyBorder());

			jTp.addTab(g.getName(), scrollPane);

		}

		docEditorPane = new JEditorPane();
		docEditorPane.setEditorKit(new HTMLEditorKit());
		docEditorPane.setEditable(false);

		JScrollPane docScrollPane = new JScrollPane(docEditorPane);
		docScrollPane.setBorder(BorderFactory.createEtchedBorder());
		docScrollPane.setVerticalScrollBarPolicy(
			javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.CENTER;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.REMAINDER;

		c.weightx = 1;
		c.weighty = 0.85;
		c.insets = new Insets(0, 0, 10, 0);
		cPane.add(jTp, c);

		c.weighty = 0.15;
		cPane.add(docScrollPane, c);
                parentPanel.add(this);

	}


	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {
		if (e.getValueIsAdjusting())
			return;

		int i = jTp.getSelectedIndex();

		if (i == -1 || i > tables.length)
			return;

		JTable currentTable = tables[i];
		if (currentTable.getSelectedRowCount() < 1)
			return;

		Object value =
			currentTable.getValueAt(currentTable.getSelectedRow(), 1);
		if (value instanceof Parameter)
			docEditorPane.setText(
				"<div style='font-family: Arial; font-size:11pt'>"
					+ "<b>"
					+ ((Parameter) value).getName()
					+ "</b><br><br>"
					+ ((Parameter) value).getDoc()
					+ "</div>");
	}

}