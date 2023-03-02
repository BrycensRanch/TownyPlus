/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.api.interfaces;

import java.util.Collection;
import me.romvnly.TownyPlus.api.entities.Channel;

public interface IChannelManager {
  void addChannel(Channel channel);

  Collection<Channel> getChannels();

  Channel getChannel(String id);

  Channel editChannel(Channel channel) throws Exception;

  void deleteChannel(String id);

  boolean channelExist(String id);
}
