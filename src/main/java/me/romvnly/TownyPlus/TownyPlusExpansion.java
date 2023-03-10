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

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.economy.Account;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.romvnly.TownyPlus.util.Debug;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TownyPlusExpansion extends PlaceholderExpansion {
    TownyPlusMain plugin;

    // Although this class does technically need Towny, the plugin will already be disabled if Towny is not present or enabled.

    public TownyPlusExpansion() {
        super();
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
    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }

    @Override
    public String onRequest(OfflinePlayer player, String placeholder) {
        if (placeholder.equalsIgnoreCase("name")) {
            return player == null ? null : player.getName(); // "name" requires the player to be valid
        }

        if (placeholder.equalsIgnoreCase("stats")) {
            return "Placeholder Text 1";
        }

        if (placeholder.equalsIgnoreCase("placeholder2")) {
            return "Placeholder Text 2";
        }

        if (placeholder.startsWith("town_") && placeholder.endsWith("_networth")) {
            String placeholderTown = placeholder.substring(5, placeholder.length() - 9);
            Town town = TownyAPI.getInstance().getTown(placeholderTown);
            if (town == null) return null;
            List < Resident > residents = town.getResidents();
            double networth = 0;
            for (Resident resident: residents) {
                Account residentAccount = resident.getAccountOrNull();
                if (residentAccount != null) {
                    networth += residentAccount.getHoldingBalance();
                } else {
                    Debug.log("Resident " + resident.getName() + " has no account! (Town: " + town.getName() + ")");
                }
            }
            return networth + "";
        }

        return null; // Placeholder is unknown by the Expansion
    }
}