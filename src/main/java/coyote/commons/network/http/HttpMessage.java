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
import java.net.InetAddress;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

import coyote.commons.ByteUtil;
import coyote.commons.StreamUtil;
import coyote.commons.StringUtil;
import coyote.commons.UriUtil;
import coyote.commons.security.Credentials;
import coyote.loader.log.Log;


/**
 * Represents the base class of all HTTP messages.
 */
public class HttpMessage {

  /** This is the output stream we use to generate the body of the HttpMessage */
  protected ByteArrayOutputStream output = new ByteArrayOutputStream();

  /**
   * This is the input stream from which we can read the remainder of the
   * message
   */
  protected InputStream input = null;

  public static final String CHARSET = "charset=";

  public static final String CONTENT_LENGTH = "Content-Length";

  public static final String CONTENT_ENCODING = "Content-Encoding";

  public static final String CONTENT_TYPE = "Content-Type";

  public static final String CONTENT_LOCATION = "Content-Location";

  public static final String CONTENT_ID = "Content-ID";

  public static final String EXPECT_HEADER = "Expect";

  public static final String ACCEPT_LANGUAGE = "Accept-Language";

  public static final String ACCEPT = "Accept";

  public static final String ACCEPT_ENCODING = "Accept-Encoding";

  public static final String MULTIPART_CONTENT_TYPE = "Multipart/Related";

  public static final String HOST = "Host";

  public static final String RANGE = "Range";

  public static final String CONNECTION = "Connection";
  public static final String KEEP_ALIVE = "Keep-Alive";
  public static final String CLOSE = "Close";

  public static final String SERVER = "Server";

  public static final String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

  public static final String HTTP_1_0 = "HTTP/1.0";

  public static final String HTTP_1_1 = "HTTP/1.1";

  public static final String COOKIE_LISTENER = "cookieListener";

  public static final String SET_COOKIE_0 = "Set-Cookie";

  public static final String SET_COOKIE_1 = "Set-Cookie2";

  public static final String COOKIE = "Cookie";

  public static final String USER_AGENT = "User-Agent";

  public static final String TEXT_PLAIN = "text/plain";

  public static final String TEXT_XML = "text/xml";

  public static final String TEXT_XML_UTF_8 = "text/xml; charset=UTF-8";

  public static final String TEXT_HTML_UTF_8 = "text/html; charset=UTF-8";

  public static final String TEXT_HTML = "text/html";

  public static final String POST = "POST";
  public static final String PUT = "PUT";
  public static final String HEAD = "HEAD";
  public static final String DELETE = "DELETE";
  public static final String OPTIONS = "OPTIONS";
  public static final String CONNECT = "CONNECT";
  public static final String GET = "GET";

  public static final String DATE = "Date";

  public static final int DEFAULT_HTTP_PORT = 80;

  public static final int DEFAULT_HTTPS_PORT = 443;

  public static final String AUTHORIZATION = "Authorization";

  public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

  public static final String PROXY_CONNECTION = "Proxy-Connection";

  public static final String BASIC = "Basic";

  public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

  public static final String OK = "OK";

  public static final String LAST_MODIFIED = "Last-Modified";

  public static final String ACCEPT_RANGES = "Accept-Ranges";

  public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

  public static final String IF_NONE_MATCH = "If-None-Match";

  public static final String DEFAULT_MIME_TYPE = "text/plain";

  public static final String TRANSFER_ENCODING = "Transfer-Encoding";

  public static final String CHUNKED = "chunked";

  public static final String MIME_BOUNDARY = "MIME_boundary";

  protected static final String MIME_VERSION = "MIME-Version";

  public static final String DEFAULT_CONTENT_TYPE = TEXT_HTML;

