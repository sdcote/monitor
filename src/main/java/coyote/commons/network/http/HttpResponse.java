/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial concept and initial implementation
 */
package coyote.commons.network.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import coyote.commons.DateUtil;
import coyote.commons.ExceptionUtil;
import coyote.commons.StreamUtil;
import coyote.commons.StringUtil;
import coyote.loader.log.Log;


/**
 * Class HttpResponse
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.5 $
 */
public class HttpResponse extends HttpMessage {
  private Date date = new Date();
  private int statusCode = 200;
  private String reasonPhrase = null;
  private String server = null;
  private static final int DEFAULT_TIMEOUT = 60000;
  private volatile long connectionTime = 0;
  private volatile long requestSent = 0;
  private volatile long responseReceived = 0;
  private volatile long started = 0;
  private volatile long ended = 0;
  private volatile long byteCount = 0;
  private PrintWriter writer = null;
  private ByteArrayOutputStream baos = null;
  private volatile boolean outputUserControlled = false;
  private OutputStream outputStream = null;




  /**
   * Constructor HttpResponse
   */
  public HttpResponse() {
    this.requestSent = System.currentTimeMillis();

    setTimeout( DEFAULT_TIMEOUT );
    super.setContentType( DEFAULT_CONTENT_TYPE );

    baos = new ByteArrayOutputStream();
    writer = new PrintWriter( baos, false );
  }




  /**
   * Constructor HttpResponse
   *
   * @param in
   *
   * @throws HttpMessageException
   */
  public HttpResponse( InputStream in ) throws HttpMessageException {
    this();

    this.input = in;

    parse( in );
  }




  /**
   * Method reset
   * 
   * called by {@link #parse(InputStream)}
   */
  public void reset() {
    // clear out headers
    super.reset();

    // Set the default response content type
    setContentType( DEFAULT_CONTENT_TYPE );

    this.date = new Date();
    this.statusCode = 200;
    this.reasonPhrase = null;
    this.server = null;
    this.started = 0;
    this.ended = 0;
    this.byteCount = 0;
  }




  /**
   * Parses the input stream using the RFC 2616 specification
   * 
   * This parses the input stream when running as a client
   *
   * TODO Support chunking as defined in RFC2068
   * 
   * @param in
   *
   * @throws HttpMessageException
   */
  public void parse( InputStream in ) throws HttpMessageException {
    // clear out everything
    reset();

    Log.append( HTTP, "Reading response..." );

    if ( in != null ) {
      // Start our time-out timer
      startTimer();

      // record when the response parsing started
      started = System.currentTimeMillis();

      // Keep looping
      while ( true ) {
        try {
          // Make sure we have data in the stream
          if ( in.available() > 0 ) {
            if ( responseReceived == 0 ) {
              responseReceived = System.currentTimeMillis();
            }

            try {
              // The first line should always be the Status-Line (section: 6.1)
              String status = StreamUtil.readLine( in );

              // Tally the length of the status line plus the CRLF terminator
              byteCount += status.length() + 2;

              Log.append( HTTP, getClass().getName() + "parse() StatusLine=" + status + "'" );

              if ( ( status != null ) && ( status.length() > 0 ) ) {
                // RFC2616 section 6.1
                int mark = 0;
                int state = 0;

                for ( int i = 0; i < status.length(); i++ ) {
                  char c = status.charAt( i );

                  if ( ( c == ' ' ) ) {
                    switch ( state ) {

                      case 0: // Looking for HTTP-Version (6.1)
                        setHttpVersion( status.substring( mark, i ) );

                        mark = i + 1;
                        state = 1;
                        break;

                      case 1: // Looking for Status-Code (6.1.1)
                        setStatusCode( Integer.parseInt( status.substring( mark, i ) ) );

                        mark = i + 1;
                        state = 2;
                        break;
                    }
                  }

                  // Check to see if we are at the end of the Status-Line
                  if ( ( i + 1 ) >= status.length() ) {
                    // Finish up the parsing of the last character
                    if ( state == 0 ) {
                      throw new HttpMessageException( "No Status-Code found" );
                    } else if ( state == 1 ) {
                      setStatusCode( Integer.parseInt( status.substring( mark ) ) );
                    } else {
                      // Do we really care about this if we have the Status-Code?
                      setReasonPhrase( status.substring( mark ) );
                    }

                  }

                }

                // Now we parse through the headers. Since this is common to
                // both requests and responses, we use HttpMessage.readHeaders
                // which also reports / returns the number of bytes received
                this.byteCount += readHeaders( in );

                // If there is more data on the line...
                if ( in.available() > 0 ) {
                  // read in the data, assigning it to the body of the message,
                  // and tally the bytes received in the process.
                  this.byteCount += readBody( in );
                } else {
                  if ( getContentLength() == 0 ) {
                    break;
                  } else {
                    // Is there a body or not?

                    // Wait around for only 20 seconds
                    long abortTime = System.currentTimeMillis() + 20000;

                    // Keep checking...
                    while ( abortTime > System.currentTimeMillis() ) {
                      if ( in.available() > 0 ) {
                        this.byteCount += readBody( in );
                        abortTime = 0;
                      }
                    }
                  }
                }

                break;
              } else {
                // This is a valid exception according to the protocol:
                // 6.1 Status-Line
                // The first line of a Response message is the Status-Line,
                // consisting of the protocol version followed by a numeric
                // status code and its associated textual phrase, with each
                // element separated by SP characters. No CR or LF is allowed
                // except in the final CRLF sequence.
                throw new HttpMessageException( "No status line" );
              }

            } catch ( Exception ex ) {
              System.err.println( "Bogus!-->" + ex.getMessage() + "\n" + ExceptionUtil.stackTrace( ex ) );

              throw new HttpMessageException( ex );
            }
          }
        } catch ( IOException ioe ) {
          throw new HttpMessageException( ioe );
        }

        if ( isTimedOut() ) {
          this.setStatusCode( 408 );
          this.setReasonPhrase( "Client timed-out reading response from server" );

          break;
        }
      }

      // record when response parsing ended
      ended = System.currentTimeMillis();
    } else {
      throw new HttpMessageException( "Inputstream was null" );
    }
  }




