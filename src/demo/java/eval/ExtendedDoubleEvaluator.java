package eval;

import java.util.Iterator;

import coyote.commons.eval.DoubleEvaluator;
import coyote.commons.eval.Function;
import coyote.commons.eval.Parameters;


/**
 * A subclass of DoubleEvaluator that supports SQRT function.
 */
public class ExtendedDoubleEvaluator extends DoubleEvaluator {
  /** Defines the new function (square root).*/
  private static final Function SQRT = new Function( "sqrt", 1 );
  private static final Parameters PARAMS;

  static {
    // Gets the default DoubleEvaluator's parameters
    PARAMS = DoubleEvaluator.getDefaultParameters();
    // add the new sqrt function to these parameters
    PARAMS.add( SQRT );
  }




  public static void main( final String[] args ) {
    // Test that all this stuff is ok
    final String expression = "sqrt(abs(-2))^2";
    System.out.println( expression + " = " + new ExtendedDoubleEvaluator().evaluate( expression ) );
  }




  public ExtendedDoubleEvaluator() {
    super( PARAMS );
  }




  /**
   * @see coyote.commons.eval.DoubleEvaluator#evaluate(coyote.commons.eval.Function, java.util.Iterator, java.lang.Object)
   */
  @Override
  protected Double evaluate( final Function function, final Iterator<Double> arguments, final Object evaluationContext ) {
    if ( function == SQRT ) {
      // Implements the new function
      return Math.sqrt( arguments.next() );
    } else {
      // If it's another function, pass it to DoubleEvaluator
      return super.evaluate( function, arguments, evaluationContext );
    }
  }

}
