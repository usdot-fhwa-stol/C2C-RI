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

import net.sf.jameleon.util.Configurator;

import javax.swing.*;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public abstract class AbstractConfigPanel extends JPanel{

    protected List fieldProperties;

    public AbstractConfigPanel(){
        super(new SpringLayout());
        fieldProperties = new LinkedList();
        init();
    }

    private void init(){
        registerFields();
        SpringUtilities.makeCompactGrid(this,
                                        fieldProperties.size(), 2,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 6);  //xPad, yPad
    }

    public abstract void registerFields();

    protected void addFieldProperty(boolean isTextField, String label, String configName, String defaultValue, String toolTip){
        fieldProperties.add(new FieldProperty(isTextField, label, configName, toolTip, defaultValue, this));
    }

    protected void updateProperties(){
        Iterator it = fieldProperties.iterator();
        FieldProperty fp;
        while (it.hasNext()) {
            fp = (FieldProperty)it.next();
            fp.updateProperty();
        }
    }

    private class FieldProperty{
        private JCheckBox checkbox;
        private JTextField textField;
        private String configName;
        private String defaultValue;

        protected FieldProperty(boolean isTextField, String labelName, String configName, String toolTip, String defaultValue, JPanel panel){
            this.configName = configName;
            this.defaultValue = defaultValue;
            if (isTextField) {
                textField = (JTextField)createNewField(isTextField, labelName, toolTip, panel);
                setTextFieldValue(textField, configName);
            }else{
                checkbox = (JCheckBox)createNewField(isTextField, labelName, toolTip, panel);
                setCheckBoxValue(checkbox, configName);
            }
        }

        protected void updateProperty(){
            if (textField != null && textField.getText() != null && 
                textField.getText().length() > 0 && !defaultValue.equalsIgnoreCase(textField.getText())) {
                Configurator.getInstance().setValue(configName, textField.getText());
            }else if (checkbox != null && checkbox.isSelected() != Boolean.valueOf(defaultValue).booleanValue()) {
                String value = Boolean.toString(checkbox.isSelected());
                Configurator.getInstance().setValue(configName, value);
            }else{
                Configurator.getInstance().setValue(configName, null);
            }
        }

        private JComponent createNewField(boolean isTextField, String label, String toolTip, JPanel panel){
            JLabel jl = new JLabel(label);
            panel.add(jl);
            JComponent field;
            if (isTextField){
                JTextField textField = new JTextField();
                textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 5));
                field = textField;
            }else{
                field = new JCheckBox();
            }
            jl.setLabelFor(field);
            field.setToolTipText("<html>"+toolTip+"</html>");
            panel.add(field);
            return field;
        }

        private void setTextFieldValue(JTextField field, String key){
            field.setText(Configurator.getInstance().getValue(key, defaultValue));
        }

        private void setCheckBoxValue(JCheckBox field, String key){
            String value = Configurator.getInstance().getValue(key, defaultValue);
            field.setSelected(Boolean.valueOf(value).booleanValue());
        }

    }

}
