/*
 * $Id: RuleExpressionException.java,v 1.3 2004/03/01 16:27:41 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.monitor.rules;

/**
 * Models an exceptional event in processing of the Rule expressions.
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.3 $
 */
public class RuleExpressionException extends RuleException {

  /**
   * Normal no-arg constructor
   */
  public RuleExpressionException() {
    super();
  }




  /**
   * Normal constructor with a single message
   *
   * @param message The exception message
   */
  public RuleExpressionException( String message ) {
    super( message );
  }




  /**
   * Constructor with a single message and a nested exception
   *
   * @param message The exception message
   * @param excptn The nested item
   */
  public RuleExpressionException( String message, Throwable excptn ) {
    super( message, excptn );
  }




  /**
   * Constructor with no message and a nested exception
   *
   * @param excptn The nested exception
   */
  public RuleExpressionException( Throwable excptn ) {
    super( excptn );
  }

}