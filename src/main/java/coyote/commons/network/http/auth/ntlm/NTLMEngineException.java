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
package coyote.commons.network.http.auth.ntlm;

/**
 * 
 */
public class NTLMEngineException extends Exception {
  private static final long serialVersionUID = -4628053879181947054L;




  public NTLMEngineException() {
    super();
  }




  /**
   * Creates a new NTLMEngineException with the specified message.
   *
   * @param message the exception detail message
   */
  public NTLMEngineException( final String message ) {
    super( message );
  }




  /**
   * Creates a new NTLMEngineException with the specified detail message and cause.
   *
   * @param message the exception detail message
   * 
   * @param cause the {@code Throwable} that caused this exception, or {@code null} if the cause is unavailable, unknown, or not a {@code Throwable}
   */
  public NTLMEngineException( final String message, final Throwable cause ) {
    super( message, cause );
  }
}
