package org.fhwa.c2cri.gui.propertyeditor;




import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
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
 * The Class PropertyEditorDialog.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class PropertyEditorDialog
	extends JDialog
	implements Observer, ListSelectionListener {

	/** The e file. */
	private EditorFile eFile;
	
	/** The tables. */
	private JTable tables[];
	
	/** The j tp. */
	private JTabbedPane jTp;
	
	/** The doc editor pane. */
	private JEditorPane docEditorPane;

	/**
	 * Instantiates a new property editor dialog.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param f the f
	 * @param myeFile the mye file
	 */
	public PropertyEditorDialog(Frame f, EditorFile myeFile) {
		super(f);
		this.eFile = myeFile;

		eFile.addObserver(this);

		setSize(400, 500);
		setLocation(UIUtils.getCenterScreenLocation(400, 500));

		setResizable(false);
		setModal(true);

		setTitle("Properties Editor");
		JPanel cPane = (JPanel) getContentPane();

		cPane.setLayout(new GridBagLayout());

		cPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

		JButton jbOk = new JButton("  Ok  ");
		jbOk.setToolTipText("Save Properties");
		JButton jbCancel = new JButton("Cancel");
		jbCancel.setToolTipText("Cancel Property Edits");

		jbCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				
			}
		});

		jbOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PropEditor.saveFile();
				dispose();
			}
		});

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

		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		c.gridwidth = 1;
		c.weighty = 0;
		c.weightx = 1;
		c.insets = new Insets(10, 0, 0, 10);

		cPane.add(jbOk, c);
		c.weightx = 0;
		cPane.add(jbCancel, c);

		setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int n = PropEditor.closeFile();
				switch (n) {
					case 0 :
						PropEditor.saveFile();
						break;
					case 1 :
						break;
					default:
						// do nothing
						break;
				}
			}
		});

	}

	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		if (eFile.isModified())
			setTitle("*Properties Editor (Edited)");
		else
			setTitle("Properties Editor");
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