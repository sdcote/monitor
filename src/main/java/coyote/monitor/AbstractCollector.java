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
import coyote.dataframe.DataFrameException;
import coyote.loader.Loader;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigSlot;
import coyote.loader.component.ManagedComponent;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;
import coyote.loader.thread.ScheduledJob;


/**
 * Forms the base class of all collectors.
 * 
 * It provides capabilities to allow collectors to be loaded as managed 
 * components by the Loader (Default Monitor)
 */
public abstract class AbstractCollector extends ScheduledJob implements Collector, ManagedComponent {

  /** How often do we generate metric data? */
  protected long metricInterval = DEFAULT_SAMPLE_INTERVAL;

  /** How often do we try to generate metric data when we are in an error state? */
  protected long errorInterval = DEFAULT_ERROR_INTERVAL;

  protected Config configuration = new Config();

  private Monitor monitor = null;

  protected CollectorCache mib = new CollectorCache();




  /**
   * Return a Configuration that can be used as a template for defining new 
   * instances of this collector.
   *
   * @return a configuration that can be used as a configuration template
   */
  public Config getTemplate() {
    Config template = new Config();

    try {
      template.addConfigSlot( new ConfigSlot( MonitorConfig.SAMPLE_INTERVAL, "Number of milliseconds between sample generation runs.", new Long( DEFAULT_SAMPLE_INTERVAL ) ) );
      template.addConfigSlot( new ConfigSlot( MonitorConfig.ERROR_INTERVAL, "Number of milliseconds between sample runs when in an error state.", new Long( DEFAULT_ERROR_INTERVAL ) ) );
      template.addConfigSlot( new ConfigSlot( MonitorConfig.ENABLED, "Flag indicating the collector is enabled to run.", new Boolean( true ) ) );
      template.addConfigSlot( new ConfigSlot( MonitorConfig.DESCRIPTION, "Description of the facility the collector is monitoring.", null ) );
    } catch ( Exception ex ) {
      // should always work
    }

    return template;
  }




  /**
   * Initialize the collector based on its currently set configuration.
   */
  @Override
  public void initialize() {
    super.initialize();
  }




  /**
   * @see coyote.loader.component.Component#getApplicationId()
   */
  @Override
  public String getApplicationId() {
    // TODO Auto-generated method stub
    return null;
  }




  /**
   * @see coyote.loader.component.Component#getCategory()
   */
  @Override
  public String getCategory() {
    // TODO Auto-generated method stub
    return null;
  }




  /**
   * @see coyote.loader.component.Component#getConfiguration()
   */
  @Override
  public Config getConfiguration() {
    return configuration;
  }




  /**
   * @see coyote.loader.component.Component#getId()
   */
  @Override
  public String getId() {
    // TODO Auto-generated method stub
    return null;
  }




  /**
   * @see coyote.loader.component.Component#getProfile()
   */
  @Override
  public DataFrame getProfile() {
    // TODO Auto-generated method stub
    return null;
  }




  /**
   * @see coyote.loader.component.Component#getStartTime()
   */
  @Override
  public long getStartTime() {
    // TODO Auto-generated method stub
    return 0;
  }




  /**
   * @see coyote.loader.component.Component#getStatus()
   */
  @Override
  public DataFrame getStatus() {
    // TODO Auto-generated method stub
    return null;
  }




  /**
   * @see coyote.loader.component.Component#getSystemId()
   */
  @Override
  public String getSystemId() {
    // TODO Auto-generated method stub
    return null;
  }




  /**
   * @see coyote.loader.component.Component#isLicensed()
   */
  @Override
  public boolean isLicensed() {
    return false;
  }




  /**
   * @see coyote.monitor.Collector#isTracing()
   */
  @Override
  public boolean isTracing() {
    // TODO Auto-generated method stub
    return false;
  }




  /**
   * @see coyote.loader.component.ManagedComponent#setConfiguration(coyote.loader.cfg.Config)
   */
  @Override
  public void setConfiguration( Config config ) {
    if ( config != null ) {
      configuration = config;
    } else {
      configuration = new Config();
    }

    //Number of milliseconds between runs.
    if ( configuration.contains( MonitorConfig.SAMPLE_INTERVAL ) ) {
      try {
        this.metricInterval = configuration.getAsLong( MonitorConfig.SAMPLE_INTERVAL );
      } catch ( DataFrameException e ) {
        Log.error( LogMsg.createMsg( "Monitor.probe_config_sample_interval", e.getMessage() ) );
      }
    }

    //Number of milliseconds between runs when in an error state.
    if ( configuration.contains( MonitorConfig.ERROR_INTERVAL ) ) {
      try {
        this.errorInterval = configuration.getAsLong( MonitorConfig.ERROR_INTERVAL );
      } catch ( DataFrameException e ) {
        Log.error( LogMsg.createMsg( "Monitor.probe_config_error_interval", e.getMessage() ) );
      }
    }

    //Flag indicating the collector is enabled to run.
    if ( configuration.contains( MonitorConfig.ENABLED ) ) {
      try {
        super.setEnabled( configuration.getAsBoolean( MonitorConfig.ENABLED ) );
      } catch ( DataFrameException e ) {
        Log.error( LogMsg.createMsg( "Monitor.probe_config_enabled", e.getMessage() ) );
      }
    }

    //Description of the facility the collector is monitoring.
    if ( configuration.contains( MonitorConfig.DESCRIPTION ) ) {
      try {
        super.setDescription( configuration.getAsString( MonitorConfig.DESCRIPTION ) );
      } catch ( Exception e ) {
        Log.error( LogMsg.createMsg( "Monitor.probe_config_description", e.getMessage() ) );
      }
    }

    // Make seure we start out as active
    setActiveFlag( true );

  }




  /**
   * @see coyote.loader.component.ManagedComponent#setLoader(coyote.loader.Loader)
   */
  @Override
  public void setLoader( Loader loader ) {
    // TODO Auto-generated method stub

  }




  /**
   * @see coyote.loader.component.ManagedComponent#quiesce()
   */
  @Override
  public void quiesce() {
    // TODO Auto-generated method stub

  }




  /**
   * @see coyote.loader.component.ManagedComponent#setId(java.lang.String)
   */
  @Override
  public void setId( String id ) {
    // TODO Auto-generated method stub

  }




  /**
   * @see coyote.loader.component.ManagedComponent#setStartTime(long)
   */
  @Override
  public void setStartTime( long millis ) {
    // TODO Auto-generated method stub

  }




  /**
   * @see coyote.loader.component.ManagedComponent#shutdown(coyote.dataframe.DataFrame)
   */
  @Override
  public void shutdown( DataFrame params ) {
    // TODO Auto-generated method stub

    super.shutdown();

  }




  /**
   * @return the monitor
   */
  public Monitor getMonitor() {
    return monitor;
  }




  /**
   * @param mon the monitor to set
   */
  public void setMonitor( Monitor mon ) {
    this.monitor = mon;
  }




  /**
   * @see coyote.monitor.Collector#getCache()
   */
  @Override
  public CollectorCache getCache() {
    return mib;
  }

}
