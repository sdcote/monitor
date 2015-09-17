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

import java.util.Date;

import coyote.commons.ArrayUtil;
import coyote.commons.StringParser;
import coyote.loader.log.Log;


/**
 * Creates a cookie, a small amount of information sent by a web server to a
 * web browser, saved by the browser, and later sent back to the server.
 *
 * <p>A cookie's value can uniquely identify a client, so cookies are commonly
 * used for session management.</p>
 *
 * <p>A cookie has a name, a single value, and optional attributes such as a
 * comment, path and domain qualifiers, a maximum age, and a version number.
 * Some browsers have bugs in how they handle the optional attributes, so use
 * them with caution.</p>
 *
 * <p>The servlet sends cookies to the browser by using the
 * HttpServletResponse.addCookie(javax.servlet.http.Cookie) method, which adds
 * fields to HTTP response headers to send cookies to the browser, one at a
 * time. The browser is expected to support 20 cookies for each Web server, 300
 * cookies total, and may limit cookie size to 4 KB each.</p>
 *
 * <p>The browser returns cookies to the servlet by adding fields to HTTP
 * request headers. Cookies can be retrieved from a request by using the
 * HttpServletRequest.getCookies() method. Several cookies might have the same
 * name but different path attributes.</p>
 *
 * <p>Cookies affect the caching of the Web pages that use them. HTTP 1.0 does
 * not cache pages that use cookies created with this class. This class does
 * not support the cache control defined with HTTP 1.1.</p>
 *
 * <p>This class supports both the Version 0(Netscape) and Version 1(RFC 2109)
 * cookie specifications. By default, cookies are created using Version 0 to
 * ensure the best interoperability.</p>
 */
public class Cookie implements Cloneable {
  private String name = null;
  private String value = null;
  private String comment = null;
  private String domain = null;
  private int maxAge = 0;
  private String path = null;
  private boolean secure = false;
  private int version = 0;
  private static final String delims = ",;";




  /**
   * Constructs a cookie with a specified name and value.
   *
   * <p>The name must conform to RFC 2109. That means it can contain only ASCII
   * alphanumeric characters and cannot contain commas, semicolons, or white
   * space or begin with a $ character. The cookie's name cannot be changed
   * after creation.</p>
   *
   * <p>The value can be anything the server chooses to send. Its value is
   * probably of interest only to the server. The cookie's value can be changed
   * after creation with the setValue method.</p>
   *
   * <p>By default, cookies are created according to the Netscape cookie
   * specification. The version can be changed with the setVersion1 method.</p>
   *
   * @param name A String specifying the name of the cookie
   * @param value A String specifying the value of the cookie
   *
   */
  public Cookie( String name, String value ) {
    maxAge = -1;
    version = 0;

    if ( !isToken( name ) || name.equalsIgnoreCase( "Comment" ) || name.equalsIgnoreCase( "Discard" ) || name.equalsIgnoreCase( "Domain" ) || name.equalsIgnoreCase( "Expires" ) || name.equalsIgnoreCase( "Max-Age" ) || name.equalsIgnoreCase( "Path" ) || name.equalsIgnoreCase( "Secure" ) || name.equalsIgnoreCase( "Version" ) ) {
      throw new IllegalArgumentException( "Cookie name '" + name + "' contains invalid characters, or is a reserved token" );
    } else {
      this.name = name;
      this.value = value;

      return;
    }
  }




  /**
   * Specifies a comment that describes a cookie's purpose.
   *
   * <p>The comment is useful if the browser presents the cookie to the user.
   * Comments are not supported by Netscape Version 0 cookies.</p>
   *
   * @param purpose a String specifying the comment to display to the user
   */
  public void setComment( String purpose ) {
    comment = purpose;
  }




  /**
   * Returns the comment describing the purpose of this cookie, or null if the
   * cookie has no comment.
   *
   * @return a String containing the comment, or null if none
   */
  public String getComment() {
    return comment;
  }




