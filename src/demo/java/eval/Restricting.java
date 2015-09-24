package eval;

import coyote.commons.eval.BracketPair;
import coyote.commons.eval.DoubleEvaluator;
import coyote.commons.eval.Parameters;


/**
 * An example of how to restrict operators, functions and constants of an 
 * existing evaluator.
 */
public class Restricting {

  private static void doIt( final DoubleEvaluator evaluator, final String expression ) {
    try {
      System.out.println( expression + " = " + evaluator.evaluate( expression ) );
    } catch ( final IllegalArgumentException e ) {
      System.out.println( expression + " is an invalid expression" );
    }
  }




  public static void main( final String[] args ) {
    // Let's create a double evaluator that only support +,-,*,and / operators, with no constants,
    // and no functions. The default parenthesis will be allowed
    // First create empty evaluator parameters
    final Parameters params = new Parameters();
    // Add the supported operators to these parameters
    params.add( DoubleEvaluator.PLUS );
    params.add( DoubleEvaluator.MINUS );
    params.add( DoubleEvaluator.MULTIPLY );
    params.add( DoubleEvaluator.DIVIDE );
    params.add( DoubleEvaluator.NEGATE );
    // Add the default expression brackets
    params.addExpressionBracket( BracketPair.PARENTHESES );
    // Create the restricted evaluator
    final DoubleEvaluator evaluator = new DoubleEvaluator( params );

    // Let's try some expressions
    doIt( evaluator, "(3*-4)+2" );
    doIt( evaluator, "3^2" );
    doIt( evaluator, "ln(5)" );
  }
}