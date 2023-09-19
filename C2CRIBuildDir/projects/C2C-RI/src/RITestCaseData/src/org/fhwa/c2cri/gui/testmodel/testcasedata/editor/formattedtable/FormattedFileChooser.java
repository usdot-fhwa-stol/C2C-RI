package org.fhwa.c2cri.gui.testmodel.testcasedata.editor.formattedtable;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.filechooser.FileFilter;

/**
 * The Class FormattedFileChooser.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public abstract class FormattedFileChooser
	extends JPanel
	implements FormattedFileInterface {
	
	/** The default file. */
	public static File DEFAULT_FILE = null;

	/** The o. */
	private Object o;
	
	/** The j value. */
	private JComponent jValue;
	
	/** The jb choose. */
	private FormattedButton jbChoose;
	
	/** The file type. */
	private int fileType;
	
	/** The dialog title. */
	private String dialogTitle = null;

	/**
	 * Instantiates a new formatted file chooser.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param o the o
	 * @param fileType the file type
	 * @param columnWidth the column width
	 * @param isRenderer the is renderer
	 */
	public FormattedFileChooser(
		Object o,
		int fileType,
		int columnWidth,
		boolean isRenderer) {

		this.o = o;
		this.fileType = fileType;
		
		setLayout(new GridBagLayout());
		setBackground(Color.white);

		if (isRenderer) {
			jValue = new FormattedLabel(o, columnWidth);
			if (fileType == JFileChooser.FILES_ONLY)
				((FormattedLabel) jValue).setIcon(
					UIManager.getIcon("FileView.fileIcon"));
			else if (fileType == JFileChooser.DIRECTORIES_ONLY)
				((FormattedLabel) jValue).setIcon(
					UIManager.getIcon("FileView.directoryIcon"));

			inputVerifier((FormattedLabel) jValue);
		} else {
			jValue = new FormattedTextField(o) {
				public void caretUpdate(
					CaretEvent e,
					FormattedTextField textField) {
					FormattedFileChooser.this.caretUpdate(e, textField);
				}

                            @Override
                            public void actionUpdate(ActionEvent e, FormattedTextField textField) {
                            }
                                
                                

			};
		}

		jbChoose = new FormattedButton("...", fileType);

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

	/**
	 * Gets the object.
	 *
	 * @return the object
	 */
	public Object getObject() {
		return o;
	}

	/**
	 * Sets the object.
	 *
	 * @param otherObject the new object
	 */
	public void setObject(Object otherObject) {
		o = otherObject;
	}

	/**
	 * Gets the text.
	 *
	 * @return the text
	 */
	public String getText() {
		if (jValue instanceof FormattedLabel)
			return ((FormattedLabel) jValue).getText();

		if (jValue instanceof FormattedTextField)
			return ((FormattedTextField) jValue).getText();

		return "";
	}

	/**
	 * Sets the text.
	 *
	 * @param s the new text
	 */
	public void setText(String s) {
		if (jValue instanceof FormattedLabel)
			 ((FormattedLabel) jValue).setText(s);

		if (jValue instanceof FormattedTextField)
			 ((FormattedTextField) jValue).setText(s);
	}

	/* (non-Javadoc)
	 * @see java.awt.Component#toString()
	 */
	public String toString() {
		return getText();
	}

	/**
	 * Gets the button.
	 *
	 * @return the button
	 */
	public FormattedButton getButton() {
		return jbChoose;
	}

	/**
	 * Update.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 */
	public void update() {
		if(!o.toString().equals(getText())) setText(o.toString());
		jValue.requestFocus();
	}
	
	/**
	 * Sets the dialog title.
	 *
	 * @param s the new dialog title
	 */
	public void setDialogTitle(String s){
		dialogTitle = s;
	}
	
	/**
	 * Gets the file chooser dialog.
	 *
	 * @return the file chooser dialog
	 */
	public JFileChooser getFileChooserDialog() {
		return getFileChooserDialog(null);
	}

	/**
	 * Gets the file chooser dialog.
	 *
	 * @param filter the filter
	 * @return the file chooser dialog
	 */
	public JFileChooser getFileChooserDialog(FileFilter filter) {
		
		JFileChooser fc = new JFileChooser();
		File tempFile = new File(o.toString());
		if(tempFile.exists()) fc.setCurrentDirectory(tempFile);
		else fc.setCurrentDirectory(DEFAULT_FILE);
		
		fc.setFileSelectionMode(fileType);
		fc.setFileFilter(filter);
		
		if (dialogTitle == null && fileType == JFileChooser.FILES_ONLY)
			fc.setDialogTitle("Choose the file");
		else if (
			dialogTitle == null && fileType == JFileChooser.DIRECTORIES_ONLY)
			fc.setDialogTitle("Choose the directory");
		else fc.setDialogTitle(dialogTitle);
		if (fc.showOpenDialog(SwingUtilities.windowForComponent(this))
			== JFileChooser.APPROVE_OPTION) {
			DEFAULT_FILE = fc.getSelectedFile();
			return fc;
		}

		return null;
	}

	/**
	 * The Class FormattedButton.
	 *
	 * @author open source community
	 * Last Updated:  1/8/2014
	 */
	public class FormattedButton extends JButton implements ActionListener {

		/**
		 * Instantiates a new formatted button.
		 * 
		 * Pre-Conditions: N/A
		 * Post-Conditions: N/A
		 *
		 * @param s the s
		 * @param fileType the file type
		 */
		public FormattedButton(String s, int fileType) {
			super(s);
			setMargin(new Insets(2, 2, 2, 2));
			setFocusPainted(false);
			addActionListener(this);

		}

		/* (non-Javadoc)
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			fileChooserAction(e, FormattedFileChooser.this);
		}
	}
}

interface FormattedFileInterface {

	//Only useful when used as an editor
	public void caretUpdate(CaretEvent e, FormattedTextField textField);

	//	Only useful when used as an editor
	public void fileChooserAction(
		ActionEvent e,
		FormattedFileChooser formattedFileChooser);

	//	Only useful when used as a renderer
	public void inputVerifier(FormattedLabel label);

}