  /**
   * Specifies the domain within which this cookie should be presented.
   *
   * <p>The form of the domain name is specified by RFC 2109. A domain name
   * begins with a dot (.foo.com) and means that the cookie is visible to
   * servers in a specified Domain Name System (DNS) zone (for example,
   * www.foo.com, but not a.b.foo.com). By default, cookies are only returned
   * to the server that sent them.</p>
   *
   * @param pattern
   */
  public void setDomain( String pattern ) {
    domain = pattern.toLowerCase();
  }




  /**
   * Returns the domain name set for this cookie. The form of the domain name
   * is set by RFC 2109.
   *
   * @return a String containing the domain name
   */
  public String getDomain() {
    return domain;
  }




  /**
   * Sets the maximum age of the cookie in seconds.
   *
   * <p>A positive value indicates that the cookie will expire after that many
   * seconds have passed. Note that the value is the maximum age when the
   * cookie will expire, not the cookie's current age.</p>
   *
   * <p>A negative value means that the cookie is not stored persistently and
   * will be deleted when the Web browser exits. A zero value causes the cookie
   * to be deleted.</p>
   *
   * @param expiry an integer specifying the maximum age of the cookie in seconds; if negative, means the cookie is not stored; if zero, deletes the cookie
   */
  public void setMaxAge( int expiry ) {
    maxAge = expiry;
  }




  /**
   * Returns the maximum age of the cookie, specified in seconds, By default,
   * -1 indicating the cookie will persist until browser shutdown.
   *
   * @return an integer specifying the maximum age of the cookie in seconds; if negative, means the cookie persists until browser shutdown
   */
  public int getMaxAge() {
    return maxAge;
  }




  /**
   * Specifies a path for the cookie to which the client should return the
   * cookie.
   *
   * <p>The cookie is visible to all the pages in the directory you specify,
   * and all the pages in that directory's sub-directories. A cookie's path 
   * must include the servlet that set the cookie, for example, /catalog, which
   * makes the cookie visible to all directories on the server under
   * /catalog.</p>
   *
   * <p>Consult RFC 2109 for more information on setting path names for
   * cookies.</p>
   *
   * @param uri a String specifying a path
   */
  public void setPath( String uri ) {
    path = uri;
  }




  /**
   * Returns the path on the server to which the browser returns this cookie.
   * The cookie is visible to all subpaths on the server.
   *
   * @return a String specifying a path that contains a servlet name, for
   *         example, /catalog
   */
  public String getPath() {
    return path;
  }




  /**
   * Indicates to the browser whether the cookie should only be sent using a
   * secure protocol, such as HTTPS or SSL.
   *
   * @param flag if true, sends the cookie from the browser to the server using
   *        only when using a secure protocol; if false, sent on any protocol.
   */
  public void setSecure( boolean flag ) {
    secure = flag;
  }




  /**
   * Returns true if the browser is sending cookies only over a secure protocol,
   * or false if the browser can send cookies using any protocol.
   *
   * @return true if the browser can use any standard protocol; otherwise, false
   */
  public boolean getSecure() {
    return secure;
  }




  /**
   * Returns the name of the cookie.
   *
   * <p>The name cannot be changed after creation.</p>
   *
   * @return a String specifying the cookie's name
   */
  public String getName() {
    return name;
  }




  /**
   * Assigns a new value to a cookie after the cookie is created.
   *
   * <p>If you use a binary value, you may want to use BASE64 encoding.</p>
   *
   * <p>With Version 0 cookies, values should not contain white space,
   * brackets, parentheses, equals signs, commas, double quotes, slashes,
   * question marks, at signs, colons, and semicolons.</p>
   *
   * <p>Empty values may not behave the same way on all browsers.</p>
   *
   *
   * @param val
   */
  public void setValue( String val ) {
    value = val;
  }




  /**
   * Returns the value of the cookie.
   *
   * @return a String containing the cookie's present value
   */
  public String getValue() {
    return value;
  }




  /**
   * Returns the version of the protocol this cookie complies with.
   *
   * <p>Version 1 complies with RFC 2109, and version 0 complies with the
   * original cookie specification drafted by Netscape. Cookies provided by a
   * browser use and identify the browser's cookie version.</p>
   *
   * @return 0 if the cookie complies with the original Netscape specification; 1 if the cookie complies with RFC 2109
   */
  public int getVersion() {
    return version;
  }




