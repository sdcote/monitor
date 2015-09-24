package eval;

import coyote.commons.eval.DoubleEvaluator;


/**
 * 
 */
public class Simplest {

  public static void main( final String[] args ) {
    // Create a new evaluator
    final DoubleEvaluator evaluator = new DoubleEvaluator();

    // create an expression
    final String expression = "(2^3-1)*sin(pi/4)/ln(pi^2)";

    // Evaluate that expression
    final Double result = evaluator.evaluate( expression );

    // Show the result
    System.out.println( expression + " = " + result );
  }
}