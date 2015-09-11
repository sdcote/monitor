/*
 * $Id: MD5.java,v 1.2 2004/01/02 15:10:28 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.security;

/**
 * MD5 Message-Digest Algorithm.
 *
 * <p>This is a translation of the original C languge code found in* RFC 1321,
 * but uses an interface more consistent with the Java MessageDigest class.</p>
 *
 * <p>A summary of the Message-Digest algorithm from RFC 1321:<BR>
 * <BLOCKQUOTE>
 * The algorithm takes as input a message of arbitrary length and produces as
 * output a 128-bit "fingerprint" or "message digest" of the input. It is
 * conjectured that it is computationally infeasible to produce two messages
 * having the same message digest, or to produce any message having a given
 * prespecified target message digest. The MD5 algorithm is intended for
 * digital signature applications, where a large file must be "compressed" in a
 * secure manner before being encrypted with a private (secret) key under a
 * public-key cryptosystem such as RSA.
 * </BLOCKQUOTE></p>
 *
 * <p>Here's a code snippit showing the interactions.
 * <PRE>
 * private byte[] RecvBuf;       // Received Packet buffer data.
 * private byte[] Authenticator; // Authenticator block for Access Request
 * private String Secret;        // Shared secret password
 * <BR>
 * MD5 md = new MD5();
 * <BR>
 * md.update(RecvBuf, 0, 4);     // Header of the received packet
 * md.update(Authenticator);     // Request authenticator
 * md.update(Secret.getBytes()); // Secret
 * <BR>
 * byte md5bytes[] = md.digest();
 * </PRE></p>
 *
 * @version $Revision: 1.3 $
 */
public class MD5 implements Digest
{
  // context class for this MD5 operation.
  private MD5_CTX context;

  // Constants for MD5Transform routine.
  private final int S11 = 7;
  private final int S12 = 12;
  private final int S13 = 17;
  private final int S14 = 22;
  private final int S21 = 5;
  private final int S22 = 9;
  private final int S23 = 14;
  private final int S24 = 20;
  private final int S31 = 4;
  private final int S32 = 11;
  private final int S33 = 16;
  private final int S34 = 23;
  private final int S41 = 6;
  private final int S42 = 10;
  private final int S43 = 15;
  private final int S44 = 21;




  /**
   * MD5 initialization. Begins an MD5 operation, writing a new context.
   */
  public MD5()
  {
    context = new MD5_CTX();
  }




  /**
   * Method reset
   */
  public void reset()
  {
    context.reset();
  }




  /**
   * MD5 block update operation.
   *
   * <p>Continues an MD5 message-digest operation, processing another message
   * block, and updating the context.</p>
   *
   * @param input byte array of data
   */
  public void update( byte input[] )
  {
    update( input, 0, input.length );
  }




  /**
   * MD5 block update operation.
   *
   * @param input byte array of data
   * @param offset offset into the array to start the digest calculation
   * @param inputLen byte count to use in the calculation
   */
  public void update( byte input[], int offset, int inputLen )
  {
    int i, index, partLen;
    // Compute number of bytes mod 64
    index = ( context.count[0] >>> 3 ) & 0x3F;

    // Update number of bits
    int slen = inputLen << 3;

    if( ( context.count[0] += slen ) < slen )
    {
      context.count[1]++;
    }

    context.count[1] += ( (int)inputLen >>> 29 );
    partLen = 64 - index;

    // Transform as many times as possible.
    if( inputLen >= partLen )
    {
      context.copy( index, input, offset, partLen );
      MD5Transform( context.buffer, 0, 0 );

      for( i = partLen; i + 63 < inputLen; i += 64 )
      {
        MD5Transform( input, offset, i );
      }

      index = 0;
    }
    else
    {
      i = 0;
    }

    // Buffer remaining input
    // MD5_memcpy
    // ((POINTER)&context->buffer[index], (POINTER)&input[i], inputLen-i);
    context.copy( index, input, i + offset, inputLen - i );
  }




  /**
   * MD5 finalization.
   *
   * <p>Ends an MD5 message-digest operation, writing the message digest and
   * zeroizing the context.</p>
   *
   * @return the digest
   */
  public byte[] digest()
  {
    byte bits[];
    byte finalDigest[];
    int index, padLen;

    // Save number of bits
    bits = Encode( context.count, 8 );

    // Pad out to 56 mod 64.
    index = (int)( ( context.count[0] >>> 3 ) & 0x3f );
    padLen = ( index < 56 ) ? ( 56 - index ) : ( 120 - index );

    // build padding buffer.
    if( padLen > 0 )
    {
      byte PADDING[] = new byte[padLen];
      PADDING[0] = (byte)0x80;

      for( int i = 1; i < padLen; i++ )
      {
        PADDING[i] = 0;
      }

      update( PADDING, 0, padLen );
    }

    // Append length (before padding)
    update( bits, 0, 8 );

    // Store state in digest
    finalDigest = Encode( context.state, 16 );

    // Zeroize sensitive information.
    context.clear();

    return finalDigest;
  }




