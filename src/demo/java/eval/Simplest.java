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