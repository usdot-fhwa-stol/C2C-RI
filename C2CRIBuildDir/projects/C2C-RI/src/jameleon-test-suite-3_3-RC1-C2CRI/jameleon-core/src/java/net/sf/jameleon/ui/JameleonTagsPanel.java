/*
    Jameleon - An automation testing tool..
    Copyright (C) 2006 Christian W. Hargraves (engrean@hotmail.com)

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package net.sf.jameleon.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.html.HTMLEditorKit;

import net.sf.jameleon.bean.Attribute;
import net.sf.jameleon.bean.FunctionalPoint;

public class JameleonTagsPanel extends JPanel implements FPDisplayer  {
	private static final long serialVersionUID = 1L;
	protected JTextField tagNameF = new JTextField(15);
    protected JTextField typeF = new JTextField(15);
    protected JTextField authorF = new JTextField(15);
    protected JTextField applicationsF = new JTextField(15);

    protected JEditorPane descriptionF = new JEditorPane();
    protected JEditorPane stepsF = new JEditorPane();
    protected DefaultTableModel attributeData = new AttributeTableModel(new Object[]{"Variable Name","Description", "Type","Required","Default Value"},0);

    protected JTable attributesT = new SortableJTable(new TableSorter(attributeData));
    protected static final int FIELD_WIDTH = 350;
    protected static final int DESC_HEIGHT = 65;
    protected static final int TABLE_HEIGHT = 80;

    public JameleonTagsPanel() {
    	super();
        setUpFunctionalPointFields();
        final JLabel tagNameL = new JLabel("Tag Name(s):");
        final JLabel typeL = new JLabel("Type:");
        final JLabel authorL = new JLabel("Author:");
        final JLabel applicationsL = new JLabel("Applications:");
        final JLabel descriptionL = new JLabel("Use:");
        final JLabel stepsL = new JLabel("Steps:");
        final JLabel attributesL = new JLabel("Atributes:");

        SpringLayout fpSpringLayout = new SpringLayout();
        JScrollPane descScrollPane = new JScrollPane(descriptionF);
        JScrollPane stepsScrollPane = new JScrollPane(stepsF);
        JScrollPane attsScrollPane = new JScrollPane(attributesT);

        setLayout(fpSpringLayout);
        add(tagNameL);
        tagNameL.setLabelFor(tagNameF);
        tagNameF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        add(tagNameF);
        add(typeL);
        typeF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        typeL.setLabelFor(typeF);
        add(typeF);
        add(authorL);
        authorF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        authorL.setLabelFor(authorF);
        add(authorF);
        add(applicationsL);
        applicationsF.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
        applicationsL.setLabelFor(applicationsF);
        add(applicationsF);
        add(descriptionL);
        descScrollPane.setMinimumSize(new Dimension(FIELD_WIDTH, DESC_HEIGHT));
        descScrollPane.setPreferredSize(new Dimension(FIELD_WIDTH, DESC_HEIGHT));
        descriptionL.setLabelFor(descScrollPane);
        add(descScrollPane);
        add(stepsL);
        stepsL.setLabelFor(stepsScrollPane);
        stepsScrollPane.setMinimumSize(new Dimension(FIELD_WIDTH, DESC_HEIGHT));
        stepsScrollPane.setPreferredSize(new Dimension(FIELD_WIDTH, DESC_HEIGHT));
        add(stepsScrollPane);
        add(attributesL);
        attributesT.getColumn("Required").setMaxWidth(55);
        attributesL.setLabelFor(attsScrollPane);
        //attsScrollPane.setMinimumSize(new Dimension(FIELD_WIDTH, TABLE_HEIGHT));
        //attsScrollPane.setPreferredSize(new Dimension(FIELD_WIDTH, TABLE_HEIGHT));
        add(attsScrollPane);
    }

    protected void setUpFunctionalPointFields(){
        descriptionF.setEditable(false);
        descriptionF.setEditorKit(new HTMLEditorKit());
        stepsF.setEditable(false);
        stepsF.setEditorKit(new HTMLEditorKit());
        tagNameF.setEditable(false);
        tagNameF.setBackground(Color.white);
        tagNameF.setBorder(descriptionF.getBorder());
        applicationsF.setEditable(false);
        applicationsF.setBackground(Color.white);
        applicationsF.setBorder(descriptionF.getBorder());
        typeF.setEditable(false);
        typeF.setBackground(Color.white);
        typeF.setBorder(descriptionF.getBorder());
        authorF.setEditable(false);
        authorF.setBackground(Color.white);
        authorF.setBorder(descriptionF.getBorder());
    }

    public void sendFunctionalPointInfoToUI(FunctionalPoint fp){
        StringBuffer tagNames = new StringBuffer();
        List tagNamesList = fp.getTagNames();
        Iterator it = tagNamesList.iterator();
        String tag;
        while (it.hasNext()) {
            tag = (String)it.next();
            tagNames.append("<").append(tag).append("/> ");
        }
        tagNameF.setText(tagNames.toString());
        typeF.setText(fp.getType());
        authorF.setText(fp.getAuthor());
        descriptionF.setText("<font size='-1'>"+fp.getDescription()+"</font>");
        descriptionF.setCaretPosition(0);
        String apps = "";
        for (it = fp.getApplications().iterator(); it.hasNext();) {
            apps += it.next();
            if (it.hasNext()) {
                apps += ", ";
            }
        }
        applicationsF.setText(apps);
        //Remove previous rows.
        populateEditorPane(stepsF, fp.getSteps());
        while (attributeData.getRowCount() > 0) {
            attributeData.removeRow(0);
        }
        it = fp.getAttributes().keySet().iterator();
        Attribute att = null;
        Object[] attRow = new Object[5];
        String defaultValue = "";
        while (it.hasNext()) {
            att = (Attribute)fp.getAttributes().get(it.next());
            attRow[0] = att.getName();
            attRow[1] = att.getDescription();
            attRow[2] = att.getType();
            attRow[3] = new Boolean(att.isRequired());
            if (att.getDefaultValue() != null) {
                defaultValue = att.getDefaultValue();
            }else{
                defaultValue = "";
            }
            attRow[4] = defaultValue;
            attributeData.addRow(attRow);
        }
    }

    protected void populateEditorPane(JEditorPane field, List lines){
        field.setText("");
        Iterator it = lines.iterator();
        StringBuffer linesS = new StringBuffer("<font size='-1'>");
        String line;
        while (it.hasNext()) {
            line = (String)it.next();
            linesS.append(line).append("<br>");
        }
        linesS.append("</font>");
        field.setText(linesS.toString());
        field.setCaretPosition(0);
    }

    protected class AttributeTableModel extends DefaultTableModel{
 		private static final long serialVersionUID = 1L;
		final int REQUIRED = 3;
        public AttributeTableModel(Object[] data, int rows){
            super(data, rows);
        }
        /**
         *  Returns <code>Object.class</code> regardless of <code>columnIndex</code>.
         *
         *  @param columnIndex  the column being queried
         *  @return the Object.class
         */
        public Class getColumnClass(int columnIndex) {
            Class dataType = super.getColumnClass(columnIndex);
            if (columnIndex == REQUIRED) {
                dataType = Boolean.class;
            }
            return dataType;
        }
    }

}