  /**
   * Method getHttpHeader
   *
   * @return
   */
  public String getHttpHeader() {
    StringBuffer retval = new StringBuffer();

    retval.append( getHttpVersion() );
    retval.append( StringUtil.SP );
    retval.append( getStatusCode() );
    retval.append( StringUtil.SP );
    retval.append( getReasonPhrase( getStatusCode() ) );
    retval.append( StringUtil.CRLF );

    if ( date != null ) {
      setHeader( HttpMessage.DATE, DateUtil.RFC822Format( date ) );
    }

    if ( server != null ) {
      setHeader( HttpMessage.SERVER, server );
    }

    if ( getBody() != null ) {
      setContentLength( getBody().length );
    } else {
      setContentLength( 0 );
    }

    try {
      retval.append( writeHeaders() );
    } catch ( Exception ioe ) {
      // "Should" not happen since we use ByteArrayOutputStream
    }

    return retval.toString();
  }




  /**
   * Method getBytes
   *
   * @return
   */
  public byte[] getBytes() {
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    try {
      getWriter().flush();

      // OK, sometimes goofy coders place a body in a response AND set the body
      // of a response. If that happens, make sure the body they set is
      // appended to the output generated by the writer
      if ( getBody().length > 0 ) {
        baos.write( getBody() );
      }

      // Set the body with the contents of the writer
      setBody( baos.toByteArray() );

      // Generate the HTTP headers
      os.write( getHttpHeader().getBytes( DEFAULT_CHARACTER_ENCODING ) );
      os.write( StringUtil.CRLF.getBytes( DEFAULT_CHARACTER_ENCODING ) );

      if ( getBody() != null ) {
        os.write( getBody() );
      }
    } catch ( UnsupportedEncodingException uee ) {} catch ( IOException ioe ) {} catch ( HttpMessageException ioe ) {}

    return os.toByteArray();
  }




  /**
   * Method toString
   *
   * @return
   */
  public String toString() {
    StringBuffer retval = new StringBuffer();

    retval.append( getHttpHeader() );

    retval.append( StringUtil.CRLF );

    if ( getBody() != null ) {
      try {
        retval.append( new String( getBody(), DEFAULT_CHARACTER_ENCODING ) );
      } catch ( Exception ex ) {
        retval.append( new String( getBody() ) );
      }

    }

    return retval.toString();
  }




  /**
   * Method getDate
   *
   * @return
   */
  public Date getDate() {
    return date;
  }




  /**
   * Method setDate
   *
   * @param date
   */
  public void setDate( Date date ) {
    this.date = date;
  }




  /**
   * Method getStatusCode
   *
   * @return
   */
  public int getStatusCode() {
    return statusCode;
  }




  /**
   * Method setStatusCode
   *
   * @param statusCode
   */
  public void setStatusCode( int statusCode ) {
    this.statusCode = statusCode;
  }




  /**
   * Method getServer
   *
   * @return
   */
  public String getServer() {
    return server;
  }




  /**
   * Method setServer
   *
   * @param server
   */
  public void setServer( String server ) {
    this.server = server;
  }




