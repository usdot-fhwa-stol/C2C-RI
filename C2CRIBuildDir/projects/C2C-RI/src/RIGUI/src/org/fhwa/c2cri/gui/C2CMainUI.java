
/*
 * File C2CMainUI.java
 *
 * Created on Jan 24, 2010, 9:05:23 PM
 */
package org.fhwa.c2cri.gui;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.fhwa.c2cri.gui.wizard.C2CRIWizardPanel;
import org.fhwa.c2cri.logger.TestLogList;
import org.fhwa.c2cri.testmodel.TestConfigurationList;
import org.fhwa.c2cri.testmodel.TestSuites;
import org.fhwa.c2cri.utilities.ProgressMonitor;

/**
 * Provides the main user interface view for the C2C RI application.  Its main method
 * acts is the main method for the C2CRI application.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 */
public class C2CMainUI extends javax.swing.JFrame {

    /** 
     * Creates new JFrame C2CMainUI, then initiates the loading of the available Test Suites.
     * Test suites represent the standards and associated test cases/procedures necessary to verify their conformance.
     * Finally, pass this C2CMainUI to a new instance of the RIUICoordinator, which then acts as a controller for all components
     * that are attached to this frame.
     */
    public C2CMainUI() {

        //Check whether a splash screen object was created
        final SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash == null) {
            System.out.println("SplashScreen.getSplashScreen() returned null");
        }

        try {

            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            System.out.println(" The current look and feel is " + javax.swing.UIManager.getLookAndFeel());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }


        System.out.println("\n\n\n");
        try (ScanResult scanResult =                // Assign scanResult in try-with-resources
                new ClassGraph()                    // Create a new ClassGraph instance
//            .verbose()                      // If you want to enable logging to stderr
            .enableAllInfo()                // Scan classes, methods, fields, annotations
            .whitelistPackages("org.fhwa.c2cri.applayer")   // Scan com.xyz and subpackages
            .scan()) {                      // Perform the scan and return a ScanResult
            // Use the ScanResult within the try block, e.g.
           try (FileWriter writer = new FileWriter("ClassFile.txt", true))
		   {
			for (ClassInfo widgetClassInfo : scanResult.getAllClasses()){
				 System.out.println(widgetClassInfo.getClasspathElementFile().getPath()+": "+widgetClassInfo.getName());
				 writer.write(widgetClassInfo.getClasspathElementFile().getPath()+": "+widgetClassInfo.getName()+"\n");
			 }
		   }
            // ...
        } catch (IOException ioex){
            ioex.printStackTrace();
        }
        System.out.println("\n\n\n");
        
        initComponents();
        ProgressMonitor monitor = ProgressMonitor.getInstance(
                this,
                "C2C RI",
                "Loading the Image Icon",
                ProgressMonitor.Options.MODAL,
                ProgressMonitor.Options.CENTER,
                ProgressMonitor.Options.SHOW_STATUS,
                ProgressMonitor.Options.SHOW_PERCENT_COMPLETE).show();

//        BasicGUIActionWrapper setImageIconAction = new BasicGUIActionWrapper(this, "Loading the Image Icon Action") {

  //          @Override
  //          protected Boolean actionMethod() throws Exception {

                BufferedImage image = null;


