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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

import coyote.loader.log.Log;


/**
 * 
 */
public class TrustAllManager implements X509TrustManager {
  private static final X509Certificate[] NO_CERTS = new X509Certificate[0];




  /**
   * @see javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.X509Certificate[], java.lang.String)
   */
  @Override
  public void checkClientTrusted( X509Certificate[] chain, String authType ) throws CertificateException {
    if ( Log.isLogging( Log.TRACE_EVENTS ) ) {
      Log.trace( "Checking client - authentication type: " + authType );
      for ( int x = 0; x < chain.length; x++ ) {
        Log.trace( chain[x].getSubjectX500Principal() );
      }
    }
  }




  /**
   * @see javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.X509Certificate[], java.lang.String)
   */
  @Override
  public void checkServerTrusted( X509Certificate[] chain, String authType ) throws CertificateException {
    if ( Log.isLogging( Log.TRACE_EVENTS ) ) {
      Log.trace( "Checking server - authentication type: " + authType );
      for ( int x = 0; x < chain.length; x++ ) {
        Log.trace( chain[x].getSubjectX500Principal() );
      }
    }
  }




  /**
   * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
   */
  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return NO_CERTS;
  }

}