  /**
   * Method F is a basic MD5 function.
   *
   * @param x
   * @param y
   * @param z
   *
   * @return
   */
  private int F( int x, int y, int z )
  {
    return ( ( ( x ) & ( y ) ) | ( ( ~x ) & ( z ) ) );
  }




  /**
   * Method G is a basic MD5 function.
   *
   * @param x
   * @param y
   * @param z
   *
   * @return
   */
  private int G( int x, int y, int z )
  {
    return ( ( ( x ) & ( z ) ) | ( ( y ) & ( ~z ) ) );
  }




  /**
   * Method H is a basic MD5 function.
   *
   * @param x
   * @param y
   * @param z
   *
   * @return
   */
  private int H( int x, int y, int z )
  {
    return ( ( x ) ^ ( y ) ^ ( z ) );
  }




  /**
   * Method I is a basic MD5 function.
   *
   * @param x
   * @param y
   * @param z
   *
   * @return
   */
  private int I( int x, int y, int z )
  {
    return ( ( y ) ^ ( ( x ) | ( ~z ) ) );
  }




  /**
   * Method ROTATE_LEFT rotates x left n bits.
   *
   * @param x
   * @param n
   *
   * @return
   */
  private int ROTATE_LEFT( int x, int n )
  {
    return ( ( ( x ) << ( n ) ) | ( ( x ) >>> ( 32 - ( n ) ) ) );
  }




  /**
   * Method FF is the transformation for round 1.
   *
   * <p>Rotation is separate from addition to prevent recomputation.</p>
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param x
   * @param s
   * @param ac
   *
   * @return
   */
  private int FF( int a, int b, int c, int d, int x, int s, int ac )
  {
    a += F( ( b ), ( c ), ( d ) ) + ( x ) + ( ac );
    a = ROTATE_LEFT( a, ( s ) );
    a += ( b );

    return a;
  }




  /**
   * Method GG is the transformation for round 2.
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param x
   * @param s
   * @param ac
   *
   * @return
   */
  private int GG( int a, int b, int c, int d, int x, int s, int ac )
  {
    a += G( ( b ), ( c ), ( d ) ) + ( x ) + ( ac );
    a = ROTATE_LEFT( a, ( s ) );
    a += ( b );

    return a;
  }




  /**
   * Method HH is the transformation for round 3.
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param x
   * @param s
   * @param ac
   *
   * @return
   */
  private int HH( int a, int b, int c, int d, int x, int s, int ac )
  {
    a += H( ( b ), ( c ), ( d ) ) + ( x ) + ( ac );
    a = ROTATE_LEFT( a, ( s ) );
    a += ( b );

    return a;
  }




  /**
   * Method II is the transformation for round 4.
   *
   * @param a
   * @param b
   * @param c
   * @param d
   * @param x
   * @param s
   * @param ac
   *
   * @return
   */
  private int II( int a, int b, int c, int d, int x, int s, int ac )
  {
    a += I( ( b ), ( c ), ( d ) ) + ( x ) + ( ac );
    a = ROTATE_LEFT( a, ( s ) );
    a += ( b );

    return a;
  }




