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
package http;

import coyote.commons.network.http.HttpMessageException;
import coyote.commons.network.http.HttpRequest;
import coyote.commons.network.http.HttpResponse;


/**
 * Demonstrate the simplicity of the HTTP classes.
 */
public class SimpleGet {

  /**
   * Show the landing page of a website.
   * 
   * @param args ignored
   * 
   * @throws HttpMessageException 
   */
  public static void main( String[] args ) throws HttpMessageException {

    // Send a new request to the given URI
    HttpResponse response = new HttpRequest().send( "http://www.yahoo.com" );

    // Show the body of the response message as a string
    System.out.println( new String( response.getBody() ) );
  }

}
