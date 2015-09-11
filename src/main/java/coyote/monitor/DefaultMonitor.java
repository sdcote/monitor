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

import coyote.commons.Version;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;
import coyote.loader.log.LogMsg.BundleBaseName;


/**
 * This class is called by the BootStrap loader (if so configured) to act as 
 * the primary component in the system.
 * 
 * <p>This loads all the collectors as Components and then keeps things running
 * by looping through the components, checking their status and reacting as
 * necessary.</p>
 */
public class DefaultMonitor extends AbstractMonitor implements Monitor {

  /** Tag used in various class identifying locations. */
  public static final String CLASS = "DefaultMonitor";

  private static final Version VERSION = new Version( 0, 9, 0, Version.DEVELOPMENT );

  static {

    LogMsg.setBundleBaseNameDefault( new BundleBaseName( "MonitorMsg" ) );
  }




  public DefaultMonitor() {}




  /**
   * Start the components running.
   */
  public void start() {
    // only start once, this is not foolproof as the active flag is set only when 
    // the watchdog loop is entered
    if ( isActive() ) {
      return;
    }

    // Save the name of the thread that is running this class
    final String oldName = Thread.currentThread().getName();

    // Rename this thread to the name of this class
    Thread.currentThread().setName( CLASS );

    // very important to get park(millis) to operate
    current_thread = Thread.currentThread();

    // Parse through the configuration and initialize all the components
    initComponents();

    Log.info( LogMsg.createMsg( "Loader.components_initialized" ) );

    // By this time all loggers (including the catch-all logger) should be open
    final StringBuffer b = new StringBuffer( CLASS );
    b.append( " " );
    b.append( VERSION.toString() );
    b.append( " initialized - Runtime: " );
    b.append( System.getProperty( "java.version" ) );
    b.append( " (" );
    b.append( System.getProperty( "java.vendor" ) );
    b.append( ")" );
    b.append( " - Platform: " );
    b.append( System.getProperty( "os.arch" ) );
    b.append( " OS: " );
    b.append( System.getProperty( "os.name" ) );
    b.append( " (" );
    b.append( System.getProperty( "os.version" ) );
    b.append( ")" );
    Log.info( b );

    // enter a loop performing watchdog and maintenance functions
    watchdog();

    // The watchdog loop has exited, so we are done processing
    terminateComponents();

    Log.info( LogMsg.createMsg( "Loader.terminated" ) );

    // Rename the thread back to what it was called before we were being run
    Thread.currentThread().setName( oldName );

  }




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
