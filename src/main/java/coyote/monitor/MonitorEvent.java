/*
 * $Id: MonitorEvent.java,v 1.5 2005/04/07 14:39:32 cotes Exp $
 */
package coyote.monitor;

import java.util.Date;

import coyote.commons.IDescribable;
import coyote.commons.IDescribed;
import coyote.dataframe.DataFrame;
import coyote.dataframe.DataFrameException;


/**
 * Class MonitorEvent
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.5 $
 */
public class MonitorEvent extends DataFrame implements IDescribable, IDescribed {

  /** 
   * Not all events are errors, and the message in the event is used to 
   * describe the details of the event.
   */
  public static final String MESSAGE_TAG = "Message";

  /** 
   * The description of the event is normally an explaination of the general 
   * purpose of the event itself. This is different from the message of the 
   * event in that the description explains the purpose of the event and the 
   * message described the details of the event.
   */
  public static final String DESCRIPTION_TAG = "Description";

  /**
   * The existence of this data indicates the event represents an error has 
   * occurred. This is different from the message attribute which describes an 
   * expected event. 
   */
  public static final String ERROR_TAG = "ErrorMessage";

  public static final String TYPE_TAG = "Type";
  public static final String TIMESTAMP_TAG = "Timestamp";
  public static final String TYPE = "Event";




  /**
   * Constructor MonitorEvent
   */
  public MonitorEvent() {
    setType( TYPE );
    setTimestamp();
  }




  /**
   * Create an event with the message (not the error message) set to the given 
   * text.
   *
   * @param msg The message to place in the event.
   */
  public MonitorEvent( String msg ) {
    this();

    setMessage( msg );
  }




  public void setTimestamp() {
    try {
      put( TIMESTAMP_TAG, new Date() );
    } catch ( Exception ex ) {
      // Should always, work even when null
    }
  }




  public void setTimestamp( Date value ) {
    try {
      put( TIMESTAMP_TAG, value );
    } catch ( Exception ex ) {
      // Should always, work even when null
    }
  }




  public Date getTimestamp() {
    try {
      return getAsDate( TIMESTAMP_TAG );
    } catch ( DataFrameException e ) {
      return null;
    }
  }




  public void setType( String value ) {
    try {
      put( TYPE_TAG, value );
    } catch ( Exception ex ) {
      // Should always, work even when null
    }
  }




  public String getType() {
    return getAsString( TYPE_TAG );
  }




  /**
   * The the description of the which event is normally an explaination of the 
   * general purpose of the event itself. 
   * 
   * <p>This is different from the message of the event in that the description 
   * explains the purpose of the event and the message describes the details of 
   * the event.</p>
   * 
   * <p>If the given text is null, then any existing description text is 
   * removed from the event.</p>
   *
   * @param desc The text to use as the general description of the event.
   */
  public void setDescription( String desc ) {
    try {
      put( DESCRIPTION_TAG, desc );
    } catch ( Exception ex ) {
      // Should always, work even when null
    }
  }




  /**
   * @return The general description of this event, whic is different from the 
   *         message text attributes in that this is used to describe the 
   *         purpose of the event, not give details.
   */
  public String getDescription() {
    return getAsString( DESCRIPTION_TAG );
  }




  /**
   * Set the error message in this event indication an unexpected or other 
   * exception has occurred in processing.
   * 
   * <p>This is different from the message attribute which indicates the 
   * details of normal, expected processing.</p>
   * 
   * <p>If the given text is null, then the error message is removed an the 
   * event no-longer indicates an error has occurred.</p>
   *
   * @param msg The text to set as the error message.
   */
  public void setError( String msg ) {
    try {
      put( ERROR_TAG, msg );
    } catch ( Exception ex ) {
      // Should always, work even when null
    }
  }




  /**
   * @return The error message currently sent in the event. This may be null.
   */
  public String getError() {
    return getAsString( ERROR_TAG );
  }




  /**
   * @return True if the event contains an error message indication the event 
   *         represents an error, false otherwise.
   */
  public boolean hasError() {
    return contains( ERROR_TAG );
  }




  /**
   * Set the message of this event to the given text.
   * 
   * <p>If the message is null, then any existing message will be removed from 
   * the event.</p>
   *
   * @param msg The message to set.
   */
  public void setMessage( String msg ) {
    try {
      put( MESSAGE_TAG, msg );
    } catch ( Exception ex ) {
      // should always work even when null
    }

  }




  /**
   * @return The message currently set in the event.
   */
  public void getMessage() {
    getAsString( MESSAGE_TAG );
  }

}