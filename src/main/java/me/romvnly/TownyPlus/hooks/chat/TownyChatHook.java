/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus.hooks.chat;

import com.palmergames.bukkit.TownyChat.Chat;
import com.palmergames.bukkit.TownyChat.channels.Channel;
import com.palmergames.bukkit.TownyChat.events.AsyncChatHookEvent;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;

import me.romvnly.TownyPlus.TownyPlusMain;
import github.scarsz.discordsrv.util.LangUtil;
import github.scarsz.discordsrv.util.PlayerUtil;
import github.scarsz.discordsrv.util.PluginUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


// https://github.com/DiscordSRV/DiscordSRV/blob/master/src/main/java/github/scarsz/discordsrv/hooks/chat/TownyChatHook.java
// I do not claim ownership of this file, it rightfully belongs to DiscordSRV, which is licensed under GPL v3, just like our plugin.
// View their license here. https://github.com/DiscordSRV/DiscordSRV/blob/master/LICENSE
public class TownyChatHook implements ChatHook {

    public TownyChatHook() {
        reload();
    }

    public void reload() {
//        if (!isEnabled()) return;

        Chat instance = (Chat) Bukkit.getPluginManager().getPlugin("TownyChat");
        if (instance == null) {
            TownyPlusMain.plugin.getLogger().warning("Could not automatically hook TownyChat channels");
            return;
        }

        List<String> linkedChannels = new LinkedList<>();
        List<String> availableChannels = new LinkedList<>();
        ArrayList<String> hookedChannels = new ArrayList<>();
        hookedChannels.add("town");
        hookedChannels.add("nation");
        hookedChannels.forEach(name -> {
            Channel channel = getChannelByCaseInsensitiveName(name);
            if (channel != null) {
                channel.setHooked(true);
                linkedChannels.add(channel.getName());
            }
            else {
                TownyPlusMain.plugin.getLogger().info("It appears as the town and nation channels do not exist on TownyChat, not hooking anything.");
            }
        });
        for (Channel channel : instance.getChannelsHandler().getAllChannels().values()) {
            availableChannels.add(channel.getName());
        }

        if (!linkedChannels.isEmpty()) {
            TownyPlusMain.plugin.getLogger().info("Marked the following TownyChat channels as hooked: " + (String.join(", ", linkedChannels)) + ". Available channels: " + String.join(", ", availableChannels));
        } else {
            TownyPlusMain.plugin.getLogger().info("No TownyChat channels were marked as hooked. Available channels: " + String.join(", ", availableChannels));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMessage(AsyncChatHookEvent event) {
        // make sure message isn't blank

        if (StringUtils.isBlank(event.getMessage())) {
            return;
        }
        TownyPlusMain.plugin.getLogger().info("processing town msg");
        TownyPlusMain.plugin.processChatMessage(event.getPlayer(), event.getMessage(), event.getChannel().getName(), this);
    }

    @Override
    public void broadcastMessageToChannel(String channel, Component message, Resident resident) {
        // get instance of TownyChat plugin
        Chat instance = (Chat) Bukkit.getPluginManager().getPlugin("TownyChat");

        // return if TownyChat is disabled
        if (instance == null) return;

        // get the destination channel
        Channel destinationChannel = getChannelByCaseInsensitiveName(channel);

        // return if channel was not available
        if (destinationChannel == null) return;

        String legacy = LegacyComponentSerializer
                .legacySection()
                .serialize(message);

        String plainMessage = LangUtil.Message.CHAT_CHANNEL_MESSAGE.toString()
                .replace("%channelcolor%", destinationChannel.getMessageColour() != null ? destinationChannel.getMessageColour() : "")
                .replace("%channelname%", destinationChannel.getName())
                .replace("%channelnickname%", destinationChannel.getChannelTag() != null ? destinationChannel.getChannelTag() : "")
                .replace("%message%", legacy);

        for (Player player : PlayerUtil.getOnlinePlayers()) {
            if (destinationChannel.isPresent(player.getName())) {
                if (channel == "town") {
                    Town residentTown = resident.getTownOrNull();
                    if (residentTown != null && residentTown.hasResident(player.getUniqueId())) {
                        player.sendMessage(plainMessage);
                    }

                } else if (channel == "nation") {
                    Nation residentNation = resident.getNationOrNull();
                    if (residentNation != null && residentNation.hasResident(player.getName().toString())) {
                        player.sendMessage(plainMessage);
                    }
                }
            }
        }

        PlayerUtil.notifyPlayersOfMentions(player -> destinationChannel.isPresent(player.getName()), legacy);
    }

    private static Channel getChannelByCaseInsensitiveName(String name) {
        Chat instance = (Chat) Bukkit.getPluginManager().getPlugin("TownyChat");
        if (instance == null) return null;
        for (Channel townyChannel : instance.getChannelsHandler().getAllChannels().values())
            if (townyChannel.getName().equalsIgnoreCase(name)) return townyChannel;
        return null;
    }

    public static String getMainChannelName() {
        Chat instance = (Chat) Bukkit.getPluginManager().getPlugin("TownyChat");
        if (instance == null) return null;
        Channel channel = instance.getChannelsHandler().getDefaultChannel();
        if (channel == null) return null;
        return channel.getName();
    }
}