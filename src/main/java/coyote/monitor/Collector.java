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

  public static final String COLLECTOR_TAG = "Collector";

  public static final String METRIC_INTERVAL_TAG = "MetricInterval";

  public static final long DEFAULT_METRIC_INTERVAL = 3600000;

  public static final String ERROR_INTERVAL_TAG = "ErrorInterval";

  public static final long DEFAULT_ERROR_INTERVAL = 60000;

  /** Name of the configuration attribute that contains the maximum number of runs for this collector */
  public static final String RUN_LIMIT_TAG = "RunLimit";

  public static final String EXPIRES_TAG = "Expires";

  public static final String ENABLED_TAG = "Enabled";

  public static final String DESCRIPTION_TAG = "Description";

  public static final String DISPLAY_NAME_TAG = "DisplayName";

  /**
   * The attribute tag that relates the name of the collector as defined in
   * the configuration attribute
   */
  public static final String COLLECTOR_NAME_TAG = "CollectorName";

  public static final String LOG_METRICS_TAG = "LogMetrics";

  /**
   * The name(s) of rule sets against which all our Monitor events and metrics
   * are checked
   */
  public static final String RULESET_TAG = "RuleSet";




  public Config getTemplate();




  /**
   * @return the monitor to which this collector belongs
   */
  public Monitor getMonitor();




  /**
   * @param mon the monitor to which this collector belongs
   */
  public void setMonitor( Monitor mon );




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
