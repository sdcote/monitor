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
package coyote.commons.security;

import java.math.BigInteger;


/**
 * Generate some large primes in the 128 bit area.
 *
 * <p>The purpose of this code is generate keys from some sort of input - in
 * this case the command line. The intention is not to generate a random
 * prime number but a reproducible number.</p>
 */
public class KeyGen
{

  /**
   * Method getKeyString
   *
   * @param args
   *
   * @return
   */
  public static String getKeyString( String[] args )
  {
    return dumpBytes( getKeyBytes( args ) );
  }




  /**
   * Method getKeyBytes
   *
   * @param args
   *
   * @return
   */
  public static byte[] getKeyBytes( String[] args )
  {
    int i;
    BigInteger biKey; // resulting key.
    BigInteger two = new BigInteger( "2" );
    BigInteger one = new BigInteger( "1" );
    int pick = 7; // nth probable prime to pick. Choose your own number.

    if( args.length == 0 )
    {
      System.err.println( "Not enough args for the key" );
      System.exit( 0 );
    }

    // Accumulate all args into a string and convert it to MD5.
    String seed = "";

    for( i = 0; i < args.length; i++ )
    {
      seed += args[i];
    }

    MD5 md = new MD5();
    md.update( seed.getBytes() );

    biKey = new BigInteger( md.digest() );

    // make sure it starts out odd - there are very few even primes.
    if( biKey.and( one ).compareTo( one ) != 0 )
    {
      biKey = biKey.add( one );
    }

    // Purely for entertainment purposes!
    // System.out.println( "Key starts with: " + dumpBytes( biKey.toByteArray() ) );

    // Search for n'th probable prime out of a thousand.
    for( i = 0; i < 1000; i++ )
    {
      // The number 1024 was chosen at pseudorandom. Larger numbers take longer
      // because the prime-picker is allegedly more picky.
      if( biKey.isProbablePrime( 1024 ) )
      {
        if( --pick == 0 )
        {
          return biKey.toByteArray();
        }
      }

      biKey = biKey.add( two );
    }

    return null;
  }




   private static String dumpBytes( byte[] b )
  {
    if( b != null )
    {
      StringBuffer r = new StringBuffer();
      final String hex = "0123456789ABCDEF";

      for( int i = 0; i < b.length; i++ )
      {
        int c = ( ( b[i] ) >>> 4 ) & 0xf;
        r.append( hex.charAt( c ) );

        c = ( (int)b[i] & 0xf );

        r.append( hex.charAt( c ) );
        // r.append(' '); // uncomment if you like spaces in the dump.
      }

      r.setLength( r.length() - 1 );

      return r.toString();
    }

    return null;
  }
  
}