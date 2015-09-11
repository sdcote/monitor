/*
 * Copyright (c) 2015 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.monitor;

import coyote.dataframe.DataFrame;


/**
 * This models a data sample taken by a collector for placement in a collector 
 * cache.
 */
public class Sample extends DataFrame {

  public static final String CLASS = "Sample";
  public static final String ERROR = "Error";
  public static final String TYPE = "Type";




  public Sample() {
    setType( CLASS );
  }




  /**
   * Set the type of data sample this is.
   * 
   * <p>This is usually the name of the collector generating this sample.</p>
   * 
   * @param value the type of data sample this is
   */
  public void setType( String value ) {
    put( TYPE, value );
  }




  /**
   * Set the error message in this data sample indicating an unexpected event 
   * has occurred in processing.
   * 
   * @param msg The text to set as the error message.
   */
  public void setError( String msg ) {
    put( ERROR, msg );
  }




  /**
   * @return The error message currently sent in the event. This may be null.
   */
  public String getError() {
    return getAsString( ERROR );
  }




  /**
   * @return True if the event contains an error message indication the event 
   *         represents an error, false otherwise.
   */
  public boolean hasError() {
    return contains( ERROR );
  }

}
