package org.fhwa.c2cri.gui.testmodel.testcasedata.editor;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 * The Class EditorPanelFileChooser.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class EditorPanelFileChooser extends JPanel {

	/** The p. */
	ParameterFile p;
	
	/** The j value. */
	JComponent jValue;

	/**
	 * Instantiates a new editor panel file chooser.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param pf the pf
	 * @param isRenderer the is renderer
	 */
	public EditorPanelFileChooser(ParameterFile pf, boolean isRenderer) {
		this.p = pf;

		setLayout(new GridBagLayout());
		setBackground(Color.white);

		if (isRenderer)
			jValue = new EditorLabel(p);
		else
			jValue = new EditorTextField(p);
		
		EditorButton jbChoose = new EditorButton("...");

		jbChoose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File tempFile = new File(p.getValue());
				JFileChooser fc = new JFileChooser(tempFile);
				fc.setFileSelectionMode(p.getFType());
				if(p.getFType() == JFileChooser.FILES_ONLY)
					fc.setDialogTitle("Choose the file");
				else if(p.getFType() == JFileChooser.DIRECTORIES_ONLY)
					fc.setDialogTitle("Choose the directory");
				if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					p.setValue(fc.getSelectedFile().getPath());
					((EditorTextField)jValue).setText(p.toString());
				}
			}
		});

		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.fill = GridBagConstraints.BOTH;
		c.gridwidth = GridBagConstraints.RELATIVE;

		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(0, 2, 0, 0);
		add(jValue, c);
		c.weightx = 0;
		add(jbChoose, c);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	public void setBackground(Color c) {
		super.setBackground(c);
		if (jValue != null)
			jValue.setBackground(c);
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setForeground(java.awt.Color)
	 */
	public void setForeground(Color c) {
		super.setForeground(c);
		if (jValue != null)
			jValue.setForeground(c);
	}

}