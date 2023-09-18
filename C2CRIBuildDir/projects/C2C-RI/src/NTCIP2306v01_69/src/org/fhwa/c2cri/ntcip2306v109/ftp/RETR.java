/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fhwa.c2cri.ntcip2306v109.ftp;

/**
 *
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import org.apache.ftpserver.command.AbstractCommand;
import org.apache.ftpserver.ftplet.DataConnection;
import org.apache.ftpserver.ftplet.DataConnectionFactory;
import org.apache.ftpserver.ftplet.DataType;
import org.apache.ftpserver.ftplet.DefaultFtpReply;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpFile;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.impl.FtpIoSession;
import org.apache.ftpserver.impl.FtpServerContext;
import org.apache.ftpserver.impl.IODataConnectionFactory;
import org.apache.ftpserver.impl.LocalizedFtpReply;
import org.apache.ftpserver.impl.ServerFtpStatistics;
import org.apache.ftpserver.usermanager.impl.TransferRateRequest;
import org.apache.ftpserver.util.IoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <strong>Internal class, do not use directly.</strong>
 *
 * <code>RETR &lt;SP&gt; &lt;pathname&gt; &lt;CRLF&gt;</code><br>
 *
 * This command causes the server-DTP to transfer a copy of the file, specified
 * in the pathname, to the server- or user-DTP at the other end of the data
 * connection. The status and contents of the file at the server site shall be
 * unaffected.
 *	
 *  Modified for the RI project to be able to obtain the addresses and ports of systems interracting.
 *
 * @author <a href="http://mina.apache.org">Apache MINA Project</a>
 */
public class RETR extends AbstractCommand implements RIRETRResponseReceiver {

    /** The Constant EOL. */
    private static final byte[] EOL = System.getProperty("line.separator").getBytes();
    
    /** The log. */
    private final Logger LOG = LoggerFactory.getLogger(RETR.class);
    
    /** The response message. */
    private byte[] responseMessage;
    
    /** The file path mismatch error. */
    private boolean filePathMismatchError = false;


    /**
 * Sets the response message.
 *
 * @param responseMessage the new response message
 */
@Override
    public void setResponseMessage(byte[] responseMessage) {
        this.responseMessage = responseMessage;
    }

    /**
     * Sets the file path mismatch error.
     *
     * @param filePathError the new file path mismatch error
     */
    @Override
    public void setFilePathMismatchError(boolean filePathError) {
        this.filePathMismatchError = filePathError;
    }