  protected static final byte[] CONTINUATION_STATUS = "HTTP/1.1 100 Continue\r\n\r\n".getBytes();
  protected static final String CONTINUE_EXPECTATION = "100-continue";
  protected static final String AUTH_USER = "authUser";
  protected static final String AUTH_PASSWORD = "authPassword";
  protected static final String PROXY_HOST = "proxyHost";
  protected static final String PROXY_PORT = "proxyPort";
  protected static final String PROXY_USER = "proxyUser";
  protected static final String PROXY_PASSWORD = "proxyPassword";
  protected static final String PROXY_HOST_PROPERTY = "http.proxyHost";
  protected static final String PROXY_PORT_PROPERTY = "http.proxyPort";
  protected static final String PROXY_USER_PROPERTY = "http.proxyUser";
  protected static final String PROXY_PASSWORD_PROPERTY = "http.proxyPassword";
  protected static final String HTTP_REQUEST = "httpRequest";
  protected static final String HTTP_RESPONSE = "httpResponse";
  private static final byte SEPARATOR[] = ": ".getBytes();
  private static final String NO_HEADERS[][] = new String[0][0];
  private static TimeZone gmt = TimeZone.getTimeZone( "GMT" );
  private static SimpleDateFormat dateFormatter1;
  private static final Hashtable<Integer, String> statusTable = new Hashtable<Integer, String>();
  private static final Hashtable<String, String> mimeTypes = new Hashtable<String, String>();
  private String HttpVersion = null;
  protected static SimpleDateFormat dateFormatter2;
  protected static SimpleDateFormat dateFormatter3;
  protected String headers[][];
  protected CookieJar cookieJar = new CookieJar();
  protected int timeout = 60000;
  protected long abortTime = 0;
  protected static final long HTTP = Log.getCode( "HTTP" );
  protected InetAddress remoteAddress = null;
  protected int remotePort = -1;

