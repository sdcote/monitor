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
 * The sha1 utility implements the Secure Hash Algorithm-1 (SHA-1). It produces
 * 160-bit Secure Hash Digests of files, strings or data read.
 *
 * <p>The Secure Hash Standard-1 (SHS-1) is a United States Department of
 * Commerce National Institute of Standards and Technology approved standard
 * (FIPS Pub 180-1) for secure hashing. The SHS-1 is designed to work with
 * the Digital Signature Standard (DSS). The Secure Hash Algorithm (SHA-1)
 * specified in this standard is necessary to ensure the security of the
 * Digital Signature Standard. When a message of length &lt; 2^64 bits is input,
 * the SHA-1 produces a 160-bit representation of the message called the
 * message digest. The message digest is used during generation of a signature
 * for the message. The SHA-1 is designed to have the following properties: it
 * is computationally infeasible to recover a message corresponding to a given
 * message digest, or to find two different messages which produce the same
 * message digest.</p>
 *
 * <p>On 1994 May 31, the United States Department of Commerce National
 * Institute of Standards and Technology published a technical modification
 * (FIPS Pub 180-1) to the Secure Hash Standard known as Secure Hash
 * Algorithm-1 (SHA-1).  The sha1 utility implements the new Secure Hash
 * Algorithm-1 (SHA-1) as specified by (FIPS Pub 180-1). The sha utility
 * implements the old Secure Hash Algorithm (SHA) as specified by (FIPS Pub
 * 180).</p>
 *
 * <PRE>
 * SHA1.java - An implementation of the SHA-1 Algorithm
 *
 *      Test Vectors (from FIPS PUB 180-1)
 *      "abc"
 *      A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D
 *
 *      "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
 *      84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1
 *
 *      A million repetitions of "a"
 *      34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F
 * </pre>
 */
public final class SHA1 implements Digest {

  private int state[] = new int[5];
  private long count;
  private boolean digestValid = false;
  private byte[] digestBits;
  private boolean NSA = true;
  private int dd[] = new int[5];

  /**
   * The following array forms the basis for the transform buffer. Update puts
   * bytes into this buffer and then transform adds it into the state of the
   * digest.
   */
  private int block[] = new int[16];
  private int blockIndex;




  /**
   * Variant constructor
   *
   * @param b true for SHA-1, false for the original SHA-0
   */
  public SHA1( boolean b ) {
    count = 0;
    digestValid = false;
    NSA = b;

    init();
  }




  /**
   * Simple constructor for SHA-1
   */
  public SHA1() {
    this( true );
  }




  /**
   * Method digestSize
   *
   * @return
   */
  public final int digestSize() {
    return 160;
  }




  /**
   * Retrieves the value of a hash.
   *
   * @param digest int[] into which to place 5 elements
   * @param offset index of first of the 5 elements
   */
  public void extract( int[] digest, int offset ) {
    for ( int i = 0; i < 5; ++i ) {
      digest[i + offset] = ( ( digestBits[4 * i + 0] << 24 ) & 0xFF000000 ) | ( ( digestBits[4 * i + 1] << 16 ) & 0x00FF0000 ) | ( ( digestBits[4 * i + 2] << 8 ) & 0x0000FF00 ) | ( ( digestBits[4 * i + 3] ) & 0x000000FF );
    }
  }




  /**
   * Method rol
   *
   * @param value
   * @param bits
   *
   * @return
   */
  private final int rol( int value, int bits ) {
    int q = ( value << bits ) | ( value >>> ( 32 - bits ) );
    return q;
  }




  /**
   * Method blk0
   *
   * @param i
   *
   * @return
   */
  private final int blk0( int i ) {
    block[i] = ( rol( block[i], 24 ) & 0xFF00FF00 ) | ( rol( block[i], 8 ) & 0x00FF00FF );

    return block[i];
  }




  /**
   * Method blk
   *
   * @param i
   *
   * @return
   */
  private final int blk( int i ) {
    block[i & 15] = block[( i + 13 ) & 15] ^ block[( i + 8 ) & 15] ^ block[( i + 2 ) & 15] ^ block[i & 15];

    if ( NSA ) {
      // this makes it SHA-1
      block[i & 15] = rol( block[i & 15], 1 );
    }

    return ( block[i & 15] );
  }




  /**
   * Method R0
   *
   * @param data
   * @param v
   * @param w
   * @param x
   * @param y
   * @param z
   * @param i
   */
  private final void R0( int data[], int v, int w, int x, int y, int z, int i ) {
    data[z] += ( ( data[w] & ( data[x] ^ data[y] ) ) ^ data[y] ) + blk0( i ) + 0x5A827999 + rol( data[v], 5 );
    data[w] = rol( data[w], 30 );
  }