    /**
     * Execute command.
     *
     * @param session the session
     * @param context the context
     * @param request the request
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws FtpException the ftp exception
     */
    public void execute(final FtpIoSession session,
            final FtpServerContext context, final FtpRequest request)
            throws IOException, FtpException {

        try {
            System.out.println("!!!! Inside the RETR Command!!!");

            // get state variable
            long skipLen = session.getFileOffset();

            // argument check
            String fileName = request.getArgument();
            if (fileName == null) {
                session.write(LocalizedFtpReply.translate(
                        session,
                        request,
                        context,
                        FtpReply.REPLY_501_SYNTAX_ERROR_IN_PARAMETERS_OR_ARGUMENTS,
                        "RETR", null));
                return;
            }
            System.out.println("!!!! Inside the RETR Command After File Name Check!!!");

            // get file object
            FtpFile file = null;

            if (filePathMismatchError) {
                session.write(LocalizedFtpReply.translate(session, request, context,
                        FtpReply.REPLY_550_REQUESTED_ACTION_NOT_TAKEN,
                        "RETR.missing", fileName));
                return;
            }

            if (session.getDataConnection() instanceof RIIODataConnectionFactory ){
                System.out.println("RETR::execute - DataConnectionFactory is instance of RIIODataConnectionFactory.");
            } else {
                System.out.println("RETR::execute - DataConnectionFactory is NOT instance of RIIODataConnectionFactory.");                
            }

            // 24-10-2007 - added check if PORT or PASV is issued, see
            // https://issues.apache.org/jira/browse/FTPSERVER-110
            DataConnectionFactory connFactory = session.getDataConnection();
            if (connFactory instanceof IODataConnectionFactory) {
                InetAddress address = ((IODataConnectionFactory) connFactory).getInetAddress();
                if (address == null) {
                    session.write(new DefaultFtpReply(
                            FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS,
                            "PORT or PASV must be issued first"));
                    return;
                }
            } else if (connFactory instanceof RIIODataConnectionFactory){
                InetAddress address = ((RIIODataConnectionFactory) connFactory).getInetAddress();
                if (address == null) {
                    System.out.println("RETR::execute RIIODataConnectionFactory - PORT or PASV must be issued first");
                    session.write(new DefaultFtpReply(
                            FtpReply.REPLY_503_BAD_SEQUENCE_OF_COMMANDS,
                            "PORT or PASV must be issued first"));
                    return;
                }

            }
            System.out.println("!!!! Inside the RETR Command After Address Check!!!");

            // get data connection
            session.write(LocalizedFtpReply.translate(session, request, context,
                    FtpReply.REPLY_150_FILE_STATUS_OKAY, "RETR", null));

            System.out.println("!!!! Inside the RETR Command After Writing File OK Status!!!");

            // send file data to client
            boolean failure = false;
            InputStream nis = null;
            
            DataConnection dataConnection;
            try {
            dataConnection = session.getDataConnection().openConnection();

            } catch (Exception e) {
            System.err.println("Exception getting the output data stream:  "+ e.getMessage());
            LOG.debug("Exception getting the output data stream", e);
            session.write(LocalizedFtpReply.translate(session, request, context,
            FtpReply.REPLY_425_CANT_OPEN_DATA_CONNECTION, "RETR",
            null));
            return;
            }
             
            try {
                
                // open streams

                System.out.println("!!!! Inside the RETR Command Setting the Response Copy");
                byte[] responseCopy = responseMessage;

                System.out.println("!!!! Inside the Setting the InputStream");
                nis = new ByteArrayInputStream(responseCopy);
                 

                // transfer data
                long transSz = dataConnection.transferToClient(session.getFtpletSession(), nis);
                // attempt to close the input stream so that errors in
                // closing it will return an error to the client (FTPSERVER-119)
                if(nis != null) {
                    nis.close();
                }

                LOG.info("File downloaded {}", fileName);

                // notify the statistics component
                ServerFtpStatistics ftpStat = (ServerFtpStatistics) context.getFtpStatistics();
                if (ftpStat != null) {
                    ftpStat.setDownload(session, file, transSz);
                }

            } catch (SocketException ex) {
                LOG.debug("Socket exception during data transfer", ex);
                failure = true;
                session.write(LocalizedFtpReply.translate(session, request, context,
                        FtpReply.REPLY_426_CONNECTION_CLOSED_TRANSFER_ABORTED,
                        "RETR", fileName));
            } catch (IOException ex) {
                LOG.debug("IOException during data transfer", ex);
                failure = true;
                session.write(LocalizedFtpReply.translate(
                        session,
                        request,
                        context,
                        FtpReply.REPLY_551_REQUESTED_ACTION_ABORTED_PAGE_TYPE_UNKNOWN,
                        "RETR", fileName));
            } finally {
                // make sure we really close the input stream
                IoUtils.close(nis);
            }

            // if data transfer ok - send transfer complete message
            if (!failure) {
                session.write(LocalizedFtpReply.translate(session, request, context,
                        FtpReply.REPLY_226_CLOSING_DATA_CONNECTION, "RETR",
                        fileName));

            }
        } finally {
            session.resetState();

        }
    }

