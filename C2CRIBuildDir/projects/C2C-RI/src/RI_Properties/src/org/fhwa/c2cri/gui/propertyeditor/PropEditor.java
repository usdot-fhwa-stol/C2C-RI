package org.fhwa.c2cri.gui.propertyeditor;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;


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



/**
 * The Class PropEditor.
 *
 * @author open source community
 * Last Updated:  1/8/2014
 */
public class PropEditor  {
    
    /** The e file. */
    public static EditorFile eFile;
    
    /**
     * Edits the file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param f the f
     */
    public static void editFile(Frame f) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogTitle("Choose a property file...");
        
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        	openFile(f, fc.getSelectedFile());
        }
        else {
        	//System.exit(0);
        } 
    }
    
    /**
     * Open file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param f the f
     * @param selectedFile the selected file
     */
    public static void openFile(Frame f, File selectedFile){
		eFile = new EditorFile(selectedFile.getPath());
            
		eFile.init();
            
		int n = Parser.parsePropertyFile(eFile);
            
		if(n > 0){
			PropertyEditorDialog ed = new PropertyEditorDialog(f, eFile);
			ed.setVisible(true);
		}
		else {
			JOptionPane.showMessageDialog(null,
			"Could not parse property file:\n" + selectedFile.getAbsolutePath() + 
			".\nPlease contact IPSP Support.",
			"Error in Property File",
			JOptionPane.ERROR_MESSAGE);
		}
    }
    
    /**
     * Close file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @return the int
     */
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
    
    /**
     * Save file.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     */
    public static void saveFile() {
        if(eFile.exists() && eFile.isModified()){
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(eFile)))
			{
				String newLine = System.getProperty("line.separator");
                System.out.println(eFile.numGroup());
                for (int i=0; i < eFile.numGroup(); i++) {
                    bw.write(eFile.groupAt(i).toText() + newLine);
                }                
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
    
    /**
     * The main method.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
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
				
				editFile(null);
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