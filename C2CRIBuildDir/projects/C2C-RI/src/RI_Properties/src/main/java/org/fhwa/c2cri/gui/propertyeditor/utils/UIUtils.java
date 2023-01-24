/*
 * Created on Jun 21, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.fhwa.c2cri.gui.propertyeditor.utils;

import java.awt.Point;
import java.awt.Toolkit;

/**
 * @author david_211245
 *
 * To change the template for this generated type comment go to
 * Window-Preferences-Java-Code Generation-Code and Comments
 */
public class UIUtils {
	
	public static Point getCenterScreenLocation(int width, int height){
		
		Toolkit tk = Toolkit.getDefaultToolkit();
		int scrWidth = (int) tk.getScreenSize().getWidth();
		int scrHeigth = (int) tk.getScreenSize().getHeight();


		int dx = (scrWidth - width) / 2;
		int dy = (scrHeigth - height) / 2;
		
		return new Point(dx, dy);
		
	}
	
}
