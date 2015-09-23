package coyote.commons.eval;

/**
 * A constant in an expression.
 * 
 * <p>Some expressions needs constants. For instance it is impossible to 
 * perform trigonometric calculus without using pi. A constant allows you to 
 * use mnemonic in your expressions instead of the raw value of the constant.</p>
 * 
 * <p>A constant for pi would be defined by :<pre>
 * Constant<Double> pi = new Constant<Double>("pi");</pre>
 * 
 * <p>With such a constant, you will be able to evaluate the expression 
 * "sin(pi/4)".</p>
 * 
 * @see AbstractEvaluator#evaluate(Constant, Object)
 */
public class Constant {
  private String name;




  /**
   * Constructor
   * 
   * @param name The mnemonic of the constant.
   * 
   * <p>The name is used in expressions to identified the constants.</p>
   */
  public Constant( String name ) {
    this.name = name;
  }




  /**
   * Gets the mnemonic of the constant.
   * 
   * @return the id
   */
  public String getName() {
    return name;
  }
  
}
