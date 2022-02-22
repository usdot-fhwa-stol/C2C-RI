/*
    Jameleon - An automation testing tool..
    Copyright (C) 2003-2006 Christian W. Hargraves (engrean@hotmail.com)

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.sf.jameleon.TestCaseTagLibrary;
import net.sf.jameleon.bean.TestCase;
import net.sf.jameleon.util.Configurator;
import net.sf.jameleon.util.JameleonDefaultValues;

public class TestCaseTree extends JTree{

    protected Configurator config;
    protected TestCasePane testCasePane;
    private final int SECONDS_TO_WAIT = 3;

    public TestCaseTree(TestCasePane testCasePn){
        super();
        testCasePane = testCasePn;
        testCasePane.setTestCaseTree(this);

        FileNode rootFileNode = createRootFileNode();
        TCTreeNode rootNode = new TCTreeNode(rootFileNode, true);
        DefaultTreeModel tm = new DefaultTreeModel(rootNode);
        setModel(tm);
        generateScriptsTree(rootFileNode);
        TCTreeListener listener = new TCTreeListener();
        addTreeWillExpandListener(listener);
        addTreeSelectionListener(listener);
    }

    public TCTreeNode generateScriptsTree(){
        return generateScriptsTree(createRootFileNode());
    }

    public TCTreeNode generateScriptsTree(FileNode rootFileNode){
        TCTreeNode rootNode = (TCTreeNode)getModel().getRoot();
        rootNode.removeAllChildren();
        rootNode.setUserObject(rootFileNode);
        addTreeToDir(rootFileNode.getFile(), rootNode);
        updateUI();
        return rootNode;
    }

    protected FileNode createRootFileNode(){
        Configurator config = Configurator.getInstance();
        String scriptDir = config.getValue(JameleonDefaultValues.SCRIPT_DIR_CONFIG_NAME, 
                                           JameleonDefaultValues.SCRIPT_DIR.getPath());
        FileNode tcFn = new FileNode(new File(scriptDir), "Test Cases");
        return tcFn;
    }

    protected void addTreeToDir(File file, DefaultMutableTreeNode parentNode){
        if ( file.isDirectory() ) {
            File[] childFiles = file.listFiles();
            for (int i = 0; i < childFiles.length; i++) {
                addChildToTree(childFiles[i], parentNode);
            }
        }
    }

    protected void addChildToTree(File file, DefaultMutableTreeNode parent){
        FileNode fn = new FileNode(file, file.getName());
        TCTreeNode node = new TCTreeNode(fn, file.isDirectory());
        final int NON_EXISTENT = -1;
        if ( parent.getIndex(node) == NON_EXISTENT && 
             !"CVS".equals(file.getName()) &&
             !".svn".equalsIgnoreCase(file.getName()) ) {
             parent.add(node);
        }
    }

    protected void genTestCaseDocs(final FileNode fn){
        disableLogging();
        new ProgressDialog(testCasePane.getJameleonUI(), SECONDS_TO_WAIT, testCasePane, fn.getFile());
    }

    protected void setTestCaseSource(final File f, final boolean activateSourceTab){
        if (f.exists() && f.canRead()) {
            StringBuffer contents = new StringBuffer();
            BufferedReader in = null;
            try{
                in = new BufferedReader(new FileReader(f));
                while (in.ready()) {
                    contents.append(in.readLine()).append("\n");
                }
                testCasePane.setTestCaseSource(contents.toString());
                if (activateSourceTab) {
                    testCasePane.getTabbedPane().setSelectedIndex(TestCasePane.SOURCE_INDEX);
                }
            }catch(IOException ioe){
                ioe.printStackTrace();
            }finally{
                try{
                    if (in != null){
                        in.close();
                    }
                }catch(IOException ioe){
                    //So I couldn't close the connection?
                }
            }
        }
    }

    protected void disableLogging(){
//         LoggerRepository repos = LogManager.getLoggerRepository();
//         Logger root = repos.getRootLogger();
//         Appender stdout = root.getAppender("STDOUT");
//         stdout.addFilter(new DenyAllFilter());
        TestCaseTagLibrary.setWarnOnNoPluginsFound(false);
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //Inner Classes
    /////////////////////////////////////////////////////////////////////////////////////
    public class TCTreeListener implements TreeWillExpandListener, TreeSelectionListener{

        public void treeWillCollapse(TreeExpansionEvent event){
        }

        protected void addNodesToDir(TreePath path){
            Object obj = path.getLastPathComponent();
            if (obj instanceof TCTreeNode) {
                TCTreeNode tn = (TCTreeNode)obj;
                FileNode fn = (FileNode)tn.getUserObject();
                if (fn.isDir()) {
                    tn.removeAllChildren();
                    addTreeToDir(fn.getFile(), tn);
                    ((DefaultTreeModel)getModel()).reload(tn);
                }
                
            }
        }

        public void treeWillExpand(TreeExpansionEvent event){
            TreePath path = event.getPath();
            addNodesToDir(path);
        }

        public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getNewLeadSelectionPath();
            if (path != null) {
                Object obj = path.getLastPathComponent();

                if (obj instanceof TCTreeNode) {
                    TCTreeNode tn = (TCTreeNode)obj;
                    FileNode fn = (FileNode)tn.getUserObject();
                    if (!fn.isDir()) {
                        boolean isFrag = false;
                        if (fn.getFile().getName().endsWith("ml")) {
                            genTestCaseDocs(fn);
                        }else{
                            testCasePane.setTestCaseInfo(new TestCase());
                            isFrag = true;
                        }
                        setTestCaseSource(fn.getFile(), isFrag);
                    }
                }
            }
        }
    }

}
