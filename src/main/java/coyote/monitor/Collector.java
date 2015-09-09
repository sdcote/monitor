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

}