  /**
   * Method R1
   *
   * @param data
   * @param v
   * @param w
   * @param x
   * @param y
   * @param z
   * @param i
   */
  private final void R1( int data[], int v, int w, int x, int y, int z, int i ) {
    data[z] += ( ( data[w] & ( data[x] ^ data[y] ) ) ^ data[y] ) + blk( i ) + 0x5A827999 + rol( data[v], 5 );
    data[w] = rol( data[w], 30 );
  }




  /**
   * Method R2
   *
   * @param data
   * @param v
   * @param w
   * @param x
   * @param y
   * @param z
   * @param i
   */
  private final void R2( int data[], int v, int w, int x, int y, int z, int i ) {
    data[z] += ( data[w] ^ data[x] ^ data[y] ) + blk( i ) + 0x6ED9EBA1 + rol( data[v], 5 );
    data[w] = rol( data[w], 30 );
  }




  /**
   * Method R3
   *
   * @param data
   * @param v
   * @param w
   * @param x
   * @param y
   * @param z
   * @param i
   */
  private final void R3( int data[], int v, int w, int x, int y, int z, int i ) {
    data[z] += ( ( ( data[w] | data[x] ) & data[y] ) | ( data[w] & data[x] ) ) + blk( i ) + 0x8F1BBCDC + rol( data[v], 5 );
    data[w] = rol( data[w], 30 );
  }




  /**
   * Method R4
   *
   * @param data
   * @param v
   * @param w
   * @param x
   * @param y
   * @param z
   * @param i
   */
  private final void R4( int data[], int v, int w, int x, int y, int z, int i ) {
    data[z] += ( data[w] ^ data[x] ^ data[y] ) + blk( i ) + 0xCA62C1D6 + rol( data[v], 5 );
    data[w] = rol( data[w], 30 );
  }




  /**
   * Hash a single 512-bit block. This is the core of the algorithm.
   *
   * Note that working with arrays is very inefficent in Java as it does a
   * class cast check each time you store into the array.
   */