                try {

                    image = ImageIO.read(getClass().getResource("C2CRIImage.ico"));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                setIconImage(image);
//                return true;
//            }
//        };
//        setImageIconAction.execute();
          monitor.dispose();
        try {
            URL url = C2CMainUI.class.getResource(
                    //                                                            "New Picture(2).png");
                    "C2C-RI.htm");
            //                                       new URL("http://www.apl.jhu.edu/~hall/");
            if (url != null) {
                c2criMainPanel.setVisible(true);
                javax.swing.JEditorPane tempPane = new javax.swing.JEditorPane();
                editorScrollPane.getViewport().add(tempPane);
//            jEditorPane1.setPage(url);
                editorScrollPane.setVisible(true);
//            jEditorPane1.setVisible(true);
                tempPane.setPage(url);
                tempPane.setEditable(false);
//            jEditorPane1.setEditable(false);
            } else {
                System.out.println("Null URL");
            }

        } catch (MalformedURLException ex) {
            System.out.println("Error setting URL -- MalFormed ");
            Logger.getLogger(C2CMainUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            System.out.println("Other Error -- " + e.getMessage());
        }

        /**
         *
        try {

        javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
        System.out.println(" The current look and feel is " + javax.swing.UIManager.getLookAndFeel());
        } catch (Exception e) {
        System.out.println("Error setting native LAF: " + e);
        }
         */
//        BasicGUIActionWrapper updateTestSuitesAction = new BasicGUIActionWrapper(this, "Loading the Test Suites Action") {
//
//            @Override
//            protected Boolean actionMethod() throws Exception {
        BasicGUIActionWrapper initRIAction = new BasicGUIActionWrapper(this, "Initializing the C2C RI") {
            ConfigFileTableModel model;

            @Override
            protected Boolean actionMethod() throws Exception {
                TestSuites.getInstance();  // Go ahead and load the testSuites
                new RIUICoordinator(C2CMainUI.this);
                TestConfigurationList.getInstance(); // Go ahead and preload the config files               
                TestLogList.getInstance(); // Go ahead and preload the log files
                return true;
            }

            @Override
            protected void wrapUp(Boolean result) {
                if (!result) {
                    javax.swing.JOptionPane.showMessageDialog(null, "An error was encountered trying to complete the Initialize TestSelection UI action.");
                }
            }
        };
        initRIAction.execute();

                
//                return true;
//            }
//        };
//        updateTestSuitesAction.execute();
//        if (splash != null) splash.close();

    }

    /** This method is called from within the constructor to
     * initialize the C2CMainUI components.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        createNewConfigToolbarButton = new javax.swing.JButton();
        openExistingToolbarButton = new javax.swing.JButton();
        executeTestToolbarButton = new javax.swing.JButton();
        reportsToolbarButton = new javax.swing.JButton();
        riOptionsToolbarButton = new javax.swing.JButton();
        helpAboutToolbarButton = new javax.swing.JButton();
        exitAppToolbarButton = new javax.swing.JButton();
        c2criMainPanel = new javax.swing.JPanel();
        editorScrollPane = new javax.swing.JScrollPane();
        jEditorPane1 = new javax.swing.JEditorPane();
        mainMenuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        executeMenuItem = new javax.swing.JMenuItem();
        reportsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        maintenanceMenuItem = new javax.swing.JMenuItem();
        optionsMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("C2C RI");
        setBackground(new java.awt.Color(255, 255, 255));
        setBounds(new java.awt.Rectangle(0, 0, 800, 600));
        setMinimumSize(new java.awt.Dimension(800, 550));

        jToolBar1.setBorder(null);
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setBorderPainted(false);

        createNewConfigToolbarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/fhwa/c2cri/gui/import_project.gif"))); // NOI18N
        createNewConfigToolbarButton.setToolTipText("Create a Test Configuration File");
        createNewConfigToolbarButton.setFocusable(false);
        createNewConfigToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        createNewConfigToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(createNewConfigToolbarButton);
        createNewConfigToolbarButton.getAccessibleContext().setAccessibleName("New Config File Buton");

        openExistingToolbarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/fhwa/c2cri/gui/project.gif"))); // NOI18N
        openExistingToolbarButton.setToolTipText("Open an existing Test Configuration File");
        openExistingToolbarButton.setFocusable(false);
        openExistingToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        openExistingToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(openExistingToolbarButton);
        openExistingToolbarButton.getAccessibleContext().setAccessibleName("Open an existing Configuration File");

        executeTestToolbarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/fhwa/c2cri/gui/running.gif"))); // NOI18N
        executeTestToolbarButton.setToolTipText("Run a Test");
        executeTestToolbarButton.setFocusable(false);
        executeTestToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        executeTestToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(executeTestToolbarButton);

        reportsToolbarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/fhwa/c2cri/gui/rireport.gif"))); // NOI18N
        reportsToolbarButton.setToolTipText("Create RI Configuration and Log Reports");
        reportsToolbarButton.setFocusable(false);
        reportsToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        reportsToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(reportsToolbarButton);
        reportsToolbarButton.getAccessibleContext().setAccessibleName("Reports Button");

        riOptionsToolbarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/fhwa/c2cri/gui/options.png"))); // NOI18N
        riOptionsToolbarButton.setToolTipText("Set application parameters");
        riOptionsToolbarButton.setFocusable(false);
        riOptionsToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        riOptionsToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(riOptionsToolbarButton);
        riOptionsToolbarButton.getAccessibleContext().setAccessibleName("Options Button");

        helpAboutToolbarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/fhwa/c2cri/gui/online_help.gif"))); // NOI18N
        helpAboutToolbarButton.setToolTipText("Get application information");
        helpAboutToolbarButton.setFocusable(false);
        helpAboutToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        helpAboutToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(helpAboutToolbarButton);
        helpAboutToolbarButton.getAccessibleContext().setAccessibleName("Help Button");

        exitAppToolbarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/fhwa/c2cri/gui/system-log-out.png"))); // NOI18N
        exitAppToolbarButton.setToolTipText("Exit the RI Application");
        exitAppToolbarButton.setFocusable(false);
        exitAppToolbarButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitAppToolbarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exitAppToolbarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitAppToolbarButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(exitAppToolbarButton);
        exitAppToolbarButton.getAccessibleContext().setAccessibleName("Exit Button");

        c2criMainPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        c2criMainPanel.setMinimumSize(new java.awt.Dimension(650, 485));
        c2criMainPanel.setPreferredSize(new java.awt.Dimension(650, 500));
        c2criMainPanel.setLayout(new java.awt.CardLayout());

        editorScrollPane.setName("editorScrollPane"); // NOI18N
        editorScrollPane.setPreferredSize(new java.awt.Dimension(650, 500));

        jEditorPane1.setEditable(false);
        jEditorPane1.setText("...");
        jEditorPane1.setAutoscrolls(false);
        jEditorPane1.setFocusable(false);
        jEditorPane1.setMinimumSize(new java.awt.Dimension(650, 400));
        jEditorPane1.setPreferredSize(new java.awt.Dimension(650, 400));
        editorScrollPane.setViewportView(jEditorPane1);

        c2criMainPanel.add(editorScrollPane, "card2");

        mainMenuBar.setBackground(new java.awt.Color(255, 255, 255));
        mainMenuBar.setFocusCycleRoot(true);
        mainMenuBar.setFont(mainMenuBar.getFont().deriveFont(mainMenuBar.getFont().getSize()+3f));

        fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
        fileMenu.setText("File");
        fileMenu.setToolTipText("File Menu");
        fileMenu.setFocusCycleRoot(true);
        fileMenu.setFocusTraversalPolicyProvider(true);
        fileMenu.setFont(fileMenu.getFont());
        fileMenu.setNextFocusableComponent(newMenuItem);

        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.ALT_MASK));
        newMenuItem.setFont(newMenuItem.getFont());
        newMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_N);
        newMenuItem.setText("New");
        newMenuItem.setToolTipText("Create a Test Configuration File");
        newMenuItem.setFocusTraversalPolicyProvider(true);
        newMenuItem.setFocusable(true);
        fileMenu.add(newMenuItem);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.ALT_MASK));
        openMenuItem.setFont(openMenuItem.getFont());
        openMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_O);
        openMenuItem.setText("Open");
        openMenuItem.setToolTipText("Open an existing Test Configuration File");
        openMenuItem.setFocusTraversalPolicyProvider(true);
        openMenuItem.setFocusable(true);
        fileMenu.add(openMenuItem);

        executeMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.ALT_MASK));
        executeMenuItem.setFont(executeMenuItem.getFont());
        executeMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_X);
        executeMenuItem.setText("Execute");
        executeMenuItem.setToolTipText("Run a test");
        fileMenu.add(executeMenuItem);
        executeMenuItem.getAccessibleContext().setAccessibleDescription("Execute a test");

        reportsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.ALT_MASK));
        reportsMenuItem.setFont(reportsMenuItem.getFont());
        reportsMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_R);
        reportsMenuItem.setText("Reports");
        reportsMenuItem.setToolTipText("Create RI Configuration and Log Reports");
        fileMenu.add(reportsMenuItem);
        reportsMenuItem.getAccessibleContext().setAccessibleDescription("Create RI Configuration and Log Reports");

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setFont(exitMenuItem.getFont());
        exitMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_E);
        exitMenuItem.setText("Exit");
        exitMenuItem.setToolTipText("Exit the RI Application");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);
        exitMenuItem.getAccessibleContext().setAccessibleDescription("Exit the RI Application");

        mainMenuBar.add(fileMenu);
        fileMenu.getAccessibleContext().setAccessibleDescription("File Menu Item");

        toolsMenu.setMnemonic(java.awt.event.KeyEvent.VK_T);
        toolsMenu.setText("Tools");
        toolsMenu.setToolTipText("Tools Menu");
        toolsMenu.setFocusCycleRoot(true);
        toolsMenu.setFocusTraversalPolicyProvider(true);
        toolsMenu.setFont(toolsMenu.getFont());

        maintenanceMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.ALT_MASK));
        maintenanceMenuItem.setFont(maintenanceMenuItem.getFont());
        maintenanceMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_M);
        maintenanceMenuItem.setText("Maintenance");
        maintenanceMenuItem.setToolTipText("Perform file maintenance");
        toolsMenu.add(maintenanceMenuItem);

        optionsMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.ALT_MASK));
        optionsMenuItem.setFont(optionsMenuItem.getFont());
        optionsMenuItem.setMnemonic(java.awt.event.KeyEvent.VK_P);
        optionsMenuItem.setText("Options");
        optionsMenuItem.setToolTipText("Set application parameters");
        toolsMenu.add(optionsMenuItem);

        mainMenuBar.add(toolsMenu);

        helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
        helpMenu.setText("Help");
        helpMenu.setToolTipText("Help Menu");
        helpMenu.setFocusCycleRoot(true);
        helpMenu.setFocusTraversalPolicyProvider(true);
        helpMenu.setFont(helpMenu.getFont());

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        aboutMenuItem.setText("About");
        aboutMenuItem.setToolTipText("Get application information");
        helpMenu.add(aboutMenuItem);

        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(c2criMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 1023, Short.MAX_VALUE)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 1023, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(c2criMainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Callback method for when a user selects file exit.
     *
     * It calls the JFrame.dispose() method and then calls System.exit method.
     *
     * @param evt - the event that triggered this callback
     */
        private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
            System.out.println(" The current look and feel is " + javax.swing.UIManager.getLookAndFeel());

            dispose();
            System.exit(0);
        }//GEN-LAST:event_exitMenuItemActionPerformed

