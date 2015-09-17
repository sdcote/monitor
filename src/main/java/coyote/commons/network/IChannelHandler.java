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
package coyote.commons.network;

/**
 * IChannelHandler defines an interface for a class that handles IChannels.
 *
 * <p>Handlers normally embed the protocols or business logic of communications
 * over a channel.</p>
 */
public interface IChannelHandler extends Runnable {

  /**
   * Assign an IChannel object to the handler so that when it is run it has a
   * reference to the channel.
   *
   * @param channel
   */
  public abstract void setChannel( IChannel channel );
}