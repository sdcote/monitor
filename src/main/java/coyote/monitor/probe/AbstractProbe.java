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

import coyote.commons.Describable;
import coyote.commons.Namable;
import coyote.dataframe.DataFrame;
import coyote.dataframe.DataFrameException;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigSlot;
import coyote.loader.log.Log;
import coyote.loader.log.LogMsg;
import coyote.monitor.AbstractCollector;
import coyote.monitor.MonitorConfig;


/**
 * An AbstractProbe is the base class for all probes and handles a majority of 
 * processing.
 * 
 * <p>In general, the sub-classes only have to override 
 * {@link #generateSample()} as it is the specialization of the probe. It is 
 * also expected that {@link #initialize()} and {@link #terminate()} be over-
 * ridden to handle any special configuration options and to set-up & tear-down 
 * any other resources required in generating samples.</p> 
 * 
 */
public abstract class AbstractProbe extends AbstractCollector implements Probe, Namable, Describable {

  /** Are we verifying errors by re-generating the metric? default = false */
  protected boolean verifingErrors = false;




  /**
   * Return a DataFrame that can be used as a template for defining instances
   * of this class.
   *
   * @return a configuration that can be used as a template
   */
  public Config getTemplate() {
    // Get the configuration attributes for collectors in general
    Config template = super.getTemplate();

    try {
      template.addConfigSlot( new ConfigSlot( MonitorConfig.VERIFY, "Flag indicating the facility will be double-checked if an error occurs with the facility.", new Boolean( false ) ) );
    } catch ( Exception ex ) {
      // Should always work
    }

    return template;
  }




  /**
   * @see coyote.monitor.AbstractCollector#setConfiguration(coyote.loader.cfg.Config)
   */
  @Override
  public void setConfiguration( Config config ) {
    super.setConfiguration( config );

    if ( configuration.contains( MonitorConfig.VERIFY ) ) {
      try {
        this.verifingErrors = configuration.getAsBoolean( MonitorConfig.VERIFY );
      } catch ( DataFrameException e ) {
        Log.error( LogMsg.createMsg( "Monitor.probe_config_verify_error", e.getMessage() ) );
      }
    }

  }




  /**
   * Initialize the probe based on its currently set configuration.
   * 
   * <p><strong>NOTE:</strong> if over-riding this method, be sure to call 
   * {@code super.initialize()} to allow the base class ({@link AbstractProbe}) 
   * to perform its initialization. It is best to call this class <em>before</em> 
   * performing any sub-class initialization logic.</p>
   * 
   * @see coyote.loader.thread.ThreadJob#initialize()
   */
  @Override
  public void initialize() {
    System.out.println( "initialized" );
  }




  /**
   * @see coyote.loader.thread.ThreadJob#doWork()
   */
  @Override
  public void doWork() {
    Log.info( "Working" );
    generateSample();
  }




  /**
   * <p><strong>NOTE:</strong> if over-riding this method, be sure to call 
   * {@code super.terminate()} to allow the base class ({@link AbstractProbe}) 
   * to perform its termination. It is best to call this class <em>after</em> 
   * performing any sub-class termination logic.</p>
   * 
   * @see coyote.loader.thread.ThreadJob#terminate()
   */
  @Override
  public void terminate() {
    System.out.println( "terminated" );
  }




  /**
   * Return whether or not this Probe is verifying errors.
   *
   * <p>True indicates this Probe will call <code>generateMetric()</code> a
   * second time if the Metric generated by the first call contained an error.
   * False indicates the call will not be repeated.<p>
   *
   * @return True indicates the Probe is verifying errors, false otherwise.
   */
  public boolean isVerifingErrors() {
    return verifingErrors;
  }




  /**
   * Set the flag indication error verification runs should be performed.
   *
   * @param flag True indicates the Probe should verify errors, false indicates
   *        only call the metric generation routine once.
   */
  public void setVerifingErrors( boolean flag ) {
    this.verifingErrors = flag;
  }




  /**
   * @see coyote.monitor.probe.Probe#generateSample()
   */
  @Override
  public DataFrame generateSample() {
    // TODO Auto-generated method stub
    return null;
  }

}
