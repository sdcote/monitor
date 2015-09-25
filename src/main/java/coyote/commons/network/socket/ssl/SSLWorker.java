/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import javax.net.ssl.SSLSocket;


/**
 * Encrypted sockets are a little strange in that they can't support the 
 * {@code available()} on their input streams without decrypting the stream to 
 * determine what is available. This will cause most code to block and make 
 * threading difficult.
 * 
 * <p>This class gets around that limitation by running a socket reader which 
 * blocks on the input stream and places any retrieved data on a piped input 
 * stream which does support {@code available()} and allows existing code to 
 * work without blocking.</p>  
 */
public class SSLWorker implements Runnable {

  final InputStreamReader reader;

  final PipedOutputStream output;
  final PipedInputStream input;

  private volatile boolean open = true;




  public SSLWorker( SSLSocket sslsocket ) throws IOException {
    output = new PipedOutputStream();
    input = new PipedInputStream( output );
    reader = new InputStreamReader( sslsocket.getInputStream() );

    // Start this object running in a separate thread
    new Thread( this ).start();
  }




  public InputStream getInputStream() {
    return input;
  }




  @Override
  public void run() {
    while ( open ) {
      try {
        int data = reader.read();
        while ( data != -1 ) {
          output.write( data );
          data = reader.read();
        }
        open = false;
      } catch ( IOException e ) {
        open = false;
      }
    }
    close();
  }




  public void close() {
    try {
      reader.close();
    } catch ( IOException ignore ) {}
    try {
      input.close();
    } catch ( IOException ignore ) {}
    try {
      output.close();
    } catch ( IOException ignore ) {}
  }




  public synchronized boolean isOpen() {
    return open;
  }

}