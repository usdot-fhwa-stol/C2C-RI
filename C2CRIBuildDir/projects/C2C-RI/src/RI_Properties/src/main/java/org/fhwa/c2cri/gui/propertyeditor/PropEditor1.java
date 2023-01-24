package org.fhwa.c2cri.gui.propertyeditor;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.UIManager;



public class PropEditor1  {
    
    /** The e file. */
    public static EditorFile eFile;
    
    
    public static void openFile(){
        org.fhwa.c2cri.utilities.RIParameters.getInstance().configure();
        eFile = org.fhwa.c2cri.utilities.RIParameters.getInstance().eFile;
//        PropertyEditorDialog ed = new PropertyEditorDialog(null, eFile);
//	ed.setVisible(true);
    }

    public static PropertyEditorDialog getDialog(){
        PropertyEditorDialog ed = new PropertyEditorDialog(null, eFile);
        return ed;
    }

    public static int closeFile(){
/*    	
        if(eFile.isModified()) {
            Object[] options = {"Save", "Don't Save", "Cancel"};
            
            int n = JOptionPane.showOptionDialog(null,
            "Are you sure you want to close the file without saving?", "Warning",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
            null, options, options[0]
            );
            
            return n;
        }
*/        
        return 1;
    }
    
    public static void saveFile() {
        if(eFile.exists() && eFile.isModified()){
            try{
				String newLine = System.getProperty("line.separator");
                BufferedWriter bw = new BufferedWriter(new FileWriter(eFile));
                System.out.println(eFile.numGroup());
                for (int i=0; i < eFile.numGroup(); i++) {
                    bw.write(eFile.groupAt(i).toText() + newLine);
                }
                bw.close();
                
                eFile.setModified(false);
            } 
            catch(Exception e) {
            	//System.out.println("Error when saving");
				JOptionPane.showMessageDialog(null,
				"Could not save properties:\n" + eFile.getAbsolutePath() + 
				".\nPlease contact IPSP Support.",
				"Error in Saving Properties",
				JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    public static void main(String args[]) throws Exception{
        
        
        UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName()); 
        
        //Create a Frame to demo the Propertyeditor component
        
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Property Editor Demo");

        
        frame.setSize(400,300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation((d.width-frame.getWidth())/2, (d.height-frame.getHeight())/2);
                
        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
		
		JMenu projectMenu = new JMenu("Property Editor");
		menuBar.add(projectMenu);
		
		JMenuItem loadProject = new JMenuItem("Load Property Editor");
		projectMenu.add(loadProject);
		loadProject.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				
				openFile();
			}
			}
        );
        
		JMenuItem exit = new JMenuItem("Exit");
		projectMenu.add(exit);
		exit.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
				
			}
			
		}
        );
        
        
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setVisible(true);

    }
    
}