package coyote.commons.eval;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/** 
 * An abstract evaluator, able to evaluate infix expressions.
 * 
 * <p>Some standard evaluators are included in the library, you can define your 
 * own by subclassing this class.</p>
 * 
 * <p>Please note that this class is not thread safe. Under multi-threaded 
 * context you may have to instantiate one instance per thread (for instance 
 * using java.lang.ThreadLocal).</p>
 * 
 * @param <T> The type of values handled by the evaluator 
 */
public abstract class AbstractEvaluator<T> {
  private final Tokenizer tokenizer;
  private final Map<String, Function> functions;
  private final Map<String, List<Operator>> operators;
  private final Map<String, Constant> constants;
  private final String functionArgumentSeparator;
  private final Map<String, BracketPair> functionBrackets;
  private final Map<String, BracketPair> expressionBrackets;




  /** Constructor.
   * @param parameters The evaluator parameters.
   * <br>Please note that there's no side effect between the evaluator and the parameters.
   * So, changes made to the parameters after the call to this constructor are ignored by the instance. 
   */
  protected AbstractEvaluator( final Parameters parameters ) {
    //TODO if constants, operators, functions are duplicated => error
    final ArrayList<String> tokenDelimitersBuilder = new ArrayList<String>();
    functions = new HashMap<String, Function>();
    operators = new HashMap<String, List<Operator>>();
    constants = new HashMap<String, Constant>();
    functionBrackets = new HashMap<String, BracketPair>();
    for ( final BracketPair pair : parameters.getFunctionBrackets() ) {
      functionBrackets.put( pair.getOpen(), pair );
      functionBrackets.put( pair.getClose(), pair );
      tokenDelimitersBuilder.add( pair.getOpen() );
      tokenDelimitersBuilder.add( pair.getClose() );
    }
    expressionBrackets = new HashMap<String, BracketPair>();
    for ( final BracketPair pair : parameters.getExpressionBrackets() ) {
      expressionBrackets.put( pair.getOpen(), pair );
      expressionBrackets.put( pair.getClose(), pair );
      tokenDelimitersBuilder.add( pair.getOpen() );
      tokenDelimitersBuilder.add( pair.getClose() );
    }
    if ( operators != null ) {
      for ( final Operator ope : parameters.getOperators() ) {
        tokenDelimitersBuilder.add( ope.getSymbol() );
        List<Operator> known = this.operators.get( ope.getSymbol() );
        if ( known == null ) {
          known = new ArrayList<Operator>();
          this.operators.put( ope.getSymbol(), known );
        }
        known.add( ope );
        if ( known.size() > 1 ) {
          validateHomonyms( known );
        }
      }
    }
    boolean needFunctionSeparator = false;
    if ( parameters.getFunctions() != null ) {
      for ( final Function function : parameters.getFunctions() ) {
        this.functions.put( parameters.getTranslation( function.getName() ), function );
        if ( function.getMaximumArgumentCount() > 1 ) {
          needFunctionSeparator = true;
        }
      }
    }
    if ( parameters.getConstants() != null ) {
      for ( final Constant constant : parameters.getConstants() ) {
        this.constants.put( parameters.getTranslation( constant.getName() ), constant );
      }
    }
    functionArgumentSeparator = parameters.getFunctionArgumentSeparator();
    if ( needFunctionSeparator ) {
      tokenDelimitersBuilder.add( functionArgumentSeparator );
    }
    tokenizer = new Tokenizer( tokenDelimitersBuilder );
  }




  private void doFunction( final Deque<T> values, final Function function, final int argCount, final Object evaluationContext ) {
    if ( ( function.getMinimumArgumentCount() > argCount ) || ( function.getMaximumArgumentCount() < argCount ) ) {
      throw new IllegalArgumentException( "Invalid argument count for " + function.getName() );
    }
    values.push( evaluate( function, getArguments( values, argCount ), evaluationContext ) );
  }




  /** 
   * Evaluates a constant.
   * 
   * <p>Subclasses that support constants must override this method. The 
   * default implementation throws a RuntimeException meaning that implementor 
   * forget to implement this method while creating a subclass that accepts 
   * constants.</p>
   * 
   * @param constant The constant
   * @param evaluationContext The context of the evaluation
   * 
   * @return The constant's value
   */
  protected T evaluate( final Constant constant, final Object evaluationContext ) {
    throw new RuntimeException( "evaluate(Constant) is not implemented for " + constant.getName() );
  }




