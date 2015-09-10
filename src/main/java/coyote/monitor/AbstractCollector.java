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
import coyote.loader.Loader;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigSlot;


/**
 * Forms the base class of all collectors.
 * 
 * It provides capabilities to allow collectors to be loaded as managed 
 * components by the Loader (Default Monitor)
 */
public abstract class AbstractCollector extends MonitorJob implements Collector {

  /** How often do we generate metric data? */
  protected long metricInterval = DEFAULT_METRIC_INTERVAL;

  /** How often do we try to generate metric data when we are in an error state? */
  protected long errorInterval = DEFAULT_ERROR_INTERVAL;

  private Monitor monitor = null;




  /**
   * Return a Configuration that can be used as a template for defining new 
   * instances of this collector.
   *
   * @return a configuration that can be used as a configuration template
   */
  public Config getTemplate() {
    Config template = new Config();

    try {
      template.addConfigSlot( new ConfigSlot( METRIC_INTERVAL_TAG, "Number of milliseconds between runs.", new Long( 1800000 ) ) );
      template.addConfigSlot( new ConfigSlot( LOG_METRICS_TAG, "Flag indicating the performance of this collector should be logged.", new Boolean( false ) ) );
      template.addConfigSlot( new ConfigSlot( ENABLED_TAG, "Flag indicating the collector is enabled to run.", new Boolean( true ) ) );
      template.addConfigSlot( new ConfigSlot( DESCRIPTION_TAG, "Description of the facility the collector is monitoring.", null ) );
      template.addConfigSlot( new ConfigSlot( DISPLAY_NAME_TAG, "The name of the collector as it is to appear on reports and displays.", null ) );
    } catch ( Exception ex ) {
      // should always work
    }

    return template;
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
    // TODO Auto-generated method stub
    return null;
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
    // TODO Auto-generated method stub
    return false;
  }




  /**
   * @see coyote.loader.component.ManagedComponent#setConfiguration(coyote.loader.cfg.Config)
   */
  @Override
  public void setConfiguration( Config config ) {
    // TODO Auto-generated method stub

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

}