  /**
   * Sets the version of the cookie protocol with which this cookie complies to
   * Version 0 (original Netscape specification).
   *
   * <p>Version 0 complies with the original Netscape cookie specification and
   * is the default.</p>
   */
  public void setVersion0() {
    version = 0;
  }




  /**
   * Sets the version of the cookie protocol with which this cookie complies to
   * Version 1 (RFC 2109).
   *
   * <p>Since RFC 2109 is still somewhat new, consider version 1 as
   * experimental; do not use it yet on production sites.</p>
   */
  public void setVersion1() {
    version = 1;
  }




  /**
   * Returns whether or not a string can be used as a token by checking it for
   * illegal characters according to RFC 2019.
   *
   * @param name
   *
   * @return
   */
  private boolean isToken( String name ) {
    for ( int i = 0; i < name.length(); i++ ) {
      char c = name.charAt( i );

      if ( ( c < ' ' ) || ( c >= '\177' ) || ( delims.indexOf( c ) != -1 ) ) {
        return false;
      }
    }

    return true;
  }




  /**
   * Method clone
   *
   * @return
   */
  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException clonenotsupportedexception ) {
      throw new RuntimeException( clonenotsupportedexception.getMessage() );
    }
  }




  /**
   * Return a string representing the cookie suitable for a HTTP header.
   *
   * @return the string representing this cookie suitable for placing in a HTTP
   *         header according to the currently set version.
   */
  public String toString() {
    return toString( this );
  }




  /**
   * Return a string representing the cookie suitable for a HTTP header.
   *
   * <p>If the cookie is set to Version 0 (Netscape) the cookie will be output
   * according to the netscape specification, otherwise it will be output
   * according to RFC 2109.</p>
   *
   * @param cookie the cookie to represent.
   *
   * @return the string representing the cookie suitable for placing in a HTTP
   *         header.
   */
  public static String toString( Cookie cookie ) {
    return ( cookie.getVersion() != 0 ) ? toStringVersion1( cookie ) : toStringVersion0( cookie );
  }




  /**
   * Return a Version 0 (Netscape) formatted cookie as a string.
   *
   * @param cookie the Cookies to format as a String
   *
   * @return the string representing the cookie suitable for placing in a HTTP
   *         header.
   */
  private static String toStringVersion0( Cookie cookie ) {
    StringBuffer stringbuffer = new StringBuffer();
    stringbuffer.append( cookie.getName() ).append( '=' ).append( cookie.getValue() );

    if ( cookie.getMaxAge() >= 0 ) {
      Date date = new Date( (long)( cookie.getMaxAge() * 1000 ) + System.currentTimeMillis() );
      stringbuffer.append( "; expires=" ).append( HttpMessage.dateFormatter2.format( date ) );
    }

    if ( cookie.getPath() != null ) {
      stringbuffer.append( "; path=" ).append( cookie.getPath() );
    }

    if ( cookie.getDomain() != null ) {
      stringbuffer.append( "; domain=" ).append( cookie.getDomain() );
    }

    if ( cookie.getSecure() ) {
      stringbuffer.append( "; secure" );
    }

    return stringbuffer.toString();
  }




  /**
   * Return a Version 1 (RFC 2109) formatted cookie as a string.
   *
   * @param cookie the Cookies to format as a String
   *
   * @return the string representing the cookie suitable for placing in a HTTP
   *         header.
   */
  private static String toStringVersion1( Cookie cookie ) {
    StringBuffer stringbuffer = new StringBuffer();
    stringbuffer.append( cookie.getName() ).append( '=' ).append( cookie.getValue() );
    stringbuffer.append( "; Version=1" );

    if ( cookie.getMaxAge() >= 0 ) {
      stringbuffer.append( "; Max-Age=\"" ).append( cookie.getMaxAge() ).append( '"' );
    }

    if ( cookie.getComment() != null ) {
      stringbuffer.append( "; Comment=\"" ).append( cookie.getComment() ).append( '"' );
    }

    if ( cookie.getPath() != null ) {
      stringbuffer.append( "; Path=\"" ).append( cookie.getPath() ).append( '"' );
    }

    if ( cookie.getDomain() != null ) {
      stringbuffer.append( "; Domain=\"" ).append( cookie.getDomain() ).append( '"' );
    }

    if ( cookie.getSecure() ) {
      stringbuffer.append( "; Secure" );
    }

    return stringbuffer.toString();
  }




  /**
   * Method parse
   *
   * @param data
   *
   * @return
   */
  public static Cookie[] parse( String data ) {
    try {

      if ( data.startsWith( "Cookie: " ) ) {
        // Complete standard cookie header was passed to us
        String body = data.substring( 8 );

        if ( body.indexOf( "Version=" ) > 0 ) {
          return parse1( body );
        } else {
          return parse0( body );
        }
      } else if ( data.startsWith( "Cookie2: " ) ) {
        // Complete RFC2965 cookie header was passed to us
        String body = data.substring( 9 );
        return parse1( body );
      } else if ( data.indexOf( '=' ) > 1 ) {
        // Apparently just the body of the cookie was passed to us
        if ( data.indexOf( "Version=" ) > 0 ) {
          return parse1( data );
        } else {
          return parse0( data );
        }
      } else {
        // Not a cookie we understand
        Log.debug( "The data of '" + data + "' is not a cookie" );

        return null;
      }
    } catch ( Exception exception ) {
      exception.printStackTrace();
    }

    return null;
  }




  /**
   * Method parse0
   *
   * @param data
   *
   * @return
   */
  private static Cookie[] parse0( String data ) {
    Cookie[] retval = new Cookie[0];
    StringParser parser = new StringParser( data.trim(), ";," );

    try {
      do {
        // Skip all the whitespace before the token
        parser.skipWhitespace();

        // read the first set of name value pairs
        String token = parser.readTo( '=' ).trim();
        // Log.debug("parse0 - name token ='"+token+"' position: "+parser.getPosition());

        String val = parser.readToDelimiter( ",;" ).trim();
        // Log.debug("parse0 - value token ='"+val+"' position: "+parser.getPosition());

        // If we are not at the end for the line...
        if ( !parser.eof() ) {
          // ...read past the delimiter that gave us the value token
          parser.read();
        }

        if ( ( token != null ) && ( token.length() > 0 ) ) {
          // add the cookie to the return value array
          retval = (Cookie[])ArrayUtil.addElement( retval, new Cookie( token, val ) );
        }

        if ( parser.eof() ) {
          break;
        }
      }
      while ( true );
    } catch ( Exception ex ) {
      Log.warn( "Error parsing cookie " + ex.getMessage() );
      Log.debug( "Cookie='" + data + "' at " + parser.getPosition() );
    }

    return retval;
  }




  /**
   * Method parse1
   *
   * @param data
   *
   * @return
   */
  private static Cookie[] parse1( String data ) {
    Cookie[] retval = new Cookie[0];

    StringParser parser = new StringParser( data.trim() );

    try {
      do {
        String token = parser.readToken();
        parser.readTo( '=' );

        String attr = parser.readToken();
        String path = null;
        String domain = null;

        if ( token.equals( "$Path" ) ) {
          path = attr;
        } else {
          if ( token.equals( "$Domain" ) ) {
            domain = attr;
          } else {
            Cookie cookie = new Cookie( token, attr );
            cookie.setVersion1();
            cookie.setPath( path );
            cookie.setDomain( domain );

            retval = (Cookie[])ArrayUtil.addElement( retval, cookie );
          }
        }

        if ( !parser.eof() ) {
          parser.readTo( ';' );
        } else {
          return retval;
        }
      }
      while ( true );
    } catch ( Exception ex ) {
      Log.warn( "Error parsing cookie " + ex.getMessage() );
      Log.debug( "Cookie='" + data + "' at " + parser.getPosition() );
    }

    return retval;
  }

}