  /**
   * Evaluates a function.
   * 
   * <p>Subclasses that support functions must override this method. The 
   * default implementation throws a RuntimeException meaning that implementor 
   * forget to implement this method while creating a subclass that accepts 
   * functions.</p>
   * 
   * @param function The function
   * @param arguments The function's arguments
   * @param evaluationContext The context of the evaluation
   * 
   * @return The result of the function
   */
  protected T evaluate( final Function function, final Iterator<T> arguments, final Object evaluationContext ) {
    throw new RuntimeException( "evaluate(Function, Iterator) is not implemented for " + function.getName() );
  }




  /**
   * Evaluates an operation.
   * <p>Subclasses that support operators must override this method. The 
   * default implementation throws a RuntimeException meaning that implementor 
   * forget to implement this method while creating a subclass that accepts 
   * operators.</p>
   * 
   * @param operator The operator
   * @param operands The operands
   * @param evaluationContext The context of the evaluation
   * 
   * @return The result of the operation
   */
  protected T evaluate( final Operator operator, final Iterator<T> operands, final Object evaluationContext ) {
    throw new RuntimeException( "evaluate(Operator, Iterator) is not implemented for " + operator.getSymbol() );
  }




  /**
   * Evaluates an expression.
   * 
   * @param expression The expression to evaluate.
   * 
   * @return the result of the evaluation.
   * 
   * @throws IllegalArgumentException if the expression is not correct.
   */
  public T evaluate( final String expression ) {
    return evaluate( expression, null );
  }




