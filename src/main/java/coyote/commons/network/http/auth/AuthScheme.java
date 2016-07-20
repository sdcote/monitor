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
package coyote.commons.network.http.auth;

import coyote.commons.network.http.HttpMessage;


/**
 * This interface represents an abstract challenge-response oriented 
 * authentication scheme.
 * 
 * <p>An authentication scheme should be able to support the following functions:
 * <ul><li>Parse and process the challenge sent by the target server in response to request for a protected resource
 *   <li>Provide its textual designation
 *   <li>Provide its parameters, if available
 *   <li>Provide the realm this authentication scheme is applicable to, if available
 *   <li>Generate authorization string for the given set of credentials and the HTTP request in response to the authorization challenge.
 * </ul>
 * <p>Authentication schemes may be stateful involving a series of challenge-response exchanges.
 */

public interface AuthScheme {

  /**
   * Processes the given challenge token. Some authentication schemes
   * may involve multiple challenge-response exchanges. Such schemes must be able
   * to maintain the state information when dealing with sequential challenges
   *
   * @param message the message with the challenge header
   */
  void processChallenge( final HttpMessage message ) throws MalformedChallengeException;




  /**
   * Returns textual designation of the given authentication scheme.
   *
   * @return the name of the given authentication scheme
   */
  String getSchemeName();




  /**
   * Returns authentication parameter with the given name, if available.
   *
   * @param name The name of the parameter to be returned
   *
   * @return the parameter with the given name
   */
  String getParameter( final String name );




  /**
   * Returns authentication realm. If the concept of an authentication
   * realm is not applicable to the given authentication scheme, returns
   * <code>null</code>.
   *
   * @return the authentication realm
   */
  String getRealm();




  /**
   * Tests if the authentication scheme is provides authorization on a per
   * connection basis instead of usual per request basis
   *
   * @return <tt>true</tt> if the scheme is connection based, <tt>false</tt>
   * if the scheme is request based.
   */
  boolean isConnectionBased();




  /**
   * Authentication process may involve a series of challenge-response exchanges.
   * This method tests if the authorization process has been completed, either
   * successfully or unsuccessfully, that is, all the required authorization
   * challenges have been processed in their entirety.
   *
   * @return <tt>true</tt> if the authentication process has been completed,
   * <tt>false</tt> otherwise.
   */
  boolean isComplete();

}
