package org.enterprisepower.net.misc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.enterprisepower.net.NetUtils;

import java.net.*;
import java.io.*;

/**
 * The Class EchoServer.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 * @deprecated
 */
class EchoServer implements Runnable {
	
	/** The log. */
	private static Log log = LogFactory.getLog(EchoServer.class);
	
	/** The server socket. */
	private ServerSocket serverSocket;
	
	/** The from. */
	private InetSocketAddress from;
	
	/** The exception. */
	private Throwable exception;

	// private Cleaner cleaner = new Cleaner();

	/**
	 * Instantiates a new echo server.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param from the from
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public EchoServer(InetSocketAddress from) throws IOException {
		this.from = from;
		serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(from);
		String hostname = from.getHostName();
		if (hostname == null)
			hostname = "*";
		log.info("Ready to accept client connection on " + hostname + ":"
				+ from.getPort());
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		Socket source = null;
		// new Thread(cleaner).start();
		while (true) {
			try {
				source = serverSocket.accept();
				log.trace("accepted client connection");
				new EchoProcessor(source).process();
			} catch (IOException e) {
				String msg = "Failed to accept client connection on port "
						+ from.getPort();
				log.error(msg, e);
				exception = e;
				return;
			}
		}
	}

	/**
	 * Close.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 */
	public void close() {
		if (!serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
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
	 * @throws Throwable the throwable
	 */
	public static void main(String[] args) throws Throwable {
		EchoServer server = new EchoServer(NetUtils
				.parseInetSocketAddress(args[0]));
		server.run();
		if (server.exception != null)
			throw server.exception;
	}
}