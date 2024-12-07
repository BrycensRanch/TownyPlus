/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) BrycensRanch <https://github.com/BrycensRanch>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author BrycensRanch
 * @author Romvnly
 * @link https://github.com/BrycensRanch/TownyPlus
 */

package me.romvnly.TownyPlus.api.interfaces;

import me.romvnly.TownyPlus.api.entities.Channel;

import java.util.Collection;

public interface IChannelManager {
    void addChannel(Channel channel);

    Collection<Channel> getChannels();

    Channel getChannel(String id);

    Channel editChannel(Channel channel)
            throws Exception;

    void deleteChannel(String id);

    boolean channelExist(String id);
}
