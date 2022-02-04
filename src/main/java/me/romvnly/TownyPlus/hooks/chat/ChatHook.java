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

import com.palmergames.bukkit.towny.object.Government;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import me.romvnly.TownyPlus.TownyPlusMain;
import me.romvnly.TownyPlus.hooks.PluginHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

// https://github.com/DiscordSRV/DiscordSRV/blob/master/src/main/java/github/scarsz/discordsrv/hooks/chat/ChatHook.java
// I do not claim ownership of this file, it rightfully belongs to DiscordSRV, which is licensed under GPL v3, just like our plugin.
// View their license here. https://github.com/DiscordSRV/DiscordSRV/blob/master/LICENSE
public interface ChatHook extends PluginHook {
    @Deprecated
    default void broadcastMessageToChannel(String channel, String message) {
        throw new UnsupportedOperationException(getClass().getName() + " has no implementation for broadcastMessageToChannel");
    }

    default void broadcastMessageToChannel(String channel, Component message) {
        broadcastMessageToChannel(channel, LegacyComponentSerializer
                .legacySection()
                .serialize(message));
    }

    default void broadcastMessageToChannel(String channel, Component message, Resident resident) {
        broadcastMessageToChannel(channel, LegacyComponentSerializer
                .legacySection()
                .serialize(message));
    }
}