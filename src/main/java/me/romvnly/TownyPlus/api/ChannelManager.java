/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.api;

import me.romvnly.TownyPlus.api.entities.Channel;
import me.romvnly.TownyPlus.api.exceptions.ChannelException;
import me.romvnly.TownyPlus.api.interfaces.IChannelManager;

import java.util.Collection;
import java.util.HashMap;

public class ChannelManager implements IChannelManager {
    private HashMap<String, Channel> channelMap;

    public ChannelManager() {
        channelMap = new HashMap<>();
    }

    @Override
    public void addChannel(Channel channel) {
        channelMap.put(channel.getId(), channel);
    }

    @Override
    public Collection<Channel> getChannels() {
        return channelMap.values();
    }

    @Override
    public Channel getChannel(String id) {
        return channelMap.get(id);
    }

    @Override
    public Channel editChannel(Channel forEdit) throws ChannelException {
        try {
            if (forEdit.getId() == null)
                throw new ChannelException("ID cannot be blank");

            Channel toEdit = channelMap.get(forEdit.getId());

            if (toEdit == null)
                throw new ChannelException("User not found");


            if (forEdit.getName() != null) {
                toEdit.setName(forEdit.getName());
            }
            if (forEdit.getTown() != null) {
                toEdit.setTown(forEdit.getTown());
            }
            if (forEdit.getId() != null) {
                toEdit.setId(forEdit.getId());
            }

            return toEdit;
        } catch (Exception ex) {
            throw new ChannelException(ex.getMessage());
        }
    }

    @Override
    public void deleteChannel(String id) {
        channelMap.remove(id);
    }

    @Override
    public boolean channelExist(String id) {
        return channelMap.containsKey(id);
    }

}