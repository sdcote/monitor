/*
 * $Id: HttpMessageException.java,v 1.3 2004/04/16 16:49:38 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
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