        /**
         * Exit app toolbar button action performed.
         * 
         * Pre-Conditions: N/A
         * Post-Conditions: N/A
         *
         * @param evt the evt
         */
        private void exitAppToolbarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitAppToolbarButtonActionPerformed
            dispose();
            System.exit(0);
        }//GEN-LAST:event_exitAppToolbarButtonActionPerformed

    /**
     * This is the main method for the RI application.
     *
     * It creates an instance of C2CMainUI Class and displays it by invoking the C2CMainUI setVisible
     * method as part of the java.awt.Eventqueue dispatch thread.
     *
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new C2CMainUI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JMenuItem aboutMenuItem;
    protected javax.swing.JPanel c2criMainPanel;
    protected javax.swing.JButton createNewConfigToolbarButton;
    protected static javax.swing.JScrollPane editorScrollPane;
    protected javax.swing.JMenuItem executeMenuItem;
    protected javax.swing.JButton executeTestToolbarButton;
    protected javax.swing.JButton exitAppToolbarButton;
    protected javax.swing.JMenuItem exitMenuItem;
    protected javax.swing.JMenu fileMenu;
    protected javax.swing.JButton helpAboutToolbarButton;
    protected javax.swing.JMenu helpMenu;
    protected javax.swing.JEditorPane jEditorPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JMenuBar mainMenuBar;
    protected javax.swing.JMenuItem maintenanceMenuItem;
    protected javax.swing.JMenuItem newMenuItem;
    protected javax.swing.JButton openExistingToolbarButton;
    protected javax.swing.JMenuItem openMenuItem;
    protected javax.swing.JMenuItem optionsMenuItem;
    protected javax.swing.JMenuItem reportsMenuItem;
    protected javax.swing.JButton reportsToolbarButton;
    protected javax.swing.JButton riOptionsToolbarButton;
    protected javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables
}