  private void transform() {

    /* Copy context->state[] to working vars */
    dd[0] = state[0];
    dd[1] = state[1];
    dd[2] = state[2];
    dd[3] = state[3];
    dd[4] = state[4];

    /* 4 rounds of 20 operations each. Loop unrolled. */
    R0( dd, 0, 1, 2, 3, 4, 0 );
    R0( dd, 4, 0, 1, 2, 3, 1 );
    R0( dd, 3, 4, 0, 1, 2, 2 );
    R0( dd, 2, 3, 4, 0, 1, 3 );
    R0( dd, 1, 2, 3, 4, 0, 4 );
    R0( dd, 0, 1, 2, 3, 4, 5 );
    R0( dd, 4, 0, 1, 2, 3, 6 );
    R0( dd, 3, 4, 0, 1, 2, 7 );
    R0( dd, 2, 3, 4, 0, 1, 8 );
    R0( dd, 1, 2, 3, 4, 0, 9 );
    R0( dd, 0, 1, 2, 3, 4, 10 );
    R0( dd, 4, 0, 1, 2, 3, 11 );
    R0( dd, 3, 4, 0, 1, 2, 12 );
    R0( dd, 2, 3, 4, 0, 1, 13 );
    R0( dd, 1, 2, 3, 4, 0, 14 );
    R0( dd, 0, 1, 2, 3, 4, 15 );
    R1( dd, 4, 0, 1, 2, 3, 16 );
    R1( dd, 3, 4, 0, 1, 2, 17 );
    R1( dd, 2, 3, 4, 0, 1, 18 );
    R1( dd, 1, 2, 3, 4, 0, 19 );
    R2( dd, 0, 1, 2, 3, 4, 20 );
    R2( dd, 4, 0, 1, 2, 3, 21 );
    R2( dd, 3, 4, 0, 1, 2, 22 );
    R2( dd, 2, 3, 4, 0, 1, 23 );
    R2( dd, 1, 2, 3, 4, 0, 24 );
    R2( dd, 0, 1, 2, 3, 4, 25 );
    R2( dd, 4, 0, 1, 2, 3, 26 );
    R2( dd, 3, 4, 0, 1, 2, 27 );
    R2( dd, 2, 3, 4, 0, 1, 28 );
    R2( dd, 1, 2, 3, 4, 0, 29 );
    R2( dd, 0, 1, 2, 3, 4, 30 );
    R2( dd, 4, 0, 1, 2, 3, 31 );
    R2( dd, 3, 4, 0, 1, 2, 32 );
    R2( dd, 2, 3, 4, 0, 1, 33 );
    R2( dd, 1, 2, 3, 4, 0, 34 );
    R2( dd, 0, 1, 2, 3, 4, 35 );
    R2( dd, 4, 0, 1, 2, 3, 36 );
    R2( dd, 3, 4, 0, 1, 2, 37 );
    R2( dd, 2, 3, 4, 0, 1, 38 );
    R2( dd, 1, 2, 3, 4, 0, 39 );
    R3( dd, 0, 1, 2, 3, 4, 40 );
    R3( dd, 4, 0, 1, 2, 3, 41 );
    R3( dd, 3, 4, 0, 1, 2, 42 );
    R3( dd, 2, 3, 4, 0, 1, 43 );
    R3( dd, 1, 2, 3, 4, 0, 44 );
    R3( dd, 0, 1, 2, 3, 4, 45 );
    R3( dd, 4, 0, 1, 2, 3, 46 );
    R3( dd, 3, 4, 0, 1, 2, 47 );
    R3( dd, 2, 3, 4, 0, 1, 48 );
    R3( dd, 1, 2, 3, 4, 0, 49 );
    R3( dd, 0, 1, 2, 3, 4, 50 );
    R3( dd, 4, 0, 1, 2, 3, 51 );
    R3( dd, 3, 4, 0, 1, 2, 52 );
    R3( dd, 2, 3, 4, 0, 1, 53 );
    R3( dd, 1, 2, 3, 4, 0, 54 );
    R3( dd, 0, 1, 2, 3, 4, 55 );
    R3( dd, 4, 0, 1, 2, 3, 56 );
    R3( dd, 3, 4, 0, 1, 2, 57 );
    R3( dd, 2, 3, 4, 0, 1, 58 );
    R3( dd, 1, 2, 3, 4, 0, 59 );
    R4( dd, 0, 1, 2, 3, 4, 60 );
    R4( dd, 4, 0, 1, 2, 3, 61 );
    R4( dd, 3, 4, 0, 1, 2, 62 );
    R4( dd, 2, 3, 4, 0, 1, 63 );
    R4( dd, 1, 2, 3, 4, 0, 64 );
    R4( dd, 0, 1, 2, 3, 4, 65 );
    R4( dd, 4, 0, 1, 2, 3, 66 );
    R4( dd, 3, 4, 0, 1, 2, 67 );
    R4( dd, 2, 3, 4, 0, 1, 68 );
    R4( dd, 1, 2, 3, 4, 0, 69 );
    R4( dd, 0, 1, 2, 3, 4, 70 );
    R4( dd, 4, 0, 1, 2, 3, 71 );
    R4( dd, 3, 4, 0, 1, 2, 72 );
    R4( dd, 2, 3, 4, 0, 1, 73 );
    R4( dd, 1, 2, 3, 4, 0, 74 );
    R4( dd, 0, 1, 2, 3, 4, 75 );
    R4( dd, 4, 0, 1, 2, 3, 76 );
    R4( dd, 3, 4, 0, 1, 2, 77 );
    R4( dd, 2, 3, 4, 0, 1, 78 );
    R4( dd, 1, 2, 3, 4, 0, 79 );

    /* Add the working vars back into context.state[] */
    state[0] += dd[0];
    state[1] += dd[1];
    state[2] += dd[2];
    state[3] += dd[3];
    state[4] += dd[4];
  }




  /**
   * Zero the count and state arrays
   */
  public final void clear() {
    count = 0;
    state[0] = state[1] = state[2] = state[3] = state[4] = 0;
  }




  /**
   * Initializes new context
   */
  public void init() {
    /* SHA1 initialization constants */
    state[0] = 0x67452301;
    state[1] = 0xEFCDAB89;
    state[2] = 0x98BADCFE;
    state[3] = 0x10325476;
    state[4] = 0xC3D2E1F0;
    count = 0;
    digestBits = new byte[20];
    digestValid = false;
    blockIndex = 0;
  }




  /**
   * Add one byte to the digest.
   *
   * <p>When this is implemented all of the abstract class methods end up
   * calling this method for types other than bytes.
   *
   * @param b byte to add
   */
  public void update( byte b ) {
    int mask = ( blockIndex & 3 ) << 3;
    count += 8;
    block[blockIndex >> 2] &= ~( 0xff << mask );
    block[blockIndex >> 2] |= ( b & 0xff ) << mask;

    blockIndex++;

    if ( blockIndex == 64 ) {
      transform();

      blockIndex = 0;
    }
  }




