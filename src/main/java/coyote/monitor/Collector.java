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

import coyote.commons.IDescribable;
import coyote.commons.IDescribed;
import coyote.commons.INamable;
import coyote.commons.INamed;


/**
 * Defines an interface for both Probes and Sensors.
 */
public interface Collector extends Runnable, INamable, INamed, IDescribable, IDescribed {

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

}
