/*
 * This file is part of TownyPlus, licensed under the GPL v3 License.
 * Copyright (C) Romvnly <https://github.com/Romvnly-Gaming>
 * Copyright (C) spigot-plugin-template team and contributors
 * Copyright (C) Pl3xmap team and contributors
 * Copyright (C) DiscordSRV team and contributors
 * @author Romvnly
 * @link https://github.com/Romvnly-Gaming/TownyPlus
 */

package me.romvnly.TownyPlus;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class TownyPlusExpansion extends PlaceholderExpansion {
    TownyPlusMain plugin;
    public TownyPlusExpansion() {
        plugin = TownyPlusMain.plugin;
    }
    @Override
    public @NotNull String getIdentifier() {
        return plugin.getName();
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }
    public String onRequest(OfflinePlayer player, String placeholder) {
        if(placeholder.equalsIgnoreCase("name")) {
            return player == null ? null : player.getName(); // "name" requires the player to be valid
        }

        if(placeholder.equalsIgnoreCase("stats")) {
            return "Placeholder Text 1";
        }

        if(placeholder.equalsIgnoreCase("placeholder2")) {
            return "Placeholder Text 2";
        }

        return null; // Placeholder is unknown by the Expansion
    }
}
