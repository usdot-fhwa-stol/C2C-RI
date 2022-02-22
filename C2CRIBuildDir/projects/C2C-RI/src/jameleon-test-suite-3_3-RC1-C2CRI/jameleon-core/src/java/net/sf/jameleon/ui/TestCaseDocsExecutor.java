package net.sf.jameleon.ui;

import java.io.File;

import javax.swing.JOptionPane;

import net.sf.jameleon.bean.TestCase;

public class TestCaseDocsExecutor extends Thread {
    private File script;
    private TestCasePane tcp;
    private ProgressDialog dialog;

    public TestCaseDocsExecutor(File script, TestCasePane tcp) {
        this.script = script;
        this.tcp = tcp;
    }

    public void setProgressDialog(ProgressDialog pg){
        dialog = pg;
    }

    public void run() {
        TestCase tc = null;
        try {
            tc = new TestCase();
            tc.readFromScript("file:"+script.getAbsolutePath());
        } catch (Throwable e) {
            e.printStackTrace();
            String msg = e.getMessage();
              JOptionPane.showMessageDialog(
                  tcp,
                  msg,
                  "Error Parsing Script",
                  JOptionPane.ERROR_MESSAGE);

        } finally {
            if (dialog != null) {
                dialog.taskCompleted();
            }
            tcp.setTestCaseInfo(tc);
        }
    }

}