  /**
   * Evaluates an expression that contains variables.
   * 
   * <p>The context is an object that can contain useful dynamic data, for 
   * example the values of the variables used in the expression (Use an 
   * AbstractVariableSet to do that).</p>
   * 
   * <p>The context is not limited to variable values but can be used for any 
   * dynamic information.</p>

   * 
   * @param expression The expression to evaluate.
   * @param evaluationContext The context of the evaluation.
   * 
   * @return the result of the evaluation.
   * 
   * @throws IllegalArgumentException if the expression is not correct.
   * 
   * @see AbstractVariableSet
   */
  public T evaluate( final String expression, final Object evaluationContext ) {
    final Deque<T> values = new ArrayDeque<T>(); // values stack
    final Deque<Token> stack = new ArrayDeque<Token>(); // operator stack
    final Deque<Integer> previousValuesSize = functions.isEmpty() ? null : new ArrayDeque<Integer>();
    final Iterator<String> tokens = tokenize( expression );
    Token previous = null;
    while ( tokens.hasNext() ) {
      // read one token from the input stream
      final String strToken = tokens.next();
      final Token token = toToken( previous, strToken );
      if ( token.isOpenBracket() ) {
        // If the token is a left parenthesis, then push it onto the stack.
        stack.push( token );
        if ( ( previous != null ) && previous.isFunction() ) {
          if ( !functionBrackets.containsKey( token.getBrackets().getOpen() ) ) {
            throw new IllegalArgumentException( "Invalid bracket after function: " + strToken );
          }
        } else {
          if ( !expressionBrackets.containsKey( token.getBrackets().getOpen() ) ) {
            throw new IllegalArgumentException( "Invalid bracket in expression: " + strToken );
          }
        }
      } else if ( token.isCloseBracket() ) {
        if ( previous == null ) {
          throw new IllegalArgumentException( "expression can't start with a close bracket" );
        }
        if ( previous.isFunctionArgumentSeparator() ) {
          throw new IllegalArgumentException( "argument is missing" );
        }
        final BracketPair brackets = token.getBrackets();
        // If the token is a right parenthesis:
        boolean openBracketFound = false;
        // Until the token at the top of the stack is a left parenthesis,
        // pop operators off the stack onto the output queue
        while ( !stack.isEmpty() ) {
          final Token sc = stack.pop();
          if ( sc.isOpenBracket() ) {
            if ( sc.getBrackets().equals( brackets ) ) {
              openBracketFound = true;
              break;
            } else {
              throw new IllegalArgumentException( "Invalid parenthesis match " + sc.getBrackets().getOpen() + brackets.getClose() );
            }
          } else {
            output( values, sc, evaluationContext );
          }
        }
        if ( !openBracketFound ) {
          // If the stack runs out without finding a left parenthesis, then
          // there are mismatched parentheses.
          throw new IllegalArgumentException( "Parentheses mismatched" );
        }
        if ( !stack.isEmpty() && stack.peek().isFunction() ) {
          // If the token at the top of the stack is a function token, pop it
          // onto the output queue.
          final int argCount = values.size() - previousValuesSize.pop();
          doFunction( values, stack.pop().getFunction(), argCount, evaluationContext );
        }
      } else if ( token.isFunctionArgumentSeparator() ) {
        if ( previous == null ) {
          throw new IllegalArgumentException( "expression can't start with a function argument separator" );
        }
        // Verify that there was an argument before this separator
        if ( previous.isOpenBracket() || previous.isFunctionArgumentSeparator() ) {
          // The cases were operator miss an operand are detected elsewhere.
          throw new IllegalArgumentException( "argument is missing" );
        }
        // If the token is a function argument separator
        boolean pe = false;
        while ( !stack.isEmpty() ) {
          if ( stack.peek().isOpenBracket() ) {
            pe = true;
            break;
          } else {
            // Until the token at the top of the stack is a left parenthesis,
            // pop operators off the stack onto the output queue.
            output( values, stack.pop(), evaluationContext );
          }
        }
        if ( !pe ) {
          // If no left parentheses are encountered, either the separator was misplaced
          // or parentheses were mismatched.
          throw new IllegalArgumentException( "Separator or parentheses mismatched" );
        }
      } else if ( token.isFunction() ) {
        // If the token is a function token, then push it onto the stack.
        stack.push( token );
        previousValuesSize.push( values.size() );
      } else if ( token.isOperator() ) {
        // If the token is an operator, op1, then:
        while ( !stack.isEmpty() ) {
          final Token sc = stack.peek();
          // While there is an operator token, o2, at the top of the stack
          // op1 is left-associative and its precedence is less than or equal
          // to that of op2,
          // or op1 has precedence less than that of op2,
          // Let + and ^ be right associative.
          // Correct transformation from 1^2+3 is 12^3+
          // The differing operator priority decides pop / push
          // If 2 operators have equal priority then associativity decides.
          if ( sc.isOperator() && ( ( token.getAssociativity().equals( Operator.Associativity.LEFT ) && ( token.getPrecedence() <= sc.getPrecedence() ) ) || ( token.getPrecedence() < sc.getPrecedence() ) ) ) {
            // Pop o2 off the stack, onto the output queue;
            output( values, stack.pop(), evaluationContext );
          } else {
            break;
          }
        }
        // push op1 onto the stack.
        stack.push( token );
      } else {
        // If the token is a number (identifier), a constant or a variable, then add its value to the output queue.
        if ( ( previous != null ) && previous.isLiteral() ) {
          throw new IllegalArgumentException( "A literal can't follow another literal" );
        }
        output( values, token, evaluationContext );
      }
      previous = token;
    }
    // When there are no more tokens to read:
    // While there are still operator tokens in the stack:
    while ( !stack.isEmpty() ) {
      final Token sc = stack.pop();
      if ( sc.isOpenBracket() || sc.isCloseBracket() ) {
        throw new IllegalArgumentException( "Parentheses mismatched" );
      }
      output( values, sc, evaluationContext );
    }
    if ( values.size() != 1 ) {
      throw new IllegalArgumentException();
    }
    return values.pop();
  }




  private Iterator<T> getArguments( final Deque<T> values, final int nb ) {
    // Be aware that arguments are in reverse order on the values stack.
    // Don't forget to reorder them in the original order (the one they appear in the evaluated formula)
    if ( values.size() < nb ) {
      throw new IllegalArgumentException();
    }
    final LinkedList<T> result = new LinkedList<T>();
    for ( int i = 0; i < nb; i++ ) {
      result.addFirst( values.pop() );
    }
    return result.iterator();
  }




  private BracketPair getBracketPair( final String token ) {
    final BracketPair result = expressionBrackets.get( token );
    return result == null ? functionBrackets.get( token ) : result;
  }




  /** 
   * Gets the constants supported by this evaluator.
   * 
   * @return a collection of constants.
   */
  public Collection<Constant> getConstants() {
    return this.constants.values();
  }




  /** 
   * Gets the functions supported by this evaluator.
   * 
   * @return a collection of functions.
   */
  public Collection<Function> getFunctions() {
    return this.functions.values();
  }




  /** 
   * Gets the operators supported by this evaluator.
   * 
   * @return a collection of operators.
   */
  public Collection<Operator> getOperators() {
    final ArrayList<Operator> result = new ArrayList<Operator>();
    final Collection<List<Operator>> values = this.operators.values();
    for ( final List<Operator> list : values ) {
      result.addAll( list );
    }
    return result;
  }




