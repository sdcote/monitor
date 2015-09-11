/*
 * $Id: Digest.java,v 1.2 2004/01/02 15:10:28 cotes Exp $
 *
 * Copyright (C) 2003 Stephan D. Cote' - All rights reserved.
 */
package coyote.commons.security;

/**
 * Interface Digest
 *
 * @author Stephan D. Cote' - Enterprise Architecture
 * @version $Revision: 1.2 $
 */
public interface Digest
{

  /**
   * Add many bytes to the digest.
   *
   * @param data byte data to add
   * @param offset start byte
   * @param length number of bytes to hash
   */
  public void update( byte[] data, int offset, int length );




  /**
   * Adds the entire contents of the byte array to the digest.
   *
   * @param data
   */
  public void update( byte[] data );




  /**
   * Returns the completed digest, reinitializing the hash function.
   *
   * @return the byte array result
   */
  public byte[] digest();

  /**
   * Return completed digest filled into the given buffer.
   *
   * @param buffer
   * @param offset
   * @param reset If true, the hash function is reinitialized
   */
  // public void digest( boolean reset, byte[] buffer, int offset );

  /**
   * Return the hash size of this digest in bits
   *
   * @return
   */
  // public int digestSize();

  /**
   * retrieve the value of a hash, by filling the provided int[] with n elements
   * of the hash (where n is the bitlength of the hash/32).
   *
   * @param digest int[] into which to place n elements
   * @param offset index of first of the n elements
   */
  // public void extract( int[] digest, int offset );

  /**
   * Add one byte to the digest.
   *
   * <p>When this is implemented all of the abstract class methods end up
   * calling this method for types other than bytes.</p>
   *
   * @param b byte to add
   */
  // public void update( byte b );
}