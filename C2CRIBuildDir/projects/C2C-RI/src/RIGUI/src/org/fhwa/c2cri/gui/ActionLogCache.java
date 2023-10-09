/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.table.AbstractTableModel;
import org.fhwa.c2cri.logger.ActionLogAppenderListener;

/**
 *
 * @author TransCore ITS
 */
public class ActionLogCache extends AbstractTableModel implements ActionLogAppenderListener {

    final static LinkedBlockingQueue<TestAction> taQueue = new LinkedBlockingQueue();
    private static ActionLogCache instance = null;
    private static int currentTestIndex=0;
    
    transient Thread logActionThread = new Thread() {
        public void run() {
            try{
                while (true){
                    if (taQueue.size() > 0){
                       TestAction ta = taQueue.take();
                       if (ta.isValidType()){
                           synchronized (testIndexlockObject){
                               if (ta.getTestIndex() > currentTestIndex)
                                    currentTestIndex = ta.getTestIndex();
                           }
                            ActionLogDatabase.getInstance().insertAction(ta.getTimestamp(), ta.getMessage(), ta.getResult(), ta.getTestIndex());
                            instance.fireTableDataChanged();
                       }
                    } else {
                       Thread.sleep(100);                        
                    }
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                taQueue.clear();
				Thread.currentThread().interrupt();
            }
        }
    };
    
    int messageId;

    /** The Constant Title_Col. */
    public static final int TimeStamp = 0;
    
    /** The Constant Source. */
    public static final int Description = 1;

    /** The Constant Source. */
    public static final int Results = 2;

    /** The Constant Test Index. */
    public static final int TestIndexColumn = 3;
    
    /** The column names. */
    private String[] columnNames = {"TimeStamp",
        "Description","Results"};    
    
    final static Object instancelockObject = new Object();    
    final static Object testIndexlockObject = new Object();   
    
    private ActionLogCache(){        
    }
    
    public static ActionLogCache getInstance(){
        synchronized (instancelockObject){
            if (instance == null){
                instance = new ActionLogCache();
                instance.start();
            } 
            return instance;
        }
    }
        
    private void start(){
        synchronized (instancelockObject){
            logActionThread.start();
        }
    }

    public void stop(){
        synchronized (instancelockObject){
            logActionThread.interrupt();
            taQueue.clear();
            ActionLogDatabase.getInstance().closeDatabase();
            setCurrentTestIndex(0);
            instance = null;
        }
    }    

    /**
     * The main method.
     *
     * Pre-Conditions: N/A Post-Conditions: N/A
     *
     * @param args the arguments
     * @throws Exception the exception
     */
    public static void main(String[] args) throws Exception {
        // Get current size of heap in bytes
        long heapSize = Runtime.getRuntime().totalMemory();

        // Get maximum size of heap in bytes. The heap cannot grow beyond this size.// Any attempt will result in an OutOfMemoryException.
        long heapMaxSize = Runtime.getRuntime().maxMemory();

        // Get amount of free memory within the heap in bytes. This size will increase // after garbage collection and decrease as new objects are created.
        long heapFreeSize = Runtime.getRuntime().freeMemory();

        System.out.println("Before --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        ActionLogCache tmdb = ActionLogCache.getInstance();
        tmdb.addAction("123","org.fhwa.fake", "True");
        System.out.println("During --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);
        Thread.sleep(200);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        ActionLogCache tmdb2 = ActionLogCache.getInstance();
        tmdb2.addAction("1234","org.fhwa.fake2", "True2");
        System.out.println("During Second --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);
        Thread.sleep(200);


        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        ActionLogCache tmdb3 = ActionLogCache.getInstance();
        tmdb3.addAction("123","org.fhwa.fake3", "False");
        System.out.println("During Third --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);
        Thread.sleep(200);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        System.out.println(tmdb.getValueAt(1, 2));
        System.out.println("After --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        System.out.println(tmdb.getValueAt(2, 1));
        System.out.println("After Second --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        System.out.println(tmdb.getValueAt(3, 0));
        System.out.println("After Third --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);


        instance.stop();
 //       instance = null;

        System.out.println("Exiting Now.");
    }


    /* (non-Javadoc)
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("Finalize was called");

        ActionLogDatabase.getInstance().closeDatabase();

    }
   
    public void appenderUpdate(long timestamp, String message){
        taQueue.add(new TestAction(timestamp, message, getCurrentTestIndex()));
    }    
    
    public void addAction(String timeStamp, String message, String result ){
        taQueue.add(new TestAction(timeStamp, message, result, getCurrentTestIndex()));
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class; //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getColumnName(int column) {
        if (column > columnNames.length) return "NA";
        return columnNames[column]; //To change body of generated methods, choose Tools | Templates.
    }

    
    @Override
    public int getRowCount() {
        synchronized (instancelockObject){
            if (instance == null)return 0;
            return ActionLogDatabase.getInstance().getCurrentActionRowCount();
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int column) {
        synchronized (instancelockObject){
            if (instance == null)return "";
            // The table rows are 0 based but we start the database records at 1.
            // The column offset is handled by the fact that we hide the ID column.
            return ActionLogDatabase.getInstance().getActionContent(row+1, column);
        }
    }


    public int getTestIndexAt(int row){
        // The table rows are 0 based but we start the database records at 1.
        String testIndex = ActionLogDatabase.getInstance().getActionContent(row+1, TestIndexColumn);
        return Integer.parseInt(testIndex);        
    }    
    
    public void refreshTableForSelection(){
        fireTableDataChanged();
    }

    public int getCurrentTestIndex() {
        synchronized (testIndexlockObject){
            return currentTestIndex;
        }
    }

    public void setCurrentTestIndex(int currentTestIndex) {
        synchronized (testIndexlockObject){
            this.currentTestIndex = currentTestIndex;
        }
    }    
}
