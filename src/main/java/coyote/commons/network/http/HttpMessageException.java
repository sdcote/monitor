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
package coyote.commons.network.http;

/**
 * Exception thrown when there is a problem with HttpMessage processing.
 */
public final class HttpMessageException extends Exception {

  /**
   * Constructor
   */
  public HttpMessageException() {
    super();
  }




  /**
   * Constructor
   * 
   * @param message Error message
   */
  public HttpMessageException( String message ) {
    super( message );
  }




  /**
   * Constructor
   * 
   * @param message Error message
   * @param excptn
   */
  public HttpMessageException( String message, Throwable excptn ) {
    super( message, excptn );
  }




  /**
   * Constructor
   * 
   * @param excptn
   */
  public HttpMessageException( Throwable excptn ) {
    super( excptn );
  }
}