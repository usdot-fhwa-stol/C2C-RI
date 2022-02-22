/*
    Jameleon - An automation testing tool..
    Copyright (C) 2005-2006 Christian W. Hargraves (engrean@hotmail.com)

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

import net.sf.jameleon.LocationAwareTagSupport;
import net.sf.jameleon.TestCaseTag;
import net.sf.jameleon.data.DataDrivable;
import net.sf.jameleon.event.*;
import net.sf.jameleon.function.FunctionTag;
import net.sf.jameleon.result.DataDrivableRowResult;
import net.sf.jameleon.result.FunctionResult;
import net.sf.jameleon.result.JameleonTestResult;
import net.sf.jameleon.result.TestCaseResult;
import net.sf.jameleon.util.JameleonUtility;
import org.apache.commons.jelly.LocationAware;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

public class TestCaseResultsPane extends JSplitPane implements TestCaseListener,
                                                                FunctionListener,
                                                                DataDrivableListener{
	private static final long serialVersionUID = 1L;
    protected DefaultTableModel tcModel, tcReasonModel;
    protected JTable tcTable, tcReasonTable;
    protected Map testCases = new HashMap();
    private static final int TC_NUM = 0;
    private static final int STATUS = 1;
    private static final int TEST_CASE = 2;
    private static final int EXEC_TIME = 3;
    private static final int RUN = 4;
    private static final int FAIL = 5;
    private static final int PASS = 6;
    private static final int TC = 7;
    protected ImageIcon passedImg, failedImg, runningImg, snapShotImg;
    private List results = new ArrayList();
    private JTextArea stackTrace = new JTextArea();
    private TestCaseTree tcTree;
    private JTextArea sourceArea;
    private JFrame rootFrame;
    private boolean stepNextTag;
    private boolean debug;
    private boolean stopExecution;
    private BasicHtmlBrowser snapShotBrowser = new BasicHtmlBrowser("Snapshot of Error");

    public TestCaseResultsPane(JFrame rootFrame) {
        super(JSplitPane.VERTICAL_SPLIT);
        this.rootFrame = rootFrame;
        setResizeWeight(0.5);
        setDividerSize(4);
        initResultsTable();
        initIcons();
    }

    protected void setSourceArea(JTextArea sourceArea){
        this.sourceArea = sourceArea;
    }

    protected void setTestCaseTree(TestCaseTree tcTree){
        this.tcTree = tcTree;
    }

    private void initIcons() {
        passedImg = createImageIcon("/icons/passed.gif", "Test Passed");
        failedImg = createImageIcon("/icons/failed.gif", "Test Failed");
        runningImg = createImageIcon("/icons/running.gif", "Test Running");
        snapShotImg = createImageIcon("/icons/snapShot.gif", "View Snapshot of Application at Failure Time");
    }

    public void stopExecution(){
        this.stopExecution = true;
    }

    public void proceedExecution(){
        this.stopExecution = false;
    }

    public void setDebug(boolean debug){
        this.debug = debug;
    }

    protected void setStepNextTag(boolean stepNextTag){
        this.stepNextTag = stepNextTag;
    }
    
    private String convertObjectToToolTip(Object obj){
    	String tip = null;
    	if (obj != null){
            if (obj instanceof ImageIcon) { //Status column
                tip = ((ImageIcon)obj).getDescription();
            } else {
                tip = obj.toString();
            }
        }
        return tip;
   	
    }

    private void initResultsTable() {
        tcModel = new DefaultTableModel(new Object[]{"#","Status","Test Case","Execution Time"," Run","Fail","Pass","."},0) {
            public Class getColumnClass(int columnIndex) {
                Class dataType = super.getColumnClass(columnIndex);
                if (columnIndex == STATUS) {
                    dataType = Icon.class;
                }
                return dataType;
            }
        };
        tcTable = new SortableJTable(new TableSorter(tcModel)) {
            public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                return convertObjectToToolTip(getValueAt(rowIndex, colIndex));
            }

        };
        tcTable.setColumnSelectionAllowed(false);
        tcTable.getColumnModel().getColumn(TEST_CASE).setPreferredWidth(200);
        tcTable.getColumnModel().getColumn(STATUS).setPreferredWidth(40);
        tcTable.getColumnModel().getColumn(STATUS).setMaxWidth(40);
        tcTable.getColumnModel().getColumn(TC_NUM).setPreferredWidth(30);
        tcTable.getColumnModel().getColumn(TC_NUM).setMaxWidth(30);
        tcTable.getColumnModel().getColumn(RUN).setPreferredWidth(30);
        tcTable.getColumnModel().getColumn(RUN).setMaxWidth(30);
        tcTable.getColumnModel().getColumn(FAIL).setPreferredWidth(30);
        tcTable.getColumnModel().getColumn(FAIL).setMaxWidth(30);
        tcTable.getColumnModel().getColumn(PASS).setPreferredWidth(40);
        tcTable.getColumnModel().getColumn(PASS).setMaxWidth(40);
        tcTable.getColumnModel().getColumn(EXEC_TIME).setPreferredWidth(100);
        tcTable.getColumnModel().getColumn(EXEC_TIME).setMaxWidth(100);
        tcTable.getColumnModel().getColumn(TC).setPreferredWidth(0);
        tcTable.getColumnModel().getColumn(TC).setMaxWidth(0);
        tcTable.getSelectionModel().addListSelectionListener(new TCResultsSelectionListeners());
        tcTable.addMouseListener(new TCResultsMouseListener());


        tcReasonModel = new DefaultTableModel(new Object[]{"Row", "Line", " ", "Function Id", "Result","."},0) {
            public Class getColumnClass(int columnIndex) {
                Class dataType = super.getColumnClass(columnIndex);
                if (columnIndex == TCResultsSelectionListeners.URL) {
                    dataType = Icon.class;
                }
                return dataType;
            }
        };
        tcReasonTable = new SortableJTable(new TableSorter(tcReasonModel)){
            public String getToolTipText(MouseEvent e) {
                Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);
                return convertObjectToToolTip(getValueAt(rowIndex, colIndex));
            }
        };

        tcReasonTable.addMouseListener(new FTResultsMouseListener());

        tcReasonTable.getColumn("Row").setMaxWidth(40);
        tcReasonTable.getColumn("Line").setMaxWidth(40);
        tcReasonTable.getColumn(".").setMaxWidth(0);
        tcReasonTable.getColumn(" ").setMaxWidth(20);
        tcReasonTable.setColumnSelectionAllowed(false);
        tcReasonTable.getSelectionModel().addListSelectionListener(new FTResultsSelectionListeners());
        setTopComponent(new JScrollPane(tcTable));
        JPanel panel = new JPanel(new SpringLayout());
        panel.add(new JScrollPane(tcReasonTable));
        stackTrace.setEditable(false);
        stackTrace.setLineWrap(false);
        stackTrace.setRows(20);
        panel.add(new JScrollPane(stackTrace));
        SpringUtilities.makeCompactGrid(panel,
                                        2, 1,   //rows, cols
                                        6, 6,   //initX, initY
                                        6, 6);  //xPad, yPad
        setBottomComponent(panel);
    }

    public void resetTable() {
        while (tcModel.getRowCount() > 0) {
            tcModel.removeRow(0);
        }
        testCases.clear();
        results.clear();
        clearReasonTable();
    }

    protected void addTestCaseRow(TestCaseTag tct) {
        String tcFileName = getScriptName(tct);
        if ( ! testCases.containsKey(tcFileName) ) {
            Object[] cols = new Object[8];
            cols[TC_NUM] = new  Integer(results.size() + 1);
            cols[TEST_CASE] = tct.getName();
            cols[STATUS] = runningImg;
            cols[TC] = tct;
            tcModel.addRow(cols);
            Integer rowNum = new Integer(tcModel.getRowCount() - 1);
            testCases.put(tcFileName, rowNum);
        }
    }

    private String getScriptName(TestCaseTag tct) {
        String scriptName = tct.getTestCase().getFile();
        boolean notFound = true;
        String tmpScriptName = null;
        for (int i = 1; notFound; i++){
            tmpScriptName = scriptName + i;
            if (testCases.containsKey(tmpScriptName)){
                Integer rowNum = (Integer)testCases.get(tmpScriptName);
                ImageIcon statusImg = (ImageIcon)tcModel.getValueAt(rowNum.intValue(), STATUS);
                if (statusImg == runningImg){
                    notFound = false;
                }
            }else{
                notFound = false;
            }
        }
        return tmpScriptName;
    }

    protected void reportTcResults(TestCaseTag tct) {
        String tcFileName = getScriptName(tct);
        int rowNum = ((Integer)testCases.get(tcFileName)).intValue();
        TestCaseResult tcr = tct.getResults();
        ImageIcon statusImg;

        if (tcr.passed()) {
            statusImg = passedImg;
        } else {
            statusImg = failedImg;
        }
        tcModel.setValueAt(statusImg, rowNum, STATUS);

        String execTime = JameleonUtility.executionTimeToString(tcr.getExecutionTime());
        tcModel.setValueAt(execTime, rowNum, EXEC_TIME);
        int numRun = tcr.getCountableResults().size();
        int numFailed = tcr.getFailedCountableResults().size();
        tcModel.setValueAt(new Integer(numRun), rowNum, RUN);
        tcModel.setValueAt(new Integer(numFailed), rowNum, FAIL);

        String percentagePassed = "N/A";
        try {
            NumberFormat nf = NumberFormat.getPercentInstance();
            int numPassed = numRun - numFailed;
            if (numRun > 0) {
                percentagePassed = nf.format((double) numPassed/numRun);
            } else {
                percentagePassed = nf.format(0);
            }
        } catch (ArithmeticException ae) {
            System.err.println("Could not divide by 0 " + ae.getMessage());

        }

        tcModel.setValueAt(percentagePassed, rowNum, PASS);
        results.add(tcr);
    }

    /**
     * @param path The path to the image
     * @param description The description of the image
     * @return ImageIcon, or null if the path was invalid.
     */
    protected ImageIcon createImageIcon(String path, String description) {
        URL imgURL = this.getClass().getResource(path);
        ImageIcon icon = null;
        if (imgURL != null) {
            icon = new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
        }
        return icon;
    }

    public void clearReasonTable(){
        while (tcReasonModel.getRowCount() > 0) {
            tcReasonModel.setRowCount(0);
        }
        stackTrace.setText("");
    }

    private void checkForStop(TestCaseTag tct){
        if (stopExecution) {
            tct.setExecuteTestCase(false);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////
    //          TestCaseListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    public void beginTestCase(TestCaseEvent event) {
        TestCaseTag tct = (TestCaseTag)event.getSource();
        checkForStop(tct);
        addTestCaseRow(tct);
        if (tcTable.getSelectedRow() == -1) {
            clearReasonTable();
        }
    }

    public void endTestCase(TestCaseEvent event) {
        TestCaseTag tct = (TestCaseTag)event.getSource();
        stepNextTag = false;
        reportTcResults(tct);
    }

    ///////////////////////////////////////////////////////////////////////////////
    //          FunctionListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    public void beginFunction(FunctionEvent event, int rowNum) {
        FunctionTag ft = (FunctionTag)event.getSource();
        if (ft != null) {
            highlightScriptLocation(ft);
            checkForStop(ft.getTestCaseTag());
            if (debug && (ft.isBreakPoint() || stepNextTag)) {
                new FunctionDebugDialog(ft, rootFrame, this, ft.getElementName() +": " +ft.getFunctionId());
            }
        }
    }

    public void endFunction(FunctionEvent event, int rowNum) {
        FunctionTag ft = (FunctionTag)event.getSource();
        checkForStop(ft.getTestCaseTag());
        FunctionResult fr = ft.getFunctionResults();
        System.out.println("Handling Function End Event --- " + fr.toString());
        if (fr.failed() && tcTable.getSelectedRow() == -1) {
            Object[] row = new Object[6];
            row[TCResultsSelectionListeners.LINE] = new Integer(fr.getLineNumber());
            if (fr.getErrorFile() != null) {
                row[TCResultsSelectionListeners.URL] = snapShotImg;
            }
            row[TCResultsSelectionListeners.FUNCTION_ID] = fr.getIdentifier();
            row[TCResultsSelectionListeners.ERR_MSG] = fr.getErrorMsg();
            row[TCResultsSelectionListeners.OBJECT] = fr;
            row[TCResultsSelectionListeners.ROW] = getFailedRowNum(fr);
    
            tcReasonModel.addRow(row);
        }
        /***********************************************************
         * Added for RI POC 
         *
         */
       else if (!fr.failed() && tcTable.getSelectedRow() == -1) {
            Object[] row = new Object[6];
            row[TCResultsSelectionListeners.LINE] = new Integer(fr.getLineNumber());
            if (fr.getErrorFile() != null) {
                row[TCResultsSelectionListeners.URL] = snapShotImg;
            }
            row[TCResultsSelectionListeners.FUNCTION_ID] = fr.getIdentifier();
            row[TCResultsSelectionListeners.ERR_MSG] = fr.getOutcome();
            row[TCResultsSelectionListeners.OBJECT] = fr;
            row[TCResultsSelectionListeners.ROW] = fr.getLineNumber();
    
            tcReasonModel.addRow(row);
        }
        /***********************************************************
         * End Added for RI POC 
         *
         */

    }

    ///////////////////////////////////////////////////////////////////////////////
    //          DataDrivableListener methods                                         //
    ///////////////////////////////////////////////////////////////////////////////
    /**
     * Gets called before the open method of a DataDrivable
     * @param event - a DataDrivableEvent Object
     */
    public void openEvent(DataDrivableEvent event){
        //Not currently supported
    }

    /**
     * Gets called before the close method of a DataDrivable
     * @param event - a DataDrivableEvent Object
     */
    public void closeEvent(DataDrivableEvent event){
        //Not currently supported
    }

    /**
     * Gets called before the executeDrivableRow
     * @param event - a DataDrivableEvent Object
     * @param rowNum - the current row number being executed from the data source.
     */
    public void executeRowEvent(DataDrivableEvent event, int rowNum){
        DataDrivable dd = (DataDrivable)event.getSource();
        if (dd instanceof LocationAwareTagSupport) {
            highlightScriptLocation((LocationAwareTagSupport)dd);
        }
        
        if (debug && (stepNextTag || (dd instanceof BreakPoint && ((BreakPoint)dd).isBreakPoint()))) {
            String title = "data drivable tag";
            if (dd instanceof LocationAwareTagSupport) {
                title = ((LocationAwareTagSupport)dd).getElementName();
            }
            new DataDrivableDebugDialog(event, rootFrame, this, title);
        }
    }

    private void highlightScriptLocation(LocationAware la){
        highlightScriptLocation(la, debug);
    }

    private void highlightScriptLocation(LocationAware la, boolean highlight){
        if (highlight) {
            int lineNum = la.getLineNumber()-1;
            try{
                if (sourceArea.getSelectionEnd() == 0) {
                    sourceArea.setCaretPosition(0);
                }
                sourceArea.setCaretPosition(sourceArea.getLineStartOffset(lineNum));
                sourceArea.moveCaretPosition(sourceArea.getLineEndOffset(lineNum));
            }catch(BadLocationException ble){
                ble.printStackTrace();
            }
        }
    }
    
    protected Integer getFailedRowNum(JameleonTestResult result){
    	int rowNum = 0;
    	DataDrivableRowResult ddr = (DataDrivableRowResult)result.findAncestorByClass(DataDrivableRowResult.class);
    	if (ddr != null){
    		rowNum = ddr.getRowNum();
    	}
    	return new Integer(rowNum);
    }


    ///////////////////////////////////////////////////////////////////////////////
    //          Event Listeners                                                  //
    ///////////////////////////////////////////////////////////////////////////////
    public class TCResultsSelectionListeners implements ListSelectionListener{
        protected static final int ROW = 0;
        protected static final int LINE = 1;
        protected static final int URL = 2;
        protected static final int FUNCTION_ID = 3;
        protected static final int ERR_MSG = 4;
        protected static final int OBJECT = 5;
        
        /**
         * Called whenever the value of the selection changes.
         * @param e the event that characterizes the change.
         */
        public void valueChanged(ListSelectionEvent e) {
            clearReasonTable();
            int selectedRow = tcTable.getSelectedRow();
            if (selectedRow > -1) {
                TestCaseTag tct = (TestCaseTag)tcTable.getModel().getValueAt(selectedRow, TC);
                TestCaseResult result = tct.getResults();
                if (result.failed()) {
                    Object[] row = new Object[6];
                    List failedResults = result.getFailedResults();
                    Iterator it = failedResults.iterator();
                    JameleonTestResult tr;
                    while (it.hasNext()) {
                        tr = (JameleonTestResult)it.next();
                        row[LINE] = new Integer(tr.getLineNumber());
                        row[FUNCTION_ID] = tr.getIdentifier();
                        if (tr.getErrorFile() != null) {
                            row[URL] = snapShotImg;
                        }
                        row[ERR_MSG] = tr.getError().getMessage();
                        row[OBJECT] = tr;
                        row[ROW] = getFailedRowNum(tr);
                        tcReasonModel.addRow(row);
                    }
                }
            }
            
        }
        
    }

    public class FTResultsSelectionListeners implements ListSelectionListener{
        /**
         * Called whenever the value of the selection changes.
         * @param e the event that characterizes the change.
         */
        public void valueChanged(ListSelectionEvent e) {
            int selectedRow = tcReasonTable.getSelectedRow();
            if (selectedRow > -1) {
                Object obj = tcReasonTable.getValueAt(selectedRow, TCResultsSelectionListeners.OBJECT);
                if (obj instanceof JameleonTestResult) {
                    JameleonTestResult result = (JameleonTestResult) obj;
                    Throwable t = result.getError();
                    if (t != null) {
                        t = getCause(t);
                        stackTrace.setText(JameleonUtility.getStack(t));
                        stackTrace.setCaretPosition(0);
                    }
                }
            }
        }

        private Throwable getCause(Throwable t){
            Throwable cause = t.getCause();
            if (cause == null) {
                cause = t;
            }
            return cause;
        }

    }

    public class FTResultsMouseListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                Point origin = e.getPoint();
                int row = tcReasonTable.rowAtPoint(origin);
                int col = tcReasonTable.columnAtPoint(origin);
                if (row > -1 && col == TCResultsSelectionListeners.URL &&
                    tcReasonTable.getValueAt(row,TCResultsSelectionListeners.OBJECT) instanceof JameleonTestResult) {
                    JameleonTestResult tr = (JameleonTestResult)tcReasonTable.getValueAt(row,TCResultsSelectionListeners.OBJECT);
                    if (tr.getErrorFile() != null) {
                        try{
                            snapShotBrowser.goToUrl(tr.getErrorFile().toURI().toURL());
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }else if (row > -1 && col == TCResultsSelectionListeners.LINE) {
                    JameleonTestResult tr = (JameleonTestResult)tcReasonTable.getValueAt(row, TCResultsSelectionListeners.OBJECT);
                    int selectedRow = tcTable.getSelectedRow();
                    if (selectedRow == -1) {
                        selectedRow = 0;
                    }
                    TestCaseTag tct = (TestCaseTag)tcTable.getValueAt(selectedRow, TC);
                    if (tct != null) {
                        try{
                            tcTree.setTestCaseSource(new File(new URI(tct.getFileName())), true);
                            highlightScriptLocation(tr, true);
                        }catch(Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public class TCResultsMouseListener extends MouseAdapter{
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 1) {
                Point origin = e.getPoint();
                int row = tcTable.rowAtPoint(origin);
                int col = tcTable.columnAtPoint(origin);
                if (row > -1 && col == TestCaseResultsPane.STATUS &&
                    tcTable.getValueAt(row,TestCaseResultsPane.TC) instanceof TestCaseTag) {
                    TestCaseTag tct = (TestCaseTag)tcTable.getValueAt(row,TestCaseResultsPane.TC);
                    if (tct != null) {
                        try{
                            System.out.println(tct.getResultsFile());
                            snapShotBrowser.goToUrl(tct.getResultsFile().toURI().toURL());
                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }



}
