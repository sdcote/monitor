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
package coyote.monitor.probe;

import coyote.dataframe.DataFrame;
import coyote.monitor.Collector;
import coyote.monitor.CollectorCache;


/**
 * Probes run intermittently to take a sampling of its environment.
 */
public interface Probe extends Collector {

  /**
   * @return an abstract data type representing the current state of the context being monitored.
   */
  public DataFrame generateSample();



}