  /**
   * Add many bytes to the digest.
   *
   * @param data byte data to add
   * @param offset start byte
   * @param length number of bytes to hash
   */
  public final void update( byte[] data, int offset, int length ) {
    for ( int i = 0; i < length; ++i ) {
      update( data[offset + i] );
    }
  }




  /**
   * Method update
   *
   * @param data
   */
  public final void update( byte[] data ) {
    update( data, 0, data.length );
  }




  /**
   * Return completed digest filled into the given buffer.
   *
   * @param buffer
   * @param offset
   * @param reset If true, the hash function is reinitialized
   */
  public final void digest( boolean reset, byte[] buffer, int offset ) {
    finish();
    System.arraycopy( digestBits, 0, buffer, offset, digestBits.length );

    if ( reset ) {
      init();
    }
  }




  /**
   * Return completed digest.
   *
   * @param reset If true, the hash function is reinitialized
   *
   * @return the byte array result
   */
  public final byte[] digest( boolean reset ) {
    byte[] out = new byte[20];
    digest( reset, out, 0 );

    return out;
  }




  /**
   * Return completed digest.
   *
   * @return the byte array result
   */
  public final byte[] digest() {
    return digest( true );
  }




  /**
   * Complete processing on the message digest.
   */
  protected void finish() {
    byte bits[] = new byte[8];
    int i, j;

    for ( i = 0; i < 8; i++ ) {
      bits[i] = (byte)( ( count >>> ( ( ( 7 - i ) << 3 ) ) ) & 0xff );
    }

    update( (byte)128 );

    while ( blockIndex != 56 ) {
      update( (byte)0 );
    }

    // This should cause a transform to happen.
    for ( i = 0; i < 8; ++i ) {
      update( bits[i] );
    }

    for ( i = 0; i < 20; i++ ) {
      digestBits[i] = (byte)( ( state[i >> 2] >>> ( ( 3 - ( i & 3 ) ) << 3 ) ) & 0xff );
    }

    digestValid = true;
  }




  /**
   * Print out the digest in a form that can be easily compared to the test
   * vectors.
   *
   * @return
   */
  protected String digout() {
    StringBuffer sb = new StringBuffer();

    for ( int i = 0; i < 20; i++ ) {
      char c1, c2;

      c1 = (char)( ( digestBits[i] >>> 4 ) & 0xf );
      c2 = (char)( digestBits[i] & 0xf );
      c1 = (char)( ( c1 > 9 ) ? 'A' + ( c1 - 10 ) : '0' + c1 );
      c2 = (char)( ( c2 > 9 ) ? 'A' + ( c2 - 10 ) : '0' + c2 );

      sb.append( c1 );
      sb.append( c2 );
    }

    return sb.toString();
  }




  /**
   * Testing
   *
   * @param args
   */
  public static void main( String[] args ) {
    int i, j;
    SHA1 s = new SHA1( true );

    // "abc"
    // A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D
    System.out.println( "First test is 'abc'" );

    String z = "abc";
    s.init();
    s.update( (byte)'a' );
    s.update( (byte)'b' );
    s.update( (byte)'c' );
    s.finish();
    System.out.println( s.digout() );
    System.out.println( "A9993E364706816ABA3E25717850C26C9CD0D89D" );

    // "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq"
    // 84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1
    System.out.println( "Next Test is 'abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq'" );

    z = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";

    s.init();

    for ( i = 0; i < z.length(); ++i ) {
      s.update( (byte)z.charAt( i ) );
    }

    s.finish();
    System.out.println( s.digout() );
    System.out.println( "84983E441C3BD26EBAAE4AA1F95129E5E54670F1" );

    // A million repetitions of "a"
    // 34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F
    long startTime = 0 - System.currentTimeMillis();

    System.out.println( "Last test is 1 million 'a' characters." );
    s.init();

    for ( i = 0; i < 1000000; i++ ) {
      s.update( (byte)'a' );
    }

    s.finish();
    System.out.println( s.digout() );
    System.out.println( "34AA973CD4C4DAA4F61EEB2BDBAD27316534016F" );

    startTime += System.currentTimeMillis();

    double d = ( (double)startTime ) / 1000.0;
    System.out.println( " done, elapsed time = " + d );
  }
}
