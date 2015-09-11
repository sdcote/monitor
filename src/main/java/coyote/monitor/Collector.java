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

import coyote.commons.Describable;
import coyote.commons.Described;
import coyote.commons.Namable;
import coyote.commons.Named;
import coyote.dataframe.DataFrame;
import coyote.loader.cfg.Config;


/**
 * Defines an interface for both Probes and Sensors.
 */
public interface Collector extends Runnable, Namable, Named, Describable, Described {

  public static final String CLASS = "Collector";


  public static final long DEFAULT_SAMPLE_INTERVAL = 3600000;


  public static final long DEFAULT_ERROR_INTERVAL = 60000;

 



  public Config getTemplate();




  /**
   * @return a reference to the data cache the collector uses to hold all its operational data. 
   */
  public CollectorCache getCache();




  /**
   * @return the monitor to which this collector belongs
   */
  public Monitor getMonitor();




  /**
   * @param mon the monitor to which this collector belongs
   */
  public void setMonitor( Monitor mon );




  /**
   * @return true if the collector is collecting operational data while 
   *         generating samples, false otherwise.
   */
  public boolean isTracing();




  /**
   * Configure the component with the given configuration.
   * 
   * @param config The object containing the configuration attributes.
   */
  public void setConfiguration( Config config );




  /**
   * Gives the collector a chance to prepare before doing work.
   */
  public void initialize();




  /**
   * Gives the collector a chance to clean up any resources before being 
   * removed from memory or restarted. 
   */
  public void terminate();




  /**
   * Signal the collector to stop processing;
   * 
   * <p>Shut this collector down using the given DataFrame as a set of 
   * parameters.</p>
   * 
   * @param params Shutdown arguments, can be null.
   */
  public void shutdown( final DataFrame params );

}
