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

import java.util.Iterator;

import coyote.commons.Version;
import coyote.dataframe.DataFrame;
import coyote.loader.cfg.Config;
import coyote.loader.component.ManagedComponent;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;
import coyote.loader.log.LogMsg.BundleBaseName;
import coyote.monitor.sensor.Sensor;


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
   * This overrides the main watchdog loop as it needs to handle Probes and 
   * Sensors differently.
   * 
   * <p>This is where the thread spends its time monitoring components it has 
   * loaded and performing housekeeping operations.</p>
   */
  @Override
  protected void watchdog() {
    setActiveFlag( true );

    Log.info( LogMsg.createMsg( "Loader.operational" ) );

    while ( !isShutdown() ) {

      // Make sure that all this loaders are active, otherwise remove the
      // reference to them and allow GC to remove them from memory
      synchronized( components ) {
        for ( final Iterator<Object> it = components.keySet().iterator(); it.hasNext(); ) {
          final Object cmpnt = it.next();
          if ( cmpnt instanceof ManagedComponent ) {
            if ( !( (ManagedComponent)cmpnt ).isActive() && cmpnt instanceof Sensor ) {
              Log.info( LogMsg.createMsg( "Loader.removing_inactive_cmpnt", cmpnt.toString() ) );

              // get a reference to the components configuration
              final Config config = components.get( cmpnt );

              // communicate the reason for the shutdown
              DataFrame frame = new DataFrame();
              frame.put( "Message", "Terminating due to inactivity" );

              // try to shut it down properly
              safeShutdown( (ManagedComponent)cmpnt, frame );

              // remove the component
              it.remove();

              // re-load the component
              loadComponent( config );
            }
          }
        }
      }

      // TODO cycle through all the hangtime objects and check their last 
      // check-in time. If expired, log the event and restart them like the 
      // above active check

      // Monitor check-in map size; if it is too large, we have a problem
      if ( checkin.size() > components.size() ) {
        Log.fatal( LogMsg.createMsg( "Loader.check_in_map_size", checkin.size(), components.size() ) );
      }

      // If we have no components which are active, there is not need for this
      // loader to remain running
      if ( components.size() == 0 ) {
        Log.warn( LogMsg.createMsg( "Loader.no_components" ) );
        this.shutdown();
      }

      // Yield to other threads and sleep(wait) for a time
      park( parkTime );

    }

    if ( Log.isLogging( Log.DEBUG_EVENTS ) ) {
      Log.debug( LogMsg.createMsg( "Loader.terminating" ) );
    }

    terminate();

    setActiveFlag( false );
  }

}