  /**
   * MD5 basic transformation.
   *
   * <p>Transforms state based on block.</p>
   *
   * @param block
   * @param offset
   * @param posn
   */
  private void MD5Transform( byte block[], int offset, int posn )
  {
    int a = context.state[0], b = context.state[1];
    int c = context.state[2], d = context.state[3];
    int x[];
    x = Decode( block, offset, posn, 64 );
    // Round 1
    a = FF( a, b, c, d, x[0], S11, 0xd76aa478 ); /* 1 */
    d = FF( d, a, b, c, x[1], S12, 0xe8c7b756 ); /* 2 */
    c = FF( c, d, a, b, x[2], S13, 0x242070db ); /* 3 */
    b = FF( b, c, d, a, x[3], S14, 0xc1bdceee ); /* 4 */
    a = FF( a, b, c, d, x[4], S11, 0xf57c0faf ); /* 5 */
    d = FF( d, a, b, c, x[5], S12, 0x4787c62a ); /* 6 */
    c = FF( c, d, a, b, x[6], S13, 0xa8304613 ); /* 7 */
    b = FF( b, c, d, a, x[7], S14, 0xfd469501 ); /* 8 */
    a = FF( a, b, c, d, x[8], S11, 0x698098d8 ); /* 9 */
    d = FF( d, a, b, c, x[9], S12, 0x8b44f7af ); /* 10 */
    c = FF( c, d, a, b, x[10], S13, 0xffff5bb1 ); /* 11 */
    b = FF( b, c, d, a, x[11], S14, 0x895cd7be ); /* 12 */
    a = FF( a, b, c, d, x[12], S11, 0x6b901122 ); /* 13 */
    d = FF( d, a, b, c, x[13], S12, 0xfd987193 ); /* 14 */
    c = FF( c, d, a, b, x[14], S13, 0xa679438e ); /* 15 */
    b = FF( b, c, d, a, x[15], S14, 0x49b40821 ); /* 16 */
    // Round 2
    a = GG( a, b, c, d, x[1], S21, 0xf61e2562 ); /* 17 */
    d = GG( d, a, b, c, x[6], S22, 0xc040b340 ); /* 18 */
    c = GG( c, d, a, b, x[11], S23, 0x265e5a51 ); /* 19 */
    b = GG( b, c, d, a, x[0], S24, 0xe9b6c7aa ); /* 20 */
    a = GG( a, b, c, d, x[5], S21, 0xd62f105d ); /* 21 */
    d = GG( d, a, b, c, x[10], S22, 0x2441453 ); /* 22 */
    c = GG( c, d, a, b, x[15], S23, 0xd8a1e681 ); /* 23 */
    b = GG( b, c, d, a, x[4], S24, 0xe7d3fbc8 ); /* 24 */
    a = GG( a, b, c, d, x[9], S21, 0x21e1cde6 ); /* 25 */
    d = GG( d, a, b, c, x[14], S22, 0xc33707d6 ); /* 26 */
    c = GG( c, d, a, b, x[3], S23, 0xf4d50d87 ); /* 27 */
    b = GG( b, c, d, a, x[8], S24, 0x455a14ed ); /* 28 */
    a = GG( a, b, c, d, x[13], S21, 0xa9e3e905 ); /* 29 */
    d = GG( d, a, b, c, x[2], S22, 0xfcefa3f8 ); /* 30 */
    c = GG( c, d, a, b, x[7], S23, 0x676f02d9 ); /* 31 */
    b = GG( b, c, d, a, x[12], S24, 0x8d2a4c8a ); /* 32 */
    // Round 3
    a = HH( a, b, c, d, x[5], S31, 0xfffa3942 ); /* 33 */
    d = HH( d, a, b, c, x[8], S32, 0x8771f681 ); /* 34 */
    c = HH( c, d, a, b, x[11], S33, 0x6d9d6122 ); /* 35 */
    b = HH( b, c, d, a, x[14], S34, 0xfde5380c ); /* 36 */
    a = HH( a, b, c, d, x[1], S31, 0xa4beea44 ); /* 37 */
    d = HH( d, a, b, c, x[4], S32, 0x4bdecfa9 ); /* 38 */
    c = HH( c, d, a, b, x[7], S33, 0xf6bb4b60 ); /* 39 */
    b = HH( b, c, d, a, x[10], S34, 0xbebfbc70 ); /* 40 */
    a = HH( a, b, c, d, x[13], S31, 0x289b7ec6 ); /* 41 */
    d = HH( d, a, b, c, x[0], S32, 0xeaa127fa ); /* 42 */
    c = HH( c, d, a, b, x[3], S33, 0xd4ef3085 ); /* 43 */
    b = HH( b, c, d, a, x[6], S34, 0x4881d05 ); /* 44 */
    a = HH( a, b, c, d, x[9], S31, 0xd9d4d039 ); /* 45 */
    d = HH( d, a, b, c, x[12], S32, 0xe6db99e5 ); /* 46 */
    c = HH( c, d, a, b, x[15], S33, 0x1fa27cf8 ); /* 47 */
    b = HH( b, c, d, a, x[2], S34, 0xc4ac5665 ); /* 48 */
    // Round 4
    a = II( a, b, c, d, x[0], S41, 0xf4292244 ); /* 49 */
    d = II( d, a, b, c, x[7], S42, 0x432aff97 ); /* 50 */
    c = II( c, d, a, b, x[14], S43, 0xab9423a7 ); /* 51 */
    b = II( b, c, d, a, x[5], S44, 0xfc93a039 ); /* 52 */
    a = II( a, b, c, d, x[12], S41, 0x655b59c3 ); /* 53 */
    d = II( d, a, b, c, x[3], S42, 0x8f0ccc92 ); /* 54 */
    c = II( c, d, a, b, x[10], S43, 0xffeff47d ); /* 55 */
    b = II( b, c, d, a, x[1], S44, 0x85845dd1 ); /* 56 */
    a = II( a, b, c, d, x[8], S41, 0x6fa87e4f ); /* 57 */
    d = II( d, a, b, c, x[15], S42, 0xfe2ce6e0 ); /* 58 */
    c = II( c, d, a, b, x[6], S43, 0xa3014314 ); /* 59 */
    b = II( b, c, d, a, x[13], S44, 0x4e0811a1 ); /* 60 */
    a = II( a, b, c, d, x[4], S41, 0xf7537e82 ); /* 61 */
    d = II( d, a, b, c, x[11], S42, 0xbd3af235 ); /* 62 */
    c = II( c, d, a, b, x[2], S43, 0x2ad7d2bb ); /* 63 */
    b = II( b, c, d, a, x[9], S44, 0xeb86d391 ); /* 64 */
    context.state[0] += a;
    context.state[1] += b;
    context.state[2] += c;
    context.state[3] += d;

    // Zeroize sensitive information.
    // MD5_memset ((POINTER)x, 0, sizeof (x));
    for( int i = 0; i < x.length; i++ )
    {
      x[i] = 0;
    }

    x = null;
  }




