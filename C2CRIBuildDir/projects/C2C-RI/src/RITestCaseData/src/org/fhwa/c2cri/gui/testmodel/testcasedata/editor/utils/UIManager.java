/*
 * Created on May 5, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.fhwa.c2cri.gui.testmodel.testcasedata.editor.utils;

import java.awt.Color;
import java.awt.Dimension;

/**
 * @author Raoul_Jetley
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UIManager extends javax.swing.UIManager {
	public static void setUIComponents() {
		UIManager.put("Container.background", Color.WHITE);
		UIManager.put("Container.foreground", Color.BLACK);
		UIManager.put("Component.background", Color.WHITE);
		UIManager.put("Component.foreground", Color.BLACK);
		UIManager.put("Panel.background", Color.WHITE);
		UIManager.put("Panel.foreground", Color.BLACK);
//		UIManager.put("Button.background", Color.WHITE);
		UIManager.put("Button.background", Color.LIGHT_GRAY);
		UIManager.put("Button.foreground", Color.BLACK);
		UIManager.put("Table.background", Color.WHITE);
		UIManager.put("Table.foreground", Color.BLACK);
		UIManager.put("SplitPane.background", Color.WHITE);
		UIManager.put("SplitPane.foreground", Color.BLACK);
		UIManager.put("SplitPane.dividerSize", new Integer(5));
		UIManager.put("Viewport.background", Color.WHITE);
		UIManager.put("Viewport.foreground", Color.BLACK);
		UIManager.put("ProgressBar.background", Color.WHITE);
		UIManager.put("ProgressBar.foreground", Color.BLACK);
		UIManager.put("CheckBoxMenuItem.background", Color.WHITE);
		UIManager.put("CheckBoxMenuItem.foreground", Color.BLACK);
		UIManager.put("CheckBox.background", Color.WHITE);
		UIManager.put("CheckBox.foreground", Color.BLACK);
		UIManager.put("ScrollBar.background", Color.WHITE);
		UIManager.put("ScrollBar.foreground", Color.BLACK);
		UIManager.put("RadioButtonMenuItem.background", Color.WHITE);
		UIManager.put("RadioButtonMenuItem.foreground", Color.BLACK);
		UIManager.put("FileChooser.background", Color.WHITE);
		UIManager.put("FileChooser.foreground", Color.BLACK);
		UIManager.put("ScrollPane.background", Color.WHITE);
		UIManager.put("ScrollPane.foreground", Color.BLACK);
//		UIManager.put("TableHeader.background", Color.WHITE);
		UIManager.put("TableHeader.background", Color.LIGHT_GRAY);
		UIManager.put("TableHeader.foreground", Color.BLACK);
		UIManager.put("Slider.background", Color.WHITE);
		UIManager.put("Slider.foreground", Color.BLACK);
//		UIManager.put("TabbedPane.background", Color.WHITE);
		UIManager.put("TabbedPane.background", Color.WHITE);
		UIManager.put("TabbedPane.foreground", Color.BLACK);
		UIManager.put("TabbedPane.selected", Color.LIGHT_GRAY);
		UIManager.put("InternalFrame.background", Color.WHITE);
		UIManager.put("InternalFrame.foreground", Color.BLACK);
		UIManager.put("ComboBox.background", Color.WHITE);
		UIManager.put("ComboBox.foreground", Color.BLACK);
	}
	
	public static Dimension getUserDimension () {
//		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//		return new Dimension (Math.max(screenSize.width - 50, 800), Math.max(screenSize.height-150, 600));
		return new Dimension (800, 600);
	}
}
