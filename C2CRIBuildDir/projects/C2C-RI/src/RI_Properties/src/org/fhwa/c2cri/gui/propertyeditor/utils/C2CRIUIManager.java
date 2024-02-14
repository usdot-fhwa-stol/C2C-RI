/*
 * Created on May 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.fhwa.c2cri.gui.propertyeditor.utils;

import java.awt.Color;
import java.awt.Dimension;

/**
 * @author Raoul_Jetley
 *
 * To change the template for this generated type comment go to
 * Window-Preferences-Java-Code Generation-Code and Comments
 */
public class C2CRIUIManager extends javax.swing.UIManager {
	public static void setUIComponents() {
		javax.swing.UIManager.put("Container.background", Color.WHITE);
		javax.swing.UIManager.put("Container.foreground", Color.BLACK);
		javax.swing.UIManager.put("Component.background", Color.WHITE);
		javax.swing.UIManager.put("Component.foreground", Color.BLACK);
		javax.swing.UIManager.put("Panel.background", Color.WHITE);
		javax.swing.UIManager.put("Panel.foreground", Color.BLACK);
		javax.swing.UIManager.put("Button.background", Color.LIGHT_GRAY);
		javax.swing.UIManager.put("Button.foreground", Color.BLACK);
		javax.swing.UIManager.put("Table.background", Color.WHITE);
		javax.swing.UIManager.put("Table.foreground", Color.BLACK);
		javax.swing.UIManager.put("SplitPane.background", Color.WHITE);
		javax.swing.UIManager.put("SplitPane.foreground", Color.BLACK);
		javax.swing.UIManager.put("SplitPane.dividerSize", 5);
		javax.swing.UIManager.put("Viewport.background", Color.WHITE);
		javax.swing.UIManager.put("Viewport.foreground", Color.BLACK);
		javax.swing.UIManager.put("ProgressBar.background", Color.WHITE);
		javax.swing.UIManager.put("ProgressBar.foreground", Color.BLACK);
		javax.swing.UIManager.put("CheckBoxMenuItem.background", Color.WHITE);
		javax.swing.UIManager.put("CheckBoxMenuItem.foreground", Color.BLACK);
		javax.swing.UIManager.put("CheckBox.background", Color.WHITE);
		javax.swing.UIManager.put("CheckBox.foreground", Color.BLACK);
		javax.swing.UIManager.put("ScrollBar.background", Color.WHITE);
		javax.swing.UIManager.put("ScrollBar.foreground", Color.BLACK);
		javax.swing.UIManager.put("RadioButtonMenuItem.background", Color.WHITE);
		javax.swing.UIManager.put("RadioButtonMenuItem.foreground", Color.BLACK);
		javax.swing.UIManager.put("FileChooser.background", Color.WHITE);
		javax.swing.UIManager.put("FileChooser.foreground", Color.BLACK);
		javax.swing.UIManager.put("ScrollPane.background", Color.WHITE);
		javax.swing.UIManager.put("ScrollPane.foreground", Color.BLACK);
		javax.swing.UIManager.put("TableHeader.background", Color.LIGHT_GRAY);
		javax.swing.UIManager.put("TableHeader.foreground", Color.BLACK);
		javax.swing.UIManager.put("Slider.background", Color.WHITE);
		javax.swing.UIManager.put("Slider.foreground", Color.BLACK);
		javax.swing.UIManager.put("TabbedPane.background", Color.WHITE);
		javax.swing.UIManager.put("TabbedPane.foreground", Color.BLACK);
		javax.swing.UIManager.put("TabbedPane.selected", Color.LIGHT_GRAY);
		javax.swing.UIManager.put("InternalFrame.background", Color.WHITE);
		javax.swing.UIManager.put("InternalFrame.foreground", Color.BLACK);
		javax.swing.UIManager.put("ComboBox.background", Color.WHITE);
		javax.swing.UIManager.put("ComboBox.foreground", Color.BLACK);
	}
	
	public static Dimension getUserDimension () {
		return new Dimension (800, 600);
	}
}
