package eval;

import java.util.Iterator;

import coyote.commons.eval.AbstractEvaluator;
import coyote.commons.eval.Operator;
import coyote.commons.eval.Parameters;


/**
 * An example of how to implement an evaluator from scratch.
 */
public class SimpleBooleanEvaluator extends AbstractEvaluator<Boolean> {
  /** The negate unary operator.*/
  public final static Operator NEGATE = new Operator( "!", 1, Operator.Associativity.RIGHT, 3 );
  /** The logical AND operator.*/
  private static final Operator AND = new Operator( "&&", 2, Operator.Associativity.LEFT, 2 );
  /** The logical OR operator.*/
  public final static Operator OR = new Operator( "||", 2, Operator.Associativity.LEFT, 1 );

  private static final Parameters PARAMETERS;

  static {
    // Create the evaluator's parameters
    PARAMETERS = new Parameters();
    // Add the supported operators
    PARAMETERS.add( AND );
    PARAMETERS.add( OR );
    PARAMETERS.add( NEGATE );
  }




  public static void main( final String[] args ) {
    final SimpleBooleanEvaluator evaluator = new SimpleBooleanEvaluator();
    String expression = "true && false";
    System.out.println( expression + " = " + evaluator.evaluate( expression ) );
    expression = "true || false";
    System.out.println( expression + " = " + evaluator.evaluate( expression ) );
    expression = "!true";
    System.out.println( expression + " = " + evaluator.evaluate( expression ) );
  }




  public SimpleBooleanEvaluator() {
    super( PARAMETERS );
  }




  /**
   * @see coyote.commons.eval.AbstractEvaluator#evaluate(coyote.commons.eval.Operator, java.util.Iterator, java.lang.Object)
   */
  @Override
  protected Boolean evaluate( final Operator operator, final Iterator<Boolean> operands, final Object evaluationContext ) {
    if ( operator == NEGATE ) {
      return !operands.next();
    } else if ( operator == OR ) {
      final Boolean o1 = operands.next();
      final Boolean o2 = operands.next();
      return o1 || o2;
    } else if ( operator == AND ) {
      final Boolean o1 = operands.next();
      final Boolean o2 = operands.next();
      return o1 && o2;
    } else {
      return super.evaluate( operator, operands, evaluationContext );
    }
  }




  /**
   * @see coyote.commons.eval.AbstractEvaluator#toValue(java.lang.String, java.lang.Object)
   */
  @Override
  protected Boolean toValue( final String literal, final Object evaluationContext ) {
    return Boolean.valueOf( literal );
  }
}