  /**
   * Method getReasonPhrase
   *
   * @return
   */
  public String getReasonPhrase() {
    return reasonPhrase;
  }




  /**
   * Method setReasonPhrase
   *
   * @param reasonPhrase
   */
  public void setReasonPhrase( String reasonPhrase ) {
    this.reasonPhrase = reasonPhrase;
  }




  /**
   * Returns the number of bytes received per second after data started being
   * received from the server.
   *
   * <p>This time includes the time we wait for the server to calculate the
   * response and the time it took to send it.<p>
   *
   * <p><b>NOTE:</b>Windoze sometimes is lazy and only updates the clock every
   * 250 milliseconds meaning it is possible for elapsed time to be 0 if the
   * started time and ended times fall within a quarter second the system time
   * was not updated. We give it the benefit of the doubt and assign 10ms for
   * an elapsed time. <b>RESULT:</b> the bytes per second rating may be wrong
   * (too generous) on Windoze systems due to poor system time updates.</p>
   *
   * @return The number of bytes received per second including server latency.
   */
  public float getBytesPerSecond() {
    if ( started > 0 ) {
      long elapsed = 0;

      if ( ended > 0 ) {
        elapsed = ended - started;
      } else {
        elapsed = System.currentTimeMillis() - started;
      }

      if ( elapsed == 0 ) {
        // System.err.println("System clock granularity issues for '"+System.getProperty( "os.name" )+"'");
        elapsed = 10;
      }

      if ( elapsed > 0 ) {
        return ( (float)byteCount / (float)elapsed ) * 1000f;
      }

      return 0;
    }

    return -1;
  }




  /**
   * Method getRequestSent
   *
   * @return
   */
  public long getRequestSent() {
    return requestSent;
  }




  /**
   * Method setRequestSent
   *
   * @param requestSent
   */
  public void setRequestSent( long requestSent ) {
    this.requestSent = requestSent;
  }




  /**
   * Method getResponseReceived
   *
   * @return
   */
  public long getResponseReceived() {
    return responseReceived;
  }




  /**
   * Method setResponseReceived
   *
   * @param responseReceived
   */
  public void setResponseReceived( long responseReceived ) {
    this.responseReceived = responseReceived;
  }




  /**
   * Method getServerLatency
   *
   * @return
   */
  public long getServerLatency() {
    if ( ( requestSent > 0 ) && ( responseReceived > 0 ) ) {
      return responseReceived - requestSent;
    }

    return -1;
  }




  /**
   * Method getConnectionTime
   *
   * @return
   */
  public long getConnectionTime() {
    return connectionTime;
  }




  /**
   * Method setConnectionTime
   *
   * @param connectionTime
   */
  public void setConnectionTime( long connectionTime ) {
    this.connectionTime = connectionTime;
  }




  /**
   * Method requestBasicAuthentication
   *
   * @param realm
   */
  public void requestBasicAuthentication( String realm ) {
    setStatusCode( 401 );
    addHeader( WWW_AUTHENTICATE, "Basic realm=\"" + realm + "\"" );
    setContentLength( 0 );
    // setBody(null); //Should there be a body?
  }




  /**
   * Method addCookie
   *
   * @param cookie
   */
  public void addCookie( Cookie cookie ) {
    if ( cookie.getVersion() == 0 ) {
      addHeader( "Set-Cookie", Cookie.toString( cookie ) );
    } else {
      addHeader( "Set-Cookie2", Cookie.toString( cookie ) );
    }
  }




  /**
   * Method getWriter
   *
   * @return
   */
  public PrintWriter getWriter() {
    return writer;
  }




  /**
   * Method getOutputStream
   *
   * @return
   */
  public OutputStream getOutputStream() {
    if ( outputStream != null ) {
      this.outputUserControlled = true;

      // Write all the headers we have so far to the peer as the output stream
      // will contain the body and we cannot send the body without the headers
      ByteArrayOutputStream os = new ByteArrayOutputStream();

      try {
        os.write( getHttpHeader().getBytes( DEFAULT_CHARACTER_ENCODING ) );
        os.write( StringUtil.CRLF.getBytes( DEFAULT_CHARACTER_ENCODING ) );
        outputStream.write( baos.toByteArray() );
        outputStream.flush();
      } catch ( UnsupportedEncodingException uee ) {} catch ( IOException ioe ) {}
    }

    return this.outputStream;
  }




  /**
   * Method setOutputStream
   *
   * @param out
   */
  public void setOutputStream( OutputStream out ) {
    this.outputStream = out;
  }




  /**
   * Method isOutputUserControlled
   *
   * @return
   */
  public boolean isOutputUserControlled() {
    return outputUserControlled;
  }

}