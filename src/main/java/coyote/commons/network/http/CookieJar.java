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

import java.util.Enumeration;
import java.util.Vector;

import coyote.commons.ArrayUtil;


/**
 * Class CookieJar
 */
public class CookieJar {

  /** Our collection of cookies */
  private Cookie[] cookies = null;




  /**
   * Constructor CookieJar
   */
  public CookieJar() {}




  /**
   * Method addCookies
   *
   * @param array
   */
  public void addCookies( Cookie[] array ) {
    if ( array != null ) {
      if ( cookies == null ) {
        cookies = new Cookie[array.length];

        System.arraycopy( array, 0, cookies, 0, array.length );
      }
    } else {
      cookies = (Cookie[])ArrayUtil.addElements( cookies, array );
    }
  }




  /**
   * Method addCookie
   *
   * @param cookie
   */
  public void addCookie( Cookie cookie ) {
    if ( cookie != null ) {
      if ( cookies == null ) {
        cookies = new Cookie[1];
        cookies[0] = cookie;
      } else {
        cookies = (Cookie[])ArrayUtil.addElement( cookies, cookie );
      }
    }

  }




  /**
   * Method getCookie
   *
   * @param name
   *
   * @return
   */
  public Cookie getCookie( String name ) {
    if ( cookies == null ) {
      return null;
    }

    // Look through all the cookies we have
    for ( int i = 0; i < cookies.length; i++ ) {
      // return the first cookie we find with a matching name
      if ( cookies[i].getName().equals( name ) ) {
        return cookies[i];
      }
    }

    // Not found
    return null;
  }




  /**
   * Method getCookieCount
   *
   * @return
   */
  public int getCookieCount() {
    return cookies.length;
  }




  /**
   * Method getCookieEnumeration
   *
   * @param name
   *
   * @return
   */
  public Enumeration getCookieEnumeration( String name ) {
    if ( cookies != null ) {
      Vector vector = new Vector();

      // Look through all the cookies we have
      for ( int i = 0; i < cookies.length; i++ ) {
        // return the first cookie we find with a matching name
        if ( cookies[i].getName().equals( name ) ) {
          vector.add( cookies[i] );
        }
      }

      return vector.elements();
    }

    return null;
  }




  /**
   * Method getCookies
   *
   * @param name
   *
   * @return
   */
  public Cookie[] getCookies( String name ) {
    Vector vector = new Vector();

    if ( cookies != null ) {
      // Look through all the cookies we have
      for ( int i = 0; i < cookies.length; i++ ) {
        // return the first cookie we find with a matching name
        if ( cookies[i].getName().equals( name ) ) {
          vector.add( cookies[i] );
        }
      }
    }

    return (Cookie[])vector.toArray();
  }
}