  /*
   * Static initializer
   */
  static {
    dateFormatter1 = new SimpleDateFormat( "EEE, dd MMM yyyy HH:mm:ss z", Locale.US );

    dateFormatter1.setTimeZone( gmt );

    dateFormatter2 = new SimpleDateFormat( "EEEEE, dd-MMM-yy HH:mm:ss z", Locale.US );

    dateFormatter2.setTimeZone( gmt );

    dateFormatter3 = new SimpleDateFormat( "EEE MMM dd HH:mm:ss yyyy", Locale.US );

    dateFormatter3.setTimeZone( gmt );

    addStatusCode( 100, "Continue" );
    addStatusCode( 101, "Switching Protocols" );
    addStatusCode( 200, "OK" );
    addStatusCode( 201, "Created" );
    addStatusCode( 202, "Accepted" );
    addStatusCode( 203, "Non-Authoritative Information" );
    addStatusCode( 204, "No Content" );
    addStatusCode( 205, "Reset Content" );
    addStatusCode( 206, "Partial Content" );
    addStatusCode( 300, "Multiple Choices" );
    addStatusCode( 301, "Moved Permanently" );
    addStatusCode( 302, "Found" );
    addStatusCode( 303, "See Other" );
    addStatusCode( 304, "Not Modified" );
    addStatusCode( 305, "Use Proxy" );
    addStatusCode( 400, "Bad Request" );
    addStatusCode( 401, "Unauthorized" );
    addStatusCode( 402, "Payment Required" );
    addStatusCode( 403, "Forbidden" );
    addStatusCode( 404, "Not Found" );
    addStatusCode( 405, "Method Not Allowed" );
    addStatusCode( 406, "Not Acceptable" );
    addStatusCode( 407, "Proxy Authentication Required" );
    addStatusCode( 408, "Request Time-out" );
    addStatusCode( 409, "Conflict" );
    addStatusCode( 410, "Gone" );
    addStatusCode( 411, "Length Required" );
    addStatusCode( 412, "Precondition Failed" );
    addStatusCode( 413, "Request Entity Too Large" );
    addStatusCode( 414, "Request-URI Too Large" );
    addStatusCode( 415, "Unsupported Media Type" );
    addStatusCode( 500, "Internal Server Error" );
    addStatusCode( 501, "Not Implemented" );
    addStatusCode( 502, "Bad Gateway" );
    addStatusCode( 503, "Service Unavailable" );
    addStatusCode( 504, "Gateway Time-out" );
    addStatusCode( 505, "HTTP Version not supported" );

    addMimeType( "ai", "application/postscript" );
    addMimeType( "aif", "audio/x-aiff" );
    addMimeType( "aifc", "audio/x-aiff" );
    addMimeType( "aiff", "audio/x-aiff" );
    addMimeType( "asc", "text/plain" );
    addMimeType( "au", "audio/basic" );
    addMimeType( "avi", "video/x-msvideo" );
    addMimeType( "bcpio", "application/x-bcpio" );
    addMimeType( "bin", "application/octet-stream" );
    addMimeType( "c", "text/plain" );
    addMimeType( "cc", "text/plain" );
    addMimeType( "ccad", "application/clariscad" );
    addMimeType( "cdf", "application/x-netcdf" );
    addMimeType( "class", "application/octet-stream" );
    addMimeType( "cpio", "application/x-cpio" );
    addMimeType( "cpt", "application/mac-compactpro" );
    addMimeType( "csh", "application/x-csh" );
    addMimeType( "css", "text/css" );
    addMimeType( "dcr", "application/x-director" );
    addMimeType( "dir", "application/x-director" );
    addMimeType( "dms", "application/octet-stream" );
    addMimeType( "doc", "application/msword" );
    addMimeType( "drw", "application/drafting" );
    addMimeType( "dvi", "application/x-dvi" );
    addMimeType( "dwg", "application/acad" );
    addMimeType( "dxf", "application/dxf" );
    addMimeType( "dxr", "application/x-director" );
    addMimeType( "eps", "application/postscript" );
    addMimeType( "etx", "text/x-setext" );
    addMimeType( "exe", "application/octet-stream" );
    addMimeType( "ez", "application/andrew-inset" );
    addMimeType( "f", "text/plain" );
    addMimeType( "f90", "text/plain" );
    addMimeType( "fli", "video/x-fli" );
    addMimeType( "gif", "image/gif" );
    addMimeType( "gtar", "application/x-gtar" );
    addMimeType( "gz", "application/x-gzip" );
    addMimeType( "h", "text/plain" );
    addMimeType( "hdf", "application/x-hdf" );
    addMimeType( "hh", "text/plain" );
    addMimeType( "hqx", "application/mac-binhex40" );
    addMimeType( "htm", "text/html" );
    addMimeType( "html", "text/html" );
    addMimeType( "ice", "x-conference/x-cooltalk" );
    addMimeType( "ief", "image/ief" );
    addMimeType( "iges", "model/iges" );
    addMimeType( "igs", "model/iges" );
    addMimeType( "ips", "application/x-ipscript" );
    addMimeType( "ipx", "application/x-ipix" );
    addMimeType( "jpe", "image/jpeg" );
    addMimeType( "jpeg", "image/jpeg" );
    addMimeType( "jpg", "image/jpeg" );
    addMimeType( "js", "application/x-javascript" );
    addMimeType( "kar", "audio/midi" );
    addMimeType( "latex", "application/x-latex" );
    addMimeType( "lha", "application/octet-stream" );
    addMimeType( "lsp", "application/x-lisp" );
    addMimeType( "lzh", "application/octet-stream" );
    addMimeType( "m", "text/plain" );
    addMimeType( "man", "application/x-troff-man" );
    addMimeType( "me", "application/x-troff-me" );
    addMimeType( "mesh", "model/mesh" );
    addMimeType( "mid", "audio/midi" );
    addMimeType( "midi", "audio/midi" );
    addMimeType( "mif", "application/vnd.mif" );
    addMimeType( "mime", "www/mime" );
    addMimeType( "mov", "video/quicktime" );
    addMimeType( "movie", "video/x-sgi-movie" );
    addMimeType( "mp2", "audio/mpeg" );
    addMimeType( "mp3", "audio/mpeg" );
    addMimeType( "mpe", "video/mpeg" );
    addMimeType( "mpeg", "video/mpeg" );
    addMimeType( "mpg", "video/mpeg" );
    addMimeType( "mpga", "audio/mpeg" );
    addMimeType( "ms", "application/x-troff-ms" );
    addMimeType( "msh", "model/mesh" );
    addMimeType( "nc", "application/x-netcdf" );
    addMimeType( "oda", "application/oda" );
    addMimeType( "pbm", "image/x-portable-bitmap" );
    addMimeType( "pdb", "chemical/x-pdb" );
    addMimeType( "pdf", "application/pdf" );
    addMimeType( "pgm", "image/x-portable-graymap" );
    addMimeType( "pgn", "application/x-chess-pgn" );
    addMimeType( "png", "image/png" );
    addMimeType( "pnm", "image/x-portable-anymap" );
    addMimeType( "pot", "application/mspowerpoint" );
    addMimeType( "ppm", "image/x-portable-pixmap" );
    addMimeType( "pps", "application/mspowerpoint" );
    addMimeType( "ppt", "application/mspowerpoint" );
    addMimeType( "ppz", "application/mspowerpoint" );
    addMimeType( "pre", "application/x-freelance" );
    addMimeType( "prt", "application/pro_eng" );
    addMimeType( "ps", "application/postscript" );
    addMimeType( "qt", "video/quicktime" );
    addMimeType( "ra", "audio/x-realaudio" );
    addMimeType( "ram", "audio/x-pn-realaudio" );
    addMimeType( "ras", "image/cmu-raster" );
    addMimeType( "rgb", "image/x-rgb" );
    addMimeType( "rm", "audio/x-pn-realaudio" );
    addMimeType( "roff", "application/x-troff" );
    addMimeType( "rpm", "audio/x-pn-realaudio-plugin" );
    addMimeType( "rtf", "text/rtf" );
    addMimeType( "rtx", "text/richtext" );
    addMimeType( "scm", "application/x-lotusscreencam" );
    addMimeType( "set", "application/set" );
    addMimeType( "sgm", "text/sgml" );
    addMimeType( "sgml", "text/sgml" );
    addMimeType( "sh", "application/x-sh" );
    addMimeType( "shar", "application/x-shar" );
    addMimeType( "silo", "model/mesh" );
    addMimeType( "sit", "application/x-stuffit" );
    addMimeType( "skd", "application/x-koan" );
    addMimeType( "skm", "application/x-koan" );
    addMimeType( "skp", "application/x-koan" );
    addMimeType( "skt", "application/x-koan" );
    addMimeType( "smi", "application/smil" );
    addMimeType( "smil", "application/smil" );
    addMimeType( "snd", "audio/basic" );
    addMimeType( "sol", "application/solids" );
    addMimeType( "spl", "application/x-futuresplash" );
    addMimeType( "src", "application/x-wais-source" );
    addMimeType( "step", "application/STEP" );
    addMimeType( "stl", "application/SLA" );
    addMimeType( "stp", "application/STEP" );
    addMimeType( "sv4cpio", "application/x-sv4cpio" );
    addMimeType( "sv4crc", "application/x-sv4crc" );
    addMimeType( "swf", "application/x-shockwave-flash" );
    addMimeType( "t", "application/x-troff" );
    addMimeType( "tar", "application/x-tar" );
    addMimeType( "tcl", "application/x-tcl" );
    addMimeType( "tex", "application/x-tex" );
    addMimeType( "texi", "application/x-texinfo" );
    addMimeType( "texinf", "application/x-texinfo" );
    addMimeType( "tif", "image/tiff" );
    addMimeType( "tiff", "image/tiff" );
    addMimeType( "tr", "application/x-troff" );
    addMimeType( "tsi", "audio/TSP-audio" );
    addMimeType( "tsp", "application/dsptype" );
    addMimeType( "tsv", "text/tab-separated-values" );
    addMimeType( "txt", "text/plain" );
    addMimeType( "unv", "application/i-deas" );
    addMimeType( "ustar", "application/x-ustar" );
    addMimeType( "vcd", "application/x-cdlink" );
    addMimeType( "vda", "application/vda" );
    addMimeType( "viv", "video/vnd.vivo" );
    addMimeType( "vivo", "video/vnd.vivo" );
    addMimeType( "vrml", "model/vrml" );
    addMimeType( "wav", "audio/x-wav" );
    addMimeType( "wrl", "model/vrml" );
    addMimeType( "xbm", "image/x-xbitmap" );
    addMimeType( "xlc", "application/vnd.ms-excel" );
    addMimeType( "xll", "application/vnd.ms-excel" );
    addMimeType( "xlm", "application/vnd.ms-excel" );
    addMimeType( "xls", "application/vnd.ms-excel" );
    addMimeType( "xlw", "application/vnd.ms-excel" );
    addMimeType( "xml", "text/xml" );
    addMimeType( "xpm", "image/x-xpixmap" );
    addMimeType( "xwd", "image/x-xwindowdump" );
    addMimeType( "xyz", "chemical/x-pdb" );
    addMimeType( "zip", "application/zip" );
  }




