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

import java.util.Date;

import coyote.dataframe.DataFrame;
import coyote.dataframe.DataFrameException;


/**
 * 
 */
public class MonitorAlert extends DataFrame {

  /** The name of the XML node that represents the MonitorAlert */
  public static final String ALERT_TAG = "Alert";
  public static final String TYPE = "Type";
  public static final String TIMESTAMP = "Timestamp";
  public static final String ID = "ID";

  private boolean acknowledged = false;
  private long expires = 0;
  private int level = 0;
  private CollectorCache mib = null;




  /**
   * Constructor MonitorAlert
   */
  public MonitorAlert() {
    setType( ALERT_TAG );
    setTimestamp();
  }




  public void setType( String value ) {
    put( TYPE, value );
  }




  /**
   * Method isAcknowledged
   *
   * @return
   */
  public boolean isAcknowledged() {
    return acknowledged;
  }




  public void setTimestamp() {
    put( TIMESTAMP, new Date() );
  }




  public void setTimestamp( Date value ) {
    put( TIMESTAMP, value );
  }




  public Date getTimestamp() {
    try {
      return getAsDate( TIMESTAMP );
    } catch ( DataFrameException e ) {
      return null;
    }
  }




  /**
   * Method setAcknowledged
   *
   * @param ack
   */
  public void setAcknowledged( boolean ack ) {
    acknowledged = ack;
  }




  /**
   * Method getLevel
   *
   * @return
   */
  public int getLevel() {
    return level;
  }




  /**
   * Method setLevel
   *
   * @param lvl
   */
  public void setLevel( int lvl ) {
    level = lvl;
  }




  /**
   * Method getExpires
   *
   * @return
   */
  public long getExpires() {
    return expires;
  }




  /**
   * Method setExpires
   *
   * @param millis
   */
  public void setExpires( long millis ) {
    expires = millis;
  }




  /**
   * Method getMib
   *
   * @return
   */
  public CollectorCache getMib() {
    return mib;
  }




  /**
   * Method setMib
   *
   * @param mib
   */
  public void setMib( CollectorCache mib ) {
    this.mib = mib;
  }




  /**
   * Method ack
   */
  public void ack() {
    //    if( mib != null )
    //    {
    //      mib.ackAlert( getIdentifier() );
    //    }
  }




  /**
   * @return
   */
  private String getIdentifier() {
    return getAsString( ID );
  }

}
