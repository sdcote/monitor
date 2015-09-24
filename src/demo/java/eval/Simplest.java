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
package eval;

import coyote.commons.eval.DoubleEvaluator;


/**
 * 
 */
public class Simplest {
  public static void main( String[] args ) {
    // Create a new evaluator
    DoubleEvaluator evaluator = new DoubleEvaluator();
    String expression = "(2^3-1)*sin(pi/4)/ln(pi^2)";
    // Evaluate an expression
    Double result = evaluator.evaluate( expression );
    // Ouput the result
    System.out.println( expression + " = " + result );
  }
}