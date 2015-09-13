/*
 * $Id: HttpRequest.java,v 1.4 2004/04/16 12:23:49 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.network.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;

import coyote.commons.StreamUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.network.socket.SocketChannel;
import coyote.commons.security.ISession;
import coyote.loader.log.Log;


/**
 * Represents a request message using the HTTP protocol.
 */
public class HttpRequest extends HttpMessage {
  private String requestMethod = HttpMessage.GET;

  /**
   * Whenever we need to assume the protocol scheme of a request (no-arg send),
   * use this value
   */
  private String requestScheme = "http";
  private String requestPath = "/";
  private String requestQuery = null;

  private String requestHost = null;

  /** The Hashtable of any query variables found on the request URI */
  private Hashtable parameters = null;

  /**
   * The default character encoding for HTTP is US_ASCII according to section
   * 2.2
   */
  private static final String HTTP_ENCODING = "ASCII";

  /** The encoding we use of this request */
  private String encoding = HTTP_ENCODING;

  /** The session object to which this request is related. */
  private ISession session = null;

  /** Empty Hashtable representing no parameters in the request */
  private static final Hashtable NO_PARAMETERS = new Hashtable();




  /**
   * Constructor HttpRequest
   */
  public HttpRequest() {
    // Set timeout on body reads to very small intervals
    setTimeout( 50 );

    // Set headers
    setHeader( ACCEPT, "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, */*" );
    setHeader( ACCEPT_LANGUAGE, "en" );
    setHeader( ACCEPT_ENCODING, "gzip, deflate" );
    setHeader( USER_AGENT, "Mozilla/4.0 (compatible; CoyoteHTTP; Java)" );
    setHeader( CONNECTION, KEEP_ALIVE );
  }




  /**
   * Constructor using a String.
   *
   * @param text
   *
   * @throws HttpMessageException
   */
  public HttpRequest( String text ) throws HttpMessageException {
    try {
      parse( new ByteArrayInputStream( text.getBytes( HTTP_ENCODING ) ) );
    } catch ( UnsupportedEncodingException uec ) {
      // Should not happen unless the JVM is non-standard
      throw new HttpMessageException( "The " + HTTP_ENCODING + " encoding is not supported in this JVM" );
    }

  }




  /**
   * Constructor using an inputstream.
   *
   * @param in
   *
   * @throws HttpMessageException
   */
  public HttpRequest( InputStream in ) throws HttpMessageException {
    parse( in );
  }




  /**
   * Parses the inputstream using the RFC 2616 specification
   *
   * @param in
   *
   * @throws HttpMessageException
   */
  public void parse( InputStream in ) throws HttpMessageException {
    // clear out everything
    reset();

    if ( in != null ) {
      try {
        // Make sure we have data in the stream
        if ( in.available() > 0 ) {
          try {
            // The first line should always be the Request-Line (5.1)
            String request = StreamUtil.readLine( in );

            // RFC2616 4.1 states we "SHOULD ignore any empty line(s) received
            // where a Request-Line is expected".
            while ( ( ( request == null ) || ( ( request != null ) && ( request.length() == 0 ) ) ) && ( in.available() > 0 ) ) {
              request = StreamUtil.readLine( in );
            }

            if ( ( request != null ) && ( request.length() > 0 ) ) {
              Log.append( HTTP, getClass().getName() + ".parse HTTPRequest parsing: '" + request + "'" );

              // RFC2616 section 5.1
              int mark = 0;
              int state = 0;

              for ( int i = 0; i < request.length(); i++ ) {
                char c = request.charAt( i );

                if ( ( c == ' ' ) ) {
                  switch ( state ) {

                    case 0: // Looking for Method (5.1.1)
                      setRequestMethod( request.substring( mark, i ) );
                      mark = i + 1;
                      state = 1;
                      break;

                    case 1: // Looking for Request-URI (5.1.2)
                      setRequestPath( request.substring( mark, i ) );
                      mark = i + 1;
                      state = 2;
                      break;
                  }
                }

                if ( ( i + 1 ) >= request.length() ) {
                  if ( state == 0 ) {
                    throw new HttpMessageException( "No Request-URI found" );
                  } else if ( state == 1 ) {
                    setRequestPath( request.substring( mark ) );
                    setHttpVersion( HTTP_1_0 ); // Assume version 1.0
                  } else {
                    setHttpVersion( request.substring( mark ) );
                  }

                }
              }
            } else {
              throw new HttpMessageException( "No request line" );
            }

            // Now we parse through the headers. Since this is common to both
            // requests and responses, we use HttpMessage.readHeaders(in)
            readHeaders( in );

            // Many requests, SOAP for example, have a body
            if ( in.available() > 0 ) {
              Log.append( HTTP, getClass().getName() + ".parse there are " + in.available() + " bytes available, calling readBody()" );
              readBody( in );
            }
          } catch ( Exception ex ) {
            Log.error( "HttpRequest threw " + ex.getClass().getName() + "\r\n" + ex.getMessage() );
          }
        } else {
          throw new HttpMessageException( "No data available from inputstream" );
        }
      } catch ( IOException ioe ) {
        throw new HttpMessageException( ioe );
      }
    } else {
      throw new HttpMessageException( "Inputstream was null" );
    }
  }




