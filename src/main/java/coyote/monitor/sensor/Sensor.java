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
package coyote.monitor.sensor;

import coyote.monitor.Collector;


/**
 * Sensors are continuously running, autonomous components that record changes 
 * in its environment in real time.
 */
public interface Sensor extends Collector {

  public void shutdown();

}