    /**
     * Skip length and open input stream.
     *
     * @param session the session
     * @param file the file
     * @param skipLen the skip len
     * @return the input stream
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public InputStream openInputStream(FtpIoSession session, FtpFile file,
            long skipLen) throws IOException {
        InputStream in;
        if (session.getDataType() == DataType.ASCII) {
            int c;
            long offset = 0L;
            in = new BufferedInputStream(file.createInputStream(0L));
            while (offset++ < skipLen) {
                if ((c = in.read()) == -1) {
                    throw new IOException("Cannot skip");
                }
                if (c == '\n') {
                    offset++;
                }
            }
        } else {
            in = file.createInputStream(skipLen);
        }
        return in;
    }

    /**
     * Gets the bytes from file.
     *
     * @param file the file
     * @param is the is
     * @return the bytes from file
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static byte[] getBytesFromFile(FtpFile file, InputStream is) throws IOException {

        // Get the size of the file
        long length = file.getSize();

        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.ftpserver.FtpDataConnection2#transferToClient(java.io.InputStream
     * )
     */
    /**
     * Transfer to client.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param in the in
     * @return the long
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public final long transferToClient(FtpIoSession session, final InputStream in)
            throws IOException {

        InetAddress theClient = session.getDataConnection().getInetAddress();


        String ipAddress;
        try {
            ipAddress = theClient.getHostAddress();
            System.out.println("RETR:  Set the Local IP address to " + ipAddress);
            LOG.debug("RETR:  Set the Local IP address to " + ipAddress);
        } catch (Exception e) {
            ipAddress = "127.0.0.1";
            LOG.debug("RETR:  Could not get the host IP address, setting to local host address ->" + ipAddress + "  Exception =" + e.getMessage());
        }


        System.out.println("RETR: Before the Socket Declaration...");

        // get data socket
        try (Socket dataSoc = new Socket(theClient, session.getDataConnection().getPort()))
		{
			// create output stream
			try (OutputStream out = dataSoc.getOutputStream())
			{

				TransferRateRequest transferRateRequest = new TransferRateRequest();
				transferRateRequest = (TransferRateRequest) session.getUser().authorize(transferRateRequest);
				int maxRate = 0;
				if (transferRateRequest != null) {
					maxRate = transferRateRequest.getMaxDownloadRate();
				}

				String message = "";
				long result;
				System.out.println("!!!!RETR: About to transfer the file.!!!");
				result = transfer(session, true, in, out, maxRate);
				System.out.println("!!!! The File was Transferred.  Returning the result in RETR !!!");
				return result;
			}
        } catch (Exception ex){
             throw new IOException("RETR: " + ex.getMessage());
        }
    }

    /**
     * Transfer.
     * 
     * Pre-Conditions: N/A
     * Post-Conditions: N/A
     *
     * @param session the session
     * @param isWrite the is write
     * @param in the in
     * @param out the out
     * @param maxRate the max rate
     * @return the long
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private final long transfer(FtpIoSession session, boolean isWrite,
            final InputStream in, final OutputStream out, final int maxRate)
            throws IOException {
        long transferredSize = 0L;

        boolean isAscii = session.getDataType() == DataType.ASCII;
        long startTime = System.currentTimeMillis();
        byte[] buff = new byte[4096];

        System.out.println("RETR: transfer --- Entered. MaxRate = " + maxRate);
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = IoUtils.getBufferedInputStream(in);

            bos = IoUtils.getBufferedOutputStream(out);

            System.out.println("RETR: transfer --- the bis and bos variables have been set.");
            byte lastByte = 0;
            while (true) {

                // if current rate exceeds the max rate, sleep for 50ms
                // and again check the current transfer rate
                if (maxRate > 0) {

                    // prevent "divide by zero" exception
                    long interval = System.currentTimeMillis() - startTime;
                    if (interval == 0) {
                        interval = 1;
                    }

                    // check current rate
                    long currRate = (transferredSize * 1000L) / interval;
                    if (currRate > maxRate) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException ex) {
                            break;
                        }
                        continue;
                    }
                }

                // read data
                int count = bis.read(buff);
                System.out.println("RETR: transfer -- Read Data Count = " + count);
                if (count == -1) {
                    break;
                }


                // write data
                // if ascii, replace \n by \r\n
                if (isAscii) {
                    for (int i = 0; i < count; ++i) {
                        byte b = buff[i];
                        if (isWrite) {
                            if (b == '\n' && lastByte != '\r') {
                                bos.write('\r');
                            }

                            bos.write(b);
                        } else {
                            if (b == '\n') {
                                // for reads, we should always get \r\n
                                // so what we do here is to ignore \n bytes
                                // and on \r dump the system local line ending
                                // Some clients won't transform new lines into \r\n so we make sure we don't delete new lines
                                if (lastByte != '\r') {
                                    bos.write(EOL);
                                }
                            } else if (b == '\r') {
                                bos.write(EOL);
                            } else {
                                // not a line ending, just output
                                bos.write(b);
                            }
                        }
                        // store this byte so that we can compare it for line endings
                        lastByte = b;
                    }
                } else {
                    bos.write(buff, 0, count);
                }

                transferredSize += count;
                System.out.println("RETR: Transfer -- Size = " + transferredSize);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.warn("Exception during data transfer", e);
            throw e;
        } catch (RuntimeException e) {
            e.printStackTrace();
            LOG.warn("Exception during data transfer", e);
            throw e;
        } finally {
            if (bos != null) {
                bos.flush();
            }
        }

        return transferredSize;
    }
}