  /**
   * Constructor HttpMessage
   */
  public HttpMessage() {
    HttpVersion = HTTP_1_1;
    headers = NO_HEADERS;
  }




  /**
   * Method addStatusCode
   *
   * @param statusCode
   * @param message
   */
  private static void addStatusCode( int statusCode, String message ) {
    statusTable.put( new Integer( statusCode ), message );
  }




  /**
   * Method getReasonPhrase
   *
   * @param statusCode
   *
   * @return
   */
  public static String getReasonPhrase( int statusCode ) {
    return (String)statusTable.get( new Integer( statusCode ) );
  }




  /**
   * Implement type suffix to mime type mappings
   *
   * @param extension, the type extension without a period(class, txt)
   * @param type, the mime type string
   */
  public static void addMimeType( String extension, String type ) {
    if ( ( extension != null ) && ( type != null ) ) {
      mimeTypes.put( extension.toLowerCase(), type.toLowerCase() );
    }
  }




  /**
   * Method getMimeType
   *
   * @param extension
   *
   * @return
   */
  public static String getMimeType( String extension ) {
    if ( mimeTypes.containsKey( extension.toLowerCase() ) ) {
      return (String)mimeTypes.get( extension.toLowerCase() );
    }

    return "application/octet-stream";
  }




  /**
   * Method getMimeType
   *
   * @param extension
   * @param dflt
   *
   * @return
   */
  public static String getMimeType( String extension, String dflt ) {
    if ( mimeTypes.containsKey( extension.toLowerCase() ) ) {
      return mimeTypes.get( extension.toLowerCase() );
    }

    return dflt;
  }




