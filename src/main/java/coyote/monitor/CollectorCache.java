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

import coyote.commons.list.LinkedList;
import coyote.commons.list.ListNode;
import coyote.dataframe.DataFrame;
import coyote.dataframe.DataFrameException;
import coyote.dataframe.marshal.JSONMarshaler;


/**
 * Collectors data.
 * 
 * The Collector Cache contains data related to the operation of one specific 
 * collector in its function of monitoring some resource. Each collector in the 
 * monitor has its own cache dedicated to holding the data generated from that 
 * collector.</p> 
 */
public class CollectorCache extends DataFrame {

  /** 
   * The current event sequence identifier to be used for identifying events 
   * from this instrumentation fixture.
   */
  private static volatile long _eventSequence = 0;

  /** The name of the attribute that holds our current status */
  public static final String STATUS = "Status";

  /** The name of the attribute that holds the last error message */
  public static final String LAST_ERROR = "LastErrorMessage";

  /** The name of the attribute that holds the previous status */
  public static final String PREV_STATUS = "PreviousStatus";

  /**
   * Standard string representing the error status - something is wrong with
   * this component
   */
  public static final String ERROR_STATUS = "ERROR";

  /** Standard string representing the clear status - everything is fine */
  public static final String CLEAR_STATUS = "CLEAR";

  /**
   * Standard string representing the unknown status - contact with endpoint has
   * been lost and its status is unknown
   */
  public static final String UNKNOWN_STATUS = "UNKNOWN";

  /** The Monitor object to which we are associated */
  Monitor monitor = null;

  /** The self-synchronized list of all the events in order of their occurrence */
  LinkedList events = new LinkedList();

  /** The self-synchronized list of all the alerts in order of their occurrence */
  LinkedList alerts = new LinkedList();

  /** Number of times the MIB has toggled from down to up */
  protected int bounceCount = 0;

  /** The current alert identifier for this MIB instance */
  private volatile long alertid = 0;




  public void setSample( DataFrame frame ) {
    frame.put( "Type", "Sample" );
    put( "Sample", frame );
  }




  /**
   * @return the previous sample
   */
  public DataFrame getSample() {
    try {
      return getAsFrame( "Sample" );
    } catch ( DataFrameException e ) {
      return null;
    }
  }




  /**
   * Return the next event in the list of events in order of their occurence.
   *
   * @return the next event in the list, or null if the event list is empty.
   */
  public MonitorEvent getNextEvent() {
    ListNode node = events.popNode();

    if ( node != null ) {
      return (MonitorEvent)node.getObject();
    }

    return null;
  }




  /**
   * Add the event to our list of events.
   *
   * <p>This method is synchronized to prevent multi-threaded applications from 
   * possibly generating multiple events with the same identifier.</p> 
   * 
   * @param evnt The event to add.
   * 
   * @return The sequence identifier of the event or -1 if the event was null.
   */
  public synchronized long addEvent( MonitorEvent evnt ) {
    if ( evnt == null ) {
      return -1;
    }

    long retval = _eventSequence++;
    evnt.setIdentifier( retval );

    // add the event to the list
    events.add( evnt );

    return retval;

  }




  /**
   * @return The number of events in this MIB.
   */
  public int getEventCount() {
    return events.size();
  }




  /**
   * Clear out all the events in this MIB.
   */
  public void clearEvents() {
    events.clear();
  }




  public String toFormattedString() {
    return JSONMarshaler.toFormattedString( this );
  }

}
