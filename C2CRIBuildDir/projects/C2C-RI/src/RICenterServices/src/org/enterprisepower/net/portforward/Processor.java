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
package org.enterprisepower.net.portforward;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.enterprisepower.io.IOUtils;
import org.enterprisepower.net.NetUtils;
import org.apache.log4j.Logger;
import org.enterprisepower.io.CopyStreamObserver;

/**
 *
 * @author Kenneth Xu
 *
 */
public class Processor implements Cleanable {
//	private static final Log log = LogFactory.getLog(Processor.class);

    private static final Logger log = Logger.getLogger("net.sf.jameleon");
    private Socket source;
    private Socket target;
    private InetSocketAddress extAddress;
    private Cleaner cleaner;
    private String testCase;
    private String connectionName;
    private boolean serverConnection;
    Processor.Copier req;
    Processor.Copier res;

    public Processor(Socket source, Socket target, InetSocketAddress extAddress, Cleaner cleaner, boolean serverConnection, String testCase, String connectionName) {
        this.testCase = testCase;
        this.connectionName = connectionName;
        this.serverConnection = serverConnection;
        this.source = source;
        this.target = target;
        this.cleaner = cleaner;
        this.extAddress = extAddress;
        cleaner.add(this);
    }

    public void process() {

//		req = new Copier(source, target);
//		res = new Copier(target, source);
        req = new Processor.Copier(source, target, extAddress, "Request", connectionName);
        res = new Processor.Copier(target, source, extAddress, "Response", connectionName);

        new Thread(req, connectionName + "-req").start();
        new Thread(res, connectionName + "-resp").start();
        if (log.isTraceEnabled()) {
            log.debug("started new request stream copier threads: " + req);
            log.debug("started new response stream copier threads: " + res);
        }
    }

    public boolean isCompleted() {
        if (req.isCompleted) {
            log.debug(connectionName + " Request Process Claims Completion");
        }
        if (res.isCompleted) {
            log.debug(connectionName + " Response Process Claims Completion");
        }
        return req.isCompleted && res.isCompleted;
    }

    public void close() {
        if (log.isTraceEnabled()) {
            log.trace("close() called on " + this);
        }
        log.debug("Now closing the " + connectionName + " processor and it's sockets");
        NetUtils.close(source);
        NetUtils.close(target);
    }

    public String toString() {
        return this.getClass().getName() + "(from " + source + " to " + target
                + ")";
    }

    private class Copier implements Runnable, CopyStreamObserver {

        Socket in;
        Socket out;
        boolean isCompleted = false;
        InetSocketAddress extAddress;
        String processType = "";
        String connectionName = "";

        Copier(Socket in, Socket out) {
            this.in = in;
            this.out = out;
        }

// Added after Here
        Copier(Socket in, Socket out, InetSocketAddress extAddress, String processType, String connectionName) {
            try {
                //                       in.setSoTimeout(5000);
                //                       out.setSoTimeout(5000);
            } catch (Exception ex) {
            }
            this.in = in;
            this.out = out;
            this.processType = processType;
            this.connectionName = connectionName;
            this.extAddress = extAddress;
        }

// Added ends Here
        public void run() {
            try {
//                            IOUtils.copyStream(in.getInputStream(), out.getOutputStream(),
//						false);
                log.debug("Processor.run: " + connectionName + ":  " + processType + "  In: " + in.getInetAddress().toString() + ":" + in.getPort() + "  Out: " + out.getInetAddress().toString() + ":" + out.getPort());
                IOUtils.copyStream(in.getInputStream(), out.getOutputStream(),
                        //Caused the FTP Client to hang on read and sockets to close incorrectly						false, this.processType);
                        true, this.processType, this, connectionName);

            } catch (java.net.SocketException e) {
                NetUtils.shutdown(source);
                NetUtils.shutdown(target);
                log.debug("***" + this.processType + " Processor SocketException: " + e.getMessage());
                log.debug(this, e);
            } catch (Exception e) {
                log.debug("***" + this.processType + " Processor Exception: " + e.getMessage());
                log.error(this, e);
            } finally {
                this.isCompleted = true;
                if (cleaner != null) {
                    synchronized (cleaner) {
                        cleaner.notifyAll();
                    }
                }
            }
        }