  /**
   * Encodes input (long) into output (unsigned char).
   *
   * <p>Assumes len is a multiple of 4.</p>
   *
   * @param input
   * @param len
   *
   * @return
   */
  private byte[] Encode( int input[], int len )
  {
    int i, j;
    byte output[] = new byte[len];

    for( i = 0, j = 0; j < len; i++, j += 4 )
    {
      output[j] = (byte)( input[i] & 0xff );
      output[j + 1] = (byte)( ( input[i] >>> 8 ) & 0xff );
      output[j + 2] = (byte)( ( input[i] >>> 16 ) & 0xff );
      output[j + 3] = (byte)( ( input[i] >>> 24 ) & 0xff );
    }

    return output;
  }




  /**
   * Decodes input (unsigned char) into output (long).
   *
   * <p>Assumes len is a multiple of 4.</p>
   *
   * @param input
   * @param offset
   * @param posn
   * @param len
   *
   * @return
   */
  int[] Decode( byte input[], int offset, int posn, int len )
  {
    int output[] = new int[len / 4];
    int i, j;
    int limit = len + posn + offset;

    for( i = 0, j = offset + posn; j < limit; i++, j += 4 )
    {
      output[i] = ( ( (int)input[j] ) & 0xff ) | ( ( ( (int)input[j + 1] ) & 0xff ) << 8 ) | ( ( ( (int)input[j + 2] ) & 0xff ) << 16 ) | ( ( ( (int)input[j + 3] ) & 0xff ) << 24 );
    }

    return output;
  }

}




/**
 * MD5 context.
 */
class MD5_CTX
{

  /** state (ABCD) */
  int state[];

  /** number of bits, modulo 2^64 (lsb first) */
  int count[];

  /** input buffer */
  byte buffer[];




  /**
   * Constructor MD5_CTX
   */
  MD5_CTX()
  {
    reset();
  }




  /**
   * Method reset
   */
  void reset()
  {
    buffer = new byte[64];
    state = new int[4];
    count = new int[2];
    // Load magic initialization constants.
    state[0] = 0x67452301;
    state[1] = 0xefcdab89;
    state[2] = 0x98badcfe;
    state[3] = 0x10325476;
    count[0] = count[1] = 0;
  }




  /**
   * Clear sensitive information.
   */
  void clear()
  {
    state[0] = 0;
    state[1] = 0;
    state[2] = 0;
    state[3] = 0;
    count[0] = 0;
    count[1] = 0;

    for( int i = 0; i < 64; i++ )
    {
      buffer[i] = 0;
    }
  }




  /**
   * Method copy
   *
   * @param bufoffset
   * @param input
   * @param offset
   * @param len
   */
  void copy( int bufoffset, byte input[], int offset, int len )
  {
    // arraycopy has no idea that it shouldn't copy some things.
    // Protect the poor dear.
    if( offset == input.length )
    {
      return;
    }

    System.arraycopy( input, offset, buffer, bufoffset, len );
  }

}