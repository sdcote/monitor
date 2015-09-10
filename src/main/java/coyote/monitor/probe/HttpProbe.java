/*
 * $Id: HttpProbe.java,v 1.7 2005/03/08 19:47:57 cotes Exp $
 */
package coyote.monitor.probe;

import java.net.URI;

import coyote.commons.network.http.HttpMessageException;
import coyote.commons.network.http.HttpRequest;
import coyote.commons.network.http.HttpResponse;
import coyote.dataframe.DataFrame;
import coyote.loader.cfg.Config;
import coyote.loader.cfg.ConfigSlot;
import coyote.loader.log.Log;


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
   * @see coyote.monitor.probe.Probe#generateSample()
   */
  @Override
  public DataFrame generateSample() {
    DataFrame retval = new DataFrame();

    HttpRequest request = new HttpRequest();

    try {
      HttpResponse response = request.send( uri );
    } catch ( HttpMessageException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
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