  /** 
   * When a token can be more than one operator (homonym operators), this 
   * method guesses the right operator.
   * 
   * <p>A very common case is the - sign in arithmetic computation which can be 
   * an unary or a binary operator, depending on what was the previous token.</p>
   *  
   * <p><b>Warning:</b> maybe the arguments of this function are not enough to 
   * deal with all the cases. So, this part of the evaluation is in alpha state 
   * (method may change in the future).</p>
   * 
   * @param previous The last parsed tokens (the previous token in the infix expression we are evaluating). 
   * @param candidates The candidate tokens.
   * 
   * @return A token
   * 
   * @see #validateHomonyms(List)
   */
  protected Operator guessOperator( final Token previous, final List<Operator> candidates ) {
    final int argCount = ( ( previous != null ) && ( previous.isCloseBracket() || previous.isLiteral() ) ) ? 2 : 1;
    for ( final Operator operator : candidates ) {
      if ( operator.getOperandCount() == argCount ) {
        return operator;
      }
    }
    return null;
  }




  @SuppressWarnings("unchecked")
  private void output( final Deque<T> values, final Token token, final Object evaluationContext ) {
    if ( token.isLiteral() ) { // If the token is a literal, a constant, or a variable name
      final String literal = token.getLiteral();
      final Constant ct = this.constants.get( literal );
      T value = ct == null ? null : evaluate( ct, evaluationContext );
      if ( ( value == null ) && ( evaluationContext != null ) && ( evaluationContext instanceof AbstractVariableSet ) ) {
        value = ( (AbstractVariableSet<T>)evaluationContext ).get( literal );
      }
      values.push( value != null ? value : toValue( literal, evaluationContext ) );
    } else if ( token.isOperator() ) {
      final Operator operator = token.getOperator();
      values.push( evaluate( operator, getArguments( values, operator.getOperandCount() ), evaluationContext ) );
    } else {
      throw new IllegalArgumentException();
    }
  }




  /**
   * Converts the evaluated expression into tokens.
   * 
   * <p>Example: The result for the expression "<i>-1+min(10,3)</i>" is an 
   * iterator on "-", "1", "+", "min", "(", "10", ",", "3", ")".</p>
   * 
   * <p>By default, the operators symbols, the brackets and the function 
   * argument separator are used as delimiter in the string.</p>
   * 
   * @param expression The expression that is evaluated
   * 
   * @return A string iterator.
   */
  protected Iterator<String> tokenize( final String expression ) {
    return tokenizer.tokenize( expression );
  }




  private Token toToken( final Token previous, final String token ) {
    if ( token.equals( functionArgumentSeparator ) ) {
      return Token.FUNCTION_ARG_SEPARATOR;
    } else if ( functions.containsKey( token ) ) {
      return Token.buildFunction( functions.get( token ) );
    } else if ( operators.containsKey( token ) ) {
      final List<Operator> list = operators.get( token );
      return ( list.size() == 1 ) ? Token.buildOperator( list.get( 0 ) ) : Token.buildOperator( guessOperator( previous, list ) );
    } else {
      final BracketPair brackets = getBracketPair( token );
      if ( brackets != null ) {
        if ( brackets.getOpen().equals( token ) ) {
          return Token.buildOpenToken( brackets );
        } else {
          return Token.buildCloseToken( brackets );
        }
      } else {
        return Token.buildLiteral( token );
      }
    }
  }




  /**
   * Evaluates a literal (Converts it to a value).
   * 
   * @param literal The literal to evaluate.
   * @param evaluationContext The context of the evaluation
   * 
   * @return an instance of T.
   * 
   * @throws IllegalArgumentException if the literal can't be converted to a value.
   */
  protected abstract T toValue( String literal, Object evaluationContext );




  /**
   * Validates that homonym operators are valid.
   * 
   * <p>Homonym operators are operators with the same name (like the unary - 
   * and the binary - operators)</p>
   * 
   * <p>This method is called when homonyms are passed to the constructor.</p>
   * 
   * <p>This default implementation only allows the case where there's two 
   * operators, one binary and one unary. Subclasses can override this method 
   * in order to accept others configurations.</p>
   *  
   * @param operators The operators to validate.
   * 
   * @throws IllegalArgumentException if the homonyms are not compatibles.
   * 
   * @see #guessOperator(Token, List)
   */
  protected void validateHomonyms( final List<Operator> operators ) {
    if ( operators.size() > 2 ) {
      throw new IllegalArgumentException();
    }
  }
}
