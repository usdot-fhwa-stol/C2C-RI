/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.gui;

import java.util.concurrent.LinkedBlockingQueue;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author TransCore ITS
 */
public class StatusLogCache extends AbstractTableModel {

    final static LinkedBlockingQueue<TestStatus> tsQueue = new LinkedBlockingQueue();
    private static StatusLogCache instance = null;
    private static int filteredTestIndex = 0;


    Thread logStatusThread = new Thread() {
        public void run() {
            try{
                while (true){
                    if (tsQueue.size() > 0){
                       TestStatus ts = tsQueue.take();
                             ActionLogDatabase.getInstance().insertStatus(ts.getTimeStamp(), ts.getMessage(), ts.getTestIndex());
                            instance.fireTableDataChanged();
                    } else {
                       Thread.sleep(100);                        
                    }
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                tsQueue.clear();
				Thread.currentThread().interrupt();
            }
        }
    };
    
    int messageId;

    /** The Constant Timestamp column. */
    public static final int TimeStamp = 0;
    
    /** The Constant Message column. */
    public static final int Message = 1;

    /** The Constant Test Index. */
    public static final int TestIndex = 2;
  
    /** The column names. */
    private String[] columnNames = {"TimeStamp",
        "Message"};    
    
    final static Object instancelockObject = new Object();    
    
    private StatusLogCache(){        
    }
    
    public static StatusLogCache getInstance(){
        synchronized (instancelockObject){
            if (instance == null){
                instance = new StatusLogCache();
                instance.start();
            } 
            return instance;
        }
    }
        
    private void start(){
        synchronized (instancelockObject){
            logStatusThread.start();
        }
    }

    public void stop(){
        synchronized (instancelockObject){
            logStatusThread.interrupt();
            tsQueue.clear();
            ActionLogDatabase.getInstance().closeDatabase();
            setFilteredTestIndex(0);
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
        StatusLogCache tmdb = StatusLogCache.getInstance();
        tmdb.addTestStatus("123","org.fhwa.fake", 1);
        System.out.println("During --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);
        Thread.sleep(200);

        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        StatusLogCache tmdb2 = StatusLogCache.getInstance();
        tmdb2.addTestStatus("1234","org.fhwa.fake2", 2);
        System.out.println("During Second --> Current Heap: " + heapSize + "  MaxHeap: " + heapMaxSize + "  FreeHeap: " + heapFreeSize);
        Thread.sleep(200);


        heapSize = Runtime.getRuntime().totalMemory();
        heapMaxSize = Runtime.getRuntime().maxMemory();
        heapFreeSize = Runtime.getRuntime().freeMemory();
        StatusLogCache tmdb3 = StatusLogCache.getInstance();
        tmdb3.addTestStatus("123","org.fhwa.fake3", 3);
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

    
    public void addTestStatus(String timeStamp, String message, int testIndex ){
        tsQueue.add(new TestStatus(timeStamp, message, testIndex));
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
            if (instance == null) return 0;
            return ActionLogDatabase.getInstance().getCurrentStatusRowCount();
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
            return ActionLogDatabase.getInstance().getStatusContent(row+1, column);
        }
    }
    
    public int getTestIndexAt(int row) {
        // The table rows are 0 based but we start the database records at 1.
        String testIndexValue = ActionLogDatabase.getInstance().getStatusContent(row+1, TestIndex);
        return Integer.valueOf(testIndexValue);
    }
    
    public int getFilteredTestIndex() {
        synchronized (instancelockObject){
            return filteredTestIndex;
        }
    }

    public void setFilteredTestIndex(int filteredTestIndex) {
        synchronized (instancelockObject){
            this.filteredTestIndex = filteredTestIndex;
        }
    }

    class TestStatus{
        String timeStamp;
        String message;
        int testIndex;

        public TestStatus(String time, String message, int index){
            this.timeStamp = time;
            this.message = message;
            this.testIndex = index;
        }
        
        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getTestIndex() {
            return testIndex;
        }

        public void setTestIndex(int testIndex) {
            this.testIndex = testIndex;
        }
        
        
    }
}
