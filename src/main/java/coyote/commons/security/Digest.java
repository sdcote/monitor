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

/**
 * Interface Digest
 */
public interface Digest {

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

}