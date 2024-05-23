/*
 */
package test.net;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketImpl;
import java.nio.ByteBuffer;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/*
 *
 * @author TransCore ITS
 */

class C2CRISocketImpl extends java.net.SocketImpl
{

  private final Delegator delegator;

  public C2CRISocketImpl() throws IOException {
    this.delegator = new Delegator(this, SocketImpl.class,
        "java.net.SocksSocketImpl");
  }

  private Socket getRealSocket() throws IOException {
    try {
       System.out.println("getSocket()");
       Field socket = SocketImpl.class.getDeclaredField("socket");
       socket.setAccessible(true);
       return (Socket) socket.get(this);
    } catch (Exception e) {
      throw new IOException("Could not discover real socket");
    }
  }

  

  public InputStream getInputStream() throws IOException {
    InputStream real = delegator.invoke();
    return new LoggingInputStream(real);
  }

  public OutputStream getOutputStream() throws IOException {
    OutputStream real = delegator.invoke();
    return new LoggingOutputStream(real);
  }

  // the rest of the class is plain delegation to real SocketImpl
  public void create(boolean stream) throws IOException {
    delegator.invoke(stream);
  }

  public void connect(String host, int port)
      throws IOException {
    System.out.println("connect(String host, int port)");
    delegator.invoke(host, port);
  }

  // We specify the exact method to delegate to.  Not actually
  // necessary here, but just to show how you would do it.
  public void connect(InetAddress address, int port)
      throws IOException {
    System.out.println("connect(InetAddress address, int port)");
    delegator
        .delegateTo("connect", InetAddress.class, int.class)
        .invoke(address, port);
  }

  public void connect(SocketAddress address, int timeout)
      throws IOException {
      System.out.println("connect(SocketAddress address, int timeout)");
    delegator.invoke(address, timeout);
  }

  public void bind(InetAddress host, int port)
      throws IOException {
    delegator.invoke(host, port);
    System.out.println("bind(InetAddress host, int port) result = " +getRealSocket());
  }

  public void listen(int backlog) throws IOException {
    delegator.invoke(backlog);
    System.out.println("listen(int backlog) result = " + getRealSocket());
  }

  
  protected void accept(java.net.SocketImpl s) throws IOException {
//      if (s instanceof C2CRISocketImpl){
//          socketAccept(s);
//      } else {
          delegator.invoke(s);         
//  }
    
  }

  public int available() throws IOException {
    Integer result = delegator.invoke();
    return result;
  }

  public void close() throws IOException {
    delegator.invoke();
  }

  public void shutdownInput() throws IOException {
    delegator.invoke();
  }

  public void shutdownOutput() throws IOException {
    delegator.invoke();
  }

  public FileDescriptor getFileDescriptor() {
    return delegator.invoke();
  }

  public InetAddress getInetAddress() {
    return delegator.invoke();
  }

  public int getPort() {
    Integer result = delegator.invoke();
    return result;
  }

  public boolean supportsUrgentData() {
    Boolean result = delegator.invoke();
    return result;
  }

  public void sendUrgentData(int data) throws IOException {
    delegator.invoke(data);
  }

  public int getLocalPort() {
    Integer result = delegator.invoke();
    return result;
  }

  public String toString() {
    return delegator.invoke();
  }

  @Override
  public void setPerformancePreferences(int connectionTime,
                                        int latency,
                                        int bandwidth) {
    delegator.invoke(connectionTime, latency, bandwidth);
  }

  @Override
  public void setOption(int optID, Object value)
      throws SocketException {
    delegator.invoke(optID, value);
  }

  @Override
  public Object getOption(int optID) throws SocketException {
      System.out.println("The OptID = "+optID);
    return delegator.invoke(optID);
  }
    
    
    
    
//    
//    @Override
//    protected synchronized InputStream getInputStream() throws IOException {
//        return new LoggingInputStream(super.getInputStream());             
//    }
//
//    @Override
//    protected synchronized OutputStream getOutputStream() throws IOException {
//        return new LoggingOutputStream(super.getOutputStream());
//    }
//
     private static class LoggingOutputStream extends OutputStream {

        private static final Logger logger = LogManager.getLogger(C2CRISocketImpl.LoggingOutputStream.class);
        //I'm using a fixed charset because my app always uses the same. 
        private static final String CHARSET = "ISO-8859-1";
        private OutputStream sktOutputStream;
        
        public LoggingOutputStream(OutputStream out) {
            sktOutputStream = out;
        }

        @Override
        public void write(byte[] b, int off, int len)
                throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append(new String(b, off, len, CHARSET));
            logger.info("\n" + sb.toString());
            System.out.println("Write\n" + sb.toString());
            sktOutputStream.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append(b);
            logger.info("\n" + sb.toString());
            System.out.println("Write\n" + sb.toString());
            sktOutputStream.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            StringBuilder sb = new StringBuilder();
            sb.append(b);
            logger.info("\n" + sb.toString());
            System.out.println("Write\n" + sb.toString());
            sktOutputStream.write(b);
        }

        @Override
        public void close() throws IOException {
            sktOutputStream.close();
        }


        @Override
        public void flush() throws IOException {
            sktOutputStream.flush();
        }       
        
    }

    private static class LoggingInputStream extends InputStream {

        private static final Logger logger = LogManager.getLogger(C2CRISocketImpl.LoggingInputStream.class);
        //I'm using a fixed charset because my app always uses the same. 
        private static final String CHARSET = "ISO-8859-1";
        private InputStream sktInputStream;
        
        public LoggingInputStream(InputStream in) {
            sktInputStream = in;
        }

        @Override
        public int read() throws IOException {
            
            int count = sktInputStream.read();
            StringBuilder sb = new StringBuilder();
            sb.append(new String(ByteBuffer.allocate(1).putInt(count).array()));
            logger.info("\n" + sb.toString());
            System.out.println("Read\n" + sb.toString());
            return count; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int read(byte[] b) throws IOException {
            int count = sktInputStream.read(b);
            StringBuilder sb = new StringBuilder();
            sb.append(new String(b));
            logger.info("\n" + sb.toString());
            System.out.println("Read\n" + sb.toString());
            return count;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int count = sktInputStream.read(b, off, len);
            StringBuilder sb = new StringBuilder();
            sb.append(new String(b, off, len, CHARSET));
            logger.info("\n" + sb.toString());
            System.out.println("Read\n" + sb.toString());
            return count;
        }

        @Override
        public long skip(long n) throws IOException {
            return sktInputStream.skip(n);
        }

        @Override
        public int available() throws IOException {
            return sktInputStream.available();
        }

        @Override
        public void close() throws IOException {
            sktInputStream.close();            
        }

        @Override
        public synchronized void mark(int readlimit) {
            sktInputStream.mark(readlimit);
        }

        @Override
        public synchronized void reset() throws IOException {
            sktInputStream.reset();
        }

        @Override
        public boolean markSupported() {
            return sktInputStream.markSupported();
        }        
        
    }
   
}
