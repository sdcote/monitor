package eval;

import coyote.commons.eval.DoubleEvaluator;
import coyote.commons.eval.StaticVariableSet;


/**
 * 
 */
public class Variables {

  /**
   * @param args
   */
  public static void main( String[] args ) {
    final String expression = "sin(x)"; // Here is the expression to evaluate
    // Create the evaluator
    final DoubleEvaluator eval = new DoubleEvaluator();
    // Create a new empty variable set
    final StaticVariableSet<Double> variables = new StaticVariableSet<Double>();
    double x = 0;
    final double step = Math.PI / 8;
    while ( x <= Math.PI / 2 ) {
      // Set the value of x
      variables.set( "x", x );
      // Evaluate the expression
      Double result = eval.evaluate( expression, variables );
      // Ouput the result
      System.out.println( "x=" + x + " -> " + expression + " = " + result );
      x += step;
    }
  }
}