        // Added to log the message updates
        public void streamUpdate(byte[] currentBuffer, int bufferCount, int sequenceCount) {
            long timeStamp = System.currentTimeMillis();
            StringBuffer fullFile = new StringBuffer();
            //            for (int i = 0; i < bufferCount; i++) {
//                    System.out.print((char) currentBuffer[i]);
            //                fullFile = fullFile.append((char) (currentBuffer[i]));

            //            }
            int width = 16;
            for (int index = 0; index < bufferCount; index += width) {
                fullFile = fullFile.append(printHex(currentBuffer, index, width));
                try {
                    fullFile = fullFile.append(printAscii(currentBuffer, index, width));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    fullFile = fullFile.append(": Error - " + ex.getMessage() + "\n");
                }
            }

            int cdataLocation = fullFile.indexOf("![CDATA[");
            int indexLocation = fullFile.indexOf("]]>");
            while ((cdataLocation != -1) && (indexLocation != -1)) {
                fullFile.replace(indexLocation, indexLocation + 2, "]]'>");
                cdataLocation = fullFile.indexOf("![CDATA[", indexLocation);
                indexLocation = fullFile.indexOf("]]>", indexLocation);
            }
//                    log.debug(fullFile.toString());
            String messageToXML = "<rawOTWMessage>\n";
            messageToXML = messageToXML.concat("<testCase>" + testCase + "</testCase>\n");
            messageToXML = messageToXML.concat("<connectionName>" + connectionName + "</connectionName>\n");
            messageToXML = messageToXML.concat("<ProcessType>" + processType + "</ProcessType>\n");
            if (serverConnection) {
//                               try{
//                                   System.out.println("External Address Local Host: "+extAddress.getAddress().getLocalHost().getHostAddress());
//                                   System.out.println("External Address Remote Host: "+extAddress.getAddress().getHostAddress());
//                                   System.out.println("In Address Inet: "+in.getInetAddress()+":"+in.getPort());
//                                   System.out.println("In Address local: "+in.getLocalAddress()+":"+in.getLocalPort());
//                                   System.out.println("Out Address Inet: "+out.getInetAddress()+":"+out.getPort());
//                                   System.out.println("Out Address local: "+out.getLocalAddress()+":"+out.getLocalPort());
//                               } catch (Exception ex){
//                                   ex.printStackTrace();
//                               }
                messageToXML = messageToXML.concat("<SourceAddress>" + (processType.equalsIgnoreCase("Request") ? in.getInetAddress().toString() + ":" + in.getPort() : out.getLocalAddress().toString() + ":" + String.valueOf(out.getLocalPort())) + "</SourceAddress>\n");
                messageToXML = messageToXML.concat("<DestinationAddress>" + (processType.equalsIgnoreCase("Request") ? in.getLocalAddress().toString() + ":" + String.valueOf(in.getLocalPort()) : out.getInetAddress().toString() + ":" + out.getPort()) + "</DestinationAddress>\n");
            } else {
                messageToXML = messageToXML.concat("<SourceAddress>" + (processType.equalsIgnoreCase("Request") ? out.getLocalAddress().toString() + ":" + out.getLocalPort() : in.getInetAddress().toString() + ":" + String.valueOf(in.getPort())) + "</SourceAddress>\n");
                messageToXML = messageToXML.concat("<DestinationAddress>" + (processType.equalsIgnoreCase("Request") ? out.getInetAddress().toString() + ":" + String.valueOf(out.getPort()) : in.getLocalAddress().toString() + ":" + in.getLocalPort()) + "</DestinationAddress>\n");
            }
            messageToXML = messageToXML.concat("<timestampInMillis>" + timeStamp + "</timestampInMillis>\n");
            messageToXML = messageToXML.concat("<sequenceCount>" + sequenceCount + "</sequenceCount>\n");
            messageToXML = messageToXML.concat("<message>\n<![CDATA[\n" + fullFile + "]]>\n</message>\n");
            messageToXML = messageToXML.concat("</rawOTWMessage>\n");

            System.out.println(messageToXML + "\n");
            log.info(messageToXML);
        }

        private String printHex(byte[] bytes, int offset, int width) {
            String fs = "";
            for (int index = 0; index < width; index++) {
                if (index + offset < bytes.length) {
                    fs = fs.concat(String.format("%02x ", bytes[index + offset]));
                } else {
                    fs = fs.concat(String.format("	"));
                }
            }
            return fs;
        }

        private String printAscii(byte[] bytes, int index, int width)
                throws UnsupportedEncodingException {
            byte[] invalidByte = new byte[1];
            invalidByte[0] = 0x00;
            String invalidString = new String(invalidByte, "UTF-8");
            invalidByte[0] = 0x1f;
            String invalidString2 = new String(invalidByte, "UTF-8");
            invalidByte[0] = 0x8;
            String invalidString3 = new String(invalidByte, "UTF-8");
            invalidByte[0] = 0x18;
            String invalidString4 = new String(invalidByte, "UTF-8");

            for (int ii = 0; ii < bytes.length; ii++) {
                if (bytes[ii] < 0x20 || bytes[ii] > 0x7f) {
                    bytes[ii] = 0x20;
                }
            }

            if (index < bytes.length) {
                width = Math.min(width, bytes.length - index);
                return ":"
                        + new String(bytes, index, width, "UTF-8").replace("\r\n", " ").replace(
                        "\n",
                        " ").replaceAll(invalidString, "").replaceAll(invalidString2, "").replaceAll(invalidString3, "").replaceAll(invalidString4, "") + "\n";
            } else {
                return "\n";
            }
        }

        public String toString() {
            return this.getClass().getName() + "(from " + in + " to " + out
                    + ")";
        }
    }
}
