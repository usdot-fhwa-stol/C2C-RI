/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.enterprisepower.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * IO related static utilties methods are defined in this class.
 *
 * @author Kenneth Xu
 *
 */
public final class IOUtils {

    private static final Log log = LogFactory.getLog(IOUtils.class);
    public static final int BUF_SIZE = 1024;

    private IOUtils() {
    }

    /**
     * Read everything from
     * <code>in</code> and write to
     * <code>out</code> until reachs the end of the input stream. Both
     * <code>in</code> and
     * <code>out</code> are then quitely closed (see {@link #close(InputStream)}
     * ).
     *
     * @param in data copy from
     * @param out data copy to
     * @throws IOException IO error occured when coping.
     * @see #copyStream(InputStream, OutputStream, boolean)
     */
    public static void copyStream(InputStream in, OutputStream out)
            throws IOException {
        copyStream(in, out, true);
    }

    /**
     * Read everything from
     * <code>in</code> and write to
     * <code>out</code> until reachs the end of the input stream. If
     * <code>closeOnFinish</code> is
     * <code>true</code>, it quitely closes both
     * <code>in</code> and
     * <code>out</code> (see {@link #close(InputStream)}) before retun.
     * Otherwise, it leaves both of them open.
     *
     * @param in data copy from
     * @param out data copy to
     * @param closeOnFinish passing <code>true</code> to close both stream
     * before method return
     * @throws IOException IO error occured when coping.
     * @see #copyStream(InputStream, OutputStream)
     */
    public static void copyStream(InputStream in, OutputStream out,
            boolean closeOnFinish) throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        int count;
        try {
            int ii = 0;  // Added
            while ((count = in.read(buf)) != -1) {
                out.write(buf, 0, count);

// Added From Here
                System.out.println("*** 3 arg copyStream *****");
                for (int i = 0; i < buf.length; i++) {
                    //                           String outString = new String(buf);

                    //                           System.out.print(
                    //                                   outString);

                    System.out.print((char) buf[i]);
                    /**
                     * System.out.println( "buf[" + ii++ + "] = " + ((int)
                     * buf[i] < 9 ? " " : "") + (((int) buf[i] > 9 && (int)
                     * buf[i] <= 99) ? " " : "") + buf[i] + " : " + "
                     * charValue=(" + (char) buf[i] + ")");
                     */
                }
//Added to Here
            }
        } finally {
            if (closeOnFinish) {
                close(in);
            }
            if (closeOnFinish) {
                close(out);
            }
        }
    }

    /**
     * Modified for the C2CRI to be able to log over the wire messages.
     *
     * Read everything from
     * <code>in</code> and write to
     * <code>out</code> until reachs the end of the input stream. If
     * <code>closeOnFinish</code> is
     * <code>true</code>, it quitely closes both
     * <code>in</code> and
     * <code>out</code> (see {@link #close(InputStream)}) before retun.
     * Otherwise, it leaves both of them open.
     *
     * @param in data copy from
     * @param out data copy to
     * @param closeOnFinish passing <code>true</code> to close both stream
     * before method return
     * @param processType indicates a request or a response
     * @return a string representation of the stream that was copied.
     * @throws IOException IO error occured when coping.
     * @see #copyStream(InputStream, OutputStream)
     */
    public static void copyStream(InputStream in, OutputStream out,
            boolean closeOnFinish, String processType, CopyStreamObserver streamObserver, String connectionName) throws IOException {
        byte[] buf = new byte[BUF_SIZE];
        Arrays.fill(buf, (byte) 32);
        int count;
        int totalSize = 0;
        try {
            int ii = 0;  // Added
            while ((count = in.read(buf)) != -1) {
                out.write(buf, 0, count);
                streamObserver.streamUpdate(buf, count, ii);
                log.debug(connectionName + " -- " + processType + ":" + " (IOUtils)");
                totalSize = totalSize + count;
                Arrays.fill(buf, (byte) 32);
                ii++;
            }
        } catch (Exception ex) {
            log.debug("[IOUTILS: " + connectionName + " -- " + processType + "] ***** Guess What there was an Error: " + ex.getMessage());
        } finally {
            if (closeOnFinish) {
                close(in);
            }
            if (closeOnFinish) {
                close(out);
            }
            log.debug("[IOUTILS] " + connectionName + " ProcessType= " + processType + "!!! Copy Complete of " + totalSize + " Bytes \n");
        }
    }

    /**
     * Quietly close a {@linkplain java.io.InputStream}. Exceptions are
     * discarded.
     *
     * @param in the input stream to close
     * @see #close(InputStream, boolean)
     */
    public static void close(InputStream in) {
        close(in, false);
    }

    /**
     * Quietly close a {@linkplain java.io.InputStream} without throwing any
     * exception. Exceptions will be logged as error if the
     * <code>logError</code> is
     * <code>true</code>.
     *
     * @param in the input stream to close
     * @param logError true to log exception as error
     * @see #close(InputStream)
     * @see #close(OutputStream, boolean)
     */
    public static void close(InputStream in, boolean logError) {
        if (in != null) {
            try {
                in.close();
            } catch (Throwable t) {
                if (logError) {
                    log.error(t.getMessage(), t);
                } else {
                    log.debug(t.getMessage(), t);
                }
            }
        }
    }

    /**
     * Quietly close a {@linkplain java.io.OutputStream}. Exceptions are
     * discarded.
     *
     * @param out the output stream to close
     * @see #close(OutputStream, boolean)
     */
    public static void close(OutputStream out) {
        close(out, false);
    }

    /**
     * Quietly close a {@linkplain java.io.OutputStream} without throwing any
     * exception. Exceptions will be logged as error if the
     * <code>logError</code> is
     * <code>true</code>.
     *
     * @param out the output stream to close
     * @param logError true to log exception as error
     * @see #close(OutputStream)
     * @see #close(InputStream, boolean)
     */
    public static void close(OutputStream out, boolean logError) {
        if (out != null) {
            try {
                out.close();
            } catch (Throwable t) {
                if (logError) {
                    log.error(t.getMessage(), t);
                } else {
                    log.debug(t.getMessage(), t);
                }
            }
        }
    }
}
