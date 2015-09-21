/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.network.socket.ssl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.Security;
import java.util.Random;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSocket;

import coyote.commons.network.socket.ISocketFactory;


/**
 * Class SSLSocketFactory
 * 
 * http://docs.oracle.com/javase/1.5.0/docs/guide/security/jsse/JSSERefGuide.html
 * 
 * Logging debug events: 
 * http://docs.oracle.com/javase/1.5.0/docs/guide/security/jsse/JSSERefGuide.html#Debug
 * 
 * java -Djavax.net.debug=all -Djavax.net.ssl.trustStore=trustStore ...
 * 
 * 
 * understand the difference between the keystore (in which you have the 
 * private key and cert you prove your own identity with) and the trust store 
 * (which determines who you trust) - and the fact that your own identity also 
 * has a 'chain' of trust to the root - which is separate from any chain to a 
 * root you need to figure out 'who' you trust.
 * 
 */
public class SSLSocketFactory implements ISocketFactory {

  static final String DEFAULT_PASSWORD = "Coyote";

  static final Random regularRandom = new Random();

  String home;

  String trustStore;

  String trustStorePassword;

  javax.net.ssl.SSLSocketFactory socketFactory;

  javax.net.ssl.SSLServerSocketFactory serverSocketFactory;




  /**
   * Default constructor
   *
   * @throws Exception
   */
  public SSLSocketFactory() throws Exception {
    try {
      initialize();
    } catch ( Exception exception ) {
      System.out.println( "SSL startup exception" );
      System.out.println( "  java.home = ".concat( String.valueOf( home ) ) );
      System.out.println( "  javax.net.ssl.trustStore = ".concat( String.valueOf( trustStore ) ) );
      System.out.println( "  javax.net.ssl.trustStorePassword = ".concat( String.valueOf( trustStorePassword ) ) );

      throw exception;
    }
  }




  /**
   * Initialize the SSL keystore and socket factories
   * 
   * @see coyote.commons.network.socket.ISocketFactory#initialize()
   */
  @Override
  public void initialize() throws Exception {

    Security.addProvider( new com.sun.net.ssl.internal.ssl.Provider() );

    // Get the socket factories for creating sockets
    socketFactory = (javax.net.ssl.SSLSocketFactory)javax.net.ssl.SSLSocketFactory.getDefault();
    serverSocketFactory = (javax.net.ssl.SSLServerSocketFactory)javax.net.ssl.SSLServerSocketFactory.getDefault();
  }




  /**
   * Returns a socket layered over an existing socket to a ServerSocket on the
   * named host, at the given port.
   *
   * <p>This method can be used when tunneling SSL through a proxy.</p>
   *
   * <p>The host and port refer to the logical destination server. This socket
   * is configured using the socket options established for this factory.</p>
   *
   * <p>This socket is configured using the socket options established for the
   * default factory.</p>
   *
   * @param socket the socket connection to wrap within the SSL socket
   * @param addr the IP address of the server to which we connect
   * @param port the port number on the given address
   * @param autoclose close the underlying socket when this socket is closed 
   *
   * @return Socket connected to the specified host and address using SSL
   *
   * @throws IOException if a connection could not be made
   */
  public Socket createSocket( Socket socket, String host, int port, boolean autoclose ) throws IOException {
    SSLSocket sslsocket = (SSLSocket)socketFactory.createSocket( socket, host, port, autoclose );
    sslsocket.addHandshakeCompletedListener( new MyHandshakeListener() );
    sslsocket.setUseClientMode( true );
    sslsocket.startHandshake();
    return sslsocket;
  }




  /**
   * Returns a SSL socket connected to a ServerSocket at the specified network
   * address and port.
   *
   * <p>This socket is configured using the socket options established for the
   * default factory.</p>
   *
   * @param addr the IP address of the server to which we connect
   * @param port the port number on the given address
   *
   * @return Socket connected to the specified host and address
   *
   * @throws IOException if a connection could not be made
   */
  public Socket createSocket( InetAddress addr, int port ) throws IOException {
    SSLSocket sslsocket = (SSLSocket)socketFactory.createSocket( addr, port );
    sslsocket.addHandshakeCompletedListener( new MyHandshakeListener() );
    sslsocket.setUseClientMode( true );
    sslsocket.startHandshake();
    return sslsocket;
  }




  /**
   * Returns a SSL server socket which uses all network interfaces on this host,
   * is bound to a the specified port, and uses the specified connection backlog.
   *
   * <p>The socket is configured with the default socket options (such as accept
   * timeout)</p>
   *
   * @param port - the port to which we listen
   * @param backlog - how many connections are queued
   *
   * @return Socket on which we can listen()
   *
   * @throws IOException if the socket could not be created
   */
  public ServerSocket createServerSocket( int port, int backlog ) throws IOException {
    return serverSocketFactory.createServerSocket( port, backlog );
  }




  /**
   * Returns a SSL server socket which uses the given network interface on this
   * host, is bound to a the specified port, and uses the specified connection
   * backlog.
   *
   * <p>The socket is configured with the default socket options (such as accept
   * timeout)</p>
   *
   * @param port - the port to which we listen
   * @param backlog - how many connections are queued
   * @param addr - the InetAddress to which the socket is bound
   *
   * @return Socket on which we can listen()
   *
   * @throws IOException if the socket could not be created
   */
  public ServerSocket createServerSocket( int port, int backlog, InetAddress addr ) throws IOException {
    return serverSocketFactory.createServerSocket( port, backlog, addr );
  }

  /**
   * 
   */
  class MyHandshakeListener implements HandshakeCompletedListener {
    public void handshakeCompleted( HandshakeCompletedEvent e ) {
      System.out.println( "SSL Handshake succesful!" );
      System.out.println( "SSL Session: " + e.getSession() );
    }
  }

}