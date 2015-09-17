/*
 * Copyright (c) 2003 Stephan D. Cote' - All rights reserved.
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the MIT License which accompanies this distribution, and is 
 * available at http://creativecommons.org/licenses/MIT/
 *
 * Contributors:
 *   Stephan D. Cote 
 *      - Initial implementation
 */
package coyote.commons.security;

/**
 * JAVA translation of the hmac_md5() function from RFC 2104.
 */
public class HMAC_MD5 {

  /** Digest to be returned upon completion of the HMAC_MD5. */
  private byte digest[];

  /** Inner Padding. */
  private byte kIpad[];

  /** Outer Padding. */
  private byte kOpad[];

  /** Inner MD5 object. */
  private MD5 innerMD5;




  /**
   * Constructor
   *
   * @param key
   */
  HMAC_MD5( byte key[] ) {
    int kLen = key.length;

    // if key is longer than 64 bytes reset it to key=MD5(key)
    if ( kLen > 64 ) {
      MD5 md5 = new MD5();
      md5.update( key );

      key = md5.digest();
    }

    kIpad = new byte[64]; // inner padding - key XORd with ipad
    kOpad = new byte[64]; // outer padding - key XORd with opad

    // start out by storing key in pads
    System.arraycopy( key, 0, kIpad, 0, kLen );
    System.arraycopy( key, 0, kOpad, 0, kLen );

    // XOR key with ipad and opad values
    for ( int i = 0; i < 64; i++ ) {
      kIpad[i] ^= 0x36;
      kOpad[i] ^= 0x5c;
    }

    clear(); // Initialize the first digest.
  }




  /**
   * Clear the HMAC_MD5 object.
   */
  public void clear() {
    innerMD5 = new MD5();

    innerMD5.update( kIpad ); // Intialize the inner pad.

    digest = null; // mark the digest as incomplete.
  }




  /**
   * HMAC_MD5 function.
   *
   * @param text Text to process
   */
  public void addData( byte text[] ) {
    addData( text, 0, text.length );
  }




  /**
   * HMAC_MD5 function.
   *
   * @param text Text to process
   * @param textStart Start position of text in text buffer.
   * @param textLen Length of text to use from text buffer.
   */
  public void addData( byte text[], int textStart, int textLen ) {
    innerMD5.update( text, textStart, textLen ); // then text of datagram.
  }




  /**
   * Method sign
   *
   * @return
   */
  public byte[] sign() {
    MD5 md5;
    // the HMAC_MD5 transform looks like:
    // MD5(K XOR opad, MD5(K XOR ipad, text))
    //
    // where K is an n byte key
    // ipad is the byte 0x36 repeated 64 times
    // opad is the byte 0x5c repeated 64 times
    // and text is the data being protected
    // Perform inner MD5
    digest = innerMD5.digest(); // finish up 1st pass.
    // Perform outer MD5
    md5 = new MD5(); // Init md5 for 2nd pass.

    md5.update( kOpad ); // Use outer pad.
    md5.update( digest ); // Use results of first pass.

    digest = md5.digest(); // Final result.

    return digest;
  }




  /**
   * Validate a signature against the current digest.
   *
   * <p>Compares the hash against the signature.</p>
   *
   * @param signature
   *
   * @return True if the signature matches the calculated hash.
   */
  public boolean verify( byte signature[] ) {
    // The digest may not have been calculated. If it's null, force a
    // calculation.
    if ( digest == null ) {
      sign();
    }

    int sigLen = signature.length;
    int digLen = digest.length;

    if ( sigLen != digLen ) {
      return false; // Different lengths, not a good sign.
    }

    for ( int i = 0; i < sigLen; i++ ) {
      if ( signature[i] != digest[i] ) {
        return false; // Mismatch. Misfortune.
      }
    }

    return true; // Signatures matched. Perseverance furthers.
  }




  /**
   * Return the digest as a HEX string.
   *
   * @return a hex representation of the MD5 digest.
   */
  public String toString() {
    // If not already calculated, do so.
    if ( digest == null ) {
      sign();
    }

    StringBuffer r = new StringBuffer();
    final String hex = "0123456789ABCDEF";
    byte b[] = digest;

    for ( int i = 0; i < 16; i++ ) {
      int c = ( ( b[i] ) >>> 4 ) & 0xf;
      r.append( hex.charAt( c ) );

      c = ( (int)b[i] & 0xf );

      r.append( hex.charAt( c ) );
    }

    return r.toString();
  }




  /**
   * Run standard tests from the RFC:
   *
   * @param arg
   */
  public static void main( String arg[] ) {
    String expectedHash;
    byte digest[];
    HMAC_MD5 hm;
    System.out.println( "Test Vectors from RFC 2104 - HMAC: Keyed-Hashing for Message Authentication" );
    System.out.println( "This test uses HMAC-MD5." );
    System.out.println();
    System.out.println( "Test #1:" );

    // Test #1:
    byte key1[] = { (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b, (byte)0x0b };

    String text1 = "Hi There";
    expectedHash = "0X9294727A3638BB1C13F48EF8158BFC9D";
    hm = new HMAC_MD5( key1 );

    hm.addData( text1.getBytes() );

    digest = hm.sign();

    System.out.println( "Calculated hash 0X" + hm );
    System.out.println( "  Expected hash " + expectedHash );
    // Test #2
    System.out.println();
    System.out.println( "Test #2:" );

    byte key2[] = "Jefe".getBytes();
    String text2 = "what do ya want for nothing?";
    expectedHash = "0X750C783E6AB0B503EAA86E310A5DB738";
    hm = new HMAC_MD5( key2 );

    hm.addData( text2.getBytes() );

    digest = hm.sign();

    System.out.println( "Calculated hash 0X" + hm );
    System.out.println( "  Expected hash " + expectedHash );
    // Test #3
    System.out.println();
    System.out.println( "Test #3:" );

    byte key3[] = { (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa, (byte)0xaa };

    byte text3[] = { (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd, (byte)0xdd };

    expectedHash = "0X56BE34521D144C88DBB8C733F0E8B3F6";

    byte eh[] = { (byte)0x56, (byte)0xBE, (byte)0x34, (byte)0x52, (byte)0x1D, (byte)0x14, (byte)0x4C, (byte)0x88, (byte)0xDB, (byte)0xB8, (byte)0xC7, (byte)0x33, (byte)0xF0, (byte)0xE8, (byte)0xB3, (byte)0xF6 };

    hm = new HMAC_MD5( key3 );

    hm.addData( text3 );

    digest = hm.sign();

    System.out.println( "Calculated hash 0X" + hm );
    System.out.println( "  Expected hash " + expectedHash );
    System.out.println( "Signature Verification: " + hm.verify( eh ) );
  }

}