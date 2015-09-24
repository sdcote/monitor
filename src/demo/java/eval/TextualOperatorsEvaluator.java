package eval;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import coyote.commons.eval.AbstractEvaluator;
import coyote.commons.eval.Operator;
import coyote.commons.eval.Parameters;


/**
 * 
 */
public class TextualOperatorsEvaluator extends AbstractEvaluator<Boolean> {
  /** The negate unary operator. */
  public final static Operator NEGATE = new Operator( "NOT", 1, Operator.Associativity.RIGHT, 3 );
  /** The logical AND operator. */
  private static final Operator AND = new Operator( "AND", 2, Operator.Associativity.LEFT, 2 );
  /** The logical OR operator. */
  public final static Operator OR = new Operator( "OR", 2, Operator.Associativity.LEFT, 1 );

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

    final Map<String, String> variableToValue = new HashMap<String, String>();
    variableToValue.put( "type", "PORT" );

    final AbstractEvaluator<Boolean> evaluator = new TextualOperatorsEvaluator();

    System.out.println( "type=PORT -> " + evaluator.evaluate( "type=PORT", variableToValue ) );
    System.out.println( "type=NORTH -> " + evaluator.evaluate( "type=NORTH", variableToValue ) );
    System.out.println( "type=PORT AND true -> " + evaluator.evaluate( "type=PORT AND true", variableToValue ) );
  }




  public TextualOperatorsEvaluator() {
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
   * @see coyote.commons.eval.AbstractEvaluator#tokenize(java.lang.String)
   */
  @Override
  protected Iterator<String> tokenize( final String expression ) {
    return Arrays.asList( expression.split( "\\s" ) ).iterator();
  }




  /**
   * @see coyote.commons.eval.AbstractEvaluator#toValue(java.lang.String, java.lang.Object)
   */
  @Override
  protected Boolean toValue( final String literal, final Object evaluationContext ) {
    final int index = literal.indexOf( '=' );
    if ( index >= 0 ) {
      final String variable = literal.substring( 0, index );
      final String value = literal.substring( index + 1 );
      return value.equals( ( (Map<String, String>)evaluationContext ).get( variable ) );
    } else {
      return Boolean.valueOf( literal );
    }
  }
}
