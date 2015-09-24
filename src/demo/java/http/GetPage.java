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

import java.io.UnsupportedEncodingException;

import coyote.commons.network.http.HttpMessage;
import coyote.commons.network.http.HttpMessageException;
import coyote.commons.network.http.HttpRequest;
import coyote.commons.network.http.HttpResponse;
import coyote.loader.log.ConsoleAppender;
import coyote.loader.log.Log;


/**
 * Demonstrate the ability to configure the HTTP classes.
 */
public class GetPage {

  /**
   * @param args
   * @throws HttpMessageException 
   * @throws UnsupportedEncodingException 
   */
  public static void main( String[] args ) throws Exception {

    // Replace the default logger (NullAppender) with the console logger
    Log.addLogger( Log.DEFAULT_LOGGER_NAME, new ConsoleAppender( Log.TRACE_EVENTS | Log.DEBUG_EVENTS | Log.INFO_EVENTS | Log.WARN_EVENTS | Log.ERROR_EVENTS | Log.FATAL_EVENTS ) );
    //Log.startLogging( "HTTP" );
    Log.setMask( -1 ); // trick to start logging everything


    HttpRequest request = new HttpRequest();

    // We can set the method, of course
    request.setRequestMethod( HttpMessage.GET );

    // we can set all manner of header values here
    // request.addHeader( "X-MyCustomHeader", "Some_Value" );
    request.setHeader( HttpMessage.CONNECTION, HttpMessage.CLOSE );

    // we can set the body to anything we want here
    // request.setBody( "{\"message\":\"Hello World\"}".getBytes( "ASCII" ) );
    
    // We can set other aspects such as time-out
    request.setTimeout( 10000 );

    // Send the request to the given URI
    HttpResponse response = request.send( "https://www.yahoo.com" );

    // Show the body of the message as a string
    System.out.println( new String( response.getBody() ) );

  }

}
