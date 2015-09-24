package eval;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import coyote.commons.eval.DoubleEvaluator;
import coyote.commons.eval.Parameters;


/**
 * An example of how to localize an existing evaluator to match French locale.
 * 
 * <p>Some French speakers may prefer "moyenne" to "avg" and "somme" to 
 * "sum".</p>
 * 
 * <p>As the default argument function (',') is used as decimal separator in 
 * France, it may be changed to ';'.</p>
 */
public class LocalizedEvaluator extends DoubleEvaluator {
  /** Defines the new function (square root).*/
  private static final Parameters PARAMS;

  static {
    // Gets the default DoubleEvaluator's parameters
    PARAMS = DoubleEvaluator.getDefaultParameters();
    // adds the translations
    PARAMS.setTranslation( DoubleEvaluator.SUM, "somme" );
    PARAMS.setTranslation( DoubleEvaluator.AVERAGE, "moyenne" );
    // Change the default function separator
    PARAMS.setFunctionArgumentSeparator( ';' );
  }




  public static void main( final String[] args ) {
    // Test that all this stuff is ok
    final LocalizedEvaluator evaluator = new LocalizedEvaluator();
    final String expression = "3 000 +moyenne(3 ; somme(1,5 ; 7 ; -3,5))";
    System.out.println( expression + " = " + evaluator.format.format( evaluator.evaluate( expression ) ) );
  }

  private final DecimalFormat format;




  public LocalizedEvaluator() {
    super( PARAMS );
    // Create a French number formatter
    format = (DecimalFormat)NumberFormat.getInstance( Locale.FRENCH );
    format.setGroupingUsed( true );
  }




  /**
   * @see coyote.commons.eval.DoubleEvaluator#toValue(java.lang.String, java.lang.Object)
   */
  @Override
  protected Double toValue( String literal, final Object evaluationContext ) {
    // Override the method that converts a literal to a number, in order to match with
    // the French decimal separator
    try {
      // For a strange reason, Java thinks that only non breaking spaces are French thousands
      // separators. So, we will replace spaces in the literal by non breaking spaces
      literal = literal.replace( ' ', (char)0x00A0 );
      return format.parse( literal ).doubleValue();
    } catch ( final ParseException e ) {
      // If the number has a wrong format, throw the right exception.
      throw new IllegalArgumentException( literal + " is not a number" );
    }
  }
}
