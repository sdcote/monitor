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
package coyote.commons.network.http;

import coyote.commons.ByteUtil;
import coyote.commons.StringUtil;


/**
 * When the user agent wants to send the server authentication credentials it 
 * may use the Authorization field.
 * 
 * The Authorization field is constructed as follows:
 * <li>Username and password are combined into a string "username:password". 
 * Note that username cannot contain the ":" character.</li>
 * <li>The resulting string is then encoded using the RFC2045-MIME variant of 
 * Base64, except not limited to 76 char/line</li>
 * <li>The authorization method and a space i.e. "Basic " is then put before 
 * the encoded string.</li>
 * 
 * <p>For example, if the user agent uses 'Aladdin' as the username and 
 * 'open sesame' as the password then the field is formed as follows:<pre>
 * Authorization: Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==</pre></p> 
 * 
 * <p>This is designed to be added to the headers of the request in the following manner:<pre>
 * request.setHeader( BasicAuth.HEADER_NAME, new BasicAuth( username, password ).toString() );</pre>
 * The result should be an "Authorization" header populated with the necessary
 * values.</p>
 */
public class BasicAuth {
  private static final String BASIC = "Basic";

  public static final String HEADER_NAME = "Authorization";
  private String username = null;
  private String password = null;




  public BasicAuth( String user, String pass ) {
    setUsername( user );
    setPassword( pass );
  }




  /**
   * @return the username
   */
  public String getUsername() {
    return username;
  }




  /**
   * @param user the username to set
   */
  public void setUsername( String user ) {
    this.username = user;
  }




  /**
   * @return the password
   */
  public String getPassword() {
    return password;
  }




  /**
   * @param pass the password to set
   */
  public void setPassword( String pass ) {
    this.password = pass;
  }




  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuffer b = new StringBuffer();
    if ( StringUtil.isNotBlank( username ) || StringUtil.isNotBlank( password ) ) {

      if ( StringUtil.isNotBlank( username ) ) {
        b.append( username );
      }
      b.append( ":" );

      if ( StringUtil.isNotBlank( password ) ) {
        b.append( password );
      }

    }
    return BASIC + " " + ByteUtil.toBase64( StringUtil.getBytes( b.toString() ) );
  }

}
