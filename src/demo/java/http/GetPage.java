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

import coyote.commons.network.http.HttpMessage;
import coyote.commons.network.http.HttpMessageException;
import coyote.commons.network.http.HttpRequest;
import coyote.commons.network.http.HttpResponse;


/**
 * 
 */
public class GetPage {

  /**
   * @param args
   * @throws HttpMessageException 
   */
  public static void main( String[] args ) throws HttpMessageException {

    HttpRequest request = new HttpRequest();

    HttpResponse response = request.send( "https://www.gitbub.com" );

    System.out.println( new String(response.getBody()) );

  }

}
