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

import coyote.loader.cfg.Config;


/**
 * 
 */
public abstract class AbstractCollector extends MonitorJob implements Collector {

  /** How often do we generate metric data? */
  protected long metricInterval = DEFAULT_METRIC_INTERVAL;
  
  /** How often do we try to generate metric data when we are in an error state? */
  protected long errorInterval = DEFAULT_ERROR_INTERVAL;

  
  /**
   * Return a Configuration that can be used as a template for defining new 
   * instances of this collector.
   *
   * @return a configuration that can be used as a configuration template
   */
  public Config getTemplate() {
    Config template = new Config();

    try
    {
      //template.setType( COLLECTOR_TAG );

      // define the slots
      //template.addAttributeSlot( new AttributeSlot( METRIC_INTERVAL_TAG, "Number of milliseconds between runs.", Attribute.LONG_TYPE, true, "ms", new Long( 1800000 ) ) );
      //template.addAttributeSlot( new AttributeSlot( LOG_METRICS_TAG, "Flag indicating the performance of this collector should be logged.", Attribute.BOOLEAN_TYPE, false, null, new Boolean( false ) ) );
      //template.addAttributeSlot( new AttributeSlot( ENABLED_TAG, "Flag indicating the collector is enabled to run.", Attribute.BOOLEAN_TYPE, false, null, new Boolean( true ) ) );
      //template.addAttributeSlot( new AttributeSlot( DESCRIPTION_TAG, "Description of the facility the collector is monitoring.", Attribute.STRING_TYPE, false, null, null ) );
      //template.addAttributeSlot( new AttributeSlot( DISPLAY_NAME_TAG, "The name of the collector as it is to appear on reports and displays.", Attribute.STRING_TYPE, false, null, null ) );
    }
    catch( Exception ex )
    {
      // should always work
    }
    
    return template;
  }

}
