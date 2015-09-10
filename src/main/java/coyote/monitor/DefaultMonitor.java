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

import coyote.loader.log.Log;


/**
 * 
 */
public class DefaultMonitor extends AbstractMonitor implements Monitor {

  /** Tag used in various class identifying locations. */
  public static final String CLASS = "DefaultMonitor";




  /**
   * @see coyote.loader.thread.ThreadJob#doWork()
   */
  @Override
  public void doWork() {
    // check status of Probes and reload if necessary
    // check status of Sensors and reload if necessary
    // Update any metrics as necessary
    Log.info( "Monitoring" );
  }




  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder( CLASS );

    return b.toString();
  }

}
