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

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.Random;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;

import com.sun.net.ssl.internal.ssl.Provider;

import coyote.commons.network.socket.ISocketFactory;
import coyote.loader.log.Log;


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

  static final String DEFAULT_PASSWORD = "changeit";

  static final Random regularRandom = new Random();

  javax.net.ssl.SSLSocketFactory socketFactory;

  javax.net.ssl.SSLServerSocketFactory serverSocketFactory;




  /**
   * Default constructor
   *
   * @throws Exception
   */
  public SSLSocketFactory() throws Exception {

  }




  /**
   * Initialize the SSL keystore and socket factories.
   * 
   * <p>If there is a truststore specified in the {@code javax.net.ssl.trustStore} 
   * system property use that truststore and a X509 Trust Manager. If not, use 
   * a default "Trust-All" trust manager and accept any certificate sent by the
   * server. FYI: the truststore is usually found in 
   * {@code JAVA_HOME/lib/security/cacerts}.</p>
   * 
   * <p>In order to use the truststore, a truststore passphrase needs to be 
   * specified in the {@code javax.net.ssl.trustStorePassword} system property.
   * FYI: the default passphrase is usually {@code changeit}.</p>
   * 
   * @see coyote.commons.network.socket.ISocketFactory#initialize()
   */
  @Override
  public void initialize() throws Exception {

    // What JRE are we using?
    String home = System.getProperty( "java.home" );

    // Determine a trust manager using the requested keystore
    String trustStore = System.getProperty( "javax.net.ssl.trustStore" );

    // What is the password used to create the trust store
    String trustStorePassword = System.getProperty( "javax.net.ssl.trustStorePassword" );

    try {

      Security.addProvider( new Provider() );
      SecureRandom securerandom = new SecureRandom();
      securerandom.setSeed( regularRandom.nextLong() );
      SSLContext sslcontext = SSLContext.getInstance( "SSL" );

      // if there is a trust store defined, use it
      if ( trustStore != null ) {
        Log.trace( "Using a truststore of " + trustStore );

        // if none specified, use a default
        if ( trustStorePassword == null ) {
          trustStorePassword = DEFAULT_PASSWORD;
        }
        Log.trace( "Using a passphrase of " + trustStorePassword );

        // Setup an X509 key manager
        char tspChars[] = trustStorePassword.toCharArray();
        KeyManagerFactory keymanagerfactory = KeyManagerFactory.getInstance( "SunX509" );
        KeyStore keystore = KeyStore.getInstance( "JKS" );
        keystore.load( new FileInputStream( trustStore ), tspChars );
        keymanagerfactory.init( keystore, tspChars );
        sslcontext.init( keymanagerfactory.getKeyManagers(), null, securerandom );
      } else {
        Log.trace( "No truststore specified in system properties, using a trust-all manager" );

        // Just use a trust-all manager until we design an easy to use strategy
        sslcontext.init( null, new TrustManager[] { new TrustAllManager() }, null );
      }

      // Get the socket factories for client and server sockets 
      socketFactory = sslcontext.getSocketFactory();
      serverSocketFactory = sslcontext.getServerSocketFactory();

    } catch ( Exception exception ) {
      System.out.println( "SSL startup exception" );
      System.out.println( "  java.home = ".concat( String.valueOf( home ) ) );
      System.out.println( "  javax.net.ssl.trustStore = ".concat( String.valueOf( trustStore ) ) );
      System.out.println( "  javax.net.ssl.trustStorePassword = ".concat( String.valueOf( trustStorePassword ) ) );

      throw exception;
    }
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
      if ( Log.isLogging( Log.TRACE_EVENTS ) ) {
        Log.trace( "SSL handshake complete - session: " + e.getSession() );
        Log.trace( "Using cipher suite: " + e.getCipherSuite() );
        if ( e.getLocalPrincipal() != null ) {
          Log.trace( "Local Principal: " + e.getLocalPrincipal() );
        }
        try {
          if ( e.getPeerPrincipal() != null )
            Log.trace( "Peer Principal: " + e.getPeerPrincipal() );
        } catch ( SSLPeerUnverifiedException ignore ) {}
        Certificate[] certs = e.getLocalCertificates();
        if ( certs != null ) {
          Log.trace( "Handshake returned " + certs.length + " local certs" );
          for ( int i = 0; i < certs.length; i++ ) {
            Certificate cert = certs[i];
            Log.trace( "cert: " + cert.toString() );
          }
        } else {
          Log.trace( "Handshake returned no local certs" );
        }
      }
    }
  }

}