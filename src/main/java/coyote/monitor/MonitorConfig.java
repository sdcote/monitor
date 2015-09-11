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

/**
 * 
 */
public class MonitorConfig {
  public static final String SAMPLE_INTERVAL = "SampleInterval";
  /** Name of the configuration attribute that contains the maximum number of runs for this collector */
  public static final String RUN_LIMIT = "RunLimit";

  public static final String EXPIRES = "Expires";

  public static final String ENABLED = "Enabled";

  public static final String DESCRIPTION = "Description";

  public static final String DISPLAY_NAME = "DisplayName";

  /**
   * The attribute tag that relates the name of the collector as defined in the 
   * configuration attribute
   */
  public static final String COLLECTOR_NAME = "CollectorName";

  public static final String LOG_SAMPLES = "LogSamples";

  /**
   * The name(s) of rule sets against which all our Monitor events and metrics
   * are checked
   */
  public static final String RULESET = "RuleSet";

  /**
   * Tag name of the attribute that contains the value of the error verification
   * config value
   */
  public static final String VERIFY = "Verify";
  
  public static final String ERROR_INTERVAL = "ErrorInterval";


}