  /**
   * Method getParameter
   *
   * @param name
   *
   * @return
   */
  public String getParameter( String name ) {
    String values[] = getParameterValues( name );
    // According to the servlet specification if there are multiple values for
    // the named parameter, we are to return the first observed value
    return ( values != null ) ? values[0] : null;
  }




  /**
   * Method getParameterNames
   *
   * @return
   */
  public Enumeration getParameterNames() {
    return getParameters().keys();
  }




  /**
   * Method getParameterValues
   *
   * @param name
   *
   * @return
   */
  public String[] getParameterValues( String name ) {
    return (String[])getParameters().get( name );
  }




  /**
   * Method getParameterMap
   *
   * @return
   */
  public Map getParameterMap() {
    return getParameters();
  }




  /**
   * Method getParameters
   *
   * @return
   */
  private synchronized Hashtable getParameters() {
    if ( parameters != null ) {
      return parameters;
    }

    // If this is a post and the content is form data...
    if ( requestMethod.equalsIgnoreCase( POST ) && ( getContentType().equalsIgnoreCase( "application/x-www-form-urlencoded" ) ) ) {
      Log.append( HTTP, "Looking in body for request parameters body='" + new String( getBody() ) + "'" );

      // ...then use the entire body as the query string for parameter data
      parameters = UriUtil.getParametersAsHashtable( new String( getBody() ) );
    } else {
      // If there was a query string detected on the URI...
      if ( requestQuery != null ) {
        Log.append( HTTP, "Looking for request parameters in '" + requestQuery + "'" );

        // ...use it as the query string for the parameters
        parameters = UriUtil.getParametersAsHashtable( requestQuery );
      }

      parameters = NO_PARAMETERS;
    }

    return parameters;
  }




  /**
   * Useful for debugging but NOT ALWAYS for sending over the wire as the body
   * is converted into a string using the locale and the raw bytes will not be
   * represented properly in some cases.
   *
   * <p>Consider posting a PNG image to a server for storage. This method will
   * convert the binary PNG file to a string (well, actualy a string of garbled
   * text) and not be in the proper format.<p>
   *
   * @return A String representing the entire request.
   */
  public String toString() {
    StringBuffer retval = new StringBuffer();

    retval.append( requestMethod );
    retval.append( StringUtil.SP );
    retval.append( requestPath );
    retval.append( StringUtil.SP );
    retval.append( getHttpVersion() );
    retval.append( StringUtil.CRLF );

    if ( getHttpVersion().equals( HttpMessage.HTTP_1_1 ) && ( requestHost != null ) ) {
      super.setHeader( HttpMessage.HOST, requestHost );
    }

    if ( getBody().length > 0 ) {
      super.setContentLength( getBody().length );
    }

    try {
      retval.append( writeHeaders() );
    } catch ( Exception ioe ) {
      // "Should" not happen since we use ByteArrayOutputStream
      ioe.printStackTrace();
    }

    retval.append( StringUtil.CRLF );

    if ( getBody().length > 0 ) {
      retval.append( new String( getBody() ) );
    }

    return retval.toString();
  }




  /**
   * Method send
   *
   * @param uri
   *
   * @return
   *
   * @throws HttpMessageException
   */
  public HttpResponse send( String uri ) throws HttpMessageException {
    try {
      return send( new URI( uri ) );
    } catch ( Exception ex ) {
      throw new HttpMessageException( ex );
    }
  }




  /**
   * Method send
   *
   * @param uri
   *
   * @return
   *
   * @throws HttpMessageException
   */
  public HttpResponse send( URI uri ) throws HttpMessageException {
    if ( uri != null ) {

      // Set our scheme
      String scheme = uri.getScheme();
      if ( StringUtil.isBlank( scheme ) ) {
        scheme = "http";
      }

      this.setRequestScheme( scheme );

      String path = uri.getPath();

      if ( StringUtil.isBlank( path ) ) {
        path = "/";
      } else {
        // If there is no authority, chances are the authority is being mistaken for the path 
        if ( StringUtil.isBlank( uri.getAuthority() ) ) {
          path = "/";
        }

        this.setRequestPath( uri.getPath() );
      }

      this.setRequestPath( path );

      if ( StringUtil.isNotBlank( uri.getQuery() ) ) {
        this.requestPath = this.requestPath + "?" + uri.getQuery();
      }

      int requestPort = uri.getPort();

      // Make sure we have a port
      if ( requestPort == -1 ) {
        if ( StringUtil.isNotBlank( uri.getScheme() ) ) {
          requestPort = UriUtil.getPort( uri.getScheme() );
        }

        // If we still do not have a port, use the default for HTTP
        if ( requestPort < 1 ) {
          requestPort = 80;
        }
      }

      String requestHost = uri.getHost();

      // Make sure we have a host
      if ( StringUtil.isBlank( requestHost ) ) {
        requestHost = StringUtil.head( uri.toString(), ':' );
      }

      this.setRequestHost( requestHost + ":" + requestPort );

      return send();
    } else {
      throw new HttpMessageException( "URI to remote host was null" );
    }
  }