  /**
   * Method startTimer
   */
  protected void startTimer() {
    abortTime = System.currentTimeMillis() + timeout;
  }




  /**
   * Method isTimedOut
   *
   * @return
   */
  protected boolean isTimedOut() {
    return System.currentTimeMillis() >= abortTime;
  }




  /**
   * Method readHeaders
   *
   * @param in
   *
   * @return
   *
   * @throws IOException
   */
  public int readHeaders( InputStream in ) throws IOException {
    int byteCount = 0;

    if ( in.available() > 0 ) {
      do {
        String line = StreamUtil.readLine( in );

        if ( ( line != null ) && ( line.length() > 0 ) ) {
          // Increment the bytes received + CRLF sequence that ended the line
          byteCount += line.length() + 2;

          int i = line.indexOf( ':' );

          if ( i == -1 ) {
            throw new IOException( "illegal HTTP header: '" + line + "'" );
          }

          String name = line.substring( 0, i ).trim();
          String data = line.substring( i + 1, line.length() ).trim();
          String as[][] = new String[headers.length + 1][];
          System.arraycopy( headers, 0, as, 0, headers.length );

          as[headers.length] = ( new String[] { name, data } );
          headers = as;

          Log.append( HTTP, getClass().getName() + "readHeaders Name=" + name + " Value=" + data );

          // If the header is a cookie...
          if ( name.equals( COOKIE ) ) {
            // ...parse it into an array of cookie objects and place them in
            // the cookie jar
            cookieJar.addCookies( Cookie.parse( line ) );
          }

        } else {
          // count the CRLF terminater that represents the empty line
          byteCount += 2;

          // Empty line means the headers are complete
          return byteCount;
        }
      }
      while ( true );
    }

    return byteCount;
  }




  /**
   * Read the body of the message from the given input stream.
   *
   * <p>Although not normally a problem for requests, responses from servers
   * can be quite slow in their processing and transmission. For this reason we
   * need to provide a way to time-out and not block on slow or frozen
   * responses.</p>
   *
   * <p>On requests, body reads usually take only one iteration, and while the
   * method may time-out in the code due to a vary small timeout value, all the
   * data usually get read from the input stream before the timeout check is
   * performed.</p>
   *
   * @param in the InputStream from which the body of the message is to be read
   *
   * @return
   *
   * @throws IOException if the body could not be completly read in from the InputStream
   */
  public int readBody( InputStream in ) throws IOException {
    // tally how many bytes we receive
    int byteCount = 0;

    // Get the length from the headers if it exists
    int expectedLength = getContentLength();
    Log.append( HTTP, getClass().getName() + ".readBody Expecting " + expectedLength + " bytes based on ContentLength header" );

    // Create a new output array representing our body
    output = new ByteArrayOutputStream();

    // Determine when we are to stop trying to read the body
    long abortTime = System.currentTimeMillis() + getTimeout();

    // if we have a body to read or the length was not defined in the header...
    if ( expectedLength != 0 ) {
      // while what we have received so far is less than expected, or the
      // length of the body was not specified...
      while ( ( output.size() < expectedLength ) || ( expectedLength == -1 ) ) {
        // If there is data "waiting on the wire"
        if ( in.available() > 0 ) {
          byte[] chunk;

          if ( ( expectedLength < 0 ) || ( ( expectedLength > 0 ) && ( in.available() <= ( expectedLength - output.size() ) ) ) ) {
            // make the chunk big enough for everything on the wire (or the
            // socket buffer)
            chunk = new byte[in.available()];
          } else {
            // make the chunk only as big as we need to read the expected size
            chunk = new byte[expectedLength - output.size()];
          }

          // read the data into the chunk
          int bytesRead = in.read( chunk );
          // increment our total byte count by the number of bytes read
          byteCount += bytesRead;

          // Write the chunk to the output stream that represents our body
          output.write( chunk, 0, bytesRead );
        }

        if ( expectedLength == -1 ) {
          // we have to block since we do not know how long the data will be,
          // and the only way determine if the stream is closed is to read the
          // next character.
          int c = in.read();

          if ( c > -1 ) {
            output.write( c );
          } else {
            // -1 means the stream is closed, and probably the connection is
            // closed as well
            break;
          }
        }

        // If the time has passed for our read
        if ( System.currentTimeMillis() >= abortTime ) {
          // we timed-out!
          break;
        }
      }

    }

    // return the number of bytes read in
    return byteCount;
  }




