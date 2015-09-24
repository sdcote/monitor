/*
 * $Id: RuleException.java,v 1.3 2004/03/01 16:27:28 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.monitor.rules;

/**
 * Models an exceptional event in processing of the Rule package.
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.3 $
 */
public class RuleException extends Exception {

  /**
   * Normal no-arg constructor
   */
  public RuleException() {
    super();
  }




  /**
   * Normal constructor with a single message
   *
   * @param message The exception message
   */
  public RuleException( String message ) {
    super( message );
  }




  /**
   * Constructor with a single message and a nested exception
   *
   * @param message The exception message
   * @param excptn The nested item
   */
  public RuleException( String message, Throwable excptn ) {
    super( message, excptn );
  }




  /**
   * Constructor with no message and a nested exception
   *
   * @param excptn The nested exception
   */
  public RuleException( Throwable excptn ) {
    super( excptn );
  }

}