  /**
   * Send the request, returning any response.
   *
   * TODO Change the channel.getOutputStream().write( toString().getBytes( HTTP_ENCODING ) ); to something that will allow binary posts (toString won't cut it)
   *
   * @return The response to the request
   *
   * @throws HttpMessageException
   */
  public HttpResponse send() throws HttpMessageException {
    SocketChannel channel = null;

    URI uri;

    try {
      uri = new URI( requestScheme + "://" + getRequestHost() + getRequestPath() );
    } catch ( URISyntaxException e ) {
      throw new HttpMessageException( e );
    }

    // Open a connection to the given URI
    try {
      // Create a socket channel to the specified host
      channel = SocketChannel.createSocketChannel( uri );

      if ( ( channel != null ) && channel.isOpen() ) {
        // Set our connection information
        remoteAddress = UriUtil.getHostAddress( channel.getRemoteURI() );
        remotePort = channel.getRemoteURI().getPort();

        HttpResponse response = new HttpResponse();
        response.setConnectionTime( channel.getConnectionTime() );

        response.remoteAddress = remoteAddress;
        response.remotePort = remotePort;

        // Send the request over the socket we just opened
        channel.getOutputStream().write( toString().getBytes( HTTP_ENCODING ) );
        channel.getOutputStream().flush();
        Log.append( HTTP, getClass().getName() + "Sent:\n" + toString() );

        response.setRequestSent( System.currentTimeMillis() );

        response.parse( channel.getInputStream() );

        return response;
      } else {
        throw new HttpMessageException( "Could not open connection to '" + uri + "'" );
      }

    } catch ( IOException ioe ) {
      throw new HttpMessageException( ioe );
    }
    finally {
      if ( channel != null ) {
        try {
          channel.close();
        } catch ( Exception ex ) {
          // ignore
        }
      }
    }
  }




  /**
   * Method getRequestMethod
   *
   * @return
   */
  public String getRequestMethod() {
    return requestMethod;
  }




  /**
   * Method setRequestMethod
   *
   * @param requestMethod
   */
  public void setRequestMethod( String requestMethod ) {
    this.requestMethod = requestMethod;
  }




  /**
   * @return the path of the request.
   */
  public String getRequestPath() {
    return requestPath;
  }




  /**
   * Set the path of the request, including any parameters.
   *
   * @param request the entire string after the host portion of the URI, including the parameters if available
   */
  public void setRequestPath( String request ) {
    this.requestPath = request;

    // If there seems to be query parameters on the URI, parse them out
    if ( request.indexOf( '?' ) > -1 ) {
      this.requestQuery = request.substring( request.indexOf( '?' ) + 1 );

      parameters = UriUtil.getParametersAsHashtable( requestQuery );
    }
  }




  /**
   * Method getEncoding
   *
   * @return
   */
  public String getEncoding() {
    return encoding;
  }




  /**
   * Method setEncoding
   *
   * @param encoding
   */
  public void setEncoding( String encoding ) {
    this.encoding = encoding;
  }




  /**
   * @return the host and port (if specified) of the request
   */
  public String getRequestHost() {
    return requestHost;
  }




  /**
   * Set the host (and optional port) of the request.
   *
   * @param requestHost the host (and optional port) of the request.
   */
  public void setRequestHost( String requestHost ) {
    this.requestHost = requestHost;
  }




  /**
   * @return the scheme (e.g. http, https, ftp, tcp) of the request.
   */
  public String getRequestScheme() {
    return requestScheme;
  }




  /**
   * Set the scheme (e.g. http, https, ftp, tcp) of the request.
   *
   * @param the scheme (e.g. http, https, ftp, tcp)
   */
  public void setRequestScheme( String requestScheme ) {
    this.requestScheme = requestScheme;
  }




  /**
   * Return the value of the query parameter with the given name.
   *
   * @return
   */
  // public String getQueryParamenter( String name )
  // {
  // return (String)parameters.get( name );
  // }

  /**
   * Method getQueryParameterNameIterator
   *
   * @return
   */
  // public Iterator getQueryParameterNameIterator()
  // {
  // return parameters.keySet().iterator();
  // }

  /**
   * Method getQueryParameterValueIterator
   *
   * @return
   */
  // public Iterator getQueryParameterValueIterator()
  // {
  // return parameters.values().iterator();
  // }

  /**
   * Method getSession
   *
   * @return
   */
  public ISession getSession() {
    return session;
  }




  /**
   * Method setSession
   *
   * @param session
   */
  public void setSession( ISession session ) {
    this.session = session;
  }




  /**
   * Method getRequestQuery
   *
   * @return
   */
  public String getRequestQuery() {
    return requestQuery;
  }
}