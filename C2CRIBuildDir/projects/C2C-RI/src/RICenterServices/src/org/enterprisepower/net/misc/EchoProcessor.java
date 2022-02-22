package org.enterprisepower.net.misc;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.enterprisepower.io.IOUtils;
import org.enterprisepower.net.NetUtils;

/**
 * The Class EchoProcessor.
 *
 * @author TransCore ITS, LLC
 * Last Updated:  1/8/2014
 * @deprecated
 */
public class EchoProcessor implements Runnable {
	
	/** The Constant log. */
	private static final Log log = LogFactory.getLog(EchoProcessor.class);

	/** The is completed. */
	boolean isCompleted = false;

	/** The socket. */
	private Socket socket;

	/**
	 * Instantiates a new echo processor.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @param source the source
	 */
	public EchoProcessor(Socket source) {
		this.socket = source;
	}

	/**
	 * Process.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 */
	public void process() {
		Thread t = new Thread(this);
		t.start();

		if (log.isTraceEnabled()) {
			log.trace("started new process threads: " + this);
		}
	}

	/**
	 * Checks if is completed.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 *
	 * @return true, if is completed
	 */
	public boolean isCompleted() {
		return isCompleted;
	}

	/**
	 * Close.
	 * 
	 * Pre-Conditions: N/A
	 * Post-Conditions: N/A
	 */
	public void close() {
		if (log.isTraceEnabled())
			log.trace("close() called on " + this);
		if (socket.isClosed()) {
			if (log.isTraceEnabled())
				log.trace("socket already closed: " + socket);
		} else {
			if (log.isTraceEnabled())
				log.trace("closing socket: " + socket);
			NetUtils.close(socket);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.getClass().getName() + "(" + socket + ")";
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			IOUtils.copyStream(socket.getInputStream(), socket
					.getOutputStream(), false);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			close();
		}
	}

}
