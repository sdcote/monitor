/*
 * $Id: HttpProbe.java,v 1.7 2005/03/08 19:47:57 cotes Exp $
 */
package coyote.monitor.probe;

import java.net.URI;

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

  /** Field DESTINATION_URI_TAG */
  public static final String DESTINATION_URI_TAG = "Destination";

  /** Field CONNECT_TIMEOUT_TAG */
  public static final String CONNECT_TIMEOUT_TAG = "ConnectTimeout";

  /** The MD5 signature attribute tag */
  public static final String SIGNATURE_TAG = "SignatureMD5";

  /** Field CONTENT_CHANGE_TAG */
  public static final String CONTENT_CHANGE_TAG = "ContentChange";

  /** Field CONNNECTION_URI_TAG */
  public static final String CONNNECTION_URI_TAG = "ConnectionURI";

  /** Field SOURCE_ADDRESS_TAG */
  public static final String SOURCE_ADDRESS_TAG = "SourceAddress";

  /** Field DESTINATION_ADDRESS_TAG */
  public static final String DESTINATION_ADDRESS_TAG = "DestinationAddress";

  /** Field CONNECTION_TIME_TAG */
  public static final String CONNECTION_TIME_TAG = "ConnectionTime";

  /** Field SERVER_LATENCY_TAG */
  public static final String SERVER_LATENCY_TAG = "ServerLatency";

  /** Field BPS_TAG */
  public static final String BPS_TAG = "BytesPerSecond";

  /** Field CONTENT_LENGTH_TAG */
  public static final String CONTENT_LENGTH_TAG = "ContentLength";

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
    //configuration.setClassName( getClass().getName() );
  }




  /**
   * Return a DataFrame that can be used as a template for defining instances
   * of this class.
   *
   * @return a capsule that can be used as a configuration template
   */
  public Config getTemplate() {
    Config template = super.getTemplate();

    try {
      // HttpProbe specific attributes
      template.setClassName( getClass().getName() );
      //template.setDescription( "The HTTP Probe sends an HTTP request using the given destination URI and measures the performance of the retrieval process." );
      template.addConfigSlot( new ConfigSlot( DESTINATION_URI_TAG, "The URI of the resource to test.", new URI( "http://localhost/index.html" ).toString() ) );
      //template.addConfigSlot( new ConfigSlot( CONNECT_TIMEOUT_TAG, "The number of milliseconds to wait for the connection.", new Integer( 5000 ) ) );
    } catch ( Exception ex ) {
      // Should always work
    }

    return template;
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
    HttpProbe probe = new HttpProbe();

    // probe.uri = new URI( "http", "www.tripod.lycos.com", 80 );
    // probe.uri = new URI( "http://www.tripod.lycos.com/adm/unknown_host.html" );
    // probe.uri = new URI( "http://www.bralyn.net/main.html" );
    probe.uri = new URI( "http://www.terralycos.com/" );

    // Run the probe
    probe.run();

    // Output the results of the probe run
    //System.err.println( probe.getMib().toIndentedXML() );

  }

}