/*
 * $Id: HttpProbe.java,v 1.7 2005/03/08 19:47:57 cotes Exp $
 */
package coyote.monitor.probe;

import java.net.URI;

import coyote.commons.ByteUtil;
import coyote.commons.ExceptionUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.network.http.HttpRequest;
import coyote.commons.network.http.HttpResponse;
import coyote.commons.security.MD5;
import coyote.dataframe.DataFrame;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigSlot;
import coyote.loader.log.Log;
import coyote.monitor.Sample;


/**
 * Connects to the HTTP interface of a site and retrieves a page.
 *
 * TODO implement connection timeout
 * TODO support POST, HEAD, PUT, DELETE and TRACE
 * TODO support Basic Authentication
 * TODO place header data in the sample and metric
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.7 $
 */
public class HttpProbe extends AbstractProbe {

  public static final String DESTINATION_URI = "Destination";

  public static final String CONNECT_TIMEOUT = "ConnectTimeout";

  /** The expected MD5 signature attribute tag */
  public static final String SIGNATURE = "SignatureMD5";

  public static final String CONTENT_CHANGE = "ContentChange";

  public static final String CONNNECTION_URI = "ConnectionURI";

  public static final String SOURCE_ADDRESS = "SourceAddress";

  public static final String DESTINATION_ADDRESS = "DestinationAddress";

  public static final String CONNECTION_TIME = "ConnectionTime";

  public static final String SERVER_LATENCY = "ServerLatency";

  public static final String BPS = "BytesPerSecond";

  public static final String CONTENT_LENGTH = "ContentLength";

  /** The uri of the peer we are to check */
  private URI uri = null;

  /** The default number of milliseconds (5000) we use for our connection timeout */
  private static final int DEFAULT_CONNECT_TIMEOUT = 5000;

  /** The actual timeout we use for making our connections in milliseconds */
  private int connectTimeOut = DEFAULT_CONNECT_TIMEOUT;




  /**
   * Constructor HttpProbe
   */
  public HttpProbe() {

  }




  /**
   * Return a DataFrame that can be used as a template for defining instances
   * of this class.
   *
   * @return a configuration that can be used as a template for other collectors
   */
  public Config getTemplate() {

    // Get the configuration attributes for probes in general
    Config template = super.getTemplate();

    try {
      // HttpProbe specific attributes
      template.setClassName( getClass().getName() );
      template.addConfigSlot( new ConfigSlot( DESTINATION_URI, "The URI of the resource to test.", "http://localhost/index.html" ) );
      template.addConfigSlot( new ConfigSlot( CONNECT_TIMEOUT, "The number of milliseconds to wait for the connection.", DEFAULT_CONNECT_TIMEOUT ) );
    } catch ( Exception ex ) {
      // Should always work
    }

    return template;
  }




  /**
   * @see coyote.monitor.probe.AbstractProbe#initialize()
   */
  @Override
  public void initialize() {
    // Take the configuration values and set them here
  }




  /**
   * This will be called by the AbstractProbe many times during its lifetime.
   * 
   * @see coyote.monitor.probe.Probe#generateSample()
   */
  @Override
  public DataFrame generateSample() {
    Sample retval = new Sample();
    retval.setType( StringUtil.getLocalJavaName( getClass().getName() ) );

    HttpRequest request = new HttpRequest();

    if ( uri != null ) {
      // Place the target URI in our current metric
      try {
        retval.put( DESTINATION_URI, UriUtil.clone( uri ) );
        retval.put( SOURCE_ADDRESS, UriUtil.getHostAddress( uri ) );
      } catch ( Exception ex ) {
        // Should always work even when null
      }

      try {
        HttpResponse response = request.send( uri );
        // If we got a connection
        if ( response != null ) {
          if ( response.getStatusCode() >= 400 ) {
            retval.setError( "Server error response: " + response.getStatusCode() + " - " + response.getReasonPhrase() );
          }

          //if( super.isTracing() )super.recordTraceData( "HTTP response status code: " + response.getStatusCode() + " - " + response.getReasonPhrase() );

          retval.put( CONNECTION_TIME, response.getConnectionTime() );
          retval.put( SERVER_LATENCY, response.getServerLatency() );
          retval.put( BPS, response.getBytesPerSecond() );
          retval.put( CONTENT_LENGTH, response.getBody().length );
          retval.put( DESTINATION_ADDRESS, response.getRemoteAddress().toString() );

          // Perform an MD5 fingerprint on the body so as to allow detection of
          // content changes
          MD5 md = new MD5();
          md.update( response.getBody() );

          String signature = ByteUtil.bytesToHex( md.digest() );
          retval.put( SIGNATURE, signature );

          //          DataFrame prev = mib.getSample();
          //
          //          // Figure out what the previous samples signature was
          //          String previousSignature = null;
          //
          //          if( prev != null )
          //          {
          //            previousSignature = prev.getString( SIGNATURE_TAG );
          //          }
          //
          //          // If the current and previous signatures do not match, set the
          //          // ContentChange attribute to true
          //          if( ( previousSignature != null ) && !signature.equalsIgnoreCase( previousSignature ) )
          //          {
          //            MonitorEvent event = new MonitorEvent( "Content changed" );
          //            event.set( "OldSignature", previousSignature );
          //            event.set( "NewSignature", signature );
          //            mib.addEvent( event );
          //            metric.putAttribute( CONTENT_CHANGE_TAG, new Boolean( true ) );
          //          }
        } else {
          retval.setError( "Could not establish connection in " + connectTimeOut + " ms" );
        }
      } catch ( Exception ae ) {
        Log.warn( getClass().getName() + ":" + getName() + " threw the following exception:\r\n" + ae.getClass().getName() + "\r\n" + ae.getMessage() + "\r\n" + ExceptionUtil.stackTrace( ae ) );
        retval.put( "Error", ae.getMessage() );
        //super.recordTraceData( ExceptionUtil.stackTrace( ae ) );
      }
      finally {
        // Save a copy of this metric as our last sample
        //mib.setSample( (DataFrame)retval.clone() );
      }
    } else {
      retval.setError( "No URI specified" );
    }
    return retval;
  }




  /**
   * Method main
   *
   * @param args
   *
   * @throws Exception
   */
  public static void main( String[] args ) throws Exception {
    Log.startLogging( Log.DEBUG );

    // Create a new probe
    Probe probe = new HttpProbe();
    Config cfg = probe.getTemplate();
    cfg.setDefaults(); // set the defaults for correct operation

    // Set the destination of the HTTP probe request
    cfg.put( HttpProbe.DESTINATION_URI, "www.lycos.com" );

    // Configure the probe to only run once
    //cfg.put( HttpProbe.EXECUTION_LIMIT, 1 );

    System.out.println( cfg.toFormattedString() );

    // Configure the probe
    probe.setConfiguration( cfg );

    // Run the probe
    probe.run();

    // Output the results of the probe run
    //System.err.println( probe.getCollectorCache().toFormattedString() );

  }

}