  /**
   * Write the headers of the HTTP message.
   *
   * @param outputstream
   *
   * @throws IOException
   */
  public void writeHeaders( OutputStream outputstream ) throws IOException {
    for ( int i = 0; i < headers.length; i++ ) {
      if ( ( headers[i][0] != null ) && ( headers[i][1] != null ) ) {
        outputstream.write( headers[i][0].getBytes() );
        outputstream.write( SEPARATOR );
        outputstream.write( headers[i][1].getBytes() );
        outputstream.write( StringUtil.CRLF.getBytes() );
      }
    }

  }




  /**
   * Method writeHeaders
   *
   * @return
   *
   * @throws IOException
   */
  public String writeHeaders() throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    this.writeHeaders( baos );

    return baos.toString();
  }




  /**
   * Method clearHeaders
   */
  public void clearHeaders() {
    headers = NO_HEADERS;
  }




  /**
   * Return the value of the named MIME header.
   *
   * @param data The name of the MIME header to retrieve.
   *
   * @return The value of the named MIME header or null if it does not exist.
   */
  public String getHeader( String data ) {
    for ( int i = 0; i < headers.length; i++ ) {
      if ( data.equalsIgnoreCase( headers[i][0] ) ) {
        return headers[i][1];
      }
    }

    return null;
  }




  /**
   * Method getHeaders
   *
   * @param data
   *
   * @return
   */
  public Enumeration getHeaders( String data ) {
    Vector vector = new Vector();

    for ( int i = 0; i < headers.length; i++ ) {
      if ( data.equalsIgnoreCase( headers[i][0] ) ) {
        vector.addElement( headers[i][1] );
      }
    }

    return vector.elements();
  }




  /**
   * Method containsHeader
   *
   * @param data
   *
   * @return
   */
  public boolean containsHeader( String data ) {
    return getHeader( data ) != null;
  }




  /**
   * Method getHeaderNames
   *
   * @return
   */
  public Enumeration getHeaderNames() {
    Vector vector = new Vector();

    for ( int i = 0; i < headers.length; i++ ) {
      if ( !vector.contains( headers[i][0] ) ) {
        vector.addElement( headers[i][0] );
      }
    }

    return vector.elements();
  }




  /**
   * Replace the header with the given name with the given value.
   *
   * <p>If the named header does not exist, then it is added to the list of
   * headers.</p>
   *
   * @param name the name of the header to set
   * @param value the value to add to the named header
   */
  public void setHeader( String name, String value ) {
    for ( int i = 0; i < headers.length; i++ ) {
      if ( name.equalsIgnoreCase( headers[i][0] ) ) {
        headers[i][0] = name;
        headers[i][1] = value;

        return;
      }
    }

    addHeader( name, value );
  }




  /**
   * Add a named header and its value to the list of headers.
   *
   * <p>Using this method will just append the name-value pair to the list
   * without checking for duplicates. This method allows for duplicate headers
   * to exist in the message, so you might want to be careful how you use
   * this.</p>
   *
   * @param name the name of the header to add
   * @param value the value to add to the named header
   */
  public void addHeader( String name, String value ) {
    String as[][] = new String[headers.length + 1][];
    System.arraycopy( headers, 0, as, 0, headers.length );

    as[headers.length] = ( new String[] { name, value } );
    headers = as;
  }




  /**
   * Return the value of the given header as an integer primitive
   *
   * @param name the name of the header to parse into a integer value
   *
   * @return the integer value of the given named header or -1 if not defined.
   */
  public int getIntHeader( String name ) {
    String value = getHeader( name );
    return ( value != null ) ? Integer.parseInt( value ) : -1;
  }




  /**
   * Set the header with the given name with a string value of the given integer
   *
   * @param name the name of the header to set.
   * @param value the integer value to set to the named header
   */
  public void setIntHeader( String name, int value ) {
    setHeader( name, String.valueOf( value ) );
  }




  /**
   * Method addIntHeader
   *
   * @param data
   * @param i
   */
  public void addIntHeader( String data, int i ) {
    addHeader( data, String.valueOf( i ) );
  }




  /**
   * Method getDateHeader
   *
   * @param data
   *
   * @return
   */
  public long getDateHeader( String data ) {
    String s1 = getHeader( data );

    if ( s1 == null ) {
      return -1L;
    }

    try {
      long l;

      synchronized( dateFormatter1 ) {
        Date date = dateFormatter1.parse( s1, new ParsePosition( 0 ) );
        l = date.getTime();
      }

      return l;
    } catch ( Exception exception ) {}

    long l1;

    synchronized( dateFormatter2 ) {
      Date date1 = dateFormatter2.parse( s1, new ParsePosition( 0 ) );
      l1 = date1.getTime();
    }

    return l1;
  }




  /**
   * Adds a cookie to the HttpMessage
   *
   * <p>While there is nothing in any specification that limits the existence
   * of duplicate cookie names, we assume that since we retrieve cookies by
   * their names we should make sure there are no duplicates. This utilizes a
   * HashMap keyed on the cookie name to ensure we only have one cookie of a
   * particular name.</p>
   *
   * @param cookie the Cookie object to store in the message
   */
  public void addCookie( Cookie cookie ) {
    if ( ( cookie.getName() != null ) && ( cookie.getName().length() > 0 ) ) {
      cookieJar.addCookie( cookie );
    } else {
      throw new IllegalArgumentException( "Can not add un-named cookie" );
    }
  }




  /**
   * Retrieves a cookie from the message with the given name.
   *
   * <p>If more than one cookie exists with the given name, only the first
   * match will be returned. This is usually the first such named cookie in the
   * cookie header.</p>
   *
   * @param name The String representation of the name of the cookie to retrieve from the message.
   *
   * @return The Cookie object representing the named cookie or null if not found.
   */
  public Cookie getCookie( String name ) {
    return cookieJar.getCookie( name );
  }




  /**
   * Retrieves all the cookies from the message with the given name.
   *
   * <p>Even if there are no cookies with the given name, the return value will
   * not be null, it will just be an array with no elements, ie. Cookie[0].</p>
   *
   * @param name The String representation of the name of the cookie to retrieve from the message.
   *
   * @return The array of Cookie objects representing the named cookie. If no cookies are found with the given name the array will be empty.
   */
  public Cookie[] getCookies( String name ) {
    return cookieJar.getCookies( name );
  }




  /**
   * Add a basic authorization header to the message.
   *
   * @param credentials the authorization credentials to place in the header
   */
  public void addBasicAuthorization( Credentials credentials ) {
    String pem = ByteUtil.toBase64( ( credentials.getAccount() + ":" + credentials.getPassword() ).getBytes() );
    addHeader( AUTHORIZATION, "Basic " + pem );
  }




  /**
   * Method getAuthorization
   *
   * @return
   */
  public Credentials getBasicAuthorization() {
    Credentials cred = null;

    try {
      if ( getHeader( AUTHORIZATION ) != null ) {
        String pem = getHeader( AUTHORIZATION );
        java.util.StringTokenizer stringtokenizer = new java.util.StringTokenizer( pem );
        String type = stringtokenizer.nextToken(); // go past "Basic"

        if ( type.equalsIgnoreCase( "Basic" ) ) {
          String field = new String( ByteUtil.fromBase64( stringtokenizer.nextToken() ) );

          if ( field != null ) {
            int delim = field.indexOf( ':' );

            if ( delim > 0 ) {
              cred = new Credentials( field.substring( 0, delim ), field.substring( delim + 1 ) );
            }
          }
        } else {
          Log.warn( "Got an authorization type of '" + type + "'" );
        }
      }
    } catch ( Exception e ) {
      e.printStackTrace();
    }

    return cred;
  }




  /**
   * Method addDateHeader
   *
   * @param data
   * @param l
   */
  public void addDateHeader( String data, long l ) {
    synchronized( dateFormatter1 ) {
      addHeader( data, dateFormatter1.format( new Date( l ) ) );
    }
  }




  /**
   * Method getHttpVersion
   *
   * @return
   */
  public String getHttpVersion() {
    return HttpVersion;
  }




  /**
   * Method setHttpVersion
   *
   * @param ver
   */
  public void setHttpVersion( String ver ) {
    HttpVersion = ver;
  }




  /**
   * Return the expected length of the body of the HttpMessage.
   *
   * @return an integer value of the stated length of the message body, or -1 if
   *         not defined.
   */
  public int getContentLength() {
    return getIntHeader( CONTENT_LENGTH );
  }




  /**
   * Set the expected length of the body of the HttpMessage.
   *
   * @param len the integer value of the stated length of the message body
   */
  public void setContentLength( int len ) {
    setIntHeader( CONTENT_LENGTH, len );
  }




  /**
   * Method getContentType
   *
   * @return
   */
  public String getContentType() {
    return getHeader( CONTENT_TYPE );
  }




  /**
   * Method setContentType
   *
   * @param data
   */
  public void setContentType( String data ) {
    setHeader( CONTENT_TYPE, data );
  }




  /**
   * Method getCharacterEncoding
   *
   * @return
   */
  public String getCharacterEncoding() {
    String data = getContentType();

    if ( data == null ) {
      return DEFAULT_CHARACTER_ENCODING;
    }

    int i = data.indexOf( CHARSET );

    if ( i == -1 ) {
      return DEFAULT_CHARACTER_ENCODING;
    }

    i += CHARSET.length();

    StringBuffer stringbuffer = new StringBuffer();

    for ( ; ( i < data.length() ) && ( data.charAt( i ) != ';' ); stringbuffer.append( data.charAt( i++ ) ) );

    String temp = stringbuffer.toString();

    if ( temp.startsWith( "\"" ) ) {
      return temp.substring( 1, temp.length() - 1 );
    } else {
      return temp;
    }
  }




  /**
   * Method isKeepAlive
   *
   * @return
   */
  public boolean isKeepAlive() {
    String status = getHeader( CONNECTION );

    if ( status != null ) {
      if ( getHttpVersion().equals( HTTP_1_0 ) ) {
        return status.equalsIgnoreCase( KEEP_ALIVE );
      } else {
        return !status.equalsIgnoreCase( CLOSE );
      }
    }

    return false;
  }




  /**
   * Method getLocales
   *
   * @return
   */
  public Enumeration getLocales() {
    return null;
  }




  /**
   * Method setLocale
   *
   * @param locale
   */
  public void setLocale( Locale locale ) {
    setHeader( ACCEPT_LANGUAGE, locale.toString() );
  }




  /**
   * Method getLocale
   *
   * @return
   */
  public Locale getLocale() {
    return (Locale)getLocales().nextElement();
  }




  /**
   * Method reset
   */
  public void reset() {
    clearHeaders();
  }




  /**
   * Method getOutput
   *
   * @return
   */
  public OutputStream getOutput() {
    return output;
  }




  /**
   * @return the number of milliseconds to wait for a response from the other 
   *         end of the connection.
   */
  public int getTimeout() {
    return timeout;
  }




  /**
   * Set the amount of time the we wait for a response from the other end. 
   *
   * @param timeout the number of milliseconds to wait.
   */
  public void setTimeout( int timeout ) {
    this.timeout = timeout;
  }




  /**
   * Return the body of the HttpMessage.
   *
   * <p>This will ALWAYS return a byte array even if there is no body. This
   * makes checking for content length simpler as you won't have to check for
   * null and size.</p>
   *
   * <p>If there is no body to the message, a byte array with zero elements
   * ( byte[0] ) will be returned.</p>
   *
   * @return a byte[] containing the body of the message as it was received or 
   *         a byte[] of zero size if there is no body.
   */
  public byte[] getBody() {
    if ( output != null ) {
      return output.toByteArray();
    } else {
      return new byte[0];
    }
  }




  /**
   * Set the body of the message to the given data.
   *
   * <p>If the data is null or contains no data, the body of the message will
   * contain no bytes, but will not be set to null.</p>
   *
   * @param data the byte array representing the data to place in the body of 
   *             the message.
   *
   * @throws HttpMessageException
   */
  public void setBody( byte[] data ) throws HttpMessageException {
    if ( data != null ) {
      output = new ByteArrayOutputStream( data.length );

      try {
        output.write( data );
      } catch ( IOException ioe ) {
        throw new HttpMessageException( ioe );
      }
    } else {
      output = new ByteArrayOutputStream();
    }
  }




  /**
   * Method decodeHtmlString
   *
   * @param data
   *
   * @return
   */
  public static String decodeHtmlString( String data ) {
    return UriUtil.decodeString( data );
  }




  /**
   * Method encodeHtmlString
   *
   * @param data
   *
   * @return
   */
  public static String encodeHtmlString( String data ) {
    return UriUtil.encodeString( data );
  }




  /**
   * Method getRemoteAddress
   *
   * @return
   */
  public InetAddress getRemoteAddress() {
    return remoteAddress;
  }




  /**
   * Method setRemoteAddress
   *
   * @param addr
   */
  public void setRemoteAddress( InetAddress addr ) {
    this.remoteAddress = addr;
  }




  /**
   * Method getRemotePort
   *
   * @return
   */
  public int getRemotePort() {
    return remotePort;
  }




  /**
   * Method setRemotePort
   *
   * @param port
   */
  public void setRemotePort( int port ) {
    this.remotePort = port;
  }

}